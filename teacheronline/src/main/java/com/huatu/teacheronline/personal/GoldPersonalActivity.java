package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gensee.utils.StringUtil;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.alipay.PartnerConfig;
import com.huatu.teacheronline.bean.PersonalInfoBean;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

/**
 * 金币账户
 * Created by ply on 2016/1/26.
 */
public class GoldPersonalActivity extends BaseActivity {
    private RelativeLayout rl_main_left, rl_main_right;
    private TextView tv_main_title, tv_main_right;
    private ImageView iv_main_right;
    private TextView tv_goldPay, tv_goldNumber;

    private ListView listView;
    private ObtainDataLister obtatinDataListener;
    private CustomAlertDialog mCustomLoadingDialog;
    private String uid;
    public static final int requestCode_GoldPersonalActivity = 1;
    private PopupWindow mPopWindow;
    private ImageView iv_alipy, iv_iconForWeixinpay, iv_goldPay;
    private ObtainExchangeGoldLister obtainExchangeGoldLister; //积分兑换成功
    private TextView tv_exchange; //可以兑换多少金币
    private TextView tv_integral;//多少分

    @Override
    public void initView() {
        setContentView(R.layout.activity_goldpersonal_new);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        mCustomLoadingDialog = new CustomAlertDialog(GoldPersonalActivity.this, R.layout.dialog_loading_custom);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_right = (TextView) findViewById(R.id.tv_main_right);
        iv_main_right = (ImageView) findViewById(R.id.ib_main_right);
        rl_main_right.setVisibility(View.VISIBLE);
        iv_main_right.setVisibility(View.GONE);
        tv_main_title.setText(R.string.goldPersonal);
        tv_main_right.setText(R.string.bill);
        tv_main_right.setVisibility(View.VISIBLE);

        tv_goldPay = (TextView) findViewById(R.id.tv_goldPay);
        tv_goldNumber = (TextView) findViewById(R.id.tv_goldNumber);
        tv_exchange = (TextView) findViewById(R.id.tv_exchange);
        tv_integral = (TextView) findViewById(R.id.tv_integral);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new listviewAdapter());
        listView.setFocusable(false);
        loadPersonalInfo();
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
        tv_goldPay.setOnClickListener(this);
        findViewById(R.id.btn_exchange_gold).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.rl_main_right://账单
                MobclickAgent.onEvent(this, "billOnclik");
                BillActivity.newIntent(this);
                break;
            case R.id.tv_goldPay://充值
                MobclickAgent.onEvent(this, "goldCoin");
                GoldRechargeActivity.newIntent(this, String.valueOf(requestCode_GoldPersonalActivity));
                break;
            case R.id.btn_exchange_gold://积分兑换
                MobclickAgent.onEvent(this, "exchangeGold");
                obtainExchangeGoldLister = new ObtainExchangeGoldLister(this);
                SendRequest.exchangeGold(uid, obtainExchangeGoldLister);
                break;

        }
    }


    public static void newIntent(Activity context) {
        Intent goldPersonIntent = new Intent(context, GoldPersonalActivity.class);
        context.startActivity(goldPersonIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode_GoldPersonalActivity == requestCode) {
            if (PartnerConfig.alipaySucess == resultCode) {
                loadPersonalInfo();
            }
        }
    }

    /**
     * 获取用户信息
     *
     * @param
     */
    public void loadPersonalInfo() {
        obtatinDataListener = new ObtainDataLister(this);
        SendRequest.getPersonalInfo(uid, obtatinDataListener);
    }

    private class ObtainDataLister extends ObtainDataFromNetListener<PersonalInfoBean, String> {

        private GoldPersonalActivity weak_activity;

        public ObtainDataLister(GoldPersonalActivity activity) {
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
        public void onSuccess(final PersonalInfoBean res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                if (res != null) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //更新金币信息
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_GOLD, res.getGold());
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_POINT, res.getUserPoint());
                            if (!StringUtil.isEmpty(res.getGold())) {
                                weak_activity.tv_goldNumber.setText(res.getGold());
                                weak_activity.tv_integral.setText(res.getUserPoint());
                                double v = Double.parseDouble(res.getUserPoint());
                                if (v > 200) {
                                    weak_activity.tv_exchange.setText("可以兑换"+(int)v/200+"金币");
                                }else {
                                    weak_activity.tv_exchange.setText("可以兑换"+0+"金币");
                                }
                            }
                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();
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
     * 活动描述适配器
     */
    public class listviewAdapter extends BaseAdapter {
        private String key[] = new String[]{
                "使用规则",
                "用于线上直播课、视频购买",
                "活动赠送",
                "绑定手机",
                "新手任务",
                "添加头像",
                "完成一次纠错",
                "首次推荐给微信好友",
                "首次推荐给qq好友",
                "首次分享到微信朋友圈",
                "首次分享到qq空间"};
        //本版不做
//        ,
//                "日常任务",
//                "完成一次定制练习",
//                "完成一套真题测试",
//                "分享1道题"
        private String value[] = new String[]{
                null,
                null,
                null,
                "领取1金币",
                null,
                "领取1金币",
                "领取1金币",
                "领取1金币",
                "领取1金币",
                "领取1金币",
                "领取1金币"};

        @Override
        public int getCount() {
            return value.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == 2 || position == 4) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == 0) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.goldactivity_title_layout, null);
                TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                tv_title.setText(key[position]);
            } else if (getItemViewType(position) == 1) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.goldactivity_item_title_layout, null);
                TextView tv_itemTitle = (TextView) convertView.findViewById(R.id.tv_item_title);
                TextView tv_itemDes = (TextView) convertView.findViewById(R.id.tv_item_des);
                tv_itemTitle.setText(key[position]);
                tv_itemDes.setText(value[position]);
            }
            return convertView;
        }
    }


    private class ObtainExchangeGoldLister extends ObtainDataFromNetListener<String, String> {

        private GoldPersonalActivity weak_activity;

        public ObtainExchangeGoldLister(GoldPersonalActivity activity) {
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
                            ToastUtils.showToast(res);
                            weak_activity.loadPersonalInfo();
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
}
