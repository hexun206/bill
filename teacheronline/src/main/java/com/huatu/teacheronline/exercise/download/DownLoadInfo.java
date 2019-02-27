package com.huatu.teacheronline.exercise.download;

/**
 * Created by only on 2015/10/27.
 */
public class DownLoadInfo {
    public long progress;
    public long fileLength;
    public String versions;

    public DownLoadCallBackForExercise.DownLoadExerciseStatus status;
    public DownLoadManagerForExercise.DownLoadFilesListener listener;
    public boolean isUpdate;
}
