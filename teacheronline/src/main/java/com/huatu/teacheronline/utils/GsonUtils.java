package com.huatu.teacheronline.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * json解析工具类
 *
 * @author kinndann
 */
public final class GsonUtils {

    private static Gson sGson;

    /**
     * json数据格式必须是["asdada","asda"...]
     *
     * @param jsonArray
     * @return
     */
    public static List<String> getStringArray(String jsonArray) {
        initGson();
        String[] arrays = sGson.fromJson(jsonArray, String[].class);
        return Arrays.asList(arrays);

    }

    private static void initGson() {
        if (sGson == null) {
            sGson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
//                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
        }
    }


    public static String getJson(String str, String name) {
        JSONObject jsonObject;
        String json = "服务器异常";
        try {
            jsonObject = new JSONObject(str);
            json = jsonObject.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return json;

    }

    public static int getIntJson(String str, String name) {
        if (StringUtils.isEmpty(str)) {
            return -1;
        }
        JSONObject jsonObject;
        int json = -1;
        try {
            jsonObject = new JSONObject(str);
            json = jsonObject.getInt(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;

    }

    /**
     * 解析JsonObject
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T parseJSON(String json, Class<T> clazz) {
        initGson();
        T info = null;
        try {
            info = sGson.fromJson(json, clazz);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return info;
    }


    /**
     * 解析JsonArray Type type = new
     * TypeToken&lt;ArrayList&lt;TypeInfo>>(){}.getType(); <br>
     * Type所在的包：java.lang.reflect <br>
     * TypeToken所在的包：com.google.gson.reflect.TypeToken
     *
     * @param jsonArr
     * @param type
     * @return
     */
    public static <T> T parseJSONArray(String jsonArr, Type type) {

        initGson();
        T infos = null;
        try {
            infos = sGson.fromJson(jsonArr, type);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }


        return infos;
    }

    public static <T> T parseJSONArrayNoAnnotation(String jsonArr, Type type) {
        T infos = null;
        try {
            Gson gson = new GsonBuilder().create();
            infos = gson.fromJson(jsonArr, type);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return infos;
    }

    private GsonUtils() {
    }

    /**
     * bean转化为json
     *
     * @param obj
     * @return
     */
    public static <T extends Object> String toJson(T obj) {
        initGson();
        String jsonResult = sGson.toJson(obj);
        return jsonResult;

    }

    public static <T extends Object> String toJsonWithoutExposeAnnotation(T obj) {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        String jsonResult = gson.toJson(obj);
        return jsonResult;

    }


    /**************泛型request response 解析***************/
//    public static <T> BaseRequest<T> parseGenericRequest(String json, Class<T> clazz) {
//        initGson();
//        Type type = new ParameterizedTypeImpl(BaseRequest.class, new Class[]{clazz});
//        return sGson.fromJson(json, type);
//    }
//    public static <T> BaseResponse<T> parseGenericReponse(String json, Class<T> clazz) {
//        initGson();
//        Type type = new ParameterizedTypeImpl(BaseResponse.class, new Class[]{clazz});
//        return sGson.fromJson(json, type);
//    }
//
//    public static <T> BaseRequest<List<T>> parseGenericRequestArray(String json, Class<T> clazz) {
//        initGson();
//        // 生成List<T> 中的 List<T>
//        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
//        // 根据List<T>生成完整的Result<List<T>>
//        Type type = new ParameterizedTypeImpl(BaseRequest.class, new Type[]{listType});
//        return sGson.fromJson(json, type);
//    }
//    public static <T> BaseResponse<List<T>> parseGenericReponseArray(String json, Class<T> clazz) {
//        initGson();
//        // 生成List<T> 中的 List<T>
//        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
//        // 根据List<T>生成完整的Result<List<T>>
//        Type type = new ParameterizedTypeImpl(BaseResponse.class, new Type[]{listType});
//        return sGson.fromJson(json, type);
//    }


}
