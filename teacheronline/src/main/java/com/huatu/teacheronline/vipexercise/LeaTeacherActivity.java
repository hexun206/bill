package com.huatu.teacheronline.vipexercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.vipexercise.vipbean.LeaTeacherBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * VIP老师留言详情
 * Created by ply on 2016/1/5.
 */
public class LeaTeacherActivity extends BaseActivity {


    private SwipeRefreshLayout mPullToRefreshLayout;
    private ListView listView;
    private View loadView;
    private View loadIcon;
    private RotateAnimation refreshingAnimation;
    private List<LeaTeacherBean.DataEntity> directBeanListPDFForClassSchedule = new ArrayList<>();
    private LeavMessageAdapter leavMessageAdapter;
    private String teacherId;
    private String teacherName;
    private String uid;
    private TextView tv_send;
    private EditText ed_addleat;

    //    private int index=1;
//    private int limit = 9;
//    private boolean isLoadEnd;
//    private boolean hasMoreData = true;
    @Override
    public void initView() {
        setContentView(R.layout.activity_lea_teacher);
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        TextView tv_title = (TextView) findViewById(R.id.tv_main_title);
        tv_send = (TextView) findViewById(R.id.tv_send);
        ed_addleat = (EditText) findViewById(R.id.ed_addleat);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_refresh_layout);
        listView = (ListView) findViewById(R.id.listview);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        teacherId = getIntent().getStringExtra("teacherId");
        teacherName = getIntent().getStringExtra("teacherName");
        tv_title.setText(teacherName);
        loadLiveData();
    }

    private void loadLiveData() {
        ObtainDataLister obtainDataLister =new ObtainDataLister(this);
        SendRequest.getleavedetail(uid, teacherId, obtainDataLister);
    }

    private void addstuentData() {
        AddstuentDataLister addstdentDataLister =new AddstuentDataLister(this);
        SendRequest.getaddstudent(uid, teacherId, ed_addleat.getText().toString().trim(), addstdentDataLister);
    }

    @Override
    public void setListener() {
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        tv_send.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                directBeans.clear();
//                adapter.setDirectBeanList(directBeans);
//                adapter.notifyDataSetChanged();
//                currentPage = 1;
                loadLiveData();
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    if (isLoadEnd) {
//                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
//                            listView.addFooterView(loadView);
//                            loadIcon.startAnimation(refreshingAnimation);
//                            loadLiveData(false, currentPage);
//                        }
//                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_main_left:
                back();
                break;
            case R.id.tv_send:
                if (ed_addleat.getText().toString().equals("")){
                    ToastUtils.showToast("留言内容不能为空！");
                    return;
                }
                addstuentData();
                closeKeybord(ed_addleat, this); // 关闭软键盘
                listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                break;
        }
    }

    /**
     * 留言详情适配器
     * Created by ljyu on 2017/2/24.
     */
    public class LeavMessageAdapter extends BaseAdapter {
        public Context context;
        private List<LeaTeacherBean.DataEntity> leabean;

        public LeavMessageAdapter(Context context, List<LeaTeacherBean.DataEntity> leabean) {
            this.context = context;
            this.leabean = leabean;
        }
        @Override
        public int getCount() {
            return (leabean == null ? 5 : leabean.size());
        }

        @Override
        public LeaTeacherBean.DataEntity getItem(int position) {
            return (leabean == null ? null : leabean.get(position));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        class ViewHolder {

            public RelativeLayout rl_time_leav_message;
            public RelativeLayout rl_left_teacher;
            public RelativeLayout rl_right_me;
            public TextView tv_time_leav_message;
            public TextView tv_teacher_leav_message;
            public TextView tv_me_leav_message;
            public SimpleDraweeView img_head_leav_message;
            public SimpleDraweeView img_headtv_me_leav_message;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_leav_message_layout, null);
                holder.rl_time_leav_message = (RelativeLayout)convertView.findViewById(R.id.rl_tv_time_leav_message);
                holder.rl_left_teacher = (RelativeLayout)convertView.findViewById(R.id.rl_left_teacher);
                holder.rl_right_me = (RelativeLayout)convertView.findViewById(R.id.rl_right_me);
                holder.tv_time_leav_message = (TextView) convertView.findViewById(R.id.tv_time_leav_message);
                holder.tv_teacher_leav_message = (TextView) convertView.findViewById(R.id.tv_teacher_leav_message);
                holder.tv_me_leav_message = (TextView) convertView.findViewById(R.id.tv_me_leav_message);
                holder.img_head_leav_message= (SimpleDraweeView) convertView.findViewById(R.id.img_head_leav_message);
                holder.img_headtv_me_leav_message= (SimpleDraweeView) convertView.findViewById(R.id.img_headtv_me_leav_message);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            if (leabean.get(position).getType()==1){//type1 老师  0学生
                holder.rl_right_me.setVisibility(View.GONE);
                holder.rl_left_teacher.setVisibility(View.VISIBLE);
                holder.tv_teacher_leav_message.setText(leabean.get(position).getContent());
            }else{
                holder.rl_right_me.setVisibility(View.VISIBLE);
                holder.rl_left_teacher.setVisibility(View.GONE);
                holder.tv_me_leav_message.setText(leabean.get(position).getContent());
            }

            holder.tv_time_leav_message.setText(leabean.get(position).getCreateTime());
            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(context.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setFadeDuration(200)
                    .setPlaceholderImage(context.getResources().getDrawable(R.drawable.avatar_t), ScalingUtils.ScaleType.FIT_XY)
                    .setFailureImage(context.getResources().getDrawable(R.drawable.avatar_t), ScalingUtils.ScaleType.FIT_XY)
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                    .build();
//            holder.img_head_leav_message.setHierarchy(hierarchy);
//            FrescoUtils.setFrescoImageUri(holder.img_head_leav_message, leabean.get(position).getTeacherPortrait(), R.drawable.avatar_t);
            holder.img_headtv_me_leav_message.setHierarchy(hierarchy);
            FrescoUtils.setFrescoImageUri(holder.img_headtv_me_leav_message, leabean.get(position).getUserPortrait(), R.drawable.avatar_t);
            GenericDraweeHierarchyBuilder builder1 =
                    new GenericDraweeHierarchyBuilder(context.getResources());
            GenericDraweeHierarchy hierarchy1 = builder1
                    .setFadeDuration(200)
                    .setPlaceholderImage(context.getResources().getDrawable(R.drawable.avatar_t), ScalingUtils.ScaleType.FIT_XY)
                    .setFailureImage(context.getResources().getDrawable(R.drawable.avatar_t), ScalingUtils.ScaleType.FIT_XY)
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                    .build();
            holder.img_head_leav_message.setHierarchy(hierarchy1);
            FrescoUtils.setFrescoImageUri(holder.img_head_leav_message, leabean.get(position).getTeacherPortrait(), R.drawable.avatar_t);
//            holder.img_headtv_me_leav_message.setHierarchy(hierarchy);
//            FrescoUtils.setFrescoImageUri(holder.img_headtv_me_leav_message,leabean.get(position).getUserPortrait(), R.drawable.avatar_t);
            return convertView;
        }
    }

    public static void newIntent(Activity context , String teacherId,String teacherName) {
        Intent intent = new Intent(context, LeaTeacherActivity.class);
        intent.putExtra("teacherId",teacherId);
        intent.putExtra("teacherName",teacherName);
        context.startActivity(intent);
    }


    /****
     * vip留言详情
     *****/
    private class ObtainDataLister extends ObtainDataFromNetListener<LeaTeacherBean, String> {
        private LeaTeacherActivity weak_activity;
        public ObtainDataLister(LeaTeacherActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }
        @Override
        public void onSuccess(final LeaTeacherBean res) {
            if (weak_activity != null) {
                if (res != null) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weak_activity.flushContent_OnSucess(res);
                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }
    }
    public void flushContent_OnSucess(LeaTeacherBean leaTeacherBean){
        mPullToRefreshLayout.setRefreshing(false);
        directBeanListPDFForClassSchedule.clear();
        directBeanListPDFForClassSchedule.addAll(leaTeacherBean.getData());
        if (leavMessageAdapter==null){
            leavMessageAdapter = new LeavMessageAdapter(this, directBeanListPDFForClassSchedule);
            listView.setAdapter(leavMessageAdapter);
        }else{
            leavMessageAdapter.notifyDataSetChanged();
        }
        //大于五条留言的时候置底部 否则在顶部
        if (directBeanListPDFForClassSchedule.size()>5){
            listView.setStackFromBottom(true);
        }
    }

    /****
     * vip内容发送
     *****/
    private class AddstuentDataLister extends ObtainDataFromNetListener<String, String> {
        private LeaTeacherActivity weak_activity;
        public AddstuentDataLister(LeaTeacherActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }
        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                if (res != null) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weak_activity.ed_addleat.setText("");
                            weak_activity.loadLiveData();
                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }
    }
    /**
     * 关闭软键盘
     *
     */
    public  void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

}
