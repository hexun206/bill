package com.huatu.teacheronline.utils;

import com.gensee.utils.StringUtil;
import com.huatu.teacheronline.exercise.bean.CategoryBean;

import java.util.ArrayList;

/**
 * Created by zhxm on 2016/1/26.
 * 保存用户相关信息，方便维护
 */
public class UserInfo {

    // 选择头像用常量
    public static final int TAKE_PICTURE = 0;
    public static final int CHOOSE_PICTURE = 1;
    public static final int CROP = 2;
    public static final int CROP_PICTURE = 3;
    public static final int SCALE = 5;
    public static final int CHOOSE_SUBJECT_RESULT_MODULE = 201;//模块题海添加科目
    public static final int CHOOSE_SUBJECT_RESULT_EVALUATION = 202;//真题测评添加科目
    public static final int CHOOSE_SUBJECT_RESULT_ERROR = 203;//错题中心添加科目
    // sp保存的key值常量
    // 是否登录
    public static final String KEY_SP_IFLOGIN = "key_sp_iflogin";
    // 用户id
    public static final String KEY_SP_USERID = "key_sp_userid";
    // 用户名
    public static final String KEY_SP_ACCOUNT = "key_sp_account";
    // 用户名
    public static final String KEY_SP_BIRTHDAY = "key_sp_birthday";
    // MD5加密后的登录密码
    public static final String KEY_SP_PASSWORD = "key_sp_password";
    // 用户电话号码
    public static final String KEY_SP_MOBILE = "key_sp_mobile";
    // 用户昵称
    public static final String KEY_SP_NICKNAME = "key_sp_nickname";
    // 省份id
    public static final String KEY_SP_PROVINCE_ID = "key_sp_province_id";//
    // 省份名称
    public static final String KEY_SP_PROVINCE_NAME = "key_sp_province_name";
    // 城市id
    public static final String KEY_SP_CITY_ID = "key_sp_city_id";//
    // 城市名称
    public static final String KEY_SP_CITY_NAME = "key_sp_city_name";
    // 考试类型id
    public static final String KEY_SP_EXAMCATEGORY_ID = "key_sp_examcategory_id";
    // 考试类型
    public static final String KEY_SP_EXAMCATEGORY_NAME = "key_sp_examcategory_name";
    // 考试学段id
    public static final String KEY_SP_EXAMSTAGE_ID = "key_sp_examstage_id";
    // 考试学段
    public static final String KEY_SP_EXAMSTAGE_NAME = "key_sp_examstage_name";
    // 考试科目id
    public static final String KEY_SP_EXAMSUBJECT_ID = "key_sp_examsubject_id";
    // 考试科目
    public static final String KEY_SP_EXAMSUBJECT_NAME = "key_sp_examsubject_name";
    // 多科目
    public static final String KEY_SP_EXAMSUBJECTS_ID_NAME = "key_sp_examsubjects_id_name";
    // 保存面试视频列表的科目筛选项版本号
    public static final String KEY_SP_VIDEO_SUBJECTS_VERSION = "key_sp_video_subjects_version";
    // 保存面试视频列表的科目筛选项
    public static final String KEY_SP_VIDEO_SUBJECTS = "key_sp_video_subjects";
    // 保存用户性别
    public static final String KEY_SP_SEX = "key_sp_sex";
    // 保存头像地址
    public static final String KEY_SP_FACEPATH = "key_sp_facepath";
    //保存金币
    public static final String KEY_SP_GOLD = "key_sp_gold";
    //保存积分
    public static final String KEY_SP_POINT = "key_sp_user_point";
    //是否修改密码
    public static final String KEY_SP_FIRST_PASS = "key_sp_first_pass";
    //保存是否第一次显示多选题
    public static final String KEY_SP_ISFRIST_MULTISELECT = "key_sp_isfrist_multiselect";
    //保存是否第一次显示真题时间暂停
    public static final String KEY_SP_ISFRIST_EVALUATION = "key_sp_isfrist_evaluation";
    //保存是否第一次选择科目
    public static final String KEY_SP_ISFRIST_SUBJECTCHOOSE = "key_sp_isfrist_subjectchoose";
    //保存是否第一次打开app
    public static final String KEY_SP_ISFRIST_OPEN_TEACHERONLINE = "key_sp_isfrist_open_teacheronline";
    //保存是否第一次进入模块题海、真题测评、错题中心中的一个
    public static final String KEY_SP_ISFRIST_OPEN_MORE_SUBJECT = "key_sp_isfrist_open_more_subject";
    //保存是否第一次第三方登陆
    public static final String KEY_SP_ISFRIST_OPEN_THIRD_LOGIN = "key_sp_isfrist_open_third_login";
    //保存是否第一次进入主页
   public static final String KEY_SP_TIS_HOME= "key_tis_home";
    //保存筛选的地区
    public static final String KEY_SP_SELECT_AREA = "key_sp_select_area";
    public static final String KEY_SP_SELECT_AREA_TYPE = "key_sp_select_area_type";
    //保存删除收货地址id
    public static final String KEY_SP_SELECT_ID = "key_sp_select_area_id";
    //保存启动页图片地址
    public static final String KEY_SP_START_IMAGE_URL = "key_sp_start_image_url";
    //保存token
    public static final String KEY_SP_ACCESSToken = "key_sp_accessToken";
    //保存code
    public static final String KEY_SP_CODE = "key_sp_code";
    //搜索历史记录
    public static final String KEY_SP_HISKEYWORD = "key_sp_hiskeyword";
    //保存公告未读
    public static final String KEY_SP_TYPE_ONE = "key_sp_type1";
    //保存课程未读
    public static final String KEY_SP_TYPE_TWO = "key_sp_type2";
    //保存资讯未读
    public static final String KEY_SP_TYPE_THREE = "key_sp_type3";
    //保存当天时间
    public static final String KEY_SP_TIME_DATA = "key_sp_time_data";
    //保存第一次进入首页 不弹出广告字段
    public static final String KEY_SP_AdVERT = "key_sp_advert";

