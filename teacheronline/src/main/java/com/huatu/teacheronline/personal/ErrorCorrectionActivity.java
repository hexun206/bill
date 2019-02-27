package com.huatu.teacheronline.personal;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.H5DetailActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.adapter.NewErrorCorrectionAdapter;
import com.huatu.teacheronline.personal.bean.MyErrorCorrectionBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
/**
 *  我的纠错
 * Created by ply on 2016/1/5.
 */
public class ErrorCorrectionActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private View loadView;
    private View loadIcon;
    private RotateAnimation refreshingAnimation;
    private ListView listview;
    private List<MyErrorCorrectionBean> myErrorCorrectionBeanList = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private String uid;
    private RelativeLayout rl_main_left;
    private String url=SendRequest.ipForExercise+"httb/httbapi/common/viewQuestion?qid=";
//    private MyErrorCorrectionAdapter adapter;

    private NewErrorCorrectionAdapter adapter;

    @Override
    public void initView() {
        setContentView(R.layout.activity_error_correction);
        TextView tv_main_title= (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("我的纠错");
        listview = (ListView) findViewById(R.id.listview);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");



        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        listview.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        adapter = new NewErrorCorrectionAdapter(this);
//        adapter = new MyErrorCorrectionAdapter(this);
        listview.setAdapter(adapter);
        loadData(true);
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        listview.setOnItemClickListener(this);
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                MobclickAgent.onEvent(ErrorCorrectionActivity.this, "Myerror");//我的纠错
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (isLoadEnd) {
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            listview.addFooterView(loadView);
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

    public void loadData(final boolean isReset) {
        if (isReset) {
            currentPage = 1;
        } else {
            currentPage++;
        }
        ObtatinDataListener obtatinDataListener = new ObtatinDataListener(isReset, this);
        SendRequest.getMyErrorCorrection(uid, currentPage,obtatinDataListener);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        H5DetailActivity.newIntent(this, "纠错试题",url+myErrorCorrectionBeanList.get(position).getqid());
    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<List<MyErrorCorrectionBean>, String> {
        private ErrorCorrectionActivity weak_activity;
        private boolean isReset;

        public ObtatinDataListener(Boolean isReset, ErrorCorrectionActivity fragment) {
            weak_activity = new WeakReference<>(fragment).get();
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
        if (listview != null && listview.getFooterViewsCount() > 0) {
            listview.removeFooterView(loadView);
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
    /**
     * 清空适配器
     */
    public void clearAdapete() {
        myErrorCorrectionBeanList.clear();
        adapter.setDirectBeanList(myErrorCorrectionBeanList);
        adapter.notifyDataSetChanged();
    }
}
