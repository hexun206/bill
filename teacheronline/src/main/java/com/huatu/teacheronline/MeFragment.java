package com.huatu.teacheronline;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gensee.utils.StringUtil;
import com.google.gson.JsonObject;
import com.huatu.teacheronline.bean.PersonalInfoBean;
import com.huatu.teacheronline.engine.HTeacherApi;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.SendRequestUtilsForExercise;
import com.huatu.teacheronline.exercise.StudyRecordsActivity;
import com.huatu.teacheronline.login.ExamCatagoryChooseNewActivity;
import com.huatu.teacheronline.message.MyMessageActivity;
import com.huatu.teacheronline.personal.AboutActivity;
import com.huatu.teacheronline.personal.ErrorCorrectionActivity;
import com.huatu.teacheronline.personal.FeedListActivity;
import com.huatu.teacheronline.personal.InterviewCommentsActivity;
import com.huatu.teacheronline.personal.MyOrderActivity;
import com.huatu.teacheronline.personal.NewFeedBackActivity;
import com.huatu.teacheronline.personal.PersonalaccountActivity;
import com.huatu.teacheronline.sensorsdata.TrackUtil;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.FragmentPhotoUtils;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.utils.GsonUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.TripleDES;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.vipexercise.MyLeaveMessageActivity;
import com.huatu.teacheronline.widget.AssCodeView;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 我的
 * Created by ply on 2016/1/5.
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {
    private String currentSelectedInfo;
    private CustomAlertDialog customAlertDialog;
    private String uid;
    private String facePath_sp;
    private SimpleDraweeView iv_personal_face;
    private TextView tv_update_info;
    private RelativeLayout rl_information;
    private RelativeLayout rl_news;
    private RelativeLayout rl_myOrder;
    private RelativeLayout rl_record;
    private RelativeLayout rl_error;
    private RelativeLayout rl_collection;
    private RelativeLayout rl_feedback;
    private RelativeLayout rl_about;
    private RelativeLayout rl_wx;
    private GenericDraweeHierarchy hierarchy;
    private ImageView iv_msg;
    private String facePath_local;
    private FragmentPhotoUtils photoUtils;
    private String nickname;
    private String birthday;
    private int selectedSexPosition = 1;
    private String is_feedback = "";//is_feedback=0反馈消息为空
    private AlertDialog mAssCodeDialog;
    private AssCodeView mAsv_asscode;
    private TextView mTvAsscodeMin;
    private TextView mTvAsscodeSec;

    private ImageView mImgInterviewMsg;


    private final int MAX_TIME = 5 * 60 - 1;//秒
    private Subscription mCountDownTask;
    private ImageView iv_no_msg;
    private ImageView mImgInterviewNoMsg;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, R.layout.fragment_me_layout);
    }

    @Override
    public void initView() {
        super.initView();
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getActivity().getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        facePath_sp = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_FACEPATH, null);//头像地址
        nickname = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_NICKNAME, "");
        birthday = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_BIRTHDAY, "");
        String userid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        facePath_local = Environment.getExternalStorageDirectory().getPath() + "/huatu/" + userid + ".jpg";
        photoUtils = new FragmentPhotoUtils(this, userid);
        iv_personal_face = (SimpleDraweeView) findViewById(R.id.iv_personal_face);
        tv_update_info = (TextView) findViewById(R.id.tv_update_info);//科目
        rl_information = (RelativeLayout) findViewById(R.id.rl_information);//账户资料
        rl_news = (RelativeLayout) findViewById(R.id.rl_news);//我的消息
        rl_myOrder = (RelativeLayout) findViewById(R.id.rl_myOrder);//我的订单
        rl_record = (RelativeLayout) findViewById(R.id.rl_Record);//做题记录
        rl_error = (RelativeLayout) findViewById(R.id.rl_error);//纠错
        rl_collection = (RelativeLayout) findViewById(R.id.rl_Collection);//收藏+
        rl_feedback = (RelativeLayout) findViewById(R.id.rl_feedback);//意见反馈
        rl_wx = (RelativeLayout) findViewById(R.id.rl_me_wx_relate);//微信关联码
        rl_about = (RelativeLayout) findViewById(R.id.rl_About);//关于我们
        iv_msg = (ImageView) findViewById(R.id.iv_msg);
        mImgInterviewMsg = (ImageView) findViewById(R.id.img_me_interview_msg);
        iv_no_msg = (ImageView) findViewById(R.id.iv_no_msg);
        mImgInterviewNoMsg = (ImageView) findViewById(R.id.img_me_interview_no_msg);


        Setlistenr();
        initAssCodeDialog();

        initHierarchy();//获得用户头像


        //神策track click
        TrackUtil.trackClick(getActivity(), rl_wx, TrackUtil.TYPE_LIST, "微信关联码");
        TrackUtil.trackClick(getActivity(), rl_record, TrackUtil.TYPE_LIST, "做题记录");
        TrackUtil.trackClick(getActivity(), rl_error, TrackUtil.TYPE_LIST, "我的纠错");


    }


    private void Setlistenr() {
        iv_personal_face.setOnClickListener(this);
        tv_update_info.setOnClickListener(this);
        rl_information.setOnClickListener(this);
        rl_news.setOnClickListener(this);
        rl_myOrder.setOnClickListener(this);
        rl_record.setOnClickListener(this);
        rl_error.setOnClickListener(this);
        rl_collection.setOnClickListener(this);
        rl_feedback.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        rl_wx.setOnClickListener(this);
        findViewById(R.id.rel_me_interview).setOnClickListener(this);
    }

    //头像
    public void initHierarchy() {
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
        hierarchy = builder
                .setFadeDuration(100)
                .setPlaceholderImage(getResources().getDrawable(R.drawable.morentouxiang), ScalingUtils.ScaleType.CENTER_CROP)
                .setFailureImage(getResources().getDrawable(R.drawable.morentouxiang), ScalingUtils.ScaleType.CENTER_CROP)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .build();
        RoundingParams mRoundingParams = new RoundingParams();
        mRoundingParams.setRoundAsCircle(true);
        mRoundingParams.setBorder(Color.WHITE, 6);
        hierarchy.setRoundingParams(mRoundingParams);
        iv_personal_face.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(iv_personal_face, facePath_sp, R.drawable.morentouxiang);
    }

    @Override
    public void onRefresh() {

    }

    private void UserInfoData() {
        ObtainDataLister obtatinDataListener = new ObtainDataLister(this);
        SendRequest.getPersonalInfo(uid, obtatinDataListener);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_me_interview:
                InterviewCommentsActivity.start(getActivity());
                break;


            case R.id.rl_information:
                //账户信息
                PersonalaccountActivity.newIntent(this);
                break;
            case R.id.rl_news:
                //我的消息
                MobclickAgent.onEvent(getActivity(), "myNews");
                MyMessageActivity.newIntent(getActivity());
                break;
            case R.id.rl_feedback:
                //意见反馈
                if (is_feedback.equals("1")) {//如果is_feedback=1 则跳转反馈列表 否则新增反馈
                    FeedListActivity.newIntent(getActivity());
                } else if (is_feedback.equals("0")) {
                    NewFeedBackActivity.newIntent(getActivity());
                }
                break;
            case R.id.rl_error:
                //我的纠错
                startActivity(new Intent(getActivity(), ErrorCorrectionActivity.class));
                break;
            case R.id.rl_myOrder:
                //我的订单
                MyOrderActivity.newIntent(getActivity());
                break;
            case R.id.rl_Record:
                //做题记录
                StudyRecordsActivity.newIntent(getActivity());
                break;
            case R.id.rl_About:
                //关于我们
                AboutActivity.newIntent(getActivity());
                break;
            case R.id.tv_update_info:
                MobclickAgent.onEvent(getActivity(), "selectTestType");
                // 更新考试类型等信息
                ExamCatagoryChooseNewActivity.newIntent(getActivity());
//                ExamCatagoryChooseActivity.newIntent(getActivity());
                break;
            case R.id.rl_Collection:
//                MyLeaveMessageActivity.newIntent(getActivity());
                //留言老师
//                startActivity(new Intent(getActivity(), LeaTeacherActivity.class));
                startActivity(new Intent(getActivity(), MyLeaveMessageActivity.class));
                break;
            case R.id.iv_personal_face:

                new RxPermissions((FragmentActivity) getActivity())
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if (granted) {
                                    //修改头像
                                    MobclickAgent.onEvent(getActivity(), "personalPicOnClik");
                                    // 修改头像
                                    photoUtils.showPicturePicker(true);
                                } else {

                                    Toast.makeText(getActivity(), "请允许授予读写存储权限及拍照权限!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                break;

            case R.id.rl_me_wx_relate:
                getAssCode();

                break;

            case R.id.img_asscode_close:
                if (mAssCodeDialog != null) {
                    mAssCodeDialog.dismiss();
                    if (mCountDownTask != null) {
                        mCountDownTask.unsubscribe();
                    }
                }
                break;


        }
    }

    /**
     * 获取微信关联码
     */
    private void getAssCode() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), SendRequest.key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String p = TripleDES.byte2hex(bs);


        OkGo.<String>get(SendRequest.url_getAssCode + p)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        String body = response.body();
                        String code = GsonUtils.getJson(body, "code");

                        if ("1".equals(code)) {
                            showAssCOdeDialog(GsonUtils.getJson(body, "data"));
                        } else {
                            ToastUtils.showToast(GsonUtils.getJson(body, "message"));
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        if (mAssCodeDialog != null) {
                            mAssCodeDialog.dismiss();
                        }
                        ToastUtils.showToast("网络异常,请稍后重试!");

                    }
                });


    }

    private void showAssCOdeDialog(String code) {
        mAsv_asscode.setEnabled(true);
        mAsv_asscode.setText(code);
        mTvAsscodeMin.setText("04");
        mTvAsscodeSec.setText("59");

        startCountDown();

        if (mAssCodeDialog != null) {
            mAssCodeDialog.show();
            android.view.WindowManager.LayoutParams p = mAssCodeDialog.getWindow().getAttributes();  //获取对话框当前的参数值
            p.height = (int) (CustomApplication.height * 0.225);
            p.width = (int) (CustomApplication.width * 0.88);


            mAssCodeDialog.getWindow().setAttributes(p);
        }

    }


    /**
     * 开始倒计时
     */
    private void startCountDown() {
        if (mCountDownTask != null) {
            mCountDownTask.unsubscribe();
        }
        mCountDownTask = Observable.interval(1, TimeUnit.SECONDS)
                .take(MAX_TIME)
                .map(new Func1<Long, Integer>() {
                    @Override
                    public Integer call(Long aLong) {

                        return (int) (MAX_TIME - aLong - 1);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        if (isDetached()) {
                            return;
                        }
                        if (integer <= 0) {
                            mAsv_asscode.setEnabled(false);

                            if (mAssCodeDialog != null && mAssCodeDialog.isShowing()) {
                                getAssCode();
                            }

                        }
                        int min = integer / 60;
                        int sec = integer - min * 60;
                        String minStr = min < 10 ? ("0" + min) : (min + "");
                        String secStr = sec < 10 ? ("0" + sec) : (sec + "");

                        mTvAsscodeMin.setText(minStr);
                        mTvAsscodeSec.setText(secStr);


                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(TAG + throwable.getMessage());
                    }
                });


    }


    private void initAssCodeDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_asscode, null);
        mAsv_asscode = (AssCodeView) view.findViewById(R.id.asv_asscode);
        view.findViewById(R.id.img_asscode_close).setOnClickListener(this);
        mTvAsscodeMin = (TextView) view.findViewById(R.id.tv_asscode_min);
        mTvAsscodeSec = (TextView) view.findViewById(R.id.tv_asscode_sec);


        mAssCodeDialog = new AlertDialog.Builder(getActivity(), R.style.noTitleDialog)
                .setView(view)
                .create();


    }

    @Override
    public void onResume() {
        super.onResume();
        UserInfoData();

        HTeacherApi.getReadStatusByUid(uid, new ObtainDataFromNetListener<String, Throwable>() {
            @Override
            public void onSuccess(String res) {
                if ("1".equals(GsonUtils.getJson(res, "code"))) {

                    if ("1".equals(GsonUtils.getJson(res, "data"))) {
                        mImgInterviewMsg.setVisibility(View.VISIBLE);
                        mImgInterviewNoMsg.setVisibility(View.GONE);
                    } else {
                        mImgInterviewMsg.setVisibility(View.GONE);
                        mImgInterviewNoMsg.setVisibility(View.VISIBLE);
                    }


                } else {

                    mImgInterviewMsg.setVisibility(View.GONE);
                    mImgInterviewNoMsg.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onFailure(Throwable res) {
                mImgInterviewMsg.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 201) {
            if (resultCode == 202) {
                // 个人信息更新后返回，需要更新首页信息
                String nickname_sp = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_NICKNAME, null);
                facePath_sp = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_FACEPATH, null);
                iv_personal_face.setHierarchy(hierarchy);
                FrescoUtils.setFrescoImageUri(iv_personal_face, facePath_sp, R.drawable.morentouxiang);
            }
            return;
        }
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case UserInfo.TAKE_PICTURE:
                    photoUtils.takePictureForResult(iv_personal_face);
                    break;
                case UserInfo.CHOOSE_PICTURE:
                    photoUtils.choosePictureForResult(data, iv_personal_face);
                    break;
                case UserInfo.CROP:
                    // 截图界面
                    photoUtils.cropForResult(data);
                    break;
                case UserInfo.CROP_PICTURE:
                    photoUtils.cropPictureForResult(data, iv_personal_face, facePath_local);
                    saveInfo();
                    break;
            }
        }
    }

    /**
     * 更新首页的科目选择信息
     */
    private void initCurrentSelectedInfo() {
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();
        Resources resources = getResources();
        String selectedSubject = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_NAME, null);
        if (StringUtils.isEmpty(selectedSubject)) {
            return;
        }
        if (selectedSubject.equals("语文") || selectedSubject.equals("英语") ||
                selectedSubject.equals("体育") || selectedSubject.equals("音乐") || selectedSubject.equals("数学")
                || selectedSubject.equals("美术")) {
            selectedSubject = selectedSubject + "专业知识";
            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_NAME, selectedSubject);
        }
        String examstagName = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSTAGE_NAME, null);
        if (getResources().getString(R.string.key_stage_government_confirm).equals(sendRequestUtilsForExercise.examType)) {
            currentSelectedInfo = resources.getString(R.string.current_info) + resources.getString(R.string.current_gj) + resources.getString(R.string.dot) +
                    examstagName + resources.getString(R.string.dot)
                    + selectedSubject;
        } else {
            String cityname = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_NAME, null);
            String provinname = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_PROVINCE_NAME, null);
            if (TextUtils.isEmpty(cityname)) {
                cityname = CommonUtils.getInstance().getCityNameByXzqh(UserInfo.KEY_SP_CITY_ID);
                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_CITY_ID, cityname);
                if (TextUtils.isEmpty(cityname)) {
                    cityname = "";
                }
            }
            if (TextUtils.isEmpty(provinname)) {
                provinname = CommonUtils.getInstance().getProvinceNameByXzqh(UserInfo.KEY_SP_PROVINCE_ID);
                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_PROVINCE_ID, provinname);
                if (TextUtils.isEmpty(provinname)) {
                    provinname = "";
                }
            }
            //防止直辖市
            if (provinname.equals(cityname)) {
                currentSelectedInfo = resources.getString(R.string.current_info) + cityname + resources.getString(R.string.current_dq) + resources.getString(R
                        .string.dot) + examstagName + resources.getString(R.string.dot) + selectedSubject;
            } else {
                currentSelectedInfo = resources.getString(R.string.current_info) + provinname + cityname + resources.getString(R.string.current_dq) +
                        resources.getString(R
                                .string.dot) + examstagName + resources.getString(R.string.dot) + selectedSubject;
            }
        }

        tv_update_info.setText(currentSelectedInfo);
    }

    /****
     * 实时请求用户信息
     *****/
    private class ObtainDataLister extends ObtainDataFromNetListener<PersonalInfoBean, String> {

        private MeFragment weak_activity;

        public ObtainDataLister(MeFragment activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onSuccess(final PersonalInfoBean res) {
            if (weak_activity != null && !weak_activity.getActivity().isFinishing()) {
                if (res != null) {
                    weak_activity.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //更新金币信息
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_GOLD, res.getGold());
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, res.getId());
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_POINT, res.getUserPoint());
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_FIRST_PASS, res.getFirst_pass());
                            weak_activity.is_feedback = res.getis_feedback();
                            if (!StringUtil.isEmpty(res.getMsg_status())) {//首页我的消息小红点显示控制
                                if (Integer.parseInt(res.getMsg_status()) == 0) {
                                    iv_msg.setVisibility(View.VISIBLE);
                                    iv_no_msg.setVisibility(View.GONE);
                                } else {
                                    iv_msg.setVisibility(View.GONE);
                                    iv_no_msg.setVisibility(View.VISIBLE);
                                }
                            }
                            updataUserInfo(res);
                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        }
                        initCurrentSelectedInfo();
                    }
                });
            }
        }
    }

    /**
     * 实时更新用户信息
     *
     * @param res
     */
    private void updataUserInfo(PersonalInfoBean res) {
        String selectedCatetoryId = res.getType_id();// 考试类型
        String selectedCityXzqh = res.getCity();// 地区城市
        String selectedProvinceXzqh = res.getProvince();// 地区省份
        String selectedStageName = res.getSec_id();// 考试学段
        String selectedSubjectId = res.getSub_id();// 考试科目id
        String selectedSubjectName = "";// 考试科目名称
        String selectedSubjects = res.getSub_ids();// 多科目考试科目
        String sex = "";
        if ("1".equals(res.getSex())) {
            sex = getActivity().getResources().getString(R.string.info_sex_male);
        } else if ("0".equals(res.getSex())) {
            sex = getActivity().getResources().getString(R.string.info_sex_female);
        }
        if (StringUtil.isEmpty(selectedSubjects)) {
            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECTS_ID_NAME, res.getSub_id() + "_" + CommonUtils
                    .getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_NAME, ""));
            selectedSubjects = res.getSub_id() + "_" + CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_NAME, "");
        } else {
            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECTS_ID_NAME, res.getSub_ids());
        }
        String localSubjectId = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_ID, "");
        //为了更新科目名称
        if (!StringUtil.isEmpty(localSubjectId) && selectedSubjects.contains(localSubjectId)) {
            //如果本地保存的当前选中的科目在多科目选项里面就不更改前面显示
        } else {
            //如果本地保存的当前选中的科目不在多科目选项里面就更新科目显示并且保存本地
            String[] split = selectedSubjects.split(",");
            for (int i = 0; i < split.length; i++) {
                String[] split1 = split[i].split("_");
                if (selectedSubjectId.equals(split1[0])) {
                    selectedSubjectName = split1[1];
                }
            }
            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_ID, selectedSubjectId);
            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_EXAMSUBJECT_NAME, selectedSubjectName);
        }
        CommonUtils.putSharedPreferenceItems(null,
                new String[]{UserInfo.KEY_SP_MOBILE, UserInfo.KEY_SP_NICKNAME, UserInfo.KEY_SP_BIRTHDAY, UserInfo.KEY_SP_CITY_ID, UserInfo.KEY_SP_CITY_NAME,
                        UserInfo.KEY_SP_PROVINCE_ID, UserInfo.KEY_SP_PROVINCE_NAME,
                        UserInfo.KEY_SP_EXAMCATEGORY_ID, UserInfo.KEY_SP_EXAMCATEGORY_NAME, UserInfo.KEY_SP_EXAMSTAGE_ID,
                        UserInfo.KEY_SP_EXAMSTAGE_NAME, UserInfo.KEY_SP_SEX, UserInfo.KEY_SP_FACEPATH},
                new String[]{res.getMobile(), res.getNickname(), res.getBirthday(),
                        selectedCityXzqh, CommonUtils.getInstance().getCityNameByXzqh(selectedCityXzqh), selectedProvinceXzqh,
                        CommonUtils.getInstance().getProvinceNameByXzqh(selectedProvinceXzqh), selectedCatetoryId,
                        CommonUtils.getExamCategoryValue(selectedCatetoryId), CommonUtils.getExamStageKey(selectedStageName),
                        selectedStageName, sex, res.getFace()});
        initCurrentSelectedInfo();
    }


    private void saveInfo() {
        ObtainDataFromNetListenerSaveInfo obtainDataFromNetListenerSaveInfo = new ObtainDataFromNetListenerSaveInfo(this);
        SendRequest.saveInformation(nickname.toString(), selectedSexPosition, facePath_local, birthday, obtainDataFromNetListenerSaveInfo);
    }

    private static class ObtainDataFromNetListenerSaveInfo extends ObtainDataFromNetListener<PersonalInfoBean, String> {
        private MeFragment weak_activity;

        public ObtainDataFromNetListenerSaveInfo(MeFragment activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final PersonalInfoBean res) {
            if (weak_activity != null) {
                weak_activity.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.customAlertDialog.dismiss();
                        ToastUtils.showToast(R.string.save_success);
                        if (null != res) {
                            // 有更改数据并且保存成功，否则没更改数据
                            // 有数据时202，首页需要更新数据，否则首页不需要更新
                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_FACEPATH, res.getFace());
                        }
                    }
                });
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            weak_activity.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weak_activity.customAlertDialog = new CustomAlertDialog(weak_activity.getActivity(), R.layout.dialog_loading_custom);
                    weak_activity.customAlertDialog.show();
                    weak_activity.customAlertDialog.setTitle(weak_activity.getResources().getString(R.string.saveing));
                }
            });
        }

        @Override
        public void onFailure(final String res) {
            weak_activity.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weak_activity.customAlertDialog.dismiss();
                    if (res.equals(SendRequest.ERROR_NETWORK)) {
                        ToastUtils.showToast(R.string.network);
                    } else if (res.equals(SendRequest.ERROR_SERVER)) {
//                        ToastUtils.showToast(R.string.save_fail);
                    } else {
                        ToastUtils.showToast(res);
                    }
                }
            });
        }
    }

}
