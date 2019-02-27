package com.huatu.teacheronline.exercise.download;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.DebugUtil;

import java.lang.ref.WeakReference;

/**
 * Created by only on 2015/10/27.
 */
public class DownLoadCallBackForExercise implements DownLoadManagerForExercise.DownLoadFilesListener {
    private WeakReference<View> viewFirstWeak;
    private WeakReference<View> viewSecondWeak;
    private WeakReference<View> viewThirdWeak;
    private WeakReference<View> viewFourthWeak;
    private String key;
    public DownLoadExerciseType type;

    public DownLoadCallBackForExercise(DownLoadExerciseType type) {
        this.type = type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setViewFirst(WeakReference<View> viewFirst) {
        this.viewFirstWeak = viewFirst;
    }

    public void setViewThird(WeakReference<View> viewThird) {
        this.viewThirdWeak = viewThird;
    }

    public void setViewFourth(WeakReference<View> viewFourth) {
        this.viewFourthWeak = viewFourth;
    }

    public void setViewSecond(WeakReference<View> viewSecond) {
        this.viewSecondWeak = viewSecond;
    }

    @Override
    public void onSuccess(byte[] responseBody, String key, DownLoadExerciseStatus status) {
        refreshItems(status);
    }

    @Override
    public void onStart(DownLoadExerciseStatus status) {
        refreshItems(status);
    }

    @Override
    public void onProcess(long bytesWritten, long totalSize, DownLoadExerciseStatus status) {
        refreshItems(bytesWritten, totalSize, status);
    }

    @Override
    public void onFailure(DownLoadExerciseStatus status) {
        refreshItems(status);
    }

    public enum DownLoadExerciseStatus {
        NeverStart, DownLoading, Success
    }

    public enum DownLoadExerciseType {
        ExerciseSelfMakeActivity, ModuleExerciseActivity, ExerciseEvaluationActivity
    }

    public void refreshItems(long bytesWritten, long totalSize, DownLoadExerciseStatus status) {
        if (DownLoadExerciseType.ExerciseSelfMakeActivity.equals(type)) {
            TextView viewFirst = (TextView) viewFirstWeak.get();
            TextView viewSecond = (TextView) viewSecondWeak.get();
            ProgressBar viewThird = (ProgressBar) viewThirdWeak.get();

            if (viewFirst == null || viewSecond == null || viewThird == null) {
                return;
            }

            if (DownLoadExerciseStatus.NeverStart.equals(status)) {
                //未开始或下载失败
                viewFirst.setVisibility(View.VISIBLE);
                viewFirst.setText(R.string.download);

                viewSecond.setVisibility(View.GONE);
                viewThird.setVisibility(View.GONE);
            } else if (DownLoadExerciseStatus.DownLoading.equals(status)) {
                viewFirst.setVisibility(View.GONE);
                viewSecond.setVisibility(View.VISIBLE);
                viewThird.setVisibility(View.VISIBLE);

                //显示进度
                int progress = 0;
                if (totalSize != 0) {
                    progress = (int) (bytesWritten * 100 / totalSize);
                }

                viewThird.setProgress(progress);
            } else if (DownLoadExerciseStatus.Success.equals(status)) {
                DebugUtil.i("downloadinfo -> onSuccess");
                viewFirst.setVisibility(View.VISIBLE);
                viewFirst.setText(R.string.commit);
                viewSecond.setVisibility(View.GONE);
                viewThird.setVisibility(View.GONE);
            }
        } else if (DownLoadExerciseType.ModuleExerciseActivity.equals(type)) {
            TextView viewFirst = (TextView) viewFirstWeak.get();
            TextView viewSecond = (TextView) viewSecondWeak.get();
            ProgressBar viewThird = (ProgressBar) viewThirdWeak.get();
            RelativeLayout viewFourth = (RelativeLayout) viewFourthWeak.get();

            if (viewFirst == null || viewSecond == null || viewThird == null) {
                return;
            }

            if (DownLoadExerciseStatus.NeverStart.equals(status)) {
                //未开始或下载失败
                viewFourth.setVisibility(View.VISIBLE);
                viewFirst.setVisibility(View.VISIBLE);
                viewFirst.setText(R.string.download);

                viewSecond.setVisibility(View.GONE);
                viewThird.setVisibility(View.GONE);
            } else if (DownLoadExerciseStatus.DownLoading.equals(status)) {
                viewFourth.setVisibility(View.VISIBLE);
                viewFirst.setVisibility(View.GONE);
                viewSecond.setVisibility(View.VISIBLE);
                viewThird.setVisibility(View.VISIBLE);

                //显示进度
                int progress = 0;
                if (totalSize != 0) {
                    progress = (int) (bytesWritten * 100 / totalSize);
                }

                viewThird.setProgress(progress);
            } else if (DownLoadExerciseStatus.Success.equals(status)) {
                viewFourth.setVisibility(View.GONE);
                DebugUtil.i("downloadinfo -> onSuccess");
                viewFirst.setVisibility(View.VISIBLE);
                viewFirst.setText(R.string.commit);
                viewSecond.setVisibility(View.GONE);
                viewThird.setVisibility(View.GONE);
            }
        }
    }

    public void refreshItems(DownLoadExerciseStatus status) {
        refreshItems(0, 0, status);
    }
}
