package com.huatu.teacheronline.direct.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.baijiahulian.livecore.context.LiveRoom;
import com.huatu.teacheronline.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 教师音视频界面
 */
public class SpeakerFragment extends Fragment {


    @BindView(R.id.surface_speaker)
    SurfaceView mSurfaceSpeaker;
    Unbinder unbinder;
    private View mRoot_view;
    private LiveRoom mLiveRoom;

    public SpeakerFragment() {
    }

    public static SpeakerFragment newInstance(LiveRoom liveRoom) {
        SpeakerFragment fragment = new SpeakerFragment();
        fragment.setLiveRoom(liveRoom);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setLiveRoom(LiveRoom liveRoom) {
        mLiveRoom = liveRoom;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot_view = inflater.inflate(R.layout.fragment_speaker, container, false);
        unbinder = ButterKnife.bind(this, mRoot_view);
        EventBus.getDefault().register(this);
        return mRoot_view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        if (mLiveRoom != null) {
            mLiveRoom.getPlayer().playAVClose(mLiveRoom.getCurrentUser().getUserId());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initViedo() {
        if (mLiveRoom != null) {
            mLiveRoom.getPlayer().playVideo(mLiveRoom.getCurrentUser().getUserId(), mSurfaceSpeaker);
            mLiveRoom.getPlayer().playAudio(mLiveRoom.getCurrentUser().getUserId());
        }


    }




}
