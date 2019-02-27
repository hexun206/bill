package com.huatu.teacheronline.sensorsdata;

import android.content.Context;
import android.view.View;

import com.huatu.teacheronline.sensorsdata.event.BrowseCourse;
import com.huatu.teacheronline.sensorsdata.event.BuyCourse;
import com.huatu.teacheronline.sensorsdata.event.PlayTrialCourse;
import com.huatu.teacheronline.sensorsdata.event.WatchCourse;
import com.huatu.teacheronline.utils.GsonUtils;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kinndann on 2018/12/19.
 * description:神策自定义事件
 */
public class TrackUtil {


    public static final String TYPE_TAB = "TAB";
    public static final String TYPE_ICON = "icon";
    public static final String TYPE_LINK = "文字链接";
    public static final String TYPE_SHORTCUT = "快捷操作";
    public static final String TYPE_CHANGE_TAB = "切换TAB";
    public static final String TYPE_CARD = "课程卡片";
    public static final String TYPE_LIST = "列表";
    public static final String TYPE_BUTTON = "普通按钮";


    /**
     * 华图教师_pc_m_app_华图教师_试听课程
     */
    private final static String EVENT_PLAY_TRIAL_COURSE = "Hteacher_pc_m_app_Hteacher_ListenCourseDetail";
    /**
     * 华图教师_pc_m_app_华图教师_浏览课程
     */
    private final static String EVENT_BROWSE_COURSE = "Hteacher_pc_m_app_Hteacher_ViewCourseDetail";
    /**
     * 华图教师_pc_m_app_华图教师_购买课程
     */
    private final static String EVENT_BUY_COURSE = "Hteacher_pc_m_app_Hteacher_BuyCourse";

    /**
     * 华图教师_pc_m_app_华图教师_开始观看课程
     */
    private final static String EVENT_START_WATCH_COURSE = "Hteacher_pc_m_app_Hteacher_StartWatchCourse";
    /**
     * 华图教师_pc_m_app_华图教师_结束观看课程
     */
    private final static String EVENT_END_WATCH_COURSE = "Hteacher_pc_m_app_Hteacher_EndWatchCourse";


    /**
     * 华图教师_app_华图教师_点击注册按钮
     */
    private final static String EVENT_SIGN_UP_CLICK = "HuaTuTeacher_app_HuaTuTeacher_clickSignUp";

    /**
     * 华图教师_app_华图教师_获取验证码
     * <br> user_phone
     */
    private final static String EVENT_SIGN_UP_GET_CHECK_CODE = "HuaTuTeacher_app_HuaTuTeacher_getCheckCode";

    /* *//**
     * 华图教师_app_华图教师_下一步
     * <br> user_phone

     *//*
    private final static String EVENT_SIGN_UP_NEXT_STEP = "HuaTuTeacher_app_HuaTuTeacher_nextStep";
    *//**
     * 华图教师_app_华图教师_注册成功
     * <br> user_phone 注册方式
     * <br> sign_up_type
     *//*
    private final static String EVENT_SIGN_UP_SUCCESS = "HuaTuTeacher_app_HuaTuTeacher_signUpSuccess";*/


    /**
     * 华图教师_app_华图教师_点击快捷登录
     * <br> quick_login_way 微信、QQ
     * <br> quick_login_account
     * <br> login_first
     */
    private final static String EVENT_QUICK_LOGIN_CLICK = "HuaTuTeacher_app_HuaTuTeacher_clickQuickLogin";
    /**
     * 华图教师_app_华图教师_绑定手机号
     * <br> quick_login_account
     * <br> user_phone
     */
    private final static String EVENT_BIND_MOBILE = "HuaTuTeacher_app_HuaTuTeacher_bindMP";
    /**
     * 华图教师_app_华图教师_选择考试
     * <br> exam_type
     */
    private final static String EVENT_SELECT_EXAM_TYPE = "HuaTuTeacher_app_HuaTuTeacher_selectExamType";
    /**
     * 华图教师_app_华图教师_选择学段
     * <br> course_study_section
     */
    private final static String EVENT_SELECT_EXAM_GRADE = "HuaTuTeacher_app_HuaTuTeacher_selectExamGrade";
    /**
     * 华图教师_app_华图教师_选择科目
     * <br> exam_subject_list
     */
    private final static String EVENT_SELECT_EXAM_SUBJECT = "HuaTuTeacher_app_HuaTuTeacher_selectExamSubject";
    /**
     * 华图教师_app_华图教师_提交信息
     * <br> exam_type
     * <br> course_study_section
     * <br> exam_subject_list
     */
    private final static String EVENT_EXAM_INFO_SUBMIT = "HuaTuTeacher_app_HuaTuTeacher_submitExamInfor";

    /**
     * 登录成功或失败事件
     * <br> login_way
     * <br> login_account
     * <br> is_success
     * <br> fail_reason
     */
    private final static String EVENT_LOGIN_INFO = "HuaTuTeacher_app_HuaTuTeacher_loginSuccess";


