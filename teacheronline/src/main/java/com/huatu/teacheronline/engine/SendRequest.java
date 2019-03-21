package com.huatu.teacheronline.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.bluelinelabs.logansquare.LoganSquare;
import com.gensee.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.greendao.DaoUtils;
import com.greendao.DirectBean;
import com.greendao.ExamSubject;
import com.huatu.teacheronline.CCVideo.InterviewVideoBean;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.SensorDataSdk;
import com.huatu.teacheronline.bean.MyssageBean;
import com.huatu.teacheronline.bean.PersonalInfoBean;
import com.huatu.teacheronline.bean.ProvinceWithCityBean;
import com.huatu.teacheronline.bean.StartsBean;
import com.huatu.teacheronline.direct.DataStore_Direct;
import com.huatu.teacheronline.direct.bean.PdfBean;
import com.huatu.teacheronline.direct.bean.RecodeRequestFailure;
import com.huatu.teacheronline.direct.bean.SelectAcademicBean;
import com.huatu.teacheronline.direct.bean.WithdrawalBean;
import com.huatu.teacheronline.direct.db.OffLineDataForPlayerDAO;
import com.huatu.teacheronline.direct.manager.RecodeRequestFailureManager;
import com.huatu.teacheronline.exercise.ModuleExerciseActivity;
import com.huatu.teacheronline.exercise.SendRequestUtilsForExercise;
import com.huatu.teacheronline.exercise.bean.CategoryBean;
import com.huatu.teacheronline.exercise.bean.PaperAttrVo;
import com.huatu.teacheronline.exercise.bean.PaperItem;
import com.huatu.teacheronline.exercise.bean.PaperPo;
import com.huatu.teacheronline.exercise.bean.QuesAttrVo;
import com.huatu.teacheronline.exercise.bean.erbean.ErPaperAttrVo;
import com.huatu.teacheronline.message.bean.SubscriptionBean;
import com.huatu.teacheronline.paymethod.bean.PersonalAddressBean;
import com.huatu.teacheronline.personal.MyOrderActivity;
import com.huatu.teacheronline.personal.bean.BillBean;
import com.huatu.teacheronline.personal.bean.ClassDetaliBean;
import com.huatu.teacheronline.personal.bean.CouserBean;
import com.huatu.teacheronline.personal.bean.DeedDetailiBean;
import com.huatu.teacheronline.personal.bean.DirecourBean;
import com.huatu.teacheronline.personal.bean.FeedListBean;
import com.huatu.teacheronline.personal.bean.InformaBean;
import com.huatu.teacheronline.personal.bean.LogisticsBean;
import com.huatu.teacheronline.personal.bean.MessageBean;
import com.huatu.teacheronline.personal.bean.MsgBean;
import com.huatu.teacheronline.personal.bean.MyErrorCorrectionBean;
import com.huatu.teacheronline.personal.bean.NCakenBean;
import com.huatu.teacheronline.personal.bean.NoticeBean;
import com.huatu.teacheronline.personal.bean.OrderBean;
import com.huatu.teacheronline.sensorsdata.TrackUtil;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.TripleDES;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.vipexercise.vipbean.LeaTeacherBean;
import com.huatu.teacheronline.vipexercise.vipbean.LeaveBean;
import com.huatu.teacheronline.vipexercise.vipbean.PaperAnalysiBean;
import com.huatu.teacheronline.vipexercise.vipbean.PaperHandBean;
import com.huatu.teacheronline.vipexercise.vipbean.VipDataBean;
import com.huatu.teacheronline.vipexercise.vipbean.VipPaperBean;
import com.huatu.teacheronline.vipexercise.vipbean.VipQuestionBean;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.AsyncHttpResponseSignHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.orhanobut.logger.Logger;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendRequest {
    private static String TAG = "SendRequest";
    public static final String ERROR_NETWORK = "11";//失败原因:未连网
    public static final String ERROR_SERVER = "-11";//失败原因:服务器错误
    public static final String ERROR_Son = "401";//失败原因:服务器错误
    public static final String SUCCESS = "2";//更新数据成功
    public static String key = "0123456789QWEQWEEWQQ1234";
    public static String source_android = "APK";

//        public static String url_user = "http://5beike.hteacher.net/estimate/index.php/";    //20171025 正式线测试版
    //    private static String ipForExercise = "http://apptest.hteacher.net:8080/";    //20161220测试题库地址

//    public static String url_user = "http://mobileappa.hteacher.net/teacher/index.php/";    //用户相关测试
//    public static String url_user = "http://5beike.hteacher.net/teacherTest/index.php/";    //用户相关测试
//    public static String ipForExercise = "http://221.122.54.133:8080/";    //20161220测试题库地址
//    public static  String url_Share_questions="http://mobileappa.hteacher.net/teacherTest/activity/score_share.html?";    //做题分享链接测试地址
//    private static String url_vip="http://112.74.196.127:8080/jszx/";    //vip相关测试
//    private static String url_vip = "http://3s.huatu.com/web/";    //vip相关测试
//    private static String url_classmesgg=url_vip+"notice";    //课程通知测试线


//    public static String url_user = "http://mobileappa.hteacher.net/teacher/index.php/"; //测试服务器地址
//    public static String url_user = "http://mobileappb.hteacher.net/teacher/index.php/"; //预发布服务器地址
    public static String url_user = "http://mobileapp.hteacher.net/teacher/index.php/"; //正式服务器

    private static String url_vip = "http://221.122.54.137:8080/jszx/";    //vip相关正式
//    private static String url_vip = "http://112.74.196.127:8080/web/";    //vip测试线

    public static String ipForExercise = "http://appjszx.hteacher.net/";    //最新正式题库Ip地址（新）
    public static String url_Share_questions = "http://mobileapp.hteacher.net/teacher/activity/score_share.html?";    //做题分享链接正式地址

    private static String url_classmesgg = url_vip + "notice";    //课程通知正式线

    //    public static String ipForExercise = "http://120.76.154.57/";    //正式题库Ip地址（旧）


    //    //首页拆分接口V1 V2
    private static String url_personalInfoV3 = url_user + "member/get_userInfo_v3";
    //清空消息列表
    public static String url_empty_message = url_user + "sms/clearMsgByType";
    //取消登录
    public static String Cancel_login = url_user + "register/loginOut";
    //我的订单列表
    public static String url_myorder = url_user + "kuaidi/myOrder";
    //我的物流详情
    public static String url_logisticsinfo = url_user + "kuaidi/getInfo";
    //取消订单
    public static String url_cancleOrder = url_user + "kuaidi/cancelOrder";
    //我的消息列表
    public static String url_myMessage = url_user + "sms/push_list";
    //删除消息
    public static String url_delMessage = url_user + "sms/delMsg";
    //更改消息状态'
    public static String url_updateMessage = url_user + "sms/upStatus";
    //增加点击数
    public static String url_addClicks = url_user + "Tk_clicks/add_clicks";
    //记录播放时长接口
    public static String url_addRecord = url_user + "video/rc_addrecordinfo";
    //直播列表
    private static String url_directList = url_user + "liveDb/get_live_list_v2";
    //直播详情
//    private static String url_directDetailsData = url_user+"live/get_id_video";
    private static String url_directDetailsData = url_user + "liveDb/get_id_video";
    //直播收藏
    private static String url_directCollection = url_user + "collect/add_collect";
    //直播足迹
    private static String url_directHistory = url_user + "collect/list_history";
    // 面试视频列表
    private static String url_getInterviewVideoList = url_user + "video/get_list";
    // 面试视频详情
    private static String url_getInterviewVideoDetail = url_user + "video/get_info";
    // 添加直播
    private static String url_addDirect = url_user + "liveDb/add_my_videos";
    //直播删除
    private static String url_delDirect = url_user + "liveDb/del_my_videos";
    //直播播放详情
    private static String url_directPlayInfoDetailsData = url_user + "liveDb/get_play_info";
    private static String url_directPlayDetailsData = url_user + "liveDb/get_id_list";
    //直播在线课件列表
    private static String url_liveClassScheduleList = url_user + "liveDb/V2_get_id_list";
    //直播在线PDF列表
    private static String url_liveClassSchedulePDFList = url_user + "liveDb/Handout";
    //我的在线视频lieb
    private static String url_myDirectList = url_user + "liveDb/get_my_lives";
    //购买课程中选择学段
    private static String url_selectAcademic = url_user + "liveDb/select_academic";
    // 验证手机号是否被注册
    private static String url_verifyMobileNO = url_user + "register/is_register";
    // 发送短信验证码
    private static String url_SendVerification = url_user + "register/V2_send_sms";

    //获取科目
    private static String url_getCatagoryList = ipForExercise + "/httb/httbapi/categoryjs/getSubject?license=123&appmark=app_js";
    // 用户注册，涉及到头像上传问题，需要用ip地址上次，返回域名，原因是服务器集群，上传后找不到图片
//    private static String url_register = "http://123.103.86.2:8008/register";
    private static String url_register = url_user + "register";
    // 用户注册验证验证码
    private static String url_register_testcode = url_user + "register/V2_testCode";
    //用户信息
    private static String url_personalInfo = url_user + "member/get_userInfo";
    //新版用户信息
    private static String url_personalInfo_v2 = url_user + "member/get_userInfo_v2";
    //积分兑换金币
    private static String url_exchangeGold = url_user + "member/exchangeGold";
    //用户登录
    private static String url_login = url_user + "register/login";
    // 用户注册成功后选择类型等信息
    private static String url_login_updateinfo = url_user + "register/update_info";
    // 保存个人信息：昵称，性别，头像
//    private static String url_save_information = "http://123.103.86.2:8008/register/update_user_info";
    private static String url_save_information = url_user + "register/update_user_info";
    //修改密码
    private static String url_change_pwd = url_user + "register/update_password";
    //收获地址保存后增加到服务器
    private static String url_save_address = url_user + "user_address/add_address";
    //打开选择收获地址界面从服务器获取地址列表
    private static String url_show_address = url_user + "user_address/get_ud_list";
    //修改地址后通知服务器进行同步更新
    private static String url_update_address = url_user + "user_address/update_address";
    //支付界面查看地址详情
    private static String url_pay_checkout_address = url_user + "user_address/get_id_address";
    //删除地址后通知服务器进行同步更新
    private static String url_delete_address = url_user + "user_address/del_id_address";
    // 忘记密码后修改密码
    private static String url_modify_pwd = url_user + "register/V2_forgot_password";
    //    //获取章节树
    private static String url_getChapterTree = ipForExercise + "/httb/httbapi/categoryjs/getCategoryById?license=123&appmark=app_js";
    //获取章节树2
    private static String url_getChapterTree2 = ipForExercise + "/httb/httbapi/categoryjs/getCategoryByIdXm?license=123&appmark=app_js";
    //获取试题包
    public static String url_getExercisePackage = ipForExercise + "/httb/httbapi/categoryjs/getQuestions?license=123&appmark=app_js";
    //获取试题包2.0
    public static String url_getExercisePackage2 = ipForExercise + "/httb/httbapi/categoryjs/getQuestionsXm?license=123&appmark=app_js";
    //根据试卷id和qids获取试卷收藏或者收藏试题包
    public static String url_getExercisePackageByPaperPidQids = ipForExercise + "httb/httbapi/paperjs/getPaperRecordPid?license=123&appmark=app_js&ques=123";
    //根据试卷id获取试题包
    public static String url_getExercisePackageById = ipForExercise + "/httb/httbapi/paperjs/getQuestionsById?license=123&appmark=app_js&ques=123";
    //根据试卷id获取模考大赛试题包
    public static String url_getExercMoniTPaperId = ipForExercise + "/httb/httbapi/paperjs/getQuestionsByMoniTPaperId?license=123&appmark=app_js&ques=123";
    // 根据科目id获取科目名称
    public static String url_getCategoryBeansByIds = ipForExercise + "/httb/httbapi/categoryjs/getSubjectByIds?license=123&appmark=app_js";
    // 获取服务器时间
    public static String url_getNowTime = ipForExercise + "/httb/httbapi/paperjs/getnowTime?license=123&appmark=app_js";
    // 根据pid获取试卷信息
    public static String url_getPaperNum = ipForExercise + "/httb/httbapi/paperjs/getPaperNum?license=123&appmark=app_js";
    //  分享战果 模块题海 在线模考 真题演练
    public static String url_add_share_result = ipForExercise + "httb/httbapi/common/addShareResult";
    // 删除错题和收藏中已下线的试卷
    public static String url_del_down_paper = ipForExercise + "httb/httbapi/paperjs/dePaperId?license=123&appmark=app_js";
    // 绑定手机号(最新)
    public static String url_update_tel = url_user + "register/update_tel";
    // 绑定手机号
    public static String url_bind_mobile = url_user + "register/bind_mobile";
    // 充值创建订单
//    public static String url_createOrder = url_user + "api/alipay/notify_url.php";
    public static String url_createOrderForGoldRecharge = url_user + "gold/create_order_v2";
    //    public static String url_createOrder = "http://tl.huatu.com/gold/create_order";
    //收藏习题 针对真题模考 和在线模拟
    public static String url_storePaperExercise = ipForExercise + "/httb/httbapi/questionjs/addPaperCollectQuestion?license=123&appmark=app_js";
    //收藏习题
    public static String url_storeExercise = ipForExercise + "/httb/httbapi/questionjs/collectQuestion?license=123&appmark=app_js";
    //取消收藏试题
    public static String url_unstoreExercise = ipForExercise + "/httb/httbapi/questionjs/deleteCollectQuestion?license=123&appmark=app_js";
    //交卷
    public static String url_handin_papers = ipForExercise + "/httb/httbapi/analyzejs/setAnswerAndgetResult?license=123&system=2.0&appmark=app_js";
    //购买生成订单接口
//    public static String url_createOrderForSubmit = url_user + "liveDb/create_live_order";
    //新购买生成订单接口
    public static String url_createOrderForSubmit = url_user + "liveDb/V2_create_live_order";
    //激活课程接口
    public static String url_activationCourse = url_user + "liveDb/activeNet";
    //金币支付
    public static String url_toPayForGold = url_user + "gold/gold_pay";
    //账单
    public static String url_billList = url_user + "gold/bill_list";
    //面试视频列表科目的筛选项
    private static String url_video_subject = url_user + "video_nav/subject";
    //用户反馈
    private static String url_submitFeedBack = url_user + "other/leave_message";


    //反馈提交接口
    private static String url_submitFeedBackv2 = url_user + "other/leave_message_v2";
    //反馈列表接口
    private static String url_Feedbacklist = url_user + "other/feedback_list";
    //反馈详情接口
    private static String url_Feedbackdetails = url_user + "other/feedback_details";
    //首页我的推荐课程
    private static String url_Recommend = url_user + "liveDb/get_recommend";

    //获取真题测评试卷列表
    private static String url_obtain_paper_list = ipForExercise + "/httb/httbapi/paperjs/getPapers4zip?license=123&appmark=app_js";
    //获取模考大赛试卷列表
    private static String test = ipForExercise + "/httb/httbapi/paperjs/getMoniTiPapers?license=123&appmark=app_js";
    //获取错题集
    private static String url_get_wrongques = ipForExercise + "/httb/httbapi/questionjs/getWrongQuestionList4zip?license=123&appmark=app_js";
    //获取收藏试题
    private static String url_get_collectionques = ipForExercise + "/httb/httbapi/questionjs/getCollectQuestionList4zip?license=123&appmark=app_js";
    //根据错题集合获取错题
    private static String url_get_wrongques_byqids = ipForExercise + "/httb/httbapi/questionjs/getQuestionsByIds?license=123&appmark=app_js";
    //移除错题 模块题海
    private static String url_remove_wrongques = ipForExercise + "/httb/httbapi/questionjs/deleteWrongQuestion?license=123&appmark=app_js";
    //移除错题 试卷
    private static String url_remove_paper_wrongques = ipForExercise + "/httb/httbapi/questionjs/delPaperQuestion?license=123&appmark=app_js";
    //收藏试题 试卷
    private static String url_collect_paper_exercise = ipForExercise + "/httb/httbapi/questionjs/addPaperCollectQuestion?license=123&appmark=app_js";
    //分享任务
    private static String url_user_share = url_user + "user_share/get_user_share";
    //获取华图公共号内容
    private static String url_obtainSubscription = url_user + "article/push_list";
    //打卡任务
    private static String url_sign = url_user + "user_sign";
    //签到日历详情接口
    private static String url_Calendardetails = url_user + "User_sign/history_android";
    //获取最新版本号
    private static String url_getVersionCode = url_user + "app_version";
    //提交题目纠错
    private static String url_submitExerciseError = ipForExercise + "/httb/httbapi/questionjs/quescorrect?license=123&appmark=app_js";
    //我的纠错
    private static String url_MyErrorCorrection = ipForExercise + "/httb/httbapi/questionjs/getMyCorrects?license=123&appmark=app_js";
    //点播直播url
    public static String url_liveGensee = "htexam.gensee.com";
    //城市列表
    private static String url_cityes = ipForExercise + "httb/httbapi/categoryjs/getDictionary?license=123";
    //二维码扫描
//    private  static String url_code="http://5beike.hteacher.net/clientSm.php";
    private static String url_code = url_user + "clientsm/clientqr";
    //启动页面
    private static String url_startpage = url_user + "sys_api/sys_welcome";
    //支付获取签名
    private static String url_getPayEncryptionSignature = url_user + "pay/applyPay";
    //搜索课程
    private static String url_url_directList_search = url_user + "liveDb/search_live";
    //联想
    private static String url_url_directList_search_lenove = url_user + "liveDb/search_associate";
    //推荐搜索
    private static String url_url_directList_search_recommend = url_user + "liveDb/search_recommend";
    //签到分享确认接口
    private static String url_Confirmshare = url_user + "user_share/update_sign_share";
    //记录首次签到
    public static String url_firstsign = url_user + "user_sign/sign_first_time";

    //课程通知推送接口
    private static String url_mycourse = url_classmesgg + "/getMyClassList";
    //智学课程通知推送接口
    private static String url_mywisdomclass = url_classmesgg + "/getMyWisdomClassList";
    //班级详情通知接口
    private static String url_Classdetails = url_classmesgg + "/getClassInfo";
    //智学课详情通知接口
    private static String url_WisdomClassdetails = url_classmesgg + "/getMyWisdomClassInfo";
    //开课通知列表
    private static String url_subjetNotice = url_classmesgg + "/getMyNoticeList";
    //获取课程通知信息
    private static String url_arrangement = url_classmesgg + "/getNoticeInfo";
    //课程通知消息确认
    private static String url_Message_confirmation = url_classmesgg + "/noticeChecked";
    //标记已读课程
    private static String url_hasReadClassInfo = url_classmesgg + "/hasReadClassInfo";
    //通知栏跳转详情页接口
    private static String url_Notificationbar = url_classmesgg + "/getNoticeInfoForBar";
    //留言提交接口
    private static String url_addmesg = url_classmesgg + "/leaveMessage";
    //购课须知
    private static String lesson_notes = url_user + "liveDb/lesson_notes";
    //获取错题集
    private static String url_get_wrongques_v2 = ipForExercise + "httb/httbapi/paperjs/getPaperRecordList?license=123&appmark=app_js";

    //课程通知-直播课-通知详情
    private static String url_class_Livebroadcast = url_user + "liveDb/course_notice";
    //课程通知-直播课-课程信息
    private static String url_class_Livedata = url_user + "liveDb/course_information";
    //Vip题库试卷列表
    private static String url_getquestion_exclusivelist = url_vip + "vipQuestion/getQuestionExclusiveList";
    //Vip试题 包
    private static String url_get_vip_questionlist = url_vip + "vipQuestion/getQuestionList";
    //Vip题交卷
    private static String url_get_vip_hand_exclusive = url_vip + "/vipQuestion/handInExclusive";
    //Vip获取测试报告
    private static String url_get_evaluation_report = url_vip + "/vipQuestion/getEvaluationReport";
    //Vip获取我的错题包
    private static String url_get_question_error_list = url_vip + "/vipQuestion/getQuestionErrorList";
    //Vip删除错题
    private static String url_del_question_error = url_vip + "/vipQuestion/deleteQuestionError";
    //Vip获取我的试卷列表
    private static String url_get_exclusive_errorList = url_vip + "/vipQuestion/getExclusiveErrorList";
    //VIP学员添加留言
    private static String url_vip_addstudent = url_vip + "vipMessage/addStudentMessage";
    //VIP学生老师留言详情
    private static String url_vip_leavedetail = url_vip + "vipMessage/getTeacherMessage";
    //VIP留言列表
    private static String url_vip_rMessageList = url_vip + "vipMessage/getTeacherMessageList";
    //VIP我的资料列表
    private static String url_vip_mydata = url_vip + "vipMaterial/getMaterialList";
    //VIP点赞
    private static String url_vip_Fabulous = url_vip + "/vipMaterial/likeMaterial";
    //vip我的资料链接
    private static String url_vip_DataPPt = url_vip + "/vipMaterial/getMaterialInfo";
    //vip测评报告分享
    private static String url_vip_get_share_result = url_vip + "/vipQuestion/shareResult";
    //vip留言未读标志
    private static String url_vipMessageMark = url_vip + "vipMessage/getMessageMark";
    //记录设备信息
    private static String url_EquipmentInformation = url_user + "member/android_insert?";
    //教师评价接口
    private static String url_submit_comments = url_user + "liveDb/tea_score";
    //视频提现接口
    private static String url_withdrawals = url_user + "liveDb/withdrawals";

    //    private static String url_getIP = "http://ip.chinaz.com/getip.aspx";
//    private static String url_getIP = "http://ip.taobao.com/service/getIpInfo.php?ip=myip";//无效
    private static String url_getIP = "http://httpbin.org/ip";


    //生成微信关联码
    public static String url_getAssCode = url_user + "member/create_assCode?p=";


    /**
     * 直播列表
     *
     * @param pageSize           页数
     * @param page               每页条数
     * @param netClassCategoryId 考试类别
     * @param classPhase         学段
     * @param class_type         课程班型
     * @param subjectType        考试科目
     * @param videoType          视频类型
     * @param listener
     */
    public static void getLiveData(String userId, String page, String pageSize, String netClassCategoryId, String classPhase, String class_type, String
            subjectType, String ActualPrice, String Province, int videoType, final ObtainDataFromNetListener<List<DirectBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_directList;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("pageSize", pageSize);
        jsonObject.addProperty("page", page);
        jsonObject.addProperty("classPhase", classPhase);
        jsonObject.addProperty("class_type", class_type);
        jsonObject.addProperty("netClassCategoryId", netClassCategoryId);
        jsonObject.addProperty("subjectType", subjectType);
        jsonObject.addProperty("ActualPrice", ActualPrice);
        jsonObject.addProperty("Province", Province);
        jsonObject.addProperty("showAllDayItem", "1");
        jsonObject.addProperty("videoType", videoType + "");
        jsonObject.addProperty("type_id", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMCATEGORY_ID, "0"));
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        params.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        List<DirectBean> directBeanList = new Gson().fromJson(object.getString("data"), new TypeToken<List<DirectBean>>() {
                        }.getType());
                        if (listener != null) {
                            listener.onSuccess(directBeanList);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getLiveData-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, params, handler);
    }

    /**
     * 获取面试视频列表数据
     *
     * @param subid ==== 科目选择
     * @param audid ==== 面试形式
     * @param secid ==== 学段
     * @param pnums ==== 页码（默认1）
     */
    public static void getInterviewVideoList(String subid, String audid, String secid, int pnums, final ObtainDataFromNetListener<List<InterviewVideoBean>,
            String>
            listener) {
        if (listener != null) {
            listener.onStart();
        }

        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_getInterviewVideoList;
        RequestParams params = new RequestParams();
        // offset ==== 每页获取的记录数（默认10条）
        params.put("subid", subid);
        params.put("audid", audid);
        params.put("secid", secid);
        params.put("offset", 10);
        params.put("pnums", pnums);
        params.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));//陈伟强测试token
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String code = jsonObject.getString("code");
                    DebugUtil.e("获取面试列表code成功", code);
                    // code 1-成功并且有数据  0-成功并且没有数据  其他是请求失败 401强制下线
                    if ("1".equals(code)) {
                        List<InterviewVideoBean> interviewVideoList = new Gson().fromJson(jsonObject.getString("list"), new
                                TypeToken<List<InterviewVideoBean>>() {
                                }.getType());
                        if (listener != null) {
                            listener.onSuccess(interviewVideoList);
                        }
                    } else if ("0".equals(code)) {
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    }
                    {
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                String msg = "getInterviewVideoList-Failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);

                if (listener != null) {
                    listener.onFailure(statusCode + "");
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 获取面试视频详情数据
     *
     * @param id ==== 视频id
     */
    public static void getInterviewVideoDetail(String id, final ObtainDataFromNetListener<InterviewVideoBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }

        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_getInterviewVideoDetail;
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String code = jsonObject.getString("code");
                    if ("1".equals(code)) {
                        Gson gson = new Gson();
                        InterviewVideoBean interviewVideoBean = gson.fromJson(jsonObject.getString("data"), new TypeToken<InterviewVideoBean>() {
                        }.getType());
                        if (listener != null) {
                            listener.onSuccess(interviewVideoBean);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getInterviewVideoDetail-Failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 直播详情
     *
     * @param directId 直播id
     * @param uid
     * @param listener
     */
    public static void getDirectDetailsData(String uid, String directId, final ObtainDataFromNetListener<DirectBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_directDetailsData + "?rid=" + directId + "&uid=" + uid + "&token=" + CommonUtils.getSharedPreferenceItem(null, UserInfo
                .KEY_SP_ACCESSToken, "");

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        DirectBean directBean = new Gson().fromJson(object.getString("data"), DirectBean.class);
                        if (listener != null) {
                            listener.onSuccess(directBean);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl);
        asyncHttpClient.get(requestUrl, handler);
    }

    /**
     * 直播收藏,取消收藏
     *
     * @param directId    直播id
     * @param type        1 直播
     * @param uid
     * @param directTitle 直播标题
     * @param img_url     直播url
     * @param price       价格 免费0
     * @param listener
     */
    public static void getCollection(String directId, String type, String uid, String directTitle, String img_url, String price, final
    ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_directCollection;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ob_id", directId);
        jsonObject.addProperty("type", "1");
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("name", directTitle);
        jsonObject.addProperty("img_url", img_url);
        jsonObject.addProperty("price", price);

        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    //1成功 0失败 -1参数为空
                    if (listener != null) {
                        listener.onSuccess(code);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        });
    }

    /**
     * 获取我的足迹
     *
     * @param listener
     */
    public static void getDirectHistory(String directIds, final ObtainDataFromNetListener<List<DirectBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_directHistory;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("historyIds", directIds);
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams);
        requestParams.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler(Looper.getMainLooper()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    DebugUtil.e("足迹code4", code);
                    if ("1".equals(code)) {//有数据
                        List<DirectBean> directBeanList = new Gson().fromJson(object.getString("data"), new TypeToken<List<DirectBean>>() {
                        }.getType());
                        if (listener != null) {
                            listener.onSuccess(directBeanList);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        DebugUtil.i("SendRequest:post" + jsonObject.toString());
        asyncHttpClient.post(requestUrl, requestParams, handler);

    }

    /**
     * 直播添加
     *
     * @param directId 直播id
     * @param uid      uid
     * @param listener 没有用到方法不需要加token
     */
    public static void addDirectForMy(String directId, String uid, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_addDirect;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rid", directId);
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("source", "APK");

        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (listener != null) {
                        listener.onSuccess(code);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, params, handler);
    }

    /**
     * 直播删除
     *
     * @param directId 直播id
     * @param oid      订单id
     * @param uid      uid
     * @param listener
     */
    public static void delDirectForMy(String directId, String oid, String uid, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_delDirect;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rid", directId);
        jsonObject.addProperty("oid", oid);
        jsonObject.addProperty("uid", uid);

//        jsonObject.addProperty("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));

        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    DebugUtil.e("code6", code);
                    if (listener != null) {
                        listener.onSuccess(code);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, params, handler);
    }

    /**
     * 直播播放详情
     *
     * @param videoId  视频 id
     * @param listener 方法没用到不需要token
     */
    public static void loadDirectPlayInfo(String videoId, String uid, final ObtainDataFromNetListener<ArrayList<DirectBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_directPlayDetailsData + "?rid=" + videoId + "&uid=" + uid;

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {
//                        DirectBean directBean = new Gson().fromJson(object.getString("data"), DirectBean.class);
                        ArrayList<DirectBean> directBeanList = new Gson().fromJson(object.getString("data"), new TypeToken<List<DirectBean>>() {
                        }.getType());
                        if (listener != null) {
                            listener.onSuccess(directBeanList);
                        }
                    } else if ("0".equals(code)) {
                        listener.onSuccess(null);
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl);
        asyncHttpClient.get(requestUrl, handler);
    }

    /**
     * 直播播放详情
     *
     * @param videoId  视频 id
     * @param listener
     */
    public static void loadDirectPlayInfoDetails(String videoId, final ObtainDataFromNetListener<DirectBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_directPlayInfoDetailsData + "?rid=" + videoId;

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {
                        DirectBean directBean = new Gson().fromJson(object.getString("data"), DirectBean.class);
                        if (listener != null) {
                            listener.onSuccess(directBean);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl);
        asyncHttpClient.get(requestUrl, handler);
    }

    /**
     * 直播在线课表列表
     *
     * @param rid      视频id
     * @param listener
     */
    public static void getLiveDataForClassSchedule(final String rid, String uid, final ObtainDataFromNetListener<ArrayList<DirectBean>, String>
            listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_liveClassScheduleList;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rid", rid);
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));


        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams);

        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    Logger.e("列表:" + rid + GsonUtils.toJson(object));
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        //保存于本地
                        OffLineDataForPlayerDAO.getInstance().save(rid, object.getString("data"));


                        ArrayList<DirectBean> directBeanList = new Gson().fromJson(object.getString("data"), new TypeToken<List<DirectBean>>() {
                        }.getType());
                        if (listener != null) {
                            listener.onSuccess(directBeanList);
                        }


                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        DebugUtil.i("SendRequest:post" + jsonObject.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, requestParams, handler);
    }

    /**
     * 直播在线课程pdf列表
     *
     * @param rid      视频id
     * @param listener
     */
    public static void getLivePDFForClassSchedule(String rid, String uid, final ObtainDataFromNetListener<ArrayList<PdfBean>, String>
            listener) {
        if (listener != null) {
            listener.onStart();
        }

        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_liveClassSchedulePDFList;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rid", rid);
        jsonObject.addProperty("uid", uid);
//        jsonObject.addProperty("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    JSONObject data = object.getJSONObject("data");
                    DataStore_Direct.Common_problem = data.getString("Common_problem");
                    String handout = data.getString("handout");
                    if ("1".equals(code)) {//有数据
                        ArrayList<PdfBean> pdfBean = new Gson().fromJson(handout, new TypeToken<List<PdfBean>>() {
                        }.getType());
                        if (listener != null) {
                            listener.onSuccess(pdfBean);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "?" + params.toString());
        DebugUtil.i("SendRequest:post" + jsonObject.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, params, handler);
    }

    /**
     * 我的在线视频列表
     *
     * @param index     页数
     * @param limit     每页条数
     * @param uid       用户id
     * @param videoType 0 直播 1 高清网课 2 教辅资料
     * @param listener
     */
    public static void getMyLiveData(String index, String limit, String uid, int videoType, final ObtainDataFromNetListener<List<DirectBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_myDirectList;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("limit", limit);
        jsonObject.addProperty("index", index);
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("videoType", videoType + "");
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        params.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        List<DirectBean> directBeanList = new Gson().fromJson(object.getString("data"), new TypeToken<List<DirectBean>>() {
                        }.getType());
                        //全部设为已购买
                        for (int i = 0; i < directBeanList.size(); i++) {
                            directBeanList.get(i).setIs_buy("1");
                        }
                        if (listener != null) {
                            listener.onSuccess(directBeanList);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, params, handler);
    }

    /**
     * 验证手机号是否被注册
     *
     * @param phoneNumber
     */
    public static void VerifyMobileNO(String phoneNumber, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_verifyMobileNO;

        RequestParams params = new RequestParams();
        params.add("mobile", phoneNumber);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String msg = object.getString("msg");
                    // code:0未注册   1已注册
                    if ("0".equals(code)) {
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else {
                        if (listener != null) {
                            listener.onSuccess(msg);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "VerifyMobileNO:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 发送短信验证码
     *
     * @param phoneNumber
     * @param type        2-新注册 1-找回密码
     */
    public static void sendSmsVerification(String phoneNumber, String type, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_SendVerification;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile", phoneNumber);
        jsonObject.addProperty("type", type);
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler(Looper.getMainLooper()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String msg = object.getString("msg");
                    // code:  1：成功  0：失败  -3：手机号已存在
                    if ("1".equals(code)) {
                        if (listener != null) {
                            listener.onSuccess(msg);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(msg);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendSmsVerification:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        DebugUtil.i("SendRequest:post" + jsonObject.toString());
        asyncHttpClient.post(requestUrl, requestParams, handler);
    }

    /**
     * 获取考试科目
     */
    public static void getCategoryList(String currentVideoSubjectId, final ObtainDataFromNetListener<List<CategoryBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_getCatagoryList;

        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", UserInfo.tempCategoryTypeId);
        jsonObject.addProperty("stage", UserInfo.tempStageId);
        if (StringUtil.isEmpty(UserInfo.tempCityId)) {
            jsonObject.addProperty("area", UserInfo.tempProvinceId);
        } else {
            jsonObject.addProperty("area", UserInfo.tempProvinceId + "_" + UserInfo.tempCityId);
        }

        RequestParams params = new RequestParams();
        ExamSubject examSubject = DaoUtils.getInstance().queryExamSubject(currentVideoSubjectId);
        String version;
        if (null != examSubject && !TextUtils.isEmpty(examSubject.getVersion())) {
            version = examSubject.getVersion();
        } else {
            version = "0";
        }
        params.put("versions", version);
        params.put("userInfo", jsonObject.toString());

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody == null || responseBody.length <= 0) {
                    if (listener != null) {
                        listener.onSuccess(null);
                    }
                } else {
                    String res = new String(responseBody);
                    try {
                        String gRes = FileUtils.gunzip(res);
                        List<CategoryBean> categoryBeanList = LoganSquare.parseList(gRes, CategoryBean.class);
                        if (listener != null) {
                            listener.onSuccess(categoryBeanList);
                            DebugUtil.e("getCategoryList:listener.onSuccess " + categoryBeanList.toString());
                        }
                    } catch (IOException e) {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getCategoryList-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "&" + params.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, params, handler);
    }

    /**
     * 获取章节树
     */
    public static void getChapterTree(final ObtainDataFromNetListener<List<CategoryBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_getChapterTree;

        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());
        params.put("versions", sendRequestUtilsForExercise.versions);

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
//                  String gRes = FileUtils.gunzip(res);
                    String gRes = FileUtils.gunzip(res);
                    JSONObject jsonObjectRes = new JSONObject(gRes);
                    List<CategoryBean> categoryBeanList = LoganSquare.parseList(jsonObjectRes.getString("data"), CategoryBean.class);
                    if (listener != null) {
                        listener.onSuccess(categoryBeanList);
                    }
                } catch (IOException e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                } catch (JSONException e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getCategoryList-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);

        AsyncHttpClient client = AsyncHttpClientHelper.createInstance();
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "&" + params.toString());
        client.post(requestUrl, params, handler);
    }

    /**
     * 获取章节树(优化)
     */
    public static void getChapterTree2(final ObtainDataFromNetListener<List<CategoryBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_getChapterTree2;

        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());
        params.put("versions", sendRequestUtilsForExercise.versions);

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
//                  String gRes = FileUtils.gunzip(res);
                    String gRes = FileUtils.gunzip(res);
                    JSONObject jsonObjectRes = new JSONObject(gRes);
                    List<CategoryBean> categoryBeanList = LoganSquare.parseList(jsonObjectRes.getString("data"), CategoryBean.class);
                    ModuleExerciseActivity.deleteQids = new Gson().fromJson(jsonObjectRes.getString("deleteQids"), String[].class);
                    ModuleExerciseActivity.updateQids = new Gson().fromJson(jsonObjectRes.getString("updateQids"), String[].class);
                    ModuleExerciseActivity.updateType = Integer.parseInt(jsonObjectRes.getString("type"));
                    String[] updateQids = new Gson().fromJson(jsonObjectRes.getString("updateQids"), String[].class);
                    for (int i = 0; i < updateQids.length; i++) {
                        DebugUtil.e("getChapterTree2", ModuleExerciseActivity.updateQids[i]);
                    }

                    if (listener != null) {
                        listener.onSuccess(categoryBeanList);
                    }
                } catch (IOException e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                } catch (JSONException e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getCategoryList-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);

        AsyncHttpClient client = AsyncHttpClientHelper.createInstance();
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "&" + params.toString());
        client.post(requestUrl, params, handler);
    }

    /**
     * 注册新用户
     * type ----- 注册类型：1-手机号注册/2-QQ注册/3-微信注册/4-微博注册
     * code ----- 手机注册的验证码 不是的话传“”
     * mobile ----- 手机号
     * password -----用户密码（手机号注册时需要填写，其它方式可为空）
     * nickname -----用户昵称
     * face -----用户图像（手机号注册时是文件类型，其他类型时为图片地址）
     * secypt_keys ------ QQ,微信，微博的 唯一key
     * third_face ----- 第三方注册的图像
     */
    public static void registerNewAccount(final int type, final String mobile, final String password, final String code, final String nickname, final String facePath,
                                          final String third_face, final String secypt_keys, final String secypt_keys_wx,
                                          final ObtainDataFromNetListener<PersonalInfoBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            if (null != listener) {
                listener.onFailure(ERROR_NETWORK);
            }
            return;
        }
        OkGo.<String>get(url_getIP).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String ip = "";

                String originIp = GsonUtils.getJson(response.body(), "origin");
                if (!"服务器异常".equals(originIp)) {
                    ip = originIp;
                }


                registerHttp(ip, type, mobile, password, code, nickname, facePath, third_face, secypt_keys, secypt_keys_wx, listener);

            }

            @Override
            public void onError(Response<String> response) {
                registerHttp("", type, mobile, password, code, nickname, facePath, third_face, secypt_keys, secypt_keys_wx, listener);
            }
        });


    }

    private static void registerHttp(String ip, int type, String mobile, String password, String code, String nickname, String facePath, String third_face, String secypt_keys, String secypt_keys_wx, final ObtainDataFromNetListener<PersonalInfoBean, String> listener) {
        String requestUrl = url_register;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);// 必须不能为空
        jsonObject.addProperty("nickname", nickname);// 必须不能为空
        jsonObject.addProperty("ip", ip);
        jsonObject.addProperty("source", source_android);

        jsonObject.addProperty("anonymous_id", SensorsDataAPI.sharedInstance().getAnonymousId());
        jsonObject.addProperty("distinct_id", SensorsDataAPI.sharedInstance().getDistinctId());

        String channelValue = CommonUtils.getAppMetaData(CustomApplication.applicationContext, "BaiduMobAd_CHANNEL");
        if (!StringUtil.isEmpty(channelValue)) {
            if (channelValue.equals("jinritoutiao") || channelValue.equals("yingyongbao") || channelValue.equals("baidu91") || channelValue.equals("mobile360")
                    || channelValue.equals("xiaomi") || channelValue.equals("oppo") || channelValue.equals("vivo")) {
                jsonObject.addProperty("canal", channelValue);
            }
        }
        if (type == 1) {
            // 手机号注册
            if (TextUtils.isEmpty(mobile)) {
                return;
            }
            if (TextUtils.isEmpty(password)) {
                return;
            }

            jsonObject.addProperty("mobile", mobile);
            jsonObject.addProperty("password", password);
            jsonObject.addProperty("code", code);
        }
        if (type != 1) {
            // 第三方注册
            if (TextUtils.isEmpty(secypt_keys)) {
                return;
            }
            jsonObject.addProperty("secypt_keys", secypt_keys);
            jsonObject.addProperty("third_face", third_face);
            if (!StringUtils.isEmpty(secypt_keys_wx)) {
                jsonObject.addProperty("secypt_keys_wx", secypt_keys_wx);
            }
        }

        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        com.lidroid.xutils.HttpUtils httpUtils = new com.lidroid.xutils.HttpUtils(10000);
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        // 此字段为添加头像送金币用
        params.addBodyParameter("p", newParams);
        if (type == 1) {
            File faceFile = new File(facePath);
            if (faceFile.exists()) {
                params.addBodyParameter("face", faceFile, "image/jpeg");
            }
        }
        DebugUtil.i("httpUtils:post" + " requestUrl:" + requestUrl + "?" + params.toString());
        DebugUtil.i("SendRequest:post" + jsonObject.toString());
        httpUtils.send(HttpMethod.POST, requestUrl, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    // 1：成功/0：失败/-2:缺少参数/-3：缺少唯一key(适用于除了手机号注册以外的方式)/-4:已授权，已注册
                    JSONObject object = new JSONObject(responseInfo.result);
                    String code = object.getString("code");
                    String msg = object.getString("msg");
                    if ("1".equals(code) || "-4".equals(code)) {
                        PersonalInfoBean personalInfoBean = LoganSquare.parse(object.getString("data"), PersonalInfoBean.class);
                        // 手机号注册时，1代表成功，data中保存uid
                        // 授权登录时，1代表成功，且是第一次登录，-4代表成功，是非第一次登录，data中保存用户信息
                        // mobile没值时去绑定手机号，mobile有值时，判断是否选择过考试类型等，进行不同跳转
                        if (listener != null) {
                            listener.onSuccess(personalInfoBean);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(com.lidroid.xutils.exception.HttpException error, String s) {
                String msg = "registerNewAccount:" + s;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        });
    }

    /**
     * 验证 验证码
     * code ----- 验证码
     * mobile ----- 手机号
     */
    public static void registerTestCode(String mobile, String code, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            if (null != listener) {
                listener.onFailure(ERROR_NETWORK);
            }
            return;
        }


        String requesetUrl = url_register_testcode;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", code);
        jsonObject.addProperty("mobile", mobile);
        jsonObject.addProperty("anonymous_id", SensorsDataAPI.sharedInstance().getAnonymousId());
        jsonObject.addProperty("distinct_id", SensorsDataAPI.sharedInstance().getDistinctId());
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams params = new RequestParams();
        params.put("p", newParams);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                DebugUtil.e("onSuccess:" + responseBody.toString());
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String msg = object.getString("msg");
                    // 1：成功
                    if ("1".equals(code)) {
                        if (listener != null) {
                            listener.onSuccess(msg);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(msg);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "loginByMobile:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.e("onFailure", msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requesetUrl + "?" + params.toString());
        DebugUtil.i("SendRequest:post" + jsonObject.toString());
        asyncHttpClient.post(requesetUrl, params, handler);
    }

    /**
     * 收获地址保存
     * uid    用户UID
     * name    用户姓名
     * tel      用户电话
     * address   用户地址
     * province   用户所在省份
     * mr          默认保存地址
     */

    public static void saveAddress(String uid, String name, String tel, String province, String address,
                                   int mr, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            if (null != listener) {
                listener.onFailure(ERROR_NETWORK);
            }
            return;
        }
        String requesetUrl = url_save_address;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("tel", tel);
        jsonObject.addProperty("province", province);
        jsonObject.addProperty("address", address);
        jsonObject.addProperty("mr", mr);
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        if (listener != null) {
                            listener.onSuccess(code);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }


            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requesetUrl + "?" + params.toString());
        asyncHttpClient.post(requesetUrl, params, handler);
    }

    /**
     * 显示地址   进入选择地址界面从服务器获取地址并展示
     * uid  用户UID
     */
    public static void showAddress(String uid, final ObtainDataFromNetListener<List<PersonalAddressBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            if (null != listener) {
                listener.onFailure(ERROR_NETWORK);
            }
            return;
        }
        String requesetUrl = url_show_address;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null));
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        params.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    DebugUtil.e("code12", code);
                    if ("1".equals(code)) {//有数据
                        List<PersonalAddressBean> personalAddressBeanList = new Gson().fromJson(object.getString("data"),
                                new TypeToken<List<PersonalAddressBean>>() {
                                }.getType());
                        if (listener != null) {
                            listener.onSuccess(personalAddressBeanList);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }


            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requesetUrl + "?" + params.toString());
        asyncHttpClient.post(requesetUrl, params, handler);
    }

    /**
     * 修改地址信息并同步更新到服务器
     * uid    用户UID
     * id
     * name    用户姓名
     * tel      用户电话
     * address   用户地址
     * province   用户所在省份
     * mr          默认保存地址
     */


    public static void updateAddress(String uid, String id, String name, String tel, String province, String address,
                                     int mr, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            if (null != listener) {
                listener.onFailure(ERROR_NETWORK);
            }
            return;
        }
        String requesetUrl = url_update_address;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("tel", tel);
        jsonObject.addProperty("address", address);
        jsonObject.addProperty("province", province);
        jsonObject.addProperty("mr", mr);
