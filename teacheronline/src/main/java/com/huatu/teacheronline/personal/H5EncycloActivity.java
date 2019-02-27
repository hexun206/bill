package com.huatu.teacheronline.personal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.HomeActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.UserInfo;
import com.umeng.socialize.UMShareAPI;

/**
 * H5展示教师百科页面
 *
 * @author ljyu
 * @date 2016-6-7 11:06:12
 */
public class H5EncycloActivity extends BaseActivity {
    @SuppressLint("JavascriptInterface")
    public static final String ACTION_NEXT = "action_h5";
    private String ad_title;
    //    private String ad_url = "http://5beike.hteacher.net/teacher/activity/testjs.html";
    private String ad_url = "";
    private WebView wv_h5detail;
    private String uid;//用户id
    private String pass;//用户密码
    private String account;//用户账户
    public static final String TAG = "H5DetailActivity";//用户账户
    private ProgressBar progressBar;
    private WebChromeClient.CustomViewCallback myCallback = null;
    private View myView;
    private int isPush = 0; //推送过来的消息
//    private RelativeLayout rl_main_left_text;
    private int splash_id;
    private String year;
    private String month;
    private String day;
    private int cycle;
    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;
    private String url_2;
    private String url_3;
    private TextView tv_one;
    private TextView tv_two;
    private TextView tv_three;

    @Override
    public void initView() {
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        pass = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PASSWORD, "");
        account = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, "");
        setContentView(R.layout.activity_encyclo);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ACTION_NEXT);
//        setIntentFilter(intentFilter);

        TextView tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        LinearLayout ll_webview = (LinearLayout) findViewById(R.id.ll_webview);
//        rl_main_left_text = (RelativeLayout) findViewById(R.id.rl_main_left_text);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_live);

        //底部按钮
        tv_one = (TextView) findViewById(R.id.tv_one);
        tv_two = (TextView) findViewById(R.id.tv_two);
        tv_three = (TextView) findViewById(R.id.tv_three);

        ad_title = getIntent().getStringExtra("title");
        ad_url = getIntent().getStringExtra("url_1");
        url_2 = getIntent().getStringExtra("url_2");
        url_3 = getIntent().getStringExtra("url_3");
        tv_main_title.setText(ad_title);
//        wv_h5detail = (WebView) findViewById(R.id.wv_h5detail);
        wv_h5detail = new WebView(this);

        ViewGroup.LayoutParams relLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll_webview.addView(wv_h5detail, relLayoutParams);
        WebSettings settings = wv_h5detail.getSettings();
        // 开启Javascript脚本
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setAppCacheEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);

        wv_h5detail.requestFocus();
        //支持缩放
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        //隐藏缩放按钮
        settings.setDisplayZoomControls(false);
        //自适应显示
        settings.setUseWideViewPort(true);//设置此属性,可任意比例缩放
        settings.setLoadWithOverviewMode(true);//表示我们的代码支持html5网页自适应。
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true); //设置可以访问文件
//        wv_h5detail.addJavascriptInterface(new JsInteration(), "control");
//

    }

    @Override
    public void setListener() {
        //不使用缓存：
        wv_h5detail.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        findViewById(R.id.rl_main_left).setOnClickListener(this);
//        rl_main_left_text.setOnClickListener(this);
        findViewById(R.id.rl_one).setOnClickListener(this);
        findViewById(R.id.rl_two).setOnClickListener(this);
        findViewById(R.id.rl_three).setOnClickListener(this);
        wv_h5detail.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url)

            { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                testMethod(wv_h5detail);
                DebugUtil.e(TAG, "url:" + url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                DebugUtil.e(TAG, "url:" + url);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        wv_h5detail.setWebChromeClient(new WebChromeClient() {

            // 配置权限 （在WebChromeClinet中实现）
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            // 扩充数据库的容量（在WebChromeClinet中实现）
            @Override
            public void onExceededDatabaseQuota(String url,
                                                String databaseIdentifier, long currentQuota,
                                                long estimatedSize, long totalUsedQuota,
                                                WebStorage.QuotaUpdater quotaUpdater) {

                quotaUpdater.updateQuota(estimatedSize * 2);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }

            // 扩充缓存的容量
            @Override
            public void onReachedMaxAppCacheSize(long spaceNeeded,
                                                 long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {

                quotaUpdater.updateQuota(spaceNeeded * 2);
            }

            // Android 使WebView支持HTML5 Video（全屏）播放的方法
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (myCallback != null) {
                    myCallback.onCustomViewHidden();
                    myCallback = null;
                    return;
                }

                ViewGroup parent = (ViewGroup) wv_h5detail.getParent();
                parent.removeView(wv_h5detail);
                parent.addView(view);
                myView = view;
                myCallback = callback;
            }

            @Override
            public void onHideCustomView() {
                hideCustomView();
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                DebugUtil.e(TAG, "onJsAlert" + "," + "url: " + url);
                DebugUtil.e(TAG, "onJsAlert" + "," + "message: " + message);
//                AlertDialog.Builder builder = new AlertDialog.Builder(H5DetailActivity.this);
//                builder.setMessage(message)
//                        .setNeutralButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface arg0, int arg1) {
//                                arg0.dismiss();
//                            }
//                        }).show();
//                result.cancel();
                if (url.contains("looyuoms")) {
                    result.cancel();
                }
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url,
                                       String message, final JsResult result) {
                // TODO Auto-generated method stub
                DebugUtil.e(TAG, "onJsConfirm" + "," + "url: " + url);
                DebugUtil.e(TAG, "onJsConfirm" + "," + "message: " + message);
//                DialogUtils.dialogBuilder(mContext, "温馨提示", message,
//                        new DialogCallBack() {
//
//                            @Override
//                            public void onCompate() {
//                                Log.i(TAG, "onJsConfirm,onCompate");
//                                result.confirm();
//                            }
//
//                            @Override
//                            public void onCancel() {
//                                Log.i(TAG, "onJsConfirm,onCancel");
//                                result.cancel();
//                            }
//                        });
                if (url.contains("looyuoms")) {
                    result.cancel();
                }
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
                DebugUtil.e(TAG, "onJsBeforeUnload" + "," + "url: " + url);
                DebugUtil.e(TAG, "onJsBeforeUnload" + "," + "message: " + message);
                if (message.contains("确定离开") && url.contains("looyuoms")) {
                    result.cancel();
                }
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                DebugUtil.e(TAG, "onJsPrompt" + "," + "url: " + url);
                DebugUtil.e(TAG, "onJsPrompt" + "," + "message: " + message);
                if (url.contains("looyuoms")) {
                    result.cancel();
                }
                result.confirm();
                return true;
            }

            //扩展浏览器上传文件
            //3.0++版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooserImpl(uploadMsg);
            }

            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooserImpl(uploadMsg);
            }

            // For Android > 5.0
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {
                openFileChooserImplForAndroid5(uploadMsg);
                return true;
            }
        });

        wv_h5detail.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addCategory("android.intent.category.BROWSABLE");
                intent.addCategory("android.intent.category.DEFAULT");
                startActivity(intent);
            }
        });
             DebugUtil.e(TAG, "url:" + ad_url);
             ad_url = getIntent().getStringExtra("url_1");
             wv_h5detail.loadUrl(ad_url);

    }

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
//                if (isPush == 1 || splash_id == 8) {
//                    goBack();
////                    MainActivity.newIntentFlag(this, "MainActivity");
//                } else {
//                goBack();
//                }
//                if (isPush == 1 || splash_id == 8) {
                    back();
