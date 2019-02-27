package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;

/**
* 我的直播-通知详情
* **/
public class DirCourseDeatailActivity extends BaseActivity {


    private String contxt;
    private String title;
    private String teacher;
    private String time;
    private String reminder;

    @Override
    public void initView() {
        setContentView(R.layout.activity_dir_course_deatail);
        TextView tv_main_title= (TextView) findViewById(R.id.tv_main_title);
        TextView tv_title= (TextView) findViewById(R.id.tv_title);
        TextView  tv_class_content= (TextView) findViewById(R.id.tv_class_content);
        TextView  tv_teacher= (TextView) findViewById(R.id.tv_teacher);
        TextView  tv_time= (TextView) findViewById(R.id.tv_time);
        TextView tv_reminder= (TextView) findViewById(R.id.tv_reminder);
        tv_main_title.setText("通知详情");
        contxt = getIntent().getStringExtra("contxt");
        title = getIntent().getStringExtra("title");
        teacher = getIntent().getStringExtra("teacher");
        time = getIntent().getStringExtra("time");
        reminder = getIntent().getStringExtra("reminder");
        tv_title.setText("课程标题："+title);
        tv_class_content.setText("课程内容："+contxt);
        tv_teacher.setText("主讲老师："+teacher);
        tv_time.setText("时间："+time);
        tv_reminder.setText(reminder);
    }

    @Override
    public void setListener() {
            findViewById(R.id.rl_main_left).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_main_left:
                back();
                break;
        }
    }


    public static void newIntent(Activity context ,String contxt,String title,String teacher,String time,String reminder) {
        Intent intent = new Intent(context, DirCourseDeatailActivity.class);
        intent.putExtra("contxt",contxt);
        intent.putExtra("title",title);
        intent.putExtra("teacher",teacher);
        intent.putExtra("time",time);
        intent.putExtra("reminder",reminder);
        context.startActivity(intent);
    }
}
