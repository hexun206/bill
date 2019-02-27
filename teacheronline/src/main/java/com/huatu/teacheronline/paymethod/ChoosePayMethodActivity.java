package com.huatu.teacheronline.paymethod;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gensee.utils.StringUtil;
import com.greendao.DirectBean;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.HomeActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.alipay.AlipayActivity;
import com.huatu.teacheronline.alipay.PartnerConfig;
import com.huatu.teacheronline.direct.fragment.DirectAllTabFragment;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.GoldPersonalActivity;
import com.huatu.teacheronline.personal.GoldRechargeActivity;
import com.huatu.teacheronline.personal.MyOrderActivity;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.wxpay.OrderPayAction;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.umeng.analytics.MobclickAgent;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wf
 * 选择支付方式页面
 */
public class ChoosePayMethodActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;

    private TextView tv_price, tv_projectName, tv_pay;
    private RelativeLayout rl_iconAlipy, rl_iconGold;
    private ImageView iv_alipy, iv_goldPay;

    //    private Double money;//金额
    private String orderId;//订单号
    //    private String projectName;//商品名称
    private int payType = 0;//0支付宝 1 金币支付
    private ObtainDataLister obtatinDataListener;
    private CustomAlertDialog mCustomLoadingDialog;

    private String uid;//用户id
    private AlipayActivity alipayActivity;
    private RelativeLayout rl_alipy;
    private RelativeLayout rl_goldPay;
    private RelativeLayout rl_weixinpay;
    private ImageView iv_iconForWeixinpay;
    private RelativeLayout rl_iconWeixinpay;
    private String personName;
    private String tel;//电话
    private String province;//省
    private String addressStreet;//街道
    private String addressId = "";//地址id
    private ObtainDataListerForCreateOrder obtainDataListerForCreateOrder;
    private DirectBean directBean;

    private LinearLayout rl_obtainaddress;
    private TextView tv_add_address, tv_choose_obtain_address_person, tv_choose_obtain_address, tv_choose_obtain_address_number, tv_gold_balance;
    private PercentRelativeLayout rl_edit_phone;
    private EditText tv_edit1;

    private final String orderType = "2"; //订单类型 1 金币充值订单 2 购买课程订单
    private CustomAlertDialog mCustomGoldDilog; //金币支付弹出框 查看订单 立即支付
    private TextView tv_zhibotime;
    private String zhibotime;
    private View tv_Prompt;
    private ImageView ib_main_left;
    private ObtainContextLister obtainContextLister;
    private TextView tv_context;
    private String context = "";
    private Boolean isNot = false;
    private ImageView iv_aniu;

    private String learning_period = ""; //学段
    private String subject = "";//科目
    private RelativeLayout rl_pop_pay;
    private RelativeLayout rl_pay;
    private TextView tv_coupon;
    private TextView tv_pric_total;
    private SimpleDraweeView sdv_icon;
    private RelativeLayout rl_voucher;
    private RelativeLayout rl_iconGold1;
    private TextView tv_gold_balance1;
    private ImageView iv_goldPay1;
    private RelativeLayout rl_goldPay1;
    private PopupWindow mPopupWindow;


    @Override
    public void initView() {
        setContentView(R.layout.activity_choose_paymethod);
        alipayActivity = new AlipayActivity(this, PartnerConfig.choosePayMethodActivity);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        mCustomLoadingDialog = new CustomAlertDialog(ChoosePayMethodActivity.this, R.layout.dialog_loading_custom);
        mCustomGoldDilog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
//        money = getIntent().getDoubleExtra("money", 1.00);//金额
//        orderId = getIntent().getStringExtra("orderId");//订单号
//        projectName = getIntent().getStringExtra("projectName");
        tv_edit1 = (EditText) findViewById(R.id.tv_edit);
        directBean = (DirectBean) getIntent().getSerializableExtra("DirectBean");
        learning_period = getIntent().getStringExtra("learning_period");
        subject = getIntent().getStringExtra("subject");

        rl_edit_phone = (PercentRelativeLayout) findViewById(R.id.rl_edit_phone);
        tv_edit1 = (EditText) findViewById(R.id.tv_edit);//订单输入手机号
        tv_edit1.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        String mobile = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_MOBILE, "");
        tv_edit1.setText(mobile);
        tv_edit1.setSelection(mobile.length());
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        ib_main_left = (ImageView) findViewById(R.id.ib_main_left);
        ib_main_left.setImageResource(R.drawable.back_arrow);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.payMethod);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_projectName = (TextView) findViewById(R.id.tv_projectName);
        tv_projectName.setText(directBean.getTitle());
        rl_iconAlipy = (RelativeLayout) findViewById(R.id.rl_iconAlipy);
        rl_iconGold = (RelativeLayout) findViewById(R.id.rl_iconGold);
