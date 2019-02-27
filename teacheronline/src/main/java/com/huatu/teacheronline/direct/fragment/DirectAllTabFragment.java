package com.huatu.teacheronline.direct.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greendao.DaoUtils;
import com.greendao.DirectBean;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.DirectDetailsActivity;
import com.huatu.teacheronline.direct.DirectFootprintActivity;
import com.huatu.teacheronline.direct.DirectHomePageActivity;
import com.huatu.teacheronline.direct.PlayerActivityForBjysdk;
import com.huatu.teacheronline.direct.adapter.DirectListAdapter;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.umeng.analytics.MobclickAgent;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 直播 全部选项卡
 * Created by ply on 2016/1/5.
 */
public class DirectAllTabFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private View view;
    private Context context;
    private TextView tv_payDirect, tv_selectDirect, tv_zeroDirect;
    private RelativeLayout rl_wifi;

    //下拉刷新
    private List<DirectBean> directBeans = new ArrayList<>();
    private DirectListAdapter adapter;
    private ListView listView;
    private SwipeRefreshLayout mPullToRefreshLayout;
    private View loadView;
    private View loadIcon;
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;

    public int currentPage = 1;//当前页数 默认1
    private int limit = 10;
    public int isSelect = 1;//1 默认 2 筛选 3 0元
    private boolean isLoadEnd;
    private boolean hasMoreData = true;
    private ObtatinDataListener obtatinDataListener;


    int pad0 = CommonUtils.dip2px(3);
    int pad1 = CommonUtils.dip2px(5);
    int pad2 = CommonUtils.dip2px(6);
    private DaoUtils daoUtils;
    private String userid;
    private ImageView img_footprint;//我的足迹
    private String province = "";//省
    private PercentRelativeLayout rl_nodata;
    private String city = "";
    private DirectHomePageActivity directHomePageActivity;
    public static int videoType = 0;//0 高清网课 1 直播  2 教辅资料
    private String class_type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        directHomePageActivity = (DirectHomePageActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_directlist_layout, null);
        Bundle arguments = getArguments();
        videoType = arguments.getInt("videoType", 0);
        initView();
        setListener();
        province = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PROVINCE_ID, "86");
        city = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_ID, "");
        if (!StringUtils.isEmpty(city)) {
            province = city;
        }
        loadLiveData(true, currentPage);
        daoUtils = DaoUtils.getInstance();
        userid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        return view;
    }

    public void initView() {
        rl_wifi = (RelativeLayout) view.findViewById(R.id.rl_wifi);
        tv_payDirect = (TextView) view.findViewById(R.id.tv_payDirect);
        tv_selectDirect = (TextView) view.findViewById(R.id.tv_selectDirect);
        tv_zeroDirect = (TextView) view.findViewById(R.id.tv_zeroDirect);
        img_footprint = (ImageView) view.findViewById(R.id.img_footprint);

        rl_nodata = (PercentRelativeLayout) view.findViewById(R.id.rl_nodata);

        loadView = getActivity().getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                getActivity(), R.anim.pull_to_refresh_and_load_rotating);
        adapter = new DirectListAdapter(context, 0, videoType);
        mPullToRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.live_refresh_layout);
        listView = (ListView) view.findViewById(R.id.listview);
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        listView.setAdapter(adapter);
        view.findViewById(R.id.rl_selectDirect).setOnClickListener(this);
        view.findViewById(R.id.rl_payDirect).setOnClickListener(this);
        view.findViewById(R.id.rl_zeroDirect).setOnClickListener(this);
    }


    public void setListener() {
        img_footprint.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.getId() == R.id.ll_loading) {//如果点击底部FooterView加载中布局，不做出来
                    return;
                }
                DirectBean directBean = directBeans.get(position);
                if (directBeans.get(position).getRid().equals("-1")) {
                    directBean.setIsTrial(0);
                    directBean.setIs_buy("1");
                    if ("1".equals(directBean.getIs_zhibo())) {//直播
                        PlayerActivityForBjysdk.newIntent(getActivity(), directBean, 0, 2);
                    } else if ("-1".equals(directBean.getIs_zhibo())) {
                        ToastUtils.showToast(R.string.direct_offline);
                    } else {//录播
                        PlayerActivityForBjysdk.newIntent(getActivity(), directBean, 0, 2);
                    }
                } else {
                    MobclickAgent.onEvent(getActivity(), "courseOnItemClick");
                    DirectDetailsActivity.newIntent(getActivity(), adapter.getdirectBeanList().get(position).getRid());

                    //足迹相关
//                    DirectBean directBean = adapter.getdirectBeanList().get(position);
                    directBean.setUserid(userid);
                    DebugUtil.e("insertOrUpdateDirectBeanHistory:" + directBean.toString());
                    daoUtils.insertOrUpdateDirectBeanHistory(directBean);
                }
