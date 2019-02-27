package com.huatu.teacheronline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
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
import android.widget.Toast;

import com.huatu.teacheronline.direct.DirectDetailsActivity;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ShareUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.umeng.socialize.UMShareAPI;

/**
 * H5相关页面
 *
 * @author ljyu
 * @date 2016-6-7 11:06:12
 */
public class H5DetailActivity extends BaseActivity {
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
    private String is_array;//是否传参
    private ProgressBar progressBar;
    private WebChromeClient.CustomViewCallback myCallback = null;
    private View myView;
    private int isPush = 0; //推送过来的消息
    private RelativeLayout rl_main_left_text;
    private int splash_id;
    private String year;
    private String month;
    private String day;
    private int cycle;
    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    @Override
    public void initView() {
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        pass = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PASSWORD, "");
        account = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, "");
        setContentView(R.layout.activity_h5_detail);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NEXT);
        setIntentFilter(intentFilter);

        TextView tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        RelativeLayout rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        LinearLayout ll_webview = (LinearLayout) findViewById(R.id.ll_webview);
        rl_main_left_text = (RelativeLayout) findViewById(R.id.rl_main_left_text);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_live);
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        ad_title = getIntent().getStringExtra("ad_title");
        is_array = getIntent().getStringExtra("is_array");
        isPush = getIntent().getIntExtra("isPush", 0);
        ad_url = getIntent().getStringExtra("ad_url");
        splash_id = getIntent().getIntExtra("splash_id", 0);//8从广告页跳转过来，9从日历签到界面跳转

        //日历界面传过来的年月日周期天
        year = getIntent().getStringExtra("year");
        month = getIntent().getStringExtra("month");
        day = getIntent().getStringExtra("day");
        cycle = getIntent().getIntExtra("cycle", 0);//签到周期天

        rl_main_right.setVisibility(View.VISIBLE);
//        ad_url = "http://kefu.qycn.com/vclient/chat/?m=m&websiteid=116775";
        tv_main_title.setText(ad_title);
//        wv_h5detail = (WebView) findViewById(R.id.wv_h5detail);
        wv_h5detail = new WebView(this);
        ViewGroup.LayoutParams relLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll_webview.addView(wv_h5detail, relLayoutParams);
        rl_main_right.setOnClickListener(this);
        WebSettings settings = wv_h5detail.getSettings();
        // 开启Javascript脚本
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setDomStorageEnabled(true);
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
        wv_h5detail.addJavascriptInterface(new JsInteration(), "control");
