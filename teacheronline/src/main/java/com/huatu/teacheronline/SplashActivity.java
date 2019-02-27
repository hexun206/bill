package com.huatu.teacheronline;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huatu.teacheronline.bean.StartsBean;
import com.huatu.teacheronline.direct.DirectDetailsActivity;
import com.huatu.teacheronline.engine.AsyncHttpClientHelper;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.login.LoginActivity;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.utils.UserInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by ljzyuhenda on 16/1/14.
 */
public class SplashActivity extends BaseActivity {
    private long startTime;
    private long intervalTime;
    private SimpleDraweeView sdv_draweeView;
    private GenericDraweeHierarchy hierarchy;
    private String sp_image_url;
    private ArrayList<StartsBean> startBeanList =new ArrayList<>();
    private String newtype="1";
    private boolean iflogin;
    private int ifStop=1;//1，普通启动页登录 2||3的时候如果点击跳转详情和H5的时候，中止启动页跳转首页。
    private TextView tv_time;

    @Override
    public void initView() {
        setContentView(R.layout.activity_splash);
        //-------------------腾讯统计MAT-------------------//
//        StatConfig.setAutoExceptionCaught(false);
//        //打开mat的debug开关，可查看mat的上报或错误日志
//        StatConfig.setDebugEnable(true);
//        //调用统计接口，触发腾讯统计MAT并上报数据
//        StatService.trackCustomEvent(this, "onCreate", "");
        //-------------------腾讯统计MAT-------------------//

        //-------------------百度统计-------------------//
        com.baidu.mobstat.StatService.start(this);
        // 打开调试开关，正式版本请关闭，以免影响性能
        com.baidu.mobstat.StatService.setDebugOn(false);
        // 打开异常收集开关，默认收集java层异常，如果有嵌入SDK提供的so库，则可以收集native crash异常
        com.baidu.mobstat.StatService.setOn(SplashActivity.this, com.baidu.mobstat.StatService.EXCEPTION_LOG);
        //-------------------百度统计-------------------//

        sdv_draweeView = (SimpleDraweeView) findViewById(R.id.sdv_draweeView);
        tv_time = (TextView) findViewById(R.id.tv_time_splash);
        if (!CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_OPEN_TEACHERONLINE)){
            tv_time.setVisibility(View.GONE);
        }
        Fresconfigure();
        sdv_draweeView.setHierarchy(hierarchy);
        sp_image_url = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_START_IMAGE_URL, "");
//            mc = new MyCountDownTimer(4000, 1000);
            isNetworkAvailable();//判断网络是否连接
//            mc.start();
        iflogin = CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_IFLOGIN);
        loadMainActivity();
    }


    private void initDatas() {
        Display mDisplay = getWindowManager().getDefaultDisplay();
        int width= mDisplay.getWidth();
        int height = mDisplay.getHeight();
        ObtainDataStartpage obtainDataStartpage=new ObtainDataStartpage(this);
        SendRequest.getStartpage(String.valueOf(width), String.valueOf(height), obtainDataStartpage);
    }
    private class ObtainDataStartpage extends ObtainDataFromNetListener<StartsBean, String> {
        private SplashActivity weak_activity;
        public ObtainDataStartpage(SplashActivity contextWeakReference) {
            weak_activity = new WeakReference<>(contextWeakReference).get();
        }

        @Override
        public void onSuccess(final StartsBean res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startBeanList.add(res);
                        if (!startBeanList.get(0).getData().equals("")) {
                            FrescoUtils.setFrescoImageUri(sdv_draweeView, startBeanList.get(0).getData(), R.drawable.splash);
                        } else {
                            sdv_draweeView.setImageResource(R.drawable.splash);
                        }
                        CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_START_IMAGE_URL, startBeanList.get(0).getData());
                        if (startBeanList.get(0).getType().equals("3")){//当type为3时 resrou为课程id
                            newtype="3";
                        }else if (startBeanList.get(0).getType().equals("2")){
                            newtype="2";
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
        }
    }


    public void loadMainActivity() {
        startTime = System.currentTimeMillis();
        AsyncHttpClientHelper.createInstance().getThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                intervalTime = System.currentTimeMillis() - startTime;
                if (intervalTime < 1.5 * 1000) {
                    SystemClock.sleep(3000 - intervalTime);
                }
                if (!CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_OPEN_TEACHERONLINE)) {
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    finish();
                    CommonUtils.putSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_OPEN_TEACHERONLINE, true);
                } else {
//                    iflogin = CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_IFLOGIN);
                    Intent intent;
                    if (ifStop!=1){
                        return;
                    }
                    if (iflogin) {
//                        if (!newtype.equals("3")&&!newtype.equals("2")){
//                            intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent = new Intent(SplashActivity.this, HomeActivity.class);
//                        }
                    } else {
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

    @Override
    public void setListener() {
        sdv_draweeView.setOnClickListener(this);
        tv_time.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      switch (view.getId()){
          case R.id.sdv_draweeView:
              if (iflogin) {
                  if (newtype.equals("2")) {
                      ifStop=2;
                      H5DetailActivity.newIntent(SplashActivity.this, startBeanList.get(0).getResource(),8,"华图教师");
                      finish();
                  } else if (newtype.equals("3")) {
                      ifStop=3;
                      DirectDetailsActivity.newIntent(SplashActivity.this, startBeanList.get(0).getResource(),8);
                      finish();
                  }
              }
              break;
          case R.id.tv_time_splash:
              if (iflogin){
//                  Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                  Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                  startActivity(intent);
                  finish();
                  return;
              }else{
//                  if (!CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_OPEN_TEACHERONLINE)){return;}
                  Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                  startActivity(intent);
                  finish();
              }
              break;
      }
    }
    public boolean isNetworkAvailable() {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            initDatas();
            return manager.getActiveNetworkInfo().isAvailable();
        }else{
            if (!sp_image_url.equals("")){
                FrescoUtils.setFrescoImageUri(sdv_draweeView, sp_image_url, R.drawable.splash); //无网络时读取本地图片
            } else{
                sdv_draweeView.setImageResource(R.drawable.splash);
            }
        }
        return false;
    }
    private void Fresconfigure() {
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(SplashActivity.this.getResources());
        hierarchy = builder
                .setFadeDuration(200)
//                .setPlaceholderImage(SplashActivity.this.getResources().getDrawable(R.drawable.splash), ScalingUtils.ScaleType.FIT_XY)
//                .setFailureImage(SplashActivity.this.getResources().getDrawable(R.drawable.splash), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
    }
}
