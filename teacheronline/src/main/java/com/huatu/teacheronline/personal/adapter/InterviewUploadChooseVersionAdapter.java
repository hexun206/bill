package com.huatu.teacheronline.personal.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.bean.UploadParamBean;

import java.util.List;

/**
 * Created by kinndann on 2018/10/31.
 * description:
 */
public class InterviewUploadChooseVersionAdapter extends BaseQuickAdapter<UploadParamBean.PhasesBean.SubjectBean.VersionBean, BaseViewHolder> {


    private int selectedPosition = -1;

    public InterviewUploadChooseVersionAdapter(List<UploadParamBean.PhasesBean.SubjectBean.VersionBean> versions) {
        super(R.layout.item_choose_param,versions);

    }


    public void select(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, UploadParamBean.PhasesBean.SubjectBean.VersionBean item) {

        int mBlack = mContext.getResources().getColor(R.color.black007);
        int mWhite = mContext.getResources().getColor(R.color.white);
        int mTeal = mContext.getResources().getColor(R.color.green004);
        int position = getData().indexOf(item);
        boolean isSelected = position == selectedPosition;
        helper.setText(R.id.tv_choose_param_tittle, item.getVersionname())
                .setTextColor(R.id.tv_choose_param_tittle, isSelected ? mWhite : mBlack)
                .setBackgroundColor(R.id.tv_choose_param_tittle, isSelected ? mTeal : mWhite);

    }


    public int getSelectedPosition() {
        return selectedPosition;
    }


//    public final static class ViewHolder extends  BaseViewHolder{
//
//        public ViewHolder(View view) {
//            super(view);
//        }
//
//
//
//
//    }

}
