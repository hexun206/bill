package com.huatu.teacheronline.message.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.message.WebViewActivity;
import com.huatu.teacheronline.message.bean.SubscriptionBean;
import com.huatu.teacheronline.utils.FrescoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljzyuhenda on 15/3/21.
 */
public class SubscriptionAdapter extends BaseAdapter {

    private List<SubscriptionBean> beanList;
    private Activity context;

    public SubscriptionAdapter(Activity context) {
        this.context = context;
    }

    public void setBeanList(List<SubscriptionBean> list) {
        this.beanList = list;
    }

    @Override
    public int getCount() {
        if (beanList == null || beanList.size() == 0) {
            return 0;
        }
        return beanList.size();
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
        LinearLayout convertView_ll;
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.item_listview_subscriptionactivity, null);
            convertView_ll = (LinearLayout) convertView;
        } else {
            convertView_ll = (LinearLayout) convertView;
            convertView_ll.removeAllViews();
        }

        //添加时间
        View view_type0 = context.getLayoutInflater().inflate(R.layout.item_subscription_type0, null);
        TextView tv_time = (TextView) view_type0.findViewById(R.id.tv_listview_subscription_time);
        final SubscriptionBean bean = beanList.get(position);
        tv_time.setText(bean.getTime());
        convertView_ll.addView(view_type0);
        //添加白色区域
        LinearLayout view_sub = (LinearLayout) context.getLayoutInflater().inflate(R.layout.item_subscription_sub, null);
        convertView_ll.addView(view_sub);

        if (bean == null || bean.getList() == null) {
            return convertView;
        }
        for (int index = 0; index < bean.getList().size(); index++) {
            if ("1".equals(bean.getList().get(index).getPush_flag())) {
                View view_type1 = context.getLayoutInflater().inflate(R.layout.item_subscription_type1, null);
                TextView textView_type1 = (TextView) view_type1.findViewById(R.id.tv_listview_subscription_itemType1);
                textView_type1.setText(bean.getList().get(index).getPush_title());

                SimpleDraweeView imageView_type1 = (SimpleDraweeView) view_type1.findViewById(R.id.iv_listview_subscription_itemType1);
                GenericDraweeHierarchyBuilder builder =
                        new GenericDraweeHierarchyBuilder(context.getResources());
                GenericDraweeHierarchy hierarchy = builder
                        .setFadeDuration(100)
                        .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                        .setPlaceholderImage(context.getResources().getDrawable(R.drawable.zbxq_mrt), ScalingUtils.ScaleType.FIT_XY)
                        .setFailureImage(context.getResources().getDrawable(R.drawable.zbxq_mrt), ScalingUtils.ScaleType.FIT_XY)
                        .build();
                imageView_type1.setHierarchy(hierarchy);
                FrescoUtils.setFrescoImageUri(imageView_type1, bean.getList().get(index).getPush_pic(), R.drawable.zbxq_mrt);
                final int finalIndex = index;
                view_type1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startLiveActivity(bean.getList().get(finalIndex).getPush_url());
                    }
                });

                view_sub.addView(view_type1);
            } else {
                View view_type2 = context.getLayoutInflater().inflate(R.layout.item_subscription_type2, null);
                TextView textView_type2 = (TextView) view_type2.findViewById(R.id.tv_listview_subscription_itemType2);
                textView_type2.setText(bean.getList().get(index).getPush_title());

                SimpleDraweeView imageView_type2 = (SimpleDraweeView) view_type2.findViewById(R.id.iv_listview_subscription_itemType2);
                GenericDraweeHierarchyBuilder builder =
                        new GenericDraweeHierarchyBuilder(context.getResources());
                GenericDraweeHierarchy hierarchy = builder
                        .setFadeDuration(100)
                        .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                        .setPlaceholderImage(context.getResources().getDrawable(R.drawable.zb_mrt), ScalingUtils.ScaleType.FIT_XY)
                        .setFailureImage(context.getResources().getDrawable(R.drawable.zb_mrt), ScalingUtils.ScaleType.FIT_XY)
                        .build();
                imageView_type2.setHierarchy(hierarchy);
                if (!TextUtils.isEmpty(bean.getList().get(index).getPush_pic())) {
                    imageView_type2.setImageURI(Uri.parse(bean.getList().get(index).getPush_pic()));
                }
                final int finalIndex = index;
                view_type2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startLiveActivity(bean.getList().get(finalIndex).getPush_url());
                    }
                });

                view_sub.addView(view_type2);
            }
        }
        return convertView;
    }

    private void startLiveActivity(String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }
}
