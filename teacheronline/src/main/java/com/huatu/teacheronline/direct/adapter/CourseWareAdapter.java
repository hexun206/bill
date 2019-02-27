package com.huatu.teacheronline.direct.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greendao.DirectBean;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.DownManageActivity;
import com.huatu.teacheronline.direct.PlayerActivityForBjysdk;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.ClickUtils;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.WithdrawalDialog;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Created by kinndann on 2018/8/6/006.
 * description:
 */

public class CourseWareAdapter extends BaseExpandableListAdapter {

    private final Activity context;
    private final String account;
    private final String password;
    private ArrayList<ArrayList<DirectBean>> mGroupDatas = new ArrayList<>();
    private String directId;
    private int directTypeActivity;
    private DirectBean zDirectBean;
    private final LayoutInflater mInflater;
    private VideoClick videoClick;
    private Toast mToast;
    private TextView mTextView;
    private ExpandableListView mListview;

    /**
     * 是否是详情页的列表
     */
    private boolean isDetail;


    public CourseWareAdapter(Activity context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        account = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, "");
        password = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PASSWORD, "");
    }

    public void isFromDetail() {
        isDetail = true;
        notifyDataSetChanged();
    }

    public void setDirectBeanList(ArrayList<DirectBean> directBeanList, String directId, int directTypeActivity) {
        mGroupDatas.clear();
        convertData(directBeanList);

        for (int i = 0; i < mGroupDatas.size(); i++) {

            mListview.expandGroup(i);
        }

        this.directId = directId;
        this.directTypeActivity = directTypeActivity;
        notifyDataSetChanged();
    }

    public void seDirectBean(DirectBean DirectBean) {
        this.zDirectBean = DirectBean;
    }

    public void setDirectId(String directId) {
        this.directId = directId;
        DebugUtil.e("setDirectId:" + directId);
        notifyDataSetChanged();
    }

    private void convertData(ArrayList<DirectBean> directBeanList) {

        if (directBeanList == null || directBeanList.size() == 0) {
            return;
        }


        for (DirectBean directBean : directBeanList) {
            if (mGroupDatas.size() == 0) {
                ArrayList<DirectBean> childDatas = new ArrayList<>();
                childDatas.add(directBean);
                mGroupDatas.add(childDatas);

            } else {
                ArrayList<DirectBean> lastGroupDatas = mGroupDatas.get(mGroupDatas.size() - 1);
                if (directBean.getClassTitle().equals(lastGroupDatas.get(0).getClassTitle())) {
                    lastGroupDatas.add(directBean);
                } else {
                    ArrayList<DirectBean> childDatas = new ArrayList<>();
                    childDatas.add(directBean);
                    mGroupDatas.add(childDatas);
                }


            }


        }


    }


    @Override
    public int getGroupCount() {
        return mGroupDatas.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mGroupDatas.get(i).size();
    }

    @Override
    public ArrayList<DirectBean> getGroup(int i) {
        return mGroupDatas.get(i);
    }

    @Override
    public DirectBean getChild(int i, int i1) {
        return getGroup(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GroupViewHolder vh = null;
        if (view == null) {
            vh = new GroupViewHolder();
            view = mInflater.inflate(R.layout.item_course_ware_group, viewGroup, false);
            vh.img_state = (ImageView) view.findViewById(R.id.img_course_ware_group_state);
            vh.tv_tittle = (TextView) view.findViewById(R.id.tv_course_ware_group_title);
            view.setTag(vh);

        } else {
            vh = (GroupViewHolder) view.getTag();
        }


        vh.tv_tittle.setText(getChild(i, 0).getClassTitle());

//        int displayColor = context.getResources().getColor(R.color.colorAccent);
//        int hideColor = context.getResources().getColor(R.color.colorPrimary);
//        vh.img_state.setBackgroundColor(b ? displayColor : hideColor);
        vh.img_state.setImageResource(b ? R.drawable.ic_course_ware_group_arrow_up : R.drawable.ic_course_ware_group_arrow_down);
        return view;
    }

    public void bind(ExpandableListView listview) {
        mListview = listview;
    }

    public void setSelection(int selection) {
        if (mListview != null) {

            int groupId = 0;
            int childId = selection;

            for (int i = 0; i < mGroupDatas.size(); i++) {
                ArrayList<DirectBean> directBeans = mGroupDatas.get(i);
                if (childId > directBeans.size()) {
                    childId -= directBeans.size();

                } else {
                    groupId = i;
                    break;

                }


            }

            mListview.setSelectedChild(groupId, childId, true);
        }


    }

    private static class GroupViewHolder {
        TextView tv_tittle;
        ImageView img_state;


    }

    @Override
    public View getChildView(final int groupId, final int childId, boolean b, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_courseware_content, null);
            holder.rl_class_shecdule = (RelativeLayout) convertView.findViewById(R.id.rl_class_shecdule);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_directTitle);
