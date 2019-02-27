package com.huatu.teacheronline.engine;

import com.huatu.teacheronline.personal.bean.InteviewVideoInfoUploadRequest;
import com.huatu.teacheronline.utils.MD5;
import com.huatu.teacheronline.utils.StringUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;

/**
 * Created by kinndann on 2018/10/30.
 * description:教师网接口
 */
public class HTeacherApi {

    private final static String key = "Se$axWGzK#QwpeYIPFj1f#ah%zBV3vYWDCyauMatk!2vxEiRMvcm61g#p4tB%8qb";

    private final static String base_url = "http://mobileapp1.hteacher.net/";//正式线
//    private final static String base_url = "http://mobileapp2.hteacher.net/";//测试线

    /**
     * 获取学段学科教材版本以及结构化试题接口
     * post
     */
    private final static String get_param_info = base_url + "api/videocomment/get_messagelist.php";


    /**
     * 学员单个点评课程提交订单的详细接口
     * post
     */
    private final static String get_comments_detail = base_url + "api/videocomment/get_commentlist.php";
    /**
     * 修改点评记录接口
     * post
     */
    private final static String update_comments_detail = base_url + "api/videocomment/update_comment_order.php";
    /**
     * 获取结构化试题接口
     * post
     */
    private final static String get_question_detail = base_url + "api/videocomment/get_questions.php";

    /**
     * 获取某学员的点评课程列表接口
     * post
     */
    private final static String get_comments_list = base_url + "api/videocomment/get_userclasslist.php";

    /**
     * 提交用户面试视频记录
     * post
     */
    private final static String post_comments = base_url + "api/videocomment/add_comment_order.php";

    /**
     * 获取用户是否存在未阅读点评
     */
    private final static String get_read_status_by_uid = base_url + "api/videocomment/get_unreadclass.php";


    /**
     * 面试点评范例前台显示接口
     * post
     */
    private final static String get_example_list = base_url + "api/videocomment/get_examplelist.php";
    /**
     * 标记订单已读
     */
    private final static String post_read_in_status = base_url + "api/videocomment/update_readstatus_order.php";


    /**
     * 获取签名
     * md5(md5($userid.$secret))
     *
     * @param uid 用户id
     * @return
     */
    private static String getSign(String uid) {
        if (StringUtils.isEmpty(uid)) {
            return "";
        }

        String sign = MD5.digest(MD5.digest(uid + key));


        return sign;
    }


    /**
     * 获取可上传视频课程列表
     *
     * @param uid
     * @param listener
     */
    public static void getCommentList(String uid, final ObtainDataFromNetListener<String, Throwable> listener) {
        if (listener != null) {
            listener.onStart();
        }

        OkGo.<String>post(get_comments_list)
                .isMultipart(true)
                .tag(HTeacherApi.class)
                .params("userid", uid)
                .params("sign", getSign(uid))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (listener != null) {
                            listener.onSuccess(response.body());

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                        if (listener != null) {
                            listener.onFailure(response.getException());

                        }
                    }
                });
    }

    public static void postReadStatus(String uid, String orderId, final ObtainDataFromNetListener<String, Throwable> listener) {
        if (listener != null) {
            listener.onStart();
        }

        OkGo.<String>post(post_read_in_status)
                .isMultipart(true)
                .tag(HTeacherApi.class)
                .params("userid", uid)
                .params("id", orderId)
                .params("sign", getSign(uid))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (listener != null) {
                            listener.onSuccess(response.body());

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                        if (listener != null) {
                            listener.onFailure(response.getException());

                        }
                    }
                });
    }


    /**
     * 获取上传视频时所需的参数
     *
     * @param uid
     * @param listener
     */
    public static void getParamInfo(String uid, final ObtainDataFromNetListener<String, Throwable> listener) {
        if (listener != null) {
            listener.onStart();
        }

        OkGo.<String>post(get_param_info)
                .isMultipart(true)
                .tag(HTeacherApi.class)
                .params("userid", uid)
                .params("sign", getSign(uid))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (listener != null) {
                            listener.onSuccess(response.body());

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                        if (listener != null) {
                            listener.onFailure(response.getException());

                        }
                    }
                });


    }

    /**
     * 获取结构化试题
     *
     * @param uid
     * @param listener
     */
    public static void getQuestionInfo(String uid, final ObtainDataFromNetListener<String, Throwable> listener) {
        if (listener != null) {
            listener.onStart();
        }

        OkGo.<String>post(get_question_detail)
                .isMultipart(true)
                .tag(HTeacherApi.class)
                .params("userid", uid)
                .params("sign", getSign(uid))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (listener != null) {
                            listener.onSuccess(response.body());

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                        if (listener != null) {
                            listener.onFailure(response.getException());

                        }
                    }
                });


    }


    /**
     * 获取用户是否存在未阅读点评
     *
     * @param uid
     * @param listener
     */
    public static void getReadStatusByUid(String uid, final ObtainDataFromNetListener<String, Throwable> listener) {
        if (listener != null) {
            listener.onStart();
        }

        OkGo.<String>post(get_read_status_by_uid)
                .isMultipart(true)
                .tag(HTeacherApi.class)
                .params("userid", uid)
                .params("sign", getSign(uid))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (listener != null) {
                            listener.onSuccess(response.body());

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                        if (listener != null) {
                            listener.onFailure(response.getException());

                        }
                    }
                });


    }

    /**
     * 获取点评详情
     *
     * @param uid
     * @param netclassid
     * @param listener
     */
    public static void getCommentDetail(String uid, String netclassid, final ObtainDataFromNetListener<String, Throwable> listener) {
        if (listener != null) {
            listener.onStart();
        }

        OkGo.<String>post(get_comments_detail)
                .isMultipart(true)
                .tag(HTeacherApi.class)
                .params("userid", uid)
                .params("netclassid", netclassid)
                .params("sign", getSign(uid))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (listener != null) {
                            listener.onSuccess(response.body());

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                        if (listener != null) {
                            listener.onFailure(response.getException());

                        }
                    }
                });


    }


