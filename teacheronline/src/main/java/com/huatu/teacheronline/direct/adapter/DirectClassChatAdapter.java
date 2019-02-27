package com.huatu.teacheronline.direct.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gensee.entity.ChatMsg;
import com.huatu.teacheronline.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 18250 on 2017/2/24.
 */
public class DirectClassChatAdapter extends BaseAdapter {
    public Context context;
    private List<ChatMsg> chatmsgList;

    public DirectClassChatAdapter(Context context,List<ChatMsg> chatmsgList) {
        this.context = context;
        this.chatmsgList = chatmsgList;
    }
//    public void setChatMsgList(List<ChatMsg> chatmsgList) {
//        this.chatmsgList = chatmsgList;
//    }
//    public List<ChatMsg> getChatMsgList() {
//        return chatmsgList;
//    }
    @Override
    public int getCount() {
        return (chatmsgList == null ? 0 : chatmsgList.size());
    }

    @Override
    public Object getItem(int position) {
        return (chatmsgList == null ? null : chatmsgList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    class ViewHolder {
     private TextView tv_name,tv_time,tv_record;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chatmsglist_layout, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_record = (TextView) convertView.findViewById(R.id.tv_record);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final ChatMsg bean = chatmsgList.get(position);
            String result = formatData("HH:mm:ss", bean.getTimeStamp());
        if (bean.getContent()==null){
            holder.tv_record.setText("");
          }else{
            holder.tv_record.setText(Html.fromHtml(bean.getContent()));
          }
          if (bean.getSender()==null){
            holder.tv_name.setText("");
           }else{
            holder.tv_name.setText(bean.getSender());
           }
           holder.tv_time.setText(result);
        return convertView;
    }
    public static String formatData(String dataFormat, long timeStamp) {
        if (timeStamp == 0) {
            return "";
        }
        timeStamp = timeStamp * 1000;
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat(dataFormat);
        result = format.format(new Date(timeStamp));
        return result;
    }
}