//                } else {

//                }
                break;

            case R.id.rl_one:
                wv_h5detail.loadUrl(ad_url);
                break;
            case R.id.rl_two:
                wv_h5detail.loadUrl(url_2);
                break;
            case R.id.rl_three:
                wv_h5detail.loadUrl(url_3);
                break;
        }
    }

    private void goBack() {
        if (wv_h5detail.canGoBack()) {
            wv_h5detail.goBack();
        } else {
                HomeActivity.newIntentFlag(this, "HomeActivity",0);
            back();
        }
    }

    /**
     * 截取uid
     *
     * @return
     */
    private String splitUrl() {
        if (ad_url.contains("uId")) {
            String iniUrl = ad_url;
            String splituid = "";
            String[] uIds = ad_url.split("uId");
            StringBuffer StringUrl = new StringBuffer();
            StringUrl.append(uIds[1]);
            for (int i = 0; i < uIds.length; i++) {
                for (int i1 = 0; i1 < uIds[1].length(); i1++) {
                    String[] split = uIds[1].split("&");
                    splituid = split[0];
                }
            }
            if (ad_url.contains("&uId" + splituid)) {
                return iniUrl.replace("&uId" + splituid, "");
            } else {
                return iniUrl.replace("uId" + splituid + "&", "");
            }
        } else {
            return ad_url;
        }
    }

    public static void newIntent(Context context,String title, String url_1, String url_2,String url_3) {
        Intent intent = new Intent(context, H5EncycloActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url_1", url_1);
        intent.putExtra("url_2", url_2);
        intent.putExtra("url_3", url_3);
        context.startActivity(intent);
    }

    /**
     * 测试传递参数过去
     *
     * @param webView
     */
    private void testMethod(WebView webView) {
//        String call = "javascript:sayHello()";

//        String call = "javascript:alertMessage(\"" + "content" + "\")";
//
//        String call = "javascript:toastMessage(\"" + "我是家虞" + "\")";
//
//        String call = "javascript:sumToJava(1,2)";
//        webView.loadUrl(call);
    }

//    public class JsInteration {
//
//        @JavascriptInterface
//        public void skipDirectDetail(String message) {
//            DirectDetailsActivity.newIntent(H5EncycloActivity.this, message);
//        }
//
//        @JavascriptInterface
//        public void onSumResult(int result) {
//            DebugUtil.i("JsInteration", "onSumResult result=" + result);
//            Toast.makeText(getApplicationContext(), result + "", Toast.LENGTH_LONG).show();
//        }
//    }

    private void hideCustomView() {
        if (myView != null) {
            ViewGroup parent = (ViewGroup) myView.getParent();
            parent.removeView(myView);
            parent.addView(wv_h5detail);
            myView = null;

            if (myCallback != null) {
                myCallback.onCustomViewHidden();
                myCallback = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wv_h5detail != null) {
            wv_h5detail.removeAllViews();
            wv_h5detail.clearHistory();
            ((ViewGroup) wv_h5detail.getParent()).removeView(wv_h5detail);
            wv_h5detail.destroy();
            wv_h5detail = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //具体的操作代码
                goBack();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(CustomApplication.applicationContext).onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }

//    @Override
//    public void onReceiveBroadCast(Context context, Intent intent) {
//        super.onReceiveBroadCast(context, intent);
//        wv_h5detail.reload();
//    }
}
