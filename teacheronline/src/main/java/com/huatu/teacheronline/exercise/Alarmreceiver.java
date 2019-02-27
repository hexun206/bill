package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huatu.teacheronline.utils.ToastUtils;

/**
 * 交卷的定时广播
 * @author ljyu
 * Created by 79937 on 2017/1/7.
 *
 */
public class Alarmreceiver extends BroadcastReceiver {

    public Alarmreceiver(){
        super();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(DoVipExerciseActivity.ACTION_HANDIN_PAPERS)) {
            DoVipExerciseActivity instance = DoVipExerciseActivity.getInstance();
            if(instance != null){
                //交卷
                instance.handinPapers();
            }
        } else {

        }
    }
}
