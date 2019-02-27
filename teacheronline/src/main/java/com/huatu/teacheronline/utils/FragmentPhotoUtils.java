package com.huatu.teacheronline.utils;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.huatu.teacheronline.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by zhxm on 2016/2/23.
 * 选择头像用到的相关方法
 */
public class FragmentPhotoUtils {
    private Fragment activity;
    private GenericDraweeHierarchy hierarchy;
    private String mobile;

    public FragmentPhotoUtils(Fragment activity, String mobile) {
        this.activity = activity;
        this.mobile = mobile;
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(activity.getResources());
        hierarchy = builder
                .setFadeDuration(100)
                .setPlaceholderImage(activity.getResources().getDrawable(R.drawable.face_big), ScalingUtils.ScaleType.CENTER_CROP)
                .setFailureImage(activity.getResources().getDrawable(R.drawable.face_big), ScalingUtils.ScaleType.CENTER_CROP)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .build();
        RoundingParams mRoundingParams = new RoundingParams();
        mRoundingParams.setRoundAsCircle(true);
        hierarchy.setRoundingParams(mRoundingParams);
    }

    public void showPicturePicker(boolean isCrop) {
        final boolean crop = isCrop;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity.getActivity());
        builder.setTitle(R.string.select_photo);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setItems(new String[]{/*activity.getResources().getString(R.string.take_photo), */activity.getResources().getString(R.string.local_library)},
                new DialogInterface.OnClickListener() {
                    int REQUEST_CODE;

                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
//                            case UserInfo.TAKE_PICTURE:
//                                Uri imageUri = null;
//                                String fileName = null;
//                                Intent openCameraIntent = new Intent(
//                                        MediaStore.ACTION_IMAGE_CAPTURE);
//                                File file =  new File(Environment.getExternalStorageDirectory(), "HuatuDownload/"+fileName);
//                                if(!file.getParentFile().exists()){
//                                    file.mkdirs();
//                                }
//
//                                if (crop) {
//                                    REQUEST_CODE = UserInfo.CROP;
//                                    SharedPreferences sharedPreferences = activity.getActivity().getSharedPreferences("temp", Context.MODE_PRIVATE);
//                                    ImageTools.deletePhotoAtPathAndName(Environment
//                                            .getExternalStorageDirectory()
//                                            .getAbsolutePath(), "HuatuDownload/"+sharedPreferences
//                                            .getString("tempName", ""));
//
//                                    fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putString("tempName", fileName);
//                                    editor.commit();
//                                } else {
//                                    REQUEST_CODE = UserInfo.TAKE_PICTURE;
//                                    fileName = "image.jpg";
//                                }
//
//
//
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//
//                                    if (openCameraIntent.resolveActivity(activity.getActivity().getPackageManager()) != null) {
//                                        Uri fileUri = FileProvider.getUriForFile(activity.getActivity(), BuildConfig.APPLICATION_ID+".fileprovider", file);
//                                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                                    }
//
//                                } else {
//                                    openCameraIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//                                    openCameraIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                }
//
////                                imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "HuatuDownload/"+fileName));
////                                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                activity.startActivityForResult(openCameraIntent, REQUEST_CODE);
//                                break;
//                            case UserInfo.CHOOSE_PICTURE:
                            default:
                                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                if (crop) {
                                    REQUEST_CODE = UserInfo.CROP;
                                } else {
                                    REQUEST_CODE = UserInfo.CHOOSE_PICTURE;
                                }
                                openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                activity.startActivityForResult(openAlbumIntent, REQUEST_CODE);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    public void cropImage(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        activity.startActivityForResult(intent, requestCode);
    }

    public Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    public File getTempFile() {
        if (isSDCARDMounted()) {
            File f = new File(Environment.getExternalStorageDirectory(), mobile + ".jpg");
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return f;
        } else {
            return null;
        }
    }

    public boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }

    public void takePictureForResult(SimpleDraweeView simpleDraweeView) {
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.jpg");
        Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / UserInfo.SCALE, bitmap.getHeight() / UserInfo.SCALE);
        bitmap.recycle();
        ImageTools.savePhotoToSDCard(newBitmap, mobile);

        simpleDraweeView.setHierarchy(hierarchy);
        simpleDraweeView.setImageURI(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "/huatu/" + mobile + ".jpg")));
    }

    public void choosePictureForResult(Intent data, SimpleDraweeView simpleDraweeView) {
        Uri originalUri = data.getData();
        simpleDraweeView.setHierarchy(hierarchy);
        simpleDraweeView.setImageURI(originalUri);
    }

    public void cropForResult(Intent data) {
        Uri uri = null;
        if (data != null) {
            uri = data.getData();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                uri = UriUtil.getUriFromPath(activity.getActivity(), UriUtil.getPath(activity.getActivity(), uri));
            }
        } else {
            String fileName = activity.getActivity().getSharedPreferences("temp", Context.MODE_PRIVATE).getString("tempName", "");
            uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "HuatuDownload"+fileName));
        }
        cropImage(uri, 200, 200, UserInfo.CROP_PICTURE);
    }

    public void cropPictureForResult(Intent data, SimpleDraweeView simpleDraweeView, String facePath) {
        Bitmap photo = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        try {
            FileInputStream fis = new FileInputStream(getTempFile());
            photo = BitmapFactory.decodeStream(fis, null, opts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (photo == null) {
            Bundle extra = data.getExtras();
            if (extra != null) {
                photo = (Bitmap) extra.get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            }
        }
        ImageTools.savePhotoToSDCard(photo, mobile);

        simpleDraweeView.setHierarchy(hierarchy);
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(activity.getActivity().getContentResolver(), photo, null, null));
        simpleDraweeView.setImageURI(uri);

        // 清空缓存
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        if (facePath != null) {
            imagePipeline.evictFromCache(Uri.parse(facePath));
        }
    }

    /**
     * 删掉本地保存的头像
     *
     * @param filePath
     */
    public void deletePhoto(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
