/*
 * 系统: LaiDianYi
 * 文件名: WXPayAction.java
 * 版权: U1CITY Corporation 2015
 * 描述:
 * 创建人: zhengjb
 * 创建时间: 2015-9-10 下午8:10:22
 */
package com.huatu.teacheronline.wxpay;

import android.content.Intent;
import android.os.Handler;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.HomeActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.alipay.PartnerConfig;
import com.huatu.teacheronline.direct.fragment.DirectAllTabFragment;
import com.huatu.teacheronline.personal.MyOrderActivity;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ToastUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;

/**
 * 订单支付动作
 *
 * @author linjy
 */
public class OrderPayAction {
    private Double orderPrice;//价格
    private String order;//订单号
    private String title;//标题
    private BaseActivity activity;
    public int payBusinessType = 0;//0-实物支付 1-积分支付
    public String fromActivity;
    int payNum = 0;//微信会返回两次信息 过滤掉一次
    private static final String TAG = "OrderPayAction";

    public OrderPayAction(BaseActivity activity) {
        this.activity = activity;
    }

    public OrderPayAction(BaseActivity activity, String order, String title, Double orderPrice, String fromActivity) {
        this.activity = activity;
        this.order = order;
        this.title = title;
        this.orderPrice = orderPrice;
        this.fromActivity = fromActivity;
    }

    /**
     * 订单支付(APP)要
     *
     * @author linjy
     */
//    public void orderPay() {
//        WXPayHelper.WXPayParams wXPayParams = new WXPayHelper.WXPayParams();
//        wXPayParams.setAppId(PartnerConfig.AppId);
//        wXPayParams.setApiKey(PartnerConfig.ApiKey);
////        wXPayParams.setDesc("订单编号:" + order);
//        wXPayParams.setMchId(PartnerConfig.MchId);
//        wXPayParams.setOrderNo(order);
//        wXPayParams.setNotifyUrl(PartnerConfig.Wx_NotifyUrl);
//        wXPayParams.setProductName(title);
//        wXPayParams.setTotalMoney(String.valueOf(orderPrice * 100) + "");//(String.valueOf(Float.valueOf(analysis.getStringFromResult("orderPrice")) * 100));
//        DebugUtil.e("orderPay()  " + " orderPay():");
//        WXPayHelper wXPayHelper = new WXPayHelper(activity, wXPayParams, /*new IPayCallBack() {
//            @Override
//            public void payCallBack(int p_result) {
//                DebugUtil.e("WXPayHelper:payCallBack  " + p_result + " fromActivity:" + fromActivity);
//                switch (p_result) {
//                    case 0:// 支付成功
//                        ToastUtils.showToast(activity.getResources().getString(R.string.pay_sucess));
//                        if (PartnerConfig.goldRechargeActivity.equals(fromActivity)) {//金币充值页面
//                            activity.setResult(PartnerConfig.alipaySucess);
//                            activity.finish();
//                        } else if (PartnerConfig.choosePayMethodActivity.equals(fromActivity)) {//选择支付方式页面
//                            if (payNum == 0) {
//                                MainActivity.newIntentFlag(activity, "ToMyDirectActivity");
//                            }
//                            payNum++;
//                        }
//                        break;
//                    case -1:// 支付失败
//                        ToastUtils.showToast(activity.getResources().getString(R.string.pay_failure));
//                        break;
//                    case -2:// 取消支付
//                        ToastUtils.showToast(activity.getResources().getString(R.string.pay_failure));
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }*/IPayCallBack);
//        wXPayHelper.startPay();
//
//    }

    private IPayCallBack IPayCallBack = new IPayCallBack() {
        @Override
        public void payCallBack(int p_result) {
            DebugUtil.e("WXPayHelper:payCallBack  " + p_result + " fromActivity:" + fromActivity);
            switch (p_result) {
                case 0:// 支付成功
                    ToastUtils.showToast(activity.getResources().getString(R.string.pay_sucess));
                    if (PartnerConfig.goldRechargeActivity.equals(fromActivity)) {//金币充值页面
                        activity.setResult(PartnerConfig.alipaySucess);
                        activity.finish();
                    } else if (PartnerConfig.choosePayMethodActivity.equals(fromActivity)) {//选择支付方式页面
                        if (payNum == 0) {
//                            activity.finish();
//                            MyDirectActivity.newIntent(activity,"1");
//                            AppManager.getInstance().finishActivity(DirectDetailsActivity.class);
                            HomeActivity.newIntentFlag(activity, "ToMyDirectActivity", DirectAllTabFragment.videoType);
                        }
                        payNum++;
                    } else if (PartnerConfig.myOrderActivity.equals(fromActivity)) {//我的订单页面
                        DebugUtil.e(TAG,"wxpay"+PartnerConfig.myOrderActivity);
                        if (payNum == 0) {
                            Intent intent = new Intent();
                            intent.setAction(BaseActivity.ACTION_REFRESH);
                            activity.sendBroadcast(intent);
                        }
                        payNum++;
                    }
                    break;
                case -1:// 支付失败
                    ToastUtils.showToast(activity.getResources().getString(R.string.pay_failure));
                    break;
                case -2:// 取消支付
                    ToastUtils.showToast(activity.getResources().getString(R.string.pay_failure));
                    if (PartnerConfig.choosePayMethodActivity.equals(fromActivity)) {//选择支付方式页面
                        activity.finish();
                        MyOrderActivity.newIntent(activity);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 支付错误处理
     *
     * @param payBusinessType 支付业务类型 0-实物支付 1-积分支付
     */
    private void payError(int payBusinessType) {
        if (payBusinessType == 0) {

        }
    }

    private void closeActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
    }

    private String getRunningActivityName() {
        String contextString = activity.toString();
        return contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@"));
    }

    /**
     * 服务器签名，调取微信支付
     * @param request
     */
    public void payLastSteps(PayReq request){
//        PayReq request = new PayReq();
//
//        request.appId = "wxd930ea5d5a258f4f";
//
//        request.partnerId = "1900000109";
//
//        request.prepayId= "1101000000140415649af9fc314aa427";
//
//        request.packageValue = "Sign=WXPay";
//
//        request.nonceStr= "1101000000140429eb40476f8896f4c9";
//
//        request.timeStamp= "1398746574";
//
//        request.sign= "7FFECB600D7157C5AA49810D2D8F28BC2811827B";

//        api.sendReq(req);
        WXPayHelper wXPayHelper = new WXPayHelper(activity, request, IPayCallBack);
    }

}