    /**
     * 试听课程播放
     *
     * @param event
     */
    public static void trackPlayTrialCourse(PlayTrialCourse event) {

        try {
            SensorsDataAPI.sharedInstance().track(EVENT_PLAY_TRIAL_COURSE, new JSONObject(GsonUtils.toJson(event)));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 购买课程
     *
     * @param event
     */
    public static void trackBuyCourse(BuyCourse event) {

        try {
            SensorsDataAPI.sharedInstance().track(EVENT_BUY_COURSE, new JSONObject(GsonUtils.toJson(event)));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 浏览课程
     *
     * @param event
     */
    public static void trackBrowseCourse(BrowseCourse event) {

        try {
            SensorsDataAPI.sharedInstance().track(EVENT_BROWSE_COURSE, new JSONObject(GsonUtils.toJson(event)));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 开始观看课程
     *
     * @param event
     */
    public static void trackStartWatchCourse(WatchCourse event) {

        try {
            SensorsDataAPI.sharedInstance().track(EVENT_START_WATCH_COURSE, new JSONObject(GsonUtils.toJson(event)));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 结束观看课程
     *
     * @param event
     */
    public static void trackEndWatchCourse(WatchCourse event) {

        try {
            SensorsDataAPI.sharedInstance().track(EVENT_END_WATCH_COURSE, new JSONObject(GsonUtils.toJson(event)));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 注册点击
     */
    public static void trackSignUpClick() {

        SensorsDataAPI.sharedInstance().track(EVENT_SIGN_UP_CLICK);


    }

    /**
     * 获取验证码
     *
     * @param user_phone
     */
    public static void trackSignUpGetCheckCode(String user_phone) {


        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_phone", user_phone);

            SensorsDataAPI.sharedInstance().track(EVENT_SIGN_UP_GET_CHECK_CODE, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 快速登录
     *
     * @param quick_login_way
     * @param quick_login_account
     * @param login_first
     */
    public static void trackQuickLoginClick(String quick_login_way, String quick_login_account, boolean login_first) {

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("quick_login_way", quick_login_way);
            jsonObject.put("quick_login_account", quick_login_account);
            jsonObject.put("login_first", login_first);

            SensorsDataAPI.sharedInstance().track(EVENT_QUICK_LOGIN_CLICK, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 绑定手机号 弃用  后端埋点
     *
     * @param user_phone
     * @param quick_login_account
     */
    @Deprecated
    public static void trackBindMobile(String user_phone, String quick_login_account) {

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_phone", user_phone);
            jsonObject.put("quick_login_account", quick_login_account);

            SensorsDataAPI.sharedInstance().track(EVENT_BIND_MOBILE, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 选择考试
     *
     * @param exam_type
     */
    public static void trackSelectExamType(String exam_type) {

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("exam_type", exam_type);

            SensorsDataAPI.sharedInstance().track(EVENT_SELECT_EXAM_TYPE, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 选择学段
     *
     * @param course_study_section
     */
    public static void trackSelectExamGrade(String course_study_section) {

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("course_study_section", course_study_section);

            SensorsDataAPI.sharedInstance().track(EVENT_SELECT_EXAM_GRADE, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 选择科目
     *
     * @param exam_subject_list
     */
    public static void trackSelectExamSubject(List<String> exam_subject_list) {

        try {

            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray(exam_subject_list);
            jsonObject.put("exam_subject_list", jsonArray);


            SensorsDataAPI.sharedInstance().track(EVENT_SELECT_EXAM_SUBJECT, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 考试信息提交
     *
     * @param exam_type
     * @param course_study_section
     * @param exam_subject_list
     */
    public static void trackExamInfoSubmit(String exam_type, String course_study_section, List<String> exam_subject_list) {

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("exam_type", exam_type);
            jsonObject.put("course_study_section", course_study_section);
            JSONArray jsonArray = new JSONArray(exam_subject_list);
            jsonObject.put("exam_subject_list", jsonArray);

            SensorsDataAPI.sharedInstance().track(EVENT_EXAM_INFO_SUBMIT, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    /**
     * 登录信息
     *
     * @param login_way
     * @param login_account
     * @param is_success
     * @param fail_reason
     */
    public static void trackLoginInfo(String login_way, String login_account, boolean is_success, String fail_reason) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("login_way", login_way);
            jsonObject.put("login_account", login_account);
            jsonObject.put("is_success", is_success);
            jsonObject.put("fail_reason", fail_reason);

            SensorsDataAPI.sharedInstance().track(EVENT_LOGIN_INFO, jsonObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * 按钮点击
     *
     * @param context
     * @param v
     * @param type
     * @param content
     */
    public static void trackClick(Context context, View v, String type, String content) {


        HashMap<String, String> map = new HashMap<>();
        map.put("$element_content", content);
        map.put("$element_type", type);

        SensorsDataAPI.sharedInstance(context).setViewProperties(v, new JSONObject(map));


    }


}
