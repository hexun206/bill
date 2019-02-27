package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.bean.BillBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 账单
 * Created by ply on 2016/2/16.
 */
public class BillActivity extends BaseActivity {
    private RelativeLayout rl_main_left;
    private TextView tv_main_title;

    //下拉刷新
    private List<BillBean> billBeanList = new ArrayList<>();
    private ListView listView;
    private BillAdapter adapter;
    private View loadView;
    private View loadIcon;
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;

    private ObtainDataLister obtatinDataListener;
    private String uid;
    private int currentPage = 1;
    private boolean isLoadEnd;
    private boolean hasMoreData = true;

    @Override
    public void initView() {
        setContentView(R.layout.activity_bill);

        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.bill);
        findViewById(R.id.rl_main_right).setVisibility(View.GONE);
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        listView = (ListView) findViewById(R.id.listview);
        adapter = new BillAdapter();
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        listView.setAdapter(adapter);
        loadBillData(true);
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (isLoadEnd) {
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            listView.addFooterView(loadView);
                            loadIcon.startAnimation(refreshingAnimation);
                            loadBillData(false);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                back();
                break;
        }
    }

    public static void newIntent(Activity context) {
        Intent billIntent = new Intent(context, BillActivity.class);
        context.startActivity(billIntent);
    }

    /**
     * 获取账单
     *
     * @param
     */
    public void loadBillData(final boolean isReset) {
        if (isReset) {
            currentPage = 1;
        } else {
            currentPage++;
        }
        obtatinDataListener = new ObtainDataLister(isReset, this);
        SendRequest.getBillList(uid, currentPage, "8", obtatinDataListener);
    }

    private static class ObtainDataLister extends ObtainDataFromNetListener<List<BillBean>, String> {

        private BillActivity weak_activity;
        private boolean isReset;

        public ObtainDataLister(Boolean isReset, BillActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.isLoadEnd = false;
            }
        }

        @Override
        public void onSuccess(final List<BillBean> res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushContent_OnSucess(isReset, res);
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }
    }

    public void flushContent_OnSucess(boolean isReset, List<BillBean> res) {
        if (isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_data);
        } else if (!isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_more);
        } else {
            hasMoreData = true;
            billBeanList.addAll(res);
        }
        completeRefresh();
    }

    /**
     * 上拉刷新
     */
    private void completeRefresh() {
        if (listView != null && listView.getFooterViewsCount() > 0) {
            listView.removeFooterView(loadView);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        if (hasMoreData) {
            isLoadEnd = true;
        } else {
            isLoadEnd = false;
        }
    }

    /**
     * listview适配器
     */
    public class BillAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return (billBeanList == null ? 0 : billBeanList.size());
        }

        @Override
        public Object getItem(int position) {
            return billBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            TextView tv_title;
            TextView tv_goldNumber;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_bill_item_layout, null);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tv_goldNumber = (TextView) convertView.findViewById(R.id.tv_goldNumber);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BillBean billBean = billBeanList.get(position);
            viewHolder.tv_title.setText(billBean.getNote());
            if ("1".equals(billBean.getType())) {//充值
                viewHolder.tv_goldNumber.setText("+" + billBean.getGold());
            } else if ("2".equals(billBean.getType())) {//消费
                viewHolder.tv_goldNumber.setText("-" + billBean.getGold());
            } else if ("3".equals(billBean.getType())) {
                //任务
                viewHolder.tv_goldNumber.setText("+" + billBean.getGold());
            } else if ("4".equals(billBean.getType())) {
                viewHolder.tv_goldNumber.setText("+" + billBean.getGold());

            }

            return convertView;
        }
    }
}
