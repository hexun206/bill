package com.huatu.teacheronline.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.exercise.bean.CategoryBean;
import com.huatu.teacheronline.utils.DimensUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 18250 on 2016/10/11.
 */
public class PopwindowUtil {
    private MyAdapter myAdapter;
    private LinearLayout rl_creat_sub;
    private ViewGroup.LayoutParams params;
    private ListView listView;
    private PopupWindow window;
    private Context context;
    //窗口在x轴偏移量
    private int xOff = 0;
    //窗口在y轴的偏移量
    private int yOff = 0;

    int maxSize = 0;
    private int height;

    public PopwindowUtil(Context context, List<CategoryBean> datas, View.OnClickListener OnClickListener) {
        window = new PopupWindow(context);
        this.context = context;
        window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        //点击 back 键的时候，窗口会自动消失
        window.setBackgroundDrawable(new BitmapDrawable());
        View localView = LayoutInflater.from(context).inflate(R.layout.lv_pw_menu, null);
        listView = (ListView) localView.findViewById(R.id.lv_pop_list);
        rl_creat_sub = (LinearLayout) localView.findViewById(R.id.rl_creat_sub);
        rl_creat_sub.setOnClickListener(OnClickListener);
        listView.setDivider(null);
        myAdapter = new MyAdapter(context, datas);
        listView.setAdapter(myAdapter);
        listView.setTag(window);
        //设置显示的视图
        window.setContentView(localView);
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
//        View listItem = listAdapter.getView(0, null, listView);
//        listItem.measure(0, 0);
//        listItem.getMeasuredHeight();//获得高度
        for (int i = 0; i < datas.size(); i++) {
            if (maxSize < datas.get(i).name.length()) {
                maxSize = datas.get(i).name.length();
            }
        }
    }

    public void setItemClickListener(AdapterView.OnItemClickListener listener) {
        listView.setOnItemClickListener(listener);
    }

    public void dismiss() {
        window.dismiss();
    }

    /**
     * @param xOff x轴（左右）偏移
     * @param yOff y轴（上下）偏移
     */
    public void setOff(int xOff, int yOff) {
        this.xOff = xOff;
        this.yOff = yOff;
    }

    /**
     * @param paramView 点击的按钮
     */
    public void show(View paramView, int count) {
        //手动调整窗口的宽度
        if (maxSize > 10) {
            window.setWidth(DimensUtil.getDisplayWidth(context) / 3 + 120);
        } else if (maxSize >= 14) {
            window.setWidth(DimensUtil.getDisplayWidth(context) / 3 + 220);
        } else {
            window.setWidth(DimensUtil.getDisplayWidth(context) / 3 + 20);
        }
        //更新窗口状态
        height = paramView.getHeight();
//        window.showAtLocation(paramView, Gravity.CENTER | Gravity.TOP, 0, height + 58);
//        window.showAsDropDown(paramView,DimensUtil.getDisplayWidth(context)/3-window.getWidth()/2,0);
        window.showAsDropDown(paramView,DimensUtil.getDisplayWidth(context)/3,0);
        window.update();
    }

    public class MyAdapter extends BaseAdapter {
        private Context context;
        private List<CategoryBean> mDatas;

        public MyAdapter(Context context, List<CategoryBean> datas) {
            this.context = context;
            if (datas == null) {
                datas = new ArrayList<>();
            }
            mDatas = datas;
        }

        public void setDatas(List<CategoryBean> datas) {
            mDatas = datas;
            notifyDataSetChanged();
            listView.setSelection(0);
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public CategoryBean getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tvItem;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_pw_menu, null);
                tvItem = (TextView) convertView.findViewById(R.id.tv_item_pw_menu);
                convertView.setTag(tvItem);
            } else {
                tvItem = (TextView) convertView.getTag();
            }
            if (mDatas.size() == 1 || mDatas.size() == 2) {//数量只有一个或者两个的时候启动适应高度
                params = listView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            if (position == 0) {
                tvItem.setTextColor(context.getResources().getColor(R.color.green004));
            } else {
                tvItem.setTextColor(context.getResources().getColor(R.color.black007));
            }
            tvItem.setText(getItem(position).name + "");
            return convertView;
        }
    }

    public MyAdapter getMyAdapter() {
        return myAdapter;
    }
}
