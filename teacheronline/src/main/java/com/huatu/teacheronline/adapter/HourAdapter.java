package com.huatu.teacheronline.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.greendao.DirectBean;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.PlayerActivityForBjysdk;
import com.huatu.teacheronline.personal.bean.TodayEntity;

import java.util.List;

/**
 * Created by 18250 on 2017/10/24.
 * 首页24小时直播适配器
 */
public class HourAdapter extends BaseAdapter {
    private List<TodayEntity> hourEntities;
    private Activity context;
    private int type;//1为今天 0为明天
    private View line;

    public HourAdapter(Activity context , List<TodayEntity> hourEntities ,int type) {
        this.context = context;
        this.hourEntities=hourEntities;
        this.type=type;
    }

    @Override
    public int getCount() {
        return hourEntities == null?0:hourEntities.size();
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
        convertView = LayoutInflater.from(context).inflate(R.layout.item_home_hour, null);
        TextView tv_title= (TextView) convertView.findViewById(R.id.tv_title);
        TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        TextView  tv_zb= (TextView) convertView.findViewById(R.id.tv_zb);
        line = convertView.findViewById(R.id.v_line);
        tv_title.setText(hourEntities.get(position).getTitle());
        tv_time.setText("【"+hourEntities.get(position).getZhibotime()+"】");
        if (type==1&&position==0){
            tv_zb.setBackgroundResource(R.drawable.ic_zzzb);
            tv_zb.setText("正在直播");
        }else if (type==1&&position!=0){
            tv_zb.setBackgroundResource(R.drawable.ic_msks);
            tv_zb.setText("马上直播");
        }else if (type==0){
            tv_zb.setText("即将开始");
            tv_zb.setBackgroundResource(R.drawable.ic_msks);
        }
        if (position==2){
            line.setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DirectBean directBean = new DirectBean();
                directBean.setRid("-1");
                directBean.setTitle("24小时大咖直播");
                directBean.setIs_fufei("1");
                directBean.setIs_buy("1");
                directBean.setCustomer("http://chat.looyuoms.com/chat/chat/p.do?c=20001211&f=10062439&g=10059670&refer=appsh");
                PlayerActivityForBjysdk.newIntent(context, directBean, 0, 2);
            }
        });
        return convertView;
    }
}
