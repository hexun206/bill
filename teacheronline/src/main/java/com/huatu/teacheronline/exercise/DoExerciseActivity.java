package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gensee.utils.StringUtil;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greendao.DaoUtils;
import com.greendao.ExerciseStore;
import com.greendao.QuestionDetail;
import com.greendao.StudyRecords;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.CCVideo.VideoParseDetailsActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.AsyncHttpClientHelper;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.bean.PaperAttrVo;
import com.huatu.teacheronline.exercise.bean.PaperItem;
import com.huatu.teacheronline.exercise.bean.PaperPo;
import com.huatu.teacheronline.exercise.bean.QuesAttrVo;
import com.huatu.teacheronline.exercise.bean.QuestionPo;
import com.huatu.teacheronline.exercise.bean.erbean.ErPaperAttrVo;
import com.huatu.teacheronline.utils.Arith;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.Anticlockwise;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.CustomWebView;
import com.umeng.analytics.MobclickAgent;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ljzyuhenda on 16/1/20.
 */
public class DoExerciseActivity extends BaseActivity implements CustomWebView.onMoveListener, Chronometer.OnChronometerTickListener {
    protected static int chooseNum = -4;//客观题数量 除了qtype = 4的（注：只有真题才设置，模块题设置为-4）
    private RelativeLayout rl_main_right;
    private TextView tv_main_right;
    private CustomWebView webView;
    private int choiceSum = 4;//选项
    private int caseNum;//本次做题总题数
    private int choiceNumMax = 14;
    private String[] choiceItemArr;
    private boolean isSingleSelect = true;//是否多选
    private ArrayList<ArrayList> selectedChoiceList;// 保存用户每道题选择的答案 填空题 主观题 只保存在第一位的答案和分值
    private int currentNum = 0;//当前题序号
    private String htmlData;
    private SendRequestUtilsForExercise mSendRequestUtilsForExercise;
    private StudyRecords studyRecords;
    private List<QuestionDetail> questionDetails;
    private TextView tv_title;
    private TextView tv_progress;
    private TextView tv_001;
    private TextView tv_002;
    private TextView tv_003;
    private RelativeLayout rl_001;
    private RelativeLayout rl_002;
    private RelativeLayout rl_003;
    private final int requestcode_answercard = 1;
    private final int requestcode_pause = 2;
    private final int requestcode_error = 3;
    public static final int resultCode_pauseAcitivty = 22;
    public static final int resultCode_erroeAcitivty = 33;
    private boolean hasDatas = false;
    private QuestionDetail currentQuestionDetail;
    private View v_icon_001;
    private View v_icon_002;
    private View v_icon_003;
    private CustomAlertDialog mCustomLoadingDialog;
    private CustomAlertDialog mCustomHandinDialog;
    private CustomAlertDialog mCustomGoneDialog;
    private DaoUtils mDaoUtils;
    private ObtainDataListenerForStore mObtainDataListenerForStore;
    private ObtainDataListenerForUnStore mObtainDataListenerForUnStore;
    private final String TAG = "DoExerciseActivity";
    private boolean isStoredForCurrent;
    private double score_right, score_all;//当前做题得分
    private int num_right;//当前作对习题数目
    private Chronometer chronometer;// 计时器
    private int miss = 0;//时间计数
    private ObtainDataListenerForHandinPapers mObtainDataListenerForHandinPapers;//模块题海交卷
    private ObtainDataListenerForHandinErPapers mObtainDataListenerForHandinErPapers;//真题交卷
    private long mId;
    private int mExercisetype;//mExercisetype 1错题解析 2全部解析 0正常做题
    private Map<String, QuesAttrVo> questionAttrMap;//本题的一些数据 易错项 正确率等等
    private TextView tv_004;
    private RelativeLayout rl_004;
    private View v_icon_004;
    private TextView tv_main_title;
    private int mType;//错题 -> 底部三个按钮功能分别为答题卡、解析、垃圾桶 0 今日特训、1 模块题海、2真题估分 3 错题中心、4收藏 5模考大赛 则依次显示为交卷、答题卡、收藏 -1是模块分数页面过来的全部解析或者错题解析  -2是真题分数页面过来的全部解析或者错题解析
    private boolean[] isAnalysisArr;//是否解析
    //    private int[] questionScore;//每题获得的分数 主要针对主观题估分
    private DecimalFormat fnum = new DecimalFormat("##0.0");//小数点后一位
    private int moveNext = 1;//用于判断错题中心划一下显示解析再划一下跳至下一题
    private String qtype;//string的题目类型
    private int newqtype;//题目的类型 5填空 4主观题  其他选择题
    private int Answersize;
    private StringBuffer Answerbuffer;
    private String Splanswers;
    private String type;//0今日特训 1模块题海 2真题模考 3错题中心 4收藏 5 模考大赛
    private LinearLayout newrl_bottom_doexercise;
    private RelativeLayout newrl_001;
    private RelativeLayout newrl_002;
    private PercentRelativeLayout rl_bottom_doexercise;
    private int clo = 1;//时间闪烁参数
    private Anticlockwise re_chronometer;//倒计时控件
    private int reTime = 0;//倒计时时间
    private Timer timer;//时间闪烁线程
    //    private Timer timerOrder;//交卷时间
//    private TimerTask taskOrder;
    Double score = 0.0;//估分
    private AlertDialog builder;//首次进入时间提示
    private ImageView img_pause;//时间暂停图片
    private ImageView image_002;//真题的时候答题卡
    private PaperItem paperItem;//真题的活动信息等等实体类
    private int timeDifference = -1;//真题活动卷时间相差秒数
    private long local_act_end = 0;//本地记录时间（活动剩余时间小于试卷推荐时间）
    public static String ACTION_FINISH = "action_finish_other";
    public static DoExerciseActivity INSTAN;
    public static final String ACTION_HANDIN_PAPERS = "action_handin_papers";
    private boolean isRecord = false;//是否收藏过来的

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
    }

    private WebSettings webSettings;
    private RelativeLayout rl_main_left;
    private String videoId, video_uid, video_key;

    public static DoExerciseActivity getInstance() {
        return INSTAN;
    }

    @Override
    public void initView() {
        INSTAN = this;
        setContentView(R.layout.activity_doexercise);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        rl_main_right.setVisibility(View.VISIBLE);
        tv_main_right = (TextView) findViewById(R.id.tv_main_right);
        tv_main_right.setText(R.string.exercise_error);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        img_pause = (ImageView) findViewById(R.id.img_pause);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        re_chronometer = (Anticlockwise) findViewById(R.id.re_chronometer);
        webView = (CustomWebView) findViewById(R.id.webView);
        tv_001 = (TextView) findViewById(R.id.tv_001);
        tv_002 = (TextView) findViewById(R.id.tv_002);
        tv_003 = (TextView) findViewById(R.id.tv_003);
        tv_004 = (TextView) findViewById(R.id.tv_004);
        rl_001 = (RelativeLayout) findViewById(R.id.rl_001);
        rl_002 = (RelativeLayout) findViewById(R.id.rl_002);
        rl_003 = (RelativeLayout) findViewById(R.id.rl_003);
        rl_004 = (RelativeLayout) findViewById(R.id.rl_004);
        v_icon_001 = findViewById(R.id.v_icon_001);
        v_icon_002 = findViewById(R.id.v_icon_002);
        v_icon_003 = findViewById(R.id.v_icon_003);
        v_icon_004 = findViewById(R.id.v_icon_004);
        image_002 = (ImageView) findViewById(R.id.image_002);
        mCustomLoadingDialog = new CustomAlertDialog(DoExerciseActivity.this, R.layout.dialog_loading_custom);
        mCustomHandinDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
        mCustomGoneDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
        mCustomGoneDialog.setCancelable(false);
        mCustomHandinDialog.setCancelable(false);
        mCustomHandinDialog.setTitle(getString(R.string.handinpapers_confirminfo));
        mCustomGoneDialog.setTitle(getString(R.string.store_delete_gone));
        mExercisetype = getIntent().getIntExtra("exercisetype", 0);
        isRecord = getIntent().getBooleanExtra("isRecord", false);
        mType = getIntent().getIntExtra("type", -1);
        //单独的布局，判断从真题测试中进入时显示出来。
        rl_bottom_doexercise = (PercentRelativeLayout) findViewById(R.id.rl_bottom_doexercise);
        newrl_bottom_doexercise = (LinearLayout) findViewById(R.id.newrl_bottom_doexercise);
        newrl_001 = (RelativeLayout) findViewById(R.id.newrl_001);
        newrl_002 = (RelativeLayout) findViewById(R.id.newrl_002);
        //这边是开始倒计时
//        re_chronometer.initTime(reTime);
//        re_chronometer.start();

        /**
         * mExercisetype 1错题解析 2全部解析 0正常做题
         * 1错题解析 2全部解析  只显示答题卡选项，其余不显示
         */
        switch (mExercisetype) {
            case 1:
                newrl_001.setVisibility(View.GONE);
                rl_001.setVisibility(View.GONE);
                rl_002.setVisibility(View.GONE);
                rl_003.setVisibility(View.GONE);
                rl_004.setVisibility(View.VISIBLE);
                tv_004.setText(R.string.answercard);
                v_icon_004.setBackgroundResource(R.drawable.icon_answercard_unselected);
                tv_main_title.setText(R.string.analysis_error);
                tv_main_title.setVisibility(View.VISIBLE);
                chronometer.setVisibility(View.GONE);
                re_chronometer.setVisibility(View.GONE);
                break;
            case 2:
                newrl_001.setVisibility(View.GONE);
                rl_001.setVisibility(View.GONE);
                rl_002.setVisibility(View.GONE);
                rl_003.setVisibility(View.GONE);
                rl_004.setVisibility(View.VISIBLE);
                tv_004.setText(R.string.answercard);
                v_icon_004.setBackgroundResource(R.drawable.icon_answercard_unselected);
                tv_main_title.setText(R.string.analysis_all);
                tv_main_title.setVisibility(View.VISIBLE);
                chronometer.setVisibility(View.GONE);
                re_chronometer.setVisibility(View.GONE);
                break;
            case 0:
            default:
                rl_001.setVisibility(View.VISIBLE);
                rl_002.setVisibility(View.VISIBLE);
                rl_003.setVisibility(View.VISIBLE);
                rl_004.setVisibility(View.GONE);
                if (mType == 3) {
                    //3错题 -> 底部三个按钮功能分别为答题卡、解析、垃圾桶
                    this.tv_001.setText(R.string.answercard);
                    this.tv_002.setText(R.string.next);
                    this.tv_003.setText(R.string.delete);
                    this.v_icon_001.setBackgroundResource(R.drawable.icon_answercard_unselected);
                    this.v_icon_002.setBackgroundResource(R.drawable.xiayiti_n);
                    this.v_icon_003.setBackgroundResource(R.drawable.icon_delete_unselected);

                    tv_main_title.setText(R.string.examerror_center);
                    tv_main_title.setVisibility(View.VISIBLE);
                    chronometer.setVisibility(View.GONE);
                } else {
                    //0今日特训、1模块题海、2真题测评 -> 底部三个按钮功能分别为 交卷、答题卡、收藏
                    this.tv_001.setText(R.string.hand_in_papers);
                    this.tv_002.setText(R.string.answercard);
                    this.tv_003.setText(R.string.store);
                    if (mType == 2 || mType == 5) {
                        img_pause.setVisibility(View.VISIBLE);
                        chronometer.setVisibility(View.GONE);
                        re_chronometer.setVisibility(View.VISIBLE);
                    } else {
                        img_pause.setVisibility(View.GONE);
                        chronometer.setVisibility(View.VISIBLE);
                        re_chronometer.setVisibility(View.GONE);
                    }
                    this.v_icon_001.setBackgroundResource(R.drawable.icon_handin_papers_unselected);
                    this.v_icon_002.setBackgroundResource(R.drawable.icon_answercard_unselected);
                    this.v_icon_003.setBackgroundResource(R.drawable.icon_store_unselected);

                }

                break;
        }

        AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableInitData(this));
        initSettings();
        //判断是否第一次进入真题 提示时间可以暂停，要有toast提示 注：还需要正常模式做题下
        if (!CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_EVALUATION) && isEvaluation()) {
            showMindTime();
            CommonUtils.putSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_EVALUATION, true);
        }
    }

    private void initDatas() {
        mId = getIntent().getLongExtra("id", -1);
        mDaoUtils = DaoUtils.getInstance();
        mSendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        mSendRequestUtilsForExercise.assignDatas();
        //获取习题数据
        studyRecords = mDaoUtils.queryStudyRecords(mId);
        type = studyRecords.getType();
        if (type.equals("2") || type.equals("5")) {//判断 如果为真题进入的话，重换个底部布局！
            rl_bottom_doexercise.setVisibility(View.GONE);
            newrl_bottom_doexercise.setVisibility(View.VISIBLE);
            paperItem = DataStore_ExamLibrary.paperItem;
//            DebugUtil.e("paperItem:" + paperItem.toString());
        }
        //mExercisetype 1错题解析 2全部解析 0正常做题
        switch (mExercisetype) {
            case 1:
                questionDetails = DataStore_ExamLibrary.wrongExerciseList;
                break;
            case 2:
            case 0:
            default:
                questionDetails = DataStore_ExamLibrary.questionDetailList;
                break;
        }

        if (questionDetails == null || questionDetails.size() == 0) {
            return;
        }
        //表明有题目
        hasDatas = true;
        // 有可能 选中的10道题的qids中根据qid找不到该题,即实际总题数小于记录的exerciseNum
        caseNum = questionDetails.size();

        isAnalysisArr = new boolean[caseNum];
//        questionScore = new int[caseNum];
        choiceItemArr = new String[choiceNumMax];
        for (int index = 0; index < choiceNumMax; index++) {
            choiceItemArr[index] = String.valueOf((char) ('A' + index));
        }
        //获取用户选择项
        //mExercisetype 1错题解析 2全部解析 0正常做题
        switch (mExercisetype) {
            case 1:
                selectedChoiceList = DataStore_ExamLibrary.wrongChoiceList;
                break;
            case 2:
            case 0:
            default:
                selectedChoiceList = DataStore_ExamLibrary.selectedChoiceList;
                break;
        }
        if (selectedChoiceList == null || selectedChoiceList.size() != caseNum) {
            selectedChoiceList = new ArrayList<>();
            // 进行初始化
            for (int index = currentNum; index < caseNum; index++) {
                ArrayList<String> array = new ArrayList<>();
                //String类型
                selectedChoiceList.add(array);
            }
        }
        questionAttrMap = DataStore_ExamLibrary.questionAttrMap;
        /**
         * mExercisetype 1错题解析 2全部解析 0正常做题
         * 1，2从第一道题开始做  0从当期进度开始做
         */
        switch (mExercisetype) {
            case 1:
            case 2:
                currentNum = 0;
                break;
            case 0:
            default:
                if (studyRecords.getCurrentprogress() == null) {
                    currentNum = 0;
                } else {
                    currentNum = Integer.valueOf(studyRecords.getCurrentprogress());
                    if (currentNum >= caseNum) {
                        currentNum = caseNum - 1;
                    }
                }
                break;
        }
        if (studyRecords.getUsedtime() == null) {
            miss = 0;
            if (mType == 2 || mType == 5) {
                reTime = (int) paperItem.ptimelimit * 60;
            }
        } else {
            //如果是真题模考时间需要至为0
            if (mType == 2 || mType == 5) {
                miss = 0;
                reTime = (int) paperItem.ptimelimit * 60;
            } else {
                miss = studyRecords.getUsedtime();
            }
        }
        if (isEvaluation()) {
            if (paperItem.onlyOne == 1 && paperItem.pconstraint == 1) {
                getActivitvTimeDifference();
            }
        }
    }

    private void initHtmlData() {
        tv_progress.setText((currentNum + 1) + "/" + caseNum);
        //主观题底部提示
        String mindExer = getString(R.string.mind_zgth);
        //主要针对错题中心错题答题卡越界
        if (currentNum >= questionDetails.size()) {
            return;
        }
        currentQuestionDetail = questionDetails.get(currentNum);

        deleteOrUpdateQuestions();
        DebugUtil.e("currentQuestionDetail:" + currentQuestionDetail.toString());
        if (StringUtils.isEmpty(currentQuestionDetail.getScore())) {
            score = 0.0;
        } else {
            score = Double.parseDouble(currentQuestionDetail.getScore());
        }

        isStoredForCurrent = mDaoUtils.queryExerciseStore(currentQuestionDetail.getQid(), mSendRequestUtilsForExercise.userId);

        if (mType == 3) {
            //解析
//            if (isAnalysisArr[currentNum]) {
//                v_icon_002.setBackgroundResource(R.drawable.icon_analysis_selected);
//            } else {
//                v_icon_002.setBackgroundResource(R.drawable.icon_analysis_unselected);
//            }
        } else {
            //收藏
            if (isStoredForCurrent) {
                v_icon_003.setBackgroundResource(R.drawable.icon_store_selected);
            } else {
                v_icon_003.setBackgroundResource(R.drawable.icon_store_unselected);
            }
        }
        //这个是联网请求得到的所有选项
        String[] choicesList = currentQuestionDetail.choicesArr;//选项
        if (choicesList == null) {
            choiceSum = 0;
        } else {
            choiceSum = choicesList.length;
        }
        //分4种，选择题js，模块题海主观题js，真题主观题js，查看解析的视频js（注意：只有正常做题才加js，非正常做题只加视频解析）
        StringBuffer jsBody = new StringBuffer();
        // 解析答案集合
        String[] answersList = currentQuestionDetail.answersArr;
        qtype = currentQuestionDetail.getQtype();
        newqtype = Integer.parseInt(qtype);//字符串强转为int类型
        if (newqtype == 5) {//判断如果为填空题则算出“|”出现几次,需要几个输入框
            QuestionDetail questionDetailEve = questionDetails.get(currentNum);
            String[] answers = questionDetailEve.answersArr;//答案“|”有几个 就能得到要几个输入框
            if (answers != null) {
                StringBuffer sbf = new StringBuffer();//获得填空题的字符串
                for (int i = 0; i < answers.length; i++) {
                    sbf.append(answers[i]);
                }
                String s = sbf.toString();
                Answersize = s.length() - s.replace("|", "").length();//得到“|”出现的次数
            }
            Answerbuffer = new StringBuffer();//拼接字符串获取对应的输入框
            if (Answersize != 0) {
                for (int i = 0; i < Answersize; i++) {
                    Answerbuffer.append(Answersize + ",");
                }
            } else {
                Answerbuffer.append("1");
            }

        }
        String choiceType = "";
        if (null != answersList && answersList.length > 1) {//根据答案判断多选题还是单选题
            isSingleSelect = false;
            choiceType = "<font color=\"#ff0000\">[多选题]  </font>";
            //判断是否第一次进入多选题，要有toast提示 注：还需要正常模式做题下
            if (!CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_MULTISELECT) && mExercisetype == 0) {
                ToastUtils.showToastGestures(this);
                CommonUtils.putSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_MULTISELECT, true);
            }
        } else {

            isSingleSelect = true;
            choiceType = String.format(StyleForExerciseActivity.choiceTitle, "[单选题]");
        }

        if (choiceSum == 2) {
            choiceType = String.format(StyleForExerciseActivity.choiceTitle, "[判断题]");
        }
        if (newqtype == 5) {
            choiceType = String.format(StyleForExerciseActivity.choiceTitle, "[填空题]");
        }
        if (newqtype == 4) {
            choiceType = String.format(StyleForExerciseActivity.choiceTitle, "[主观题]");
        }
        //主观题
        if (newqtype == 4) {
            //真题主观题
            if (mType == 2 || mType == 5) {
                jsBody.append(StyleForExerciseActivity.zhuguan_jiexi_Er);//点击查看解析出现解析内容
                mindExer = getString(R.string.mind_zgmk);
            } else {
                //模块题主观题
                jsBody.append(StyleForExerciseActivity.zhuguan_jiexi);//点击查看解析出现解析内容
                mindExer = getString(R.string.mind_zgth);
            }
        } else {
            jsBody.append(StyleForExerciseActivity.jsCode);
        }
        String questions = "";
        String contentQuestions = "";
        questions = currentQuestionDetail.getQuestion().trim();
        //为了防止第一个为p标签
        if (!StringUtils.isEmpty(currentQuestionDetail.getContent())) {
            contentQuestions = currentQuestionDetail.getContent() + questions;
        } else {
            contentQuestions = questions;
        }
        if (contentQuestions.length() > 3 && contentQuestions.substring(0, 3).contains("<p>")) {
            contentQuestions = contentQuestions.substring(3, contentQuestions.length());
        } else {
            contentQuestions = currentQuestionDetail.getContent().trim() + questions;
        }
        // 题目body
        String body = String.format(StyleForExerciseActivity.bodyStyle,
                choiceType + contentQuestions + "</p>");
        /*
         * 1$s 选项  2$s 选项样式 3$s 选项内容
		 */
        StringBuffer choiceBody = new StringBuffer();
        String choiceClassName;
        //private String[] choiceClassname = {"r-cs", "w-cs", "u-choose", ""};
        choiceBody.append(StyleForExerciseActivity.choiceStyleNormal_Prefix);

        if (choiceSum < 1) {
            choiceSum = 4;
            //表明 没有选项 则默认是 4个
            for (int index = 0; index < 13; index++) {
                //A，B，C，D，确定choiceClassName
                if (selectedChoiceList.get(currentNum)
                        .contains(choiceItemArr[index])) {
                    //该选项被选择
                    if (isSingleSelect) {
                        choiceClassName = StyleForExerciseActivity.choiceClassname[7];
                    } else {
                        choiceClassName = StyleForExerciseActivity.choiceClassname[2];
                    }
                } else {
                    //该选项未被选择
                    if (isSingleSelect) {
                        choiceClassName = StyleForExerciseActivity.choiceClassname[4];
                    } else {
                        choiceClassName = StyleForExerciseActivity.choiceClassname[3];
                    }
                }
                //在for循环里面。把选项内容写进去
                choiceBody.append(String.format(StyleForExerciseActivity.choiceStyleNormal,
                        choiceItemArr[index], choiceClassName, "如图所示"));//后面这三个分别跟1S，2S，3S对应
            }
        } else {
            if (newqtype == 4) {//如果为主观题的话，选项就为1个，查看解析！
                choiceSum = 1;
            }
            for (int index = 0; index < choiceSum; index++) {
                if (selectedChoiceList.get(currentNum).
                        contains(choiceItemArr[index])) {
                    //该选项被选择
                    if (isSingleSelect) {
                        choiceClassName = StyleForExerciseActivity.choiceClassname[7];
                    } else {
                        choiceClassName = StyleForExerciseActivity.choiceClassname[2];
                    }
                } else {
                    //该选项未被选择
                    if (isSingleSelect) {
                        choiceClassName = StyleForExerciseActivity.choiceClassname[4];
                    } else {
                        choiceClassName = StyleForExerciseActivity.choiceClassname[3];
                    }
                }
                if (newqtype != 5 && newqtype != 4) {
                    //选择题
                    if (TextUtils.isEmpty(choicesList[index])) {
                        choiceBody.append(String.format(StyleForExerciseActivity.choiceStyleNormal,
                                choiceItemArr[index], choiceClassName, "如图所示"));
                    } else {
                        choiceBody.append(String.format(StyleForExerciseActivity.choiceStyleNormal,
                                choiceItemArr[index], choiceClassName, choicesList[index].replaceAll(StyleForExerciseActivity.regix, "")));
                    }
                } else if (newqtype == 5) {
                    //填空题
                    if (selectedChoiceList.get(currentNum).size() == 0) {
                        choiceBody.append(StyleForExerciseActivity.bodyCompletion);
                    } else {
                        choiceBody.append(String.format(StyleForExerciseActivity.bodyCompletion, selectedChoiceList.get(currentNum)
                                , choicesList[index].replaceAll(StyleForExerciseActivity.regix, "")));

                    }
                } else if (newqtype == 4) {//主观题 解析错题
                    //主观题
//                  choiceBody.append(StyleForExerciseActivity.zgjiexi);
                    if (mType == 2 || mType == 5) {
                        //真题主观题
                        choiceBody.append(String.format(StyleForExerciseActivity.zgjiexi_Er,
                                choicesList[index].replaceAll(StyleForExerciseActivity.regix, "")));
                    } else {
                        //模块主观题
                        choiceBody.append(String.format(StyleForExerciseActivity.zgjiexi,
                                choicesList[index].replaceAll(StyleForExerciseActivity.regix, "")));
                    }

                }
            }
        }
        choiceBody.append(StyleForExerciseActivity.choiceStyleNormal_suffix);

        //mExercisetype 1错题解析 2全部解析 0正常做题
        if (mExercisetype == 0 && !isAnalysisArr[currentNum]) {
            if (newqtype == 5) {
                htmlData = "<body >" + jsBody + StyleForExerciseActivity.newStyle + StyleForExerciseActivity.bodyStyle_prefix + body + choiceBody +
                        StyleForExerciseActivity.bodyStyle_suffix;
            } else if (newqtype == 4) {
                if (mType == 2 || mType == 5) {
                    //真题主观题
//                    String jiexi = String.format(StyleForExerciseActivity.estimate_div,currentQuestionDetail.getComment()+"111",currentQuestionDetail
// .getComment()+"222");
                    htmlData = StyleForExerciseActivity.gfzgHead + "<body>" + jsBody + StyleForExerciseActivity.zgstyle /*+ StyleForExerciseActivity
                    .estimate_style*/ + StyleForExerciseActivity.bodyStyle_prefix + body + choiceBody +
                       /* StyleForExerciseActivity.estimate_analysis+StyleForExerciseActivity.analysis_of_the_estimate+*/ String.format
                            (StyleForExerciseActivity.buzhichi, mindExer) + StyleForExerciseActivity.bodyStyle_suffix;
                } else {
                    //模块主观题
                    htmlData = "<body>" + jsBody + StyleForExerciseActivity.zgstyle /*+ StyleForExerciseActivity.estimate_style*/ + StyleForExerciseActivity
                            .bodyStyle_prefix + body + choiceBody +
                       /* StyleForExerciseActivity.estimate_analysis+StyleForExerciseActivity.analysis_of_the_estimate+*/ String.format
                            (StyleForExerciseActivity.buzhichi, mindExer) + StyleForExerciseActivity.bodyStyle_suffix;

                }
            } else {
                htmlData = "<body>" + jsBody + StyleForExerciseActivity.style + StyleForExerciseActivity.bodyStyle_prefix + body + choiceBody +
                        StyleForExerciseActivity.bodyStyle_suffix;
            }

        } else {//mExercisetype 1错题解析 2全部解析 0正常做题
            //视频解析
            String videoAnalysisBody = String.format(StyleForExerciseActivity.videoAnalysisStyle,
                    "视频");
            // 1$s
            //答案解析
            String answerAnalysisBody = String.format(StyleForExerciseActivity.answerAnalysisStyle,
                    currentQuestionDetail.getComment());

            //用户选线，答案
            String userChoose1 = selectedChoiceList.get(currentNum).toString();
            String userChoose = StringUtils.changHtmlTo(userChoose1);
            ;

            //查看解析
            String htmlTo = StringUtils.decode1(currentQuestionDetail.getAnswers());//替换掉/u003d
            String answer = StringUtils.changHtmlTo(htmlTo);//将＞ ＜这些替换成html能识别的符号
//            String fixStr = StringUtils.getFixStr(answer);
            if (newqtype == 5) {//填空题并且解析的时候获取|替换为，。
                if (mExercisetype == 1 || mExercisetype == 2) {
                    answer = answer.replace('|', ',');//正确答案替换符号
                    Splanswers = userChoose.replace('|', ',');//我输入的答案替换符号
                }
            }
            if (newqtype == 5 && mType == 3) {//判断如果从做题中心进入的话获取|替换为，输入的答案也替换逗号。 3代表错题 5代表填空题
                answer = answer.replace('|', ',');//正确答案替换符号
                String streList = selectedChoiceList.get(currentNum).toString();
                Splanswers = streList.replace('|', ',');//我输入的答案替换符号
            }
            //正确答案是。。。。回答错误 和 全站数据  主观题不用拼接这个
            String answerBody;
//            String String = "个人数据";
            String String0 = "全站数据";
            String String1 = "被作答";
            String String2 = "错误率";
            String String3 = "易错项";
            //1$s正确答案  2$s 选择答案   3$s作答次数  4$准确率

            //正确率 作答次数 这些数据是否有
            if (null != questionAttrMap && questionAttrMap.size() > 0) {
                //全站数据有
                DebugUtil.e("questionAttrMap:1" + questionAttrMap.toString());
                QuesAttrVo questionAttr = questionAttrMap.get(currentQuestionDetail.getQid());
                if (questionAttr == null) {//正确率 作答次数 这些数据是否有
                    //本题全站数据没有
                    if (isCorrect(currentNum)) {
                        //是否正确样式
                        answerBody = String.format(StyleForExerciseActivity.answerStyle_more_right, answer, userChoose,
                                "", "");
                        DebugUtil.e("questionAttrMap:2" + questionAttrMap.toString());
                    } else {
                        answerBody = String.format(StyleForExerciseActivity.answerStyle_more_wrong, answer, userChoose,
                                "", "");
                        DebugUtil.e("questionAttrMap:3" + questionAttrMap.toString());
                    }
                } else {
                    //本题全站数据有
                    DebugUtil.e("questionAttrMap:4" + questionAttrMap.toString());
                    String[] strings = questionAttr.getPrecision().split("%");
                    double wrongParent = 0.0;
                    if (strings.length > 0) {
                        String precision = strings[0];
                        wrongParent = (100 - Float.parseFloat(precision));
                    }
                    if (isCorrect(currentNum)) {//是否正确
                        //是
                        if (newqtype == 5) {
                            answerBody = String.format(StyleForExerciseActivity.answerStyle_more_right, answer, Splanswers,
                                    questionAttr.getAnswercount(), questionAttr.getPrecision());
                            answerBody = answerBody +
                                    StyleForExerciseActivity.table.replace("%1$s", String0).replace("%2$s", String1).replace("%3$s", String2).replace("%4$s",
                                            String3).replace("%5$s", questionAttr.getAnswercount()).replace("%6$s", fnum.format(wrongParent) + "%").replace
                                            ("%7$s", "无");
                        } else {
                            answerBody = String.format(StyleForExerciseActivity.answerStyle_more_right, answer, userChoose,
                                    questionAttr.getAnswercount(), questionAttr.getPrecision());
                            answerBody = answerBody +
                                    StyleForExerciseActivity.table.replace("%1$s", String0).replace("%2$s", String1).replace("%3$s", String2).replace("%4$s",
                                            String3).replace("%5$s", questionAttr.getAnswercount()).replace("%6$s", fnum.format(wrongParent) + "%").replace
                                            ("%7$s", questionAttr.getFallibility());
                        }
                        DebugUtil.e("questionAttrMap:5" + questionAttrMap.toString());
                    } else {
                        //否    主观题必走这边
                        DebugUtil.e("questionAttrMap:6" + questionAttrMap.toString());
                        //这个是表格,判断如果是填空题，把questionAttr.getFallibility() 易错项换成无
                        if (newqtype == 5) {
                            answerBody = String.format(StyleForExerciseActivity.answerStyle_more_wrong, answer, Splanswers,
                                    questionAttr.getAnswercount(), questionAttr.getPrecision());
                            answerBody = answerBody +
                                    StyleForExerciseActivity.table.replace("%1$s", String0).replace("%2$s", String1).replace("%3$s", String2).replace("%4$s",
                                            String3).replace("%5$s", questionAttr.getAnswercount()).replace("%6$s", fnum.format(wrongParent) + "%").replace
                                            ("%7$s", "无");
                        } else {
                            answerBody = String.format(StyleForExerciseActivity.answerStyle_more_wrong, answer, userChoose,
                                    questionAttr.getAnswercount(), questionAttr.getPrecision());
                            answerBody = answerBody +
                                    StyleForExerciseActivity.table.replace("%1$s", String0).replace("%2$s", String1).replace("%3$s", String2).replace("%4$s",
                                            String3).replace("%5$s", questionAttr.getAnswercount()).replace("%6$s", fnum.format(wrongParent) + "%").replace
                                            ("%7$s", questionAttr.getFallibility());
                        }

                    }
                }
            } else {
                //全站数据没有
                DebugUtil.e("questionAttrMap:7");
                if (isCorrect(currentNum)) {
                    DebugUtil.e("questionAttrMap:8");
                    answerBody = String.format(StyleForExerciseActivity.answerStyle_right, answer, userChoose);
                } else {
                    DebugUtil.e("questionAttrMap:9");
                    if (newqtype == 5) {
                        answerBody = String.format(StyleForExerciseActivity.answerStyle_wrong, answer, Splanswers);
                    } else {
                        answerBody = String.format(StyleForExerciseActivity.answerStyle_wrong, answer, userChoose);
                    }
                }
            }
            DebugUtil.e("questionAttrMap:10");
            //是否有视频解析
            if (currentQuestionDetail.getQvideo() == null || "".equals(currentQuestionDetail.getQvideo())) {
                //无视频
                if (newqtype == 5) {//加判断，换样式
                    htmlData = "<body>" + StyleForExerciseActivity.jsCode + StyleForExerciseActivity.newStyle + StyleForExerciseActivity.bodyStyle_prefix +
                            body + choiceBody + answerBody
                            + answerAnalysisBody + StyleForExerciseActivity.bodyStyle_suffix;
                } else if (newqtype == 4) {//answerBody全站数据
                    if (mType == 2 || mType == 5) { //
                        //真题主观题
                        Double integer = 0.0;//分数
                        ArrayList arrayList = selectedChoiceList.get(currentNum);
                        if (arrayList.size() == 0) {
                            integer = 0.0;
                        } else {
                            integer = Double.parseDouble(arrayList.get(0).toString());
                        }

                        String jiexi = String.format(StyleForExerciseActivity.estimate_div, mindExer, score + "", integer + "", currentQuestionDetail
                                .getComment(), currentQuestionDetail.getExtension());
                        htmlData = StyleForExerciseActivity.gfzgHead + "<body >" + jsBody + StyleForExerciseActivity.zgstyle + StyleForExerciseActivity
                                .bodyStyle_prefix +
                                body +//题目
                                choiceBody//查看解析按钮
//                                + answerBody
                                + jiexi
//                                + answerAnalysisBody//答案解析
                                + StyleForExerciseActivity.bodyStyle_suffix + StyleForExerciseActivity.scorllEstimate;
                    } else {
                        //模块题海主观题
                        htmlData = StyleForExerciseActivity.zgstyle + StyleForExerciseActivity.bodyStyle_prefix +
                                body +//题目
                                choiceBody//填空题主观题查看解析按钮 选择题是那些选项
//                           + answerBody 回答正确与否就不拼接进去了
                                + answerAnalysisBody//答案解析
                                + StyleForExerciseActivity.bodyStyle_suffix;
                    }

                } else {
                    htmlData = "<body>" + StyleForExerciseActivity.style + StyleForExerciseActivity.bodyStyle_prefix + body + choiceBody + answerBody
                            + answerAnalysisBody + StyleForExerciseActivity.bodyStyle_suffix;
                }

            } else {
                //有视频
                String videos[] = currentQuestionDetail.getQvideo().split("###");
                videoId = videos[0];
                video_uid = videos[1];
                video_key = videos[2];
//                videoId = "2D408C0785C990489C33DC5901307461";
//                video_uid = "4F21A251DAE61656";
//                video_key = "aLEa1ASWHT3SJEVP4btnirMgdcfOdQU4";
                //拼接H5,加判断，换样式
                if (newqtype == 5) {
                    htmlData = "<body>" + StyleForExerciseActivity.jsCode + StyleForExerciseActivity.jsCode_jiexi + StyleForExerciseActivity.newStyle +
                            StyleForExerciseActivity.bodyStyle_prefix +
                            body + choiceBody + answerBody
                            + videoAnalysisBody + answerAnalysisBody + StyleForExerciseActivity.bodyStyle_suffix;
                } else if (newqtype == 4) {
                    if (mType == 2 || mType == 5) {
                        Double integer = 0.0;//分数
                        if (StringUtils.isEmpty(userChoose)) {
                            integer = 0.0;
                        } else {
                            integer = Double.parseDouble(userChoose);
                        }
                        String jiexi = String.format(StyleForExerciseActivity.estimate_div, mindExer, score + "", integer + "", currentQuestionDetail
                                .getComment(), currentQuestionDetail.getExtension());
                        htmlData = StyleForExerciseActivity.gfzgHead + "<body>" + jsBody + StyleForExerciseActivity.jsCode_jiexi + StyleForExerciseActivity
                                .style + StyleForExerciseActivity.bodyStyle_prefix +
                                body + choiceBody + jiexi/*+ answerBody*/
                                + videoAnalysisBody + StyleForExerciseActivity.bodyStyle_suffix + StyleForExerciseActivity.scorllEstimate;
                    } else {
                        htmlData = "<body>" + StyleForExerciseActivity.jsCode_jiexi + StyleForExerciseActivity.style + StyleForExerciseActivity
                                .bodyStyle_prefix +
                                body + choiceBody /*+ answerBody 主观题不需要回答正确 和 全站数据*/
                                + videoAnalysisBody + answerAnalysisBody + StyleForExerciseActivity.bodyStyle_suffix;
                    }
                } else if (newqtype != 4 && newqtype != 5) {
                    htmlData = "<body>" + StyleForExerciseActivity.jsCode_jiexi + StyleForExerciseActivity.style + StyleForExerciseActivity.bodyStyle_prefix +
                            body + choiceBody + answerBody
                            + videoAnalysisBody + answerAnalysisBody + StyleForExerciseActivity.bodyStyle_suffix;
                }

            }
        }
        String hel = htmlData.toString();
        //加载本地网页 htmlData网页内容
        webView.loadDataWithBaseURL(null, htmlData, StyleForExerciseActivity.mimeType, StyleForExerciseActivity.encoding, null);
        if ((mType == 2 || mType == 5) && newqtype == 4 && htmlData.contains("pfbz")) {
            //判断是否开启了估分界面
            webView.setOnEstimateListenerSwitch(true);
        } else {
            webView.setOnEstimateListenerSwitch(false);
        }
    }

    /**
     * 判断此题是否更新或者删除
     */
    private void deleteOrUpdateQuestions() {
        if (StringUtils.isEmpty(currentQuestionDetail.getStateQuestion()))return;
        if (mType == 3) {
            if (currentQuestionDetail.getStateQuestion().equals("-1")) {
                mCustomGoneDialog.setTitle(getString(R.string.store_delete_gone));
                mCustomGoneDialog.show();
                mCustomGoneDialog.setCancelGone();
                mCustomGoneDialog.setOkText(getString(R.string.delete_gone));
            }
        }
        if (mType == 4) {
            if (currentQuestionDetail.getStateQuestion().equals("-1")) {
                mCustomGoneDialog.setTitle(getString(R.string.store_delete_gone));
                mCustomGoneDialog.show();
                mCustomGoneDialog.setCancelGone();
                mCustomGoneDialog.setOkText(getString(R.string.store_gone));
            }
        }
        if (mType == 1) {
            if (currentQuestionDetail.getStateQuestion().equals("-1")) {
                mCustomGoneDialog.setTitle(getString(R.string.store_delete_gone));
                mCustomGoneDialog.show();
                mCustomGoneDialog.setCancelGone();
                mCustomGoneDialog.setOkText(getString(R.string.iKnow));
                currentQuestionDetail.setStateQuestion("0");
                mDaoUtils.insertOrUpdateQuestionDetail2(currentQuestionDetail);
            }else if (currentQuestionDetail.getStateQuestion().equals("1")){
                mCustomGoneDialog.setTitle(getString(R.string.update_questions));
                mCustomGoneDialog.show();
                mCustomGoneDialog.setCancelGone();
                mCustomGoneDialog.setOkText(getString(R.string.iKnow));
                currentQuestionDetail.setStateQuestion("0");
                mDaoUtils.insertOrUpdateQuestionDetail2(currentQuestionDetail);
            }
        }
    }


    @Override
    public void setListener() {
        webView.registerHandler("onVideoClick", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Intent intent = new Intent(DoExerciseActivity.this, VideoParseDetailsActivity.class);
                intent.putExtra("videoId", videoId);
                intent.putExtra("video_uid", video_uid);
                intent.putExtra("video_key", video_key);
                startActivity(intent);
            }
        });
        webView.registerHandler("parsingClick", new BridgeHandler() {
            @Override
            public void handler(String s, CallBackFunction callBackFunction) {
                //触动网页里面的查看解析，回掉函数
                analysisClick();
            }
        });
        webView.registerHandler("parsingErClick", new BridgeHandler() {
            @Override
            public void handler(String s, CallBackFunction callBackFunction) {
                //触动网页里面的查看解析，回掉函数
                analysisClick();
            }
        });
        //估分滑块数值
        webView.registerHandler("sliderErClick", new BridgeHandler() {
            @Override
            public void handler(String s, CallBackFunction callBackFunction) {
                //触动网页里面的滑块
//                    fenshu = Integer.parseInt(s);
//                questionScore[currentNum] = Integer.parseInt(s);
                //添加到选项
                if (selectedChoiceList.get(currentNum) != null) {
                    selectedChoiceList.get(currentNum).clear();
                    selectedChoiceList.get(currentNum).add(s);
                } else {
                    selectedChoiceList.get(currentNum).add(s);
                }
            }
        });
        //查看解析估分关闭按钮
        webView.registerHandler("clickClose", new BridgeHandler() {
            @Override
            public void handler(String s, CallBackFunction callBackFunction) {
                analysisClick();
            }
        });

        webView.registerHandler("onChoiceClick", new BridgeHandler() {
            //js互掉，答案在data里
            @Override
            public void handler(String data, CallBackFunction function) {
                String currentItem = "A";
                JSONObject jsonRes;
                try {
                    jsonRes = new JSONObject(data);
                    currentItem = jsonRes.getString("param");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int index = 0; index < choiceSum; index++) {
                    if (choiceItemArr[index].equals(currentItem)) {
                        if (isSingleSelect) {
                            selectedChoiceList.get(currentNum).clear();
                            //这个应该是保存
                            selectedChoiceList.get(currentNum).add(choiceItemArr[index]);
                            if (mType == 3) {
                                //错题中心是显示解析的
                                analysisClick();
                            } else {
                                //做题 单选题选完选项后直接跳到下一题，不需手动滑动
                                moveToNext();
                            }
                        } else {
                            if (selectedChoiceList.get(currentNum)
                                    .contains(choiceItemArr[index])) {

                                selectedChoiceList.get(currentNum)
                                        .remove(choiceItemArr[index]);
                            } else {
                                selectedChoiceList.get(currentNum)
                                        .add(choiceItemArr[index]);
                            }
                        }
                        break;
                    }
                }
            }
        });

        webView.setOnMoveListener(this);
        rl_001.setOnClickListener(this);
        rl_002.setOnClickListener(this);
        rl_003.setOnClickListener(this);
        rl_004.setOnClickListener(this);
        mCustomHandinDialog.setOkOnClickListener(this);
        mCustomHandinDialog.setCancelOnClickListener(this);
        chronometer.setOnChronometerTickListener(this);
        //倒计时控件
        re_chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                re_chronometer.setText(FormatMiss(reTime));
                reTime--;
                if (reTime == 9) {
                    //剩余10秒 开始闪烁
                    if (isEvaluation()) {
                        flashingTime();
                    }
                }
            }
        });
        rl_main_left.setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
        newrl_001.setOnClickListener(this);
        newrl_002.setOnClickListener(this);
        chronometer.setOnClickListener(this);
        re_chronometer.setOnClickListener(this);
        img_pause.setOnClickListener(this);
        mCustomGoneDialog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomGoneDialog.dismiss();

                if (3 == mType) {
                    //垃圾桶
                    deleteClick();
                    MobclickAgent.onEvent(DoExerciseActivity.this, "doExweciseDeleteClick");
                } else if ( 4 == mType) {
                    //收藏
                    questionCollection();
                    MobclickAgent.onEvent(DoExerciseActivity.this, "doExweciseCollectiOnClick");
                } else if ( 1 == mType) {
                    //正常做题遇到被下线的

                }
            }
        });
    }

    /**
     * 一开始进来做试卷的时间和活动结束时长对比，大于则执行activityCountdown，小于则不执行 注：活动期内卷强制交卷
     */
    private void getActivitvTimeDifference() {
        SendRequest.getNowTime(new ObtainDataFromNetListener<String, String>() {
            @Override
            public void onSuccess(String res) {
                DebugUtil.e("服务器时间：" + res);
                DebugUtil.e("活动结束时间：" + paperItem.endtime);
//                Date now = new Date();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
//                String hehe = dateFormat.format(now);
//                DebugUtil.e("本地时间：" + hehe);
                //活动剩余时间以秒为单位
                long act_end = StringUtils.twoDateDistanceSeconds(paperItem.endtime, res);
                timeDifference = (int) act_end;
                DebugUtil.e("时间相差秒数" + timeDifference);
                if (timeDifference < 30) {
                    //小于30秒直接交卷
                    handinPapers();
                } else if (timeDifference < paperItem.ptimelimit * 60) {
                    local_act_end = act_end;
//                活动剩余时间小于试卷推荐时间，重新开一个线程按活动剩余时间倒计时
                    activityCountdown();
                }
                //活动剩余时间大于试卷推荐时间则用户自己控制。这边不做操作
            }

            @Override
            public void onFailure(final String res) {
                DoExerciseActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        } else {
                            ToastUtils.showToast(res);
                        }
                    }
                });
            }
        });
    }

    /**
     * 时间闪烁
     */
    private void flashingTime() {
        timer = new Timer();
        TimerTask taskcc = new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (clo == 0) {
                            clo = 1;
                            re_chronometer.setTextColor(Color.TRANSPARENT); // 透明
                        } else {
                            if (clo == 1) {
                                clo = 0;
                                re_chronometer.setTextColor(Color.RED);
                                if (re_chronometer.getText().equals("00:00:00")) {
                                    if (paperItem.onlyOne == 1 && paperItem.pconstraint == 1) {
                                        re_chronometer.stop();
                                        handinPapers();
                                        timer.cancel();
                                    } else {
                                        re_chronometer.setTextColor(Color.BLACK);
                                        re_chronometer.setVisibility(View.GONE);
                                        chronometer.setVisibility(View.VISIBLE);
                                        chronometer.setTextColor(Color.RED);
                                        chronometer.start();
                                        timer.cancel();
                                    }
                                }
                            }
                        }
                    }
                });
            }
        };
        timer.schedule(taskcc, 1, 250);
    }

    /**
     * 活动时间倒计时（注：活动剩余时间小于试卷推荐时间，这个倒计时结束直接交卷）
     */
    private void activityCountdown() {
        //由于timer在锁屏时会暂停因此用闹钟的倒计时
//        timerOrder = new Timer();
//        taskOrder = new TimerTask() {
//            public void run() {
//                DebugUtil.e("活动时间倒计时交卷：" + timeDifference);
//                timeDifference--;
//                if (timeDifference < 12) {
//                        handinPapers();
//                    timerOrder.cancel();
//                }
//            }
//        };
//        timerOrder.schedule(taskOrder, 1, 1000);
        Intent intent = new Intent(DoExerciseActivity.this, Alarmreceiver.class);
        intent.setAction(ACTION_HANDIN_PAPERS);
        PendingIntent sender =
                PendingIntent.getBroadcast(DoExerciseActivity.this, 0, intent, 0);

        //设定一个几秒后的时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, timeDifference - 30);

        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        //或者以下面方式简化
        //alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5*1000, sender);


    }

    /**
     * 是否正确
     *
     * @param currentNum
     * @return
     */
    private boolean isCorrect(int currentNum) {
        QuestionDetail questionDetailEve = questionDetails.get(currentNum);
        String[] answers = questionDetailEve.answersArr;
        ArrayList<String> choices = selectedChoiceList.get(currentNum);// 用户选择的答案
        if (newqtype != 5 && newqtype != 4) {//判断如果不为填空和主观题，选择的答案按字母排序
            Collections.sort(choices);
        }
        if (answers == null || answers.length < 1) {
            //表明没有选项->创建4个选项
            answers = new String[4];
            for (int index = 0; index < 4; index++) {
                answers[index] = String.valueOf((char) ('A' + index));
            }
        }

        if (null != choices && choices.size() > 0) {
            if (answers.length != choices.size()) {
                //答错
                return false;
            } else {
                boolean isRight = true;
                for (int j = 0; j < answers.length; j++) {
                    if (!choices.contains(answers[j])) {
                        isRight = false;
                        break;
                    }
                }

                if (isRight) {
                    //答对
                    return true;
                } else {
                    //答错
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_cancel:
                MobclickAgent.onEvent(this, "doExweciseSubmitClickCancel");
                //取消交卷
                mCustomHandinDialog.dismiss();
                break;
            case R.id.tv_dialog_ok:
                MobclickAgent.onEvent(this, "doExweciseSubmitClickOK");
                mCustomHandinDialog.dismiss();
                if (newqtype == 5) {//交卷的时候如果是填空题，直接添加答案
                    webView.callHandler("getinputcontent", "", new CallBackFunction() {
                        @Override
                        public void onCallBack(String s) {
                            getInputReplace(s);
                        }
                    });
                }
                webView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //确定交卷
                        handinPapers();
                    }
                }, 100);
                break;
            case R.id.newrl_001://真题测试进入的交卷按钮
                if (3 == mType) {
                    //答题卡
                    MobclickAgent.onEvent(this, "doExweciseAnswerCard");
                    answercardClick();
                } else {
                    //交卷
                    MobclickAgent.onEvent(this, "doExweciseSubmitClick");
                    if (newqtype == 5) {
                        getInputContent();
                        tv_main_title.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handinpapersClick();
                            }
                        }, 200);
                    } else {
                        handinpapersClick();
                    }
                }
                break;
            case R.id.rl_001:
                if (3 == mType) {
                    //答题卡
                    MobclickAgent.onEvent(this, "doExweciseAnswerCard");
                    answercardClick();
                } else {
                    //交卷
                    MobclickAgent.onEvent(this, "doExweciseSubmitClick");
                    if (newqtype == 5) {
                        getInputContent();
                        tv_main_title.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handinpapersClick();
                            }
                        }, 200);
                    } else {
                        handinpapersClick();
                    }
                }

                break;
            case R.id.newrl_002://真题测试进入时的答题按钮
                if (3 == mType) {
                    //解析
//                    analysisClick();
                    moveToNext();
                    MobclickAgent.onEvent(this, "doExweciseAnalysisClick");
                } else {
                    //答题卡
                    answercardClick();
                    MobclickAgent.onEvent(this, "doExweciseAnswerCard");
                }
                break;
            case R.id.rl_002:
                if (3 == mType) {
                    //解析
//                    analysisClick();
                    moveToNext();
                    MobclickAgent.onEvent(this, "doExweciseAnalysisClick");
                } else {
                    //答题卡
                    answercardClick();
                    MobclickAgent.onEvent(this, "doExweciseAnswerCard");
                }

                break;
            case R.id.rl_003:
                if (3 == mType) {
                    //垃圾桶
                    deleteClick();
                    MobclickAgent.onEvent(this, "doExweciseDeleteClick");
                } else {
                    //收藏
                    questionCollection();
                    MobclickAgent.onEvent(this, "doExweciseCollectiOnClick");
                }


                break;
            case R.id.rl_004:
                //答题卡
                if (currentQuestionDetail == null) {
                    ToastUtils.showToast("该题目暂未加载出来。。。");
                    return;
                }
                MobclickAgent.onEvent(this, "doExweciseAnswerCard");
                v_icon_004.setBackgroundResource(R.drawable.icon_answercard_selected);
                startAnswerCardActivity();
                break;
            case R.id.rl_main_left:
                back();
                break;
            case R.id.rl_main_right://题目纠错
                MobclickAgent.onEvent(this, "doExweciseErrorCorrectionOnClick");
                ExerciseErrorSubmitActivity.newIntent(this, currentQuestionDetail.getQid(), currentQuestionDetail.getQtype(), currentQuestionDetail
                        .getQuestion(), requestcode_error,mType+"");
                break;
            case R.id.tis_subject://首次进入时间提示
                if (builder != null) {
                    builder.dismiss();
                }
                break;
            case R.id.chronometer://暂停
            case R.id.re_chronometer://暂停
            case R.id.img_pause://暂停
                if (isEvaluation()) {
                    img_pause.setImageResource(R.drawable.test_break);
                    EvaluationPauseActivity.newIntent(this, requestcode_pause);
                }
                break;
        }
    }

    /**
     * 准备题库习题数据 针对模块题海或者今日特训或者错题中心
     *
     * @param studyRecords
     */
    public static void initExerciseData(final StudyRecords studyRecords) {
        final DaoUtils mDaoUtils = DaoUtils.getInstance();
        SendRequestUtilsForExercise mSendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        mSendRequestUtilsForExercise.assignDatas();
        chooseNum = -4;
        final List<QuestionDetail> questionDetails = new ArrayList<>();
        final Gson gson = new Gson();
        final List<String> qids = gson.fromJson(studyRecords.getEids(), ArrayList.class);
        mDaoUtils.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                List<Integer> delExerciseList = new LinkedList<Integer>();
                int content = 0;
                for (String qid : qids) {
                    QuestionDetail questionDetail = mDaoUtils.queryQuestionDetail(qid, studyRecords.getChoosecategory());
                    if (questionDetail == null) {
                        continue;
                    }

                    String[] answersArr = gson.fromJson(questionDetail.getAnswers(), String[].class);
                    questionDetail.answersArr = answersArr;

                    String[] qrootpointArr = gson.fromJson(questionDetail.getQrootpoint(), String[].class);
                    questionDetail.qrootpointArr = qrootpointArr;

                    String[] choicesArr = gson.fromJson(questionDetail.getChoices(), String[].class);
                    questionDetail.choicesArr = choicesArr;

                    questionDetails.add(questionDetail);
                    DebugUtil.e("mDaoUtils.queryQuestionDetail:" + content);
                    if (!StringUtils.isEmpty(questionDetail.getStateQuestion()) && questionDetail.getStateQuestion().contains("-1")) {
                        delExerciseList.add(content);
                    }
                    content++;
                }
                DataStore_ExamLibrary.delExerciseList = delExerciseList;
            }
        });

        DataStore_ExamLibrary.questionDetailList = questionDetails;
    }

    /**
     * 准备题库习题数据 针对真题测评
     *
     * @param studyRecords
     */
    public static void initExerciseDataForExerciseEvaluation(final StudyRecords studyRecords) {
        String eids = CommonUtils.getSharedPreferenceItem(null, studyRecords.getCid(), "").trim();
//        String substring = eids.substring(1, eids.length() - 1);
        //初始化真题的选择题数量
        chooseNum = -4;
        final DaoUtils mDaoUtils = DaoUtils.getInstance();
        SendRequestUtilsForExercise mSendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        mSendRequestUtilsForExercise.assignDatas();

//        //真题根据id查找题目 顺序错乱
//        List<QuestionDetail> questionDetails = mDaoUtils.queryQuestionDetails(studyRecords.getCid());
//        Gson gson = new Gson();
//        for (QuestionDetail questionDetail : questionDetails) {
//            DebugUtil.e("获取数据库的题目："+questionDetail.getQuestion());
//            String[] answersArr = gson.fromJson(questionDetail.getAnswers(), String[].class);
//            questionDetail.answersArr = answersArr;
//
//            String[] qrootpointArr = gson.fromJson(questionDetail.getQrootpoint(), String[].class);
//            questionDetail.qrootpointArr = qrootpointArr;
//
//            String[] choicesArr = gson.fromJson(questionDetail.getChoices(), String[].class);
//            questionDetail.choicesArr = choicesArr;
//        }
//
//        DataStore_ExamLibrary.questionDetailList = questionDetails;

        //真题根据qid查找题目 防止乱序 为了评测报告
        final List<QuestionDetail> questionDetails = new ArrayList<>();
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        final List<String> qids = gson.fromJson(eids, ArrayList.class);
        final Map questionOrderMap = new HashMap<>();
        final List<Integer> objList = new ArrayList<>();//客观题题序
        final List<String> subList = new ArrayList<>(); //主观题题序
        //如果eids为空表示没有题
        if (StringUtils.isEmpty(eids)) {
            return;
        }
        mDaoUtils.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
//                for (String qid : qids) {
//                    QuestionDetail questionDetail = mDaoUtils.queryQuestionDetail(qid, studyRecords.getCid());
//                    if (questionDetail == null) {
//                        continue;
//                    }
                for (int i = 0; i < qids.size(); i++) {
                    String qid = qids.get(i);
                    QuestionDetail questionDetail = mDaoUtils.queryQuestionDetail(qid, studyRecords.getCid());
                    if (questionDetail == null) {
                        continue;
                    }
//                    DebugUtil.e("initExerciseDataForExerciseEvaluation",questionDetail.toString());
                    String[] answersArr = gson.fromJson(questionDetail.getAnswers(), String[].class);
//                    DebugUtil.e("initExerciseDataForExerciseEvaluation","getAnswers : "+ questionDetail.getAnswers());
//                    String decode = URLDecoder.decode(questionDetail.getAnswers());
//                    DebugUtil.e("initExerciseDataForExerciseEvaluation","getAnswers decode: "+ decode);
//                    String fixStr = StringUtils.getFixStr(questionDetail.getAnswers());
//                    DebugUtil.e("initExerciseDataForExerciseEvaluation","getAnswers fixStr: "+ fixStr);
                    questionDetail.answersArr = answersArr;

                    String[] qrootpointArr = gson.fromJson(questionDetail.getQrootpoint(), String[].class);
                    questionDetail.qrootpointArr = qrootpointArr;

                    String[] qpointArr = gson.fromJson(questionDetail.getQpoint(), String[].class);
                    questionDetail.qpointArr = qpointArr;

                    String[] choicesArr = gson.fromJson(questionDetail.getChoices(), String[].class);
                    questionDetail.choicesArr = choicesArr;
//                    if (Integer.parseInt(questionDetail.getQtype()) == 4 && chooseNum == 0) {
//                        chooseNum = i;
//                    }
//                    if (i == qids.size() - 1 && chooseNum == 0) {
//                        chooseNum = i + 1;
//                    }
                    questionDetails.add(questionDetail);
                    questionOrderMap.put(questionDetail.getQid(), i + 1);
                    if (Integer.parseInt(questionDetail.getQtype()) == 4) {
                        subList.add(questionDetail.getQid());
                    } else {
                        objList.add(i + 1);
                    }
//                    DebugUtil.e("questionOrderMap:"+ questionOrderMap.toString());
                }

                //防止第一题为填空题i=0 chooseNum = 0状况
//                if (Integer.parseInt(questionDetails.get(0).getQtype()) == 4) {
//                    chooseNum = chooseNum - 1;
//                }
            }
        });

        DataStore_ExamLibrary.questionDetailList = questionDetails;
        DataStore_ExamLibrary.erPaperOrder = questionOrderMap;
        DataStore_ExamLibrary.objExerciseList = objList;
        DataStore_ExamLibrary.subExerciseList = subList;
    }

    /**
     * 准备本次做题记录的用户所选项
     *
     * @param studyRecords
     */
    public static void initSelectChoiceList(StudyRecords studyRecords) {
        Gson gson = new Gson();
        String choicesforuser = studyRecords.getChoicesforuser();
        if (choicesforuser != null) {
            DataStore_ExamLibrary.selectedChoiceList = gson.fromJson(studyRecords.getChoicesforuser(), ArrayList.class);
        }
    }

    /**
     * 准备本次做题记录的错题情况
     */
    public static void initWrongExerice() {
        //answerFlag 0错误  1正确  -1未答
        List<QuestionDetail> questionDetails = DataStore_ExamLibrary.questionDetailList;
        int caseNum = questionDetails.size();
        ArrayList<ArrayList> selectedChoiceList = DataStore_ExamLibrary.selectedChoiceList;

        ArrayList<QuestionDetail> wrongExerciseList = new ArrayList<>();
        ArrayList<ArrayList> wrongChoiceList = new ArrayList<>();
        String[] answersFlag = new String[caseNum];

        for (int i = 0; i < caseNum; i++) {
            QuestionDetail questionDetailEve = questionDetails.get(i);

            String[] answers = questionDetailEve.answersArr;
            ArrayList<String> choices = selectedChoiceList.get(i);// 用户选择的答案
            if (answers == null || answers.length < 1) {
                //表明没有选项->创建4个选项
                answers = new String[4];
                for (int index = 0; index < 4; index++) {
                    answers[index] = String.valueOf((char) ('A' + index));
                }
            }

            if (null != choices && choices.size() > 0) {
                if (answers.length != choices.size()) {
                    answersFlag[i] = "0";
                    //答错
                    wrongExerciseList.add(questionDetailEve);
                    wrongChoiceList.add(choices);
                    continue;
                } else {
                    boolean isRight = true;
                    for (int j = 0; j < answers.length; j++) {
                        if (!choices.contains(answers[j])) {
                            isRight = false;
                            break;
                        }
                    }

                    if (isRight) {
                        answersFlag[i] = "1";
                    } else {
                        answersFlag[i] = "0";
                        //答错
                        wrongExerciseList.add(questionDetailEve);
                        wrongChoiceList.add(choices);
                    }
                }
            } else {
                answersFlag[i] = "-1";
            }
        }

        DataStore_ExamLibrary.answerFlag = answersFlag;
        DataStore_ExamLibrary.wrongChoiceList = wrongChoiceList;
        DataStore_ExamLibrary.wrongExerciseList = wrongExerciseList;
    }

    /**
     * @param context
     * @param id           studayRecords对应的id
     * @param exercisetype 1错题解析 2全部解析 0正常做题
     * @param type         3错题 -》正常做题时,底部依次显示 答题卡、解析、垃圾桶；
     *                     0 今日特训、1 模块题海、2真题估分、3错题 4收藏 5模考大赛 则依次显示为交卷、答题卡、收藏  模块：-1 错题和全部解析  真题 -2
     */
    public static void newIntent(Activity context, long id, int exercisetype, int type) {
        Intent intent = new Intent(context, DoExerciseActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("exercisetype", exercisetype);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }
    /**
     * @param context
     * @param id           studayRecords对应的id
     * @param exercisetype 1错题解析 2全部解析 0正常做题
     * @param type         3错题 -》正常做题时,底部依次显示 答题卡、解析、垃圾桶；
     *                     0 今日特训、1 模块题海、2真题测评、3错题 4收藏 则依次显示为交卷、答题卡、收藏  模块：-1 错题和全部解析  真题 -2
     */
    public static void newIntentEvaluationRecord(Activity context, long id, int exercisetype, int type) {
        Intent intent = new Intent(context, DoExerciseActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("exercisetype", exercisetype);
        intent.putExtra("type", type);
        intent.putExtra("isRecord", true);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param id           studayRecords对应的id
     * @param exercisetype 1错题解析 2全部解析 0正常做题
     * @param type         3错题 -》正常做题时,底部依次显示 答题卡、解析、垃圾桶；
     *                     0 今日特训、1 模块题海、2真题测评、3错题 4收藏 则依次显示为交卷、答题卡、收藏  模块：-1 错题和全部解析  真题 -2
     */
    public static void newIntentMoudleRecord(Activity context, long id, int exercisetype, int type) {
        Intent intent = new Intent(context, DoExerciseActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("exercisetype", exercisetype);
        intent.putExtra("type", type);
        intent.putExtra("isRecord", true);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param id           studayRecords对应的id
     * @param exercisetype 1错题解析 2全部解析 0正常做题
     */
    public static void newIntent(Activity context, long id, int exercisetype) {
        DoExerciseActivity.newIntent(context, id, exercisetype, -1);
    }

    @Override
    public void movePrivous() {
        moveToPre();
    }

    @Override
    public void moveNext() {
        if (newqtype == 5) {//如果是填空题的话保存答案
            getInputContent();
        }
        webView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((mType == 3 && !isSingleSelect) || (mType == 3 && Integer.parseInt(qtype) == 5)) {
                    if (moveNext % 2 == 1) {
                        moveNext++;
                        analysisClick();
                    } else {
                        moveToNext();
                        moveNext = 1;
                    }
                } else {
                    moveToNext();
                }
            }
        }, 100);
    }

    private void moveToPre() {
        if (newqtype == 5) {
            getInputContent();
        }
        webView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentNum <= 0) {
                    return;
                }
                currentNum--;
                initHtmlData();
                //更新学习记录
                updateStudyRecord(0);
            }
        }, 100);

    }

    /**
     * 填空题获取输入框里面的内容（注：交卷时候最后一题为填空题时需要调取）
     */
    void getInputContent() {
        webView.callHandler("getinputcontent", "", new CallBackFunction() {
            @Override
            public void onCallBack(String s) {
                //需要判断是否为空
                DebugUtil.e("getinputcontent:" + s);
                getInputReplace(s);
            }
        });

    }

    /**
     * 替换填空题相关
     *
     * @param s
     */
    private void getInputReplace(String s) {
        String other = s;
        String choose = other.replace("|", "│");
        String replace = choose.replace("##JSZXSP1##", "|");
        DebugUtil.e("getinputcontent:replace" + replace);
        if (StringUtil.isEmpty(replace)) {
            selectedChoiceList.get(currentNum).clear();
            return;
        }
        if (selectedChoiceList.get(currentNum) != null) {
            selectedChoiceList.get(currentNum).clear();
            selectedChoiceList.get(currentNum).add(replace);
        } else {
            selectedChoiceList.get(currentNum).add(replace);
        }
    }

    private void moveToNext() {
        if (currentNum == caseNum - 1) {
            //最后一题
            initHtmlData();
            ToastUtils.showToast(getResources().getString(R.string.lastexericse));
            return;
        } else {
            currentNum++;
            initHtmlData();
        }

        //更新学习记录
        updateStudyRecord(0);
    }

    /**
     * 用于答题卡的相关标记
     * int orderType = 0;//0 表示做题时打开答题卡，计算答题数  注：1和2针对真题 1表示交卷评测报告显示的对错 设这个字段主要是 评测报告没做的题目也要为红色  2交卷的时候需要传-1
     *
     * @param answersFlag
     */
    private void orderResult(String[] answersFlag, int orderType) {
        //answerFlag 0错误  1正确  -1未答 -2已答
        score_right = 0;//正确分数
        num_right = 0;
        score_all = 0;//总分
        ArrayList<QuestionDetail> wrongExerciseList = new ArrayList<>();
        ArrayList<ArrayList> wrongChoiceList = new ArrayList<>();

        for (int i = 0; i < caseNum; i++) {
            QuestionDetail questionDetailEve = questionDetails.get(i);
            double scoreForcurrentQues;
            //统计总分数
            if (questionDetailEve.getScore() == null) {
                scoreForcurrentQues = 1;
            } else {
                try {
                    scoreForcurrentQues = Double.parseDouble(questionDetailEve.getScore());
                } catch (NumberFormatException e) {
                    scoreForcurrentQues = 1;
                }

//                if (scoreForcurrentQues == 0) {
//                    scoreForcurrentQues = 1;
//                }
            }
//            score_all += scoreForcurrentQues;
            score_all = Arith.add(score_all, scoreForcurrentQues);
            //通过选项判断正确和错误
            String[] answers = questionDetailEve.answersArr;
            ArrayList<String> choices = selectedChoiceList.get(i);// 用户选择的答案
            if (answers == null || answers.length < 1) {
                //表明没有选项->创建4个选项
                answers = new String[4];
                for (int index = 0; index < 4; index++) {
                    answers[index] = String.valueOf((char) ('A' + index));
                }
            }
            if (null != choices && choices.size() > 0) {
                //已做
                if (answers.length != choices.size()) {
                    //如果是 主观题 正常做题 真题
                    if (Integer.parseInt(questionDetailEve.getQtype()) == 4 && (isEvaluation() || mType == 2 || mType == -2)) {
                        //真题主观题估分只有大于0 才为已做
                        if (Double.parseDouble(choices.get(0)) == 0.0) {
                            answersFlag[i] = "-1";
                        } else {
                            num_right++;
//                            score_right += Double.parseDouble(choices.get(0));
                            score_right = Arith.add(score_right, Double.parseDouble(choices.get(0)));
                            answersFlag[i] = "-2";
                        }
                        //针对全部解析 主观题全部为未做
                        if (mType == -2) {
                            answersFlag[i] = "-1";
                        }
                    } else {
                        answersFlag[i] = "0";
                        //答错
                        wrongExerciseList.add(questionDetailEve);
                        wrongChoiceList.add(choices);
                    }

                    continue;
                } else {
                    //有的真题主观题有默认选项，所以这边也得判断
                    if (Integer.parseInt(questionDetailEve.getQtype()) == 4 && (isEvaluation() || mType == 2 || mType == -2)) {
                        //真题主观题估分只有大于0 才为已做
                        if (Double.parseDouble(choices.get(0)) == 0.0) {
                            answersFlag[i] = "-1";
                        } else {
                            num_right++;
//                            score_right += Double.parseDouble(choices.get(0));
                            score_right = Arith.add(score_right, Double.parseDouble(choices.get(0)));
                            answersFlag[i] = "-2";
                        }
                        //针对全部解析 主观题全部为未做
                        if (mType == -2) {
                            answersFlag[i] = "-1";
                        }
                        continue;
                    }
                    boolean isRight = true;
                    for (int j = 0; j < answers.length; j++) {
                        if (!choices.contains(answers[j])) {
                            isRight = false;
                            break;
                        }
                    }

                    if (isRight) {
                        answersFlag[i] = "1";
                        num_right++;
//                        score_right += scoreForcurrentQues;
                        score_right = Arith.add(score_right, scoreForcurrentQues);
                    } else {
                        answersFlag[i] = "0";
                        //答错
                        wrongExerciseList.add(questionDetailEve);
                        wrongChoiceList.add(choices);
                    }
                }
            } else {
                //未做
                answersFlag[i] = "-1";
                if (orderType == 1) {
                    answersFlag[i] = "0";
                    //真题未做算答错
                    if (Integer.parseInt(questionDetailEve.getQtype()) != 4) {
                        wrongExerciseList.add(questionDetailEve);
                        wrongChoiceList.add(choices);
                    }
                    //针对全部解析 主观题全部为未做
                    if (mType == -2 && Integer.parseInt(questionDetailEve.getQtype()) == 4) {
                        answersFlag[i] = "-1";
                    }
                }
                if (orderType == 2) {
                    //真题未做算答错
                    if (Integer.parseInt(questionDetailEve.getQtype()) != 4) {
                        wrongExerciseList.add(questionDetailEve);
                        wrongChoiceList.add(choices);
                    }
                    //针对全部解析 主观题全部为未做
                    if (mType == -2 && Integer.parseInt(questionDetailEve.getQtype()) == 4) {
                        answersFlag[i] = "-1";
                    }
                }
            }
        }

        DataStore_ExamLibrary.answerFlag = answersFlag;
        DataStore_ExamLibrary.wrongChoiceList = wrongChoiceList;
        DataStore_ExamLibrary.wrongExerciseList = wrongExerciseList;
        DataStore_ExamLibrary.selectedChoiceList = selectedChoiceList;

    }

    /**
     * 打开答题卡页面
     */
    private void startAnswerCardActivity() {
        //如果是错题解析不需要重置本套题的相关修改，只需要传多少题错题就行了，questionDetails.size()错题数量
        if (mExercisetype == 1) {
//            AnswerCardActivity.newIntent(this, requestcode_answercard, DataStore_ExamLibrary.answerFlag/*, qrootpointList*/);
            AnswerCardActivity.newIntent(this, requestcode_answercard, DataStore_ExamLibrary.answerFlag, questionDetails.size(),mType,currentNum);
        } else {
            DateFormat dateFormat = new SimpleDateFormat("HH-mm-ss-SSSS");
            DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
            String[] answersFlag = new String[caseNum];
            if (mType == -2) {
                orderResult(answersFlag, 1);
            } else {
                orderResult(answersFlag, 0);
            }
            DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
            // 答题卡不显示答题正确与错误，只有已作答和未作答  -2已作答  -1未作答
            String[] qrootpointList = new String[caseNum];
            for (int i = 0; i < answersFlag.length; i++) {
                QuestionDetail questionDetailEve = questionDetails.get(i);
                if (mExercisetype == 0) {
                    //错题中心过来正常做题 需要显示答案
                    if (mType == 3) {

                    } else {
                        // 正常做题把正确和错误答案分别标识为已作答
                        if (!TextUtils.isEmpty(answersFlag[i]) && ("0".equals(answersFlag[i]) || "1".equals(answersFlag[i]))) {
                            answersFlag[i] = "-2";
                        }
                    }
                }

                if (!TextUtils.isEmpty(questionDetails.get(i).getQrootpoint())) {
                    String[] qrootpoints = questionDetailEve.qrootpointArr;
                    if (null != qrootpoints && qrootpoints.length > 0) {
                        qrootpointList[i] = qrootpoints[0];
                    } else {
                        qrootpointList[i] = "000###综合题";
                    }
                } else {
                    qrootpointList[i] = "000###综合题";
                }
            }

            AnswerCardActivity.newIntent(this, requestcode_answercard, answersFlag,mType,currentNum/*, qrootpointList*/);
            DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //答题卡置为未选中
        if (mType == 3) {
            v_icon_001.setBackgroundResource(R.drawable.icon_answercard_unselected);
        } else {
            v_icon_002.setBackgroundResource(R.drawable.icon_answercard_unselected);
        }
        if (mType == 2 || mType == 5) {
            image_002.setImageResource(R.drawable.icon_answercard_unselected);
        }

        v_icon_004.setBackgroundResource(R.drawable.icon_answercard_unselected);

        switch (resultCode) {
            case AnswerCardActivity.resultCode_doExerciseAcitivty:
                if (data != null) {
                    // 从答题卡返回时的操作，获取到当前是哪个题目
                    currentNum = data.getIntExtra("currentNum", currentNum + 1) - 1;
                    initHtmlData();
                }
                break;
            case resultCode_pauseAcitivty:
                //暂停页面回来
                if (isEvaluation() && DataStore_ExamLibrary.erPaperAttrVo != null) {
                    showLoadingDialogToReportEvaluation();
                }
                break;
            case resultCode_erroeAcitivty:
                //纠错页面回来
                if (isEvaluation() && DataStore_ExamLibrary.erPaperAttrVo != null) {
                    showLoadingDialogToReportEvaluation();
                }
                break;
        }
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        chronometer.setText(FormatMiss(miss));
        miss++;
    }

    public static String FormatMiss(int miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }


    private static class RunnableInitData implements Runnable {
        private DoExerciseActivity doExerciseActivity;

        public RunnableInitData(DoExerciseActivity doExerciseActivity) {
            this.doExerciseActivity = new WeakReference<>(doExerciseActivity).get();
        }

        @Override
        public void run() {
            doExerciseActivity.initDatas();
            if (doExerciseActivity != null) {
                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.tv_title.setText(doExerciseActivity.studyRecords.getName());
                        if (doExerciseActivity.isEvaluation()) {
                            if (doExerciseActivity.chronometer.getVisibility() == View.VISIBLE) {
                                doExerciseActivity.chronometer.start();
                            } else {
                                if (doExerciseActivity.reTime == 0) {
                                    doExerciseActivity.chronometer.setVisibility(View.VISIBLE);
                                    doExerciseActivity.re_chronometer.setVisibility(View.GONE);
                                    doExerciseActivity.chronometer.start();
                                } else {
                                    doExerciseActivity.re_chronometer.initTime(doExerciseActivity.reTime);
                                    doExerciseActivity.re_chronometer.start();
                                }
                            }
                        }
                        if (doExerciseActivity.hasDatas) {
                            doExerciseActivity.initHtmlData();
                        }
                    }
                });
            }
        }
    }

    private static class ObtainDataListenerForStore extends ObtainDataFromNetListener {
        private DoExerciseActivity doExerciseActivity;

        public ObtainDataListenerForStore(DoExerciseActivity doExerciseActivity) {
            this.doExerciseActivity = new WeakReference<>(doExerciseActivity).get();
        }

        @Override
        public void onSuccess(Object res) {
            if (doExerciseActivity != null) {
                String qid = doExerciseActivity.currentQuestionDetail.getQid();
                String useId = doExerciseActivity.mSendRequestUtilsForExercise.userId;
                ExerciseStore exerciseStore = new ExerciseStore(qid, useId);
                if (!doExerciseActivity.mDaoUtils.queryExerciseStore(qid, useId)) {
                    doExerciseActivity.mDaoUtils.insertExerciseStore(exerciseStore);
                }

                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.v_icon_003.setBackgroundResource(R.drawable.icon_store_selected);
                        doExerciseActivity.mCustomLoadingDialog.dismiss();
                    }
                });

            }
        }

        @Override
        public void onFailure(Object res) {
            if (doExerciseActivity != null) {
                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.mCustomLoadingDialog.dismiss();
                        ToastUtils.showToast(doExerciseActivity.getString(R.string.server_error));
                    }
                });

            }
        }

        @Override
        public void onStart() {
            if (doExerciseActivity != null) {
                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.mCustomLoadingDialog.show();
                    }
                });
            }
        }
    }

    private static class ObtainDataListenerForUnStore extends ObtainDataFromNetListener {
        private DoExerciseActivity doExerciseActivity;

        public ObtainDataListenerForUnStore(DoExerciseActivity doExerciseActivity) {
            this.doExerciseActivity = new WeakReference<>(doExerciseActivity).get();
        }

        @Override
        public void onSuccess(Object res) {
            if (doExerciseActivity != null) {
                ExerciseStore exerciseStore = new ExerciseStore(doExerciseActivity.currentQuestionDetail.getQid(), doExerciseActivity
                        .mSendRequestUtilsForExercise.userId);
                doExerciseActivity.mDaoUtils.deleteExerciseStore(exerciseStore);

                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.v_icon_003.setBackgroundResource(R.drawable.icon_store_unselected);
                        doExerciseActivity.mCustomLoadingDialog.dismiss();
                    }
                });
            }
        }

        @Override
        public void onFailure(Object res) {
            if (doExerciseActivity != null) {
                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.mCustomLoadingDialog.dismiss();
                        ToastUtils.showToast(doExerciseActivity.getString(R.string.server_error));
                    }
                });

            }
        }

        @Override
        public void onStart() {
            if (doExerciseActivity != null) {
                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.mCustomLoadingDialog.show();
                    }
                });
            }
        }
    }

    /**
     * 交卷
     */
    protected void handinPapers() {
        if (img_pause.getVisibility() == View.VISIBLE) {
            Intent intent = new Intent();
            intent.setAction(DoExerciseActivity.ACTION_FINISH);
            sendBroadcast(intent);
        }
//        if (timerOrder != null) {
//            timerOrder.cancel();
//        }
        //如果当前题为填空题则需要获取填空题内容
//        if(newqtype == 5){
//            getInputContent();
//        }
        String[] answersFlag = new String[caseNum];
        if (isEvaluation()) {
            orderResult(answersFlag, 2);
        } else {
            orderResult(answersFlag, 0);
        }

        List<QuestionPo> queslist = new ArrayList<>();
        String userId = mSendRequestUtilsForExercise.userId;
        for (int i = 0; i < questionDetails.size(); i++) {
            QuestionDetail questionDetail = questionDetails.get(i);
            QuestionPo questionPo = new QuestionPo();
            questionPo.setUserno(userId);
            questionPo.setQid(questionDetail.getQid());
            questionPo.setQusanswer(questionDetail.answersArr);
            questionPo.setUseranswers(selectedChoiceList.get(i));
            questionPo.setIserror(answersFlag[i]);
//            if (isEvaluation()) {
            questionPo.setQscore(questionDetail.getScore());
            if (answersFlag[i].equals("1")) {
                questionPo.setUserqscore(questionDetail.getScore());
            } else if (answersFlag[i].equals("0") || answersFlag[i].equals("-1")) {
                questionPo.setUserqscore(0 + "");
            }
            if (answersFlag[i].equals("-2") && Integer.parseInt(questionDetail.getQtype()) == 4) {
                Double integer = 0.0;//分数
                ArrayList arrayList = selectedChoiceList.get(i);
                if (arrayList.size() == 0) {
                    integer = 0.0;
                } else {
                    integer = Double.parseDouble(selectedChoiceList.get(i).get(0).toString());
                }
                questionPo.setUserqscore(integer + "");
                questionPo.setIserror("1");
//                    questionPo.setUseranswers(null);
            }
            questionPo.setQscore(questionDetail.getScore());
            if (isEvaluation()) {
                questionPo.setQpoint(questionDetail.qpointArr);
            }
            int qattribute = Integer.parseInt(questionDetail.getQtype());
            if (qattribute == 4) {
                //主观题
                questionPo.setQattribute(1 + "");
            } else {
                //客观题
                questionPo.setQattribute(0 + "");
            }
//            }
            queslist.add(questionPo);
        }
        PaperPo paperPo = new PaperPo();
        paperPo.setUserno(userId);
        if ("2".equals(type) || "5".equals(type)) {
            paperPo.setType("2");
        } else {
            paperPo.setType(type);
        }
        paperPo.setScore(score_right + "");
        DebugUtil.e("paperPo.setScore:" + score_right);
        //0今日特训 1模块题海 2真题测评
        if ("0".equals(type) || "1".equals(type)) {
            paperPo.setPoint(studyRecords.getCid());
        } else {
            paperPo.setPid(studyRecords.getCid());
        }

        if (isEvaluation()) {
            //这边真题 有倒计时的，需要一起加起来
            if (re_chronometer.getVisibility() == View.GONE) {
                //倒计时已过
                String time = chronometer.getText().toString();
                String[] allTime = time.split(":");
                int seconds = Integer.valueOf(allTime[0]) * 3600 + Integer.valueOf(allTime[1]) * 60
                        + Integer.valueOf(allTime[2]);
                paperPo.setSecond(String.valueOf((int) paperItem.ptimelimit * 60 + seconds));
            } else {
                //倒计时没过
                String time = re_chronometer.getText().toString();
                String[] allTime = time.split(":");
                int seconds = 0;
                try {
                    seconds = Integer.valueOf(allTime[0]) * 3600 + Integer.valueOf(allTime[1]) * 60
                            + Integer.valueOf(allTime[2]);
                } catch (NumberFormatException e) {
                    return;
                } catch (ArrayIndexOutOfBoundsException e) {
                    return;
                }
                paperPo.setSecond(String.valueOf((int) paperItem.ptimelimit * 60 - seconds));
            }
            paperPo.setMostsecond(paperItem.ptimelimit * 60 + "");
        } else {
            //没有倒计时 正常做题
            String time = chronometer.getText().toString();
            String[] allTime = time.split(":");
            int seconds = Integer.valueOf(allTime[0]) * 3600 + Integer.valueOf(allTime[1]) * 60
                    + Integer.valueOf(allTime[2]);
            paperPo.setSecond(String.valueOf(seconds));
        }
        paperPo.setQueslist(queslist);

        //更新学习记录
        updateStudyRecord(1);

        if (mObtainDataListenerForHandinPapers == null) {
            mObtainDataListenerForHandinPapers = new ObtainDataListenerForHandinPapers(this);
        }
        if (mObtainDataListenerForHandinErPapers == null) {
            mObtainDataListenerForHandinErPapers = new ObtainDataListenerForHandinErPapers(this);
        }
        mCustomLoadingDialog.setTitle(getString(R.string.handinpapers_ing));
        if (isEvaluation()) {
            //真题交卷
            SendRequest.handinErPapers(paperPo, mObtainDataListenerForHandinErPapers);
        } else {
            SendRequest.handinPapers(paperPo, mObtainDataListenerForHandinPapers);
        }
    }

    private static class ObtainDataListenerForHandinPapers extends ObtainDataFromNetListener<PaperAttrVo, String> {
        private DoExerciseActivity doExerciseActivity;

        public ObtainDataListenerForHandinPapers(DoExerciseActivity doExerciseActivity) {
            this.doExerciseActivity = new WeakReference<>(doExerciseActivity).get();
        }

        @Override
        public void onSuccess(PaperAttrVo res) {
            if (doExerciseActivity != null) {
                //答案解析 按照qid为key，解析内容为value形式组织
                List<QuesAttrVo> qavlistRes = res.getQavList();
                Map questionAttrMap = new HashMap<>();

                for (int i = 0; i < qavlistRes.size(); i++) {
                    QuesAttrVo quesAttrVo = qavlistRes.get(i);
                    questionAttrMap.put(quesAttrVo.getQid(), quesAttrVo);
                    DebugUtil.e("第几题：" + i + quesAttrVo.toString());
                }
                DataStore_ExamLibrary.questionAttrMap = questionAttrMap;

                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.mCustomLoadingDialog.dismiss();
                        doExerciseActivity.finish();
                        ScoreActivity.newIntent(doExerciseActivity, doExerciseActivity.mId);
                    }
                });

            }
        }

        @Override
        public void onFailure(String res) {
            if (doExerciseActivity != null) {
                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.mCustomLoadingDialog.dismiss();
                        doExerciseActivity.finish();
                        ScoreActivity.newIntent(doExerciseActivity, doExerciseActivity.mId);
                    }
                });

            }
        }

        @Override
        public void onStart() {
            if (doExerciseActivity != null) {
                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.mCustomLoadingDialog.show();
                    }
                });
            }
        }
    }

    private static class ObtainDataListenerForHandinErPapers extends ObtainDataFromNetListener<ErPaperAttrVo, String> {
        private DoExerciseActivity doExerciseActivity;

        public ObtainDataListenerForHandinErPapers(DoExerciseActivity doExerciseActivity) {
            this.doExerciseActivity = new WeakReference<>(doExerciseActivity).get();
        }

        @Override
        public void onSuccess(final ErPaperAttrVo res) {
            if (doExerciseActivity != null) {
                //答案解析 按照qid为key，解析内容为value形式组织
//                List<QuesAttrVo> qavlistRes = res.getQavList();
//                Map questionAttrMap = new HashMap<>();
//
//                for (int i = 0; i < qavlistRes.size(); i++) {
//                    QuesAttrVo quesAttrVo = qavlistRes.get(i);
//                    questionAttrMap.put(quesAttrVo.getQid(), quesAttrVo);
//                }
//                DataStore_ExamLibrary.questionAttrMap = questionAttrMap;
                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.mCustomLoadingDialog.dismiss();
                        boolean isTaskTop = CommonUtils.isTaskTop(doExerciseActivity, doExerciseActivity.getLocalClassName());
                        boolean applicationBroughtToBackground = CommonUtils.isApplicationBroughtToBackground(doExerciseActivity);
                        DebugUtil.e("handinErPapers:" + res.toString());
                        DebugUtil.e("isTaskTop:" + isTaskTop);
                        DebugUtil.e("applicationBroughtToBackground:" + applicationBroughtToBackground);
                        DataStore_ExamLibrary.erPaperAttrVo = res;
                        if (isTaskTop || applicationBroughtToBackground) {
                            doExerciseActivity.finish();
                            ReportEvaluationActivity.newIntent(doExerciseActivity, doExerciseActivity.mId);
                        }
                    }
                });


            }
        }

        @Override
        public void onFailure(final String res) {
            if (doExerciseActivity != null) {
                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.mCustomLoadingDialog.dismiss();
//                        doExerciseActivity.finish();
//                        ScoreActivity.newIntent(doExerciseActivity, doExerciseActivity.mId);
                        if (res.equals(SendRequest.ERROR_NETWORK)) {
                            ToastUtils.showToast(R.string.network);
                        } else if (res.equals(SendRequest.ERROR_SERVER)) {
                            ToastUtils.showToast(R.string.server_error);
                        } else {
                            ToastUtils.showToast(res);
                        }
                    }
                });

            }
        }

        @Override
        public void onStart() {
            if (doExerciseActivity != null) {
                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.mCustomLoadingDialog.show();
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
//        chronometer.start();
        if (chronometer.getVisibility() == View.VISIBLE) {
            chronometer.start();
        } else {
            re_chronometer.start();
        }
        webView.resumeTimers();
        if (img_pause.getVisibility() == View.VISIBLE) {
            img_pause.setImageResource(R.drawable.test_do);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (chronometer.getVisibility() == View.VISIBLE) {
            chronometer.stop();
        } else {
            re_chronometer.stop();
        }
        webView.pauseTimers();
//        chronometer.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (chronometer.getVisibility() == View.VISIBLE) {
            chronometer.setBase(SystemClock.elapsedRealtime());
        } else {
            re_chronometer.setBase(SystemClock.elapsedRealtime());
        }
//        if (timerOrder != null) {
//            timerOrder.cancel();
//        }
        INSTAN = null;
        if (mType == 1) {
            processingDeleteExercise();
        }
        super.onDestroy();

    }

    /**
     * 处理已经删除的题目
     */
    private void processingDeleteExercise() {
        if (DataStore_ExamLibrary.delExerciseList == null) {
            return;
        }
        List<Integer> delExerciseList = DataStore_ExamLibrary.delExerciseList;
        if (delExerciseList.size() == 0) {
            return;
        }
        if (delExerciseList.size() == studyRecords.getExercisenum()) {
            //删掉的题为全部做题记录的题
            studyRecords.setLastprogress(studyRecords.getLastprogress() - delExerciseList.size());
            studyRecords.setEids("");
            studyRecords.setChoicesforuser(null);
            studyRecords.setUsedtime(0);
            studyRecords.setCurrentprogress(null);
            studyRecords.setCompleted("1");
            mDaoUtils.updateStudyRecord(studyRecords);
        } else {
            Gson gson = new Gson();
            ArrayList<String> qids = gson.fromJson(studyRecords.getEids(), ArrayList.class);
            ArrayList arrayList = gson.fromJson(studyRecords.getChoicesforuser(), ArrayList.class);

            for (int i = 0; i < delExerciseList.size(); i++) {
                //设置本次记录的题目id 去除删掉的
                int integer = delExerciseList.get(i);
                qids.remove(integer);
                studyRecords.setEids(gson.toJson(qids));
                DebugUtil.e("题目的qids：" + gson.toJson(qids));
                //设置用户选项
                int integer1 = delExerciseList.get(i);
                if (arrayList == null || arrayList.size() == 0) {
                    continue;
                }
                arrayList.remove(integer1);
                DebugUtil.e("用户选项：" + arrayList);
                studyRecords.setChoicesforuser(gson.toJson(arrayList));
            }
//            //设置用户的正确做题数
//            studyRecords.getRightnum();
            //设置最后的位置相对于整个知识点
            studyRecords.setLastprogress(studyRecords.getLastprogress() - delExerciseList.size());
            //设置本10套题做到哪里
            if (Integer.valueOf(studyRecords.getCurrentprogress()) > qids.size()) {
                studyRecords.setCurrentprogress(qids.size() + "");
            }
            studyRecords.setCurrentprogress(Integer.valueOf(studyRecords.getCurrentprogress()) - delExerciseList.size() + "");
            //设置本次记录的题目数
            studyRecords.setExercisenum(studyRecords.getExercisenum() - delExerciseList.size());
            //设置用户选项
            studyRecords.setChoicesforuser(gson.toJson(arrayList));
            //设置本次记录的题目id 去除删掉的
            studyRecords.setEids(gson.toJson(qids, ArrayList.class));
            mDaoUtils.updateStudyRecord(studyRecords);
        }
        Intent intent = new Intent();
        intent.setAction(BaseActivity.ACTION_REFRESH);
        sendBroadcast(intent);
    }

    /**
     * 更新学习记录
     *
     * @param complete 1交卷完成 0未完成
     */
    private void updateStudyRecord(int complete) {
        Gson gson = new Gson();
        if (complete == 1) {
            //已交卷
            if ("2".equals(studyRecords.getType()) || "5".equals(studyRecords.getType())) {
                //如果是真题还需要更新做到哪一题
                studyRecords.setCurrentprogress(String.valueOf(currentNum));
            }
            studyRecords.setCompleted("1");
            studyRecords.setChoicesforuser(gson.toJson(selectedChoiceList));
            studyRecords.setRightnum(num_right);
            if (isEvaluation()) {
                //这边真题 有倒计时的，需要一起加起来
                if (re_chronometer.getVisibility() == View.GONE) {
                    //倒计时已过
                    String time = chronometer.getText().toString();
                    String[] allTime = time.split(":");
                    int seconds = Integer.valueOf(allTime[0]) * 3600 + Integer.valueOf(allTime[1]) * 60
                            + Integer.valueOf(allTime[2]);
                    studyRecords.setUsedtime((int) paperItem.ptimelimit * 60 + seconds - 1);
                } else {
                    //倒计时没过
                    String time = re_chronometer.getText().toString();
                    String[] allTime = time.split(":");
                    int seconds = Integer.valueOf(allTime[0]) * 3600 + Integer.valueOf(allTime[1]) * 60
                            + Integer.valueOf(allTime[2]);
                    studyRecords.setUsedtime(seconds - 1);
                }
            } else {
                studyRecords.setUsedtime(miss - 1);
                if (!isRecord) {
                    //不是做题记录进来的 要插入新的学习记录
                    StudyRecords mStudyRecords = new StudyRecords();
                    mStudyRecords.setChoosecategory(studyRecords.getChoosecategory());//为了记录模块题海已完成的 做题记录
                    mStudyRecords.setCurrentprogress(studyRecords.getCurrentprogress());
                    mStudyRecords.setEids(studyRecords.getEids());
                    mStudyRecords.setLastprogress(studyRecords.getLastprogress());
                    mStudyRecords.setChoicesforuser(studyRecords.getChoicesforuser());
                    mStudyRecords.setCid(studyRecords.getCid());
                    mStudyRecords.setCompleted(studyRecords.getCompleted());
                    mStudyRecords.setDate(studyRecords.getDate());
                    mStudyRecords.setExercisenum(studyRecords.getExercisenum());
                    mStudyRecords.setIsdelete(studyRecords.getIsdelete());
                    mStudyRecords.setName(studyRecords.getName());
                    mStudyRecords.setPtimelimit(studyRecords.getPtimelimit());
                    mStudyRecords.setRightnum(studyRecords.getRightnum());
                    mStudyRecords.setToday(studyRecords.getToday());
                    mStudyRecords.setType(studyRecords.getType());
                    mStudyRecords.setUsedtime(studyRecords.getUsedtime());
                    mStudyRecords.setUserid(studyRecords.getUserid());
                    mDaoUtils.insertStudyRecord(mStudyRecords);
                }
            }
            if (isRecord) {
                //学习记录进来的直接更新学习记录就行
                StudyRecords studyRecordses = mDaoUtils.queryStudyRecordsByType(studyRecords.getType(), studyRecords.getCid(), studyRecords.getChoosecategory(),
                        studyRecords.getUserid());
                if (studyRecordses.getLastprogress() == studyRecords.getLastprogress()) {
                    DebugUtil.e("我是最后一条记录");
                    StudyRecords mStudyRecords = new StudyRecords();
                    mStudyRecords.setChoosecategory(studyRecords.getChoosecategory());//为了记录模块题海已完成的 做题记录
                    mStudyRecords.setCurrentprogress(studyRecords.getCurrentprogress());
                    mStudyRecords.setEids(studyRecords.getEids());
                    mStudyRecords.setLastprogress(studyRecords.getLastprogress());
                    mStudyRecords.setChoicesforuser(studyRecords.getChoicesforuser());
                    mStudyRecords.setCid(studyRecords.getCid());
                    mStudyRecords.setCompleted(studyRecords.getCompleted());
                    mStudyRecords.setDate(studyRecords.getDate());
                    mStudyRecords.setExercisenum(studyRecords.getExercisenum());
                    mStudyRecords.setIsdelete(studyRecords.getIsdelete());
                    mStudyRecords.setName(studyRecords.getName());
                    mStudyRecords.setPtimelimit(studyRecords.getPtimelimit());
                    mStudyRecords.setRightnum(studyRecords.getRightnum());
                    mStudyRecords.setToday(studyRecords.getToday());
                    mStudyRecords.setType(studyRecords.getType());
                    mStudyRecords.setUsedtime(studyRecords.getUsedtime());
                    mStudyRecords.setUserid(studyRecords.getUserid());
                    studyRecords.setIsdelete("1");
                    mDaoUtils.insertStudyRecord(mStudyRecords);
                }
            }else {
                //不是学习记录进入的 需要最新的时间 且不在做题记录显示
                studyRecords.setDate(new Date());
                studyRecords.setIsdelete("1");//做完最新记录不在列表显示
            }
            mDaoUtils.updateStudyRecord(studyRecords);

        } else {
            //未交卷
            if ("2".equals(studyRecords.getType()) || "5".equals(studyRecords.getType())) {
                //如果是真题还需要更新是否完成
                studyRecords.setCompleted("0");
            }
            studyRecords.setDate(new Date());
            studyRecords.setChoicesforuser(gson.toJson(selectedChoiceList));
            studyRecords.setUsedtime(miss - 1);
            studyRecords.setCurrentprogress(String.valueOf(currentNum));
            mDaoUtils.updateStudyRecord(studyRecords);
        }
    }

    private void handinpapersClick() {
        //交卷
        if (currentQuestionDetail == null) {
            ToastUtils.showToast("该题目暂未加载出来。。。");
            return;
        }

        //是否所有题已做答
        boolean hasNoEmpty = true;
        if (null != selectedChoiceList && selectedChoiceList.size() > 0) {
            for (int i = 0; i < selectedChoiceList.size(); i++) {
                List<String> choicesEve = selectedChoiceList.get(i);
                if (null != choicesEve && choicesEve.size() > 0) {
                } else {
                    hasNoEmpty = false;
                    break;
                }
            }
        } else {
            hasNoEmpty = false;
        }

        //都已作答,交卷
        if (hasNoEmpty) {
            handinPapers();
        } else {
            // 还有未答完的题目，提示是否交卷
            mCustomHandinDialog.show();
        }
    }

    private void answercardClick() {
        //答题卡
        if (currentQuestionDetail == null) {
            ToastUtils.showToast("该题目暂未加载出来。。。");
            return;
        }
        if (mType == 3) {
            v_icon_001.setBackgroundResource(R.drawable.icon_answercard_selected);

        } else {
            v_icon_002.setBackgroundResource(R.drawable.icon_answercard_selected);
        }

        if (mType == 2 || mType == 5) {
            image_002.setImageResource(R.drawable.icon_answercard_selected);
        }
        if (img_pause.getVisibility() == View.VISIBLE) {
            img_pause.setImageResource(R.drawable.test_break);
        }

        startAnswerCardActivity();
    }

    private void questionCollection() {
        //收藏习题
        //收藏当前习题
        if (currentQuestionDetail == null) {
            ToastUtils.showToast("该题目暂未加载出来。。。");
            return;
        }
        isStoredForCurrent = mDaoUtils.queryExerciseStore(currentQuestionDetail.getQid(), mSendRequestUtilsForExercise.userId);
        if (isStoredForCurrent) {
            //取消收藏
            //收藏
            mCustomLoadingDialog.setTitle(getString(R.string.unstoring));
            List<String> ids = new ArrayList<>();
            ids.add(currentQuestionDetail.getQid());

            if (mObtainDataListenerForUnStore == null) {
                mObtainDataListenerForUnStore = new ObtainDataListenerForUnStore(this);
            }

            SendRequest.unstoreExercise(ids, mObtainDataListenerForUnStore);
        } else {
            //收藏
            mCustomLoadingDialog.setTitle(getString(R.string.storing));
            List<String> ids = new ArrayList<>();
            ids.add(currentQuestionDetail.getQid());
            if (mObtainDataListenerForStore == null) {
                mObtainDataListenerForStore = new ObtainDataListenerForStore(this);
            }

            SendRequest.storeExercise(ids, mObtainDataListenerForStore);
        }
    }

    private void analysisClick() {
        if (isAnalysisArr[currentNum]) {
            isAnalysisArr[currentNum] = false;
//            v_icon_002.setBackgroundResource(R.drawable.icon_analysis_unselected);
        } else {
            isAnalysisArr[currentNum] = true;
//            v_icon_002.setBackgroundResource(R.drawable.icon_answercard_selected);
        }

        initHtmlData();
    }

    private void deleteClick() {
        List<String> qids = new ArrayList<>();
        qids.add(currentQuestionDetail.getQid());
        SendRequest.deleteWrongQuestions(qids, new ObtainDataListenerForRemoveQues(this));
    }

    private void initSettings() {
//        webView.getSettings().setNeedInitialFocus(false);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new BridgeWebViewClient(webView) {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (newqtype == 5) {//判断为填空题的话获取输入框的数量
                    webView.callHandler("inputfield", Answerbuffer.toString(), new CallBackFunction() {
                        @Override
                        public void onCallBack(String s) {
                        }
                    });

                }
                if (newqtype == 5 && selectedChoiceList.get(currentNum).size() > 0) {//获得已经输入的内容
                    webView.callHandler("setUserInput_id", selectedChoiceList.get(currentNum).get(0).toString(), new CallBackFunction() {
                        @Override
                        public void onCallBack(String s) {

                        }
                    });
                }
                //估分设置分数，如果分数等于0不用设置显示默认的
                if (newqtype == 4 && (mType == 2 || mType == 5) && isAnalysisArr[currentNum]) {
                    selectedChoiceList.get(currentNum);
                    Double integer = 0.0;//分数
                    ArrayList arrayList = selectedChoiceList.get(currentNum);
                    if (arrayList.size() == 0) {
                        integer = 0.0;
                    } else {
                        integer = Double.parseDouble(selectedChoiceList.get(currentNum).get(0).toString());
                    }
                    if (integer != 0) {
                        webView.callHandler("setDrage_id", Double.parseDouble(currentQuestionDetail.getScore()) + "," + integer, new CallBackFunction() {
                            @Override
                            public void onCallBack(String s) {

                            }
                        });
                    }
                    if (mExercisetype == 1 || mExercisetype == 2) {//填空题且解析的时候，禁止输入框输入内容
                        webView.callHandler("setUserInputDisabled_id", "", new CallBackFunction() {
                            @Override
                            public void onCallBack(String s) {
                            }
                        });
                    }
                }
                if (mExercisetype == 1 || mExercisetype == 2) {//填空题且解析的时候，禁止输入框输入内容
                    webView.callHandler("setUserInputDisabled_id", "", new CallBackFunction() {
                        @Override
                        public void onCallBack(String s) {
                        }
                    });
                }
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

//        webView.addJavascriptInterface(new PayJavaScriptInterface(), "js");
//        //自适应显示
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);
    }

    private static class ObtainDataListenerForRemoveQues extends ObtainDataFromNetListener {
        private DoExerciseActivity doExerciseActivity;

        public ObtainDataListenerForRemoveQues(DoExerciseActivity doExerciseActivity) {
            this.doExerciseActivity = new WeakReference<>(doExerciseActivity).get();
        }

        @Override
        public void onSuccess(Object res) {
            if (doExerciseActivity != null) {

                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(R.string.deletewrongques_success);
                        doExerciseActivity.mCustomLoadingDialog.dismiss();
//                        doExerciseActivity.updateStudyRecord(0);
//                        DoExerciseActivity.initExerciseData(doExerciseActivity.studyRecords);
//                        DoExerciseActivity.initSelectChoiceList(doExerciseActivity.studyRecords);
//                        doExerciseActivity.initView();
//                        doExerciseActivity.setListener();
                    }
                });
            }
        }

        @Override
        public void onFailure(Object res) {
            if (doExerciseActivity != null) {
                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.mCustomLoadingDialog.dismiss();
                        ToastUtils.showToast(doExerciseActivity.getString(R.string.server_error));
                    }
                });

            }
        }

        @Override
        public void onStart() {
            if (doExerciseActivity != null) {
                doExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doExerciseActivity.mCustomLoadingDialog.show();
                    }
                });
            }
        }
    }

    /**
     * 进入首页提示
     */
    private void showMindTime() {
        builder = new AlertDialog.Builder(DoExerciseActivity.this, R.style.mindPause).create();//让对话框全屏
        builder.setCancelable(false);
        builder.show();
        View inflate = DoExerciseActivity.this.getLayoutInflater().inflate(R.layout.home_pist, null);
        ImageView img_mindTime = (ImageView) inflate.findViewById(R.id.tis_subject);
        img_mindTime.setImageResource(R.drawable.mkds_pause);
        img_mindTime.setOnClickListener(DoExerciseActivity.this);
        //透明
        Window window = builder.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        Display display = window.getWindowManager().getDefaultDisplay();
        lp.width = (int) (display.getWidth()); //设置宽度
        lp.alpha = 0.999f;
        window.setAttributes(lp);
        builder.setContentView(inflate);
    }

    /**
     * 正常做题情况下 是否真题(包括真题估分和模考大赛)
     *
     * @return
     */
    private boolean isEvaluation() {
        if ((mType == 2 || mType == 5) && mExercisetype == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 展示loadingDialog,并且跳转至评测报告 ：只针对真题
     */
    private void showLoadingDialogToReportEvaluation() {
        mCustomLoadingDialog.setTitle(getString(R.string.handinpapers_ing));
        mCustomLoadingDialog.show();
        final int[] showTime = {3};
        final Timer timerLoading = new Timer();
        TimerTask taskLoading = new TimerTask() {
            public void run() {
                showTime[0]--;
                if (showTime[0] == 0) {
                    mCustomLoadingDialog.dismiss();
                    timerLoading.cancel();
                    finish();
                    ReportEvaluationActivity.newIntent(DoExerciseActivity.this, mId);
                }
            }
        };
        timerLoading.schedule(taskLoading, 1, 1000);
    }

}
