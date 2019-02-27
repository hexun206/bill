package com.huatu.teacheronline.vipexercise.adapter;

import android.content.Context;
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
import com.huatu.teacheronline.vipexercise.vipbean.LeaveBean;

import java.util.List;

/**
 * 我的消息适配器
 * @author ljyu
 * @time 2016-8-15 14:54:48
 */
public class LeaveListAdapter extends BaseAdapter{

    public Context context;
    private List<LeaveBean.DataEntity> messageBeanList;

    public LeaveListAdapter(Context context,List<LeaveBean.DataEntity> messageBeanList) {
        this.context = context;
        this.messageBeanList = messageBeanList;

    }

    public void setMessageBeanList(List<LeaveBean.DataEntity> messageBeanList) {
        this.messageBeanList = messageBeanList;
    }

    public List<LeaveBean.DataEntity> getMessageBeanList() {
        return messageBeanList;
    }

    @Override
    public int getCount() {
        if (messageBeanList != null) {
            DebugUtil.e(" LeaveListAdapter getCount " + messageBeanList.size());
        }
        return (messageBeanList == null ? 0 : messageBeanList.size());
    }

    @Override
    public LeaveBean.DataEntity getItem(int position) {
        return (messageBeanList == null ? null : messageBeanList.get(position));
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_myteacher_msg, null);
            holder.iv_personal_face = (SimpleDraweeView) convertView.findViewById(R.id.iv_personal_face);
            holder.tv_msg = (TextView) convertView.findViewById(R.id.tv_msg);
            holder.tv_teacher_name = (TextView) convertView.findViewById(R.id.tv_teacher_name);
            holder.tv_context = (TextView) convertView.findViewById(R.id.tv_context);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (messageBeanList.get(position).getCount()!=0){
            holder.tv_msg.setVisibility(View.VISIBLE);
            holder.tv_msg.setText(messageBeanList.get(position).getCount()+"");
        }else {
            holder.tv_msg.setVisibility(View.GONE);
        }
        if (messageBeanList.get(position) != null) {
            DebugUtil.e("position : "+position+"  "+messageBeanList.get(position).toString());
        }
        holder.tv_teacher_name.setText(messageBeanList.get(position).getTeacherName());
        holder.tv_context.setText(messageBeanList.get(position).getContent());
        holder.tv_time.setText(messageBeanList.get(position).getCreateTime());

        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.avatar_t), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(context.getResources().getDrawable(R.drawable.avatar_t), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        holder.iv_personal_face.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(holder.iv_personal_face, messageBeanList.get(position).getTeacherPortrait(), R.drawable.avatar_t);

        return convertView;
    }

    class ViewHolder{
        public SimpleDraweeView iv_personal_face;
        public TextView tv_msg;
        public TextView tv_teacher_name;
        public TextView tv_context;
        public TextView tv_time;
    }
}
