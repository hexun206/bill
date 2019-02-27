package com.huatu.teacheronline.direct.db;

import com.huatu.teacheronline.direct.bean.PlayerConfig;
import com.huatu.teacheronline.direct.bean.PlayerConfig_Table;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by kinndann on 2018/8/29.
 * description:
 */
public class PlayerConfigDAO {



    private final String TAG = "PlayerConfigDAO :";

        private void PlayerConfigManager() {
        }

        private static class Holder {
            private final static PlayerConfigDAO instance = new PlayerConfigDAO();

        }

        public static PlayerConfigDAO getInstance() {
            return Holder.instance;
        }




    public Observable<Boolean> isFirstWatch() {

        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
                PlayerConfig playerConfig = new Select().from(PlayerConfig.class)
                        .where(PlayerConfig_Table.UserId.eq(uid))
                        .querySingle();
                boolean isFirstWatch;

                if(playerConfig == null){
                    isFirstWatch = true;
                    playerConfig = new PlayerConfig();
                    playerConfig.setUserId(uid);
                    playerConfig.setFirstWatch(1);
                    playerConfig.save();

                }else{
                    isFirstWatch = playerConfig.isFirstWatch();
                    playerConfig.setFirstWatch(1);

                }


                return isFirstWatch;

            }
        }).onErrorReturn(new Func1<Throwable, Boolean>() {
            @Override
            public Boolean call(Throwable throwable) {

                return false;

            }
        }).subscribeOn(Schedulers.io());


    }

}
