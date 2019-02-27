package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gensee.utils.StringUtil;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.alipay.AlipayActivity;
import com.huatu.teacheronline.alipay.PartnerConfig;
import com.huatu.teacheronline.direct.DirectDetailsActivity;
import com.huatu.teacheronline.direct.TestChatActivity;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.adapter.OrderAdapter;
import com.huatu.teacheronline.personal.bean.OrderBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.TeacherOnlineUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.swipemenulistview.SwipeMenuListView;
import com.huatu.teacheronline.wxpay.OrderPayAction;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.umeng.analytics.MobclickAgent;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cn.xiaoneng.coreapi.ChatParamsBody;
import cn.xiaoneng.uiapi.Ntalker;

/**
 * 我的订单
 *
 * @author ljyu
 * @time 2016-8-8 16:47:45
 */
public class MyOrderActivity extends BaseActivity {
//    private String   settingid1="kf_10092_1513839603881";
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;
    private View loadView;
    private View loadIcon;
    private RotateAnimation refreshingAnimation;
    private OrderAdapter adapter;
    private SwipeRefreshLayout mPullToRefreshLayout;
    private SwipeMenuListView listView;
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private int currentPage = 1;
    private int isNew = 1;//是否新版本
    private int limit = 8;
    private String uid = "";
    private ObtatinMyOrderListener obtatinDataListener;
    private boolean isReset = true;
    private ArrayList<OrderBean> directBeanList = new ArrayList<OrderBean>();
    private PercentRelativeLayout rl_nodata;
    private CustomAlertDialog mCustomLoadingDialog;
    private ObtainCancleOrderLister obtainCancleOrderLister; //取消订单
    private AlertDialog alertDialog; //金币弹出框
    private ImageView iv_alipy, iv_iconForWeixinpay, iv_goldPay;
    private TextView tv_pay;//去支付
    private int payType = 0; //支付方式： 0支付宝 1微信 2金币 支付
    private final String orderType = "2"; //订单类型 1 金币充值订单 2 购买课程订单
    private AlipayActivity alipayActivity;
    private ObtainGoldPayLister obtainGoldPayLister; //金币支付
    public static String customer;//金币支付
    public static String branchschoolid;//小能id
    private ImageView ib_main_right; //标题右边图片
    private CustomAlertDialog mCustomCancleDilog;// 删除或者取消订单
    private boolean isCancle = true;//是否删除订单
    private TeacherOnlineUtils teacherOnlineUtils;


