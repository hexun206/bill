package com.huatu.teacheronline.personal;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.lang.ref.WeakReference;
/**
 * 我的课程 添加留言
 * Created by 18250 on 2017/8/25.
 */
public class AddMessageActivity extends BaseActivity {

    private TextView tv_main_title;
    private int noticeId;
    private String uid;
    private RelativeLayout rl_ok;
    private CustomAlertDialog mCustomLoadingDialog;
    private int messageId;
    private EditText et_content;
    private TextView tv_num;

    @Override
    public void initView() {
        setContentView(R.layout.activity_add_message);
        noticeId = getIntent().getIntExtra("noticeId", 0);
        messageId = getIntent().getIntExtra("messageId", 0);
        mCustomLoadingDialog = new CustomAlertDialog(AddMessageActivity.this, R.layout.dialog_loading_custom);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        et_content = (EditText) findViewById(R.id.et_content);
        rl_ok = (RelativeLayout) findViewById(R.id.rl_ok);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_main_title.setText("添加留言");
    }

    private void AddMessageData() {
        ObtainDataLister   obtainDataLister = new ObtainDataLister(this);
        SendRequest.getAddmasg(uid, String.valueOf(noticeId), String.valueOf(messageId), et_content.getText().toString(), obtainDataLister);
    }

    @Override
    public void setListener() {
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        rl_ok.setOnClickListener(this);
        et_content.addTextChangedListener(passwordListener());
        }



    @Override
    public void onClick(View v) {
          switch (v.getId()){
              case R.id.rl_main_left:
                  back();
                  break;
              case R.id.rl_ok:
                  AddMessageData();
                  break;

          }
    }

    private TextWatcher passwordListener() {
        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                tv_num.setText(length + "/" + 100);
                if (length==100){
                    ToastUtils.showToast("已经达到字数限制范围");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

        };
    }
    public static void newIntent(Context context, int noticeId ,int messageId) {
        Intent intent = new Intent(context, AddMessageActivity.class);
        intent.putExtra("noticeId", noticeId);
        intent.putExtra("messageId", messageId);
        context.startActivity(intent);
    }


    public class ObtainDataLister extends ObtainDataFromNetListener<String, String> {
        private AddMessageActivity feedBackActivity;

        public ObtainDataLister(AddMessageActivity activity) {
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
                    ToastUtils.showToast(res);
                    feedBackActivity.finish();
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
