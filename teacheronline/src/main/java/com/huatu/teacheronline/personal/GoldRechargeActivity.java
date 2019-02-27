package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.alipay.AlipayActivity;
import com.huatu.teacheronline.alipay.PartnerConfig;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.wxpay.OrderPayAction;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 金币充值
 * Created by ply on 2016/1/26.
 */
public class GoldRechargeActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;

    private RelativeLayout rl_one, rl_two, rl_three, rl_four, rl_five, rl_six;
    private TextView tv_moneyOne, tv_moneyTwo, tv_moneyThree, tv_moneyFour, tv_moneyFive, tv_moneySix;
    private TextView tv_goldOne, tv_goldTwo, tv_goldThree, tv_goldFour, tv_goldFive, tv_goldSix;
    private List<RelativeLayout> rl_list = new ArrayList<>();
    private List<TextView> moneyList = new ArrayList<>();
    private List<TextView> goldList = new ArrayList<>();
    private TextView tv_sumMoney, tv_sumGold;
    private TextView tv_recharge;

    private CustomAlertDialog mCustomLoadingDialog;
    private AlipayActivity alipayActivity;
    private String uid;
    private PopupWindow mPopWindow;
    private ImageView iv_alipy, iv_iconForWeixinpay, iv_goldPay;
    private TextView tv_pay;//去支付
    private int payType = 0;//支付类型 0 alipay  1  wxpay
    private AlertDialog alertDialog;
    private final String orderType = "1"; //订单类型 1 金币充值订单 2 购买课程订单

    @Override
    public void initView() {
        setContentView(R.layout.activity_goldrecharge);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        mCustomLoadingDialog = new CustomAlertDialog(GoldRechargeActivity.this, R.layout.dialog_loading_custom);
        alipayActivity = new AlipayActivity(this, PartnerConfig.goldRechargeActivity);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.goldRecharge);
        tv_recharge = (TextView) findViewById(R.id.tv_recharge);
        tv_sumMoney = (TextView) findViewById(R.id.tv_sumMoney);
        tv_sumGold = (TextView) findViewById(R.id.tv_sumGold);
        rl_one = (RelativeLayout) findViewById(R.id.rl_one);
        rl_two = (RelativeLayout) findViewById(R.id.rl_two);
        rl_three = (RelativeLayout) findViewById(R.id.rl_three);
        rl_four = (RelativeLayout) findViewById(R.id.rl_four);
        rl_five = (RelativeLayout) findViewById(R.id.rl_five);
        rl_six = (RelativeLayout) findViewById(R.id.rl_six);
        tv_moneyOne = (TextView) findViewById(R.id.tv_money_one);
        tv_moneyTwo = (TextView) findViewById(R.id.tv_money_two);
        tv_moneyThree = (TextView) findViewById(R.id.tv_money_three);
        tv_moneyFour = (TextView) findViewById(R.id.tv_money_four);
        tv_moneyFive = (TextView) findViewById(R.id.tv_money_five);
        tv_moneySix = (TextView) findViewById(R.id.tv_money_six);
        tv_goldOne = (TextView) findViewById(R.id.tv_goldNumber_one);
        tv_goldTwo = (TextView) findViewById(R.id.tv_goldNumber_two);
        tv_goldThree = (TextView) findViewById(R.id.tv_goldNumber_three);
        tv_goldFour = (TextView) findViewById(R.id.tv_goldNumber_four);
        tv_goldFive = (TextView) findViewById(R.id.tv_goldNumber_five);
        tv_goldSix = (TextView) findViewById(R.id.tv_goldNumber_six);
        rl_list.add(rl_one);
        rl_list.add(rl_two);
        rl_list.add(rl_three);
        rl_list.add(rl_four);
        rl_list.add(rl_five);
        rl_list.add(rl_six);
        moneyList.add(tv_moneyOne);
        moneyList.add(tv_moneyTwo);
        moneyList.add(tv_moneyThree);
        moneyList.add(tv_moneyFour);
        moneyList.add(tv_moneyFive);
        moneyList.add(tv_moneySix);
        goldList.add(tv_goldOne);
        goldList.add(tv_goldTwo);
        goldList.add(tv_goldThree);
        goldList.add(tv_goldFour);
        goldList.add(tv_goldFive);
        goldList.add(tv_goldSix);
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        rl_one.setOnClickListener(this);
        rl_two.setOnClickListener(this);
        rl_three.setOnClickListener(this);
        rl_four.setOnClickListener(this);
        rl_five.setOnClickListener(this);
        rl_six.setOnClickListener(this);
        tv_recharge.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.rl_alipy:
            case R.id.rl_iconAlipy:
                MobclickAgent.onEvent(this, "alipayPayOnClik");
                iv_alipy.setImageResource(R.drawable.rance_select_paymethod);
                iv_iconForWeixinpay.setImageResource(R.drawable.rance_noselect_paymethod);
                payType = 0;
                break;
            case R.id.rl_weixinpay:
            case R.id.rl_iconWeixinpay:
                MobclickAgent.onEvent(this, "weChatPayOnClik");
                iv_alipy.setImageResource(R.drawable.rance_noselect_paymethod);
                iv_iconForWeixinpay.setImageResource(R.drawable.rance_select_paymethod);
                payType = 1;
                break;
            case R.id.tv_recharge://确认充值弹出框
                MobclickAgent.onEvent(this, "rechargeConfirmation");
                payType = 0;
                alertDialogToChoosePayMethod();
                break;
            case R.id.tv_pay://去支付
                alertDialog.dismiss();
                MobclickAgent.onEvent(this, "toPayOnClik");
                setRecharge();
                break;
            case R.id.rl_one:
                changeSelectGoldColor(0);
                break;
            case R.id.rl_two:
                changeSelectGoldColor(1);
                break;
            case R.id.rl_three:
                changeSelectGoldColor(2);
                break;
            case R.id.rl_four:
                changeSelectGoldColor(3);
                break;
            case R.id.rl_five:
                changeSelectGoldColor(4);
                break;
            case R.id.rl_six:
                changeSelectGoldColor(5);
                break;
        }
    }


    /**
     * 金币充值选择弹出框
     */
    private void alertDialogToChoosePayMethod() {
        alertDialog = new AlertDialog.Builder(GoldRechargeActivity.this).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        alertDialog.setCanceledOnTouchOutside(true);
        window.setContentView(R.layout.popwindow_choose_paymethod);
        window.findViewById(R.id.rl_weixinpay).setOnClickListener(this);
        window.findViewById(R.id.rl_alipy).setOnClickListener(this);
        window.findViewById(R.id.rl_iconAlipy).setOnClickListener(this);
        window.findViewById(R.id.rl_iconWeixinpay).setOnClickListener(this);
        iv_alipy = (ImageView) window.findViewById(R.id.iv_iconForAlipy);
        iv_iconForWeixinpay = (ImageView) window.findViewById(R.id.iv_iconForWeixinpay);
        tv_pay = (TextView) window.findViewById(R.id.tv_pay);
        tv_pay.setOnClickListener(this);
    }

    /**
     * 跳转到充值页
     *
     * @param context
     * @param requestCode 跳转充值页时的requestCode
     */
    public static void newIntent(Activity context, String requestCode) {
        Intent intent = new Intent(context, GoldRechargeActivity.class);
        if (TextUtils.isEmpty(requestCode)) {
            context.startActivity(intent);
        } else {
            context.startActivityForResult(intent, Integer.parseInt(requestCode));
        }

    }

    /**
     * 改变选中的充值金额布局
     *
     * @param index
     */
    public void changeSelectGoldColor(int index) {
        for (int i = 0; i < rl_list.size(); i++) {
            if (index == i) {
                rl_list.get(i).setBackgroundResource(R.drawable.bg_rectangle_frame_green);
                moneyList.get(i).setTextColor(getResources().getColor(R.color.green001));
                goldList.get(i).setTextColor(getResources().getColor(R.color.green001));
                tv_sumMoney.setText(moneyList.get(i).getText().toString().replace("￥", ""));
                tv_sumGold.setText(goldList.get(i).getText().toString().replace("金币", ""));
            } else {
                rl_list.get(i).setBackgroundResource(R.drawable.bg_rectangle_frame_gray);
                moneyList.get(i).setTextColor(getResources().getColor(R.color.gray014));
                goldList.get(i).setTextColor(getResources().getColor(R.color.gray014));
            }

        }
    }

    /**
     * 充值金币生成订单
     *
     * @param
     */
    public void setRecharge() {
//        obtatinDataListener = new ObtainDataLister(this);
        if (payType == 0) {
            //支付宝
            //                alipayActivity.pay("金币充值", Double.parseDouble(tv_sumMoney.getText().toString()), "0", res, PartnerConfig.SELLER, PartnerConfig
//                        .NOTIFY_URL_course);
            ObtainPayEncryptionSignature obtainPayEncryptionSignature = new ObtainPayEncryptionSignature(this,0);
            SendRequest.createOrderForGold(uid, tv_sumGold.getText().toString()+"", "2", obtainPayEncryptionSignature);
        }else if(payType == 1){
            //微信支付
//                OrderPayAction OrderPayAction = new OrderPayAction(GoldRechargeActivity.this, res, "金币充值", Double.parseDouble(tv_sumMoney.getText().toString
//                        ()), PartnerConfig.goldRechargeActivity);
//                OrderPayAction.orderPay();
            ObtainPayEncryptionSignature obtainPayEncryptionSignature = new ObtainPayEncryptionSignature(this,2);
            SendRequest.createOrderForGold(uid, tv_sumGold.getText().toString()+"", "1", obtainPayEncryptionSignature);
        }

    }

    private class ObtainPayEncryptionSignature extends ObtainDataFromNetListener<String, String> {
        private int type = 0; //0支付宝 2微信支付
        private GoldRechargeActivity weak_activity;

        public ObtainPayEncryptionSignature(GoldRechargeActivity contextWeakReference, int type) {
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
                                new OrderPayAction(weak_activity, res, "金币充值", Double.parseDouble(tv_sumMoney.getText().toString()), PartnerConfig
                                        .goldRechargeActivity).payLastSteps(request);
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
                        }
                    }
                });
            }
        }
    }
}