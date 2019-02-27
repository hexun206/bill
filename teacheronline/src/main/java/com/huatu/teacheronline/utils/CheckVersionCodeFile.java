package com.huatu.teacheronline.utils;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.CustomProgressDialog;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import io.reactivex.functions.Consumer;


/**
 * 检查版本更新
 * Created by ply on 2016/3/2.
 */
public class CheckVersionCodeFile {
    private String appDownLoadUrlForLocal = Environment.getExternalStorageDirectory() + "/HuatuDownload/new.apk";//app下载到本地地址
    public final static String FromMainActivity = "fromMainActivity";
    public final static String FromAboutActivity = "fromAboutActivity";
    public String activityFrom;
    public Activity context;
    public String versions[];
    private CustomProgressDialog pd_update;
    private TextView tv_check;

    /**
     * @param context
     * @param activityFrom 来自mainactivity或者aboutactivity
     * @param versions     更新信息
     * @param tv_check     关于我们页面得检查更新按钮
     */
    public CheckVersionCodeFile(Activity context, String activityFrom, String versions[], TextView tv_check) {
        this.context = context;
        this.activityFrom = activityFrom;
        this.versions = versions;
        this.tv_check = tv_check;
    }

    /**
     * 检查是否需要更新
     */
    public void checkVersion() {
        DebugUtil.e("checkVersion:" + versions[0] + " getAppVersionName" + CommonUtils.getAppVersionName());

        int result = CommonUtils.compareVersionNames(CommonUtils.getAppVersionName(), versions[0]);
        if (result < 0) {
            Log.i("版本更新", "更新" + versions[0] + "...." + CommonUtils.getAppVersionName());
            updateAlertDialog(Integer.parseInt(versions[3]));
        } else {
            if (FromAboutActivity.equals(activityFrom)) {
                tv_check.setEnabled(true);
                ToastUtils.showToast(R.string.appUpdateInfo);
            }
        }
    }

    /**
     * 更新进度对话框
     */
    public void updateAlertDialog(int isupdate) {
        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, R.layout.dialog_new_updateinfo);
        customAlertDialog.show();
        customAlertDialog.setTitle(versions[1].toString());
        customAlertDialog.setCancelable(false);
        if (isupdate == 1) {
            customAlertDialog.setCancelGone();
        }
        customAlertDialog.setOkOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new RxPermissions((FragmentActivity) context)
                        .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                if (permission.granted) {
                                    customAlertDialog.dismiss();
                                    if (FromAboutActivity.equals(activityFrom)) {
                                        tv_check.setEnabled(true);
                                    }
                                    if (Environment.getExternalStorageState().equals(
                                            Environment.MEDIA_MOUNTED)) {
                                        pd_update = new CustomProgressDialog(context);
                                        pd_update.setCancelable(false);
                                        new Thread(new DownLoadFileThreadTask()).start();
                                    } else {
                                        ToastUtils.showToast(R.string.sdk_no);
                                    }


                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    new RxPermissions((FragmentActivity) context)
                                            .request(permission.name)
                                            .subscribe(new Consumer<Boolean>() {
                                                @Override
                                                public void accept(Boolean granted) throws Exception {
                                                    if (!granted) {
                                                        Toast.makeText(context, "请允许授予读写存储权限及拍照权限!", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });

                                } else {
                                    Toast.makeText(context, "请在设置界面授予读写存储权限及拍照权限!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                    intent.setData(uri);
                                    context.startActivity(intent);
                                }
                            }
                        });


            }
        });
        customAlertDialog.setCancelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog.dismiss();
                if (FromAboutActivity.equals(activityFrom)) {
                    tv_check.setEnabled(true);
                }
            }
        });
        customAlertDialog.setOnKeyDownClickListener(new CustomAlertDialog.OnKeyDownClickListener() {
            @Override
            public void setOnKeyListener(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    context.finish();
                }
            }
        });
    }

    public class DownLoadFileThreadTask implements Runnable {
        @Override
        public void run() {
            try {
                File parentFile = new File(appDownLoadUrlForLocal).getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                File file = DownLoadFileTask.getFile(versions[2], appDownLoadUrlForLocal, pd_update);
                pd_update.dismiss();
                install(file);
            } catch (Exception e) {
                e.printStackTrace();
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd_update.dismiss();
                        ToastUtils.showToast(R.string.appUpdatefailure);//下载失败
                    }
                });
            }
        }
    }

    /**
     * 安装apk
     *
     * @param file
     */
    private void install(File file) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            data = FileProvider.getUriForFile(context, "com.huatu.teacheronline.fileprovider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");

        context.startActivity(intent);
    }
}