//        iv_alipy = (ImageView) findViewById(R.id.iv_iconForAlipy);
//        iv_goldPay = (ImageView) findViewById(R.id.iv_iconForGoldPay);
        tv_Prompt = findViewById(R.id.tv_Prompt);
        tv_pay = (TextView) findViewById(R.id.tv_pay);
        zhibotime = getIntent().getStringExtra("zhibotime");
        tv_zhibotime = (TextView) findViewById(R.id.tv_zhibotime);//课程开课结束时间
        if (StringUtils.isEmpty(directBean.getHd())) {
            tv_zhibotime.setText(learning_period + "  " + zhibotime);
        } else {
            String textStr = "<font color=\"#00b38a\">" + directBean.getHd() + "</font>" + "<font color=\"#999999\">"
                    + "</font>";
            setDirectTime(tv_zhibotime, R.drawable.ic_hd_online, textStr, 0);
        }


//        if (("￥" + directBean.getActualPrice()).equals("￥0.0")) {
//            tv_pay.setText("免费获取");
//        }
//        iv_iconForWeixinpay = (ImageView) findViewById(R.id.iv_iconForWeixinpay);
        rl_iconWeixinpay = (RelativeLayout) findViewById(R.id.rl_iconWeixinpay);
//        rl_alipy = (RelativeLayout) findViewById(R.id.rl_alipy);
        rl_iconAlipy = (RelativeLayout) findViewById(R.id.rl_iconAlipy);
        rl_iconWeixinpay = (RelativeLayout) findViewById(R.id.rl_iconWeixinpay);
        rl_iconGold = (RelativeLayout) findViewById(R.id.rl_iconGold);

//        rl_goldPay = (RelativeLayout) findViewById(R.id.rl_goldPay);
//        rl_weixinpay = (RelativeLayout) findViewById(R.id.rl_weixinpay);

        rl_obtainaddress = (LinearLayout) findViewById(R.id.rl_obtainaddress);
        tv_add_address = (TextView) findViewById(R.id.tv_add_address);
        tv_choose_obtain_address_person = (TextView) findViewById(R.id.tv_choose_obtain_address_person);
        tv_choose_obtain_address = (TextView) findViewById(R.id.tv_choose_obtain_address);
        tv_choose_obtain_address_number = (TextView) findViewById(R.id.tv_choose_obtain_address_number);
