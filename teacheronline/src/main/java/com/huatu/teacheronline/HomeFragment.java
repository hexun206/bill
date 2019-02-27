package com.huatu.teacheronline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.greendao.DirectBean;
import com.huatu.teacheronline.CCVideo.InterviewVideoListActivity;
import com.huatu.teacheronline.adapter.AdLoopAdapter;
import com.huatu.teacheronline.adapter.CoursesAdapter;
import com.huatu.teacheronline.adapter.HourAdapter;
import com.huatu.teacheronline.adapter.PraiseAdapter;
import com.huatu.teacheronline.bean.AdBean;
import com.huatu.teacheronline.bean.PersonalInfoBean;
import com.huatu.teacheronline.bean.PopupAdsBean;
import com.huatu.teacheronline.bean.PraiseBean;
import com.huatu.teacheronline.bean.RecomBean;
import com.huatu.teacheronline.direct.DirectDetailsActivity;
import com.huatu.teacheronline.direct.DirectHomePageActivity;
import com.huatu.teacheronline.direct.PlayerActivityForBjysdk;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.ExerciseDeductionActivity;
import com.huatu.teacheronline.exercise.ExerciseModeActivity;
import com.huatu.teacheronline.exercise.ModuleExerciseActivity;
import com.huatu.teacheronline.personal.CalendarsignActivity;
import com.huatu.teacheronline.personal.GoldPersonalActivity;
import com.huatu.teacheronline.personal.H5EncycloActivity;
import com.huatu.teacheronline.personal.MyDirectActivity;
import com.huatu.teacheronline.personal.bean.TodayEntity;
import com.huatu.teacheronline.sensorsdata.TrackUtil;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.utils.ShareUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.vipexercise.MyDataActivity;
import com.huatu.teacheronline.vipexercise.VipQuestionsActivity;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.CustomListView;
import com.huatu.teacheronline.widget.HuaTuGridView;
import com.huatu.teacheronline.widget.rollviewpager.RollPagerView;
import com.huatu.teacheronline.widget.rollviewpager.hintview.IconHintView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 * Created by ply on 2017/8/5.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private RollPagerView img_mian_adpic;
    private String uid;
    private TextView tv_gold;
    private TextView tv_integral;
    private TextView tv_questions;
    private TextView tv_video;
    private TextView tv_myclass;
    private RelativeLayout rl_moudular;
    private LinearLayout rl_questions;
    private LinearLayout rl_myclass;
    private LinearLayout rl_video;
    private int QRC_REQUEST_CODE = 101;//扫描二维码
    private AlertDialog builder_code_url;
    private AlertDialog builder_code;
    private String result;
    private HuaTuGridView gv_course;
    private TextView iv_mydata;
    private String province;
    private List<RecomBean> recombean = new ArrayList<>();//推荐课程
    private List<PraiseBean> Praisebean = new ArrayList<>();//学员好评
    private List<TodayEntity> hourEntities = new ArrayList<>();//今日24小时直播
    private List<TodayEntity> hourNextities = new ArrayList<>();//明日24小时直播

    private List<String> H5bean = new ArrayList<>();//学员好评
    private CoursesAdapter adapter;
    private HuaTuGridView lv_praise;
    private PraiseAdapter praiseAdapter;
    private CustomAlertDialog mCustomLoadingDialog;
    private String time_date;//当前时间
    private String sp_time_data;//sp保存的时间
    private ArrayList<PopupAdsBean> popupAdsBeans = new ArrayList<>();//广告数组
    private AlertDialog builder_advert;
    private RelativeLayout rl_my_class;
    private String Iscourses;
    private SimpleDraweeView sdv_title_face;
    private TextView tv_home_title;
    private SimpleDraweeView img_home_living;
    private CustomListView lv_hour;
    private HourAdapter hourAdapter;
    private CustomListView lv_next_hour;
    private HourAdapter hourNextAdapter;
    private String class_result;
    private TextView tv_myclass_num;
    private TextView tv_notice_num;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, R.layout.fragment_home_layout);
    }

    @Override
    public void initView() {
        super.initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getActivity().getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        province = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PROVINCE_ID, "86");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        time_date = sDateFormat.format(new java.util.Date());//当天时间，用来判断广告一天弹一次
        mCustomLoadingDialog = new CustomAlertDialog(getActivity(), R.layout.dialog_loading_custom);
        img_mian_adpic = (RollPagerView) findViewById(R.id.img_mian_adpic);
        tv_gold = (TextView) findViewById(R.id.tv_mygold);
        rl_moudular = (RelativeLayout) findViewById(R.id.rl_Modular);
        tv_integral = (TextView) findViewById(R.id.tv_myintegral);
        tv_questions = (TextView) findViewById(R.id.tv_Questions);//题库
        tv_video = (TextView) findViewById(R.id.tv_video);//直播视频
        tv_myclass = (TextView) findViewById(R.id.tv_myclass);//课程
        rl_questions = (LinearLayout) findViewById(R.id.rl_Questions);//题库布局
        rl_myclass = (LinearLayout) findViewById(R.id.rl_myclass);
        iv_mydata = (TextView) findViewById(R.id.iv_mydata);//我的资料
        rl_video = (LinearLayout) findViewById(R.id.rl_video);//直播视频布局
        gv_course = (HuaTuGridView) findViewById(R.id.gv_course);//课程推荐列表
        lv_praise = (HuaTuGridView) findViewById(R.id.lv_praise);//学员好评列表
        rl_my_class = (RelativeLayout) findViewById(R.id.rl_my_class);
        sdv_title_face = (SimpleDraweeView) findViewById(R.id.sdv_title_face);//首页背景图
        tv_home_title = (TextView) findViewById(R.id.tv_home_title);
        img_home_living = (SimpleDraweeView) findViewById(R.id.img_home_living);
        lv_hour = (CustomListView) findViewById(R.id.lv_hour);//24小时直播列表 今日
        lv_next_hour = (CustomListView) findViewById(R.id.lv_next_hour);//24小时直播列表 明日

        tv_myclass_num = (TextView) findViewById(R.id.tv_myclass_num);
        tv_notice_num = (TextView) findViewById(R.id.tv_notice_num);
        setListener();
        Iscourses = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_NON_STUDENT, "0");
        if (Iscourses.equals("0")) {
            rl_my_class.setVisibility(View.GONE);
            rl_moudular.setBackgroundResource(R.drawable.topbar_ii_i);
        } else if (Iscourses.equals("1")) {
            rl_my_class.setVisibility(View.VISIBLE);
            rl_moudular.setBackgroundResource(R.drawable.topbar_1);
        }


        //神策track click
        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_moudle), TrackUtil.TYPE_ICON, "模块题海");
        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_deduction), TrackUtil.TYPE_ICON, "真题演练");
        TrackUtil.trackClick(getActivity(), findViewById(R.id.iv_mode), TrackUtil.TYPE_ICON, "在线模考");

        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_Liveclass), TrackUtil.TYPE_ICON, "直播课程");
        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_Networkclass), TrackUtil.TYPE_ICON, "高清网课");
        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_information), TrackUtil.TYPE_ICON, "教辅资料");
        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_video_interview), TrackUtil.TYPE_ICON, "面试视频");

        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_class_notice), TrackUtil.TYPE_ICON, "课程通知");
        TrackUtil.trackClick(getActivity(), findViewById(R.id.iv_mydata), TrackUtil.TYPE_ICON, "我的资料");

        TrackUtil.trackClick(getActivity(), findViewById(R.id.rl_my_gold_home), TrackUtil.TYPE_LINK, "我的金币");
        TrackUtil.trackClick(getActivity(), findViewById(R.id.rl_my_integral_home), TrackUtil.TYPE_LINK, "我的积分");
        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_more_account), TrackUtil.TYPE_LINK, "更多");

        TrackUtil.trackClick(getActivity(), findViewById(R.id.iv_play_video), TrackUtil.TYPE_SHORTCUT, "当前直播");
        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_detail_intro), TrackUtil.TYPE_CHANGE_TAB, "今日直播");
        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_detail_text), TrackUtil.TYPE_CHANGE_TAB, "明日直播");

        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_teacher_recruit), TrackUtil.TYPE_ICON, "教师招聘");
        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_teacher_certificate), TrackUtil.TYPE_ICON, "教师资格证");
        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_special_teacher), TrackUtil.TYPE_ICON, "特岗教师");

        //in adapter
