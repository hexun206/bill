package com.huatu.teacheronline.direct;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdflib.FilePicker;
import com.artifex.mupdflib.MuPDFCore;
import com.artifex.mupdflib.MuPDFPageAdapter;
import com.artifex.mupdflib.MuPDFReaderView;
import com.gensee.common.PlayerEnv;
import com.gensee.common.ServiceType;
import com.gensee.download.VodDownLoader;
import com.gensee.entity.ChatMsg;
import com.gensee.entity.DocInfo;
import com.gensee.entity.InitParam;
import com.gensee.entity.QAMsg;
import com.gensee.entity.VodObject;
import com.gensee.media.PlaySpeed;
import com.gensee.media.VODPlayer;
import com.gensee.pdu.GSDocView;
import com.gensee.utils.StringUtil;
import com.gensee.view.GSDocViewGx;
import com.gensee.view.GSVideoView;
import com.gensee.vod.VodSite;
import com.greendao.DaoUtils;
import com.greendao.DirectBean;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.H5DetailActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.adapter.DirectClassChatAdapter;
import com.huatu.teacheronline.direct.adapter.DirectClassPdfAdapter;
import com.huatu.teacheronline.direct.adapter.DirectClassScheduleCustomAdapter;
import com.huatu.teacheronline.direct.bean.PdfBean;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.DownLoadFileTask;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.ChooseSpeedPopwindows;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.CustomProgressDialog;
import com.huatu.teacheronline.widget.PinnedHeaderListView;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 点播播放页面
 * Created by ply on 2016/1/18.
 */
public class DirectPlayDetailsActivityBase extends BaseActivity implements VodSite.OnVodListener, VODPlayer.OnVodPlayListener, SeekBar
        .OnSeekBarChangeListener, GSDocView.OnDocViewEventListener, OnPageChangeListener, AbsListView.OnScrollListener,FilePicker.FilePickerSupport{
    private static final String TAG = "DirectPlayDetailsActivityForBase";
    private RelativeLayout rl_main_left;
    private RelativeLayout rl_main_left_player;
    private TextView tv_main_title;
    private RadioButton tv_jianjie, tv_kebiao, tv_hudong,tv_chanjian;
    private WebView webview;
    private RelativeLayout rl_deatil_waiting;
    private ImageView iv_icon;
    private ImageView iv_screenDirect;
    private LinearLayout top_bar;
    private RelativeLayout rl_fullScreenBack;
    private TextView tv_fullScreenTitle;
    private RelativeLayout rl_fullScreenTopBar;
    private RelativeLayout rl_sendMessage;
    //底部进度条布局
    private RelativeLayout rl_play_bottom, rl_play, rl_screen;
    private ImageView iv_play, iv_screenVod;
    private SeekBar skbProgress;
    private TextView playDuration, videoDuration;
    //listview
    private View loadView;
    private View loadIcon;
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;
    private PinnedHeaderListView listview;
    public DirectClassScheduleCustomAdapter directClassScheduleAdapter;
    private ArrayList<DirectBean> directBeanListForClassSchedule = new ArrayList<DirectBean>();
    private int currentPagerForClassSchedule = 1;
    private int limit = 8;
    //往期
    private GSDocViewGx dosView;
    private GSVideoView videoView;
    private VodSite vodSite;
    private VODPlayer vodPlayer;

    private DirectBean directBean;
    private CustomAlertDialog mCustomLoadingDialog;
    private ObtatinDataListener obtatinDataListener;
    private boolean isBarVisible = true;
    private boolean isPlaying = true;//默认播放
    private boolean isScreen = false;
    private boolean isVideoShow = false;//是否有视频直播
    private int tab = 1;//1 讲义 2课表 3 互动 4常见问题
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private String flag = "视频";
    private String nickName;
    private PowerManager.WakeLock m_wakeLock;
    private int directPosition;//当前播放项
    public int position;//传过来课程表位置
    private String uid;
    private PDFView pdfView;
    private CustomProgressDialog pd_update;//讲义下载的进度条
    private String jyDownLoadUrlForLocal = "";//讲义下载到本地地址
    private boolean isShowJy;
    private VodDownLoader downloader;
    private DaoUtils daoUtils;//下载记录
    private ImageView ib_main_right;//下载页面
    private int wherefrom;//0代表从我的直播课直接跳过来的
    private boolean isLocaleData = false;
    private String camera;//人像 1 显示 -1隐藏
    private String getplayrecord;//最后播放的视频行数
    public int playNumbe;//最后播放的视频行数转化类型
    private int playrow = 0;//根据number查找到对应的行数
    private int indexChat = 1;//聊天记录页码
    private ListView lv_chat;
    private DirectClassChatAdapter directClassChatAdapter;
    private List<ChatMsg> chatmsgList = new ArrayList<ChatMsg>();
    private boolean ispage;//聊天记录是否有下一页
    private boolean isPlayLoacl = false;//是否播放本地的视频
    private LinearLayout ll_no_chat;
    private RelativeLayout ll_no_notes;
    private ImageView img_customer_service;

    private MuPDFCore core;
    private int totalPageCount;
    private int lastPagePosition;
    private boolean isFinished;
    private MuPDFReaderView mDocView;
    private LinearLayout ll_content_play;
    private String mFileName;
    private ObtatinPdfListener obtatinPdfListener;
    private LinearLayout top_bar_no_titlebar;
    private ImageView ib_main_right_player;
    private ChooseSpeedPopwindows chooseSpeedPopwindows;//倍速播放弹出框
    private TextView tv_speed;//倍速播放
    private int speedType = 0;
    private ArrayList<PdfBean> directBeanListPDFForClassSchedule = new ArrayList<PdfBean>();

    private ListView lv_pdf;
    private DirectClassPdfAdapter directClassPdfAdapter;

    @Override
    public void initView() {
        setContentView(R.layout.activity_direct_play_layout);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = getWindow();
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownManageActivity.ACTION_REFRASH);
        setIntentFilter(intentFilter);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        m_wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "cn");
        m_wakeLock.acquire(); //设置保持唤醒
        VodSite.init(CustomApplication.applicationContext, null);//点播全局初始化，app运行后加载一次
        nickName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_NICKNAME, "");
        if (StringUtil.isEmpty(nickName)) {
            nickName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, "2");
        }
        directBean = (DirectBean) getIntent().getSerializableExtra("DirectBean");
        directBeanListForClassSchedule = DataStore_Direct.directDatailList;
        position = getIntent().getIntExtra("position", 0);
        wherefrom = getIntent().getIntExtra("wherefrom", 0);
        tv_hudong = (RadioButton) findViewById(R.id.tv_hudong);
        tv_hudong.setText("聊天");
        ib_main_right = (ImageView) findViewById(R.id.ib_main_right);
        ib_main_right_player = (ImageView) findViewById(R.id.ib_main_right_player);
        if (directBean.getIs_buy()!=null && Integer.parseInt(directBean.getIs_buy()) == 1) {
            ib_main_right.setVisibility(View.VISIBLE);
            ib_main_right.setOnClickListener(this);
            ib_main_right_player.setVisibility(View.VISIBLE);
            ib_main_right_player.setOnClickListener(this);
        }
        getplayrecord = CommonUtils.getSharedPreferenceItem(null, directBean.getRid(), "");

        //拿到number循环遍历list看数据在那个位置
