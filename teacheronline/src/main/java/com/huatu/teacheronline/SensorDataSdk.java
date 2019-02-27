package com.huatu.teacheronline;

import android.content.Context;

import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kinndann on 2018/12/19.
 * description:
 */
public class SensorDataSdk {

    public final static String TAG = "SensorDataSdk :";
    private SensorsDataAPI mSensorsDataAPI;

    private void SensorDataSdk() {
    }

    private static class Holder {
        private final static SensorDataSdk instance = new SensorDataSdk();

    }

    public static SensorDataSdk getInstance() {
        return Holder.instance;
    }


    // Debug 模式选项
    //   SensorsDataAPI.DebugMode.DEBUG_OFF - 关闭 Debug 模式
    //   SensorsDataAPI.DebugMode.DEBUG_ONLY - 打开 Debug 模式，校验数据，但不进行数据导入
    //   SensorsDataAPI.DebugMode.DEBUG_AND_TRACK - 打开 Debug 模式，校验数据，并将数据导入到神策分析中
    // 注意！请不要在正式发布的 App 中使用 Debug 模式！
    final SensorsDataAPI.DebugMode SA_DEBUG_MODE = SensorsDataAPI.DebugMode.DEBUG_OFF;
    // 神策数据接收的 URL
    final String SA_SERVER_URL_DEBUG = "https://datax-api.huatu.com/sa?project=default";
    final String SA_SERVER_URL_RELEASE = "";
    final String SA_SCHEME = "sa45d51a9c";

    public void init(Context context) {
        String uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, "");


        JSONObject properties = null;
        try {
            properties = new JSONObject();
            properties.put("platform", "AndroidApp");
            properties.put("business_line", "华图教师");
            properties.put("product_name", "华图教师app");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 传入 Context
// 数据接收的 URL
// Debug 模式选项
        mSensorsDataAPI = SensorsDataAPI.sharedInstance(
                context,                               // 传入 Context
                SA_SERVER_URL_DEBUG,                      // 数据接收的 URL
                SA_DEBUG_MODE);
        mSensorsDataAPI.enableLog(true); //开启调试日志
        mSensorsDataAPI.registerSuperProperties(properties); //设置公共属性



//        sensorsDataAPI.trackInstallation();
        if (!StringUtils.isEmpty(uid)) {
            mSensorsDataAPI.login(uid);
        }

        // 打开自动采集, 并指定追踪哪些 AutoTrack 事件
        List<SensorsDataAPI.AutoTrackEventType> eventTypeList = new ArrayList<>();
        // $AppStart
        eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_START);
        // $AppEnd
        eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_END);
        // $AppViewScreen
        eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_VIEW_SCREEN);
        // $AppClick
        eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_CLICK);
        SensorsDataAPI.sharedInstance().enableAutoTrack(eventTypeList);

    }


}
