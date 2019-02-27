package com.huatu.teacheronline.personal.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.bean.UploadParamBean;

/**
 * Created by kinndann on 2018/10/31.
 * description:
 */
public class InterviewUploadChoosePhaseAdapter extends BaseQuickAdapter<UploadParamBean.PhasesBean, BaseViewHolder> {


    private int selectedPosition = -1;

    public InterviewUploadChoosePhaseAdapter() {
        super(R.layout.item_video_uoload_phases);

    }


    public void select(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, UploadParamBean.PhasesBean item) {
        int mBlack = mContext.getResources().getColor(R.color.black016);
        int mWhite = mContext.getResources().getColor(R.color.white);
        int position = getData().indexOf(item);
        boolean isSelected = position == selectedPosition;
        helper.setText(R.id.tv_tittle, item.getPhasename())
                .setTextColor(R.id.tv_tittle, isSelected ? mWhite : mBlack)
                .setBackgroundRes(R.id.tv_tittle, isSelected ? R.drawable.bg_corner_round_teal : R.drawable.bg_corner_round_gray);

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
