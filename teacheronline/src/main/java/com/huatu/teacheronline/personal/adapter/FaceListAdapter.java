package com.huatu.teacheronline.personal.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.BooknoticeActivity;
import com.huatu.teacheronline.personal.MyCourseActivity;
import com.huatu.teacheronline.personal.bean.CouserBean;
import com.huatu.teacheronline.personal.frament.MyDirectTabFragment;
import com.huatu.teacheronline.utils.ToastUtils;

import java.util.List;

/**我的课程适班级配器
 * Created by 18250 on 2017/3/20.
 */
public class FaceListAdapter extends BaseAdapter {
    private final boolean isWisdomClass;
    public Context context;
    private List<CouserBean> couserbeanList;
    private CouserBean couserbean;

    public FaceListAdapter(Context context, List<CouserBean> couserbeanList, boolean isWisdomClass) {
        this.context = context;
        this.couserbeanList = couserbeanList;
        this.isWisdomClass = isWisdomClass;
    }
    public void setCousrBeanList(List<CouserBean> couserbeanList) {
        this.couserbeanList = couserbeanList;
    }
    public List<CouserBean> getCousrBeanList() {
        return couserbeanList;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_face, null);
            holder.tv_name= (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_course= (TextView)convertView.findViewById(R.id.tv_course);
            holder.tv_number= (TextView)convertView.findViewById(R.id.tv_number);
            holder.tv_time= (TextView)convertView.findViewById(R.id.tv_time);
            holder.tv_ks= (TextView)convertView.findViewById(R.id.tv_ks);
            holder.tv_msg_icon= (TextView)convertView.findViewById(R.id.tv_msg_icon);
            holder. tv_notice = (TextView)convertView.findViewById(R.id.tv_notice);
            holder.img_kc_icon= (ImageView)convertView.findViewById(R.id.img_kc_icon);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        couserbean = couserbeanList.get(position);
        if (couserbean.gethasNoCheckCount()!=0){//是否确认所有消息
            holder.tv_msg_icon.setVisibility(View.VISIBLE);
            if (couserbean.gethasNoCheckCount()>9){
                holder.tv_msg_icon.setText("···");
            }else{
                holder.tv_msg_icon.setText(couserbean.gethasNoCheckCount()+"");
            }
        }else{
            holder.tv_msg_icon.setVisibility(View.GONE);
        }
        if (couserbean.getIsUpdate()==0){//是否课程已修改，0并无修改，1已被修改
            holder.img_kc_icon.setVisibility(View.GONE);
        }else{
            holder.img_kc_icon.setVisibility(View.VISIBLE);
        }
        holder.tv_number.setText("班级号："+couserbean.getNumber());
        holder.tv_name.setText(couserbean.getTitle());
        holder.tv_time.setText(couserbean.getSchoolTime()+"");
        holder.tv_ks.setText(couserbean.getPeriod());
        holder.tv_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//点击进入通知详情
                context.startActivity(new Intent(context, MyCourseActivity.class).putExtra("classId",couserbeanList.get(position).getClassId())
                        .putExtra(MyDirectTabFragment.KEY_ISWISDOMCLASS,isWisdomClass));
            }
        });
        holder.tv_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//点击进入开课通知
                if (couserbeanList.get(position).getNoticeCount()==0){//消息条数为0的话 点击无效
                    ToastUtils.showToast("没有开课通知！");
                    return;
                }
              context.startActivity(new Intent(context, BooknoticeActivity.class).putExtra("classId",couserbeanList.get(position).getClassId()));
            }
        });
        return convertView;
    }
    class ViewHolder{
        public TextView tv_name;
        public TextView tv_course;
        public TextView tv_number;
        public TextView tv_time;
        public TextView tv_ks;
        public TextView tv_notice;
        public TextView tv_msg_icon;
        public ImageView img_kc_icon;
    }
}
