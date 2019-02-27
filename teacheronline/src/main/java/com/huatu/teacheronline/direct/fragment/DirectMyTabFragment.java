package com.huatu.teacheronline.direct.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.greendao.DaoUtils;
import com.greendao.DirectBean;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.DataStore_Direct;
import com.huatu.teacheronline.direct.PlayerActivityForBjysdk;
import com.huatu.teacheronline.direct.adapter.DirectListAdapter;
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
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 直播 我的选项卡
 * Created by ply on 2016/1/5.
 */
public class DirectMyTabFragment extends Fragment implements View.OnClickListener {
    private String fileLocalPath;
    private View view;
    private Context context;
    //下拉刷新
    private List<DirectBean> directBeanList = new ArrayList<>();
    private ArrayList<DirectBean> directBeanListForClassSchedule = new ArrayList<DirectBean>();
//    private DirectListAdapter adapter;
    private SwipeMenuListView listView;
    private SwipeRefreshLayout mPullToRefreshLayout;
    private View loadView;
    private View loadIcon;
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;
    private ObtatinDataListener obtatinDataListener;
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private int currentPage = 1;
    private int limit = 8;
    private String uid;
    private CustomAlertDialog mCustomDelDirectDilog;
    private CustomAlertDialog mCustomLoadingDialog;
    private ObtainDataListerForDelDirect obtainDataListerForAddDirect;
    private DaoUtils daoUtils;
    private PercentRelativeLayout rl_nodata;
    private int videoType;//1 直播 0 高清网课 2 教辅资料
    private DirectListAdapter adapter;
    private MyDirectListAdapter myDirectListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        fileLocalPath = FileUtils.dataPath() + "/huatu/TeacherOnline/myVideo.txt";
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        daoUtils = DaoUtils.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_directlist_layout, null);
        Bundle arguments = getArguments();
        videoType = arguments.getInt("videoType",0);
        initView();
        setListener();
        LoadNetOrCashData();
/*        if (CommonUtils.isNetWorkAvilable()){
            loadLiveData(true);
        }else {
            List<DirectBean> directBeans = daoUtils.queryDirectBeanForCacheForUserid(uid);
            if(directBeans.size() == 0){
                ToastUtils.showToast(R.string.network);
            }else {
                directBeanList.clear();
                directBeanList.addAll(directBeans);
                hasMoreData = false;
                completeRefresh();
            }
        }*/
        return view;
    }

    public void initView() {
        loadView = getActivity().getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                getActivity(), R.anim.pull_to_refresh_and_load_rotating);