//        tv_gold_balance = (TextView) findViewById(R.id.tv_gold_balance);

        tv_pric_total = (TextView) findViewById(R.id.tv_pric_total);
        rl_pay = (RelativeLayout) findViewById(R.id.rl_pay);
        tv_coupon = (TextView) findViewById(R.id.tv_coupon);//优惠券
        rl_voucher = (RelativeLayout) findViewById(R.id.rl_Voucher);
        sdv_icon = (SimpleDraweeView) findViewById(R.id.sdv_icon);
        TextView tv_cash = (TextView) findViewById(R.id.tv_cash);
        TextView tv_discou = (TextView) findViewById(R.id.tv_discou);
        TextView tv_gift = (TextView) findViewById(R.id.tv_gift);

        if (directBean.getdisproperty().equals("1") || !"2".equals(directBean.getState())) {//优惠方式，1是正常，2是特价，3是秒杀，4是折扣
            tv_pric_total.setText("¥" + directBean.getActualPrice());//原本价格
            tv_price.setText("￥" + directBean.getActualPrice());
        } else {


            tv_pric_total.setText("¥" + directBean.getDisPrice());//活动价格
            tv_price.setText("￥" + directBean.getDisPrice());
        }

        if (directBean.getMostrcash() != 0) {//返现金额为0 说明没有返现反之有
            tv_cash.setVisibility(View.VISIBLE);
        } else {
            tv_cash.setVisibility(View.GONE);
        }
        if (directBean.getdisproperty().equals("1") || !"2".equals(directBean.getState())) {//优惠方式，1是正常，2是特价，3是秒杀，4是折扣
            tv_discou.setVisibility(View.GONE);
        } else if (directBean.getdisproperty().equals("2")) {
            tv_discou.setVisibility(View.VISIBLE);
            tv_discou.setText("特价");
        } else if (directBean.getdisproperty().equals("3")) {
            tv_discou.setVisibility(View.VISIBLE);
            tv_discou.setText("秒杀");
        } else if (directBean.getdisproperty().equals("4")) {
            tv_discou.setVisibility(View.VISIBLE);
            tv_discou.setText(directBean.getDiscount() + "折");
        } else {
            tv_discou.setVisibility(View.GONE);
        }

        if (directBean.getGift().size() > 0) {//如果赠品数量大于0说明有赠品
            tv_gift.setVisibility(View.VISIBLE);
        } else {
            tv_gift.setVisibility(View.GONE);
        }

        iv_aniu = (ImageView) findViewById(R.id.iv_aniu);
