package com.huatu.teacheronline.personal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.bean.MyErrorCorrectionBean;
import com.huatu.teacheronline.utils.CommonUtils;

import java.util.List;

/**
 * 我的纠错，适配器
 * Created by ply on 2016/1/5.
 */
public class NewErrorCorrectionAdapter extends BaseAdapter {
    public Context context;
    private List<MyErrorCorrectionBean> myErrorCorrectionBeanList;
    private boolean isClose;

    public NewErrorCorrectionAdapter(Context context) {
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

//    class ViewHolder {
//        public TextView  tv_title,tv_Submit_time,tv_examin_time,tv_adopt_time;
//        public TextView  tv_time,  tv_reason, tv_name,tv_Submit,tv_examin,tv_adopt;
//        public LinearLayout ll_Submit ,ll_examine,ll_Result ,ll_mine;
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        final ViewHolder holder;
//        if (convertView == null) {
//            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_new_error_correction, null);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            TextView tv_title=(TextView) convertView.findViewById(R.id.tv_title);
            TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            final TextView tv_reason = (TextView) convertView.findViewById(R.id.tv_reason);

             final LinearLayout ll_mine= (LinearLayout) convertView.findViewById(R.id.ll_mine);
        LinearLayout ll_Submit= (LinearLayout) convertView.findViewById(R.id.ll_Submit);
        LinearLayout ll_examine= (LinearLayout) convertView.findViewById(R.id.ll_examine);
        LinearLayout ll_Result= (LinearLayout) convertView.findViewById(R.id.ll_Result);
        TextView tv_Submit_time= (TextView) convertView.findViewById(R.id.tv_Submit_time);
        TextView tv_examin_time= (TextView) convertView.findViewById(R.id.tv_examin_time);
        TextView tv_adopt_time= (TextView) convertView.findViewById(R.id.tv_adopt_time);

        TextView tv_Submit= (TextView) convertView.findViewById(R.id.tv_Submit);
        TextView tv_examin= (TextView) convertView.findViewById(R.id.tv_examin);
        TextView tv_adopt= (TextView) convertView.findViewById(R.id.tv_adopt);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
        final MyErrorCorrectionBean myErrorCorrectionBean = myErrorCorrectionBeanList.get(position);
        tv_title.setText(Html.fromHtml(myErrorCorrectionBean.userSuggest));
        tv_time.setText(myErrorCorrectionBean.createTime);
        tv_name.setText(myErrorCorrectionBean.module);

        if (myErrorCorrectionBean.createTime!=null&&myErrorCorrectionBean.audittime!=null&&myErrorCorrectionBean.updatetime!=null){
               tv_Submit_time.setText(myErrorCorrectionBean.createTime);
               tv_adopt_time.setText(myErrorCorrectionBean.audittime);
               tv_examin_time.setText(myErrorCorrectionBean.updatetime);
            if (myErrorCorrectionBean.qstatus!=null){
                if (myErrorCorrectionBean.qstatus.equals("2")){
                     tv_adopt.setText("未通过:"+myErrorCorrectionBean.opinion);
//                    tv_adopt.setTextColor(Color.parseColor("#ff0000"));
                }
            }
        }else if (myErrorCorrectionBean.audittime==null&&myErrorCorrectionBean.updatetime==null){
               tv_Submit_time.setText(myErrorCorrectionBean.createTime);
               tv_Submit.setTextColor(Color.parseColor("#04cbae"));
               ll_examine.setVisibility(View.GONE);
               ll_Result.setVisibility(View.GONE);
        }else if (myErrorCorrectionBean.audittime!=null&&myErrorCorrectionBean.updatetime==null){
               tv_Submit_time.setText(myErrorCorrectionBean.createTime);
               tv_adopt_time.setText(myErrorCorrectionBean.audittime);
               ll_examine.setVisibility(View.GONE);
            if (myErrorCorrectionBean.qstatus!=null) {
                if (myErrorCorrectionBean.qstatus.equals("2")) {
                     tv_adopt.setText("未通过:"+myErrorCorrectionBean.opinion);
                }
            }
        }else if (myErrorCorrectionBean.audittime==null&&myErrorCorrectionBean.updatetime!=null){
              tv_Submit_time.setText(myErrorCorrectionBean.createTime);
              tv_examin_time.setText(myErrorCorrectionBean.updatetime);
              tv_examin.setTextColor(Color.parseColor("#04cbae"));
              ll_Result.setVisibility(View.GONE);
        }




//        if (myErrorCorrectionBean.createTime!=null){
//        if (myErrorCorrectionBean.createTime!=null&&myErrorCorrectionBean.audittime==null&&myErrorCorrectionBean.updatetime==null){
//            holder.tv_Submit_time.setText(myErrorCorrectionBean.createTime);
//            holder.tv_Submit.setTextColor(Color.parseColor("#04cbae"));
//            holder.ll_examine.setVisibility(View.GONE);
//            holder.ll_Result.setVisibility(View.GONE);
//        }else if(myErrorCorrectionBean.createTime!=null&&myErrorCorrectionBean.audittime!=null&&myErrorCorrectionBean.updatetime==null){
//            holder.tv_Submit_time.setText(myErrorCorrectionBean.createTime);
//            holder.tv_adopt_time.setText(myErrorCorrectionBean.audittime);
//            holder.ll_examine.setVisibility(View.GONE);
//        }else if(myErrorCorrectionBean.createTime!=null&&myErrorCorrectionBean.audittime!=null&&myErrorCorrectionBean.updatetime!=null){
//            holder.tv_Submit_time.setText(myErrorCorrectionBean.createTime);
//            holder.tv_adopt_time.setText(myErrorCorrectionBean.audittime);
//            holder.tv_examin_time.setText(myErrorCorrectionBean.updatetime);
//            if (myErrorCorrectionBean.qstatus!=null){
//                if (myErrorCorrectionBean.qstatus.equals("2")){
//                    holder.tv_adopt.setText("审核未通过");
//                }
//            }
//        }else if(myErrorCorrectionBean.createTime!=null&&myErrorCorrectionBean.audittime==null&&myErrorCorrectionBean.updatetime!=null){
//            holder.ll_Result.setVisibility(View.GONE);
//            holder.tv_Submit_time.setText(myErrorCorrectionBean.createTime);
//            holder.tv_examin_time.setText(myErrorCorrectionBean.updatetime);
//            holder.tv_examin.setTextColor(Color.parseColor("#04cbae"));
//            if (myErrorCorrectionBean.qstatus!=null){
//                if (myErrorCorrectionBean.qstatus.equals("2")){
//                    holder.tv_adopt.setText("审核未通过");
//                }
//            }
//        }
//        }
        if (myErrorCorrectionBean.getisCheck()){
             ll_mine.setVisibility(View.VISIBLE);
//             Drawable drawable = context.getResources().getDrawable(R.drawable.triangle);
//             drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//             tv_reason.setCompoundDrawables(null, null, drawable, null);
//             tv_reason.setCompoundDrawablePadding(CommonUtils.dip2px(14));
        }else{
             ll_mine.setVisibility(View.GONE);
//             Drawable drawable = context.getResources().getDrawable(R.drawable.ic_more_shyj_1);
//             drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//             tv_reason.setCompoundDrawables(null, null, drawable, null);
//             tv_reason.setCompoundDrawablePadding(CommonUtils.dip2px(14));
        }

         tv_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myErrorCorrectionBean.getisCheck()) {//打开状态
                     isClose = false;
                     ll_mine.setVisibility(View.GONE);
                     myErrorCorrectionBean.setisCheck(isClose);
                     Drawable drawable = context.getResources().getDrawable(R.drawable.ic_more_shyj_1);
                     drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                     tv_reason.setCompoundDrawables(null, null, drawable, null);
                     tv_reason.setCompoundDrawablePadding(CommonUtils.dip2px(14));
                } else {//关闭状态
                     isClose = true;
                     ll_mine.setVisibility(View.VISIBLE);
                     myErrorCorrectionBean.setisCheck(isClose);
                     Drawable drawable = context.getResources().getDrawable(R.drawable.triangle);
                     drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                     tv_reason.setCompoundDrawables(null, null, drawable, null);
                     tv_reason.setCompoundDrawablePadding(CommonUtils.dip2px(14));
                }
            }
        });
        return convertView;
    }
}
