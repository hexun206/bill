package com.huatu.teacheronline.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.sensorsdata.TrackUtil;
import com.huatu.teacheronline.utils.UserInfo;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by ljzyuhenda on 16/1/18.
 * 选择考试学段
 */
public class ExamStageChooseNewActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    //    private TextView tv_main_title;
//    private ListView lv_stagelist;
    private String[] stageLists;
    private String[] stageKeyLists;
    //    private StageExamChooseAdapter mExamChooseAdapter;
//    private TextView tv_next_step;
    private TextView tv_preschool_education, tv_primary, tv_secondary;//幼教 小学 中学

    private TextView tv_choose_examstage;//考试类型
    private TextView tv_period;//学段
    private TextView tv_area;//地区

    public static final String ACTION_FINISH_EXAMSTAGECHOOSE = "FinishExamStageChooseNewActivity";

    @Override
    public void initView() {
        setContentView(R.layout.activity_examstagechoosenew_layout);
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.action_name_exit_activity));
        filter.addAction(ACTION_FINISH_EXAMSTAGECHOOSE);
        this.registerReceiver(this.broadcastReceiver, filter);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
//        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
//        tv_main_title.setText(R.string.stage_exam_choose);
//        lv_stagelist = (ListView) findViewById(R.id.lv_stagelist);
        tv_choose_examstage = (TextView) findViewById(R.id.tv_choose_examstage);
        tv_period = (TextView) findViewById(R.id.tv_period);
        tv_area = (TextView) findViewById(R.id.tv_area);
        if (getResources().getString(R.string.key_stage_government_confirm).equals(UserInfo.tempCategoryTypeId)){
            //国考
            tv_choose_examstage.setText(UserInfo.tempCategoryTypeName);
            tv_period.setVisibility(View.GONE);
            tv_area.setVisibility(View.GONE);
        }else {
            //教师招聘
            if(UserInfo.tempCityId.equals(UserInfo.tempProvinceId)){
                //省市编码一样 只有省
                tv_choose_examstage.setText(UserInfo.tempCategoryTypeName);
                tv_period.setVisibility(View.VISIBLE);
                tv_area.setText(UserInfo.tempProvinceName);
                tv_period.setVisibility(View.GONE);
            }else {
                //有市
                tv_choose_examstage.setText(UserInfo.tempCategoryTypeName);
                tv_period.setVisibility(View.VISIBLE);
                tv_area.setText(UserInfo.tempProvinceName+"·"+UserInfo.tempCityName);
                tv_period.setVisibility(View.GONE);
            }
        }


        stageLists = getResources().getStringArray(R.array.categoryArrForExamStage);
        stageKeyLists = getResources().getStringArray(R.array.key_stage);
//        mExamChooseAdapter = new StageExamChooseAdapter();
//        lv_stagelist.setAdapter(mExamChooseAdapter);
//        tv_next_step = (TextView) findViewById(R.id.tv_next_step);
        tv_preschool_education = (TextView) findViewById(R.id.tv_preschool_education);
        tv_primary = (TextView) findViewById(R.id.tv_primary);
        tv_secondary = (TextView) findViewById(R.id.tv_secondary);
    }

    @Override
    public void setListener() {
//        tv_next_step.setOnClickListener(this);
        rl_main_left.setOnClickListener(this);
        tv_preschool_education.setOnClickListener(this);
        tv_primary.setOnClickListener(this);
        tv_secondary.setOnClickListener(this);
        tv_choose_examstage.setOnClickListener(this);
        tv_area.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_preschool_education:
                MobclickAgent.onEvent(ExamStageChooseNewActivity.this, "Studysection");//选择考试学段
                UserInfo.tempStageId = stageKeyLists[0];
                UserInfo.tempStageName = stageLists[0];
                TrackUtil.trackSelectExamGrade(stageLists[0]);
                ExamSubjectChooseNewActivity.newIntent(ExamStageChooseNewActivity.this);
                break;
            case R.id.tv_primary:
                MobclickAgent.onEvent(ExamStageChooseNewActivity.this, "Studysection");//选择考试学段
                UserInfo.tempStageId = stageKeyLists[1];
                UserInfo.tempStageName = stageLists[1];
                TrackUtil.trackSelectExamGrade(stageLists[1]);
                ExamSubjectChooseNewActivity.newIntent(ExamStageChooseNewActivity.this);
                break;
            case R.id.tv_secondary:
                MobclickAgent.onEvent(ExamStageChooseNewActivity.this, "Studysection");//选择考试学段
                UserInfo.tempStageId = stageKeyLists[2];
                UserInfo.tempStageName = stageLists[2];
                TrackUtil.trackSelectExamGrade(stageLists[2]);
                ExamSubjectChooseNewActivity.newIntent(ExamStageChooseNewActivity.this);
                break;
            case R.id.rl_main_left:
                finish();
                break;
            case R.id.tv_choose_examstage:
            case R.id.tv_area:
                finish();
                break;
        }
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, ExamStageChooseNewActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserInfo.tempStageId = null;
        UserInfo.tempStageName = null;
        this.unregisterReceiver(this.broadcastReceiver);
    }

//    private class StageExamChooseAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return stageLists.length-2;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            ExamStageViewHolder viewHolder;
//            if (convertView == null) {
//                convertView = View.inflate(ExamStageChooseNewActivity.this, R.layout.item_examstagechooselist, null);
//                viewHolder = new ExamStageViewHolder();
//                viewHolder.iv_choose = (ImageView) convertView.findViewById(R.id.iv_choose);
//                viewHolder.tv_name_stage = (TextView) convertView.findViewById(R.id.tv_name_stage);
//                convertView.setTag(viewHolder);
//            }
//
//            viewHolder = (ExamStageViewHolder) convertView.getTag();
//            viewHolder.tv_name_stage.setText(stageLists[position]);
////            if (selectedPosition == position) {
////                viewHolder.iv_choose.setImageResource(R.drawable.icon_choice_selected);
////            } else {
////                viewHolder.iv_choose.setImageResource(R.drawable.icon_choice_unselected);
////            }
//            viewHolder.iv_choose.setVisibility(View.GONE);
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    MobclickAgent.onEvent(ExamStageChooseNewActivity.this, "Studysection");//选择考试学段
//                    selectedPosition = position;
//                    UserInfo.tempStageId = stageKeyLists[position];
//                    UserInfo.tempStageName = stageLists[position];
//                    notifyDataSetChanged();
//                    ExamSubjectChooseActivity.newIntent(ExamStageChooseNewActivity.this);
//                }
//            });
//
//            return convertView;
//        }
//    }

//    private class ExamStageViewHolder {
//        private ImageView iv_choose;
//        private TextView tv_name_stage;
//    }

    /**
     * 注册完登录到首页时，关掉之前所有打开的activity
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
            if (intent.getAction().equals(ACTION_FINISH_EXAMSTAGECHOOSE)) {
                finish();
            }
        }
    };
}
