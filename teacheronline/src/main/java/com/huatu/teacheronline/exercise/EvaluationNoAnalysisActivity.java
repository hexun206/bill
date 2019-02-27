package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.exercise.bean.PaperItem;

/**
 * 暂无解析
 * @author ljyu
 * @time 2016-12-16 17:11:17
 */
public class EvaluationNoAnalysisActivity extends BaseActivity {

    private TextView tv_realTime;
    private TextView tv_recommand_time;
    private TextView tv_paper_title;
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;
    private String title = "";
    private String realTime = "";
    private String time_recommand = "";
    private String maintitle = "";

    @Override
    public void initView() {
        setContentView(R.layout.activity_evaluation_noanalysis);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_realTime = (TextView) findViewById(R.id.tv_realTime);
        tv_recommand_time = (TextView) findViewById(R.id.tv_recommand_time);
        tv_paper_title = (TextView) findViewById(R.id.tv_paper_title);
        title = getIntent().getStringExtra("title");
        realTime = getIntent().getStringExtra("realTime");
        time_recommand = getIntent().getStringExtra("time_recommand");
        maintitle = getIntent().getStringExtra("maintitle");
        tv_main_title.setText(maintitle);
        tv_recommand_time.setText(time_recommand);
        tv_realTime.setText(realTime);
        tv_paper_title.setText(title);
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
        }
    }

    public static void newIntent(String maintitle,String title,String realTime,String time_recommand,Activity context) {
        Intent intent = new Intent(context, EvaluationNoAnalysisActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("realTime",realTime);
        intent.putExtra("time_recommand",time_recommand);
        intent.putExtra("maintitle",maintitle);
        context.startActivity(intent);
    }
}
