package com.huatu.teacheronline.alipay;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.HomeActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.fragment.DirectAllTabFragment;
import com.huatu.teacheronline.personal.MyOrderActivity;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ToastUtils;

/**
 * 支付宝支付页面
 * Created by ply on 2016/1/28.
 */
public class AlipayActivity {
    public Activity activity;
    public String fromActivity;

    public AlipayActivity(Activity activity, String fromActivity) {
        this.activity = activity;
        this.fromActivity = fromActivity;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PartnerConfig.SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((String) msg.obj);
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {//支付成功
                        ToastUtils.showToast(R.string.pay_sucess);
                        if (PartnerConfig.goldRechargeActivity.equals(fromActivity)) {//金币充值页面
                            activity.setResult(PartnerConfig.alipaySucess);
                            activity.finish();
                        } else if (PartnerConfig.choosePayMethodActivity.equals(fromActivity)) {//选择支付方式页面
//                            activity.finish();
//                            MyDirectActivity.newIntent(activity,"1");
//                            AppManager.getInstance().finishActivity(DirectDetailsActivity.class);
                            HomeActivity.newIntentFlag(activity, "ToMyDirectActivity", DirectAllTabFragment.videoType);
                        } else if (PartnerConfig.myOrderActivity.equals(fromActivity)) {//我的订单页面
                            Intent intent = new Intent();
                            intent.setAction(BaseActivity.ACTION_REFRESH);
                            activity.sendBroadcast(intent);
                        }
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastUtils.showToast(R.string.pay_wait);
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            ToastUtils.showToast(R.string.pay_failure);
                            if (PartnerConfig.choosePayMethodActivity.equals(fromActivity)) {//选择支付方式页面
//                                MainActivity.newIntentFlag(activity, "ToMyDirectActivity");
                                activity.finish();
                                MyOrderActivity.newIntent(activity);
                            }
                        }
                    }
                    break;
            }
        }
    };

    /**
     * call alipay sdk pay. 调用SDK支付
     *
     * @param productName 商品名称
     * @param price       商品价格
     * @param body        商品描述(商品类型 1直播支付 0金币充值支付)
     * @param orderId     订单号
     * @param seller      支付宝账号
     * @param notifyUrl   支付回调地址
     */
//    public void pay(String productName, Double price, String body, String orderId, String seller, String notifyUrl) {
//        String orderInfo = getOrderInfo(productName, body, price, orderId, seller, notifyUrl);
//        String sign = sign(orderInfo);
//        try {
//            sign = URLEncoder.encode(sign, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        /**
//         * 完整的符合支付宝参数规范的订单信息
//         */
//        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
//
//        payLastSteps(payInfo);
//    }

    /**
     * 最后一个步骤 调起支付宝
     * @param payInfo
     */
    public void payLastSteps(final String payInfo) {
        DebugUtil.e("支付宝payInfo："+ payInfo);
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(activity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = PartnerConfig.SDK_PAY_FLAG;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 创建订单信息
     *
     * @param subject     商品名
     * @param body        描述
     * @param price       价格
     * @param ordernumber 订单号
     * @param seller      支付宝账户
     * @param notifyUrl   回调地址
     * @return
     */
//    public String getOrderInfo(String subject, String body, Double price, String ordernumber, String seller, String notifyUrl) {
//        // 签约合作者身份ID
//        String orderInfo = "partner=" + "\"" + PartnerConfig.PARTNER + "\"";
//
//        // 签约卖家支付宝账号
//        orderInfo += "&seller_id=" + "\"" + seller + "\"";
//
//        // 商户网站唯一订单号
//        orderInfo += "&out_trade_no=" + "\"" + ordernumber + "\"";
//
//        // 商品名称
//        orderInfo += "&subject=" + "\"" + subject + "\"";
//
//        // 商品详情
//        orderInfo += "&body=" + "\"" + body + "\"";
//
//        // 商品金额
//        orderInfo += "&total_fee=" + "\"" + price + "\"";
//
//        // 服务器异步通知页面路径
//        orderInfo += "&notify_url=" + "\"" + notifyUrl + "\"";
//
//        // 服务接口名称， 固定值
//        orderInfo += "&service=\"mobile.securitypay.pay\"";
//
//        // 支付类型， 固定值
//        orderInfo += "&payment_type=\"1\"";
//
//        // 参数编码， 固定值
//        orderInfo += "&_input_charset=\"utf-8\"";
//
////        // 设置未付款交易的超时时间
////        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
////        // 取值范围：1m～15d。
////        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
////        // 该参数数值不接受小数点，如1.5h，可转换为90m。
////        orderInfo += "&it_b_pay=\"30m\"";
////
////        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
////         orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
////
////        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
////        orderInfo += "&return_url=\"m.alipay.com\"";
////
////        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
////         orderInfo += "&paymethod=\"expressGateway\"";
//
//        return orderInfo;
//    }

    /**
     * sign the order info. 对订单信息进行RSA签名
     *
     * @param content 待签名订单信息
     */
//    public String sign(String content) {
//        return SignUtils.sign(content, PartnerConfig.RSA_PRIVATE);
//    }

    /**
     * get the sign type we use. 获取签名方式MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMZGL3NTFwSNyq7wCp6SdvT27keBl6YC7/OukLgD+r07xoa66CKSInoCEUhpPVTWl7nN4I
     * +rJjmDRePTyrSC1QppH4EjV9bZYw8jnGAbYaBryaquLTpWa55hoin8dwGBpPB6c7RTEafweW3RDs3tFLuAj0dt/gx3jOyeert0k
     * /qxAgMBAAECgYAGae7PBDkSZKPR1OIDDynZ8sLyfTayoBWzBEqaKUbLKEvbv+ASfjevSrHUbdMWE3sE/6vr
     * +dVatuxj2k4MWM8bYIM5MbCvX28BIGIt0su7XELS4AU9wXYlVAGzJEm3LNqaWhaWAbtqssNbPZlzjopRK/kGwIP3Z/wOodmZIS+koQJBAOye7Xx1yIVAXAhNxmU4uA7+p8kovkAzUn11
     * /2FpXw10tRSRhhwtyIa7KNJsS1WDbtVQvhor+3hTLT4BuAzqfQsCQQDWg0TPOs2W/PTfA3dMrILt6v6v3xPpT6CkiAr4LCEtc/NYPuifDOCZUGHcQcRIm/XumfsiOXEfpxOswwnUuySzAkEAw7P+v
     * /26x1sfw05wVK/Aq8Y7h8jG8dqA03uQqUS5dSudyVFDFnNXcvfBH6ip9iQquwt477SI3FAPD0Xkr+oRWQJAbaubKF+l8IXPI52ABsFEByNXps7kPSoqtTOgeBUHuVdImFtBTt+kjH2eKp
     * +tHHowGBa9YFgBz+75jiABDLPJJQJAfW9bh3aihhxmqZoElkmEif1uRY0LLLCMmjTyRxZGOXrL0PH4NavErYTPjSjVzBPefaTXTVu+oK4jscvI9ytgFQ==
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
