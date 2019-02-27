package com.huatu.teacheronline.vipexercise;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.adapter.VipAdapter;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.vipexercise.vipbean.VipPaperBean;
import com.huatu.teacheronline.vipexercise.vipbean.VipQuestionBean;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.SupportPopupWindow;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 *  VIP我的错题
 * Created by ply on 2016/1/5.
 */
public class TitleErrorActivity extends BaseActivity {
    private ListView listView;
    private VipAdapter adapter;

    private SwipeRefreshLayout mPullToRefreshLayout;
    private View loadView;
    private View loadIcon;
    private RotateAnimation refreshingAnimation;

    private List<VipPaperBean> vipPaperBeans = new ArrayList<>();
    public int currentPage = 1;//当前页数 默认1
    private int limit = 10;
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private ObtatinDataListener obtatinDataListener;
    private ObtatinQuestionDataListener obtatinQuestionDataListener;

    private String userid;
    private RelativeLayout rl_wifi;
    private PercentRelativeLayout rl_nodata;
    private VipPaperBean currentVipPaperBean;//当前试卷
    private CustomAlertDialog mCustomLoadingDialog;


    @Override
    public void initView() {
        setContentView(R.layout.activity_vip_error_questions);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = getWindow();
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
        userid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        TextView tv_title= (TextView) findViewById(R.id.tv_main_title);
        findViewById(R.id.rl_main_right).setVisibility(View.GONE);
        rl_wifi = (RelativeLayout) findViewById(R.id.rl_wifi);
        rl_nodata = (PercentRelativeLayout) findViewById(R.id.rl_nodata);
        ImageView iv_nodata = (ImageView) findViewById(R.id.iv_nodata);
        iv_nodata.setImageResource(R.drawable.pic_zwlx);
        TextView tv_nodata = (TextView) findViewById(R.id.tv_nodata);
        tv_nodata.setText("暂无错题");
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.vip_list_refresh_layout);
        mCustomLoadingDialog = new CustomAlertDialog(TitleErrorActivity.this, R.layout.dialog_loading_custom);
        listView = (ListView) findViewById(R.id.listview);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        tv_title.setText(R.string.my_error_exercise);
        listView = (ListView) findViewById(R.id.listview);
        adapter = new VipAdapter(this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVipErrorListData(true);
    }

    @Override
    public void setListener() {
        findViewById(R.id.rl_main_left).setOnClickListener(this);

        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                vipPaperBeans.clear();
                adapter.setData(vipPaperBeans);
                adapter.notifyDataSetChanged();
                loadVipErrorListData(true);
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
                            loadVipErrorListData(false);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataStore_VipExamLibrary.resetDatas();
                currentVipPaperBean = vipPaperBeans.get(position);
                loadVipErrorQuestionListData(userid, vipPaperBeans.get(position).getExclusiveId());
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_main_left:
                back();
                break;
        }
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, TitleErrorActivity.class);
        context.startActivity(intent);
    }


    /**
     * 获取试卷列表
     *
     * @param
     */
    public void loadVipErrorListData(final boolean isReset) {
        obtatinDataListener = new ObtatinDataListener(isReset, this);
        if (isReset) {
            currentPage = 1;
        }
        SendRequest.getExclusiveErrorList(userid, currentPage, limit, obtatinDataListener);
    }

    /**
     * 获取试题集
     *
     * @param
     */
    public void loadVipErrorQuestionListData(String uid,String exclusiveId) {
        obtatinQuestionDataListener = new ObtatinQuestionDataListener(this);
        SendRequest.getErrorQuestionExclusiveList(uid, exclusiveId, obtatinQuestionDataListener);
    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<List<VipPaperBean>, String> {
        private TitleErrorActivity activity;
        private boolean isReset;

        public ObtatinDataListener(Boolean isReset, TitleErrorActivity activity) {
            this.activity = new WeakReference<>(activity).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (activity != null) {
                activity.mCustomLoadingDialog.setTitle(activity.getString(R.string.loading));
                activity.mCustomLoadingDialog.show();
                activity.isLoadEnd = false;
                activity.listView.setVisibility(View.VISIBLE);
                activity.rl_wifi.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSuccess(final List<VipPaperBean> res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.mCustomLoadingDialog.dismiss();
                        activity.flushContent_OnSucess(isReset, res);
//                        if (res!=null){
//                            weak_fragment.rl_nodata.setVisibility(View.GONE);
//                        }else{
//                            weak_fragment.rl_nodata.setVisibility(View.VISIBLE);
//                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.flushContent_OnFailure(isReset, res);
                    }
                });
            }
        }
    }

    public void flushContent_OnSucess(boolean isReset, List<VipPaperBean> res) {
        mPullToRefreshLayout.setVisibility(View.VISIBLE);
        if (isReset && (res == null || res.size() == 0)) {
            ToastUtils.showToast(R.string.no_data);
            rl_nodata.setVisibility(View.VISIBLE);
        }else{
            rl_nodata.setVisibility(View.GONE);
        }

        if (res == null || res.size() == 0) {
            hasMoreData = false;
        } else {
            if (isReset) {
                vipPaperBeans.clear();
            }
            hasMoreData = true;
            currentPage++;
            vipPaperBeans.addAll(res);
        }
        completeRefresh();
    }

    public void flushContent_OnFailure(boolean isReset, String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
            if (isReset) {
                listView.setVisibility(View.GONE);
                rl_wifi.setVisibility(View.VISIBLE);
                mPullToRefreshLayout.setVisibility(View.GONE);
            }
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

        if (adapter != null) {
            adapter.setData(vipPaperBeans);
        }

        if (hasMoreData) {
            isLoadEnd = true;
        } else {
            isLoadEnd = false;
        }
    }

    private static class ObtatinQuestionDataListener extends ObtainDataFromNetListener<VipQuestionBean, String> {
        private TitleErrorActivity activity;

        public ObtatinQuestionDataListener(TitleErrorActivity activity) {
            this.activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (activity != null) {
                activity.mCustomLoadingDialog.setTitle(activity.getString(R.string.loading));
                activity.mCustomLoadingDialog.show();
                activity.isLoadEnd = false;
                activity.listView.setVisibility(View.VISIBLE);
                activity.rl_wifi.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSuccess(final VipQuestionBean res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.mCustomLoadingDialog.dismiss();
                        activity.flushQuestionContent_OnSucess(res);
//                        if (res!=null){
//                            weak_fragment.rl_nodata.setVisibility(View.GONE);
//                        }else{
//                            weak_fragment.rl_nodata.setVisibility(View.VISIBLE);
//                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.flushQuestionContent_OnFailure(res);
                    }
                });
            }
        }
    }

    private void flushQuestionContent_OnFailure(String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
        } else {
            ToastUtils.showToast(R.string.server_error);
        }
    }

    private void flushQuestionContent_OnSucess(VipQuestionBean res) {
        if (res!=null) {
            DataStore_VipExamLibrary.VipQuestionBean = res;
            DataStore_VipExamLibrary.VipPaperBean = currentVipPaperBean;
            DataStore_VipExamLibrary.ResultListBeanDetailList = res.getResultList();
            DoVipQuestionActivity.newIntent(this,0,3);
        }
    }

}
