package com.huatu.teacheronline;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huatu.teacheronline.bean.EventMessage;
import com.huatu.teacheronline.login.LoginActivity;
import com.huatu.teacheronline.personal.SsoDialog;
import com.huatu.teacheronline.utils.AppManager;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.UserInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

/**
 * 基础Fragment类
 *
 * @author ljyu
 * @time 2017-6-21 10:16:40
 * 对广播、网络状态变化等
 * 进行了处理
 */
public abstract class BaseFragment extends Fragment {
    private boolean isShowing;//避免重复弹窗

    protected static final String TAG = BaseFragment.class.getName();
    protected View view;

    /**
     * 使用者定义并使用的广播
     */
    private BroadcastReceiver broadcastReceiver;
    /**
     * 网络状态转变的广播
     */
    private BroadcastReceiver connectionChangeReceiver;
    /**
     * 使用者定义的广播意图过滤器
     */
    private IntentFilter intentFilter = new IntentFilter();

    private boolean connetionChangeEnable = true;
    /**
     * 标志网络是否连接着
     */
    private boolean isNetConnected = true;

    private long onCreateTime;

    public BaseFragment() {
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, int resource) {
        onCreateTime = new Date().getTime();

        view = inflater.inflate(resource, container, false);
        init();

        //注册eventBus
        EventBus.getDefault().register(this);
        AppManager.getInstance().addActivity(getActivity());//添加当前Activity到堆栈
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
//        if (!this.getClass().getName().
//                equals(AppManager.getInstance().currentActivity().getClass().)) {//如果是当前的Activity 才弹出
//            return;
//        }
        if (event.getType() == EventMessage.NEED_RE_LOGIN) {
            String errCode = (String) event.getObject();
            if ("401".equals(errCode)) {
                if (isShowing) { //已经弹出
                    return;
                }
                isShowing = true;
                new SsoDialog(getActivity()).builder().setPositiveButton("", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
                        CustomApplication.applicationContext.removeAlias(uid);
                        UserInfo.registerNumber = null;
                        CommonUtils.clearSharedPreferenceItems();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivity(intent);
                        isShowing = false;
                        getActivity().finish();
                    }
                }).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DebugUtil.d(TAG, "create view used time:" + (new Date().getTime() - onCreateTime) + "ms");
        onCreateTime = new Date().getTime();
    }


    /**
     * 在最早版本的应用中出现的，已经很少使用了
     */
    @Deprecated
    public abstract void onRefresh();

    /**
     * 默认不开启注册广播，设置广播的过滤同时开启广播
     *
     * @param intentFilter 此activity中的广播过滤器
     */
    public void setIntentFilter(IntentFilter intentFilter) {
        this.intentFilter = intentFilter;
        registerBroadCast();
    }

    @Override
    public void onDestroy() {
        unRegisterBroadCast();
        EventBus.getDefault().unregister(this);//解除订阅
        super.onDestroy();
    }

    /**
     * 在activity被销毁时，注销广播
     */
    private void unRegisterBroadCast() {
        if (broadcastReceiver != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }

        if (connectionChangeReceiver != null) {
            getActivity().unregisterReceiver(connectionChangeReceiver);
            connectionChangeReceiver = null;
        }
    }

    /**
     * @return 是否注册网络状态变化的广播，如果否请在initData之前或之中设置
     */
    public boolean isConnetionChangeEnable() {
        return connetionChangeEnable;
    }

    /**
     * @param connetionChangeEnable 是否注册网络状态变化的广播，如果否请在initData之前或之中设置
     */
    public void setConnetionChangeEnable(boolean connetionChangeEnable) {
        this.connetionChangeEnable = connetionChangeEnable;
    }


    /**
     * 为fragment准备的广播接收器，主要为了实现fragment在需要刷新的时候刷新，也可以有其他用途
     */
    public void registerBroadCast() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    onReceiveBroadCast(context, intent);
                }
            };

            getActivity().registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    /**
     * 当接受到广播后的处理
     *
     * @param context 接受广播的上下文
     * @param intent  该广播的意图
     */
    public void onReceiveBroadCast(Context context, Intent intent) {

    }

    /**
     * fragment的findViewById
     *
     * @param id 视图的id
     */
    public View findViewById(int id) {
        if (view != null) {
            return view.findViewById(id);
        } else {
            return null;
        }
    }

    /**
     * 运行在onCreate中，实现视图和数据的初始化
     */
    public void init() {
        initView();
        initData();
        registerConnectionChange();
    }

    /**
     * 注册网络状态变更的广播
     */
    private void registerConnectionChange() {
        DebugUtil.i(TAG, "registerConnectionChange");
        if (connetionChangeEnable && connectionChangeReceiver == null) {
            connectionChangeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    DebugUtil.i(TAG, "on connection change");
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    NetworkInfo mobileNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                    // wifi、移动网络都没有连接
                    if ((wifiNetInfo == null || !wifiNetInfo.isConnected()) && (mobileNetInfo == null || !mobileNetInfo.isConnected())) {
                        isNetConnected = false;
                        onNetBreakUp();
                    } else {
                        if (!isNetConnected) {// 重新连接的状态
                            isNetConnected = true;
                            onNetReConnected();
                        }
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            getActivity().registerReceiver(connectionChangeReceiver, intentFilter);
        }
    }

    /**
     * 当网络中断时的处理
     */
    public void onNetBreakUp() {
        DebugUtil.i(TAG, "net break up");
    }

    /**
     * 当网络重新连接上时的处理
     */
    public void onNetReConnected() {
        DebugUtil.i(TAG, "net re-connected");
    }

    /**
     * 初始化视图
     */
    public void initView() {
    }

    ;

    /**
     * 加载数据
     */
    public void initData() {
    }

    public void onNetChange() {
        DebugUtil.i(TAG, "net connection change");
    }

    public void onNetReMobileNet() {
        DebugUtil.i(TAG, "net connection of mobile");
    }

    public void onNetReWifiNet() {
        DebugUtil.i(TAG, "net connection of wifi");
    }

}
