package com.huatu.teacheronline.wxpay;

/**
 * 支付返回结果接口
 */
public interface IPayCallBack {

    /**
     * 支付返回结果
     *
     * @param p_result 0-支付成功 -1-支付失败 -2-取消支付
     */
    public void payCallBack(int p_result);
}
