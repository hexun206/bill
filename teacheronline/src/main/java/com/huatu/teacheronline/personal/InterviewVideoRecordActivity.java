package com.huatu.teacheronline.personal;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;

import butterknife.ButterKnife;

public class InterviewVideoRecordActivity extends BaseActivity {


    public static void start(Context context) {

        context.startActivity(new Intent(context, InterviewVideoUploadEditActivity.class));


    }


    @Override
    public void initView() {
        setContentView(R.layout.activity_interview_video_record);
        ButterKnife.bind(this);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View v) {

    }
}
