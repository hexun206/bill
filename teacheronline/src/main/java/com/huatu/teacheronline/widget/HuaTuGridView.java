package com.huatu.teacheronline.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 固定gridview
 * Created by ljyu on 2016/4/27.
 */
public class HuaTuGridView extends GridView {
    public HuaTuGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HuaTuGridView(Context context) {
        super(context);
    }

    public HuaTuGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
