package com.huatu.teacheronline.direct;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gensee.utils.StringUtil;
import com.greendao.DirectBean;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.HomeActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.SensorDataSdk;
import com.huatu.teacheronline.direct.adapter.CourseWareAdapter;
import com.huatu.teacheronline.direct.adapter.DirectGiftAdapter;
import com.huatu.teacheronline.direct.bean.SelectAcademicBean;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.paymethod.ChoosePayMethodActivity;
import com.huatu.teacheronline.sensorsdata.TrackUtil;
import com.huatu.teacheronline.sensorsdata.event.BrowseCourse;
import com.huatu.teacheronline.sensorsdata.event.BuyCourse;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.utils.ShareUtils;
import com.huatu.teacheronline.utils.SoftKeyboardUtil;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.ChooseSubjectPopwindows;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.ScorllPinnedHeaderExpandableListView.PinnedHeaderExpandableListView;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cn.iwgang.countdownview.CountdownView;
import cn.iwgang.countdownview.DynamicConfig;
import cn.xiaoneng.coreapi.ChatParamsBody;
import cn.xiaoneng.uiapi.Ntalker;

/**
 * @author wf
 * 课程详情页面
 */
public class DirectDetailsActivity extends BaseActivity implements AdapterView.OnItemClickListener, RadioGroup.OnCheckedChangeListener, ExpandableListView
        .OnChildClickListener
        , ExpandableListView.OnGroupClickListener, PinnedHeaderExpandableListView.OnHeaderUpdateListener {
    public static String siteid = "kf_10092";// 企业id, 示例kf_9979,kf_8002,kf_3004,zf_1000,yy_1000
    public static String sdkkey = "96FCDC85-293E-4D92-9FA9-DD7767FAC103";// 示例FB7677EF-00AC-169D-1CAD-DEDA35F9C07B
    public static String settingid1 = "kf_10092_1513839567729";// 客服组id示例kf_9979_1452750735837
    private RelativeLayout rl_main_left, rl_main_right;
    private SimpleDraweeView simpleDraweeView;
    //    private TextView tv_directTitle;
    //    private TextView tv_tab;
    private WebView wv_direct_course_introduce;
    //    private WebView wv_direct_teacher_introduce;
    private CustomAlertDialog mCustomLoadingDialog;
    private CustomAlertDialog mCustomAddDirectDilog;
    private String directId;
    private DirectBean directBean;
    private ObtainDataListerForAddDirect obtainDataListerForAddDirect;
    private ObtainDataListerForLoadingDirectInfo obtainDataListerForLoadingDirectInfo;
    //    private ObtainDataListerForCreateOrder obtainDataListerForCreateOrder;
    private ArrayList<DirectBean> directBeanListForClassSchedule = new ArrayList<>();
    private String uid;
    private ExpandableListView lv_listview_course_table;
    private Button btn_buy;
    private RadioButton rb_direct_introduce;
    private RadioButton rb_direct_teacher_introduce;
    private RadioButton rb_direct_curriculum;
    private int tab = 1;//1 课程介绍 2 课程表 3 老师介绍
    private View loadIcon;
    private View loadView;
    //    private DirectClassScheduleAdapter directClassScheduleAdapter;
    private CourseWareAdapter directClassScheduleAdapter;

    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;
    private boolean hasMoreData = true;
    private int currentPagerForClassSchedule = 1;
    private ObtatinDataListenerForClassSchedule obtatinDataListenerForClassSchedule;
    private int limit = 8;
    private boolean isLoadEnd;
    private String money;//金额
    private TextView tv_title_directdetail;
    private TextView tv_date_directdetail;
    //    private TextView tv_dirct_clas_type_answer;
//    private TextView tv_dirct_course_state_answer;
//    private TextView tv_dirct_course_time_answer;
//    private TextView tv_dirct_effective_time_answer;
    private TextView tv_dirct_teacher;
    //    private TextView tv_dirct_timing_answer;
    private TextView tv_discontinued_date;
    private TextView tv_money_directdetail;
    private TextView tv_buynum_directdetail;
    private LinearLayout ll_listview_course_table;
    //        private EditText et_accode_directdetail;
    private LinearLayout ll_dirct_course_introduce;
    private LinearLayout ll_scroll_detail;
    private LinearLayout ll_direct_teacher_introduce;
    private View view_line_direct_introduce, view_line_direct_curriculum, view_line_teacher_introduce;
    private View rl_buttom_goumai;
    private int isPush;//是否推送过来的消息
    private TextView cp_date_consult;
    private boolean isBackH5 = false;//聊天页面是否返回前一个乐语聊天页面
    private int spalsh_id;
    private String learning_period = ""; //学段
    private String subject = "";//科目
    private ChooseSubjectPopwindows chooseSubjectPopwindows;
    public SelectAcademicBean selectAcademicBean;//筛选的科目
    //    private ShopServiceAdapter madapter;
    private LinearLayout ll_button_all;
    private TextView tv_longtime_directdetail;
    private RelativeLayout rl_gift;
    private LinearLayout ll_special;
    private LinearLayout ll_discount;
    private RelativeLayout rl_seckill;
    private SimpleDraweeView img_direct_dealite_introduce;
    private DirectGiftAdapter directGiftAdapter;
    private TextView tv_textbook;
    private TextView tv_gift_num;
    private TextView tv_startend_title;
    private CountdownView cv_countdown_special;
    private TextView tv_seckill_num;
    private TextView tv_seckill_starttime;
    private CountdownView cv_countdown_seckill;
    private TextView tv_endtime_ms;
    private TextView tv_fla_price;
    private CountdownView cv_countdown_discount;
    private TextView tv_discount_title;
    private TextView tv_discount;
    private TextView tv_cash_title;
    private LinearLayout ll_cash;
    private TextView cp_share_consult;
    private TextView tv_date_num;
    private EditText et_accode_directdetail;
    //    private RelativeLayout rl_buttom_jihuo;

    @Override
    public void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dirctdetails_layout_nested);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        directId = getIntent().getStringExtra("directId");
        isPush = getIntent().getIntExtra("isPush", 0);
        isBackH5 = getIntent().getBooleanExtra("isBackH5", false);


        spalsh_id = getIntent().getIntExtra("spalsh_id", 0);//spalsh_id=8表示从广告页跳转过来 9 从跑马灯广告跳转过来
        mCustomLoadingDialog = new CustomAlertDialog(DirectDetailsActivity.this, R.layout.dialog_loading_custom);
        mCustomAddDirectDilog = new CustomAlertDialog(DirectDetailsActivity.this, R.layout.dialog_join_mydirect);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        rl_main_right.setVisibility(View.GONE);
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.pull_to_refresh_and_load_rotating);
        wv_direct_course_introduce = (WebView) findViewById(R.id.wv_direct_course_introduce);
        ll_scroll_detail = (LinearLayout) findViewById(R.id.ll_scroll_detail);
