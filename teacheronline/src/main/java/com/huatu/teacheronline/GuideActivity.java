package com.huatu.teacheronline;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.huatu.teacheronline.login.LoginActivity;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ImageTools;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.ScrollLayout;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {

    private ViewPager viewPager;
    private List<View> datas = new ArrayList<View>();
    private boolean isLoading = false;
    private int currentPosition;
    LayoutInflater inflater;
    Button btnGo;


    @Override
    public void initView() {
        setContentView(R.layout.activity_guide);
        inflater = LayoutInflater.from(this);
        viewPager = (ViewPager) this.findViewById(R.id.view_pager);
        View v4 = inflater.inflate(R.layout.item_guide, null);
        ImageView page1 = new ImageView(this);
        ImageView page2 = new ImageView(this);
//        ImageView page3 = new ImageView(this);
        ImageView page4 = (ImageView) v4.findViewById(R.id.iv);
        btnGo = (Button) v4.findViewById(R.id.btn_go);
        btnGo.setOnClickListener(mCKListener);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        Bitmap bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.guide_page_one);
        Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.guide_page_two);
//        Bitmap bm3 = BitmapFactory.decodeResource(getResources(), R.drawable.kmduoxk);
        Bitmap bm4 = BitmapFactory.decodeResource(getResources(), R.drawable.guide_page_three);
        Drawable drawable1 = new BitmapDrawable(getResources(), ImageTools.zoomBitmap2(bm1, screenWidth, screenHeight));
        Drawable drawable2 = new BitmapDrawable(getResources(), ImageTools.zoomBitmap2(bm2, screenWidth, screenHeight));
//        Drawable drawable3 = new BitmapDrawable(getResources(), ImageTools.zoomBitmap2(bm3, screenWidth, screenHeight));
        Drawable drawable4 = new BitmapDrawable(getResources(), ImageTools.zoomBitmap2(bm4, screenWidth, screenHeight));
        page1.setBackgroundDrawable(drawable1);
        page2.setBackgroundDrawable(drawable2);
//        page3.setBackgroundDrawable(drawable3);
        page4.setBackgroundDrawable(drawable4);
//        bm1.recycle();
//        bm2.recycle();
//        bm3.recycle();
//        bm4.recycle();
        datas.add(page1);
        datas.add(page2);
//        datas.add(page3);
        datas.add(v4);
        viewPager.setAdapter(new GuidePagerAdapter(datas));

        setListener();
        String channelValue = CommonUtils.getAppMetaData(this, "BaiduMobAd_CHANNEL");
        DebugUtil.e("GuideActivity channelValue:"+channelValue);
        //这边360手机助手才调用
//        SendRequest.putEquipmentInformation(this, new ObtainDataFromNetListener<String, String>() {
//            @Override
//            public void onSuccess(String res) {
//            }
//
//            @Override
//            public void onFailure(String res) {
//
//            }
//        });
    }

    @Override
    public void setListener() {
        viewPager.setOnPageChangeListener(mPCHListener);
        viewPager.setOnTouchListener(mTListener);
    }

    @Override
    public void onClick(View v) {

    }

    private View.OnClickListener mCKListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
//            if(!isLoading)
//                startActivity(new Intent(GuideActivity.this,LoginTaoBaoNewActivity.class));
            boolean iflogin = CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_IFLOGIN);
            Intent intent;
            if (!isLoading) {
                if (iflogin) {
//                    intent = new Intent(GuideActivity.this, MainActivity.class);
                    intent = new Intent(GuideActivity.this, HomeActivity.class);
                } else {
                    intent = new Intent(GuideActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }
    };


    private ViewPager.OnPageChangeListener mPCHListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            currentPosition = position;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            if (position == datas.size() - 1) {
                if (btnGo.getVisibility() == View.GONE) btnGo.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private View.OnTouchListener mTListener = new View.OnTouchListener() {
        private VelocityTracker mVelocityTracker = null;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int index = event.getActionIndex();
            int action = event.getActionMasked();
            int pointerId = event.getPointerId(index);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        mVelocityTracker.clear();
                    }
                    mVelocityTracker.addMovement(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mVelocityTracker == null) {
                        break;
                    }
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000);
                    break;
                case MotionEvent.ACTION_UP:
                    final VelocityTracker velocityTracker = mVelocityTracker;

                    int velocityX = 0;

                    if (mVelocityTracker != null) {
                        velocityTracker.computeCurrentVelocity(1000);
                        velocityX = (int) velocityTracker.getXVelocity();

                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }

                    if (velocityX > ScrollLayout.SNAP_VELOCITY) {
                    } else if (velocityX < -ScrollLayout.SNAP_VELOCITY) {//跳到下一个Activity
                        if (currentPosition == datas.size() - 1) {
                            boolean iflogin = CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_IFLOGIN);
                            Intent intent;
                            if (iflogin) {
//                                intent = new Intent(GuideActivity.this, MainActivity.class);
                                intent = new Intent(GuideActivity.this, HomeActivity.class);
                            } else {
                                intent = new Intent(GuideActivity.this, LoginActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        }
                    } else {
                    }

                    break;
                case MotionEvent.ACTION_CANCEL:
                    mVelocityTracker.recycle();
                    break;
            }
            return false;
        }
    };

    private class GuidePagerAdapter extends PagerAdapter {
        private List<View> imageViews;

        public GuidePagerAdapter(List<View> imageViews) {
            super();
            this.imageViews = imageViews;
        }

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(imageViews.get(arg1), ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
            return imageViews.get(arg1);
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(imageViews.get(position));
        }

    }

}
