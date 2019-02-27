package com.huatu.teacheronline.personal.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gensee.utils.StringUtil;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.bean.OrderBean;
import com.huatu.teacheronline.sensorsdata.TrackUtil;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by 79937 on 2016/8/9.
 */
public class OrderAdapter extends BaseAdapter {

    private View.OnClickListener onClickListener;
    private Context context;
    private ArrayList<OrderBean> orderBeanList;

    public OrderAdapter(Context context, View.OnClickListener onClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
    }


    @Override
    public int getCount() {
        return (orderBeanList == null ? 0 : orderBeanList.size());
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_myorder, null);
            holder.img_pic_order = (SimpleDraweeView) convertView.findViewById(R.id.img_pic_order);
            holder.img_over_order = (ImageView) convertView.findViewById(R.id.img_over_order);
            holder.tv_data_order = (TextView) convertView.findViewById(R.id.tv_data_order);
            holder.tv_orderno = (TextView) convertView.findViewById(R.id.tv_orderno);
            holder.tv_title_order = (TextView) convertView.findViewById(R.id.tv_title_order);
            holder.tv_price_order = (TextView) convertView.findViewById(R.id.tv_price_order);
            holder.tv_adjust_price_order = (TextView) convertView.findViewById(R.id.tv_adjust_price_order);
            holder.btn_logistics_order = (TextView) convertView.findViewById(R.id.btn_logistics_order);
            holder.btn_cacle_order = (TextView) convertView.findViewById(R.id.btn_cacle_order);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        OrderBean orderBean = orderBeanList.get(position);
        holder.tv_data_order.setText("有效期：" + orderBean.getPay_time() + "-" + orderBean.getOverdue_time());
//        if("0".equals(orderBean.getActualPrice())||StringUtil.isEmpty(orderBean.getOrderNo())){
//            holder.tv_orderno.setText("订单号："+orderBean.getOrderNo());
//        }else {
        holder.tv_orderno.setText("订单号：" + orderBean.getOrderNo());
//        }
        holder.tv_title_order.setText(orderBean.getTitle());
        if (StringUtils.isEmpty(orderBean.getAdjust_price())) {
            holder.tv_adjust_price_order.setVisibility(View.GONE);
            holder.tv_price_order.setText("¥ " + orderBean.getActualPrice());
        } else {
            holder.tv_adjust_price_order.setVisibility(View.VISIBLE);
            holder.tv_price_order.setText("¥ " + orderBean.getAdjust_price());
            holder.tv_adjust_price_order.setText("¥ " + orderBean.getActualPrice());
            holder.tv_adjust_price_order.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (orderBean.getIs_overdue().equals("-1")) {
            holder.img_over_order.setVisibility(View.VISIBLE);
        } else {
            holder.img_over_order.setVisibility(View.GONE);
        }

        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(context.getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        holder.img_pic_order.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(holder.img_pic_order, orderBean.getScaleimg(), R.drawable.ic_loading);

        String trackContent = "$element_content";
        if (orderBean.getPay_type().equals("1")) {
            //已付款
            holder.btn_logistics_order.setText(R.string.immediately_learn);
            trackContent = "马上学习";
            holder.btn_logistics_order.setTextColor(context.getResources().getColor(R.color.white));
            holder.btn_logistics_order.setBackgroundResource(R.drawable.bg_rectangle_green3);
            if (orderBean.getJsstatus().equals("0") || orderBean.getJsstatus().equals("-1") || orderBean.getIs_overdue().equals("-1")) {
                //无讲义 或者 教师网 暂不支持查看
                holder.btn_cacle_order.setVisibility(View.GONE);
                if (orderBean.getIs_overdue().equals("-1")) {
                    holder.btn_logistics_order.setBackgroundResource(R.drawable.bg_rectangle_frame_red);
                    holder.btn_logistics_order.setText(R.string.deletecn);
                    holder.btn_logistics_order.setTextColor(context.getResources().getColor(R.color.red001));
                }
            } else {
                holder.btn_cacle_order.setVisibility(View.VISIBLE);
                //有讲义
                if (orderBean.getJsstatus().equals("2") && !StringUtil.isEmpty(orderBean.getNu())) {
                    //有物流
                    holder.btn_cacle_order.setBackgroundResource(R.drawable.bg_rectangle_frame_green);
                    holder.btn_cacle_order.setText(R.string.logistics);
                    holder.btn_cacle_order.setTextColor(context.getResources().getColor(R.color.green001));
                } else {
                    //无物流
                    holder.btn_cacle_order.setBackgroundResource(R.drawable.bg_rectangle_frame_gray);
                    holder.btn_cacle_order.setText(R.string.logisticsnull);
                    holder.btn_cacle_order.setTextColor(context.getResources().getColor(R.color.gray013));
                }
            }
        } else {
            //未付款
            holder.btn_logistics_order.setText(R.string.immediately_pay);
            trackContent = "马上付款";
            holder.btn_logistics_order.setTextColor(context.getResources().getColor(R.color.white));
            holder.btn_logistics_order.setBackgroundResource(R.drawable.bg_rectangle_green3);

            holder.btn_cacle_order.setVisibility(View.VISIBLE);
            holder.btn_cacle_order.setBackgroundResource(R.drawable.bg_rectangle_frame_green);
            holder.btn_cacle_order.setText(R.string.cacle_order);
            holder.btn_cacle_order.setTextColor(context.getResources().getColor(R.color.green001));
        }
        holder.btn_logistics_order.setTag(orderBean);
        holder.btn_logistics_order.setOnClickListener(onClickListener);
        holder.btn_cacle_order.setTag(orderBean);
        holder.btn_cacle_order.setOnClickListener(onClickListener);


        //神策track click
        TrackUtil.trackClick(context, holder.btn_logistics_order, TrackUtil.TYPE_BUTTON, trackContent);
        TrackUtil.trackClick(context, holder.btn_logistics_order, TrackUtil.TYPE_BUTTON, trackContent);
        TrackUtil.trackClick(context, holder.btn_cacle_order, TrackUtil.TYPE_BUTTON, "马上付款");


        return convertView;
    }

    class ViewHolder {
        public SimpleDraweeView img_pic_order;
        public TextView tv_data_order;
        public TextView tv_orderno;
        public TextView tv_title_order;
        public TextView btn_logistics_order;
        public TextView btn_cacle_order;
        public TextView tv_price_order;
        public TextView tv_adjust_price_order;
        public ImageView img_over_order;
    }

    public void setOrderBeanList(ArrayList<OrderBean> orderBeanList) {
        this.orderBeanList = orderBeanList;
    }
}