//        wv_direct_teacher_introduce = (WebView) findViewById(R.id.wv_direct_teacher_introduce);
        lv_listview_course_table = (ExpandableListView) findViewById(R.id.lv_listview_course_table);
        directClassScheduleAdapter = new CourseWareAdapter(this);
        lv_listview_course_table.setAdapter(directClassScheduleAdapter);
        directClassScheduleAdapter.bind(lv_listview_course_table);


        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.sdv_icon);
        tv_money_directdetail = (TextView) findViewById(R.id.tv_money_directdetail);
        tv_dirct_teacher = (TextView) findViewById(R.id.tv_dirct_teacher);
        tv_title_directdetail = (TextView) findViewById(R.id.tv_title_directdetail);
        tv_date_directdetail = (TextView) findViewById(R.id.tv_date_directdetail);
//        et_accode_directdetail = (EditText) findViewById(R.id.et_accode_directdetail);
        cp_date_consult = (TextView) findViewById(R.id.cp_date_consult);
        tv_discontinued_date = (TextView) findViewById(R.id.tv_discontinued_date);
        tv_buynum_directdetail = (TextView) findViewById(R.id.tv_buynum_directdetail);
        ll_listview_course_table = (LinearLayout) findViewById(R.id.ll_listview_course_table);
        ll_dirct_course_introduce = (LinearLayout) findViewById(R.id.ll_dirct_course_introduce);
        ll_direct_teacher_introduce = (LinearLayout) findViewById(R.id.ll_direct_teacher_introduce);
        view_line_direct_introduce = findViewById(R.id.view_line_direct_introduce);
        view_line_direct_curriculum = findViewById(R.id.view_line_direct_curriculum);
        view_line_teacher_introduce = findViewById(R.id.view_line_teacher_introduce);
        rl_buttom_goumai = findViewById(R.id.rl_buttom_goumai);
        ll_button_all = (LinearLayout) findViewById(R.id.ll_button_all);
        tv_longtime_directdetail = (TextView) findViewById(R.id.tv_longtime_directdetail);
//        wv_direct_course_introduce = new WebView(this);
        rl_gift = (RelativeLayout) findViewById(R.id.rl_gift);
        ll_special = (LinearLayout) findViewById(R.id.ll_special);
        ll_discount = (LinearLayout) findViewById(R.id.ll_discount);
        rl_seckill = (RelativeLayout) findViewById(R.id.rl_seckill);
        img_direct_dealite_introduce = (SimpleDraweeView) findViewById(R.id.img_direct_dealite_introduce);
        tv_textbook = (TextView) findViewById(R.id.tv_textbook);
        tv_gift_num = (TextView) findViewById(R.id.tv_gift_num);
        tv_startend_title = (TextView) findViewById(R.id.tv_startend_title);//特价未开始标题
        cv_countdown_special = (CountdownView) findViewById(R.id.cv_countdown_special);
        tv_seckill_num = (TextView) findViewById(R.id.tv_seckill_num);
        tv_seckill_starttime = (TextView) findViewById(R.id.tv_seckill_starttime);
        cv_countdown_seckill = (CountdownView) findViewById(R.id.cv_countdown_seckill);
        tv_endtime_ms = (TextView) findViewById(R.id.tv_endtime_ms);
        tv_fla_price = (TextView) findViewById(R.id.tv_fla_price);

        tv_discount = (TextView) findViewById(R.id.tv_discount);
        tv_discount_title = (TextView) findViewById(R.id.tv_discount_title);
        cv_countdown_discount = (CountdownView) findViewById(R.id.cv_countdown_discount);
        ll_cash = (LinearLayout) findViewById(R.id.ll_cash);
        tv_cash_title = (TextView) findViewById(R.id.tv_cash_title);
        cp_share_consult = (TextView) findViewById(R.id.cp_share_consult);
        tv_date_num = (TextView) findViewById(R.id.tv_date_num);
//        rl_buttom_jihuo = (RelativeLayout) findViewById(R.id.rl_buttom_jihuo);

        DynamicConfig dynamicConfig = new DynamicConfig.Builder().setSuffixGravity(DynamicConfig.SuffixGravity.CENTER).build();
        cv_countdown_special.dynamicShow(dynamicConfig);
        cv_countdown_seckill.dynamicShow(dynamicConfig);
        cv_countdown_discount.dynamicShow(dynamicConfig);

/*
     三个注释（课程介绍，课程表，老师介绍）
 */
        rb_direct_introduce = (RadioButton) findViewById(R.id.rb_direct_introduce);
        rb_direct_curriculum = (RadioButton) findViewById(R.id.rb_direct_curriculum);
        rb_direct_teacher_introduce = (RadioButton) findViewById(R.id.rb_direct_teacher_introduce);
        btn_buy = (Button) findViewById(R.id.btn_buy);
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        //设置高度为屏幕的高度
        ViewGroup.LayoutParams params = wv_direct_course_introduce.getLayoutParams();
//        params.height = height;
        int v = (int) (width * 0.1361);
        params.height = height - 3 * v;
        DebugUtil.e("params.height:" + params.height + " v:" + v);
        lv_listview_course_table.setLayoutParams(params);
        lv_listview_course_table.requestLayout();
        wv_direct_course_introduce.setLayoutParams(params);
        wv_direct_course_introduce.requestLayout();
    }


    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
//        tv_tab.setOnClickListener(this);
        btn_buy.setOnClickListener(this);
        rb_direct_introduce.setOnClickListener(this);
        rb_direct_curriculum.setOnClickListener(this);
        rb_direct_teacher_introduce.setOnClickListener(this);
        lv_listview_course_table.setOnItemClickListener(this);
        cp_date_consult.setOnClickListener(this);
        rl_gift.setOnClickListener(this);
        cp_share_consult.setOnClickListener(this);
//        findViewById(R.id.btn_activate).setOnClickListener(this);
        directClassScheduleAdapter.setVideoClickbutton(new CourseWareAdapter.VideoClick() {
            @Override
            public void setVideoClick() {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

//        wv_direct_course_introduce.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent ev) {
//                ((WebView) v).requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Log.i("TAG", "onChildClick");
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }

    @Override
    public View getPinnedHeader() {
        View headerView = getLayoutInflater().inflate(R.layout.header, null);
        headerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT));
        return headerView;
    }

    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
        TextView tv = (TextView) headerView.findViewById(R.id.tv_header);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        String name = "";
        if (firstVisibleGroupPos >= 0) {
//            name = (String) madapter.getGroup(firstVisibleGroupPos);
        }
        tv.setText(name);
    }


//    public static final boolean isApkInstalled(Context context, String packageName) {
//        try {
//            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
//            return true;
//        } catch (PackageManager.NameNotFoundException e) {
//            return false;
//}
//}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                if (isPush == 1 || spalsh_id == 8) {
                    back();
                    HomeActivity.newIntentFlag(this, "HomeActivity", 0);
                } else {
                    back();
                }
                break;
            case R.id.cp_share_consult://分享
                MobclickAgent.onEvent(this, "shareOnclik");
                ShareUtils.popShare(this, ShareUtils.url_appdownload_qq, ShareUtils.content_share, ShareUtils.title_share, true);
                break;
            case R.id.rl_main_right://分享
                MobclickAgent.onEvent(this, "shareOnclik");
