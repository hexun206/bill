package com.bysj.bill_system.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bysj.bill_system.R;
import com.bysj.bill_system.bean.ReplyBean;
import com.bysj.bill_system.bean.TiebaBean;
import com.bysj.bill_system.listener.OnReplyClickListener;
import com.bysj.bill_system.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TiebaDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ReplyBean> list;
    OnReplyClickListener onReplyClickListener;

    public TiebaDetailAdapter(Context context, OnReplyClickListener onReplyClickListener) {
        this.context = context;
        this.list = new ArrayList<>();
        this.onReplyClickListener = onReplyClickListener;
    }

    public void setList(List<ReplyBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1)
            return new TitleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_tieba_detail_title_layout, null, false));
        else
            return new ReplyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_tieba_reply_layout, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 1) {
            TitleViewHolder titleHolder = (TitleViewHolder) holder;
            TiebaBean tiebaBean = list.get(position).tiebaBean;
            titleHolder.discussTitle.setText(tiebaBean.title);
            titleHolder.discussContent.setText(tiebaBean.content);
            titleHolder.discussName.setText(tiebaBean.phone);
            titleHolder.discussGroup.setText("「" + tiebaBean.style + "」");
            titleHolder.discussTime.setText(DateUtils.simpleFormat(tiebaBean.time));
            Glide.with(context).load(tiebaBean.headUrl).apply(new RequestOptions().error(R.mipmap.ic_header).circleCrop()).into(titleHolder.discussImage);
        } else {
            ReplyViewHolder replyViewHolder = (ReplyViewHolder) holder;
            ReplyBean replyBean = list.get(position);
            replyViewHolder.replyName.setText(replyBean.owner);
            replyViewHolder.replyContent.setText(replyBean.content);
            replyViewHolder.replyTime.setText(DateUtils.simpleFormat(replyBean.sendTime));
            Glide.with(context).load("").apply(new RequestOptions().error(R.mipmap.ic_header).circleCrop()).into(replyViewHolder.replyImage);
            replyViewHolder.replyComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReplyClickListener.onClick(replyBean);
                }
            });
            replyViewHolder.llReply.removeAllViews();
            if (replyBean.replyBeanList != null)
                for (ReplyBean bean : replyBean.replyBeanList) {
                    TextView t = new TextView(context);
                    t.setText(Html.fromHtml("<font color='#F9A329'>" + bean.owner + "</font>回复" + "<font color='#4A88FB'>" + bean.toName + "</font>：" + bean.content));
                    t.setTextSize(12);
                    t.setPadding(14, 8, 14, 8);
                    t.setTextColor(context.getResources().getColor(R.color.black_5B6C8A));
                    replyViewHolder.llReply.addView(t, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 1 : 0;
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.discuss_image)
        ImageView discussImage;
        @BindView(R.id.discuss_name)
        TextView discussName;
        @BindView(R.id.discuss_time)
        TextView discussTime;
        @BindView(R.id.discuss_group)
        TextView discussGroup;
        @BindView(R.id.discuss_title)
        TextView discussTitle;
        @BindView(R.id.discuss_content)
        TextView discussContent;

        TitleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class ReplyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.reply_image)
        ImageView replyImage;
        @BindView(R.id.reply_name)
        TextView replyName;
        @BindView(R.id.reply_time)
        TextView replyTime;
        @BindView(R.id.reply_like)
        TextView replyLike;
        @BindView(R.id.reply_comment)
        TextView replyComment;
        @BindView(R.id.reply_content)
        TextView replyContent;
        @BindView(R.id.llReply)
        LinearLayout llReply;

        ReplyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
