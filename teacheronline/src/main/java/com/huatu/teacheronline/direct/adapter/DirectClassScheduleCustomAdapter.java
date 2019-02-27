package com.huatu.teacheronline.direct.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gensee.utils.StringUtil;
import com.greendao.DirectBean;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.DirectPlayDetailsActivityForBjysdk;
import com.huatu.teacheronline.direct.DownManageActivity;
import com.huatu.teacheronline.direct.PlayerActivityForBjysdk;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.ClickUtils;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.TeacherOnlineUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.PinnedHeaderListView;
import com.huatu.teacheronline.widget.PinnedHeaderListView.PinnedHeaderAdapter;
import com.huatu.teacheronline.widget.WithdrawalDialog;
import com.orhanobut.logger.Logger;
import com.ta.utdid2.android.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class DirectClassScheduleCustomAdapter extends BaseAdapter
        implements OnScrollListener, PinnedHeaderAdapter {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String TAG = DirectClassScheduleCustomAdapter.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private ArrayList<DirectBean> mData;
    private LayoutInflater mLayoutInflater;
    private VideoClick videoClick;
    private String account;//用户acc
    private String password;//用户密码
    public Activity context;
    private String directId;//当前播放的视频的id
    //1直播页面 2点播页面 3课程详情页面 4百家云点播页面(以前为cc网课页面)
    //live  vod   直接启动   bjy
    int directTypeActivity;
    DirectBean zDirectBean;//外层课程详情
    private Toast mToast;
    private TextView mTextView;

    // ===========================================================
    // Constructors
    // ===========================================================

    public DirectClassScheduleCustomAdapter(Activity pContext, ArrayList<DirectBean> pData) {
        context = pContext;
        mData = pData;
        mLayoutInflater = LayoutInflater.from(context);
        account = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, "");
        password = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PASSWORD, "");
    }

    public DirectClassScheduleCustomAdapter(Activity context) {
        this.context = context;
        account = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_ACCOUNT, "");
    }

    public void setDirectBeanList(ArrayList<DirectBean> directBeanList, String directId, int directTypeActivity) {
        this.mData = directBeanList;
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


    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // 常见的优化ViewHolder
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.direct_class_shecdule_item_rt_vod, null);
            holder.rl_class_shecdule = (RelativeLayout) convertView.findViewById(R.id.rl_class_shecdule);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_directTitle);
            holder.tv_state = (TextView) convertView.findViewById(R.id.tv_directState);
            holder.tv_withdrawal = (TextView) convertView.findViewById(R.id.tv_withdrawal);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_title2 = (TextView) convertView.findViewById(R.id.tv_title);
            holder.img_statePlay = (ImageView) convertView.findViewById(R.id.img_statePlay);
            holder.rl_state = (RelativeLayout) convertView.findViewById(R.id.rl_state);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 获取数据
        DirectBean directBean = (DirectBean) getItem(position);
        holder.progressBar.setVisibility(View.VISIBLE);
        if (needTitle(position)) {
            // 显示标题并设置内容
            DebugUtil.e("needTitle position" + position);
            holder.tv_title2.setText(directBean.getClassTitle());
            holder.tv_title2.setVisibility(View.VISIBLE);
        } else {
            // 内容项隐藏标题
            holder.tv_title2.setVisibility(View.GONE);
        }
        DebugUtil.e("directBean:" + directBean.toString());
        holder.tv_title.setText(directBean.getTitle());
        String code = directBean.getVideo_status();
        if (directBean.getVideoType() == 0) {
//            DirectBean directBean1 = daoUtils.queryDirectBeanForCCVedioId(userid, directBean.getCcCourses_id());
            if (/*directBean1 != null && */directBean.getDown_status() != null && Integer.parseInt(directBean.getDown_status()) == DownManageActivity
                    .CCDOWNSTATE_COMPLETE) {
//                holder.tv_time.setText(setDrawableLeft(); + "  已下载");
                String textStr = "<font color=\"#04CBAE\">" + context.getString(R.string.hd_video) + "</font>";
                setDirectTime(holder.tv_time, R.drawable.ic_hd_online, textStr, 0);
                TeacherOnlineUtils.setTextViewLeftImage(context, holder.tv_title, R.drawable.ic_downloaded, 5);
            } else {
                String textStr2 = "<font color=\"#04CBAE\">" + context.getString(R.string.hd_video) + "</font>";
                setDirectTime(holder.tv_time, R.drawable.ic_hd_online, textStr2, 0);
                TeacherOnlineUtils.setTextViewLeftRightImageNull(context, holder.tv_title);
            }
        } else {
//            DirectBean directBean2 = daoUtils.queryDirectBeanForGeeneVedioId(userid, directBean.getLubourl());
            String time = directBean.getZhibotime();
            if (directBean.getFstatus() == 2) {
                time = time + "   本讲已提现金额：" + directBean.getReturncash() + "元";
            }
            if (directBean.getDown_status() != null && Integer.parseInt(directBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_COMPLETE) {
                if (zDirectBean.getIs_buy().equals("0")) {
                    time = directBean.getIsTrial() == 1 ? time + "    试听" : time;
                }
                setDirectTime(holder.tv_time, R.drawable.ic_hd_online, time, 1);
                TeacherOnlineUtils.setTextViewLeftImage(context, holder.tv_title, R.drawable.ic_downloaded, 5);
            } else {
                if (zDirectBean.getIs_buy().equals("0")) {
                    time = directBean.getIsTrial() == 1 ? time + "    试听" : time;
                }
                setDirectTime(holder.tv_time, R.drawable.ic_hd_online, time, 1);
                TeacherOnlineUtils.setTextViewLeftRightImageNull(context, holder.tv_title);
            }
        }


        if ("0".equals(code)) {//正在直播
            DebugUtil.e(".equals(code):" + directBean.getZhibourl());
            holder.tv_state.setText("正在直播");
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.gray010));
            holder.tv_state.setTextColor(context.getResources().getColor(R.color.gray014));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.gray014));
            holder.tv_state.setVisibility(View.GONE);
            holder.img_statePlay.setVisibility(View.VISIBLE);
            holder.tv_withdrawal.setVisibility(View.VISIBLE);
            holder.img_statePlay.setBackgroundResource(R.drawable.bt_play_class);
        } else if ("2".equals(code)) {//观看课程
            holder.tv_state.setText("观看课程");
//            holder.tv_state.setVisibility(View.GONE);
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.gray010));
            holder.tv_state.setTextColor(context.getResources().getColor(R.color.gray014));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.gray014));