//                ShareUtils.share(this, ShareUtils.url_appdownload_qq, ShareUtils.content_share, ShareUtils.title_share, true);
                ShareUtils.popShare(this, ShareUtils.url_appdownload_qq, ShareUtils.content_share, ShareUtils.title_share, true);
                break;
            case R.id.cp_date_consult:
                if (directBean == null) {
                    Toast.makeText(DirectDetailsActivity.this, "数据加载中", Toast.LENGTH_SHORT).show();
                    return;
                }

                Ntalker.getBaseInstance().login(uid, CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, ""), 0);
                ChatParamsBody chatparams = new ChatParamsBody();
                chatparams.itemparams.itemparam = "android";
                chatparams.itemparams.goods_id = directBean.getRid();
                chatparams.headurl = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_FACEPATH, "");
                Ntalker.getBaseInstance().startChat(this, directBean.getbranchschoolid(), "华图教师", chatparams, TestChatActivity.class);
//                if (isBackH5) {
//                    back();
//                } else {
//                    MobclickAgent.onEvent(this, "consultationOnClik");
//                    H5DetailActivity.newIntent(this, "咨询", directBean.getCustomer());
//                }
                break;
            case R.id.rb_direct_introduce:
                if (tab != 1) {
                    MobclickAgent.onEvent(this, "courseIntroduction");
                    tab = 1;
                    rb_direct_introduce.setTextColor(getResources().getColor(R.color.green004));
                    rb_direct_curriculum.setTextColor(getResources().getColor(R.color.black));
                    rb_direct_teacher_introduce.setTextColor(getResources().getColor(R.color.black));
                    ll_dirct_course_introduce.setVisibility(View.VISIBLE);
                    ll_listview_course_table.setVisibility(View.GONE);
                    ll_direct_teacher_introduce.setVisibility(View.GONE);
                    view_line_direct_introduce.setBackgroundColor(getResources().getColor(R.color.green007));
                    view_line_direct_curriculum.setBackgroundColor(getResources().getColor(R.color.white));
                    view_line_teacher_introduce.setBackgroundColor(getResources().getColor(R.color.white));
                }

                break;
            case R.id.rb_direct_curriculum:
                if (tab != 2) {
                    tab = 2;
                    MobclickAgent.onEvent(this, "scheduleListOnClik");
                    rb_direct_introduce.setTextColor(getResources().getColor(R.color.black));
                    rb_direct_curriculum.setTextColor(getResources().getColor(R.color.green004));
                    rb_direct_teacher_introduce.setTextColor(getResources().getColor(R.color.black));
                    ll_dirct_course_introduce.setVisibility(View.GONE);
                    ll_listview_course_table.setVisibility(View.VISIBLE);
                    ll_direct_teacher_introduce.setVisibility(View.GONE);
                    view_line_direct_introduce.setBackgroundColor(getResources().getColor(R.color.white));
                    view_line_direct_curriculum.setBackgroundColor(getResources().getColor(R.color.green007));
                    view_line_teacher_introduce.setBackgroundColor(getResources().getColor(R.color.white));
                    if (directBean == null) {
                        return;
                    }
//                    lv_listview_course_table.addFooterView(loadView);
//                    loadIcon.startAnimation(refreshingAnimation);
//                    lv_listview_course_table.setAdapter(directClassScheduleAdapter);
//                    loadDirectClasSchedule(true);

                    if (directBeanListForClassSchedule == null || directBeanListForClassSchedule.size() == 0) {
//                        lv_listview_course_table.setVisibility(View.VISIBLE);
//                        lv_listview_course_table.addFooterView(loadView);
//                        loadIcon.startAnimation(refreshingAnimation);
//                        directBeanListForClassSchedule.clear();
//                        lv_listview_course_table.setAdapter(directClassScheduleAdapter);
//                        loadDirectClasSchedule(true);
//                    } else {
                        lv_listview_course_table.setVisibility(View.VISIBLE);
//                        completeRefresh();
                    }
                }
                break;
            case R.id.rb_direct_teacher_introduce:
                if (tab != 3) {
                    tab = 3;
                    MobclickAgent.onEvent(this, "teacheriIntroductionOnClik");
                    rb_direct_introduce.setTextColor(getResources().getColor(R.color.black));
                    rb_direct_curriculum.setTextColor(getResources().getColor(R.color.black));
                    rb_direct_teacher_introduce.setTextColor(getResources().getColor(R.color.green004));
                    ll_dirct_course_introduce.setVisibility(View.GONE);
                    ll_listview_course_table.setVisibility(View.GONE);
                    ll_direct_teacher_introduce.setVisibility(View.VISIBLE);
                    view_line_direct_introduce.setBackgroundColor(getResources().getColor(R.color.white));
                    view_line_direct_curriculum.setBackgroundColor(getResources().getColor(R.color.white));
                    view_line_teacher_introduce.setBackgroundColor(getResources().getColor(R.color.green007));
                }
                break;
            case R.id.btn_buy://立即学习||立即购买
                if (directBean == null) {
                    Toast.makeText(DirectDetailsActivity.this, "数据加载中", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ("1".equals(directBean.getIsTimeout())) {
                    Toast.makeText(this, "该课程已过期,无法购买!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!"1".equals(directBean.getIs_buy())) {
                    if ("1".equals(directBean.getIs_ax_Type())) {
                        //激活产品
                        PopVation(v);
                        return;
                    }
                }
                String zhibotime = directBean.getZhibotime() + "-" + directBean.getZhiboendtime() + "(" + directBean
                        .getLessionCount() + "小时" + ")";
                if ("0".equals(directBean.getIs_buy())) {
                    //track 神策购买课程事件
                    trackBuyCourse();

                    if (directBean.getActualPrice() == 0) {
                        MobclickAgent.onEvent(this, "buyOnClik");
//                        addDirectForMy();
                        //选择科目弹出框
                        if (selectAcademicBean != null && ((selectAcademicBean.getStudySection().size() > 0 || selectAcademicBean.getSubject().size() > 0))) {
                            chooseSubjectPopwindows = new ChooseSubjectPopwindows(this);
                            chooseSubjectPopwindows.showPopWindow(v, selectAcademicBean, directBean, this, this);
                        } else {
                            ChoosePayMethodActivity.newIntent(DirectDetailsActivity.this, directBean, zhibotime, learning_period, subject);
                        }
                        //0元课程进入
                    } else {
//                        createOrder();
                        MobclickAgent.onEvent(this, "buyOnClik");
//                        ChoosePayMethodActivity.newIntent(DirectDetailsActivity.this, directBean);

                        //选择科目弹出框
                        if (selectAcademicBean != null && ((selectAcademicBean.getStudySection().size() > 0 || selectAcademicBean.getSubject().size() > 0))) {
                            chooseSubjectPopwindows = new ChooseSubjectPopwindows(this);
                            chooseSubjectPopwindows.showPopWindow(v, selectAcademicBean, directBean, this, this);
                        } else {
                            ChoosePayMethodActivity.newIntent(DirectDetailsActivity.this, directBean, zhibotime, learning_period, subject);
                        }
                    }
                } else if ("1".equals(directBean.getIs_buy())) {
                    MobclickAgent.onEvent(this, "studyOnClik");
                    if (directBeanListForClassSchedule.size() > 0) {
                        DirectBean mDirectBean = directBeanListForClassSchedule.get(0);
//                        if (mDirectBean.getVideoType() == 1) {
//                            //直播
//                            DataStore_Direct.directDatailList = directBeanListForClassSchedule;
//                            LiveActivityForBjysdk.newIntent(DirectDetailsActivity.this, directBean, 0, 2);
//
//                            //网课
//                        } else if (mDirectBean.getVideoType() == 0) {
//                            DataStore_Direct.directDatailList = directBeanListForClassSchedule;
//                            DirectPlayDetailsActivityForBjysdk.newIntent(DirectDetailsActivity.this, directBean, 0, 2);
//                        }
                        DataStore_Direct.directDatailList = directBeanListForClassSchedule;
                        PlayerActivityForBjysdk.newIntent(DirectDetailsActivity.this, directBean, 0, 2);
                    }
                }
                break;

            case R.id.btn_activate:
                if (directBean != null) {
//                    String acCode = "";
                    String acCode = et_accode_directdetail.getText().toString().trim();
                    if ("1".equals(directBean.getIs_buy())) {
                        if (directBeanListForClassSchedule.size() > 0) {
                            MobclickAgent.onEvent(this, "studyOnClik");
                            DirectBean mDirectBean = directBeanListForClassSchedule.get(0);
//                            if (mDirectBean.getVideoType() == 1) {
//                                //直播
//                                if ("2".equals(mDirectBean.getVideo_status())) {
//                                    DataStore_Direct.directDatailList = directBeanListForClassSchedule;
//                                    LiveActivityForBjysdk.newIntent(DirectDetailsActivity.this, directBean, 0, 2);
//                                } else if ("0".equals(mDirectBean.getVideo_status())) {
//                                    DataStore_Direct.directDatailList = directBeanListForClassSchedule;
//                                    LiveActivityForBjysdk.newIntent(DirectDetailsActivity.this, directBean, 0, 2);
//                                } else {
//                                    ToastUtils.showToast(R.string.video_des);
//                                }
//                                //网课
//                            } else if (mDirectBean.getVideoType() == 0) {
//                                DataStore_Direct.directDatailList = directBeanListForClassSchedule;
//                                DirectPlayDetailsActivityForBjysdk.newIntent(DirectDetailsActivity.this, directBean, 0, 2);
//                            }
                            DataStore_Direct.directDatailList = directBeanListForClassSchedule;
                            PlayerActivityForBjysdk.newIntent(DirectDetailsActivity.this, directBean, 0, 2);
                        }
                    } else {
                        if (StringUtil.isEmpty(acCode)) {
                            ToastUtils.showToast(getResources().getString(R.string.pleaseInputAccode));
                        } else {
                            MobclickAgent.onEvent(this, "activationOnClik");
                            SoftKeyboardUtil.hintKbTwo(this);
                            ObtainDataListerForActiveNet ObtainDataFromNetListener = new ObtainDataListerForActiveNet(this);
                            SendRequest.getActiveNet(uid, directBean.getRid(), acCode, ObtainDataFromNetListener);
                        }
                    }
                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.img_close_choose_sub:
                chooseSubjectPopwindows.dissmiss();
                break;
            case R.id.bt_yes_choose_sub:
                chooseSubjectPopwindows.dissmiss();
                String zhibotime2 = directBean.getZhibotime() + "-" + directBean.getZhiboendtime() + "(" + directBean
                        .getLessionCount() + "小时" + ")";
                ChoosePayMethodActivity.newIntent(DirectDetailsActivity.this, directBean, zhibotime2, learning_period, subject);
                break;
            case R.id.rl_gift:
                if (directBean.getGift() == null) {
                    return;
                }
                PopGift(v);
                break;
        }
    }

    private void trackBuyCourse() {

        try {
            BuyCourse.Builder builder = new BuyCourse.Builder()
                    .course_title(directBean.getTitle())
                    .course_id(directBean.getRid())

                    .course_number(directBean.getClassNo())
                    .course_examination_method(directBean.getCourse_examination_method())
                    .course_examination(directBean.getCourse_examination())
                    .course_study_section(directBean.getCourse_study_section())
                    .course_subject(directBean.getCourse_subject())
                    .course_agreement(directBean.getCourse_agreement())
                    .course_class_type(directBean.getCourse_class_type())
                    .course_province(directBean.getCourse_province())

                    .course_class_hour(Integer.valueOf(directBean.getLessionCount()))
                    .course_cashback_hour(Double.valueOf(directBean.getCourse_cashback_hour()).intValue())
                    .course_courseware_quantity(Integer.valueOf(directBean.getLessionCount()))

                    .course_teacher(directBean.getTeacherDesc())

                    .course_video_review(directBean.isCourse_video_review())
                    .course_video_review_frequency(Integer.valueOf(directBean.getCourse_video_review_frequency()))
                    .course_creation_date(StringUtils.checkEmpty2Null(directBean.getCreateDate()))

                    .course_live_start_time(StringUtils.checkEmpty2Null(directBean.getZhibotime()))
                    .course_live_end_time(StringUtils.checkEmpty2Null(directBean.getZhiboendtime()))

                    .course_validity_period(Integer.valueOf(directBean.getUseday()))
                    .course_offline_date(StringUtils.checkEmpty2Null(directBean.getEndDate()))

                    .course_price(directBean.getActualPrice())
                    .course_audition_nature(directBean.getCourse_audition_nature())
                    .course_courseware_nature(directBean.getCourse_courseware_nature())

                    .course_1to1(directBean.isCourse_1to1())
                    .course_agreement_class(directBean.isCourse_agreement_class())
                    .course_series_class(directBean.isCourse_series_class())

                    .course_preferential_method(directBean.getdisproperty())

                    .course_preferential_start_time(StringUtils.checkEmpty2Null(directBean.getStart_time()))
                    .course_preferential_end_time(StringUtils.checkEmpty2Null(directBean.getTerminal_time()));


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

            TrackUtil.trackBuyCourse(builder.build());
        } catch (Exception e) {
            Logger.e(SensorDataSdk.TAG + e.toString());
        }


    }

    private void trackBrowseCourse() {


        try {
            BrowseCourse.Builder builder = new BrowseCourse.Builder()
                    .course_title(directBean.getTitle())
                    .course_id(directBean.getRid())

                    .course_number(directBean.getClassNo())
                    .course_examination_method(directBean.getCourse_examination_method())
                    .course_examination(directBean.getCourse_examination())
                    .course_study_section(directBean.getCourse_study_section())
                    .course_subject(directBean.getCourse_subject())
                    .course_agreement(directBean.getCourse_agreement())
                    .course_class_type(directBean.getCourse_class_type())
                    .course_province(directBean.getCourse_province())

                    .course_cashback_hour(Double.valueOf(directBean.getCourse_cashback_hour()).intValue())

                    .course_teacher(directBean.getTeacherDesc())

                    .course_video_review(directBean.isCourse_video_review())
                    .course_video_review_frequency(Integer.valueOf(directBean.getCourse_video_review_frequency()))
                    .course_creation_date(StringUtils.checkEmpty2Null(directBean.getCreateDate()))

                    .course_live_start_time(StringUtils.checkEmpty2Null(directBean.getZhibotime()))
                    .course_live_end_time(StringUtils.checkEmpty2Null(directBean.getZhiboendtime()))

                    .course_validity_period(Integer.valueOf(directBean.getUseday()))
                    .course_offline_date(StringUtils.checkEmpty2Null(directBean.getEndDate()))

                    .course_price(directBean.getActualPrice())
                    .course_audition_nature(directBean.getCourse_audition_nature())
                    .course_courseware_nature(directBean.getCourse_courseware_nature())

                    .course_1to1(directBean.isCourse_1to1())
                    .course_agreement_class(directBean.isCourse_agreement_class())
                    .course_series_class(directBean.isCourse_series_class())

                    .course_preferential_method(directBean.getdisproperty())

                    .course_preferential_start_time(StringUtils.checkEmpty2Null(directBean.getStart_time()))
                    .course_preferential_end_time(StringUtils.checkEmpty2Null(directBean.getTerminal_time()));


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


            TrackUtil.trackBrowseCourse(builder.build());

        } catch (Exception e) {
            Logger.e(SensorDataSdk.TAG + e.toString());
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.gv_learning_period_choose_sub:
                RadioButton rb_learning_period = (RadioButton) chooseSubjectPopwindows.getContentView().findViewById(group.getCheckedRadioButtonId());
                if (rb_learning_period == null) return;
                learning_period = rb_learning_period.getText().toString();
                break;
            case R.id.gv_subjects_choose_sub:
                RadioButton rb_subjects = (RadioButton) chooseSubjectPopwindows.getContentView().findViewById(group.getCheckedRadioButtonId());
                if (rb_subjects == null) return;
                subject = rb_subjects.getText().toString();
                break;

        }
    }

    /**
     * 激活课程
     */
    private class ObtainDataListerForActiveNet extends ObtainDataFromNetListener<String, String> {
        private DirectDetailsActivity weak_activity;

        public ObtainDataListerForActiveNet(DirectDetailsActivity activity) {
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
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("1".equals(res)) {//1 加入成功 2 重复加入
                            ToastUtils.showToast(R.string.accodeSuccess);
                            HomeActivity.newIntentFlag(DirectDetailsActivity.this, "ToMyDirectActivity", 0);
                        } else if ("2".equals(res)) {//加入失败
                            ToastUtils.showToast(R.string.accodeAlreadyUsed);
                        } else if ("3".equals(res)) {//加入失败
                            ToastUtils.showToast(R.string.accodeAlready);
                        } else if ("4".equals(res)) {//加入失败
                            ToastUtils.showToast(R.string.accodeError);
                        } else {
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }
    }

    /**
     * 加载课表列表
     */
    public void loadDirectClasSchedule(boolean isReset) {
//        if (isReset) {
//            currentPagerForClassSchedule = 1;
        directBeanListForClassSchedule.clear();
//        } else {
//            currentPagerForClassSchedule++;
//        }
        obtatinDataListenerForClassSchedule = new ObtatinDataListenerForClassSchedule(isReset, this);
        SendRequest.getLiveDataForClassSchedule(directId, uid, obtatinDataListenerForClassSchedule);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ("1".equals(directBean.getIs_buy()) || directBean.getVideoType() == 0) {
            //直播点播
//            if (directBean.getVideoType() == 1) {
//                if ("1".equals(directBean.getIs_zhibo())) {//直播
//                    DataStore_Direct.directDatailList = directBeanListForClassSchedule;
//                    LiveActivityForBjysdk.newIntent(DirectDetailsActivity.this, directBean, position);
//                } else if ("0".equals(directBean.getIs_zhibo())) {//录播
//                    DataStore_Direct.directDatailList = directBeanListForClassSchedule;
//                    LiveActivityForBjysdk.newIntent(DirectDetailsActivity.this, directBean, position);
//                }
//            } else if (directBean.getVideoType() == 0) {
//                //网课
//                if (directBeanListForClassSchedule.get(position).getIsTrial() == 1 || "1".equals(directBean.getIs_buy())) {
//                    DataStore_Direct.directDatailList = directBeanListForClassSchedule;
//                    DirectPlayDetailsActivityForBjysdk.newIntent(DirectDetailsActivity.this, directBean, position);
//                } else {
//                    ToastUtils.showToast("请先购买！谢谢！");
//                }
//            }
            DataStore_Direct.directDatailList = directBeanListForClassSchedule;
            PlayerActivityForBjysdk.newIntent(DirectDetailsActivity.this, directBean, 0, 2);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            ToastUtils.showToast("请先购买！谢谢！");
        }

    }


    /**
     * 获取课程表
     */
    private static class ObtatinDataListenerForClassSchedule extends ObtainDataFromNetListener<ArrayList<DirectBean>, String> {
        private DirectDetailsActivity weak_activity;
        private boolean isReset;

        public ObtatinDataListenerForClassSchedule(boolean isReset, DirectDetailsActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
            isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.isLoadEnd = false;
            }
        }

        @Override
        public void onSuccess(final ArrayList<DirectBean> res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushClassSchedule_OnSucess(isReset, res);
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
                        weak_activity.flushClassSchedule_OnFailure(res);
                    }
                });
            }
        }
    }

    private void completeRefresh() {
        if (lv_listview_course_table != null && lv_listview_course_table.getFooterViewsCount() > 0) {
            lv_listview_course_table.removeFooterView(loadView);
        }

        if (directClassScheduleAdapter != null) {
            directClassScheduleAdapter.isFromDetail();
            directClassScheduleAdapter.setDirectBeanList(directBeanListForClassSchedule, directBean.getRid(), 3);
            directClassScheduleAdapter.notifyDataSetChanged();
        }

        if (hasMoreData) {
            isLoadEnd = true;
        } else {
            isLoadEnd = false;
        }
    }

    /**
     * @param context
     * @param directId
     * @param isBackH5 是否返回H5咨询页面，默认不回
     */
    public static void newIntent(Context context, String directId, boolean isBackH5) {
        Intent intent = new Intent(context, DirectDetailsActivity.class);
        intent.putExtra("directId", directId);
        intent.putExtra("isBackH5", isBackH5);
        context.startActivity(intent);

    }

