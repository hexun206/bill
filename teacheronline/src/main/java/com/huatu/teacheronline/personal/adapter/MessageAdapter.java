package com.huatu.teacheronline.personal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.bean.MessageBean;

import java.util.ArrayList;

/**
 * 我的消息适配器
 * @author ljyu
 * @time 2016-8-15 14:54:48
 */
public class MessageAdapter extends BaseAdapter{

    public Context context;
    private ArrayList<MessageBean> messageBeanList;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void setMessageBeanList(ArrayList<MessageBean> messageBeanList) {
        this.messageBeanList = messageBeanList;
    }

    public ArrayList<MessageBean> getMessageBeanList() {
        return messageBeanList;
    }

    @Override
    public int getCount() {
        return (messageBeanList == null ? 0 : messageBeanList.size());
    }

    @Override
    public MessageBean getItem(int position) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_messagelv_layout, null);
            holder.tv_title_message = (TextView) convertView.findViewById(R.id.tv_title_message);
            holder.img_weidu_message = (ImageView) convertView.findViewById(R.id.img_weidu_message);
            holder.tv_date_message = (TextView) convertView.findViewById(R.id.tv_date_message);
            holder.tv_withouthtml = (TextView)   convertView.findViewById(R.id.tv_withouthtml);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MessageBean messageBean = getItem(position);
        holder.tv_title_message.setText(messageBean.getTitle());
        //计算几天前
        long l = (messageBean.getNow_time() - messageBean.getUpdate_time()) / 86400;
        if(l == 0){
            holder.tv_date_message.setText("刚刚");
        }else {
            holder.tv_date_message.setText(l+"天前");
        }
        if(Integer.parseInt(messageBean.getStatus()) == 0){
            holder.img_weidu_message.setVisibility(View.VISIBLE);
//            holder.tv_date_message.setTextColor(context.getResources().getColor(R.color.green004));
        }else {
            holder.img_weidu_message.setVisibility(View.GONE);
//            holder.tv_date_message.setTextColor(context.getResources().getColor(R.color.gray004));
        }
        holder.tv_withouthtml.setText(messageBean.getcontentWithoutHtml());
        return convertView;
    }

    class ViewHolder{

        public TextView tv_title_message;
        public ImageView img_weidu_message;
        public TextView tv_date_message;
        public TextView tv_withouthtml;
    }
}
