package com.huatu.teacheronline.personal;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.adapter.MyBookAdapter;
import com.huatu.teacheronline.personal.bean.NoticeBean;
import com.huatu.teacheronline.personal.frament.MyDirectTabFragment;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.swipemenulistview.SwipeMenuListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 开课通知
 * @author
 * @time 2017-3-21
 */
public class BooknoticeActivity extends BaseActivity {


    private RelativeLayout rl_left;
    private TextView title;
    private SwipeRefreshLayout mPullToRefreshLayout;
    private SwipeMenuListView listView;
    private RotateAnimation refreshingAnimation;
    private View loadView;
    private View loadIcon;
    private int index=1;
    private int limit=6;
    private String uid;
    private boolean hasMoreData = true;
    private boolean isLoadEnd;
    private ArrayList<NoticeBean> noticeBeans=new ArrayList<>();
    private MyBookAdapter adapter;
    private int classId;

    // 均匀旋转动画
    @Override
    public void initView() {
        setContentView(R.layout.activity_booknotice);
        classId = getIntent().getIntExtra("classId", 0);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        rl_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        title = (TextView) findViewById(R.id.tv_main_title);
        title.setText("开课通知");
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.live_refresh_layout);
        listView = (SwipeMenuListView) findViewById(R.id.listview);
        adapter = new MyBookAdapter(this,noticeBeans);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        listView.setAdapter(adapter);
        listView.setParentRefreshlayout(mPullToRefreshLayout);
    }
    /**
     * 开课通知列表数据
     */
    private void NoticeData(final boolean isReset) {
        if (isReset) {
            index = 1;
        } else {
            index++;
        }
        ObtatinBookData obtatinmycourse= new ObtatinBookData(isReset,this);
        SendRequest.getsubjectNotice(uid, String.valueOf(classId), index + "", limit + "", obtatinmycourse);
    }
    private static class ObtatinBookData extends ObtainDataFromNetListener<List<NoticeBean>,String >{
        private BooknoticeActivity weak_activity;
        private boolean isReset;
        public ObtatinBookData( Boolean isReset,BooknoticeActivity activity) {
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
        public void onSuccess(final List<NoticeBean> res) {
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
    public void flushContent_OnSucess(boolean isReset, List<NoticeBean> res){
        if (isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
        } else if (!isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_more);
        } else {
            hasMoreData = true;
            noticeBeans.addAll(res);
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
    private void completeRefresh() {
        mPullToRefreshLayout.setRefreshing(false);
        if (listView != null && listView.getFooterViewsCount() > 0) {
            listView.removeFooterView(loadView);
        }
        if (adapter != null) {
            adapter.setNoticeBeanBeanList(noticeBeans);
            adapter.notifyDataSetChanged();
        }
        isLoadEnd = hasMoreData;
    }

    @Override
    public void setListener() {
        rl_left.setOnClickListener(this);
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NoticeData(true);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(BooknoticeActivity.this, DetailsnoticeActivity.class).putExtra("noticeId", noticeBeans.get(position).getNoticeId()));
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (isLoadEnd) {
                        if (view.getLastVisiblePosition() == view.getCount() - 1){
                            listView.addFooterView(loadView);
                            loadIcon.startAnimation(refreshingAnimation);
                            NoticeData(false);
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
    protected void onResume() {
        NoticeData(true);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.rl_main_left:
               Intent intent = new Intent(MyDirectTabFragment.ACTION_NEXT);
               LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
               back();
               break;
       }
    }
    /**
     * 清空适配器
     */
    public void clearAdapete() {
        noticeBeans.clear();
        adapter.setNoticeBeanBeanList(noticeBeans);
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(MyDirectTabFragment.ACTION_NEXT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            back();
        }
        return false;
    }
}
