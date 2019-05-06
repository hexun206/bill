package com.bysj.bill_system.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bysj.bill_system.R;
import com.bysj.bill_system.listener.CalendarItemClickListener;

import java.util.List;
import java.util.Map;

/**
 * @author hexun
 * @date 2018/10/22
 * @email hexun206@163.com
 * @discribe 弹窗日历适配器
 */
public class CalendarAdapter extends BaseAdapter {

    Context context;
    List<Map<String, Integer>> list;
    CalendarItemClickListener calendarItemClickListener;

    public void setCalendarItemClickListener(CalendarItemClickListener calendarItemClickListener) {
        this.calendarItemClickListener = calendarItemClickListener;
    }

    public CalendarAdapter(Context context, List<Map<String, Integer>> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Map<String, Integer> getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_calander_content, null, false);
            holder = new ViewHolder();
            holder.content = convertView.findViewById(R.id.text);
            holder.rlLayout = convertView.findViewById(R.id.rlLayout);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.content.setText(list.get(position).get("month") + "");
        switch (list.get(position).get("table_status")) {
            case 0://未选中状态
                holder.content.setTextColor(Color.parseColor("#999999"));
                holder.content.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 1://选中状态
                holder.content.setTextColor(Color.parseColor("#ffffff"));
                holder.content.setBackgroundResource(R.drawable.shape_distribution_orange_circle_bac);
                break;
            case 2://不可选择状态 未来的时间
                holder.content.setTextColor(Color.parseColor("#EEEEEE"));
                holder.content.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 3://当前月
                holder.content.setTextColor(Color.parseColor("#FF7736"));
                holder.content.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
        }
        //#FF7736//#999999//#EEEEEE
        holder.rlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarItemClickListener != null && list.get(position).get("table_status") != 2)
                    calendarItemClickListener.onItemClick(position);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView content;
        RelativeLayout rlLayout;
    }
}