//        TrackUtil.trackClick(getActivity(),  findViewById(R.id.iv_mode), TrackUtil.TYPE_CARD, "推荐课程");
        TrackUtil.trackClick(getActivity(), findViewById(R.id.tv_very), TrackUtil.TYPE_LINK, "更多");


    }

    public void setListener() {
        findViewById(R.id.tv_deduction).setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);
        findViewById(R.id.v_scanning).setOnClickListener(this);
        findViewById(R.id.v_sign).setOnClickListener(this);
        findViewById(R.id.iv_mode).setOnClickListener(this);
        findViewById(R.id.tv_Liveclass).setOnClickListener(this);
        findViewById(R.id.tv_Networkclass).setOnClickListener(this);
        findViewById(R.id.tv_information).setOnClickListener(this);
        findViewById(R.id.tv_moudle).setOnClickListener(this);
        findViewById(R.id.rl_my_integral_home).setOnClickListener(this);
        findViewById(R.id.rl_my_gold_home).setOnClickListener(this);
        findViewById(R.id.tv_more_account).setOnClickListener(this);
        findViewById(R.id.tv_class_notice).setOnClickListener(this);
        findViewById(R.id.tv_teacher_recruit).setOnClickListener(this);
        findViewById(R.id.tv_teacher_certificate).setOnClickListener(this);
        findViewById(R.id.tv_special_teacher).setOnClickListener(this);
        findViewById(R.id.rl_one).setOnClickListener(this);
        findViewById(R.id.rl_two).setOnClickListener(this);
        findViewById(R.id.rl_three).setOnClickListener(this);
        findViewById(R.id.rl_four).setOnClickListener(this);
        findViewById(R.id.tv_very).setOnClickListener(this);
        findViewById(R.id.rl_questions).setOnClickListener(this);
        findViewById(R.id.rl_video_direc).setOnClickListener(this);

        rl_my_class.setOnClickListener(this);

