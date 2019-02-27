package com.huatu.teacheronline.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.PraiseBean;
import com.huatu.teacheronline.utils.FrescoUtils;

import java.util.List;

/**
 * 学员好评适配器
 * Created by 18250 on 2017/9/13.
 */
public class PraiseAdapter extends BaseAdapter {
    private List<PraiseBean> beanList;
    private Activity context;
    private GenericDraweeHierarchy hierarchy;

    public PraiseAdapter(Activity context , List<PraiseBean> beanList ) {
        this.context = context;
        this.beanList=beanList;
    }
    @Override
    public int getCount() {
        return beanList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_praise, null);
            holder.tv_student_name= (TextView) convertView.findViewById(R.id.tv_student_name);
            holder.tv_teacher= (TextView) convertView.findViewById(R.id.tv_teacher);
            holder.tv_context= (TextView) convertView.findViewById(R.id.tv_context);
            holder.sdv_icon= (SimpleDraweeView) convertView.findViewById(R.id.sdv_icon);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_student_name.setText(beanList.get(position).getUserName());
        holder.tv_teacher.setText(beanList.get(position).getTeacherName());
        holder.tv_context.setText(beanList.get(position).getRemark());

        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        hierarchy = builder
                .setFadeDuration(100)
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.avator_boy), ScalingUtils.ScaleType.CENTER_CROP)
                .setFailureImage(context.getResources().getDrawable(R.drawable.avator_boy), ScalingUtils.ScaleType.CENTER_CROP)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .build();
        RoundingParams mRoundingParams = new RoundingParams();
        mRoundingParams.setRoundAsCircle(true);
        mRoundingParams.setBorder(Color.WHITE, 6);
        hierarchy.setRoundingParams(mRoundingParams);
        holder.sdv_icon.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(holder.sdv_icon, beanList.get(position).getUserFace(), R.drawable.avator_boy);
        return convertView;
    }



    class ViewHolder {
        public TextView tv_student_name, tv_teacher, tv_context;
        public SimpleDraweeView sdv_icon;
    }
}
