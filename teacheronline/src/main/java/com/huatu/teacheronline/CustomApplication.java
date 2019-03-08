package com.huatu.teacheronline;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.baijiayun.BJYPlayerSDK;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.greendao.DirectBean;
import com.huatu.teacheronline.alipay.PartnerConfig;
import com.huatu.teacheronline.direct.DirectDetailsActivity;
import com.huatu.teacheronline.direct.bean.CrashInfo;
import com.huatu.teacheronline.direct.manager.RecordInfoManager;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.login.LoginActivity;
import com.huatu.teacheronline.personal.DetailsnoticeActivity;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.common.SocializeConstants;
import com.umeng.socialize.utils.Log;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import cn.xiaoneng.uiapi.Ntalker;
import cn.xiaoneng.xpush.XPush;
import okhttp3.OkHttpClient;

/**
 * Created by ljzyuhenda on 15/12/30.
 */
public class CustomApplication extends Application {
    public static CustomApplication applicationContext;
    //    private RefWatcher mRefWatcher;
//    private PushAgent mPushAgent;
    private HashMap<String, String> notifMap = new HashMap<String, String>();
    private boolean iflogin;//是否登录
    private String uid = "";
    private ObtainDataListerForUpdateMessage obtainDataListerForUpdateMessage;//消息标记为已读
    String SINA_WEIBO = "SINA_WEIBO";
    //百家云初始化id
    public static final long BJPlayerView_partnerId = 49752705;
    private boolean isDebug = false;

    public static int width = 0;
    public static int height = 0;
    private CrashHandler crashHandler;





//    public static RefWatcher getRefWatcher() {
//        return applicationContext.mRefWatcher;
//    }


    @Override
    public void onCreate() {
        super.onCreate();

        isDebug = getApplicationInfo() != null &&
                (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        Config.DEBUG = false;
        applicationContext = this;
        //神策集成
        SensorDataSdk.getInstance().init(this);


        //百家云视频播放
        new BJYPlayerSDK.Builder(this)
                .setDevelopMode(true)
                //如果没有个性域名请注释
//                .setCustomDomain("teacheronline")
                .setEncrypt(true)
                .build();
        FlowManager.init(this);
//        try {
//            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
//            String JPUSH =appInfo.metaData.getString("JPUSH_CHANNEL");
//            String UMENG =appInfo.metaData.getString("UMENG_CHANNEL");
//            DebugUtil.e("JPUSH: "+JPUSH+"   UMENG: "+UMENG);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        String sdkVersion = SocializeConstants.SDK_VERSION;
//        mRefWatcher = LeakCanary.install(this);
        iflogin = CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_IFLOGIN);
        if (iflogin) uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)
                .methodCount(3)
//                .methodOffset(7)
                .tag("TeacherOnline")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {

                return isDebug;
            }
        });

        okHttpInit();

        //全局异常捕获
        crashHandler = new CrashHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);

        //初始化二维码扫码
        ZXingLibrary.initDisplayOpinion(this);
        //初始化freso
        Fresco.initialize(this);
        //友盟点击统计
        MobclickAgent.setDebugMode(false);
        //----------友盟推送
//        mPushAgent = PushAgent.getInstance(this);
//        //注册推送服务，每次调用register方法都会回调该接口
//        mPushAgent.register(new IUmengRegisterCallback() {
//
//            @Override
//            public void onSuccess(String deviceToken) {
//                //注册成功会返回device token
//                DebugUtil.e("deviceToken:" + deviceToken);
//            }
//
//            @Override
//            public void onFailure(String s, String s1) {
//
//            }
//        });
//
//        setXPush(); //设置XPush
        Ntalker.getBaseInstance().enableDebug(true);//是否开启debug模式
        //初始化sdk, return 0 为正常
        Ntalker.getBaseInstance().initSDK(getApplicationContext(), DirectDetailsActivity.siteid, DirectDetailsActivity.sdkkey);
