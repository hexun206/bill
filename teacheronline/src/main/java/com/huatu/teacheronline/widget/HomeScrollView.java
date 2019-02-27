package com.huatu.teacheronline.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by 18250 on 2017/9/15.
 */
public class HomeScrollView extends ScrollView {
    private Context mContext;
    public HomeScrollView(Context context) {
        super(context);
        init(context);
    }

    public HomeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public HomeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }
}
