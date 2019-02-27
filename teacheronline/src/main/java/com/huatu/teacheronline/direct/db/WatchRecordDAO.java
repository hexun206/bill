package com.huatu.teacheronline.direct.db;

import com.huatu.teacheronline.direct.bean.WatchRecord;
import com.huatu.teacheronline.direct.bean.WatchRecord_Table;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.orhanobut.logger.Logger;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by kinndann on 2018/8/28.
 * description:
 */
public class WatchRecordDAO {
    private final String TAG = "WatchRecordDAO :";

    private void WatchRecordDAO() {
    }

    private static class Holder {
        private final static WatchRecordDAO instance = new WatchRecordDAO();

    }

    public static WatchRecordDAO getInstance() {
        return Holder.instance;
    }


    public Observable<WatchRecord> queryRecord(final String rid) {

        return Observable.fromCallable(new Callable<WatchRecord>() {
            @Override
            public WatchRecord call() throws Exception {
                String uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
                return new Select().from(WatchRecord.class)
                        .where(WatchRecord_Table.UserId.eq(uid))
                        .and(WatchRecord_Table.rid.eq(rid))
                        .querySingle();


            }
        }).onErrorReturn(new Func1<Throwable, WatchRecord>() {
            @Override
            public WatchRecord call(Throwable throwable) {

                return null;

            }
        }).subscribeOn(Schedulers.io());


    }


    public void addRecord(final String rid, final String courseWareId) {

        if (StringUtils.isEmpty(rid)) {
            return;
        }


        queryRecord(rid).map(new Func1<WatchRecord, Boolean>() {
            @Override
            public Boolean call(WatchRecord watchRecord) {

                if (watchRecord != null) {
                    watchRecord.setCourseWareId(courseWareId);
                } else {
                    String uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
                    watchRecord = new WatchRecord();
                    watchRecord.setCourseWareId(courseWareId);
                    watchRecord.setRid(rid);
                    watchRecord.setUserId(uid);

                }
                watchRecord.setWatchTime(StringUtils.getNowTime());
                boolean result = watchRecord.save();
                return result;

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        Logger.e(TAG + "rid:" + rid + "  coursewareId:" + courseWareId + "  保存结果:" + aBoolean);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(TAG  + throwable.getMessage());
                    }
                });


    }


}
