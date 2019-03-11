package com.huatu.teacheronline;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.huatu.teacheronline.bean.EventMessage;
import com.huatu.teacheronline.login.LoginActivity;
import com.huatu.teacheronline.personal.SsoDialog;
import com.huatu.teacheronline.utils.AppManager;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.UserInfo;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class BaseActivity extends FragmentActivity implements OnClickListener {
    private boolean isShowing;//避免重复弹窗
    public boolean isDestoryed;
    protected boolean isLandscape;
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
    /**
     * 使用者定义的广播刷新
     */
    public static final String ACTION_REFRESH = "action_refresh";
    public static final String TAG = "BaseActivity";

    private boolean connetionChangeEnable = true;
    /**
     * 标志网络是否连接着
     */
    private boolean isNetConnected = true;
    private AlertDialog mLoadingDialog;

    @SuppressLint("WrongConstant")
    @Subscribe
    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        isDestoryed = false;
        initView();
        setListener();
        registerConnectionChange();
        //注册eventBus
        EventBus.getDefault().register(this);
        AppManager.getInstance().addActivity(this);//添加当前Activity到堆栈
        this.isLandscape = this.getWindowManager().getDefaultDisplay().getRotation() == 1;
    }


    protected void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }


    }

    protected void showLoadingDialog() {

        mLoadingDialog = new AlertDialog.Builder(this, R.style.dialog_translucent)
                .setView(R.layout.dialog_loading)
                .setCancelable(false)
                .show();


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (!this.getClass().getName().
                equals(AppManager.getInstance().currentActivity().getClass().getName())) {//如果是当前的Activity 才弹出
            return;
        }
        if (event.getType() == EventMessage.NEED_RE_LOGIN) {
            String errCode = (String) event.getObject();
            if ("401".equals(errCode)) {
                if (isShowing) { //已经弹出
                    return;
                }
                isShowing = true;
                new SsoDialog(this).builder().setPositiveButton("", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
                        CustomApplication.applicationContext.removeAlias(uid);
                        UserInfo.registerNumber = null;
                        CommonUtils.clearSharedPreferenceItems();
                        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                        BaseActivity.this.startActivity(intent);
                        isShowing = false;
                        BaseActivity.this.finish();
                    }
                }).show();
            }
        }
    }

    /**
     * 初始化控件
     */
    public abstract void initView();

    /**
     * 设置点击事件
     */
    public abstract void setListener();

    /**
     * 处理返回键
     *
     * @return
     */
    public boolean back() {
        this.finish();

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            return back();
        }

        return super.onKeyDown(keyCode, event);
    }
    @SuppressLint("WrongConstant")
    public void onBackPressed() {
        if (this.isLandscape) {
            this.setRequestedOrientation(1);
        } else {
            super.onBackPressed();
        }
    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == 2) {
            this.isLandscape = true;
            this.requestLayout(true);
        } else {
            this.isLandscape = false;
            this.requestLayout(false);
        }

    }

    protected void requestLayout(boolean isLandscape) {
    }
    @Override
    protected void onPause() {
        super.onPause();
//        JPushInterface.onPause(BaseActivity.this);

        MobclickAgent.onPause(this);

//        StatService.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        JPushInterface.onResume(BaseActivity.this);

        MobclickAgent.onResume(this);

//        StatService.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isDestoryed = true;
        AppManager.getInstance().finishActivity(this);//移除Activity
        unRegisterBroadCast();
//        CustomApplication.getRefWatcher().watch(this);
        EventBus.getDefault().unregister(this);//解除订阅
    }

    /**
     * 默认不开启注册广播，设置广播的过滤同时开启广播
     *
     * @param intentFilter 此activity中的广播过滤器
     */
    public void setIntentFilter(IntentFilter intentFilter) {
        // Debug.d("TAG", "setIntentFilter:" + intentFilter.getAction(0));
        this.intentFilter = intentFilter;
        registerBroadCast();
    }

    /**
     * 默认不开启注册广播，设置广播的过滤同时开启广播
     * 此activity中的广播过滤器 主要用于刷新界面
     */
    public void setRefreshIntentFilter() {
        // Debug.d("TAG", "setIntentFilter:" + intentFilter.getAction(0));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH);
        this.intentFilter = intentFilter;
        registerBroadCast();
    }


    /**
     * 在activity被销毁时，注销广播
     */
    private void unRegisterBroadCast() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        if (connectionChangeReceiver != null) {
            unregisterReceiver(connectionChangeReceiver);
            connectionChangeReceiver = null;
        }
    }

    /**
     * 为activity准备的广播接收器，主要为了实现activity在需要刷新的时候刷新，也可以有其他用途
     */
    public void registerBroadCast() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    onReceiveBroadCast(context, intent);
                }
            };

            registerReceiver(broadcastReceiver, intentFilter);
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
     * @return 是否注册网络状态变化的广播，如果否请在initView之中设置
     */
    public boolean isConnetionChangeEnable() {
        return connetionChangeEnable;
    }

    /**
     * 是否注册网络状态变化的广播，如果否请在initView之中设置
     */
    public void setConnetionChangeEnable(boolean connetionChangeEnable) {
        this.connetionChangeEnable = connetionChangeEnable;
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
                        onNetChange();
                        //断开到连接
                        if (!isNetConnected) {// 重新连接的状态
                            isNetConnected = true;
                            onNetReConnected();
                            if (wifiNetInfo != null && wifiNetInfo.isConnected()) {
                                onNetReWifiNet();
                            }
                            if (mobileNetInfo != null && mobileNetInfo.isConnected()) {
                                onNetReMobileNet();
                            }
                        } else {
                            if (mobileNetInfo != null && mobileNetInfo.isConnected()) {
                                onNetReMobileNet();
                            }
                        }
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(connectionChangeReceiver, intentFilter);
        }
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


}