//            holder.tv_state = (TextView) convertView.findViewById(R.id.tv_directState);
            holder.tv_withdrawal = (TextView) convertView.findViewById(R.id.tv_withdrawal);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.img_statePlay = (ImageView) convertView.findViewById(R.id.img_statePlay);
            holder.img_video_status = (ImageView) convertView.findViewById(R.id.img_courseware_video_status);
            holder.img_download = (ImageView) convertView.findViewById(R.id.img_courseware_download);
            holder.ll_withdraw = (LinearLayout) convertView.findViewById(R.id.ll_courseware_withdraw);
            holder.rl_state = (RelativeLayout) convertView.findViewById(R.id.rl_state);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final int position = getOrgPosition(groupId, childId);
        // 获取数据
        final DirectBean directBean = getChild(groupId, childId);
        holder.progressBar.setVisibility(View.VISIBLE);
        DebugUtil.e("directBean:" + directBean.toString());
        holder.tv_title.setText(directBean.getTitle());
        String code = directBean.getVideo_status();
        holder.img_download.setVisibility(View.GONE);
        holder.tv_title.setTextColor(context.getResources().getColor(R.color.gray010));
        holder.tv_time.setTextColor(context.getResources().getColor(R.color.gray014));

        if (/*directBean1 != null && */directBean.getDown_status() != null && Integer.parseInt(directBean.getDown_status()) == DownManageActivity
                .CCDOWNSTATE_COMPLETE) {
//                holder.tv_time.setText(setDrawableLeft(); + "  已下载");
//                String textStr = "<font color=\"#04CBAE\">" + context.getString(R.string.hd_video) + "</font>";
//                setDirectTime(holder.tv_time, R.drawable.ic_hd_online, textStr, 0);
//                TeacherOnlineUtils.setTextViewLeftImage(context, holder.tv_title, R.drawable.ic_downloaded, 5);

            holder.img_download.setVisibility(View.VISIBLE);

        }

        holder.img_statePlay.setImageResource(R.drawable.bt_play_class);
        holder.img_video_status.setImageResource(R.drawable.ic_hd);


        if (directBean.getVideoType() == 0) {
//            DirectBean directBean1 = daoUtils.queryDirectBeanForCCVedioId(userid, directBean.getCcCourses_id());
            holder.tv_time.setVisibility(View.GONE);
            if ((position + "").equals(directId)) {
                holder.tv_title.setTextColor(context.getResources().getColor(R.color.green004));
                holder.tv_time.setTextColor(context.getResources().getColor(R.color.green004));
                holder.img_statePlay.setImageResource(R.drawable.bt_playing_class);
            }


        } else {
            holder.tv_time.setVisibility(View.VISIBLE);
            holder.img_video_status.setVisibility(View.VISIBLE);
//            DirectBean directBean2 = daoUtils.queryDirectBeanForGeeneVedioId(userid, directBean.getLubourl());
            String time = directBean.getZhibotime();
            if (directBean.getFstatus() == 2) {
                time = time + "   本讲已提现金额：" + directBean.getReturncash() + "元";
            }
            if (directBean.getDown_status() != null && Integer.parseInt(directBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_COMPLETE) {
                if (zDirectBean.getIs_buy().equals("0")) {
                    time = directBean.getIsTrial() == 1 ? time + "    试听" : time;
                }

            } else {
                if (zDirectBean.getIs_buy().equals("0")) {
                    time = directBean.getIsTrial() == 1 ? time + "    试听" : time;
                }
            }
            holder.tv_time.setText(time);


            if ("0".equals(code)) {//正在直播
                holder.img_statePlay.setImageResource(R.drawable.bt_play_class);
                holder.img_video_status.setImageResource(R.drawable.ic_courseware_live);
            } else if ("2".equals(code)) {//观看课程
//            holder.tv_state.setVisibility(View.GONE);
                holder.tv_title.setTextColor(context.getResources().getColor(R.color.gray010));
                holder.tv_time.setTextColor(context.getResources().getColor(R.color.gray014));
//            holder.tv_statePlay.setVisibility(View.VISIBLE);
                holder.img_statePlay.setImageResource(R.drawable.bt_play_class);
                holder.img_video_status.setImageResource(R.drawable.ic_courseware_playback);
            } else if ("1".equals(code)) {//即将直播
//            holder.progressBar.setVisibility(View.GONE);
                holder.img_statePlay.setImageResource(R.drawable.bt_play_unable);
                holder.img_video_status.setImageResource(R.drawable.ic_courseware_live);
            } else if ("3".equals(code)) {//没地址
//            holder.progressBar.setVisibility(View.GONE);
                holder.img_statePlay.setImageResource(R.drawable.bt_play_unable);
                holder.img_video_status.setImageResource(R.drawable.ic_courseware_playback_none);
            }

            if ((position + "").equals(directId)) {
                holder.tv_title.setTextColor(context.getResources().getColor(R.color.green004));
                holder.tv_time.setTextColor(context.getResources().getColor(R.color.green004));
                holder.img_statePlay.setImageResource(R.drawable.bt_playing_class);
            }
        }


        holder.rl_class_shecdule.setTag(directBean);
        holder.rl_class_shecdule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ClickUtils.isFastClick()) {
                    return;
                }


                DirectBean mDirectBean = (DirectBean) holder.rl_class_shecdule.getTag();
                MobclickAgent.onEvent(context, "courseScheduleOnIntemClick");


                playVideo(mDirectBean, position);
            }
        });
        holder.tv_withdrawal.setTag(directBean);
        holder.tv_withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (directBean.getFstatus() == 0 || directBean.getProgress() == 0) {

                    showCustomToast("您的听课时间太短，请加油听课，再来提现～");

                    return;
                }


                withDrawalDialog(holder);
            }
        });
        if (directBean.getVideoType() == 0 || zDirectBean.getRid().equals("-1") ||
                directBean.getIsTrial() == 1 || directBean.getFstatus() == 2 || directBean.getReturncash() == 0) {
            holder.ll_withdraw.setVisibility(View.GONE);


        } else {
            holder.ll_withdraw.setVisibility(View.VISIBLE);
            if (directBean.getFstatus() == 0 || directBean.getProgress() == 0) {
                holder.tv_withdrawal.setTextColor(context.getResources().getColor(R.color.white));
                holder.tv_withdrawal.setBackgroundResource(R.drawable.ic_withdraw_unable);
            } else {
                holder.tv_withdrawal.setTextColor(context.getResources().getColor(R.color.white));
                holder.tv_withdrawal.setBackgroundResource(R.drawable.ic_withdraw);
            }
            holder.progressBar.setProgress(directBean.getProgress());
        }


        if (Integer.parseInt(directBean.getIs_buy()) == 0) {

            if (directBean.getIsTrial() == 1) {
                holder.img_statePlay.setVisibility(View.VISIBLE);
            } else {


                if (isDetail) {
                    holder.tv_title.setTextColor(context.getResources().getColor(R.color.gray010));
                    holder.img_statePlay.setVisibility(View.GONE);
                } else {
                    holder.tv_title.setTextColor(context.getResources().getColor(R.color.gray013));
                    holder.img_statePlay.setVisibility(View.INVISIBLE);
                }
            }

        } else {
            holder.img_statePlay.setVisibility(View.VISIBLE);
        }

        return convertView;
    }


    private int getOrgPosition(int groupId, int childId) {
        int position = childId;
        for (int i = 0; i < groupId; i++) {
            position += mGroupDatas.get(i).size();
        }
        return position;
    }

    private static class ViewHolder {
        public TextView tv_title, /*tv_state,*/
                tv_time;
        private ImageView img_statePlay;
        public RelativeLayout rl_state;
        public RelativeLayout rl_class_shecdule;
        public TextView tv_withdrawal;
        public ProgressBar progressBar;
        public LinearLayout ll_withdraw;
        public ImageView img_video_status, img_download;
    }

    public interface VideoClick {
        void setVideoClick();
    }

    public void setVideoClickbutton(VideoClick videoClick) {
        this.videoClick = videoClick;
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
            textView.setText(Html.fromHtml(text));
        } else {
            textView.setCompoundDrawables(null, null, null, null);
            textView.setTextColor(context.getResources().getColor(R.color.gray013));
            textView.setText(text);
        }

    }


    /**
     * 提现弹窗
     *
     * @param holder
     */
    private void withDrawalDialog(final ViewHolder holder) {
        WithdrawalDialog mWithdrawalDialog = new WithdrawalDialog((Activity) context, (DirectBean) holder.tv_withdrawal.getTag(), zDirectBean.getRid(), zDirectBean.getOrderid());
        mWithdrawalDialog.show();
        mWithdrawalDialog.setOnWithdrawaledListener(new WithdrawalDialog.OnWithdrawaledListener() {
            @Override
            public void submitCompleted(String res) {
                DirectBean mDirectBean = (DirectBean) holder.tv_withdrawal.getTag();
                mDirectBean.setFstatus(2);
                notifyDataSetChanged();
            }
        });
    }

    /**
     * 播放视频
     *
     * @param mDirectBean
     * @param position
     */
    public void playVideo(DirectBean mDirectBean, int position) {
        Logger.d("adpter click:" + GsonUtils.toJson(mDirectBean));

        if (directTypeActivity == 3) {
            detailVideo(mDirectBean, position);
        } else {
            liveVideo(mDirectBean, position);

        }

//            if (directTypeActivity == 4) {
//            ccVideo(mDirectBean, position);
//        } else {
//            liveVideo(mDirectBean, position);
//        }


    }

