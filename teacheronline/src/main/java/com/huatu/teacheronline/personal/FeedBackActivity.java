package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

/**
 * 意见反馈
 * Created by ply on 2016/2/15.
 */
public class FeedBackActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;
    private TextView tv_submit;
    private EditText et_content;

    private CustomAlertDialog mCustomLoadingDialog;
    private ObtainDataLister obtainDataLister;
    private String uid;

    @Override
    public void initView() {
        setContentView(R.layout.activity_feedback);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        mCustomLoadingDialog = new CustomAlertDialog(FeedBackActivity.this, R.layout.dialog_loading_custom);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.customerCommand);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        et_content = (EditText) findViewById(R.id.et_content);

    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        tv_submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.tv_submit://提交
                MobclickAgent.onEvent(this, "Submit");//意见提交
                if (TextUtils.isEmpty(et_content.getText().toString())) {
                    ToastUtils.showToast(R.string.feedbackSrc);
                    return;
                }
                submitFeedBack();
                break;
        }
    }

    public static void newIntent(Activity context) {
        Intent goldPersonIntent = new Intent(context, FeedBackActivity.class);
        context.startActivity(goldPersonIntent);
    }

    /**
     * 提交意见反馈
     */
    public void submitFeedBack() {
        obtainDataLister = new ObtainDataLister(this);
        SendRequest.submitFeedBack(uid, et_content.getText().toString(), obtainDataLister);
    }

    public class ObtainDataLister extends ObtainDataFromNetListener<String, String> {
        private FeedBackActivity feedBackActivity;

        public ObtainDataLister(FeedBackActivity activity) {
            feedBackActivity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (feedBackActivity != null) {
                feedBackActivity.mCustomLoadingDialog.show();
            }
        }

        @Override
        public void onSuccess(final String res) {
            if (feedBackActivity != null) {
                feedBackActivity.mCustomLoadingDialog.dismiss();
                feedBackActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("1".equals(res)) {
                            ToastUtils.showToast(R.string.submitSucess);
                            et_content.setText("");
                        } else {
                            ToastUtils.showToast(R.string.submitFaile);
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (feedBackActivity != null) {
                feedBackActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        feedBackActivity.mCustomLoadingDialog.dismiss();
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
}
