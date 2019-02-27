package com.huatu.teacheronline.personal.frament;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.adapter.FaceListAdapter;
import com.huatu.teacheronline.personal.bean.CouserBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.swipemenulistview.SwipeMenuListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * A我的课程班级
 */
public class MyDirectTabFragment extends Fragment {
    public static final String ACTION_NEXT = "action_MyDirect";

    public static final String KEY_ISWISDOMCLASS = "key_iswisdomclass";

    private View inflate;
    private Context context;
    private SwipeRefreshLayout mPullToRefreshLayout;
    private SwipeMenuListView lv_face;
    private CustomAlertDialog mCustomDelDirectDilog;
    private CustomAlertDialog mCustomLoadingDialog;
    private View loadView;
    private View loadIcon;
    private int index=1;
    private int limit=4;
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;
    private FaceListAdapter faceListAdapter;
    private ArrayList<CouserBean> couserbeanslist=new ArrayList<>();
    private String uid;
    private boolean hasMoreData = true;
    private boolean isLoadEnd;
    private RelativeLayout rl_nodata;

    private RelativeLayout rl_wifi;

    private boolean isWisdomClass;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments!=null){
            isWisdomClass = arguments.getBoolean(KEY_ISWISDOMCLASS,false);
        }

        context = getActivity().getApplicationContext();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_my_direct_tab, container, false);
        registerReceiver();
        initView();
        setListener();
        LoadNetOrCashData();//课程列表数据
        return inflate;
    }

    private void initView() {
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        mPullToRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.live_refresh_layout);
        lv_face = (SwipeMenuListView) inflate.findViewById(R.id.lv_face);
        mCustomDelDirectDilog = new CustomAlertDialog(getActivity(), R.layout.dialog_join_mydirect);
        mCustomLoadingDialog = new CustomAlertDialog(getActivity(), R.layout.dialog_loading_custom);
        rl_nodata = (RelativeLayout) inflate.findViewById(R.id.rl_nodata);
        rl_wifi = (RelativeLayout) inflate.findViewById(R.id.rl_wifi);
        mCustomDelDirectDilog.setTitle("提示<br/>课程确定删除？");
        loadView = getActivity().getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                getActivity(), R.anim.pull_to_refresh_and_load_rotating);
        faceListAdapter = new FaceListAdapter(getActivity(),couserbeanslist,isWisdomClass);
        lv_face.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        lv_face.setParentRefreshlayout(mPullToRefreshLayout);
        lv_face.setAdapter(faceListAdapter);
    }

    private void setListener() {
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               LoadNetOrCashData();
            }
        });
        lv_face.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (isLoadEnd) {
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            lv_face.addFooterView(loadView);
                            loadIcon.startAnimation(refreshingAnimation);
                            loadLiveData(false);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }
    private void LoadNetOrCashData(){
        if (CommonUtils.isNetWorkAvilable()){
            loadLiveData(true);
            rl_wifi.setVisibility(View.GONE);
        }else{
           if (couserbeanslist.size()==0||couserbeanslist==null){
               rl_wifi.setVisibility(View.VISIBLE);
               ToastUtils.showToast(R.string.network);
               return;
           }
        }

    }
    /**
     * 获取我的课程通知列表
     *
     * @param
     */
    public void loadLiveData(final boolean isReset){
        if (isReset) {
            index = 1;
        } else {
            index++;
        }
        Obtatinmycourse obtatinmycourse= new Obtatinmycourse(isReset,this);

        if(isWisdomClass){
            SendRequest.getmyWisdomClass(uid, index + "", limit + "", obtatinmycourse);
        }else{
            SendRequest.getmycourse(uid, index + "", limit + "", obtatinmycourse);
        }


    }
    private static class Obtatinmycourse extends ObtainDataFromNetListener<List<CouserBean>,String >{
        private MyDirectTabFragment weak_activity;
        private boolean isReset;
        public Obtatinmycourse( Boolean isReset,MyDirectTabFragment activity) {
            weak_activity = new WeakReference<>(activity).get();
            this.isReset = isReset;
        }
        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.isLoadEnd = false;
                if (isReset) {
                    weak_activity.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weak_activity.clearAdapete();
                        }
                    });
                }
            }
        }

        @Override
        public void onSuccess(final List<CouserBean> res) {
            if (weak_activity != null) {
                weak_activity.getActivity().runOnUiThread(new Runnable() {
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
                weak_activity.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushContent_OnFailure(res);
                    }
                });

        }
        }
    }

    private void flushContent_OnSucess(boolean isReset, List<CouserBean> res) {
        if (isReset && (res == null || res.size() == 0)){
            hasMoreData = false;
            rl_nodata.setVisibility(View.VISIBLE);
            ToastUtils.showToast(R.string.no_data);
        }else if (!isReset && (res == null || res.size() == 0)){
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_more);
            rl_nodata.setVisibility(View.GONE);
        }else {
            rl_nodata.setVisibility(View.GONE);
            hasMoreData = true;
            couserbeanslist.addAll(res);
        }
        completeRefresh();
    }

    /**
     * 上拉刷新
     */
    private void completeRefresh() {
        mPullToRefreshLayout.setRefreshing(false);
        if (lv_face != null && lv_face.getFooterViewsCount() > 0) {
            lv_face.removeFooterView(loadView);
        }

        if (faceListAdapter != null) {
            faceListAdapter.setCousrBeanList(couserbeanslist);
            faceListAdapter.notifyDataSetChanged();
        }

        isLoadEnd = hasMoreData;
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
     * 清空适配器
     */
    public void clearAdapete() {
        couserbeanslist.clear();
        faceListAdapter.setCousrBeanList(couserbeanslist);
        faceListAdapter.notifyDataSetChanged();
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//    }

    private void registerReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NEXT);
        mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                LoadNetOrCashData();//课程列表数据
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    private LocalBroadcastManager broadcastManager;
    BroadcastReceiver mItemViewListClickReceiver;
    @Override
    public void onDestroy() {
        broadcastManager.unregisterReceiver(mItemViewListClickReceiver);
        super.onDestroy();
    }
}
