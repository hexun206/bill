package com.huatu.teacheronline.personal.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.vipexercise.vipbean.VipPaperBean;

import java.util.List;

/**
 * Created by 18250 on 2017/7/26.
 * VIP我的错题 适配器
 */
public class VipAdapter extends BaseAdapter {
    private Activity context;
    private List<VipPaperBean> data;

    public VipAdapter(Activity context) {
        this.context = context;
//        this.beanList=beanList;
    }
    @Override
    public int getCount() {
        return data==null?0:data.size();
    }

    @Override
    public VipPaperBean getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_vip_ques, null);
            holder.tv_title= (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_teacher= (TextView) convertView.findViewById(R.id.tv_teacher);
            holder.tv_sc_time= (TextView) convertView.findViewById(R.id.tv_sc_time);
            holder.tv_num= (TextView) convertView.findViewById(R.id.tv_num);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_title.setText(data.get(position).getTitle());
        holder.tv_sc_time.setText("上传时间："+data.get(position).getCreateTime());
        holder.tv_teacher.setText("题库归属老师：" + data.get(position).getCreateUser());
        holder.tv_num.setText(data.get(position).getCount()+"题");
        return convertView;
    }

    public void setData(List<VipPaperBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    class ViewHolder {
        public TextView tv_title,  tv_teacher, tv_sc_time,tv_num;
    }
}