//    /**
//     * 百家云点播界面
//     *
//     * @param mDirectBean
//     * @param position
//     */
//    private void ccVideo(DirectBean mDirectBean, int position) {
//
//        if ("0".equals(zDirectBean.getIs_buy()) && mDirectBean.getIsTrial() == 0) {
//            showCustomToast(context.getResources().getString(R.string.pleaseBuy));
//            return;
//        }
//
//        addClicks(mDirectBean);
//        if (mDirectBean.getVideoType() == 0) {
//            if (mDirectBean != null) {
//
//                ((DirectPlayDetailsActivityForBjysdk) context).position = position;
//                ((DirectPlayDetailsActivityForBjysdk) context).initData(mDirectBean);
//                CommonUtils.putSharedPreferenceItem(null, mDirectBean.getRid(), String.valueOf(mDirectBean.getNumber()));
//                setDirectId(mDirectBean.getBjyvideoid());
//            } else {
//                showCustomToast(context.getResources().getString(R.string.pleaseBuy));
//                return;
//            }
//        } else {
//            CommonUtils.putSharedPreferenceItem(null, mDirectBean.getRid(), String.valueOf(mDirectBean.getNumber()));
//            switch (mDirectBean.getVideo_status()) {
//                case "0":
//                    context.finish();
//                    if (videoClick != null) {
//                        videoClick.setVideoClick();
//                    }
//                    LiveActivityForBjysdk.newIntent(context, zDirectBean, position);
//                    break;
//                case "1":
//                    showCustomToast(context.getString(R.string.will_play));
//                    break;
//                case "2":
//                    context.finish();
//                    if (videoClick != null) {
//                        videoClick.setVideoClick();
//                    }
//                    LiveActivityForBjysdk.newIntent(context, zDirectBean, position);
//                    break;
//                case "3":
//                    showCustomToast(context.getString(R.string.no_replay));
//                    break;
//            }
//
//
//        }
//    }


    public void showCustomToast(String content) {


        if (mToast == null) {
            View v = LayoutInflater.from(context).inflate(R.layout.bg_toast, null);
            mTextView = (TextView) v.findViewById(R.id.tv_content);
            mToast = new Toast(context);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setView(v);
        }


        mTextView.setText(content);
        mToast.show();
    }


    /**
     * 详情页面  直接跳转
     *
     * @param mDirectBean
     * @param position
     */
    private void detailVideo(DirectBean mDirectBean, int position) {

        if ("0".equals(zDirectBean.getIs_buy()) && mDirectBean.getIsTrial() == 0) {
            showCustomToast(context.getResources().getString(R.string.pleaseBuy));
            return;
        }

        if (mDirectBean.getVideoType() == 1) {
            switch (mDirectBean.getVideo_status()) {
                case "1":
                    showCustomToast(context.getString(R.string.will_play));
                    return;
                case "3":
                    showCustomToast(context.getString(R.string.no_replay));
                    return;
            }

        }

        addClicks(mDirectBean);
        CommonUtils.putSharedPreferenceItem(null, mDirectBean.getRid(), String.valueOf(mDirectBean.getNumber()));

        PlayerActivityForBjysdk.newIntent(context, zDirectBean, position);
    }


    /**
     * 直播页面逻辑处理
     *
     * @param mDirectBean
     * @param position
     */
    private void liveVideo(DirectBean mDirectBean, int position) {

        if ("0".equals(zDirectBean.getIs_buy()) && mDirectBean.getIsTrial() == 0) {
            showCustomToast(context.getResources().getString(R.string.pleaseBuy));
            return;
        }
        addClicks(mDirectBean);
        CommonUtils.putSharedPreferenceItem(null, mDirectBean.getRid(), String.valueOf(mDirectBean.getNumber()));
        if (mDirectBean.getVideoType() == 0) {
//            context.finish();
//            if (videoClick != null) {
//                videoClick.setVideoClick();
//            }
//            PlayerActivityForBjysdk.newIntent(context, zDirectBean, position);
//            setDirectId(position + "");
            ((PlayerActivityForBjysdk) context).initLiveRoom(mDirectBean, position);
        } else {

            switch (mDirectBean.getVideo_status()) {
                case "0":
//                    setDirectId(position + "");
                    ((PlayerActivityForBjysdk) context).initLiveRoom(mDirectBean, position);
//                    context.finish();
//                    if (videoClick != null) {
//                        videoClick.setVideoClick();
//                    }
//                    LiveActivityForBjysdk.newIntent(context, zDirectBean, position);


                    break;
                case "1":
                    showCustomToast(context.getString(R.string.will_play));
                    break;
                case "2":
//                    setDirectId(position + "");
                    ((PlayerActivityForBjysdk) context).initLiveRoom(mDirectBean, position);
//                    context.finish();
//                    if (videoClick != null) {
//                        videoClick.setVideoClick();
//                    }
//                    LiveActivityForBjysdk.newIntent(context, zDirectBean, position);
                    break;
                case "3":
                    showCustomToast(context.getString(R.string.no_replay));
                    break;
            }


        }


    }

    void addClicks(DirectBean mDirectBean) {
        SendRequest.addCliks(account, mDirectBean.getNumber(), zDirectBean.getRid(), zDirectBean.getTitle(), mDirectBean.getTitle(), password, mDirectBean
                .getVideoType() + "", new
                ObtainDataFromNetListener<String, String>() {
                    @Override
                    public void onSuccess(String res) {
                    }

                    @Override
                    public void onFailure(String res) {
                    }
                });
    }


}
