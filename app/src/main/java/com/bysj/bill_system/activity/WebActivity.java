package com.bysj.bill_system.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bysj.bill_system.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebActivity extends BaseActivity {


    @BindView(R.id.vStub)
    View vStub;
    @BindView(R.id.rlBack)
    RelativeLayout rlBack;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.wvWeb)
    WebView wvWeb;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @Override
    void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_web);
    }

    @Override
    void initData() {
        wvWeb.setWebViewClient(new WebViewClient());
        wvWeb.getSettings().setSupportMultipleWindows(false);
        WebSettings settings = wvWeb.getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setJavaScriptEnabled(true);
        tvTitle.setText(getIntent().getStringExtra("title"));
        wvWeb.loadUrl(getIntent().getStringExtra("url"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.rlBack)
    public void onViewClicked() {
        if (wvWeb.canGoBack())
            wvWeb.goBack();
        else
            finish();
    }
}
