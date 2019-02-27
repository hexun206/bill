package com.huatu.teacheronline.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.H5DetailActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.SendRequest;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.lang.ref.WeakReference;

/**
 * Created by ljzyuhenda on 16/1/11.
 */
public class ShareUtils {
    private static Activity context;
    private static boolean isAddGold1;
    private static String Year;
    private static String Month;
    private static String Day;
    private static int Splash_id;
    private static int Cycle=1;
    private static UMImage image;

    /**
     * umeng自带的分享弹出框 简约
     * @param activity
     * @param targetUrl   点击分享内容时需要跳转的页面
     * @param description 当分享目标为微信时,布局为 上面为title,左边为图片,右边为description
     *                    分享目标为朋友圈时，description不显示，显示title
     * @param title
     */
    public static void share(Activity activity, String targetUrl, String description, String title, boolean isAddGold) {
        context = new WeakReference<>(activity).get();
        isAddGold1 = isAddGold;
        UMImage image = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_for_share));
        /**shareboard  need the platform all you want and callbacklistener,then open it**/

        UMWeb web = new UMWeb(targetUrl);
        web.setTitle(title);//标题
        web.setThumb(image);  //缩略图
        web.setDescription(description);//描述
        new ShareAction(context)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.EMAIL)
                .withMedia(web)
                .setListenerList(umShareListener, umShareListener, umShareListener, umShareListener, umShareListener)
                .open();
