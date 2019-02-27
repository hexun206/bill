package com.huatu.teacheronline.direct.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.ppt.LPPPTFragment;

/**
 * Created by kinndann on 2018/6/25/025.
 * description:必须在liveroom成功进入之后加载
 */

public class PPTFragment extends LPPPTFragment {


    public LiveRoom getLiveRoom() {
        return mLiveRoom;
    }

    private LiveRoom mLiveRoom;

    public static PPTFragment newInstance(LiveRoom liveRoom) {
        PPTFragment fragment = new PPTFragment();
        fragment.initLiveRoom(liveRoom);
        fragment.setAnimPPTEnable(false);
        fragment.setLiveRoom(liveRoom);
        fragment.setFlingEnable(true);
        //设置白板不消费事件
        fragment.changePPTTouchAble(true);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void initLiveRoom(LiveRoom liveRoom) {
        mLiveRoom = liveRoom;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPPTShowWay(LPConstants.LPPPTShowWay.SHOW_FULL_SCREEN);
    }

}
