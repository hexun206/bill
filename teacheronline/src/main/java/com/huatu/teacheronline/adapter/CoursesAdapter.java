package com.huatu.teacheronline.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.RecomBean;
import com.huatu.teacheronline.sensorsdata.TrackUtil;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.sensorsdata.analytics.android.sdk.SensorsAdapterViewItemTrackProperties;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 18250 on 2017/7/24.
 * 推荐课程适配器
 */
public class CoursesAdapter extends BaseAdapter implements SensorsAdapterViewItemTrackProperties {
    private List<RecomBean> beanList;
    private Activity context;

    public CoursesAdapter(Activity context,List<RecomBean> beanList) {
        this.context = context;
        this.beanList=beanList;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_courses, null);
            holder.sdv_icon= (SimpleDraweeView) convertView.findViewById(R.id.sdv_icon);
            holder.tv_course_title= (TextView) convertView.findViewById(R.id.tv_course_title);
            holder.tv_money= (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_num= (TextView) convertView.findViewById(R.id.tv_num);
            holder.tv_label= (TextView) convertView.findViewById(R.id.tv_label);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_course_title.setText(beanList.get(position).getTitle());
        holder.tv_money.setText("¥"+beanList.get(position).getActualPrice());
        holder.tv_num.setText(beanList.get(position).getBuy_lives()+"已抢");
        if (beanList.get(position).getactivity().equals("")){
            holder.tv_label.setVisibility(View.GONE);
        }else{
            holder.tv_label.setVisibility(View.VISIBLE);
            holder.tv_label.setText(beanList.get(position).getactivity());
        }

        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(context.getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        holder.sdv_icon.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(holder.sdv_icon, beanList.get(position).getScaleimg(), R.drawable.ic_loading);
        return convertView;
    }

    @Override
    public JSONObject getSensorsItemTrackProperties(int position) throws JSONException {


        HashMap<String, String> map = new HashMap<>();
        map.put("$element_content", "推荐课程");
        map.put("$element_type", TrackUtil.TYPE_CARD);




        return new JSONObject(map);
    }

    class ViewHolder {
        public TextView tv_course_title, tv_money, tv_num,tv_label;
        public SimpleDraweeView sdv_icon;
    }


}
