package com.huatu.teacheronline.direct;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huatu.teacheronline.BaseFragmentActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.fragment.DirectAllTabFragment;
import com.huatu.teacheronline.direct.fragment.DirectMyTabFragment;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 直播模块
 * Created by ply on 2016/1/5.
 */
public class DirectHomePageActivity extends BaseFragmentActivity {
    private RelativeLayout rl_main_left;
    private LinearLayout ll_main_title;
    private TextView tv_main_all, tv_main_my, tv_main_all_touch, tv_main_my_touch;
    private RelativeLayout rl_root;

    private DirectAllTabFragment directAllTabFragment;
    private DirectMyTabFragment directMyTabFragment;
    private FragmentManager manager;
    private Map<String, Fragment> mapFragmet = new HashMap<>();

    //筛选
    private ScrollView sl_select;
    public LinearLayout ll_test_categories, ll__interview, ll_learning_period, ll_subjects, ll_class_type;
    private RelativeLayout rl_test_categories, rl_interview, rl_learning_period, rl_subjects;
    private String[] test_categories, learning_periods, wy_subjects, wp_subjects, wh_subjects, i_subjects, recruitment_wy_subjects, tw_middle_subject,test_materialss,
            career_subjects,
            recruitment_wp_subjects, recruitment_iy_subjects, charge_types, class_types;
    public RadioGroup gv_test_categories, gv_interview, gv_subjects, gv_learning_period, gv_class_type;//筛选 考试类别 笔面试 学段 学科 收费类型
    private String[] subjects;
    public static String categorie = "", interview = "", period = "", subject = "", ActualPrice = "", class_type = "";
    private String categorie_ = "", interview_ = "", period_ = "", subject_ = "", ActualPrice_ = "", class_type_ = "";
    int pad1 = CommonUtils.dip2px(5);
    int pad2 = CommonUtils.dip2px(6);
    public DrawerLayout drawer;
    private RelativeLayout drawer_view_right;
    private int videoType;//0 高清网课 1 直播  2 教辅资料
    private boolean isToMe; //是否跳转到我的

