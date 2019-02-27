package com.huatu.teacheronline.CCVideo;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.SupportPopupWindow;
import com.umeng.analytics.MobclickAgent;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 面试视频列表页面
 */
public class InterviewVideoListActivity extends BaseActivity {

    private RelativeLayout rl_main_left,ll_interview_form;
    private LinearLayout ll_top_interview_screening, ll_subject,  ll_grade;
    private TextView tv_main_title, tv_subject, tv_interview_form, tv_grade;
    private ImageView iv_subject, iv_interview_form, iv_grade;
    private boolean[] isSelectedArr;
    private PopupWindow pw_subject, pw_interviewForm, pw_grade;
    private String[] subjectItems;
    private String[] interviewFormItems;
    private String[] gradeItems;
    private String currentSubject = "";
    private String currentFormId = "", currentGradeId = "";
    private ListView listView;
    private InterviewListAdapter adapter;

    private View loadView;
    private View loadIcon;
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;
    //当前页
    private int currentPage = 1;
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private RelativeLayout rl_wifi;
    private PercentRelativeLayout rl_nodata;

    @Override
    public void initView() {
        setContentView(R.layout.activity_interview_video_list);
        isSelectedArr = new boolean[3];
        for (int index = 0; index < 3; index++) {
            isSelectedArr[index] = false;
        }
        initData();
        rl_nodata = (PercentRelativeLayout) findViewById(R.id.rl_nodata);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(getResources().getString(R.string.interview_video));
        ll_top_interview_screening = (LinearLayout) findViewById(R.id.ll_top_interview_screening);
        ll_subject = (LinearLayout) findViewById(R.id.ll_subject);
        ll_interview_form = (RelativeLayout) findViewById(R.id.ll_interview_form);
        ll_grade = (LinearLayout) findViewById(R.id.ll_grade);
        tv_subject = (TextView) findViewById(R.id.tv_subject);
        tv_interview_form = (TextView) findViewById(R.id.tv_interview_form);
        tv_grade = (TextView) findViewById(R.id.tv_grade);
        tv_subject.setText(getResources().getString(R.string.sub_interview));
        tv_interview_form.setText(getResources().getString(R.string.type_interview));
        tv_grade.setText(getResources().getString(R.string.section_interview));
        iv_subject = (ImageView) findViewById(R.id.iv_subject);
        iv_interview_form = (ImageView) findViewById(R.id.iv_interview_form);
        iv_grade = (ImageView) findViewById(R.id.iv_grade);
        listView = (ListView) findViewById(R.id.lv_interview_list);
        rl_wifi = (RelativeLayout) findViewById(R.id.rl_wifi);
        adapter = new InterviewListAdapter(this);

        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.pull_to_refresh_and_load_rotating);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        listView.setAdapter(adapter);

