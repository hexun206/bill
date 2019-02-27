package com.huatu.teacheronline.personal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CheckVersionCodeFile;
import com.huatu.teacheronline.utils.ToastUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.ref.WeakReference;

import io.reactivex.functions.Consumer;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * 关于我们
 * Created by ply on 2016/2/15.
 */
public class AboutActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;
    private TextView tv_update;
    private TextView vison_name;
    private TextView call_phone;
    private String phone;

    @Override
    public void initView() {
        setContentView(R.layout.activity_about);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.aboutUs);
        tv_update = (TextView) findViewById(R.id.tv_update);
        vison_name = (TextView) findViewById(R.id.version_name);
        call_phone = (TextView) findViewById(R.id.iv_iconForAlipy);//拨打电话
        getVison();//获取版本号
    }

    public void getVison() {
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            vison_name.setText("V" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        tv_update.setOnClickListener(this);
        call_phone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.tv_update:
                tv_update.setEnabled(false);
                checkVersion();
                break;
            case R.id.iv_iconForAlipy:
                new RxPermissions(this)
                        .requestEach(Manifest.permission.CALL_PHONE)
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                if (permission.granted) {
                                    phone = call_phone.getText().toString();
                                    final MaterialDialog materialDialog = new MaterialDialog(AboutActivity.this);
                                    materialDialog.setTitle("客服热线")
                                            .setMessage(phone)
                                            .setNegativeButton("取消", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    materialDialog.dismiss();
                                                }
                                            })
                                            .setPositiveButton("呼叫", new View.OnClickListener() {
                                                @SuppressLint("MissingPermission")
                                                @Override
                                                public void onClick(View v) {
                                                    materialDialog.dismiss();

                                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                                    Uri data = Uri.parse("tel:" + phone);
                                                    intent.setData(data);
                                                    startActivity(intent);


                                                }
                                            }).show();

                                } else {
                                    Toast.makeText(AboutActivity.this, "没有获取拨打电话的权限", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


//                new AlertDialog(this).builder().setTitle(phone).setPositiveButton("呼叫", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
//                        startActivity(intent);
//
//                    }
//                }).setNegativeButton("取消", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                }).show();
                break;
        }
    }

    public static void newIntent(Activity context) {
        Intent goldPersonIntent = new Intent(context, AboutActivity.class);
        context.startActivity(goldPersonIntent);
    }

    public void checkVersion() {
        ObtationListener obtationListener = new ObtationListener(this);
        SendRequest.getVersion(obtationListener);
    }


    public class ObtationListener extends ObtainDataFromNetListener<String[], String> {
        public AboutActivity weak_activity;

        public ObtationListener(AboutActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String[] res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CheckVersionCodeFile checkVersionCodeFile = new CheckVersionCodeFile(AboutActivity.this, CheckVersionCodeFile.FromAboutActivity, res,
                                tv_update);
                        checkVersionCodeFile.checkVersion();
                    }
                });
            }
        }

        @Override
        public void onFailure(String res) {
            if (SendRequest.ERROR_NETWORK.equals(res)) {
                ToastUtils.showToast(R.string.network);
            } else {
                ToastUtils.showToast(R.string.check_failure);
            }
        }
    }
}
