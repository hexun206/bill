package com.bysj.bill_system.utils;

import android.accounts.Account;
import android.content.Context;
import android.util.Log;

import com.bysj.bill_system.bean.AccountBean;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DataUtils {
    public static final String USER_DATA = "user_data";
    public static final String LAST_LOGIN_USER_DATA = "last_login_user_data";

    public static List<AccountBean> getUserData(Context context) {
        List<AccountBean> list = new ArrayList<>();
        String userData = (String) SPUtils.getParam(context, USER_DATA, "");
        if (userData != null && userData.length() != 0) {
            try {
                JSONArray jsonArray = new JSONArray(userData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(), AccountBean.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("用户数据", userData + "");
        return list;
    }

    public static void addUserData(Context context, AccountBean accountBean) {
        List<AccountBean> data = getUserData(context);
        boolean isContains = false;
        for (AccountBean datum : data) {
            if (datum.phone.equals(accountBean.phone)) {
                datum.passwrod = accountBean.passwrod;
                isContains = true;
            }
        }
        if (!isContains)
            data.add(accountBean);
        if (data.size() > 50)//最多存储50条本地数据
            data.remove(0);
        String s = JsonUtils.obj2Json(data);
        SPUtils.setParam(context, USER_DATA, s);
        Log.d("用户数据", s);
    }

    public static boolean checkUserData(Context context, AccountBean accountBean) {
        List<AccountBean> data = getUserData(context);
        boolean isRightPasswrod = false;
        boolean isContains = false;
        for (AccountBean datum : data) {
            if (datum.phone.equals(accountBean.phone)) {
                isContains = true;
                isRightPasswrod = datum.passwrod.equals(accountBean.passwrod);
                if (!isRightPasswrod)
                    ToastUtils.showToast(context, "输入密码错误");
            }
        }
        if (!isContains)
            ToastUtils.showToast(context, "账号不存在，请注册账号后登录");
        return isContains && isRightPasswrod;
    }

    public static void saveLoginAccount(Context context, AccountBean accountBean) {
        SPUtils.setParam(context, LAST_LOGIN_USER_DATA, JsonUtils.obj2Json(accountBean));
    }

    public static AccountBean getLoginAccount(Context context) {
        return JsonUtils.json2Obj(SPUtils.getParam(context, LAST_LOGIN_USER_DATA, "{}").toString(), AccountBean.class);
    }

    public static void outLoginAccount(Context context) {
        SPUtils.setParam(context, LAST_LOGIN_USER_DATA, "");
    }
}
