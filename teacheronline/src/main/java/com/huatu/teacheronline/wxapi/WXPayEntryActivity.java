package com.huatu.teacheronline.wxapi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.alipay.PartnerConfig;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.wxpay.IPayCallBack;
import com.huatu.teacheronline.wxpay.WXPayHelper;
import com.ta.utdid2.android.utils.StringUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**
 * *
 * 微信支付结果回调
 *
 * @author ljyu 2016-5-20
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI m_WXApi;
    private Context context;


    @Override
    public void initView() {
        setContentView(R.layout.activity_h5_detail);
        context = this;
        m_WXApi = WXAPIFactory.createWXAPI(this, PartnerConfig.AppId);
        m_WXApi.handleIntent(getIntent(), this);
    }

    @Override
    public void setListener() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        m_WXApi.handleIntent(intent, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.wxpay_entry, menu);
        return true;
    }

    @Override
    public void onReq(BaseReq arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onResp(BaseResp p_BaseResp) {
        // TODO Auto-generated method stub
        if (p_BaseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int m_PayResult = 0;
            if (p_BaseResp.errCode == -2) {
                ToastUtils.showToast("您取消了支付");

                m_PayResult = -2;
            }
            if (p_BaseResp.errCode == -1) {
                String m_ErroInfor = StringUtils.isEmpty(p_BaseResp.errStr) ? "微信登录错误，请重新登录" : p_BaseResp.errStr;
                ToastUtils.showToast("支付失败:" + m_ErroInfor);
                m_PayResult = -1;
            }
            IPayCallBack m_IPayCallBack = WXPayHelper.getIPayCallBack();
            if (m_IPayCallBack != null) {
                m_IPayCallBack.payCallBack(m_PayResult);
            }
            finish();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
