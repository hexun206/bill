package com.huatu.teacheronline.personal.frament;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.greendao.DaoUtils;
import com.greendao.DirectBean;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.DataStore_Direct;
import com.huatu.teacheronline.direct.adapter.MyDirectListAdapter;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.swipemenulistview.SwipeMenu;
import com.huatu.teacheronline.widget.swipemenulistview.SwipeMenuCreator;
import com.huatu.teacheronline.widget.swipemenulistview.SwipeMenuItem;
import com.huatu.teacheronline.widget.swipemenulistview.SwipeMenuListView;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的直播
 */
public class MyDirectAllFragment extends Fragment {
    private Context context;
    private View inflate;
    private SwipeRefreshLayout mPullToRefreshLayout;
    private SwipeMenuListView listView;
    private CustomAlertDialog mCustomDelDirectDilog;
    private CustomAlertDialog mCustomLoadingDialog;
    private DaoUtils daoUtils;
    private String fileLocalPath;
    private String uid;
    private View loadView;
    private View loadIcon;
    private List<DirectBean> directBeanList = new ArrayList<>();
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;
    private MyDirectListAdapter adapter;
    private boolean hasMoreData = true;
    private int currentPage = 1;
    private int limit = 8;
    private boolean isLoadEnd;
    private RelativeLayout rl_nodata;
    private RelativeLayout rl_wifi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_my_direct_all, container, false);
        initView();
        setListener();
        LoadNetOrCashData();
        return inflate;
    }

    private void initView() {
        mPullToRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.live_refresh_layout);
        listView = (SwipeMenuListView) inflate.findViewById(R.id.listview);
        daoUtils = DaoUtils.getInstance();
        mCustomDelDirectDilog = new CustomAlertDialog(getActivity(), R.layout.dialog_join_mydirect);
        mCustomLoadingDialog = new CustomAlertDialog(getActivity(), R.layout.dialog_loading_custom);
        mCustomDelDirectDilog.setTitle("提示<br/>课程确定删除？");
        fileLocalPath = FileUtils.dataPath() + "/huatu/TeacherOnline/myVideo.txt";
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        loadView = getActivity().getLayoutInflater().inflate(R.layout.background_isloading, null);
        rl_wifi = (RelativeLayout) inflate.findViewById(R.id.rl_wifi);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        rl_nodata = (RelativeLayout) inflate.findViewById(R.id.rl_nodata);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                getActivity(), R.anim.pull_to_refresh_and_load_rotating);
        adapter = new MyDirectListAdapter(getActivity(),3,1);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        listView.setAdapter(adapter);
        listView.setParentRefreshlayout(mPullToRefreshLayout);
    }
    private void LoadNetOrCashData() {
        if (CommonUtils.isNetWorkAvilable()) {
            loadLiveData(true);
            rl_wifi.setVisibility(View.GONE);
        } else {
            List<DirectBean> directBeans = daoUtils.queryDirectBeanForCacheForUserid(uid);
            if(directBeans == null||directBeans.size() == 0){
                rl_wifi.setVisibility(View.VISIBLE);
                ToastUtils.showToast(R.string.network);
                return;
            }
            directBeanList.clear();
            directBeanList.addAll(directBeans);
            hasMoreData = false;
            completeRefresh();
        }
    }


    /**
     * 上拉刷新
     */
    private void completeRefresh() {
        mPullToRefreshLayout.setRefreshing(false);
        if (listView != null && listView.getFooterViewsCount() > 0) {
            listView.removeFooterView(loadView);
        }

        if (adapter != null) {
            adapter.setDirectBeanList(directBeanList);
            adapter.notifyDataSetChanged();
        }

        isLoadEnd = hasMoreData;
    }
    /**
     * 获取我的在线视频列表
     *
     * @param
     */
    public void loadLiveData(final boolean isReset) {
        if (isReset) {
            currentPage = 1;
        } else {
            currentPage++;
        }

        ObtatinDataListener  obtatinDataListener = new ObtatinDataListener(isReset, this);
        SendRequest.getMyLiveData(String.valueOf(currentPage), String.valueOf(limit), uid,1, obtatinDataListener);
    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<List<DirectBean>, String> {
        private MyDirectAllFragment weak_activity;
        private boolean isReset;
        public ObtatinDataListener(Boolean isReset, MyDirectAllFragment activity) {
            weak_activity = new WeakReference<>(activity).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.isLoadEnd = false;
                if (isReset) {
                    weak_activity.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weak_activity.clearAdapete();
                        }
                    });
                }
            }
        }

        @Override
        public void onSuccess(final List<DirectBean> res) {
            if (weak_activity != null) {
                weak_activity.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushContent_OnSucess(isReset, res);
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushContent_OnFailure(res);
                    }
                });
            }
        }

    }

    public void flushContent_OnSucess(boolean isReset, List<DirectBean> res) {
        if (isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            rl_nodata.setVisibility(View.VISIBLE);
        } else if (!isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_more);
        } else {
            hasMoreData = true;
            directBeanList.addAll(res);
            for (int i = 0; i < res.size(); i++) {
                DebugUtil.e("flushContent_OnSucess: res.size()" + res.size());
                DirectBean directBean = res.get(i);
                directBean.setUserid(uid);
                daoUtils.insertOrUpdateDirectBeanCache(directBean);
            }
        }
        completeRefresh();
    }

    public void flushContent_OnFailure(String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
        } else if (SendRequest.ERROR_SERVER.equals(res)) {
            ToastUtils.showToast(R.string.server_error);
        }
        completeRefresh();
    }
    public void setListener(){
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (view.getId() == R.id.ll_loading) {//如果点击底部FooterView加载中布局，不做出来
//                    return;
//                }
//                MobclickAgent.onEvent(getActivity(), "courseOnItemClick");
//                DirectBean directBean = directBeanList.get(position);
//                if (directBean.getVideoType() == 1) {
//                    if ("1".equals(directBean.getIs_zhibo())) {//直播
//                        DirectPlayDetailsActivityForRtsdk.newIntent(getActivity(), directBean, 0, 2);
//                    } else if ("-1".equals(directBeanList.get(position).getIs_zhibo())) {
//                        ToastUtils.showToast(R.string.direct_offline);
//                    } else {//录播
//                        DirectPlayDetailsActivityForVodsdk.newIntent(getActivity(), directBean, 0, 2);
//                    }
//                } else if (directBean.getVideoType() == 0) {
//                    //网课
//                    DirectPlayDetailsActivityForCCsdk.newIntent(getActivity(), directBean, 0, 2);
//                }
//            }
//        });
                mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadNetOrCashData();
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (isLoadEnd) {
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            listView.addFooterView(loadView);
                            loadIcon.startAnimation(refreshingAnimation);
                            loadLiveData(false);
                        }
                    }
                }


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            //            @Overrideerride
//            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            }
//        });
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(CommonUtils.dip2px(80));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(15);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        MobclickAgent.onEvent(getActivity(), "courseDeleteClick");
                        mCustomDelDirectDilog.show();
                        mCustomDelDirectDilog.setTag(directBeanList.get(position));
                        break;

                    default:
                        break;
                }
                return false;
            }
        });

        mCustomDelDirectDilog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "courseDeleteOK");
                mCustomDelDirectDilog.dismiss();
                delDirectForMy((DirectBean) mCustomDelDirectDilog.getTag());
            }
        });
        mCustomDelDirectDilog.setCancelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "courseDeleteCancel");
                mCustomDelDirectDilog.dismiss();
            }
        });
    }
            /**
     * 清空适配器
     */
    public void clearAdapete() {
        directBeanList.clear();
        adapter.setDirectBeanList(directBeanList);
        adapter.notifyDataSetChanged();
    }
    /**
     * 将直播删除
     */
    public void delDirectForMy(DirectBean directBean) {
        ObtainDataListerForDelDirect  obtainDataListerForAddDirect = new ObtainDataListerForDelDirect(this);
        SendRequest.delDirectForMy(directBean.getRid(),directBean.getOid(), uid, obtainDataListerForAddDirect);
    }

    private class ObtainDataListerForDelDirect extends ObtainDataFromNetListener<String, String> {
        private MyDirectAllFragment weak_activity;

        public ObtainDataListerForDelDirect(MyDirectAllFragment activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.show();
            }
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("1".equals(res) || "2".equals(res)) {//1 删除成功 2 重复删除
                            ToastUtils.showToast(R.string.del_success);
                            weak_activity.directBeanList.remove(weak_activity.mCustomDelDirectDilog.getTag());
                            if (weak_activity.directBeanList.size() == 0) {
                                rl_nodata.setVisibility(View.VISIBLE);
                            }else{
                                rl_nodata.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                        } else {//删除失败
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            weak_activity.mCustomLoadingDialog.dismiss();
            if (weak_activity != null) {
                weak_activity.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadIcon.startAnimation(refreshingAnimation);
        DataStore_Direct.resetDatas();
    }

}
