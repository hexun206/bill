package com.huatu.teacheronline.direct;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdflib.FilePicker;
import com.artifex.mupdflib.MuPDFCore;
import com.artifex.mupdflib.MuPDFPageAdapter;
import com.artifex.mupdflib.MuPDFReaderView;
import com.baijia.baijiashilian.liveplayer.ViESurfaceViewRenderer;
import com.baijiahulian.livecore.LiveSDK;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.context.OnLiveRoomListener;
import com.baijiahulian.livecore.launch.LPLaunchListener;
import com.baijiahulian.livecore.models.imodels.ILoginConflictModel;
import com.baijiahulian.livecore.models.imodels.IMediaModel;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;
import com.baijiayun.BJYPlayerSDK;
import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.playback.PBRoom;
import com.baijiayun.videoplayer.IBJYVideoPlayer;
import com.baijiayun.videoplayer.VideoPlayerFactory;
import com.baijiayun.videoplayer.player.PlayerStatus;
import com.baijiayun.videoplayer.player.error.PlayerError;
import com.baijiayun.videoplayer.ui.event.UIEventKey;
import com.baijiayun.videoplayer.util.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gensee.utils.StringUtil;
import com.google.gson.reflect.TypeToken;
import com.greendao.DaoUtils;
import com.greendao.DirectBean;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.SensorDataSdk;
import com.huatu.teacheronline.bean.DefinitionBean;
import com.huatu.teacheronline.direct.adapter.CourseWareAdapter;
import com.huatu.teacheronline.direct.adapter.CourseWareSimpleAdapter;
import com.huatu.teacheronline.direct.adapter.DefinitionAdapter;
import com.huatu.teacheronline.direct.adapter.DirectClassPdfAdapter;
import com.huatu.teacheronline.direct.adapter.SpeedAdapter;
import com.huatu.teacheronline.direct.bean.HideSoftInputEvent;
import com.huatu.teacheronline.direct.bean.NetWorkChangeEvent;
import com.huatu.teacheronline.direct.bean.OffLineDataForPlayer;
import com.huatu.teacheronline.direct.bean.PdfBean;
import com.huatu.teacheronline.direct.bean.PlayByIndexEvent;
import com.huatu.teacheronline.direct.bean.WatchRecord;
import com.huatu.teacheronline.direct.bean.WithdrawalBean;
import com.huatu.teacheronline.direct.db.OffLineDataForPlayerDAO;
import com.huatu.teacheronline.direct.db.PlayerConfigDAO;
import com.huatu.teacheronline.direct.db.WatchRecordDAO;
import com.huatu.teacheronline.direct.fragment.ChatFragment;
import com.huatu.teacheronline.direct.fragment.MessageListFragment;
import com.huatu.teacheronline.direct.fragment.PPTFragment;
import com.huatu.teacheronline.direct.manager.PdfDownloadManager;
import com.huatu.teacheronline.direct.manager.RecodeRequestFailureManager;
import com.huatu.teacheronline.direct.manager.RecordInfoManager;
import com.huatu.teacheronline.direct.receiver.NetworkChangeReceiver;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.sensorsdata.TrackUtil;
import com.huatu.teacheronline.sensorsdata.event.PlayTrialCourse;
import com.huatu.teacheronline.sensorsdata.event.WatchCourse;
import com.huatu.teacheronline.utils.ClickUtils;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DateTimeUtil;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.NetWorkUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.AppraiseAlertDialog;
import com.huatu.teacheronline.widget.ChooseSpeedPopwindows;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.WithdrawalDialog;
import com.huatu.teacheronline.widget.bjywidget.BjyBottomViewPresenter;
import com.huatu.teacheronline.widget.bjywidget.MyBJYVideoView;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.kyleduo.switchbutton.SwitchButton;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaoneng.coreapi.ChatParamsBody;
import cn.xiaoneng.uiapi.Ntalker;
import me.drakeet.materialdialog.MaterialDialog;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class PlayerActivityForBjysdk extends BaseActivity implements OnLiveRoomListener,
        OnPageChangeListener, FilePicker.FilePickerSupport {
    private String TAG = "DirectPlayDetailsActivityForRtsdk";
    private String settingid1 = "kf_10092_1513839603881";
    private RelativeLayout rl_main_left_player;
    private RadioButton tv_jianjie, tv_kebiao, tv_hudong, tv_chanjian;
    private WebView webview;
    private RelativeLayout rl_deatil_waiting;
    private ImageView iv_icon;
//    private ImageView iv_screen;

    //listview
    private View loadView;
    // 均匀旋转动画
    private ExpandableListView listview;
    private CourseWareAdapter directClassScheduleAdapter;
    private ArrayList<DirectBean> directBeanListForClassSchedule = new ArrayList<>();

    private DirectBean directBean;
    private ObtatinDataListener obtatinDataListener;
    private CustomAlertDialog mCustomLoadingDialog;
    private int tab = 2;//1 讲义 2课表 3 互动 4 常见问题
    private boolean isPPT = true;
    private String nickName;
    private PowerManager.WakeLock m_wakeLock;
    //    private List<DirectBean> data;
    public int position;//传过来课程表位置
    private String uid;
    //讲义相关
    private PDFView pdfView;
    private int pageNumber;
    private boolean isShowJy;
    private DaoUtils daoUtils;
    private int wherefrom; //0 表示从课程详情进入 需要指定播放，2表示我的课程进入
    private boolean isLocaleData = false;

    public int playNumbe;//最后播放的视频行数转化类型
    private int playrow = 0;//根据number查找到对应的行数
    private ImageView ib_main_right_player;
    private ImageView img_customer_service;

    private MuPDFCore core;
    private int totalPageCount;
    private int lastPagePosition;
    private boolean isFinished;
    private MuPDFReaderView mDocView;
    private LinearLayout ll_content_play;
    private String mFileName;

    private ListView lv_pdf;
    private DirectClassPdfAdapter directClassPdfAdapter;
    private RelativeLayout ll_no_notes;
    private ArrayList<PdfBean> directBeanListPDFForClassSchedule = new ArrayList<PdfBean>();
    private ObtatinPdfListener obtatinPdfListener;

    private RelativeLayout rl_main_left_pdf;
    private boolean isShowPdf = false;
    private String account;

    private String joinTime;//当前视频开始时间
    private String leavetime;//当前视频离开时间
    private LiveRoom mLiveRoom;
    @BindView(R.id.rl_playlayout)
    PercentRelativeLayout rlPlayLayout;

    @BindView(R.id.ll_live_container)
    LinearLayout mSurfaceContainer;
    @BindView(R.id.fra_live_container)
    FrameLayout mFra_container;

    IBJYVideoPlayer ibjyVideoPlayer;
    MyBJYVideoView mPlayerView;
    @BindView(R.id.fra_live_ppt)
    FrameLayout mFraLivePpt;
    @BindView(R.id.fra_live_chat)
    FrameLayout mFraLiveChat;
    @BindView(R.id.fra_live_touch)
    FrameLayout mFraLiveTouch;
    @BindView(R.id.rl_play_bottom_)
    RelativeLayout mRelPlayBackBottom;
    @BindView(R.id.ll_playback_top)
    LinearLayout mLlPlayBackTop;
    @BindView(R.id.fra_playback_center)
    FrameLayout mFraPlayBackCenter;
    @BindView(R.id.tv_player_speed)
    TextView tv_speed;

    /*********横屏view**********/

    @BindView(R.id.ll_bottombar_top)
    LinearLayout mLlTop;
    @BindView(R.id.ll_bottombar_bottom)
    LinearLayout mLlBottiom;

    @BindView(R.id.switch_bottombar)
    SwitchButton mSwitch;
    @BindView(R.id.ll_bottombar_withdraw)
    LinearLayout mLlWithDraw;
    @BindView(R.id.img_bottombar_withdraw)
    ImageView mImgWithDraw;
    @BindView(R.id.pb_bottombar_withdraw)
    ProgressBar mPbWithDraw;
    @BindView(R.id.tv_bottombar_withdraw)
    TextView mTvWithDraw;
    @BindView(R.id.ll_bottombar_timetable)
    LinearLayout mLlTimeTableBottomBar;
    @BindView(R.id.img_bottombar_timetable)
    ImageView mImgTimeTable;
    @BindView(R.id.tv_bottombar_timetable)
    TextView mTvTimeTable;

    @BindView(R.id.ll_live_timetable)
    LinearLayout mLlTimeTable;
    @BindView(R.id.lv_live_fullscreen)
    ExpandableListView mLvTimeTable;
    @BindView(R.id.cl_guide)
    ConstraintLayout mClGuide;
    @BindView(R.id.iv_screen)
    ImageView mImgBottomBarScreen;

    @BindView(R.id.rcv_player_definition)
    RecyclerView mRcvDefinition;
    @BindView(R.id.rcv_player_speed)
    RecyclerView mRcvSpeed;
    @BindView(R.id.tv_player_definition)
    TextView mTvDefinition;
    @BindView(R.id.img_live_center_dialog)
    ImageView mImgLiveCenterDialog;
    @BindView(R.id.ll_live_center_dialog)
    LinearLayout mLlLiveCenterDialog;
    @BindView(R.id.tv_live_center_dialog)
    TextView mTvLiveCenterDialog;

    /****直播时的bottombar****/
    @BindView(R.id.rl_live_bottom)
    RelativeLayout mRelLiveBottom;

    @BindView(R.id.switch_live_bottombar_continuous)
    SwitchButton mSwitchLiveContinuous;
    @BindView(R.id.tv_live_bottombar_continuous)
    TextView mTvLiveContinuous;
    @BindView(R.id.ll_live_bottombar_withdraw)
    LinearLayout mLlLiveWithDraw;
    @BindView(R.id.pb_live_bottombar_withdraw)
    ProgressBar mPbLiveWithDraw;
    @BindView(R.id.img_live_bottombar_withdraw)
    ImageView mImgLiveWithDraw;
    @BindView(R.id.tv_live_bottombar_withdraw)
    TextView mTvLiveWithDraw;
    @BindView(R.id.ll_live_bottombar_timetable)
    LinearLayout mLlLiveTimeTableBottomBar;
    @BindView(R.id.img_live_bottombar_timetable)
    ImageView mImgLiveTimeTable;
    @BindView(R.id.iv_live_screen)
    ImageView mImgLiveScreen;
    @BindView(R.id.tv_live_bottombar_timetable)
    TextView mTvLiveTimeTable;
    @BindView(R.id.ll_live_bottombar_function)
    LinearLayout mLlLiveFunction;


    PBRoom mRoom = null;
    private boolean mIsPlayback = true;
    private BjyBottomViewPresenter mBjyBottomViewPresenter;
    private ChooseSpeedPopwindows chooseSpeedPopwindows;
    private int speedType;
    private String mPresenteUserId;
    private boolean mOffLinePlayBack;
    private SurfaceView mSurface;
    private MaterialDialog mNetWorkDialog;
    private NetworkChangeReceiver mNetworkChangeReceiver;
    private boolean isFirstRegister = true;
    private PPTFragment mPptFragment;
    private boolean mInBack;


    private DirectBean recodingInfo;
    /**
     * 点播
     */
    private boolean mIsVideo;
    private DirectBean mPlayingDirectBean;
    private CourseWareSimpleAdapter mTimeTableAdapter;
    private boolean mIsFirstWatch = true;
    private DefinitionAdapter mDefinitionAdapter;
    private SpeedAdapter mSpeedAdapter;
    private AudioManager mAudioManager;
    private int minVolume = 0;
    private int mStreamVolume;
    private float brightness;
    private GestureDetector mGestureDetector;

    private int isContinuous = -1;


    private volatile boolean luanchSuccess;
    /**
     * 神策使用
     */
    private String mStartWatchCourse;

    //    private String mProvince;
//    private String mCategorie;
//    private String mPeriod;
//    private String mSubject;
    private List<VideoDefinition> definitionList = new ArrayList<>(Arrays.asList(VideoDefinition._720P,
            VideoDefinition.SHD, VideoDefinition.HD, VideoDefinition.SD, VideoDefinition._1080P));

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if (arg0 != null) {

            String TAG = "android:support:fragments";
            arg0.remove(TAG);


        }
        //禁止录屏和截屏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        mNetworkChangeReceiver = new NetworkChangeReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");

        registerReceiver(mNetworkChangeReceiver, intentFilter);

    }


    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void initView() {
        setContentView(R.layout.activity_live);
        ButterKnife.bind(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownManageActivity.ACTION_REFRASH);
        setIntentFilter(intentFilter);
        uid = CommonUtils.getSharedPreferenceItem(null, com.huatu.teacheronline.utils.UserInfo.KEY_SP_USERID, "");
        account = CommonUtils.getSharedPreferenceItem(null, com.huatu.teacheronline.utils.UserInfo.KEY_SP_ACCOUNT, "");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        m_wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "cn");
        m_wakeLock.acquire(); //设置保持唤醒
        daoUtils = DaoUtils.getInstance();
//        nickName = CommonUtils.getSharedPreferenceItem(null, com.huatu.teacheronline.utils.UserInfo.KEY_SP_NICKNAME, "");
//        if (StringUtil.isEmpty(nickName)) {
        //直播使用用户名不用昵称
        nickName = CommonUtils.getSharedPreferenceItem(null, com.huatu.teacheronline.utils.UserInfo.KEY_SP_ACCOUNT, "1");
