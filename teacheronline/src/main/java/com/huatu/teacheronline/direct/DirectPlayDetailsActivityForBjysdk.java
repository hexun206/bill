package com.huatu.teacheronline.direct;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.artifex.mupdflib.FilePicker;
import com.artifex.mupdflib.MuPDFCore;
import com.artifex.mupdflib.MuPDFPageAdapter;
import com.artifex.mupdflib.MuPDFReaderView;
import com.baijiahulian.common.networkv2.HttpException;
import com.baijiayun.videoplayer.bean.SectionItem;
import com.baijiayun.videoplayer.bean.VideoItem;
import com.gensee.utils.StringUtil;
import com.greendao.DaoUtils;
import com.greendao.DirectBean;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.adapter.DirectClassPdfAdapter;
import com.huatu.teacheronline.direct.adapter.DirectClassScheduleCustomAdapter;
import com.huatu.teacheronline.direct.bean.PdfBean;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.DownLoadFileTask;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.AppraiseAlertDialog;
import com.huatu.teacheronline.widget.ChooseSpeedPopwindows;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.CustomProgressDialog;
import com.huatu.teacheronline.widget.PinnedHeaderListView;
import com.huatu.teacheronline.widget.bjywidget.BjyBottomViewPresenter;
import com.joanzapata.pdfview.PDFView;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.xiaoneng.coreapi.ChatParamsBody;
import cn.xiaoneng.uiapi.Ntalker;

//import io.vov.vitamio.Vitamio;

/**
 * 面试视频详情页 百家云播放页面
 * Created by zhxm on 2016/1/11.
 */
public class DirectPlayDetailsActivityForBjysdk extends BaseActivity  {
//implements OnPlayerViewListener, FilePicker.FilePickerSupport
    private RelativeLayout rl_main_left_player;
    private String settingid1 = "kf_10092_1513839603881";
    private RadioButton tv_detail_intro, tv_detail_text, tv_detail_plan, tv_chanjian;
    //    private TextView tv_main_title;
    private WebView webView;
    private CustomAlertDialog mCustomLoadingDialog;
    //    private RelativeLayout rl_video;
//    private SurfaceView surfaceView;
//    private SurfaceHolder surfaceHolder;
//    private DWIjkMediaPlayer player;
    private boolean isPrepared;
    private boolean isSurfaceDestroy = false;
    // 当player未准备好，并且当前activity经过onPause()生命周期时，此值为true
    private Boolean isPlaying;// 是否正在播放
    //    private SeekBar skbProgress;// 播放进度
//    private ImageView iv_play, iv_screen;
    private boolean isFullScreen = false;// 当前是否全屏状态
//    private TextView videoDuration, playDuration;

    private final static String CLASS_LABEL = "DirectPlayDetailsActivityForCCsdk";
    private final static String TAG = "DirectPlayDetailsActivityForBjysdk";
    private PowerManager.WakeLock mWakeLock;// 禁止锁屏
    private AlertDialog errorDialog;
    //    private LinearLayout rl_play_top;
//    private RelativeLayout rl_full_screen_back;
//    private TextView tv_full_screen_title;
//    private RelativeLayout rl_click_middle;
    private LinearLayout ll_play_detail_bottom;
    private LinearLayout top_bar_no_titlebar;
    private boolean isDisplay = true;// 播放时的顶部返回和底部进度栏是否显示
    private PinnedHeaderListView listview;
    private DirectBean directBean;//传过来的课程
    private ObtatinDataListener obtatinDataListener;
    private ArrayList<DirectBean> directBeanListForClassSchedule = new ArrayList<DirectBean>();//课程表
    private DirectClassScheduleCustomAdapter directClassScheduleAdapter; //课程表适配器
    public int position;//传过来的位置
    private String uid;

    private PDFView pdfView;
    private CustomProgressDialog pd_update;//讲义下载的进度条
    private String jyDownLoadUrlForLocal = "";//讲义下载到本地地址
    private int pageNumber;//讲义页码
    private boolean isShowJy;//是否显示过讲义暂无
    private DaoUtils daoUtils;
    private ImageView ib_main_right_player;
    private int wherefrom;//哪一个页面过来 0课程详情 1 直播直接过来
    private boolean isLocaleData = false;//是否本地数据加载的

    private String getplayrecord;//最后播放的视频行数
    public int playNumbe;//最后播放的视频行数转化类型
    private boolean isLocalPlay = false;//是否加载本地视频
    private String localPlaypPath = ""; //本地视频地址
    private int playrow = 0;//根据number查找到对应的行数
    private LinearLayout ll_no_chat;
    private ImageView img_customer_service;

    private MuPDFCore core;
    private int totalPageCount;
    private int lastPagePosition;
    private boolean isFinished;
    private MuPDFReaderView mDocView;
    private RelativeLayout rl_content_play;
    private String mFileName;

