package com.bysj.bill_system.activity;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.bysj.bill_system.utils.LoadingWindow;
import com.bysj.bill_system.utils.ScreenUtils;

public abstract class BaseActivity extends AppCompatActivity {
    private LoadingWindow mLoadingWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(savedInstanceState);
        ButterKnife.bind(this);
        initData();
    }

    abstract void initView(Bundle savedInstanceState);

    abstract void initData();

    protected void showLoadingDialog() {
        if (mLoadingWindow == null) {
            mLoadingWindow = new LoadingWindow(this);
        }
        mLoadingWindow.show();
    }

    protected void hiddenLoadingDialog() {
        if (mLoadingWindow != null) {
            mLoadingWindow.dismiss();
        }
    }

    /**
     * 设置状态栏占位
     *
     * @param status_bar
     */
    public void setStatusBarHeight(View status_bar) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            status_bar.getLayoutParams().height = ScreenUtils.getStatusBarHeight(this);
            status_bar.requestLayout();
        }
    }
}
