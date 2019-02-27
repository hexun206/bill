package com.huatu.teacheronline.wxpay;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.MD5;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 微信支付帮助类
 *
 * @author linjy
 * @date 2016-5-19 08:34:15
 */
public class WXPayHelper {
    private static final String TAG = "WXPayHelper";
    private Context m_Context;
    private WXPayParams m_WXPayParams;
    PayReq m_PayReq;
    final IWXAPI m_WXApi;
    StringBuffer m_Sb;
    /**
     * 生成预付单结果
     */
    Map<String, String> m_UnifiedOrderResult;
    public static IPayCallBack m_IPayCallBack;

    /**
     * 微信支付帮助类构造函数
     *
     * @param p_Context      上下文对象
     * @param p_WXPayParams  微信支付参数
     * @param p_IPayCallBack 微信结果回调函数
     */
    public WXPayHelper(Context p_Context, WXPayParams p_WXPayParams, IPayCallBack p_IPayCallBack) {
        m_Context = p_Context;
        m_IPayCallBack = p_IPayCallBack;
        m_WXApi = WXAPIFactory.createWXAPI(p_Context, null);
        m_PayReq = new PayReq();
        m_Sb = new StringBuffer();
        m_WXPayParams = p_WXPayParams;
//        Constants.APP_ID=p_WXPayParams.getAppId();
        //m_WXApi.registerApp(p_WXPayParams.getAppId());
    }
    /**
     * 微信支付帮助类构造函数
     *
     * @param p_Context      上下文对象
     * @param p_IPayCallBack 微信结果回调函数
     */
    public WXPayHelper(Context p_Context,PayReq payReq,IPayCallBack p_IPayCallBack) {
        m_Context = p_Context;
        m_IPayCallBack = p_IPayCallBack;
        m_WXApi = WXAPIFactory.createWXAPI(p_Context, null);
//        m_PayReq = new PayReq();
//        m_Sb = new StringBuffer();
//        Constants.APP_ID=p_WXPayParams.getAppId();
        m_WXApi.registerApp(payReq.appId);
//        DebugUtil.e("WXPayHelper:m_WXApi.sendReq(payReq)"+payReq.sign);
        m_WXApi.sendReq(payReq);
    }

    /**
     * 获取微信支付回调函数
     */
    public static IPayCallBack getIPayCallBack() {
        return m_IPayCallBack;
    }

    /**
     * 发起支付请求
     */
    public void startPay() {
        if (!isWXAppInstalledAndSupported()) {
            Toast.makeText(m_Context, "请安装微信客户端", Toast.LENGTH_SHORT).show();
        } else {
            GetPrepayIdTask m_GetPrepayId = new GetPrepayIdTask();
            m_GetPrepayId.execute();
        }
    }

    /**
     * 是否安装微信客户端
     */
    public boolean isWXAppInstalledAndSupported() {
        boolean sIsWXAppInstalledAndSupported = m_WXApi.isWXAppInstalled()
                && m_WXApi.isWXAppSupportAPI();
        return sIsWXAppInstalledAndSupported;
    }

    /**
     * 获取预支付订单id任务
     */
    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            m_UnifiedOrderResult = result;
            if (result.containsKey("return_code")) {
                if (TextUtils.equals(result.get("return_code"), "SUCCESS")) {
                    genPayReq();
                    sendPayReq();
                } else {
                    Toast.makeText(m_Context, result.get("return_msg"), Toast.LENGTH_SHORT).show();
                }
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {
            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
            String entity = genProductArgs();
            Log.e("orion", entity);
            byte[] buf = UtilHelper.httpPost(url, entity);
            String content = new String(buf);
            Log.e("orion", content);
            Map<String, String> xml = UtilHelper.decodeXml(content);
            return xml;
        }
    }

    /***/
    private void genPayReq() {
        m_PayReq.appId = m_WXPayParams.getAppId();
        m_PayReq.partnerId = m_WXPayParams.getMchId();
        m_PayReq.prepayId = m_UnifiedOrderResult.get("prepay_id");
        m_PayReq.packageValue = "prepay_id=" + m_UnifiedOrderResult.get("prepay_id");
        m_PayReq.nonceStr = genNonceStr();
        m_PayReq.timeStamp = String.valueOf(genTimeStamp());

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", m_PayReq.appId));
        signParams.add(new BasicNameValuePair("noncestr", m_PayReq.nonceStr));
        signParams.add(new BasicNameValuePair("package", m_PayReq.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", m_PayReq.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", m_PayReq.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", m_PayReq.timeStamp));
        m_PayReq.sign = genAppSign(signParams);
        Log.e("orion", signParams.toString());

    }

    /**
     * 发送支付请求
     */
    private void sendPayReq() {
        m_WXApi.registerApp(m_WXPayParams.getAppId());
        m_WXApi.sendReq(m_PayReq);
    }

    /**
     * 获取产品参数
     */
    private String genProductArgs() {
        StringBuffer xml = new StringBuffer();
        try {
            String nonceStr = genNonceStr();
            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("appid", m_WXPayParams.getAppId()));
            packageParams.add(new BasicNameValuePair("body", m_WXPayParams.getProductName()));
            packageParams.add(new BasicNameValuePair("mch_id", m_WXPayParams.getMchId()));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            packageParams.add(new BasicNameValuePair("notify_url", m_WXPayParams.getNotifyUrl()));
            packageParams.add(new BasicNameValuePair("out_trade_no", m_WXPayParams.getOrderNo()));
            // packageParams.add(new BasicNameValuePair("attach",
            // m_WXPayParams.getAttach()));
            packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
            packageParams.add(new BasicNameValuePair("total_fee", m_WXPayParams.getTotalMoney()));
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));
            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));
            String xmlstring = UtilHelper.toXml(packageParams);
            return new String(xmlstring.toString().getBytes(), "ISO8859-1");// 解决中文乱码
        } catch (Exception e) {
            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }

    }

    /**
     * 获取随数
     */
    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    /**
     * 获取时间戳
     */
    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 生成包签名
     */
    private String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(m_WXPayParams.getApiKey());
        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        Log.e("orion", packageSign);
        return packageSign;
    }

    /**
     * 获取APP签名
     */
    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(m_WXPayParams.getApiKey());

        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        Log.e("orion", appSign);
        return appSign;
    }

    /**
     * 微信支付参数
     */
    public static class WXPayParams extends PayParams {
        private String mchId;

        /**
         * 设置微信支付商户ID
         */
        public void setMchId(String p_MchId) {
            this.mchId = p_MchId;
        }

        /**
         * 获取微信支付商户ID
         */
        public String getMchId() {
            return this.mchId;
        }

        private String apiKey;

        /**
         * 设置微信支付商户API密钥
         */
        public void setApiKey(String p_ApiKey) {
            this.apiKey = p_ApiKey;
        }

        /**
         * 获取微信支付商户API密钥
         */
        public String getApiKey() {
            return this.apiKey;
        }
    }

}
