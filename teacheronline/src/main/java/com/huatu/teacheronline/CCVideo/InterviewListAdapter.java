package com.huatu.teacheronline.CCVideo;

import android.app.Activity;
import android.content.Intent;
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
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.FrescoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhxm on 2016/1/7.
 * 面试视频列表适配器
 */
public class InterviewListAdapter extends BaseAdapter {
    private Activity context;
    private List<InterviewVideoBean> list;
    private static final String TAG = "InterviewListAdapter";

    public InterviewListAdapter(Activity context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public List<InterviewVideoBean> getList() {
        return list;
    }

    public void setList(List<InterviewVideoBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public InterviewVideoBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_interview_video_list, null);
            holder.sdv_icon = (SimpleDraweeView) convertView.findViewById(R.id.sdv_icon);
            holder.tv_video_title = (TextView) convertView.findViewById(R.id.tv_video_title);
            holder.tv_count_read = (TextView) convertView.findViewById(R.id.tv_count_read);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final InterviewVideoBean interviewVideoBean = list.get(position);
        DebugUtil.e(TAG,interviewVideoBean.toString());
        holder.tv_video_title.setText(interviewVideoBean.getTitle());
        holder.tv_count_read.setText(interviewVideoBean.getClick());
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(context.getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        holder.sdv_icon.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(holder.sdv_icon, interviewVideoBean.getThumb(), R.drawable.ic_loading);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InterviewVideoDetailBjyActivity.class);
                intent.putExtra("video_id", interviewVideoBean.getId());
                intent.putExtra("bjyid", interviewVideoBean.getBjyid());
                intent.putExtra("token", interviewVideoBean.getToken());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView sdv_icon;
        TextView tv_video_title, tv_count_read;
    }
}