//        tv_questions.setOnClickListener(this);
//        tv_video.setOnClickListener(this);
//        tv_myclass.setOnClickListener(this);
        iv_mydata.setOnClickListener(this);
        findViewById(R.id.iv_vip_quesr).setOnClickListener(this);
        findViewById(R.id.tv_video_interview).setOnClickListener(this);
//        findViewById(R.id.tv_More).setOnClickListener(this);
        gv_course.setOnItemClickListener(this);

        findViewById(R.id.tv_detail_intro).setOnClickListener(this);
        findViewById(R.id.tv_detail_text).setOnClickListener(this);

        findViewById(R.id.iv_play_video).setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
    }

    public void RecommendData() {
        //课程推荐
        RecomObtainDataLister recomObtainDataLister = new RecomObtainDataLister(this);
        SendRequest.getRecommend(province, recomObtainDataLister);
    }


    @Override
    public void onResume() {
        super.onResume();
        sp_time_data = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_TIME_DATA, "");
        //用户信息
        Display mDisplay = getActivity().getWindowManager().getDefaultDisplay();
        int width = mDisplay.getWidth();
        int height = mDisplay.getHeight();
        ObtainDataLister obtatinDataListener = new ObtainDataLister(this);
        SendRequest.getPersonalInfoV2(uid, String.valueOf(width), String.valueOf(height), obtatinDataListener);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_very:
                DirectHomePageActivity.newIntent(getActivity(), 1);
                break;
            case R.id.tv_Liveclass:
                //直播
                DirectHomePageActivity.newIntent(getActivity(), 1);
                break;
            case R.id.rl_my_integral_home:
            case R.id.rl_my_gold_home:
            case R.id.tv_more_account:
                //金币 积分
                GoldPersonalActivity.newIntent(getActivity());
                break;
            case R.id.tv_Networkclass:
                //网课
                DirectHomePageActivity.newIntent(getActivity(), 0);
                break;
            case R.id.tv_information:
                //教辅资料
                DirectHomePageActivity.newIntent(getActivity(), 2);
                break;
            case R.id.tv_moudle:
                //模块题海
                ModuleExerciseActivity.newIntent(getActivity());
                break;
            case R.id.rl_questions:
                //vip题库
                if (Iscourses.equals("1")) {
                    rl_moudular.setBackgroundResource(R.drawable.topbar_1);
                    rl_questions.setVisibility(View.VISIBLE);
                    rl_myclass.setVisibility(View.GONE);
                    rl_video.setVisibility(View.GONE);
                } else if (Iscourses.equals("0")) {
                    rl_moudular.setBackgroundResource(R.drawable.topbar_ii_i);
                    rl_questions.setVisibility(View.VISIBLE);
                    rl_myclass.setVisibility(View.GONE);
                    rl_video.setVisibility(View.GONE);
                }

                break;
            case R.id.rl_video_direc:
                //面试视频
                if (Iscourses.equals("1")) {
                    rl_moudular.setBackgroundResource(R.drawable.topbar_2);
                    rl_questions.setVisibility(View.GONE);
                    rl_myclass.setVisibility(View.GONE);
                    rl_video.setVisibility(View.VISIBLE);
                } else if (Iscourses.equals("0")) {
                    rl_moudular.setBackgroundResource(R.drawable.topbar_ii_ii);
                    rl_questions.setVisibility(View.GONE);
                    rl_myclass.setVisibility(View.GONE);
                    rl_video.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.rl_my_class:
                //我的课程
                rl_moudular.setBackgroundResource(R.drawable.topbar_3);
                rl_questions.setVisibility(View.GONE);
                rl_myclass.setVisibility(View.VISIBLE);
                rl_video.setVisibility(View.GONE);
                break;
            case R.id.iv_share:
                //分享
                MobclickAgent.onEvent(getActivity(), "shareOnclik");
//                ShareUtils.share(this, ShareUtils.url_appdownload_qq, ShareUtils.content_share, ShareUtils.title_share, true);
                ShareUtils.popShare(getActivity(), ShareUtils.url_appdownload_qq, ShareUtils.content_share, ShareUtils.title_share, true);
                break;
            case R.id.v_sign:
                //签到界面
                startActivity(new Intent(getActivity(), CalendarsignActivity.class));
                break;
            case R.id.v_scanning:
                //二维码
                Intent intent22 = new Intent(getActivity(), CodestyleActivity.class);
                startActivityForResult(intent22, QRC_REQUEST_CODE);
                break;
            case R.id.tv_confirm_login:
                //二维码确认登录
                String account = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, "");
                ObtainDatacode obewm = new ObtainDatacode(this);
                SendRequest.get_barcode(account, result, obewm);
                builder_code.cancel();
                break;
            case R.id.tv_deduction:
                //真题演练
                ExerciseDeductionActivity.newIntent(getActivity());
                break;
            case R.id.iv_mode:
                //在线模考
                ExerciseModeActivity.newIntent(getActivity());
