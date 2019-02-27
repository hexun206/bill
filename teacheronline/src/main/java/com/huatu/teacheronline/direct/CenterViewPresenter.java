package com.huatu.teacheronline.direct;


import android.view.View;

import com.baijiahulian.player.playerview.BJCenterViewPresenter;

/**
 * Created by kinndann on 2018/12/27.
 * description:
 */
public class CenterViewPresenter extends BJCenterViewPresenter {
    private View mRootView;

    public CenterViewPresenter(View view) {
        super(view);
        mRootView = view;
    }


    @Override
    public void showWarning(String s) {
        super.showWarning(s);


    }

    @Override
    public void dismissLoading() {
        super.dismissLoading();
//        mRootView.findViewById(R.id.bjplayer_center_video_progress_dialog_ll).setVisibility(View.GONE);

    }
}
