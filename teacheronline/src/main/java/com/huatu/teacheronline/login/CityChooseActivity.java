package com.huatu.teacheronline.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.ProvinceBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.SectionIndexable;
import com.huatu.teacheronline.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ljzyuhenda on 16/1/14. SectionIndexable接口已被我改变 需要改回。
 * 选择省份下城市
 */
public class CityChooseActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private static final String EXTRA_POSITION_PROVINCE_CURRENTCHOOSE = "extra_position_province_currentchoose";
    private ListView lv_cityList;
//    private CityListAdapter adapter;
    private List<ProvinceBean> mCityBeanList;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private SideBar sidebar;
    private int position;
    private TextView tv_main_title;
    private RelativeLayout rl_main_left;

    @Override
    public void initView() {
        setContentView(R.layout.activity_citychooseactivity_layout);
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.action_name_exit_activity));
        this.registerReceiver(this.broadcastReceiver, filter);
        position = getIntent().getIntExtra(EXTRA_POSITION_PROVINCE_CURRENTCHOOSE, 0);
        lv_cityList = (ListView) findViewById(R.id.lv_cityList);
        mCityBeanList = CommonUtils.getInstance().getProvinceWithCityBeanList().get(position).getCityList();
        Collections.sort(mCityBeanList);
//        adapter = new CityListAdapter();
//        lv_cityList.setAdapter(adapter);
        sidebar = (SideBar) findViewById(R.id.sidebar);
//        sidebar.setListView(lv_cityList);

        String title = CommonUtils.getInstance().getProvinceWithCityBeanList().get(position).Name;
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(title);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        lv_cityList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_main_left:
                back();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra(UserInfo.KEY_SP_CITY_ID, mCityBeanList.get(position).Xzqh);
        intent.putExtra(UserInfo.KEY_SP_CITY_NAME, mCityBeanList.get(position).Name);
        setResult(11, intent);
        finish();
    }

//    private class CityListAdapter extends BaseAdapter implements SectionIndexable {
//
//        @Override
//        public int getCount() {
//            if (mCityBeanList == null) {
//                return 0;
//            }
//            return mCityBeanList.size();
//        }
//
//        @Override
//        public ProvinceBean getItem(int i) {
//            return mCityBeanList.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View view, ViewGroup viewGroup) {
//            ViewHolderForCitylist viewHolderForCitylist;
//            if (view == null) {
//                view = View.inflate(CityChooseActivity.this, R.layout.item_citylist_chooseactivity, null);
//                viewHolderForCitylist = new ViewHolderForCitylist();
//                viewHolderForCitylist.tv_name = (TextView) view.findViewById(R.id.tv_name);
//                viewHolderForCitylist.tv_header = (TextView) view.findViewById(R.id.tv_header);
//                viewHolderForCitylist.view_line1 = view.findViewById(R.id.v_line1);
//                viewHolderForCitylist.view_line2 = view.findViewById(R.id.v_line2);
//                viewHolderForCitylist.view_line3 = view.findViewById(R.id.v_line3);
//                view.setTag(viewHolderForCitylist);
//            }
//
//            viewHolderForCitylist = (ViewHolderForCitylist) view.getTag();
//            viewHolderForCitylist.tv_name.setText(getItem(position).Name);
//
//            if (position == 0 || !(getItem(position - 1).Header.substring(0, 1)).equals(getItem(position).Header.substring(0, 1))) {
//                viewHolderForCitylist.tv_header.setVisibility(View.VISIBLE);
//                viewHolderForCitylist.view_line1.setVisibility(View.VISIBLE);
//
//                viewHolderForCitylist.tv_header.setText(getItem(position).Header.substring(0, 1));
//            } else {
//                viewHolderForCitylist.tv_header.setVisibility(View.GONE);
//                viewHolderForCitylist.view_line1.setVisibility(View.GONE);
//            }
//
//            if (position + 1 >= getCount() || !(getItem(position + 1).Header.substring(0, 1)).equals(getItem(position).Header.substring(0, 1))) {
//                viewHolderForCitylist.view_line2.setVisibility(View.GONE);
//                viewHolderForCitylist.view_line3.setVisibility(View.VISIBLE);
//            } else {
//                viewHolderForCitylist.view_line2.setVisibility(View.VISIBLE);
//                viewHolderForCitylist.view_line3.setVisibility(View.GONE);
//            }
//
//            return view;
//        }
//
//        @Override
//        public List<String> getSections() {
//            List<String> letterList = new ArrayList<>();
//            if (positionOfSection == null) {
//                positionOfSection = new SparseIntArray();
//            }
//
//            if (sectionOfPosition == null) {
//                sectionOfPosition = new SparseIntArray();
//            }
//
//            positionOfSection = new SparseIntArray();
//
//            int count = getCount();
//            for (int i = 0; i < count; i++) {
//                String letter = getItem(i).Header.substring(0, 1).toUpperCase();
//                int section = letterList.size() - 1;
//                if (letterList.size() == 0 || (letterList.get(section) != null && !letterList.get(section).equals(letter))) {
//                    letterList.add(letter);
//                    section++;
//                    positionOfSection.put(section, i);
//                }
//
//                sectionOfPosition.put(i, section);
//            }
//
//            return letterList;
//        }
//
//        @Override
//        public int getPositionForSection(int section) {
//            if (positionOfSection == null) {
//                return -1;
//            }
//
//            return positionOfSection.get(section);
//        }
//    }

    private class ViewHolderForCitylist {
        TextView tv_name;
        TextView tv_header;
        View view_line1;
        View view_line2;
        View view_line3;
    }

    public static void newIntent(Activity context, int position) {
        Intent intent = new Intent(context, CityChooseActivity.class);
        //省份页面选择的省份位置
        intent.putExtra(EXTRA_POSITION_PROVINCE_CURRENTCHOOSE, position);
        context.startActivityForResult(intent, 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.broadcastReceiver);
    }

    /**
     * 注册完登录到首页时，关掉之前所有打开的activity
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}
