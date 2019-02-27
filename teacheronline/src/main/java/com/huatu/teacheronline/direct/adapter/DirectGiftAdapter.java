package com.huatu.teacheronline.direct.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.teacheronline.R;

import java.util.List;

/**
 * Created by 18250 on 2017/2/24.
 * 赠品展示适配器
 */
public class DirectGiftAdapter extends BaseAdapter {
    public Context context;
    private List<String> giftlist;

    public DirectGiftAdapter(Context context ,List<String> giftlist) {
        this.context = context;
        this.giftlist = giftlist;
    }
    @Override
    public int getCount() {
        return (giftlist == null ? 0 : giftlist.size());
    }

    @Override
    public Object getItem(int position) {
        return (giftlist == null ? null : giftlist.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    class ViewHolder {
     private TextView tv_gift_context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gift, null);
            holder.tv_gift_context = (TextView) convertView.findViewById(R.id.tv_gift_context);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final String bean = giftlist.get(position);
        holder.tv_gift_context.setText(bean.toString());
        return convertView;
    }

}