        loadData(true);
    }

    private void initData() {
        gradeItems = getResources().getStringArray(R.array.categoryArrForExamStage);
        interviewFormItems = getResources().getStringArray(R.array.video_form);
        // 先从sp中取值，如果取值失败，使用本地保存的初始值
        String videoSubjects = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_VIDEO_SUBJECTS, null);
        if (!TextUtils.isEmpty(videoSubjects) && videoSubjects.length() > 0) {
            subjectItems = videoSubjects.split(",");
        } else {
            subjectItems = getResources().getStringArray(R.array.video_subject);
        }
    }

    private void loadData(final boolean isReset) {
        if (isReset) {
            currentPage = 1;
        } else {
            currentPage++;
        }

        ObtatinDataListener obtatinDataListener = new ObtatinDataListener(isReset, this);
        SendRequest.getInterviewVideoList(currentSubject, currentFormId, currentGradeId, currentPage, obtatinDataListener);
    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<List<InterviewVideoBean>, String> {
        private WeakReference<InterviewVideoListActivity> weak_activity;
        private boolean isReset;

        public ObtatinDataListener(Boolean isReset, InterviewVideoListActivity activity) {
            weak_activity = new WeakReference<>(activity);
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity.get() != null) {
                weak_activity.get().isLoadEnd = false;
                weak_activity.get().listView.setVisibility(View.VISIBLE);
                weak_activity.get().rl_wifi.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSuccess(final List<InterviewVideoBean> res) {
            if (weak_activity.get() != null) {
                weak_activity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            weak_activity.get().flushContent_OnSucess(isReset, res);
                        }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity.get() != null) {
                weak_activity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.get().flushContent_OnFailure(isReset, res);
                    }
                });
            }
        }
    }

    public void flushContent_OnSucess(boolean isReset, List<InterviewVideoBean> res) {
        if (res == null || res.size() == 0) {
            hasMoreData = false;
            if (isReset) {
                adapter.getList().clear();//清空数据
                adapter.notifyDataSetChanged();
                ToastUtils.showToast(R.string.no_data);
                rl_nodata.setVisibility(View.VISIBLE);
            }
        } else {
            hasMoreData = true;
            if (isReset) {
                adapter.getList().clear();//清空数据
            }
            rl_nodata.setVisibility(View.GONE);
            adapter.getList().addAll(res);
            adapter.notifyDataSetChanged();
        }
        completeRefresh();
    }

    public void flushContent_OnFailure(boolean isReset, String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            if (isReset) {
                listView.setVisibility(View.GONE);
                rl_nodata.setVisibility(View.GONE);
                rl_wifi.setVisibility(View.VISIBLE);
            }
        } else if (SendRequest.ERROR_SERVER.equals(res)) {
            if (isReset) {
                adapter.getList().clear();//清空数据
            }
            ToastUtils.showToast(R.string.server_error);
        }
        completeRefresh();
    }

    private void completeRefresh() {
        if (listView != null && listView.getFooterViewsCount() > 0) {
            listView.removeFooterView(loadView);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        isLoadEnd = hasMoreData;
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        ll_subject.setOnClickListener(this);
        ll_interview_form.setOnClickListener(this);
        ll_grade.setOnClickListener(this);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (isLoadEnd) {
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            listView.addFooterView(loadView);
                            loadIcon.startAnimation(refreshingAnimation);
                            loadData(false);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.ll_subject:
                MobclickAgent.onEvent(this, "interviewSubject");
                if (isSelectedArr[0]) {
                    hidePop_subject();
                } else {
                    showPop_subject();
                    hidePop_interviewForm();
                    hidePop_grade();
                }
                break;
            case R.id.ll_interview_form:
                MobclickAgent.onEvent(this, "interviewForm");
                if (isSelectedArr[1]) {
                    hidePop_interviewForm();
                } else {
                    hidePop_subject();
                    showPop_interviewForm();
                    hidePop_grade();
                }
                break;
            case R.id.ll_grade:
                MobclickAgent.onEvent(this, "interviewStudySection");
                if (isSelectedArr[2]) {
                    hidePop_grade();
                } else {
                    hidePop_subject();
                    hidePop_interviewForm();
                    showPop_grade();
                }
                break;
        }
    }

    private void hidePop_subject() {
        isSelectedArr[0] = false;
        tv_subject.setTextColor(getResources().getColor(R.color.black));
        iv_subject.setImageResource(R.drawable.arrow_down_black);

        if (null != pw_subject && pw_subject.isShowing()) {
            pw_subject.setFocusable(false);
            pw_subject.dismiss();
        }
    }

    private void showPop_subject() {
        isSelectedArr[0] = true;
        tv_subject.setTextColor(getResources().getColor(R.color.green001));
        iv_subject.setImageResource(R.drawable.arrow_up_green);

        View customView = getLayoutInflater().inflate(R.layout.popupwindow_interview_screening, null, false);

        ListView lv_interview_subject = (ListView) customView.findViewById(R.id.lv_interview_screening);
        lv_interview_subject.setAdapter(new ScreeningAdapter(1, subjectItems));
        LinearLayout ll_hide_pop = (LinearLayout) customView.findViewById(R.id.ll_hide_pop);
        ll_hide_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePop_subject();
            }
        });

        pw_subject = new SupportPopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pw_subject.showAsDropDown(ll_top_interview_screening, 0, 0);
    }

    private void hidePop_interviewForm() {
        isSelectedArr[1] = false;
        tv_interview_form.setTextColor(getResources().getColor(R.color.black));
        iv_interview_form.setImageResource(R.drawable.arrow_down_black);

        if (null != pw_interviewForm && pw_interviewForm.isShowing()) {
            pw_interviewForm.setFocusable(false);
            pw_interviewForm.dismiss();
        }
    }

    private void showPop_interviewForm() {
        isSelectedArr[1] = true;
        tv_interview_form.setTextColor(getResources().getColor(R.color.green001));
        iv_interview_form.setImageResource(R.drawable.arrow_up_green);

        View customView = getLayoutInflater().inflate(R.layout.popupwindow_interview_screening, null, false);

        ListView lv_interview_form = (ListView) customView.findViewById(R.id.lv_interview_screening);
        lv_interview_form.setAdapter(new ScreeningAdapter(2, interviewFormItems));
        LinearLayout ll_hide_pop = (LinearLayout) customView.findViewById(R.id.ll_hide_pop);
        ll_hide_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePop_interviewForm();
            }
        });

        pw_interviewForm = new SupportPopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pw_interviewForm.showAsDropDown(ll_top_interview_screening, 0, 0);
    }

    private void hidePop_grade() {
        isSelectedArr[2] = false;
        tv_grade.setTextColor(getResources().getColor(R.color.black));
        iv_grade.setImageResource(R.drawable.arrow_down_black);

        if (null != pw_grade && pw_grade.isShowing()) {
            pw_grade.setFocusable(false);
            pw_grade.dismiss();
        }
    }

    private void showPop_grade() {
        isSelectedArr[2] = true;
        tv_grade.setTextColor(getResources().getColor(R.color.green001));
        iv_grade.setImageResource(R.drawable.arrow_up_green);

        View customView = getLayoutInflater().inflate(R.layout.popupwindow_interview_screening, null, false);

        ListView lv_interview_grade = (ListView) customView.findViewById(R.id.lv_interview_screening);
        lv_interview_grade.setAdapter(new ScreeningAdapter(3, gradeItems));
        LinearLayout ll_hide_pop = (LinearLayout) customView.findViewById(R.id.ll_hide_pop);
        ll_hide_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePop_grade();
            }
        });

        pw_grade = new SupportPopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pw_grade.showAsDropDown(ll_top_interview_screening, 0, 0);
    }

    private class ScreeningAdapter extends BaseAdapter {
        private int popPosition;
        private String[] screenings;

        public ScreeningAdapter(int popPosition, String[] screenings) {
            this.popPosition = popPosition;
            this.screenings = screenings;
        }

        @Override
        public int getCount() {
            if (screenings == null) {
                return 0;
            }
            return screenings.length;
        }

        @Override
        public String getItem(int position) {
            return screenings[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.listview_screening_item, null);

                viewHolder.ll_screening_item = (LinearLayout) convertView.findViewById(R.id.ll_screening_item);
                viewHolder.tv_screening_item = (TextView) convertView.findViewById(R.id.tv_screening_item);
                viewHolder.iv_screening_item = (ImageView) convertView.findViewById(R.id.iv_screening_item);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_screening_item.setText(screenings[position]);
            viewHolder.ll_screening_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(InterviewVideoListActivity.this, "interviewScreen");
                    notifyDataSetChanged();
                    if (popPosition == 1) {
                        currentSubject = subjectItems[position];
                        tv_subject.setText(subjectItems[position]);
                        hidePop_subject();
                    } else if (popPosition == 2) {
                        currentFormId = position + 1 + "";
                        tv_interview_form.setText(interviewFormItems[position]);
                        hidePop_interviewForm();
                    } else if (popPosition == 3) {
                        currentGradeId = position + 1 + "";
                        tv_grade.setText(gradeItems[position]);
                        hidePop_grade();
                    }
                    currentPage = 1;
                    loadData(true);
                }
            });

            if (popPosition == 1) {
                if (currentSubject.equals(subjectItems[position])) {
                    viewHolder.tv_screening_item.setTextColor(getResources().getColor(R.color.green001));
                    viewHolder.iv_screening_item.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.tv_screening_item.setTextColor(getResources().getColor(R.color.black));
                    viewHolder.iv_screening_item.setVisibility(View.INVISIBLE);
                }
            } else if (popPosition == 2) {
                if (currentFormId.equals(position + 1 + "")) {
                    viewHolder.tv_screening_item.setTextColor(getResources().getColor(R.color.green001));
                    viewHolder.iv_screening_item.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.tv_screening_item.setTextColor(getResources().getColor(R.color.black));
                    viewHolder.iv_screening_item.setVisibility(View.INVISIBLE);
                }
            } else if (popPosition == 3) {
                if (currentGradeId.equals(position + 1 + "")) {
                    viewHolder.tv_screening_item.setTextColor(getResources().getColor(R.color.green001));
                    viewHolder.iv_screening_item.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.tv_screening_item.setTextColor(getResources().getColor(R.color.black));
                    viewHolder.iv_screening_item.setVisibility(View.INVISIBLE);
                }
            }

            return convertView;
        }
    }

    private class ViewHolder {
        private LinearLayout ll_screening_item;
        private ImageView iv_screening_item;
        private TextView tv_screening_item;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != pw_subject && pw_subject.isShowing()) {
            pw_subject.setFocusable(false);
            pw_subject.dismiss();
        }
        if (null != pw_interviewForm && pw_interviewForm.isShowing()) {
            pw_interviewForm.setFocusable(false);
            pw_interviewForm.dismiss();
        }
        if (null != pw_grade && pw_grade.isShowing()) {
            pw_grade.setFocusable(false);
            pw_grade.dismiss();
        }
    }
}
