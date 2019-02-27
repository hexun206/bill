package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.bean.FeedListBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.swipemenulistview.SwipeMenuListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
/**
 *  意见反馈
 * Created by ply on 2016/1/5.
 */
public class FeedListActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private SwipeRefreshLayout mPullToRefreshLayout;
    private SwipeMenuListView listView;
    private View loadView;
    private View loadIcon;
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;
    private int page = 1;
    private int pagesize = 9;
    private String uid;
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private FeedAdapter adapter;
    private ArrayList<FeedListBean.DataEntity> messageBeanList = new ArrayList<FeedListBean.DataEntity>();

    @Override
    public void initView() {
        setContentView(R.layout.activity_feed_list);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        TextView tv_title= (TextView) findViewById(R.id.tv_main_title);
        tv_title.setText("意见反馈");
        mPullToRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.live_refresh_layout);
        listView = (SwipeMenuListView)findViewById(R.id.listview);
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.pull_to_refresh_and_load_rotating);
        adapter = new FeedAdapter();
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        listView.setAdapter(adapter);
        listView.setParentRefreshlayout(mPullToRefreshLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FeedListData(true);
    }

    @Override
    public void setListener() {
       findViewById(R.id.rl_xzfk).setOnClickListener(this);
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        listView.setOnItemClickListener(this);
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FeedListData(true);
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
                            FeedListData(false);
                        }
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.rl_xzfk:
               NewFeedBackActivity.newIntent(this);
               break;
           case R.id.rl_main_left:
               back();
               break;
       }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           FeeddetailsActivity.newIntent(this, messageBeanList.get(position).getfeedback_id());
    }

    class FeedAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return messageBeanList == null ? 0 : messageBeanList.size();
        }

        @Override
        public FeedListBean.DataEntity getItem(int position) {
            return (messageBeanList == null ? null : messageBeanList.get(position));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView=getLayoutInflater().inflate(R.layout.item_feed_list,null);
                holder.tv_start_time= (TextView) convertView.findViewById(R.id.tv_start_time);
                holder.tv_reply= (TextView) convertView.findViewById(R.id.tv_reply);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            FeedListBean.DataEntity feedlistbean = getItem(position);
            holder.tv_start_time.setText(feedlistbean.getCreate_time());
            if (feedlistbean.getIs_feedback().equals("0")){
                holder.tv_reply.setText("未回复");
                holder.tv_reply.setTextColor(getResources().getColor(R.color.gray013));
            }else{
                holder.tv_reply.setText("已回复");
                holder.tv_reply.setTextColor(getResources().getColor(R.color.green013));
            }
            return convertView;
        }
    }
    public static void newIntent(Activity context) {
        Intent goldPersonIntent = new Intent(context, FeedListActivity.class);
        context.startActivity(goldPersonIntent);
    }

    class ViewHolder {
        public TextView tv_start_time, tv_reply;
    }

    /**
     * 获取我的反馈列表
     *
     * @param
     */
    public void FeedListData(final boolean isReset) {
        if (isReset) {
            page = 1;
        } else {
            page++;
        }
        ObtatinDataListener   obtatinDataListener = new ObtatinDataListener(isReset, this);
        SendRequest.getFeedbackList(uid,String.valueOf(page) ,String.valueOf(pagesize),obtatinDataListener);
    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<List<FeedListBean.DataEntity>, String> {
        private FeedListActivity weak_activity;
        private boolean isReset;

        public ObtatinDataListener(Boolean isReset, FeedListActivity activity) {
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
        public void onSuccess(final List<FeedListBean.DataEntity> res) {
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

    public void flushContent_OnSucess(boolean isReset, List<FeedListBean.DataEntity> res) {
        if (isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_data);
        } else if (!isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_more);
        } else {
            hasMoreData = true;
            messageBeanList.addAll(res);
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

        if (adapter != null) {
            adapter.notifyDataSetChanged();
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
        messageBeanList.clear();
        adapter.notifyDataSetChanged();
    }
}
