package com.huatu.teacheronline.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huatu.teacheronline.H5DetailActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.AdBean;
import com.huatu.teacheronline.direct.DirectDetailsActivity;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.widget.rollviewpager.RollPagerView;
import com.huatu.teacheronline.widget.rollviewpager.adapter.LoopPagerAdapter;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 广告轮播adpter
 * Created by ljyu on 2016/6/2.
 */
public class AdLoopAdapter extends LoopPagerAdapter {
    private Context context;
    private ArrayList<AdBean> adBeans = new ArrayList<>();
    private int count = 0;

    public void add() {
        count++;
        if (count > adBeans.size()) count = adBeans.size();
        notifyDataSetChanged();
    }

    public void minus() {
        count--;
        if (count < 1) count = 1;
        notifyDataSetChanged();
    }

    public AdLoopAdapter(RollPagerView viewPager, ArrayList<AdBean> adBeans, Context context) {
        super(viewPager);
        this.adBeans = adBeans;
        this.context = context;
        count = adBeans.size();
    }

    @Override
    public View getView(ViewGroup container, final int position) {
        SimpleDraweeView view = new SimpleDraweeView(container.getContext());
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(context.getResources());
        final GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.ic_ad_loading))
                .setFailureImage(context.getResources().getDrawable(R.drawable.ic_ad_loading))
                .build();
        view.setScaleType(ImageView.ScaleType.MATRIX);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adBeans.get(position) != null) {
                    if(Integer.parseInt(adBeans.get(position).getIs_array()) == 2){
                        DirectDetailsActivity.newIntent(context,adBeans.get(position).getGg_rid());
                    }else {
                        MobclickAgent.onEvent(context, "adsOnclik");
                        H5DetailActivity.newIntent(context, adBeans.get(position).getAd_title(), adBeans.get(position).getAd_url(), adBeans.get(position)
                                .getIs_array());
                    }
                }
            }
        });
        view.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(view, adBeans.get(position).getAd_pic(), R.drawable.ic_ad_loading);
        return view;
    }

    @Override
    public int getRealCount() {
        return count;
    }

}