package com.huatu.teacheronline.direct.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Created by kinndann on 2018/8/6/006.
 * description:
 */

public class CourseWareSimpleAdapter extends BaseExpandableListAdapter {

    private final Activity context;
    private final String account;
    private final String password;
    private ArrayList<ArrayList<DirectBean>> mGroupDatas = new ArrayList<>();
    private String directId;
    private DirectBean zDirectBean;
    private final LayoutInflater mInflater;
    private Toast mToast;
    private TextView mTextView;
    private ExpandableListView mListview;


    public CourseWareSimpleAdapter(Activity context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        account = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, "");
        password = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PASSWORD, "");
    }

    public void setDirectBeanList(ArrayList<DirectBean> directBeanList, String directId) {

        convertData(directBeanList);

        for (int i = 0; i < mGroupDatas.size(); i++) {

            mListview.expandGroup(i);
        }

        this.directId = directId;
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
            vh.view_bg = view.findViewById(R.id.bg_course_ware_simple_group);
            view.setTag(vh);

        } else {
            vh = (GroupViewHolder) view.getTag();
        }
        vh.view_bg.setBackgroundColor(context.getResources().getColor(R.color.black));

        vh.tv_tittle.setText(getChild(i, 0).getClassTitle());
        vh.tv_tittle.setTextColor(context.getResources().getColor(R.color.white));

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
        View view_bg;


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
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.img_statePlay = (ImageView) convertView.findViewById(R.id.img_statePlay);
            holder.img_video_status = (ImageView) convertView.findViewById(R.id.img_courseware_video_status);
            holder.img_download = (ImageView) convertView.findViewById(R.id.img_courseware_download);
            holder.rl_state = (RelativeLayout) convertView.findViewById(R.id.rl_state);
            holder.view_bg = convertView.findViewById(R.id.rel_course_ware_bg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.view_bg.setBackgroundColor(context.getResources().getColor(R.color.black));

        final int position = getOrgPosition(groupId, childId);
        // 获取数据
        final DirectBean directBean = getChild(groupId, childId);
        holder.tv_title.setText(directBean.getTitle());
        String code = directBean.getVideo_status();
        holder.img_download.setVisibility(View.GONE);

        holder.tv_title.setTextColor(context.getResources().getColor(R.color.white));
        holder.tv_time.setTextColor(context.getResources().getColor(R.color.white));

        if (Integer.parseInt(directBean.getIs_buy()) == 0) {

            if (directBean.getIsTrial() == 1) {
                holder.img_statePlay.setVisibility(View.VISIBLE);
            } else {
                holder.img_statePlay.setVisibility(View.INVISIBLE);
            }

        } else {
            holder.img_statePlay.setVisibility(View.VISIBLE);
        }

        if (directBean.getVideoType() == 0) {
            holder.tv_time.setVisibility(View.GONE);
            holder.img_statePlay.setImageResource(R.drawable.bt_play_class);
            holder.img_video_status.setImageResource(R.drawable.ic_hd);

            if ((position + "").equals(directId)) {
                holder.tv_title.setTextColor(context.getResources().getColor(R.color.green004));
                holder.tv_time.setTextColor(context.getResources().getColor(R.color.green004));
                holder.img_statePlay.setImageResource(R.drawable.bt_playing_class);
            }


        } else {
            holder.tv_time.setVisibility(View.VISIBLE);
            holder.img_video_status.setVisibility(View.VISIBLE);
            String time = directBean.getZhibotime();
            holder.tv_time.setText(time);

            if ((position + "").equals(directId)) {
                holder.tv_title.setTextColor(context.getResources().getColor(R.color.green004));
                holder.tv_time.setTextColor(context.getResources().getColor(R.color.green004));
                holder.img_statePlay.setImageResource(R.drawable.bt_playing_class);
            }


            if ("0".equals(code)) {//正在直播
                holder.img_statePlay.setImageResource(R.drawable.bt_play_class);
                holder.img_video_status.setImageResource(R.drawable.ic_courseware_live);
            } else if ("2".equals(code)) {//观看课程
                holder.img_statePlay.setImageResource(R.drawable.bt_play_class);
                holder.img_video_status.setImageResource(R.drawable.ic_courseware_playback);
            } else if ("1".equals(code)) {//即将直播
//            holder.progressBar.setVisibility(View.GONE);
                holder.img_statePlay.setImageResource(R.drawable.bt_play_unable);
                holder.img_video_status.setImageResource(R.drawable.ic_courseware_live);
            } else if ("3".equals(code)) {//没地址
                holder.img_statePlay.setImageResource(R.drawable.bt_play_unable);
                holder.img_video_status.setImageResource(R.drawable.ic_courseware_playback_none);
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


        if (Integer.parseInt(directBean.getIs_buy()) == 0) {

            if (directBean.getIsTrial() == 1) {
                holder.img_statePlay.setVisibility(View.VISIBLE);
            } else {
                holder.img_statePlay.setVisibility(View.INVISIBLE);
                holder.tv_title.setTextColor(context.getResources().getColor(R.color.gray013));
                holder.tv_time.setTextColor(context.getResources().getColor(R.color.gray013));
            }

        } else {
            holder.img_statePlay.setVisibility(View.VISIBLE);
        }

        if (!CommonUtils.netAvailable && (directBean.getDown_status() == null || !directBean.getDown_status().equals(DownManageActivity.CCDOWNSTATE_COMPLETE + ""))) {
            holder.img_statePlay.setImageResource(R.drawable.bt_play_unable);
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.gray013));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.gray013));
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
        public ImageView img_video_status, img_download;
        public View view_bg;
    }


    /**
     * 播放视频
     *
     * @param mDirectBean
     * @param position
     */
    public void playVideo(DirectBean mDirectBean, int position) {
        Logger.d("adpter click:" + GsonUtils.toJson(mDirectBean));

        liveVideo(mDirectBean, position);


    }


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

        if (!CommonUtils.netAvailable && (mDirectBean.getDown_status() == null || !mDirectBean.getDown_status().equals(DownManageActivity.CCDOWNSTATE_COMPLETE + ""))) {

            showCustomToast("无法播放!当前网络异常且该课件未下载!");
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
