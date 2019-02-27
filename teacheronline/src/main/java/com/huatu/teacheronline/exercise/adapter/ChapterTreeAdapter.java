package com.huatu.teacheronline.exercise.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.greendao.DaoUtils;
import com.greendao.StudyRecords;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.exercise.SendRequestUtilsForExercise;
import com.huatu.teacheronline.exercise.bean.CategoryBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.AnimatedExpandableListView;

import java.util.HashMap;
import java.util.List;

/**
 * 模块题海知识树
 * @author ljyu
 * @time 2017-8-7 10:03:19
 */
public class ChapterTreeAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private String userId;
    private DaoUtils mDaoUtils;
    private ExpandableListView listview;
    private Context context;
    private LayoutInflater inflater;

    private List<CategoryBean> items;
    private HashMap<Integer, Integer> groupState = new HashMap<Integer, Integer>();

    public ChapterTreeAdapter(Context context, ExpandableListView listview) {
        this.context = context;
        this.listview = listview;
        inflater = LayoutInflater.from(context);
        mDaoUtils = DaoUtils.getInstance();
        userId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "null");
    }

    public void setData(List<CategoryBean> items) {
        this.items = items;
        for (int i = 0; i < items.size(); i++) {
            groupState.put(i, 0);
        }
    }

    @Override
    public CategoryBean getChild(int groupPosition, int childPosition) {

        return items.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        CategoryBean item = getChild(groupPosition, childPosition);
        StudyRecords studyRecords = mDaoUtils.queryStudyRecordsByType("1", item.cid, SendRequestUtilsForExercise.getKeyForExercisePackageDownload(), userId);
        if (studyRecords !=null){
            DebugUtil.e("getRealChildView: "+studyRecords.toString());
        }
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.item_groupchi_moduleexercise, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.tv_categoryName);
            holder.chile_ratingBar = (RatingBar) convertView.findViewById(R.id.chile_ratingBar);
            holder.tv_group_chi = (TextView) convertView.findViewById(R.id.tv_group_chi);
            holder.v_line4 = convertView.findViewById(R.id.v_line4);
            holder.v_line6 = convertView.findViewById(R.id.v_line6);
            holder.view_line = convertView.findViewById(R.id.view_line);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        if (getGroup(groupPosition).children.size() - 1 == childPosition) {
            holder.v_line4.setVisibility(View.VISIBLE);
            holder.v_line6.setVisibility(View.GONE);
        } else {
            holder.v_line4.setVisibility(View.GONE);
            holder.v_line6.setVisibility(View.VISIBLE);
        }
        holder.title.setText(item.name);

        if (studyRecords == null) {
            holder.chile_ratingBar.setProgress(0);
            holder.tv_group_chi.setText(0 + context.getString(R.string.divide) + item.qids.size());
        } else if ("1".equals(studyRecords.getCompleted())) {
            int totalProgress = studyRecords.getLastprogress();
            int totalExerciseNum = item.qids.size();
            if (totalExerciseNum == 0) {
                holder.chile_ratingBar.setProgress(100);
                holder.tv_group_chi.setText(totalProgress + context.getString(R.string.divide) + totalExerciseNum);
            } else {
                int i = totalProgress * 100 / totalExerciseNum;
                holder.chile_ratingBar.setProgress(i);
                holder.tv_group_chi.setText(totalProgress + context.getString(R.string.divide) + totalExerciseNum);
            }
        } else {
            int totalProgress;
            if (studyRecords.getCurrentprogress() == null) {
                totalProgress = studyRecords.getLastprogress() - studyRecords.getExercisenum();
            } else {
                totalProgress = studyRecords.getLastprogress() + Integer.valueOf(studyRecords.getCurrentprogress()) + 1 - studyRecords.getExercisenum();
            }

            int totalExerciseNum = item.qids.size();

            if (totalExerciseNum == 0) {
                holder.chile_ratingBar.setProgress(100);
                holder.tv_group_chi.setText(totalProgress + context.getString(R.string.divide) + totalExerciseNum);
            } else {
                holder.chile_ratingBar.setProgress(totalProgress * 100 / totalExerciseNum);
                holder.tv_group_chi.setText(totalProgress + context.getString(R.string.divide) + totalExerciseNum);
            }
        }
        if (childPosition == getGroup(groupPosition).children.size()-1) {
            holder.view_line.setVisibility(View.GONE);
        }else {
            holder.view_line.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).children.size();
    }

    @Override
    public CategoryBean getGroup(int groupPosition) {
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
        CategoryBean item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.item_group_moduleexercise, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.tv_categoryName);
            holder.group_ratingBar = (RatingBar) convertView.findViewById(R.id.group_ratingBar);
            holder.tv_group = (TextView) convertView.findViewById(R.id.tv_group);
            holder.img_expanable = (ImageView) convertView.findViewById(R.id.img_expanable);
            holder.btn_right_group_module = convertView.findViewById(R.id.btn_right_group_module);
            holder.v_line4 = convertView.findViewById(R.id.v_line4);
            holder.v_line7 = convertView.findViewById(R.id.v_line7);
            holder.view_line = convertView.findViewById(R.id.view_line);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }
        holder.btn_right_group_module.setOnClickListener(new View.OnClickListener() {
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
            holder.view_line.setVisibility(View.VISIBLE);
        } else {
            holder.v_line4.setVisibility(View.GONE);
            holder.img_expanable.setImageResource(R.drawable.ic_expanable);
            holder.view_line.setVisibility(View.GONE);
        }

//        int prograss = 0;
//        for (int i = 0; i < item.children.size(); i++) {
//            StudyRecords studyRecords = mDaoUtils.queryStudyRecordsByType("1", item.children.get(i).cid, SendRequestUtilsForExercise
//                    .getKeyForExercisePackageDownload
//                            (), userId);
//            if (studyRecords != null) {
//                prograss = prograss + studyRecords.getLastprogress();
//            }
//        }
//        DebugUtil.e("getGroupView:" + groupPosition + " prograss:" + prograss);
        if (groupState.size() > 0) {
            DebugUtil.e("groupState:" + groupState.get(groupPosition));
        }
        for (int i = 0; i < groupState.size(); i++) {
            Integer integer = groupState.get(i);
            DebugUtil.e(" groupState.size():" + " key:" + i + " value:" + integer);
        }
//        if (prograss == 0) {
//            holder.group_ratingBar.setProgress(0);
//            holder.tv_group.setText(prograss + context.getString(R.string.divide) + item.getCount());
//        } else {
//            int i = prograss * 100 / Integer.parseInt(item.getCount());
//            holder.group_ratingBar.setProgress(i);
            holder.tv_group.setText(/*prograss + context.getString(R.string.divide) +*/ item.getCount());
//        }
        holder.title.setText(item.name);

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
        View btn_right_group_module;
        RatingBar group_ratingBar;
        View v_line7;
        View view_line;
    }

    private class ChildHolder {
        TextView title;
        View v_line4;
        View v_line6;
        TextView tv_group_chi;
        RatingBar chile_ratingBar;
        View view_line;
    }
}