//        new ShareAction(context).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.EMAIL)
//                .withMedia(image)
//                .setListenerList(umShareListener, umShareListener, umShareListener, umShareListener, umShareListener)
//                .withText(description)
////                .withTitle(title)
////                .withTargetUrl(targetUrl)
//                .open();
    }
    private static UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            DebugUtil.e("umShareListener：true" + " isAddGold1" + isAddGold1);
            String uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
            String type = "";
            switch (platform) {
                case WEIXIN:
                    MobclickAgent.onEvent(context, "shareByWeiXin");
                    type = "2";
                    if (isAddGold1) {
                        SendRequest.shareTask(uid, type, null);
                    }
                    break;
                case QQ:
                    MobclickAgent.onEvent(context, "shareByQQ");
                    type = "4";
                    if (isAddGold1) {
                        SendRequest.shareTask(uid, type, null);
                    }
                    break;
                case QZONE:
                    MobclickAgent.onEvent(context, "shareByQQSpace");
                    type = "6";
                    if (isAddGold1) {
                        SendRequest.shareTask(uid, type, null);
                    }
                    break;
                case WEIXIN_CIRCLE:
                    MobclickAgent.onEvent(context, "shareByWeiXinCircle");
                    type = "5";
                    if (isAddGold1) {
                        SendRequest.shareTask(uid, type, null);
                    }
                    break;
            }
                  ToastUtils.showToast("分享成功");
                 if (Splash_id==9){//做题分享H5页面 调用确认接口
                   SendRequest.getshareConfirm(uid, Year, Month, Day, null);
                   Intent intent = new Intent();
                   intent.setAction(H5DetailActivity.ACTION_NEXT);
                   context.sendBroadcast(intent);
                   Splash_id=1;
            }
                  Log.i("友盟", "分享成功" + type);
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            Toast.makeText(ShareActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(ShareActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    public static final String url_appdownload_qq = "http://a.app.qq.com/o/simple.jsp?pkgname=com.huatu.teacheronline";
    public static final String url_share_exercise = SendRequest.ipForExercise+"httb/httbapi/common/sharQuestion";
    public static final String content_share = "我在华图教师学习，加入华图教师和我一起轻松备考吧!";
    public static final String content_share_module = "我刷题成魔，碾压无数，这道题难到我了，你来试试？";
    public static final String content_share_evaluation = "这道题，100个人参加笔试，只有1个人做对，你会是那个人吗？";
    public static final String content_share_test = "全真模考，检验真知，直面你的弱项，看看这道题。";
    public static final String content_share_evaluation_score = "晒分是一种常态，优秀是一种习惯；华图教师成就你的教师梦。";
    public static final String title_share = "华图教师";

    /**
     * 弹出自定义的分享对话框。
     *
     * @param activity
     * @param targetUrl   地址链接
     * @param description 描述
     * @param title       标题
     * @param isAddGold   是否加金币
     */
    public static void popShare(Activity activity, final String targetUrl, final String description, final String title, boolean isAddGold) {

        context = new WeakReference<>(activity).get();
        isAddGold1 = isAddGold;

        int screenHeight = CommonUtils.getScreenHeight();
        int screenWidth = CommonUtils.getScreenWidth();
        View share_dialog = null;
        if (share_dialog == null) {
            share_dialog = LayoutInflater.from(context).inflate(
                    R.layout.activity_share, null);
        }
        Dialog showDialogToClearCache = new Dialog(context, R.style.shareDialog);
        showDialogToClearCache.setContentView(share_dialog);
        android.view.WindowManager.LayoutParams p = showDialogToClearCache
                .getWindow().getAttributes();
        p.height = screenHeight; // 高度设置为屏幕的0.3
        p.width = screenWidth; // 宽度设置为屏幕的0.5
        showDialogToClearCache.getWindow().setAttributes(p); // 设置生效
        showDialogToClearCache.setCanceledOnTouchOutside(false);
        RelativeLayout dis_pop = (RelativeLayout) share_dialog.findViewById(R.id.dis_pop);
        RelativeLayout share_wechat = (RelativeLayout) share_dialog.findViewById(R.id.share_wechat);
        RelativeLayout share_wechat_circle1 = (RelativeLayout) share_dialog.findViewById(R.id.share_wechat_circle1);
        RelativeLayout share_qq = (RelativeLayout) share_dialog.findViewById(R.id.share_qq);
        RelativeLayout share_qzone = (RelativeLayout) share_dialog.findViewById(R.id.share_qzone);
        RelativeLayout share_sina = (RelativeLayout) share_dialog.findViewById(R.id.share_sina);
        RelativeLayout share_gmail = (RelativeLayout) share_dialog.findViewById(R.id.share_gmail);
        final Dialog finalShowDialogToClearCache = showDialogToClearCache;
        dis_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalShowDialogToClearCache.dismiss();
            }
        });

        share_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(SHARE_MEDIA.WEIXIN, targetUrl, title, description);
            }
        });
        share_wechat_circle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(SHARE_MEDIA.WEIXIN_CIRCLE, targetUrl, title, description);
            }
        });
        share_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(SHARE_MEDIA.QQ, targetUrl, description, title);
            }
        });
        share_qzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(SHARE_MEDIA.QZONE, targetUrl, description, title);
            }
        });
        share_sina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(SHARE_MEDIA.SINA, targetUrl, description, title);

            }
        });
        share_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(SHARE_MEDIA.EMAIL, targetUrl, description, title);
            }
        });
        showDialogToClearCache.show();

    }
    //单独调用微信朋友圈 不需要弹出选择框
    public static void Newshare(Activity activity,final String targetUrl, final String description,
        final String title, final String year, final String month, final String day, final int splash_id,final int cycle, boolean isAddGold){
//        context = new WeakReference<>(activity).get();
        Year=year; Month=month; Day=day; Splash_id=splash_id; Cycle=cycle;
//        Wxshare(SHARE_MEDIA.WEIXIN_CIRCLE, targetUrl, description, title);
        context = new WeakReference<>(activity).get();
        isAddGold1 = isAddGold;

        int screenHeight = CommonUtils.getScreenHeight();
        int screenWidth = CommonUtils.getScreenWidth();
        View share_dialog = null;
        if (share_dialog == null) {
            share_dialog = LayoutInflater.from(context).inflate(
                    R.layout.activity_new_share, null);
        }
        Dialog showDialogToClearCache = new Dialog(context, R.style.shareDialog);
        showDialogToClearCache.setContentView(share_dialog);
        android.view.WindowManager.LayoutParams p = showDialogToClearCache
                .getWindow().getAttributes();
        p.height = screenHeight; // 高度设置为屏幕的0.3
        p.width = screenWidth; // 宽度设置为屏幕的0.5
        showDialogToClearCache.getWindow().setAttributes(p); // 设置生效
        showDialogToClearCache.setCanceledOnTouchOutside(false);
        RelativeLayout dis_pop = (RelativeLayout) share_dialog.findViewById(R.id.dis_pop);
        RelativeLayout share_wechat = (RelativeLayout) share_dialog.findViewById(R.id.share_wechat);
        RelativeLayout share_wechat_circle1 = (RelativeLayout) share_dialog.findViewById(R.id.share_wechat_circle1);
        RelativeLayout share_qq = (RelativeLayout) share_dialog.findViewById(R.id.share_qq);
        RelativeLayout share_qzone = (RelativeLayout) share_dialog.findViewById(R.id.share_qzone);
        RelativeLayout share_sina = (RelativeLayout) share_dialog.findViewById(R.id.share_sina);
        TextView tv_cancle= (TextView) share_dialog.findViewById(R.id.tv_cancle);
//        RelativeLayout share_gmail = (RelativeLayout) share_dialog.findViewById(R.id.share_gmail);
        final Dialog finalShowDialogToClearCache = showDialogToClearCache;
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalShowDialogToClearCache.dismiss();
            }
        });
        dis_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalShowDialogToClearCache.dismiss();
            }
        });

        share_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wxshare(SHARE_MEDIA.WEIXIN, targetUrl, title, description);
            }
        });
        share_wechat_circle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wxshare(SHARE_MEDIA.WEIXIN_CIRCLE, targetUrl, title, description);
            }
        });
        share_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wxshare(SHARE_MEDIA.QQ, targetUrl, description, title);
            }
        });
        share_qzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wxshare(SHARE_MEDIA.QZONE, targetUrl, description, title);
            }
        });
        share_sina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wxshare(SHARE_MEDIA.SINA, targetUrl, description, title);

            }
        });
