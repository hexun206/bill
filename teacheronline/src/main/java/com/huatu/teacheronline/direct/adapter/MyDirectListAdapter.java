package com.huatu.teacheronline.direct.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.greendao.DirectBean;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.PlayerActivityForBjysdk;
import com.huatu.teacheronline.personal.DirectCourseActivity;
import com.huatu.teacheronline.personal.DirectDataActivity;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

//import com.facebook.drawee.drawable.ScalingUtils;
//import com.facebook.drawee.generic.GenericDraweeHierarchy;
//import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
//import com.facebook.drawee.view.SimpleDraweeView;
//import com.huatu.handheld_huatu.R;
//import com.huatu.handheld_huatu.entity.VODBean;
//import com.huatu.handheld_huatu.utils.CommonUtils;
//import com.huatu.handheld_huatu.utils.FrescoUtils;

/**
 * 我的直播，适配器
 * Created by ply on 2016/1/5.
 */
public class MyDirectListAdapter extends BaseAdapter {
    private  int videoType;
    private int type; //0 全部 或者查询  1 我的
    public Activity context;
    private List<DirectBean> directBeanList;

    public MyDirectListAdapter(Activity context) {
        this.context = context;
    }
    public MyDirectListAdapter(Activity context, int type,int videoType) {
        this.context = context;
        this.type = type;
        this.videoType = videoType;
    }

    public void setDirectBeanList(List<DirectBean> directBeanList) {
        this.directBeanList = directBeanList;
    }

    public List<DirectBean> getdirectBeanList() {
        return directBeanList;
    }

    @Override
    public int getCount() {
        return (directBeanList == null ? 0 : directBeanList.size());
    }

    @Override
    public Object getItem(int position) {
        return (directBeanList == null ? null : directBeanList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        public TextView tv_liveTitle, tv_livePersion, tv_information, tv_keshiValue;
        public SimpleDraweeView iv_live;
        public ImageView iv_paly,iv_paly_video;
        public TextView tv_class_notice;
        public TextView tv_number;
        public TextView tv_add_qq;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_my_directlist_layout, null);
            holder.iv_live = (SimpleDraweeView) convertView.findViewById(R.id.sdv_icon);
            holder.tv_liveTitle = (TextView) convertView.findViewById(R.id.tv_direct_title);
            holder.tv_livePersion = (TextView) convertView.findViewById(R.id.tv_teacher);
            holder.tv_information = (TextView) convertView.findViewById(R.id.tv_information);
            holder.tv_keshiValue = (TextView) convertView.findViewById(R.id.tv_classNumber);
            holder.tv_class_notice = (TextView) convertView.findViewById(R.id.tv_class_notice);
            holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            holder.iv_paly = (ImageView) convertView.findViewById(R.id.iv_paly);
            holder.iv_paly_video= (ImageView) convertView.findViewById(R.id.iv_paly_video);
            holder.tv_add_qq = (TextView) convertView.findViewById(R.id.tv_add_qq);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DirectBean bean = directBeanList.get(position);
        holder.tv_liveTitle.setText(bean.getTitle());
        holder.tv_livePersion.setText(bean.getTeacherDesc()+"");
        holder.tv_number.setText("编号：" + bean.getRid());
        if (bean.getAndroidqqidkey()!=null){
            if (bean.getAndroidqqidkey().equals("")){
                holder.tv_add_qq.setVisibility(View.GONE);
            }else{
                holder.tv_add_qq.setVisibility(View.VISIBLE);
            }
        }
        if (bean.getRid().equals("-1")){//直播列表第一行修改成这个
            holder.tv_keshiValue.setText(bean.getLessionCount() + "小时播放");
        }else{

            if(videoType == 0){
                holder.tv_keshiValue.setText(bean.getLessionCount() + "课件");
            }else{
                holder.tv_keshiValue.setText(bean.getLessionCount() + "小时");
            }
        }

        holder.iv_paly_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.ll_loading) {//如果点击底部FooterView加载中布局，不做出来
                    return;
                }
                MobclickAgent.onEvent(context, "courseOnItemClick");
                DirectBean directBean = directBeanList.get(position);
//                if (directBean.getVideoType() == 1) {
//                    if ("1".equals(directBean.getIs_zhibo())) {//直播
////                        context.startActivity(new Intent(context, DirectPlayDetailsActivityForRtsdk.class)
////                                .putExtra("DirectBean", directBean).putExtra("position", 0).putExtra("wherefrom", 2));
//                        context.startActivity(new Intent(context, LiveActivityForBjysdk.class)
//                                .putExtra("DirectBean", directBean).putExtra("position", 0).putExtra("wherefrom", 2));
//                    } else if ("-1".equals(directBeanList.get(position).getIs_zhibo())) {
//                        ToastUtils.showToast(R.string.direct_offline);
//                    } else {//录播
//                        context.startActivity(new Intent(context, LiveActivityForBjysdk.class)
//                                .putExtra("DirectBean", directBean).putExtra("position", 0).putExtra("wherefrom", 2));
//                    }
//                } else if (directBean.getVideoType() == 0) {
                    //网课
                context.startActivity(new Intent(context, PlayerActivityForBjysdk.class)
                                .putExtra("DirectBean", directBean).putExtra("position", 0).putExtra("wherefrom", 2));
//                }
            }
        });

        holder.tv_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (directBeanList.get(position).getCourse()==1){
                    DirectDataActivity.newIntent(context, directBeanList.get(position).getRid(), directBeanList.get(position).getTitle());
                }else{
                    ToastUtils.showToast("暂无课程信息！");
                }

            }
        });
        holder.tv_class_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (directBeanList.get(position).getCourse()==1){
                    DirectCourseActivity.newIntent(context, directBeanList.get(position).getRid(), directBeanList.get(position).getTitle());
                }else{
                    ToastUtils.showToast("暂无课程通知！");
                }
            }
        });
        holder.tv_add_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQQGroup(directBeanList.get(position).getAndroidqqidkey());
            }
        });

        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(context.getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        holder.iv_live.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(holder.iv_live, bean.getScaleimg(), R.drawable.ic_loading);
        return convertView;
    }


    public boolean addQQGroup(String key)
    {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
           context.startActivity(intent);
            return true;
        } catch (Exception e) {
             ToastUtils.showToast("未安装手机QQ或安装的版本不支持");
            return false;    }
    }
}
