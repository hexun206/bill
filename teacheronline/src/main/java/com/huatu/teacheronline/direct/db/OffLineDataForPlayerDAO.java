package com.huatu.teacheronline.direct.db;

import com.huatu.teacheronline.direct.bean.OffLineDataForPlayer;
import com.huatu.teacheronline.direct.bean.OffLineDataForPlayer_Table;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.orhanobut.logger.Logger;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by kinndann on 2018/9/5.
 * description:
 */
public class OffLineDataForPlayerDAO {
    private final String TAG = "OffLineDataForPlayerDAO :";

    private void OffLineDataForPlayerDAO() {
    }

    private static class Holder {
        private final static OffLineDataForPlayerDAO instance = new OffLineDataForPlayerDAO();

    }

    public static OffLineDataForPlayerDAO getInstance() {
        return Holder.instance;
    }


    public void save(final String rid, final String json) {

        queryByRid(rid)
                .map(new Func1<OffLineDataForPlayer, Boolean>() {
                    @Override
                    public Boolean call(OffLineDataForPlayer offLineDataForPlayer) {

                        if (offLineDataForPlayer != null) {
                            offLineDataForPlayer.setJson(json);
                        } else {
                            String uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
                            offLineDataForPlayer = new OffLineDataForPlayer();
                            offLineDataForPlayer.setJson(json);
                            offLineDataForPlayer.setRid(rid);
                            offLineDataForPlayer.setUid(uid);

                        }

                        return offLineDataForPlayer.save();

                    }
                }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                Logger.e(TAG + "rid:" + rid + "  保存结果:" + aBoolean);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Logger.e(TAG  + throwable.getMessage());
            }
        });


    }


    public Observable<OffLineDataForPlayer> queryByRid(final String rid) {

        return Observable.fromCallable(new Callable<OffLineDataForPlayer>() {
            @Override
            public OffLineDataForPlayer call() throws Exception {
                String uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
                return new Select().from(OffLineDataForPlayer.class)
                        .where(OffLineDataForPlayer_Table.uid.eq(uid))
                        .and(OffLineDataForPlayer_Table.rid.eq(rid))
                        .querySingle();


            }
        }).onErrorReturn(new Func1<Throwable, OffLineDataForPlayer>() {
            @Override
            public OffLineDataForPlayer call(Throwable throwable) {

                return null;

            }
        }).subscribeOn(Schedulers.io());


    }


}
