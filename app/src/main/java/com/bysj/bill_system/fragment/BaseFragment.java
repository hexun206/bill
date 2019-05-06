package com.bysj.bill_system.fragment;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bysj.bill_system.utils.LoadingWindow;
import com.bysj.bill_system.utils.ScreenUtils;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    protected String TAG;
    protected Context mContext;
    protected Activity mActivity;
    private Unbinder binder;
    private LoadingWindow mLoadingWindow;

    @Override
    public void onAttach(Context context) {
        mActivity = (Activity) context;
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        if (getLayoutView() != null) {
            return getLayoutView();
        } else {
            return inflater.inflate(getLayoutId(), container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TAG = getClass().getSimpleName();
        binder = ButterKnife.bind(this, view);
        getBundle(getArguments());
        initData();
        initUI(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binder != null)
            binder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @LayoutRes
    public abstract int getLayoutId();

    public View getLayoutView() {
        return null;
    }

    /**
     * 得到Activity传进来的值
     */
    public void getBundle(Bundle bundle) {
    }

    /**
     * 初始化UI
     */
    public abstract void initUI(View view, @Nullable Bundle savedInstanceState);

    /**
     * 在监听器之前把数据准备好
     */
    public void initData() {
        mContext = getContext();
    }

    protected void showLoadingDialog() {
        if (mLoadingWindow == null) {
            mLoadingWindow = new LoadingWindow(getContext());
        }
        try {

            mLoadingWindow.show();
        } catch (Exception e) {

        }
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
            status_bar.getLayoutParams().height = ScreenUtils.getStatusBarHeight(mContext);
            status_bar.requestLayout();
        }
    }
}