//        }
        directBean = (DirectBean) getIntent().getSerializableExtra("DirectBean");

//        mCategorie = StringUtils.isEmpty(getIntent().getStringExtra(DirectDetailsActivity.KEY_CATEGORIE)) ? null : getIntent().getStringExtra(DirectDetailsActivity.KEY_CATEGORIE);
//        mPeriod = StringUtils.isEmpty(getIntent().getStringExtra(DirectDetailsActivity.KEY_PERIOD)) ? null : getIntent().getStringExtra(DirectDetailsActivity.KEY_PERIOD);
//        mSubject = StringUtils.isEmpty(getIntent().getStringExtra(DirectDetailsActivity.KEY_SUBJECT)) ? null : getIntent().getStringExtra(DirectDetailsActivity.KEY_SUBJECT);
//        mProvince = StringUtils.isEmpty(getIntent().getStringExtra(DirectDetailsActivity.KEY_PROVINCE)) ? null : getIntent().getStringExtra(DirectDetailsActivity.KEY_PROVINCE);

        directBeanListForClassSchedule = DataStore_Direct.directDatailList;
        position = getIntent().getIntExtra("position", 0);
        wherefrom = getIntent().getIntExtra("wherefrom", 0);
        int key = getIntent().getIntExtra("key", 0);//判断是否从24小时直播进入 0不是 1是
//        if (key==1){
//            settingid1="kf_10092_1515490706283";
//        }else{
//            settingid1="kf_10092_1513839603881";
//        }
        mCustomLoadingDialog = new CustomAlertDialog(PlayerActivityForBjysdk.this, R.layout.dialog_loading_custom);

        rl_main_left_player = (RelativeLayout) findViewById(R.id.rl_main_left_player);
        tv_jianjie = (RadioButton) findViewById(R.id.tv_jianjie);
        tv_kebiao = (RadioButton) findViewById(R.id.tv_kebiao);
        tv_chanjian = (RadioButton) findViewById(R.id.tv_chanjian);
        tv_hudong = (RadioButton) findViewById(R.id.tv_hudong);
        webview = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);


        tv_speed.setOnClickListener(this);
        rl_deatil_waiting = (RelativeLayout) findViewById(R.id.rl_deatil_waiting);
        ll_content_play = (LinearLayout) findViewById(R.id.ll_content_play);
        rl_main_left_pdf = (RelativeLayout) findViewById(R.id.rl_main_left_pdf);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
//        iv_screen = (ImageView) findViewById(R.id.iv_directScreen);
        img_customer_service = (ImageView) findViewById(R.id.img_customer_service);
        img_customer_service.setVisibility(View.GONE);
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        listview = (ExpandableListView) findViewById(R.id.listview);
        lv_pdf = (ListView) findViewById(R.id.pdf_listview);//pdf列表
        ll_no_notes = (RelativeLayout) findViewById(R.id.ll_no_notes);//暂无pdf
        ll_no_notes = (RelativeLayout) findViewById(R.id.ll_no_notes);//暂无pdf

        mPlayerView = new MyBJYVideoView(this);
        ibjyVideoPlayer = new VideoPlayerFactory.Builder()
                //后台暂停播放
                .setSupportBackgroundAudio(false)
                //开启循环播放
                .setSupportLooping(true)
                //开启记忆播放
                .setSupportBreakPointPlay(true, this)
                //绑定activity生命周期
                .setLifecycle(getLifecycle()).build();
        mPlayerView.initPlayer(ibjyVideoPlayer);
        mPlayerView.setComponentEventListener((eventCode, bundle) -> {
            switch (eventCode) {
//                case UIEventKey.CUSTOM_CODE_REQUEST_BACK://播放器返回按钮 已经自定义隐藏
//                    if (isLandscape) {
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    } else {
//                        finish();
//                    }
//                    break;
                case UIEventKey.CUSTOM_CODE_REQUEST_TOGGLE_SCREEN://播放器全屏按钮
                    setRequestedOrientation(isLandscape ?
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                default:
                    break;
            }
        });
        //初始设置显示布局宽高
        requestLayout(false);
        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((SwitchButton) v).isChecked();
                isContinuous = isChecked ? 0 : 1;
            }
        });


        mSwitchLiveContinuous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((SwitchButton) v).isChecked();
                isContinuous = isChecked ? 0 : 1;
            }
        });


        initAdapter();


        ib_main_right_player = (ImageView) findViewById(R.id.ib_main_right_player);

        if ("-1".equals(directBean.getRid())) {
            ib_main_right_player.setVisibility(View.INVISIBLE);
        } else if (directBean.getIs_buy() != null && Integer.parseInt(directBean.getIs_buy()) == 1) {
            ib_main_right_player.setVisibility(View.VISIBLE);
            ib_main_right_player.setOnClickListener(this);
        } else {
            ib_main_right_player.setVisibility(View.INVISIBLE);
        }
        pdfView = (PDFView) findViewById(R.id.pdfView);

//        if (directBeanListForClassSchedule == null || directBeanListForClassSchedule.size() == 0) {
        //判断有无网络是否加载本地
        if (CommonUtils.isNetWorkAvilable()) {
            loadDirectPlayInfo();
        } else {

            OffLineDataForPlayerDAO.getInstance().queryByRid(directBean.getRid())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<OffLineDataForPlayer>() {
                        @Override
                        public void call(OffLineDataForPlayer offLineDataForPlayer) {
                            if (offLineDataForPlayer != null) {

                                ArrayList<DirectBean> directBeanList = GsonUtils.parseJSONArray(offLineDataForPlayer.getJson(), new TypeToken<List<DirectBean>>() {
                                }.getType());

                                if (directBeanList.size() == 0) {
                                    ToastUtils.showToast(R.string.network);
                                } else {
                                    flushContent_OnSucess(directBeanList);
                                }


                            }
                        }
                    });


//                List<DirectBean> directBeans = daoUtils.queryDirectBeanForCacheClassForRid(directBean.getRid());
//                //数据库没有需要提示
//                if (directBeans.size() == 0) {
//                    ToastUtils.showToast(R.string.network);
//                    return;
//                }
//                if (directBeanListForClassSchedule == null) {
//                    directBeanListForClassSchedule = (ArrayList<DirectBean>) directBeans;
//                    DataStore_Direct.directDatailList = directBeanListForClassSchedule;
//                } else {
//                    directBeanListForClassSchedule.clear();
//                    directBeanListForClassSchedule.addAll(directBeans);
//                }
//                isLocaleData = true;
//                iniFristData(isLocaleData);
        }