    @Override
    public void initView() {
        setContentView(R.layout.activity_direct_homepager);
        videoType = getIntent().getIntExtra("videoType", 0);
        isToMe = getIntent().getBooleanExtra("isToMe", false);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        ll_main_title = (LinearLayout) findViewById(R.id.ll_main_title);
        tv_main_all = (TextView) findViewById(R.id.tv_main_all);
        tv_main_my = (TextView) findViewById(R.id.tv_main_my);
        tv_main_all_touch = (TextView) findViewById(R.id.tv_main_all_touch);
        tv_main_my_touch = (TextView) findViewById(R.id.tv_main_my_touch);

        rl_root = (RelativeLayout) findViewById(R.id.rl_root);

        directAllTabFragment = new DirectAllTabFragment();
        directMyTabFragment = new DirectMyTabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("videoType", videoType);
        directAllTabFragment.setArguments(bundle);
        directMyTabFragment.setArguments(bundle);
        manager = getFragmentManager();
        manager.beginTransaction().add(R.id.rl_root, directAllTabFragment).commit();
        mapFragmet.put("fragment", directAllTabFragment);

        //筛选相关
        drawer_view_right = (RelativeLayout) findViewById(R.id.drawer_view_right);
        sl_select = (ScrollView) findViewById(R.id.sv_select);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//关闭侧滑
        iniSelectView();
        if (isToMe) {
            toMe();
        }
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        tv_main_all_touch.setOnClickListener(this);
        tv_main_my_touch.setOnClickListener(this);
        findViewById(R.id.rl_main_right).setOnClickListener(this);
        findViewById(R.id.btn_commit_select).setOnClickListener(this);
        findViewById(R.id.btn_cancle_select).setOnClickListener(this);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {


            }

            @Override
            public void onDrawerClosed(View drawerView) {
                iniSelectData(gv_test_categories, categorie, test_categories);
//                if (!StringUtils.isEmpty(interview)){
//                    iniSelectData(gv_interview, interview, interviews);
//                    ll__interview.setVisibility(View.VISIBLE);
//                    ll_learning_period.setVisibility(View.VISIBLE);
//                }else {
//                    ll__interview.setVisibility(View.GONE);
//                }
                if (!StringUtils.isEmpty(period)) {
                    iniSelectData(gv_learning_period, period, learning_periods);
                    ll_learning_period.setVisibility(View.VISIBLE);
                    ll_class_type.setVisibility(View.VISIBLE);
                } else {
                    ll_learning_period.setVisibility(View.GONE);
                }
                if (!StringUtils.isEmpty(class_type)) {
                    iniSelectData(gv_class_type, class_type, class_types);
                    ll_class_type.setVisibility(View.VISIBLE);
                    ll_subjects.setVisibility(View.VISIBLE);
                } else {
                    ll_class_type.setVisibility(View.GONE);
                }
                if (!StringUtils.isEmpty(subject)) {
                    setSubjectSelector(subject);
                } else {
                    ll_subjects.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    @Override
    public boolean back() {
        finish();
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.rl_main_right:
                DirectSearchActivity.newIntent(this,videoType);
                break;
            case R.id.tv_main_all_touch://全部
                if (directAllTabFragment != null && directAllTabFragment.isVisible()) {
                    return;
                } else {
                    MobclickAgent.onEvent(this, "allCourses");
                    if (directAllTabFragment.isAdded()) {
                        manager.beginTransaction().hide(mapFragmet.get("fragment")).show(directAllTabFragment).commit();
                    } else {
                        manager.beginTransaction().hide(mapFragmet.get("fragment")).add(R.id.rl_root, directAllTabFragment).commit();
                    }
                    ll_main_title.setBackgroundResource(R.drawable.bg_topbar_direct_all);
                    tv_main_all.setTextColor(getResources().getColor(R.color.white));
                    tv_main_my.setTextColor(getResources().getColor(R.color.green001));
                    mapFragmet.put("fragment", directAllTabFragment);
                }
                break;
            case R.id.tv_main_my_touch://我的
                toMe();
                break;
            case R.id.btn_commit_select:
                drawer.closeDrawers();
                categorie = categorie_;
                interview = interview_;
                class_type = class_type_;
                period = period_;
                subject = subject_;
                directAllTabFragment.screeningCommit();
                break;
            case R.id.btn_cancle_select:
                drawer.closeDrawers();
                break;
            case R.id.rl_test_categories:
                expandGridview(gv_test_categories);
                break;
            case R.id.rl_interview:
                expandGridview(gv_interview);
                break;
            case R.id.rl_subjects:
                expandGridview(gv_subjects);
                break;
            case R.id.rl_learning_period:
                expandGridview(gv_learning_period);
                break;
        }
    }

    /**
     * 去我的页面
     */
    private void toMe() {
        if (directMyTabFragment != null && directMyTabFragment.isVisible()) {
            return;
        } else {
            MobclickAgent.onEvent(this, "myCourses");
            if (directMyTabFragment.isAdded()) {
                manager.beginTransaction().hide(mapFragmet.get("fragment")).show(directMyTabFragment).commit();
            } else {
                manager.beginTransaction().hide(mapFragmet.get("fragment")).add(R.id.rl_root, directMyTabFragment).commit();
            }
            ll_main_title.setBackgroundResource(R.drawable.bg_topbar_direct_my);
            tv_main_all.setTextColor(getResources().getColor(R.color.green001));
            tv_main_my.setTextColor(getResources().getColor(R.color.white));
            mapFragmet.put("fragment", directMyTabFragment);
        }
    }

    /**
     * @param context
     * @param videoType 0 高清网课 1 直播  2 教辅资料
     */
    public static void newIntent(Activity context, int videoType) {
        Intent videoIntent = new Intent(context, DirectHomePageActivity.class);
        videoIntent.putExtra("videoType", videoType);
        context.startActivity(videoIntent);
    }
    /**
     * @param context
     * @param videoType 0 高清网课 1 直播  2 教辅资料
     * @param isToMe 是否到我的视频界面
     */
    public static void newIntent(Activity context, int videoType,boolean isToMe) {
        Intent videoIntent = new Intent(context, DirectHomePageActivity.class);
        videoIntent.putExtra("videoType", videoType);
        videoIntent.putExtra("isToMe", isToMe);
        context.startActivity(videoIntent);
    }

    /**
     * 初始化筛选控件
     ***/
    public void iniSelectView() {
        int width = (int) (CommonUtils.getScreenWidth() * 0.8);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawer_view_right.getLayoutParams();
        params.width = width;
        drawer_view_right.setLayoutParams(params);

        test_categories = getResources().getStringArray(R.array.test_categories);
        test_materialss = getResources().getStringArray(R.array.test_materialss);
//        interviews = getResources().getStringArray(R.array.interview);
        class_types = getResources().getStringArray(R.array.class_type);
        learning_periods = getResources().getStringArray(R.array.learning_period);
        wy_subjects = getResources().getStringArray(R.array.wy_subjects);
        wp_subjects = getResources().getStringArray(R.array.wp_subjects);
        wh_subjects = getResources().getStringArray(R.array.wh_subjects);
        i_subjects = getResources().getStringArray(R.array.i_subjects);
        charge_types = getResources().getStringArray(R.array.charge_types);
        recruitment_wy_subjects = getResources().getStringArray(R.array.recruitment_wy_subjects);
        recruitment_wp_subjects = getResources().getStringArray(R.array.recruitment_wp_subjects);
        recruitment_iy_subjects = getResources().getStringArray(R.array.recruitment_iy_subjects);
        tw_middle_subject = getResources().getStringArray(R.array.tw_middle_subject);
        career_subjects = getResources().getStringArray(R.array.career_subjects);
        sl_select = (ScrollView) findViewById(R.id.sv_select);
        gv_test_categories = (RadioGroup) findViewById(R.id.gv_test_categories);
        gv_interview = (RadioGroup) findViewById(R.id.gv_interview);
        rl_test_categories = (RelativeLayout) findViewById(R.id.rl_test_categories);
        rl_interview = (RelativeLayout) findViewById(R.id.rl_interview);
        rl_subjects = (RelativeLayout) findViewById(R.id.rl_subjects);
        rl_learning_period = (RelativeLayout) findViewById(R.id.rl_learning_period);
        rl_test_categories.setOnClickListener(this);
        rl_interview.setOnClickListener(this);
        rl_subjects.setOnClickListener(this);
        rl_learning_period.setOnClickListener(this);
        gv_learning_period = (RadioGroup) findViewById(R.id.gv_learning_period);
        gv_subjects = (RadioGroup) findViewById(R.id.gv_subjects);
        ll_test_categories = (LinearLayout) findViewById(R.id.ll_test_categories);
        ll__interview = (LinearLayout) findViewById(R.id.ll_interview);
        ll_learning_period = (LinearLayout) findViewById(R.id.ll_learning_period);
        ll_subjects = (LinearLayout) findViewById(R.id.ll_subjects);
        subjects = getResources().getStringArray(R.array.video_subject);
        gv_class_type = (RadioGroup) findViewById(R.id.gv_class_type);
        ll_class_type = (LinearLayout) findViewById(R.id.ll_class_type);

        //设置考试类型
        if (videoType == 2) {
            iniSelectData(gv_test_categories, categorie, test_materialss);
        }else {
            iniSelectData(gv_test_categories, categorie, test_categories);
        }
//        findViewById(R.id.btn_commit_select).setOnClickListener(this);
        gv_test_categories.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton tempButton = (RadioButton) findViewById(checkedId);
                if (tempButton == null) return;
                categorie_ = tempButton.getText().toString();
                period_ = "";
                class_type_ = "";
                subject_ = "";
                if ("教师资格证笔试".equals(categorie_) || "教师招聘笔试".equals(categorie_) || "教师资格证面试".equals(categorie_) || "教师招聘面试".equals(categorie_) || "特岗教师"
                        .equals(categorie_) || "事业单位D类".equals(categorie_)) {
//                    iniSelectData(gv_interview, interview_,interviews);
                    gv_learning_period.removeAllViews();
                    iniSelectData(gv_learning_period, period_, learning_periods);
                    ll_learning_period.setVisibility(View.VISIBLE);
                    ll__interview.setVisibility(View.GONE);
                    ll_class_type.setVisibility(View.GONE);
                    ll_subjects.setVisibility(View.GONE);
                } else {
                    ll_class_type.setVisibility(View.GONE);
                    ll__interview.setVisibility(View.GONE);
                    ll_learning_period.setVisibility(View.GONE);
                    ll_subjects.setVisibility(View.GONE);
                }
            }
        });
//        gv_interview.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton tempButton = (RadioButton) findViewById(checkedId);
//                if (tempButton == null)return;
//                interview_ = tempButton.getText().toString();
//                period_ = "";
//                subject_ = "";
//                class_type_ = "";
//                ll_learning_period.setVisibility(View.VISIBLE);
//                ll_subjects.setVisibility(View.GONE);
//                ll_class_type.setVisibility(View.GONE);
//                gv_learning_period.removeAllViews();
//                iniSelectData(gv_learning_period, period_,learning_periods);
//            }
//        });
        //学段
        gv_learning_period.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton tempButton = (RadioButton) findViewById(checkedId);
                if (tempButton == null) return;
                period_ = tempButton.getText().toString();
                if (videoType == 2) {
                }else {
                    class_type_ = "";
                    subject_ = "";
                    gv_class_type.removeAllViews();
                    iniSelectData(gv_class_type, class_type_, class_types);
                    ll_class_type.setVisibility(View.VISIBLE);
                    ll_subjects.setVisibility(View.GONE);
                }

            }
        });
        //班级类型
        gv_class_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton tempButton = (RadioButton) findViewById(checkedId);
                if (tempButton == null) return;
                class_type_ = tempButton.getText().toString();
                subject_ = "";
                ll_subjects.setVisibility(View.VISIBLE);
                gv_subjects.removeAllViews();
                setSubjectSelector(subject_);
            }
        });
        //科目
        gv_subjects.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton tempButton = (RadioButton) findViewById(checkedId);
                if (tempButton == null) return;
                subject_ = tempButton.getText().toString();
            }
        });


    }

    /**
     * 设置学科
     */
    private void setSubjectSelector(String subject_) {
        if (videoType == 2) {


        } else {
            if (categorie_.equals("教师资格证笔试")) {
                if ("幼儿".equals(period_)) {
                    iniSelectData(gv_subjects, subject_, wy_subjects);
                } else if ("小学".equals(period_)) {
                    iniSelectData(gv_subjects, subject_, wp_subjects);
                } else if ("中学".equals(period_)) {
                    iniSelectData(gv_subjects, subject_, wh_subjects);
                }
            } else if (categorie_.equals("教师资格证面试")) {
                iniSelectData(gv_subjects, subject_, i_subjects);
            } else if (categorie_.equals("教师招聘面试")) {
                iniSelectData(gv_subjects, subject_, recruitment_iy_subjects);
            } else if (categorie_.equals("教师招聘笔试") || categorie_.equals("特岗教师")) {
                if ("幼儿".equals(period_)) {
                    iniSelectData(gv_subjects, subject_, recruitment_wy_subjects);
                } else if ("小学".equals(period_)) {
                    iniSelectData(gv_subjects, subject_, recruitment_wp_subjects);
                } else if ("中学".equals(period_)) {
                    iniSelectData(gv_subjects, subject_, tw_middle_subject);
                }
            } else if (categorie_.equals("事业单位D类")) {
                iniSelectData(gv_subjects, subject_, career_subjects);
            }
        }
    }

    /**
     * 筛选数据适配
     **/
    public void iniSelectData(RadioGroup contentRg, String selctorText, String... datas) {
        contentRg.removeAllViews();
        for (int i = 0; i < datas.length; i++) {
            RadioButton tempButton = new RadioButton(this);
//            ColorStateList colorStateList = getColorStateList(R.color.text_check_staus);
            int color = ContextCompat.getColor(this, R.color.text_check_staus);
            tempButton.setTextColor(color);
            tempButton.setBackgroundResource(R.drawable.bg_select_gv);   // 设置RadioButton的背景图片
            tempButton.setButtonDrawable(android.R.color.transparent);
//            tempButton.setButtonDrawable(R.drawable.xxx);           // 设置按钮的样式
//            tempButton.setPadding(pad2, pad1, pad2, pad1);                 // 设置文字距离按钮四周的距离

            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(pad2, pad1, pad2, pad1);
            if (datas[i].length() == 2 || datas[i].length() == 3 || datas[i].length() == 4) {
                lp.width = CommonUtils.dip2px(75);
                lp.height = CommonUtils.dip2px(36);
            } else if (datas[i].length() == 5 || datas[i].length() == 6) {
                lp.width = CommonUtils.dip2px(100);
                lp.height = CommonUtils.dip2px(36);
            } else if (datas[i].length() > 5) {
                lp.width = CommonUtils.dip2px(120);
                lp.height = CommonUtils.dip2px(36);
            }
            tempButton.setGravity(Gravity.CENTER);
            tempButton.setLayoutParams(lp);
            tempButton.setText(datas[i]);
            contentRg.addView(tempButton);
            if (datas[i].equals(selctorText)) {
                contentRg.check(tempButton.getId());
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (drawer.isDrawerOpen(drawer_view_right)) {
                drawer.closeDrawers();
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        categorie = "";
        interview = "";
        period = "";
        subject = "";
        ActualPrice = "";
        class_type = "";
    }

    /**
     * 展开筛选 gridview
     */
    private static void expandGridview(RadioGroup gv_content) {
        if (gv_content.getVisibility() == View.GONE) {
//            Animation expandAnimation = AnimationUtil.expand(gv_content,
//                    true, 300);
//            gv_content.startAnimation(expandAnimation);
            gv_content.setVisibility(ViewGroup.VISIBLE);
        } else {
//            Animation expandAnimation = AnimationUtil.expand(gv_content,
//                    false, 300);
//            gv_content.startAnimation(expandAnimation);
            gv_content.setVisibility(ViewGroup.GONE);
        }
    }

    //初始化筛选数据
    public void iniSenddata() {
        RadioButton tempButton = (RadioButton) findViewById(gv_test_categories.getCheckedRadioButtonId());
        if (tempButton != null) {
            DirectHomePageActivity.categorie = tempButton.getText().toString();
        }
        RadioButton interviewButton = (RadioButton) findViewById(gv_interview.getCheckedRadioButtonId());
        if (interviewButton != null) {
            DirectHomePageActivity.interview = interviewButton.getText().toString();
        }
        RadioButton periodButton = (RadioButton) findViewById(gv_learning_period.getCheckedRadioButtonId());
        if (periodButton != null) {
            DirectHomePageActivity.period = periodButton.getText().toString();
        }
        RadioButton subjectButton = (RadioButton) findViewById(gv_subjects.getCheckedRadioButtonId());
        if (subjectButton != null) {
            DirectHomePageActivity.subject = subjectButton.getText().toString();
        }
        RadioButton classTypeButton = (RadioButton) findViewById(gv_class_type.getCheckedRadioButtonId());
        if (subjectButton != null) {
            DirectHomePageActivity.class_type = classTypeButton.getText().toString();
        }
    }
}