//        share_gmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Wxshare(SHARE_MEDIA.EMAIL, targetUrl, description, title);
//            }
//        });
        showDialogToClearCache.show();
    }

    /**
     *  分享(单独用于分享做题记录朋友圈)
     * @param qq 分享平台
     * @param targetUrl 分享地址
     * @param description 分享描述
     * @param title 分享标题
     */
    public static void Wxshare(SHARE_MEDIA qq, String targetUrl, String description, String title) {
        //开启自定义分享页面
        if (Cycle>=1&&Cycle<=33){
            image = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.iconimage_1));
        }else if (Cycle>33&&Cycle<=66){
            image = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.iconimage_2));
        }else{
            image = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.iconimage_3));
        }

        /**shareboard  need the platform all you want and callbacklistener,then open it**/

        UMWeb web = new UMWeb(targetUrl);
        web.setTitle(title);//标题
        web.setThumb(image);  //缩略图
        web.setDescription(description);//描述
        new ShareAction(context)
                .setPlatform(qq)
                .withMedia(web)
                .setCallback(umShareListener)
                .share();
    }
    /**
     *  分享
     * @param qq 分享平台
     * @param targetUrl 分享地址
     * @param description 分享描述
     * @param title 分享标题
     */
    public static void share(SHARE_MEDIA qq, String targetUrl, String description, String title) {
        //开启自定义分享页面
        UMImage  image = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_for_share));
        /**shareboard  need the platform all you want and callbacklistener,then open it**/

        UMWeb web = new UMWeb(targetUrl);
        web.setTitle(title);//标题
        web.setThumb(image);  //缩略图
        web.setDescription(description);//描述
        new ShareAction(context)
                .setPlatform(qq)
                .withMedia(web)
                .setCallback(umShareListener)
                .share();

//        UMImage image = new UMImage(context, "https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=2042276435,923048600&fm=80");
//        new ShareAction(context)
//                .setPlatform(qq)
//                .setCallback(umShareListener)
//                .withTitle("我的分享")
//                .withText("hello world!")
//                .withTargetUrl("http://www.baidu.com")
//                .withMedia(image)
//                .share();
    }

    /**
     * 直接分享图片 bitmap
     * @param activity
     * @param bitmap
     * @param qq
     * @param targetUrl
     * @param description
     * @param title
     */
    public static void shareBitmap(Activity activity,Bitmap bitmap,SHARE_MEDIA qq, String targetUrl, String description, String title){
        context = new WeakReference<>(activity).get();
        UMImage image = new UMImage(context, bitmap);//bitmap文件

        new ShareAction(context).setPlatform(qq)
                .withMedia(image)
                .setCallback(umShareListener)
                .share();
    }
}
