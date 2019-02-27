package com.huatu.teacheronline.direct;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baijiahulian.BJVideoPlayerSDK;
import com.baijiayun.download.DownloadTask;
import com.baijiayun.download.constant.TaskStatus;
import com.gensee.utils.StringUtil;
import com.greendao.DaoUtils;
import com.greendao.DirectBean;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.adapter.Downloadaper;
import com.huatu.teacheronline.direct.bean.PlayByIndexEvent;
import com.huatu.teacheronline.utils.ClickUtils;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.FileUtils;
import com.huatu.teacheronline.utils.NetWorkUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.RctView;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import me.drakeet.materialdialog.MaterialDialog;


/**
 * video下载管理
 *
 * @author ljyu
 * @time 2016-8-4 08:54:39
 */
public class DownManageActivity extends BaseActivity implements View.OnClickListener {
    public static final String ACTION_REFRASH = "action_refrash";
    private TextView main_right;
    private PercentLinearLayout Select_down;
    private ListView list_download;
    private Downloadaper manageAdadper;
    private TextView main_cancel;
    private List<DirectBean> directBeanListForClassSchedule = new ArrayList<DirectBean>();
    private List<DirectBean> downLoaded = new ArrayList<DirectBean>();//已经下载完的目录 //编辑需要用
    private TextView choice_download;
    private boolean isCheckAll = true;
    private DaoUtils daoUtils;//数据库工具类
    private String userid;//用户id
    private static final String TAG = "DownManageActivity";
    private TextView tv_main_title;
    public static final int CCDOWNSTATE_WAIT = 100;//下载等待
    public static final int CCDOWNSTATE_STAR = 200;//开始下载
    public static final int CCDOWNSTATE_PAUSE = 300;//下载暂停
    public static final int CCDOWNSTATE_COMPLETE = 400;//下载完成
    public static final int CCDOWNSTATE_DOWN_WAIT = 600;//等待下载
    public static final int CCDOWNSTATE_OTHER = 500;//下载出错
    private ServiceConnection sc;
    private VideoDownLoadService.SimpleBinder sBinder;
    private Downloadaper editAdapter;//编辑的adapter
    private CustomAlertDialog customAlertDialog;//删除的dialog

    @BindView(R.id.rct_download_progress)
    RctView mPbStorage;
    @BindView(R.id.tv_download_storage)
    TextView mTvStorage;
    private MaterialDialog mNetWorkDialog;


    @Override
    public void initView() {
        setContentView(R.layout.activity_down_manage);
        ButterKnife.bind(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRASH);
        setIntentFilter(intentFilter);

        daoUtils = DaoUtils.getInstance();
        userid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        directBeanListForClassSchedule = DataStore_Direct.directDatailList;
        list_download = (ListView) findViewById(R.id.list_download);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.video_down);
        main_right = (TextView) findViewById(R.id.tv_main_right);

        findViewById(R.id.rl_main_left).setOnClickListener(this);
        Select_down = (PercentLinearLayout) findViewById(R.id.Select_down);
        choice_download = (TextView) findViewById(R.id.Choice_download);
        main_cancel = (TextView) findViewById(R.id.tv_main_cancel);
        manageAdadper = new Downloadaper(this, directBeanListForClassSchedule, false, this);
        editAdapter = new Downloadaper(this, downLoaded, true, this);
        list_download.setAdapter(manageAdadper);

