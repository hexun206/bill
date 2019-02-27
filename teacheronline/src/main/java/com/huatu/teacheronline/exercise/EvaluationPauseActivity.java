package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.DownManageActivity;

/**
 * 做题暂停页面
 * @author ljyu
 * @time 2016-12-16 17:11:17
 */
public class EvaluationPauseActivity extends BaseActivity {
    public static final int resultCode_doExerciseAcitivty = 107;

    @Override
    public void initView() {
        setContentView(R.layout.activity_evaluation_pause);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(DoExerciseActivity.ACTION_FINISH);
//        setIntentFilter(intentFilter);
    }

    @Override
    public void setListener() {
        findViewById(R.id.tv_evaluation_continue).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_evaluation_continue:
                setResult(DoVipExerciseActivity.resultCode_pauseAcitivty);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }



    public static void newIntent(Activity context, int requestCode) {
        Intent intent = new Intent(context, EvaluationPauseActivity.class);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
        back();
    }
}
