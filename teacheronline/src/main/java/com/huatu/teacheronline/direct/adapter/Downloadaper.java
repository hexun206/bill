package com.huatu.teacheronline.direct.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gensee.utils.StringUtil;
import com.greendao.DirectBean;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.DownManageActivity;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.widget.CircleView;

import java.util.List;

/**
 * 视频下载adapter
 * Created by 18250 on 2016/7/26.
 */
public class Downloadaper extends BaseAdapter {
    private View.OnClickListener OnClickListener;
    private Context context;
    private List<DirectBean> mChapterTreeData;
    public boolean isEdit;
    private static final String TAG = "Downloadaper";

    public Downloadaper(Context context, List<DirectBean> mChapterTreeData, boolean isEdit, View.OnClickListener OnClickListener) {
        this.context = context;
        this.mChapterTreeData = mChapterTreeData;
        this.isEdit = isEdit;
        this.OnClickListener = OnClickListener;
    }

    public void setmChapterTreeData(List<DirectBean> mChapterTreeData) {
        this.mChapterTreeData = mChapterTreeData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mChapterTreeData.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.down_manage_itm, null);
            holder = new ViewHolder();
            holder.tv_directTitle = (TextView) convertView.findViewById(R.id.tv_directTitle);
            holder.rl_state = (RelativeLayout) convertView.findViewById(R.id.rl_state);
            holder.choice = (ImageView) convertView.findViewById(R.id.tv_directState);
            holder.tv_tick = (ImageView) convertView.findViewById(R.id.tv_tick);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.cv_prograss = (CircleView) convertView.findViewById(R.id.cv_prograss);
            holder.cv_prograss.setStrokeWidth(1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //对应的实体类
        final DirectBean interviewVideoBean = mChapterTreeData.get(position);
        holder.tv_time.setText(interviewVideoBean.getZhibotime());
        if ((interviewVideoBean.getVideoType() == 0)) {
            setDirectTime(holder.tv_time, R.drawable.ic_hd_online, context.getString(R.string.hd_video), 0);
        } else {
            setDirectTime(holder.tv_time, R.drawable.ic_hd_online, interviewVideoBean.getZhibotime(), 1);

        }
        DebugUtil.e("Downloadaper" + interviewVideoBean.toString());
        //删除之后mChapterTreeDatas集合里面就没有了
        holder.tv_directTitle.setText(interviewVideoBean.getTitle());
        holder.tv_tick.setImageResource(interviewVideoBean.isCheck() ? R.drawable.ic_sc_xuanzhong : R.drawable.ic_sc_weixuan);
        if (interviewVideoBean.getStart() != null) {
            final int v = (int) (interviewVideoBean.getStart() / interviewVideoBean.getEnd() * 100);
            holder.cv_prograss.setProgress(v);
        }
        if (isEdit) {
            holder.choice.setVisibility(View.GONE);
            holder.cv_prograss.setVisibility(View.GONE);
            holder.tv_tick.setVisibility(View.VISIBLE);
        } else {

            if (interviewVideoBean.getVideo_status().equals("2")) {
                holder.choice.setVisibility(View.VISIBLE);
            } else {
                holder.choice.setVisibility(View.GONE);
            }


            holder.cv_prograss.setVisibility(View.VISIBLE);
            holder.tv_tick.setVisibility(View.GONE);
        }

        if (!StringUtil.isEmpty(interviewVideoBean.getDown_status())) {
            if (DownManageActivity.CCDOWNSTATE_WAIT == Integer.parseInt(interviewVideoBean.getDown_status())) {
                holder.cv_prograss.setVisibility(View.GONE);
                holder.choice.setImageResource(R.drawable.ic_zj_xiazai);
            } else if (DownManageActivity.CCDOWNSTATE_STAR == Integer.parseInt(interviewVideoBean.getDown_status())) {
                holder.cv_prograss.setVisibility(View.VISIBLE);
                holder.choice.setImageResource(R.drawable.ic_huancun);
            } else if (DownManageActivity.CCDOWNSTATE_PAUSE == Integer.parseInt(interviewVideoBean.getDown_status())) {
                holder.cv_prograss.setVisibility(View.VISIBLE);
                holder.choice.setImageResource(R.drawable.ic_xiazai_zhong);
            } else if (DownManageActivity.CCDOWNSTATE_COMPLETE == Integer.parseInt(interviewVideoBean.getDown_status())) {
                holder.cv_prograss.setVisibility(View.GONE);
                holder.choice.setImageResource(R.drawable.ic_video_completed);
            } else if (DownManageActivity.CCDOWNSTATE_DOWN_WAIT == Integer.parseInt(interviewVideoBean.getDown_status())) {
                holder.cv_prograss.setVisibility(View.GONE);
                holder.choice.setImageResource(R.drawable.xz_dengdai);
            } else {
                holder.cv_prograss.setVisibility(View.GONE);
                holder.choice.setImageResource(R.drawable.ic_xz_shibai);
            }
        } else {
            holder.cv_prograss.setVisibility(View.GONE);
            holder.choice.setImageResource(R.drawable.ic_zj_xiazai);
        }
        if (interviewVideoBean.getStart() != null) {
            int v = (int) (interviewVideoBean.getStart() * 100 / interviewVideoBean.getEnd());
            holder.cv_prograss.setProgress(v);
        } else {
            holder.cv_prograss.setProgress(100);
        }
        holder.rl_state.setTag(interviewVideoBean);
        holder.rl_state.setOnClickListener(OnClickListener);
        holder.tv_tick.setTag(interviewVideoBean);
        holder.tv_tick.setOnClickListener(OnClickListener);
        return convertView;
    }

    public class ViewHolder {
        public TextView tv_directTitle;
        public ImageView choice;
        public ImageView tv_tick;
        public RelativeLayout rl_state;
        public CircleView cv_prograss;
        public TextView tv_time;
    }

    /**
     * 设置textview 的drawable属性
     *
     * @param textView
     * @param drawableId 图片
     * @param text       内容
     * @param type       类型 0网课，显示高清视频 1直播或者录播课
     */
    private void setDirectTime(TextView textView, int drawableId, String text, int type) {
        if (type == 0) {
            Drawable drawable = context.getResources().getDrawable(drawableId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
            textView.setCompoundDrawablePadding(CommonUtils.dip2px(6));
            textView.setTextColor(context.getResources().getColor(R.color.green001));
            textView.setText(text);
        } else {
            textView.setCompoundDrawables(null, null, null, null);
            textView.setTextColor(context.getResources().getColor(R.color.gray013));
            textView.setText(text);
        }

    }
}
