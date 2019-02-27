package com.huatu.teacheronline.direct;//package com.huatu.teacheronline.direct;
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.ActivityInfo;
//import android.content.res.Configuration;
//import android.graphics.PixelFormat;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.PowerManager;
//import android.support.v4.app.Fragment;
//import android.view.MotionEvent;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//import android.webkit.WebView;
//import android.widget.AdapterView;
//import android.widget.ExpandableListView;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RadioButton;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.artifex.mupdflib.FilePicker;
//import com.artifex.mupdflib.MuPDFCore;
//import com.artifex.mupdflib.MuPDFPageAdapter;
//import com.artifex.mupdflib.MuPDFReaderView;
//import com.baijia.baijiashilian.liveplayer.ViESurfaceViewRenderer;
//import com.baijia.player.playback.LivePlaybackSDK;
//import com.baijia.player.playback.PBRoom;
//import com.baijia.player.playback.mocklive.OnPlayerListener;
//import com.baijiahulian.common.networkv2.HttpException;
//import com.baijiahulian.livecore.LiveSDK;
//import com.baijiahulian.livecore.context.LPConstants;
//import com.baijiahulian.livecore.context.LPError;
//import com.baijiahulian.livecore.context.LiveRoom;
//import com.baijiahulian.livecore.context.OnLiveRoomListener;
//import com.baijiahulian.livecore.launch.LPLaunchListener;
//import com.baijiahulian.livecore.models.imodels.ILoginConflictModel;
//import com.baijiahulian.livecore.models.imodels.IMediaModel;
//import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;
//import com.baijiahulian.player.BJPlayerView;
//import com.baijiahulian.player.bean.SectionItem;
//import com.baijiahulian.player.bean.VideoItem;
//import com.baijiahulian.player.playerview.BJCenterViewPresenter;
//import com.gensee.pdu.GSDocView;
//import com.gensee.utils.StringUtil;
//import com.greendao.DaoUtils;
//import com.greendao.DirectBean;
//import com.huatu.teacheronline.BaseActivity;
//import com.huatu.teacheronline.R;
//import com.huatu.teacheronline.direct.adapter.CourseWareAdapter;
//import com.huatu.teacheronline.direct.adapter.DirectClassPdfAdapter;
//import com.huatu.teacheronline.direct.bean.HideSoftInputEvent;
//import com.huatu.teacheronline.direct.bean.NetWorkChangeEvent;
//import com.huatu.teacheronline.direct.bean.PdfBean;
//import com.huatu.teacheronline.direct.bean.WithdrawalBean;
//import com.huatu.teacheronline.direct.fragment.ChatFragment;
//import com.huatu.teacheronline.direct.fragment.MessageListFragment;
//import com.huatu.teacheronline.direct.fragment.PPTFragment;
//import com.huatu.teacheronline.direct.manager.PdfDownloadManager;
//import com.huatu.teacheronline.direct.manager.RecodeRequestFailureManager;
//import com.huatu.teacheronline.direct.receiver.NetworkChangeReceiver;
//import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
//import com.huatu.teacheronline.engine.SendRequest;
//import com.huatu.teacheronline.utils.CommonUtils;
//import com.huatu.teacheronline.utils.DebugUtil;
//import com.huatu.teacheronline.utils.GsonUtils;
//import com.huatu.teacheronline.utils.NetWorkUtils;
//import com.huatu.teacheronline.utils.StringUtils;
//import com.huatu.teacheronline.utils.ToastUtils;
//import com.huatu.teacheronline.widget.AppraiseAlertDialog;
//import com.huatu.teacheronline.widget.ChooseSpeedPopwindows;
//import com.huatu.teacheronline.widget.CustomAlertDialog;
//import com.huatu.teacheronline.widget.bjywidget.BjyBottomViewPresenter;
//import com.joanzapata.pdfview.PDFView;
//import com.joanzapata.pdfview.listener.OnPageChangeListener;
//import com.orhanobut.logger.Logger;
//import com.umeng.analytics.MobclickAgent;
//
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.File;
//import java.lang.ref.WeakReference;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import cn.xiaoneng.coreapi.ChatParamsBody;
//import cn.xiaoneng.uiapi.Ntalker;
//import me.drakeet.materialdialog.MaterialDialog;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.functions.Action1;
//import rx.functions.Func1;
//
///**
// * 直播在线  直播播放页 展示互动直播
// * Created by ply on 2016/1/12.
// */
//public class LiveActivityForBjysdk extends BaseActivity implements OnLiveRoomListener, OnPlayerListener, GSDocView.OnDocViewEventListener,
//        OnPageChangeListener, FilePicker.FilePickerSupport {
//    private String TAG = "LiveActivityForBjysdk";
//    private String settingid1 = "kf_10092_1513839603881";
//    private RelativeLayout rl_main_left_player;
//    private RadioButton tv_jianjie, tv_kebiao, tv_hudong, tv_chanjian;
//    private WebView webview;
//    private RelativeLayout rl_deatil_waiting;
//    private ImageView iv_icon;
//    private ImageView iv_screen;
//
//    //listview
//    private View loadView;
//    // 均匀旋转动画
//    private ExpandableListView listview;
//    private CourseWareAdapter directClassScheduleAdapter;
//    private ArrayList<DirectBean> directBeanListForClassSchedule = new ArrayList<>();
//
//    private DirectBean directBean;
//    private ObtatinDataListener obtatinDataListener;
//    private CustomAlertDialog mCustomLoadingDialog;
//    private boolean isScreen = false;
//    private int tab = 2;//1 讲义 2课表 3 互动 4 常见问题
//    private boolean isPPT = true;
//    private boolean isVideoShow = false;//是否有视频直播
//    private SimpleDateFormat simpleDateFormat;
//    private String nickName;
//    private PowerManager.WakeLock m_wakeLock;
//    //    private List<DirectBean> data;
//    public int position;//传过来课程表位置
//    private String uid;
//    //讲义相关
//    private PDFView pdfView;
//    private int pageNumber;
//    private boolean isShowJy;
//    private DaoUtils daoUtils;
//    private int wherefrom; //0 表示从课程详情进入 需要指定播放，2表示我的课程进入
//    private boolean isLocaleData = false;
//
//    public int playNumbe;//最后播放的视频行数转化类型
//    private int playrow = 0;//根据number查找到对应的行数
//    private ImageView ib_main_right_player;
//    private ImageView img_customer_service;
//
//    private MuPDFCore core;
//    private int totalPageCount;
//    private int lastPagePosition;
//    private boolean isFinished;
//    private MuPDFReaderView mDocView;
//    private LinearLayout ll_content_play;
//    private String mFileName;
//
//    private ListView lv_pdf;
//    private DirectClassPdfAdapter directClassPdfAdapter;
//    private RelativeLayout ll_no_notes;
//    private ArrayList<PdfBean> directBeanListPDFForClassSchedule = new ArrayList<PdfBean>();
//    private ObtatinPdfListener obtatinPdfListener;
//
//    private RelativeLayout rl_main_left_pdf;
//    private boolean isShowPdf = false;
//    private String account;
//
//    private String joinTime;//当前视频开始时间
//    private String leavetime;//当前视频离开时间
//    private LiveRoom mLiveRoom;
//
//    @BindView(R.id.ll_live_container)
//    LinearLayout mSurfaceContainer;
//    @BindView(R.id.fra_live_container)
//    FrameLayout mFra_container;
//
//    //    @BindView(R.id.playview_live_playback)
//    BJPlayerView mPlayerView;
//    @BindView(R.id.fra_live_ppt)
//    FrameLayout mFraLivePpt;
//    @BindView(R.id.fra_live_chat)
//    FrameLayout mFraLiveChat;
//    @BindView(R.id.fra_live_touch)
//    FrameLayout mFraLiveTouch;
//    @BindView(R.id.rl_play_bottom_)
//    RelativeLayout mRelPlayBackBottom;
//    @BindView(R.id.ll_playback_top)
//    LinearLayout mLlPlayBackTop;
//    @BindView(R.id.fra_playback_center)
//    FrameLayout mFraPlayBackCenter;
//    @BindView(R.id.tv_speed)
//    TextView tv_speed;
//
//    PBRoom mRoom = null;
//    private boolean mIsPlayback;
//    private BjyBottomViewPresenter mBjyBottomViewPresenter;
//    private ChooseSpeedPopwindows chooseSpeedPopwindows;
//    private int speedType;
//    private String mPresenteUserId;
//    private boolean mOffLinePlayBack;
//    private SurfaceView mSurface;
//    private MaterialDialog mNetWorkDialog;
//    private NetworkChangeReceiver mNetworkChangeReceiver;
//    private boolean isFirstRegister = true;
//    private PPTFragment mPptFragment;
//    private boolean mInBack;
//
//
//    private DirectBean recodingInfo;
//
//    @Override
//    public void onCreate(Bundle arg0) {
//        super.onCreate(arg0);
//
//        mNetworkChangeReceiver = new NetworkChangeReceiver();
//
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
//        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
//
//        registerReceiver(mNetworkChangeReceiver, intentFilter);
//
//    }
//
//    @Override
//    public void initView() {
//        setContentView(R.layout.activity_live);
//        ButterKnife.bind(this);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(DownManageActivity.ACTION_REFRASH);
//        setIntentFilter(intentFilter);
//        uid = CommonUtils.getSharedPreferenceItem(null, com.huatu.teacheronline.utils.UserInfo.KEY_SP_USERID, "");
//        account = CommonUtils.getSharedPreferenceItem(null, com.huatu.teacheronline.utils.UserInfo.KEY_SP_ACCOUNT, "");
//        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//        m_wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "cn");
//        m_wakeLock.acquire(); //设置保持唤醒
//        daoUtils = DaoUtils.getInstance();
//        nickName = CommonUtils.getSharedPreferenceItem(null, com.huatu.teacheronline.utils.UserInfo.KEY_SP_NICKNAME, "");
//        if (StringUtil.isEmpty(nickName)) {
//            nickName = CommonUtils.getSharedPreferenceItem(null, com.huatu.teacheronline.utils.UserInfo.KEY_SP_ACCOUNT, "1");
//        }
//        directBean = (DirectBean) getIntent().getSerializableExtra("DirectBean");
//        Logger.e("DataFromBundle : " + GsonUtils.toJson(directBean));
//
//        directBeanListForClassSchedule = DataStore_Direct.directDatailList;
//        position = getIntent().getIntExtra("position", 0);
//        wherefrom = getIntent().getIntExtra("wherefrom", 0);
//        int key = getIntent().getIntExtra("key", 0);//判断是否从24小时直播进入 0不是 1是
////        if (key==1){
////            settingid1="kf_10092_1515490706283";
////        }else{
////            settingid1="kf_10092_1513839603881";
////        }
//        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//        mCustomLoadingDialog = new CustomAlertDialog(LiveActivityForBjysdk.this, R.layout.dialog_loading_custom);
//        tv_speed.setOnClickListener(this);
//
//        rl_main_left_player = (RelativeLayout) findViewById(R.id.rl_main_left_player);
//        tv_jianjie = (RadioButton) findViewById(R.id.tv_jianjie);
//        tv_kebiao = (RadioButton) findViewById(R.id.tv_kebiao);
//        tv_chanjian = (RadioButton) findViewById(R.id.tv_chanjian);
//        tv_hudong = (RadioButton) findViewById(R.id.tv_hudong);
//        webview = (WebView) findViewById(R.id.webview);
//
//        rl_deatil_waiting = (RelativeLayout) findViewById(R.id.rl_deatil_waiting);
//        ll_content_play = (LinearLayout) findViewById(R.id.ll_content_play);
//        rl_main_left_pdf = (RelativeLayout) findViewById(R.id.rl_main_left_pdf);
//        iv_icon = (ImageView) findViewById(R.id.iv_icon);
//        iv_screen = (ImageView) findViewById(R.id.iv_directScreen);
//        img_customer_service = (ImageView) findViewById(R.id.img_customer_service);
//        img_customer_service.setVisibility(View.GONE);
//        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
//        listview = (ExpandableListView) findViewById(R.id.listview);
//        lv_pdf = (ListView) findViewById(R.id.pdf_listview);//pdf列表
//        ll_no_notes = (RelativeLayout) findViewById(R.id.ll_no_notes);//暂无pdf
//        directClassScheduleAdapter = new CourseWareAdapter(this);
//        directClassScheduleAdapter.seDirectBean(directBean);
//        listview.setAdapter(directClassScheduleAdapter);
//        directClassScheduleAdapter.bind(listview);
//        listview.setVisibility(View.VISIBLE);
//        directClassPdfAdapter = new DirectClassPdfAdapter(this, directBeanListPDFForClassSchedule);
//        lv_pdf.setAdapter(directClassPdfAdapter);
//        ib_main_right_player = (ImageView) findViewById(R.id.ib_main_right_player);
//
//        if ("-1".equals(directBean.getRid())) {
//            ib_main_right_player.setVisibility(View.INVISIBLE);
//        }
//
//        if (directBean.getIs_buy() != null && Integer.parseInt(directBean.getIs_buy()) == 1) {
//            ib_main_right_player.setVisibility(View.VISIBLE);
//            ib_main_right_player.setOnClickListener(this);
//        }
//        pdfView = (PDFView) findViewById(R.id.pdfView);
//
//        if (directBeanListForClassSchedule == null || directBeanListForClassSchedule.size() == 0) {
//            //判断有无网络是否加载本地
//            if (CommonUtils.isNetWorkAvilable()) {
//                loadDirectPlayInfo();
//            } else {
//
//                //todo 离线播放
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
//            }
//        } else {
//            isLocaleData = false;
//            iniFristData(isLocaleData);
//        }
//
//        img_customer_service.setVisibility(View.VISIBLE);
//        tab = 2;
//        pdfView.setVisibility(View.GONE);
//        listview.setVisibility(View.VISIBLE);
//
//        chooseSpeedPopwindows = new ChooseSpeedPopwindows(this);
//        getLivePDFForClassSchedule();
//        //修复某些机器上surfaceView导致的闪黑屏的bug
//        getWindow().setFormat(PixelFormat.TRANSLUCENT);
//    }
//
//    public void setZOrderMediaOverlay(View view, boolean overlay) {
//        if (view instanceof SurfaceView) {
//            ((SurfaceView) view).setZOrderMediaOverlay(overlay);
//        } else if (view instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                setZOrderMediaOverlay(((ViewGroup) view).getChildAt(i), overlay);
//            }
//        }
//
//
//    }
//
//
//    public void initLiveRoom(final DirectBean zDirectBean, int position, boolean playback) {
//        Logger.d("liveRoom directBean:" + GsonUtils.toJson(zDirectBean));
//        mIsPlayback = zDirectBean.getVideo_status().equals("2");
//        showAppraiseDialog(position);
//
//
//        LivePlaybackSDK.deployType = LPConstants.LPDeployType.Product;
//
//        quitLiveRoom();
//        rl_deatil_waiting.setVisibility(View.VISIBLE);
////        joinTime = StringUtils.getNowTime();
//
//        if (mIsPlayback) {
//            //playback
//            if (StringUtils.isEmpty(zDirectBean.getRoom_id())) {
//                ToastUtils.showToast("找不到房间号!");
//                return;
//            }
////             <!--app:aspect_ratio="fit_parent_4_3"-->
////                <!--app:bottom_controller="@layout/playback_empty"-->
////                <!--app:center_controller="@layout/playback_empty"-->
////                <!--app:top_controller="@layout/playback_empty">-->
//
//            mPlayerView = new BJPlayerView(this);
//            mPlayerView.removeView(mPlayerView.getTopView());
//            mPlayerView.removeView(mPlayerView.getBottomView());
//            mPlayerView.removeView(mPlayerView.getCenterView());
////            mFra_container.addView(mPlayerView, 0);
//
//
//            //开启记忆播放
//            mPlayerView.setMemoryPlayEnable(true);
//
//            mFraLivePpt.setVisibility(View.VISIBLE);
//            mSurfaceContainer.setVisibility(View.GONE);
////            mPlayerView.setVisibility(View.GONE);
//            mRelPlayBackBottom.setVisibility(View.VISIBLE);
//            iv_screen.setVisibility(View.GONE);
//
////            rl_fullScreenBack.setVisibility(View.GONE);
//
//
//            mBjyBottomViewPresenter = new BjyBottomViewPresenter(mRelPlayBackBottom, this);
//            mPlayerView.setBottomPresenter(mBjyBottomViewPresenter);
////        player.setBottomPresenter(new BJBottomViewPresenter(player.getBottomView()));
//            mPlayerView.setCenterPresenter(new BJCenterViewPresenter(mFraPlayBackCenter));
//
//
//            //回放离线播放
////            DirectBean directQuery = daoUtils.queryDirectBeanForPlayBack(uid, zDirectBean.getRoom_id(), zDirectBean.getSession_id(), zDirectBean.getNumber());
////            if (directQuery != null) {
////                zDirectBean.setDown_status(directQuery.getDown_status());
////                zDirectBean.setLocalPath(directQuery.getLocalPath());
////                zDirectBean.setStart(directQuery.getStart());
////                zDirectBean.setEnd(directQuery.getEnd());
////            }
//
//            String localpath = zDirectBean.getLocalPath();
//
//            mOffLinePlayBack = false;
//
//            if (!StringUtil.isEmpty(zDirectBean.getDown_status()) && Integer.parseInt(zDirectBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_COMPLETE
//                    && !StringUtil.isEmpty(localpath)) {
//                String[] split = localpath.split(";");
//                if (split.length == 2) {
//                    File videoFile = new File(split[0]);
//                    File signalFile = new File(split[1]);
//                    if (videoFile.exists() && signalFile.exists()) {
//
//                        mRoom = LivePlaybackSDK.newPlayBackRoom(this, Long.parseLong(zDirectBean.getRoom_id()),
//                                videoFile.getAbsolutePath(),
//                                signalFile.getAbsolutePath());
//                        mOffLinePlayBack = true;
//                    }
//
//
//                }
//
//
//            }
//
//            Logger.d("liveRoom playback path:" + localpath + "   " + mOffLinePlayBack);
//
////            if (!StringUtil.isEmpty(zDirectBean.getDown_status()) && Integer.parseInt(zDirectBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_COMPLETE
////                    && !StringUtil.isEmpty(localpath) && new File(localpath).exists()) {
////                File videoFile = new File(localpath);
////                String sigleName = "s_" + videoFile.getName().substring(0, videoFile.getName().length() - 4);
////                String singleFile = FileUtils.getBjyVideoDiskCacheDir() + sigleName;
////                mRoom = LivePlaybackSDK.newPlayBackRoom(this, Long.parseLong(zDirectBean.getRoom_id()), directBean.getLocalPath(), singleFile);
////                mOffLinePlayBack = true;
////
////            } else {
//            if (!mOffLinePlayBack) {
//                if (StringUtils.isEmpty(zDirectBean.getSession_id())) {
//                    mRoom = LivePlaybackSDK.newPlayBackRoom(this, Long.parseLong(zDirectBean.getRoom_id()), zDirectBean.getBjyhftoken());
//
//                } else {
//                    mRoom = LivePlaybackSDK.newPlayBackRoom(this, Long.parseLong(zDirectBean.getRoom_id()), Long.parseLong(zDirectBean.getSession_id()), zDirectBean.getBjyhftoken());
//                }
//            }
//
////            }
//
//            mRoom.setOnLiveRoomListener(new OnLiveRoomListener() {
//                @Override
//                public void onError(LPError lpError) {
////                    addRecordTime();
//                    Logger.e("liveRoom playback" + "code" + lpError.getCode() + " msg:" + lpError.getMessage());
//                }
//            });
//
//            mRoom.enterRoom(new LPLaunchListener() {
//                @Override
//                public void onLaunchSteps(int step, int totalStep) {
////                    Logger.e("liveRoom launchStep:" + (step * 100 / totalStep) + "%");
//                }
//
//                @Override
//                public void onLaunchError(LPError lpError) {
//                    ToastUtils.showToast(R.string.notice_bjy_enter_error + lpError.getMessage());
//
//                }
//
//                @Override
//                public void onLaunchSuccess(LiveRoom liveRoom) {
//                    Logger.d("liveRoom onLaunchSuccess");
//                    recodingInfo = zDirectBean;
//                    joinTime = StringUtils.getNowTime();
//                    rl_deatil_waiting.setVisibility(View.GONE);
//                    if (!mInBack) {
//                        mPlayerView.playVideo();
//                        Logger.d("liveActivity playVideo mInBack = " + mInBack);
//                    }
//
//
//                    setZOrderMediaOverlay(mPlayerView, false);
//                    setZOrderMediaOverlay(mPptFragment.getView(), true);
//
//
//                }
//            });
//
//            mRoom.bindPlayerView(mPlayerView);
//            mRoom.setOnPlayerListener(this);
//            mPptFragment = PPTFragment.newInstance(mRoom);
//
//
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.fra_live_ppt, mPptFragment, PPTFragment.class.getName())
//                    .commitAllowingStateLoss();
//
//            MessageListFragment messageFragment = MessageListFragment.newInstance(mRoom);
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.fra_live_chat, messageFragment, MessageListFragment.class.getName())
//                    .commitAllowingStateLoss();
//
//
////            mAm = (AudioManager) getSystemService(AUDIO_SERVICE);
//
//
//        } else {
//
//            if (StringUtils.isEmpty(zDirectBean.getStudent_code())) {
//                ToastUtils.showToast("找不到邀请码!");
//                return;
//            }
////            rl_fullScreenBack.setVisibility(View.VISIBLE);
//            iv_screen.setVisibility(View.VISIBLE);
//            mFraLivePpt.setVisibility(View.VISIBLE);
////                    mSurfaceSpeaker.setVisibility(View.GONE);
////            mPlayerView.setVisibility(View.GONE);
//            mSurfaceContainer.setVisibility(View.GONE);
//            mRelPlayBackBottom.setVisibility(View.GONE);
//
//            LiveSDK.enterRoom(this, zDirectBean.getStudent_code(), nickName, new LPLaunchListener() {
//                @Override
//                public void onLaunchSteps(int i, int i1) {
//
//                }
//
//                @Override
//                public void onLaunchError(LPError lpError) {
//                    ToastUtils.showToast(R.string.notice_bjy_enter_error + lpError.getMessage());
//                }
//
//                @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//                @Override
//                public void onLaunchSuccess(LiveRoom liveRoom) {
//
//                    mLiveRoom = liveRoom;
//
//                    mLiveRoom.getObservableOfLoginConflict().observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(new Action1<ILoginConflictModel>() {
//                                @Override
//                                public void call(ILoginConflictModel iLoginConflictModel) {
//                                    //用户被踢出
//                                    ToastUtils.showToast(R.string.notice_bjy_kik_out);
//
//                                }
//                            });
//
//                    mLiveRoom.setOnLiveRoomListener(LiveActivityForBjysdk.this);
//                    rl_deatil_waiting.setVisibility(View.GONE);
//                    recodingInfo = zDirectBean;
//                    joinTime = StringUtils.getNowTime();
//
//                    mPptFragment = PPTFragment.newInstance(mLiveRoom);
//
////                    boolean sdkValid = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
////                    boolean canUse = true && sdkValid;
////                    mPptFragment.setAnimPPTEnable(canUse);
//
//                    setZOrderMediaOverlay(mPptFragment.getView(), false);
//
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .add(R.id.fra_live_ppt, mPptFragment, PPTFragment.class.getName())
//                            .commitAllowingStateLoss();
//
//                    mSurface = ViESurfaceViewRenderer.CreateRenderer(LiveActivityForBjysdk.this, true);
//                    mSurface.setZOrderMediaOverlay(true);
////                    mSurface.setZOrderOnTop(true);
//                    mSurfaceContainer.removeAllViews();
//                    mSurfaceContainer.addView(mSurface);
//
//
//                    mLiveRoom.getSpeakQueueVM().requestActiveUsers();
//
//                    mLiveRoom.getSpeakQueueVM().getObservableOfActiveUsers()
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(new LPErrorPrintSubscriber<List<IMediaModel>>() {
//                                @Override
//                                public void call(List<IMediaModel> iMediaModels) {
//                                    Logger.d("liveRoom presenterUser:" + GsonUtils.toJson(mLiveRoom.getPresenterUser()));
//                                    mPresenteUserId = mLiveRoom.getPresenterUser().getUserId();
//                                    if (!StringUtils.isEmpty(mPresenteUserId) && !mInBack) {
//                                        mLiveRoom.getPlayer().playVideo(mPresenteUserId, mSurface);
//                                    }
//
//                                }
//                            });
//
//                    mLiveRoom.getSpeakQueueVM().getObservableOfMediaNew()
//                            .mergeWith(mLiveRoom.getSpeakQueueVM().getObservableOfMediaChange())
//                            .mergeWith(mLiveRoom.getSpeakQueueVM().getObservableOfMediaClose())
//                            .filter(new Func1<IMediaModel, Boolean>() {
//                                @Override
//                                public Boolean call(IMediaModel iMediaModel) {
//                                    return !isTeacherOrAssistant() && iMediaModel.getUser().getType() == LPConstants.LPUserType.Teacher;
//                                }
//                            })
//                            .throttleLast(500, TimeUnit.MILLISECONDS)
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(new LPErrorPrintSubscriber<IMediaModel>() {
//                                @Override
//                                public void call(IMediaModel iMediaModel) {
//                                    if (!mLiveRoom.isClassStarted()) {
//                                        return;
//                                    }
//
//                                    if (iMediaModel.isAudioOn()) {
//                                        Logger.d("liveRoom audio on");
//                                    } else {
//                                        Logger.d("liveRoom audio off");
//                                        if (!StringUtils.isEmpty(mPresenteUserId) && !mInBack) {
//                                            mLiveRoom.getPlayer().playVideo(mPresenteUserId, mSurface);
//                                        }
//                                    }
//                                    if (iMediaModel.isVideoOn()) {
//                                        Logger.d("liveRoom video on");
//                                    } else {
//                                        Logger.d("liveRoom video off");
//                                        if (!StringUtils.isEmpty(mPresenteUserId) && !mInBack) {
//                                            mLiveRoom.getPlayer().playVideo(mPresenteUserId, mSurface);
//                                        }
//                                    }
//
//
//                                }
//                            });
//
//
//                    ChatFragment chatFragment = ChatFragment.newInstance(mLiveRoom);
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .add(R.id.fra_live_chat, chatFragment, ChatFragment.class.getName())
//                            .commitAllowingStateLoss();
//
//
//                }
//            });
//
//
//        }
//
//
//    }
//
//    private boolean isTeacherOrAssistant() {
//        return mLiveRoom.getCurrentUser().getType() == LPConstants.LPUserType.Teacher ||
//                mLiveRoom.getCurrentUser().getType() == LPConstants.LPUserType.Assistant;
//    }
//
//
//    private void quitLiveRoom() {
//        isPPT = true;
//        changeDisplayContent();
//        addRecordTime();
//
//
//        if (mPlayerView != null) {
//            if (mPlayerView.isPlaying()) {
//                mPlayerView.pause();
//                mPlayerView.onDestroy();
//            }
//
//            mFra_container.removeView(mPlayerView);
//        }
//
//
//        if (mLiveRoom != null) {
//            if (!StringUtils.isEmpty(mPresenteUserId)) {
//                mLiveRoom.getPlayer().playAVClose(mPresenteUserId);
//            }
//
//            mLiveRoom.quitRoom();
//            mLiveRoom = null;
//
//        }
//
//        if (mRoom != null) {
//            mRoom.quitRoom();
//            mRoom = null;
//        }
//        Fragment pptFragment = getSupportFragmentManager().findFragmentByTag(PPTFragment.class.getName());
//        Fragment chatFragment = getSupportFragmentManager().findFragmentByTag(ChatFragment.class.getName());
//        Fragment messageFragment = getSupportFragmentManager().findFragmentByTag(MessageListFragment.class.getName());
//        if (pptFragment != null && pptFragment.isAdded()) {
//            getSupportFragmentManager().beginTransaction().remove(pptFragment).commitAllowingStateLoss();
//
//        }
//        if (chatFragment != null && chatFragment.isAdded()) {
//            getSupportFragmentManager().beginTransaction().remove(chatFragment).commitAllowingStateLoss();
//
//        }
//        if (messageFragment != null && messageFragment.isAdded()) {
//            getSupportFragmentManager().beginTransaction().remove(messageFragment).commitAllowingStateLoss();
//
//        }
//    }
//
//    @Override
//    public void setListener() {
//        mFraLiveTouch.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                long time = new Date().getTime();
//                if ((time - touchTime) > 500) {
//                    touchTime = time;
//                    immersive = !immersive;
////            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                    mLlPlayBackTop.setVisibility(immersive ? View.GONE : View.VISIBLE);
//                    iv_icon.setVisibility(immersive ? View.GONE : View.VISIBLE);
//                    if (mIsPlayback) {
//                        mRelPlayBackBottom.setVisibility(immersive ? View.GONE : View.VISIBLE);
//                        if (!isPPT && mPlayerView.getOrientation() == BJPlayerView.VIDEO_ORIENTATION_LANDSCAPE) {
//                            findViewById(R.id.bjplayer_center_video_functions_ll).setVisibility(immersive ? View.GONE : View.VISIBLE);
//                        } else {
//                            findViewById(R.id.bjplayer_center_video_functions_ll).setVisibility(View.GONE);
//                        }
//
//
//                    } else {
//                        iv_screen.setVisibility(immersive ? View.GONE : View.VISIBLE);
//                    }
//
//                    return false;
//
//                }
//
//                return false;
//            }
//        });
//
//
//        rl_main_left_player.setOnClickListener(this);
//        rl_main_left_pdf.setOnClickListener(this);
//        tv_jianjie.setOnClickListener(this);
//        tv_kebiao.setOnClickListener(this);
//        tv_chanjian.setOnClickListener(this);
//        tv_hudong.setOnClickListener(this);
//        iv_screen.setOnClickListener(this);
////        dosView.setOnDocViewClickedListener(this);
////        rl_fullScreenBack.setOnClickListener(this);
//        tv_jianjie.setOnClickListener(this);
//        iv_icon.setOnClickListener(this);
////        videoView.setOnClickListener(this);
//        img_customer_service.setOnClickListener(this);
//        directClassScheduleAdapter.setVideoClickbutton(new CourseWareAdapter.VideoClick() {
//            @Override
//            public void setVideoClick() {
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            }
//        });
////        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
////            @Override
////            public void onScrollStateChanged(AbsListView view, int scrollState) {
////                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
////                    if (isLoadEnd) {
////                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
////                            listview.addFooterView(loadView);
////                            loadIcon.startAnimation(refreshingAnimation);
////                            loadDirectClasSchedule(false);
////                        }
////                    }
////                }
////            }
////
////            @Override
////            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
////            }
////        });
//
////        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                if ("0".equals(directBeanListForClassSchedule.get(position).getVideo_status())) {//正在直播
////                    initRtComp(position);
////                } else {
////                    ToastUtils.showToast("直播未开始");
////                }
////            }
////        });
//        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                PdfBean pdfBean = directBeanListPDFForClassSchedule.get(position);
//                String[] split = pdfBean.getFileUrl().split("/");
//                String fileName = split[split.length - 1];
//                String path = Environment.getExternalStorageDirectory() + "/jy/" + fileName;
//
//                switch (pdfBean.getState()) {
//                    case 1:
//                        pdfView.setVisibility(View.VISIBLE);
//                        lv_pdf.setVisibility(View.GONE);
//                        rl_main_left_pdf.setVisibility(View.VISIBLE);
//                        isShowPdf = true;
//                        initPdfView(path);
//                        break;
//                    case 2:
//                        PdfDownloadManager.getInstance().cancelWaitTask(pdfBean);
//                        break;
//
//                    case 4:
//                        directClassScheduleAdapter.showCustomToast("该下载任务无法取消!");
//                        break;
//                    default:
//                        PdfDownloadManager.getInstance().addDownloadTask(pdfBean);
//                        break;
//
//                }
//
//
//            }
//        });
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onPdfDownloadProgressEvent(PdfBean info) {
//        int index = directBeanListPDFForClassSchedule.indexOf(info);
//        directBeanListPDFForClassSchedule.set(index, info);
//        directClassScheduleAdapter.notifyDataSetChanged();
//
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.rl_main_left:
//            case R.id.rl_main_left_player:
//                back();
//                break;
////            case R.id.img_direct_dealite_face:
////                if (mGridView.getVisibility() == View.GONE) {
////                    mGridView.setVisibility(View.VISIBLE);
////                } else {
////                    mGridView.setVisibility(View.GONE);
////                }
////                break;
//            case R.id.img_customer_service:
//                MobclickAgent.onEvent(this, "consultationOnClik");
////                H5DetailActivity.newIntent(this, "咨询", directBean.getCustomer());
//
//                if (directBean.getRid().equals("-1")) {
//                    settingid1 = "kf_10092_1515490706283";
//                } else {
//                    settingid1 = "kf_10092_1513839603881";
//                }
//
//
//                if (directBeanListForClassSchedule != null && directBeanListForClassSchedule.size() != 0) {
//
//                    String getbranchschoolid = directBeanListForClassSchedule.get(position).getbranchschoolid();
//
//                    if (!StringUtils.isEmpty(getbranchschoolid)) {
//                        settingid1 = getbranchschoolid;
//                    }
//
//                }
//
////                settingid1 = directBean.getbranchschoolid();
////                if (directBean.getRid().equals("-1")) {
////                    settingid1 = "kf_10092_1515490706283";
////                } else {
////                    settingid1 = "kf_10092_1513839603881";
////                }
//                //小能登录
//                Ntalker.getBaseInstance().login(uid, CommonUtils.getSharedPreferenceItem(null, com.huatu.teacheronline.utils.UserInfo.KEY_SP_ACCOUNT, ""), 0);
//                ChatParamsBody chatparams = new ChatParamsBody();
//                chatparams.headurl = CommonUtils.getSharedPreferenceItem(null, com.huatu.teacheronline.utils.UserInfo.KEY_SP_FACEPATH, "");
//                Ntalker.getBaseInstance().startChat(this, settingid1, "华图客服", chatparams, TestChatActivity.class);
//                break;
//            case R.id.ib_main_right:
//            case R.id.ib_main_right_player:
//                if (directBean.getRid().equals("-1")) {
//                    showToast("24小时直播不提供下载服务!");
//
//                    return;
//                }
//
//
//                if (directBeanListForClassSchedule != null && directBeanListForClassSchedule.size() > 0) {
//                    DownManageActivity.newIntent(LiveActivityForBjysdk.this, directBeanListForClassSchedule);
//                }
//                break;
//            case R.id.videoView://视频点击事件
//                if (directBean != null) {
//                    isScreenButtonVisible();
//                }
//                break;
//            case R.id.iv_icon://ppt和视频切换
//                if (directBean == null) {
//                    return;
//                }
//                //直播未开始，不能切换
////                if ("0".equals(directBean.getVideo_status())) {
//                isPPT = !isPPT;
//                changeDisplayContent();
//
//
////                }
//                break;
//            case R.id.iv_directScreen://全屏
//                if (directBean != null) {
//                    if (isScreen) {
//                        isScreen = false;
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
////                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//退出全屏
//                    } else {
//                        isScreen = true;
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
////                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
//                    }
//                }
//                break;
//            case R.id.top_bar_no_titlebar:
//                if (isScreen) {
//                    isScreen = false;
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                } else {
//                    finish();
//                }
//
//
//                break;
//            case R.id.tv_jianjie://pdf
//                if (tab != 1) {
//                    tabJianjie();
//                    ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.class.getName());
//                    if (chatFragment != null) {
//                        chatFragment.back();
//                    }
//                }
//                break;
//            case R.id.tv_kebiao://课表
//                if (tab != 2) {
//                    tabKebiao();
//                    ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.class.getName());
//                    if (chatFragment != null) {
//                        chatFragment.back();
//                    }
//                }
//                break;
//            case R.id.tv_hudong://互动
//                if (tab != 3) {
//                    tabHudong();
//                }
//                break;
//            case R.id.tv_chanjian://常见
//                if (tab != 4) {
//                    tabChanjian();
//                    ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.class.getName());
//                    if (chatFragment != null) {
//                        chatFragment.back();
//                    }
//                }
//                break;
//            case R.id.rl_main_left_pdf:
//                rl_main_left_pdf.setVisibility(View.GONE);
//                pdfView.setVisibility(View.GONE);
//                lv_pdf.setVisibility(View.VISIBLE);
//                if (mDocView != null) {
//                    isShowPdf = false;
//                    ll_content_play.removeView(mDocView);
//                }
//                break;
//
//            case R.id.tv_speed:
//                if (chooseSpeedPopwindows != null) {
//                    chooseSpeedPopwindows.showPopUp(v, this, speedType);
//                }
//                break;
//            case R.id.tv_speed_original:
//                speedType = 0;
////                chooseSpeedPopwindows.setClickViewGone(v);
//                chooseSpeedPopwindows.dissmiss();
//                tv_speed.setText(getString(R.string.speed_original));
//                if (mPlayerView != null) {
//                    mPlayerView.setVideoRate(10);
//                }
//                break;
//            case R.id.tv_speed_quick1:
//                speedType = 1;
////                chooseSpeedPopwindows.setClickViewGone(v);
//                chooseSpeedPopwindows.dissmiss();
//                tv_speed.setText(getString(R.string.speed_quick1));
//                if (mPlayerView != null) {
//                    mPlayerView.setVideoRate(12);
//                }
//                break;
//            case R.id.tv_speed_quick2:
//                speedType = 2;
////                chooseSpeedPopwindows.setClickViewGone(v);
//                chooseSpeedPopwindows.dissmiss();
//                tv_speed.setText(getString(R.string.speed_quick2));
//                if (mPlayerView != null) {
//                    mPlayerView.setVideoRate(15);
//                }
//                break;
//            case R.id.tv_speed_quick3:
//                speedType = 3;
////                chooseSpeedPopwindows.setClickViewGone(v);
//                chooseSpeedPopwindows.dissmiss();
//                tv_speed.setText(getString(R.string.speed_quick3));
//                if (mPlayerView != null) {
//                    mPlayerView.setVideoRate(20);
//                }
//                break;
//
//
//        }
//    }
//
//    private void changeDisplayContent() {
//        mLlPlayBackTop.setVisibility(View.VISIBLE);
//        iv_icon.setVisibility(View.VISIBLE);
//
//        if (mPptFragment != null) {
//            setZOrderMediaOverlay(mPptFragment.getView(), isPPT);
//        }
//        if (mPlayerView != null) {
//            setZOrderMediaOverlay(mPlayerView, !isPPT);
//
//        }
//
//
//        if (isPPT) {
//            iv_icon.setBackgroundResource(R.drawable.ic_tovi);
//            mFraLivePpt.setVisibility(View.VISIBLE);
//            mFraLivePpt.setTranslationX(0);
//            findViewById(R.id.bjplayer_center_video_functions_ll).setVisibility(View.GONE);
////            mSurfaceContainer.setVisibility(View.GONE);
////            mPlayerView.setVisibility(View.GONE);
//            if (mPlayerView != null) {
//                mFra_container.removeView(mPlayerView);
//                mPlayerView.setTranslationX(3000);
//            }
//            mSurfaceContainer.setTranslationX(3000);
//            if (!mIsPlayback) {
//                iv_screen.setVisibility(View.VISIBLE);
////                Fragment pptFragment = getSupportFragmentManager().findFragmentByTag(PPTFragment.class.getName());
//
//            } else {
//
//
//                mRelPlayBackBottom.setVisibility(View.VISIBLE);
//            }
//            mLlPlayBackTop.setVisibility(View.VISIBLE);
//
//
////            top_bar_player.setVisibility(View.VISIBLE);
//        } else {
//            if (mIsPlayback) {
//
//
//                mFraLivePpt.setTranslationX(3000);
//                if (mPlayerView != null) {
//                    mPlayerView.setTranslationX(0);
//                    mFra_container.addView(mPlayerView, 0);
//                }
//            } else {
//                mSurfaceContainer.setTranslationX(0);
//            }
//
//
//            iv_icon.setBackgroundResource(R.drawable.ic_toppt);
//
//            if (mIsPlayback) {
//                if (mPlayerView != null) {
//                    mPlayerView.setVisibility(View.VISIBLE);
//                }
//                iv_screen.setVisibility(View.GONE);
//                mRelPlayBackBottom.setVisibility(View.VISIBLE);
//                if (mPlayerView != null && mPlayerView.getOrientation() == BJPlayerView.VIDEO_ORIENTATION_LANDSCAPE) {
//                    findViewById(R.id.bjplayer_center_video_functions_ll).setVisibility(View.VISIBLE);
//                }
//
////                top_bar_player.setVisibility(View.GONE);
//            } else {
//                findViewById(R.id.bjplayer_center_video_functions_ll).setVisibility(View.GONE);
//                iv_screen.setVisibility(View.VISIBLE);
//                mSurfaceContainer.setVisibility(View.VISIBLE);
//            }
//        }
//    }
//
//    /**
//     * pdf 设置 tab
//     */
//    private void tabJianjie() {
//        tab = 1;
//        img_customer_service.setVisibility(View.VISIBLE);
////        rl_sendMessage.setVisibility(View.GONE);
//        pdfView.setVisibility(View.GONE);
//        listview.setVisibility(View.GONE);
////        lv_chat.setVisibility(View.GONE);
////        mGridView.setVisibility(View.GONE);
//        webview.setVisibility(View.GONE);
//        mFraLiveChat.setVisibility(View.GONE);
//        if (directBean == null || directBeanListForClassSchedule == null || directBeanListForClassSchedule.size() == 0) {
//            return;
//        }
//        if (directBeanListPDFForClassSchedule.size() == 0) {
//            lv_pdf.setVisibility(View.GONE);
//            ll_no_notes.setVisibility(View.VISIBLE);
//        } else {
//            if (isShowPdf) {
//                ll_content_play.addView(mDocView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
//                        .MATCH_PARENT));
//                rl_main_left_pdf.setVisibility(View.VISIBLE);
//                ll_no_notes.setVisibility(View.GONE);
//            } else {
//                lv_pdf.setVisibility(View.VISIBLE);
//                ll_no_notes.setVisibility(View.GONE);
//            }
//        }
////        String fileUrl = directBeanListForClassSchedule.get(position).getNetclass_pdf();
////        loadPDF(fileUrl);
//    }
//
//    /**
//     * 课表 设置 tab
//     */
//    private void tabKebiao() {
//        if (mDocView != null) {
//            ll_content_play.removeView(mDocView);
//        }
//        img_customer_service.setVisibility(View.VISIBLE);
//        tab = 2;
////        rl_sendMessage.setVisibility(View.GONE);
//        rl_main_left_pdf.setVisibility(View.GONE);
//        pdfView.setVisibility(View.GONE);
//        listview.setVisibility(View.VISIBLE);
//        lv_pdf.setVisibility(View.GONE);
////        lv_chat.setVisibility(View.GONE);
//        ll_no_notes.setVisibility(View.GONE);
////        mGridView.setVisibility(View.GONE);
//        mFraLiveChat.setVisibility(View.GONE);
//        if (directBean == null || directBeanListForClassSchedule == null || directBeanListForClassSchedule.size() == 0) {
//            return;
//        }
//        if (directBeanListForClassSchedule != null && directBeanListForClassSchedule.size() > 0) {
////                        listview.addFooterView(loadView);
////                        loadIcon.startAnimation(refreshingAnimation);
////                        listview.setAdapter(directClassScheduleAdapter);
////                        loadDirectClasSchedule(true);
////                    } else {
//            DebugUtil.e("初始化课程列表");
////            View HeaderView = getLayoutInflater().inflate(R.layout.header, listview, false);
////            listview.setPinnedHeader(HeaderView);
////            listview.setAdapter(directClassScheduleAdapter);
////            directClassScheduleAdapter.bind(listview);
////            listview.setOnScrollListener(directClassScheduleAdapter);
//            directClassScheduleAdapter.setSelection(position);
////                        completeRefresh();
//        }
//    }
//
//    /**
//     * 互动 设置 tab
//     */
//    private void tabHudong() {
//        if (mDocView != null) {
//            ll_content_play.removeView(mDocView);
//        }
//        tab = 3;
////        lv_chat.setVisibility(View.VISIBLE);
////        if (directBean != null) {
////            lv_chat.setAdapter(directUserMessgAdapter);
////        }
////        rl_sendMessage.setVisibility(View.VISIBLE);
//        mFraLiveChat.setVisibility(View.VISIBLE);
//
//
//        img_customer_service.setVisibility(View.GONE);
//        webview.setVisibility(View.GONE);
//        rl_main_left_pdf.setVisibility(View.GONE);
//        pdfView.setVisibility(View.GONE);
//        listview.setVisibility(View.GONE);
////        mGridView.setVisibility(View.GONE);
//        lv_pdf.setVisibility(View.GONE);
//        ll_no_notes.setVisibility(View.GONE);
//
//    }
//
//    /**
//     * 常见问题 tab
//     */
//    private void tabChanjian() {
//        if (mDocView != null) {
//            ll_content_play.removeView(mDocView);
//        }
//        img_customer_service.setVisibility(View.VISIBLE);
//        tab = 4;
//        pdfView.setVisibility(View.GONE);
//        listview.setVisibility(View.GONE);
////        mGridView.setVisibility(View.GONE);
//        ll_no_notes.setVisibility(View.GONE);
//        lv_pdf.setVisibility(View.GONE);
////        lv_chat.setVisibility(View.GONE);
//        rl_main_left_pdf.setVisibility(View.GONE);
//        webview.setVisibility(View.VISIBLE);
//        webview.loadUrl(DataStore_Direct.Common_problem);
//
//        mFraLiveChat.setVisibility(View.GONE);
//    }
//
//    public static void newIntent(Activity context, DirectBean directBean, /*ArrayList<DirectBean> directBeanList,*/ int position) {
//        Intent intent = new Intent(context, LiveActivityForBjysdk.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("DirectBean", directBean);
////        bundle.putSerializable("DirectBeanList", directBeanList);
//        intent.putExtras(bundle);
//        intent.putExtra("position", position);
//        context.startActivity(intent);
//    }
//
//    public static void newIntent(Activity context, DirectBean directBean, /*ArrayList<DirectBean> directBeanList,*/ int position, int type) {
//        Intent intent = new Intent(context, LiveActivityForBjysdk.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("DirectBean", directBean);
////        bundle.putSerializable("DirectBeanList", directBeanList);
//        intent.putExtras(bundle);
//        intent.putExtra("position", position);
//        intent.putExtra("wherefrom", type);
//        context.startActivity(intent);
//    }
//
//    public static void newIntent(Activity context, DirectBean directBean, int position, int type, int key) {
//        Intent intent = new Intent(context, LiveActivityForBjysdk.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("DirectBean", directBean);
////        bundle.putSerializable("DirectBeanList", directBeanList);
//        intent.putExtras(bundle);
//        intent.putExtra("position", position);
//        intent.putExtra("wherefrom", type);
//        intent.putExtra("key", key);
//        context.startActivity(intent);
//    }
//
//    @Override
//    public boolean back() {
//        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
//            isScreen = false;
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
////            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
//            ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.class.getName());
//            //判断隐藏表情栏
//            boolean used = false;
//            if (chatFragment != null) {
//                used = chatFragment.back();
//            }
//
//            if (!used) {
//                finish();
//            }
//
//        }
//        return false;
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
//            iv_screen.setBackgroundResource(R.drawable.ic_fullsc_b_g);
//            img_customer_service.setVisibility(View.GONE);
////            rl_sendMessage.setVisibility(View.GONE);
////            top_bar_player.setVisibility(View.GONE);
//            isScreenButtonVisible();
//        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
////            img_customer_service.setVisibility(View.VISIBLE);
//            iv_screen.setBackgroundResource(R.drawable.ic_fullsc_g);
//            findViewById(R.id.bjplayer_center_video_functions_frame_tv).setVisibility(View.GONE);
////            if (isPPT) {
////                top_bar_player.setVisibility(View.VISIBLE);
////            }
//
//
//            if (tab != 3) {
//                img_customer_service.setVisibility(View.VISIBLE);
//            }
//            isScreenButtonVisible();
//        }
//    }
//
//    private boolean immersive = false;
//    private long touchTime;
//
//
//    /**
//     * 评价弹窗
//     *
//     * @param position
//     */
//    private void showAppraiseDialog(int position) {
//        if (directBeanListForClassSchedule.get(position).getIs_last() != null && directBeanListForClassSchedule.get(position).getIs_last()) {
//            AppraiseAlertDialog appraiseAlertDialog = new AppraiseAlertDialog(this, directBeanListForClassSchedule.get(position));
//            appraiseAlertDialog.show();
//            directBeanListForClassSchedule.get(position).setIs_last(false);
//        }
//    }
//
//
//    @Override
//    public boolean onDoubleClicked(GSDocView gsDocView) {
//        return false;
//    }
//
//    @Override
//    public boolean onSingleClicked(GSDocView gsDocView) {
//        isScreenButtonVisible();
//        return true;
//    }
//
//    @Override
//    public boolean onEndHDirection(GSDocView gsDocView, int i, int i1) {
//        return false;
//    }
//
//
//    public void showToast(final String str) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                ToastUtils.showToast(str);
//            }
//        });
//    }
//
//    /**
//     * 隐藏/显示播放屏幕中的按钮
//     */
//    public void isScreenButtonVisible() {
//    }
//
//    /**
//     * 加载播放信息
//     */
//    public void loadDirectPlayInfo() {
//        obtatinDataListener = new ObtatinDataListener(this);
//        SendRequest.getLiveDataForClassSchedule(directBean.getRid(), uid, obtatinDataListener);
//    }
//
//
//    @Override
//    public void performPickFor(FilePicker picker) {
//
//    }
//
//
//    /**
//     * 百家云直播出错状态
//     *
//     * @param lpError
//     */
//    @Override
//    public void onError(LPError lpError) {
////        addRecordTime();
//        Logger.e("liveRoom Error:" + lpError.getMessage());
//        switch ((int) lpError.getCode()) {
//
//
//            case LPError.CODE_ERROR_NETWORK_FAILURE: //无网
//                break;
//            case LPError.CODE_ERROR_NETWORK_MOBILE: //当前网络为mobile
//                break;
//            case LPError.CODE_ERROR_NETWORK_WIFI: //wifi
//                break;
//            case LPError.CODE_ERROR_UNKNOWN: // 未知错误
//                break;
//            case LPError.CODE_ERROR_JSON_PARSE_FAIL: // 数据解析失败
//                break;
//            case LPError.CODE_ERROR_INVALID_PARAMS: // 无效参数
//                break;
//            case LPError.CODE_ERROR_ROOMSERVER_FAILED:  //roomserver登录失败
//                break;
//            case LPError.CODE_ERROR_OPEN_AUDIO_RECORD_FAILED: //打开麦克风失败，采集声音失败
//                break;
//            case LPError.CODE_ERROR_OPEN_AUDIO_CAMERA_FAILED: //打开摄像头失败，采集图像失败
//                break;
//            case LPError.CODE_ERROR_MAX_STUDENT: //人数上限
//                break;
//            case LPError.CODE_ERROR_ROOMSERVER_LOSE_CONNECTION:  // roomserver 连接断开
//                break;
//            case LPError.CODE_ERROR_LOGIN_CONFLICT: // 被踢下线
//                break;
//            case LPError.CODE_ERROR_PERMISSION_DENY: // 权限错误
//                break;
//            case LPError.CODE_RECONNECT_SUCCESS:// 重连成功
//                break;
//            case LPError.CODE_ERROR_STATUS_ERROR:  // 状态错误
//                break;
//            case LPError.CODE_ERROR_MEDIA_SERVER_CONNECT_FAILED: //音视频服务器连接错误
//                break;
//            case LPError.CODE_ERROR_MEDIA_PLAY_FAILED:  //音视频播放失败
//                break;
//            case LPError.CODE_ERROR_CHATSERVER_LOSE_CONNECTION:  // chatserver 连接断开
//                break;
//            case LPError.CODE_ERROR_MESSAGE_SEND_FORBID:  //发言被禁止
//                break;
//            case LPError.CODE_ERROR_VIDEO_PLAY_EXCEED:  // 超出最大播放视频数量
//                break;
//            case LPError.CODE_ERROR_LOGIN_KICK_OUT:  //被踢
//                break;
//            case LPError.CODE_ERROR_FORBID_RAISE_HAND: //举手被禁止
//                break;
//
//        }
//
//    }
//
//
//    //回放 OnPlayerListener
//
//    @Override
//    public void onVideoInfoInitialized(BJPlayerView bjPlayerView, long l, HttpException e) {
//        Logger.d("liveRoom onVideoInfoInitialized:");
//    }
//
//    @Override
//    public void onError(BJPlayerView bjPlayerView, int i) {
//        Logger.e("liveRoom onError:" + i);
//
//
//    }
//
//    @Override
//    public void onUpdatePosition(BJPlayerView bjPlayerView, int i) {
//
//    }
//
//    @Override
//    public void onSeekComplete(BJPlayerView bjPlayerView, int i) {
//
//    }
//
//    @Override
//    public void onSpeedUp(BJPlayerView bjPlayerView, float v) {
//
//    }
//
//    @Override
//    public void onVideoDefinition(BJPlayerView bjPlayerView, int i) {
//
//    }
//
//    @Override
//    public void onPlayCompleted(BJPlayerView bjPlayerView, VideoItem videoItem, SectionItem sectionItem) {
////        addRecordTime();
//    }
//
//    @Override
//    public void onVideoPrepared(BJPlayerView bjPlayerView) {
//        if (mPlayerView != null) {
//            setZOrderMediaOverlay(mPlayerView.getVideoView(), !isPPT);
//        }
//
//
//    }
//
//    @Override
//    public void onPause(BJPlayerView bjPlayerView) {
////        addRecordTime();
//    }
//
//    @Override
//    public void onPlay(BJPlayerView bjPlayerView) {
////        joinTime = StringUtils.getNowTime();
//    }
//
//    private static class ObtatinDataListener extends ObtainDataFromNetListener<ArrayList<DirectBean>, String> {
//        private LiveActivityForBjysdk weak_activity;
//
//        public ObtatinDataListener(LiveActivityForBjysdk activity) {
//            weak_activity = new WeakReference<>(activity).get();
//        }
//
//        @Override
//        public void onStart() {
//            super.onStart();
//            if (weak_activity != null) {
//                weak_activity.mCustomLoadingDialog.show();
//            }
//
//        }
//
//
//        @Override
//        public void onSuccess(final ArrayList<DirectBean> res) {
//            if (weak_activity != null) {
//                weak_activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        weak_activity.flushContent_OnSucess(res);
//                    }
//                });
//            }
//        }
//
//        @Override
//        public void onFailure(final String res) {
//            if (weak_activity != null) {
//                weak_activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        weak_activity.flushContent_OnFailure(res);
//                    }
//                });
//            }
//        }
//    }
//
//    public void flushContent_OnSucess(ArrayList<DirectBean> res) {
//        mCustomLoadingDialog.dismiss();
//        if (res != null) {
//            directBeanListForClassSchedule = res;
//            DataStore_Direct.directDatailList = directBeanListForClassSchedule;
//            iniFristData(isLocaleData);
//        } else {
//            ToastUtils.showToast(getString(R.string.course_not_start));
//        }
//    }
//
//    /**
//     * 初始化首次加载课程表
//     */
//    private void iniFristData(boolean isLocaleData) {
//        if (!isLocaleData) {
//            if (!isFinishing()) {
//                synchronizationLocal(directBeanListForClassSchedule);
//            }
//        }
//
//
//        directClassScheduleAdapter.setDirectBeanList(directBeanListForClassSchedule, position + "", 1);
//        webview.loadDataWithBaseURL(null, directBean.getContent(), "text/html", "UTF-8", null);
////        tv_main_title.setText(directBeanListForClassSchedule.get(position).getTitle());
//
//
//        playNumbe = playrow;
//        //课程详情进入
//        if (wherefrom == 0) {
//            directClassScheduleAdapter.setSelection(position >= 1 ? position - 1 : 0);
////            listview.setSelection(position >= 1 ? position - 1 : 0);
//            CommonUtils.putSharedPreferenceItem(null, directBean.getRid(), String.valueOf(directBeanListForClassSchedule.get(position).getNumber()));
//        } else {
//            if (position == 0) {
//
//                for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
//                    DirectBean directBean = directBeanListForClassSchedule.get(i);
//                    if (directBean.getVideo_status().equals("0") || directBean.getVideo_status().equals("2")) {
//                        position = i;
//                        break;
//                    }
//
//
//                }
//
//            }
//            DirectBean mDirectBean = directBeanListForClassSchedule.get(position);
//
//            if (mDirectBean.getVideoType() == 1 && StringUtils.isEmpty(mDirectBean.getLubourl())) {
//                switch (mDirectBean.getVideo_status()) {
//                    case "1":
//                        ToastUtils.showToast("直播暂未开始");
//
//                        return;
//                    case "3":
//                        ToastUtils.showToast("暂无回放");
//                        return;
//                }
//
//            }
//
//            directClassScheduleAdapter.setSelection(position);
////            listview.setSelection(position);
//            directClassScheduleAdapter.setDirectId(position + "");
//
//
//            if (mDirectBean.getVideoType() == 0 || !StringUtils.isEmpty(mDirectBean.getLubourl()) || !StringUtils.isEmpty(mDirectBean.getZhibourl())) {
//                directClassScheduleAdapter.playVideo(mDirectBean, position);
//                return;
//            }
//
//
//        }
//
//
//        int networkType = NetWorkUtils.getAPNType(this);
//        String netWorkName = "";
//        switch (networkType) {
//            case 0:
//                netWorkName = "无网络";
//                break;
//            case 1:
//                netWorkName = "WIFI网络";
//                break;
//            case 2:
//                netWorkName = "2G网络";
//                break;
//            case 3:
//                netWorkName = "3G网络";
//                break;
//            case 4:
//                netWorkName = "4G网络";
//                break;
//        }
//
//        mIsPlayback = directBeanListForClassSchedule.get(position).getVideo_status().equals("2");
//        if (mIsPlayback || networkType == 1 || networkType == 0) {
//            initLiveRoom(directBeanListForClassSchedule.get(position), position, false);
//        } else {
//            mNetWorkDialog = new MaterialDialog(this);
//            mNetWorkDialog.setTitle("提示")
//                    .setMessage("当前网络为" + netWorkName + ",是否继续播放?")
//                    .setNegativeButton("退出", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            mNetWorkDialog.dismiss();
//                            finish();
//                        }
//                    })
//                    .setPositiveButton("继续播放", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            mNetWorkDialog.dismiss();
//                            initLiveRoom(directBeanListForClassSchedule.get(position), position, false);
//                        }
//                    })
//                    .show();
//        }
//
//
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onNetWorkStateChangeEvent(NetWorkChangeEvent event) {
//
//        //屏蔽刚注册广播时的通知
//        if (isFirstRegister) {
//            isFirstRegister = false;
//            return;
//        }
//        int networkType = NetWorkUtils.getAPNType(this);
//
//        //网络恢复时重新请求失败的听课记录
//        if (networkType != 0) {
//            RecodeRequestFailureManager.getInstance().checkRequestRetry();
//
//        }
//
//        if (mIsPlayback) {
//            return;
//        }
//
//
//        String netWorkName = "";
//        switch (networkType) {
//            case 0:
//                netWorkName = "无网络";
//                break;
//            case 1:
//                netWorkName = "WIFI网络";
//                break;
//            case 2:
//                netWorkName = "2G网络";
//                break;
//            case 3:
//                netWorkName = "3G网络";
//                break;
//            case 4:
//                netWorkName = "4G网络";
//                break;
//        }
//
//
//        if (mNetWorkDialog == null) {
//            mNetWorkDialog = new MaterialDialog(this);
//        }
//
//        mNetWorkDialog.setTitle("提示")
//                .setMessage("当前网络为" + netWorkName + ",是否继续播放?")
//                .setNegativeButton("退出", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mNetWorkDialog.dismiss();
//                        finish();
//                    }
//                })
//                .setPositiveButton("继续播放", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mNetWorkDialog.dismiss();
//                        if (!mIsPlayback && mLiveRoom != null && mLiveRoom.getPresenterUser() != null) {
//                            mPresenteUserId = mLiveRoom.getPresenterUser().getUserId();
//                            if (!StringUtils.isEmpty(mPresenteUserId)) {
//                                mLiveRoom.getPlayer().playVideo(mPresenteUserId, mSurface);
//                            }
//                        }
//
//
//                    }
//                })
//                .show();
//
//
//    }
//
//    public void flushContent_OnFailure(String res) {
//        mCustomLoadingDialog.dismiss();
//        if (SendRequest.ERROR_NETWORK.equals(res)) {
//            ToastUtils.showToast("网络异常,请检查网络");
//        } else if (SendRequest.ERROR_SERVER.equals(res)) {
//            ToastUtils.showToast(getResources().getString(R.string.server_error));
//        }
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        m_wakeLock.acquire(); //设置保持唤醒
//
////        if (mPptFragment != null) {
////            mPptFragment.onStop();
////            setZOrderMediaOverlay(mPptFragment.getView(), isPPT);
////            mPptFragment.onStart();
////
////        }
////
//        if (mIsPlayback && mPlayerView != null) {
//
////            setZOrderMediaOverlay(mPlayerView, !isPPT);
//            mPlayerView.onResume();
//        }
//
//        isPPT = true;
//        changeDisplayContent();
//
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        m_wakeLock.release();//解除保持唤醒
//        if (mIsPlayback && mPlayerView != null) {
//            mPlayerView.onPause();
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mInBack = false;
//        joinTime = StringUtils.getNowTime();
//        if (!mIsPlayback && mLiveRoom != null && mLiveRoom.getPresenterUser() != null) {
//            mPresenteUserId = mLiveRoom.getPresenterUser().getUserId();
//            if (!StringUtils.isEmpty(mPresenteUserId)) {
//                mLiveRoom.getPlayer().playVideo(mPresenteUserId, mSurface);
//            }
//        }
//
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mInBack = true;
//        Logger.d("liveActivity onStop mInBack = " + mInBack);
//
//        addRecordTime();
//
//        if (!mIsPlayback && mLiveRoom != null && mLiveRoom.getPresenterUser() != null) {
//            mPresenteUserId = mLiveRoom.getPresenterUser().getUserId();
//            if (!StringUtils.isEmpty(mPresenteUserId)) {
//                mLiveRoom.getPlayer().playAVClose(mPresenteUserId);
//            }
//        }
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        m_wakeLock.release(); //解除保持唤醒
//
//        quitLiveRoom();
//        if (mNetworkChangeReceiver != null) {
//            unregisterReceiver(mNetworkChangeReceiver);
//
//        }
//
//
//    }
//
//
//    @Override
//    public void onPageChanged(int page, int pageCount) {
//        pageNumber = page;
//    }
//
//    /**
//     * 同步本地的数据
//     *
//     * @param directBeanListForClassSchedule
//     */
//    private void synchronizationLocal(List<DirectBean> directBeanListForClassSchedule) {
//        for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
//            DirectBean directBean = directBeanListForClassSchedule.get(i);
//            if (directBeanListForClassSchedule.get(i).getVideoType() == 0) {
//                if (StringUtil.isEmpty(directBean.getBjyvideoid())) {
//                    continue;
//                }
//                //网课
//                DirectBean directBean1 = null;
//                if (!StringUtils.isEmpty(directBeanListForClassSchedule.get(i).getBjyvideoid())) {
//                    directBean1 = daoUtils.queryDirectBeanForBjyVedioId(uid, directBeanListForClassSchedule.get(i).getBjyvideoid(),
//                            directBeanListForClassSchedule.get(i).getNumber());
//                }
//                if (directBean1 == null && !StringUtils.isEmpty(directBeanListForClassSchedule.get(i).getCcCourses_id())) {
//                    //通过百家云的id查询不到的还需要用CC视频的id查询一次
//                    directBean1 = daoUtils.queryDirectBeanForCCVedioId(uid, directBeanListForClassSchedule.get(i).getCcCourses_id(),
//                            directBeanListForClassSchedule.get(i).getNumber());
//                }
//                if (directBean1 != null) {
//                    directBeanListForClassSchedule.get(i).setDown_status(directBean1.getDown_status());
//                    directBeanListForClassSchedule.get(i).setLocalPath(directBean1.getLocalPath());
//                    directBeanListForClassSchedule.get(i).setStart(directBean1.getStart());
//                    directBeanListForClassSchedule.get(i).setEnd(directBean1.getEnd());
//                }
//            } else if (directBeanListForClassSchedule.get(i).getVideoType() == 1) {
//                DirectBean directOrg = directBeanListForClassSchedule.get(i);
//                if (StringUtil.isEmpty(directBean.getLubourl()) && !StringUtil.isEmpty(directOrg.getRoom_id())) {
//                    DirectBean directQuery = daoUtils.queryDirectBeanForPlayBack(uid, directOrg.getRoom_id(), directOrg.getSession_id(), directOrg.getNumber());
//                    if (directQuery != null) {
//                        directBeanListForClassSchedule.get(i).setDown_status(directQuery.getDown_status());
//                        directBeanListForClassSchedule.get(i).setLocalPath(directQuery.getLocalPath());
//                        directBeanListForClassSchedule.get(i).setStart(directQuery.getStart());
//                        directBeanListForClassSchedule.get(i).setEnd(directQuery.getEnd());
//                    }
//
//
//                }
////                else {
////                    DirectBean directBean1 = daoUtils.queryDirectBeanForGeeneVedioId(uid, directBeanListForClassSchedule.get(i).getLubourl(),
////                            directBeanListForClassSchedule.get(i).getNumber());
////                    if (directBean1 != null) {
////                        directBeanListForClassSchedule.get(i).setDown_status(directBean1.getDown_status());
////                        directBeanListForClassSchedule.get(i).setLocalPath(directBean1.getLocalPath());
////                        directBeanListForClassSchedule.get(i).setStart(directBean1.getStart());
////                        directBeanListForClassSchedule.get(i).setEnd(directBean1.getEnd());
//////                    directBeanListForClassSchedule.get(i).setVideo_status(directBean1.getVideo_status());
////                    }
////                }
//
//            }
//        }
//        //删除rid相同的课程后，同步完数据库之后再加入课时缓存
////        DebugUtil.e("synchronizationLocal:" + DirectPlayDetailsActivityForRtsdk.this.directBean.toString());
//        daoUtils.deletDirectBeanListForRid(LiveActivityForBjysdk.this.directBean.getRid());
//        for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
//            DirectBean directBean = directBeanListForClassSchedule.get(i);
//            directBean.setUserid(uid);
//            directBean.setRid(LiveActivityForBjysdk.this.directBean.getRid());
//            daoUtils.insertOrUpdateDirectBeanCacheClass(directBean);
//            DebugUtil.e("synchronizationLocal:" + directBean.toString());
//        }
//    }
//
//    /**
//     * 接收广播后刷新数据 主要针对下载
//     *
//     * @param context 接受广播的上下文
//     * @param intent  该广播的意图
//     */
//    @Override
//    public void onReceiveBroadCast(Context context, Intent intent) {
//        super.onReceiveBroadCast(context, intent);
//        DirectBean mDirectBean = (DirectBean) intent.getSerializableExtra("DirectBean");
//        DebugUtil.e("onReceiveBroadCast:rt" + mDirectBean.toString());
//        //在下载的时候不要刷新ui
//        if ("200".equals(mDirectBean.getDown_status())) {
//            return;
//        }
//        for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
//            if (directBeanListForClassSchedule.get(i).getVideoType() == 0) {
//                if (directBeanListForClassSchedule.get(i).getBjyvideoid() != null && directBeanListForClassSchedule.get(i).getBjyvideoid().equals(mDirectBean.getBjyvideoid())) {
//                    directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
//                    directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
//                    directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
//                    directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
//                } else if (directBeanListForClassSchedule.get(i).getCcCourses_id() != null && directBeanListForClassSchedule.get(i).getCcCourses_id().equals(mDirectBean.getCcCourses_id())) {
//                    directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
//                    directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
//                    directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
//                    directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
//                }
//            } else {
//                if (!StringUtils.isEmpty(directBeanListForClassSchedule.get(i).getLubourl())) {
//                    if (directBeanListForClassSchedule.get(i).getLubourl().equals(mDirectBean.getLubourl())) {
//                        directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
//                        directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
//                        directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
//                        directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
//                        directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
//                    }
//
//                } else {
//
//                    if (directBeanListForClassSchedule.get(i).getRoom_id().equals(mDirectBean.getRoom_id()) && directBeanListForClassSchedule.get(i).getSession_id().equals(mDirectBean.getSession_id())) {
//
//                        directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
//                        directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
//                        directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
//                        directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
//                        directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
//                    }
//
//                }
//            }
//
//        }
//        directClassScheduleAdapter.notifyDataSetChanged();
//    }
//
//    private void initPdfView(String path) {
//        core = openFile(path);
//        if (core == null) {
//            Toast.makeText(this, "open pdf file failed", Toast.LENGTH_SHORT).show();
////            finish();
//        }
//        totalPageCount = core.countPages();
//        if (lastPagePosition > totalPageCount) {
//            lastPagePosition = totalPageCount;
//            isFinished = true;
//        }
//        if (totalPageCount == 0) {
//            Toast.makeText(this, "PDF file has format error", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//        //one page per screen
//        core.setDisplayPages(1);
//
//        mDocView = new MuPDFReaderView(this) {
//            @Override
//            protected void onMoveToChild(int i) {
//                DebugUtil.e(TAG, "onMoveToChild " + i);
//                super.onMoveToChild(i);
////                mTitle.setText(String.format(" %s / %s ",  i + 1, totalPageCount));
//                if ((i + 1) == totalPageCount) {
//                    isFinished = true;
//                }
//            }
//
//            @Override
//            protected void onTapMainDocArea() {
//                //Log.d(TAG,"onTapMainDocArea");
//            }
//
//            @Override
//            protected void onDocMotion() {
//                //Log.d(TAG,"onDocMotion");
//            }
//
//        };
//        mDocView.setAdapter(new MuPDFPageAdapter(LiveActivityForBjysdk.this, this, core));
//        mDocView.setKeepScreenOn(true);
//        mDocView.setLinksHighlighted(false);
//        mDocView.setScrollingDirectionHorizontal(true);
//
//        ll_content_play.addView(mDocView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        mDocView.setDisplayedViewIndex(lastPagePosition - 1);
////        mTitle.setText(String.format(" %s / %s ", lastPagePosition, totalPageCount));
//    }
//
//    private MuPDFCore openFile(String path) {
//        int lastSlashPos = path.lastIndexOf('/');
//        mFileName = lastSlashPos == -1 ? path
//                : path.substring(lastSlashPos + 1);
//        System.out.println("Trying to open " + path);
//        try {
//            core = new MuPDFCore(this, path);
//            // New file: drop the old outline data
//            //OutlineActivityData.set(null);
////            PDFPreviewGridActivityData.set(null);
//        } catch (Exception e) {
//            System.out.println(e);
//            DebugUtil.e(TAG, e.getMessage());
//            return null;
//        }
//        return core;
//    }
//
//    private static class ObtatinPdfListener extends ObtainDataFromNetListener<ArrayList<PdfBean>, String> {
//        private WeakReference<LiveActivityForBjysdk> weak_activity;
//
//        public ObtatinPdfListener(LiveActivityForBjysdk activity) {
//            weak_activity = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void onStart() {
//            super.onStart();
//            if (weak_activity.get() != null) {
//                weak_activity.get().mCustomLoadingDialog.show();
//            }
//        }
//
//
//        @Override
//        public void onSuccess(final ArrayList<PdfBean> res) {
//            if (weak_activity.get() != null) {
//                weak_activity.get().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        weak_activity.get().flushPdfContent_OnSucess(res);
//                    }
//                });
//            }
//        }
//
//        @Override
//        public void onFailure(final String res) {
//            if (weak_activity.get() != null) {
//                weak_activity.get().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        weak_activity.get().flushPdfContent_OnFailure(res);
//                    }
//                });
//            }
//        }
//
//    }
//
//    public void flushPdfContent_OnSucess(ArrayList<PdfBean> res) {
//        mCustomLoadingDialog.dismiss();
//        if (res != null && res.size() > 0) {
////            tv_main_title.setText(directBean.getTitle());
////            webview.loadDataWithBaseURL(null, directBean.getContent(), "text/html", "UTF-8", null);
//
//            directBeanListPDFForClassSchedule.addAll(res);
//            for (PdfBean pdfBean : directBeanListPDFForClassSchedule) {
//
//                String[] split = pdfBean.getFileUrl().split("/");
//                String fileName = split[split.length - 1];
//                String jyDownLoadUrlForLocal = Environment.getExternalStorageDirectory() + "/jy/" + fileName;
//                final File file = new File(jyDownLoadUrlForLocal);
//                if (file.exists()) {
//                    pdfBean.setState(1);
//
//                }
//
//
//            }
//
//            DataStore_Direct.directPdfDatailList = directBeanListPDFForClassSchedule;
//            directClassPdfAdapter.notifyDataSetChanged();
//        } else {
//            if (tab == 1) {
//                ll_no_notes.setVisibility(View.VISIBLE);
//            }
//        }
//    }
//
//    public void flushPdfContent_OnFailure(String res) {
//        mCustomLoadingDialog.dismiss();
//        if (SendRequest.ERROR_NETWORK.equals(res)) {
//            ToastUtils.showToast(R.string.network);
//        } else if (SendRequest.ERROR_SERVER.equals(res)) {
//            ToastUtils.showToast(R.string.server_error);
//        }
//    }
//
//    /**
//     * 加载pdf列表
//     */
//    public void getLivePDFForClassSchedule() {
//        obtatinPdfListener = new ObtatinPdfListener(LiveActivityForBjysdk.this);
//        SendRequest.getLivePDFForClassSchedule(directBean.getRid(), uid, obtatinPdfListener);
//    }
//
//    /**
//     * 记录时长
//     */
//    private void addRecordTime() {
//        if (!StringUtils.isEmpty(joinTime) && recodingInfo != null) {
//            //记录时长
//            leavetime = StringUtils.getNowTime();
//            if (recodingInfo.getReturncash() > 0) {
//                addRecord(recodingInfo);
//            }
//
//            joinTime = "";
//        }
//    }
//
//    void addRecord(final DirectBean mDirectBean) {
//        Logger.e(directBean.getRid() + " " + mDirectBean.getTitle() + " joinTime:" + joinTime + " leavetime:" + leavetime);
//        SendRequest.addRecord(uid, account, mDirectBean.getOrderid(), joinTime, leavetime, directBean.getRid(), mDirectBean.getLessonid(), mIsPlayback ? "lubo" : "zhibo", new
//                ObtainDataFromNetListener<WithdrawalBean, String>() {
//                    @Override
//                    public void onSuccess(final WithdrawalBean res) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Logger.e("recode success");
//                                mDirectBean.setProgress(res.getProgress());
//                                mDirectBean.setFstatus(res.getFstatus());
//                                directClassScheduleAdapter.notifyDataSetChanged();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailure(String res) {
//                        Logger.e("recode error :" + res);
//                    }
//                });
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onHideSoftInputEvent(HideSoftInputEvent event) {
//        if (event.isClose()) {
//            InputMethodManager mInputKeyBoard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            if (getCurrentFocus() != null) {
//                mInputKeyBoard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        }
//
//    }
//}
