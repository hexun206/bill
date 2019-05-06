package com.bysj.bill_system.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bysj.bill_system.R;
import com.bysj.bill_system.bean.BillBean;
import com.bysj.bill_system.listener.RemoveBillDataListener;
import com.bysj.bill_system.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {

    private List<BillBean> data;
    private Context context;
    private RemoveBillDataListener removeBillDataListener;

    public void setData(List<BillBean> data) {
        this.data.clear();
        this.data.addAll(data);
        resetData();
    }

    public BillAdapter(Context context, RemoveBillDataListener removeBillDataListener) {
        this.context = context;
        this.removeBillDataListener = removeBillDataListener;
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_bill_layout, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvType.setText(data.get(position).type);
        holder.tvMoney.setText((data.get(position).isIncome ? "+" : "-") + data.get(position).money);
        if (position == 0 || !DateUtils.isSameDay(data.get(position).time, data.get(position - 1).time)) {
            holder.llTime.setVisibility(View.VISIBLE);
            holder.vDivider.setVisibility(View.VISIBLE);
            holder.tvTime.setText(DateUtils.formatDate(data.get(position).time));
            StringBuffer sb = new StringBuffer();
            if (data.get(position).totalIncome != 0)
                sb.append("收入 " + data.get(position).totalIncome);
            sb.append("     ");
            if (data.get(position).totalSpending != 0)
                sb.append("支出 " + data.get(position).totalSpending);
            holder.tvContent.setText(sb.toString());
        } else {
            holder.llTime.setVisibility(View.GONE);
            holder.vDivider.setVisibility(View.GONE);
        }
        holder.llContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("确认删除该条记账记录？");
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (removeBillDataListener != null)
                            removeBillDataListener.removed(data.get(holder.getAdapterPosition()));
                        remove(holder.getAdapterPosition());
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.create();
                dialog.show();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void add(int position, BillBean data) {
        this.data.add(position, data);
        notifyItemInserted(position);
    }

    public void change(int position, BillBean data) {
        this.data.remove(position);
        this.data.add(position, data);
        notifyItemChanged(position);
    }

    private void resetData() {
        int startIndex = 0;//记录当日的开始游标点
        int endIndex;  //记录当日的结束游标点
        double totalI = 0;//统计当日收入
        double totalS = 0;//统计当日支出
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isIncome)
                totalI += data.get(i).money;
            else
                totalS += data.get(i).money;
            if (i == 0)
                startIndex = i;
            else if (!DateUtils.isSameDay(data.get(i).time, data.get(i - 1).time) || i == data.size() - 1) {
                endIndex = i;
                for (int j = startIndex; j < endIndex; j++) {
                    data.get(j).totalIncome = totalI;
                    data.get(j).totalSpending = totalS;
                }
                startIndex = endIndex;
                totalI = 0;
                totalS = 0;
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.tvContent)
        TextView tvContent;
        @BindView(R.id.llTime)
        LinearLayout llTime;
        @BindView(R.id.ivLogo)
        ImageView ivLogo;
        @BindView(R.id.tvType)
        TextView tvType;
        @BindView(R.id.tvMoney)
        TextView tvMoney;
        @BindView(R.id.vDivider)
        View vDivider;
        @BindView(R.id.llContent)
        View llContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
