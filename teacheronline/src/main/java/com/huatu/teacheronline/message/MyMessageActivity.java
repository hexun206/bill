package com.huatu.teacheronline.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.H5DetailActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.DirectDetailsActivity;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.adapter.MessageAdapter;
import com.huatu.teacheronline.personal.bean.MessageBean;
import com.huatu.teacheronline.sensorsdata.TrackUtil;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.swipemenulistview.SwipeMenu;
import com.huatu.teacheronline.widget.swipemenulistview.SwipeMenuCreator;
import com.huatu.teacheronline.widget.swipemenulistview.SwipeMenuItem;
import com.huatu.teacheronline.widget.swipemenulistview.SwipeMenuListView;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 我的消息
 *
 * @author ljyu
 * @time 2016-8-11 09:11:47
 */
public class MyMessageActivity extends BaseActivity {

    private String uid;
    private TextView tv_main_title;
    private TextView tv_main_right;
    private CustomAlertDialog mCustomDelDirectDilog;

    private View loadView;
    private View loadIcon;
    private RotateAnimation refreshingAnimation;
    private MessageAdapter adapter;
    private SwipeRefreshLayout mPullToRefreshLayout;
    private SwipeMenuListView listView;
    private ObtatinDataListener obtatinDataListener;
    private String fileLocalPath;
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private int currentPage = 1;
    private int limit = 6;
    private ArrayList<MessageBean> messageBeanList = new ArrayList<MessageBean>();
    private CustomAlertDialog mCustomLoadingDialog;
    private ObtainDataListerForDelMessage obtainDataListerDelMessage;
    private ObtainDataListerForUpdateMessage obtainDataListerForUpdateMessage;
    private PercentRelativeLayout rl_nodata;
    private TextView tv_notice;
    private TextView tv_directCurr;
    private TextView tv_message;
    private int type = 3;//1 公告 2 课程 3 资讯
    private RelativeLayout rl_notice;
    private RelativeLayout rl_directCurr;
    private RelativeLayout rl_message;
    private ObtainDataListerSortsDelMessage obtainDataListerSortsDelMessage;
    private RelativeLayout rl_wifi;
    private View v_notice;
    private View v_directCurr;
    private View v_message;
    private ImageView iv_no_notice;
    private ImageView iv_no_directCurr;
    private ImageView iv_no_message;


    @Override
    public void initView() {
        setContentView(R.layout.activity_my_message);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.my_message);
        tv_main_right = (TextView) findViewById(R.id.tv_main_right);
        rl_nodata = (PercentRelativeLayout) findViewById(R.id.rl_nodata);
        tv_main_right.setText(R.string.clear);
        tv_main_right.setTextColor(getResources().getColor(R.color.black));
        tv_main_right.setVisibility(View.GONE);
        rl_notice = (RelativeLayout) findViewById(R.id.rl_notice);
        rl_directCurr = (RelativeLayout) findViewById(R.id.rl_directCurr);
        rl_message = (RelativeLayout) findViewById(R.id.rl_message);
        tv_notice = (TextView) findViewById(R.id.tv_notice);
        tv_directCurr = (TextView) findViewById(R.id.tv_directCurr);
        tv_message = (TextView) findViewById(R.id.tv_message);
        iv_no_message = (ImageView) findViewById(R.id.iv_no_message);
        iv_no_directCurr = (ImageView) findViewById(R.id.iv_no_directCurr);
        rl_wifi = (RelativeLayout) findViewById(R.id.rl_wifi);

        v_notice = findViewById(R.id.v_notice);
        v_directCurr = findViewById(R.id.v_directCurr);
        v_message = findViewById(R.id.v_message);

        iv_no_notice = (ImageView) findViewById(R.id.iv_no_notice);


        mCustomDelDirectDilog = new CustomAlertDialog(MyMessageActivity.this, R.layout.dialog_join_mydirect);
        mCustomLoadingDialog = new CustomAlertDialog(MyMessageActivity.this, R.layout.dialog_loading_custom);
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        adapter = new MessageAdapter(this);
        mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.live_refresh_layout);
        mPullToRefreshLayout.setRefreshing(false);
        listView = (SwipeMenuListView) findViewById(R.id.listview);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        listView.setAdapter(adapter);
        listView.setParentRefreshlayout(mPullToRefreshLayout);
