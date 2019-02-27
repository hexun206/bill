package com.huatu.teacheronline.exercise.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.teacheronline.R;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangxm on 2015/6/10.
 */
public class AnswercardGridAdapter extends BaseAdapter {

    private Context context;
    private List<Integer> codeList;// 题号集合
    private Map<Integer, String> gridMap;// 每个知识点下的数据集合<题号，答题情况>

    public AnswercardGridAdapter(Context context, List<Integer> codeList, Map<Integer, String> gridMap) {
        this.context = context;
        this.codeList = codeList;
        this.gridMap = gridMap;
    }

    @Override
    public int getCount() {
        if (null == codeList) {
            return 0;
        } else {
            return codeList.size();
        }
    }

    @Override
    public Integer getItem(int position) {
        return codeList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_answercard_grid_item, null);
            holder.textview = (TextView) convertView.findViewById(R.id.textview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textview.setText(String.valueOf(codeList.get(position)));
//         答题情况：0错误  1正确  -1未答  -2已作答:答题卡页面专用
        String flag = gridMap.get(codeList.get(position));
        if (!TextUtils.isEmpty(flag)) {
            if ("1".equals(flag)) {
                holder.textview.setTextColor(context.getResources().getColor(R.color.white));
                holder.textview.setBackgroundResource(R.drawable.bg_round_green);
            } else if ("0".equals(flag)) {
                holder.textview.setTextColor(context.getResources().getColor(R.color.white));
                holder.textview.setBackgroundResource(R.drawable.bg_round_red);
            } else if ("-1".equals(flag)) {
                holder.textview.setTextColor(context.getResources().getColor(R.color.green006));
                holder.textview.setBackgroundResource(R.drawable.bg_round_white);
            } else if ("-2".equals(flag)) {
                holder.textview.setTextColor(context.getResources().getColor(R.color.white));
                holder.textview.setBackgroundResource(R.drawable.bg_round_green);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView textview;
    }
}