//        setExtraFunc();//设置额外功能


//        mPushAgent.enable();
//        String device_token = UmengRegistrar.getRegistrationId(this);
//        mPushAgent.setDebugMode(false);
        //设置最多显示几条推送信息
//        mPushAgent.setDisplayNotificationNumber(5);
        //极光统计
//        JAnalyticsInterface.init(this);
//        JAnalyticsInterface.setDebugMode(true);
        //极光统计活跃用户 极光推送
//        JPushInterface.init(this);
//        JPushInterface.setDebugMode(true);
        /**
         * 消息接受处理类
         * 该Handler是在IntentService中被调用，故
         * 1. 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * 2. IntentService里的onHandleIntent方法是并不处于主线程中，因此，如果需调用到主线程，需如下所示;
         * 	      或者可以直接启动Service
         * */
//        UmengMessageHandler messageHandler = new UmengMessageHandler() {
//            @Override
//            public void dealWithCustomMessage(final Context context, final UMessage msg) {
//                //自定义消息处理
//                new Handler(getMainLooper()).post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        notificationsANDCustomMessagesClik(msg, context, 1);
//                    }
//                });
//            }
//
//            @Override
//            public Notification getNotification(Context context,
//                                                UMessage msg) {
//                //自定义通知栏样式
//                switch (msg.builder_id) {
//                    case 1:
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view_umengpush);
//                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
//                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
//                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
//                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
//                        builder.setContent(myNotificationView);
//                        builder.setAutoCancel(true);
//                        builder.setSmallIcon(R.drawable.icon);
//                        Notification mNotification = builder.build();
//                        //由于Android v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
//                        mNotification.contentView = myNotificationView;
//                        return mNotification;
//                    default:
//                        //默认为0，若填写的builder_id并不存在，也使用默认。
//                        return super.getNotification(context, msg);
//                }
//            }
//        };
//        mPushAgent.setMessageHandler(messageHandler);
        /**
         * 通知处理类
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                //推送消息处理
                android.util.Log.i("HuatuApplication", "url is: ");
//                if (iflogin) {
//                    SubscriptionActivity.newIntent(CustomApplication.this);
//                }else {
//                    Intent intent = new Intent();
//                    intent.setClass(context, LoginActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }


//                iflogin = CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_IFLOGIN);
//                if(iflogin)uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
//                for (Map.Entry<String, String> entry : msg.extra.entrySet())
//                {
//                    String key = entry.getKey();
//                    String value = entry.getValue();
//                    notifMap.put(key,value);
//                }
//                String stype = notifMap.get("stype");
//                String tId = notifMap.get("tId");
//                String msgId = notifMap.get("msgId");
//                Intent intent = new Intent();
//                if (iflogin) {
//                    if(Integer.parseInt(stype) == 1||Integer.parseInt(stype) == 2){
//                        intent.putExtra("directId", tId);
//                        intent.setClass(context, DirectDetailsActivity.class);
//                        updateMessageForMy(uid,msgId);
//                    }else if(Integer.parseInt(stype) == 0){
//                        intent.putExtra("ad_title", getResources().getString(R.string.my_message));
//                        intent.putExtra("ad_url", tId + uid);
//                        intent.setClass(context, H5DetailActivity.class);
//                    }else if(Integer.parseInt(stype) == 0){
//                        intent.putExtra("ad_title", getResources().getString(R.string.my_message));
//                        intent.putExtra("ad_url", tId + uid);
//                        intent.setClass(context, H5DetailActivity.class);
//                    }
//                }else {
//                    intent.setClass(context, LoginActivity.class);
//                }
//                intent.putExtra("isPush", 1);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
                notificationsANDCustomMessagesClik(msg, context, 0);
            }
        };
//        mPushAgent.setNotificationClickHandler(notificationClickHandler);


//        @SuppressLint("MissingPermission") String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
//
//        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//        DebugUtil.e("Imei:" + Imei + " android_id:" + android_id);


        //chrome调试使用
//        if(isDebug){
//            Stetho.initialize(
//                    Stetho.newInitializerBuilder(this)
//                            .enableDumpapp(
//                                    Stetho.defaultDumperPluginsProvider(this))
//                            .enableWebKitInspector(
//                                    Stetho.defaultInspectorModulesProvider(this))
//                            .build());
//        }


    }



    class CrashHandler implements Thread.UncaughtExceptionHandler {

        private final Thread.UncaughtExceptionHandler mDefaultHandler;
        private Application app = null;

        public CrashHandler(Application app) {
            this.app = app;
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        }


        @Override
        public void uncaughtException(Thread t, Throwable e) {


            try {

                RecordInfoManager.getInstance().saveInUncaughtException();

//                Thread.sleep(2 * 1000);
//                AppManager.getInstance().finishAllActivity();
//                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(0);
                if (isDebug) {
                    CrashInfo crashInfo = new CrashInfo();
                    crashInfo.setContent(GsonUtils.toJson(e));
                    crashInfo.save();
                }

            } catch (Exception ignored) {
            }

            mDefaultHandler.uncaughtException(t, e);


        }
    }


    private void okHttpInit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(isDebug ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);
        //全局的读取超时时间
        builder.readTimeout(10, TimeUnit.SECONDS);
        //全局的写入超时时间
        builder.writeTimeout(10, TimeUnit.SECONDS);
        //全局的连接超时时间
        builder.connectTimeout(10, TimeUnit.SECONDS);

        OkGo.getInstance().init(this)
                .setOkHttpClient(builder.build())
                .setCacheMode(CacheMode.NO_CACHE)
                .setRetryCount(0);

    }

    /**
     * 友盟的推送和自定义消息处理
     *
     * @param msg
     * @param context
     * @param notifyType 友盟的推送类型 0 推送 1 自定义消息
     */
    private void notificationsANDCustomMessagesClik(UMessage msg, Context context, int notifyType) {
        iflogin = CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_IFLOGIN);
        if (iflogin) uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        android.util.Log.i("HuatuApplication", "the Umeng message is : " + msg);
        UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);//通知点击统计
        String s = msg.custom.toString();
        DebugUtil.e("key:" + s);
        for (Map.Entry<String, String> entry : msg.extra.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            notifMap.put(key, value);
        }
