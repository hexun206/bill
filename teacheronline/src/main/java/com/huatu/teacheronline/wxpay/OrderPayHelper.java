/*
 * 系统: LaiDianYi
 * 文件名: WXPayAction.java
 * 版权: U1CITY Corporation 2015
 * 描述:
 * 创建人: zhengjb
 * 创建时间: 2015-9-10 下午8:10:22
 */
package com.huatu.teacheronline.wxpay;

import android.os.Handler;

import com.huatu.teacheronline.BaseActivity;


/**
 * 订单支付相关
 *
 * @author linjy
 */
public class OrderPayHelper {
    /**
     * 订单支付成功处理
     *
     * @param payBusinessType 支付业务类型 0-实物支付 1-积分支付
     */
    public static void paySucceed(BaseActivity activity, int payBusinessType, String orderNo, String orderId) {
        switch (payBusinessType) {
            case 0:
                break;
            case 1:
                break;
        }
        closeActivity(activity);
    }

    /**
     * 订单支付失败处理
     */
    public static void payFail(BaseActivity activity) {
        closeActivity(activity);
    }

    private static void closeActivity(final BaseActivity activity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.finish();
            }
        }, 500);
    }

}
