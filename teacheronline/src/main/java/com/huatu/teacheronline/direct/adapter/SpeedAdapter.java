package com.huatu.teacheronline.direct.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.R;

import java.util.Arrays;

/**
 * Created by kinndann on 2018/9/3.
 * description:
 */
public class SpeedAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private final static String[] speedList = {"x1", "x1.25", "x1.5", "x2"};

    private int mSelectPosition;

    public SpeedAdapter() {
        super(R.layout.item_definition, Arrays.asList(speedList));
    }


    public void select(int position) {
        mSelectPosition = position;
        notifyDataSetChanged();

    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_definition_name, item);
        if (getData().get(mSelectPosition).equals(item)) {
            helper.setTextColor(R.id.tv_definition_name, mContext.getResources().getColor(R.color.green004));
        } else {
            helper.setTextColor(R.id.tv_definition_name, mContext.getResources().getColor(R.color.white));
        }


    }

    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        View view = mLayoutInflater.inflate(layoutResId, parent, false);

        int height = CustomApplication.width / 4;
        view.setMinimumHeight(height);
        return view;
    }
}
