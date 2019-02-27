package com.huatu.teacheronline.utils;

import android.net.Uri;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by KaelLi on 2015/9/22.
 * Fresco设置图片加载URL
 */
public class FrescoUtils {

    /**
     * 意外情况下，获取不到图片的加载地址，此时显示默认图片，故有此函数预防异常情况
     *
     * @param mSimpleDraweeView 需要加载图片的SimpleDraweeView实例
     * @param imageUrl          图片的加载地址
     * @param resId             默认图片的资源id
     */
    public static void setFrescoImageUri(SimpleDraweeView mSimpleDraweeView, String imageUrl, int resId) {
        Uri uri;
        if (imageUrl == null) {
            uri = Uri.parse("res:///" + resId);
        } else {
            uri = Uri.parse(imageUrl);
        }
        mSimpleDraweeView.setImageURI(uri);
    }
}
