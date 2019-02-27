package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.CustomAlertDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 题目纠错
 * Created by ply on 2016/3/21.
 */
public class ExerciseErrorSubmitActivity extends BaseActivity {
    private RelativeLayout rl_main_left, rl_main_right;
    private TextView tv_main_title;
    private ImageView iv_main_right;

    private TextView tv_submit;
    private EditText et_des;
    private RelativeLayout rl_error1, rl_error2, rl_error3, rl_error4;
    private ImageView iv_error1, iv_error2, iv_error3, iv_error4;
    private Map<ImageView, Boolean> map = new HashMap<>();
    private boolean chooseAll;
    private CustomAlertDialog mCustomLoadingDialog;
    private CustomAlertDialog mCustomDirectDilog;
    private String qid;//试题id
    private String qtype;//试题类型(单选，多选)
    private String qcontent;//题干
    private String module="";

    @Override
    public void initView() {
        setContentView(R.layout.activity_exercise_error_submit);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(DoExerciseActivity.ACTION_FINISH);
//        setIntentFilter(intentFilter);

        qid = getIntent().getStringExtra("qid");
        qtype = getIntent().getStringExtra("qtype");
        qcontent = getIntent().getStringExtra("qcontent");
        String  type = getIntent().getStringExtra("type");
        if (type.equals("1")){
            module="模块题海";
        }else if(type.equals("2")){
            module="真题演绎";
        }else if (type.equals("5")){
            module="在线模拟";
        }
        mCustomLoadingDialog = new CustomAlertDialog(ExerciseErrorSubmitActivity.this, R.layout.dialog_loading_custom);
        mCustomDirectDilog = new CustomAlertDialog(ExerciseErrorSubmitActivity.this, R.layout.dialog_exercise_error);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        iv_main_right = (ImageView) findViewById(R.id.iv_main_right);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.submit_error);

        tv_submit = (TextView) findViewById(R.id.tv_submit);
        et_des = (EditText) findViewById(R.id.et_content);
        rl_error1 = (RelativeLayout) findViewById(R.id.rl_error1);
        rl_error2 = (RelativeLayout) findViewById(R.id.rl_error2);
        rl_error3 = (RelativeLayout) findViewById(R.id.rl_error3);
        rl_error4 = (RelativeLayout) findViewById(R.id.rl_error4);
        iv_error1 = (ImageView) findViewById(R.id.iv_error1);
        iv_error2 = (ImageView) findViewById(R.id.iv_error2);
        iv_error3 = (ImageView) findViewById(R.id.iv_error3);
        iv_error4 = (ImageView) findViewById(R.id.iv_error4);
        map.put(iv_main_right, false);
        map.put(iv_error1, false);
        map.put(iv_error2, false);
        map.put(iv_error3, false);
        map.put(iv_error4, false);

    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        rl_error1.setOnClickListener(this);
        rl_error2.setOnClickListener(this);
        rl_error3.setOnClickListener(this);
        rl_error4.setOnClickListener(this);
    }

    @Override
    public boolean back() {
        mCustomDirectDilog.show();
        mCustomDirectDilog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomDirectDilog.dismiss();
                setResult(DoVipExerciseActivity.resultCode_erroeAcitivty);
                finish();
            }
        });
        mCustomDirectDilog.setCancelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomDirectDilog.dismiss();
            }
        });
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.rl_main_right://全选
                if (chooseAll) {//全选状态下
                    chooseAll = false;
                    chooseAll(chooseAll);
                } else {//全部不选状态下
                    chooseAll = true;
                    chooseAll(chooseAll);
                }
                break;
            case R.id.rl_error1:
                chooseMulti(iv_error1);
                break;
            case R.id.rl_error2:
                chooseMulti(iv_error2);
                break;
            case R.id.rl_error3:
                chooseMulti(iv_error3);
                break;
            case R.id.rl_error4:
                chooseMulti(iv_error4);
                break;
            case R.id.tv_submit://提交
                submitInfo();
                break;
        }

    }

    /**
     * 全选
     *
     * @param choose true 全选 false 全部不选中
     */
    public void chooseAll(boolean choose) {
        for (ImageView imageView : map.keySet()) {
            if (choose) {
                imageView.setBackgroundResource(R.drawable.icon_select);
                map.put(imageView, true);
            } else {
                imageView.setBackgroundResource(R.drawable.icon_no_select);
                map.put(imageView, false);
            }
        }
    }


    /**
     * 多选
     *
     * @param imageView
     */
    public void chooseMulti(ImageView imageView) {
        if (map.get(imageView) == true) {
            imageView.setBackgroundResource(R.drawable.icon_no_select);
            map.put(imageView, false);
        } else {
            imageView.setBackgroundResource(R.drawable.icon_select);
            map.put(imageView, true);
        }

        if (map.get(iv_error1) == true && map.get(iv_error2) == true && map.get(iv_error3) == true && map.get(iv_error4) == true) {
            iv_main_right.setBackgroundResource(R.drawable.icon_select);
            chooseAll = true;
        } else {
            iv_main_right.setBackgroundResource(R.drawable.icon_no_select);
            chooseAll = false;
        }
    }

    /**
     * 跳转到本页
     *
     * @param activity
     * @param qid      试题ID
     * @param qtype    试题类型
     * @param qcontent 题干
     */
    public static void newIntent(Activity activity, String qid, String qtype, String qcontent,int requestCode ,String type) {
        Intent intent = new Intent(activity, ExerciseErrorSubmitActivity.class);
        intent.putExtra("qid", qid);
        intent.putExtra("qtype", qtype);
        intent.putExtra("qcontent", qcontent);
        intent.putExtra("type", type);
//        activity.startActivity(intent);
        activity.startActivityForResult(intent,requestCode);
    }

    /**
     * 提交纠错信息
     */
    public void submitInfo() {
        List<String> error_type = new ArrayList<>();
        if (TextUtils.isEmpty(et_des.getText().toString())) {
            ToastUtils.showToast("请描述您发现的问题");
            return;
        }
        if (map.get(iv_error1) == true) {
            error_type.add("correct_0");
        }
        if (map.get(iv_error2) == true) {
            error_type.add("correct_1");
        }
        if (map.get(iv_error3) == true) {
            error_type.add("correct_2");
        }
        if (map.get(iv_error4) == true) {
            error_type.add("correct_3");
        }

        if (error_type.size() == 0) {
            ToastUtils.showToast("请选择纠错类型");
            return;
        }

        ObtainDataLister obtainDataLister = new ObtainDataLister(this);
        //添加题目来源参数module
        SendRequest.submitExerciseError(qid, qtype, qcontent, error_type, et_des.getText().toString(),module, obtainDataLister);
    }

    private static class ObtainDataLister extends ObtainDataFromNetListener<String, String> {

        private ExerciseErrorSubmitActivity weak_activity;

        public ObtainDataLister(ExerciseErrorSubmitActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.show();
            }
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushUI(res);
                    }
                });
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
                weak_activity.mCustomLoadingDialog.dismiss();
            }
        }
    }

    public void flushUI(String res) {
        if (TextUtils.isEmpty(res)) {
            ToastUtils.showToast(R.string.server_error);
            return;
        }

        if ("true".equals(res)) {
            ToastUtils.showToast(R.string.submit_sucess);
            setResult(DoVipExerciseActivity.resultCode_erroeAcitivty);
            finish();
        } else {
            ToastUtils.showToast(R.string.submitFaile);
        }
    }

    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
        back();
    }
}