//    /**
//     * @param context
//     * @param directId
//     * @param categorie 所属考试
//     * @param period    学段
//     * @param subject   科目
//     * @param province  地区
//     */
//    public final static String KEY_CATEGORIE = "key_categorie";
//    public final static String KEY_PERIOD = "key_categorie";
//    public final static String KEY_SUBJECT = "key_subject";
//    public final static String KEY_PROVINCE = "key_province";
//
//
//    public static void newIntent(Context context, String directId, String categorie, String period, String subject, String province) {
//        Intent intent = new Intent(context, DirectDetailsActivity.class);
//        intent.putExtra("directId", directId);
//        intent.putExtra(KEY_CATEGORIE, categorie);
//        intent.putExtra(KEY_PERIOD, period);
//        intent.putExtra(KEY_SUBJECT, subject);
//        intent.putExtra(KEY_PROVINCE, province);
//        context.startActivity(intent);
//
//    }

    /**
     * @param context
     * @param directId
     */
    public static void newIntent(Context context, String directId) {
        Intent intent = new Intent(context, DirectDetailsActivity.class);
        intent.putExtra("directId", directId);
        context.startActivity(intent);
    }

    public static void newIntent(Context context, String directId, int spalsh_id) {
        Intent intent = new Intent(context, DirectDetailsActivity.class);
        intent.putExtra("directId", directId);
        intent.putExtra("spalsh_id", spalsh_id);
        context.startActivity(intent);
    }

    /**
     * 课程表完成刷新
     *
     * @param isReset
     * @param res
     */
    public void flushClassSchedule_OnSucess(boolean isReset, ArrayList<DirectBean> res) {
        if (isReset && (res == null || res.size() == 0)) {
            ToastUtils.showToast(R.string.no_data);
        }

        directBeanListForClassSchedule.clear();
        if (res != null && res.size() != 0) {
            directBeanListForClassSchedule.addAll(res);
        }

//        directClassScheduleAdapter.setDirectBeanList(directBeanListForClassSchedule, directBean.getRid(), 1);
//        if (res == null || res.size() == 0) {
//            hasMoreData = false;
//        } else {
//            hasMoreData = true;
//            directBeanListForClassSchedule.addAll(res);
//        }

        completeRefresh();

    }

    public void flushClassSchedule_OnFailure(String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
        } else if (SendRequest.ERROR_SERVER.equals(res)) {
            ToastUtils.showToast(R.string.server_error);
        }
        completeRefresh();
    }

    /**
     * 获取直播详情
     */
    public void loadDirectData() {
        obtainDataListerForLoadingDirectInfo = new ObtainDataListerForLoadingDirectInfo(this);
        SendRequest.getDirectDetailsData(uid, directId, obtainDataListerForLoadingDirectInfo);
    }

    private static class ObtainDataListerForLoadingDirectInfo extends ObtainDataFromNetListener<DirectBean, String> {

        private DirectDetailsActivity weak_activity;

        public ObtainDataListerForLoadingDirectInfo(DirectDetailsActivity activity) {
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
        public void onSuccess(DirectBean res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                if (res != null) {
                    weak_activity.directBean = res;
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weak_activity.flushUI();
                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
                weak_activity.mCustomLoadingDialog.dismiss();
            }
        }
    }

    /**
     * 获取directbean刷新ui
     */
    public void flushUI() {
        //神策浏览课程事件
        trackBrowseCourse();

//        tv_directTitle.setText(directBean.getTitle());
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        img_direct_dealite_introduce.setHierarchy(hierarchy);
        tv_date_num.setText("课程编号：" + directBean.getRid());

        if (directBean.getGift() == null || directBean.getGift().size() == 0) {//赠品数量大于0则显示否则隐藏赠品一栏
            rl_gift.setVisibility(View.GONE);

        } else {
            rl_gift.setVisibility(View.VISIBLE);
            tv_textbook.setText(directBean.getGift().get(0));
            tv_gift_num.setText("共有" + directBean.getGift().size() + "个赠品");
        }

        FrescoUtils.setFrescoImageUri(img_direct_dealite_introduce, directBean.getScaleimg(), R.drawable.ic_loading);
        tv_title_directdetail.setText(directBean.getTitle());
        if (StringUtils.isEmpty(directBean.getHd())) {
//            setDirectTime(tv_date_directdetail, R.drawable.ic_during, directBean.getZhibotime() + "-" + directBean.getZhiboendtime() + "(" + directBean
//                    .getLessionCount() + "课时" + ")", 1);
//            setDirectTime(tv_date_directdetail, R.drawable.ic_during, directBean.getZhibotime() + "-" + directBean.getZhiboendtime() + "(" + directBean
//                    .getLessionCount() + "课时" + ")", 1);
            tv_date_directdetail.setText("开课时间：" + directBean.getZhibotime() + "-" + directBean.getZhiboendtime());
        } else {
//            String textStr = "<font color=\"#00b38a\">" + directBean.getHd() + "</font>" + "<font color=\"#999999\">" + "   (" + directBean.getLessionCount()
//                    + "课时" + ")" + "</font>";
            String textStr = "<font color=\"#00b38a\">" + directBean.getHd() + "</font>" + "<font color=\"#999999\">"
                    + "</font>";
            setDirectTime(tv_date_directdetail, R.drawable.ic_hd_online, textStr, 0);

//            tv_date_directdetail.setText("开课时间：" + directBean.getHd());

//            tv_date_directdetail.setText(directBean.getZhibotime() + "-" + directBean.getZhiboendtime() + "(" + directBean.getLessionCount() + "课时" + ")");
        }

        directClassScheduleAdapter.seDirectBean(directBean);
        lv_listview_course_table.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        // * 创建新的HeaderView，即置顶的HeaderView
//        View HeaderView = getLayoutInflater().inflate(R.layout.header, lv_listview_course_table, false);
//        lv_listview_course_table.setPinnedHeader(HeaderView);


        loadDirectClasSchedule(true);
//        tv_dirct_clas_type_answer.setText(directBean.getTypeName());
//        tv_dirct_course_state_answer.setText(directBean.getZhibotime());
//        tv_dirct_course_time_answer.setText(directBean.getLessionCount());
//        tv_dirct_effective_time_answer.setText(directBean.getEffectDateDesc());
        tv_dirct_teacher.setText("主讲老师：" + directBean.getTeacherDesc());
//        tv_dirct_timing_answer.setText(directBean.getZhibotime() + "---" + directBean.getZhiboendtime());
        String palynum = "已有" + directBean.getBuy_lives() + "人购买";
        SpannableStringBuilder span = new SpannableStringBuilder(palynum);
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#04CBAE")), 2, directBean.getBuy_lives().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tv_money_directdetail.setText("¥" + directBean.getActualPrice());
        tv_discontinued_date.setText("有效期：" + directBean.getEffectDateDesc());
        tv_buynum_directdetail.setText(span);
        if (directBean.getVideoType() == 1) {
            tv_longtime_directdetail.setText("时长：" + directBean.getLessionCount() + "小时");
        } else {
            tv_longtime_directdetail.setText("课件：" + directBean.getLessionCount() + "课件");
        }

        tv_cash_title.setText("最高返现" + directBean.getMostrcash() + "元");

        long time = directBean.getCount_down();//倒计时时间戳
        //折扣 特价 秒杀布局的展示,1是正常，2是特价，3是秒杀
        if (directBean.getdisproperty().equals("1")) {
            tv_money_directdetail.setText("" + directBean.getActualPrice());
            tv_fla_price.setVisibility(View.GONE);
            ll_special.setVisibility(View.GONE);
            ll_discount.setVisibility(View.GONE);
            rl_seckill.setVisibility(View.GONE);
        } else if (directBean.getdisproperty().equals("2")) {//优惠方式特价

            if (directBean.getState().equals("1")) {//特价活动未开始
                tv_money_directdetail.setText("" + directBean.getActualPrice());
                tv_fla_price.setVisibility(View.GONE);
                ll_special.setVisibility(View.GONE);
                ll_discount.setVisibility(View.GONE);
                rl_seckill.setVisibility(View.GONE);
                cv_countdown_special.setVisibility(View.GONE);
            } else if (directBean.getState().equals("2")) {//进行中
                ll_special.setVisibility(View.VISIBLE);
                tv_fla_price.setVisibility(View.VISIBLE);
                tv_money_directdetail.setText("" + directBean.getDisPrice());
                tv_fla_price.setText(directBean.getActualPrice() + "");
                tv_fla_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); // 设置中划线并加清晰
                tv_startend_title.setTextColor(getResources().getColor(R.color.gray007));
                tv_startend_title.setText("距离结束还有：");
                cv_countdown_special.setVisibility(View.VISIBLE);
                cv_countdown_special.start(time * 1000);
                cv_countdown_special.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                    @Override
                    public void onEnd(CountdownView cv) {
                        loadDirectData();//倒计时结束后自动刷新接口数据
                    }
                });
            } else {//活动结束或者无活动
                ll_special.setVisibility(View.GONE);
            }
        } else if (directBean.getdisproperty().equals("3")) {//优惠方式秒杀
            rl_seckill.setVisibility(View.VISIBLE);
            tv_fla_price.setVisibility(View.VISIBLE);
            tv_money_directdetail.setText("" + directBean.getDisPrice());
            tv_fla_price.setText(directBean.getActualPrice() + "");
            tv_fla_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); // 设置中划线并加清晰
            if (directBean.getState().equals("1")) {
                tv_seckill_num.setTextColor(getResources().getColor(R.color.gray007));
                tv_seckill_num.setText("限量" + directBean.getSeckillcount() + "份");
                tv_seckill_starttime.setText("开始时间：" + directBean.getStart_time());
                tv_seckill_starttime.setTextColor(getResources().getColor(R.color.orange002));
                tv_seckill_starttime.setVisibility(View.VISIBLE);
                cv_countdown_seckill.setVisibility(View.GONE);
                tv_endtime_ms.setVisibility(View.GONE);
            } else if (directBean.getState().equals("2")) {
                tv_endtime_ms.setVisibility(View.VISIBLE);
                tv_seckill_starttime.setVisibility(View.GONE);
                cv_countdown_seckill.setVisibility(View.VISIBLE);
                String SeckillNum = "仅剩" + directBean.getSeckill() + "份";
                SpannableStringBuilder spanSeck = new SpannableStringBuilder(SeckillNum);
                spanSeck.setSpan(new ForegroundColorSpan(Color.parseColor("#FB5334")), 2, String.valueOf(directBean.getSeckill()).length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_seckill_num.setText(spanSeck);
                cv_countdown_seckill.start(time * 1000);
                cv_countdown_seckill.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                    @Override
                    public void onEnd(CountdownView cv) {
                        loadDirectData();//倒计时结束后自动刷新接口数据
                    }
                });

            } else {
                rl_seckill.setVisibility(View.GONE);
            }

        } else if (directBean.getdisproperty().equals("4")) {//优惠方式 折扣
            ll_discount.setVisibility(View.VISIBLE);
            tv_discount.setText(directBean.getDiscount() + "折");
            tv_fla_price.setVisibility(View.VISIBLE);
            tv_money_directdetail.setText("" + directBean.getDisPrice());
            tv_fla_price.setText(directBean.getActualPrice() + "");
            tv_fla_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); // 设置中划线并加清晰
            if (directBean.getState().equals("1")) {//特价活动未开始
                tv_discount_title.setTextColor(getResources().getColor(R.color.orange002));
                tv_discount_title.setText("开始时间：" + directBean.getStart_time());
                cv_countdown_discount.setVisibility(View.GONE);
            } else if (directBean.getState().equals("2")) {//进行中
                tv_discount_title.setTextColor(getResources().getColor(R.color.gray007));
                tv_discount_title.setText("距离结束还有：");
                cv_countdown_discount.setVisibility(View.VISIBLE);
                cv_countdown_discount.start(time * 1000);
                cv_countdown_discount.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                    @Override
                    public void onEnd(CountdownView cv) {
                        loadDirectData();//倒计时结束后自动刷新接口数据
                    }
                });
            } else {//活动结束或者无活动
                ll_discount.setVisibility(View.GONE);
            }

        } else {
            tv_money_directdetail.setText("" + directBean.getActualPrice());
            ll_special.setVisibility(View.GONE);
            ll_discount.setVisibility(View.GONE);
            rl_seckill.setVisibility(View.GONE);
            tv_fla_price.setVisibility(View.GONE);
        }

        if (directBean.getMostrcash() == 0) {//最高返现金额 如果大于0显示布局
            ll_cash.setVisibility(View.GONE);
        } else {
            ll_cash.setVisibility(View.VISIBLE);
        }

        if (wv_direct_course_introduce == null) {
            return;
        }
        // 设置可以支持缩放
        wv_direct_course_introduce.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        wv_direct_course_introduce.getSettings().setBuiltInZoomControls(true);
        wv_direct_course_introduce.getSettings().setDisplayZoomControls(false);
        wv_direct_course_introduce.getSettings().setDomStorageEnabled(true);
        //扩大比例的缩放
        wv_direct_course_introduce.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        wv_direct_course_introduce.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wv_direct_course_introduce.getSettings().setLoadWithOverviewMode(true);
        wv_direct_course_introduce.getSettings().setJavaScriptEnabled(true);
        wv_direct_course_introduce.getSettings().setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wv_direct_course_introduce.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
