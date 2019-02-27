package com.huatu.teacheronline.personal.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.personal.bean.CanUploadInterviewVideoBean;

/**
 * Created by kinndann on 2018/10/30.
 * description:
 */
public class InterviewCommentsAdapter extends BaseQuickAdapter<CanUploadInterviewVideoBean.ClasslistBean, BaseViewHolder> {


    public InterviewCommentsAdapter() {
        super(R.layout.item_interview_comments);
    }

    @Override
    protected void convert(BaseViewHolder helper, CanUploadInterviewVideoBean.ClasslistBean item) {
        helper.addOnClickListener(R.id.tv_inteview_comments_result)
                .addOnClickListener(R.id.tv_inteview_comments_upload)
                .setText(R.id.tv_interview_comments_tittle, item.getNetclassname())
                .setText(R.id.tv_interview_comments_count, item.isOutOfDate() ? item.getState() + " (已过期)" : item.getState())
                .setGone(R.id.fra_inteview_comments_result, !"0".equals(item.getIsnumbers()))
                .setGone(R.id.tv_inteview_comments_upload, item.getIscomment() == 1)
                .setVisible(R.id.view_inteview_comments_msg, item.hasUnReadInfo());
        View view = helper.getView(R.id.line_dash);
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        view.setVisibility(View.GONE);
        if (!"0".equals(item.getIsnumbers()) || item.getIscomment() == 1) {
            view.setVisibility(View.VISIBLE);
        }


    }
}
