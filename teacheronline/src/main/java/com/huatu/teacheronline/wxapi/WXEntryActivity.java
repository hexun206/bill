package com.huatu.teacheronline.wxapi;

import android.os.Bundle;

import com.umeng.weixin.callback.WXCallbackActivity;


/**
 * Created by ntop on 15/9/4.
 */
public class WXEntryActivity extends WXCallbackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
        }catch (Exception e){
            finish();
        }
    }
}