//            holder.tv_statePlay.setVisibility(View.VISIBLE);
            holder.tv_state.setVisibility(View.GONE);
            holder.img_statePlay.setVisibility(View.VISIBLE);
            holder.tv_withdrawal.setVisibility(View.VISIBLE);
            holder.img_statePlay.setBackgroundResource(R.drawable.bt_play_class);
        } else if ("1".equals(code)) {//即将直播
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.gray010));
            holder.tv_state.setText("即将直播");
            holder.tv_state.setTextColor(context.getResources().getColor(R.color.gray014));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.gray014));
            holder.tv_state.setVisibility(View.GONE);
            holder.img_statePlay.setVisibility(View.VISIBLE);
            holder.tv_withdrawal.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
            holder.img_statePlay.setBackgroundResource(R.drawable.bt_play_unable);
        } else if ("3".equals(code)) {//没地址
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.gray010));
            holder.tv_state.setText("暂无回放");
            holder.tv_state.setTextColor(context.getResources().getColor(R.color.gray014));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.gray014));
            holder.tv_state.setVisibility(View.GONE);
            holder.img_statePlay.setVisibility(View.VISIBLE);
            holder.tv_withdrawal.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
            holder.img_statePlay.setBackgroundResource(R.drawable.bt_play_unable);
        }

        if (!StringUtil.isEmpty(directBean.getBjyvideoid())) {
            holder.tv_state.setText("观看课程");
//            holder.tv_state.setVisibility(View.GONE);
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.gray010));
            holder.tv_state.setTextColor(context.getResources().getColor(R.color.gray014));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.green001));
            holder.tv_state.setVisibility(View.GONE);
            holder.img_statePlay.setVisibility(View.VISIBLE);
            holder.tv_withdrawal.setVisibility(View.VISIBLE);
            holder.img_statePlay.setBackgroundResource(R.drawable.bt_play_class);
        }

        if ((position + "").equals(directId) && "2".equals(code)) {
            holder.tv_state.setText("正在播放");
            holder.tv_state.setTextColor(context.getResources().getColor(R.color.green004));
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.green004));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.green004));
            holder.tv_state.setVisibility(View.GONE);
            holder.img_statePlay.setVisibility(View.VISIBLE);
            holder.img_statePlay.setBackgroundResource(R.drawable.bt_playing_class);
        }
        if ((position + "").equals(directId) && "0".equals(code)) {
            DebugUtil.e("setDirectId:" + directId + "getZhibourl:" + directBean.getZhibourl());
            holder.tv_state.setText("正在直播");
            holder.tv_state.setTextColor(context.getResources().getColor(R.color.green004));
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.green004));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.green004));
//            holder.tv_state.setVisibility(View.VISIBLE);
            holder.tv_state.setVisibility(View.GONE);
            holder.img_statePlay.setVisibility(View.VISIBLE);
            holder.img_statePlay.setBackgroundResource(R.drawable.bt_playing_class);
        }
        if (!StringUtils.isEmpty(directBean.getBjyvideoid()) && directBean.getBjyvideoid().equals(directId)) {
            holder.tv_state.setText("正在播放");
            holder.tv_state.setTextColor(context.getResources().getColor(R.color.green004));
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.green004));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.green004));
//            holder.tv_state.setVisibility(View.VISIBLE);
            holder.tv_state.setVisibility(View.GONE);
            holder.img_statePlay.setVisibility(View.VISIBLE);
            holder.img_statePlay.setBackgroundResource(R.drawable.bt_playing_class);
        }
        if (!StringUtils.isEmpty(directBean.getLubourl()) && directBean.getLubourl().equals(directId)) {
            holder.tv_state.setText("正在播放");
            holder.tv_state.setTextColor(context.getResources().getColor(R.color.green004));
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.green004));
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.green004));
//            holder.tv_state.setVisibility(View.VISIBLE);
            holder.tv_state.setVisibility(View.GONE);
            holder.img_statePlay.setVisibility(View.VISIBLE);
            holder.img_statePlay.setBackgroundResource(R.drawable.bt_playing_class);
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
                withDrawalDialog(holder);
            }
        });
        //todo
        if (directTypeActivity == 3 || directBean.getVideoType() == 0 || zDirectBean.getRid().equals("-1") ||
                directBean.getIsTrial() == 1 || directBean.getFstatus() == 2 || directBean.getReturncash() == 0) {
            holder.tv_withdrawal.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
        } else {

            if (directBean.getFstatus() == 0 || directBean.getProgress() == 0) {
                holder.tv_withdrawal.setTextColor(context.getResources().getColor(R.color.gray016));
                holder.tv_withdrawal.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_rectangle_glay_radius));
                holder.tv_withdrawal.setEnabled(false);
            } else {
                holder.tv_withdrawal.setTextColor(context.getResources().getColor(R.color.green004));
                holder.tv_withdrawal.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_rectangle_green_radius));
                holder.tv_withdrawal.setEnabled(true);
            }
            holder.progressBar.setProgress(directBean.getProgress());
        }


        if (Integer.parseInt(directBean.getIs_buy()) == 0) {

            if (directBean.getIsTrial() == 1) {
                holder.img_statePlay.setVisibility(View.VISIBLE);
            } else {
                holder.img_statePlay.setVisibility(View.INVISIBLE);
            }

        } else {
            holder.img_statePlay.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    /**
     * 提现弹窗
     *
     * @param holder
     */
    private void withDrawalDialog(final ViewHolder holder) {
        WithdrawalDialog mWithdrawalDialog = new WithdrawalDialog(context, (DirectBean) holder.tv_withdrawal.getTag(), zDirectBean.getRid(), zDirectBean.getOrderid());
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
        Logger.e("adpter click:" + GsonUtils.toJson(mDirectBean));

        if (directTypeActivity == 3) {
            detailVideo(mDirectBean, position);
        } else {
            liveVideo(mDirectBean, position);

        }


    }

    /**
     * 百家云点播界面
     *
     * @param mDirectBean
     * @param position
     */
    private void ccVideo(DirectBean mDirectBean, int position) {

        if ("0".equals(zDirectBean.getIs_buy()) && mDirectBean.getIsTrial() == 0) {
            showCustomToast(context.getResources().getString(R.string.pleaseBuy));
            return;
        }

        addClicks(mDirectBean);
        if (mDirectBean.getVideoType() == 0) {
            if (mDirectBean != null) {

                ((DirectPlayDetailsActivityForBjysdk) context).position = position;
                ((DirectPlayDetailsActivityForBjysdk) context).initData(mDirectBean);
                CommonUtils.putSharedPreferenceItem(null, mDirectBean.getRid(), String.valueOf(mDirectBean.getNumber()));
                setDirectId(mDirectBean.getBjyvideoid());
            } else {
                showCustomToast(context.getResources().getString(R.string.pleaseBuy));
                return;
            }
        } else {
            CommonUtils.putSharedPreferenceItem(null, mDirectBean.getRid(), String.valueOf(mDirectBean.getNumber()));
            switch (mDirectBean.getVideo_status()) {
                case "0":
                    context.finish();
                    if (videoClick != null) {
                        videoClick.setVideoClick();
                    }
                    PlayerActivityForBjysdk.newIntent(context, zDirectBean, position);
                    break;
                case "1":
                    showCustomToast(context.getString(R.string.will_play));
                    break;
                case "2":
                    context.finish();
                    if (videoClick != null) {
                        videoClick.setVideoClick();
                    }
                    PlayerActivityForBjysdk.newIntent(context, zDirectBean, position);
                    break;
                case "3":
                    showCustomToast(context.getString(R.string.no_replay));
                    break;
            }


        }
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
        addClicks(mDirectBean);
        CommonUtils.putSharedPreferenceItem(null, mDirectBean.getRid(), String.valueOf(mDirectBean.getNumber()));
//        if (mDirectBean.getVideoType() == 0) {
//            DirectPlayDetailsActivityForBjysdk.newIntent(context, zDirectBean, position);
//        } else {
//            //互动展示点播
//
//            switch (mDirectBean.getVideo_status()) {
//                case "0":
//                    LiveActivityForBjysdk.newIntent(context, zDirectBean, position);
//                    break;
//                case "1":
//                    showCustomToast(context.getString(R.string.will_play));
//                    break;
//                case "2":
//                    LiveActivityForBjysdk.newIntent(context, zDirectBean, position);
//                    break;
//                case "3":
//                    showCustomToast(context.getString(R.string.no_replay));
//                    break;
//            }
//
//
//        }

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
            ((PlayerActivityForBjysdk) context).initLiveRoom(mDirectBean, position);
        } else {

            switch (mDirectBean.getVideo_status()) {
                case "0":
                    setDirectId(position + "");
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
                    setDirectId(position + "");
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

    @Override
    public int getCount() {
        return (mData == null ? 0 : mData.size());
    }

    @Override
    public Object getItem(int position) {
        if (null != mData && position < getCount()) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) view).controlPinnedHeader(firstVisibleItem);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }


    @Override
    public int getPinnedHeaderState(int position) {
        if (getCount() == 0 || position < 0) {
            return PinnedHeaderAdapter.PINNED_HEADER_GONE;
        }

        if (isMove(position) == true) {
            return PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP;
        }

        return PinnedHeaderAdapter.PINNED_HEADER_VISIBLE;
    }


    @Override
    public void configurePinnedHeader(View headerView, int position, int alpaha) {
        // 设置标题的内容
        DirectBean itemEntity = (DirectBean) getItem(position);
        String headerValue = itemEntity.getClassTitle();

//        DebugUtil.e("header = " + headerValue);
        if (!TextUtils.isEmpty(headerValue)) {
            TextView headerTextView = (TextView) headerView.findViewById(R.id.tv_header);
            headerTextView.setText(headerValue);
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * 判断是否需要显示标题
     *
     * @param position
     * @return
     */
    private boolean needTitle(int position) {
        // 第一个肯定是分类
        if (position == 0) {
            return true;
        }

        // 异常处理
        if (position < 0) {
            return false;
        }

        // 当前  // 上一个
        DirectBean currentEntity = (DirectBean) getItem(position);
        DirectBean previousEntity = (DirectBean) getItem(position - 1);
        if (null == currentEntity || null == previousEntity) {
            return false;
        }

        String currentTitle = currentEntity.getClassTitle();
        String previousTitle = previousEntity.getClassTitle();
        if (null == previousTitle || null == currentTitle) {
            return false;
        }

        // 当前item分类名和上一个item分类名不同，则表示两item属于不同分类
        if (currentTitle.equals(previousTitle)) {
            return false;
        }

        return true;
    }


    private boolean isMove(int position) {
        // 获取当前与下一项
        DirectBean currentEntity = (DirectBean) getItem(position);
        DirectBean nextEntity = (DirectBean) getItem(position + 1);
        if (null == currentEntity || null == nextEntity) {
            return false;
        }

        // 获取两项header内容
        String currentTitle = currentEntity.getClassTitle();
        String nextTitle = nextEntity.getClassTitle();
        if (null == currentTitle || null == nextTitle) {
            return false;
        }

        // 当前不等于下一项header，当前项需要移动了
        if (!currentTitle.equals(nextTitle)) {
            return true;
        }

        return false;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    class ViewHolder {
        public TextView tv_title, tv_state, tv_time, tv_title2;
        private ImageView img_statePlay;
        public RelativeLayout rl_state;
        public RelativeLayout rl_class_shecdule;
        public TextView tv_withdrawal;
        public ProgressBar progressBar;
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
}
