package com.huatu.teacheronline.direct.db;

import com.huatu.teacheronline.direct.bean.RecodeRequestFailure;
import com.huatu.teacheronline.direct.bean.RecodeRequestFailure_Table;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.orhanobut.logger.Logger;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by kinndann on 2018/8/9/009.
 * description:
 */

public class RecodeRequestFailureDAO {

    private final String TAG = "RecodeRequestFailureDAO :";

    private void RecodeRequestFailureDAO() {
    }

    private static class Holder {
        private final static RecodeRequestFailureDAO instance = new RecodeRequestFailureDAO();

    }

    public static RecodeRequestFailureDAO getInstance() {
        return Holder.instance;
    }


    public Observable<List<RecodeRequestFailure>> queryAll() {

        return Observable.fromCallable(new Callable<List<RecodeRequestFailure>>() {
            @Override
            public List<RecodeRequestFailure> call() throws Exception {
                String uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
                Logger.e(TAG + uid);
                return new Select().from(RecodeRequestFailure.class)
                        .where(RecodeRequestFailure_Table.UserId.eq(uid))
                        .queryList();

            }
        }).subscribeOn(Schedulers.io());


    }


    public void save(final RecodeRequestFailure info) {
        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                boolean result = info.save();
                return result;
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {

                               @Override
                               public void call(Boolean aBoolean) {
                                   Logger.d(TAG + "save " + aBoolean);

                               }
                           }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Logger.e(TAG + throwable.getMessage());
                            }
                        }
                );


    }


    public void deleteByJoinTime(final String joinTime) {
        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                RecodeRequestFailure recodeRequestFailure = new Select().from(RecodeRequestFailure.class)
                        .where(RecodeRequestFailure_Table.JoinTime.eq(joinTime))
                        .querySingle();
                boolean result = false;
                if (recodeRequestFailure != null) {
                    result = recodeRequestFailure.delete();
                }

                return result;
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {

                    @Override
                    public void call(Boolean aBoolean) {

                        Logger.d(TAG + "deleteByJoinTime " + aBoolean);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(TAG  + throwable.getMessage());
                    }
                });


    }


}
