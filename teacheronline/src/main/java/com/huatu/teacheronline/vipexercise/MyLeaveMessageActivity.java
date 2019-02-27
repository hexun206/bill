package com.huatu.teacheronline.vipexercise;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.vipexercise.adapter.LeaveListAdapter;
import com.huatu.teacheronline.vipexercise.vipbean.LeaveBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的留言列表
 * Created by ply on 2016/1/5.
 */
public class MyLeaveMessageActivity extends BaseActivity {

    private SwipeRefreshLayout mPullToRefreshLayout;
    private ListView listView;
    private View loadView;
    private View loadIcon;
    private RotateAnimation refreshingAnimation;
    private String uid;
    private int index=1;
    private int limit = 9;
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private List<LeaveBean.DataEntity> leavebean=new ArrayList<>();
    private LeaveListAdapter leaveListAdapter;
    private RelativeLayout rl_no_teacher_message;

    @Override
    public void initView() {
        setContentView(R.layout.activity_leave_message);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        rl_no_teacher_message = (RelativeLayout) findViewById(R.id.rl_no_teacher_message);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_refresh_layout);
        listView = (ListView) findViewById(R.id.listview);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
//        listView.setAdapter(adapter);
        TextView tv_title = (TextView) findViewById(R.id.tv_main_title);
        TextView tv_no_data = (TextView) findViewById(R.id.tv_no_data);
        tv_no_data.setText("暂无留言");
        leaveListAdapter = new LeaveListAdapter(this, leavebean);
        listView.setAdapter(leaveListAdapter);
//        listView.setParentRefreshlayout(mPullToRefreshLayout);
        tv_title.setText("我的留言");

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLiveData(true);
    }

    private void loadLiveData(final boolean isReset) {
        if (isReset) {
            index = 1;
        } else {
            index++;
        }
        ObtainDataLister obtainDataLister =new ObtainDataLister(isReset,this);
        SendRequest.getTeacherMessageList(uid,String.valueOf(index),String.valueOf(limit),obtainDataLister);
    }

    @Override
    public void setListener() {
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  LeaTeacherActivity.newIntent(MyLeaveMessageActivity.this,leavebean.get(position).getTeacherId()+"",leavebean.get(position).getTeacherName());
            }
        });
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                leavebean.clear();
                  leaveListAdapter.setMessageBeanList(leavebean);
                  leaveListAdapter.notifyDataSetChanged();
                  index = 1;
                  loadLiveData(true);
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
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.rl_main_left:
               back();
               break;
       }
    }


    /****
     * vip留言列表
     *****/
    private class ObtainDataLister extends ObtainDataFromNetListener<LeaveBean, String> {
        private MyLeaveMessageActivity weak_activity;
        private boolean isReset;
        public ObtainDataLister(Boolean isReset,MyLeaveMessageActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.isLoadEnd = false;
                if (isReset) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weak_activity.clearAdapete();
                        }
                    });
                }
            }
        }

        @Override
        public void onSuccess(final LeaveBean res) {
            if (weak_activity != null) {
                if (res != null) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weak_activity.flushContent_OnSucess(isReset, res.getData());
                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
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
    public void flushContent_OnSucess(boolean isReset, List<LeaveBean.DataEntity> res) {
//        MessageMethod();
        if (isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
//            ToastUtils.showToast(R.string.no_data);
            mPullToRefreshLayout.setVisibility(View.GONE);
            rl_no_teacher_message.setVisibility(View.VISIBLE);
        } else if (!isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_more);
        } else {
            mPullToRefreshLayout.setVisibility(View.VISIBLE);
            rl_no_teacher_message.setVisibility(View.GONE);
            hasMoreData = true;
            leavebean.addAll(res);
        }
        completeRefresh();
    }

    public void flushContent_OnFailure(String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
//            rl_wifi.setVisibility(View.VISIBLE);
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

        if (leaveListAdapter != null) {
            leaveListAdapter.setMessageBeanList(leavebean);
            leaveListAdapter.notifyDataSetChanged();
        }

        if (hasMoreData) {
            isLoadEnd = true;
        } else {
            isLoadEnd = false;
        }
    }

    /**
     * 清空适配器
     */
    public void clearAdapete() {
        leavebean.clear();
        leaveListAdapter.setMessageBeanList(leavebean);
        leaveListAdapter.notifyDataSetChanged();
    }

}
