package com.huatu.teacheronline.alipay;

import com.huatu.teacheronline.engine.SendRequest;

public class PartnerConfig {

    public static final int SDK_PAY_FLAG = 1;
    //微信支付相关
    public static final String AppId = "wx7ee269efbd76b187";
    public static final String NOTIFY_URL_course = SendRequest.url_user + "pay/alipay";//回调地址

    public static final int alipaySucess = 1;//支付成功
    public static final String goldRechargeActivity = "GoldRechargeActivity";//金币充值页
    public static final String choosePayMethodActivity = "ChoosePayMethodActivity";//选择支付方式页面
    public static final String myOrderActivity = "MyOrderActivity";//我的订单
}