//                ExerciseErrorCenterActivity.newIntent(getActivity());
                break;
            case R.id.iv_mydata:
                //我的资料
                MyDataActivity.newIntent(getActivity());
                break;
            case R.id.iv_vip_quesr:
                //VIP题库
                VipQuestionsActivity.newIntent(getActivity());
                break;
            case R.id.tv_video_interview:
                MobclickAgent.onEvent(getActivity(), "interviewVideo");
                //面试视频getActivity()
                Intent intent = new Intent(getActivity(), InterviewVideoListActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_class_notice:
                MobclickAgent.onEvent(getActivity(), "myLiveClass");
//                //我的课程
                MyDirectActivity.newIntent(getActivity(), "0");
                break;
//            case R.id.tv_More:
//                //金币账户
//                GoldPersonalActivity.newIntent(getActivity());
//                break;
            case R.id.tv_teacher_recruit:
                if (H5bean.size() != 0) {
                    H5EncycloActivity.newIntent(getActivity(),
                            "教师招聘百科", H5bean.get(0).toString(), H5bean.get(1).toString(), H5bean.get(2).toString());
                }
                break;
            case R.id.tv_teacher_certificate:
                if (H5bean.size() != 0) {
                    H5EncycloActivity.newIntent(getActivity(), "教师资格证百科",
                            H5bean.get(3).toString(), H5bean.get(4).toString(), H5bean.get(5).toString());
                }
                break;
            case R.id.tv_special_teacher:
                if (H5bean.size() != 0) {
                    H5EncycloActivity.newIntent(getActivity(), "特岗教师百科",
                            H5bean.get(6).toString(), H5bean.get(7).toString(), H5bean.get(8).toString());
                }
                break;
            case R.id.rl_one:
                if (H5bean.size() != 0) {
                    H5DetailActivity.newIntent(getActivity(), "华图教师", H5bean.get(9).toString());
                }
                break;
            case R.id.rl_two:
                if (H5bean.size() != 0) {
                    H5DetailActivity.newIntent(getActivity(), "华图教师", H5bean.get(10).toString());
                }
                break;
            case R.id.rl_three:
                if (H5bean.size() != 0) {
                    H5DetailActivity.newIntent(getActivity(), "华图教师", H5bean.get(11).toString());
                }
                break;
            case R.id.rl_four:
                if (H5bean.size() != 0) {
                    H5DetailActivity.newIntent(getActivity(), "华图教师", H5bean.get(12).toString());
                }
                break;
            case R.id.rl_canle:
                builder_advert.cancel();
                break;
            case R.id.sdv_draweeView:
                if (popupAdsBeans.get(0).getType().equals("1")) {
                    H5DetailActivity.newIntent(getActivity(), popupAdsBeans.get(0).getResource(), 8, popupAdsBeans.get(0).getTitle());
                    Intent intent2 = new Intent(getActivity(), H5DetailActivity.class);
                    intent2.putExtra("ad_url", popupAdsBeans.get(0).getResource());
                    intent2.putExtra("splash_id", 8);
                    intent2.putExtra("ad_title", popupAdsBeans.get(0).getTitle());
                    intent2.putExtra("is_array", "1");
                    startActivity(intent2);
                } else if (popupAdsBeans.get(0).getType().equals("2")) {
                    DirectDetailsActivity.newIntent(getActivity(), popupAdsBeans.get(0).getResource(), 8);
                }
                builder_advert.cancel();
                getActivity().finish();
                break;

            case R.id.tv_detail_text:
                lv_hour.setVisibility(View.GONE);
                lv_next_hour.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_detail_intro:
                lv_hour.setVisibility(View.VISIBLE);
                lv_next_hour.setVisibility(View.GONE);
                break;
            case R.id.iv_play_video:
                DirectBean directBean = new DirectBean();
                directBean.setRid("-1");
                directBean.setTitle("24小时大咖直播");
                directBean.setIs_fufei("1");
                directBean.setIs_buy("1");
                directBean.setIsTrial(0);
//                directBean.setCustomer("http://chat.looyuoms.com/chat/chat/p.do?c=20001211&f=10062439&g=10059670&refer=appsh");
//                DirectPlayDetailsActivityForRtsdk.newIntent(getActivity(), directBean, 0, 2,1);
                PlayerActivityForBjysdk.newIntent(getActivity(), directBean, 0, 2, 1);
                break;
            case R.id.rl_dialog_canle:
                builder_code_url.cancel();
                break;
        }
    }


    /**
     * 初始化轮播图
     */
    public void initCarsuelView(ArrayList<AdBean> adBeans) {
        img_mian_adpic.setPlayDelay(3300);
        AdLoopAdapter mLoopAdapter = new AdLoopAdapter(img_mian_adpic, adBeans, getActivity());
        img_mian_adpic.setAdapter(mLoopAdapter);
        img_mian_adpic.setHintView(new IconHintView(getActivity(), R.drawable.ad_red_circleviewindicator, R.drawable.ad_white_circleviewindicator));
    }

    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DirectDetailsActivity.newIntent(getActivity(), recombean.get(position).getRid() + "");
    }

    /****
     * 实时请求用户信息
     *****/
    private class ObtainDataLister extends ObtainDataFromNetListener<PersonalInfoBean, String> {
        private HomeFragment weak_activity;

        public ObtainDataLister(HomeFragment activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                if (CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ONE_LOADING, "1").equals("1")) {
                    weak_activity.mCustomLoadingDialog.show();
                    CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_ONE_LOADING, "2");
                }

            }
        }

        @Override
        public void onSuccess(final PersonalInfoBean res) {
            if (weak_activity != null && weak_activity.getActivity() != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                if (res != null) {
                    weak_activity.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //更新金币信息
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_GOLD, res.getGold());
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, res.getId());
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_POINT, res.getUserPoint());
                            popupAdsBeans.add(res.getPopup_ads());
                            if (StringUtils.isEmpty(CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PROVINCE_NAME, ""))) {
                                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_PROVINCE_NAME, res.getProvinceName());
                            }
                            if (StringUtils.isEmpty(CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_NAME, ""))) {
                                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_NAME, res.getCityName());
                            }
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_FIRST_PASS, res.getFirst_pass());
                            if (res.getPopup_ads() != null && !res.getPopup_ads().getUrl().equals("")) {
                                if (!time_date.equals(sp_time_data)) {
                                    CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_TIME_DATA, time_date);
                                    DialogAdvert();
                                }
                            }
                            if (res.getface_courses() != null) {
                                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_NON_STUDENT, res.getface_courses());
                                if (res.getface_courses().equals("0")) {
                                    rl_my_class.setVisibility(View.GONE);
                                } else if (res.getface_courses().equals("1")) {
                                    rl_my_class.setVisibility(View.VISIBLE);
                                }

                            }
                            if (res.getfacesum() == 0) {
                                tv_myclass_num.setVisibility(View.GONE);
                                tv_notice_num.setVisibility(View.GONE);
                            } else {
                                tv_myclass_num.setVisibility(View.VISIBLE);
                                tv_notice_num.setVisibility(View.VISIBLE);
                                tv_myclass_num.setText(res.getfacesum() + "");
                                tv_notice_num.setText(res.getfacesum() + "");
                            }
                            Iscourses = res.getface_courses();
                            tv_gold.setText(res.getGold());
                            tv_integral.setText(res.getUserPoint());
                            initCarsuelView(res.getAdvertising());
                            initHierarchy(res.getim_pic(), res.gethour().getPic());
                            tv_home_title.setText(res.gettitle());
                            SetHourAdapter(res);
                            RecommendData();

                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            weak_activity.mCustomLoadingDialog.dismiss();
            if (weak_activity != null && weak_activity.getActivity() != null) {
                weak_activity.getActivity().runOnUiThread(new Runnable() {
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
     * 扫描二维码弹出对话框
     */
    private void Dialog_code() {
        builder_code = new AlertDialog.Builder(getActivity(), R.style.Dialog_Fullscreen).create();//让对话框全屏
        builder_code.setCancelable(false);
        builder_code.show();
        Window window = builder_code.getWindow();
        window.setContentView(R.layout.scan_code_login);
        TextView tv_code_canle = (TextView) window.findViewById(R.id.tv_code_canle);
        tv_code_canle.setOnClickListener(this);
        TextView tv_confirm_login = (TextView) window.findViewById(R.id.tv_confirm_login);
        tv_confirm_login.setOnClickListener(this);
        PercentRelativeLayout rl_dialog_left = (PercentRelativeLayout) window.findViewById(R.id.rl_dialog_left);
        rl_dialog_left.setOnClickListener(this);
    }

    /**
     * 扫描二维码弹出网页
     */
    private void Dialog_url_code() {
        ToastUtils.showToast("请稍等。。。");
        builder_code_url = new AlertDialog.Builder(getActivity(), R.style.Dialog_Fullscreen).create();//让对话框全屏
        builder_code_url.setCancelable(false);
        builder_code_url.show();
        Window window = builder_code_url.getWindow();
        window.setContentView(R.layout.dialog_url_code);
        WebView wb_url = (WebView) window.findViewById(R.id.cwq_web);
        final TextView tv_web_title = (TextView) window.findViewById(R.id.tv_main_title);
        WebSettings mSettings = wb_url.getSettings();
        mSettings.setUseWideViewPort(true);
        mSettings.setJavaScriptEnabled(true);
        mSettings.setLoadWithOverviewMode(true);
        wb_url.loadUrl(result);
        wb_url.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        // 设置setWebChromeClient对象
        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                tv_web_title.setText(title);
            }
        };
        wb_url.setWebChromeClient(wvcc);
        RelativeLayout rl_dialog_canle = (RelativeLayout) window.findViewById(R.id.rl_dialog_canle);
        rl_dialog_canle.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == QRC_REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    result = bundle.getString(CodeUtils.RESULT_STRING);
                    String Intercept_result = result.substring(0, 4);
                    if (result.length() > 4) {
                        class_result = result.substring(4, result.length());
                    }
//                   Intercept_result前缀为jszx二维码扫描登录，jskc，二维码跳转对应课程 否则跳转至网页
                    if (Intercept_result.equals("jszx")) {
                        Dialog_code();
                    } else if (Intercept_result.equals("jskc")) {
                        DirectDetailsActivity.newIntent(getActivity(), class_result);
                    } else {
                        Dialog_url_code();
                    }
//                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getActivity(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
        /** attention to this below ,must add this**/
        UMShareAPI.get(CustomApplication.applicationContext).onActivityResult(requestCode, resultCode, data);
    }

    private class ObtainDatacode extends ObtainDataFromNetListener<String, String> {
        private HomeFragment weak_activity;

        public ObtainDatacode(HomeFragment activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(res);
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            this.weak_activity.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (res.equals(SendRequest.ERROR_NETWORK)) {
                        ToastUtils.showToast(R.string.network);
                    } else if (res.equals(SendRequest.ERROR_SERVER)) {
                        ToastUtils.showToast(R.string.server_error);
                    } else {
                        ToastUtils.showToast(res);
                    }
                }
            });
        }
    }


    /****
     * 推荐课程 学员好评 百科链接
     *****/
    private class RecomObtainDataLister extends ObtainDataFromNetListener<PersonalInfoBean, String> {
        private HomeFragment weak_activity;

        public RecomObtainDataLister(HomeFragment activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onSuccess(final PersonalInfoBean res) {
            if (weak_activity != null) {
                if (res != null) {
                    weak_activity.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SetRecomAdapter(res);
                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.getActivity().runOnUiThread(new Runnable() {
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

    public void SetHourAdapter(PersonalInfoBean res) {
        hourEntities.clear();
        hourNextities.clear();
        if (res.gethour().getToday() != null) {
            hourEntities.addAll(res.gethour().getToday());
        }
        if (res.gethour().getNext() != null) {
            hourNextities.addAll(res.gethour().getNext());
        }
        if (hourNextAdapter != null) {
            hourNextAdapter.notifyDataSetChanged();
        } else {
            hourNextAdapter = new HourAdapter(getActivity(), hourNextities, 0);
            lv_next_hour.setAdapter(hourNextAdapter);
        }

        if (hourAdapter != null) {
            hourAdapter.notifyDataSetChanged();
        } else {
            hourAdapter = new HourAdapter(getActivity(), hourEntities, 1);
            lv_hour.setAdapter(hourAdapter);
        }
    }

    public void SetRecomAdapter(PersonalInfoBean res) {
        recombean.clear();
        Praisebean.clear();
        recombean.addAll(res.getrecommend());
        Praisebean.addAll(res.getpraise());

        if (res.getbaike() != null) {
            H5bean.addAll(res.getbaike());
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new CoursesAdapter(getActivity(), recombean);
            gv_course.setAdapter(adapter);
        }

        if (praiseAdapter != null) {
            praiseAdapter.notifyDataSetChanged();
        } else {
            praiseAdapter = new PraiseAdapter(getActivity(), Praisebean);
            lv_praise.setAdapter(praiseAdapter);
        }
    }

    /**
     * 首页广告
     */
    private void DialogAdvert() {
        builder_advert = new AlertDialog.Builder(getActivity(), R.style.Dialog_Fullscreenx).create();
        builder_advert.setCancelable(false);
        builder_advert.show();
        Window window = builder_advert.getWindow();
        window.setContentView(R.layout.dialog_advertisement);
        RelativeLayout iv_canle = (RelativeLayout) window.findViewById(R.id.rl_canle);
        iv_canle.setOnClickListener(this);
        SimpleDraweeView sdv_adver = (SimpleDraweeView) window.findViewById(R.id.sdv_draweeView);
        sdv_adver.setOnClickListener(this);
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        sdv_adver.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(sdv_adver, popupAdsBeans.get(0).getUrl(), R.drawable.ic_loading);
    }

    private void initHierarchy(String url, String hoururl) {
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(getResources().getDrawable(R.drawable.hp_pic_0), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(getResources().getDrawable(R.drawable.hp_pic_0), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        sdv_title_face.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(sdv_title_face, url, R.drawable.hp_pic_0);

        GenericDraweeHierarchyBuilder builder1 =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy1 = builder1
                .setFadeDuration(200)
                .setPlaceholderImage(getResources().getDrawable(R.drawable.pic_zb_play), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(getResources().getDrawable(R.drawable.pic_zb_play), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        img_home_living.setHierarchy(hierarchy1);
        FrescoUtils.setFrescoImageUri(img_home_living, hoururl, R.drawable.hp_pic_0);
    }
}