//        } else {
//            isLocaleData = false;
//            iniFristData(isLocaleData);
//        }

        img_customer_service.setVisibility(View.VISIBLE);
        tab = 2;
        pdfView.setVisibility(View.GONE);
        listview.setVisibility(View.VISIBLE);

        chooseSpeedPopwindows = new ChooseSpeedPopwindows(this);
        getLivePDFForClassSchedule();
        //修复某些机器上surfaceView导致的闪黑屏的bug
        getWindow().setFormat(PixelFormat.TRANSLUCENT);


        //神策track click
        TrackUtil.trackClick(this, tv_hudong, TrackUtil.TYPE_CHANGE_TAB, "互动");
        TrackUtil.trackClick(this, tv_kebiao, TrackUtil.TYPE_CHANGE_TAB, "课表");
        TrackUtil.trackClick(this, tv_jianjie, TrackUtil.TYPE_CHANGE_TAB, "讲义");
        TrackUtil.trackClick(this, tv_chanjian, TrackUtil.TYPE_CHANGE_TAB, "常见问题");


    }

    private void initAdapter() {
        directClassScheduleAdapter = new CourseWareAdapter(this);

        directClassScheduleAdapter.seDirectBean(directBean);
        listview.setAdapter(directClassScheduleAdapter);
        directClassScheduleAdapter.bind(listview);
        listview.setVisibility(View.VISIBLE);

        mClGuide.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        mTimeTableAdapter = new CourseWareSimpleAdapter(this);
        mTimeTableAdapter.seDirectBean(directBean);
        mLvTimeTable.setAdapter(mTimeTableAdapter);
        mTimeTableAdapter.bind(mLvTimeTable);
        directClassPdfAdapter = new DirectClassPdfAdapter(this, directBeanListPDFForClassSchedule);
        lv_pdf.setAdapter(directClassPdfAdapter);

        mRcvDefinition.setHasFixedSize(true);
        mRcvDefinition.setLayoutManager(new LinearLayoutManager(this));
        mDefinitionAdapter = new DefinitionAdapter();
        mDefinitionAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                DefinitionBean definitionBean = mDefinitionAdapter.getData().get(position);

                if (mPlayerView != null) {
//                    mPlayerView.changeDefinition(Utils.getVideoDefinitionFromString(definitionBean.getType()));
                    mPlayerView.changeDefinition(definitionList.get(2));
                    mDefinitionAdapter.select(definitionBean);

                } else {
                    directClassScheduleAdapter.showCustomToast("视频暂未初始化完成!");
                }

                mRcvDefinition.setVisibility(View.GONE);

            }
        });
        mRcvDefinition.setAdapter(mDefinitionAdapter);

        mRcvSpeed.setHasFixedSize(true);
        mRcvSpeed.setLayoutManager(new LinearLayoutManager(this));
        mSpeedAdapter = new SpeedAdapter();
        mSpeedAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mPlayerView != null) {
                    speedType = position;
                    switch (position) {
                        case 1:
                            mPlayerView.setPlayRate(1.25f);
                            tv_speed.setText(getString(R.string.speed_quick1));
                            break;
                        case 2:
                            mPlayerView.setPlayRate(1.5f);
                            tv_speed.setText(getString(R.string.speed_quick2));
                            break;
                        case 3:
                            mPlayerView.setPlayRate(2.0f);
                            tv_speed.setText(getString(R.string.speed_quick3));
                            break;
                        default:
                            mPlayerView.setPlayRate(1.0f);
                            tv_speed.setText(getString(R.string.speed_original));
                            break;

                    }

                    mSpeedAdapter.select(position);

                } else {
                    directClassScheduleAdapter.showCustomToast("视频暂未初始化完成!");
                }

                mRcvSpeed.setVisibility(View.GONE);
            }
        });
        mRcvSpeed.setAdapter(mSpeedAdapter);


    }

    public void setZOrderMediaOverlay(View view, boolean overlay) {
        if (view instanceof SurfaceView) {
            ((SurfaceView) view).setZOrderMediaOverlay(overlay);
        } else if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setZOrderMediaOverlay(((ViewGroup) view).getChildAt(i), overlay);
            }
        }


    }


    public void initLiveRoom(final DirectBean zDirectBean, final int position) {
        Logger.d("liveRoom directBean:" + GsonUtils.toJson(zDirectBean));

        //试听课程播放事件track
        if (zDirectBean.getIsTrial() == 1) {
            trackPlayTrialCourse(zDirectBean);

        }


        if (mLlTimeTable != null && mLlTimeTable.getVisibility() == View.VISIBLE) {
            mLlTimeTable.setVisibility(View.GONE);

        }

        directClassScheduleAdapter.setDirectId(position + "");
        directClassScheduleAdapter.setSelection(position);

        mTimeTableAdapter.setDirectId(position + "");
        mTimeTableAdapter.setSelection(position);

        quitLiveRoom();
        if (("-1").equals(directBean.getRid()) || mPlayingDirectBean == null || !mPlayingDirectBean.getLessonid().equals(zDirectBean.getLessonid())) {
            joinTime = "";
        }

        this.position = position;
        mPlayingDirectBean = zDirectBean;

        if (!directBean.getRid().equals("-1")) {
            addWatchRecord(zDirectBean);

        }


        mSpeedAdapter.select(0);


        RecordInfoManager.getInstance().setJoinTime(joinTime);


        initBottomExtendView(zDirectBean);


////        LivePlaybackSDK.deployType = LPConstants.LPDeployType.Product;
////        rl_deatil_waiting.setVisibility(View.VISIBLE);
//
        showAppraiseDialog(position);
        mIsVideo = zDirectBean.getVideoType() == 0;
//
        tv_speed.setVisibility(View.GONE);


        if (mIsVideo) {
            //点播

//            mPlayerView = new BJYPlayerView(this);
//            mPlayerView.removeView(mPlayerView.getTopView());
//            mPlayerView.removeView(mPlayerView.getBottomView());
//            mPlayerView.removeView(mPlayerView.getCenterView());

            mPlayerView.setUserInfo(nickName, uid);

            mFra_container.addView(mPlayerView, 0);
            iv_icon.setVisibility(View.GONE);

            //开启记忆播放
//            mPlayerView.enableBreakPointMemory(this);


            mFraLivePpt.setVisibility(View.GONE);
            mSurfaceContainer.setVisibility(View.GONE);
            mRelPlayBackBottom.setVisibility(View.GONE);
//            iv_screen.setVisibility(View.GONE);
            mRelLiveBottom.setVisibility(View.GONE);


//            mBjyBottomViewPresenter = new BjyBottomViewPresenter(mRelPlayBackBottom, this);
//            mPlayerView.setBottomPresenter(mBjyBottomViewPresenter);
//            mPlayerView.setCenterPresenter(new CenterViewPresenter(mFraPlayBackCenter));
//            mPlayerView.initPartner(CustomApplication.BJPlayerView_partnerId, BJYVideoView.PLAYER_DEPLOY_ONLINE);


            boolean isLocalPlay = false;
            if (zDirectBean.getDown_status() != null && zDirectBean.getDown_status().equals(DownManageActivity.CCDOWNSTATE_COMPLETE + "") && !StringUtils.isEmpty(zDirectBean.getLocalPath())) {
                File file = new File(zDirectBean.getLocalPath());
                if (file.exists()) {
                    isLocalPlay = true;

                }

            }

            if (mSwitch.isChecked() && !CommonUtils.isNetWorkAvilable() && !isLocalPlay) {
                playNextVideo(position);
                return;
            }


//            mPlayerView.setOnPlayerViewListener(mPlayerListener);
            mPlayerView.setPlayRate(10);
            if (isLocalPlay) {
                mPlayerView.setupLocalVideoWithFilePath(zDirectBean.getLocalPath());
            } else {
                if (!StringUtil.isEmpty(zDirectBean.getBjyvideoid()) && !StringUtil.isEmpty(zDirectBean.getBjytoken())) {
                    mPlayerView.setupOnlineVideoWithId(Long.parseLong(zDirectBean.getBjyvideoid()), zDirectBean.getBjytoken());
                } else {

                    ToastUtils.showToast("播放参数错误！");

                }


            }
            mPlayerView.play(0);


        } else {
            //直播或回放

            mIsPlayback = zDirectBean.getVideo_status().equals("2") || zDirectBean.getVideo_status().equals("3");

//        joinTime = StringUtils.getNowTime();
            //<<<<<<<<<<<<<<<<回放部分开始>>>>>>>>>>>>>>
            if (mIsPlayback) {
                //playback
                if (StringUtils.isEmpty(zDirectBean.getRoom_id())) {
                    ToastUtils.showToast("找不到房间号!");
                    return;
                }

//                mPlayerView = new BJYPlayerView(this);
//              mPlayerView.removeView(mPlayerView.getTopView());
//              mPlayerView.removeView(mPlayerView.getBottomView());
//              mPlayerView.removeView(mPlayerView.getCenterView());

                mPlayerView.setUserInfo(nickName, uid);

                mFra_container.addView(mPlayerView, 0);

                //开启记忆播放
//                mPlayerView.enableBreakPointMemory(this);

                mFraLivePpt.setVisibility(View.VISIBLE);
                mSurfaceContainer.setVisibility(View.GONE);
//            mPlayerView.setVisibility(View.GONE);
                mRelPlayBackBottom.setVisibility(View.GONE);
//                iv_screen.setVisibility(View.GONE);
                mRelLiveBottom.setVisibility(View.GONE);
//            rl_fullScreenBack.setVisibility(View.GONE);


                mBjyBottomViewPresenter = new BjyBottomViewPresenter(mRelPlayBackBottom, this);
//                mPlayerView.setBottomPresenter(mBjyBottomViewPresenter);
//                mPlayerView.setBottomPresenter(new BJBottomViewPresenter(mPlayerView.getBottomView()));
//                mPlayerView.setCenterPresenter(new CenterViewPresenter(mFraPlayBackCenter));


                //回放离线播放
//                DirectBean directQuery = daoUtils.queryDirectBeanForPlayBack(uid, zDirectBean.getRoom_id(), zDirectBean.getSession_id(), zDirectBean.getNumber());
//                if (directQuery != null) {
//                    zDirectBean.setDown_status(directQuery.getDown_status());
//                    zDirectBean.setLocalPath(directQuery.getLocalPath());
//                    zDirectBean.setStart(directQuery.getStart());
//                    zDirectBean.setEnd(directQuery.getEnd());
//                }

                String localpath = zDirectBean.getLocalPath();

                mOffLinePlayBack = false;

                if (!StringUtil.isEmpty(zDirectBean.getDown_status()) && Integer.parseInt(zDirectBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_COMPLETE
                        && !StringUtil.isEmpty(localpath)) {
                    String[] split = localpath.split(";");
                    if (split.length == 2) {
                        File videoFile = new File(split[0]);
                        File signalFile = new File(split[1]);
                        if (videoFile.exists() && signalFile.exists()) {

//                            mRoom = BJYPlayerSDK.newPlayBackRoom(this, Long.parseLong(zDirectBean.getRoom_id()),
//                                    videoFile.getAbsolutePath(),
//                                    signalFile.getAbsolutePath());
                            mOffLinePlayBack = true;
                        }


                    }


                }

                Logger.d("liveRoom playback path:" + localpath + "   " + mOffLinePlayBack);
                if (!StringUtil.isEmpty(zDirectBean.getDown_status()) && Integer.parseInt(zDirectBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_COMPLETE
                        && !StringUtil.isEmpty(localpath) && new File(localpath).exists()) {
                    File videoFile = new File(localpath);
                    String sigleName = "s_" + videoFile.getName().substring(0, videoFile.getName().length() - 4);
                    String singleFile = FileUtils.getBjyVideoDiskCacheDir() + sigleName;
                    mRoom = BJYPlayerSDK.newPlayBackRoom(this, directBean.getLocalPath(), singleFile);//, Long.parseLong(zDirectBean.getRoom_id())
                    mOffLinePlayBack = true;

                } else {
                    if (!mOffLinePlayBack) {
                        if (StringUtils.isEmpty(zDirectBean.getSession_id())) {
                            mRoom = BJYPlayerSDK.newPlayBackRoom(this, Long.parseLong(zDirectBean.getRoom_id()), zDirectBean.getBjyhftoken());

                        } else {
                            mRoom = BJYPlayerSDK.newPlayBackRoom(this, Long.parseLong(zDirectBean.getRoom_id()), Long.parseLong(zDirectBean.getSession_id()), zDirectBean.getBjyhftoken());
                        }
                    }

                }

                mRoom.enterRoom(
                        new com.baijiayun.playback.mocklive.LPLaunchListener() {
                            @Override
                            public void onLaunchSteps(int step, int totalStep) {
//                    Logger.e("liveRoom launchStep:" + (step * 100 / totalStep) + "%");
                            }

                            @Override
                            public void onLaunchError(com.baijiayun.playback.context.LPError lpError) {
                                ToastUtils.showToast(R.string.notice_bjy_enter_error + lpError.getMessage());

                            }

                            @Override
                            public void onLaunchSuccess(PBRoom liveRoom) {
                                Logger.e("playback onLaunchSuccess");


                                addRecordTime();
                                recodingInfo = zDirectBean;
                                joinTime = StringUtils.getNowTime();

                                RecordInfoManager.getInstance().init(uid, account, joinTime, recodingInfo);

                                luanchSuccess = true;
                                trackWatchCourse(true);
//                        rl_deatil_waiting.setVisibility(View.GONE);
                                if (!mInBack) {
                                    mPlayerView.play();
                                    Logger.d("liveActivity playVideo mInBack = " + mInBack);
                                }


                                setZOrderMediaOverlay(mPlayerView, false);
//                                setZOrderMediaOverlay(mPptFragment.getView(), true);


                            }
                        });

                mRoom.bindPlayer(ibjyVideoPlayer);
//                mRoom.setOnPlayerListener(mPlayerListener);
//                mPptFragment = PPTFragment.newInstance(mRoom);//2.0在线回放无画板


                if (!isFinishing() && !isDestroyed()&&mPptFragment!=null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fra_live_ppt, mPptFragment, PPTFragment.class.getName())
                            .commitAllowingStateLoss();
                }


                MessageListFragment messageFragment = MessageListFragment.newInstance(mRoom);

                if (!isFinishing() && !isDestroyed()) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fra_live_chat, messageFragment, MessageListFragment.class.getName())
                            .commitAllowingStateLoss();
                }


//            mAm = (AudioManager) getSystemService(AUDIO_SERVICE);

                //<<<<<<<<<<<<<<<<回放部分结束>>>>>>>>>>>>>>

            } else {
                //<<<<<<<<<<<<<<<<直播部分开始>>>>>>>>>>>>>>
                if (StringUtils.isEmpty(zDirectBean.getStudent_code())) {
                    ToastUtils.showToast("找不到邀请码!");
                    return;
                }

                tv_speed.setVisibility(View.GONE);
//            rl_fullScreenBack.setVisibility(View.VISIBLE);
//                iv_screen.setVisibility(View.VISIBLE);
                mRelLiveBottom.setVisibility(View.VISIBLE);
                mFraLivePpt.setVisibility(View.VISIBLE);
//                    mSurfaceSpeaker.setVisibility(View.GONE);
//            mPlayerView.setVisibility(View.GONE);
                mSurfaceContainer.setVisibility(View.GONE);
                mRelPlayBackBottom.setVisibility(View.GONE);

                LiveSDK.enterRoom(this, zDirectBean.getStudent_code(), nickName, new LPLaunchListener() {
                    @Override
                    public void onLaunchSteps(int i, int i1) {

                    }

                    @Override
                    public void onLaunchError(LPError lpError) {
                        ToastUtils.showToast(R.string.notice_bjy_enter_error + lpError.getMessage());
                    }

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onLaunchSuccess(LiveRoom liveRoom) {
                        minVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                        Logger.e("live onLaunchSuccess");
                        Logger.e("minVolume:" + minVolume);
                        mLiveRoom = liveRoom;


                        mLiveRoom.getObservableOfClassEnd().observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Void>() {
                                    @Override
                                    public void call(Void aVoid) {
                                        //下课回调  暂定为直播结束
                                        playNextVideo(position);


                                    }
                                });

                        mLiveRoom.getObservableOfLoginConflict().observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<ILoginConflictModel>() {
                                    @Override
                                    public void call(ILoginConflictModel iLoginConflictModel) {
                                        //用户被踢出
                                        ToastUtils.showToast(R.string.notice_bjy_kik_out);

                                    }
                                });

                        mLiveRoom.setOnLiveRoomListener(PlayerActivityForBjysdk.this);
//                        rl_deatil_waiting.setVisibility(View.GONE);

                        addRecordTime();
                        recodingInfo = zDirectBean;
                        joinTime = StringUtils.getNowTime();

                        RecordInfoManager.getInstance().init(uid, account, joinTime, recodingInfo);
                        luanchSuccess = true;
                        trackWatchCourse(true);


                        mPptFragment = PPTFragment.newInstance(mLiveRoom);

//                    boolean sdkValid = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
//                    boolean canUse = true && sdkValid;
//                    mPptFragment.setAnimPPTEnable(canUse);

                        setZOrderMediaOverlay(mPptFragment.getView(), false);
                        if (!isFinishing() && !isDestroyed()) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.fra_live_ppt, mPptFragment, PPTFragment.class.getName())
                                    .commitAllowingStateLoss();
                        }

                        mSurface = ViESurfaceViewRenderer.CreateRenderer(PlayerActivityForBjysdk.this, true);

                        //TODO 直播时的亮度和音量调节
//                        LiveSurfaceGestureListener liveSurfaceGestureListener = new LiveSurfaceGestureListener();
//
//                        mGestureDetector = new GestureDetector(PlayerActivityForBjysdk.this, liveSurfaceGestureListener);
//
//                        mSurface.setOnTouchListener(new View.OnTouchListener() {
//                            @Override
//                            public boolean onTouch(View v, MotionEvent event) {
//                                if (mLlLiveCenterDialog.getVisibility() == View.VISIBLE) {
//                                    return false;
//                                } else if (mGestureDetector.onTouchEvent(event)) {
//                                    return true;
//                                } else {
//                                    return false;
//                                }
//
//
//                            }
//                        });


                        mSurface.setZOrderMediaOverlay(true);
//                        mSurface.setZOrderOnTop(true);
                        mSurfaceContainer.removeAllViews();
                        mSurfaceContainer.addView(mSurface);


                        mLiveRoom.getSpeakQueueVM().requestActiveUsers();

                        mLiveRoom.getSpeakQueueVM().getObservableOfActiveUsers()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new LPErrorPrintSubscriber<List<IMediaModel>>() {
                                    @Override
                                    public void call(List<IMediaModel> iMediaModels) {
                                        Logger.d("liveRoom presenterUser:" + GsonUtils.toJson(mLiveRoom.getPresenterUser()));
                                        mPresenteUserId = mLiveRoom.getPresenterUser().getUserId();
                                        if (!StringUtils.isEmpty(mPresenteUserId) && !mInBack) {
                                            mLiveRoom.getPlayer().playVideo(mPresenteUserId, mSurface);
                                        }

                                    }
                                });

                        mLiveRoom.getSpeakQueueVM().getObservableOfPresenterChange()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new LPErrorPrintSubscriber<String>() {
                                    @Override
                                    public void call(String newPresenter) {
                                        mLiveRoom.getPlayer().playAVClose(mPresenteUserId);
                                        mPresenteUserId = newPresenter;
                                        mLiveRoom.getPlayer().playVideo(mPresenteUserId, mSurface);
                                    }
                                });


                        mLiveRoom.getSpeakQueueVM().getObservableOfMediaNew()
                                .mergeWith(mLiveRoom.getSpeakQueueVM().getObservableOfMediaChange())
                                .mergeWith(mLiveRoom.getSpeakQueueVM().getObservableOfMediaClose())
                                .filter(new Func1<IMediaModel, Boolean>() {
                                    @Override
                                    public Boolean call(IMediaModel iMediaModel) {
                                        return !isTeacherOrAssistant() && iMediaModel.getUser().getType() == LPConstants.LPUserType.Teacher;
                                    }
                                })
                                .throttleLast(500, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new LPErrorPrintSubscriber<IMediaModel>() {
                                    @Override
                                    public void call(IMediaModel iMediaModel) {
                                        if (!mLiveRoom.isClassStarted()) {
                                            return;
                                        }

                                        if (iMediaModel.isAudioOn()) {
                                            Logger.d("liveRoom audio on");
                                            if (!StringUtils.isEmpty(mPresenteUserId) && !mInBack) {
                                                mLiveRoom.getPlayer().playVideo(mPresenteUserId, mSurface);
                                            }
                                        } else {
                                            Logger.d("liveRoom audio off");

                                        }
                                        if (iMediaModel.isVideoOn()) {
                                            Logger.d("liveRoom video on");
                                            if (!StringUtils.isEmpty(mPresenteUserId) && !mInBack) {
                                                mLiveRoom.getPlayer().playVideo(mPresenteUserId, mSurface);
                                            }
                                        } else {
                                            Logger.d("liveRoom video off");

                                        }


                                    }
                                });


                        ChatFragment chatFragment = ChatFragment.newInstance(mLiveRoom);
                        if (!isFinishing() && !isDestroyed()) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.fra_live_chat, chatFragment, ChatFragment.class.getName())
                                    .commitAllowingStateLoss();
                        }


                    }
                });
                //<<<<<<<<<<<<<<<<直播部分结束>>>>>>>>>>>>>>
            }
        }


        //初始化底部


    }


    /**
     * 试听课程播放事件
     *
     * @param zDirectBean
     */
    private void trackPlayTrialCourse(DirectBean zDirectBean) {

        try {

            PlayTrialCourse.Builder builder = new PlayTrialCourse.Builder()
                    .course_title(zDirectBean.getClassTitle())
                    .course_id(zDirectBean.getRid())


                    .course_teacher(directBean.getTeacherDesc())
                    .course_price(directBean.getActualPrice())
                    .course_preferential_method(directBean.getdisproperty())

                    .course_title(directBean.getTitle())
                    .course_id(directBean.getRid())

                    .course_number(directBean.getClassNo())
                    .course_examination_method(directBean.getCourse_examination_method())
                    .course_examination(directBean.getCourse_examination())
                    .course_study_section(directBean.getCourse_study_section())
                    .course_subject(directBean.getCourse_subject())
                    .course_class_type(directBean.getCourse_class_type())
                    .course_province(directBean.getCourse_province())

                    .course_cashback_hour(Double.valueOf(directBean.getCourse_cashback_hour()).intValue())

                    .course_teacher(directBean.getTeacherDesc());


            if ("2".equals(directBean.getState())) {//活动进行中
                builder.course_real_price(Double.valueOf(directBean.getDisPrice()));
            } else {
                builder.course_real_price(directBean.getActualPrice());
            }
            if ("4".equals(directBean.getdisproperty())) {
                builder.course_preferential_price(Double.valueOf(directBean.getDisPrice()))
                        .course_discount_rate(Double.valueOf(directBean.getDiscount()));
            }


            if (directBean.getVideoType() == 1) {
                builder.course_class_hour(Integer.valueOf(directBean.getLessionCount()));
            } else {
                builder.course_courseware_quantity(Integer.valueOf(directBean.getLessionCount()));
            }


            TrackUtil.trackPlayTrialCourse(builder.build());
        } catch (Exception e) {
            Logger.e(SensorDataSdk.TAG + e.toString());
        }
    }


    private boolean isTeacherOrAssistant() {
        return mLiveRoom.getCurrentUser().getType() == LPConstants.LPUserType.Teacher ||
                mLiveRoom.getCurrentUser().getType() == LPConstants.LPUserType.Assistant;
    }


    private void quitLiveRoom() {

        isPPT = true;
        changeDisplayContent();
        addRecordTime();

        trackWatchCourse(false);
        luanchSuccess = false;


        speedType = 0;
        if (chooseSpeedPopwindows != null) {
            chooseSpeedPopwindows.dissmiss();
        }

        tv_speed.setText("倍数");
        if (mPlayerView != null) {
            mPlayerView.setPlayRate(10);
        }


        if (mPlayerView != null) {
//            if (mPlayerView.isPlaying()) {
//            mPlayerView.pause();
//            mPlayerView.onDestroy();
//            }

            mFra_container.removeView(mPlayerView);
        }


        if (mLiveRoom != null) {
            if (!StringUtils.isEmpty(mPresenteUserId)) {
                mLiveRoom.getPlayer().playAVClose(mPresenteUserId);
            }

            mLiveRoom.quitRoom();
            mLiveRoom = null;

        }

        if (mRoom != null) {
            mRoom.quitRoom();
            mRoom = null;
        }
        Fragment pptFragment = getSupportFragmentManager().findFragmentByTag(PPTFragment.class.getName());
        Fragment chatFragment = getSupportFragmentManager().findFragmentByTag(ChatFragment.class.getName());
        Fragment messageFragment = getSupportFragmentManager().findFragmentByTag(MessageListFragment.class.getName());
        if (pptFragment != null && pptFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(pptFragment).commitAllowingStateLoss();

        }
        if (chatFragment != null && chatFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(chatFragment).commitAllowingStateLoss();

        }
        if (messageFragment != null && messageFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(messageFragment).commitAllowingStateLoss();

        }
    }

    @Override
    public void setListener() {
        mFraLiveTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                long time = new Date().getTime();
                if (mLlTimeTable.getVisibility() == View.VISIBLE) {
                    touchTime = time;
                    mLlTimeTable.setVisibility(View.GONE);
                    return true;
                }
                if (mRcvDefinition.getVisibility() == View.VISIBLE) {
                    touchTime = time;
                    mRcvDefinition.setVisibility(View.GONE);
                    return true;
                }
                if (mRcvSpeed.getVisibility() == View.VISIBLE) {
                    touchTime = time;
                    mRcvSpeed.setVisibility(View.GONE);
                    return true;
                }


                if ((time - touchTime) > 500) {
                    touchTime = time;
                    immersive = !immersive;
//            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mLlPlayBackTop.setVisibility(immersive ? View.GONE : View.VISIBLE);
                    if (!mIsVideo) {
                        iv_icon.setVisibility(immersive ? View.GONE : View.VISIBLE);
                    }
                    if (mIsPlayback || mIsVideo) {
                        mRelPlayBackBottom.setVisibility(immersive ? View.GONE : View.VISIBLE);
                        if (!isPPT && mPlayerView.getDuration() == 0) {//IBJYVideoPlayer.VIDEO_ORIENTATION_LANDSCAPE
                            findViewById(R.id.bjplayer_center_video_functions_ll).setVisibility(immersive ? View.GONE : View.VISIBLE);
                        } else {
                            findViewById(R.id.bjplayer_center_video_functions_ll).setVisibility(View.GONE);
                        }


                    } else {
//                        iv_screen.setVisibility(immersive ? View.GONE : View.VISIBLE);
                        mRelLiveBottom.setVisibility(immersive ? View.GONE : View.VISIBLE);
                    }

                    return false;

                }

                return false;
            }
        });

        mImgBottomBarScreen.setOnClickListener(this);
        mImgLiveScreen.setOnClickListener(this);
        rl_main_left_player.setOnClickListener(this);
        rl_main_left_pdf.setOnClickListener(this);
        tv_jianjie.setOnClickListener(this);
        tv_kebiao.setOnClickListener(this);
        tv_chanjian.setOnClickListener(this);
        tv_hudong.setOnClickListener(this);
