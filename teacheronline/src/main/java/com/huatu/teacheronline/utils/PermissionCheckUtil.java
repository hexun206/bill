package com.huatu.teacheronline.utils;

import android.hardware.Camera;
import android.os.Build;

import java.lang.reflect.Field;

/**
 * Created by kinndann on 2018/11/20.
 * description:为某些奇葩机型服务的权限验证类
 */
public class PermissionCheckUtil {

    public static boolean checkRecordAudio() throws Exception {
        if (!checkVIVO()) {
            return true;
        }

        AudioRecordManager recordManager = new AudioRecordManager();

        recordManager.startRecord(FileUtils.getInterviewVideoRecordFolder() + "/" +
                "recordAudioPermissionCheck" + ".mp4");
        recordManager.stopRecord();

        return recordManager.getSuccess();
    }


    /**
     * vivo手机拒绝camera权限时  camera不抛异常且正常打开
     *
     * @param camera
     * @return
     */
    public static boolean checkCamera(Camera camera) {
        if (!checkVIVO()) {
            return true;
        }
        
        try {
            Field mHasPermission = camera.getClass().getDeclaredField("mHasPermission");
            mHasPermission.setAccessible(true);
            return (boolean) mHasPermission.get(camera);


        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;

    }


    /**
     * 检测是否为vivo手机
     *
     * @return
     */
    public static boolean checkVIVO() {
        String model = Build.MANUFACTURER.toLowerCase();
        return model.contains("vivo");
    }


}
