package com.huatu.teacheronline.message;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.HomeActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.message.adapter.SubscriptionAdapter;
import com.huatu.teacheronline.message.bean.SubscriptionBean;
import com.huatu.teacheronline.utils.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 公众号
 * Created by ply on 2016/2/25.
 */
public class SubscriptionActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;
    private RelativeLayout rl_wifi;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private List<SubscriptionBean> subscriptionBeanList = new ArrayList<>();
    private SubscriptionAdapter adapter;
    private View loadView;
    private View loadIcon;
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;

    private ObtatinDataListener obtatinDataListener;
    private int currentPage;

    @Override
    public void initView() {
        setContentView(R.layout.activity_subscription);

        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.teacher_serviceNO);

        rl_wifi = (RelativeLayout) findViewById(R.id.rl_wifi);
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        adapter = new SubscriptionAdapter(this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        listView = (ListView) findViewById(R.id.listview);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        listView.setAdapter(adapter);
        loadTeacherServerNo(true);

    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTeacherServerNo(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
//                MainActivity.newIntentFlag(this, "MainActivity");
                HomeActivity.newIntentFlag(this, "MainActivity",0);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //具体的操作代码
            back();
//            MainActivity.newIntentFlag(this, "MainActivity");
            HomeActivity.newIntentFlag(this, "MainActivity",0);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取服务号
     *
     * @param
     */
    public void loadTeacherServerNo(final boolean isReset) {
        if (isReset) {
            currentPage = 1;
        } else {
            currentPage++;
        }
        obtatinDataListener = new ObtatinDataListener(isReset, this);
        SendRequest.obtainSubscription_teacherOnline(currentPage, obtatinDataListener);
    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<List<SubscriptionBean>, String> {
        private SubscriptionActivity weak_fragment;
        private boolean isReset;

        public ObtatinDataListener(Boolean isReset, SubscriptionActivity fragment) {
            weak_fragment = new WeakReference<>(fragment).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_fragment != null) {
                weak_fragment.listView.setVisibility(View.VISIBLE);
                weak_fragment.rl_wifi.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSuccess(final List<SubscriptionBean> res) {
            if (weak_fragment != null) {
                weak_fragment.runOnUiThread(new Runnable() {
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
                weak_fragment.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_fragment.flushContent_OnFailure(isReset, res);
                    }
                });
            }
        }
    }

    public void flushContent_OnSucess(boolean isReset, List<SubscriptionBean> res) {
        if (isReset && (res == null || res.size() == 0)) {
            ToastUtils.showToast(R.string.no_data);
        } else if (!isReset && (res == null || res.size() == 0)) {
            ToastUtils.showToast(R.string.no_more);
        } else {
            subscriptionBeanList.addAll(0, res);
            adapter.setBeanList(subscriptionBeanList);
        }
        completeRefresh();
    }

    public void flushContent_OnFailure(boolean isReset, String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
            if (isReset) {
                listView.setVisibility(View.GONE);
                rl_wifi.setVisibility(View.VISIBLE);
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
        swipeRefreshLayout.setRefreshing(false);
        if (listView != null && listView.getFooterViewsCount() > 0) {
            listView.removeFooterView(loadView);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public static void newIntent(Context context) {
        Intent intent = new Intent(context, SubscriptionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
