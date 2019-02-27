package com.huatu.teacheronline.direct.manager;

import com.huatu.teacheronline.direct.bean.RecodeRequestFailure;
import com.huatu.teacheronline.direct.db.RecodeRequestFailureDAO;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.GsonUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by kinndann on 2018/8/9/009.
 * description:
 */

public class RecodeRequestFailureManager {

    private final String TAG = "RecodeRequestFailureManager :";
    private Subscription mSubscribe;
    private volatile boolean isChecking = false;

    private void RecodeRequestFailureManager() {
    }

    private static class Holder {
        private final static RecodeRequestFailureManager instance = new RecodeRequestFailureManager();

    }

    public static RecodeRequestFailureManager getInstance() {
        return Holder.instance;
    }


    public void checkRequestRetry() {
        if (isChecking) {
            return;
        }
        isChecking = true;
        release();
        mSubscribe = RecodeRequestFailureDAO.getInstance().queryAll()
                .map(new Func1<List<RecodeRequestFailure>, Integer>() {
                    @Override
                    public Integer call(List<RecodeRequestFailure> recodeRequestFailures) {
                        int count = 0;
                        if (recodeRequestFailures != null) {
                            Logger.e(TAG +GsonUtils.toJson(recodeRequestFailures));
                            for (RecodeRequestFailure recodeRequestFailure : recodeRequestFailures) {
                                String result = SendRequest.addRecodeSync(recodeRequestFailure);
                                if (result != null) {
                                    deleteByJoinTime(recodeRequestFailure.getJoinTime());

                                }
                                count++;
                            }

                        }


                        return count;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Logger.e(TAG + "success:" + integer);
                        isChecking = false;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(TAG  + throwable.getMessage());
                    }
                });


    }

    public void release() {
        if (mSubscribe != null) {
            mSubscribe.unsubscribe();
            mSubscribe = null;
        }
    }


    public void save(RecodeRequestFailure info) {
        RecodeRequestFailureDAO.getInstance().save(info);


    }


    public void deleteByJoinTime(String joinTime) {
        RecodeRequestFailureDAO.getInstance().deleteByJoinTime(joinTime);
    }


}