//        tv_gold_balance.setText("（余额：" + CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_GOLD, "0") + ")");
        if ("0".equals(directBean.getIsHasJy())) {
            rl_obtainaddress.setVisibility(View.GONE);
            rl_edit_phone.setVisibility(View.VISIBLE);
        } else {
            rl_obtainaddress.setVisibility(View.VISIBLE);
            rl_edit_phone.setVisibility(View.GONE);
        }
        ContextData();
        Hierar();
    }

    private void Hierar() {
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        sdv_icon.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(sdv_icon, directBean.getScaleimg(), R.drawable.ic_loading);
    }

    private void ContextData() {
        obtainContextLister = new ObtainContextLister(this);
        SendRequest.getContext(obtainContextLister);
    }

    @Override
    public void setListener() {
        rl_iconWeixinpay.setOnClickListener(this);
        rl_main_left.setOnClickListener(this);
//        rl_weixinpay.setOnClickListener(this);
//        tv_pay.setOnClickListener(this);
//        rl_alipy.setOnClickListener(this);
//        rl_goldPay.setOnClickListener(this);
        rl_obtainaddress.setOnClickListener(this);
        rl_iconAlipy.setOnClickListener(this);
        rl_iconWeixinpay.setOnClickListener(this);
        rl_iconGold.setOnClickListener(this);
        mCustomGoldDilog.setOkOnClickListener(this);
        mCustomGoldDilog.setCancelOnClickListener(this);
        findViewById(R.id.tv_p).setOnClickListener(this);
        iv_aniu.setOnClickListener(this);
        rl_pay.setOnClickListener(this);
//        rl_voucher.setOnClickListener(this);//代金卷点击
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.tv_p:
                Binding_iphone(v);
                break;
            case R.id.tv_dialog_ok:
                mCustomGoldDilog.dismiss();
                toPayForGold();
                break;
            case R.id.tv_dialog_cancel:
                mCustomGoldDilog.dismiss();
                back();
                MyOrderActivity.newIntent(this);
                break;
            case R.id.rl_obtainaddress:
                MobclickAgent.onEvent(ChoosePayMethodActivity.this, "address");//点击填写地址姓名
                Intent intent = new Intent(ChoosePayMethodActivity.this, ChooseObtainAddressActivity.class);
                startActivityForResult(intent, 1);

                break;
            case R.id.rl_weixinpay://微信布局
            case R.id.rl_iconWeixinpay:
                payType = 2;
                MobclickAgent.onEvent(ChoosePayMethodActivity.this, "weChatPayOnClik");//计数微信支付
                iv_alipy.setImageResource(R.drawable.rance_noselect_paymethod);
                iv_iconForWeixinpay.setImageResource(R.drawable.dl_ch);
                iv_goldPay.setImageResource(R.drawable.rance_noselect_paymethod);
                iv_goldPay1.setImageResource(R.drawable.rance_noselect_paymethod);
                break;
            case R.id.rl_alipy://支付宝布局
            case R.id.rl_iconAlipy:
                payType = 0;
                MobclickAgent.onEvent(ChoosePayMethodActivity.this, " alipayPayOnClik");//计数支付宝
                iv_alipy.setImageResource(R.drawable.dl_ch);
                iv_goldPay.setImageResource(R.drawable.rance_noselect_paymethod);
                iv_iconForWeixinpay.setImageResource(R.drawable.rance_noselect_paymethod);
                iv_goldPay1.setImageResource(R.drawable.rance_noselect_paymethod);
                break;
            case R.id.rl_goldPay://金币布局
            case R.id.rl_iconGold:
                payType = 1;
                MobclickAgent.onEvent(ChoosePayMethodActivity.this, "Goldpayment");//计数金币
                iv_goldPay.setImageResource(R.drawable.dl_ch);
                iv_alipy.setImageResource(R.drawable.rance_noselect_paymethod);
                iv_iconForWeixinpay.setImageResource(R.drawable.rance_noselect_paymethod);
                break;

            case R.id.rl_iconGold1:
                payType = 1;
                MobclickAgent.onEvent(ChoosePayMethodActivity.this, "Goldpayment");//计数金币
                iv_goldPay1.setImageResource(R.drawable.dl_ch);
                iv_alipy.setImageResource(R.drawable.rance_noselect_paymethod);
                iv_iconForWeixinpay.setImageResource(R.drawable.rance_noselect_paymethod);
                break;

            case R.id.iv_aniu:
                if (isNot) {
                    iv_aniu.setImageResource(R.drawable.oval_g);
                    isNot = false;
                    rl_pay.setBackgroundColor(getResources().getColor(R.color.gray002));
                } else {
                    iv_aniu.setImageResource(R.drawable.dl_ch_ii);
                    isNot = true;
                    rl_pay.setBackgroundColor(getResources().getColor(R.color.green013));
                }

                break;

            case R.id.rl_pop_pay:
                if (isNot) {
                    MobclickAgent.onEvent(ChoosePayMethodActivity.this, "toPayOnClik");//计数去支付
                    String telephone = tv_edit1.getText().toString();
                    if ("1".equals(directBean.getIsHasJy())) {
                        if (TextUtils.isEmpty(tel)) {
                            ToastUtils.showToast("手机号不能为空");
                        }
                    } else {
                        if (TextUtils.isEmpty(telephone)) {
                            ToastUtils.showToast("手机号不能为空");
                            return;
                        }
                        if (telephone.length() != 11) {
                            ToastUtils.showToast("请输入正确手机号");
                            return;
                        }
                    }
                    createOrder();
                    break;
                }
//                else{
//                    ToastUtils.showToast("请阅读勾选查看须知内容！");
//                }
                break;

            case R.id.rl_pay:
                if (!isNot) {
                    ToastUtils.showToast("请阅读勾选查看须知内容！");
                } else {
                    String telephone = tv_edit1.getText().toString();
                    if (("￥" + directBean.getActualPrice()).equals("￥0.0")) {
                        if ("1".equals(directBean.getIsHasJy())) {
                            if (TextUtils.isEmpty(tel)) {
                                ToastUtils.showToast("手机号不能为空");
                            }
                        } else {
                            if (TextUtils.isEmpty(telephone)) {
                                ToastUtils.showToast("手机号不能为空");
                                return;
                            }
                            if (telephone.length() != 11) {
                                ToastUtils.showToast("请输入正确手机号");
                                return;
                            }
                        }
                        createOrder();
                    } else {
                        //点击弹出支付框
                        PopPayment(v);
                        tv_gold_balance.setText("（余额：" + CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_GOLD, "0") + ")");
                        tv_gold_balance1.setText("（余额：" + CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_GOLD, "0") + ")");
                    }
                }
                break;
            //点击弹出待金价列表
