package com.huatu.teacheronline.personal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.bean.UploadEvent;
import com.luck.picture.lib.PictureSelector;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UploadVideoConfirmActivity extends BaseActivity {

    public final static String KEY_PATH = "key_path";
    public final static int CODE_REQUEST = 0xa0;

    @BindView(R.id.img_video_display)
    ImageView mImgVideoDisplay;
    private String mPath;
    private AlertDialog mNoticeDialog;
    private AlertDialog mBackNoticeDialog;


    public static void start(Context context, String path) {
        Intent intent = new Intent(context, UploadVideoConfirmActivity.class);
        intent.putExtra(KEY_PATH, path);
        context.startActivity(intent);


    }


    @Override
    public void initView() {
        setContentView(R.layout.activity_upload_video_confirm);
        ButterKnife.bind(this);
        if (getIntent() != null) {
            mPath = getIntent().getStringExtra(KEY_PATH);
            RequestOptions options = new RequestOptions()
                    .transforms(new CenterCrop(), new RoundedCorners(20))
                    .placeholder(R.color.gray007)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(this)
                    .load(mPath)
                    .apply(options)
                    .into(mImgVideoDisplay);
        }

        initRestartRecorderNoticeDialog();
        initBackNoticeDialog();


    }

    private void initRestartRecorderNoticeDialog() {

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_common_notice, null);

        RecorderActivity.ViewHolder holder = new RecorderActivity.ViewHolder(view);
        holder.mTvTittle.setText("确定放弃本次录制结果？");
        holder.mTvTittleSub.setText("重新录制后,会删除本次录制结果.");
        holder.mTvDialogCancel.setText("取消");
        holder.mTvDialogConfirm.setText("确定");


        view.findViewById(R.id.tv_dialog_cancel).setOnClickListener(cancel -> mNoticeDialog.dismiss());

        view.findViewById(R.id.tv_dialog_confirm).setOnClickListener(call -> {
            mNoticeDialog.dismiss();

            File file = new File(mPath);
            if (file.exists()) {
                file.delete();
            }

            RecorderActivity.start(this);
            finish();

        });

        mNoticeDialog = new AlertDialog.Builder(this, R.style.dialog_notitle)
                .setView(view)
                .create();


    }

    private void initBackNoticeDialog() {

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_common_notice, null);

        RecorderActivity.ViewHolder holder = new RecorderActivity.ViewHolder(view);
        holder.mTvTittle.setText("确定放弃本次录制结果？");
        holder.mTvTittleSub.setText("放弃后,会删除本次录制结果.");
        holder.mTvDialogCancel.setText("取消");
        holder.mTvDialogConfirm.setText("确定");


        view.findViewById(R.id.tv_dialog_cancel).setOnClickListener(cancel -> mBackNoticeDialog.dismiss());

        view.findViewById(R.id.tv_dialog_confirm).setOnClickListener(call -> {
            mBackNoticeDialog.dismiss();

            File file = new File(mPath);
            if (file.exists()) {
                file.delete();
            }

            finish();

        });

        mBackNoticeDialog = new AlertDialog.Builder(this, R.style.dialog_notitle)
                .setView(view)
                .create();


    }


    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View v) {

    }


//    @Override
//    public void onBackPressed() {
//        mNoticeDialog.show();
//
////        super.onBackPressed();
//    }
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        //拦截返回键
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            //判断触摸UP事件才会进行返回事件处理
//            if (event.getAction() == KeyEvent.ACTION_UP) {
//                onBackPressed();
//            }
//            //只要是返回事件，直接返回true，表示消费掉
//            return true;
//        }
//        return super.dispatchKeyEvent(event);
//    }


    @Override
    public boolean back() {
        mBackNoticeDialog.show();
//        return super.back();
        return true;
    }

    @OnClick({R.id.img_back, R.id.img_video_preview, R.id.tv_video_record, R.id.tv_upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                back();
                break;
            case R.id.img_video_preview:
                PictureSelector.create(this).externalPictureVideo(mPath);

                break;
            case R.id.tv_video_record:
                mNoticeDialog.show();
                break;
            case R.id.tv_upload:
                UploadEvent uploadEvent = new UploadEvent();
                uploadEvent.setPath(mPath);
                EventBus.getDefault().post(uploadEvent);
                finish();
                break;
        }
    }
}
