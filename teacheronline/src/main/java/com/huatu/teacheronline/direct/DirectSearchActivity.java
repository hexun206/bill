package com.huatu.teacheronline.direct;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greendao.DaoUtils;
import com.greendao.DirectBean;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.adapter.DirectListAdapter;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.TagsLayout;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程搜索
 * @author ljyu
 * @time 2016-8-8 08:42:46
 */
public class DirectSearchActivity extends BaseActivity {
    private ListView lv_footprint;
    private TextView main_right;
    private List<DirectBean> directBeanList = new ArrayList<DirectBean>();
    private String userid;
    private boolean isLoadEnd;
    private RelativeLayout rl_wifi;
    private int currentPage; //页数
    private int pageSize = 10; //页数
    private int currentAll = 0;//当前个数
    private View loadView;
    private View loadIcon;
    private RotateAnimation refreshingAnimation;
    private boolean hasMoreData = true;
    private RelativeLayout rl_nodata;//没数据
    private String keyWord = "";//关键字
    //    private SwipeRefreshLayout mPullToRefreshLayout;
    private DirectListAdapter adapter;
    private TextView edit_main_title; //搜索框
    private String[] datas;//联想内容
    private String[] recommendedDatas;//搜索推荐内容
    private LenoveAdaper mLenoveAdaper;//联想适配器
    private ListView lv_lenovo;//联想列表
    private ObtatinLenoveDataListener obtatinLenoveDataListener;//联想
    private RelativeLayout rl_keyword_lenovo; //联想布局
    private int keyWordlength = 0;
    private boolean isLoading = false; //是否正在搜索
    private TagsLayout tl_history;//搜索历史 流布局
    private TagsLayout tl_recommended;//推荐搜索 流布局
    private LinearLayout ll_searchhis;//历史推荐布局
    private LinearLayout ll_history; //历史视频
    private LinearLayout ll_recommended;//推荐搜索
    private String[] hisKeyWords;//历史记录 数组
    private String getHisKeyWord;// 历史记录 未分割
    private ObtatinRecommendedDataListener obtatinRecommendedDataListener;//搜索推荐
    private CustomAlertDialog mCustomDelDirectDilog; //清空历史记录
    private DaoUtils daoUtils;
    private int mVideoType;

