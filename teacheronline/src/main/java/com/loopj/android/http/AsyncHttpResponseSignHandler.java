package com.loopj.android.http;

import android.os.Looper;

import com.huatu.teacheronline.bean.EventMessage;
import com.huatu.teacheronline.utils.DebugUtil;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 18250 on 2017/3/7.
 */
public abstract class AsyncHttpResponseSignHandler extends AsyncHttpResponseHandler {
    private Looper looper = null;
    public AsyncHttpResponseSignHandler() {
        this(null);
    }
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        String res = new String(responseBody);
        try {
            JSONObject   jsonObject = new JSONObject(res);
            String code = jsonObject.getString("code");
            if (code.equals("401")) {
                EventBus.getDefault().post(new EventMessage(EventMessage.NEED_RE_LOGIN, code));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
 public AsyncHttpResponseSignHandler(Looper looper){
     this.looper = looper == null ? Looper.myLooper() : looper;
     // Use asynchronous mode by default.
     setUseSynchronousMode(false);
     // Do not use the pool's thread to fire callbacks by default.
     setUsePoolThread(false);
 }
}