    //保存题包的update数组
    public static final String KEY_SP_UPDATEQIDS_KEY = "key_sp_updateqids_key";
    //保存题包的delete数组
    public static final String KEY_SP_DELETEQIDS_KEY = "key_sp_deleteqids_key";

    //保存启动应用和退出应用后首页请求的dialog
    public static final String KEY_SP_ONE_LOADING="key_sp_loading";
    //保存学员和非学员的判断值
    public static final String KEY_SP_NON_STUDENT="key_sp_non_student";
    // 用户手机号码
    public static String registerNumber;
    // 临时存储，只有当用户在选择完科目提交的时候才会保存到sp中，否则用户返回后不保存
    public static String tempCategoryTypeId; //课程类型id
    public static String tempCategoryTypeName;//课程类型名字
    public static String tempProvinceId;// 省id
    public static String tempProvinceName;//省名字
    public static String tempCityId;//城市id
    public static String tempCityName;//城市名字
    public static String tempStageId;//学段id
    public static String tempStageName;//学段名字
    public static String tempSubjectId;//当前选中科目id
    public static String tempSubjectName;//当前选中科目名字
    public static String tempSubjectsIdName;//当前多选科目id和名字用下划线隔开，科目间用逗号隔开 例如：yj0014_语文专业知识,63fe1feb8ead45adb3ba0cbd53ca5689_数学专业知识
    public static String KEY_SP_VIPHANDPAPERBEAN;//试卷的做题记录

    /**
     * 获取多科目ids
     * @return ids
     */
    public  static ArrayList<CategoryBean> getSubjects(){
        String subjectsIdNames = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECTS_ID_NAME, "");
        String currentSubjectsId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_ID, "");
        String[] split = subjectsIdNames.split(",");StringBuilder sb_id = new StringBuilder();
        ArrayList<CategoryBean> categoryBeans = new ArrayList<CategoryBean>();
        if(StringUtil.isEmpty(subjectsIdNames)){
            return categoryBeans;
        }
        for (int i = 0; i < split.length; i++) {
            CategoryBean categoryBean = new CategoryBean();
            String[] split1 = split[i].split("_");
            sb_id.append(split1[0]+",");
            categoryBean.cid= split1[0];
            categoryBean.name = (split1[1]);
            if(currentSubjectsId.equals(split1[0])){
                categoryBeans.add(0,categoryBean);
            }else {
                categoryBeans.add(categoryBean);
            }
        }
        return categoryBeans;
    }
    /**
     * 添加多科目选择保存临时的信息
     * @return
     */
    public  static String setTempSubjectsIdName(){
        tempCategoryTypeId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMCATEGORY_ID, "");
        tempCategoryTypeName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMCATEGORY_NAME, "");
        tempCityId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_ID, "");
        tempCityName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_NAME, "");
        tempProvinceId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PROVINCE_ID, "");
        tempProvinceName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PROVINCE_NAME, "");
        tempStageId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSTAGE_ID, "");
        tempStageName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSTAGE_NAME, "");
        String subjectsIdNames = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECTS_ID_NAME, "");
        return subjectsIdNames;
    }

}