//        dosView.setOnDocViewClickedListener(this);
//        rl_fullScreenBack.setOnClickListener(this);
        tv_jianjie.setOnClickListener(this);
        iv_icon.setOnClickListener(this);
//        videoView.setOnClickListener(this);
        img_customer_service.setOnClickListener(this);
        directClassScheduleAdapter.setVideoClickbutton(new CourseWareAdapter.VideoClick() {
            @Override
            public void setVideoClick() {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
//        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    if (isLoadEnd) {
//                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
//                            listview.addFooterView(loadView);
//                            loadIcon.startAnimation(refreshingAnimation);
//                            loadDirectClasSchedule(false);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            }
//        });

//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if ("0".equals(directBeanListForClassSchedule.get(position).getVideo_status())) {//正在直播
//                    initRtComp(position);
//                } else {
//                    ToastUtils.showToast("直播未开始");
//                }
//            }
//        });
        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PdfBean pdfBean = directBeanListPDFForClassSchedule.get(position);

                switch (pdfBean.getState()) {
                    case 1:
                        pdfView.setVisibility(View.VISIBLE);
                        lv_pdf.setVisibility(View.GONE);
                        rl_main_left_pdf.setVisibility(View.VISIBLE);
                        isShowPdf = true;
                        initPdfView(pdfBean);
                        break;
                    case 2:
                        PdfDownloadManager.getInstance().cancelWaitTask(pdfBean);
                        break;

                    case 4:
                        directClassScheduleAdapter.showCustomToast("该下载任务无法取消!");
                        break;
                    default:
                        PdfDownloadManager.getInstance().addDownloadTask(pdfBean);
                        break;

                }


            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPdfDownloadProgressEvent(PdfBean info) {
        if (isFinishing()) {
            return;
        }


        Logger.e("pdf download:" + GsonUtils.toJson(info));
        int index = directBeanListPDFForClassSchedule.indexOf(info);
        directBeanListPDFForClassSchedule.set(index, info);
        directClassPdfAdapter.notifyDataSetChanged();

        if (info.getState() == 3 && !StringUtils.isEmpty(info.getErrorMessage())) {
            directClassScheduleAdapter.showCustomToast(info.getErrorMessage());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
            case R.id.rl_main_left_player:
                back();
                break;
//            case R.id.img_direct_dealite_face:
//                if (mGridView.getVisibility() == View.GONE) {
//                    mGridView.setVisibility(View.VISIBLE);
//                } else {
//                    mGridView.setVisibility(View.GONE);
//                }
//                break;
            case R.id.img_customer_service:
                MobclickAgent.onEvent(this, "consultationOnClik");
//                H5DetailActivity.newIntent(this, "咨询", directBean.getCustomer());

                if (directBean.getRid().equals("-1")) {
                    settingid1 = "kf_10092_1515490706283";
                } else {
                    settingid1 = "kf_10092_1513839603881";
                }


                if (directBeanListForClassSchedule != null && directBeanListForClassSchedule.size() != 0) {

                    String getbranchschoolid = directBeanListForClassSchedule.get(position).getbranchschoolid();

                    if (!StringUtils.isEmpty(getbranchschoolid)) {
                        settingid1 = getbranchschoolid;
                    }

                }

//                settingid1 = directBean.getbranchschoolid();
//                if (directBean.getRid().equals("-1")) {
//                    settingid1 = "kf_10092_1515490706283";
//                } else {
//                    settingid1 = "kf_10092_1513839603881";
//                }
                //小能登录
                Ntalker.getBaseInstance().login(uid, CommonUtils.getSharedPreferenceItem(null, com.huatu.teacheronline.utils.UserInfo.KEY_SP_ACCOUNT, ""), 0);
                ChatParamsBody chatparams = new ChatParamsBody();
                chatparams.headurl = CommonUtils.getSharedPreferenceItem(null, com.huatu.teacheronline.utils.UserInfo.KEY_SP_FACEPATH, "");
                Ntalker.getBaseInstance().startChat(this, settingid1, "华图客服", chatparams, TestChatActivity.class);
                break;
            case R.id.ib_main_right:
            case R.id.ib_main_right_player:
                if (ClickUtils.isFastClick()) {
                    return;
                }

                if (directBeanListForClassSchedule != null && directBeanListForClassSchedule.size() > 0) {
                    DownManageActivity.newIntent(PlayerActivityForBjysdk.this, directBeanListForClassSchedule);
                }
                break;
            case R.id.videoView://视频点击事件
                if (directBean != null) {
                    isScreenButtonVisible();
                }
                break;
            case R.id.iv_icon://ppt和视频切换
                if (directBean == null) {
                    return;
                }
                //直播未开始，不能切换
//                if ("0".equals(directBean.getVideo_status())) {
                isPPT = !isPPT;
                changeDisplayContent();


//                }
                break;
            case R.id.iv_screen:
            case R.id.iv_live_screen:
            case R.id.iv_directScreen://全屏
                if (directBean != null) {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏


                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
                    }
                }
                break;
            case R.id.top_bar_no_titlebar:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    finish();
                }


                break;
            case R.id.tv_jianjie://pdf
                if (tab != 1) {
                    tabJianjie();
                    ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.class.getName());
                    if (chatFragment != null) {
                        chatFragment.back();
                    }
                }
                break;
            case R.id.tv_kebiao://课表
                if (tab != 2) {
                    tabKebiao();
                    ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.class.getName());
                    if (chatFragment != null) {
                        chatFragment.back();
                    }
                }
                break;
            case R.id.tv_hudong://互动
                if (tab != 3) {
                    tabHudong();
                }
                break;
            case R.id.tv_chanjian://常见
                if (tab != 4) {
                    tabChanjian();
                    ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.class.getName());
                    if (chatFragment != null) {
                        chatFragment.back();
                    }
                }
                break;
            case R.id.rl_main_left_pdf:
                rl_main_left_pdf.setVisibility(View.GONE);
                pdfView.setVisibility(View.GONE);
                lv_pdf.setVisibility(View.VISIBLE);
                if (mDocView != null) {
                    isShowPdf = false;
                    ll_content_play.removeView(mDocView);
                }
                break;

            case R.id.tv_player_speed://清晰度 标清
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    mSpeedAdapter.select(speedType);
                    mRcvSpeed.setVisibility(View.VISIBLE);


                } else {
                    if (chooseSpeedPopwindows != null) {
                        chooseSpeedPopwindows.showPopUp(v, this, speedType);
                    }
                }


                break;
            case R.id.tv_speed_original:
                speedType = 0;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                tv_speed.setText(getString(R.string.speed_original));
                if (mPlayerView != null) {
                    mPlayerView.setPlayRate(1.0f);
                }
                break;
            case R.id.tv_speed_quick1:
                speedType = 1;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                tv_speed.setText(getString(R.string.speed_quick1));
                if (mPlayerView != null) {
                    mPlayerView.setPlayRate(1.25f);
                }
                break;
            case R.id.tv_speed_quick2:
                speedType = 2;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                tv_speed.setText(getString(R.string.speed_quick2));
                if (mPlayerView != null) {
                    mPlayerView.setPlayRate(1.5f);
                }
                break;
            case R.id.tv_speed_quick3:
                speedType = 3;
