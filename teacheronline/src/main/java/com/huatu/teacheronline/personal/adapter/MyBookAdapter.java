package com.huatu.teacheronline.personal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.bean.NoticeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 18250 on 2017/4/6.
 */
public class MyBookAdapter extends BaseAdapter{
    public Context context;
    private List<NoticeBean> noticeBean;
    private NoticeBean noticeBean1;


    public MyBookAdapter(Context context,List<NoticeBean> noticeBean) {
        this.context = context;
        this.noticeBean = noticeBean;
    }
    public void setNoticeBeanBeanList(ArrayList<NoticeBean> noticeBean) {
        this.noticeBean = noticeBean;
    }
    @Override
    public int getCount() {
        return ( noticeBean == null ? 0 : noticeBean.size());
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
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ooknotice, null);
            holder = new ViewHolder();
            holder.img_weidu_message= (ImageView) convertView.findViewById(R.id.img_weidu_message);
            holder.iv_mark_isno= (ImageView) convertView.findViewById(R.id.iv_mark_isno);
            holder.tv_just= (TextView) convertView.findViewById(R.id.tv_just);//时间
            holder.tv_place= (TextView) convertView.findViewById(R.id.tv_place);//地址
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_type= (TextView) convertView.findViewById(R.id.tv_type);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        noticeBean1 = noticeBean.get(position);
        long l = (Integer.parseInt(noticeBean1.getNowTime()) - Integer.parseInt(noticeBean1.getPushTime())) / 86400;
        if(l == 0){
            holder.tv_just.setText("刚刚");
        }else {
            holder.tv_just.setText(l+"天前");
        }
        if (noticeBean1.getChecked()==0){
            holder.img_weidu_message.setVisibility(View.VISIBLE);
            holder.iv_mark_isno.setVisibility(View.GONE);
        }else{
            holder.img_weidu_message.setVisibility(View.GONE);
            holder.iv_mark_isno.setVisibility(View.VISIBLE);
        }
        holder.tv_place.setText(noticeBean1.getAddress());
        holder. tv_name.setText(noticeBean1.getTitle());
        holder.tv_type.setText(noticeBean1.gettype());
        return convertView;
    }
    class ViewHolder{
        public TextView tv_just;
        public TextView tv_place;
        public ImageView iv_mark_isno;
        public ImageView img_weidu_message;
        public TextView tv_name;
        public TextView  tv_type;
    }
}
