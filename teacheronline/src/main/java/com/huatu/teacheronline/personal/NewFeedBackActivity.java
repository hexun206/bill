package com.huatu.teacheronline.personal;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
/**
 *  新增反馈
 * Created by ply on 2016/1/5.
 */
public class NewFeedBackActivity extends BaseActivity implements  BGASortableNinePhotoLayout.Delegate ,EasyPermissions.PermissionCallbacks{
    private static final int REQUEST_CODE_PERMISSION_PHOTO_PICKER = 1;

    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_PHOTO_PREVIEW = 2;

    private static final String EXTRA_MOMENT = "EXTRA_MOMENT";
    private EditText et_phone;
    private EditText et_version;
    private EditText et_content;
    private BGASortableNinePhotoLayout mPhotosSnpl;
    private CustomAlertDialog mCustomLoadingDialog;
    private ArrayList<String> selectedImages=new ArrayList<>();//储存图片地址


    @Override
    public void initView() {
        setContentView(R.layout.activity_new_feed_back);
        TextView tv_title= (TextView) findViewById(R.id.tv_main_title);
        tv_title.setText("新增反馈");
        mCustomLoadingDialog = new CustomAlertDialog(NewFeedBackActivity.this, R.layout.dialog_loading_custom);
        et_version = (EditText) findViewById(R.id.et_version);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_content = (EditText) findViewById(R.id.et_content);
        mPhotosSnpl = (BGASortableNinePhotoLayout) findViewById(R.id.snpl_moment_add_photos);
        //设置拍照选择图片最多三张等属性
        mPhotosSnpl.setMaxItemCount(3);
        mPhotosSnpl.setEditable(true);
        mPhotosSnpl.setPlusEnable(true);
        mPhotosSnpl.setSortable(true);
        mPhotosSnpl.setDelegate(this);

    }

    @Override
    public void setListener() {
      findViewById(R.id.rl_main_left).setOnClickListener(this);
        findViewById(R.id.tv_Submit).setOnClickListener(this);
    }

    /**
     * 提交意见反馈
     */
    public void submitFeedBack() {
        ObtainDataLister   obtainDataLister = new ObtainDataLister(this);
        //如果selectedImages容器长度为0，1，2说明拍照后上传的图片根据不同数量图片提交参数
        if (selectedImages.size()==0){
            SendRequest.NewsubmitFeedBack(et_phone.getText().toString(), et_version.getText().toString(), et_content.getText().toString(),"", "", "", obtainDataLister);
            return;
        }else if (selectedImages.size()==1){
            SendRequest.NewsubmitFeedBack(et_phone.getText().toString(), et_version.getText().toString(), et_content.getText().toString(), selectedImages.get(0).toString(), "", "", obtainDataLister);
            return;
        }else if (selectedImages.size()==2){
            SendRequest.NewsubmitFeedBack(et_phone.getText().toString(), et_version.getText().toString(),
                    et_content.getText().toString(), selectedImages.get(0).toString(), selectedImages.get(1).toString(), "", obtainDataLister);
            return;
        } else{
            SendRequest.NewsubmitFeedBack(et_phone.getText().toString(), et_version.getText().toString(),
                    et_content.getText().toString(), selectedImages.get(0).toString(), selectedImages.get(1).toString(), selectedImages.get(2).toString(), obtainDataLister);
            return;
        }
    }

    public class ObtainDataLister extends ObtainDataFromNetListener<String, String> {
        private NewFeedBackActivity feedBackActivity;

        public ObtainDataLister(NewFeedBackActivity activity) {
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
                        ToastUtils.showToastExercise(feedBackActivity, "反馈提交成功");
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

    @Override
    public void onClick(View v) {
          switch (v.getId()){
              case R.id.rl_main_left:
                  back();
                  break;
              case R.id.tv_Submit:
                  MobclickAgent.onEvent(this, "Submit");//意见提交
                  if (TextUtils.isEmpty(et_content.getText().toString())) {
                      ToastUtils.showToast(R.string.feedbackSrc);
                      return;
                  }
                  if (TextUtils.isEmpty(et_phone.getText().toString())) {//手机型号
                      ToastUtils.showToast(R.string.phonemodel);
                      return;
                  }
                  if (TextUtils.isEmpty(et_version.getText().toString())) {//系统版本
                      ToastUtils.showToast(R.string.phoneversion);
                      return;
                  }
                  submitFeedBack();
                  break;
          }
    }

    public static void newIntent(Activity context) {
        Intent goldPersonIntent = new Intent(context, NewFeedBackActivity.class);
        context.startActivity(goldPersonIntent);
    }

    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
        //拍照保存选择图片等功能
        choicePhotoWrapper();
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        //删除图片
        mPhotosSnpl.removeItem(position);
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                .previewPhotos(models) // 当前预览的图片路径集合
                .selectedPhotos(models) // 当前已选中的图片路径集合
                .maxChooseCount(mPhotosSnpl.getMaxItemCount()) // 图片选择张数的最大值
                .currentPosition(position) // 当前预览图片的位置
                .isFromTakePhoto(false) // 是否是拍完照后跳转过来
                .build();
        startActivityForResult(photoPickerPreviewIntent, REQUEST_CODE_PHOTO_PREVIEW);

    }

    @Override
    public void onNinePhotoItemExchanged(BGASortableNinePhotoLayout sortableNinePhotoLayout, int fromPosition, int toPosition, ArrayList<String> models) {

    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_PHOTO_PICKER)
    private void choicePhotoWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "Photo");

            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    .cameraFileDir( takePhotoDir) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                    .maxChooseCount(mPhotosSnpl.getMaxItemCount() - mPhotosSnpl.getItemCount()) // 图片选择张数的最大值
                    .selectedPhotos(null) // 当前已选中的图片路径集合
                    .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                    .build();
            startActivityForResult(photoPickerIntent, REQUEST_CODE_CHOOSE_PHOTO);



        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", REQUEST_CODE_PERMISSION_PHOTO_PICKER, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_CODE_PERMISSION_PHOTO_PICKER) {
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
                mPhotosSnpl.addMoreData(BGAPhotoPickerActivity.getSelectedPhotos(data));
                selectedImages.addAll(BGAPhotoPickerActivity.getSelectedPhotos(data));//添加相册或者拍照的照片地址进入容器
        } else if (requestCode == REQUEST_CODE_PHOTO_PREVIEW) {
            mPhotosSnpl.setData(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
        }
    }
}
