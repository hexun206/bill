package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.adapter.MyErrorCorrectionAdapter;
import com.huatu.teacheronline.personal.bean.MyErrorCorrectionBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.umeng.analytics.MobclickAgent;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的纠错
 * Created by ply on 2016/2/15.
 */
public class MyErrorCorrectionActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;

    private View loadView;
    private View loadIcon;
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;
    private ListView listView;
    private List<MyErrorCorrectionBean> myErrorCorrectionBeanList = new ArrayList<>();
    private MyErrorCorrectionAdapter adapter;
    private int currentPage = 1;
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private String uid;
    private PercentRelativeLayout rl_nodata;

    @Override
    public void initView() {
        setContentView(R.layout.activity_my_error_correction);

        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.my_error);
        rl_nodata = (PercentRelativeLayout) findViewById(R.id.rl_nodata);
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        listView = (ListView) findViewById(R.id.listview);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        adapter = new MyErrorCorrectionAdapter(this);
        listView.setAdapter(adapter);
        loadData(true);
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                MobclickAgent.onEvent(MyErrorCorrectionActivity.this, "Myerror");//我的纠错
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (isLoadEnd) {
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            listView.addFooterView(loadView);
                            loadIcon.startAnimation(refreshingAnimation);
                            loadData(false);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
        }
    }

    public static void newIntent(Activity context) {
        Intent goldPersonIntent = new Intent(context, MyErrorCorrectionActivity.class);
        context.startActivity(goldPersonIntent);
    }

    public void loadData(final boolean isReset) {
        if (isReset) {
            currentPage = 1;
        } else {
            currentPage++;
        }
        ObtatinDataListener obtatinDataListener = new ObtatinDataListener(isReset, this);
        SendRequest.getMyErrorCorrection(uid, currentPage, obtatinDataListener);
    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<List<MyErrorCorrectionBean>, String> {
        private MyErrorCorrectionActivity weak_activity;
        private boolean isReset;

        public ObtatinDataListener(Boolean isReset, MyErrorCorrectionActivity fragment) {
            weak_activity = new WeakReference<>(fragment).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.isLoadEnd = false;
            }
        }

        @Override
        public void onSuccess(final List<MyErrorCorrectionBean> res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
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
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushContent_OnFailure(res);
                    }
                });
            }
        }

    }

    public void flushContent_OnSucess(boolean isReset, List<MyErrorCorrectionBean> res) {
        if (isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_data);
            rl_nodata.setVisibility(View.VISIBLE);
        } else if (!isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_more);
        } else {
            hasMoreData = true;
            myErrorCorrectionBeanList.addAll(res);
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
        if (listView != null && listView.getFooterViewsCount() > 0) {
            listView.removeFooterView(loadView);
        }

        if (adapter != null) {
            adapter.setDirectBeanList(myErrorCorrectionBeanList);
            adapter.notifyDataSetChanged();
        }

        if (hasMoreData) {
            isLoadEnd = true;
        } else {
            isLoadEnd = false;
        }
    }
}
