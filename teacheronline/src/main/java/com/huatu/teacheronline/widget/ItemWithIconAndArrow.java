package com.huatu.teacheronline.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.zhy.android.percent.support.PercentRelativeLayout;

/**
 * Created by ljzyuhenda on 16/1/9.
 */
public class ItemWithIconAndArrow extends PercentRelativeLayout {
    private int mIconId;
    private int mArrowId;
    private String mText;
    private ImageView mItem_icon_label;
    private TextView mItem_text;
    private ImageView mItem_icon_arrow;

    public ItemWithIconAndArrow(Context context) {
        super(context);
    }

    public ItemWithIconAndArrow(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context, attrs);
    }

    public ItemWithIconAndArrow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context, attrs);
    }

    void initView(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.item_main_slide, this);
        mItem_icon_label = (ImageView) view.findViewById(R.id.iv_item_icon_label);
        mItem_text = (TextView) view.findViewById(R.id.tv_item_text);
        mItem_icon_arrow = (ImageView) view.findViewById(R.id.iv_item_icon_arrow);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemWithIconAndArrow);
        mIconId = typedArray.getResourceId(R.styleable.ItemWithIconAndArrow_ItemWithIconAndArrow_Icon_Id, -1);
        mArrowId = typedArray.getResourceId(R.styleable.ItemWithIconAndArrow_ItemWithIconAndArrow_Icon_Arrow, -1);
        mText = typedArray.getString(R.styleable.ItemWithIconAndArrow_ItemWithIconAndArrow_Text);

        typedArray.recycle();


        mItem_text.setText(mText);
        mItem_icon_arrow.setImageResource(mArrowId);
        mItem_icon_label.setImageResource(mIconId);
    }
}
