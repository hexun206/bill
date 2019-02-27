package com.huatu.teacheronline.direct.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.DefinitionBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kinndann on 2018/9/3.
 * description:
 */
public class DefinitionAdapter extends BaseQuickAdapter<DefinitionBean, BaseViewHolder> {

    private List<DefinitionBean> mDefinitionList = new ArrayList<>();
    private List<DefinitionBean> mOwnedList = new ArrayList<>();
    private String[] nameList = {"流畅", "标清", "高清", "超清", "蓝光"};
    private String[] typeList = {"low", "high", "superHD", "720p", "1080p"};

    private DefinitionBean mSelectedDefinition;

    public DefinitionAdapter() {
        super(R.layout.item_definition);
        initData();
    }


    private void initData() {
        for (int i = 0; i < nameList.length; i++) {
            DefinitionBean definitionBean = new DefinitionBean();
            definitionBean.setDescription(nameList[i]);
            definitionBean.setType(typeList[i]);
            definitionBean.setValue(i);
            mDefinitionList.add(definitionBean);

        }
        Collections.reverse(mDefinitionList);

        setNewData(mDefinitionList);


    }

    public String getNameByValue(int value) {
        return nameList[value];

    }


    public void setOwnedList(List<DefinitionBean> ownedList) {
        mOwnedList.clear();
        mOwnedList.addAll(ownedList);
        notifyDataSetChanged();

    }


    public void select(DefinitionBean selectedDefinition) {
        mSelectedDefinition = selectedDefinition;
        notifyDataSetChanged();


    }

    @Override
    protected void convert(BaseViewHolder helper, DefinitionBean item) {
        helper.setText(R.id.tv_definition_name, item.getDescription());
        if (item.equals(mSelectedDefinition)) {
            helper.setTextColor(R.id.tv_definition_name, mContext.getResources().getColor(R.color.green004));
            helper.getView(R.id.tv_definition_name).setEnabled(true);
        } else if (mOwnedList.contains(item)) {
            helper.setTextColor(R.id.tv_definition_name, mContext.getResources().getColor(R.color.white));
            helper.getView(R.id.tv_definition_name).setEnabled(true);

        } else {
            helper.setTextColor(R.id.tv_definition_name, mContext.getResources().getColor(R.color.gray007));
            helper.getView(R.id.tv_definition_name).setEnabled(false);

        }
        helper.addOnClickListener(R.id.tv_definition_name);


    }

    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        View view = mLayoutInflater.inflate(layoutResId, parent, false);

        int height = CustomApplication.width / 5;
        view.setMinimumHeight(height);
        return view;
    }
}
