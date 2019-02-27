package com.huatu.teacheronline.direct;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.greendao.DaoUtils;
import com.greendao.DirectBean;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.umeng.analytics.MobclickAgent;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的足迹
 * @author ljyu
 * @time 2016-8-8 08:42:46
 */
public class DirectFootprintActivity extends BaseActivity {


    private ListView lv_footprint;
    private TextView tv_main_title;
    private TextView main_right;
    private PercentLinearLayout Select_down;
    private TextView choice_download;
    private TextView main_cancel;
    private FootAdaper footAdaper;
    private List<DirectBean> directBeanList = new ArrayList<DirectBean>();
    private CustomAlertDialog customAlertDialog;
    private boolean isCheckAll = true;
    private DaoUtils daoUtils;
    private String userid;
    private boolean isLoadEnd;
    private RelativeLayout rl_wifi;
    private int currentPage; //页数
    private int pageSize = 8; //页数
    private int currentAll = 0;//当前个数
    private View loadView;
    private View loadIcon;
    private RotateAnimation refreshingAnimation;
    private boolean hasMoreData = true;
    private RelativeLayout rl_nodata;//没数据

    @Override
    public void initView() {
        setContentView(R.layout.activity_direct_footprint);
        daoUtils = DaoUtils.getInstance();
        userid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        rl_wifi = (RelativeLayout) findViewById(R.id.rl_wifi);
        rl_nodata = (RelativeLayout) findViewById(R.id.rl_nodata);
        lv_footprint = (ListView) findViewById(R.id.lv_footprint);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.my_footprint);
        main_right = (TextView) findViewById(R.id.tv_main_right);
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        Select_down = (PercentLinearLayout) findViewById(R.id.Select_down);
        choice_download = (TextView) findViewById(R.id.Choice_download);
        main_cancel = (TextView) findViewById(R.id.tv_main_cancel);
        main_right.setVisibility(View.VISIBLE);
//        List<DirectBean> directBeans = daoUtils.queryDirectBeanForHistoryForUserid(userid);
//        directBeanList.addAll(directBeans);
        footAdaper = new FootAdaper(this, directBeanList, false, this);
        loadView = getLayoutInflater().inflate(R.layout.background_isloading, null);
        loadIcon = loadView.findViewById(R.id.loading_icon);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.pull_to_refresh_and_load_rotating);
        lv_footprint.addFooterView(loadView);
        loadIcon.startAnimation(refreshingAnimation);
        lv_footprint.setAdapter(footAdaper);
        iniData(true);
    }

    private void iniData(boolean isReset) {
        StringBuffer directIds = new StringBuffer();
        if (isReset) {
            currentPage = 1;
        } else {
            currentPage++;
        }
        List<DirectBean> directBeans = daoUtils.queryDirectBeanForHistoryForUserid(userid);

        for(int i = currentAll;currentAll < directBeans.size() && currentAll < currentPage*pageSize; this.currentAll++){
            String rid = directBeans.get(currentAll).getRid()+",";
            directIds.append(rid);
        }
        if(directIds.length() == 0){
            lv_footprint.removeFooterView(loadView);
//            main_right.setVisibility(View.GONE);
            if(isReset){
                lv_footprint.setVisibility(View.GONE);
                rl_nodata.setVisibility(View.VISIBLE);
            }
            return;
        }
        ObtatinDataListener obtatinDataListener = new ObtatinDataListener(isReset,DirectFootprintActivity.this);
        SendRequest.getDirectHistory(directIds.toString().substring(0, directIds.length() - 1), obtatinDataListener);
    }

    private static class ObtatinDataListener extends ObtainDataFromNetListener<List<DirectBean>, String> {
        private DirectFootprintActivity directFootprintActivity;
        private boolean isReset;

        public ObtatinDataListener(Boolean isReset, DirectFootprintActivity directFootprintActivity) {
            this.directFootprintActivity = new WeakReference<>(directFootprintActivity).get();
            this.isReset = isReset;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (directFootprintActivity != null) {
                directFootprintActivity.isLoadEnd = false;
                directFootprintActivity.lv_footprint.setVisibility(View.VISIBLE);
                directFootprintActivity.rl_wifi.setVisibility(View.GONE);
                directFootprintActivity.rl_nodata.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSuccess(final List<DirectBean> res) {
            if (directFootprintActivity != null) {
                directFootprintActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        directFootprintActivity.flushContent_OnSucess(isReset, res);
                        if (res.size() == 0) {
                            directFootprintActivity.main_right.setVisibility(View.GONE);
                        }
                    }
                });

            }
        }

        @Override
        public void onFailure(final String res) {
            if (directFootprintActivity != null) {
                directFootprintActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        directFootprintActivity.flushContent_OnFailure(isReset, res);
                    }
                });
            }
        }
    }

    public void flushContent_OnSucess(boolean isReset, List<DirectBean> res) {
        if (res == null || res.size() == 0) {
            hasMoreData = false;
            if (isReset) {
                directBeanList.clear();//清空数据
                footAdaper.notifyDataSetChanged();
                ToastUtils.showToast(R.string.no_data);
            }
        } else {
            hasMoreData = true;
            if (isReset) {
                directBeanList.clear();//清空数据
            }
            directBeanList.addAll(res);
            footAdaper.notifyDataSetChanged();
        }
        completeRefresh();
    }

    public void flushContent_OnFailure(boolean isReset, String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            if (isReset) {
                lv_footprint.setVisibility(View.GONE);
                rl_wifi.setVisibility(View.VISIBLE);
            }
        } else if (SendRequest.ERROR_SERVER.equals(res)) {
            if (isReset) {
                directBeanList.clear();//清空数据
                rl_nodata.setVisibility(View.VISIBLE);
            }
            ToastUtils.showToast(R.string.server_error);
        }
        completeRefresh();
    }
    private void completeRefresh() {
        if (lv_footprint != null && lv_footprint.getFooterViewsCount() > 0) {
            lv_footprint.removeFooterView(loadView);
        }

        if (footAdaper != null) {
            footAdaper.notifyDataSetChanged();
        }

        if (hasMoreData) {
            isLoadEnd = true;
        } else {
            isLoadEnd = false;
        }
    }

    @Override
    public void setListener() {
        main_right.setOnClickListener(this);//编辑
        main_cancel.setOnClickListener(this);//删除
        choice_download.setOnClickListener(this);//全选
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
                MobclickAgent.onEvent(DirectFootprintActivity.this, "courseOnItemClick");
                if (1 == directBeanList.get(position).getStatus()) {//在售
                    DirectDetailsActivity.newIntent(DirectFootprintActivity.this, directBeanList.get(position).getRid());
                } else{//下架
                    ToastUtils.showToast(getString(R.string.direct_shelves));
                }
                //足迹相关
//                DirectBean directBean = directBeanList.get(position);
//                directBean.setUserid(userid);
//                DebugUtil.e("insertOrUpdateDirectBeanHistory:" + directBean.toString());
//                daoUtils.insertOrUpdateDirectBeanHistory(directBean);
//                List<DirectBean> directBeans = daoUtils.queryDirectBeanForHistoryForUserid(userid);
//                DebugUtil.e("queryDirectBean:"+directBeans.size());
//                DebugUtil.e("queryDirectBean:"+directBeans.toString());

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_right://编辑
                if (directBeanList.size() == 0) {
                    return;
                }
                main_right.setVisibility(View.GONE);
                main_cancel.setVisibility(View.VISIBLE);
                Select_down.setVisibility(View.VISIBLE);
                choice_download.setVisibility(View.VISIBLE);
                main_cancel.setText("取消");
                footAdaper.setIsEdit(true);
                break;
            case R.id.tv_main_cancel://删除
                if (main_cancel.getText().equals("取消")) {
                    for (int i = 0; i < directBeanList.size(); i++) {
                        directBeanList.get(i).setIsCheck(false);
                    }
                    main_right.setVisibility(View.VISIBLE);
                    main_cancel.setVisibility(View.GONE);
                    Select_down.setVisibility(View.GONE);
                    footAdaper.setIsEdit(false);
                    isCheckAll = true;
                    choice_download.setText("全选");
                } else {
                    customAlertDialog = new CustomAlertDialog(this, R.layout.dialog_delete_video);
                    customAlertDialog.show();
                    customAlertDialog.setOkOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            main_right.setVisibility(View.VISIBLE);
                            main_cancel.setVisibility(View.GONE);
                            Select_down.setVisibility(View.GONE);
                            DebugUtil.e("tv_main_cancel:" + directBeanList.toString());
                            deleteDirectBean();
                            footAdaper.setIsEdit(false);
                            if (directBeanList.size()==0) {
                                rl_nodata.setVisibility(View.VISIBLE);
                                main_right.setVisibility(View.GONE);

                            }
                            footAdaper.notifyDataSetChanged();
                            main_cancel.setText("删除");
                            customAlertDialog.dismiss();
                        }
                    });
                    customAlertDialog.setCancelOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customAlertDialog.dismiss();
                        }
                    });
                }
                break;
            case R.id.Choice_download://点击全选
                if (isCheckAll) {
                    for (int i = 0; i < directBeanList.size(); i++) {
                        directBeanList.get(i).setIsCheck(true);
                        DebugUtil.e("ischeckAlltrue:" + directBeanList.get(i).toString());
                    }
                    main_cancel.setText("删除");
                    choice_download.setText(isCheckAll ? "取消全选" : "全选");
                    isCheckAll = !isCheckAll;
                } else {
                    for (int i = 0; i < directBeanList.size(); i++) {
                        directBeanList.get(i).setIsCheck(false);
                        DebugUtil.e("ischeckAllfalse:" + directBeanList.get(i).toString());
                    }
                    main_cancel.setText("取消");
                    choice_download.setText(isCheckAll ? "取消全选" : "全选");
                    isCheckAll = !isCheckAll;
                }
                footAdaper.notifyDataSetChanged();
                break;
            case R.id.tv_tick://点击勾选
                DirectBean editDirectBean = (DirectBean) v.getTag();
                clickCheck(editDirectBean);
                break;
            case R.id.rl_main_left://back
                finish();
                break;
        }
    }

    /**
     * 删除DirectBean
     */
    private void deleteDirectBean() {
        for (int i = directBeanList.size() - 1; i >= 0; i--) {
            DirectBean directBean = directBeanList.get(i);
            if (directBean.isCheck()) {
                directBean.setFootprint(1);
                directBeanList.remove(i);
                daoUtils.deleteDirectBeanForRid(directBean);
                }
        }
    }

    /**
     * 点击勾选切换不同的状态
     *
     * @param editDirectBean
     */
    private void clickCheck(DirectBean editDirectBean) {
        if (editDirectBean.isCheck()) {
            editDirectBean.setIsCheck(false);
        } else {
            editDirectBean.setIsCheck(true);
        }
        footAdaper.notifyDataSetChanged();
        int checkSize = 0;
        for (int i = 0; i < directBeanList.size(); i++) {
            if (directBeanList.get(i).isCheck()) {
                checkSize++;
            }
        }
        if (checkSize == 0) {
            main_cancel.setText("取消");
        } else if (checkSize == directBeanList.size()) {
            main_cancel.setText("删除");
            isCheckAll = false;
            choice_download.setText("取消全选");
        } else {
            isCheckAll = true;
            choice_download.setText("全选");
            main_cancel.setText("删除");
        }
    }

    /**
     * 跳转页面
     * @param context
     */
    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, DirectFootprintActivity.class);
        context.startActivity(intent);
    }

    class FootAdaper extends BaseAdapter {
        private View.OnClickListener OnClickListener;
        private Context context;
        private List<DirectBean> mChapterTreeData;
        public boolean isEdit;//是否编辑
        private static final String TAG = "FootAdaper";

        public FootAdaper(Context context, List<DirectBean> mChapterTreeData, boolean isEdit, View.OnClickListener OnClickListener) {
            this.context = context;
            this.mChapterTreeData = mChapterTreeData;
            this.isEdit = isEdit;
            this.OnClickListener = OnClickListener;
        }

        public void setmChapterTreeData(List<DirectBean> mChapterTreeData) {
            this.mChapterTreeData = mChapterTreeData;
            notifyDataSetChanged();
        }
        public void setIsEdit(boolean isEdit) {
            this.isEdit = isEdit;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mChapterTreeData.size();
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
                convertView = LayoutInflater.from(context).inflate(R.layout.item_footprint, null);
                holder = new ViewHolder();
                holder.tv_directTitle = (TextView) convertView.findViewById(R.id.tv_direct_title);
                holder.sdv_icon = (SimpleDraweeView) convertView.findViewById(R.id.sdv_icon);
                holder.iv_todayTab = (ImageView) convertView.findViewById(R.id.iv_todayTab);
                holder.tv_tick = (ImageView) convertView.findViewById(R.id.tv_tick);
                holder.img_isover_footprint = (ImageView) convertView.findViewById(R.id.img_isover_footprint);
                holder.tv_livePersion = (TextView) convertView.findViewById(R.id.tv_teacher);
                holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                holder.tv_keshiValue = (TextView) convertView.findViewById(R.id.tv_classNumber);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //对应的实体类
            final DirectBean interviewVideoBean = mChapterTreeData.get(position);
            DebugUtil.e("FootAdaper" + interviewVideoBean.toString());
            //删除之后mChapterTreeDatas集合里面就没有了
            holder.tv_livePersion.setText(interviewVideoBean.getTeacherDesc());
            holder.tv_keshiValue.setText(interviewVideoBean.getLessionCount() + "课时");
            holder.tv_price.setText("￥" + interviewVideoBean.getActualPrice());
            holder.tv_directTitle.setText(interviewVideoBean.getTitle());
            holder.tv_tick.setImageResource(interviewVideoBean.isCheck() ? R.drawable.ic_sc_xuanzhong : R.drawable.ic_sc_weixuan);
            if ("0".equals(interviewVideoBean.getIs_zhibo())) {//往期
                holder.iv_todayTab.setVisibility(View.GONE);
            } else if ("1".equals(interviewVideoBean.getIs_zhibo())) {//今日
                holder.iv_todayTab.setVisibility(View.VISIBLE);
            }
            if (1 == interviewVideoBean.getStatus()) {//在售
                holder.img_isover_footprint.setVisibility(View.GONE);
            } else{//下架
                holder.img_isover_footprint.setVisibility(View.VISIBLE);
            }
            if (isEdit) {
                holder.tv_tick.setVisibility(View.VISIBLE);
            } else {
                holder.tv_tick.setVisibility(View.GONE);
            }
            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(context.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setFadeDuration(200)
                    .setPlaceholderImage(context.getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                    .setFailureImage(context.getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                    .build();
            holder.sdv_icon.setHierarchy(hierarchy);
            FrescoUtils.setFrescoImageUri(holder.sdv_icon, interviewVideoBean.getScaleimg(), R.drawable.ic_loading);
            holder.tv_tick.setTag(interviewVideoBean);
            holder.tv_tick.setOnClickListener(OnClickListener);
            return convertView;
        }

        public class ViewHolder {
            public TextView tv_directTitle;
            public SimpleDraweeView sdv_icon;
            public ImageView tv_tick;
            public ImageView iv_todayTab;
            public TextView tv_livePersion;
            public TextView tv_price;
            public TextView tv_keshiValue;
            public ImageView img_isover_footprint;
        }
    }
}
