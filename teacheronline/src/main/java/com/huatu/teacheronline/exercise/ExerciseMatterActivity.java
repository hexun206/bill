package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;

/**
 * 真题注意事项
 * @time 2016-12-21 14:36:02
 * @author cwqiang
 */
public class ExerciseMatterActivity extends BaseActivity {
    public static final String ACTION_NEXT = "action_next";
    private TextView started;
    private long id;
    private  String name;
    private int exercisetype;//exercisetype 1错题解析 2全部解析 0正常做题
    private int mType;//错题 -> 底部三个按钮功能分别为答题卡、解析、垃圾桶 0 今日特训、1 模块题海、2真题估分 3 错题中心、4收藏 则依次显示为交卷、答题卡、收藏 -1是分数页面过来的全部解析或者错题解析 5模考大赛


    @Override
    public void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏标题栏
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN; //定义全屏参数 隐藏状态栏
        Window window=ExerciseMatterActivity.this.getWindow();//获得当前窗体对象
        window.setFlags(flag, flag); //设置当前窗体为全屏显示
        setContentView(R.layout.activity_exercise_matter);
        TextView title= (TextView) findViewById(R.id.Title);
        started = (TextView) findViewById(R.id.started);
        TextView attentions= (TextView) findViewById(R.id.attentions);
        id = getIntent().getLongExtra("id", -1);
        exercisetype = getIntent().getIntExtra("exercisetype", 0);
        mType = getIntent().getIntExtra("type", -1);
        name = getIntent().getStringExtra("name");
        String pattentions= getIntent().getStringExtra("pattentions");//注意事项
        title.setText(name);
        attentions.setText(Html.fromHtml(pattentions));
    }

    @Override
    public void setListener() {
        started.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
     switch (v.getId()){
        case  R.id.started:
            DoVipExerciseActivity.newIntent(ExerciseMatterActivity.this, id, exercisetype,mType);
            finish();
         break;
     }
    }
    public static void newIntent(Activity context, long id, int exercisetype, int type,String name ,String pattentions) {
        Intent intent = new Intent(context, ExerciseMatterActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("exercisetype", exercisetype);
        intent.putExtra("type", type);
        intent.putExtra("name", name);
        intent.putExtra("pattentions", pattentions);
        context.startActivity(intent);
    }
    public static void newIntent(Activity context, long id, int exercisetype,String name,String pattentions) {
        ExerciseMatterActivity.newIntent(context, id, exercisetype, -1, name,pattentions);
    }
}
