package com.huatu.teacheronline.direct.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import com.facebook.drawee.drawable.ScalingUtils;
//import com.facebook.drawee.generic.GenericDraweeHierarchy;
//import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
//import com.facebook.drawee.view.SimpleDraweeView;
//import com.huatu.handheld_huatu.R;
//import com.huatu.handheld_huatu.entity.VODBean;
//import com.huatu.handheld_huatu.utils.CommonUtils;
//import com.huatu.handheld_huatu.utils.FrescoUtils;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.greendao.DirectBean;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.FrescoUtils;

import java.util.List;

/**
 * 直播模块全部，我的，适配器
 * Created by ply on 2016/1/5.
 */
public class DirectListAdapter extends BaseAdapter {
    private int type; //0 全部 或者查询  1 我的
    private int videoType; //0 高清网课 1 直播  2 教辅资料
    public Context context;
    private List<DirectBean> directBeanList;

    public DirectListAdapter(Context context) {
        this.context = context;
    }
    public DirectListAdapter(Context context,int type,int videoType) {
        this.context = context;
        this.type = type;
        this.videoType = videoType;
    }

    public void setDirectBeanList(List<DirectBean> directBeanList) {
        this.directBeanList = directBeanList;
    }

    public List<DirectBean> getdirectBeanList() {
        return directBeanList;
    }

    @Override
    public int getCount() {
        return (directBeanList == null ? 0 : directBeanList.size());
    }

    @Override
    public Object getItem(int position) {
        return (directBeanList == null ? null : directBeanList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        public TextView tv_liveTitle, tv_livePersion, tv_price, tv_keshiValue, tv_buyNumber;
        public TextView tv_rebates,tv_Seckill,tv_discount,tv_gift;
        public SimpleDraweeView iv_live;
        public ImageView iv_todayTab;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_directlist_layout, null);
            holder.iv_live = (SimpleDraweeView) convertView.findViewById(R.id.sdv_icon);
            holder.tv_liveTitle = (TextView) convertView.findViewById(R.id.tv_direct_title);
            holder.tv_livePersion = (TextView) convertView.findViewById(R.id.tv_teacher);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tv_buyNumber = (TextView) convertView.findViewById(R.id.tv_payNumber);
            holder.tv_keshiValue = (TextView) convertView.findViewById(R.id.tv_classNumber);
            holder.iv_todayTab = (ImageView) convertView.findViewById(R.id.iv_todayTab);
            holder.tv_rebates = (TextView) convertView.findViewById(R.id.tv_rebates);
            holder.tv_Seckill = (TextView) convertView.findViewById(R.id.tv_Seckill);
            holder.tv_discount = (TextView) convertView.findViewById(R.id.tv_discount);
            holder.tv_gift = (TextView) convertView.findViewById(R.id.tv_gift);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DirectBean bean = directBeanList.get(position);
        if (type==0){
            if (bean.getback_now()==1){
                holder.tv_rebates.setVisibility(View.VISIBLE);
            }else{
                holder.tv_rebates.setVisibility(View.GONE);
            }
            if (bean.getdisproperty().equals("1")){
                holder.tv_Seckill.setVisibility(View.GONE);
                holder.tv_discount.setVisibility(View.GONE);
                holder.tv_gift.setVisibility(View.GONE);
            }else if (bean.getdisproperty().equals("2")){
                holder.tv_Seckill.setVisibility(View.VISIBLE);
                holder.tv_Seckill.setText("特价");
            }else if (bean.getdisproperty().equals("3")){
                holder.tv_Seckill.setVisibility(View.VISIBLE);
                holder.tv_Seckill.setText("秒杀");
            }
            if (bean.getdis().equals("")){
                holder.tv_discount.setVisibility(View.GONE);
            }else{
                holder.tv_discount.setVisibility(View.VISIBLE);
                holder.tv_discount.setText(bean.getdis()+"折");
            }

            if (bean.getgiveaway().equals("1")){
                holder.tv_gift.setVisibility(View.VISIBLE);
            }else {
                holder.tv_gift.setVisibility(View.GONE);
            }

        }

        holder.tv_liveTitle.setText(bean.getTitle());
        holder.tv_livePersion.setText(bean.getTeacherDesc());
        if (bean.getRid().equals("-1")){//直播列表第一行修改成这个
            holder.tv_keshiValue.setText(bean.getLessionCount() + "小时播放");
            holder.tv_buyNumber.setText(bean.getBuy_lives() + "人在线");
        }else{
            if(bean.getVideoType() == 0){
                holder.tv_keshiValue.setText(bean.getLessionCount() + "课件");
            }else{
                holder.tv_keshiValue.setText(bean.getLessionCount() + "小时");
            }

            holder.tv_buyNumber.setText(bean.getBuy_lives() + "人已抢");
        }
        if(type == 0){
            if (bean.getdisproperty().equals("2")||bean.getdisproperty().equals("3")){//2是特价，3是秒杀 价格 否则为打折与原价
                holder.tv_price.setText("￥" + bean.getDisPrice());
            }else{
                holder.tv_price.setText("￥" + bean.getActualPrice());
            }
        }else {
            holder.tv_price.setText("已购买");
        }
        if ("0".equals(bean.getIs_zhibo())) {//往期
            holder.iv_todayTab.setVisibility(View.GONE);
        } else if ("1".equals(bean.getIs_zhibo())) {//今日
            holder.iv_todayTab.setVisibility(View.VISIBLE);
        }else {
            holder.iv_todayTab.setVisibility(View.GONE);
        }
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(context.getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        holder.iv_live.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(holder.iv_live, bean.getScaleimg(), R.drawable.ic_loading);
        return convertView;
    }
}
