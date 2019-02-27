package com.huatu.teacheronline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;

import com.huatu.teacheronline.bean.EventMessage;
import com.huatu.teacheronline.login.LoginActivity;
import com.huatu.teacheronline.personal.SsoDialog;
import com.huatu.teacheronline.utils.AppManager;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class BaseFragmentActivity extends FragmentActivity implements View.OnClickListener {

    public boolean isDestoryed;
    private boolean isShowing;//避免重复弹窗
    @Subscribe
    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        isDestoryed = false;
        initView();
        setListener();
        //注册eventBus
        EventBus.getDefault().register(this);
        AppManager.getInstance().addActivity(this);//添加当前Activity到堆栈
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
                new SsoDialog(this).builder().setPositiveButton("", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
                        CustomApplication.applicationContext.removeAlias(uid);
                        UserInfo.registerNumber = null;
                        CommonUtils.clearSharedPreferenceItems();
                        Intent intent = new Intent(BaseFragmentActivity.this, LoginActivity.class);
                        BaseFragmentActivity.this.startActivity(intent);
                        isShowing = false;
                        BaseFragmentActivity.this.finish();
                    }
                }).show();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 初始化控件
     */
    public abstract void initView();

    /**
     * 设置点击事件
     */
    public abstract void setListener();

    public abstract boolean back();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            return back();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);//移除Activity
        isDestoryed = true;
//        CustomApplication.getRefWatcher().watch(this);
        EventBus.getDefault().unregister(this);//解除订阅
    }
}
