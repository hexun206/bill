package com.huatu.teacheronline.vipexercise;

import android.app.Activity;
import android.content.Intent;
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
import com.huatu.teacheronline.personal.adapter.MyDataAdapter;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.vipexercise.vipbean.VipDataBean;
import com.huatu.teacheronline.widget.CustomAlertDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的资料列表
 * @auto cwqiang 2017-9-20 14:43:30
 */
public class MyDataActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private View loadView;
    private View loadIcon;
    private RotateAnimation refreshingAnimation;
    private String uid;
    private int index=1;
    private int limit = 4;
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private SwipeRefreshLayout mPullToRefreshLayout;
    private List<VipDataBean.DataEntity> vipdatabean=new ArrayList<>();
    private MyDataAdapter adapter;
    private CustomAlertDialog mCustomLoadingDialog;
    private RelativeLayout rl_no_data;

    @Override
    public void initView() {
        setContentView(R.layout.activity_my_data);
        TextView tv_title= (TextView) findViewById(R.id.tv_main_title);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        tv_title.setText("我的资料");
        listView = (ListView) findViewById(R.id.listview);
        mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_refresh_layout);
        rl_no_data = (RelativeLayout) findViewById(R.id.rl_no_data);
//        MyDataAdapter adapter=  new MyDataAdapter(this);
//        listView.setAdapter(adapter);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        adapter = new MyDataAdapter(this, vipdatabean,this);
        listView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLiveData(true);
    }

    private void loadLiveData(boolean isReset) {
        if (isReset) {
            index = 1;
        } else {
            index++;
        }
        ObtainDataLister obtainDataLister =new ObtainDataLister(isReset,this);
        SendRequest.getMyVipDataList(uid, String.valueOf(index), String.valueOf(limit), obtainDataLister);
    }

    @Override
    public void setListener() {
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        listView.setOnItemClickListener(this);
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                vipdatabean.clear();
                adapter.setvipDataBeanList(vipdatabean);
                adapter.notifyDataSetChanged();
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
            case R.id.iv_zan://点赞，如果已经点过，监听失效
                int position = (int)v.getTag();
                if (vipdatabean.get(position).getIsLike()==1){
                    return;
                }else{
                    FabulousData(position);
                }
                break;

        }
    }

    public void FabulousData(int position){
        ObtainFabulousLister fabulousLister =new ObtainFabulousLister(MyDataActivity.this);
        SendRequest.getipFabulous(uid,vipdatabean.get(position).getMaterialId()+"",fabulousLister);
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, MyDataActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyDatapptActivity.newIntent(this,vipdatabean.get(position).getMaterialId()+"",vipdatabean.get(position).getTitle());
    }


    /****
     * vip我的资料列表·
     *****/
    private class ObtainDataLister extends ObtainDataFromNetListener<VipDataBean, String> {
        private MyDataActivity weak_activity;
        private boolean isReset;
        public ObtainDataLister(Boolean isReset,MyDataActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.show();
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
        public void onSuccess(final VipDataBean res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
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
            weak_activity.mCustomLoadingDialog.dismiss();
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        }
                        completeRefresh();
                    }
                });
            }
        }
    }

    /**
     * 清空适配器
     */
    public void clearAdapete() {
        vipdatabean.clear();
        adapter.setvipDataBeanList(vipdatabean);
        adapter.notifyDataSetChanged();
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
            adapter.setvipDataBeanList(vipdatabean);
            adapter.notifyDataSetChanged();
        }

        if (hasMoreData) {
            isLoadEnd = true;
        } else {
            isLoadEnd = false;
        }
    }

    public void flushContent_OnSucess(boolean isReset, List<VipDataBean.DataEntity> res) {
        if (isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_data);
            mPullToRefreshLayout.setVisibility(View.GONE);
            rl_no_data.setVisibility(View.VISIBLE);
        } else if (!isReset && (res == null || res.size() == 0)) {
            mPullToRefreshLayout.setVisibility(View.VISIBLE);
            rl_no_data.setVisibility(View.GONE);
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_more);
        } else {
            mPullToRefreshLayout.setVisibility(View.VISIBLE);
            rl_no_data.setVisibility(View.GONE);
            hasMoreData = true;
            vipdatabean.addAll(res);
        }
        completeRefresh();
    }

    /****
     * vip我的点赞·
     *****/
    private class ObtainFabulousLister extends ObtainDataFromNetListener<String, String> {
        private MyDataActivity weak_activity;
        public ObtainFabulousLister(MyDataActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }
        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                if (res != null) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadLiveData(true);
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

}
