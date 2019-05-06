package com.bysj.bill_system.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static Gson sGson = new Gson();


    public static String obj2Json(Object object) {
        return sGson.toJson(object);
    }


    public static <T> T json2Obj(String json, Class<T> tClass) {
        return sGson.fromJson(json, tClass);
    }


    public static <T> List<T> json2List(String json, Class<T> tClass) {
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();

        for (final JsonElement elem : array) {
            list.add(sGson.fromJson(elem, tClass));
        }
        return list;
    }

}
