package com.huatu.teacheronline.exercise;

import com.greendao.DaoUtils;
import com.greendao.ExerciseDownloadPackage;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.UserInfo;

/**
 * Created by ljzyuhenda on 16/1/26.
 */
public class SendRequestUtilsForExercise {
    private static SendRequestUtilsForExercise mSendRequestUtilsForExercise;

    private SendRequestUtilsForExercise() {
    }

    public static SendRequestUtilsForExercise getInstance() {
        if (mSendRequestUtilsForExercise == null) {
            synchronized (SendRequestUtilsForExercise.class) {
                if (mSendRequestUtilsForExercise == null) {
                    mSendRequestUtilsForExercise = new SendRequestUtilsForExercise();
                }
            }
        }

        return mSendRequestUtilsForExercise;
    }

    public String userId;//用户id
    public String deviceId;//设备id
    public String examType;//考试类型
    public String stage;//学段
    public String area;//地区码
    public String versions;//版本
    public String subject;//考试科目

    public void assignDatas() {
        userId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "null");
        deviceId = "mate";
        examType = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMCATEGORY_ID, "examType_qgjs");
        stage = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSTAGE_ID, "stage_yj");
        area = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PROVINCE_ID, "110000") + "_" + CommonUtils.getSharedPreferenceItem(null, UserInfo
                .KEY_SP_CITY_ID, CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PROVINCE_ID, "110000"));

        subject = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_ID, null);

//        examType = "examType_dqjs";
//        stage = "stage_zx";
//        area = "340000";
//        subject = "a712a40703ce4b09aef5827fbec0689d";

        ExerciseDownloadPackage downloadPackage = DaoUtils.getInstance().queryExerciseDownloadPackageInfo(getKeyForExercisePackageDownload(subject, stage,
                area, examType));
        if (downloadPackage == null) {
            versions = "1";
        } else {
            versions = downloadPackage.getVersions();
        }
    }

    public static String getKeyForExercisePackageDownload() {
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();

        return getKeyForExercisePackageDownload(sendRequestUtilsForExercise.subject, sendRequestUtilsForExercise.stage, sendRequestUtilsForExercise.area,
                sendRequestUtilsForExercise.examType);
    }

    public static String getKeyForExercisePackageDownload(String subject, String stage, String area, String examType) {
        if (examType == "examType_qgjs") {
            return examType + subject;
        }

        return examType + area + stage + subject;
    }
}