//                chooseSpeedPopwindows.setClickViewGone(v);
                chooseSpeedPopwindows.dissmiss();
                tv_speed.setText(getString(R.string.speed_quick3));
                if (mPlayerView != null) {
                    mPlayerView.setPlayRate(2.0f);
                }
                break;


        }
    }

    /**
     * 检测是否为第一次观看 显示引导界面s
     */
    private void checkFirstWatch() {
        if (mIsFirstWatch) {
            PlayerConfigDAO.getInstance()
                    .isFirstWatch()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            mIsFirstWatch = false;
                            if (aBoolean) {
                                // TODO: 2019/3/11  遮挡播放布局待处理
//                                mClGuide.setVisibility(View.VISIBLE);

                            }


                        }
                    });


        }


    }


    private void changeDisplayContent() {

        if (mIsVideo) {
            checkFirstWatch();
            return;
        }

        mLlPlayBackTop.setVisibility(View.VISIBLE);
        iv_icon.setVisibility(View.VISIBLE);

        if (mPptFragment != null) {
            setZOrderMediaOverlay(mPptFragment.getView(), isPPT);
        }
        if (mPlayerView != null) {
            setZOrderMediaOverlay(mPlayerView, !isPPT);

        }


//        if (isPPT) {
//            iv_icon.setBackgroundResource(R.drawable.ic_tovi);
//            mFraLivePpt.setVisibility(View.VISIBLE);
//            mFraLivePpt.setTranslationX(0);
//            findViewById(R.id.bjplayer_center_video_functions_ll).setVisibility(View.GONE);
////            mSurfaceContainer.setVisibility(View.GONE);
////            mPlayerView.setVisibility(View.GONE);
//            if (playerView != null) {
//                mFra_container.removeView(playerView);
//                playerView.setTranslationX(3000);
//            }
//            mSurfaceContainer.setTranslationX(3000);
//            if (!mIsPlayback) {
//                mRelLiveBottom.setVisibility(View.VISIBLE);
////                Fragment pptFragment = getSupportFragmentManager().findFragmentByTag(PPTFragment.class.getName());
//
//            } else {
//
//                mRelPlayBackBottom.setVisibility(View.VISIBLE);
//            }
//            mLlPlayBackTop.setVisibility(View.VISIBLE);
//
//
////            top_bar_player.setVisibility(View.VISIBLE);
//        } else {
//
//
//            if (mIsPlayback) {
//                checkFirstWatch();
//
//                mFraLivePpt.setTranslationX(3000);
//                if (playerView != null) {
//                    playerView.setTranslationX(0);
////                    mFra_container.addView(mPlayerView, 0);
//                }
//            } else {
//                mSurfaceContainer.setTranslationX(0);
//            }
//
//
//            iv_icon.setBackgroundResource(R.drawable.ic_toppt);
//
//            if (mIsPlayback) {
//                if (playerView != null) {
//                    playerView.setVisibility(View.VISIBLE);
//                }
//                mRelLiveBottom.setVisibility(View.GONE);
//                mRelPlayBackBottom.setVisibility(View.VISIBLE);
//                if (mPlayerView != null && mPlayerView.getDuration() == 0) {//mPlayerView.VIDEO_ORIENTATION_LANDSCAPE
//                    findViewById(R.id.bjplayer_center_video_functions_ll).setVisibility(View.VISIBLE);
//                }
//
////                top_bar_player.setVisibility(View.GONE);
//            } else {
//                findViewById(R.id.bjplayer_center_video_functions_ll).setVisibility(View.GONE);
//                mRelLiveBottom.setVisibility(View.VISIBLE);
//                mSurfaceContainer.setVisibility(View.VISIBLE);
//            }
//        }
    }

    /**
     * pdf 设置 tab
     */
    private void tabJianjie() {
        tab = 1;
        img_customer_service.setVisibility(View.VISIBLE);
//        rl_sendMessage.setVisibility(View.GONE);
        pdfView.setVisibility(View.GONE);
        listview.setVisibility(View.GONE);
//        lv_chat.setVisibility(View.GONE);
//        mGridView.setVisibility(View.GONE);
        webview.setVisibility(View.GONE);
        mFraLiveChat.setVisibility(View.GONE);
        if (directBean == null || directBeanListForClassSchedule == null || directBeanListForClassSchedule.size() == 0) {
            return;
        }
        if (directBeanListPDFForClassSchedule.size() == 0) {
            lv_pdf.setVisibility(View.GONE);
            ll_no_notes.setVisibility(View.VISIBLE);
        } else {
            if (isShowPdf) {
                ll_content_play.addView(mDocView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                        .MATCH_PARENT));
                rl_main_left_pdf.setVisibility(View.VISIBLE);
                ll_no_notes.setVisibility(View.GONE);
            } else {
                lv_pdf.setVisibility(View.VISIBLE);
                ll_no_notes.setVisibility(View.GONE);
            }
        }