//                List<DirectBean> directBeans = daoUtils.queryDirectBeanForHistoryForUserid(userid);
//                DebugUtil.e("queryDirectBean:"+directBeans.size());
//                DebugUtil.e("queryDirectBean:"+directBeans.toString());

            }
        });
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                if (isSelect == 1||isSelect == 2) {//默认
                directBeans.clear();
                adapter.setDirectBeanList(directBeans);
                adapter.notifyDataSetChanged();
                currentPage = 1;
                loadLiveData(true, currentPage);
//                }
                /* else if (isSelect == 2) {
                    directBeans.clear();
                    adapter.setDirectBeanList(directBeans);
                    adapter.notifyDataSetChanged();
                    currentPage = 1;
                    loadLiveData(true, currentPage);
                }*/
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
                            loadLiveData(false, currentPage);
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
            case R.id.rl_payDirect:
                if (listView.getFooterViewsCount() > 0) {
                    return;
                }
                if (isSelect == 1) {//标示当前是收费模块
                    return;
                } else {
                    MobclickAgent.onEvent(getActivity(), "defaultSort");
                    DirectHomePageActivity.categorie = "";
                    DirectHomePageActivity.interview = "";
                    DirectHomePageActivity.period = "";
                    DirectHomePageActivity.subject = "";
                    DirectHomePageActivity.class_type = "";
                    DirectHomePageActivity.ActualPrice = "";
                    mPullToRefreshLayout.setVisibility(View.VISIBLE);
                    img_footprint.setVisibility(View.VISIBLE);
                    isSelect = 1;
                    tv_payDirect.setTextColor(getResources().getColor(R.color.green004));
                    tv_selectDirect.setTextColor(getResources().getColor(R.color.black));
                    tv_zeroDirect.setTextColor(getResources().getColor(R.color.black));
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_shaixuan);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tv_selectDirect.setCompoundDrawables(null, null, drawable, null);
                    directBeans.clear();
                    listView.addFooterView(loadView);
                    loadIcon.startAnimation(refreshingAnimation);
                    adapter.notifyDataSetChanged();
                    currentPage = 1;
                    loadLiveData(true, currentPage);
                    directHomePageActivity.gv_test_categories.clearCheck();
                    directHomePageActivity.ll__interview.setVisibility(View.GONE);
                    directHomePageActivity.ll_learning_period.setVisibility(View.GONE);
                    directHomePageActivity.ll_subjects.setVisibility(View.GONE);
                }
                break;
            case R.id.rl_selectDirect:
                if (listView.getFooterViewsCount() > 0) {
                    return;
                }
                MobclickAgent.onEvent(getActivity(), "screen");
                directHomePageActivity.drawer.openDrawer(Gravity.RIGHT);
                break;
            case R.id.rl_zeroDirect:
                if (listView.getFooterViewsCount() > 0) {
                    return;
                }
                mPullToRefreshLayout.setVisibility(View.VISIBLE);
                img_footprint.setVisibility(View.VISIBLE);
                if (isSelect == 2) {
                    directHomePageActivity.iniSenddata();
                }
                MobclickAgent.onEvent(getActivity(), "zeroCourse");
                isSelect = 3;
                DirectHomePageActivity.ActualPrice = "0";
                tv_payDirect.setTextColor(getResources().getColor(R.color.black));
                tv_selectDirect.setTextColor(getResources().getColor(R.color.black));
                Drawable drawable0 = getResources().getDrawable(R.drawable.ic_shaixuan);
                drawable0.setBounds(0, 0, drawable0.getMinimumWidth(), drawable0.getMinimumHeight());
                tv_selectDirect.setCompoundDrawables(null, null, drawable0, null);
                tv_zeroDirect.setTextColor(getResources().getColor(R.color.green004));
                directBeans.clear();
                listView.addFooterView(loadView);
                loadIcon.startAnimation(refreshingAnimation);
                adapter.notifyDataSetChanged();
                currentPage = 1;
                loadLiveData(true, currentPage);
                break;
            case R.id.img_footprint:
                //我的足迹
                DirectFootprintActivity.newIntent(getActivity());
                break;
        }

    }

    /**
     * 筛选提交
     */
    public void screeningCommit() {
        MobclickAgent.onEvent(getActivity(), "screeningSubmission");
        mPullToRefreshLayout.setVisibility(View.VISIBLE);
        img_footprint.setVisibility(View.VISIBLE);
        isSelect = 2;
        tv_payDirect.setTextColor(getResources().getColor(R.color.black));
        tv_selectDirect.setTextColor(getResources().getColor(R.color.green004));
        Drawable drawable = getResources().getDrawable(R.drawable.ic_shaixuan_on);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv_selectDirect.setCompoundDrawables(null, null, drawable, null);
        tv_zeroDirect.setTextColor(getResources().getColor(R.color.black));
        directBeans.clear();
        listView.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        adapter.notifyDataSetChanged();
        currentPage = 1;
        DirectHomePageActivity.ActualPrice = "";
        loadLiveData(true, currentPage);
    }

    /**
     * 展开筛选 gridview
     */
    private static void expandGridview(RadioGroup gv_content) {
        if (gv_content.getVisibility() == View.GONE) {
//            Animation expandAnimation = AnimationUtil.expand(gv_content,
//                    true, 300);
//            gv_content.startAnimation(expandAnimation);
            gv_content.setVisibility(ViewGroup.VISIBLE);
        } else {
//            Animation expandAnimation = AnimationUtil.expand(gv_content,
//                    false, 300);
//            gv_content.startAnimation(expandAnimation);
            gv_content.setVisibility(ViewGroup.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gv_test_categories:
                break;
            case R.id.gv_interview:
                break;
            case R.id.gv_subjects:
                break;
            case R.id.gv_learning_period:
                break;
            default:
                break;
        }

    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<List<DirectBean>, String> {
        private DirectAllTabFragment weak_fragment;
        private boolean isReset;

        public ObtatinDataListener(Boolean isReset, DirectAllTabFragment fragment) {
            weak_fragment = new WeakReference<>(fragment).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (weak_fragment != null) {
                weak_fragment.isLoadEnd = false;
                weak_fragment.listView.setVisibility(View.VISIBLE);
                weak_fragment.rl_wifi.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSuccess(final List<DirectBean> res) {
            if (weak_fragment != null) {
                weak_fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_fragment.flushContent_OnSucess(isReset, res);
                        weak_fragment.img_footprint.setVisibility(View.VISIBLE);
//                        if (res!=null){
//                            weak_fragment.rl_nodata.setVisibility(View.GONE);
//                        }else{
//                            weak_fragment.rl_nodata.setVisibility(View.VISIBLE);
//                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_fragment != null) {
                weak_fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_fragment.flushContent_OnFailure(isReset, res);
                    }
                });
            }
        }
    }

    public void flushContent_OnSucess(boolean isReset, List<DirectBean> res) {
        mPullToRefreshLayout.setVisibility(View.VISIBLE);
        if (isReset && (res == null || res.size() == 0)) {
            ToastUtils.showToast(R.string.no_data);
            rl_nodata.setVisibility(View.VISIBLE);
        } else {
            rl_nodata.setVisibility(View.GONE);
        }

        if (res == null || res.size() == 0) {
            hasMoreData = false;
        } else {
            hasMoreData = true;
            currentPage++;
            directBeans.addAll(res);
        }
        completeRefresh();
    }

    public void flushContent_OnFailure(boolean isReset, String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
            if (isReset) {
                listView.setVisibility(View.GONE);
                rl_wifi.setVisibility(View.VISIBLE);
                mPullToRefreshLayout.setVisibility(View.GONE);
                img_footprint.setVisibility(View.GONE);
            }
        } else if (SendRequest.ERROR_SERVER.equals(res)) {
            ToastUtils.showToast(R.string.server_error);
        }
        completeRefresh();
    }

    /**
     * 获取直播列表
     *
     * @param
     */
    public void loadLiveData(final boolean isReset, int currentPage) {
        obtatinDataListener = new ObtatinDataListener(isReset, this);
//        SendRequest.getLiveData(String.valueOf(currentPage), String.valueOf(limit), String.valueOf(isFuFei), obtatinDataListener);
        DebugUtil.e("考试类别categorie:" + DirectHomePageActivity.categorie
                + " 学段period:" + " 课程班型class_type：" + DirectHomePageActivity.class_type + DirectHomePageActivity.period
                + " 学科subject:" + DirectHomePageActivity.subject +
                " 价格ActualPrice:" + DirectHomePageActivity.ActualPrice +
                " 省份province:" + province +
                " 视频类型videoType:" + videoType);
        SendRequest.getLiveData(userid, String.valueOf(currentPage), String.valueOf(limit), String.valueOf(DirectHomePageActivity.categorie), String.valueOf
                (DirectHomePageActivity.period), DirectHomePageActivity.class_type, String.valueOf(DirectHomePageActivity.subject), String.valueOf(DirectHomePageActivity.ActualPrice), province, videoType, obtatinDataListener);


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
            adapter.setDirectBeanList(directBeans);
            adapter.notifyDataSetChanged();
        }

        if (hasMoreData) {
            isLoadEnd = true;
        } else {
            isLoadEnd = false;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        videoType = 0;
    }
}