        choice_download.setText("全选");
        new RxPermissions(this)
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            init();
                            synchronizationLocal(directBeanListForClassSchedule);
                            manageAdadper.notifyDataSetChanged();
                        } else {
                            Toast.makeText(DownManageActivity.this, "没有获取读写sd卡权限", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });


    }


    private void init() {

        sc = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                sBinder = (VideoDownLoadService.SimpleBinder) service;
//                if (sBinder != null && sBinder.getDownLoaderList().size() > 0) {
//                    ArrayList<DirectBean> downLoaderList = sBinder.getDownLoaderList();
//                    for (int i = 0; i < downLoaderList.size(); i++) {
//                        DirectBean directBean = downLoaderList.get(i);
//                        for (int j = 0; j < directBeanListForClassSchedule.size(); j++)
//                            if (directBeanListForClassSchedule.get(j).getVideoType() == 0) {
//                                if (directBeanListForClassSchedule.get(j).getCcCourses_id().equals(directBean.getCcCourses_id())) {
//                                    directBeanListForClassSchedule.get(j).setDown_status(directBean.getDown_status());
//                                }
//                            } else {
//                                if (!StringUtils.isEmpty(directBeanListForClassSchedule.get(j).getLubourl())) {
//                                    directBeanListForClassSchedule.get(j).setDown_status(directBean.getDown_status());
//                                } else if (directBeanListForClassSchedule.get(j).getVideo_status().equals("2")) {
//                                    directBeanListForClassSchedule.get(j).setDown_status(directBean.getDown_status());
//                                }
//
//
//                            }
//                    }
//                    manageAdadper.notifyDataSetChanged();
//                }
            }
        };

        Intent startIntent = new Intent(this, VideoDownLoadService.class);
        startService(startIntent);
        bindService(startIntent, sc, Context.BIND_AUTO_CREATE);

        initStorageState();
    }

    private void initStorageState() {
        float unit = 1024 * 1024 * 1024;
        float sdTotalSize = CommonUtils.getSDTotalSize() / unit;
        float sdAvailableSize = CommonUtils.getSDAvailableSize() / unit;
        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");

        mTvStorage.setText("已使用存储空间 " + df.format(sdTotalSize - sdAvailableSize) + "/" + df.format(sdTotalSize) + "G");
        mPbStorage.setProgress((int) ((sdTotalSize - sdAvailableSize) * 100 / sdTotalSize));


    }

    /**
     * 同步本地的数据
     *
     * @param directBeanListForClassSchedule
     */
    private void synchronizationLocal(List<DirectBean> directBeanListForClassSchedule) {
        for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
            DirectBean directBean = directBeanListForClassSchedule.get(i);
            if (directBeanListForClassSchedule.get(i).getVideoType() == 0) {
                if (StringUtil.isEmpty(directBean.getCcCourses_id())) {
                    continue;
                }
                //网课
                DirectBean directBean1 = daoUtils.queryDirectBeanForCCVedioId(userid, directBeanListForClassSchedule.get(i).getCcCourses_id(), directBeanListForClassSchedule.get(i).getNumber());
                if (directBean1 != null) {
                    directBeanListForClassSchedule.get(i).setDown_status(directBean1.getDown_status());
                    directBeanListForClassSchedule.get(i).setLocalPath(directBean1.getLocalPath());
                    directBeanListForClassSchedule.get(i).setStart(directBean1.getStart());
                    directBeanListForClassSchedule.get(i).setEnd(directBean1.getEnd());
                    if (!StringUtil.isEmpty(directBean1.getDown_status()) && Integer.parseInt(directBean1.getDown_status()) == CCDOWNSTATE_COMPLETE) {
                        downLoaded.add(directBean1);
                    }
                    DebugUtil.e("synchronizationLocal:" + directBeanListForClassSchedule.get(i).toString());
                }
            } else if (directBeanListForClassSchedule.get(i).getVideoType() == 1) {
                if (StringUtil.isEmpty(directBean.getLubourl())) {

                    DirectBean directOrg = directBeanListForClassSchedule.get(i);
                    DirectBean directQuery = daoUtils.queryDirectBeanForPlayBack(userid, directOrg.getRoom_id(), directOrg.getSession_id(), directOrg.getNumber());
                    if (directQuery != null) {

                        directBeanListForClassSchedule.get(i).setDown_status(directQuery.getDown_status());
                        directBeanListForClassSchedule.get(i).setLocalPath(directQuery.getLocalPath());
                        directBeanListForClassSchedule.get(i).setStart(directQuery.getStart());
                        directBeanListForClassSchedule.get(i).setEnd(directQuery.getEnd());
                        if (!StringUtil.isEmpty(directQuery.getDown_status()) && Integer.parseInt(directQuery.getDown_status()) == CCDOWNSTATE_COMPLETE) {
                            downLoaded.add(directQuery);
                        }
                        DebugUtil.e("synchronizationLocal:" + directBeanListForClassSchedule.get(i).toString());

                    }

                } else {
                    //展示互动点播
                    DirectBean directBean1 = daoUtils.queryDirectBeanForGeeneVedioId(userid, directBeanListForClassSchedule.get(i).getLubourl(), directBeanListForClassSchedule.get(i).getNumber());
                    if (directBean1 != null) {
                        directBeanListForClassSchedule.get(i).setDown_status(directBean1.getDown_status());
                        directBeanListForClassSchedule.get(i).setLocalPath(directBean1.getLocalPath());
                        directBeanListForClassSchedule.get(i).setStart(directBean1.getStart());
                        directBeanListForClassSchedule.get(i).setEnd(directBean1.getEnd());
                        if (!StringUtil.isEmpty(directBean1.getDown_status()) && Integer.parseInt(directBean1.getDown_status()) == CCDOWNSTATE_COMPLETE) {
                            downLoaded.add(directBean1);
                        }
                        DebugUtil.e("synchronizationLocal:" + directBeanListForClassSchedule.get(i).toString());
                    }
                }

            }
        }
        if (downLoaded.size() > 0) {
            //右边编辑按钮
            main_right.setVisibility(View.VISIBLE);
        } else {
            main_right.setVisibility(View.GONE);
        }
    }

    @Override
    public void setListener() {
        main_right.setOnClickListener(this);//编辑
        main_cancel.setOnClickListener(this);//删除
        choice_download.setOnClickListener(this);//全选


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_right://编辑
                if (downLoaded.size() == 0) {
                    return;
                }
                main_right.setVisibility(View.GONE);
                main_cancel.setText("取消");
                main_cancel.setVisibility(View.VISIBLE);
                Select_down.setVisibility(View.VISIBLE);
                choice_download.setVisibility(View.VISIBLE);
                list_download.setAdapter(editAdapter);
                break;
            case R.id.tv_main_cancel://删除
                if (main_cancel.getText().equals("取消")) {
                    for (int i = 0; i < downLoaded.size(); i++) {
                        downLoaded.get(i).setIsCheck(false);
                    }
                    main_right.setVisibility(View.VISIBLE);
                    main_cancel.setVisibility(View.GONE);
                    Select_down.setVisibility(View.GONE);
                    list_download.setAdapter(manageAdadper);
                    isCheckAll = true;
                    choice_download.setText("全选");
                } else {

                    int selectedItem = 0;

                    for (int i = downLoaded.size() - 1; i >= 0; i--) {
                        DirectBean directBean = downLoaded.get(i);
                        if (directBean.isCheck()) {
                            selectedItem++;
                        }


                    }

                    if (selectedItem == 0) {
                        ToastUtils.showToast("请先选择视频");
                        return;
                    }


                    customAlertDialog = new CustomAlertDialog(this, R.layout.dialog_delete_video);
                    customAlertDialog.show();
                    customAlertDialog.setOkOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            main_right.setVisibility(View.VISIBLE);
                            main_cancel.setVisibility(View.GONE);
                            Select_down.setVisibility(View.GONE);
                            deleteFile();
                            initStorageState();
                            DebugUtil.e("tv_main_cancel:" + downLoaded.toString());
                            editAdapter.notifyDataSetChanged();
                            downLoaded.clear();
                            synchronizationLocal(directBeanListForClassSchedule);
                            list_download.setAdapter(manageAdadper);
                            manageAdadper.notifyDataSetChanged();
                            main_cancel.setText("删除");

                            choice_download.setText("全选");


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
                    for (int i = 0; i < downLoaded.size(); i++) {
                        downLoaded.get(i).setIsCheck(true);
                        DebugUtil.e("ischeckAlltrue:" + downLoaded.get(i).toString());
                    }
                    main_cancel.setText("删除");
                    choice_download.setText(isCheckAll ? "取消全选" : "全选");
                    isCheckAll = !isCheckAll;
                } else {
                    for (int i = 0; i < downLoaded.size(); i++) {
                        downLoaded.get(i).setIsCheck(false);
                        DebugUtil.e("ischeckAllfalse:" + downLoaded.get(i).toString());
                    }
                    main_cancel.setText("取消");
                    choice_download.setText(isCheckAll ? "取消全选" : "全选");
                    isCheckAll = !isCheckAll;
                }
                editAdapter.notifyDataSetChanged();
                break;
            case R.id.rl_state://点击下载
                //下载前需要判断存储容量是否大于300M
                final DirectBean directBean = (DirectBean) v.getTag();

                if (directBean.getDown_status() != null && Integer.parseInt(directBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_COMPLETE) {

                    if (DataStore_Direct.directDatailList != null) {

                        if (ClickUtils.isFastClick()) {
                            return;
                        }


                        int index = DataStore_Direct.directDatailList.indexOf(directBean);

                        PlayByIndexEvent playByIndexEvent = new PlayByIndexEvent();
                        playByIndexEvent.setInfo(directBean);
                        playByIndexEvent.setIndex(index);
                        EventBus.getDefault().post(playByIndexEvent);


                        finish();
                    }


                    return;
                }

                int networkType = NetWorkUtils.getAPNType(this);
                String netWorkName = "";
                switch (networkType) {
                    case 0:
                        netWorkName = "无网络";
                        break;
                    case 1:
                        netWorkName = "WIFI网络";
                        break;
                    case 2:
                        netWorkName = "2G网络";
                        break;
                    case 3:
                        netWorkName = "3G网络";
                        break;
                    case 4:
                        netWorkName = "4G网络";
                        break;
                }


                if (directBean.getVideoType() == 0) {
//                    if (directBean.getDown_status() != null && Integer.parseInt(directBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_OTHER) {
//                        sBinder.delete(directBean);
//                        return;
//                    }

                    if (directBean.getDown_status() != null && Integer.parseInt(directBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_DOWN_WAIT) {
                        sBinder.delete(directBean);
                    } else {

                        boolean exist = checkTaskFinished(directBean);
                        if (exist) {
                            return;
                        }

//                        DebugUtil.e("是否大于300M"+ CommonUtils.isAvaiableSpace(300));
                        if (!CommonUtils.isAvaiableSpace(500)) {
                            ToastUtils.showToast(R.string.sd_is_full_info);
                            return;
                        }
//                        sBinder.starCDownload(directBean);
                        if ((directBean.getDown_status() == null || (directBean.getDown_status() != null && Integer.parseInt(directBean.getDown_status()) != DownManageActivity.CCDOWNSTATE_STAR)) && networkType > 1) {
                            mNetWorkDialog = new MaterialDialog(this);
                            mNetWorkDialog.setTitle("提示")
                                    .setMessage("当前网络为" + netWorkName + ",是否继续下载?")
                                    .setNegativeButton("取消", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            mNetWorkDialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("继续下载", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            mNetWorkDialog.dismiss();
                                            sBinder.addDownloadTask(directBean);

                                        }
                                    })
                                    .show();


                        } else {
                            sBinder.addDownloadTask(directBean);
                        }
                    }
                } else {


                    switch (directBean.getVideo_status()) {
                        case "0":
                            ToastUtils.showToast("正在直播,无法下载!");
                            break;
                        case "1":
                            ToastUtils.showToast("未开始直播,无法下载!");
                            break;
                        case "2":
                            //回放视频下载
//                            if (directBean.getDown_status() != null && Integer.parseInt(directBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_COMPLETE) {
//                                return;
//                            }
                            if (directBean.getDown_status() != null && Integer.parseInt(directBean.getDown_status()) == DownManageActivity.CCDOWNSTATE_DOWN_WAIT) {
                                sBinder.delete(directBean);
                            } else {
                                boolean exist = checkTaskFinished(directBean);
                                if (exist) {
                                    return;
                                }
                                if (!CommonUtils.isAvaiableSpace(500)) {
                                    ToastUtils.showToast(R.string.sd_is_full_info);
                                    return;
                                }

                                if ((directBean.getDown_status() == null || (directBean.getDown_status() != null && Integer.parseInt(directBean.getDown_status()) != DownManageActivity.CCDOWNSTATE_STAR)) && networkType > 1) {
                                    mNetWorkDialog = new MaterialDialog(this);
                                    mNetWorkDialog.setTitle("提示")
                                            .setMessage("当前网络为" + netWorkName + ",是否继续下载?")
                                            .setNegativeButton("取消", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    mNetWorkDialog.dismiss();
                                                }
                                            })
                                            .setPositiveButton("继续下载", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    mNetWorkDialog.dismiss();
                                                    sBinder.addDownloadTask(directBean);

                                                }
                                            })
                                            .show();


                                } else {
                                    sBinder.addDownloadTask(directBean);
                                }


                            }

                            break;
                        case "3":

                            ToastUtils.showToast("回放视频未生成,无法下载!");
                            break;
                    }


                }
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
     * TODO 判断该视频是否已下载
     *
     * @param directBean
     */
    private boolean checkTaskFinished(DirectBean directBean) {
        boolean exist = false;
        DownloadTask task = null;
        if (directBean.getVideoType() == 0) {
            String bjyvideoid = directBean.getBjyvideoid();
            if (!StringUtils.isEmpty(bjyvideoid)) {
                task = sBinder.getBjydownloader().getTaskByVideoId(Long.parseLong(bjyvideoid));
            }
        } else {
            long sessionId = (StringUtils.isEmpty(directBean.getSession_id()) ? 0 : Long.parseLong(directBean.getSession_id()));
            if (!StringUtils.isEmpty(directBean.getRoom_id())) {
                long roomId = Long.parseLong(directBean.getRoom_id());
                task = sBinder.getBjydownloader().getTaskByRoom(roomId, sessionId);
            }
        }

        if (task != null) {
//            switch (task.getTaskStatus().getType()) {
//                case 0:
//                    break;
//                case 1:
//                    break;
//                case 2:
//                    break;
//                case 3:
//                    break;
//                case 4:
//                    break;
//                case 5:
//                    break;
//            }

            if (task.getTaskStatus() == TaskStatus.Finish) {
                exist = true;

                if (directBean.getVideoType() == 0) {
                    String fileName = task.getFileName();
                    directBean.setDown_status(DownManageActivity.CCDOWNSTATE_COMPLETE + "");
                    DebugUtil.e(TAG, "bjyDOWNSTATE_COMPLETE" + directBean.toString());
                    directBean.setLocalPath(FileUtils.getBjyVideoDiskCacheDir() + fileName);
                    directBean.setStart((long) (100));
                    directBean.setEnd(new Long(100));
                    daoUtils.insertOrUpdateDirectBean(directBean, 0);
                    sendBroadcastByDirectBean(directBean);
                } else {

                    String signalFileName = task.getSignalFileName();
                    String targetName = task.getVideoDownloadInfo().targetName;

                    directBean.setDown_status(DownManageActivity.CCDOWNSTATE_COMPLETE + "");

                    directBean.setLocalPath(FileUtils.getBjyVideoDiskCacheDir() + targetName + ";" + FileUtils.getBjyVideoDiskCacheDir() + signalFileName);
                    directBean.setStart((long) (100));
                    directBean.setEnd(new Long(100));
                    daoUtils.insertOrUpdateDirectBean(directBean, 0);
                    sendBroadcastByDirectBean(directBean);
                }


            }


        }


        return exist;
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
        editAdapter.notifyDataSetChanged();
        int checkSize = 0;
        for (int i = 0; i < downLoaded.size(); i++) {
            if (downLoaded.get(i).isCheck()) {
                checkSize++;
            }
        }
        if (checkSize == 0) {
            main_cancel.setText("取消");
            choice_download.setText("全选");
        } else if (checkSize == downLoaded.size()) {
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
     * 删除文件并且保存到数据库同时更新本页面的信息
     */
    public void deleteFile() {
        for (int i = downLoaded.size() - 1; i >= 0; i--) {
            DirectBean directBean = downLoaded.get(i);
            if (directBean.isCheck()) {
                downLoaded.remove(i);
                if (directBean.getVideoType() == 0) {
                    if (StringUtil.isEmpty(directBean.getLocalPath())) {
                        editAdapter.notifyDataSetChanged();
                        directBean.setLocalPath("");
                        directBean.setDown_status(DownManageActivity.CCDOWNSTATE_WAIT + "");
                        //发广播通知各个activity
                        sendBroadcastByDirectBean(directBean);
                        daoUtils.insertOrUpdateDirectBean(directBean, directBean.getVideoType());
                        return;
                    }
                    if (!StringUtil.isEmpty(directBean.getBjyvideoid())) {
                        DownloadTask taskByVideoId = sBinder.getBjydownloader().getTaskByVideoId(Long.parseLong(directBean.getBjyvideoid()));
                        if (taskByVideoId != null) {
                            taskByVideoId.deleteFiles();
                        }

                    } else if (!StringUtil.isEmpty(directBean.getCcCourses_id())) {
                        FileUtils.deleteFolder(directBean.getLocalPath());
                    }
                    editAdapter.notifyDataSetChanged();
                    directBean.setLocalPath("");
                    directBean.setDown_status(DownManageActivity.CCDOWNSTATE_WAIT + "");
                    //发广播通知各个activity
                    sendBroadcastByDirectBean(directBean);
                    daoUtils.insertOrUpdateDirectBean(directBean, directBean.getVideoType());
                } else {
                    if (!StringUtil.isEmpty(directBean.getLubourl())) {
                        File file = FileUtils.dataPath();
                        String absolutePath = file.getAbsolutePath();
                        FileUtils.deleteFolder(absolutePath + "/video/0/" + directBean.getLubourl());
                        editAdapter.notifyDataSetChanged();
                        directBean.setLocalPath("");
                        directBean.setDown_status(DownManageActivity.CCDOWNSTATE_WAIT + "");
                        //发广播通知各个activity
                        sendBroadcastByDirectBean(directBean);
                        daoUtils.insertOrUpdateDirectBean(directBean, directBean.getVideoType());
                    } else {
                        if (directBean.getVideo_status().equals("2")) {
                            if (!StringUtils.isEmpty(directBean.getLocalPath())) {
                                long sessionId = (StringUtils.isEmpty(directBean.getSession_id()) ? 0 : Long.parseLong(directBean.getSession_id()));
                                long roomId = Long.parseLong(directBean.getRoom_id());
                                DownloadTask taskByRoom = sBinder.getBjydownloader().getTaskByRoom(roomId, sessionId);
                                if (taskByRoom != null) {
                                    taskByRoom.deleteFiles();
                                }

                                String[] split = directBean.getLocalPath().split(";");
                                if (split.length == 2) {
                                    File videoFile = new File(split[0]);
                                    File singleFile = new File(split[1]);

                                    FileUtils.deleteFolder(videoFile.getAbsolutePath());
                                    FileUtils.deleteFolder(singleFile.getAbsolutePath());
                                }

                            }


                            editAdapter.notifyDataSetChanged();
                            directBean.setLocalPath("");
                            directBean.setDown_status(DownManageActivity.CCDOWNSTATE_WAIT + "");
                            //发广播通知各个activity
                            sendBroadcastByDirectBean(directBean);
                            daoUtils.insertOrUpdateDirectBean(directBean, 0);


                        }


                    }

                }
            }
        }
    }

    public static void newIntent(Activity context, ArrayList<DirectBean> directBeanList) {
        Intent intent = new Intent(context, DownManageActivity.class);
        Bundle bundle = new Bundle();
//        bundle.putSerializable("DirectBeanList", directBeanList);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    // 下载时更新指定item的数据
    public void updateView(int index) {
        int visiblePos = list_download.getFirstVisiblePosition();
        int offset = index - visiblePos;
        // 只有在可见区域才更新
        if (offset < 0) return;
        View view = list_download.getChildAt(offset);
        if (view == null) return;
        final DirectBean directBean = directBeanListForClassSchedule.get(index);
        DebugUtil.e("updateView：directBean" + directBean.toString());
        final Downloadaper.ViewHolder holder = (Downloadaper.ViewHolder) view.getTag();

        if (!StringUtil.isEmpty(directBean.getDown_status())) {
            if (DownManageActivity.CCDOWNSTATE_WAIT == Integer.parseInt(directBean.getDown_status())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.choice.setImageResource(R.drawable.ic_zj_xiazai);
                        holder.choice.setVisibility(View.VISIBLE);
                        holder.cv_prograss.setVisibility(View.GONE);
                        DebugUtil.e("updateView：view" + "准备下载");
                    }
                });

            } else if (DownManageActivity.CCDOWNSTATE_STAR == Integer.parseInt(directBean.getDown_status())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DebugUtil.e("updateView：view" + "开始下载");
                        holder.choice.setImageResource(R.drawable.ic_huancun);
                        holder.choice.setVisibility(View.VISIBLE);
                        holder.cv_prograss.setVisibility(View.VISIBLE);
                        if (directBean.getStart() != null) {
                            int v = (int) (directBean.getStart() * 100 / directBean.getEnd());
                            DebugUtil.e("cv_prograss:" + v);
                            holder.cv_prograss.setProgress(v);
                        }
                    }
                });

            } else if (DownManageActivity.CCDOWNSTATE_PAUSE == Integer.parseInt(directBean.getDown_status())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DebugUtil.e("updateView：view" + "暂停下载");
                        holder.choice.setImageResource(R.drawable.ic_xiazai_zhong);
                        holder.cv_prograss.setVisibility(View.VISIBLE);
                        holder.choice.setVisibility(View.VISIBLE);
                    }
                });

            } else if (DownManageActivity.CCDOWNSTATE_COMPLETE == Integer.parseInt(directBean.getDown_status())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DebugUtil.e("updateView：view" + "完成下载");
                        holder.choice.setImageResource(R.drawable.ic_video_completed);
                        holder.choice.setVisibility(View.VISIBLE);
                        holder.cv_prograss.setVisibility(View.GONE);
                        directBean.setIsCheck(false);
                        downLoaded.add(directBean);
                        main_right.setVisibility(View.VISIBLE);

                    }
                });

            } else if (DownManageActivity.CCDOWNSTATE_DOWN_WAIT == Integer.parseInt(directBean.getDown_status())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DebugUtil.e("updateView：view" + "等待下载");
                        holder.choice.setImageResource(R.drawable.xz_dengdai);
                        holder.choice.setVisibility(View.VISIBLE);
                        holder.cv_prograss.setVisibility(View.GONE);
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DebugUtil.e("updateView：view" + "下载失败");
                        holder.choice.setImageResource(R.drawable.ic_xz_shibai);
                        holder.choice.setVisibility(View.VISIBLE);
                        holder.cv_prograss.setVisibility(View.GONE);
                    }
                });

            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DebugUtil.e("updateView：view" + "下载");
                    holder.choice.setImageResource(R.drawable.ic_zj_xiazai);
                    holder.choice.setVisibility(View.VISIBLE);
                    holder.cv_prograss.setVisibility(View.GONE);
                }
            });

        }
    }

    /**
     * 更新下载进度
     *
     * @param context 接受广播的上下文
     * @param intent  该广播的意图
     */
    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        DirectBean mDirectBean = (DirectBean) intent.getSerializableExtra("DirectBean");
        if (String.valueOf(DownManageActivity.CCDOWNSTATE_COMPLETE).equals(mDirectBean.getDown_status())) {
            initStorageState();
        }


        //为了防止在编辑模式下进度条还在更新
        if (main_right.getVisibility() == View.GONE && main_cancel.getVisibility() == View.VISIBLE) {
            if (DownManageActivity.CCDOWNSTATE_COMPLETE == Integer.parseInt(mDirectBean.getDown_status())) {
                mDirectBean.setIsCheck(false);
                downLoaded.add(mDirectBean);

                int checkSize = 0;
                for (int i = 0; i < downLoaded.size(); i++) {
                    if (downLoaded.get(i).isCheck()) {
                        checkSize++;
                    }
                }
                if (checkSize == 0) {
                    main_cancel.setText("取消");
                    choice_download.setText("全选");
                } else if (checkSize == downLoaded.size()) {
                    main_cancel.setText("删除");
                    isCheckAll = false;
                    choice_download.setText("取消全选");
                } else {
                    isCheckAll = true;
                    choice_download.setText("全选");
                    main_cancel.setText("删除");
                }

//                main_right.setVisibility(View.VISIBLE);
                editAdapter.notifyDataSetChanged();
            }
            return;
        }