//        jsonObject.addProperty("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    // 1：成功/0：失败/
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {
                        if (listener != null) {
                            listener.onSuccess(code);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "updateAddress:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure("编辑成功");
                }

            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requesetUrl + "?" + params.toString());
        asyncHttpClient.post(requesetUrl, params, handler);
    }


    /**
     * 删除地址信息同步更新到服务器
     * id   用户ID
     */
    public static void deleteAddress(String uid, String id, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            if (null != listener) {
                listener.onFailure(ERROR_NETWORK);
            }
            return;
        }
        String requesetUrl = url_delete_address;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null));
        jsonObject.addProperty("id", id);
//        jsonObject.addProperty("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        if (listener != null) {
                            listener.onSuccess(code);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }


            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requesetUrl + "?" + params.toString());
        asyncHttpClient.post(requesetUrl, params, handler);
    }

    /**
     * 个人信息保存
     * nickname -----用户昵称
     * sex -----性别
     * face -----用户图像
     * 注意：用了xutils Http上传文件 AsyncHttpClient上传失败
     */
    public static void saveInformation(String nickname, int sex, String facePath, String birthday, final ObtainDataFromNetListener<PersonalInfoBean, String>
            listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            if (null != listener) {
                listener.onFailure(ERROR_NETWORK);
            }
            return;
        }
        String requestUrl = url_save_information;
        com.lidroid.xutils.HttpUtils httpUtils = new com.lidroid.xutils.HttpUtils(10000);
        // 上传文件到服务器
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("uid", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null));
        params.addBodyParameter("nickname", nickname);
        params.addBodyParameter("sex", sex + "");
        params.addBodyParameter("birthday", birthday + "");
        // 此字段为添加头像送金币用
        params.addBodyParameter("source", source_android);
        File faceFile = new File(facePath);
        if (faceFile.exists()) {
            params.addBodyParameter("face", faceFile, "image/jpeg");
        }
        DebugUtil.i("httpUtils:post" + " requestUrl:" + requestUrl + "?" + params.getEntity());
        httpUtils.send(HttpMethod.POST, requestUrl, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                DebugUtil.e("xutils:" + responseInfo.result);
                try {
                    // 1：成功/0：失败/-2：缺少参数/-3：没有可更新的数据
                    JSONObject object = new JSONObject(responseInfo.result);
                    String code = object.getString("code");
                    String msg = object.getString("msg");
                    if ("1".equals(code)) {
                        PersonalInfoBean personalInfoBean = LoganSquare.parse(object.getString("info"), PersonalInfoBean.class);
                        if (listener != null) {
                            listener.onSuccess(personalInfoBean);
                        }
                    } else if ("-3".equals(code)) {
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(com.lidroid.xutils.exception.HttpException error, String s) {
                String msg = "saveInformation:" + s;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        });

    }

    /**
     * 用户信息
     *
     * @param uid
     * @param listener
     */
    public static void getPersonalInfo(String uid, final ObtainDataFromNetListener<PersonalInfoBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_personalInfo_v2 + "?uid=" + uid + "&token" + CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, "");
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//成功
                        String data = object.getString("data");
                        PersonalInfoBean personalInfoBean = LoganSquare.parse(data, PersonalInfoBean.class);
                        if (listener != null) {
                            listener.onSuccess(personalInfoBean);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl);
        asyncHttpClient.get(requestUrl, handler);
    }

    /**
     * 用户登录
     *
     * @param mobile
     * @param password
     * @param listener
     */
    public static void loginByMobile(final String mobile, final String password, final ObtainDataFromNetListener<PersonalInfoBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }


        OkGo.<String>get(url_getIP).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String ip = "";
                String originIp = GsonUtils.getJson(response.body(), "origin");
                if (!"服务器异常".equals(originIp)) {
                    ip = originIp;
                }


                loginByMobileHttp(mobile, password, ip, listener);

            }

            @Override
            public void onError(Response<String> response) {

                loginByMobileHttp(mobile, password, "", listener);
            }
        });


    }

    private static void loginByMobileHttp(String mobile, String password, String ip, final ObtainDataFromNetListener<PersonalInfoBean, String> listener) {
        String requestUrl = url_login;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile", mobile);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("ip", ip);
        jsonObject.addProperty("anonymous_id", SensorsDataAPI.sharedInstance().getAnonymousId());
        jsonObject.addProperty("distinct_id", SensorsDataAPI.sharedInstance().getDistinctId());

        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams params = new RequestParams();
        params.put("p", newParams);


        final String loginType = checkAccount(mobile);


        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                DebugUtil.e("onSuccess:" + responseBody.toString());
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String msg = object.getString("msg");
                    // 1：成功/0：没有此用户或失败/-2:密码错误
                    if ("1".equals(code)) {//成功
                        PersonalInfoBean personalInfoBean = LoganSquare.parse(object.getString("info"), PersonalInfoBean.class);
                        if (listener != null) {
                            listener.onSuccess(personalInfoBean);
                        }
                    } else if ("0".equals(code)) {
                        if (listener != null) {
                            listener.onFailure(msg);
                            DebugUtil.e("onFailure0" + msg.toString());
                        }
                    } else if ("-2".equals(code)) {
                        if (listener != null) {
                            listener.onFailure(msg);
                            DebugUtil.e("onFailure-2" + msg.toString());
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                            DebugUtil.e("onFailure5555" + msg.toString());
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                    TrackUtil.trackLoginInfo(loginType, null, false, "android :数据解析异常");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "loginByMobile:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.e("onFailure", msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }

                TrackUtil.trackLoginInfo(loginType, null, false, "android :" + error != null ? error.getLocalizedMessage() : "网络请求框架异常");
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        DebugUtil.i("SendRequest:get" + jsonObject.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    private static String checkAccount(String mobile) {

        try {
            long num = Long.parseLong(mobile);
            if (num > (Math.pow(10,10)) && num < (Math.pow(10,10)*2)) {
                return "手机号";
            } else {
                return "用户名";
            }

        } catch (Exception e) {
            //非纯数字

        }

        return isEmail(mobile) ? "邮箱" : "用户名";


    }

    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 用户注册后选择考试类型，学段，科目等信息
     *
     * @param listener
     */
    public static void updateInfoAfterRegister(final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }


        try {
            String[] split = UserInfo.tempSubjectsIdName.split(",");
            TrackUtil.trackExamInfoSubmit(UserInfo.tempCategoryTypeName,
                    UserInfo.tempStageName,
                    split.length == 0 ? Collections.singletonList(UserInfo.tempSubjectsIdName) : Arrays.asList(split));
        } catch (Exception e) {
            Logger.e(SensorDataSdk.TAG + e.getLocalizedMessage());
        }


        String requestUrl = url_login_updateinfo;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null));
        jsonObject.addProperty("type_id", UserInfo.tempCategoryTypeId);
        if (!StringUtil.isEmpty(UserInfo.tempCityId)) {
            jsonObject.addProperty("city", UserInfo.tempCityId);
            jsonObject.addProperty("province", UserInfo.tempProvinceId);
        } else {
            jsonObject.addProperty("province", UserInfo.tempProvinceId);
            jsonObject.addProperty("city", UserInfo.tempProvinceId);
        }
        jsonObject.addProperty("sec_id", UserInfo.tempStageName);
        jsonObject.addProperty("sub_id", UserInfo.tempSubjectId);
        jsonObject.addProperty("sub_ids", UserInfo.tempSubjectsIdName);
//        jsonObject.addProperty("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));

        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    // 1：更新成功/0：更新失败
                    if ("1".equals(code)) {//成功
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "updateInfoAfterRegister:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 根据科目id获取科目名称
     */
    public static void getCategoryBeansByIds(String subject, final ObtainDataFromNetListener<List<CategoryBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(subject);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.add("examSubject", jsonArray);
        String requestUrl = url_getCategoryBeansByIds + "&userInfo=" + jsonObject.toString();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler(Looper.getMainLooper()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    String gRes = FileUtils.gunzip(res);
                    DebugUtil.e("getCategoryBeansByIds:" + gRes.toString());
                    List<CategoryBean> categoryBeanList = LoganSquare.parseList(gRes, CategoryBean.class);

                    if (listener != null) {
                        listener.onSuccess(categoryBeanList);
                    }
                } catch (IOException e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getCategoryBeansByIds-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl);
        AsyncHttpClientHelper.createInstance().get(requestUrl, handler);
    }

    /**
     * 绑定手机号
     * type 1-更换手机 2-设置手机
     *
     * @param listener
     */
    public static void bindMobile(String uid, String mobile, String old_mobile, String code, String type, final ObtainDataFromNetListener<String, String>
            listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_update_tel;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("mobile", mobile);
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("code", code);
        if (!StringUtil.isEmpty(old_mobile)) {
            jsonObject.addProperty("old_mobile", old_mobile);
        }

        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailure(ERROR_SERVER);
            }
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams params = new RequestParams();
        params.put("p", newParams);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String msg = object.getString("msg");
                    // 1：绑定成功/0：绑定失败/-1：缺少参数/-5：手机号已绑定
                    if ("1".equals(code)) {//成功
                        if (listener != null) {
                            listener.onSuccess(msg);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(msg);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "bindMobile:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "?" + params.toString());
        DebugUtil.i("SendRequest:post" + jsonObject.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 金币充值(消费)创建订单
     *
     * @param uid
     * @param goldNumber 金币数量
     * @param type       1充值 2消费
     * @param listener
     */
    public static void createOrderForGold(String uid, String goldNumber, String type, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_createOrderForGoldRecharge;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("gold", goldNumber);
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("source", source_android);
//        jsonObject.addProperty("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        byte[] bs = null;   //3des加密
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailure(ERROR_SERVER);
            }
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams params = new RequestParams();
        params.put("p", newParams);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String message = object.getString("message");
                    if (!TextUtils.isEmpty(code) && "1".equals(code)) {//成功
                        String data = object.getString("data");
                        if (listener != null) {
                            listener.onSuccess(data);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(message);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        DebugUtil.i("SendRequest:" + jsonObject.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 收藏习题
     *
     * @param qids     习题题号集合
     * @param listener
     */
    public static void storeExercise(List<String> qids, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_storeExercise;
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        Gson gson = new Gson();
        String jsonIds = gson.toJson(qids, ArrayList.class);

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());
        params.put("qids", jsonIds);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        asyncHttpClient.setTimeout(5 * 1000);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    boolean success = object.getBoolean("success");
                    String msg = object.getString("message");
                    if (success) {
                        if (listener != null) {
                            listener.onSuccess("success");
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(msg);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "storeExercise:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 取消收藏
     *
     * @param qids     习题题号集合
     * @param listener
     */
    public static void unstoreExercise(List<String> qids, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_unstoreExercise;
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        Gson gson = new Gson();
        String jsonIds = gson.toJson(qids, ArrayList.class);

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());
        params.put("qids", jsonIds);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        asyncHttpClient.setTimeout(5 * 1000);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    boolean success = object.getBoolean("success");
                    String msg = object.getString("message");
                    if (success) {
                        if (listener != null) {
                            listener.onSuccess("success");
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(msg);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "storeExercise:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 在线模考和真题演练 收藏习题（试卷）
     *
     * @param pid      试卷id
     * @param qid      试题id
     * @param listener
     */
    public static void storePaperExercise(String pid, String qid, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_storePaperExercise;
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);


        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());
        params.put("pid", pid);
        params.put("qid", qid);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        asyncHttpClient.setTimeout(5 * 1000);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    boolean success = object.getBoolean("success");
                    String msg = object.getString("message");
                    if (success) {
                        if (listener != null) {
                            listener.onSuccess("success");
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(msg);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "storeExercise:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 交卷
     *
     * @param paperPo
     * @param listener
     */
    public static void handinPapers(final PaperPo paperPo, final ObtainDataFromNetListener<PaperAttrVo, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_handin_papers;
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        Gson gson = new Gson();
        String paperpoGson = gson.toJson(paperPo, PaperPo.class);

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());
        params.put("testpaper", paperpoGson);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        asyncHttpClient.setTimeout(10 * 1000);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    boolean isSuccess = (boolean) jsonObject.get("success");
                    if (isSuccess) {
                        PaperAttrVo paperAttrVo;
                        // 0今日特训 1模块题海 2真题测评
                        if ("0".equals(paperPo.getType()) || "1".equals(paperPo.getType())) {
                            List<QuesAttrVo> qavList = new Gson().fromJson(jsonObject.getString("data"), new TypeToken<List<QuesAttrVo>>() {
                            }.getType());
                            paperAttrVo = new PaperAttrVo();
                            paperAttrVo.setQavList(qavList);
                        } else {
                            paperAttrVo = new Gson().fromJson(jsonObject.getString("data"), PaperAttrVo.class);
                        }

                        if (listener != null) {
                            listener.onSuccess(paperAttrVo);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "storeExercise:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);
        DebugUtil.e("SendRequest:post123" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 购买生成订单接口
     *
     * @param directId 直播id
     * @param uid      uid
     * @param listener
     */
    public static void createOrderForSumit(String directId, String uid, String addressId, String pay_moble, String learning_period, String subject, final ObtainDataFromNetListener<String, String>
            listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_createOrderForSubmit;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rid", directId);
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("source", "APK");
//        jsonObject.addProperty("isHasJy", isHasJy);
        jsonObject.addProperty("addressId", addressId);
        jsonObject.addProperty("pay_moble", pay_moble);
        jsonObject.addProperty("studySection", learning_period);
        jsonObject.addProperty("subject", subject);
        jsonObject.addProperty("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));

        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailure(ERROR_SERVER);
            }
        }
        String newparams = TripleDES.byte2hex(bs);
        RequestParams params = new RequestParams();
        params.put("p", newparams);
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {
                        if (listener != null) {
                            listener.onSuccess(object.getString("data"));
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.e("SendRequest:post" + " requestUrl:" + requestUrl + "?" + params.toString());
        DebugUtil.i("SendRequest:post" + jsonObject.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, params, handler);
    }

    /**
     * 金币支付
     *
     * @param uid
     * @param orderid    订单号
     * @param goldNumber 金币数
     * @param type       1直播
     * @param listener
     */
    public static void toPayForGold(String uid, String orderid, Double goldNumber, String type, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_toPayForGold;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("golds", goldNumber);
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("oid", orderid);
        jsonObject.addProperty("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailure(ERROR_SERVER);
            }
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams params = new RequestParams();
        params.put("p", newParams);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String message = object.getString("message");
                    //本地记录的金币更新
                    if (!TextUtils.isEmpty(code)) {
                        if (listener != null) {
                            if ("1".equals(code)) {
                                listener.onSuccess(code);
                                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_GOLD, object.getString("gold"));
                            } else {
                                listener.onFailure(message);
                            }
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        DebugUtil.i("SendRequest:" + jsonObject.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 账单
     *
     * @param uid
     * @param index    页码
     * @param limit    条目数
     * @param listener
     */
    public static void getBillList(String uid, int index, String limit, final ObtainDataFromNetListener<List<BillBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_billList;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("index", String.valueOf(index));
        jsonObject.addProperty("limit", limit);
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        params.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    List<BillBean> billBeanList = LoganSquare.parseList(object.getString("data"), BillBean.class);
                    if ("1".equals(code)) {//有数据
                        if (listener != null) {
                            listener.onSuccess(billBeanList);
                        }
                    } else if ("0".equals(code)) {//无数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 获取面试视频列表科目的筛选项
     *
     * @param listener
     */
    public static void getVideoSubjects(final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_video_subject;
        RequestParams params = new RequestParams();
        params.put("version", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_VIDEO_SUBJECTS_VERSION, "1.0"));
//        params.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    // code   1：成功/0：没有数据
                    if ("1".equals(code)) {//有数据
                        CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_VIDEO_SUBJECTS_VERSION, object.getString("version"));
                        if (listener != null) {
                            listener.onSuccess(object.getString("list"));
                        }
                    } else if ("0".equals(code)) {//无数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getVideoSubjects:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 用户反馈
     *
     * @param uid
     * @param content  反馈内容
     * @param listener
     */
    public static void submitFeedBack(String uid, String content, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_submitFeedBack;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("content", content);
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        params.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (listener != null) {
                        listener.onSuccess(code);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 获取真题测评试卷列表
     *
     * @param page     获取的第几页数据
     * @param listener
     */

    public static void getPaperList(int page, String years, final ObtainDataFromNetListener<List<PaperItem>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_obtain_paper_list;
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);
        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());
        params.put("pageindex", page);
        params.put("keyword", years);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    String gRes = FileUtils.gunzip(res);
                    List<PaperItem> paperItemList = LoganSquare.parseList(gRes, PaperItem.class);
                    if (listener != null) {
                        listener.onSuccess(paperItemList);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getPaperList:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 获取模考大赛试卷列表
     *
     * @param page     获取的第几页数据
     * @param listener
     */
    public static void getCompetition(int page, String years, final ObtainDataFromNetListener<List<PaperItem>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = test;
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());
        params.put("pageindex", page);
        params.put("keyword", years);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    String gRes = FileUtils.gunzip(res);
                    List<PaperItem> paperItemList = LoganSquare.parseList(gRes, PaperItem.class);
                    if (listener != null) {
                        listener.onSuccess(paperItemList);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getPaperList:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 获取错题
     *
     * @param listener
     */
    public static void getWrongQuestions(final ObtainDataFromNetListener<CategoryBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_get_wrongques;
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    String gRes = FileUtils.gunzip(res);
                    CategoryBean categoryBean = LoganSquare.parse(gRes, CategoryBean.class);
                    if (listener != null) {
                        listener.onSuccess(categoryBean);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getWrongQuestions:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }

                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 模块题海 移除错题
     *
     * @param listener
     */
    public static void deleteWrongQuestions(List<String> qids, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_remove_wrongques;
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        Gson gson = new Gson();

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());
        params.put("qids", gson.toJson(qids, ArrayList.class));

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    if (listener != null) {
                        listener.onSuccess(res);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getWrongQuestions:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }

                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 真题演练和在线模考 移除错题或取消收藏（试卷）
     *
     * @param pid      试卷id
     * @param qid      试题id
     * @param type     错题:error,收藏:collect
     * @param listener
     */
    public static void deletePaperWrongQuestions(String pid, String qid, String type, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_remove_paper_wrongques;
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        Gson gson = new Gson();

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());
        params.put("pid", pid);
        params.put("type", type);
        params.put("qid", qid);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    if (listener != null) {
                        listener.onSuccess(res);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "deletePaperWrongQuestions:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }

                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 获取收藏的错题
     *
     * @param listener
     */
    public static void getCollectionQuestions(final ObtainDataFromNetListener<CategoryBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_get_collectionques;
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    String gRes = FileUtils.gunzip(res);
                    CategoryBean categoryBean = LoganSquare.parse(gRes, CategoryBean.class);
                    if (listener != null) {
                        listener.onSuccess(categoryBean);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getWrongQuestions:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }

                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 忘记密码的修改密码
     *
     * @param mobile   手机号
     * @param newPwd   新密码
     * @param listener
     */
    public static void modifyPwd(String mobile, String newPwd, String code, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_modify_pwd;


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile", mobile);
        jsonObject.addProperty("code", code);
        jsonObject.addProperty("password", newPwd);
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailure(ERROR_SERVER);
            }
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String msg = object.getString("msg");
                    // 1：成功/0：失败/-2：缺少参数
                    if (!TextUtils.isEmpty(code.trim()) && "1".equals(code)) {
                        if (listener != null) {
                            listener.onSuccess("密码修改成功");
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(msg);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "modifyPwd:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        DebugUtil.i("SendRequest:post" + jsonObject.toString());
        asyncHttpClient.post(requestUrl, requestParams, handler);
    }

    //获取公共号内容
    public static void obtainSubscription_teacherOnline(int page, final ObtainDataFromNetListener<List<SubscriptionBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_obtainSubscription + "/" + page;
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler(Looper.getMainLooper()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    List<SubscriptionBean> beans = LoganSquare.parseList(res, SubscriptionBean.class);
                    if (listener != null) {
                        listener.onSuccess(beans);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                String msg = "obtainSubscription_Huatu:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl);
        asyncHttpClient.post(requestUrl, handler);
    }

    //获取打卡信息
    public static void querySignCard(String uid, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_sign;
        RequestParams requestParams = new RequestParams();
        requestParams.put("type", 1);
        requestParams.put("source", "APK");
        requestParams.put("uid", uid);
        requestParams.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler(Looper.getMainLooper()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(res);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    String code = jsonObject.get("code").getAsString();

                    //1 表示已打卡 0表示未打卡
                    if (listener != null) {
                        listener.onSuccess(code);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                String msg = "signCardEveDay:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        asyncHttpClient.get(requestUrl, requestParams, handler);
    }

    //获取打卡信息
    public static void signCardEveDay(String uid, final ObtainDataFromNetListener<PersonalInfoBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_sign;
        RequestParams requestParams = new RequestParams();
        requestParams.put("type", 3);//新版打卡传参3 原版2
        requestParams.put("source", "APK");
        requestParams.put("uid", uid);
        requestParams.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler(Looper.getMainLooper()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(res);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    String code = jsonObject.get("code").getAsString();
                    PersonalInfoBean personalInfoBean;

                    if (listener != null) {
                        //1打卡成功
                        if ("1".equals(code)) {
                            personalInfoBean = LoganSquare.parse(jsonObject.get("info").toString(), PersonalInfoBean.class);
                            listener.onSuccess(personalInfoBean);
                        } else {
                            //0打卡失败 3此用户已打卡
                            listener.onFailure(code);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                String msg = "signCardEveDay:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        asyncHttpClient.get(requestUrl, requestParams, handler);
    }

    //分享任务
    public static void shareTask(String uid, String type, final ObtainDataFromNetListener<PersonalInfoBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("source", "APK");
        jsonObject.addProperty("uid", uid);

        String requestUrl = url_user_share;

        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);

        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler(Looper.getMainLooper()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(res);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    String code = jsonObject.get("code").getAsString();
                    PersonalInfoBean personalInfoBean;
                    if (listener != null) {
                        //1首次分享成功
                        if ("1".equals(code)) {
                            personalInfoBean = LoganSquare.parse(jsonObject.get("info").toString(), PersonalInfoBean.class);
                            listener.onSuccess(personalInfoBean);
                        } else {
                            //不关注
                            listener.onFailure(code);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                String msg = "signCardEveDay:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl);
        DebugUtil.i("SendRequest:" + jsonObject.toString());
        asyncHttpClient.get(requestUrl, requestParams, handler);
    }

    //获取最新版本
    public static void getVersion(final ObtainDataFromNetListener<String[], String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_getVersionCode;
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl);
        asyncHttpClient.post(requestUrl, new AsyncHttpResponseHandler(Looper.getMainLooper()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String app_versionCode = jsonObject.getString("APP_VERSION");//版本号
//                    String app_updateLog = jsonObject.getString("APP_UPDATE_LOG");//更新日志
                    JSONArray app_update_log = jsonObject.getJSONArray("APP_UPDATE_LOG");
                    StringBuffer update_log = new StringBuffer();
                    for (int i = 0; i < app_update_log.length(); i++) {
                        if (i < app_update_log.length() - 1) {
                            update_log.append(app_update_log.getString(i) + "<br>");
                        } else {
                            update_log.append(app_update_log.getString(i));
                        }
                    }
                    String app_downloadUrl = jsonObject.getString("APP_DOWN_URL");//更新地址
                    String app_isupdate = jsonObject.getString("IS_UPDATE");//是否强制更新
                    if (!TextUtils.isEmpty(app_versionCode)) {
                        String[] versionInfo = new String[4];
                        versionInfo[0] = app_versionCode;
                        versionInfo[1] = update_log.toString();
                        versionInfo[2] = app_downloadUrl;
                        versionInfo[3] = app_isupdate;
                        if (listener != null) {
                            listener.onSuccess(versionInfo);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                String msg = "obtainSubscription_Huatu:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        });
    }

    /**
     * 提交题目纠错
     *
     * @param qid        试题ID
     * @param qtype      试题类型
     * @param qcontent   题干
     * @param error_type 纠错类型
     * @param desc       纠错描述
     * @param listener
     */
    public static void submitExerciseError(String qid, String qtype, String qcontent, List<String> error_type, String desc, String module, final
    ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_submitExerciseError;

        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        JsonArray correctTypeJsonArray = new JsonArray();
        for (int i = 0; i < error_type.size(); i++) {
            correctTypeJsonArray.add(error_type.get(i));
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());
        params.put("qid", qid);//试题ID
        params.put("qtype", qtype);//试题类型
        params.put("qcontent", qcontent);//题干
        params.put("correctType", correctTypeJsonArray);//纠错类型
        params.put("desc", desc);//纠错描述
        params.put("module", module);//题目来源

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String result = jsonObject.getString("success");
                    if (listener != null) {
                        listener.onSuccess(result);
                    }
                } catch (JSONException e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getCategoryList-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);

        AsyncHttpClient client = AsyncHttpClientHelper.createInstance();
        client.setTimeout(5 * 1000);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        client.post(requestUrl, params, handler);
    }

    /**
     * 我的纠错题
     *
     * @param uid
     * @param currentPage
     * @param listener
     */
    public static void getMyErrorCorrection(String uid, int currentPage, final ObtainDataFromNetListener<List<MyErrorCorrectionBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_MyErrorCorrection + "&uid=" + uid + "&currentPage=" + currentPage;

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                Gson gson = new Gson();
                List<MyErrorCorrectionBean> myErrorCorrectionBeanList = gson.fromJson(res, new TypeToken<List<MyErrorCorrectionBean>>() {
                }.getType());
                if (listener != null) {
                    listener.onSuccess(myErrorCorrectionBeanList);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getCategoryList-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);
        AsyncHttpClient client = AsyncHttpClientHelper.createInstance();
        client.setTimeout(5 * 1000);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl);
        client.get(requestUrl, handler);
    }

    /**
     * 激活课程
     *
     * @param uid
     * @param acCode
     * @param listener
     */
    public static void getActiveNet(String uid, String rid, String acCode, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_activationCourse;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("rid", rid);
        jsonObject.addProperty("acCode", acCode);
        jsonObject.addProperty("source", "APK");
        jsonObject.addProperty("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (listener != null) {
                        listener.onSuccess(code);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 获取我的订单
     *
     * @param limit
     * @param index
     * @param uid
     * @param isNew    是否新版本
     * @param listener
     */
    public static void getMyOrder(String limit, String index, String uid, String isNew, final ObtainDataFromNetListener<ArrayList<OrderBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_myorder;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("limit", limit);
        jsonObject.addProperty("index", index);
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("isNew", isNew);
//        RequestParams params = new RequestParams();
//        params.put("p", jsonObject.toString());
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams);
        requestParams.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        String customer = object.getString("customer");
                        String branchschoolid = object.getString("branchschoolid");
                        MyOrderActivity.customer = customer;
                        MyOrderActivity.branchschoolid = branchschoolid;
                        ArrayList<OrderBean> orderBeanList = new Gson().fromJson(object.getString("data"), new TypeToken<List<OrderBean>>() {
                        }.getType());
                        if (listener != null) {
                            listener.onSuccess(orderBeanList);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getLiveData-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        DebugUtil.i("SendRequest:post" + jsonObject.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, requestParams, handler);
    }

    /**
     * 获取订单物流信息
     *
     * @param com      物流公司
     * @param nu       物流订单号
     * @param listener
     */
    public static void getLogisticsInfo(String com, String nu, final ObtainDataFromNetListener<LogisticsBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_logisticsinfo;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("com", com);
        jsonObject.addProperty("nu", nu);
//        RequestParams params = new RequestParams();
//        params.put("p", jsonObject.toString());
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams);
        requestParams.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    LogisticsBean logisticsBean = new Gson().fromJson(res, LogisticsBean.class);
                    if (listener != null) {
                        listener.onSuccess(logisticsBean);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getLiveData-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        DebugUtil.i("SendRequest:post" + jsonObject.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, requestParams, handler);
    }

    /**
     * 获取信息列表
     *
     * @param listener
     */
    public static void getMessageList(String uid, String page, String pageSize, int type, final ObtainDataFromNetListener<ArrayList<MessageBean>, String>
            listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_myMessage;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page", page);
        jsonObject.addProperty("uId", uid);
        jsonObject.addProperty("pageSize", pageSize);
        jsonObject.addProperty("type", type);
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        params.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String status = object.getString("status");
                    String code = object.getString("code");
                    MyssageBean messageBean = new Gson().fromJson(status, MyssageBean.class);
                    CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_TYPE_ONE, messageBean.gettype_3());
                    CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_TYPE_TWO, messageBean.gettype_1());
                    CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_TYPE_THREE, messageBean.gettype_2());
                    if ("1".equals(code)) {//有数据
                        ArrayList<MessageBean> messageBeannList = new Gson().fromJson(object.getString("data"), new TypeToken<List<MessageBean>>() {
                        }.getType());
                        if (listener != null) {
                            listener.onSuccess(messageBeannList);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getLiveData-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        DebugUtil.i("SendRequest:" + jsonObject.toString());
        AsyncHttpClientHelper.createInstance().get(requestUrl, params, handler);
    }

    /**
     * 消息删除
     *
     * @param msgId    消息id
     * @param uid      uid
     * @param listener
     */
    public static void delMessageForMy(String msgId, String uid, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_delMessage;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("msgId", msgId);
        jsonObject.addProperty("uId", uid);
//        jsonObject.addProperty("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));

//        RequestParams params = new RequestParams();
//        params.put("p", jsonObject.toString());
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams);


        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (listener != null) {
                        listener.onSuccess(code);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        DebugUtil.i("SendRequest:post" + jsonObject.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, requestParams, handler);
    }

    /**
     * 更改消息状态
     *
     * @param msgId    消息id
     * @param uid      uid
     * @param listener
     */
    public static void updateMessageForMy(String msgId, String uid, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_updateMessage;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("msgId", msgId);
        jsonObject.addProperty("uId", uid);
//        RequestParams params = new RequestParams();
//        params.put("p", jsonObject.toString());
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams);
        requestParams.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (listener != null) {
                        listener.onSuccess(code);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        DebugUtil.i("SendRequest:" + jsonObject.toString());
        AsyncHttpClientHelper.createInstance().get(requestUrl, requestParams, handler);
    }

    /**
     * @param account      用户id
     * @param sid          课时房间号
     * @param rid          课程id
     * @param rName        课程标题
     * @param nTitle       课时标题
     * @param userpassword 用户密码
     * @param videoType    直播类型
     * @param listener
     */
    public static void addCliks(String account, String sid, String rid, String rName, String nTitle, String userpassword, String videoType, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_addClicks;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("account", account);
        jsonObject.addProperty("sid", sid);
        jsonObject.addProperty("rid", rid);
        jsonObject.addProperty("rName", rName);
        jsonObject.addProperty("nTitle", nTitle);
        jsonObject.addProperty("userpassword", userpassword);
        jsonObject.addProperty("videoType", videoType);
//        jsonObject.addProperty("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
//        RequestParams params = new RequestParams();
//        params.put("p", jsonObject.toString());
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams);


        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String message = object.getString("message");
                    if (listener != null) {
                        if (!StringUtils.isEmpty(code) && code.equals("1")) {
                            listener.onSuccess(code);
                        } else {
                            if (!StringUtils.isEmpty(code)) {
                                listener.onFailure(message);
                            } else {
                                listener.onFailure(ERROR_SERVER);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        DebugUtil.i("SendRequest:" + jsonObject.toString());
        AsyncHttpClientHelper.createInstance().get(requestUrl, requestParams, handler);
    }

    /**
     * 添加提现时长
     *
     * @param UserId     用户id
     * @param UserName   用户名称
     * @param OrderNum   订单id
     * @param JoinTime   开始播放时间
     * @param Leavetime  离开播放时间
     * @param Netclassid 课程id
     * @param Lessionid  课件id
     * @param Params     课时标题
     * @param listener   固定参数，看回放参数为 lubo，看直播参数为zhibo
     */
    public static void addRecord(String UserId, String UserName, String OrderNum, final String JoinTime, String Leavetime, String Netclassid, String Lessionid, String Params, final ObtainDataFromNetListener<WithdrawalBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }


        final RecodeRequestFailure recodeRequestFailure = new RecodeRequestFailure(UserId, UserName, OrderNum, JoinTime, Leavetime, Netclassid, Lessionid, Params);

        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            RecodeRequestFailureManager.getInstance().save(recodeRequestFailure);
            return;
        }

        String requestUrl = url_addRecord;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("UserId", UserId);
        jsonObject.addProperty("UserName", UserName);
        jsonObject.addProperty("OrderNum", OrderNum);
        jsonObject.addProperty("JoinTime", JoinTime);
        jsonObject.addProperty("Leavetime", Leavetime);
        jsonObject.addProperty("Netclassid", Netclassid);
        jsonObject.addProperty("Lessionid", Lessionid);
        jsonObject.addProperty("Params", Params);

        Logger.e("addRecord:" + GsonUtils.toJson(jsonObject));
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
//        byte[] bs = null;
//        try {
//            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String newParams = TripleDES.byte2hex(bs);
//        RequestParams requestParams = new RequestParams();
//        requestParams.put("p", newParams);

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String message = object.getString("message");
                    if ("1".equals(code)) {
                        WithdrawalBean withdrawalBean = new Gson().fromJson(object.getString("data"), WithdrawalBean.class);
                        if (listener != null) {
                            listener.onSuccess(withdrawalBean);
                        }
                    } else {
                        listener.onFailure(message);
                    }

                    RecodeRequestFailureManager.getInstance().deleteByJoinTime(JoinTime);
                } catch (Exception e) {
                    RecodeRequestFailureManager.getInstance().save(recodeRequestFailure);
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                RecodeRequestFailureManager.getInstance().save(recodeRequestFailure);

                String msg = "addRecord:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        DebugUtil.i("SendRequest:" + jsonObject.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, params, handler);
    }


    public static String addRecodeSync(RecodeRequestFailure info) {
        Logger.e("RecodeRequestFailure");
        HttpParams httpParams = new HttpParams();
        httpParams.put("p", GsonUtils.toJsonWithoutExposeAnnotation(info));
        String result = null;
        try {
            okhttp3.Response response = OkGo.post(url_addRecord)
                    .isSpliceUrl(true)
                    .params(httpParams)
                    .execute();

            String data = response.body().string();
//            if (data != null) {
//
//                String code = GsonUtils.getJson(data, "code");
//
//            }
            result = data;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 修改密码
     *
     * @param account  账号
     * @param password 新密码
     * @param old_pass 旧密码
     * @param listener
     */
    public static void changePwd(String account, String password, String old_pass, String type, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_change_pwd;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("account", account);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("old_pass", old_pass);
        jsonObject.addProperty("type", type);

//        RequestParams params = new RequestParams();
//        params.put("p", jsonObject.toString());
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams);


        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String msg = object.getString("msg");
                    if (code.equals("1")) {
                        if (listener != null) {
                            listener.onSuccess("密码修改成功");
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(msg);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        DebugUtil.i("SendRequest:post" + jsonObject.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, requestParams, handler);
    }

    /**
     * 获得城市列表
     */
    public static void getlicense(final ObtainDataFromNetListener<ArrayList<ProvinceWithCityBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_cityes;
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("message");
                    if (code.equals("获取省份-城市信息成功")) {
                        ArrayList<ProvinceWithCityBean> mProvinceWithCityBeanList =
                                new Gson().fromJson(object.getString("data"), new TypeToken<List<ProvinceWithCityBean>>() {
                                }.getType());
                        if (listener != null) {
                            listener.onSuccess(mProvinceWithCityBeanList);
                        }
                    }
                } catch (JSONException e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl);
        AsyncHttpClientHelper.createInstance().get(requestUrl, handler);
    }

    /**
     * 取消登录接口
     */
    public static void Canel_login(String uid, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = Cancel_login;
        RequestParams requestParams = new RequestParams();
        requestParams.put("uid", uid.toString());
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject json = new JSONObject(res.toString());
                    String code = json.getString("code");
                    if (code.equals("1")) {
                        listener.onSuccess(code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.e("SendRequest:" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        AsyncHttpClientHelper.createInstance().get(requestUrl, requestParams, handler);
    }


    /**
     * 二维码登录
     */
    public static void get_barcode(String username, String randnumber, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_code;
        RequestParams requestParams = new RequestParams();
        requestParams.put("username", username);
        requestParams.put("randnumber", randnumber);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject json = new JSONObject(res.toString());
                    String code = json.getString("status");
                    String msg = json.getString("msg");
                    if (code.equals("1")) {
                        listener.onSuccess(msg);
                    } else {
                        if (listener != null) {
                            listener.onFailure(msg);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.e("SendRequest:" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, requestParams, handler);
    }

    /**
     * 真题交卷
     *
     * @param paperPo
     * @param listener
     */
    public static void handinErPapers(final PaperPo paperPo, final ObtainDataFromNetListener<ErPaperAttrVo, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_handin_papers;
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        Gson gson = new Gson();
        String paperpoGson = gson.toJson(paperPo, PaperPo.class);
        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());
        params.put("testpaper", paperpoGson);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        asyncHttpClient.setTimeout(10 * 1000);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    boolean isSuccess = (boolean) jsonObject.get("success");
                    if (isSuccess) {
                        ErPaperAttrVo erPaperAttrVo = LoganSquare.parse(jsonObject.getString("data"), ErPaperAttrVo.class);
                        if (listener != null) {
                            DebugUtil.e("handinErPapers" + erPaperAttrVo.toString());
                            listener.onSuccess(erPaperAttrVo);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(jsonObject.getString("message"));
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "storeExercise:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "&" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 获取服务器当前时间
     *
     * @param listener
     */
    public static void getNowTime(final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_getNowTime;
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        asyncHttpClient.setTimeout(5 * 1000);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    boolean isSuccess = (boolean) jsonObject.get("success");
                    if (isSuccess) {
                        long time = jsonObject.getLong("data");
                        String simpleDate = StringUtils.getSimpleDate(time);
                        listener.onSuccess(simpleDate);
                    } else {
                        if (listener != null) {
                            listener.onFailure(jsonObject.getString("message"));
                        }
                    }
                } catch (Exception e) {
                    DebugUtil.i(TAG, e.getMessage());
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "storeExercise:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:get" + " requestUrl:" + requestUrl);
        asyncHttpClient.get(requestUrl, handler);
    }

    /**
     * 根据pid获取该试卷信息
     *
     * @param listener
     */
    public static void getPaperNum(String pid, final ObtainDataFromNetListener<PaperItem, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_getPaperNum;
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());
        params.put("pid", pid);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        asyncHttpClient.setTimeout(5 * 1000);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    boolean isSuccess = (boolean) jsonObject.get("success");
                    if (isSuccess) {
                        PaperItem paperItem = LoganSquare.parse(jsonObject.getString("data"), PaperItem.class);
                        listener.onSuccess(paperItem);
                    } else {
                        if (listener != null) {
                            listener.onFailure(jsonObject.getString("message"));
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "storeExercise:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };

        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "&" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 启动页
     *
     * @param listener
     */
    public static void getStartpage(String width, String height, final ObtainDataFromNetListener<StartsBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_startpage;
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("width", width);
        jsonObject.addProperty("height", height);
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        asyncHttpClient.setTimeout(5 * 1000);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String code = (String) jsonObject.get("code");
                    StartsBean startBean = new Gson().fromJson(res, StartsBean.class);
//                    DebugUtil.e("启动数据",res);
//                    if (listener != null) {
//                        listener.onFailure(jsonObject.getString("message"));
//                    }
                    if (code.equals("1") || code.equals("0")) {
                        listener.onSuccess(startBean);
                    } else {
                        listener.onFailure(jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "storeExercise:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "&" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 清空对应消息
     *
     * @param listener
     */
    public static void EmptyMessage(String uid, String type, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_empty_message;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uId", uid);
        jsonObject.addProperty("type", type);
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams.toString());
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        asyncHttpClient.setTimeout(5 * 1000);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String code = (String) jsonObject.get("code");
                    if (code.equals("1")) {
                        listener.onSuccess(code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:post" + " requestUrl:" + requestUrl + "&" + newParams.toString());
        asyncHttpClient.get(requestUrl, requestParams, handler);
    }

    /**
     * 获取支付签名
     *
     * @param type      支付类型 1：微信 2：支付宝
     * @param oid       订单id
     * @param orderType 订单类型 1：金币充值订单 2：课程购买订单
     * @param listener
     */
    public static void getPayEncryptionSignature(String type, String oid, String orderType, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_getPayEncryptionSignature + "?type=" + type + "&oid=" + oid + "&orderType=" + orderType + "&token" + CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, "");
//        final JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("type", type);
//        jsonObject.addProperty("oid", oid);
//        RequestParams params = new RequestParams();
//        params.put("p", jsonObject.toString());

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        asyncHttpClient.setTimeout(5 * 1000);
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    DebugUtil.e("getPayEncryptionSignature onSuccess:" + res);
                    String code = jsonObject.getString("code");
                    String data = jsonObject.getString("data");
                    String message = jsonObject.getString("message");
                    if (listener != null) {
                        if (!TextUtils.isEmpty(code) && code.equals("1")) {
                            listener.onSuccess(data);
                        } else {
                            listener.onFailure(message);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "storeExercise:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:get" + " requestUrl:" + requestUrl);
        asyncHttpClient.get(requestUrl, handler);
    }

    /**
     * 取消订单
     *
     * @param uid
     * @param orderid  订单号
     * @param listener
     */
    public static void cancleOrder(String uid, String orderid, String rid, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_cancleOrder;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("oid", orderid);
        jsonObject.addProperty("rid", rid);
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailure(ERROR_SERVER);
            }
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams params = new RequestParams();
        params.put("p", newParams);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String message = object.getString("message");
                    if (!TextUtils.isEmpty(code) && code.equals("1")) {
                        if (listener != null) {
                            listener.onSuccess(code);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(message);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        DebugUtil.i("SendRequest:" + jsonObject.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 积分兑换金币
     *
     * @param uid
     * @param listener
     */
    public static void exchangeGold(String uid, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_exchangeGold;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("source", source_android);
        jsonObject.addProperty("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailure(ERROR_SERVER);
            }
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams params = new RequestParams();
        params.put("p", newParams);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String message = object.getString("message");
                    if (!TextUtils.isEmpty(code) && code.equals("1")) {
                        if (listener != null) {
                            listener.onSuccess("兑换成功");
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(message);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        DebugUtil.i("SendRequest:" + jsonObject.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 搜索课程
     *
     * @param pageSize 页数
     * @param page     每页条数
     * @param key      关键字
     * @param listener
     */
    public static void getLiveSearchData(String userId, String page, String pageSize, String key, final ObtainDataFromNetListener<List<DirectBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_url_directList_search;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", userId);
        jsonObject.addProperty("pageSize", pageSize);
        jsonObject.addProperty("page", page);
        jsonObject.addProperty("key", key);
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        params.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        List<DirectBean> directBeanList = new Gson().fromJson(object.getString("data"), new TypeToken<List<DirectBean>>() {
                        }.getType());
                        if (listener != null) {
                            listener.onSuccess(directBeanList);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(object.getString("message"));
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getLiveData-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, params, handler);
    }

    /**
     * 搜索联想
     *
     * @param key      关键字
     * @param listener
     */
    public static void getSearchLenoveData(String userId, String key, final ObtainDataFromNetListener<String[], String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_url_directList_search_lenove;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", userId);
        jsonObject.addProperty("key", key);
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        params.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        String[] datas = new Gson().fromJson(object.getString("data"), String[].class);
                        if (listener != null) {
                            listener.onSuccess(datas);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(object.getString("message"));
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getLiveData-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, params, handler);
    }

    /**
     * 搜索推荐
     *
     * @param listener
     */
    public static void getSearchRecommendData(String userId, final ObtainDataFromNetListener<String[], String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

        String requestUrl = url_url_directList_search_recommend;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", userId);
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        params.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        String[] datas = new Gson().fromJson(object.getString("data"), String[].class);
                        if (listener != null) {
                            listener.onSuccess(datas);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(object.getString("message"));
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getLiveData-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        AsyncHttpClientHelper.createInstance().post(requestUrl, params, handler);
    }


    /**
     * 获得我的课程推送列表
     *
     * @param uid
     * @param listener
     */
    public static void getmycourse(String uid, String page, String pagesize, final ObtainDataFromNetListener<List<CouserBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_mycourse;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("index", page);
        params.put("limit", pagesize);
        params.put("license", "123");
        params.put("appmark", "app_js");
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1") || code.equals("0")) {
                        List<CouserBean> couserbean = new Gson().fromJson(object.getString("data"), new
                                TypeToken<List<CouserBean>>() {
                                }.getType());
                        if (listener != null) {
                            listener.onSuccess(couserbean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 智学课通知列表
     *
     * @param uid
     * @param page
     * @param pagesize
     * @param listener
     */
    public static void getmyWisdomClass(String uid, String page, String pagesize, final ObtainDataFromNetListener<List<CouserBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_mywisdomclass;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("index", page);
        params.put("limit", pagesize);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1") || code.equals("0")) {
                        List<CouserBean> couserbean = new Gson().fromJson(object.getString("data"), new
                                TypeToken<List<CouserBean>>() {
                                }.getType());
                        if (listener != null) {
                            listener.onSuccess(couserbean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 获得我的班级详情
     *
     * @param listener
     */
    public static void getClassDetails(String uid, String classId, final ObtainDataFromNetListener<ClassDetaliBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_Classdetails;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("classId", classId);
        params.put("license", "123");
        params.put("appmark", "app_js");
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        ClassDetaliBean detaliBean = new Gson().fromJson(object.getString("data"), ClassDetaliBean.class);
                        if (listener != null) {
                            listener.onSuccess(detaliBean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    public static void getWisdomClassDetails(String uid, String classId, final ObtainDataFromNetListener<ClassDetaliBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_WisdomClassdetails;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("classId", classId);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        ClassDetaliBean detaliBean = new Gson().fromJson(object.getString("data"), ClassDetaliBean.class);
                        if (listener != null) {
                            listener.onSuccess(detaliBean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 获得我的课程推送列表
     *
     * @param uid
     * @param listener
     */
    public static void getsubjectNotice(String uid, String classId, String index, String limit, final ObtainDataFromNetListener<List<NoticeBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_subjetNotice;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("classId", classId);
        params.put("index", index);
        params.put("limit", limit);
        params.put("license", "123");
        params.put("appmark", "app_js");
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        List<NoticeBean> noticebean = new Gson().fromJson(object.getString("data"), new
                                TypeToken<List<NoticeBean>>() {
                                }.getType());
                        if (listener != null) {
                            listener.onSuccess(noticebean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 获取开课详情信息
     *
     * @param noticeId
     * @param listener
     */
    public static void getarrangement(String noticeId, final ObtainDataFromNetListener<MsgBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_arrangement;
        RequestParams params = new RequestParams();
        params.put("noticeId", noticeId);
        params.put("license", "123");
        params.put("appmark", "app_js");
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    DebugUtil.e(object.toString());
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        MsgBean snoticebean = new Gson().fromJson(object.getString("data"), MsgBean.class);
                        if (listener != null) {
                            listener.onSuccess(snoticebean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 获取课程消息确认
     *
     * @param noticeId
     * @param listener
     */
    public static void getsnoticeok(String noticeId, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_Message_confirmation;
        RequestParams params = new RequestParams();
        params.put("noticeId", noticeId);
        params.put("license", "123");
        params.put("appmark", "app_js");
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        if (listener != null) {
                            listener.onSuccess(code);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 标记已读课程
     *
     * @param
     * @param listener
     */
    public static void gethasReadClassInfo(String uid, String classId, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_hasReadClassInfo;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("classId", classId);
        params.put("license", "123");
        params.put("appmark", "app_js");
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        if (listener != null) {
                            listener.onSuccess(code);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 通知栏跳转获取开课详情信息
     *
     * @param messageId
     * @param listener
     */
    public static void getNotificationbar(String messageId, String uid, final ObtainDataFromNetListener<MsgBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_Notificationbar;
        RequestParams params = new RequestParams();
        params.put("messageId", messageId);
        params.put("uid", uid);
        params.put("license", "123");
        params.put("appmark", "app_js");
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        MsgBean snoticebean = new Gson().fromJson(object.getString("data"), MsgBean.class);
//                        List<MsgBean> snoticebean = new Gson().fromJson(object.getString("data"), new
//                                TypeToken<List<MsgBean>>() {}.getType());
                        if (listener != null) {
                            listener.onSuccess(snoticebean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 签到日历详情接口
     *
     * @param
     */
    public static void getCalendardetails(String uid, String year, String month, final ObtainDataFromNetListener<NCakenBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_Calendardetails;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("year", year);
        params.put("month", month);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    NCakenBean cakendbean = new Gson().fromJson(res, NCakenBean.class);
                    if (cakendbean.getCode().equals("1")) {
                        if (listener != null) {
                            listener.onSuccess(cakendbean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * H5
     * 签到做题记录分享确认接口
     *
     * @param
     */
    public static void getshareConfirm(String uid, String year, String month, String day, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_Confirmshare;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("year", year);
        jsonObject.addProperty("month", month);
        jsonObject.addProperty("day", day);
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (listener != null) {
                        listener.onSuccess(code);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        asyncHttpClient.get(requestUrl, requestParams, handler);
    }

    /**
     * 记录首次签到接口
     *
     * @param
     */
    public static void getfirstsign(String uid, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_firstsign;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (listener != null) {
                        listener.onSuccess(code);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }


    /**
     * 测试反馈新接口
     * contentm -----内容
     * phonemodel -----手机型号
     * pic -----用户图像
     * system -----系统版本
     * 注意：用了xutils Http上传文件 AsyncHttpClient上传失败
     */
    public static void NewsubmitFeedBack(String phonemodel, String system, String content, String pic1, String pic2, String pic3, final ObtainDataFromNetListener<String, String>
            listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            if (null != listener) {
                listener.onFailure(ERROR_NETWORK);
            }
            return;
        }
        String requestUrl = url_submitFeedBackv2;

        PostRequest<String> params = OkGo.<String>post(requestUrl)
                .isMultipart(true)
                .params("uid", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null))
                .params("content", content)
                .params("phonemodel", phonemodel)
                .params("system", system);

        if (!StringUtils.isEmpty(pic1)) {
            params.params("pic1", new File(pic1));
        }

        if (!StringUtils.isEmpty(pic2)) {
            params.params("pic2", new File(pic2));
        }

        if (!StringUtils.isEmpty(pic3)) {
            params.params("pic3", new File(pic3));
        }

        params.execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    // 1：成功/0：失败/-2：缺少参数/-3：没有可更新的数据
                    JSONObject object = new JSONObject(response.body());
                    String code = object.getString("code");
                    String msg = object.getString("message");
                    if ("1".equals(code)) {
                        listener.onSuccess("反馈成功！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }


            @Override
            public void uploadProgress(Progress progress) {
                Logger.e("progress:" + progress.filePath + progress.fraction);
            }

            @Override
            public void onError(Response<String> response) {
                if (listener != null) {
                    listener.onFailure(SendRequest.ERROR_SERVER);
                }
            }
        });


//        com.lidroid.xutils.HttpUtils httpUtils = new com.lidroid.xutils.HttpUtils(10000);
//        // 上传文件到服务器
//        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
//        params.addBodyParameter("uid", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null));
//        params.addBodyParameter("content", content);
//        params.addBodyParameter("phonemodel", phonemodel);
//        params.addBodyParameter("system", system);
//        File faceFile = new File(pic1);
//        if (faceFile.exists()) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(pic1, options);
//            String type1 = options.outMimeType;
//            params.addBodyParameter("pic1", faceFile, type1);
//        }
//        File faceFile2 = new File(pic2);
//        if (faceFile2.exists()) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(pic2, options);
//            String type2 = options.outMimeType;
//            params.addBodyParameter("pic2", faceFile2, type2);
//        }
//        File faceFile3 = new File(pic3);
//        if (faceFile3.exists()) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(pic3, options);
//            String type3 = options.outMimeType;
//            params.addBodyParameter("pic3", faceFile3, type3);
//        }
//        httpUtils.send(HttpMethod.POST, requestUrl, params, new RequestCallBack<String>() {
//            @Override
//            public void onSuccess(ResponseInfo<String> responseInfo) {
//                DebugUtil.e("xutils:" + responseInfo.result);
//                try {
//                    // 1：成功/0：失败/-2：缺少参数/-3：没有可更新的数据
//                    JSONObject object = new JSONObject(responseInfo.result);
//                    String code = object.getString("code");
//                    String msg = object.getString("message");
//                    if ("1".equals(code)) {
//                        listener.onSuccess("反馈成功！");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    if (listener != null) {
//                        listener.onFailure(ERROR_SERVER);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(com.lidroid.xutils.exception.HttpException error, String s) {
//                String msg = "saveInformation:" + s;
//                if (error != null) {
//                    msg += error.getMessage();
//                }
//                DebugUtil.i(TAG, msg);
//                if (listener != null) {
//                    listener.onFailure(ERROR_SERVER);
//                }
//            }
//        });
    }

    /**
     * 获得我的反馈列表
     *
     * @param uid
     * @param listener
     */
    public static void getFeedbackList(String uid, String page, String pagesize, final ObtainDataFromNetListener<List<FeedListBean.DataEntity>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_Feedbacklist;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("page", page);
        params.put("pagesize", pagesize);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1") || code.equals("0")) {
                        List<FeedListBean.DataEntity> noticebean = new Gson().fromJson(object.getString("data"), new
                                TypeToken<List<FeedListBean.DataEntity>>() {
                                }.getType());
                        if (listener != null) {
                            listener.onSuccess(noticebean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }


    /**
     * 获得我的反馈详情
     *
     * @param feedback_id
     * @param listener
     */
    public static void getFeeddetail(String feedback_id, final ObtainDataFromNetListener<DeedDetailiBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_Feedbackdetails;
        RequestParams params = new RequestParams();
        params.put("feedback_id", feedback_id);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        DeedDetailiBean feedDetailiBean = LoganSquare.parse(res, DeedDetailiBean.class);
                        if (listener != null) {
                            listener.onSuccess(feedDetailiBean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 获得我的推荐课程
     *
     * @param province 城市编码
     * @param listener
     */
    public static void getRecommend(String province, final ObtainDataFromNetListener<PersonalInfoBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_Recommend;
        RequestParams params = new RequestParams();
        params.put("province", province);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String data = object.getString("data");
                    if (code.equals("1")) {
                        PersonalInfoBean personalInfoBean = LoganSquare.parse(data, PersonalInfoBean.class);
                        if (listener != null) {
                            listener.onSuccess(personalInfoBean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 留言提交接口
     *
     * @param noticeId
     * @param listener
     */
    public static void getAddmasg(String uid, String noticeId, String messageId, String content, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_addmesg;
        RequestParams params = new RequestParams();
        params.put("messageId", messageId);
        params.put("noticeId", noticeId);
        params.put("uid", uid);
        params.put("content", content);
        params.put("license", "123");
        params.put("appmark", "app_js");
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String message = object.getString("message");
                    if (code.equals("1")) {
                        if (listener != null) {
                            listener.onSuccess(message);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }


    /**
     * 购课须知
     *
     * @param
     * @param
     */
    public static void getContext(final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = lesson_notes;
        RequestParams params = new RequestParams();
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String data = object.getString("data");
                    if (code.equals("1")) {
                        if (listener != null) {
                            listener.onSuccess(data);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 购课选择学段
     *
     * @param
     * @param
     */
    public static void getSelectAcademic(String rid, final ObtainDataFromNetListener<SelectAcademicBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_selectAcademic;
        RequestParams params = new RequestParams();
        params.put("rid", rid);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String data = object.getString("data");
                    if (code.equals("1")) {
                        if (listener != null) {
                            SelectAcademicBean selectAcademicBean = new Gson().fromJson(data, SelectAcademicBean.class);
                            listener.onSuccess(selectAcademicBean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 获取 真题模拟和在线模考试卷 错题或者收藏
     *
     * @param mode     真题:zhenti,模拟:moni
     * @param type     错题：error，收藏：collect
     * @param listener
     */
    public static void getPaperWrongOrColectionQuestions(String mode, String type, final ObtainDataFromNetListener<List<PaperItem>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }

//        String requestUrl = url_get_wrongques;
        String requestUrl = ipForExercise + "httb/httbapi/paperjs/getPaperRecordList?license=123&appmark=app_js&type=" + type + "&mode=" + mode;
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sendRequestUtilsForExercise.subject);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sendRequestUtilsForExercise.userId);
        jsonObject.addProperty("deviceId", sendRequestUtilsForExercise.deviceId);
        jsonObject.addProperty("examType", sendRequestUtilsForExercise.examType);
        jsonObject.addProperty("stage", sendRequestUtilsForExercise.stage);
        jsonObject.addProperty("area", sendRequestUtilsForExercise.area);
        jsonObject.add("examSubject", jsonArray);

        RequestParams params = new RequestParams();
        params.put("userInfo", jsonObject.toString());

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {

            private List<PaperItem> categoryBean;

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    String gRes = FileUtils.gunzip(res);
                    categoryBean = LoganSquare.parseList(gRes, PaperItem.class);
                    if (listener != null) {
                        listener.onSuccess(categoryBean);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getWrongQuestions:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }

                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }


    /**
     * 直播课通知详情
     *
     * @param
     * @param
     */
    public static void getLivebroadcast(String rid, final ObtainDataFromNetListener<DirecourBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_class_Livebroadcast;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rid", rid);
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        if (listener != null) {
                            DirecourBean DirecourBean = new Gson().fromJson(res, DirecourBean.class);
                            listener.onSuccess(DirecourBean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }


    /**
     * 直播课-课程信息
     *
     * @param
     * @param
     */
    public static void getcourse_information(String rid, final ObtainDataFromNetListener<InformaBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_class_Livedata;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rid", rid);
        RequestParams params = new RequestParams();
        params.put("p", jsonObject.toString());
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        if (listener != null) {
                            InformaBean DirecourBean = new Gson().fromJson(res, InformaBean.class);
                            listener.onSuccess(DirecourBean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }


    /**
     * vip 老师学生留言列表
     *
     * @param
     * @param
     */
    public static void getTeacherMessageList(String uid, String index, String limit, final ObtainDataFromNetListener<LeaveBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_vip_rMessageList;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("index", index);
        params.put("limit", limit);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        if (listener != null) {
                            LeaveBean DirecourBean = new Gson().fromJson(res, LeaveBean.class);
                            listener.onSuccess(DirecourBean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * vip 老师学生留言详情
     *
     * @param
     * @param
     */
    public static void getleavedetail(String uid, String teacherId, final ObtainDataFromNetListener<LeaTeacherBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_vip_leavedetail;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("teacherId", teacherId);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        if (listener != null) {
                            LeaTeacherBean DirecourBean = new Gson().fromJson(res, LeaTeacherBean.class);
                            listener.onSuccess(DirecourBean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }


    /**
     * vip学生添加留言
     *
     * @param
     * @param
     */
    public static void getaddstudent(String uid, String teacherId, String content, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_vip_addstudent;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("teacherId", teacherId);
        params.put("content", content);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        if (listener != null) {
                            listener.onSuccess("1");
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }


    /**
     * vip 我的资料列表
     *
     * @param
     * @param
     */
    public static void getMyVipDataList(String uid, String index, String limit, final ObtainDataFromNetListener<VipDataBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_vip_mydata;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("index", index);
        params.put("limit", limit);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1") || code.equals("0")) {
                        if (listener != null) {
                            VipDataBean DirecourBean = new Gson().fromJson(res, VipDataBean.class);
                            listener.onSuccess(DirecourBean);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * vip 我的资料列表
     *
     * @param
     * @param
     */
    public static void getipFabulous(String uid, String materialId, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_vip_Fabulous;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("materialId", materialId);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (code.equals("1")) {
                        if (listener != null) {
                            listener.onSuccess("点赞成功！");
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * vip 我的资料ppt链接
     *
     * @param
     * @param
     */
    public static void getVipDatappt(String uid, String materialId, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_vip_DataPPt;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("materialId", materialId);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {

            private String url;
            private JSONObject data;

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    data = object.getJSONObject("data");
                    url = data.getString("filePath");
                    if (code.equals("1")) {
                        if (listener != null) {
                            listener.onSuccess(url);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }


    /**
     * 用户信息拆分
     *
     * @param uid
     * @param listener
     */
    public static void getPersonalInfoV2(String uid, String width, String height, final ObtainDataFromNetListener<PersonalInfoBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
//        String requestUrl = url_personalInfoV3 + "?uid=" + uid + "&token=" + CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, "");
        String requestUrl = url_personalInfoV3;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("width", width);
        params.put("height", height);
        params.put("token", CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCESSToken, ""));
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//成功
                        String data = object.getString("data");
                        PersonalInfoBean personalInfoBean = LoganSquare.parse(data, PersonalInfoBean.class);
                        if (listener != null) {
                            listener.onSuccess(personalInfoBean);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "sendMain:" + statusCode;//
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * VIP题库列表
     *
     * @param uid
     * @param index    页数
     * @param limit    每页多少条数据
     * @param listener
     */
    public static void getQuestionExclusiveList(String uid, int index, int limit, final ObtainDataFromNetListener<List<VipPaperBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_getquestion_exclusivelist;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("index", index);
        params.put("limit", limit);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        List<VipPaperBean> vipPaperBeanlist = new Gson().fromJson(object.getString("data"), new TypeToken<List<VipPaperBean>>() {
                        }.getType());
                        if (listener != null) {
                            listener.onSuccess(vipPaperBeanlist);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getQuestionExclusiveList-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * VIP题库列表
     *
     * @param exclusiveId 试卷id
     * @param listener
     */
    public static void getVipQuestionList(String exclusiveId, final ObtainDataFromNetListener<VipQuestionBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_get_vip_questionlist;
        RequestParams params = new RequestParams();
        params.put("exclusiveId", exclusiveId);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        VipQuestionBean vipQuestionBean = new Gson().fromJson(object.getString("data"), VipQuestionBean.class);
                        if (listener != null) {
                            listener.onSuccess(vipQuestionBean);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getQuestionExclusiveList-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * VIP 试题 交卷
     *
     * @param uid               用户id
     * @param exclusiveId       试卷id
     * @param createUserId      试卷老师id
     * @param actualTime        用时
     * @param paperHandBeanList 交卷的答案list
     * @param listener
     */
    public static void handVipExclusive(String uid, String exclusiveId, int createUserId, int actualTime, List<PaperHandBean> paperHandBeanList, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_get_vip_hand_exclusive;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("exclusiveId", exclusiveId);
        params.put("createUserId", createUserId);
        params.put("actualTime", actualTime);
        Gson mGson = new Gson();
        String records = mGson.toJson(paperHandBeanList);
        params.put("records", records);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        asyncHttpClient.setTimeout(10 * 1000);
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        if (listener != null) {
                            listener.onSuccess(code);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getQuestionExclusiveList-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * VIP 试题 交卷
     *
     * @param uid         用户id
     * @param exclusiveId 试卷id
     * @param questionId  题目id
     * @param childId     子题的id
     * @param listener
     */
    public static void delVipQuestionError(String uid, String exclusiveId, String questionId, String childId, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_del_question_error;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("exclusiveId", exclusiveId);
        params.put("questionId", questionId);
        params.put("childId", childId);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        if (listener != null) {
                            listener.onSuccess(code);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getQuestionExclusiveList-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 删除试卷
     *
     * @param uid      用户id
     * @param pid      试卷id
     * @param type     试卷类型（error:错题，collect：收藏）
     * @param listener
     */
    public static void delDownPaper(String uid, String pid, String type, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_del_down_paper;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("pid", pid);
        params.put("type", type);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
//                    String code = object.getString("code");
                    String msg = object.getString("message");
                    if ("删除用户试题成功".equals(msg)) {//有数据
                        if (listener != null) {
                            listener.onSuccess(msg);
                        }
                    }
//                    else if ("0".equals(code)) {//没有数据
//                        if (listener != null) {
//                            listener.onSuccess(null);
//                        }h'h
//                    } else if ("-1".equals(code)) {//参数为空
//                        if (listener != null) {
//                            listener.onFailure(ERROR_SERVER);
//                        }
//                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "delDownPaper-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "&" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * VIP 试题 获取试题报告
     *
     * @param uid         用户id
     * @param exclusiveId 试卷id
     * @param listener
     */
    public static void getEvaluationReport(String uid, String exclusiveId, final ObtainDataFromNetListener<PaperAnalysiBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_get_evaluation_report;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("exclusiveId", exclusiveId);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        if (listener != null) {
                            Gson mGson = new Gson();
                            PaperAnalysiBean data = mGson.fromJson(object.getString("data"), PaperAnalysiBean.class);
                            listener.onSuccess(data);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getEvaluationReport-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 模块题海 在线模考 真题演练 分享结果
     *
     * @param num      超过多少人
     * @param rank     排名
     * @param score    分数
     * @param accuracy 正确率，不要%
     * @param listener
     */
    public static void getShareResultUrl(String num, String rank, String score, String accuracy, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_add_share_result;
        RequestParams params = new RequestParams();
        params.put("num", num);
        params.put("rank", rank);
        params.put("score", score);
        params.put("accuracy", accuracy);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        if (listener != null) {
                            listener.onSuccess(object.getString("data"));
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getShareResultUrl-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "&" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * VIP题库错题列表
     *
     * @param uid
     * @param listener
     */
    public static void getErrorQuestionExclusiveList(String uid, String exclusiveId, final ObtainDataFromNetListener<VipQuestionBean, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_get_question_error_list;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("exclusiveId", exclusiveId);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        VipQuestionBean vipQuestionBean = new Gson().fromJson(object.getString("data"), VipQuestionBean.class);
                        if (listener != null) {
                            listener.onSuccess(vipQuestionBean);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getQuestionExclusiveList-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * VIP题库错题列表
     *
     * @param uid
     * @param index    页数
     * @param limit    每页多少条数据
     * @param listener
     */
    public static void getExclusiveErrorList(String uid, int index, int limit, final ObtainDataFromNetListener<List<VipPaperBean>, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_get_exclusive_errorList;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("index", index);
        params.put("limit", limit);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        List<VipPaperBean> vipPaperBeanlist = new Gson().fromJson(object.getString("data"), new TypeToken<List<VipPaperBean>>() {
                        }.getType());
                        if (listener != null) {
                            listener.onSuccess(vipPaperBeanlist);
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getQuestionExclusiveList-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }


    /**
     * VIP留言未读
     *
     * @param uid
     */
    public static void getMessageMark(String uid, final ObtainDataFromNetListener<Integer, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_vipMessageMark;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    JSONObject jsonObject = object.getJSONObject("data");
                    int messageMark = jsonObject.getInt("messageMark");
                    if ("1".equals(code)) {//有数据
                        if (listener != null) {
                            listener.onSuccess(messageMark);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getQuestionExclusiveList-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.get(requestUrl, params, handler);
    }

    /**
     * 模块题海 在线模考 真题演练 分享结果
     *
     * @param uid         用户id
     * @param exclusiveId 试卷id
     * @param defeatNum   超过多少人
     * @param ranking     排名
     * @param difficulty  难易度
     * @param correctRate 正确率，不要%
     * @param listener
     */
    public static void getVipShareResultUrl(String uid, String exclusiveId, String defeatNum, String ranking, String difficulty, String correctRate, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_vip_get_share_result;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("exclusiveId", exclusiveId);
        params.put("defeatNum", defeatNum);
        params.put("ranking", ranking);
        params.put("difficulty", difficulty);
        params.put("correctRate", correctRate);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);//成功后先调用父类的统一处理方法
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if ("1".equals(code)) {//有数据
                        if (listener != null) {
                            listener.onSuccess(object.getString("data"));
                            DebugUtil.e("getVipShareResultUrl:" + object.getString("data"));
                        }
                    } else if ("0".equals(code)) {//没有数据
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else if ("-1".equals(code)) {//参数为空
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getVipShareResultUrl-failure:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl + "&" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 记录首次签到接口
     *
     * @param
     */
    public static void putEquipmentInformation(Context context, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        @SuppressLint("MissingPermission") String Imei = ((TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE)).getDeviceId();
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String requestUrl = url_EquipmentInformation + "imei=" + Imei + "&androidid=" + android_id + "&os=0" + "&source=360shoujizhushou";
        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                DebugUtil.e("SendRequest putEquipmentInformation" + res);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    if (listener != null) {
                        listener.onSuccess(code);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "putEquipmentInformation:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest:" + " requestUrl:" + requestUrl);
        asyncHttpClient.get(requestUrl, handler);
    }

    /**
     * 提交打分
     *
     * @param uid      用户id
     * @param lessonid 课程id
     * @param source   教师分数
     * @param listener
     * @message 评价
     */
    public static void submitComments(String uid, String lessonid, float source, String message, final
    ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_submit_comments;
        RequestParams params = new RequestParams();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("lessonid", lessonid);
        jsonObject.addProperty("comment", message);
        jsonObject.addProperty("source", (int) source);
        params.put("p", jsonObject.toString());

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String message = object.getString("message");
                    if (code.equals("1")) {
                        if (listener != null) {
                            listener.onSuccess(message);
                        }
                    } else if (!StringUtils.isEmpty(message)) {
                        if (listener != null) {
                            listener.onFailure(message);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "submitComments:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest post:" + " requestUrl:" + requestUrl + "?" + params.toString());
        asyncHttpClient.post(requestUrl, params, handler);
    }

    /**
     * 提现
     *
     * @param uid         用户id
     * @param netclassId  课程id
     * @param lessonid    课件id
     * @param hasfee      返现金额
     * @param ftype       支付类型，网站微信支付为1，APP微信支付为2，手动输入账号为0
     * @param userAccount 提现账号
     * @param realName    真实姓名
     * @param listener
     */
    public static void getWithdrawals(String uid, String orderId, String netclassId, String lessonid, String hasfee, String ftype, String userAccount, String realName,
                                      String tittle, String classTittle, final ObtainDataFromNetListener<String, String> listener) {
        if (listener != null) {
            listener.onStart();
        }
        if (!CommonUtils.isNetWorkAvilable()) {
            listener.onFailure(ERROR_NETWORK);
            return;
        }
        String requestUrl = url_withdrawals;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("UserId", uid);
        jsonObject.addProperty("OrderId", orderId);
        jsonObject.addProperty("NetclassId", netclassId);
        jsonObject.addProperty("Lessonid", lessonid);
        jsonObject.addProperty("hasfee", hasfee);
        jsonObject.addProperty("ftype", ftype);
        jsonObject.addProperty("UserAccount", userAccount);
        jsonObject.addProperty("RealName", realName);
        jsonObject.addProperty("netclassTitle", tittle);
        jsonObject.addProperty("lessonTitle", classTittle);
        jsonObject.addProperty("source", source_android);

        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        RequestParams requestParams = new RequestParams();
        requestParams.put("p", newParams);

        AsyncHttpClient asyncHttpClient = AsyncHttpClientHelper.createInstance();
        AsyncHttpResponseSignHandler handler = new AsyncHttpResponseSignHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                Logger.e("withDraw:" + res);
                try {
                    JSONObject object = new JSONObject(res);
                    String code = object.getString("code");
                    String message = object.getString("message");
                    if (code.equals("1")) {
                        if (listener != null) {
                            listener.onSuccess(message);
                        }
                    } else if (!StringUtils.isEmpty(message)) {
                        if (listener != null) {
                            listener.onFailure(message);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(ERROR_SERVER);
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(ERROR_SERVER);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String msg = "getWithdrawals:" + statusCode;
                if (error != null) {
                    msg += error.getMessage();
                }
                DebugUtil.i(TAG, msg);
                if (listener != null) {
                    listener.onFailure(ERROR_SERVER);
                }
            }
        };
        handler.setUsePoolThread(true);
        DebugUtil.i("SendRequest post:" + " requestUrl:" + requestUrl + "?" + requestParams.toString());
        asyncHttpClient.post(requestUrl, requestParams, handler);
    }


}