//        String fileUrl = directBeanListForClassSchedule.get(position).getNetclass_pdf();
//        loadPDF(fileUrl);
    }

    /**
     * 课表 设置 tab
     */
    private void tabKebiao() {
        if (mDocView != null) {
            ll_content_play.removeView(mDocView);
        }
        img_customer_service.setVisibility(View.VISIBLE);
        tab = 2;
//        rl_sendMessage.setVisibility(View.GONE);
        rl_main_left_pdf.setVisibility(View.GONE);
        pdfView.setVisibility(View.GONE);
        listview.setVisibility(View.VISIBLE);
        lv_pdf.setVisibility(View.GONE);
//        lv_chat.setVisibility(View.GONE);
        ll_no_notes.setVisibility(View.GONE);
//        mGridView.setVisibility(View.GONE);
        mFraLiveChat.setVisibility(View.GONE);
        if (directBean == null || directBeanListForClassSchedule == null || directBeanListForClassSchedule.size() == 0) {
            return;
        }
        if (directBeanListForClassSchedule != null && directBeanListForClassSchedule.size() > 0) {
//                        listview.addFooterView(loadView);
//                        loadIcon.startAnimation(refreshingAnimation);
//                        listview.setAdapter(directClassScheduleAdapter);
//                        loadDirectClasSchedule(true);
//                    } else {
            DebugUtil.e("初始化课程列表");
//            View HeaderView = getLayoutInflater().inflate(R.layout.header, listview, false);
//            listview.setPinnedHeader(HeaderView);
//            listview.setAdapter(directClassScheduleAdapter);
//            directClassScheduleAdapter.bind(listview);
//            listview.setOnScrollListener(directClassScheduleAdapter);
            directClassScheduleAdapter.setSelection(position);
//                        completeRefresh();
        }
    }

    /**
     * 互动 设置 tab
     */
    private void tabHudong() {
        if (mDocView != null) {
            ll_content_play.removeView(mDocView);
        }
        tab = 3;
//        lv_chat.setVisibility(View.VISIBLE);
//        if (directBean != null) {
//            lv_chat.setAdapter(directUserMessgAdapter);
//        }
//        rl_sendMessage.setVisibility(View.VISIBLE);
        mFraLiveChat.setVisibility(View.VISIBLE);


        img_customer_service.setVisibility(View.GONE);
        webview.setVisibility(View.GONE);
        rl_main_left_pdf.setVisibility(View.GONE);
        pdfView.setVisibility(View.GONE);
        listview.setVisibility(View.GONE);
//        mGridView.setVisibility(View.GONE);
        lv_pdf.setVisibility(View.GONE);
        ll_no_notes.setVisibility(View.GONE);

    }

    /**
     * 常见问题 tab
     */
    private void tabChanjian() {
        if (mDocView != null) {
            ll_content_play.removeView(mDocView);
        }
        img_customer_service.setVisibility(View.VISIBLE);
        tab = 4;
        pdfView.setVisibility(View.GONE);
        listview.setVisibility(View.GONE);
//        mGridView.setVisibility(View.GONE);
        ll_no_notes.setVisibility(View.GONE);
        lv_pdf.setVisibility(View.GONE);
//        lv_chat.setVisibility(View.GONE);
        rl_main_left_pdf.setVisibility(View.GONE);
        webview.setVisibility(View.VISIBLE);
        webview.loadUrl(DataStore_Direct.Common_problem);

        mFraLiveChat.setVisibility(View.GONE);
    }

    public static void newIntent(Activity context, DirectBean directBean, /*ArrayList<DirectBean> directBeanList,*/ int position) {
        Intent intent = new Intent(context, PlayerActivityForBjysdk.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DirectBean", directBean);
//        bundle.putSerializable("DirectBeanList", directBeanList);
        intent.putExtras(bundle);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    public static void newIntent(Activity context, DirectBean directBean, /*ArrayList<DirectBean> directBeanList,*/ int position, int type) {
        Intent intent = new Intent(context, PlayerActivityForBjysdk.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DirectBean", directBean);
//        bundle.putSerializable("DirectBeanList", directBeanList);
        intent.putExtras(bundle);
        intent.putExtra("position", position);
        intent.putExtra("wherefrom", type);
        context.startActivity(intent);
    }

    public static void newIntent(Activity context, DirectBean directBean, int position, int type, int key) {
        Intent intent = new Intent(context, PlayerActivityForBjysdk.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DirectBean", directBean);
//        bundle.putSerializable("DirectBeanList", directBeanList);
        intent.putExtras(bundle);
        intent.putExtra("position", position);
        intent.putExtra("wherefrom", type);
        intent.putExtra("key", key);
        context.startActivity(intent);
    }


//    public static void newIntent(Context context, DirectBean directBean, int position, int type, String categorie, String period, String subject, String province) {
//        Intent intent = new Intent(context, PlayerActivityForBjysdk.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("DirectBean", directBean);
////        bundle.putSerializable("DirectBeanList", directBeanList);
//        intent.putExtras(bundle);
//        intent.putExtra("position", position);
//        intent.putExtra("wherefrom", type);
//        intent.putExtra(DirectDetailsActivity.KEY_CATEGORIE, categorie);
//        intent.putExtra(DirectDetailsActivity.KEY_PERIOD, period);
//        intent.putExtra(DirectDetailsActivity.KEY_SUBJECT, subject);
//        intent.putExtra(DirectDetailsActivity.KEY_PROVINCE, province);
//        context.startActivity(intent);
//
//    }


    @Override
    public boolean back() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.class.getName());
            //判断隐藏表情栏
            boolean used = false;
            if (chatFragment != null) {
                used = chatFragment.back();
            }

            if (!used) {
                finish();
            }

        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏


            mImgLiveScreen.setImageResource(R.drawable.ic_fullsc_land);
            mImgBottomBarScreen.setImageResource(R.drawable.ic_fullsc_land);
            img_customer_service.setVisibility(View.GONE);
//            rl_sendMessage.setVisibility(View.GONE);
//            top_bar_player.setVisibility(View.GONE);
            if (mIsVideo || mIsPlayback) {
                tv_speed.setVisibility(View.VISIBLE);

                mTvDefinition.setVisibility(View.VISIBLE);
                mLlBottiom.setVisibility(View.VISIBLE);


            } else {

                if (!directBean.getRid().equals("-1")) {
                    mLlLiveFunction.setVisibility(View.VISIBLE);
                }


            }

            View firstView = mLlTop.getChildAt(0);
            mLlTop.removeView(firstView);
            View endView = mLlTop.getChildAt(mLlTop.getChildCount() - 1);
            mLlTop.removeView(endView);

            mLlBottiom.addView(firstView, 0);
            mLlBottiom.addView(endView);


            isScreenButtonVisible();
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏

//            img_customer_service.setVisibility(View.VISIBLE);
            mRcvDefinition.setVisibility(View.GONE);
            mRcvSpeed.setVisibility(View.GONE);

            mLlTimeTable.setVisibility(View.GONE);
            mImgLiveScreen.setImageResource(R.drawable.ic_fullsc);
            mImgBottomBarScreen.setImageResource(R.drawable.ic_fullsc);
            findViewById(R.id.bjplayer_center_video_functions_frame_tv).setVisibility(View.GONE);
//            if (isPPT) {
//                top_bar_player.setVisibility(View.VISIBLE);
//            }
            if (mIsVideo || mIsPlayback) {
                tv_speed.setVisibility(View.VISIBLE);
                mTvDefinition.setVisibility(View.INVISIBLE);

            } else {
                mLlLiveFunction.setVisibility(View.INVISIBLE);
            }

            mLlBottiom.setVisibility(View.GONE);
            View firstView = mLlBottiom.getChildAt(0);
            mLlBottiom.removeView(firstView);
            View endView = mLlBottiom.getChildAt(mLlBottiom.getChildCount() - 1);
            mLlBottiom.removeView(endView);

            mLlTop.addView(firstView, 0);
            mLlTop.addView(endView);


            if (tab != 3) {
                img_customer_service.setVisibility(View.VISIBLE);
            }
            isScreenButtonVisible();
        }
    }

    private boolean immersive = false;
    private long touchTime;


    /**
     * 评价弹窗
     *
     * @param position
     */
    private void showAppraiseDialog(int position) {
        if (directBeanListForClassSchedule != null && directBeanListForClassSchedule.size() != 0 && directBeanListForClassSchedule.get(position).getIs_last() != null && directBeanListForClassSchedule.get(position).getIs_last()) {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
            }

            AppraiseAlertDialog appraiseAlertDialog = new AppraiseAlertDialog(this, directBeanListForClassSchedule.get(position));
            appraiseAlertDialog.show();
            directBeanListForClassSchedule.get(position).setIs_last(false);
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

    /**
     * 隐藏/显示播放屏幕中的按钮
     */
    public void isScreenButtonVisible() {
    }

    /**
     * 加载播放信息
     */
    public void loadDirectPlayInfo() {
        obtatinDataListener = new ObtatinDataListener(this);
        SendRequest.getLiveDataForClassSchedule(directBean.getRid(), uid, obtatinDataListener);
    }


    /**
     * 百家云直播出错状态
     *
     * @param lpError
     */
    @Override
    public void onError(LPError lpError) {
//        addRecordTime();
        Logger.e("liveRoom Error:" + lpError.getMessage());
        switch ((int) lpError.getCode()) {


            case LPError.CODE_ERROR_NETWORK_FAILURE: //无网
                break;
            case LPError.CODE_ERROR_NETWORK_MOBILE: //当前网络为mobile
                break;
            case LPError.CODE_ERROR_NETWORK_WIFI: //wifi
                break;
            case LPError.CODE_ERROR_UNKNOWN: // 未知错误
                break;
            case LPError.CODE_ERROR_JSON_PARSE_FAIL: // 数据解析失败
                break;
            case LPError.CODE_ERROR_INVALID_PARAMS: // 无效参数
                break;
            case LPError.CODE_ERROR_ROOMSERVER_FAILED:  //roomserver登录失败
                break;
            case LPError.CODE_ERROR_OPEN_AUDIO_RECORD_FAILED: //打开麦克风失败，采集声音失败
                break;
            case LPError.CODE_ERROR_OPEN_AUDIO_CAMERA_FAILED: //打开摄像头失败，采集图像失败
                break;
            case LPError.CODE_ERROR_MAX_STUDENT: //人数上限
                break;
            case LPError.CODE_ERROR_ROOMSERVER_LOSE_CONNECTION:  // roomserver 连接断开
                break;
            case LPError.CODE_ERROR_LOGIN_CONFLICT: // 被踢下线
                break;
            case LPError.CODE_ERROR_PERMISSION_DENY: // 权限错误
                break;
            case LPError.CODE_RECONNECT_SUCCESS:// 重连成功
                break;
            case LPError.CODE_ERROR_STATUS_ERROR:  // 状态错误
                break;
            case LPError.CODE_ERROR_MEDIA_SERVER_CONNECT_FAILED: //音视频服务器连接错误
                break;
            case LPError.CODE_ERROR_MEDIA_PLAY_FAILED:  //音视频播放失败
                break;
            case LPError.CODE_ERROR_CHATSERVER_LOSE_CONNECTION:  // chatserver 连接断开
                break;
            case LPError.CODE_ERROR_MESSAGE_SEND_FORBID:  //发言被禁止
                break;
            case LPError.CODE_ERROR_VIDEO_PLAY_EXCEED:  // 超出最大播放视频数量
                break;
            case LPError.CODE_ERROR_LOGIN_KICK_OUT:  //被踢
                break;
            case LPError.CODE_ERROR_FORBID_RAISE_HAND: //举手被禁止
                break;

        }


    }


    private PlayerListener mPlayerListener = new PlayerListener() {
        @Override
        public void onPlayingTimeChange(int i, int i1) {

        }

        @Override
        public void onStatusChange(PlayerStatus playerStatus) {

        }

        @Override
        public void onError(PlayerError playerError) {

        }

        @Override
        public void onBufferingStart() {

        }

        @Override
        public void onBufferingEnd() {

        }


        //回放 OnPlayerListener

//        @Override
//        public void onVideoInfoInitialized(BJPlayerView bjPlayerView, long l, HttpException e) {
//            Logger.d("liveRoom onVideoInfoInitialized:");
//
//
//        }
//
//        @Override
//        public void onVideoInfoInitialized(BJPlayerView bjPlayerView, HttpException e) {
//            Logger.d("player onVideoInfoInitialized:");
////            rl_deatil_waiting.setVisibility(View.GONE);
//            luanchSuccess = true;
//            trackWatchCourse(true);
//
//        }
//
//        @Override
//        public void onError(BJPlayerView bjPlayerView, int i) {
//            Logger.e("liveRoom onError:" + i);
//
//
//        }
//
//        @Override
//        public void onUpdatePosition(BJPlayerView bjPlayerView, int i) {
//
//        }
//
//        @Override
//        public void onSeekComplete(BJPlayerView bjPlayerView, int i) {
//
//        }
//
//        @Override
//        public void onSpeedUp(BJPlayerView bjPlayerView, float v) {
//
//        }
//
//        @Override
//        public void onVideoDefinition(BJPlayerView bjPlayerView, int i) {
//            DefinitionBean definitionBean = new DefinitionBean();
//            definitionBean.setType(Utils.getVideoDefinitionFromInt(i));
//            definitionBean.setValue(i);
//
//            mDefinitionAdapter.select(definitionBean);
//            mTvDefinition.setText(mDefinitionAdapter.getNameByValue(i));
//
//            Logger.d("onVideoDefinition : " + i);
//        }
//
//        @Override
//        public void onPlayCompleted(BJPlayerView bjPlayerView, VideoItem videoItem, SectionItem sectionItem) {
//
//            playNextVideo(position);
////        addRecordTime();
//        }
//
//        @Override
//        public void onVideoPrepared(BJPlayerView bjPlayerView) {
//            if (mPlayerView != null) {
//                setZOrderMediaOverlay(mPlayerView.getVideoView(), !isPPT);
//
//                if (mPlayerView.getCenterViewPresenter() != null) {
//                    mPlayerView.getCenterViewPresenter().dismissLoading();
//
//                }
//            }
//
//            List<DefinitionBean> ownedList = new ArrayList<>();
//
//            if (bjPlayerView.getVideoItem().definition != null) {
//                for (int i = 0; i < bjPlayerView.getVideoItem().definition.size(); i++) {
//                    VideoItem.DefinitionItem definitionItem = bjPlayerView.getVideoItem().definition.get(i);
//                    DefinitionBean definitionBean = new DefinitionBean();
//                    definitionBean.setType(definitionItem.type);
//                    definitionBean.setValue(Utils.getVideoDefinitionFromString(definitionItem.type));
//                    ownedList.add(definitionBean);
//
//                }
//            }
//
//
//            mDefinitionAdapter.setOwnedList(ownedList);
//            DefinitionBean definitionBean = new DefinitionBean();
//            definitionBean.setType(Utils.getVideoDefinitionFromInt(bjPlayerView.getVideoDefinition()));
//            definitionBean.setValue(bjPlayerView.getVideoDefinition());
//
//            mDefinitionAdapter.select(definitionBean);
//            mTvDefinition.setText(mDefinitionAdapter.getNameByValue(bjPlayerView.getVideoDefinition()));
//
//
//        }
//
//        @Override
//        public void onPause(BJPlayerView bjPlayerView) {
////        addRecordTime();
//        }
//
//        @Override
//        public void onPlay(BJPlayerView bjPlayerView) {
////        joinTime = StringUtils.getNowTime();
//        }

    };

    @Override
    public void performPickFor(FilePicker picker) {

    }


    private static class ObtatinDataListener extends ObtainDataFromNetListener<ArrayList<DirectBean>, String> {
        private PlayerActivityForBjysdk weak_activity;

        public ObtatinDataListener(PlayerActivityForBjysdk activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.show();
            }

        }


        @Override
        public void onSuccess(final ArrayList<DirectBean> res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushContent_OnSucess(res);
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushContent_OnFailure(res);
                    }
                });
            }
        }
    }

    public void flushContent_OnSucess(ArrayList<DirectBean> res) {

        if (res != null) {
            directBeanListForClassSchedule = res;
            DataStore_Direct.directDatailList = directBeanListForClassSchedule;
            iniFristData(isLocaleData);
        } else {
            ToastUtils.showToast(getString(R.string.course_not_start));
        }
        mCustomLoadingDialog.dismiss();
    }

    /**
     * 初始化首次加载课程表
     */
    private void iniFristData(boolean isLocaleData) {
//        if (!isLocaleData) {
        if (!isFinishing()) {
            synchronizationLocal(directBeanListForClassSchedule);
        }
//        }


        directClassScheduleAdapter.setDirectBeanList(directBeanListForClassSchedule, position + "", 1);
        mTimeTableAdapter.setDirectBeanList(directBeanListForClassSchedule, position + "");

//        webview.loadDataWithBaseURL(null, directBean.getContent(), "text/html", "UTF-8", null);
//        tv_main_title.setText(directBeanListForClassSchedule.get(position).getTitle());


        playNumbe = playrow;
        //课程详情进入
        if (wherefrom == 0) {
            chekNetState();
        } else {
            if (position == 0) {

                for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
                    DirectBean directBean = directBeanListForClassSchedule.get(i);
                    if (directBean.getVideo_status().equals("0") || directBean.getVideo_status().equals("2")) {
                        position = i;
                        break;
                    }


                }

            }
            DirectBean mDirectBean = directBeanListForClassSchedule.get(position);

            if (mDirectBean.getVideoType() == 1 && StringUtils.isEmpty(mDirectBean.getLubourl())) {
                switch (mDirectBean.getVideo_status()) {
                    case "1":
                        ToastUtils.showToast("直播暂未开始");

                        return;
                    case "3":
                        ToastUtils.showToast("暂无回放");
                        return;
                }

            }
            WatchRecordDAO.getInstance().queryRecord(directBean.getRid())
                    .map(new Func1<WatchRecord, Integer>() {
                        @Override
                        public Integer call(WatchRecord watchRecord) {

                            if (watchRecord != null) {
                                for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {

                                    if (directBeanListForClassSchedule.get(i).getLessonid().equals(watchRecord.getCourseWareId())) {

                                        return i;
                                    }
                                }
                            }
                            return -1;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer result) {
                            if (result != -1) {


                                position = result;

                            }

                            chekNetState();


                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Logger.e("queryRecord:" + throwable.getMessage());
                        }
                    });


        }


    }

    private void chekNetState() {
        int networkType = NetWorkUtils.getAPNType(this);
        String netWorkName = "";
        switch (networkType) {
            case 0:
                netWorkName = "无网络";
                break;
            case 1:
                netWorkName = "WIFI网络";
                break;
            case 2:
                netWorkName = "2G网络";
                break;
            case 3:
                netWorkName = "3G网络";
                break;
            case 4:
                netWorkName = "4G网络";
                break;
        }

        mIsPlayback = directBeanListForClassSchedule.get(position).getVideo_status().equals("2") || directBeanListForClassSchedule.get(position).getVideo_status().equals("3");
        boolean isNotLive = directBeanListForClassSchedule.get(position).getVideoType() == 0;
        if (isNotLive || mIsPlayback || networkType == 1 || networkType == 0) {
            initLiveRoom(directBeanListForClassSchedule.get(position), position);
        } else {
            mNetWorkDialog = new MaterialDialog(this);
            mNetWorkDialog.setTitle("提示")
                    .setMessage("当前网络为" + netWorkName + ",是否继续播放?")
                    .setNegativeButton("退出", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mNetWorkDialog.dismiss();
                            finish();
                        }
                    })
                    .setPositiveButton("继续播放", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mNetWorkDialog.dismiss();
                            initLiveRoom(directBeanListForClassSchedule.get(position), position);
                        }
                    })
                    .show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetWorkStateChangeEvent(NetWorkChangeEvent event) {

        //屏蔽刚注册广播时的通知
        if (isFirstRegister) {
            isFirstRegister = false;
            return;
        }
        int networkType = NetWorkUtils.getAPNType(this);

        //网络恢复时重新请求失败的听课记录
        if (networkType != 0) {
            RecodeRequestFailureManager.getInstance().checkRequestRetry();

        }

        if (mIsPlayback || mIsVideo) {
            return;
        }


        String netWorkName = "";
        switch (networkType) {
            case 0:
                netWorkName = "无网络";
                break;
            case 1:
                netWorkName = "WIFI网络";
                break;
            case 2:
                netWorkName = "2G网络";
                break;
            case 3:
                netWorkName = "3G网络";
                break;
            case 4:
                netWorkName = "4G网络";
                break;
        }


        if (mNetWorkDialog == null) {
            mNetWorkDialog = new MaterialDialog(this);
        }

        mNetWorkDialog.setTitle("提示")
                .setMessage("当前网络为" + netWorkName + ",是否继续播放?")
                .setNegativeButton("退出", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mNetWorkDialog.dismiss();
                        finish();
                    }
                })
                .setPositiveButton("继续播放", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mNetWorkDialog.dismiss();
                        if (!mIsPlayback && mLiveRoom != null && mLiveRoom.getPresenterUser() != null) {
                            mPresenteUserId = mLiveRoom.getPresenterUser().getUserId();
                            if (!StringUtils.isEmpty(mPresenteUserId)) {
                                mLiveRoom.getPlayer().playVideo(mPresenteUserId, mSurface);
                            }
                        }


                    }
                })
                .show();


    }

    public void flushContent_OnFailure(String res) {
        mCustomLoadingDialog.dismiss();
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast("网络异常,请检查网络");
        } else if (SendRequest.ERROR_SERVER.equals(res)) {
            ToastUtils.showToast(getResources().getString(R.string.server_error));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        m_wakeLock.acquire(); //设置保持唤醒

//        if (mPptFragment != null) {
//            mPptFragment.onStop();
//            setZOrderMediaOverlay(mPptFragment.getView(), isPPT);
//            mPptFragment.onStart();
//
//        }
//
//        if (mPlayerView != null) {
//
////            setZOrderMediaOverlay(mPlayerView, !isPPT);
//            mPlayerView.play();
//        }

        isPPT = true;
//        changeDisplayContent();


    }

    @Override
    protected void onPause() {
        super.onPause();
        m_wakeLock.release();//解除保持唤醒
//        if (mPlayerView != null) {
//            mPlayerView.pause();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mInBack = false;
        joinTime = StringUtils.getNowTime();

        RecordInfoManager.getInstance().setJoinTime(joinTime);

        if (!mIsPlayback && mLiveRoom != null && mLiveRoom.getPresenterUser() != null) {
            mPresenteUserId = mLiveRoom.getPresenterUser().getUserId();
            if (!StringUtils.isEmpty(mPresenteUserId)) {
                mLiveRoom.getPlayer().playVideo(mPresenteUserId, mSurface);
            }
        }


//        trackWatchCourse(true);


    }

    private void trackWatchCourse(boolean isStart) {

        if (!luanchSuccess || "-1".equals(directBean.getRid()) || (mPlayingDirectBean == null)) {
            return;
        }

        try {

            WatchCourse.Builder builder = new WatchCourse.Builder()
                    .course_id(mPlayingDirectBean.getNetClassId())
                    .course_courseware_id(Long.valueOf(mPlayingDirectBean.getLessonid()))
                    .course_courseware_title(mPlayingDirectBean.getClassTitle())
                    .course_courseware_live_start_time(StringUtils.checkEmpty2Null(mPlayingDirectBean.getZhibotime()))
                    .course_courseware_live_end_time(StringUtils.checkEmpty2Null(mPlayingDirectBean.getZhiboendtime()))
                    .course_courseware_live(mPlayingDirectBean.getVideoType() == 1 && "0".equals(mPlayingDirectBean.getVideo_status()))
                    .course_courseware_BJY_id(mPlayingDirectBean.getBjyvideoid())
//                .course_courseware_hour(Integer.valueOf(mPlayingDirectBean.getLessionCount()))
                    .course_courseware_teacher(mPlayingDirectBean.getTeacherDesc())
                    .course_id(directBean.getRid())

                    .course_examination_method(directBean.getCourse_examination_method())
                    .course_examination(directBean.getCourse_examination())
                    .course_study_section(directBean.getCourse_study_section())
                    .course_subject(directBean.getCourse_subject())
                    .course_class_type(directBean.getCourse_class_type())
                    .course_province(directBean.getCourse_province());


            if (isStart) {
                mStartWatchCourse = DateTimeUtil.getNowTime(DateTimeUtil.yMd_Hms_format);
                TrackUtil.trackStartWatchCourse(builder.build());
            } else {
                if (StringUtils.isEmpty(mStartWatchCourse)) {
                    return;
                }
                String endWatchCourse = DateTimeUtil.getNowTime(DateTimeUtil.yMd_Hms_format);
                long duration = DateTimeUtil.parseTime(endWatchCourse, DateTimeUtil.yMd_Hms_format) -
                        DateTimeUtil.parseTime(mStartWatchCourse, DateTimeUtil.yMd_Hms_format);
                builder.course_courseware_duration(duration);
                TrackUtil.trackEndWatchCourse(builder.build());
            }

        } catch (Exception e) {
            Logger.e(SensorDataSdk.TAG + e.toString());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mInBack = true;
        Logger.d("liveActivity onStop mInBack = " + mInBack);

        addRecordTime();
        joinTime = "";
        RecordInfoManager.getInstance().setJoinTime(joinTime);

        if (!mIsPlayback && mLiveRoom != null && mLiveRoom.getPresenterUser() != null) {
            mPresenteUserId = mLiveRoom.getPresenterUser().getUserId();
            if (!StringUtils.isEmpty(mPresenteUserId)) {
                mLiveRoom.getPlayer().playAVClose(mPresenteUserId);
            }
        }

//        trackWatchCourse(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_wakeLock.release(); //解除保持唤醒
        mPlayerView.onDestroy();//释放播放器
        //取消pdf下载
        PdfDownloadManager.getInstance().destory();
        quitLiveRoom();

        //关闭异常崩溃保存听课记录
        RecordInfoManager.getInstance().release();


        if (mNetworkChangeReceiver != null) {
            unregisterReceiver(mNetworkChangeReceiver);

        }


    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
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
                //网课
                DirectBean directBean1 = null;
                if (!StringUtils.isEmpty(directBeanListForClassSchedule.get(i).getBjyvideoid())) {
                    directBean1 = daoUtils.queryDirectBeanForBjyVedioId(uid, directBeanListForClassSchedule.get(i).getBjyvideoid(),
                            directBeanListForClassSchedule.get(i).getNumber());
                }
                if (directBean1 == null && !StringUtils.isEmpty(directBeanListForClassSchedule.get(i).getCcCourses_id())) {
                    //通过百家云的id查询不到的还需要用CC视频的id查询一次
                    directBean1 = daoUtils.queryDirectBeanForCCVedioId(uid, directBeanListForClassSchedule.get(i).getCcCourses_id(),
                            directBeanListForClassSchedule.get(i).getNumber());
                }
                if (directBean1 != null) {
                    String down_status = directBean1.getDown_status();
                    if (!StringUtils.isEmpty(down_status) && Integer.parseInt(directBean1.getDown_status()) == DownManageActivity.CCDOWNSTATE_STAR) {
                        down_status = DownManageActivity.CCDOWNSTATE_PAUSE + "";
                    }

                    directBeanListForClassSchedule.get(i).setDown_status(down_status);
                    directBeanListForClassSchedule.get(i).setLocalPath(directBean1.getLocalPath());
                    directBeanListForClassSchedule.get(i).setStart(directBean1.getStart());
                    directBeanListForClassSchedule.get(i).setEnd(directBean1.getEnd());
                }
            } else if (directBeanListForClassSchedule.get(i).getVideoType() == 1) {
                DirectBean directOrg = directBeanListForClassSchedule.get(i);
                if (StringUtil.isEmpty(directBean.getLubourl()) && !StringUtil.isEmpty(directOrg.getRoom_id())) {
                    DirectBean directQuery = daoUtils.queryDirectBeanForPlayBack(uid, directOrg.getRoom_id(), directOrg.getSession_id(), directOrg.getNumber());
                    if (directQuery != null) {
                        String down_status = directQuery.getDown_status();
                        if (!StringUtils.isEmpty(down_status) && Integer.parseInt(directQuery.getDown_status()) == DownManageActivity.CCDOWNSTATE_STAR) {
                            down_status = DownManageActivity.CCDOWNSTATE_PAUSE + "";
                        }
                        directBeanListForClassSchedule.get(i).setDown_status(down_status);
                        directBeanListForClassSchedule.get(i).setLocalPath(directQuery.getLocalPath());
                        directBeanListForClassSchedule.get(i).setStart(directQuery.getStart());
                        directBeanListForClassSchedule.get(i).setEnd(directQuery.getEnd());
                    }


                }
//                else {
//                    DirectBean directBean1 = daoUtils.queryDirectBeanForGeeneVedioId(uid, directBeanListForClassSchedule.get(i).getLubourl(),
//                            directBeanListForClassSchedule.get(i).getNumber());
//                    if (directBean1 != null) {
//                        directBeanListForClassSchedule.get(i).setDown_status(directBean1.getDown_status());
//                        directBeanListForClassSchedule.get(i).setLocalPath(directBean1.getLocalPath());
//                        directBeanListForClassSchedule.get(i).setStart(directBean1.getStart());
//                        directBeanListForClassSchedule.get(i).setEnd(directBean1.getEnd());
////                    directBeanListForClassSchedule.get(i).setVideo_status(directBean1.getVideo_status());
//                    }
//                }

            }
        }


        //删除rid相同的课程后，同步完数据库之后再加入课时缓存