//            case R.id.rl_Voucher:
//
//                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                switch (resultCode) {
                    case RESULT_OK:
                        addressId = data.getStringExtra("addressId");
                        personName = data.getStringExtra("personName");
                        tel = data.getStringExtra("tel");
                        province = data.getStringExtra("province");
                        addressStreet = data.getStringExtra("addressStreet");
                        rl_obtainaddress.setVisibility(View.VISIBLE);
                        tv_choose_obtain_address_number.setText(tel);
                        tv_choose_obtain_address.setText(province + addressStreet);
                        tv_choose_obtain_address_person.setText("收货人：" + personName);
                        if (!personName.equals("")) {
                            tv_choose_obtain_address_person.setVisibility(View.VISIBLE);
                            tv_Prompt.setVisibility(View.GONE);
                        }
                        break;
                }
                break;
        }


    }

//    public static void newIntent(Activity context, Double money, String projectName, String orderId ,String zhibotime) {
//        Intent intent = new Intent(context, ChoosePayMethodActivity.class);
//        intent.putExtra("money", money);
//        intent.putExtra("projectName", projectName);
//        intent.putExtra("orderId", orderId);
//        intent.putExtra("zhibotime", zhibotime);
//        context.startActivity(intent);
//    }

    /**
     * @param context
     * @param directBean
     * @param zhibotime
     * @param learning_period //学段
     * @param subject//科目
     */
    public static void newIntent(Activity context, DirectBean directBean, String zhibotime, String learning_period, String subject) {
        Intent intent = new Intent(context, ChoosePayMethodActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("DirectBean", directBean);
        intent.putExtras(mBundle);
        intent.putExtra("zhibotime", zhibotime);
        intent.putExtra("learning_period", learning_period);
        intent.putExtra("subject", subject);
        context.startActivity(intent);
    }


    /**
     * 金币支付
     *
     * @param
     */
    public void toPayForGold() {
        obtatinDataListener = new ObtainDataLister(this);
        if (StringUtils.isEmpty(orderId)) {
            ToastUtils.showToast(getResources().getString(R.string.data_loading));
            return;
        }
        if (directBean.getdisproperty().equals("1")) {//优惠方式，1是正常，2是特价，3是秒杀，4是折扣
            SendRequest.toPayForGold(uid, orderId, directBean.getActualPrice(), "1", obtatinDataListener);
        } else {
            SendRequest.toPayForGold(uid, orderId, Double.parseDouble(directBean.getDisPrice()), "1", obtatinDataListener);
        }

    }

    private static class ObtainDataLister extends ObtainDataFromNetListener<String, String> {

        private ChoosePayMethodActivity weak_activity;

        public ObtainDataLister(ChoosePayMethodActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.show();
                weak_activity.mCustomLoadingDialog.setTitle(weak_activity.getResources().getString(R.string.loading));
            }
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                if (res != null) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weak_activity.toPayForGold_Sucess(res);
                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        } else {
                            ToastUtils.showToast(res);
                        }
                    }
                });
            }
        }
    }

    public void toPayForGold_Sucess(String res) {
        if ("1".equals(res)) {//支付成功
//            ToastUtils.showToast(R.string.pay_sucess);
//            finish();
//            MyDirectActivity.newIntent(this,"1");
//            AppManager.getInstance().finishActivity(DirectDetailsActivity.class);
            HomeActivity.newIntentFlag(this, "ToMyDirectActivity", DirectAllTabFragment.videoType);
        } else if ("2".equals(res)) {//金币不足
            showDialogForRecharge();
        } else {
            ToastUtils.showToast(R.string.pay_failure);
        }
    }

    /**
     * 充值对话框
     */
    public void showDialogForRecharge() {
        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(ChoosePayMethodActivity.this, R.layout.pay_method_dialog_layout);
        customAlertDialog.show();
        customAlertDialog.setOkOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                customAlertDialog.dismiss();
                finish();
                MyOrderActivity.newIntent(ChoosePayMethodActivity.this);
                GoldRechargeActivity.newIntent(ChoosePayMethodActivity.this, String.valueOf(GoldPersonalActivity.requestCode_GoldPersonalActivity));
            }
        });
        customAlertDialog.setCancelOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                customAlertDialog.dismiss();
            }
        });
    }

    /**
     * 生成订单接口
     */
    public void createOrder() {
//        addressId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_NAME, "");
        if ("1".equals(directBean.getIsHasJy())) {
            if (StringUtil.isEmpty(addressId)) {
                ToastUtils.showToast(getResources().getString(R.string.addressIdIsNull));
                return;
            }
        }
        obtainDataListerForCreateOrder = new ObtainDataListerForCreateOrder(this);
        if ("1".equals(directBean.getIsHasJy())) {
            SendRequest.createOrderForSumit(directBean.getRid(), uid, addressId, tel, learning_period, subject, obtainDataListerForCreateOrder);
        } else {
            SendRequest.createOrderForSumit(directBean.getRid(), uid, addressId, tv_edit1.getText().toString().trim(), learning_period, subject, obtainDataListerForCreateOrder);
        }

    }

    private static class ObtainDataListerForCreateOrder extends ObtainDataFromNetListener<String, String> {

        private ChoosePayMethodActivity weak_activity;

        public ObtainDataListerForCreateOrder(ChoosePayMethodActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.setTitle(weak_activity.getResources().getString(R.string.loading));
                weak_activity.mCustomLoadingDialog.show();
            }
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(res)) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weak_activity.orderId = res;
                            if (("￥" + weak_activity.directBean.getActualPrice()).equals("￥0.0")) {
                                ToastUtils.showToast("购买成功！");
//                                weak_activity.finish();
//                                MyDirectActivity.newIntent(weak_activity, "1");
//                                AppManager.getInstance().finishActivity();
                                HomeActivity.newIntentFlag(weak_activity, "ToMyDirectActivity", DirectAllTabFragment.videoType);
                            } else {
                                weak_activity.orderSuccessPaymethod(res);
                            }

                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {

                            ToastUtils.showToast(R.string.server_error);


                        }
                    }
                });
            }
        }
    }

    /**
     * 订单生成成功
     *
     * @param res
     */
    private void orderSuccessPaymethod(String res) {
        switch (payType) {
            case 0://支付宝
//                alipayActivity.pay(directBean.getTitle(), this
//                                .directBean.getActualPrice(), "1",
//                        res, PartnerConfig.SELLER, PartnerConfig.NOTIFY_URL_course);
                ObtainPayEncryptionSignature obtainPayEncryptionSignature = new ObtainPayEncryptionSignature(this, 0);
                SendRequest.getPayEncryptionSignature(2 + "", res, orderType, obtainPayEncryptionSignature);
                break;
            case 1://金币支付
                mCustomGoldDilog.show();
                mCustomGoldDilog.setCancelText("查看订单");
                mCustomGoldDilog.setOkText("立即支付");
                mCustomGoldDilog.setCanceledOnTouchOutside(false);
                mCustomGoldDilog.setTitle("订单已生成<br/>订单已生成是否立即支付？");
                break;
            case 2://微信支付
//                new OrderPayAction(this, res, directBean.getTitle(), directBean.getActualPrice(), PartnerConfig.choosePayMethodActivity).orderPay();
                ObtainPayEncryptionSignature obtainPayEncryptionSignature2 = new ObtainPayEncryptionSignature(this, 2);
                SendRequest.getPayEncryptionSignature(1 + "", res, orderType, obtainPayEncryptionSignature2);
                break;
            default:
                break;
        }
    }

    /**
     * 判断是否是手机格式
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {

//        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Pattern p = Pattern.compile("^[1][3578][0-9]{9}$");
        Matcher m = p.matcher(mobiles);

        return m.matches();

    }

    /**
     * 判断用户输入信息
     */
    private boolean inputInformation(String phone) {
        if (!isMobileNO(phone)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//判断如果删除的id和收货地址id相同，收货地址初始化
                        if (CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_SELECT_ID, "").equals(addressId)) {
                            tv_choose_obtain_address_number.setText("");
                            tv_choose_obtain_address.setText("");
                            tv_choose_obtain_address_person.setText("");
//                            tv_choose_obtain_address_number.setVisibility(View.GONE);
//                            tv_choose_obtain_address.setVisibility(View.GONE);
                            tv_choose_obtain_address_person.setVisibility(View.GONE);
                            tv_Prompt.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }.start();
        super.onResume();
//        tv_gold_balance = (TextView) findViewById(R.id.tv_gold_balance);
//        tv_gold_balance.setText("余额：" + CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_GOLD, "0") );
    }

    private class ObtainPayEncryptionSignature extends ObtainDataFromNetListener<String, String> {
        private int type = 0; //0支付宝 2微信支付
        private ChoosePayMethodActivity weak_activity;

        public ObtainPayEncryptionSignature(ChoosePayMethodActivity contextWeakReference, int type) {
            weak_activity = new WeakReference<>(contextWeakReference).get();
            this.type = type;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.show();
                weak_activity.mCustomLoadingDialog.setTitle(weak_activity.getResources().getString(R.string.order_comiting));
            }
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (type == 0) {
                            alipayActivity.payLastSteps(res);
                        } else if (type == 2) {
//                            Gson Gson = new Gson();
//                            PayReq payReq1 = Gson.fromJson(res, PayReq.class);
                            try {
                                JSONObject JSONObject = new JSONObject(res);
                                String appid = JSONObject.getString("appid");
                                String partnerid = JSONObject.getString("partnerid");
                                String prepayid = JSONObject.getString("prepayid");
                                String aPackage = JSONObject.getString("package");
                                String noncestr = JSONObject.getString("noncestr");
                                String timestamp = JSONObject.getString("timestamp");
                                String sign = JSONObject.getString("sign");
                                PayReq request = new PayReq();
                                //
                                request.appId = appid;

                                request.partnerId = partnerid;

                                request.prepayId = prepayid;

                                request.packageValue = aPackage;

                                request.nonceStr = noncestr;

                                request.timeStamp = timestamp;

                                request.sign = sign;
                                if (directBean.getdisproperty().equals("1")) {//优惠方式，1是正常，2是特价，3是秒杀，4是折扣
                                    new OrderPayAction(weak_activity, res, directBean.getTitle(), directBean.getActualPrice(), PartnerConfig
                                            .choosePayMethodActivity).payLastSteps(request);
                                } else {
                                    new OrderPayAction(weak_activity, res, directBean.getTitle(), Double.parseDouble(directBean.getDisPrice()), PartnerConfig
                                            .choosePayMethodActivity).payLastSteps(request);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        } else {
                            ToastUtils.showToast(res);
                        }
                    }
                });
            }
        }
    }


    private static class ObtainContextLister extends ObtainDataFromNetListener<String, String> {

        private ChoosePayMethodActivity weak_activity;

        public ObtainContextLister(ChoosePayMethodActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.context = res;
//                            weak_activity.tv_context.setText(Html.fromHtml(res));
                    }
                });
            }

        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);


                        }
                    }
                });
            }
        }
    }

    /**
     * 查看须知弹窗
     */

    public void Binding_iphone(View v) {
        View inflate = getLayoutInflater().inflate(R.layout.dialog_notice, null);
        final PopupWindow mPopupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(false);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        inflate.findViewById(R.id.iv_canle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        tv_context = (TextView) inflate.findViewById(R.id.tv_context);
        tv_context.setText(context);
        BitmapDrawable bitmapDrawable = new BitmapDrawable();
        mPopupWindow.setBackgroundDrawable(bitmapDrawable);
        mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 支付弹窗
     */
    public void PopPayment(View v) {
        View inflate = getLayoutInflater().inflate(R.layout.item_pop_payment, null);
        mPopupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(false);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        inflate.findViewById(R.id.rl_canle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        rl_alipy = (RelativeLayout) inflate.findViewById(R.id.rl_alipy);
        iv_alipy = (ImageView) inflate.findViewById(R.id.iv_iconForAlipy);
        rl_weixinpay = (RelativeLayout) inflate.findViewById(R.id.rl_weixinpay);
        rl_goldPay = (RelativeLayout) inflate.findViewById(R.id.rl_goldPay);
        rl_iconGold1 = (RelativeLayout) inflate.findViewById(R.id.rl_iconGold1);
        iv_iconForWeixinpay = (ImageView) inflate.findViewById(R.id.iv_iconForWeixinpay);
        iv_goldPay = (ImageView) inflate.findViewById(R.id.iv_iconForGoldPay);
        iv_goldPay1 = (ImageView) inflate.findViewById(R.id.iv_iconForGoldPay1);
        rl_pop_pay = (RelativeLayout) inflate.findViewById(R.id.rl_pop_pay);
        tv_gold_balance = (TextView) inflate.findViewById(R.id.tv_gold_balance);
        tv_gold_balance1 = (TextView) inflate.findViewById(R.id.tv_gold_balance1);
        TextView tv_pop_price = (TextView) inflate.findViewById(R.id.tv_pop_price);
        rl_goldPay1 = (RelativeLayout) inflate.findViewById(R.id.rl_goldPay1);
        if (directBean.getdisproperty().equals("1")||!"2".equals(directBean.getState())) {//优惠方式，1是正常，2是特价，3是秒杀，4是折扣
            tv_pop_price.setText("¥" + directBean.getActualPrice());//原本价格
        } else {
            tv_pop_price.setText("¥" + directBean.getDisPrice());//活动价格
        }
        if (directBean.getMostrcash() == 0) {//返现金额为0 说明没有返现反之有
            rl_goldPay1.setVisibility(View.VISIBLE);
            rl_goldPay.setVisibility(View.GONE);
        } else {
            rl_goldPay1.setVisibility(View.GONE);
            rl_goldPay.setVisibility(View.VISIBLE);
        }
        rl_alipy.setOnClickListener(this);
        rl_goldPay.setOnClickListener(this);
        rl_weixinpay.setOnClickListener(this);
        rl_pop_pay.setOnClickListener(this);
        rl_iconGold1.setOnClickListener(this);
        BitmapDrawable bitmapDrawable = new BitmapDrawable();
        mPopupWindow.setBackgroundDrawable(bitmapDrawable);
        mPopupWindow.showAtLocation(rl_pay, Gravity.BOTTOM, 0, 0);

    }

    /**
     * 设置textview 的drawable属性
     *
     * @param textView
     * @param drawableId 图片
     * @param text       内容
     * @param type       类型 0网课，显示高清视频 1直播或者录播课 显示时间
     */
    private void setDirectTime(TextView textView, int drawableId, String text, int type) {
        if (type == 0) {
            Drawable drawable = getResources().getDrawable(drawableId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
            textView.setCompoundDrawablePadding(CommonUtils.dip2px(6));
            textView.setTextColor(getResources().getColor(R.color.green004));
            textView.setText(Html.fromHtml(text));
        } else {
            Drawable drawable = getResources().getDrawable(drawableId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
            textView.setCompoundDrawablePadding(CommonUtils.dip2px(6));
            textView.setCompoundDrawables(drawable, null, null, null);
            textView.setTextColor(getResources().getColor(R.color.gray013));
            textView.setText(text);
        }

    }

}
