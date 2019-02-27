package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.frament.MyDirectAllFragment;
import com.huatu.teacheronline.personal.frament.MyDirectTabFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * 2016/2/29.我的课程与我的直播
 */
public class MyDirectActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
//    private TextView tv_main_title;
    private View loadView;
    private View loadIcon;
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;
    private RelativeLayout rl_face;
    private RelativeLayout rl_liveclass;
    private TextView tv_face;
    private TextView tv_liveclass;
    private LinearLayout rl_direct;
    private Map<String, Fragment> mapFragmet = new HashMap<>();
    private MyDirectAllFragment myDirectAllFragment;
    private FragmentManager manager;
    private MyDirectTabFragment myDirectTabFragment;
    private String key;//传过来的key如果为1则片段为我的直播课 0 初始片段为我的课程
    private View v_face;
    private View v_live;
    private TextView tv_faces;
    private TextView tv_liveclas;
    private MyDirectTabFragment mMyWisdomClassFragment;
    private TextView tv_wisdom;
    private View v_wisdom;

    @Override
    public void initView() {
        setContentView(R.layout.activity_mydirect);
        key = getIntent().getStringExtra("key");
        myDirectTabFragment = new MyDirectTabFragment();//我的班级通知
        mMyWisdomClassFragment = new MyDirectTabFragment();//智学课通知
        Bundle bundle = new Bundle();
        bundle.putBoolean(MyDirectTabFragment.KEY_ISWISDOMCLASS,true);
        mMyWisdomClassFragment.setArguments(bundle);


        myDirectAllFragment = new MyDirectAllFragment();//我的直播
        manager = getSupportFragmentManager();
//        manager.beginTransaction().add(R.id.rl_root, myDirectTabFragment).commit();
//        mapFragmet.put("fragment", myDirectTabFragment);
        rl_direct = (LinearLayout) findViewById(R.id.rl_directlayout);
        rl_face = (RelativeLayout) findViewById(R.id.rl_face);
        rl_face.setOnClickListener(this);
        rl_liveclass = (RelativeLayout) findViewById(R.id.rl_liveclass);
        rl_liveclass.setOnClickListener(this);
        tv_face = (TextView) findViewById(R.id.tv_face);
        tv_liveclass = (TextView) findViewById(R.id.tv_liveclass);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
//        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
//        tv_main_title.setText(R.string.class_my);

        tv_faces = (TextView) findViewById(R.id.tv_faces);
        tv_liveclas = (TextView) findViewById(R.id.tv_liveclas);
        tv_wisdom = (TextView) findViewById(R.id.tv_wisdom);
        v_face = findViewById(R.id.v_face);
        v_live = findViewById(R.id.v_live);
        v_wisdom = findViewById(R.id.v_wisdom);

//        if (key.equals("1")){
//            rl_direct.setBackgroundColor(getResources().getColor(R.color.white));
//            tv_liveclass.setTextColor(getResources().getColor(R.color.green001));
//            tv_face.setTextColor(getResources().getColor(R.color.black));
//            manager.beginTransaction().add(R.id.rl_root, myDirectAllFragment).commit();
//            mapFragmet.put("fragment", myDirectAllFragment);
//            key="0";
//        }else{
            manager.beginTransaction().add(R.id.rl_root, myDirectTabFragment).commit();
            mapFragmet.put("fragment", myDirectTabFragment);
//        }
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);

    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        tv_faces.setOnClickListener(this);
        tv_liveclas.setOnClickListener(this);
        tv_wisdom.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
            case R.id.tv_liveclas:
                if (myDirectAllFragment!=null && myDirectAllFragment.isVisible()){
                    return;
                }else{
                    if (myDirectAllFragment.isAdded()) {
                        manager.beginTransaction().hide(mapFragmet.get("fragment")).show(myDirectAllFragment).commit();
                    } else {
                        manager.beginTransaction().hide(mapFragmet.get("fragment")).add(R.id.rl_root, myDirectAllFragment).commit();
                    }
                }
                rl_direct.setBackgroundColor(getResources().getColor(R.color.white));
                tv_liveclass.setTextColor(getResources().getColor(R.color.green001));
                tv_face.setTextColor(getResources().getColor(R.color.black));

                v_face.setVisibility(View.GONE);
                v_live.setVisibility(View.VISIBLE);
                tv_liveclas.setTextColor(getResources().getColor(R.color.green013));
                tv_faces.setTextColor(getResources().getColor(R.color.gray013));
                tv_wisdom.setTextColor(getResources().getColor(R.color.gray013));
                v_wisdom.setVisibility(View.GONE);
                mapFragmet.put("fragment", myDirectAllFragment);
                break;
            case  R.id.tv_faces:
                if (myDirectTabFragment != null && myDirectTabFragment.isVisible()) {
                    return;
                } else{
                    if (myDirectTabFragment.isAdded()) {//判断片段是否已经添加
                        manager.beginTransaction().hide(mapFragmet.get("fragment")).show(myDirectTabFragment).commit();
                    } else {
                        manager.beginTransaction().hide(mapFragmet.get("fragment")).add(R.id.rl_root, myDirectTabFragment).commit();
                    }
                }
                loadIcon.startAnimation(refreshingAnimation);
                rl_direct.setBackgroundColor(getResources().getColor(R.color.white008));
                tv_face.setTextColor(getResources().getColor(R.color.green001));
                tv_liveclass.setTextColor(getResources().getColor(R.color.black));

                v_face.setVisibility(View.VISIBLE);
                v_live.setVisibility(View.GONE);
                tv_liveclas.setTextColor(getResources().getColor(R.color.gray013));

                tv_faces.setTextColor(getResources().getColor(R.color.green013));

                tv_wisdom.setTextColor(getResources().getColor(R.color.gray013));
                v_wisdom.setVisibility(View.GONE);
                mapFragmet.put("fragment", myDirectTabFragment);
                break;

            case R.id.tv_wisdom:

                if (mMyWisdomClassFragment != null && mMyWisdomClassFragment.isVisible()) {
                    return;
                } else{
                    if (mMyWisdomClassFragment.isAdded()) {//判断片段是否已经添加
                        manager.beginTransaction().hide(mapFragmet.get("fragment")).show(mMyWisdomClassFragment).commit();
                    } else {
                        manager.beginTransaction().hide(mapFragmet.get("fragment")).add(R.id.rl_root, mMyWisdomClassFragment).commit();
                    }
                }
                loadIcon.startAnimation(refreshingAnimation);
                rl_direct.setBackgroundColor(getResources().getColor(R.color.white008));
                tv_face.setTextColor(getResources().getColor(R.color.green001));
                tv_liveclass.setTextColor(getResources().getColor(R.color.black));

                v_face.setVisibility(View.GONE);
                v_live.setVisibility(View.GONE);
                tv_liveclas.setTextColor(getResources().getColor(R.color.gray013));
                tv_faces.setTextColor(getResources().getColor(R.color.gray013));
                tv_wisdom.setTextColor(getResources().getColor(R.color.green013));
                v_wisdom.setVisibility(View.VISIBLE);
                mapFragmet.put("fragment", mMyWisdomClassFragment);
                break;


        }
    }

    public static void newIntent(Activity context , String key) {
        Intent intent = new Intent(context, MyDirectActivity.class);
        intent.putExtra("key",key);
        context.startActivity(intent);
    }

}