//

    }

    @Override
    public void setListener() {
        //不使用缓存：
        wv_h5detail.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        rl_main_left_text.setOnClickListener(this);
        wv_h5detail.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url)

            { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                if (splash_id == 9 && url.contains("share_123")) {
//                    ShareUtils.popShare(this, splitUrl(), ShareUtils.content_share, ad_title, false);
//                    ShareUtils.WXshare(H5DetailActivity.this, splitUrl(), content_newshare, content_newshare, year, month, day, splash_id, cycle);
                    //如果从日历界面跳转点击直接分享朋友圈
                    if (splash_id == 9){
                        String content_newshare = "100天成就百万题霸，今天第" + cycle + "天，挑战百万题霸，来华图教师，我等你!";
                        ShareUtils.Newshare(H5DetailActivity.this, splitUrl(), content_newshare, content_newshare, year, month, day, splash_id, cycle,false);
                    }else{
                        ShareUtils.popShare(H5DetailActivity.this
                                , splitUrl(), ShareUtils.content_share, ad_title, false);
                    }
                    return true;
                } else {
                    if (url.contains("www.hteacher.net") && url.contains("id=") && ad_title.equals("咨询")) {
                        String[] split = url.split("id=");
                        String directId = split[1];
                        DirectDetailsActivity.newIntent(H5DetailActivity.this, directId, true);
                        return false;
                    }  else {
                        view.loadUrl(url);
                        return true;
                    }
                }

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
        if ("1".equals(is_array)) {
            String url = getIntent().getStringExtra("ad_url") + "?uid=" + uid + "&pass=" + pass + "&account=" + account + "&source=" + SendRequest
                    .source_android;
            wv_h5detail.loadUrl(url);
        } else {
            ad_url = getIntent().getStringExtra("ad_url");
            wv_h5detail.loadUrl(ad_url);
        }

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
                goBack();
//                }
//                if (isPush == 1 || splash_id == 8) {
//                    back();
//                } else {

//                }
                break;
            case R.id.rl_main_left_text:
                if (isPush == 1 || splash_id == 8) {
                    back();
//                    MainActivity.newIntentFlag(this, "MainActivity");
                    HomeActivity.newIntentFlag(this, "HomeActivity",0);
                } else {
                    back();
                }

                break;
            case R.id.rl_main_right:
                String s = splitUrl();
                DebugUtil.e("H5ShareUrl:" + s);
                //                ShareUtils.share(this, splitUrl(), ShareUtils.content_share, ad_title, false);
                if (splash_id == 9){
                    String content_newshare = "100天成就百万题霸，今天第" + cycle + "天，挑战百万题霸，来华图教师，我等你!";
                    ShareUtils.Newshare(H5DetailActivity.this, splitUrl(), content_newshare, content_newshare, year, month, day, splash_id, cycle,false);
                }else{
                    ShareUtils.popShare(this, splitUrl(), ShareUtils.content_share, ad_title, false);
                }

                break;
            default:
                back();
                break;
        }
    }

    private void goBack() {
        if (wv_h5detail.canGoBack()) {
            if (rl_main_left_text.getVisibility() == View.GONE) {
                rl_main_left_text.setVisibility(View.VISIBLE);
            }
            wv_h5detail.goBack();
        } else {
            if (isPush == 1 || splash_id == 8) {
//                MainActivity.newIntentFlag(this, "MainActivity");
                HomeActivity.newIntentFlag(this, "HomeActivity",0);
            }
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

    public static void newIntent(Context context, String ad_title, String ad_url) {
        Intent intent = new Intent(context, H5DetailActivity.class);
        intent.putExtra("ad_title", ad_title);
        intent.putExtra("ad_url", ad_url);
        context.startActivity(intent);
    }

    public static void newIntent(Context context, String ad_title, String ad_url, String is_array) {
        Intent intent = new Intent(context, H5DetailActivity.class);
        intent.putExtra("ad_title", ad_title);
        intent.putExtra("ad_url", ad_url);
        intent.putExtra("is_array", is_array);
        context.startActivity(intent);
    }

    public static void newIntent(Context context, String ad_url, int splash_id) {
        Intent intent = new Intent(context, H5DetailActivity.class);
        intent.putExtra("ad_url", ad_url);
        intent.putExtra("splash_id", splash_id);
        context.startActivity(intent);
    }

    public static void newIntent(Context context, String ad_url, int splash_id, String ad_title) {
        Intent intent = new Intent(context, H5DetailActivity.class);
        intent.putExtra("ad_url", ad_url);
        intent.putExtra("splash_id", splash_id);
        intent.putExtra("ad_title", ad_title);
        context.startActivity(intent);
    }

    public static void newIntent(Context context, String ad_url, int splash_id, String ad_title, String year, String month, String day, int cycle) {
        Intent intent = new Intent(context, H5DetailActivity.class);
        intent.putExtra("ad_url", ad_url);
        intent.putExtra("splash_id", splash_id);
        intent.putExtra("ad_title", ad_title);
        intent.putExtra("year", year);
        intent.putExtra("month", month);
        intent.putExtra("day", day);
        intent.putExtra("cycle", cycle);
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

    public class JsInteration {

        @JavascriptInterface
        public void skipDirectDetail(String message) {
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            DirectDetailsActivity.newIntent(H5DetailActivity.this, message);
        }

        @JavascriptInterface
        public void onSumResult(int result) {
            DebugUtil.i("JsInteration", "onSumResult result=" + result);
            Toast.makeText(getApplicationContext(), result + "", Toast.LENGTH_LONG).show();
        }
    }

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
            if (isPush == 1 || splash_id == 8) {
//                back();
//                MainActivity.newIntentFlag(this, "MainActivity");
                goBack();
            } else {
                goBack();
            }
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

    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
        wv_h5detail.reload();
    }
}