//    userid	string	是	用户id
//    netclassid	string	是	点评课程id
//    classtitle	string	是	课文标题(例如：测试标题1)
//    classphase	string	是	学段(例如：幼儿)
//    classsubject	string	是	学科(例如：语文)
//    versions	string	是	教材版本(例如：人教版)
//    questions	string	是	结构化题目(序号拼接即可，例如1,2)
//    bjyvideoid	string	是	点评视频bjyvideoid
//    sign	string	是	//签名 md5(md5($userid.$secret))

    /**
     * 提交面试视频信息
     *
     * @param info
     * @param listener
     */
//    public static void postVideoInfo(InteviewVideoInfoUploadRequest info, final ObtainDataFromNetListener<String, Throwable> listener) {
//        if (listener != null) {
//            listener.onStart();
//        }
//
//        OkGo.<String>post(post_comments)
//                .isMultipart(true)
//                .tag(HTeacherApi.class)
//                .params("userid", info.getUserid())
//                .params("netclassid", info.getNetclassid())
//                .params("classtitle", info.getClasstitle())
//                .params("classphase", info.getClassphase())
//                .params("classsubject", info.getClasssubject())
//                .params("versions", info.getVersions())
//                .params("questions", info.getQuestions())
//                .params("bjyvideoid", String.valueOf(info.getBjyvideoid()))
//                .params("sign", getSign(info.getUserid()))
//                .params("ostatus", info.getOstatus())
//
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        if (listener != null) {
//                            listener.onSuccess(response.body());
//
//                        }
//                    }
//
//                    @Override
//                    public void onError(Response<String> response) {
//
//                        if (listener != null) {
//                            listener.onFailure(response.getException());
//
//                        }
//                    }
//                });
//
//
//    }
    public static void updateVideoInfo(InteviewVideoInfoUploadRequest info, final ObtainDataFromNetListener<String, Throwable> listener) {
        if (listener != null) {
            listener.onStart();
        }

        String url = info.getOrderId() == null ? post_comments : update_comments_detail;
        PostRequest<String> request = OkGo.<String>post(url)
                .isMultipart(true)
                .tag(HTeacherApi.class);
        if (info.getOrderId() != null) {
            request.params("id", info.getOrderId());
        }

        request.params("userid", info.getUserid())
                .params("netclassid", info.getNetclassid())
                .params("classtitle", info.getClasstitle())
                .params("classphase", info.getClassphase())
                .params("classsubject", info.getClasssubject())
                .params("versions", info.getVersions())
                .params("questions", info.getQuestions())
                .params("bjyvideoid", String.valueOf(info.getBjyvideoid()))
                .params("ostatus", info.getOstatus())
                .params("agreedisplay", info.getAgreedisplay())

                .params("sign", getSign(info.getUserid()))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (listener != null) {
                            listener.onSuccess(response.body());

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                        if (listener != null) {
                            listener.onFailure(response.getException());

                        }
                    }
                });


    }


    public static void cancel() {
        OkGo.getInstance().cancelTag(HTeacherApi.class);

    }


}
