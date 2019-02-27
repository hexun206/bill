package com.huatu.teacheronline.engine;

import com.huatu.teacheronline.utils.GsonUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.HashMap;

/**
 * Created by kinndann on 2018/11/5.
 * description:百家云相关接口
 */
public class BJ_CloudApi {

    private final static String BASE_URL = SendRequest.url_user;


    private final static String DELETE_VIDEO = BASE_URL + "face/delete?video_id=";

    private final static String GET_UPLOAD_FROM_BREAK_URL = BASE_URL + "face/getResumeUploadUrl?video_id=";

    private final static String GET_TRANSCODE_STATUS = BASE_URL + "face/getCode";

    private final static String GET_UPLOAD_URL = BASE_URL + "face/upload";

    private final static String GET_IMAGE = BASE_URL + "face/getImage?video_id=";


    /**
     * 获取百家云上传地址
     *
     * @param file_name
     * @param definition
     * @param l
     */
    public static void getVideoUploadUrl(String file_name, int definition, ObtainDataFromNetListener<String, Throwable> l) {

        HashMap<String, String> map = new HashMap<>();
        map.put("file_name", file_name);
        map.put("definition", definition + "");

        String getUploadUrl = GET_UPLOAD_URL + "?p=" + GsonUtils.toJson(map);
        OkGo.<String>get(getUploadUrl).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                l.onSuccess(response.body());
            }

            @Override
            public void onError(Response<String> response) {
                l.onFailure(response.getException());
            }
        });


    }

    /**
     * 获取视频封面
     *
     * @param videoId
     * @param l
     */
    public static void getVideoImage(String videoId, ObtainDataFromNetListener<String, Throwable> l) {


        String getUploadUrl = GET_IMAGE + videoId;
        OkGo.<String>get(getUploadUrl).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                l.onSuccess(response.body());
            }

            @Override
            public void onError(Response<String> response) {
                l.onFailure(response.getException());
            }
        });


    }

    /**
     * 获取百家云断点续传地址
     *
     * @param videoId
     * @param l
     */
    public static void getVideoUploadFromBreakUrl(String videoId, ObtainDataFromNetListener<String, Throwable> l) {

        OkGo.<String>get(GET_UPLOAD_FROM_BREAK_URL + videoId).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                l.onSuccess(response.body());
            }

            @Override
            public void onError(Response<String> response) {
                l.onFailure(response.getException());
            }
        });
    }

    /**
     * 根据百家云id删除视频
     *
     * @param videoId
     * @param l
     */

    public static void deleteVideoById(String videoId, ObtainDataFromNetListener<String, Throwable> l) {

        OkGo.<String>get(DELETE_VIDEO + videoId).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                l.onSuccess(response.body());
            }

            @Override
            public void onError(Response<String> response) {
                l.onFailure(response.getException());
            }
        });


    }
}