    @Override
    public void initView() {
        setContentView(R.layout.activity_my_order);
        setRefreshIntentFilter();
        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        mCustomCancleDilog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
        alipayActivity = new AlipayActivity(this, PartnerConfig.myOrderActivity);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        ib_main_right = (ImageView) findViewById(R.id.ib_main_right);
        ib_main_right.setImageResource(R.drawable.consult_selector);
        ib_main_right.setVisibility(View.GONE);
        rl_nodata = (PercentRelativeLayout) findViewById(R.id.rl_nodata);
        tv_main_title.setText(R.string.my_order);
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        adapter = new OrderAdapter(this, this);
        mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.live_refresh_layout);
        listView = (SwipeMenuListView) findViewById(R.id.listview);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        listView.setAdapter(adapter);
        loadData(true);
        teacherOnlineUtils = new TeacherOnlineUtils(this);
    }

    private void loadData(boolean isReset) {
        if (isReset) {
            currentPage = 1;
        } else {
            currentPage++;
        }
        obtatinDataListener = new ObtatinMyOrderListener(isReset, this);
        SendRequest.getMyOrder(limit + "", currentPage + "", uid, isNew + "", obtatinDataListener);
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
//        ib_main_right.setOnClickListener(this);
        mCustomCancleDilog.setOkOnClickListener(this);
        mCustomCancleDilog.setCancelOnClickListener(this);
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(true);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (isLoadEnd) {
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            listView.addFooterView(loadView);
                            loadIcon.startAnimation(refreshingAnimation);
                            loadData(false);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                finish();
                break;
            case R.id.tv_dialog_cancel:
                //取消
                mCustomCancleDilog.dismiss();
                break;
            case R.id.tv_dialog_ok:
                //删除订单
                mCustomCancleDilog.dismiss();
                OrderBean orderBeanCancle = (OrderBean) mCustomCancleDilog.getTag();
                obtainCancleOrderLister = new ObtainCancleOrderLister(this);
                SendRequest.cancleOrder(uid, orderBeanCancle.getOrderNo(), orderBeanCancle.getRid(),obtainCancleOrderLister);
                break;
            case R.id.ib_main_right:
                MobclickAgent.onEvent(this, "consultationOnClik");
//                H5DetailActivity.newIntent(this, "咨询", customer);
                if (branchschoolid==null||branchschoolid.equals("")){
                    return;
                }
                //小能登录
                Ntalker.getBaseInstance().login(uid, CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, ""), 0);
                ChatParamsBody  chatparams = new ChatParamsBody();
                chatparams.headurl=CommonUtils.getSharedPreferenceItem(null,UserInfo.KEY_SP_FACEPATH,"");
                Ntalker.getBaseInstance().startChat(this, branchschoolid, "华图客服", chatparams, TestChatActivity.class);
                break;
            case R.id.btn_cacle_order:
                OrderBean orderBean = (OrderBean) v.getTag();
                if (orderBean.getPay_type().equals("1")) {
                    //已付款 查看物流  尚未发货

                    //有讲义
                    if (orderBean.getJsstatus().equals("2") && !StringUtil.isEmpty(orderBean.getNu())) {
                        //有物流 查看物流
                        LogisticsDetailActivity.newIntent(MyOrderActivity.this, orderBean);
                    } else {
                        //无物流 尚未发货
                    }
                } else {
                    //未付款 取消订单

                    isCancle = true;
                    mCustomCancleDilog.show();
                    mCustomCancleDilog.setTag(orderBean);
                    mCustomCancleDilog.setTitle("确定取消订单？");
                }

                break;
            case R.id.btn_logistics_order:
                OrderBean orderBean1 = (OrderBean) v.getTag();
                if (orderBean1.getPay_type().equals("1")) {
                    if (orderBean1.getIs_overdue().equals("-1")) {
                        //已付款 过期 删除按钮
                        isCancle = false;
                        mCustomCancleDilog.show();
                        mCustomCancleDilog.setTag(orderBean1);
                        mCustomCancleDilog.setTitle("确定删除订单？");
                    } else {
                        //已付款  未过期 马上学习
                        DirectDetailsActivity.newIntent(this, orderBean1.getRid());
                    }
                } else {
                    // 马上付款 按钮
                    payType = 0;
                    alertDialogToChoosePayMethod(orderBean1);
                }
                break;

            case R.id.rl_alipy:
            case R.id.rl_iconAlipy:
                MobclickAgent.onEvent(this, "alipayPayOnClik");
                iv_alipy.setImageResource(R.drawable.rance_select_paymethod);
                iv_iconForWeixinpay.setImageResource(R.drawable.rance_noselect_paymethod);
                iv_goldPay.setImageResource(R.drawable.rance_noselect_paymethod);
                payType = 0;
                break;
            case R.id.rl_weixinpay:
            case R.id.rl_iconWeixinpay:
                MobclickAgent.onEvent(this, "weChatPayOnClik");
                iv_alipy.setImageResource(R.drawable.rance_noselect_paymethod);
                iv_goldPay.setImageResource(R.drawable.rance_noselect_paymethod);
                iv_iconForWeixinpay.setImageResource(R.drawable.rance_select_paymethod);
                payType = 1;
                break;
            case R.id.rl_goldpay:
            case R.id.rl_icongoldpay:
                MobclickAgent.onEvent(this, "Goldpayment");//计数金币
                iv_iconForWeixinpay.setImageResource(R.drawable.rance_noselect_paymethod);
                iv_alipy.setImageResource(R.drawable.rance_noselect_paymethod);
                iv_goldPay.setImageResource(R.drawable.rance_select_paymethod);
                payType = 2;
                break;
            case R.id.tv_pay://去支付
                alertDialog.dismiss();
                MobclickAgent.onEvent(this, "toPayOnClik");
                OrderBean orderBeanpay = (OrderBean) v.getTag();
                orderPaymethod(orderBeanpay);
                break;
        }
    }

    private static class ObtatinMyOrderListener extends ObtainDataFromNetListener<ArrayList<OrderBean>, String> {
        private MyOrderActivity weak_activity;
        private boolean isReset;

        public ObtatinMyOrderListener(Boolean isReset, MyOrderActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.isLoadEnd = false;
                if (isReset) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weak_activity.clearAdapete();
                        }
                    });
                }
            }
        }

        @Override
        public void onSuccess(final ArrayList<OrderBean> res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushContent_OnSucess(isReset, res);
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushContent_OnFailure(res);
                    }
                });
            }
        }

    }

    public void flushContent_OnSucess(boolean isReset, ArrayList<OrderBean> res) {
        if (isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
//            ToastUtils.showToast(R.string.no_data);
            rl_nodata.setVisibility(View.VISIBLE);
        } else if (!isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_more);
        } else {
            hasMoreData = true;
            directBeanList.addAll(res);
        }
        completeRefresh();
    }

    public void flushContent_OnFailure(String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
        } else if (SendRequest.ERROR_SERVER.equals(res)) {
            ToastUtils.showToast(R.string.server_error);
        }
        completeRefresh();
    }

    /**
     * 上拉刷新
     */
    private void completeRefresh() {
        mPullToRefreshLayout.setRefreshing(false);
        if (listView != null && listView.getFooterViewsCount() > 0) {
            listView.removeFooterView(loadView);
        }

        if (adapter != null) {
            adapter.setOrderBeanList(directBeanList);
            adapter.notifyDataSetChanged();
        }

        if (hasMoreData) {
            isLoadEnd = true;
        } else {
            isLoadEnd = false;
        }
    }

    /**
     * 清空适配器
     */
    public void clearAdapete() {
        directBeanList.clear();
        adapter.setOrderBeanList(directBeanList);
        adapter.notifyDataSetChanged();
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, MyOrderActivity.class);
        context.startActivity(intent);
    }

    private class ObtainCancleOrderLister extends ObtainDataFromNetListener<String, String> {

        private MyOrderActivity weak_activity;

        public ObtainCancleOrderLister(MyOrderActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.show();
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
                            if (weak_activity.isCancle) {
                                ToastUtils.showToast("订单取消成功");
                            }else {
                                ToastUtils.showToast("订单删除成功");
                            }
                            listView.addFooterView(loadView);
                            loadIcon.startAnimation(refreshingAnimation);
                            weak_activity.loadData(true);
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

    /**
     * 订单支付弹出框
     */
    private void alertDialogToChoosePayMethod(OrderBean orderBean) {

        if ("1".equals(orderBean.getIsTimeout())) {
            Toast.makeText(this, "该课程已过期,无法购买!", Toast.LENGTH_SHORT).show();
            return;
        }


        alertDialog = new AlertDialog.Builder(MyOrderActivity.this).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        alertDialog.setCanceledOnTouchOutside(true);
        window.setContentView(R.layout.popwindow_choose_paymethod);
        window.findViewById(R.id.rl_weixinpay).setOnClickListener(this);
        window.findViewById(R.id.rl_goldpay).setOnClickListener(this);
        View rl_goldpay = window.findViewById(R.id.rl_goldpay);
        rl_goldpay.setVisibility(View.VISIBLE);
        rl_goldpay.setOnClickListener(this);
        window.findViewById(R.id.rl_alipy).setOnClickListener(this);
        window.findViewById(R.id.rl_iconAlipy).setOnClickListener(this);
        window.findViewById(R.id.rl_iconWeixinpay).setOnClickListener(this);
        window.findViewById(R.id.rl_icongoldpay).setOnClickListener(this);
        iv_alipy = (ImageView) window.findViewById(R.id.iv_iconForAlipy);
        iv_iconForWeixinpay = (ImageView) window.findViewById(R.id.iv_iconForWeixinpay);
        iv_goldPay = (ImageView) window.findViewById(R.id.iv_iconForGoldpay);
        tv_pay = (TextView) window.findViewById(R.id.tv_pay);
        TextView tv_gold_balance = (TextView) window.findViewById(R.id.tv_gold_balance);
        tv_gold_balance.setText("（余额：" + CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_GOLD, "0") + ")");
//        DebugUtil.e("余额："+CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_GOLD, "0"));
        tv_pay.setTag(orderBean);
        tv_pay.setOnClickListener(this);
    }

    /**
     * 去支付
     *
     * @param orderBean 订单信息
     */
    private void orderPaymethod(OrderBean orderBean) {
        switch (payType) {
            case 0://支付宝
//                alipayActivity.pay(directBean.getTitle(), this
//                                .directBean.getActualPrice(), "1",
//                        res, PartnerConfig.SELLER, PartnerConfig.NOTIFY_URL_course);
                ObtainPayEncryptionSignature obtainPayEncryptionSignature = new ObtainPayEncryptionSignature(this, 0, orderBean);
                SendRequest.getPayEncryptionSignature(2 + "", orderBean.getOrderNo(), orderType, obtainPayEncryptionSignature);
                break;
            case 2://金币支付
//                toPayForGold();
                obtainGoldPayLister = new ObtainGoldPayLister(this);
                if (StringUtils.isEmpty(orderBean.getAdjust_price())) {
                    SendRequest.toPayForGold(uid, orderBean.getOrderNo(), Double.valueOf(orderBean.getActualPrice()), "1", obtainGoldPayLister);
                }else {
                    SendRequest.toPayForGold(uid, orderBean.getOrderNo(), Double.valueOf(orderBean.getAdjust_price()), "1", obtainGoldPayLister);
                }
                break;
            case 1://微信支付
//                new OrderPayAction(this, res, directBean.getTitle(), directBean.getActualPrice(), PartnerConfig.choosePayMethodActivity).orderPay();
                ObtainPayEncryptionSignature obtainPayEncryptionSignature2 = new ObtainPayEncryptionSignature(this, 2, orderBean);
                SendRequest.getPayEncryptionSignature(1 + "", orderBean.getOrderNo(), orderType, obtainPayEncryptionSignature2);
                break;
            default:
                break;
        }
    }

    private class ObtainPayEncryptionSignature extends ObtainDataFromNetListener<String, String> {
        private OrderBean orderBean;
        private int type = 0; //0支付宝 2微信支付
        private MyOrderActivity weak_activity;

        public ObtainPayEncryptionSignature(MyOrderActivity contextWeakReference, int type, OrderBean orderBean) {
            weak_activity = new WeakReference<>(contextWeakReference).get();
            this.type = type;
            this.orderBean = orderBean;
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
                            weak_activity.alipayActivity.payLastSteps(res);
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
                                new OrderPayAction(weak_activity, res, orderBean.getTitle(), Double.valueOf(orderBean.getActualPrice()), PartnerConfig
                                        .myOrderActivity).payLastSteps(request);
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
                        }else {
                            ToastUtils.showToast(res);
                        }
                    }
                });
            }
        }
    }

    private class ObtainGoldPayLister extends ObtainDataFromNetListener<String, String> {

        private MyOrderActivity weak_activity;

        public ObtainGoldPayLister(MyOrderActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.show();
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
                            if ("1".equals(res)) {//支付成功
                                ToastUtils.showToast(R.string.pay_sucess);
                                weak_activity.listView.addFooterView(weak_activity.loadView);
                                weak_activity.loadIcon.startAnimation(weak_activity.refreshingAnimation);
                                weak_activity.loadData(true);
//                                MainActivity.newIntentFlag(weak_activity, "ToMyDirectActivity");
                            } else if ("2".equals(res)) {//金币不足
                                weak_activity.showDialogForRecharge();
                            } else {
                                ToastUtils.showToast(res);
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
                        }else {
                            ToastUtils.showToast(res);
                        }
                    }
                });
            }
        }
    }

    /**
     * 充值对话框
     */
    public void showDialogForRecharge() {
        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(this, R.layout.pay_method_dialog_layout);
        customAlertDialog.show();
        customAlertDialog.setOkOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                customAlertDialog.dismiss();
                MobclickAgent.onEvent(MyOrderActivity.this, "goldCoin");
                GoldRechargeActivity.newIntent(MyOrderActivity.this, String.valueOf(GoldPersonalActivity.requestCode_GoldPersonalActivity));
            }
        });
        customAlertDialog.setCancelOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                customAlertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customer = null;
        branchschoolid=null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        teacherOnlineUtils.loadPersonalInfo(this);
    }

    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        loadData(true);
    }
}