//        notifMap.put("title",msg.title);
//        notifMap.put("content",msg.text);
        String stype = notifMap.get("stype");
        final String tId = notifMap.get("tId");
        if (Integer.parseInt(stype) == 2) {
            //如果是课程提醒判断我的列表里面是否有该课程有就notify
            getMyLiveData(tId, context, notifyType);
        } else {
            //不是课程提醒，直接notify
            showNotify(context, notifyType);
        }
    }

    /**
     * 购买的课程列表
     *
     * @param tId
     * @param context
     */
    private void getMyLiveData(final String tId, final Context context, final int notifyType) {
        SendRequest.getMyLiveData(1 + "", 2000 + "", uid, 0, new ObtainDataFromNetListener<List<DirectBean>, String>() {
            @Override
            public void onSuccess(List<DirectBean> res) {
                DebugUtil.e("getMyLiveData:" + res.toString());
                for (DirectBean directBean : res) {
                    if (tId.equals(directBean.getRid())) {
                        showNotify(context, notifyType);
                    }
                }
            }

            @Override
            public void onFailure(String res) {

            }
        });
    }

    /**
     * 通知
     *
     * @param context notifyType 友盟的推送类型 0 推送 1 自定义消息
     */
    private void showNotify(Context context, int notifyType) {
        // stype 0公告 1课程推荐 2开课提醒 3资讯 4面授课
        String stype = notifMap.get("stype");
        String tId = notifMap.get("tId");
        String title = notifMap.get("title");
        String cotent = notifMap.get("content");
        Intent intent = new Intent();
        if (iflogin) {
            if (Integer.parseInt(stype) == 1) {
                intent.putExtra("directId", tId);
                intent.putExtra("isPush", 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, DirectDetailsActivity.class);
            } else if (Integer.parseInt(stype) == 2) {
                intent.putExtra("directId", tId);
                intent.putExtra("isPush", 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, DirectDetailsActivity.class);
            } else if (Integer.parseInt(stype) == 0 || Integer.parseInt(stype) == 3) {
                intent.putExtra("ad_title", title);
                intent.putExtra("ad_url", tId);
                intent.putExtra("isPush", 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, H5DetailActivity.class);
            } else if (Integer.parseInt(stype) == 4) {
                intent.putExtra("directId", tId);
                intent.putExtra("isPush", 1);
                intent.putExtra("bar", "Notificationbar");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, DetailsnoticeActivity.class);
            }
        } else {
            intent.setClass(context, LoginActivity.class);
        }
        if (notifyType == 0) {
            startActivity(intent);
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view_umengpush);
            myNotificationView.setTextViewText(R.id.notification_title, title);
            myNotificationView.setTextViewText(R.id.notification_text, cotent);
            myNotificationView.setImageViewResource(R.id.notification_large_icon, R.mipmap.ic_launcher);
            myNotificationView.setImageViewResource(R.id.notification_small_icon, R.mipmap.ic_launcher);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContent(myNotificationView);
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentIntent(contentIntent);
            Notification mNotification = builder.build();
            //由于Android v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
            mNotification.contentView = myNotificationView;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, mNotification);
