package com.huatu.teacheronline.vipexercise.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.greendao.DaoUtils;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.exercise.bean.erbean.PointanalysisBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.vipexercise.vipbean.PaperAnalysiBean;
import com.huatu.teacheronline.widget.AnimatedExpandableListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * vip测评报告 知识树
 * @auto ljyu
 * @time 2017-8-7 10:04:54
 */
public class VipResultChapterTreeAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private AnimatedExpandableListView listview;
    private Context context;
    private LayoutInflater inflater;

    private List<PaperAnalysiBean.PointAnalysisBean> items;
    private ArrayList<String> erjis = new ArrayList<String>();
    private HashMap<Integer, Integer> groupState = new HashMap<Integer, Integer>();

    public VipResultChapterTreeAdapter(Context context, AnimatedExpandableListView listview) {
        this.context = context;
        this.listview = listview;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<PaperAnalysiBean.PointAnalysisBean> items) {
        this.items = items;
        for (int i = 0; i < items.size(); i++) {
            groupState.put(i, 0);
            erjis.clear();
            for (int i1 = 0; i1 < new Random().nextInt(10) + 1; i1++) {
                erjis.add("二级 title："+i1);
                DebugUtil.e("二级 title："+i1);
            }
        }

    }

    @Override
    public PaperAnalysiBean.PointAnalysisBean.ChildListBean getChild(int groupPosition, int childPosition) {

        return items.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        PaperAnalysiBean.PointAnalysisBean.ChildListBean item = getChild(groupPosition, childPosition);
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.item_groupchi_result, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.tv_categoryName);
            holder.tv_group_chi = (TextView) convertView.findViewById(R.id.tv_group_chi);
            holder.v_line4 = convertView.findViewById(R.id.v_line4);
            holder.v_line6 = convertView.findViewById(R.id.v_line6);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        if (items.get(groupPosition).getChildList().size() - 1 == childPosition) {
            holder.v_line4.setVisibility(View.VISIBLE);
            holder.v_line6.setVisibility(View.GONE);
        } else {
            holder.v_line4.setVisibility(View.GONE);
            holder.v_line6.setVisibility(View.VISIBLE);
        }
        holder.title.setText(item.getName());
        holder.tv_group_chi.setText("共"+item.getCount()+"题，答对"+item.getCorrectTimes()+"题，正确率"+item.getCorrectRate());

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).getChildList()==null?0:items.get(groupPosition).getChildList().size();
    }

    @Override
    public PaperAnalysiBean.PointAnalysisBean getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupHolder holder;
        PaperAnalysiBean.PointAnalysisBean item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.item_group_result, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.tv_categoryName);
            holder.tv_group = (TextView) convertView.findViewById(R.id.tv_group);
            holder.img_expanable = (ImageView) convertView.findViewById(R.id.img_expanable);
            holder.v_line4 = convertView.findViewById(R.id.v_line4);
            holder.v_line7 = convertView.findViewById(R.id.v_line7);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }
        holder.img_expanable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    groupState.put(groupPosition, 0);
                    listview.collapseGroup(groupPosition);
                } else {
                    groupState.put(groupPosition, 1);
                    listview.expandGroup(groupPosition);
                }
            }
        });
        holder.v_line7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    groupState.put(groupPosition, 0);
                    listview.collapseGroup(groupPosition);
                } else {
                    groupState.put(groupPosition, 1);
                    listview.expandGroup(groupPosition);
                }
            }
        });
        if (isExpanded) {
            groupState.put(groupPosition, 1);
        } else {
            groupState.put(groupPosition, 0);
        }


        if (groupState.size() > 0 && groupState.get(groupPosition) == 1) {
            holder.v_line4.setVisibility(View.VISIBLE);
            holder.img_expanable.setImageResource(R.drawable.ic_close_expan);
        } else {
            holder.v_line4.setVisibility(View.GONE);
            holder.img_expanable.setImageResource(R.drawable.ic_expanable);
        }
        holder.title.setText(item.getName());
        holder.tv_group.setText("共"+item.getCount()+"题，答对"+item.getCorrectTimes()+"题，正确率"+item.getCorrectRate());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    private class GroupHolder {
        TextView title;
        TextView tv_group;
        ImageView img_expanable;
        View v_line4;
        View v_line7;
    }

    private class ChildHolder {
        TextView title;
        View v_line4;
        View v_line6;
        TextView tv_group_chi;
    }
}