//        wv_direct_course_introduce.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//可以解决缓存问题,刚测试可用
        DebugUtil.e("课程详情：" + directBean.toString());
        String content = directBean.getContent();
        DebugUtil.e("课程详情：课程介绍" + content);
        wv_direct_course_introduce.getSettings().setDefaultTextEncodingName("UTF-8");
        //webView.loadData(webData, "text/html","UTF-8");
        wv_direct_course_introduce.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
//        wv_direct_teacher_introduce.loadDataWithBaseURL(null, directBean.getBrief(), "text/html", "UTF-8", null);
        GenericDraweeHierarchyBuilder builder1 =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy1 = builder1
                .setFadeDuration(100)
                .setPlaceholderImage(getResources().getDrawable(R.drawable.introduce_teacher_default))
                .setFailureImage(getResources().getDrawable(R.drawable.introduce_teacher_default)).setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        simpleDraweeView.setHierarchy(hierarchy1);
        FrescoUtils.setFrescoImageUri(simpleDraweeView, directBean.getPhoto_url(), R.drawable.introduce_teacher_default);


        if ("1".equals(directBean.getIs_buy())) {
            if ("1".equals(directBean.getIs_ax_Type())) {
                btn_buy.setText(R.string.study);
            } else {
                btn_buy.setText(R.string.study);
            }
        } else {
            btn_buy.setText(R.string.buy);
            //是否线下激活
            if ("1".equals(directBean.getIs_ax_Type())) {
                btn_buy.setText(R.string.activate);
            } else {
                btn_buy.setText(R.string.buy);
            }
        }
        if (directBean != null) {
            //加载筛选的数据
            SendRequest.getSelectAcademic(directBean.getRid(), new ObtainDataFromNetListener<SelectAcademicBean, String>() {

                @Override
                public void onSuccess(SelectAcademicBean res) {
                    selectAcademicBean = res;
                }

                @Override
                public void onFailure(String res) {
                    if (res.equals(SendRequest.ERROR_NETWORK)) {
                        ToastUtils.showToast(R.string.network);
                    } else if (res.equals(SendRequest.ERROR_SERVER)) {
                        ToastUtils.showToast(R.string.server_error);
                    }
                }
            });
        }

    }


    /**
     * 将直播加入我的模块
     */
    public void addDirectForMy() {
        obtainDataListerForAddDirect = new ObtainDataListerForAddDirect(this);
        SendRequest.addDirectForMy(directBean.getRid(), uid, obtainDataListerForAddDirect);
    }

    private class ObtainDataListerForAddDirect extends ObtainDataFromNetListener<String, String> {
        private DirectDetailsActivity weak_activity;

        public ObtainDataListerForAddDirect(DirectDetailsActivity activity) {
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
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("1".equals(res) || "2".equals(res)) {//1 加入成功 2 重复加入
                            //免费加入需要手动设置已经购买。
                            weak_activity.directBean.setIs_buy("1");
                            weak_activity.directBeanListForClassSchedule.clear();
                            weak_activity.loadDirectClasSchedule(true);
                            weak_activity.btn_buy.setText(getResources().getString(R.string.study));
                            weak_activity.mCustomAddDirectDilog.show();
                            weak_activity.mCustomAddDirectDilog.setOkOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    weak_activity.mCustomAddDirectDilog.dismiss();
//                                    if (weak_activity.directBean.getVideoType() == 0) {
//                                        DataStore_Direct.directDatailList = directBeanListForClassSchedule;
//                                        DirectPlayDetailsActivityForBjysdk.newIntent(weak_activity, directBean, 0);
//                                    }else{
//                                        DataStore_Direct.directDatailList = directBeanListForClassSchedule;
//                                        LiveActivityForBjysdk.newIntent(weak_activity,directBean,0);
//                                    }
                                    DataStore_Direct.directDatailList = directBeanListForClassSchedule;
                                    PlayerActivityForBjysdk.newIntent(DirectDetailsActivity.this, directBean, 0, 2);
                                }
                            });
                            weak_activity.mCustomAddDirectDilog.setCancelOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    weak_activity.mCustomAddDirectDilog.dismiss();
                                }
                            });
                        } else {//加入失败
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {

                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                            ;
                        }
                    }
                });
            }
        }
    }

