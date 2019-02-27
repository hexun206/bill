package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gensee.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greendao.DaoUtils;
import com.greendao.ExerciseStore;
import com.greendao.QuestionDetail;
import com.greendao.StudyRecords;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.AsyncHttpClientHelper;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.adapter.DoExercisePagerAdapter;
import com.huatu.teacheronline.exercise.adapter.ExerciseViewPagerAdapter;
import com.huatu.teacheronline.exercise.bean.PaperAttrVo;
import com.huatu.teacheronline.exercise.bean.PaperItem;
import com.huatu.teacheronline.exercise.bean.PaperPo;
import com.huatu.teacheronline.exercise.bean.QuesAttrVo;
import com.huatu.teacheronline.exercise.bean.QuestionPo;
import com.huatu.teacheronline.exercise.bean.erbean.ErPaperAttrVo;
import com.huatu.teacheronline.exercise.fragment.AnswerCardFragment;
import com.huatu.teacheronline.utils.Arith;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ShareUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.Anticlockwise;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.ExerciseViewPager;
import com.huatu.teacheronline.widget.PopwindowDoExercise;
import com.umeng.analytics.MobclickAgent;
import com.zzhoujay.richtext.RichText;

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
public class DoVipExerciseActivity extends BaseActivity implements Chronometer.OnChronometerTickListener {
    protected static int chooseNum = -4;//客观题数量 除了qtype = 4的（注：只有真题才设置，模块题设置为-4）
    private boolean isEvaluationError = false; //是否收藏和错题 只针对真题演练和在线模拟
    private boolean isEvaluationCollect = false; //是否收藏和错题 只针对真题演练和在线模拟
    private RelativeLayout rl_main_right;
    private int caseNum;//本次做题总题数
    private int choiceNumMax = 14;
    private String[] choiceItemArr;//每一题用户的选项 ABCD
    private boolean isSingleSelect = true;//是否多选
    public ArrayList<ArrayList> selectedChoiceList;// 保存用户每道题选择的答案 填空题 主观题 只保存在第一位的答案和分值
    private int currentNum = 0;//当前题序号
    private SendRequestUtilsForExercise mSendRequestUtilsForExercise;
    private StudyRecords studyRecords;
    private List<QuestionDetail> questionDetails;
    private TextView tv_title;
    private TextView tv_progress;
    private final int requestcode_answercard = 1;
    private final int requestcode_pause = 2;
    private final int requestcode_error = 3;
    public static final int resultCode_pauseAcitivty = 22;
    public static final int resultCode_erroeAcitivty = 33;
    private QuestionDetail currentQuestionDetail;
    private CustomAlertDialog mCustomLoadingDialog;
    private CustomAlertDialog mCustomHandinDialog;
    private CustomAlertDialog mCustomGoneDialog;
    private CustomAlertDialog mCustomLastDialog;
    private DaoUtils mDaoUtils;
    private ObtainDataListenerForStore mObtainDataListenerForStore;
    private ObtainDataListenerForUnStore mObtainDataListenerForUnStore;
    private final String TAG = "DoVipExerciseActivity";
    private boolean isStoredForCurrent;
    private double score_right, score_all;//当前做题得分
    private int num_right;//当前作对习题数目
    private Chronometer chronometer;// 计时器
    private int miss = 0;//时间计数
    private ObtainDataListenerForHandinPapers mObtainDataListenerForHandinPapers;//模块题海交卷
    private ObtainDataListenerForHandinErPapers mObtainDataListenerForHandinErPapers;//真题交卷
    private long mId;
    private int mExercisetype;//mExercisetype 1错题解析 2全部解析 0正常做题
    public Map<String, QuesAttrVo> questionAttrMap;//本题的一些数据 易错项 正确率等等
    private TextView tv_main_title;
    private int mType;//错题 -> 底部三个按钮功能分别为答题卡、解析、垃圾桶 0 今日特训、1 模块题海、2真题估分 3 错题中心、4收藏 则依次显示为交卷、答题卡、收藏 -1是模块分数页面过来的全部解析或者错题解析 5模考大赛 -2是真题分数页面过来的全部解析或者错题解析
    private boolean[] isAnalysisArr;//是否解析
    private DecimalFormat fnum = new DecimalFormat("##0.0");//小数点后一位
    private int newqtype;//题目的类型 5填空 4主观题  其他选择题
    private String type;//0今日特训 1模块题海 2真题模考 3错题中心 4收藏 5 模考大赛
    private int clo = 1;//时间闪烁参数
    private Anticlockwise re_chronometer;//倒计时控件
    private int reTime = 0;//倒计时时间
    private Timer timer;//时间闪烁线程
    private AlertDialog builder;//首次进入时间提示
    private ImageView img_pause;//时间暂停图片
    private PaperItem paperItem;//真题的活动信息等等实体类
    private int timeDifference = -1;//真题活动卷时间相差秒数
    private long local_act_end = 0;//本地记录时间（活动剩余时间小于试卷推荐时间）
    public static String ACTION_FINISH = "action_finish_other";
    public static DoVipExerciseActivity INSTAN;
    public static final String ACTION_HANDIN_PAPERS = "action_handin_papers";
    private boolean isRecord = false;//是否收藏过来的 //主要是为了做完一套 生成一个做题记录
    public ExerciseViewPager vp_doexercise;
    private List<View> viewPagerItems = new ArrayList<View>();
    private DoExercisePagerAdapter myPagerAdapter;
    private PageScrollListener pageScrollListener;//滚动监听
    public LinearLayoutManager layout;
    private PopwindowDoExercise popwindowDoExercise;//做题页面popwindow
    private ImageView ib_main_right;//右侧更多
    private boolean isChronometer;//是否正计时
    private TextView tv_main_right;//右边计时显示
    private boolean isFlashingTimeOver = false;//闪烁时间已过？表示真题推荐时间过了
    private ImageView img_answer_card;//底部答题卡
    private TextView tv_their_papers;//底部交卷
    private RelativeLayout rl_next_papers;//下一题