    private int tab = 2;//1 讲义 2课表 3 互动 4 常见问题
    private ListView lv_pdf;
    private DirectClassPdfAdapter directClassPdfAdapter;
    private RelativeLayout ll_no_notes;
    private ArrayList<PdfBean> directBeanListPDFForClassSchedule = new ArrayList<PdfBean>();
    private ObtatinPdfListener obtatinPdfListener;
    private RelativeLayout rl_main_left_pdf;
    private boolean isShowPdf = false;
    private ChooseSpeedPopwindows chooseSpeedPopwindows;
    private int speedType = 0;
    private DirectBean playdirectBean;//播放中的课程
//    private BJPlayerView player;//百家云播放器
    private BjyBottomViewPresenter mBjyBottomViewPresenter;//底部自定义

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void initView() {
        setContentView(R.layout.activity_direct_playbjy_detail);
//        if (!LibsChecker.checkVitamioLibs(this))
//            return;

//        Vitamio.isInitialized(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownManageActivity.ACTION_REFRASH);
        setIntentFilter(intentFilter);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
        if ((mWakeLock != null) && !mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        daoUtils = DaoUtils.getInstance();
//        currentVideoId = getIntent().getStringExtra("video_id");
        directBean = (DirectBean) getIntent().getSerializableExtra("DirectBean");
        ib_main_right_player = (ImageView) findViewById(R.id.ib_main_right_player);
        if (directBean.getIs_buy() != null && Integer.parseInt(directBean.getIs_buy()) == 1) {
            ib_main_right_player.setVisibility(View.VISIBLE);
            ib_main_right_player.setOnClickListener(this);
        }
        directBeanListForClassSchedule = DataStore_Direct.directDatailList;
        position = getIntent().getIntExtra("position", 0);
        wherefrom = getIntent().getIntExtra("wherefrom", 0);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        mCustomLoadingDialog = new CustomAlertDialog(DirectPlayDetailsActivityForBjysdk.this, R.layout.dialog_loading_custom);
//        player = (BJPlayerView) findViewById(R.id.videoView);
//        //以下三个方法分别设置底部、顶部和中部界面
//        mBjyBottomViewPresenter = new BjyBottomViewPresenter(player.getBottomView(), this);
//        player.setBottomPresenter(mBjyBottomViewPresenter);
////        player.setBottomPresenter(new BJBottomViewPresenter(player.getBottomView()));
//        player.setCenterPresenter(new BJCenterViewPresenter(player.getCenterView()));
//        //初始化partnerId，第一个参数换成您的partnerId
//        //        player.initPartner(123456L, BJPlayerView.PLAYER_DEPLOY_ONLINE);
//        player.initPartner(CustomApplication.BJPlayerView_partnerId, BJPlayerView.PLAYER_DEPLOY_ONLINE);
        getplayrecord = CommonUtils.getSharedPreferenceItem(null, directBean.getRid(), "");
        rl_main_left_player = (RelativeLayout) findViewById(R.id.rl_main_left_player);
        rl_main_left_pdf = (RelativeLayout) findViewById(R.id.rl_main_left_pdf);
        rl_content_play = (RelativeLayout) findViewById(R.id.rl_content_play);
//        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_detail_text = (RadioButton) findViewById(R.id.tv_detail_text);
        tv_detail_plan = (RadioButton) findViewById(R.id.tv_detail_plan);
        tv_chanjian = (RadioButton) findViewById(R.id.tv_chanjian);
        webView = (WebView) findViewById(R.id.webView);
        img_customer_service = (ImageView) findViewById(R.id.img_customer_service);
        ll_no_chat = (LinearLayout) findViewById(R.id.ll_no_chat);
        tv_detail_intro = (RadioButton) findViewById(R.id.tv_detail_intro);
        top_bar_no_titlebar = (LinearLayout) findViewById(R.id.top_bar_no_titlebar);
        ll_play_detail_bottom = (LinearLayout) findViewById(R.id.ll_play_detail_bottom);
        listview = (PinnedHeaderListView) findViewById(R.id.listview);
        lv_pdf = (ListView) findViewById(R.id.pdf_listview);//pdf列表
        ll_no_notes = (RelativeLayout) findViewById(R.id.ll_no_notes);//暂无pdf
        directClassScheduleAdapter = new DirectClassScheduleCustomAdapter(this);
        directClassScheduleAdapter.seDirectBean(directBean);
        directClassPdfAdapter = new DirectClassPdfAdapter(this, directBeanListPDFForClassSchedule);
        lv_pdf.setAdapter(directClassPdfAdapter);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        if (directBeanListForClassSchedule == null || directBeanListForClassSchedule.size() == 0) {
            //判断有无网络是否加载本地
            if (CommonUtils.isNetWorkAvilable()) {
                loadDirectPlayInfo();
            } else {
                List<DirectBean> directBeans = daoUtils.queryDirectBeanForCacheClassForRid(directBean.getRid());
                DebugUtil.e("加载本地" + directBeans.size());
                //数据库没有需要提示
                if (directBeans.size() == 0) {
                    ToastUtils.showToast(R.string.network);
                    return;
                }
                if (directBeanListForClassSchedule == null) {
                    directBeanListForClassSchedule = (ArrayList<DirectBean>) directBeans;
                    DataStore_Direct.directDatailList = directBeanListForClassSchedule;
                } else {
                    directBeanListForClassSchedule.clear();
                    directBeanListForClassSchedule.addAll(directBeans);
                }
                isLocaleData = true;
                iniFristdata(isLocaleData);
            }
        } else {
            iniFristdata(isLocaleData);
        }

//        initUI();
        chooseSpeedPopwindows = new ChooseSpeedPopwindows(this);
        getLivePDFForClassSchedule();
    }


    public void initData(final DirectBean directBean) {
        if (directBean.getIsTrial() == 1 || "1".equals(directBean.getIs_buy())) {
            initPlayInfo(directBean);
        } else {
            ToastUtils.showToast("请先购买！谢谢！");
            return;
        }
    }

    public void initPlayInfo(DirectBean directBean) {
        this.playdirectBean = directBean;
        //评价弹窗
        if (directBean.getIs_last() != null && directBean.getIs_last()) {
//            if (player.isPlaying()) {
//                player.pause();
//            }
            DebugUtil.e("initPlayInfo :" + playdirectBean.toString());
            AppraiseAlertDialog appraiseAlertDialog = new AppraiseAlertDialog(this, directBean);
            appraiseAlertDialog.show();
            appraiseAlertDialog.setOnSubmitCompletedListener(new AppraiseAlertDialog.OnSubmitCompletedListener() {
                @Override
                public void submitCompleted(String res) {
                    if (playdirectBean != null) {
                        initPlayInfo(playdirectBean);
                    }
                }
            });
            directBean.setIs_last(false);
            return;
        }
        downOutDate(directBean);

        try {
            if (!StringUtil.isEmpty(directBean.getDown_status()) && Integer.parseInt(directBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_COMPLETE
                    && !StringUtil.isEmpty(directBean.getLocalPath()) && !directBean.getLocalPath().contains(".MP4")) {
                isLocalPlay = true;
//                //播放已下载的
                String localpath = directBean.getLocalPath();
                File file = new File(localpath);
                if (file.exists()) {
//                    player.setOnPlayerViewListener(this);
//                    player.setVideoRate(10);
//                    player.setVideoPath(localpath);
//                    player.playVideo(0);
                } else {
                    ToastUtils.showToast(localpath + "不存在该视频");
                }
            } else {
                isLocalPlay = false;
//                //播放网络的
                //第一个参数为百家云后台配置的视频id，第二个参数为视频token
                if (!StringUtil.isEmpty(directBean.getBjyvideoid()) && !StringUtil.isEmpty(directBean.getBjytoken())) {
//                    player.setVideoId(Long.parseLong(directBean.getBjyvideoid()), directBean.getBjytoken());
//                    player.setVideoRate(10);
//                    //播放
//                    player.playVideo(0);
//                    player.setOnPlayerViewListener(this);
                }else {
                    ToastUtils.showToast("参数错误");
                }

            }
        } catch (IllegalArgumentException e) {
            Log.e("player error", e.getMessage());
        } catch (IllegalStateException e) {
            Log.e("player error", e + "");
        }
    }

    /**
     * 检查已下载CC的视频 通知用户重新下载
     *
     * @param directBean
     */
    private void downOutDate(DirectBean directBean) {
        String ccId = "";
        if (!StringUtils.isEmpty(directBean.getCcCourses_id())) {
            ccId = directBean.getCcCourses_id();
        }
        DebugUtil.e("getLocalPath:" + directBean.getLocalPath() + " ccId:" + ccId);
        if (!StringUtil.isEmpty(directBean.getDown_status()) && Integer.parseInt(directBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_COMPLETE
                && !StringUtil.isEmpty(directBean.getLocalPath()) && directBean.getLocalPath().contains(ccId)) {
            directBean.setLocalPath("");
            directBean.setDown_status(DownManageActivity.CCDOWNSTATE_WAIT + "");
            //发广播通知各个activity
            sendBroadcastByDirectBean(directBean);
            daoUtils.insertOrUpdateDirectBean(directBean, directBean.getVideoType());
            ToastUtils.showToastLong("视频课程的缓存已过期，正在为你在线播放，请重新下载");
        }
        if (!StringUtil.isEmpty(directBean.getDown_status()) && Integer.parseInt(directBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_COMPLETE
                && StringUtil.isEmpty(directBean.getLocalPath())) {
            directBean.setLocalPath("");
            directBean.setDown_status(DownManageActivity.CCDOWNSTATE_WAIT + "");
            //发广播通知各个activity
            sendBroadcastByDirectBean(directBean);
            daoUtils.insertOrUpdateDirectBean(directBean, directBean.getVideoType());
            ToastUtils.showToastLong("视频课程的缓存已过期，正在为你在线播放，请重新下载");
        }
    }

    @Override
    public void setListener() {
        rl_main_left_player.setOnClickListener(this);
        rl_main_left_pdf.setOnClickListener(this);
        tv_detail_text.setOnClickListener(this);
        tv_detail_plan.setOnClickListener(this);
        tv_chanjian.setOnClickListener(this);
        tv_detail_intro.setOnClickListener(this);
        img_customer_service.setOnClickListener(this);
        directClassScheduleAdapter.setVideoClickbutton(new DirectClassScheduleCustomAdapter.VideoClick() {
            @Override
            public void setVideoClick() {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PdfBean pdfBean = directBeanListPDFForClassSchedule.get(position);
                loadPDF(pdfBean.getFileUrl());

            }
        });
    }

    /**
     * 更新状态后的directBean发广播给vod cc rt activity
     *
     * @param directBean
     */
    private void sendBroadcastByDirectBean(DirectBean directBean) {
        Intent intent = new Intent();
        intent.putExtra("DirectBean", directBean);
        intent.setAction(DownManageActivity.ACTION_REFRASH);
        sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left_player:
                if (isFullScreen) {
                    // 全屏的返回键
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }else {
                    // 非全屏的返回键
                    back();
                }
                break;
            case R.id.img_customer_service:
                MobclickAgent.onEvent(this, "consultationOnClik");
                //小能登录
                Ntalker.getBaseInstance().login(uid, CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, ""), 0);
                ChatParamsBody chatparams = new ChatParamsBody();
                chatparams.headurl = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_FACEPATH, "");


                if (directBeanListForClassSchedule != null && directBeanListForClassSchedule.size() != 0) {

                    String getbranchschoolid = directBeanListForClassSchedule.get(position).getbranchschoolid();

                    if (!StringUtils.isEmpty(getbranchschoolid)) {
                        settingid1 = getbranchschoolid;
                    }

                }


                Ntalker.getBaseInstance().startChat(this, settingid1, "华图客服", chatparams, TestChatActivity.class);
                break;
            case R.id.rl_full_screen_back:
                // 全屏时的返回键
                isFullScreen = false;
//                iv_screen.setBackgroundResource(R.drawable.ic_fullsc);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case R.id.tv_detail_text:
                tab = 2;
                // 课表
                reloadData(tab);
                break;
            case R.id.tv_detail_plan:
                tab = 1;
                // pdf 列表
                reloadData(tab);
                break;
            case R.id.tv_detail_intro:
                tab = 3;
                // 互动
                reloadData(tab);
                break;
            case R.id.tv_chanjian:
                tab = 4;
                // 常见
                reloadData(tab);
                break;
            case R.id.ib_main_right_player:
                if (directBeanListForClassSchedule != null && directBeanListForClassSchedule.size() > 0) {
                    DownManageActivity.newIntent(DirectPlayDetailsActivityForBjysdk.this, directBeanListForClassSchedule);
                }
                break;
            case R.id.rl_main_left_pdf:
                rl_main_left_pdf.setVisibility(View.GONE);
                pdfView.setVisibility(View.GONE);
                lv_pdf.setVisibility(View.VISIBLE);
                if (mDocView != null) {
                    isShowPdf = false;
                    rl_content_play.removeView(mDocView);
                }
                break;
            case R.id.tv_speed:
                if (chooseSpeedPopwindows != null) {
                    chooseSpeedPopwindows.showPopUp(v, this, speedType);
                }
                break;
            case R.id.tv_speed_original:
                speedType = 0;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                mBjyBottomViewPresenter.setVideoSpeedText(getString(R.string.speed_original));
//                if (player != null) {
//                    player.setVideoRate(10);
//                }
                break;
            case R.id.tv_speed_quick1:
                speedType = 1;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                mBjyBottomViewPresenter.setVideoSpeedText(getString(R.string.speed_quick1));
//                if (player != null){
//                    player.setVideoRate(12);
//                }
                break;
            case R.id.tv_speed_quick2:
                speedType = 2;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                mBjyBottomViewPresenter.setVideoSpeedText(getString(R.string.speed_quick2));
//                if (player != null){
//                    player.setVideoRate(15);
//                }
                break;
            case R.id.tv_speed_quick3:
                speedType = 3;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                mBjyBottomViewPresenter.setVideoSpeedText(getString(R.string.speed_quick3));
//                if (player != null){
//                    player.setVideoRate(20);
//                }
                break;

        }
    }


    private void reloadData(int index) {
        switch (index) {
            case 2:// 课程列表
                if (mDocView != null) {
                    rl_content_play.removeView(mDocView);
                }
                img_customer_service.setVisibility(View.VISIBLE);
                pdfView.setVisibility(View.GONE);
                rl_main_left_pdf.setVisibility(View.GONE);
                listview.setVisibility(View.VISIBLE);
                lv_pdf.setVisibility(View.GONE);
                ll_no_notes.setVisibility(View.GONE);
                ll_no_chat.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                break;
            case 1:// pdf
                img_customer_service.setVisibility(View.VISIBLE);
                pdfView.setVisibility(View.GONE);
                listview.setVisibility(View.GONE);
                ll_no_chat.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                if (directBean == null || directBeanListForClassSchedule == null || directBeanListForClassSchedule.size() == 0) {
                    return;
                }
                if (directBeanListPDFForClassSchedule.size() == 0) {
                    lv_pdf.setVisibility(View.GONE);
                    ll_no_notes.setVisibility(View.VISIBLE);
                } else {
//                    lv_pdf.setVisibility(View.VISIBLE);
//                    ll_no_notes.setVisibility(View.GONE);
                    if (isShowPdf) {
                        rl_content_play.addView(mDocView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                                .MATCH_PARENT));
                        rl_main_left_pdf.setVisibility(View.VISIBLE);
                        ll_no_notes.setVisibility(View.GONE);
                    } else {
                        lv_pdf.setVisibility(View.VISIBLE);
                        ll_no_notes.setVisibility(View.GONE);
                    }
                }
                break;
            case 3://互动
                if (mDocView != null) {
                    rl_content_play.removeView(mDocView);
                }
                img_customer_service.setVisibility(View.GONE);
                pdfView.setVisibility(View.GONE);
                listview.setVisibility(View.GONE);
                lv_pdf.setVisibility(View.GONE);
                ll_no_notes.setVisibility(View.GONE);
                ll_no_chat.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                rl_main_left_pdf.setVisibility(View.GONE);
                break;
            case 4://常见问题
                if (mDocView != null) {
                    rl_content_play.removeView(mDocView);
                }
                img_customer_service.setVisibility(View.VISIBLE);
                pdfView.setVisibility(View.GONE);
                listview.setVisibility(View.GONE);
                ll_no_notes.setVisibility(View.GONE);
                lv_pdf.setVisibility(View.GONE);
                ll_no_chat.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(DataStore_Direct.Common_problem);
                rl_main_left_pdf.setVisibility(View.GONE);
                break;
        }
    }

