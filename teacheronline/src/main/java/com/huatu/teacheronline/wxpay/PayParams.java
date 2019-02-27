package com.huatu.teacheronline.wxpay;

/**
 * 支付参数
 */
public class PayParams {

    private String notifyUrl;

    /**
     * 设置支付商户主动通知地址
     */
    public void setNotifyUrl(String p_NotifyUrl) {
        this.notifyUrl = p_NotifyUrl;
    }

    /**
     * 获取支付商户主动通知地址
     */
    public String getNotifyUrl() {
        return this.notifyUrl;
    }

    private String appId;

    /**
     * 设置开放平台AppId
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * 获取开放平台AppId
     */
    public String getAppId() {
        return this.appId;
    }

    private String orderNo;

    /**
     * 设置订单号
     */
    public void setOrderNo(String p_OrderNo) {
        this.orderNo = p_OrderNo;
    }

    /**
     * 获取订单号
     */
    public String getOrderNo() {
        return this.orderNo;
    }

    private String productname;

    /**
     * 设置支付商品名称
     */
    public void setProductName(String p_ProductName) {
        this.productname = p_ProductName;
    }

    /**
     * 获取支付商品名称
     */
    public String getProductName() {
        return this.productname;
    }

    private String desc;

    /**
     * 设置支付商品描述
     */
    public void setDesc(String p_Desc) {
        this.desc = p_Desc;
    }

    /**
     * 获取支付商品描述
     */
    public String getDesc() {
        return this.desc;
    }

    private String totalMoney;

    /**
     * 设置支付总额 微信支付-分 支付宝-元
     */
    public void setTotalMoney(String p_TotalMoney) {
        if (p_TotalMoney.indexOf(".") > 0) {
            // 正则表达
            p_TotalMoney = p_TotalMoney.replaceAll("0+?$", "");// 去掉后面无用的零
            p_TotalMoney = p_TotalMoney.replaceAll("[.]$", "");// 如小数点后面全是零则去掉小数点
        }
        this.totalMoney = p_TotalMoney;
    }

    /**
     * 获取支付总额 单位：微信支付-分 支付宝-元
     */
    public String getTotalMoney() {
        return this.totalMoney;
    }

    private String attach;

    /**
     * 设置附加数据(微信支付支持)，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
     */
    public void setAttach(String p_Attach) {
        this.attach = p_Attach;
    }

    /**
     * 获取附加数据(微信支付支持)，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
     */
    public String getAttach() {
        return this.attach;
    }

}