    private LinearLayout ll_errorstore_exercise_title;//错题或者收藏的标题
    private ImageView img_card_exercise;//错题和收藏的答题卡
    private ImageView img_del_exercise;//错题和收藏的删除
    private ImageView img_store_exercise;//错题和收藏的收藏
    private ExerciseViewPagerAdapter myViewPagerAdapter;
    private boolean isAnswerCard = false;//是否答题卡 针对时间倒计时闪烁

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
    }

    private RelativeLayout rl_main_left;

    public static DoVipExerciseActivity getInstance() {
        return INSTAN;
    }

    @Override
    public void initView() {
        INSTAN = this;
        setContentView(R.layout.activity_dovipexercise);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
//        ll_time_exercise_title = (LinearLayout) findViewById(R.id.ll_time_exercise_title);
        ll_errorstore_exercise_title = (LinearLayout) findViewById(R.id.ll_errorstore_exercise_title);

        tv_their_papers = (TextView) findViewById(R.id.tv_their_papers);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);

        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        rl_main_right.setVisibility(View.VISIBLE);

        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_right = (TextView) findViewById(R.id.tv_main_right);
        tv_main_right.setTextColor(Color.BLACK);
        tv_main_right.setVisibility(View.GONE);
        img_pause = (ImageView) findViewById(R.id.img_pause);
        ib_main_right = (ImageView) findViewById(R.id.ib_main_right);
        ib_main_right.setImageResource(R.drawable.more_dot);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        re_chronometer = (Anticlockwise) findViewById(R.id.re_chronometer);
        vp_doexercise = (ExerciseViewPager) findViewById(R.id.vp_doexercise);
        mCustomLoadingDialog = new CustomAlertDialog(DoVipExerciseActivity.this, R.layout.dialog_loading_custom);
        mCustomHandinDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
        mCustomGoneDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
        mCustomGoneDialog.setCancelable(false);
        mCustomLastDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
        mCustomHandinDialog.setCancelable(false);
        mCustomHandinDialog.setTitle(getString(R.string.handinpapers_confirminfo));
        mCustomGoneDialog.setTitle(getString(R.string.store_delete_gone));
        mCustomLastDialog.setTitle(getString(R.string.wrongexercise_last));
        mExercisetype = getIntent().getIntExtra("exercisetype", 0);
        isEvaluationError = getIntent().getBooleanExtra("isEvaluationError", false);
        isEvaluationCollect = getIntent().getBooleanExtra("isEvaluationCollect", false);
        isRecord = getIntent().getBooleanExtra("isRecord", false);
        mType = getIntent().getIntExtra("type", -1);
        img_answer_card = (ImageView) findViewById(R.id.img_answer_card);
        rl_next_papers = (RelativeLayout) findViewById(R.id.rl_next_papers);
        img_card_exercise = (ImageView) findViewById(R.id.img_card_exercise);
        img_del_exercise = (ImageView) findViewById(R.id.img_del_exercise);
        img_store_exercise = (ImageView) findViewById(R.id.img_store_exercise);


        /**
         * mExercisetype 1错题解析 2全部解析 0正常做题
         * 1错题解析 2全部解析  只显示答题卡选项，其余不显示
         */
        switch (mExercisetype) {
            case 1:
                ll_errorstore_exercise_title.setVisibility(View.GONE);
                rl_next_papers.setVisibility(View.GONE);
                img_answer_card.setVisibility(View.VISIBLE);

                tv_main_title.setText(R.string.analysis_error);
                tv_main_title.setVisibility(View.VISIBLE);
                break;
            case 2:
                tv_main_title.setText(R.string.analysis_all);
                ll_errorstore_exercise_title.setVisibility(View.GONE);
                tv_main_title.setVisibility(View.VISIBLE);
                break;
            case 0:
            default:
                if (mType == 3 || mType == 4) {
                    ll_errorstore_exercise_title.setVisibility(View.VISIBLE);
                    rl_next_papers.setVisibility(View.GONE);
                    img_answer_card.setVisibility(View.VISIBLE);
                    if (mType == 3) {
                        img_answer_card.setVisibility(View.GONE);
                        rl_next_papers.setVisibility(View.VISIBLE);
                    }
                    if (isEvaluationError) {
                        //如果是试卷的错题
                        tv_their_papers.setVisibility(View.GONE);
                        img_card_exercise.setVisibility(View.VISIBLE);
                        img_store_exercise.setVisibility(View.VISIBLE);
                        rl_next_papers.setVisibility(View.VISIBLE);
                        img_answer_card.setVisibility(View.GONE);
                    }
                    if (isEvaluationCollect) {
                        //如果是试卷的收藏
                        tv_their_papers.setVisibility(View.GONE);
                        img_card_exercise.setVisibility(View.VISIBLE);
                        img_store_exercise.setVisibility(View.GONE);
                        rl_next_papers.setVisibility(View.VISIBLE);
                        img_answer_card.setVisibility(View.GONE);
                    }
                    if (mType == 4 && !isEvaluationCollect){
                        ll_errorstore_exercise_title.setVisibility(View.GONE);
                        tv_main_title.setVisibility(View.VISIBLE);
                        isChronometer = true;
                    }

                } else {
                    ll_errorstore_exercise_title.setVisibility(View.GONE);
                    tv_main_title.setVisibility(View.VISIBLE);
                    if (mType == 2 || mType == 5) {
                        img_pause.setVisibility(View.VISIBLE);
                        isChronometer = false;
                        Drawable rightDrawable = getResources().getDrawable(R.drawable.test_do);
                        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                        tv_main_title.setCompoundDrawables(null, null, rightDrawable, null);
                    } else {
                        img_pause.setVisibility(View.GONE);
                        isChronometer = true;
                    }
                }

                break;
        }

        AsyncHttpClientHelper.createInstance().getThreadPool().execute(new RunnableInitData(this));
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
        if (type.equals("2") || type.equals("5") || isEvaluationCollect || isEvaluationError || mType == -2 ) {//判断 如果为真题进入的话，重换个底部布局！
            paperItem = DataStore_ExamLibrary.paperItem;
            mainRightVisible();
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
        // 有可能 选中的10道题的qids中根据qid找不到该题,即实际总题数小于记录的exerciseNum
        caseNum = questionDetails.size();

        isAnalysisArr = new boolean[caseNum];
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
                        currentNum = caseNum;
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
        if (mType == 3) {
            //为错题中心的时候 自动滑到第一题
            currentNum = 0;
        }
        DebugUtil.e(TAG, "isEvaluationCollect:" + isEvaluationCollect);
        DebugUtil.e(TAG, "isEvaluationError:" + isEvaluationError);
//        new AddViewPagerItemAsyncTask().execute("执行添加viewpager");
        myViewPagerAdapter = new ExerciseViewPagerAdapter(getSupportFragmentManager(), this, questionDetails, mExercisetype, mType,isEvaluationCollect,isEvaluationError);
        vp_doexercise.setAdapter(myViewPagerAdapter);
        currentQuestionDetail = questionDetails.get(currentNum);
        vp_doexercise.setCurrentItem(currentNum);
        DebugUtil.e(TAG, "getCount:" + myViewPagerAdapter.getCount());
        setStore();
    }

    /**
     * 右上角的更多是否显示
     */
    private void mainRightVisible() {
        if (paperItem.onlyOne == 1) {
            //活动卷
            if (paperItem.isactivitie == 1) {
                if (paperItem.open.equals("1")) {
                    ib_main_right.setVisibility(View.VISIBLE);
                }else {
                    ib_main_right.setVisibility(View.GONE);
                }
            }else {
                if (paperItem.openHour == -1) {
                    ib_main_right.setVisibility(View.VISIBLE);
                }else {
                    ib_main_right.setVisibility(View.GONE);
                }
            }
        }else {
            //非活动卷
//            if (paperItem.openHour==-1) {
                ib_main_right.setVisibility(View.VISIBLE);
//            }else {
//                ib_main_right.setVisibility(View.GONE);
//            }
        }
    }


    /**
     * 判断此题是否更新或者删除
     */
    private void deleteOrUpdateQuestions() {
        if (StringUtils.isEmpty(currentQuestionDetail.getStateQuestion())) return;
        if (mType == 3) {
            if (currentQuestionDetail.getStateQuestion().equals("-1")) {
                mCustomGoneDialog.setTitle(getString(R.string.store_delete_gone));
                mCustomGoneDialog.show();
                mCustomGoneDialog.setCancelGone();
                mCustomGoneDialog.setOkText(getString(R.string.delete_gone));
//                deleteClick();
            }
        }
        if (mType == 4) {
            if (currentQuestionDetail.getStateQuestion().equals("-1")) {
                mCustomGoneDialog.setTitle(getString(R.string.store_delete_gone));
                mCustomGoneDialog.show();
                mCustomGoneDialog.setCancelGone();
                mCustomGoneDialog.setOkText(getString(R.string.store_gone));
//                questionCollection();
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
            } else if (currentQuestionDetail.getStateQuestion().equals("1")) {
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
        img_answer_card.setOnClickListener(this);
        tv_their_papers.setOnClickListener(this);
        rl_next_papers.setOnClickListener(this);

        img_card_exercise.setOnClickListener(this);
        img_del_exercise.setOnClickListener(this);
        img_store_exercise.setOnClickListener(this);
        tv_main_title.setOnClickListener(this);

        popwindowDoExercise = new PopwindowDoExercise(this, this);

        mCustomHandinDialog.setOkOnClickListener(this);
        mCustomHandinDialog.setCancelOnClickListener(this);
        chronometer.setOnChronometerTickListener(this);
        //倒计时控件
        re_chronometer.setOnChronometerTickListener(OnChronometerTickListener);
        rl_main_left.setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
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
                    MobclickAgent.onEvent(DoVipExerciseActivity.this, "doExweciseDeleteClick");
                } else if (4 == mType) {
                    //收藏
                    questionCollection();
                    MobclickAgent.onEvent(DoVipExerciseActivity.this, "doExweciseCollectiOnClick");
                } else if (1 == mType) {
                    //正常做题遇到被下线的

                }
            }
        });
        mCustomLastDialog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomLastDialog.dismiss();
                finish();
                Intent intent = new Intent();
                intent.setAction(ExercisePaperErrorCollectionActivity.ACTION_REPETITION_GROUP);
                sendBroadcast(intent);
            }
        });

        vp_doexercise.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                DebugUtil.e(TAG, "onPageScrolled" + " position:" + position + " positionOffset:" + positionOffset + " positionOffsetPixels:" +
                        positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                DebugUtil.e("onPageSelected"+"isEvaluationError"+isEvaluationError);
                DebugUtil.e("onPageSelected"+"isEvaluationCollect"+isEvaluationCollect);
                if (isDoExercise()&&(!myViewPagerAdapter.isEvaluationError&&!myViewPagerAdapter.isEvaluationCollect)) {
                    if (position == questionDetails.size()) {
                        isAnswerCard = true;
//                        myPagerAdapter.reFreshAnswerCard();
                        AnswerCardFragment item = (AnswerCardFragment) myViewPagerAdapter.getItem(position);
                        item.reFreshAnswerCard();
//                    ll_time_exercise_title.setVisibility(View.GONE);
                        tv_main_title.setText(R.string.answercard);
                        ib_main_right.setVisibility(View.GONE);
                        tv_main_right.setVisibility(View.VISIBLE);
                        img_answer_card.setVisibility(View.GONE);
                        tv_main_title.setCompoundDrawables(null, null, null, null);
//                        if (isEvaluation()) {
//                            img_pause.setVisibility(View.VISIBLE);
//                        }
                        tv_their_papers.setVisibility(View.VISIBLE);
                        return;
                    } else {
                        isAnswerCard = false;
                        if (isEvaluation()) {
                            mainRightVisible();
                            Drawable rightDrawable = getResources().getDrawable(R.drawable.test_do);
                            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                            tv_main_title.setCompoundDrawables(null, null, rightDrawable, null);
                        }else if(mType == 1 || mType == 4){
                            ib_main_right.setVisibility(View.VISIBLE);
                        }
                        tv_main_right.setVisibility(View.GONE);
//                    ll_time_exercise_title.setVisibility(View.VISIBLE);
                        tv_main_title.setVisibility(View.VISIBLE);
                        img_answer_card.setVisibility(View.VISIBLE);
                        tv_their_papers.setVisibility(View.GONE);
                        img_pause.setVisibility(View.GONE);
                    }
                } else if (position == questionDetails.size()) {
                    return;
                }
                isStoredForCurrent = mDaoUtils.queryExerciseStore(currentQuestionDetail.getQid(), mSendRequestUtilsForExercise.userId);
                CommonUtils.closeInputMethod(DoVipExerciseActivity.this);
                currentNum = position;
                tv_progress.setText((currentNum + 1) + "/" + caseNum);
                DebugUtil.e(TAG, "onPageSelected" + " position:" + position);
                currentQuestionDetail = questionDetails.get(position);
                newqtype = Integer.parseInt(currentQuestionDetail.getQtype());
                //更新学习记录
                updateStudyRecord(0);
//                if(mExercisetype == 0 && mType == 3){
//                    vp_doexercise.setScrollable(false,currentNum);
//                }
                setStore();

                deleteOrUpdateQuestions();
            }

            /**
             * @param state 0：什么都没做 1：开始滑动 2：滑动结束
             */
            @Override
            public void onPageScrollStateChanged(int state) {
//                DebugUtil.e(TAG, "onPageScrollStateChanged" + " state:" + state);
                if (pageScrollListener != null) {
                    pageScrollListener.onPageScrollStateChangedListener(state, currentNum);
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
//                DebugUtil.e("服务器时间：" + res);
//                DebugUtil.e("活动结束时间：" + paperItem.endtime);
//                Date now = new Date();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
//                String hehe = dateFormat.format(now);
//                DebugUtil.e("本地时间：" + hehe);
                //活动剩余时间以秒为单位
                long act_end = StringUtils.twoDateDistanceSeconds(paperItem.endtime, res);
                timeDifference = (int) act_end;
//                DebugUtil.e("时间相差秒数" + timeDifference);
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
                DoVipExerciseActivity.this.runOnUiThread(new Runnable() {
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
     * 设置收藏
     */
    private void setStore() {
        if (!isDoExercise()) {
            //收藏
            isStoredForCurrent = mDaoUtils.queryExerciseStore(currentQuestionDetail.getQid(), mSendRequestUtilsForExercise.userId);
            if (isStoredForCurrent) {
                img_store_exercise.setImageResource(R.drawable.ic_store_exercise_pressed);
            } else {
                img_store_exercise.setImageResource(R.drawable.ic_store_exercise);
            }
        }
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
//                            re_chronometer.setTextColor(Color.TRANSPARENT); // 透明
                            if (isAnswerCard) {
                                tv_main_right.setTextColor(Color.TRANSPARENT);
                            }else {
                                tv_main_title.setTextColor(Color.TRANSPARENT);
                            }
//                            if (tv_main_right.getVisibility() == View.VISIBLE) {
//                                tv_main_right.setTextColor(Color.TRANSPARENT);
//                            } else {
//                                tv_main_title.setTextColor(Color.TRANSPARENT);
//                            }
                        } else {
                            if (clo == 1) {
                                clo = 0;
//                                re_chronometer.setTextColor(Color.RED);
                                String time = "";
                                if (isAnswerCard) {
                                    tv_main_right.setTextColor(Color.RED);
                                    tv_main_title.setTextColor(Color.BLACK);
                                    time = tv_main_right.getText().toString();
                                }else {
                                    tv_main_title.setTextColor(Color.RED);
                                    tv_main_right.setTextColor(Color.BLACK);
                                    time = tv_main_title.getText().toString();
                                }
//                                if (tv_main_right.getVisibility() == View.VISIBLE) {
//                                    tv_main_right.setTextColor(Color.RED);
//                                    time = tv_main_right.getText().toString();
//                                } else {
//                                    tv_main_title.setTextColor(Color.RED);
//                                    time = tv_main_title.getText().toString();
//                                }
                                String s = re_chronometer.getText().toString();
                                boolean equals = CommonUtils.FormatSends(re_chronometer.getText().toString().split(":")).equals(String.valueOf(paperItem
                                        .ptimelimit * 60));
                                String s1 = CommonUtils.FormatSends(re_chronometer.getText().toString().split(":"));
                                String s2 = String.valueOf(paperItem.ptimelimit * 60);
                                if (equals) {
                                    if (paperItem.onlyOne == 1 && paperItem.pconstraint == 1) {
                                        re_chronometer.stop();
                                        handinPapers();
                                        timer.cancel();
                                    } else {
                                        if (isAnswerCard) {
                                            tv_main_right.setVisibility(View.VISIBLE);
                                            tv_main_title.setVisibility(View.VISIBLE);
                                            tv_main_title.setText(R.string.answercard);
                                            tv_main_title.setTextColor(Color.BLACK);
                                            tv_main_right.setTextColor(Color.BLACK);
                                        }else {
                                            tv_main_right.setVisibility(View.GONE);
                                            tv_main_title.setVisibility(View.VISIBLE);
                                            ib_main_right.setVisibility(View.VISIBLE);
                                            tv_main_title.setTextColor(Color.BLACK);
                                        }
                                        isChronometer = true;
                                        isFlashingTimeOver = true;
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
        Intent intent = new Intent(DoVipExerciseActivity.this, Alarmreceiver.class);
        intent.setAction(ACTION_HANDIN_PAPERS);
        PendingIntent sender =
                PendingIntent.getBroadcast(DoVipExerciseActivity.this, 0, intent, 0);

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
    public boolean isCorrect(int currentNum) {
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
                tv_main_title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handinPapers();
                    }
                },500);
                break;
//            case R.id.newrl_001://真题测试进入的交卷按钮
//                if (3 == mType) {
//                    //答题卡
//                    MobclickAgent.onEvent(this, "doExweciseAnswerCard");
//                    answercardClick();
//                } else {
//                    //交卷
//                    MobclickAgent.onEvent(this, "doExweciseSubmitClick");
//                    if (newqtype == 5) {
//                        getInputContent();
//                        tv_main_title.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                handinpapersClick();
//                            }
//                        }, 200);
//                    } else {
//                        handinpapersClick();
//                    }
//                }
//                break;
//            case R.id.rl_001:
//                if (3 == mType) {
//                    //答题卡
//                    MobclickAgent.onEvent(this, "doExweciseAnswerCard");
//                    answercardClick();
//                } else {
//                    //交卷
//                    MobclickAgent.onEvent(this, "doExweciseSubmitClick");
//                    if (newqtype == 5) {
//                        getInputContent();
//                        tv_main_title.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                handinpapersClick();
//                            }
//                        }, 200);
//                    } else {
//                        handinpapersClick();
//                    }
//                }
//
//                break;
//            case R.id.newrl_002://真题测试进入时的答题按钮
//                if (3 == mType) {
//                    //解析
////                    analysisClick();
//                    moveToNext();
//                    MobclickAgent.onEvent(this, "doExweciseAnalysisClick");
//                } else {
//                    //答题卡
//                    answercardClick();
//                    MobclickAgent.onEvent(this, "doExweciseAnswerCard");
//                }
//                break;
//            case R.id.rl_002:
//                if (3 == mType) {
//                    //解析
////                    analysisClick();
//                    moveToNext();
//                    MobclickAgent.onEvent(this, "doExweciseAnalysisClick");
//                } else {
//                    //答题卡
//                    answercardClick();
//                    MobclickAgent.onEvent(this, "doExweciseAnswerCard");
//                }
//
//                break;
//            case R.id.rl_003:
//                if (3 == mType) {
//                    //垃圾桶
//                    deleteClick();
//                    MobclickAgent.onEvent(this, "doExweciseDeleteClick");
//                } else {
//                    //收藏
//                    questionCollection();
//                    MobclickAgent.onEvent(this, "doExweciseCollectiOnClick");
//                }
//
//
//                break;
//            case R.id.rl_004:
//                //答题卡
//                if (currentQuestionDetail == null) {
//                    ToastUtils.showToast("该题目暂未加载出来。。。");
//                    return;
//                }
//                MobclickAgent.onEvent(this, "doExweciseAnswerCard");
//                v_icon_004.setBackgroundResource(R.drawable.icon_answercard_selected);
//                startAnswerCardActivity();
//                break;
            case R.id.img_answer_card://答题卡
                if (currentQuestionDetail == null) {
                    ToastUtils.showToast("该题目暂未加载出来。。。");
                    return;
                }
                MobclickAgent.onEvent(this, "doExweciseAnswerCard");
                startAnswerCardActivity();
                break;
            case R.id.tv_their_papers://交卷查看结果
                //交卷
                MobclickAgent.onEvent(this, "doExweciseSubmitClick");
                handinpapersClick();
                break;
            case R.id.rl_next_papers://错题收藏下一题
                //错题收藏下一题
                moveToNext();
                break;
            case R.id.rl_main_left:
                back();
                break;
            case R.id.rl_main_right://更多
                if (tv_main_right.getVisibility() == View.VISIBLE) return;
                if (isEvaluationCollect || isEvaluationError || mType == 3) {
                //针对试卷的收藏和错题
                    popwindowDoExercise.setData(5);
                }else {
                    if (mDaoUtils.queryExerciseStore(currentQuestionDetail.getQid(), mSendRequestUtilsForExercise.userId)) {
                        popwindowDoExercise.setData(2);
                    } else {
                        popwindowDoExercise.setData(1);
                    }
                }
                popwindowDoExercise.show(rl_main_right);
                break;
            case R.id.tis_subject://首次进入时间提示
                if (builder != null) {
                    builder.dismiss();
                }
                break;
            case R.id.chronometer://暂停
            case R.id.re_chronometer://暂停
            case R.id.img_pause://暂停
            case R.id.tv_main_title://暂停
                if (isEvaluation()) {
                    img_pause.setImageResource(R.drawable.test_break);
                    EvaluationPauseActivity.newIntent(this, requestcode_pause);
                }
                break;
            case R.id.tv_collection_exercise:
                TextView tc = (TextView) v;
                popwindowDoExercise.dismiss();
                if ("纠错本题".equals(tc.getText())) {
                    MobclickAgent.onEvent(this, "doExweciseErrorCorrectionOnClick");
                    ExerciseErrorSubmitActivity.newIntent(DoVipExerciseActivity.this, currentQuestionDetail.getQid(), currentQuestionDetail.getQtype(),
                            currentQuestionDetail
                                    .getQuestion(), requestcode_error, mType + "");
                }else {
                    MobclickAgent.onEvent(this, "doExweciseCollectiOnClick");
                    questionCollection();
                }
                break;
            case R.id.tv_correction_exercise:
                popwindowDoExercise.dismiss();
                MobclickAgent.onEvent(this, "doExweciseErrorCorrectionOnClick");
//                ExerciseErrorSubmitActivity.newIntent(this, currentQuestionDetail.getQid(), currentQuestionDetail.getQtype(), currentQuestionDetail
//                        .getQuestion(), requestcode_error);
                ExerciseErrorSubmitActivity.newIntent(DoVipExerciseActivity.this, currentQuestionDetail.getQid(), currentQuestionDetail.getQtype(),
                        currentQuestionDetail
                        .getQuestion(), requestcode_error, mType + "");
                break;
            case R.id.tv_share_exercise:
                popwindowDoExercise.dismiss();
                shareUrl();

                break;
            case R.id.img_card_exercise:
                if (currentQuestionDetail == null) {
                    ToastUtils.showToast("该题目暂未加载出来。。。");
                    return;
                }
                MobclickAgent.onEvent(this, "doExweciseAnswerCard");
                startAnswerCardActivity();
                break;
            case R.id.img_del_exercise:
                MobclickAgent.onEvent(this, "doExweciseDeleteClick");
                deleteClick();
                break;
            case R.id.img_store_exercise:
                MobclickAgent.onEvent(this, "doExweciseCollectiOnClick");
                questionCollection();
                break;
        }
    }

    /**
     * 分享题目
     */
    private void shareUrl() {
        SendRequestUtilsForExercise sendRequestUtilsForExercise = SendRequestUtilsForExercise.getInstance();
        sendRequestUtilsForExercise.assignDatas();
        if (mType == 1) {
            ShareUtils.popShare(this, ShareUtils.url_share_exercise + "?qid=" + currentQuestionDetail.getQid() + "&examSubject=" +
                    sendRequestUtilsForExercise.subject + "&area=" + sendRequestUtilsForExercise.area + "&examType=" + sendRequestUtilsForExercise.examType +
                    "&stage=" + sendRequestUtilsForExercise.stage, ShareUtils.content_share_module, ShareUtils.title_share, false);
        }else if(mType == 2){
            ShareUtils.popShare(this, ShareUtils.url_share_exercise+"?qid="+currentQuestionDetail.getQid()+"&examSubject="+sendRequestUtilsForExercise.subject+"&area="+sendRequestUtilsForExercise.area+"&examType="+sendRequestUtilsForExercise.examType+"&stage="+sendRequestUtilsForExercise.stage+"&pid="+paperItem.pid, ShareUtils.content_share_evaluation, ShareUtils.title_share, false);
        }else if(mType == 3){
            if (myViewPagerAdapter.isEvaluationError) {
                ShareUtils.popShare(this, ShareUtils.url_share_exercise+"?qid="+currentQuestionDetail.getQid()+"&examSubject="+sendRequestUtilsForExercise.subject+"&area="+sendRequestUtilsForExercise.area+"&examType="+sendRequestUtilsForExercise.examType+"&stage="+sendRequestUtilsForExercise.stage+"&pid="+paperItem.pid, ShareUtils.content_share_test, ShareUtils.title_share, false);
            }else {
                ShareUtils.popShare(this, ShareUtils.url_share_exercise+"?qid="+currentQuestionDetail.getQid()+"&examSubject="+sendRequestUtilsForExercise.subject+"&area="+sendRequestUtilsForExercise.area+"&examType="+sendRequestUtilsForExercise.examType+"&stage="+sendRequestUtilsForExercise.stage, ShareUtils.content_share_module, ShareUtils.title_share, false);
            }
        }else if(mType == 4){
            if (myViewPagerAdapter.isEvaluationCollect) {
                ShareUtils.popShare(this, ShareUtils.url_share_exercise+"?qid="+currentQuestionDetail.getQid()+"&examSubject="+sendRequestUtilsForExercise.subject+"&area="+sendRequestUtilsForExercise.area+"&examType="+sendRequestUtilsForExercise.examType+"&stage="+sendRequestUtilsForExercise.stage+"&pid="+paperItem.pid, ShareUtils.content_share_evaluation, ShareUtils.title_share, false);
            }else {
                ShareUtils.popShare(this, ShareUtils.url_share_exercise+"?qid="+currentQuestionDetail.getQid()+"&examSubject="+sendRequestUtilsForExercise.subject+"&area="+sendRequestUtilsForExercise.area+"&examType="+sendRequestUtilsForExercise.examType+"&stage="+sendRequestUtilsForExercise.stage, ShareUtils.content_share_module, ShareUtils.title_share, false);
            }
        }else if(mType == 5){
            ShareUtils.popShare(this, ShareUtils.url_share_exercise+"?qid="+currentQuestionDetail.getQid()+"&examSubject="+sendRequestUtilsForExercise.subject+"&area="+sendRequestUtilsForExercise.area+"&examType="+sendRequestUtilsForExercise.examType+"&stage="+sendRequestUtilsForExercise.stage+"&pid="+paperItem.pid, ShareUtils.content_share_test, ShareUtils.title_share, false);
        }else {
            ShareUtils.popShare(this, ShareUtils.url_share_exercise+"?qid="+currentQuestionDetail.getQid()+"&examSubject="+sendRequestUtilsForExercise.subject+"&area="+sendRequestUtilsForExercise.area+"&examType="+sendRequestUtilsForExercise.examType+"&stage="+sendRequestUtilsForExercise.stage, ShareUtils.content_share_module, ShareUtils.title_share, false);
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
        String eids = "";
        if (studyRecords.getType().equals("3")) {
            //试卷的错题
            eids = CommonUtils.getSharedPreferenceItem(null, studyRecords.getCid()+studyRecords.getType(), "").trim();
        }else if (studyRecords.getType().equals("4")){
            //试卷的收藏
            eids = CommonUtils.getSharedPreferenceItem(null, studyRecords.getCid()+studyRecords.getType(), "").trim();
        }else {
            eids = CommonUtils.getSharedPreferenceItem(null, studyRecords.getCid(), "").trim();
        }

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
                for (int i = 0; i < qids.size(); i++) {
                    String qid = qids.get(i);
                    QuestionDetail questionDetail = mDaoUtils.queryQuestionDetail(qid, studyRecords.getCid());
                    if (questionDetail == null) {
                        continue;
                    }
                    String[] answersArr = gson.fromJson(questionDetail.getAnswers(), String[].class);
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
     *                     0 今日特训、1 模块题海、2真题测评、3错题 4收藏 则依次显示为交卷、答题卡、收藏  模块：-1 错题和全部解析  真题： -2 错题和全部解析
     */
    public static void newIntent(Activity context, long id, int exercisetype, int type) {
        Intent intent = new Intent(context, DoVipExerciseActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("exercisetype", exercisetype);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }
    public static void newIntent(Activity context, long id, int exercisetype, int type,boolean isEvaluationError,boolean isEvaluationCollect ) {
        Intent intent = new Intent(context, DoVipExerciseActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("exercisetype", exercisetype);
        intent.putExtra("type", type);
        intent.putExtra("isEvaluationError", isEvaluationError);
        intent.putExtra("isEvaluationCollect", isEvaluationCollect);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param id           studayRecords对应的id
     * @param exercisetype 1错题解析 2全部解析 0正常做题
     * @param type         3错题 -》正常做题时,底部依次显示 答题卡、解析、垃圾桶；
     *                     0 今日特训、1 模块题海、2真题测评、3错题 4收藏 则依次显示为交卷、答题卡、收藏  模块：-1 错题和全部解析  真题： -2
     */
    public static void newIntentEvaluationRecord(Activity context, long id, int exercisetype, int type) {
        Intent intent = new Intent(context, DoVipExerciseActivity.class);
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
        Intent intent = new Intent(context, DoVipExerciseActivity.class);
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
        DoVipExerciseActivity.newIntent(context, id, exercisetype, -1);
    }

    private void moveToNext() {
        if (currentNum == caseNum - 1) {
            //最后一题
//            initHtmlData();
            if (mType == 3 || isEvaluationError || isEvaluationCollect) {
                //错题中心和试卷的收藏 错题 都要弹出来
                mCustomLastDialog.show();
                mCustomLastDialog.setCancelGone();
            } else {
                ToastUtils.showToast(getResources().getString(R.string.lastexericse));
            }
            return;
        } else {
            currentNum++;
//            initHtmlData();
            vp_doexercise.setCurrentItem(currentNum);
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
    public void orderResult(String[] answersFlag, int orderType) {
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
            AnswerCardActivity.newIntent(this, requestcode_answercard, DataStore_ExamLibrary.answerFlag, questionDetails.size(), mType, currentNum);
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
                    //错题中心过来正常做题 需要显示答案  试卷收藏也要显示对错
                    if (mType == 3 || isEvaluationCollect) {
                        DebugUtil.e("答题卡错题显示");
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
            if (isEvaluationCollect) {
                AnswerCardActivity.newIntent(this, requestcode_answercard, answersFlag, 0, -3, currentNum);
            }else {
                AnswerCardActivity.newIntent(this, requestcode_answercard, answersFlag, 0, mType, currentNum);
            }
            DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
        }
    }

    /**
     * 滚动到答题卡页面调用
     */
    public String[] startAnswerCardPager() {
        //如果是错题解析不需要重置本套题的相关修改，只需要传多少题错题就行了，questionDetails.size()错题数量
        if (mExercisetype == 1) {
//            AnswerCardActivity.newIntent(this, requestcode_answercard, DataStore_ExamLibrary.answerFlag/*, qrootpointList*/);
//            AnswerCardActivity.newIntent(this, requestcode_answercard, DataStore_ExamLibrary.answerFlag, questionDetails.size());
            return DataStore_ExamLibrary.answerFlag;
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
//            AnswerCardActivity.newIntent(this, requestcode_answercard, answersFlag/*, qrootpointList*/);
            DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
            return answersFlag;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case AnswerCardActivity.resultCode_doExerciseAcitivtyTheirPapers:
                handinpapersClick();
                break;
            case AnswerCardActivity.resultCode_doExerciseAcitivty:
                if (data != null) {
                    // 从答题卡返回时的操作，获取到当前是哪个题目
                    currentNum = data.getIntExtra("currentNum", currentNum + 1) - 1;
//                    initHtmlData();
                    vp_doexercise.setCurrentItem(currentNum);
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
//        chronometer.setText(FormatMiss(miss));
        if (isChronometer && isDoExercise()) {
            if (tv_main_right.getVisibility() == View.VISIBLE) {
                tv_main_right.setText(CommonUtils.FormatMiss(miss));
            } else {
                tv_main_title.setText(CommonUtils.FormatMiss(miss));
            }

        }
        miss++;
    }

    Chronometer.OnChronometerTickListener OnChronometerTickListener = new Chronometer.OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
//            re_chronometer.setText(FormatMiss(reTime));
            if (!isChronometer && isDoExercise()) {
                if (tv_main_right.getVisibility() == View.VISIBLE) {
                    tv_main_right.setText(CommonUtils.FormatMiss(reTime));
                } else {
                    tv_main_title.setText(CommonUtils.FormatMiss(reTime));
                }
            }
            reTime--;
            if (reTime == 9) {
                //剩余10秒 开始闪烁
                if (isEvaluation()) {
                    flashingTime();
                }
            }
        }
    };

    /**
     * 是否正常做题
     */
    public boolean isDoExercise() {
        if (mType == 1 || mType == 2 || mType == 5 || mType == 4) {
            return true;
        } else {
            return false;
        }
    }


    private static class RunnableInitData implements Runnable {
        private DoVipExerciseActivity doVipExerciseActivity;

        public RunnableInitData(DoVipExerciseActivity doVipExerciseActivity) {
            this.doVipExerciseActivity = new WeakReference<>(doVipExerciseActivity).get();
        }

        @Override
        public void run() {
            if (doVipExerciseActivity != null) {
                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doVipExerciseActivity.initDatas();
                        doVipExerciseActivity.tv_title.setText(doVipExerciseActivity.studyRecords.getName());
                        doVipExerciseActivity.tv_progress.setText((doVipExerciseActivity.currentNum + 1) + "/" + doVipExerciseActivity.caseNum);
                    }
                });
            }
        }
    }

    private static class ObtainDataListenerForStore extends ObtainDataFromNetListener {
        private DoVipExerciseActivity doVipExerciseActivity;

        public ObtainDataListenerForStore(DoVipExerciseActivity doVipExerciseActivity) {
            this.doVipExerciseActivity = new WeakReference<>(doVipExerciseActivity).get();
        }

        @Override
        public void onSuccess(Object res) {
            if (doVipExerciseActivity != null) {
                String qid = doVipExerciseActivity.currentQuestionDetail.getQid();
                String useId = doVipExerciseActivity.mSendRequestUtilsForExercise.userId;
                ExerciseStore exerciseStore = new ExerciseStore(qid, useId);
                if (!doVipExerciseActivity.mDaoUtils.queryExerciseStore(qid, useId)) {
                    doVipExerciseActivity.mDaoUtils.insertExerciseStore(exerciseStore);
                }

                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToastExercise(doVipExerciseActivity, "收藏成功");
                        doVipExerciseActivity.img_store_exercise.setImageResource(R.drawable.ic_store_exercise_pressed);
//                        doVipExerciseActivity.v_icon_003.setBackgroundResource(R.drawable.icon_store_selected);
                        doVipExerciseActivity.mCustomLoadingDialog.dismiss();
                    }
                });

            }
        }

        @Override
        public void onFailure(Object res) {
            if (doVipExerciseActivity != null) {
                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doVipExerciseActivity.mCustomLoadingDialog.dismiss();
                        ToastUtils.showToast(doVipExerciseActivity.getString(R.string.server_error));
                    }
                });

            }
        }

        @Override
        public void onStart() {
            if (doVipExerciseActivity != null) {
                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doVipExerciseActivity.mCustomLoadingDialog.show();
                    }
                });
            }
        }
    }

    private static class ObtainDataListenerForUnStore extends ObtainDataFromNetListener {
        private DoVipExerciseActivity doVipExerciseActivity;

        public ObtainDataListenerForUnStore(DoVipExerciseActivity doVipExerciseActivity) {
            this.doVipExerciseActivity = new WeakReference<>(doVipExerciseActivity).get();
        }

        @Override
        public void onSuccess(Object res) {
            if (doVipExerciseActivity != null) {
                ExerciseStore exerciseStore = new ExerciseStore(doVipExerciseActivity.currentQuestionDetail.getQid(), doVipExerciseActivity
                        .mSendRequestUtilsForExercise.userId);
                doVipExerciseActivity.mDaoUtils.deleteExerciseStore(exerciseStore);

                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToastExercise(doVipExerciseActivity, "取消成功");
                        doVipExerciseActivity.img_store_exercise.setImageResource(R.drawable.ic_store_exercise);
//                        doVipExerciseActivity.v_icon_003.setBackgroundResource(R.drawable.icon_store_unselected);
                        doVipExerciseActivity.mCustomLoadingDialog.dismiss();
                    }
                });
            }
        }

        @Override
        public void onFailure(Object res) {
            if (doVipExerciseActivity != null) {
                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doVipExerciseActivity.mCustomLoadingDialog.dismiss();
                        ToastUtils.showToast(doVipExerciseActivity.getString(R.string.server_error));
                    }
                });

            }
        }

        @Override
        public void onStart() {
            if (doVipExerciseActivity != null) {
                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doVipExerciseActivity.mCustomLoadingDialog.show();
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
            intent.setAction(DoVipExerciseActivity.ACTION_FINISH);
            sendBroadcast(intent);
        }
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
            if (isFlashingTimeOver) {
                //倒计时已过
                String time = "";
                if (tv_main_right.getVisibility() == View.VISIBLE) {
                    time = tv_main_right.getText().toString();
                } else {
                    time = tv_main_title.getText().toString();
                }

                String[] allTime = time.split(":");
                int seconds = Integer.valueOf(allTime[0]) * 3600 + Integer.valueOf(allTime[1]) * 60
                        + Integer.valueOf(allTime[2]);
                paperPo.setSecond(String.valueOf((int) paperItem.ptimelimit * 60 + seconds));
            } else {
                //倒计时没过
                String time = "";
                if (tv_main_right.getVisibility() == View.VISIBLE) {
                    time = tv_main_right.getText().toString();
                } else {
                    time = tv_main_title.getText().toString();
                }

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
            String time = "";
            if (tv_main_right.getVisibility() == View.VISIBLE) {
                time = tv_main_right.getText().toString();
            } else {
                time = tv_main_title.getText().toString();
            }
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
        private DoVipExerciseActivity doVipExerciseActivity;

        public ObtainDataListenerForHandinPapers(DoVipExerciseActivity doVipExerciseActivity) {
            this.doVipExerciseActivity = new WeakReference<>(doVipExerciseActivity).get();
        }

        @Override
        public void onSuccess(PaperAttrVo res) {
            if (doVipExerciseActivity != null) {
                //答案解析 按照qid为key，解析内容为value形式组织
                List<QuesAttrVo> qavlistRes = res.getQavList();
                Map questionAttrMap = new HashMap<>();

                for (int i = 0; i < qavlistRes.size(); i++) {
                    QuesAttrVo quesAttrVo = qavlistRes.get(i);
                    questionAttrMap.put(quesAttrVo.getQid(), quesAttrVo);
                    DebugUtil.e("第几题：" + i + quesAttrVo.toString());
                }
                DataStore_ExamLibrary.questionAttrMap = questionAttrMap;

                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doVipExerciseActivity.mCustomLoadingDialog.dismiss();
                        doVipExerciseActivity.finish();
                        ScoreActivity.newIntent(doVipExerciseActivity, doVipExerciseActivity.mId);
                    }
                });

            }
        }

        @Override
        public void onFailure(String res) {
            if (doVipExerciseActivity != null) {
                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doVipExerciseActivity.mCustomLoadingDialog.dismiss();
                        doVipExerciseActivity.finish();
                        ScoreActivity.newIntent(doVipExerciseActivity, doVipExerciseActivity.mId);
                    }
                });

            }
        }

        @Override
        public void onStart() {
            if (doVipExerciseActivity != null) {
                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doVipExerciseActivity.mCustomLoadingDialog.show();
                    }
                });
            }
        }
    }

    private static class ObtainDataListenerForHandinErPapers extends ObtainDataFromNetListener<ErPaperAttrVo, String> {
        private DoVipExerciseActivity doVipExerciseActivity;

        public ObtainDataListenerForHandinErPapers(DoVipExerciseActivity doVipExerciseActivity) {
            this.doVipExerciseActivity = new WeakReference<>(doVipExerciseActivity).get();
        }

        @Override
        public void onSuccess(final ErPaperAttrVo res) {
            if (doVipExerciseActivity != null) {
                //答案解析 按照qid为key，解析内容为value形式组织
//                List<QuesAttrVo> qavlistRes = res.getQavList();
//                Map questionAttrMap = new HashMap<>();
//
//                for (int i = 0; i < qavlistRes.size(); i++) {
//                    QuesAttrVo quesAttrVo = qavlistRes.get(i);
//                    questionAttrMap.put(quesAttrVo.getQid(), quesAttrVo);
//                }
//                DataStore_ExamLibrary.questionAttrMap = questionAttrMap;
                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doVipExerciseActivity.mCustomLoadingDialog.dismiss();
                        boolean isTaskTop = CommonUtils.isTaskTop(doVipExerciseActivity, doVipExerciseActivity.getLocalClassName());
                        boolean applicationBroughtToBackground = CommonUtils.isApplicationBroughtToBackground(doVipExerciseActivity);
                        DebugUtil.e("handinErPapers:" + res.toString());
                        DebugUtil.e("isTaskTop:" + isTaskTop);
                        DebugUtil.e("applicationBroughtToBackground:" + applicationBroughtToBackground);
                        DataStore_ExamLibrary.erPaperAttrVo = res;
                        if (isTaskTop || applicationBroughtToBackground) {
                            doVipExerciseActivity.finish();
                            ReportEvaluationActivity.newIntent(doVipExerciseActivity, doVipExerciseActivity.mId,doVipExerciseActivity.isRecord);
                        }
                    }
                });


            }
        }

        @Override
        public void onFailure(final String res) {
            if (doVipExerciseActivity != null) {
                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doVipExerciseActivity.mCustomLoadingDialog.dismiss();
//                        doVipExerciseActivity.finish();
//                        ScoreActivity.newIntent(doVipExerciseActivity, doVipExerciseActivity.mId);
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
            if (doVipExerciseActivity != null) {
                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doVipExerciseActivity.mCustomLoadingDialog.show();
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
//        chronometer.start();
        if (isChronometer) {
            chronometer.start();
        } else {
            re_chronometer.start();
        }
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
        INSTAN = null;
        if (mType == 1) {
            processingDeleteExercise();
        }
        myViewPagerAdapter = null;
        super.onDestroy();
        System.gc();
        RichText.recycle();
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
    public void updateStudyRecord(int complete) {
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
                if (isFlashingTimeOver) {
                    //倒计时已过
                    String time = "";
                    if (tv_main_right.getVisibility() == View.VISIBLE) {
                        time = tv_main_right.getText().toString();
                    } else {
                        time = tv_main_title.getText().toString();
                    }
                    String[] allTime = time.split(":");
                    int seconds = Integer.valueOf(allTime[0]) * 3600 + Integer.valueOf(allTime[1]) * 60
                            + Integer.valueOf(allTime[2]);
                    studyRecords.setUsedtime((int) paperItem.ptimelimit * 60 + seconds - 1);
                } else {
                    //倒计时没过
                    String time = "";
                    if (tv_main_right.getVisibility() == View.VISIBLE) {
                        time = tv_main_right.getText().toString();
                    } else {
                        time = tv_main_title.getText().toString();
                    }
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
                    mStudyRecords.setCid(studyRecords.getCid() + new Date());
                    mStudyRecords.setEids(studyRecords.getEids());
                    mStudyRecords.setLastprogress(studyRecords.getLastprogress());
                    mStudyRecords.setChoicesforuser(studyRecords.getChoicesforuser());
                    mStudyRecords.setCompleted(studyRecords.getCompleted());
                    mStudyRecords.setDate(studyRecords.getDate());
                    mStudyRecords.setExercisenum(studyRecords.getExercisenum());
                    mStudyRecords.setIsdelete("0");
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
            }
            if (isRecord) {
                //学习记录进来的直接更新学习记录就行
//                StudyRecords studyRecordses = mDaoUtils.queryStudyRecordsByType(studyRecords.getType(), studyRecords.getCid(), studyRecords.getChoosecategory(),
//                        studyRecords.getUserid());
//                if (studyRecordses.getLastprogress() == studyRecords.getLastprogress()) {
                    DebugUtil.e("我是最后一条记录");
                    StudyRecords mStudyRecords = new StudyRecords();
                    mStudyRecords.setChoosecategory(studyRecords.getChoosecategory());//为了记录模块题海已完成的 做题记录
                    mStudyRecords.setCurrentprogress(studyRecords.getCurrentprogress());
                mStudyRecords.setEids(studyRecords.getEids());
                mStudyRecords.setLastprogress(studyRecords.getLastprogress());
                    mStudyRecords.setChoicesforuser(studyRecords.getChoicesforuser());
                mStudyRecords.setCid(studyRecords.getCid()+ new Date());
                mStudyRecords.setCompleted(studyRecords.getCompleted());
                mStudyRecords.setDate(studyRecords.getDate());
                mStudyRecords.setExercisenum(studyRecords.getExercisenum());
                mStudyRecords.setIsdelete("0");
                mStudyRecords.setName(studyRecords.getName());
                mStudyRecords.setPtimelimit(studyRecords.getPtimelimit());
                mStudyRecords.setRightnum(studyRecords.getRightnum());
                mStudyRecords.setToday(studyRecords.getToday());
                mStudyRecords.setType(studyRecords.getType());
                mStudyRecords.setUsedtime(studyRecords.getUsedtime());
                mStudyRecords.setUserid(studyRecords.getUserid());
                studyRecords.setIsdelete("1");
                    mDaoUtils.insertStudyRecord(mStudyRecords);
//                }
            } else {
                //不是学习记录进入的 需要最新的时间 且不在做题记录显示
                studyRecords.setDate(new Date());
                if (isEvaluation()){
                    //真题 type 2 5 不需要隐藏
                    studyRecords.setIsdelete("0");
                }else {
                    //模块题海
                    studyRecords.setIsdelete("1");
                }
            }
            mDaoUtils.updateStudyRecord(studyRecords);

        } else {
            //未交卷
            if (("2".equals(studyRecords.getType()) || "5".equals(studyRecords.getType()))&& !isRecord) {
                //如果是真题还需要更新是否完成
                studyRecords.setCompleted("0");
            }
            if (mType == 3 ) {
                //错题 不进入做题记录 真题错题收藏不进入记录
                studyRecords.setIsdelete("1");
            }
            if (mType != -2) {
                studyRecords.setDate(new Date());
            }
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
            tv_main_title.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handinPapers();
                }
            }, 500);
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
            //真题 的 我的错题 收藏的 取消收藏
            if (isEvaluation() || mType == -2|| isEvaluationCollect ||isEvaluationError) {
//                SendRequest.deletePaperWrongQuestions(paperItem.pid,currentQuestionDetail.getQid(),"collect",mObtainDataListenerForUnStore);
                if (isEvaluationCollect) {
                    SendRequest.deletePaperWrongQuestions(paperItem.pid,currentQuestionDetail.getQid(),"collect",mObtainDataListenerForUnStore);
                }else if (isEvaluationError) {
                    SendRequest.deletePaperWrongQuestions(paperItem.pid,currentQuestionDetail.getQid(),"error",mObtainDataListenerForUnStore);
                }else {
                    SendRequest.deletePaperWrongQuestions(paperItem.pid,currentQuestionDetail.getQid(),"collect",mObtainDataListenerForUnStore);
                }
            }else {
                SendRequest.unstoreExercise(ids, mObtainDataListenerForUnStore);
            }
        } else {
            //收藏
            mCustomLoadingDialog.setTitle(getString(R.string.storing));
            List<String> ids = new ArrayList<>();
            ids.add(currentQuestionDetail.getQid());
            if (mObtainDataListenerForStore == null) {
                mObtainDataListenerForStore = new ObtainDataListenerForStore(this);
            }
            if (isEvaluation()|| mType == -2 || isEvaluationCollect ||isEvaluationError) {
                SendRequest.storePaperExercise(paperItem.pid,currentQuestionDetail.getQid(),mObtainDataListenerForStore);
            }else {
                SendRequest.storeExercise(ids, mObtainDataListenerForStore);
            }
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

//        initHtmlData();
    }

    private void deleteClick() {
        if (isEvaluation() || isEvaluationCollect || isEvaluationError) {
            if (mObtainDataListenerForUnStore == null) {
                mObtainDataListenerForUnStore = new ObtainDataListenerForUnStore(this);
            }
            if (isEvaluationCollect) {
                mCustomLoadingDialog.setTitle(getString(R.string.unstoring));
                SendRequest.deletePaperWrongQuestions(paperItem.pid,currentQuestionDetail.getQid(),"collect",mObtainDataListenerForUnStore);
            }else if (isEvaluationError) {
                mCustomLoadingDialog.setTitle(getString(R.string.deletewrongques_success));
                SendRequest.deletePaperWrongQuestions(paperItem.pid,currentQuestionDetail.getQid(),"error",mObtainDataListenerForUnStore);
            }else {
                mCustomLoadingDialog.setTitle(getString(R.string.deletewrongques_success));
                SendRequest.deletePaperWrongQuestions(paperItem.pid,currentQuestionDetail.getQid(),"error",mObtainDataListenerForUnStore);
            }
        }else {
            List<String> qids = new ArrayList<>();
            qids.add(currentQuestionDetail.getQid());
            SendRequest.deleteWrongQuestions(qids, new ObtainDataListenerForRemoveQues(this));
        }


    }

    private static class ObtainDataListenerForRemoveQues extends ObtainDataFromNetListener {
        private DoVipExerciseActivity doVipExerciseActivity;

        public ObtainDataListenerForRemoveQues(DoVipExerciseActivity doVipExerciseActivity) {
            this.doVipExerciseActivity = new WeakReference<>(doVipExerciseActivity).get();
        }

        @Override
        public void onSuccess(Object res) {
            if (doVipExerciseActivity != null) {

                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(R.string.deletewrongques_success);
                        doVipExerciseActivity.mCustomLoadingDialog.dismiss();
                    }
                });
            }
        }

        @Override
        public void onFailure(Object res) {
            if (doVipExerciseActivity != null) {
                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doVipExerciseActivity.mCustomLoadingDialog.dismiss();
                        ToastUtils.showToast(doVipExerciseActivity.getString(R.string.server_error));
                    }
                });

            }
        }

        @Override
        public void onStart() {
            if (doVipExerciseActivity != null) {
                doVipExerciseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doVipExerciseActivity.mCustomLoadingDialog.show();
                    }
                });
            }
        }
    }

    /**
     * 进入首页提示
     */
    private void showMindTime() {
        builder = new AlertDialog.Builder(DoVipExerciseActivity.this, R.style.mindPause).create();//让对话框全屏
        builder.setCancelable(false);
        builder.show();
        View inflate = DoVipExerciseActivity.this.getLayoutInflater().inflate(R.layout.home_pist, null);
        ImageView img_mindTime = (ImageView) inflate.findViewById(R.id.tis_subject);
        img_mindTime.setImageResource(R.drawable.mkds_pause);
        img_mindTime.setOnClickListener(DoVipExerciseActivity.this);
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
    public boolean isEvaluation() {
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
                    ReportEvaluationActivity.newIntent(DoVipExerciseActivity.this, mId);
                }
            }
        };
        timerLoading.schedule(taskLoading, 1, 1000);
    }

    public void moveToNext(final int position) {
        updateStudyRecord(0);
        vp_doexercise.postDelayed(new Runnable() {
            @Override
            public void run() {
                vp_doexercise.setCurrentItem(position + 1);
            }
        }, 200);

    }

    public interface PageScrollListener {
        void onPageScrollStateChangedListener(int state, int currentNum);
    }

    public void setPageScrollListener(PageScrollListener pageScrollListener) {
        this.pageScrollListener = pageScrollListener;
    }

    class AddViewPagerItemAsyncTask extends AsyncTask<String, Integer, String> {
        /**
         * 异步任务：AsyncTask<Params, Progress, Result>
         * 1.Params:UI线程传过来的参数。
         * 2.Progress:发布进度的类型。
         * 3.Result:返回结果的类型。耗时操作doInBackground的返回结果传给执行之后的参数类型。
         * <p/>
         * 执行流程：
         * 1.onPreExecute()
         * 2.doInBackground()-->onProgressUpdate()
         * 3.onPostExecute()
         */
        @Override
        protected void onProgressUpdate(Integer... values)//执行操作中，发布进度后
        {
//            progressBar.setProgress(values[0]);//每次更新进度条
            if (values[0] == 0) {
                mCustomLoadingDialog.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            //在此方法执行耗时操作,耗时操作中发布进度，更新进度条
            //String result = download();
            for (int i = 0; i < questionDetails.size(); i++) {
                publishProgress(i);//执行中创建新线程处理onProgressUpdate()
//                String yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
//                DebugUtil.e("viewPagerItems.add Time:" + yyyyMMddHHmmssSSS);
                viewPagerItems.add(getLayoutInflater().inflate(R.layout.pager_do_exercise, null));
            }
            if (isDoExercise()) {
                viewPagerItems.add(getLayoutInflater().inflate(R.layout.pager_answercard, null));
            }
            return "添加完成!";
        }

        @Override
        protected void onPreExecute()//执行耗时操作之前处理UI线程事件
        {
//            progressBar.setVisibility(View.VISIBLE);//点击之后，下载执行之前，设置进度条可见
            if (isEvaluation()) {
                if (isChronometer) {
                    chronometer.stop();
                } else {
                    if (reTime == 0) {
//                        chronometer.setVisibility(View.VISIBLE);
//                        re_chronometer.setVisibility(View.GONE);
                        chronometer.stop();
                    } else {
                        re_chronometer.initTime(reTime);
                        re_chronometer.stop();
                    }
                }
            }
            chronometer.setVisibility(View.GONE);
            re_chronometer.setVisibility(View.GONE);

        }

        @Override
        protected void onPostExecute(String result)//执行耗时操作之后处理UI线程事件
        {
            //在此方法执行main线程操作
//            progressBar.setVisibility(View.GONE);//下载完成后，隐藏进度条
            mCustomLoadingDialog.dismiss();
            myPagerAdapter = new DoExercisePagerAdapter(DoVipExerciseActivity.this, viewPagerItems, studyRecords.getName(), questionDetails, mExercisetype,
                    mType);
            vp_doexercise.setAdapter(myPagerAdapter);
            vp_doexercise.setCurrentItem(currentNum);
            if (isEvaluation()) {
                if (isChronometer) {
                    chronometer.start();
                } else {
                    if (reTime == 0) {
                        isChronometer = true;
                        chronometer.start();
                    } else {
                        isChronometer = false;
                        re_chronometer.initTime(reTime);
                        re_chronometer.start();
                    }
                }
            }
        }
    }


}
