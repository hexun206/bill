package com.huatu.teacheronline.personal.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.vipexercise.vipbean.VipDataBean;

import java.util.List;

/**
 * Created by 18250 on 2017/7/25.
 */
public class MyDataAdapter extends BaseAdapter {
    private Activity context;
    private List<VipDataBean.DataEntity> vipDataBeans;
    private View.OnClickListener mListener;


    public MyDataAdapter(Activity context ,List<VipDataBean.DataEntity> vipDataBeans,View.OnClickListener listener) {
        this.context = context;
        this.vipDataBeans=vipDataBeans;
        mListener = listener;
    }

    public void setvipDataBeanList(List<VipDataBean.DataEntity> vipDataBeans) {
        this.vipDataBeans = vipDataBeans;
    }

    public List<VipDataBean.DataEntity> getvipDataBeanList() {
        return vipDataBeans;
    }
    @Override
    public int getCount() {
        return vipDataBeans == null ? 0 : vipDataBeans.size();
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_mydata, null);
            holder.tv_title= (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_teacher= (TextView) convertView.findViewById(R.id.tv_teacher);
            holder.tv_vip_time= (TextView) convertView.findViewById(R.id.tv_vip_time);
            holder.tv_num= (TextView) convertView.findViewById(R.id.tv_num);
            holder.tv_yd_num= (TextView) convertView.findViewById(R.id.tv_yd_num);
            holder.iv_zan = (ImageView) convertView.findViewById(R.id.iv_zan);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.iv_zan.setTag(position);
        holder.iv_zan.setOnClickListener(mListener);
        holder.tv_title.setText(vipDataBeans.get(position).getTitle());
        holder.tv_teacher.setText("题库归属教师："+vipDataBeans.get(position).getCreateUser());
        holder.tv_vip_time.setText("上传时间："+vipDataBeans.get(position).getCreateTime());
        holder.tv_num.setText(vipDataBeans.get(position).getLikeCount()+"");
        holder.tv_yd_num.setText(vipDataBeans.get(position).getReadCount()+"已阅");
        if (vipDataBeans.get(position).getIsLike()==1){
            holder.iv_zan.setImageResource(R.drawable.ic_zan_2);
        }else{
            holder.iv_zan.setImageResource(R.drawable.ic_zan_1);
        }
        return convertView;
    }

    class ViewHolder{
        public TextView tv_title;
        public TextView tv_teacher;
        public TextView tv_vip_time;
        public TextView tv_num;
        public TextView tv_yd_num;
        public ImageView iv_zan;
    }

}