//    /**
//     * 生成订单接口
//     */
//    public void createOrder() {
//        String addressId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_NAME, "");
//        obtainDataListerForCreateOrder = new ObtainDataListerForCreateOrder(this);
//        SendRequest.createOrderForSumit(directBean.getRid(), uid, addressId, "", obtainDataListerForCreateOrder);
//    }
//
//    /**
//     * 生成订单接口回调
//     */
//    private static class ObtainDataListerForCreateOrder extends ObtainDataFromNetListener<String, String> {
//
//        private DirectDetailsActivity weak_activity;
//
//        public ObtainDataListerForCreateOrder(DirectDetailsActivity activity) {
//            weak_activity = new WeakReference<>(activity).get();
//        }
//
//        @Override
//        public void onStart() {
//            super.onStart();
//            if (weak_activity != null) {
//                weak_activity.mCustomLoadingDialog.show();
//            }
//        }
//
//        @Override
//        public void onSuccess(final String res) {
//            if (weak_activity != null) {
//                weak_activity.mCustomLoadingDialog.dismiss();
//                if (!TextUtils.isEmpty(res)) {
//                    weak_activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                       String zhibotime =weak_activity. directBean.getZhibotime() + "-" + weak_activity.directBean.getZhiboendtime() + "(" + weak_activity
// .directBean
//                                    .getLessionCount() + "课时" + ")";
//                            ChoosePayMethodActivity.newIntent(weak_activity, weak_activity.directBean.getActualPrice(), weak_activity.directBean.getTitle()
// , res, zhibotime);
//                        }
//                    });
//                }
//            }
//        }
//
//        @Override
//        public void onFailure(final String res) {
//            if (weak_activity != null) {
//                weak_activity.mCustomLoadingDialog.dismiss();
//                weak_activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (res.equals(SendRequest.ERROR_NETWORK)) {
//                            ToastUtils.showToast(R.string.network);
//                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
//                            ToastUtils.showToast(R.string.server_error);
//                        }
//                    }
//                });
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /** attention to this below ,must add this**/
        UMShareAPI.get(CustomApplication.applicationContext).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //具体的操作代码
            if (isPush == 1 || spalsh_id == 8) {
                back();
//                MainActivity.newIntentFlag(this, "MainActivity");
                HomeActivity.newIntentFlag(this, "HomeActivity", 0);
            } else {
                back();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDirectData();
        DataStore_Direct.resetDatas();
        wv_direct_course_introduce.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wv_direct_course_introduce.pauseTimers();
//        if(isFinishing()){
//            wv_direct_course_introduce.loadUrl("about:blank");
//            setContentView(new FrameLayout(this));
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wv_direct_course_introduce != null) {
            wv_direct_course_introduce.removeAllViews();
            wv_direct_course_introduce.clearHistory();
            ((ViewGroup) wv_direct_course_introduce.getParent()).removeView(wv_direct_course_introduce);
            wv_direct_course_introduce.destroy();
            wv_direct_course_introduce = null;
        }
        DataStore_Direct.directDatailList = null;
    }

    /**
     * 设置textview 的drawable属性
     *
     * @param textView
     * @param drawableId 图片
     * @param text       内容
     * @param type       类型 0网课，显示高清视频 1直播或者录播课 显示时间
     */
    private void setDirectTime(TextView textView, int drawableId, String text, int type) {
        if (type == 0) {
            Drawable drawable = getResources().getDrawable(drawableId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
            textView.setCompoundDrawablePadding(CommonUtils.dip2px(6));
            textView.setTextColor(getResources().getColor(R.color.green004));
            textView.setText(Html.fromHtml(text));
        } else {
            Drawable drawable = getResources().getDrawable(drawableId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
            textView.setCompoundDrawablePadding(CommonUtils.dip2px(6));
            textView.setCompoundDrawables(drawable, null, null, null);
            textView.setTextColor(getResources().getColor(R.color.gray013));
            textView.setText(text);
        }

    }

    /**
     * 赠品弹窗
     */
    public void PopGift(View v) {
        View inflate = getLayoutInflater().inflate(R.layout.item_pop_equivalent, null);
        final PopupWindow mPopupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(false);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        inflate.findViewById(R.id.iv_canle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        ListView lv_pop = (ListView) inflate.findViewById(R.id.lv_pop);
        directGiftAdapter = new DirectGiftAdapter(this, directBean.getGift());
        lv_pop.setAdapter(directGiftAdapter);
        BitmapDrawable bitmapDrawable = new BitmapDrawable();
        mPopupWindow.setBackgroundDrawable(bitmapDrawable);
        mPopupWindow.showAtLocation(rl_gift, Gravity.BOTTOM, 0, 0);
//        mPopupWindow.showAsDropDown(rl_pay, 0, 0);
    }

    /**
     * 激活
     */
    public void PopVation(View v) {
        View inflate = getLayoutInflater().inflate(R.layout.pop_jihuo_direct, null);
        final PopupWindow mPopupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(false);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        inflate.findViewById(R.id.iv_canle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        et_accode_directdetail = (EditText) inflate.findViewById(R.id.et_accode_directdetail);
        et_accode_directdetail.setOnClickListener(this);
        inflate.findViewById(R.id.btn_activate).setOnClickListener(this);
        BitmapDrawable bitmapDrawable = new BitmapDrawable();
        mPopupWindow.setBackgroundDrawable(bitmapDrawable);
        mPopupWindow.showAtLocation(rl_gift, Gravity.CENTER, 0, 0);
//        mPopupWindow.showAsDropDown(rl_pay, 0, 0);
    }
}
