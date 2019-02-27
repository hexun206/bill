package com.huatu.teacheronline.vipexercise;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.huatu.teacheronline.widget.SupportPopupWindow;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 *  VIP题库
 * Created by ply on 2016/1/5.
 */
public class VipQuestionsActivity extends BaseActivity {
    private ListView listView;
    private VipAdapter adapter;
    private ImageView iv_main_righ;
    private RelativeLayout rl_main_right;

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
    private ImageView iv_vipred;
    private int Msgmark=0;//判断是否存在未读消息：0不存在，1存在


    @Override
    public void initView() {
        setContentView(R.layout.activity_vip_questions);
        userid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        TextView tv_title= (TextView) findViewById(R.id.tv_main_title);
        iv_main_righ = (ImageView) findViewById(R.id.iv_main_righ);
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        rl_main_right.setVisibility(View.VISIBLE);
        rl_wifi = (RelativeLayout) findViewById(R.id.rl_wifi);
        rl_nodata = (PercentRelativeLayout) findViewById(R.id.rl_nodata);

        iv_vipred = (ImageView) findViewById(R.id.iv_vipred);

        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.vip_list_refresh_layout);
        listView = (ListView) findViewById(R.id.listview);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        tv_title.setText("VIP题库");
        listView = (ListView) findViewById(R.id.listview);
        adapter = new VipAdapter(this);
        listView.setAdapter(adapter);
        loadVipListData(true,currentPage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MarkData();

    }

    private void MarkData() {
        ObtatinMsgListener obtatinMsgListener = new ObtatinMsgListener(this);
        SendRequest.getMessageMark(userid, obtatinMsgListener);
    }

    @Override
    public void setListener() {
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
        findViewById(R.id.rl_main_right).setOnClickListener(this);
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                vipPaperBeans.clear();
                adapter.setData(vipPaperBeans);
                adapter.notifyDataSetChanged();
                currentPage = 1;
                loadVipListData(true, currentPage);
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
                            loadVipListData(false, currentPage);
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
                loadVipQuestionListData(vipPaperBeans.get(position).getExclusiveId());
            }
        });

    }

    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.rl_main_left:
            back();
            break;

        case R.id.rl_main_right:
            Prompt_time();
            break;

    }
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, VipQuestionsActivity.class);
        context.startActivity(intent);
    }


    /*
     vip弹窗
    */
    public void Prompt_time() {
        View inflate = getLayoutInflater().inflate(R.layout.pop_vipques, null);
        final SupportPopupWindow mPopupWindow = new SupportPopupWindow(inflate, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ImageView    iv_vip_popred = (ImageView)inflate.findViewById(R.id.iv_vip_popred);
        if (Msgmark==1){
            iv_vip_popred.setVisibility(View.VISIBLE);
        }else{
            iv_vip_popred.setVisibility(View.GONE);
        }
        //VIP错题
        inflate.findViewById(R.id.ll_myError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TitleErrorActivity.newIntent(VipQuestionsActivity.this);
                mPopupWindow.dismiss();
            }
        });

        inflate.findViewById(R.id.rl_pop_vip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        inflate.findViewById(R.id.rl_mymessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VipQuestionsActivity.this, MyLeaveMessageActivity.class));
                mPopupWindow.dismiss();
            }
        });
        BitmapDrawable bitmapDrawable = new BitmapDrawable();
        mPopupWindow.setBackgroundDrawable(bitmapDrawable);
        mPopupWindow.showAsDropDown(iv_main_righ, 0, 0);
    }

    /**
     * 获取试卷列表
     *
     * @param
     */
    public void loadVipListData(final boolean isReset, int currentPage) {
        obtatinDataListener = new ObtatinDataListener(isReset, this);
        SendRequest.getQuestionExclusiveList(userid, currentPage, limit, obtatinDataListener);
    }
    /**
     * 获取试题集
     *
     * @param
     */
    public void loadVipQuestionListData(String exclusiveId) {
        obtatinQuestionDataListener = new ObtatinQuestionDataListener(this);
        SendRequest.getVipQuestionList(exclusiveId, obtatinQuestionDataListener);
    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<List<VipPaperBean>, String> {
        private VipQuestionsActivity vipQuestionsActivity;
        private boolean isReset;

        public ObtatinDataListener(Boolean isReset, VipQuestionsActivity fragment) {
            vipQuestionsActivity = new WeakReference<>(fragment).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (vipQuestionsActivity != null) {
                vipQuestionsActivity.isLoadEnd = false;
                vipQuestionsActivity.listView.setVisibility(View.VISIBLE);
                vipQuestionsActivity.rl_wifi.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSuccess(final List<VipPaperBean> res) {
            if (vipQuestionsActivity != null) {
                vipQuestionsActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vipQuestionsActivity.flushContent_OnSucess(isReset, res);
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (vipQuestionsActivity != null) {
                vipQuestionsActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vipQuestionsActivity.flushContent_OnFailure(isReset, res);
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
        private VipQuestionsActivity vipQuestionsActivity;

        public ObtatinQuestionDataListener(VipQuestionsActivity fragment) {
            vipQuestionsActivity = new WeakReference<>(fragment).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (vipQuestionsActivity != null) {
                vipQuestionsActivity.isLoadEnd = false;
                vipQuestionsActivity.listView.setVisibility(View.VISIBLE);
                vipQuestionsActivity.rl_wifi.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSuccess(final VipQuestionBean res) {
            if (vipQuestionsActivity != null) {
                vipQuestionsActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vipQuestionsActivity.flushQuestionContent_OnSucess(res);
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
            if (vipQuestionsActivity != null) {
                vipQuestionsActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vipQuestionsActivity.flushQuestionContent_OnFailure(res);
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
            DataStore_VipExamLibrary.resetDatas();
            DataStore_VipExamLibrary.VipQuestionBean = res;
            DataStore_VipExamLibrary.VipPaperBean = currentVipPaperBean;
            DataStore_VipExamLibrary.ResultListBeanDetailList = res.getResultList();
            DoVipQuestionActivity.newIntent(this,0,5);
        }
    }


    private static class ObtatinMsgListener extends ObtainDataFromNetListener<Integer, String> {
        private VipQuestionsActivity vipQuestionsActivity;

        public ObtatinMsgListener( VipQuestionsActivity fragment) {
            vipQuestionsActivity = new WeakReference<>(fragment).get();
        }

        @Override
        public void onSuccess(final Integer res) {
            if (vipQuestionsActivity != null) {
                vipQuestionsActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                if (res!=null){
                    vipQuestionsActivity.Msgmark=res;
                    //res 判断是否存在未读消息：0不存在，1存在
                    if (res==1){
                        vipQuestionsActivity.iv_vipred.setVisibility(View.VISIBLE);
                    }else{
                        vipQuestionsActivity.iv_vipred.setVisibility(View.GONE);
                    }
                }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (SendRequest.ERROR_NETWORK.equals(res)) {
                ToastUtils.showToast(R.string.network);
            } else if (SendRequest.ERROR_SERVER.equals(res)) {
                ToastUtils.showToast(R.string.server_error);
            }
            }
        }

}
