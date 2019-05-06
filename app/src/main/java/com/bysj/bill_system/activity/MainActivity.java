package com.bysj.bill_system.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bysj.bill_system.R;
import com.bysj.bill_system.fragment.BillFragment;
import com.bysj.bill_system.fragment.MineFragment;
import com.bysj.bill_system.fragment.TiebaFragment;
import com.bysj.bill_system.fragment.TripFragment;
import com.bysj.bill_system.utils.ToastUtils;

import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends BaseActivity {


    @BindView(R.id.frCountView)
    FrameLayout frCountView;
    @BindView(R.id.ivTab)
    ImageView ivTab;
    @BindView(R.id.tvTab)
    TextView tvTab;
    @BindView(R.id.llTab)
    LinearLayout llTab;
    @BindView(R.id.ivTab1)
    ImageView ivTab1;
    @BindView(R.id.tvTab1)
    TextView tvTab1;
    @BindView(R.id.llTab1)
    LinearLayout llTab1;
    @BindView(R.id.ivTab2)
    ImageView ivTab2;
    @BindView(R.id.tvTab2)
    TextView tvTab2;
    @BindView(R.id.llTab2)
    LinearLayout llTab2;
    @BindView(R.id.ivTab3)
    ImageView ivTab3;
    @BindView(R.id.tvTab3)
    TextView tvTab3;
    @BindView(R.id.llTab3)
    LinearLayout llTab3;
    @BindView(R.id.ivTab4)
    ImageView ivTab4;
    @BindView(R.id.tvTab4)
    TextView tvTab4;
    @BindView(R.id.llTab4)
    LinearLayout llTab4;
    @BindView(R.id.llTabLayout)
    LinearLayout llTabLayout;
    @BindView(R.id.rlMainActivity)
    RelativeLayout rlMainActivity;

    private HashMap<String, Fragment> fragmentMap = new HashMap<String, Fragment>();
    public FragmentManager mFragmentManager;
    private long firstTime;

    @Override
    void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    void initData() {
        showLoadingDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hiddenLoadingDialog();
            }
        }, 500);
        mFragmentManager = getSupportFragmentManager();
        checkTab(0);
    }

    @OnClick({R.id.vTab, R.id.llTab, R.id.llTab1, R.id.llTab2, R.id.llTab3, R.id.llTab4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.vTab:
            case R.id.llTab:
                ActionActivity.intentActionActivity(this);
                break;
            case R.id.llTab1:
                checkTab(0);
                break;
            case R.id.llTab2:
                checkTab(1);
                break;
            case R.id.llTab3:
                checkTab(2);
                break;
            case R.id.llTab4:
                checkTab(3);
                break;
        }
    }

    private void checkTab(int page) {
        ivTab1.setImageResource(R.mipmap.ic_main_tab1);
        tvTab1.setTextColor(getResources().getColor(R.color.black_5B6C8A));
        ivTab2.setImageResource(R.mipmap.ic_main_tab2);
        tvTab2.setTextColor(getResources().getColor(R.color.black_5B6C8A));
        ivTab3.setImageResource(R.mipmap.ic_main_tab3);
        tvTab3.setTextColor(getResources().getColor(R.color.black_5B6C8A));
        ivTab4.setImageResource(R.mipmap.ic_main_tab4);
        tvTab4.setTextColor(getResources().getColor(R.color.black_5B6C8A));
        switch (page) {
            case 0:
                ivTab1.setImageResource(R.mipmap.ic_main_tab1_c);
                tvTab1.setTextColor(getResources().getColor(R.color.gold_f9));
                break;
            case 1:
                ivTab2.setImageResource(R.mipmap.ic_main_tab2_c);
                tvTab2.setTextColor(getResources().getColor(R.color.gold_f9));
                break;
            case 2:
                ivTab3.setImageResource(R.mipmap.ic_main_tab3_c);
                tvTab3.setTextColor(getResources().getColor(R.color.gold_f9));
                break;
            case 3:
                ivTab4.setImageResource(R.mipmap.ic_main_tab4_c);
                tvTab4.setTextColor(getResources().getColor(R.color.gold_f9));
                break;
        }
        changeNav(page);
    }

    private void hideFragment() {
        List<Fragment> frages = mFragmentManager.getFragments();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (frages != null) {
            for (Fragment frag : frages) {
                transaction.hide(frag);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    private void changeNav(int pos) {
        hideFragment();
        switch (pos) {
            case 0:
                if (null == fragmentMap.get("tab1")) {
                    fragmentMap.put("tab1", new BillFragment());
                    mFragmentManager.beginTransaction().add(R.id.frCountView, fragmentMap.get("tab1"), "tab1").commitAllowingStateLoss();
                }
                mFragmentManager.beginTransaction().show(fragmentMap.get("tab1")).commitAllowingStateLoss();
                break;
            case 1:
                if (fragmentMap.get("tab2") == null) {
                    fragmentMap.put("tab2", new TripFragment());
                    mFragmentManager.beginTransaction().add(R.id.frCountView, fragmentMap.get("tab2"), "tab2").commitAllowingStateLoss();
                }
                mFragmentManager.beginTransaction().show(fragmentMap.get("tab2")).commitAllowingStateLoss();
                break;

            case 2:
                if (fragmentMap.get("tab3") == null) {
                    fragmentMap.put("tab3", new TiebaFragment());
                    mFragmentManager.beginTransaction().add(R.id.frCountView, fragmentMap.get("tab3"), "tab3").commitAllowingStateLoss();
                }
                mFragmentManager.beginTransaction().show(fragmentMap.get("tab3")).commitAllowingStateLoss();
                break;
            case 3:
                if (fragmentMap.get("tab4") == null) {
                    fragmentMap.put("tab4", new MineFragment());
                    mFragmentManager.beginTransaction().add(R.id.frCountView, fragmentMap.get("tab4"), "tab4").commitAllowingStateLoss();
                }
                mFragmentManager.beginTransaction().show(fragmentMap.get("tab4")).commitAllowingStateLoss();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