    private void loadWebData(String webData) {
        webView.loadDataWithBaseURL(null, webData, "text/html", "UTF-8", null);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (player != null) {
//            player.onResume();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (player != null) {
//            player.onPause();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (player != null) {
//            player.onDestroy();
//        }
//    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (player != null) {
//            player.onConfigurationChanged(newConfig);
//        }
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            //全面屏
            isFullScreen = true;
            img_customer_service.setVisibility(View.GONE);
            ib_main_right_player.setVisibility(View.GONE);
            rl_main_left_player.setVisibility(View.VISIBLE);
        } else {
            //            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
            isFullScreen = false;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//显示状态栏
            ib_main_right_player.setVisibility(View.VISIBLE);
            rl_main_left_player.setVisibility(View.VISIBLE);
            if (tab == 3) {
                img_customer_service.setVisibility(View.GONE);
            } else {
                img_customer_service.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
//        if (!player.onBackPressed()) {
//            super.onBackPressed();
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 对返回键的处理，横屏时返回后变竖屏，竖屏时返回后退出
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                isFullScreen = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || getRequestedOrientation() == ActivityInfo
                    .SCREEN_ORIENTATION_UNSPECIFIED) {
                finish();
            }
        }
        return false;
    }

    public static void newIntent(Activity context, DirectBean directBean/*, ArrayList<DirectBean> directBeanList*/, int position) {
        Intent intent = new Intent(context, DirectPlayDetailsActivityForBjysdk.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DirectBean", directBean);
//        bundle.putSerializable("DirectBeanList", directBeanList);
        intent.putExtras(bundle);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    public static void newIntent(Activity context, DirectBean directBean/*, ArrayList<DirectBean> directBeanList*/, int position, int type) {
        Intent intent = new Intent(context, DirectPlayDetailsActivityForBjysdk.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DirectBean", directBean);
//        bundle.putSerializable("DirectBeanList", directBeanList);
        intent.putExtras(bundle);
        intent.putExtra("position", position);
        intent.putExtra("wherefrom", type);
        context.startActivity(intent);
    }

    /**
     * 加载课程表
     */
    public void loadDirectPlayInfo() {
        obtatinDataListener = new ObtatinDataListener(DirectPlayDetailsActivityForBjysdk.this);
        SendRequest.getLiveDataForClassSchedule(directBean.getRid(), uid, obtatinDataListener);
    }

//    @Override
//    public void onVideoInfoInitialized(BJPlayerView bjPlayerView, HttpException exception) {
//        DebugUtil.e(TAG, "onVideoInfoInitialized:");
//        //TODO: 视频信息初始化结束
//        if (exception != null) {
//            // 视频信息初始化成功
//            VideoItem videoItem = player.getVideoItem();
//        }
//    }
//
//    @Override
//    public void onPause(BJPlayerView bjPlayerView) {
//        //TODO: video暂停
//        DebugUtil.e(TAG, "onPause:");
//    }
//
//    @Override
//    public void onPlay(BJPlayerView bjPlayerView) {
//        //TODO: 开始播放
//        DebugUtil.e(TAG, "onPlay:");
//    }
//
//    @Override
//    public void onError(BJPlayerView bjPlayerView, int i) {
//        //TODO: 播放出错
//        DebugUtil.e(TAG, "onError:" + " onError:" + i);
//    }
//
//    @Override
//    public void onUpdatePosition(BJPlayerView bjPlayerView, int i) {
//        //TODO: 播放过程中更新播放位置
//        DebugUtil.e(TAG, "onUpdatePosition:" + i);
//    }
//
//    @Override
//    public void onSeekComplete(BJPlayerView bjPlayerView, int i) {
//        //TODO: 拖动进度条
//        DebugUtil.e(TAG, "onSeekComplete:" + i);
//    }
//
//    @Override
//    public void onSpeedUp(BJPlayerView bjPlayerView, float v) {
//        //TODO: 设置倍速播放
//        DebugUtil.e(TAG, "onSpeedUp:" + v);
//    }
//
//    @Override
//    public void onVideoDefinition(BJPlayerView bjPlayerView, int i) {
//        //TODO: 设置清晰度完成
//        DebugUtil.e(TAG, "onVideoDefinition:" + i);
//    }
//
//    @Override
//    public void onPlayCompleted(BJPlayerView bjPlayerView, VideoItem videoItem, SectionItem nextSection) {
//        //TODO: 当前视频播放完成 [nextSection已被废弃，请勿使用]
//        DebugUtil.e(TAG, "onPlayCompleted:" + videoItem.videoId);
//    }
//
//    @Override
//    public void onVideoPrepared(BJPlayerView bjPlayerView) {
//        DebugUtil.e(TAG, "onVideoPrepared" + bjPlayerView.getVideoItem().videoId);
//        //TODO: 准备好了，马上要播放
//        // 可以在这时获取视频时长
//        player.getDuration();
//    }
//
//    @Override
//    public void onCaton(BJPlayerView bjPlayerView) {
//
//    }
//
//    @Override
//    public String getVideoTokenWhenInvalid() {
//        return null;
//    }
//
//    @Override
//    public void performPickFor(FilePicker picker) {
//    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<ArrayList<DirectBean>, String> {
        private WeakReference<DirectPlayDetailsActivityForBjysdk> weak_activity;

        public ObtatinDataListener(DirectPlayDetailsActivityForBjysdk activity) {
            weak_activity = new WeakReference<>(activity);
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity.get() != null) {
                weak_activity.get().mCustomLoadingDialog.show();
            }
        }

        @Override
        public void onSuccess(final ArrayList<DirectBean> res) {
            if (weak_activity.get() != null) {
                weak_activity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.get().flushContent_OnSucess(res);
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity.get() != null) {
                weak_activity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.get().flushContent_OnFailure(res);
                    }
                });
            }
        }
    }

    public void flushContent_OnSucess(ArrayList<DirectBean> res) {
        mCustomLoadingDialog.dismiss();
        if (res != null && res.size() > 0) {
            directBeanListForClassSchedule = res;
            DataStore_Direct.directDatailList = directBeanListForClassSchedule;
            iniFristdata(isLocaleData);
        } else {
            ToastUtils.showToast(getString(R.string.course_not_start));
        }
    }

    /**
     * 初始化课程列表数据
     */
    private void iniFristdata(boolean isLocaleData) {
        if (!isLocaleData) {
            if (!isFinishing()) {
                synchronizationLocal(directBeanListForClassSchedule);
            }
        }
        View HeaderView = getLayoutInflater().inflate(R.layout.header, listview, false);
        listview.setPinnedHeader(HeaderView);
        listview.setAdapter(directClassScheduleAdapter);
        listview.setOnScrollListener(directClassScheduleAdapter);
        directClassScheduleAdapter.setDirectBeanList(directBeanListForClassSchedule, directBeanListForClassSchedule.get(position).getBjyvideoid(), 4);
//        tv_main_title.setText(directBeanListForClassSchedule.get(position).getTitle());
        for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
            if (directBeanListForClassSchedule.get(i).getNumber() != null) {
                if (directBeanListForClassSchedule.get(i).getNumber().equals(getplayrecord)) {
                    playrow = i;
                    break;
                }
            }
        }
        playNumbe = playrow;
        if (directBeanListForClassSchedule.get(position).getIsTrial() == 1 || "1".equals(directBeanListForClassSchedule.get(position).getIs_buy())) {
            if (wherefrom == 0) {
                listview.setSelection(position >= 1 ? position - 1 : 0);
                initData(directBeanListForClassSchedule.get(position));
                CommonUtils.putSharedPreferenceItem(null, directBean.getRid(), String.valueOf(directBeanListForClassSchedule.get(position).getNumber()));
            } else {
                if (playNumbe >= directBeanListForClassSchedule.size() && directBeanListForClassSchedule.size() > 1) {
                    listview.setSelection(0);
                    directClassScheduleAdapter.playVideo(directBeanListForClassSchedule.get(0), 0);
                    directClassScheduleAdapter.setDirectId(directBeanListForClassSchedule.get(0).getBjyvideoid());
                } else {
                    listview.setSelection(playNumbe >= 1 ? playNumbe - 1 : 0);
                    directClassScheduleAdapter.playVideo(directBeanListForClassSchedule.get(playNumbe), playNumbe);
                    directClassScheduleAdapter.setDirectId(directBeanListForClassSchedule.get(playNumbe).getBjyvideoid());
                }

            }
        }
    }

    public void flushContent_OnFailure(String res) {
        mCustomLoadingDialog.dismiss();
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
        } else if (SendRequest.ERROR_SERVER.equals(res)) {
            ToastUtils.showToast(R.string.server_error);
        }
    }

    /**
     * 加载pdf信息
     * 本地有的话加载本地
     * 没有的话下载
     *
     * @param fileUrl 文件下载地址
     */
    public void loadPDF(String fileUrl) {
        if (!StringUtil.isEmpty(fileUrl)) {
            String[] split = fileUrl.split("/");
            String fileName = split[split.length - 1];
            jyDownLoadUrlForLocal = Environment.getExternalStorageDirectory() + "/jy/" + fileName;
            final File file = new File(jyDownLoadUrlForLocal);
            boolean exists = file.exists();
            if (file != null && exists) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        pdfView.fromFile(file)
//                                .defaultPage(pageNumber)
//                                .onPageChange(DirectPlayDetailsActivityForCCsdk.this)
//                                .load();
                        pdfView.setVisibility(View.VISIBLE);
                        lv_pdf.setVisibility(View.GONE);
                        rl_main_left_pdf.setVisibility(View.VISIBLE);
                        isShowPdf = true;
                        initPdfView(jyDownLoadUrlForLocal);
                    }
                });
            } else {
                file.mkdirs();//记得创建文件夹
                pd_update = new CustomProgressDialog(this);
                pd_update.setLoadingMsg(getResources().getString(R.string.downloadpdf));
                pd_update.setCancelable(false);
                new Thread(new DownLoadPDFFileThreadTask(fileUrl, jyDownLoadUrlForLocal)).start();
            }
        } else {
            if (!isShowJy) {
                isShowJy = true;
                ToastUtils.showToast(R.string.noTranslation);
            }
        }
    }

    /**
     * pdf下载线程
     */
    public class DownLoadPDFFileThreadTask implements Runnable {

        private String path;
        private String filepath;

        public DownLoadPDFFileThreadTask(String path, String filepath) {
            this.path = path;
            this.filepath = filepath;
        }

        @Override
        public void run() {
            try {
//                DownLoadFileTask.makeRootDirectory(filepath);
                File file = DownLoadFileTask.getFile(path, filepath, pd_update);
                pd_update.dismiss();
//                pdfView.fromFile(new File(filepath))
//                        .defaultPage(pageNumber)
//                        .onPageChange(DirectPlayDetailsActivityForCCsdk.this)
//                        .load();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pdfView.setVisibility(View.VISIBLE);
                        lv_pdf.setVisibility(View.GONE);
                        rl_main_left_pdf.setVisibility(View.VISIBLE);
                        directClassPdfAdapter.notifyDataSetChanged();
                        isShowPdf = true;
                        initPdfView(filepath);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd_update.dismiss();
                        FileUtils.deleteFolder(filepath);
                        ToastUtils.showToast(R.string.pdfDownloadError);//下载失败
                    }
                });
            }
        }
    }

    /**
     * 同步本地的数据
     *
     * @param directBeanListForClassSchedule
     */
    private void synchronizationLocal(List<DirectBean> directBeanListForClassSchedule) {
        for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
            DirectBean directBean = directBeanListForClassSchedule.get(i);
            if (directBeanListForClassSchedule.get(i).getVideoType() == 0) {
                if (StringUtil.isEmpty(directBean.getBjyvideoid())) {
                    continue;
                }
                //百家云视频（以前的网课）
                DirectBean directBean1 = null;
                if (!StringUtils.isEmpty(directBeanListForClassSchedule.get(i).getBjyvideoid())){
                    directBean1 = daoUtils.queryDirectBeanForBjyVedioId(uid, directBeanListForClassSchedule.get(i).getBjyvideoid(),
                            directBeanListForClassSchedule.get(i).getNumber());
                }
                if (directBean1 == null && !StringUtils.isEmpty(directBeanListForClassSchedule.get(i).getCcCourses_id())) {
                    //通过百家云的id查询不到的还需要用CC视频的id查询一次
                    directBean1 = daoUtils.queryDirectBeanForCCVedioId(uid, directBeanListForClassSchedule.get(i).getCcCourses_id(),
                            directBeanListForClassSchedule.get(i).getNumber());
                }
                if (directBean1 != null) {
                    directBeanListForClassSchedule.get(i).setDown_status(directBean1.getDown_status());
                    directBeanListForClassSchedule.get(i).setLocalPath(directBean1.getLocalPath());
                    directBeanListForClassSchedule.get(i).setStart(directBean1.getStart());
                    directBeanListForClassSchedule.get(i).setEnd(directBean1.getEnd());
                }
            } else if (directBeanListForClassSchedule.get(i).getVideoType() == 1) {
                DirectBean directOrg = directBeanListForClassSchedule.get(i);
                if (StringUtil.isEmpty(directBean.getLubourl()) && !StringUtil.isEmpty(directOrg.getRoom_id())) {
                    DirectBean directQuery = daoUtils.queryDirectBeanForPlayBack(uid, directOrg.getRoom_id(), directOrg.getSession_id(), directOrg.getNumber());
                    if (directQuery != null) {
                        directBeanListForClassSchedule.get(i).setDown_status(directQuery.getDown_status());
                        directBeanListForClassSchedule.get(i).setLocalPath(directQuery.getLocalPath());
                        directBeanListForClassSchedule.get(i).setStart(directQuery.getStart());
                        directBeanListForClassSchedule.get(i).setEnd(directQuery.getEnd());
                    }


                } else {
                    DirectBean directBean1 = daoUtils.queryDirectBeanForGeeneVedioId(uid, directBeanListForClassSchedule.get(i).getLubourl(),
                            directBeanListForClassSchedule.get(i).getNumber());
                    if (directBean1 != null) {
                        DebugUtil.e("directBean1：" + directBean1.toString());
                        directBeanListForClassSchedule.get(i).setDown_status(directBean1.getDown_status());
                        directBeanListForClassSchedule.get(i).setLocalPath(directBean1.getLocalPath());
                        directBeanListForClassSchedule.get(i).setStart(directBean1.getStart());
                        directBeanListForClassSchedule.get(i).setEnd(directBean1.getEnd());
//                    directBeanListForClassSchedule.get(i).setVideo_status(directBean1.getVideo_status());
                    }
                }

            }
        }
        //删除rid相同的课程后，同步完数据库之后再加入课时缓存
