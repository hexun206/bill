package com.huatu.teacheronline.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.ProvinceWithCityBean;
import com.huatu.teacheronline.utils.CommonUtils;

import java.util.List;

/**
 * Created by 18250 on 2016/9/24.
 */
public class ExSideBar extends View {
    private Paint paint;
    private String[] sections = new String[]{"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"};
    private int height;
    private ExpandableListView mListView;
    private TextView header;//显示当前选择的字母

    public ExSideBar(Context context) {
        super(context);

        init();
    }

    public ExSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ExSideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.green001));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(CommonUtils.sp2px(11, getContext()));
    }

    public void setListView(ExpandableListView listView) {
        mListView = listView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float center = getWidth() / 2;
        height = getHeight() / sections.length;
        for (int index = sections.length - 1; index > -1; index--) {
            canvas.drawText(sections[index], center, height * (index + 1), paint);
        }
    }

    private int sectionForPoint(float y) {
        int index = (int) (y / height);
        if (index < 0) {
            index = 0;
        }
        if (index > sections.length - 1) {
            index = sections.length - 1;
        }
        return index;
    }

    private void setHeaderTextAndscroll(MotionEvent event) {
        if (mListView == null) {
            //check the mListView to avoid NPE. but the mListView shouldn't be null
            //need to check the call stack later
            return;
        }
        String headerString = sections[sectionForPoint(event.getY())];
        header.setText(headerString);
        SectionIndexable secionIndexable = (SectionIndexable) mListView.getExpandableListAdapter();
        List<String> adapterSections = secionIndexable.getSections();
        try {
            for (int i = adapterSections.size() - 1; i > -1; i--) {
                if (adapterSections.get(i).equals(headerString)) {
                    mListView.setSelection(secionIndexable.getPositionForSection(i));
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("setHeaderTextAndscroll", e.getMessage());
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (header == null) {
                    header = (TextView) ((View) getParent()).findViewById(R.id.floating_side_header);
                }
                setHeaderTextAndscroll(event);
                header.setVisibility(View.VISIBLE);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                setHeaderTextAndscroll(event);
                return true;
            }
            case MotionEvent.ACTION_UP:
                header.setVisibility(View.INVISIBLE);
                setBackgroundColor(Color.TRANSPARENT);
                return true;
            case MotionEvent.ACTION_CANCEL:
                header.setVisibility(View.INVISIBLE);
                setBackgroundColor(Color.TRANSPARENT);
                return true;
        }

        return super.onTouchEvent(event);
    }
}