//            intent.putExtra("isPush", 1);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }


    }

    //各个平台的配置，建议放在全局Application或者程序入口
    {
        //新浪微博
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad", PartnerConfig.NOTIFY_URL_course);
        //已配置
        PlatformConfig.setQQZone("1105027899", "t75F8QR99BnupGTx");
        //微信
        PlatformConfig.setWeixin("wx7ee269efbd76b187", "3c04dbde94f60aad7c254e1858e6e71c");

        Log.LOG = false;
//        Config.IsToastTip = false;
    }

    public void addAlias(final String userId) {
//        new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    CustomApplication.applicationContext.mPushAgent.addAlias(userId, SINA_WEIBO, new UTrack.ICallBack() {
//                        @Override
//                        public void onMessage(boolean b, String s) {
//
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.run();
    }

    public void removeAlias(final String userId) {
//        new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    CustomApplication.applicationContext.mPushAgent.removeAlias(userId, SINA_WEIBO, new UTrack.ICallBack() {
//                        @Override
//                        public void onMessage(boolean b, String s) {
//
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.run();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 更改消息状态
     */
    public void updateMessageForMy(String uid, String messageId) {
        obtainDataListerForUpdateMessage = new ObtainDataListerForUpdateMessage(CustomApplication.this);
        SendRequest.updateMessageForMy(messageId, uid, obtainDataListerForUpdateMessage);
    }

    private class ObtainDataListerForUpdateMessage extends ObtainDataFromNetListener<String, String> {
        private Application weak_activity;

        public ObtainDataListerForUpdateMessage(Application activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String res) {
        }

        @Override
        public void onFailure(final String res) {
        }
    }


    private void setXPush() {
//        XPush.setNotificationClickToActivity(getApplicationContext(), TestChatActivity.class);
        XPush.setNotificationShowIconId(getApplicationContext(), 0);
        XPush.setNotificationShowTitleHead(getApplicationContext(), null);//getResources().getString(R.string.app_name)

//		XPush.enableHuaweiPush(getApplicationContext(), true);
//		XPush.setHuaweiPushParams(getApplicationContext(), "10556196");
//		XPush.enableXiaomiPush(getApplicationContext(), true);
//		XPush.setXiaomiPushParams(getApplicationContext(), getPackageName(), "2882303761517480753", "5641748066753");

    }


}