//        DebugUtil.e("onReceiveBroadCast:ssss" + mDirectBean.toString());
        for (int i = 0; i < directBeanListForClassSchedule.size(); i++) {
            if (directBeanListForClassSchedule.get(i).getVideoType() == 0) {
                if (directBeanListForClassSchedule.get(i).getBjyvideoid().equals(mDirectBean.getBjyvideoid())) {
                    directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
                    directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
                    directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
                    directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
                    updateView(i);
                }
            } else {
                if (!StringUtils.isEmpty(directBeanListForClassSchedule.get(i).getLubourl())) {
                    if (directBeanListForClassSchedule.get(i).getLubourl().equals(mDirectBean.getLubourl())) {
                        directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
                        directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
                        directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
                        directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
                        updateView(i);
                    }
                } else {

                    if (directBeanListForClassSchedule.get(i).getRoom_id().equals(mDirectBean.getRoom_id())
                            && directBeanListForClassSchedule.get(i).getSession_id().equals(mDirectBean.getSession_id())
                            && directBeanListForClassSchedule.get(i).getVideo_status().equals("2")) {
                        directBeanListForClassSchedule.get(i).setDown_status(mDirectBean.getDown_status());
                        directBeanListForClassSchedule.get(i).setLocalPath(mDirectBean.getLocalPath());
                        directBeanListForClassSchedule.get(i).setStart(mDirectBean.getStart());
                        directBeanListForClassSchedule.get(i).setEnd(mDirectBean.getEnd());
                        Logger.d(mDirectBean.getId() + " ; " + mDirectBean.getDown_status());
                        updateView(i);


                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BJVideoPlayerSDK.getInstance().releaseDownloadClient();
        if (downLoaded != null) {
            for (DirectBean directBean : downLoaded) {
                directBean.setIsCheck(false);
            }

        }


        if (sc != null) {
            unbindService(sc);
        }
    }

    /**
     * 更新状态后的directBean发广播给vod cc rt activity
     *
     * @param directBean
     */
    private void sendBroadcastByDirectBean(DirectBean directBean) {
        Intent intent = new Intent();
        intent.putExtra("DirectBean", directBean);
        intent.setAction(DownManageActivity.ACTION_REFRASH);
        sendBroadcast(intent);
    }
}