//        adapter = new DirectListAdapter(context,1);
        if (videoType==1||videoType==2){
            myDirectListAdapter = new MyDirectListAdapter(getActivity(),1,videoType);
        }else{
            adapter = new DirectListAdapter(getActivity(),1,videoType);
        }

        mPullToRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.live_refresh_layout);
        rl_nodata = (PercentRelativeLayout) view.findViewById(R.id.rl_nodata);
        listView = (SwipeMenuListView) view.findViewById(R.id.listview);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        if (videoType==1||videoType==2){
            listView.setAdapter(myDirectListAdapter);
        }else{
            listView.setAdapter(adapter);
        }
        listView.setParentRefreshlayout(mPullToRefreshLayout);
        mCustomDelDirectDilog = new CustomAlertDialog(getActivity(), R.layout.dialog_join_mydirect);
        mCustomLoadingDialog = new CustomAlertDialog(getActivity(), R.layout.dialog_loading_custom);
        mCustomDelDirectDilog.setTitle("提示<br/>课程确定删除？");

    }

    public void setListener() {
        if (videoType!=1&&videoType!=2){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (view.getId() == R.id.ll_loading) {//如果点击底部FooterView加载中布局，不做出来
                        return;
                    }
                    DebugUtil.e("DirectMyTabFragment:" + directBeanList.get(position).toString());
                    DirectBean directBean = directBeanList.get(position);
//                    if (directBean.getVideoType() == 1) {
//                        if ("1".equals(directBeanList.get(position).getIs_zhibo())) {//直播
//                            LiveActivityForBjysdk.newIntent(getActivity(), directBean, 0, 2);
//                        }else if("-1".equals(directBeanList.get(position).getIs_zhibo())){
//                            ToastUtils.showToast(R.string.direct_offline);
//                        } else{//回放
//                            LiveActivityForBjysdk.newIntent(getActivity(), directBean, 0, 2);
//                        }
//                    } else if (directBean.getVideoType() == 0) {
//                        //网课
//                        DirectPlayDetailsActivityForBjysdk.newIntent(getActivity(), directBean, 0, 2);
//                    }

                    PlayerActivityForBjysdk.newIntent(getActivity(),directBean,0,2);
                }
            });
        }

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
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(CommonUtils.dip2px(80));
                deleteItem.setTitle(getResources().getString(R.string.deletecn));
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
                        mCustomDelDirectDilog.show();
                        mCustomDelDirectDilog.setTag(directBeanList.get(position));
                        MobclickAgent.onEvent(getActivity(), "courseDeleteClick");
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

    @Override
    public void onClick(View v) {

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
        obtatinDataListener = new ObtatinDataListener(isReset, this);
        SendRequest.getMyLiveData(String.valueOf(currentPage), String.valueOf(limit), uid, videoType, obtatinDataListener);
    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<List<DirectBean>, String> {
        private DirectMyTabFragment weak_fragment;
        private boolean isReset;

        public ObtatinDataListener(Boolean isReset, DirectMyTabFragment fragment) {
            weak_fragment = new WeakReference<>(fragment).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_fragment != null) {
                weak_fragment.isLoadEnd = false;
                if (isReset) {
                    weak_fragment.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weak_fragment.clearAdapete();

                        }
                    });
                }
            }
        }

        @Override
        public void onSuccess(final List<DirectBean> res) {
            if (weak_fragment != null) {
                weak_fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_fragment.flushContent_OnSucess(isReset, res);
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_fragment != null) {
                weak_fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_fragment.flushContent_OnFailure(res);
                    }
                });
            }
        }

    }

    public void flushContent_OnSucess(boolean isReset, List<DirectBean> res) {
        if (isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_data);
            rl_nodata.setVisibility(View.VISIBLE);
        } else if (!isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_more);
        } else {
            hasMoreData = true;
            directBeanList.addAll(res);
            for (int i = 0; i < res.size(); i++) {
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

    /**
     * 上拉刷新
     */
    private void completeRefresh() {
        mPullToRefreshLayout.setRefreshing(false);
        if (listView != null && listView.getFooterViewsCount() > 0) {
            listView.removeFooterView(loadView);
        }
         if (videoType!=1&&videoType!=2){
             if (adapter != null) {
                 adapter.setDirectBeanList(directBeanList);
                 adapter.notifyDataSetChanged();
             }
         }else{
             if ( myDirectListAdapter!= null) {
                 myDirectListAdapter.setDirectBeanList(directBeanList);
                 myDirectListAdapter.notifyDataSetChanged();
             }
         }


        if (hasMoreData) {
            isLoadEnd = true;
        } else {
            isLoadEnd = false;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            listView.addFooterView(loadView);
            loadIcon.startAnimation(refreshingAnimation);
            LoadNetOrCashData();
//            loadLiveData(true);
        }
    }

    private void LoadNetOrCashData() {
        if (CommonUtils.isNetWorkAvilable()) {
            loadLiveData(true);
        } else {
            List<DirectBean> directBeans = daoUtils.queryDirectBeanForCacheForUserid(uid);
            if(directBeans == null || directBeans.size() == 0){
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
     * 清空适配器
     */
    public void clearAdapete() {
        directBeanList.clear();
        if (videoType!=1&&videoType!=2){
            adapter.setDirectBeanList(directBeanList);
            adapter.notifyDataSetChanged();
        }else{
            myDirectListAdapter.setDirectBeanList(directBeanList);
            myDirectListAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 将直播删除
     */
    public void delDirectForMy(DirectBean directBean) {
        obtainDataListerForAddDirect = new ObtainDataListerForDelDirect(this);
        SendRequest.delDirectForMy(directBean.getRid(),directBean.getOid(), uid, obtainDataListerForAddDirect);
        DebugUtil.e("delDirectForMy:"+ directBean.toString());
        daoUtils.deleteDirectBeanForRid(directBean);
    }

    private class ObtainDataListerForDelDirect extends ObtainDataFromNetListener<String, String> {
        private DirectMyTabFragment weak_fragment;

        public ObtainDataListerForDelDirect(DirectMyTabFragment weak_fragment) {
            this.weak_fragment = new WeakReference<>(weak_fragment).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_fragment != null) {
                weak_fragment.mCustomLoadingDialog.show();
            }
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_fragment != null) {
                weak_fragment.mCustomLoadingDialog.dismiss();
                weak_fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("1".equals(res) || "2".equals(res)) {//1 删除成功 2 重复删除
                            ToastUtils.showToast(R.string.del_success);
                            weak_fragment.directBeanList.remove(weak_fragment.mCustomDelDirectDilog.getTag());
                            if (weak_fragment.directBeanList.size()==0){
                                weak_fragment.rl_nodata.setVisibility(View.VISIBLE);
                            }
                            if (videoType!=1&&videoType!=2){
                                weak_fragment.adapter.notifyDataSetChanged();
                            }else{
                                weak_fragment.myDirectListAdapter.notifyDataSetChanged();
                            }

                        } else {//删除失败
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            weak_fragment.mCustomLoadingDialog.dismiss();
            if (weak_fragment != null) {
                weak_fragment.getActivity().runOnUiThread(new Runnable() {
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
        DataStore_Direct.resetDatas();
    }
}
