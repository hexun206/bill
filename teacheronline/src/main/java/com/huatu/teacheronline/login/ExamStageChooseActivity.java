package com.huatu.teacheronline.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by ljzyuhenda on 16/1/18.
 * 选择考试学段
 */
public class ExamStageChooseActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;
    private ListView lv_stagelist;
    private String[] stageLists;
    private String[] stageKeyLists;
    private StageExamChooseAdapter mExamChooseAdapter;
    private TextView tv_next_step;
    private int selectedPosition = -1;

    @Override
    public void initView() {
        setContentView(R.layout.activity_examstagechoose_layout);
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.action_name_exit_activity));
        this.registerReceiver(this.broadcastReceiver, filter);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.stage_exam_choose);
        lv_stagelist = (ListView) findViewById(R.id.lv_stagelist);
        stageLists = getResources().getStringArray(R.array.categoryArrForExamStage);
        stageKeyLists = getResources().getStringArray(R.array.key_stage);
        mExamChooseAdapter = new StageExamChooseAdapter();
        lv_stagelist.setAdapter(mExamChooseAdapter);
        tv_next_step = (TextView) findViewById(R.id.tv_next_step);
    }

    @Override
    public void setListener() {
        tv_next_step.setOnClickListener(this);
        rl_main_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next_step:
                MobclickAgent.onEvent(this, "Learnnext");//考试学段下一步
                if (!TextUtils.isEmpty(UserInfo.tempStageId)) {
                    ExamSubjectChooseActivity.newIntent(this);
                } else {
                    ToastUtils.showToast(getResources().getString(R.string.stage_exam_choose));
                }
                break;
            case R.id.rl_main_left:
                finish();
                break;
        }
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, ExamStageChooseActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserInfo.tempStageId = null;
        UserInfo.tempStageName = null;
        this.unregisterReceiver(this.broadcastReceiver);
    }

    private class StageExamChooseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return stageLists.length-2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ExamStageViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(ExamStageChooseActivity.this, R.layout.item_examstagechooselist, null);
                viewHolder = new ExamStageViewHolder();
                viewHolder.iv_choose = (ImageView) convertView.findViewById(R.id.iv_choose);
                viewHolder.tv_name_stage = (TextView) convertView.findViewById(R.id.tv_name_stage);
                convertView.setTag(viewHolder);
            }

            viewHolder = (ExamStageViewHolder) convertView.getTag();
            viewHolder.tv_name_stage.setText(stageLists[position]);
//            if (selectedPosition == position) {
//                viewHolder.iv_choose.setImageResource(R.drawable.icon_choice_selected);
//            } else {
//                viewHolder.iv_choose.setImageResource(R.drawable.icon_choice_unselected);
//            }
            viewHolder.iv_choose.setVisibility(View.GONE);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(ExamStageChooseActivity.this, "Studysection");//选择考试学段
                    selectedPosition = position;
                    UserInfo.tempStageId = stageKeyLists[position];
                    UserInfo.tempStageName = stageLists[position];
                    notifyDataSetChanged();
                    ExamSubjectChooseActivity.newIntent(ExamStageChooseActivity.this);
                }
            });

            return convertView;
        }
    }

    private class ExamStageViewHolder {
        private ImageView iv_choose;
        private TextView tv_name_stage;
    }

    /**
     * 注册完登录到首页时，关掉之前所有打开的activity
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}
