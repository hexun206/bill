package com.huatu.teacheronline.personal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.bean.MsgBean;

import java.util.List;

/**
 * 我的课程留言列表
 * Created by 18250 on 2017/8/25.
 */
public class MessageListAdapter extends BaseAdapter {

    public Context context;
    private List<MsgBean.LeaveMessageListEntity> couserbeanList;


    public MessageListAdapter(Context context,List<MsgBean.LeaveMessageListEntity> couserbeanList) {
        this.context = context;
        this.couserbeanList = couserbeanList;
    }

    @Override
    public int getCount() {
        return (couserbeanList == null ? 0 : couserbeanList.size());
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
        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_msglist, null);
            holder.tv_me_msg= (TextView) convertView.findViewById(R.id.tv_me_msg);
            holder.tv_me_time= (TextView)convertView.findViewById(R.id.tv_me_time);
            holder.tv_he_msg= (TextView)convertView.findViewById(R.id.tv_he_msg);
            holder.tv_he_time= (TextView)convertView.findViewById(R.id.tv_he_time);
            holder.rl_hf= (RelativeLayout)convertView.findViewById(R.id.rl_hf);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_me_msg.setText(couserbeanList.get(position).getContent());
        holder.tv_me_time.setText(couserbeanList.get(position).getCreateTime());
        holder.tv_he_msg.setText(couserbeanList.get(position).getReplayContent());
        holder.tv_he_time.setText(couserbeanList.get(position).getReplayTime());
        if (couserbeanList.get(position).getIsReplyFlg()==0){
            holder.rl_hf.setVisibility(View.GONE);
        }else{
            holder.rl_hf.setVisibility(View.VISIBLE);
        }
        return convertView;
    }


    class ViewHolder{
        public TextView tv_me_msg;
        public TextView tv_me_time;
        public TextView tv_he_msg;
        public TextView tv_he_time;
        public  RelativeLayout rl_hf;

    }
}