//        loadMessageData(true);


        //神策track click
        TrackUtil.trackClick(this, rl_notice, TrackUtil.TYPE_CHANGE_TAB, "资讯");
        TrackUtil.trackClick(this, rl_directCurr, TrackUtil.TYPE_CHANGE_TAB, "公告");
        TrackUtil.trackClick(this, rl_message, TrackUtil.TYPE_CHANGE_TAB, "课程");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessageData(true);
    }

    @Override
    public void setListener() {
        tv_main_right.setOnClickListener(this);
        rl_notice.setOnClickListener(this);
        tv_notice.setOnClickListener(this);
        rl_directCurr.setOnClickListener(this);
        rl_message.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.getId() == R.id.ll_loading) {//如果点击底部FooterView加载中布局，不做出来
                    return;
                }
                MessageBean messageBean = messageBeanList.get(position);
                if(Integer.parseInt(messageBean.getStype()) == 0 ||Integer.parseInt(messageBean.getStype()) == 3){
                    H5DetailActivity.newIntent(MyMessageActivity.this, messageBean.getTitle(), messageBean.getArticle_url());
                }else if(Integer.parseInt(messageBean.getStype()) == 1 || Integer.parseInt(messageBean.getStype()) == 2){
                    DirectDetailsActivity.newIntent(MyMessageActivity.this, messageBean.getRid());
                }
                updateMessageForMy(messageBean.getId());
                messageBean.setStatus("1");
                adapter.notifyDataSetChanged();

            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (isLoadEnd) {
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            listView.addFooterView(loadView);
                            loadIcon.startAnimation(refreshingAnimation);
                            loadMessageData(false);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMessageData(true);
            }
        });
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem deleteItem = new SwipeMenuItem(MyMessageActivity.this);
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(CommonUtils.dip2px(80));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(15);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        mCustomDelDirectDilog.show();
                        mCustomDelDirectDilog.setTitle("提示<br/>确定删除该消息？");
                        mCustomDelDirectDilog.setTag(messageBeanList.get(position));
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mCustomDelDirectDilog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageBean tag = (MessageBean) mCustomDelDirectDilog.getTag();
                if (tag == null || tag.getId().equals("ALL")) {
                    delMessageSorts(uid, String.valueOf(type));
                } else {
                    delMessageForMy(tag.getId());
                }
                mCustomDelDirectDilog.dismiss();

            }
        });
        mCustomDelDirectDilog.setCancelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomDelDirectDilog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                finish();
                break;
            case R.id.tv_main_right:
