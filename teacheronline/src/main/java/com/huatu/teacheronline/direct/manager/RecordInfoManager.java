package com.huatu.teacheronline.direct.manager;

import com.greendao.DirectBean;
import com.huatu.teacheronline.direct.bean.RecodeRequestFailure;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.orhanobut.logger.Logger;

/**
 * Created by kinndann on 2018/9/28.
 * description:临时只做异常崩溃时听课记录的保存
 */
public class RecordInfoManager {

    private final String TAG = "RecordInfoManager :";

    private void RecordInfoManager() {
    }

    private static class Holder {
        private final static RecordInfoManager instance = new RecordInfoManager();

    }

    public static RecordInfoManager getInstance() {
        return Holder.instance;
    }


    private String mJoinTime;

    private DirectBean mRecodingInfo;

    private String mUid;

    private String mAccount;


    public void init(String uid, String account, String joinTime, DirectBean recodingInfo) {
        mUid = uid;
        mAccount = account;
        mJoinTime = joinTime;
        mRecodingInfo = recodingInfo;
    }


    public void setJoinTime(String time) {
        mJoinTime = time;
    }


    /**
     * 隐藏崩溃后的存储
     *
     * @return
     */
    public boolean saveInUncaughtException() {
        boolean needSave = false;
        if (!StringUtils.isEmpty(mUid) && !StringUtils.isEmpty(mJoinTime) && mRecodingInfo != null && mRecodingInfo.getVideoType() == 1) {

            boolean isPlayback = mRecodingInfo.getVideo_status().equals("2");


            String leavetime = StringUtils.getNowTime();
            if (mRecodingInfo.getReturncash() > 0) {
                RecodeRequestFailure recodeRequestFailure = new RecodeRequestFailure(mUid,
                        mAccount,
                        mRecodingInfo.getOrderid(),
                        mJoinTime,
                        leavetime,
                        mRecodingInfo.getNetClassId(),
                        mRecodingInfo.getLessonid(),
                        isPlayback ? "lubo" : "zhibo");
                boolean save = recodeRequestFailure.save();
                Logger.e(TAG + "crash save result : "+save+"   " + GsonUtils.toJson(recodeRequestFailure));

                mJoinTime = null;
                needSave = true;
            }


        }

        return needSave;


    }


    public void release() {
        mJoinTime = null;
        mRecodingInfo = null;
        mUid = null;
    }


}
