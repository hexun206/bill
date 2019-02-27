package com.huatu.teacheronline.personal;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.H5DetailActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.bean.LogisticsBean;
import com.huatu.teacheronline.personal.bean.OrderBean;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 物流详情
 *
 * @author ljyu
 * @time 2016-8-9 10:55:53
 */
public class LogisticsDetailActivity extends BaseActivity {


    private TextView tv_statu_logdetail;
    private TextView tv_com_logdetail;
    private TextView tv_no_logdetail;
    private SimpleDraweeView img_pic_logdetail;
    private ListView listView;
    private TextView tv_title;
    private OrderBean orderBean;
    private LogisticsBean logisticsBean; //物流详情

    @Override
    public void initView() {
        setContentView(R.layout.activity_logistics_detail);
        orderBean = (OrderBean) getIntent().getSerializableExtra("OrderBean");
        tv_title = (TextView) findViewById(R.id.tv_main_title);
        tv_title.setText(R.string.logDetail);
        tv_statu_logdetail = (TextView) findViewById(R.id.tv_statu_logdetail);
        tv_com_logdetail = (TextView) findViewById(R.id.tv_com_logdetail);
        tv_no_logdetail = (TextView) findViewById(R.id.tv_no_logdetail);
        img_pic_logdetail = (SimpleDraweeView) findViewById(R.id.img_pic_logdetail);
        listView = (ListView) findViewById(R.id.listview);
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        img_pic_logdetail.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(img_pic_logdetail, orderBean.getScaleimg(), R.drawable.ic_loading);
        tv_no_logdetail.setText("运单编号：" + orderBean.getNu());
        loadData();
    }

    private void loadData() {
        ObtatinDataListener obtatinDataListener = new ObtatinDataListener(LogisticsDetailActivity.this);
        SendRequest.getLogisticsInfo(orderBean.getCom(), orderBean.getNu(), obtatinDataListener);

    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<LogisticsBean, String> {
        private LogisticsDetailActivity weak_activity;

        public ObtatinDataListener(LogisticsDetailActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final LogisticsBean res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushContent_OnSucess(res);
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

    private void flushContent_OnFailure(String res) {
    }

    private void flushContent_OnSucess(LogisticsBean res) {
        this.logisticsBean = res;
        List<LogisticsBean.DataBean> data = logisticsBean.getData();
        String textStr = "物流状态： <font color=\"#FF0000\">" + logisticsBean.getMessage() + "</font>";
        tv_statu_logdetail.setText(Html.fromHtml(textStr));
        tv_com_logdetail.setText("承运公司：" + logisticsBean.getCompanytype());
        tv_no_logdetail.setText("运单编号：" + logisticsBean.getNu());
        tv_statu_logdetail.setText("物流状态：" + logisticsBean.getMessage());
        LogDetailAdapter logDetailAdapter = new LogDetailAdapter(LogisticsDetailActivity.this, data);
        listView.setAdapter(logDetailAdapter);

    }

    @Override
    public void setListener() {
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        findViewById(R.id.rl_buttom_zixun).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                finish();
                break;
            case R.id.rl_buttom_zixun:
                if (logisticsBean == null) {
                    Toast.makeText(this, "数据加载中", Toast.LENGTH_SHORT).show();
                    return;
                }
                MobclickAgent.onEvent(this, "consultationOnClik");
                H5DetailActivity.newIntent(this, "咨询", logisticsBean.getCustomer());
                break;
        }
    }

    public static void newIntent(Context context, OrderBean orderBean) {
        Intent Intent = new Intent();
        Intent.putExtra("OrderBean", orderBean);
        Intent.setClass(context, LogisticsDetailActivity.class);
        context.startActivity(Intent);
    }

    class LogDetailAdapter extends BaseAdapter {


        private List<LogisticsBean.DataBean> data;
        private Context context;

        public LogDetailAdapter(Context context, List<LogisticsBean.DataBean> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_logdetail, null);
                holder.img_above_logdetail = (ImageView) convertView.findViewById(R.id.img_above_logdetail);
                holder.tv_info_logdetail = (TextView) convertView.findViewById(R.id.tv_info_logdetail);
                holder.tv_time_logdetail = (TextView) convertView.findViewById(R.id.tv_time_logdetail);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            LogisticsBean.DataBean dataBean = data.get(position);
            if (position == 0) {
                holder.img_above_logdetail.setImageResource(R.drawable.ic_wl_zuixin);
                holder.tv_info_logdetail.setTextColor(getResources().getColor(R.color.green004));
                holder.tv_time_logdetail.setTextColor(getResources().getColor(R.color.green004));
            } else if (position == data.size() - 1) {
                holder.img_above_logdetail.setImageResource(R.drawable.ic_wl_zuizao);
                holder.tv_info_logdetail.setTextColor(getResources().getColor(R.color.gray002));
                holder.tv_time_logdetail.setTextColor(getResources().getColor(R.color.gray002));
            } else {
                holder.img_above_logdetail.setImageResource(R.drawable.ic_wl_zhiqian);
                holder.tv_info_logdetail.setTextColor(getResources().getColor(R.color.gray002));
                holder.tv_time_logdetail.setTextColor(getResources().getColor(R.color.gray002));
            }
            holder.tv_info_logdetail.setText(dataBean.getContext());
            holder.tv_time_logdetail.setText(dataBean.getFtime());
            return convertView;
        }

        class ViewHolder {
            public ImageView img_above_logdetail;
            public TextView tv_info_logdetail;
            public TextView tv_time_logdetail;
        }
    }
}
