package com.bysj.bill_system.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bysj.bill_system.R;
import com.bysj.bill_system.activity.TiebaDetailActivity;
import com.bysj.bill_system.bean.TiebaBean;
import com.bysj.bill_system.utils.DataUtils;
import com.bysj.bill_system.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TiebaAdapter extends RecyclerView.Adapter<TiebaAdapter.ViewHolder> {
    Context context;
    List<TiebaBean> list;

    public TiebaAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void setData(List<TiebaBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_tieba_layout, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TiebaBean tiebaBean = list.get(position);
        holder.discussTitle.setText(tiebaBean.title);
        holder.discussContent.setText(tiebaBean.content);
        holder.discussName.setText(tiebaBean.phone);
        holder.discussGroup.setText("「" + tiebaBean.style + "」");
        holder.discussLike.setText("" + new Random().nextInt(100));
        if (tiebaBean.replys != null)
            holder.discussComment.setText("" + tiebaBean.replys.size());
        holder.discussTime.setText(DateUtils.simpleFormat(tiebaBean.time));
        Glide.with(context).load(tiebaBean.headUrl).apply(new RequestOptions().error(R.mipmap.ic_header).circleCrop()).into(holder.discussImage);
        holder.llContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, TiebaDetailActivity.class).putExtra("id", tiebaBean.id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.discuss_title)
        TextView discussTitle;
        @BindView(R.id.discuss_image)
        ImageView discussImage;
        @BindView(R.id.discuss_name)
        TextView discussName;
        @BindView(R.id.discuss_group)
        TextView discussGroup;
        @BindView(R.id.discuss_content)
        TextView discussContent;
        @BindView(R.id.discuss_time)
        TextView discussTime;
        @BindView(R.id.discuss_like)
        TextView discussLike;
        @BindView(R.id.discuss_comment)
        TextView discussComment;
        @BindView(R.id.llContent)
        View llContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