//        DebugUtil.e("synchronizationLocal:" + DirectPlayDetailsActivityForRtsdk.this.directBean.toString());
        daoUtils.deletDirectBeanListForRid(PlayerActivityForBjysdk.this.directBean.getRid());
        for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
            DirectBean directBean = directBeanListForClassSchedule.get(i);
            directBean.setUserid(uid);
            directBean.setRid(PlayerActivityForBjysdk.this.directBean.getRid());
            daoUtils.insertOrUpdateDirectBeanCacheClass(directBean);
            DebugUtil.e("synchronizationLocal:" + directBean.toString());


        }


        mCustomLoadingDialog.dismiss();
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
        DebugUtil.e("onReceiveBroadCast:rt" + mDirectBean.toString());
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
                } else if (directBeanListForClassSchedule.get(i).getCcCourses_id() != null && directBeanListForClassSchedule.get(i).getCcCourses_id().equals(mDirectBean.getCcCourses_id())) {
                    directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
                    directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
                    directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
                    directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
                }
            } else {
                if (!StringUtils.isEmpty(directBeanListForClassSchedule.get(i).getLubourl())) {
                    if (directBeanListForClassSchedule.get(i).getLubourl().equals(mDirectBean.getLubourl())) {
                        directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
                        directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
                        directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
                        directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
                        directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
                    }

                } else {

                    if (directBeanListForClassSchedule.get(i).getRoom_id().equals(mDirectBean.getRoom_id()) && directBeanListForClassSchedule.get(i).getSession_id().equals(mDirectBean.getSession_id())) {

                        directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
                        directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
                        directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
                        directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
                        directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
                    }

                }
            }

        }
        directClassScheduleAdapter.notifyDataSetChanged();
    }

    private void initPdfView(PdfBean pdfBean) {

        String[] split = pdfBean.getFileUrl().split("/");
        String fileName = split[split.length - 1];
        String path = Environment.getExternalStorageDirectory() + "/jy/" + fileName;

        core = openFile(path);
        if (core == null) {
            if (path.endsWith("pdf") || path.endsWith("PDF")) {
                Toast.makeText(this, "讲义文件异常,请重新下载!", Toast.LENGTH_SHORT).show();
                File file = new File(path);
                file.delete();
                pdfBean.setState(0);
                directClassPdfAdapter.notifyDataSetChanged();

            } else {
                Toast.makeText(this, "讲义打开失败,请前往该目录查看!" + path, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        totalPageCount = core.countPages();
        if (lastPagePosition > totalPageCount) {
            lastPagePosition = totalPageCount;
            isFinished = true;
        }
        if (totalPageCount == 0) {

            if (path.endsWith("pdf") || path.endsWith("PDF")) {
                Toast.makeText(this, "讲义文件异常,请重新下载!", Toast.LENGTH_SHORT).show();
                File file = new File(path);
                file.delete();
                pdfBean.setState(0);
                directClassPdfAdapter.notifyDataSetChanged();

            } else {
                Toast.makeText(this, "讲义打开失败,请前往该目录查看!" + path, Toast.LENGTH_SHORT).show();
            }
            return;

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
        mDocView.setAdapter(new MuPDFPageAdapter(PlayerActivityForBjysdk.this, this, core));
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
            DebugUtil.e(TAG, e.getMessage());
            return null;
        }
        return core;
    }

    private static class ObtatinPdfListener extends ObtainDataFromNetListener<ArrayList<PdfBean>, String> {
        private WeakReference<PlayerActivityForBjysdk> weak_activity;

        public ObtatinPdfListener(PlayerActivityForBjysdk activity) {
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
            for (PdfBean pdfBean : directBeanListPDFForClassSchedule) {

                String[] split = pdfBean.getFileUrl().split("/");
                String fileName = split[split.length - 1];
                String jyDownLoadUrlForLocal = Environment.getExternalStorageDirectory() + "/jy/" + fileName;
                final File file = new File(jyDownLoadUrlForLocal);
                if (file.exists()) {
                    pdfBean.setState(1);

                }


            }

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
        obtatinPdfListener = new ObtatinPdfListener(PlayerActivityForBjysdk.this);
        SendRequest.getLivePDFForClassSchedule(directBean.getRid(), uid, obtatinPdfListener);
    }


//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        Logger.e("crash save");
//        //异常销毁时  保存当前的观看时长记录
//        if (!StringUtils.isEmpty(joinTime) && recodingInfo != null) {
//            //记录时长
//            leavetime = StringUtils.getNowTime();
//            if (recodingInfo.getReturncash() > 0) {
//
//
//
//                RecodeRequestFailure recodeRequestFailure = new RecodeRequestFailure(uid,
//                        account,
//                        recodingInfo.getOrderid(),
//                        joinTime,
//                        leavetime,
//                        recodingInfo.getNetClassId(),
//                        recodingInfo.getLessonid(),
//                        mIsPlayback ? "lubo" : "zhibo");
//
//                RecodeRequestFailureManager.getInstance().save(recodeRequestFailure);
//                Logger.e("crash save success");
//            }
//
//            joinTime = "";
//        }
//        super.onSaveInstanceState(outState);
//    }


    /**
     * 记录时长
     */
    private void addRecordTime() {
        if (!StringUtils.isEmpty(joinTime) && recodingInfo != null) {

            //记录时长
            leavetime = StringUtils.getNowTime();

            if (!leavetime.equals(joinTime)) {

                if (recodingInfo.getReturncash() > 0) {
                    addRecord(recodingInfo);
                    joinTime = StringUtils.getNowTime();
                    RecordInfoManager.getInstance().setJoinTime(joinTime);

                }
            }


        }
    }

    void addRecord(final DirectBean mDirectBean) {
        Logger.e(mDirectBean.getNetClassId() + " " + mDirectBean.getTitle() + " joinTime:" + joinTime + " leavetime:" + leavetime);
        SendRequest.addRecord(uid, account, mDirectBean.getOrderid(), joinTime, leavetime, mDirectBean.getNetClassId(), mDirectBean.getLessonid(), mIsPlayback ? "lubo" : "zhibo", new
                ObtainDataFromNetListener<WithdrawalBean, String>() {
                    @Override
                    public void onSuccess(final WithdrawalBean res) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Logger.e("recode success" + res.getProgress());

                                mDirectBean.setProgress(res.getProgress());
                                mDirectBean.setFstatus(res.getFstatus());
                                directClassScheduleAdapter.notifyDataSetChanged();
                                if (mDirectBean.getLessonid().equals(mPlayingDirectBean.getLessonid())) {
                                    mPbWithDraw.setProgress(res.getProgress());
                                    mPbLiveWithDraw.setProgress(res.getProgress());
                                }


                            }
                        });
                    }

                    @Override
                    public void onFailure(String res) {
                        Logger.e("recode error :" + res);
                    }
                });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideSoftInputEvent(HideSoftInputEvent event) {
        if (event.isClose()) {
            InputMethodManager mInputKeyBoard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null) {
                mInputKeyBoard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

    }


    @OnClick({R.id.ll_bottombar_withdraw, R.id.ll_bottombar_timetable, R.id.ll_live_timetable_title, R.id.img_guide_confirm, R.id.tv_player_definition
            , R.id.ll_live_bottombar_withdraw, R.id.ll_live_bottombar_timetable
    })
    public void onBottomBarClick(View v) {
        switch (v.getId()) {
            case R.id.ll_bottombar_withdraw:
            case R.id.ll_live_bottombar_withdraw:
                if (mPlayingDirectBean == null || mPlayingDirectBean.getFstatus() == 2 || mPlayingDirectBean.getProgress() == 0 || mPlayingDirectBean.getFstatus() == 0 || mPlayingDirectBean == null) {

                    return;
                }

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏

                WithdrawalDialog mWithdrawalDialog = new WithdrawalDialog(this, mPlayingDirectBean, mPlayingDirectBean.getRid(), mPlayingDirectBean.getOrderid());
                mWithdrawalDialog.show();
                mWithdrawalDialog.setOnWithdrawaledListener(new WithdrawalDialog.OnWithdrawaledListener() {
                    @Override
                    public void submitCompleted(String res) {
                        mPlayingDirectBean.setFstatus(2);

                    }
                });


                break;
            case R.id.ll_live_bottombar_timetable:
            case R.id.ll_bottombar_timetable:

                mLlTimeTable.setVisibility(View.VISIBLE);
                mTimeTableAdapter.setSelection(position);

                break;


            case R.id.ll_live_timetable_title:
                mLlTimeTable.setVisibility(View.GONE);

                break;

            case R.id.img_guide_confirm:
                mClGuide.setVisibility(View.GONE);

                break;

            case R.id.tv_player_definition:

                mRcvDefinition.setVisibility(View.VISIBLE);

                break;


        }

    }

    /**
     * 观看记录
     *
     * @param info
     */
    private void addWatchRecord(DirectBean info) {

        WatchRecordDAO.getInstance()
                .addRecord(directBean.getRid(), info.getLessonid());


    }


    /**
     * 连续播放
     */
    private void playNextVideo(int currentPosition) {

        if (directBean.getRid().equals("-1")) {
            return;
        }

        int nextPosition = currentPosition + 1;


        if (mSwitch.isChecked()) {


            if (directBeanListForClassSchedule.size() > nextPosition) {

                DirectBean directBean = directBeanListForClassSchedule.get(nextPosition);

                if ((directBean.getIs_buy() == null || (Integer.parseInt(directBean.getIs_buy()) == 0) && directBean.getIsTrial() != 1)) {
                    return;
                }


                if (directBean.getVideoType() == 1 && (directBean.getVideo_status().equals("1") || directBean.getVideo_status().equals("3"))) {

                    playNextVideo(nextPosition);

                } else {

                    if (NetWorkUtils.isNetworkConnected(this)) {
                        initLiveRoom(directBean, nextPosition);
                    } else if ((DownManageActivity.CCDOWNSTATE_COMPLETE + "").equals(directBean.getDown_status())) {
                        initLiveRoom(directBean, nextPosition);
                    } else {
                        playNextVideo(nextPosition);
                    }


                }

            } else {
                directClassScheduleAdapter.showCustomToast("没有可以连续播放的视频了~");


            }


        }


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mIsVideo || mIsPlayback) {
            return super.onKeyDown(keyCode, event);
        }


        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
            mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (mLiveRoom != null) {
                mLiveRoom.getPlayer().unMute();
            }

            Logger.e("mStreamVolume up:" + mStreamVolume);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (mAudioManager == null) return true;
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER,
                    AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
            int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (volume == minVolume && mStreamVolume == minVolume && mLiveRoom != null) {
                mLiveRoom.getPlayer().mute();
            }
            mStreamVolume = volume;
            Logger.e("mStreamVolume down:" + mStreamVolume);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 初始化全屏时的BottomView
     *
     * @param directBean
     */
    private void initBottomExtendView(DirectBean directBean) {

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            mLlBottiom.setVisibility(View.VISIBLE);
            mLlLiveFunction.setVisibility(View.VISIBLE);


        } else {
            mLlBottiom.setVisibility(View.GONE);
            mLlLiveFunction.setVisibility(View.INVISIBLE);
        }


        if (isContinuous != -1) {
            mSwitchLiveContinuous.setChecked(isContinuous == 0);
            mSwitch.setChecked(isContinuous == 0);

        }


        //提现状态逻辑

        if (this.directBean.getRid().equals("-1") ||
                directBean.getIsTrial() == 1 || directBean.getReturncash() == 0) {
            mLlWithDraw.setVisibility(View.GONE);
            mLlLiveWithDraw.setVisibility(View.GONE);


        } else {


            mLlWithDraw.setVisibility(View.VISIBLE);
            mLlLiveWithDraw.setVisibility(View.VISIBLE);
            mPbWithDraw.setProgress(directBean.getProgress());
            mPbLiveWithDraw.setProgress(directBean.getProgress());
            if (directBean.getProgress() == 100 && directBean.getFstatus() != 0) {
                mImgWithDraw.setVisibility(View.VISIBLE);
                mPbWithDraw.setVisibility(View.GONE);
                mTvWithDraw.setTextColor(getResources().getColor(R.color.gold));
                mImgLiveWithDraw.setVisibility(View.VISIBLE);
                mPbLiveWithDraw.setVisibility(View.GONE);
                mTvLiveWithDraw.setTextColor(getResources().getColor(R.color.gold));

            } else {
                mImgWithDraw.setVisibility(View.GONE);
                mPbWithDraw.setVisibility(View.VISIBLE);
                mTvWithDraw.setTextColor(getResources().getColor(R.color.white));
                mImgLiveWithDraw.setVisibility(View.GONE);
                mPbLiveWithDraw.setVisibility(View.VISIBLE);
                mTvLiveWithDraw.setTextColor(getResources().getColor(R.color.white));


            }
            if (directBean.getFstatus() == 2) {
                mTvWithDraw.setText("已提现");
                mTvLiveWithDraw.setText("已提现");
            } else {
                mTvWithDraw.setText("提现");
                mTvLiveWithDraw.setText("提现");
            }


        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayEvent(PlayByIndexEvent event) {
        initLiveRoom(event.getInfo(), event.getIndex());


    }


    /**********************************手势处理*************************************/


    private class LiveSurfaceGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean M;
        private boolean N;
        private boolean O;


        public boolean onDown(MotionEvent var1) {
            this.O = true;
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            dismissBrightnessSlide();

            return true;
        }

        public boolean onScroll(MotionEvent var1, MotionEvent var2, float var3, float var4) {
            float var5 = var1.getX();
            float var6 = var1.getY();
            float var7 = var6 - var2.getY();
            if (this.O) {
                this.M = Math.abs(var3) >= Math.abs(var4);
                this.N = var5 > (float) mSurface.getMeasuredWidth() * 0.5F;
                this.O = false;
            }

//            if (this.M) {
//                if (!BJPlayerView.this.isPlayingAd && !BJPlayerView.this.isAdViewShowing && BJPlayerView.this.enableSeekCtrl) {
//                    BJPlayerView.this.onProgressSlide(-var8 / (float)mVideoWidth);
//                }
//            } else
            if (!M) {

                float var9 = var7 / (float) mSurface.getMeasuredHeight();

                if (this.N) {
                    onVolumeSlide(var9);
                } else {
                    onBrightnessSlide(var9);
                }

                return true;
            }

            return super.onScroll(var1, var2, var3, var4);
        }


    }


    private void onBrightnessSlide(float var1) {
        if (this.brightness < 0.0F) {
            this.brightness = getWindow().getAttributes().screenBrightness;
            if (this.brightness <= 0.0F) {
                this.brightness = 0.5F;
            } else if (this.brightness < 0.01F) {
                this.brightness = 0.01F;
            }
        }

        WindowManager.LayoutParams var2 = getWindow().getAttributes();
        var2.screenBrightness = this.brightness + var1;
        if (var2.screenBrightness > 1.0F) {
            var2.screenBrightness = 1.0F;
        } else if (var2.screenBrightness < 0.01F) {
            var2.screenBrightness = 0.01F;
        }

        showBrightnessSlide((int) (var2.screenBrightness * 100.0F));

        getWindow().setAttributes(var2);

    }

    private void showBrightnessSlide(int i) {
        mLlLiveCenterDialog.setVisibility(View.VISIBLE);

        mImgLiveCenterDialog.setImageResource(R.drawable.bjplayer_ic_brightness);
        mTvLiveCenterDialog.setText(i + "%");


    }

    private void dismissBrightnessSlide() {
        mLlLiveCenterDialog.setVisibility(View.GONE);

    }

    private void onVolumeSlide(float var9) {


    }

    @Override
    protected void requestLayout(boolean isLandscape) {
        super.requestLayout(isLandscape);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlPlayLayout.getLayoutParams();
        if (isLandscape) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            layoutParams.width = Utils.getScreenWidthPixels(this);
            layoutParams.height = layoutParams.width * 9 / 16;
        }
        rlPlayLayout.setLayoutParams(layoutParams);
    }
}
