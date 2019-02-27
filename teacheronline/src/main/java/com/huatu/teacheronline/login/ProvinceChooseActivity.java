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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.ProvinceBean;
import com.huatu.teacheronline.bean.ProvinceWithCityBean;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.ExSideBar;
import com.huatu.teacheronline.widget.SectionIndexable;
import com.huatu.teacheronline.widget.SideBar;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ljzyuhenda on 16/1/18.
 */
public class ProvinceChooseActivity extends BaseActivity implements ExpandableListView.OnGroupExpandListener, ExpandableListView.OnChildClickListener {
    private ExpandableListView lv_cityList;
    private CityListAdapter adapter;
    private ArrayList<ProvinceWithCityBean> mProvinceWithCityBeanList = new ArrayList<ProvinceWithCityBean>();
    ;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private ExSideBar sidebar;
    private TextView tv_main_title;
    private RelativeLayout rl_main_left;
    private int extraType;//2 真题过来的页面
    private List<List<ProvinceBean>> mLists = new ArrayList<>();//遍历展开后的数组
    private List<ProvinceBean> cityList;
    private List<ProvinceBean> Citybeans;//获得展开后的数组（遍历前）
    private List<ProvinceBean> mCityBeanList;

    @Override
    public void initView() {
        setContentView(R.layout.activity_citychooseactivity_layout);
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.action_name_exit_activity));
        this.registerReceiver(this.broadcastReceiver, filter);
        extraType = getIntent().getIntExtra(UserInfo.KEY_SP_SELECT_AREA_TYPE, 0);
        lv_cityList = (ExpandableListView) findViewById(R.id.lv_cityList);
        lv_cityList.setGroupIndicator(null);//隐藏Exlistview自带的箭头