//                if(messageBeanList == null||messageBeanList.size()==0){
//                    return;
//                }
                mCustomDelDirectDilog.setTitle("提示<br/>消息确定清空？");
                mCustomDelDirectDilog.show();
                MessageBean messageBean = new MessageBean();
                messageBean.setId("ALL");
                mCustomDelDirectDilog.setTag(messageBean);
                break;
            case R.id.tv_notice:
                if (type!=3){
                    tv_notice.setTextColor(getResources().getColor(R.color.green001));
                    tv_message.setTextColor(getResources().getColor(R.color.black));
                    tv_directCurr.setTextColor(getResources().getColor(R.color.black));
                    listView.addFooterView(loadView);
                    loadIcon.startAnimation(refreshingAnimation);
                    adapter.notifyDataSetChanged();
                    currentPage = 1;
                    type = 3;
                    loadMessageData(true);

                    v_notice.setVisibility(View.VISIBLE);
                    v_directCurr.setVisibility(View.GONE);
                    v_message.setVisibility(View.GONE);
                }
                break;
            case R.id.rl_message:
                if (type!=2){
                    tv_message.setTextColor(getResources().getColor(R.color.green001));
                    tv_notice.setTextColor(getResources().getColor(R.color.black));
                    tv_directCurr.setTextColor(getResources().getColor(R.color.black));
                    listView.addFooterView(loadView);
                    loadIcon.startAnimation(refreshingAnimation);
                    adapter.notifyDataSetChanged();
                    currentPage = 1;
                    type = 2;
                    loadMessageData(true);
                    v_notice.setVisibility(View.GONE);
                    v_directCurr.setVisibility(View.GONE);
                    v_message.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rl_directCurr:
                if (type!=1){
                    tv_notice.setTextColor(getResources().getColor(R.color.black));
                    tv_message.setTextColor(getResources().getColor(R.color.black));
                    tv_directCurr.setTextColor(getResources().getColor(R.color.green001));
                    listView.addFooterView(loadView);
                    loadIcon.startAnimation(refreshingAnimation);
                    adapter.notifyDataSetChanged();
                    currentPage = 1;
                    type = 1;
                    loadMessageData(true);
                    v_notice.setVisibility(View.GONE);
                    v_directCurr.setVisibility(View.VISIBLE);
                    v_message.setVisibility(View.GONE);
                }
                break;
        }
    }

    /**
     * 获取我的在线视频列表
     *
     * @param
     */
    public void loadMessageData(final boolean isReset) {
        if (isReset) {
            currentPage = 1;
        } else {
            currentPage++;
        }
        obtatinDataListener = new ObtatinDataListener(isReset, this);
      SendRequest.getMessageList(uid, String.valueOf(currentPage), String.valueOf(limit),type, obtatinDataListener);
    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<ArrayList<MessageBean>, String> {
        private MyMessageActivity weak_activity;
        private boolean isReset;

        public ObtatinDataListener(Boolean isReset, MyMessageActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.isLoadEnd = false;
                if (isReset) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weak_activity.clearAdapete();
                        }
                    });
                }
            }
        }

        @Override
        public void onSuccess(final ArrayList<MessageBean> res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.flushContent_OnSucess(isReset, res);
                        if(weak_activity.messageBeanList == null||weak_activity.messageBeanList.size()==0){//无数据的时候隐藏清空按钮
                            weak_activity.tv_main_right.setVisibility(View.GONE);
                        }else{
                            weak_activity.tv_main_right.setVisibility(View.VISIBLE);
                        }
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
                        weak_activity.flushContent_OnFailure(res);
                    }
                });
            }
        }

    }

    public void flushContent_OnSucess(boolean isReset, ArrayList<MessageBean> res) {
        rl_wifi.setVisibility(View.GONE);
        MessageMethod();
        if (isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
//            ToastUtils.showToast(R.string.no_data);
            rl_nodata.setVisibility(View.VISIBLE);
        } else if (!isReset && (res == null || res.size() == 0)) {
            hasMoreData = false;
            ToastUtils.showToast(R.string.no_more);
        } else {
            rl_nodata.setVisibility(View.GONE);
            hasMoreData = true;
            messageBeanList.addAll(res);
        }
        completeRefresh();
    }

    public void flushContent_OnFailure(String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
            rl_wifi.setVisibility(View.VISIBLE);
        } else if (SendRequest.ERROR_SERVER.equals(res)) {
            ToastUtils.showToast(R.string.server_error);
        }
        completeRefresh();
    }

    /**
     * 上拉刷新
     */
    private void completeRefresh() {
        mPullToRefreshLayout.setRefreshing(false);
        if (listView != null && listView.getFooterViewsCount() > 0) {
            listView.removeFooterView(loadView);
        }

        if (adapter != null) {
            adapter.setMessageBeanList(messageBeanList);
            adapter.notifyDataSetChanged();
        }

        if (hasMoreData) {
            isLoadEnd = true;
        } else {
            isLoadEnd = false;
        }
    }

    /**
     * 清空适配器
     */
    public void clearAdapete() {
        messageBeanList.clear();
        adapter.setMessageBeanList(messageBeanList);
        adapter.notifyDataSetChanged();
    }

    public static void newIntent(Context context) {
        Intent intent = new Intent(context, MyMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 将消息删除
     */
    public void delMessageForMy(String messageId) {
        obtainDataListerDelMessage = new ObtainDataListerForDelMessage(this);
        SendRequest.delMessageForMy(messageId, uid, obtainDataListerDelMessage);
    }
    /**
     * 分类消息删除
     */
    public void delMessageSorts(String uid,String type) {
        obtainDataListerSortsDelMessage = new ObtainDataListerSortsDelMessage(this);
        SendRequest.EmptyMessage(uid, type, obtainDataListerSortsDelMessage);
    }
    private class ObtainDataListerSortsDelMessage extends ObtainDataFromNetListener<String, String>{
        private MyMessageActivity weak_activity;

        public ObtainDataListerSortsDelMessage(MyMessageActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }
        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      if (res.equals("1")){
                          ToastUtils.showToast("删除成功！");
                          loadMessageData(true);
                      }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            weak_activity.mCustomLoadingDialog.dismiss();
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
    /**
     * 更改消息状态
     */
    public void updateMessageForMy(String messageId) {
        obtainDataListerForUpdateMessage = new ObtainDataListerForUpdateMessage(this,messageId);
        SendRequest.updateMessageForMy(messageId, uid, obtainDataListerForUpdateMessage);
    }

    private class ObtainDataListerForDelMessage extends ObtainDataFromNetListener<String, String> {
        private MyMessageActivity weak_activity;

        public ObtainDataListerForDelMessage(MyMessageActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.show();
            }
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("1".equals(res) || "2".equals(res)) {//1 删除成功 2 重复删除
                            ToastUtils.showToast(R.string.del_success);
                            MessageBean tag = (MessageBean) weak_activity.mCustomDelDirectDilog.getTag();
                            if (tag == null || tag.getId().equals("ALL")) {
                                weak_activity.clearAdapete();
                            } else {
                                weak_activity.messageBeanList.remove(weak_activity.mCustomDelDirectDilog.getTag());
                            }
                            if(weak_activity.messageBeanList == null||weak_activity.messageBeanList.size()==0){//无数据的时候隐藏清空按钮
                                weak_activity.tv_main_right.setVisibility(View.GONE);
                                rl_nodata.setVisibility(View.VISIBLE);
                            }else{
                                weak_activity.tv_main_right.setVisibility(View.VISIBLE);
                                rl_nodata.setVisibility(View.GONE);
                            }
                            loadMessageData(true);
                            weak_activity.adapter.notifyDataSetChanged();
                        } else {//删除失败
                            ToastUtils.showToast(R.string.server_error);
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            weak_activity.mCustomLoadingDialog.dismiss();
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

    private class ObtainDataListerForUpdateMessage extends ObtainDataFromNetListener<String, String> {
        private String messageId;
        private MyMessageActivity weak_activity;

        public ObtainDataListerForUpdateMessage(MyMessageActivity activity,String messageId) {
            weak_activity = new WeakReference<>(activity).get();
            this.messageId = messageId;
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("1".equals(res) || "2".equals(res)) {//1 更新成功 2 重复更新
                            for (int i = 0; i < weak_activity.messageBeanList.size(); i++) {
                                if (messageId.equals(weak_activity.messageBeanList.get(i).getId())) {
                                    weak_activity.messageBeanList.get(i).setStatus("1");
                                    weak_activity.adapter.notifyDataSetChanged();
                                }
                            }
                        } else {//更新失败
                            ToastUtils.showToast(R.string.server_error);
                        }
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
    private void MessageMethod(){//获得未读的条数
        String type_one=CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_TYPE_ONE, "");
        String type_two=CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_TYPE_TWO, "");
        String type_three=CommonUtils.getSharedPreferenceItem(null,UserInfo.KEY_SP_TYPE_THREE,"");
       if (Integer.valueOf(type_one)>0){
            iv_no_notice.setVisibility(View.VISIBLE);
        } else{
           iv_no_notice.setVisibility(View.GONE);
        }


        if (Integer.valueOf(type_two)>0){
            iv_no_directCurr.setVisibility(View.VISIBLE);
        } else{
            iv_no_directCurr.setVisibility(View.GONE);
        }

        if (Integer.valueOf(type_three)>0){
            iv_no_message.setVisibility(View.VISIBLE);
        } else{
            iv_no_message.setVisibility(View.GONE);
        }

    }

}