    @Override
    public void initView() {
        setContentView(R.layout.activity_direct_search);

        mVideoType = getIntent().getIntExtra("videoType", 0);

        userid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        rl_wifi = (RelativeLayout) findViewById(R.id.rl_wifi);
        rl_nodata = (RelativeLayout) findViewById(R.id.rl_nodata);
        ll_searchhis = (LinearLayout) findViewById(R.id.ll_searchhis);
        ll_history = (LinearLayout) findViewById(R.id.ll_history);
        ll_recommended = (LinearLayout) findViewById(R.id.ll_recommended);
        rl_keyword_lenovo = (RelativeLayout) findViewById(R.id.rl_keyword_lenovo);
        lv_footprint = (ListView) findViewById(R.id.lv_footprint);
        lv_lenovo = (ListView) findViewById(R.id.lv_lenovo);
        tl_history = (TagsLayout) findViewById(R.id.tl_history);
        tl_recommended = (TagsLayout) findViewById(R.id.tl_recommended);
        main_right = (TextView) findViewById(R.id.tv_main_right);
        edit_main_title = (TextView) findViewById(R.id.edit_main_title);
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        findViewById(R.id.img_delete_hiskey).setOnClickListener(this);
        main_right.setVisibility(View.VISIBLE);
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.pull_to_refresh_and_load_rotating);
        adapter = new DirectListAdapter(this, 0,mVideoType);
//        mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.live_refresh_layout);
//        mPullToRefreshLayout.setRefreshing(false);
        lv_footprint.setAdapter(adapter);
        mLenoveAdaper = new LenoveAdaper(this, datas);
        lv_lenovo.setAdapter(mLenoveAdaper);
        ll_searchhis.setVisibility(View.VISIBLE);
        mCustomDelDirectDilog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
        daoUtils = DaoUtils.getInstance();
        iniHisData();
        iniRecommended();
    }

    /**
     * 加载搜索推荐
     */
    private void iniRecommended() {
        obtatinRecommendedDataListener = new ObtatinRecommendedDataListener(DirectSearchActivity.this);
        SendRequest.getSearchRecommendData(userid, obtatinRecommendedDataListener);
    }

    /**
     * 初始化 本地搜索历史数据
     */
    private void iniHisData() {
        hisKeyWords = getHisKeyWord();
        if (StringUtils.isEmpty(getHisKeyWord)) {
            ll_history.setVisibility(View.GONE);
            return;
        }
        ll_history.setVisibility(View.VISIBLE);
        tl_history.removeAllViews();
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < hisKeyWords.length; i++) {
            final TextView textView = new TextView(this);
            textView.setText(hisKeyWords[i]);
            textView.setTextColor(Color.parseColor("#9B9B9B"));
            textView.setTextSize(12);
            textView.setPadding(15, 10, 15, 10);
            textView.setBackgroundResource(R.drawable.bg_rectangle_frame_gray_radius);
            tl_history.addView(textView, lp);
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isLoading = true;
                    edit_main_title.setText(hisKeyWords[finalI]);
                    searchOnclik();
                }
            });
            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mCustomDelDirectDilog.show();
                    mCustomDelDirectDilog.setTitle("提示<br/>确认删除该历史记录？");
                    mCustomDelDirectDilog.setOkOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCustomDelDirectDilog.dismiss();
                            tl_history.removeView(textView);
                            String replace = getHisKeyWord.replace(hisKeyWords[finalI] + "#", "");
                            getHisKeyWord = replace;
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_HISKEYWORD, replace);
                            DebugUtil.e("setOnLongClickListener:" + replace);
                            if (tl_history.getChildCount() == 0) {
                                ll_history.setVisibility(View.GONE);
                            }
                        }
                    });
                    mCustomDelDirectDilog.setCancelOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCustomDelDirectDilog.dismiss();
                        }
                    });
                    return true;
                }
            });
        }
    }

    /**
     * 获取本地搜索记录
     *
     * @return
     */
    private String[] getHisKeyWord() {
        getHisKeyWord = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_HISKEYWORD, "");
        DebugUtil.e("getHisKeyWord:" + getHisKeyWord);
        return getHisKeyWord.split("#");
    }


    /**
     * 保存搜索历史
     *
     * @param keyWords
     */
    private void saveKeyWord(String keyWords) {
        String[] split = getHisKeyWord.split("#");
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if (keyWords.equals(split[i])) {
                return;
            }
        }
        String saveReal = "";
        if (split.length < 10) {
            getHisKeyWord = keyWords + "#" + getHisKeyWord;
        } else {
            for (int i = 8; i >= 0; i--) {
                saveReal = split[i] + "#" + saveReal;
            }
            getHisKeyWord = keyWords + "#" + saveReal;
        }

        CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_HISKEYWORD, getHisKeyWord);
        DebugUtil.e("saveKeyWord:" + getHisKeyWord);
    }


    /**
     * 执行搜索请求
     * @param isReset
     */
    private void iniData(boolean isReset) {
        keyWord = edit_main_title.getText().toString();
        saveKeyWord(keyWord);
        ObtatinDataListener obtatinDataListener = new ObtatinDataListener(isReset, DirectSearchActivity.this);
        SendRequest.getLiveSearchData(userid, currentPage + "", pageSize + "", keyWord, obtatinDataListener);
    }

    /**
     * 搜索课程数据
     */
    private static class ObtatinDataListener extends ObtainDataFromNetListener<List<DirectBean>, String> {
        private DirectSearchActivity activity;
        private boolean isReset;

        public ObtatinDataListener(Boolean isReset, DirectSearchActivity fragment) {
            activity = new WeakReference<>(fragment).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (activity != null) {
                activity.isLoadEnd = false;
                activity.lv_footprint.setVisibility(View.VISIBLE);
                activity.rl_wifi.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSuccess(final List<DirectBean> res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.isLoading = false;
                        activity.flushContent_OnSucess(isReset, res);
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.isLoading = false;
                        activity.flushContent_OnFailure(isReset, res);
                    }
                });
            }
        }
    }

    public void flushContent_OnSucess(boolean isReset, List<DirectBean> res) {
        if (isReset && (res == null || res.size() == 0)) {
            ToastUtils.showToast(R.string.no_data);
            rl_nodata.setVisibility(View.VISIBLE);
        } else {
            rl_nodata.setVisibility(View.GONE);
            rl_keyword_lenovo.setVisibility(View.GONE);
            ll_searchhis.setVisibility(View.GONE);
        }

        if (res == null || res.size() == 0) {
            hasMoreData = false;
        } else {
            hasMoreData = true;
            currentPage++;
            directBeanList.addAll(res);
        }
        completeRefresh();
    }

    public void flushContent_OnFailure(boolean isReset, String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
            if (isReset) {
                lv_footprint.setVisibility(View.GONE);
                rl_wifi.setVisibility(View.VISIBLE);
            }
        } else if (SendRequest.ERROR_SERVER.equals(res)) {
            ToastUtils.showToast(R.string.server_error);
        } else {
            ToastUtils.showToast(res);
        }
        completeRefresh();
    }

    /**
     * 上拉刷新
     */
    private void completeRefresh() {
//        mPullToRefreshLayout.setRefreshing(false);
        if (lv_footprint != null && lv_footprint.getFooterViewsCount() > 0) {
            lv_footprint.removeFooterView(loadView);
        }

        if (adapter != null) {
            adapter.setDirectBeanList(directBeanList);
            adapter.notifyDataSetChanged();
        }

        if (hasMoreData) {
            isLoadEnd = true;
        } else {
            isLoadEnd = false;
        }
    }

    @Override
    public void setListener() {
        main_right.setOnClickListener(this);//搜索
        lv_footprint.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (isLoadEnd) {
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            lv_footprint.addFooterView(loadView);
                            loadIcon.startAnimation(refreshingAnimation);
                            iniData(false);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        lv_footprint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.getId() == R.id.ll_loading) {//如果点击底部FooterView加载中布局，不做出来
                    return;
                }
                MobclickAgent.onEvent(DirectSearchActivity.this, "courseOnItemClick");
                DirectDetailsActivity.newIntent(DirectSearchActivity.this, directBeanList.get(position).getRid());
                //足迹相关
                DirectBean directBean = adapter.getdirectBeanList().get(position);
                directBean.setUserid(userid);
                DebugUtil.e("insertOrUpdateDirectBeanHistory:" + directBean.toString());
                daoUtils.insertOrUpdateDirectBeanHistory(directBean);
            }
        });

        edit_main_title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    //执行搜索功能功能
                    isLoading = true;
                    searchOnclik();
                }
                return false;

            }
        });

        edit_main_title.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                //text  输入框中改变后的字符串信息
                //start 输入框中改变后的字符串的起始位置
                //before 输入框中改变前的字符串的位置 默认为0
                //count 输入框中改变后的一共输入字符串的数量
            }

            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
                //text  输入框中改变前的字符串信息
                //start 输入框中改变前的字符串的起始位置
                //count 输入框中改变前后的字符串改变数量一般为0
                //after 输入框中改变后的字符串与起始位置的偏移量
            }

            @Override
            public void afterTextChanged(Editable edit) {
                //edit  输入结束呈现在输入框中的信息
                keyWordlength = edit.length();
                DebugUtil.e("afterTextChanged:" + edit);
                if (edit.length() > 0) {
                    obtatinLenoveDataListener = new ObtatinLenoveDataListener(DirectSearchActivity.this);
                    SendRequest.getSearchLenoveData(userid, edit.toString(), obtatinLenoveDataListener);
                } else {
                    DebugUtil.e("afterTextChanged2:" + edit);
                    rl_keyword_lenovo.setVisibility(View.GONE);
                    ll_searchhis.setVisibility(View.VISIBLE);
                    iniHisData();
                }
            }
        });

        lv_lenovo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.getId() == R.id.ll_loading) {//如果点击底部FooterView加载中布局，不做出来
                    return;
                }
                isLoading = true;
                edit_main_title.setText(datas[position]);
                searchOnclik();
            }
        });

        ll_searchhis.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_right://搜索
                isLoading = true;
                searchOnclik();
                break;
            case R.id.rl_main_left://back
                finish();
                break;
            case R.id.img_delete_hiskey://删除搜索历史
                mCustomDelDirectDilog.show();
                mCustomDelDirectDilog.setTitle("提示<br/>确认删除全部历史记录？");
                mCustomDelDirectDilog.setOkOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomDelDirectDilog.dismiss();
                        CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_HISKEYWORD, "");
                        getHisKeyWord = "";
                        ll_history.setVisibility(View.GONE);
                    }
                });
                mCustomDelDirectDilog.setCancelOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomDelDirectDilog.dismiss();
                    }
                });

                break;
        }
    }

    /**
     * 点击搜索按钮
     */
    private void searchOnclik() {
        CommonUtils.closeInputMethod(DirectSearchActivity.this);
        if (StringUtils.isEmpty(edit_main_title.getText().toString())) {
            if (recommendedDatas!=null&&recommendedDatas.length>0) {
                edit_main_title.setText(recommendedDatas[0]);
                isLoading = true;
                searchOnclik();
            }else {
                ToastUtils.showToast(getString(R.string.text_keyword_empty));
                isLoading = false;
                return;
            }
        }
        rl_keyword_lenovo.setVisibility(View.GONE);
        ll_searchhis.setVisibility(View.GONE);
        directBeanList.clear();
        adapter.setDirectBeanList(directBeanList);
        adapter.notifyDataSetChanged();
        currentPage = 1;
        lv_footprint.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        iniData(true);
    }

    /**
     * 跳转页面
     *
     * @param context
     * @param videoType
     */
    public static void newIntent(Activity context, int videoType) {
        Intent intent = new Intent(context, DirectSearchActivity.class);
        intent.putExtra("videoType",videoType);
        context.startActivity(intent);
    }

    /**
     * 联想适配器
     */
    class LenoveAdaper extends BaseAdapter {
        private Context context;
        private String[] datas;

        public LenoveAdaper(Context context, String[] datas) {
            this.context = context;
            this.datas = datas;
        }

        public void setmLenoveData(String[] datas) {
            this.datas = datas;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return datas == null ? 0 : datas.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_search_lenove, null);
                holder = new ViewHolder();
                holder.tv_lenove = (TextView) convertView.findViewById(R.id.tv_lenove);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //对应的实体类
            String data = datas[position];
            holder.tv_lenove.setText(data);
            return convertView;
        }

        public class ViewHolder {
            public TextView tv_lenove;
        }
    }

    /**
     * 联想数据
     */
    private class ObtatinLenoveDataListener extends ObtainDataFromNetListener<String[], String> {
        private DirectSearchActivity activity;

        public ObtatinLenoveDataListener(DirectSearchActivity fragment) {
            activity = new WeakReference<>(fragment).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (activity != null) {
                activity.isLoadEnd = false;
                activity.lv_footprint.setVisibility(View.VISIBLE);
                activity.rl_wifi.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSuccess(final String[] res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (keyWordlength == 0 || activity.isLoading) {
                            return;
                        }
                        activity.rl_keyword_lenovo.setVisibility(View.VISIBLE);
                        activity.ll_searchhis.setVisibility(View.GONE);
                        activity.datas = res;
                        activity.mLenoveAdaper.setmLenoveData(activity.datas);
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        }
    }

    /**
     * 推荐数据
     */
    private class ObtatinRecommendedDataListener extends ObtainDataFromNetListener<String[], String> {
        private DirectSearchActivity activity;

        public ObtatinRecommendedDataListener(DirectSearchActivity fragment) {
            activity = new WeakReference<>(fragment).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (activity != null) {
                activity.isLoadEnd = false;
                activity.lv_footprint.setVisibility(View.VISIBLE);
                activity.rl_wifi.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSuccess(final String[] res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.rl_keyword_lenovo.setVisibility(View.GONE);
                        activity.ll_searchhis.setVisibility(View.VISIBLE);
                        activity.recommendedDatas = res;
                        activity.setRecommendedDatas(recommendedDatas);

                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }
    }

    public void setRecommendedDatas(final String[] recommendedDatas) {
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < recommendedDatas.length; i++) {
            final TextView textView = new TextView(this);
            textView.setText(recommendedDatas[i]);
            textView.setTextColor(Color.parseColor("#9B9B9B"));
            textView.setTextSize(12);
            textView.setPadding(15, 10, 15, 10);
            textView.setBackgroundResource(R.drawable.bg_rectangle_frame_gray_radius);
            tl_recommended.addView(textView, lp);
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isLoading = true;
                    edit_main_title.setText(recommendedDatas[finalI]);
                    searchOnclik();
                }
            });
        }
        edit_main_title.setHint(recommendedDatas[0]);
    }
}