//        DebugUtil.e("synchronizationLocal:" + DirectPlayDetailsActivityForCCsdk.this.directBean.toString());
        daoUtils.deletDirectBeanListForRid(DirectPlayDetailsActivityForBjysdk.this.directBean.getRid());
        for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
            DirectBean directBean = directBeanListForClassSchedule.get(i);
            directBean.setUserid(uid);
            directBean.setRid(DirectPlayDetailsActivityForBjysdk.this.directBean.getRid());
            daoUtils.insertOrUpdateDirectBeanCacheClass(directBean);
//            DebugUtil.e("synchronizationLocal:" + directBean.toString());
        }
    }

    /**
     * 接收广播后刷新数据 主要针对下载
     *
     * @param context 接受广播的上下文
     * @param intent  该广播的意图
     */
    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
        DirectBean mDirectBean = (DirectBean) intent.getSerializableExtra("DirectBean");
        DebugUtil.e("onReceiveBroadCast:bjy" + mDirectBean.toString());
        //在下载的时候不要刷新ui
        if ("200".equals(mDirectBean.getDown_status())) {
            return;
        }
        for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
            if (directBeanListForClassSchedule.get(i).getVideoType() == 0) {

                if (directBeanListForClassSchedule.get(i).getBjyvideoid() != null && directBeanListForClassSchedule.get(i).getBjyvideoid().equals(mDirectBean.getBjyvideoid())) {
                    directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
                    directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
                    directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
                    directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
                }else if (directBeanListForClassSchedule.get(i).getCcCourses_id() != null && directBeanListForClassSchedule.get(i).getCcCourses_id().equals(mDirectBean.getCcCourses_id())){
                    directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
                    directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
                    directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
                    directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
                }
            } else {
                if (!StringUtils.isEmpty(directBeanListForClassSchedule.get(i).getLubourl())&&!StringUtils.isEmpty(mDirectBean.getLubourl())&&directBeanListForClassSchedule.get(i).getLubourl().equals(mDirectBean.getLubourl())) {
                    directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
                    directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
                    directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
                    directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
                }
            }
        }
        directClassScheduleAdapter.notifyDataSetChanged();
    }

    private void initPdfView(String path) {
        core = openFile(path);
        if (core == null) {
            Toast.makeText(this, "open pdf file failed", Toast.LENGTH_SHORT).show();
//            finish();
        }
        totalPageCount = core.countPages();
        if (lastPagePosition > totalPageCount) {
            lastPagePosition = totalPageCount;
            isFinished = true;
        }
        if (totalPageCount == 0) {
            Toast.makeText(this, "PDF file has format error", Toast.LENGTH_SHORT).show();
            finish();
        }
        //one page per screen
        core.setDisplayPages(1);

        mDocView = new MuPDFReaderView(this) {
            @Override
            protected void onMoveToChild(int i) {
                DebugUtil.e(TAG, "onMoveToChild " + i);
                super.onMoveToChild(i);
//                mTitle.setText(String.format(" %s / %s ",  i + 1, totalPageCount));
                if ((i + 1) == totalPageCount) {
                    isFinished = true;
                }
            }

            @Override
            protected void onTapMainDocArea() {
                //Log.d(TAG,"onTapMainDocArea");
            }

            @Override
            protected void onDocMotion() {
                //Log.d(TAG,"onDocMotion");
            }

        };
//        mDocView.setAdapter(new MuPDFPageAdapter(DirectPlayDetailsActivityForBjysdk.this, this, core));
        mDocView.setKeepScreenOn(true);
        mDocView.setLinksHighlighted(false);
        mDocView.setScrollingDirectionHorizontal(true);

        rl_content_play.addView(mDocView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mDocView.setDisplayedViewIndex(lastPagePosition - 1);
//        mTitle.setText(String.format(" %s / %s ", lastPagePosition, totalPageCount));
    }

    private MuPDFCore openFile(String path) {
        int lastSlashPos = path.lastIndexOf('/');
        mFileName = lastSlashPos == -1 ? path
                : path.substring(lastSlashPos + 1);
        System.out.println("Trying to open " + path);
        try {
            core = new MuPDFCore(this, path);
            // New file: drop the old outline data
            //OutlineActivityData.set(null);
//            PDFPreviewGridActivityData.set(null);
        } catch (Exception e) {
            System.out.println(e);
            DebugUtil.e(TAG, e.getMessage());
            return null;
        }
        return core;
    }

    // 视频中的秒数转成字符串
    public static String millsecondsToStr(Long seconds) {
        seconds = seconds / 1000;
        String result = "";
        Long hour = 0L, min = 0L, second = 0L;
        hour = seconds / 3600;
        min = (seconds - hour * 3600) / 60;
        second = seconds - hour * 3600 - min * 60;
        if (hour < 10) {
            result += "0" + hour + ":";
        } else {
            result += hour + ":";
        }
        if (min < 10) {
            result += "0" + min + ":";
        } else {
            result += min + ":";
        }
        if (second < 10) {
            result += "0" + second;
        } else {
            result += second;
        }
        return result;
    }

    private static class ObtatinPdfListener extends ObtainDataFromNetListener<ArrayList<PdfBean>, String> {
        private WeakReference<DirectPlayDetailsActivityForBjysdk> weak_activity;

        public ObtatinPdfListener(DirectPlayDetailsActivityForBjysdk activity) {
            weak_activity = new WeakReference<>(activity);
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity.get() != null) {
                weak_activity.get().mCustomLoadingDialog.show();
            }
        }

        @Override
        public void onSuccess(final ArrayList<PdfBean> res) {
            if (weak_activity.get() != null) {
                weak_activity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.get().flushPdfContent_OnSucess(res);
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity.get() != null) {
                weak_activity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.get().flushPdfContent_OnFailure(res);
                    }
                });
            }
        }
    }

    public void flushPdfContent_OnSucess(ArrayList<PdfBean> res) {
        mCustomLoadingDialog.dismiss();
        if (res != null && res.size() > 0) {
//            tv_main_title.setText(directBean.getTitle());
//            webview.loadDataWithBaseURL(null, directBean.getContent(), "text/html", "UTF-8", null);

            directBeanListPDFForClassSchedule.addAll(res);
            DataStore_Direct.directPdfDatailList = directBeanListPDFForClassSchedule;
            directClassPdfAdapter.notifyDataSetChanged();
        } else {
            if (tab == 1) {
                ll_no_notes.setVisibility(View.VISIBLE);
            }
        }
    }

    public void flushPdfContent_OnFailure(String res) {
        mCustomLoadingDialog.dismiss();
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
        } else if (SendRequest.ERROR_SERVER.equals(res)) {
            ToastUtils.showToast(R.string.server_error);
        }
    }

    /**
     * 加载pdf列表
     */
    public void getLivePDFForClassSchedule() {
        obtatinPdfListener = new ObtatinPdfListener(DirectPlayDetailsActivityForBjysdk.this);
        SendRequest.getLivePDFForClassSchedule(directBean.getRid(), uid, obtatinPdfListener);
    }
}