//        mProvinceWithCityBeanList = CommonUtils.getInstance().getProvinceWithCityBeanList();
        adapter = new CityListAdapter();
        sidebar = (ExSideBar) findViewById(R.id.sidebar);
        sidebar.setListView(lv_cityList);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.city_chooose);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        CityData();
    }

    private void CityData() {
        ObtationListener obtationListener = new ObtationListener(this);
        SendRequest.getlicense(obtationListener);
    }

    private static class ObtationListener extends ObtainDataFromNetListener<ArrayList<ProvinceWithCityBean>, String> {
        private ProvinceChooseActivity weak_activity;

        public ObtationListener(ProvinceChooseActivity contextWeakReference) {
            weak_activity = new WeakReference<>(contextWeakReference).get();
        }

        public void onSuccess(final ArrayList<ProvinceWithCityBean> res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res != null){
                            weak_activity.sidebar.setVisibility(View.VISIBLE);
                            weak_activity.flushContent_OnSucess(res);
                            weak_activity.lv_cityList.setAdapter(weak_activity.adapter);
                            weak_activity.adapter.notifyDataSetChanged();
                        }else {
                            ToastUtils.showToast(R.string.network);
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(String res) {
            weak_activity.sidebar.setVisibility(View.GONE);
            ToastUtils.showToast(R.string.network);
        }
    }

    public void flushContent_OnSucess(List<ProvinceWithCityBean> res) {
        mProvinceWithCityBeanList.addAll(res);
        Collections.sort(mProvinceWithCityBeanList);
        for (int i = 0; i < mProvinceWithCityBeanList.size(); i++) {//遍历数据后保存起来
            List<ProvinceBean> ProvinceBeanList = mProvinceWithCityBeanList.get(i).getCityList();
            if (null != ProvinceBeanList && ProvinceBeanList.size() > 0) {
                mLists.add(ProvinceBeanList);
            } else {
                cityList = new ArrayList<>();//如果为null数组就new一个添加
                mLists.add(ProvinceBeanList);
            }
        }
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        lv_cityList.setOnGroupExpandListener(this);//EXlistview父控件监听
        lv_cityList.setOnChildClickListener(this);//EXlistview子控件监听
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_main_left:
                back();
                break;
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (null != data) {
//            switch (resultCode) {
//                case 11:// 选择城市后返回
//                    String xzqh = data.getStringExtra(UserInfo.KEY_SP_CITY_ID);
//                    String cityName = data.getStringExtra(UserInfo.KEY_SP_CITY_NAME);
//                    setResultActivity(xzqh, cityName);
//                    break;
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    // 省返回上个页面
    private void setResultProvinActivity(String xzqh, String cityName) {
        Intent intent = new Intent();
        intent.putExtra(UserInfo.KEY_SP_PROVINCE_ID, xzqh);
        intent.putExtra(UserInfo.KEY_SP_PROVINCE_NAME, cityName);
        setResult(10, intent);
        finish();
    }
    // 市返回上个页面
    private void setResultCityActivity(String pXzqh, String pName,String xzqh, String cityName) {
        Intent intent = new Intent();
        intent.putExtra(UserInfo.KEY_SP_CITY_ID, xzqh);
        intent.putExtra(UserInfo.KEY_SP_CITY_NAME, cityName);
        intent.putExtra(UserInfo.KEY_SP_PROVINCE_ID, pXzqh);
        intent.putExtra(UserInfo.KEY_SP_PROVINCE_NAME, pName);
        setResult(10, intent);
        finish();
    }

    // 返回上个页面
    private void setResultActivity(String provinceName) {
        Intent intent = new Intent();
        intent.putExtra(UserInfo.KEY_SP_CITY_NAME, provinceName);
        setResult(2, intent);
        finish();
    }

    @Override
    public void onGroupExpand(int groupPosition) {//EXlistview父控件的监听
        Citybeans = mProvinceWithCityBeanList.get(groupPosition).getCityList();//获得省市的数组
        if (null != Citybeans && Citybeans.size() > 0) {
            for (int i = 0, count = lv_cityList.getExpandableListAdapter().getGroupCount(); i < count; i++) {
                if (groupPosition != i) {// 判断当展开一项时关闭其他分组
                    lv_cityList.collapseGroup(i);
                }
            }
        } else {//或者直辖市直接进入
            UserInfo.tempProvinceId = mProvinceWithCityBeanList.get(groupPosition).Xzqh;
            UserInfo.tempProvinceName = mProvinceWithCityBeanList.get(groupPosition).Name;
            if (extraType == 2) {//判断从真题过来
                setResultActivity(UserInfo.tempProvinceName);
            } else {
                setResultProvinActivity(UserInfo.tempProvinceId, UserInfo.tempProvinceName);
            }
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        mCityBeanList = mProvinceWithCityBeanList.get(groupPosition).getCityList();
        setResultCityActivity(mProvinceWithCityBeanList.get(groupPosition).Xzqh,mProvinceWithCityBeanList.get(groupPosition).Name,mCityBeanList.get(childPosition).Xzqh,mCityBeanList.get(childPosition).Name);
        return true;
    }

    private class CityListAdapter extends BaseExpandableListAdapter implements SectionIndexable {
        private String letter;

        @Override
        public int getGroupCount() {
            if (mProvinceWithCityBeanList == null) {
                return 0;
            }
            return mProvinceWithCityBeanList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (mLists == null) {
                return 0;
            }
            return mLists.get(groupPosition).size();
        }

        @Override
        public ProvinceWithCityBean getGroup(int groupPosition) {
            return mProvinceWithCityBeanList.get(groupPosition);
        }

        @Override
        public ProvinceBean getChild(int groupPosition, int childPosition) {
            return mLists.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolderForCitylist viewHolderForCitylist;
            if (convertView == null) {
                convertView = View.inflate(ProvinceChooseActivity.this, R.layout.item_citylist_chooseactivity, null);
                viewHolderForCitylist = new ViewHolderForCitylist();
                viewHolderForCitylist.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolderForCitylist.tv_header = (TextView) convertView.findViewById(R.id.tv_header);
                viewHolderForCitylist.view_line1 = convertView.findViewById(R.id.v_line1);
                viewHolderForCitylist.view_line2 = convertView.findViewById(R.id.v_line2);
                viewHolderForCitylist.view_line3 = convertView.findViewById(R.id.v_line3);
                viewHolderForCitylist.down_ang = (ImageView) convertView.findViewById(R.id.down_ang);
                convertView.setTag(viewHolderForCitylist);
            }
            viewHolderForCitylist = (ViewHolderForCitylist) convertView.getTag();
            viewHolderForCitylist.tv_name.setText(mProvinceWithCityBeanList.get(groupPosition).Name);
            cityList = mProvinceWithCityBeanList.get(groupPosition).getCityList();
            if (null != cityList && cityList.size() > 0) {//如果城市中有省市则显示图标
                viewHolderForCitylist.down_ang.setVisibility(View.VISIBLE);
                if (isExpanded) {
                    viewHolderForCitylist.down_ang.setImageResource(R.drawable.down_ang);
                } else {
                    viewHolderForCitylist.down_ang.setImageResource(R.drawable.right_ang);
                }
            } else {
                viewHolderForCitylist.down_ang.setVisibility(View.GONE);
            }
            if (groupPosition == 0 || !(mProvinceWithCityBeanList.get(groupPosition - 1).Header.substring(0, 1)).equals(mProvinceWithCityBeanList.get(groupPosition).Header.substring(0, 1))) {
                viewHolderForCitylist.tv_header.setVisibility(View.VISIBLE);
//                viewHolderForCitylist.view_line1.setVisibility(View.VISIBLE);
                viewHolderForCitylist.tv_header.setText(mProvinceWithCityBeanList.get(groupPosition).Header.substring(0, 1));
            } else {
                viewHolderForCitylist.tv_header.setVisibility(View.GONE);
                viewHolderForCitylist.view_line1.setVisibility(View.GONE);
            }
//            if (groupPosition + 1 >= getGroupCount() || !(mProvinceWithCityBeanList.get(groupPosition + 1).Header.substring(0, 1)).equals(mProvinceWithCityBeanList.get(groupPosition).Header.substring(0, 1))) {
//                viewHolderForCitylist.view_line2.setVisibility(View.GONE);
//                viewHolderForCitylist.view_line3.setVisibility(View.VISIBLE);
//            } else {
//                viewHolderForCitylist.view_line2.setVisibility(View.VISIBLE);
//                viewHolderForCitylist.view_line3.setVisibility(View.GONE);
//            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ViewHolderForCitylist viewHolderForCitylist;
            if (convertView == null) {
                viewHolderForCitylist = new ViewHolderForCitylist();
                convertView = View.inflate(ProvinceChooseActivity.this, R.layout.item_citylist_chooseactivity, null);
                convertView.findViewById(R.id.tv_header).setVisibility(View.GONE);
                viewHolderForCitylist.mlist_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolderForCitylist.view_line1 = convertView.findViewById(R.id.v_line1);
                viewHolderForCitylist.view_line2 = convertView.findViewById(R.id.v_line2);
                viewHolderForCitylist.view_line3 = convertView.findViewById(R.id.v_line3);
                convertView.setTag(viewHolderForCitylist);
            }
            viewHolderForCitylist = (ViewHolderForCitylist) convertView.getTag();
            viewHolderForCitylist.mlist_name.setText("    "+getChild(groupPosition, childPosition).Name);
            viewHolderForCitylist.view_line1.setVisibility(View.GONE);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public List<String> getSections() {
            List<String> letterList = new ArrayList<>();
            if (positionOfSection == null) {
                positionOfSection = new SparseIntArray();
            }
            if (sectionOfPosition == null) {
                sectionOfPosition = new SparseIntArray();
            }
            positionOfSection = new SparseIntArray();
            int count = getGroupCount();
            for (int i = 0; i < count; i++) {
                letter = getGroup(i).Header.substring(0, 1).toUpperCase();
                int section = letterList.size() - 1;
                if (letterList.size() == 0 || (letterList.get(section) != null && !letterList.get(section).equals(letter))) {
                    letterList.add(letter);
                    section++;
                    positionOfSection.put(section, i);
                }
                sectionOfPosition.put(i, section);
            }
            return letterList;
        }

        @Override
        public int getPositionForSection(int section) {
            if (positionOfSection == null) {
                return -1;
            }
            return positionOfSection.get(section);
        }
    }

    public class ViewHolderForCitylist {
        TextView tv_name;
        TextView tv_header;
        View view_line1;
        View view_line2;
        View view_line3;
        ImageView down_ang;
        TextView mlist_name;
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, ProvinceChooseActivity.class);
        context.startActivityForResult(intent, 0);
    }

    public static void newIntent(Activity context, int type) {
        Intent intent = new Intent(context, ProvinceChooseActivity.class);
        intent.putExtra(UserInfo.KEY_SP_SELECT_AREA_TYPE, type);
        context.startActivityForResult(intent, 2);
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
