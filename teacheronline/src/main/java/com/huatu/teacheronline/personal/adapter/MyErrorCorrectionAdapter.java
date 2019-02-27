package com.huatu.teacheronline.personal.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.bean.MyErrorCorrectionBean;
import com.huatu.teacheronline.utils.CommonUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 我的纠错，适配器
 * Created by ply on 2016/1/5.
 */
public class MyErrorCorrectionAdapter extends BaseAdapter {
    public Context context;
    private List<MyErrorCorrectionBean> myErrorCorrectionBeanList;
    private boolean isClose;

    public MyErrorCorrectionAdapter(Context context) {
        this.context = context;
    }

    public void setDirectBeanList(List<MyErrorCorrectionBean> myErrorCorrectionBeanList) {
        this.myErrorCorrectionBeanList = myErrorCorrectionBeanList;
    }

    @Override
    public int getCount() {
        return (myErrorCorrectionBeanList == null ? 0 : myErrorCorrectionBeanList.size());
    }

    @Override
    public Object getItem(int position) {
        return (myErrorCorrectionBeanList == null ? null : myErrorCorrectionBeanList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        public TextView tv_title, tv_time, tv_goldNumber, tv_state, tv_reason, tv_reasonValue;
        public ScrollView scrollView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_my_error_correction, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_goldNumber = (TextView) convertView.findViewById(R.id.tv_goldNumber);
            holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
            holder.tv_reason = (TextView) convertView.findViewById(R.id.tv_reason);
            holder.tv_reasonValue = (TextView) convertView.findViewById(R.id.rv_reasonValue);
            holder.scrollView = (ScrollView) convertView.findViewById(R.id.scrollView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MyErrorCorrectionBean myErrorCorrectionBean = myErrorCorrectionBeanList.get(position);
        holder.tv_title.setText(Html.fromHtml(myErrorCorrectionBean.qcontent));
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat simpleDate = new SimpleDateFormat("MM月dd日 HH时mm分");
            Date date = format.parse(myErrorCorrectionBean.createTime);
            holder.tv_time.setText(simpleDate.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        switch (Integer.parseInt(myErrorCorrectionBean.qstatus)) {
            case 0://提交审核
                holder.tv_goldNumber.setVisibility(View.GONE);
                holder.tv_reason.setVisibility(View.GONE);
                holder.tv_state.setText(R.string.auditing);
                break;
            case 1://待审核
                holder.tv_goldNumber.setVisibility(View.GONE);
                holder.tv_reason.setVisibility(View.GONE);
                holder.tv_state.setText(R.string.auditingg);
                break;

            case 2://审核未通过
                holder.tv_goldNumber.setVisibility(View.GONE);
                holder.tv_reason.setVisibility(View.VISIBLE);
                holder.tv_state.setText(R.string.audit_fail);
                holder.tv_reasonValue.setText(myErrorCorrectionBean.opinion);
                break;
            case 3://审核通过
                holder.tv_goldNumber.setVisibility(View.VISIBLE);
                holder.tv_reason.setVisibility(View.GONE);
                holder.tv_goldNumber.setText("+" + myErrorCorrectionBean.money + "金币");
                holder.tv_state.setText(R.string.audit_sucess);
                break;
        }
        holder.tv_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClose) {//打开状态
                    isClose = false;
                    holder.scrollView.setVisibility(View.GONE);
                    Drawable drawable = context.getResources().getDrawable(R.drawable.icon_down);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    holder.tv_reason.setCompoundDrawables(null, null, drawable, null);
                    holder.tv_reason.setCompoundDrawablePadding(CommonUtils.dip2px(14));
                } else {//关闭状态
                    isClose = true;
                    holder.scrollView.setVisibility(View.VISIBLE);
                    Drawable drawable = context.getResources().getDrawable(R.drawable.icon_up);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    holder.tv_reason.setCompoundDrawables(null, null, drawable, null);
                    holder.tv_reason.setCompoundDrawablePadding(CommonUtils.dip2px(14));
                }
            }
        });
        return convertView;
    }
}