//        int j=0;
//        for (int i=0;i<directBeanListForClassSchedule.size();i++){
//            if(directBeanListForClassSchedule.get(i).getNumber().equals(getplayrecord)){
//                j=i;
//                break;
//            }
//        }
        mCustomLoadingDialog = new CustomAlertDialog(DirectPlayDetailsActivityBase.this, R.layout.dialog_loading_custom);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        rl_main_left_player = (RelativeLayout) findViewById(R.id.rl_main_left_player);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_speed = (TextView) findViewById(R.id.tv_speed);
        tv_jianjie = (RadioButton) findViewById(R.id.tv_jianjie);
        tv_kebiao = (RadioButton) findViewById(R.id.tv_kebiao);
        tv_chanjian = (RadioButton) findViewById(R.id.tv_chanjian);
        webview = (WebView) findViewById(R.id.webview);
        rl_deatil_waiting = (RelativeLayout) findViewById(R.id.rl_deatil_waiting);
        ll_content_play = (LinearLayout) findViewById(R.id.ll_content_play);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        iv_screenDirect = (ImageView) findViewById(R.id.iv_directScreen);
        iv_screenDirect.setVisibility(View.GONE);
        top_bar = (LinearLayout) findViewById(R.id.top_bar);
        top_bar_no_titlebar = (LinearLayout) findViewById(R.id.top_bar_no_titlebar);
        rl_sendMessage = (RelativeLayout) findViewById(R.id.rl_sendMessage);
        rl_fullScreenTopBar = (RelativeLayout) findViewById(R.id.rl_fullScreenTopBar);
        rl_fullScreenBack = (RelativeLayout) findViewById(R.id.rl_fullScreenBack);
        tv_fullScreenTitle = (TextView) findViewById(R.id.tv_fullScreenTitle);
        rl_play_bottom = (RelativeLayout) findViewById(R.id.rl_play_bottom);
        rl_play_bottom.setVisibility(View.VISIBLE);
        rl_play = (RelativeLayout) findViewById(R.id.rl_play);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_play.setEnabled(false);
        rl_screen = (RelativeLayout) findViewById(R.id.rl_screen);
        iv_screenVod = (ImageView) findViewById(R.id.iv_screen);
        img_customer_service = (ImageView) findViewById(R.id.img_customer_service);
        skbProgress = (SeekBar) findViewById(R.id.skbProgress);
        skbProgress.setOnSeekBarChangeListener(this);
        skbProgress.setEnabled(false);
        playDuration = (TextView) findViewById(R.id.playDuration);
        videoDuration = (TextView) findViewById(R.id.videoDuration);
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        listview = (PinnedHeaderListView) findViewById(R.id.listview);

        lv_chat = (ListView) findViewById(R.id.chat_listview);//聊天记录列表
        lv_pdf = (ListView) findViewById(R.id.pdf_listview);//pdf列表
        ll_no_chat = (LinearLayout) findViewById(R.id.ll_no_chat);//暂无聊天记录提示
        ll_no_notes = (RelativeLayout) findViewById(R.id.ll_no_notes);//暂无pdf
        lv_chat.setVisibility(View.GONE);
        lv_chat.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        directClassChatAdapter = new DirectClassChatAdapter(DirectPlayDetailsActivityBase.this, chatmsgList);
        lv_chat.setAdapter(directClassChatAdapter);

        directClassScheduleAdapter = new DirectClassScheduleCustomAdapter(this);
        directClassScheduleAdapter.seDirectBean(directBean);
        videoView = (GSVideoView) findViewById(R.id.videoView);
        videoView.setRenderMode(GSVideoView.RenderMode.RM_ADPT_XY);
        dosView = (GSDocViewGx) findViewById(R.id.docView);
        dosView.showAdaptView();
        dosView.setBackgroundColor(getResources().getColor(R.color.green003));

        vodSite = new VodSite(CustomApplication.applicationContext);//创建VodSite点播管理器
        vodSite.setVodListener(this);
        tv_main_title.setText(directBean.getTitle());
        tab = 2;
        tv_hudong.setVisibility(View.VISIBLE);
        tv_hudong.setOnClickListener(this);
        listview.setVisibility(View.VISIBLE);
        daoUtils = DaoUtils.getInstance();
        //pdf相关
        pdfView = (PDFView) findViewById(R.id.pdfView);
        if (directBeanListForClassSchedule == null || directBeanListForClassSchedule.size() == 0) {
            //判断有无网络是否加载本地
            if (CommonUtils.isNetWorkAvilable()) {
                loadDirectPlayInfo();
            } else {
                List<DirectBean> directBeans = daoUtils.queryDirectBeanForCacheClassForRid(directBean.getRid());
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
                iniFristDate(isLocaleData);
            }

        } else {
            iniFristDate(isLocaleData);
        }
        chooseSpeedPopwindows = new ChooseSpeedPopwindows(this);
        getLivePDFForClassSchedule();
    }

    @Override
    public void setListener() {
        tv_speed.setOnClickListener(this);
        rl_main_left.setOnClickListener(this);
        ib_main_right.setOnClickListener(this);
        rl_main_left_player.setOnClickListener(this);
        tv_jianjie.setOnClickListener(this);
        tv_kebiao.setOnClickListener(this);
        tv_chanjian.setOnClickListener(this);
        rl_play.setOnClickListener(this);
        rl_screen.setOnClickListener(this);
        videoView.setOnClickListener(this);
        dosView.setOnDocViewClickedListener(this);
        rl_fullScreenBack.setOnClickListener(this);
        iv_icon.setOnClickListener(this);
        lv_chat.setOnScrollListener(this);
        img_customer_service.setOnClickListener(this);
        directClassScheduleAdapter.setVideoClickbutton(new DirectClassScheduleCustomAdapter.VideoClick() {
            @Override
            public void setVideoClick() {
                chatmsgList.clear();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
            case R.id.rl_main_left_player:
                    back();
//                downloader.release();
                break;
            case R.id.tv_speed:
                chooseSpeedPopwindows.showPopUp(v, this,speedType);
                break;
            case R.id.tv_speed_original:
                speedType = 0;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                tv_speed.setText(R.string.speed_original);
                vodPlayer.setSpeed(PlaySpeed.SPEED_NORMAL,null);
                break;
            case R.id.tv_speed_quick1:
                speedType = 1;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                tv_speed.setText(R.string.speed_quick1);
                vodPlayer.setSpeed(PlaySpeed.SPEED_125,null);
                break;
            case R.id.tv_speed_quick2:
                speedType = 2;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                tv_speed.setText(getString(R.string.speed_quick2));
                vodPlayer.setSpeed(PlaySpeed.SPEED_150,null);
                break;
            case R.id.tv_speed_quick3:
                speedType = 3;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                tv_speed.setText(getString(R.string.speed_quick3));
                vodPlayer.setSpeed(PlaySpeed.SPEED_200,null);
                break;
            case R.id.ib_main_right:
            case R.id.ib_main_right_player:
//                if (directBeanListForClassSchedule.get(playNumbe).getVideo_status().equals("3")){
//                    ToastUtils.showToast("本课程暂无视频可提供下载！");
//                    return;
//                }
                if (directBeanListForClassSchedule != null && directBeanListForClassSchedule.size() > 0) {
                    DownManageActivity.newIntent(DirectPlayDetailsActivityBase.this, directBeanListForClassSchedule);
                }
                break;
            case R.id.videoView://视频点击事件
                if (directBean != null) {
                    isScreenButtonVisible();
                }
                break;
            case R.id.iv_icon://ppt和文档的切换
                if (directBean == null) {
                    return;
                }
                if ("视频".equals(flag)) {
                    flag = "PPT";
                    iv_icon.setBackgroundResource(R.drawable.ic_toppt);
                    dosView.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                } else if ("PPT".equals(flag)) {
                    flag = "视频";
                    iv_icon.setBackgroundResource(R.drawable.ic_tovi);
                    dosView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                }
                break;
            case R.id.rl_play://开始/暂停
                if (directBean == null) {
                    return;
                }
                if (vodPlayer != null) {
                    if (isPlaying) {
                        isPlaying = false;
                        iv_play.setBackgroundResource(R.drawable.ic_playv);
                        vodPlayer.pause();
                    } else {
                        isPlaying = true;
                        iv_play.setBackgroundResource(R.drawable.ic_pause);
                        vodPlayer.resume();
                    }
                }
                break;
            case R.id.rl_screen://全屏
                if (directBean == null) {
                    return;
                }
                if (isScreen) {
                    isScreen = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                } else {
                    isScreen = true;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
//                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                break;
            case R.id.rl_fullScreenBack://全屏下的返回按钮
                isScreen = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case R.id.img_customer_service://客服
                MobclickAgent.onEvent(this, "consultationOnClik");
                H5DetailActivity.newIntent(this, "咨询", directBean.getCustomer());
//                vodPlayer.setSpeed(PlaySpeed.SPEED_200,null);
                break;

            case R.id.tv_jianjie://讲义
                if (tab != 1) {
                    tab = 1;
                    if (directBean == null) {
                        return;
                    }
                    rl_sendMessage.setVisibility(View.GONE);
                    pdfView.setVisibility(View.VISIBLE);
                    listview.setVisibility(View.GONE);
                    lv_chat.setVisibility(View.GONE);
                    ll_no_chat.setVisibility(View.GONE);
                    if (directBean == null || directBeanListForClassSchedule == null || directBeanListForClassSchedule.size() == 0) {
                        return;
                    }
                    if (directBeanListPDFForClassSchedule.size() == 0) {
                        ll_no_notes.setVisibility(View.VISIBLE);
                    }else {
                        ll_no_notes.setVisibility(View.GONE);
                    }
//                    String fileUrl = directBeanListForClassSchedule.get(position).getNetclass_pdf();
//                    loadPDF(fileUrl);
                }
                break;
            case R.id.tv_kebiao://课表
                if (tab != 2) {
                    tab = 2;
                    if (mDocView !=null){
                        ll_content_play.removeView(mDocView);
                    }
                    rl_sendMessage.setVisibility(View.GONE);
                    pdfView.setVisibility(View.GONE);
                    listview.setVisibility(View.VISIBLE);
                    ll_no_chat.setVisibility(View.GONE);
                    ll_no_notes.setVisibility(View.GONE);
                    if (directBean == null) {
                        return;
                    }
                    if (directBeanListForClassSchedule != null && directBeanListForClassSchedule.size() > 0) {
                        rl_sendMessage.setVisibility(View.GONE);
                        webview.setVisibility(View.GONE);
                        listview.setVisibility(View.VISIBLE);
                        lv_chat.setVisibility(View.GONE);
                        ll_no_notes.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.tv_hudong://互动
                if (tab != 3) {
                    tab = 3;
                    if (mDocView !=null){
                        ll_content_play.removeView(mDocView);
                    }
                    lv_chat.setVisibility(View.VISIBLE);
                    lv_chat.setAdapter(directClassChatAdapter);
                    directClassChatAdapter.notifyDataSetChanged();
                    webview.setVisibility(View.GONE);
                    listview.setVisibility(View.GONE);
                    if (chatmsgList.size() == 0) {
                        ll_no_chat.setVisibility(View.VISIBLE);
                    } else {
                        ll_no_chat.setVisibility(View.GONE);
                    }
                    pdfView.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_chanjian://常见问题
                if (tab != 4) {
                    tab = 4;
                    if (mDocView !=null){
                        ll_content_play.removeView(mDocView);
                    }
                    lv_chat.setVisibility(View.GONE);
                    listview.setVisibility(View.GONE);
                    webview.setVisibility(View.VISIBLE);
                    ll_no_chat.setVisibility(View.GONE);
                    ll_no_notes.setVisibility(View.GONE);
                    pdfView.setVisibility(View.GONE);
                    webview.loadUrl(DataStore_Direct.Common_problem);
                }
                break;
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
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            pdfView.fromFile(file)
//                                    .defaultPage(pageNumber)
//                                    .onPageChange(DirectPlayDetailsActivityForVodsdk.this)
//                                    .load();
                            initPdfView(jyDownLoadUrlForLocal);
                        }
                    });
                } catch (Exception e) {
                    ToastUtils.showToast("PDF解析失败");
                }
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

    public static void newIntent(Activity context, DirectBean directBean/*, ArrayList<DirectBean> directBeanList*/, int position) {
        Intent intent = new Intent(context, DirectPlayDetailsActivityBase.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DirectBean", directBean);
//        bundle.putSerializable("DirectBeanList", directBeanList);
        intent.putExtras(bundle);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    public static void newIntent(Activity context, DirectBean directBean/*, ArrayList<DirectBean> directBeanList*/, int position, int type) {
        Intent intent = new Intent(context, DirectPlayDetailsActivityBase.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DirectBean", directBean);
//        bundle.putSerializable("DirectBeanList", directBeanList);
        intent.putExtras(bundle);
        intent.putExtra("position", position);
        intent.putExtra("wherefrom", type);
        context.startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            iv_screenVod.setBackgroundResource(R.drawable.ic_fullsc_b);
            img_customer_service.setVisibility(View.GONE);
            top_bar_no_titlebar.setVisibility(View.GONE);
            isScreenButtonVisible();
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            img_customer_service.setVisibility(View.VISIBLE);
            isScreenButtonVisible();
            top_bar_no_titlebar.setVisibility(View.VISIBLE);
            iv_screenVod.setBackgroundResource(R.drawable.ic_fullsc);
        }
    }

    /**
     * 切换播放视频
     */
    public void changePlayVideo(int directPosition) {
        this.directPosition = directPosition;
        rl_deatil_waiting.setVisibility(View.VISIBLE);
        iv_icon.setVisibility(View.GONE);
        isVideoShow = false;
        if (vodPlayer != null) {
            vodPlayer.stop();
            vodPlayer.release();
        }
        videoView.renderDefault();
        initParam(directPosition);
    }

    /**
     * 初始化往期视频参数
     */
    public void initParam(int position) {
        this.position = position;
        DirectBean directBean = directBeanListForClassSchedule.get(position);
        DebugUtil.e("directBeanListForClassSchedule:" + directBean.toString());
        tv_main_title.setText(directBean.getTitle());
        //初始化view
        flag = "视频";
        boolean b = PlayerEnv.loadLibrary();
        DebugUtil.e("PlayerEnv.loadLibrary: "+b );
        iv_icon.setBackgroundResource(R.drawable.ic_tovi);
        videoView.setVisibility(View.VISIBLE);
        dosView.setVisibility(View.GONE);
        if (directBean.getDown_status() != null && Integer.parseInt(directBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_COMPLETE) {
            //播放已下载的
            if (vodPlayer == null) {
                vodPlayer = new VODPlayer();
            }
            vodPlayer.setGSDocViewGx(dosView);
            vodPlayer.setGSVideoView(videoView);
            String localPath = directBean.getLocalPath();
            vodPlayer.play(localPath, this, "",false);
            isPlayLoacl = true;
            InitParam initParam = new InitParam();
            initParam.setDomain(SendRequest.url_liveGensee);//域名
            initParam.setLiveId(directBeanListForClassSchedule.get(position).getLubourl());//录播id
            initParam.setLoginAccount("");//站点认证帐号
            initParam.setLoginPwd(""); //站点认证密码
            initParam.setVodPwd(directBeanListForClassSchedule.get(position).getKouling());//点播口令
            initParam.setNickName(nickName);//昵称  用于统计和显示
            initParam.setServiceType(ServiceType.ST_CASTLINE);
            if (!isFinishing()) {
                vodSite.getVodObject(initParam);
            }
//            vodSite.getChatHistory( directBeanListForClassSchedule.get(position).getLubourl());
        } else {
            //播放未下载的
            InitParam initParam = new InitParam();
            initParam.setDomain(SendRequest.url_liveGensee);//域名
            initParam.setLiveId(directBeanListForClassSchedule.get(position).getLubourl());//录播id
            initParam.setLoginAccount("");//站点认证帐号
            initParam.setLoginPwd(""); //站点认证密码
            initParam.setVodPwd(directBeanListForClassSchedule.get(position).getKouling());//点播口令
            initParam.setNickName(nickName);//昵称  用于统计和显示
            initParam.setServiceType(ServiceType.ST_CASTLINE);
            isPlayLoacl = false;
            if (!isFinishing()) {
                vodSite.getVodObject(initParam);
            }
//            vodSite.getChatHistory(directBeanListForClassSchedule.get(position).getLubourl());
        }
    }

    //获取时间
    private String getTime(int time) {
        return String.format("%02d", time / 3600) + ":"
                + String.format("%02d", time % 3600 / 60) + ":"
                + String.format("%02d", time % 3600 % 60);
    }

    /*****
     * vodSite管理器监听
     *****/
    @Override
    public void onChatHistory(String s, final List<ChatMsg> list, final int i, final boolean b) {
        //聊天记录响应
//        for (int i1 = 0; i1 < list.size(); i1++) {
//            DebugUtil.e("聊天记录：" + list.get(i1).getContent());
//        }
        chatmsgList.addAll(list);
        this.ispage = b;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chatmsgList.size() == 0 && b == false && tab == 3) {
                    ll_no_chat.setVisibility(View.VISIBLE);
                }else{
                    ll_no_chat.setVisibility(View.GONE);
                }
                completeRefresh();
            }
        });

    }

    @Override
    public void onQaHistory(String s, List<QAMsg> list, int i, boolean b) {
        //问答历史记录响应onVodErr
    }

    @Override
    public void onVodErr(int i) {
        //错误响应
        switch (i) {
            case ERR_VOD_NUM_UNEXIST://点播不存在
                showToast("视频不存在");
                break;
            case ERR_TIME_OUT://超时
                showToast("超时");
                break;
            case ERR_UN_NET://无网络
                if (!isPlayLoacl) {
                    showToast("无网络");
                }
                break;
            default:
                showToast("初始化失败");
                break;
        }

    }

    private String vodId;

    @Override
    public void onVodObject(String vodId) {
        // vodSite.getVodObject(initParam)后响应，获取点播vodID
        if (vodPlayer == null) {
            vodPlayer = new VODPlayer();
        }
        vodPlayer.setGSDocViewGx(dosView);
        vodPlayer.setGSVideoView(videoView);
        if (!isPlayLoacl) {
            vodPlayer.play(vodId, this, "",false);
        }
        this.vodId = vodId;
        vodSite.getChatHistory(vodId, indexChat);//根据传入的index 获取分页的内容
    }

    @Override
    public void onVodDetail(VodObject vodObject) {
        //点播详细信息响应

    }

    /*****
     * vodPlay播放器监听
     ****/
    @Override
    public void onInit(int result, boolean haveVideo, final int duration, List<DocInfo> docInfos) {
        //VodPlay初始化完成，开始播放
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (vodSite != null && vodPlayer != null) {
                    rl_deatil_waiting.setVisibility(View.GONE);
                    dosView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    skbProgress.setEnabled(true);
                    iv_play.setEnabled(true);
                    iv_play.setBackgroundResource(R.drawable.ic_pause);
                    isPlaying = true;
                    skbProgress.setMax(duration);
                    videoDuration.setText("/" + getTime(duration / 1000));
                }
            }
        });
    }

    @Override
    public void onPlayStop() {

    }

    @Override
    public void onPlayPause() {

    }

    @Override
    public void onPlayResume() {

    }

    @Override
    public void onPosition(final int position) {
        //时间进度
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playDuration.setText(getTime(position / 1000));
                skbProgress.setProgress(position);
            }
        });
    }

    @Override
    public void onVideoSize(int i, int i1, int i2) {

    }

    @Override
    public void onPageSize(int i, int i1, int i2) {

    }

    @Override
    public void onSeek(final int position) {
        DebugUtil.e("onSeek:" + position);
    }

    @Override
    public void onAudioLevel(int i) {

    }

    @Override
    public void onCaching(final boolean b) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (b) {//进入缓冲
                    rl_deatil_waiting.setVisibility(View.VISIBLE);
                } else {//缓冲完成
                    rl_deatil_waiting.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onVideoStart() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //显示切换按钮
                if (directBeanListForClassSchedule.get(position).getCamera().equals("1")) {
                    iv_icon.setVisibility(View.VISIBLE);
                } else {
                    iv_icon.setVisibility(View.GONE);
                }
                isVideoShow = true;
            }
        });
    }

    @Override
    public void onChat(List<ChatMsg> list) {

    }

    @Override
    public void onDocInfo(List<DocInfo> list) {

    }

    @Override
    public void onError(int i) {
        //播放出错
        DebugUtil.e(TAG, "onError:" + i);
        switch (i) {
            case ERR_PAUSE://暂停失败
                showToast("暂停失败");
                break;
            case ERR_STOP://停止失败
                break;
            case ERR_RESUME://继续播放
                break;
            default:
                if (!isPlayLoacl) {
                    showToast("播放失败");
                }
                break;
        }

    }

    public void showToast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showToast(str);
            }
        });
    }

    /*****
     * seekBar滑动事件
     ******/
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (vodPlayer != null) {
            vodPlayer.seekTo(skbProgress.getProgress());
        }
    }

    /*****
     * onDocViewEnent
     *******/
    @Override
    public boolean onDoubleClicked(GSDocView gsDocView) {
        return false;
    }

    @Override
    public boolean onSingleClicked(GSDocView gsDocView) {
        isScreenButtonVisible();
        return true;
    }

    @Override
    public boolean onEndHDirection(GSDocView gsDocView, int i, int i1) {
        return false;
    }

    /**
     * 隐藏/显示播放屏幕中的按钮
     */
    public void isScreenButtonVisible() {
        if (isBarVisible) {
            isBarVisible = false;
            rl_play_bottom.setVisibility(View.GONE);
            iv_icon.setVisibility(View.GONE);
            rl_fullScreenTopBar.setVisibility(View.GONE);
            if (isScreen) {
                top_bar.setVisibility(View.GONE);
            } else {
                top_bar.setVisibility(View.VISIBLE);
            }
        } else {
            isBarVisible = true;
            rl_play_bottom.setVisibility(View.VISIBLE);
            if (directBeanListForClassSchedule.get(position).getCamera().equals("1")) {
                if (isVideoShow) {
                    iv_icon.setVisibility(View.VISIBLE);
                } else {
                    iv_icon.setVisibility(View.VISIBLE);
                }
            }
            if (isScreen) {
                top_bar.setVisibility(View.GONE);
                rl_fullScreenTopBar.setVisibility(View.VISIBLE);
                tv_fullScreenTitle.setText(directBean.getTitle());
            } else {
                top_bar.setVisibility(View.VISIBLE);
                rl_fullScreenTopBar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 加载播放信息
     */
    public void loadDirectPlayInfo() {
        obtatinDataListener = new ObtatinDataListener(this);
        SendRequest.getLiveDataForClassSchedule(directBean.getRid(), uid, obtatinDataListener);
    }
    /**
     * 加载pdf列表
     */
    public void getLivePDFForClassSchedule() {
        obtatinPdfListener = new ObtatinPdfListener(this);
        SendRequest.getLivePDFForClassSchedule(directBean.getRid(), uid, obtatinPdfListener);
    }


    private static class ObtatinDataListener extends ObtainDataFromNetListener<ArrayList<DirectBean>, String> {
        private WeakReference<DirectPlayDetailsActivityBase> weak_activity;

        public ObtatinDataListener(DirectPlayDetailsActivityBase activity) {
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

    private static class ObtatinPdfListener extends ObtainDataFromNetListener<ArrayList<PdfBean>, String> {
        private WeakReference<DirectPlayDetailsActivityBase> weak_activity;

        public ObtatinPdfListener(DirectPlayDetailsActivityBase activity) {
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

    public void flushContent_OnSucess(ArrayList<DirectBean> res) {
        mCustomLoadingDialog.dismiss();
        if (res != null && res.size() > 0) {
//            tv_main_title.setText(directBean.getTitle());
//            webview.loadDataWithBaseURL(null, directBean.getContent(), "text/html", "UTF-8", null);
            directBeanListForClassSchedule = res;
            DataStore_Direct.directDatailList = directBeanListForClassSchedule;
            iniFristDate(isLocaleData);
        } else {
            ToastUtils.showToast(getString(R.string.course_not_start));
        }
    }
    public void flushPdfContent_OnSucess(ArrayList<PdfBean> res) {
        mCustomLoadingDialog.dismiss();
        if (res != null && res.size() > 0) {
//            tv_main_title.setText(directBean.getTitle());
//            webview.loadDataWithBaseURL(null, directBean.getContent(), "text/html", "UTF-8", null);

            directBeanListPDFForClassSchedule = res;
            DataStore_Direct.directPdfDatailList = directBeanListPDFForClassSchedule;
            lv_pdf.setAdapter(new DirectClassPdfAdapter(this,directBeanListPDFForClassSchedule));
        } else {
            if (tab == 1) {
                ll_no_notes.setVisibility(View.VISIBLE);
            }
        }
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 首次加载课程表
     */
    private void iniFristDate(boolean isLocaleData) {
        if (!isLocaleData) {
            //加载本地数据就不用同步了
            synchronizationLocal(directBeanListForClassSchedule);
        }
        View HeaderView = getLayoutInflater().inflate(R.layout.header, listview, false);
        listview.setPinnedHeader(HeaderView);
        listview.setAdapter(directClassScheduleAdapter);
        listview.setOnScrollListener(directClassScheduleAdapter);
        directClassScheduleAdapter.setDirectBeanList(directBeanListForClassSchedule, directBeanListForClassSchedule.get(position).getLubourl(), 2);
        for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
            if (directBeanListForClassSchedule.get(i).getNumber() != null) {
                if (directBeanListForClassSchedule.get(i).getNumber().equals(getplayrecord)) {
                    playrow = i;
                    break;
                }
            }
        }
        playNumbe = playrow;
        if (directBeanListForClassSchedule.get(playNumbe).getVideo_status().equals("3")
                || directBeanListForClassSchedule.get(playNumbe).getVideo_status().equals("1")) {
            lv_chat.removeFooterView(loadView);
        }
        if (wherefrom == 0) {
            listview.setSelection(position >= 1 ? position - 1:0);
            tv_main_title.setText(directBeanListForClassSchedule.get(position).getTitle());
            initParam(position);
            CommonUtils.putSharedPreferenceItem(null, directBean.getRid(), String.valueOf(directBeanListForClassSchedule.get(position).getNumber()));
        } else {
            if (playNumbe >= directBeanListForClassSchedule.size() && directBeanListForClassSchedule.size() > 1) {
                listview.setSelection(0);
                directClassScheduleAdapter.playVideo(directBeanListForClassSchedule.get(0), 0);
                directClassScheduleAdapter.setDirectId(directBeanListForClassSchedule.get(0).getLubourl());
            } else {
                listview.setSelection(playNumbe >= 1 ? playNumbe - 1:0);
                directClassScheduleAdapter.playVideo(directBeanListForClassSchedule.get(playNumbe), playNumbe);
                directClassScheduleAdapter.setDirectId(directBeanListForClassSchedule.get(playNumbe).getLubourl());
            }
        }
        if (directBeanListForClassSchedule.get(position).getCamera().equals("1")) {
            iv_icon.setVisibility(View.VISIBLE);
        } else {
            iv_icon.setVisibility(View.GONE);
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
    public void flushPdfContent_OnFailure(String res) {
        mCustomLoadingDialog.dismiss();
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
        } else if (SendRequest.ERROR_SERVER.equals(res)) {
            ToastUtils.showToast(R.string.server_error);
        }
    }

    public void flushClassSchedule_OnSucess(List<DirectBean> res) {
        if ((res == null || res.size() == 0)) {
            ToastUtils.showToast(R.string.no_data);
            return;
        }

        hasMoreData = !(res == null || res.size() == 0);
        directBeanListForClassSchedule.addAll(res);
        directClassScheduleAdapter.notifyDataSetChanged();

    }

    public void flushClassSchedule_OnFailure(String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
        } else if (SendRequest.ERROR_SERVER.equals(res)) {
            ToastUtils.showToast(R.string.server_error);
        }
    }

    @Override
    public boolean back() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            isScreen = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            finish();
        }
        return false;
    }

    protected void onResume() {
        super.onResume();
        if (vodPlayer != null) {
//            vodPlayer.resume();
            if (isPlaying) {
                iv_play.setBackgroundResource(R.drawable.ic_pause);
                vodPlayer.resume();
            } else {
                iv_play.setBackgroundResource(R.drawable.ic_playv);
                vodPlayer.pause();
            }
        }
        m_wakeLock.acquire(); //设置保持唤醒
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (vodPlayer != null) {
//            vodPlayer.pause();
//        }
        m_wakeLock.release();//解除保持唤醒
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vodPlayer != null) {
            vodPlayer.stop();
            vodPlayer.release();
        }
        m_wakeLock.release();//解除保持唤醒
        vodSite = null;
    }

    //    public static final String SAMPLE_FILE = "sample.pdf";
//
//    public static final String ABOUT_FILE = "about.pdf";
//
//
//    String pdfName = SAMPLE_FILE;
//
    Integer pageNumber = 1;
//
//    void afterViews() {
//        display(pdfName, false);
//    }
//
//    public void about() {
//        if (!displaying(ABOUT_FILE))
//            display(ABOUT_FILE, true);
//    }
//
//    private void display(String assetFileName, boolean jumpToFirstPage) {
//        if (jumpToFirstPage) pageNumber = 1;
//        setTitle(pdfName = assetFileName);
//
//        pdfView.fromAsset(assetFileName)
//                .defaultPage(pageNumber)
//                .onPageChange(this)
//                .load();
//    }

//    @Override
//    protected void onCreate(Bundle arg0) {
//        super.onCreate(arg0);
//    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
//        setTitle(format("%s %s / %s", pdfName, page, pageCount));
    }

    @Override
    public void onBackPressed() {
//        if (ABOUT_FILE.equals(pdfName)) {
//            display(SAMPLE_FILE, true);
//        } else {
        super.onBackPressed();
//        }
    }

//    private boolean displaying(String fileName) {
//        return fileName.equals(pdfName);
//    }

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
                File file = DownLoadFileTask.getFile(path, filepath, pd_update);
                pd_update.dismiss();
//                pdfView.fromFile(new File(filepath))
//                        .defaultPage(pageNumber)
//                        .onPageChange(DirectPlayDetailsActivityForVodsdk.this)
//                        .load();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initPdfView(filepath);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                DebugUtil.e(e.toString());
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
                if (StringUtil.isEmpty(directBean.getCcCourses_id())) {
                    continue;
                }
                //网课
                DirectBean directBean1 = daoUtils.queryDirectBeanForCCVedioId(uid, directBeanListForClassSchedule.get(i).getCcCourses_id(), directBeanListForClassSchedule.get(i).getNumber());
                if (directBean1 != null) {
                    directBeanListForClassSchedule.get(i).setDown_status(directBean1.getDown_status());
                    directBeanListForClassSchedule.get(i).setLocalPath(directBean1.getLocalPath());
                    directBeanListForClassSchedule.get(i).setStart(directBean1.getStart());
                    directBeanListForClassSchedule.get(i).setEnd(directBean1.getEnd());
                }
            } else if (directBeanListForClassSchedule.get(i).getVideoType() == 1) {
                if (StringUtil.isEmpty(directBean.getLubourl())) {
                    continue;
                }
                //录播
                DirectBean directBean1 = daoUtils.queryDirectBeanForGeeneVedioId(uid, directBeanListForClassSchedule.get(i).getLubourl(),
                        directBeanListForClassSchedule.get(i).getNumber());
                if (directBean1 != null) {
                    directBeanListForClassSchedule.get(i).setDown_status(directBean1.getDown_status());
                    directBeanListForClassSchedule.get(i).setLocalPath(directBean1.getLocalPath());
                    directBeanListForClassSchedule.get(i).setStart(directBean1.getStart());
                    directBeanListForClassSchedule.get(i).setEnd(directBean1.getEnd());
                }
            }
        }
        //删除rid相同的课程后，同步完数据库之后再加入课时缓存
//        DebugUtil.e("synchronizationLocal:" + DirectPlayDetailsActivityForVodsdk.this.directBean.toString());
        daoUtils.deletDirectBeanListForRid(DirectPlayDetailsActivityBase.this.directBean.getRid());
        for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
            DirectBean directBean = directBeanListForClassSchedule.get(i);
            directBean.setUserid(uid);
            directBean.setRid(DirectPlayDetailsActivityBase.this.directBean.getRid());
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
        if ("200".equals(mDirectBean.getDown_status())) {
            return;
        }
        DebugUtil.e("onReceiveBroadCast:vod" + mDirectBean.toString());
        for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
            if (directBeanListForClassSchedule.get(i).getVideoType() == 0) {
                if (directBeanListForClassSchedule.get(i).getCcCourses_id().equals(mDirectBean.getCcCourses_id())) {
                    directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
                    directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
                    directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
                    directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
                }
            } else {
                if (directBeanListForClassSchedule.get(i).getLubourl().equals(mDirectBean.getLubourl())) {
                    directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
                    directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
                    directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
                    directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
                }
            }
        }
        directClassScheduleAdapter.notifyDataSetChanged();
    }

    /**
     * 上拉刷新
     */
    private void completeRefresh() {
        if (lv_chat != null && lv_chat.getFooterViewsCount() > 0) {
            lv_chat.removeFooterView(loadView);
        }
        if (directClassChatAdapter != null) {
            directClassChatAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (view.getLastVisiblePosition() == view.getCount() - 1) {
                if (ispage) {//如果为true说明有下一页聊天记录可以调取
                    lv_chat.addFooterView(loadView);
                    loadIcon.startAnimation(refreshingAnimation);
                    indexChat++;
                    vodSite.getChatHistory(vodId, indexChat);
                } else {
                    ToastUtils.showToast(R.string.no_more);
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private void initPdfView(String path) {
        core = openFile(path);
        if(core==null){
            Toast.makeText(this, "open pdf file failed", Toast.LENGTH_SHORT).show();
//            finish();
        }
        totalPageCount = core.countPages();
        if(lastPagePosition>totalPageCount){
            lastPagePosition=totalPageCount;
            isFinished=true;
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
        mDocView.setAdapter(new MuPDFPageAdapter(DirectPlayDetailsActivityBase.this, this, core));
        mDocView.setKeepScreenOn(true);
        mDocView.setLinksHighlighted(false);
        mDocView.setScrollingDirectionHorizontal(true);

        ll_content_play.addView(mDocView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
            DebugUtil.e(TAG,e.getMessage());
            return null;
        }
        return core;
    }

    @Override
    public void performPickFor(FilePicker picker) {

    }

}

