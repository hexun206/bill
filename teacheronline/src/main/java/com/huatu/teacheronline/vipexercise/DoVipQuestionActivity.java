package com.huatu.teacheronline.vipexercise;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.AnswerCardActivity;
import com.huatu.teacheronline.utils.Arith;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.vipexercise.adapter.VipExerciseViewPagerAdapter;
import com.huatu.teacheronline.vipexercise.fragment.VipAnswerCardFragment;
import com.huatu.teacheronline.vipexercise.vipbean.HandPaperBean;
import com.huatu.teacheronline.vipexercise.vipbean.PaperHandBean;
import com.huatu.teacheronline.vipexercise.vipbean.VipQuestionBean;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.ExerciseViewPager;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的资料
 *
 * @auto cwqiang 2017-9-20 14:43:30
 */
public class DoVipQuestionActivity extends BaseActivity implements Chronometer.OnChronometerTickListener {

    private TextView tv_main_title;//标题
    private TextView tv_main_right;//右边文字
    private TextView tv_title;//试卷标题
    private TextView tv_progress;// 试卷进度
    private ImageView img_answer_card;//底部答题卡
    private TextView tv_their_papers;//底部交卷
    private RelativeLayout rl_next_papers;//下一题
    private Chronometer chronometer;// 计时器
    public ExerciseViewPager vp_doexercise;//试题pager
    private VipExerciseViewPagerAdapter myViewPagerAdapter;

    private CustomAlertDialog mCustomHandinDialog;
    private CustomAlertDialog mCustomLoadingDialog;

    private int mExercisetype;//mExercisetype 1错题解析 2全部解析 0正常做题
    private int mType;//错题 -> 底部三个按钮功能分别为答题卡、解析、垃圾桶 0 今日特训、1 模块题海、2真题估分 3 错题中心、4收藏 则依次显示为交卷、答题卡、收藏 -1是模块分数页面过来的全部解析或者错题解析 5模考大赛 -2是真题分数页面过来的全部解析或者错题解析 5Vip题库
    private int caseNum;//本次做题总题数
    private int currentNum = 0;//当前题序号
    private int maxNum = 1;///最大做题数
    private VipQuestionBean VipQuestionBean;
    private VipQuestionBean.ResultListBean currentQuestionDetail;//本题的试题详情
    private List<VipQuestionBean.ResultListBean> questionDetails; //试题列表
    private boolean[] isAnalysisArr;//是否解析
    public List<List> selectedChoiceList;// 保存用户每道题选择的答案 填空题 主观题 只保存在第一位的答案和分值
    public Map<String, String> selectedDate = new HashMap<String, String>();// 保存用户每道题做题时间
    private int miss = 0;//时间计数
    private double score_right, score_all;//当前做题得分
    private int num_right;//当前作对习题数目
    private PageScrollListener pageScrollListener;
    private int requestcode_answercard = 1;
    private String exclusiveId = "";//试卷id
    private ObtainDataListenerForHandinVipPapers mObtainDataListenerForHandinVipPapers;
    private String uid = "";
    private int createUserId = 0;
    private ObtainDataListenerForDeleteQuestion obtainDataListenerForDeleteQuestion;
    private List<String> wrongExerciseIdList = new ArrayList<String>();
    private HandPaperBean handPaperBean;
    private ArrayList<List> selectedChoiceListRecord;


    @Override
    public void initView() {
        setContentView(R.layout.activity_dovipquestion);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_main_right = (TextView) findViewById(R.id.tv_main_right);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        img_answer_card = (ImageView) findViewById(R.id.img_answer_card);
        tv_their_papers = (TextView) findViewById(R.id.tv_their_papers);
        vp_doexercise = (ExerciseViewPager) findViewById(R.id.vp_doexercise);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        rl_next_papers = (RelativeLayout) findViewById(R.id.rl_next_papers);

        mCustomLoadingDialog = new CustomAlertDialog(DoVipQuestionActivity.this, R.layout.dialog_loading_custom);
        mCustomHandinDialog = new CustomAlertDialog(this, R.layout.dialog_join_mydirect);
        mCustomHandinDialog.setCancelable(false);
        mCustomHandinDialog.setTitle(getString(R.string.handinpapers_confirminfo));

        mExercisetype = getIntent().getIntExtra("exercisetype", 0);
        exclusiveId = DataStore_VipExamLibrary.VipPaperBean.getExclusiveId();
        createUserId = DataStore_VipExamLibrary.VipPaperBean.getCreateUserId();
        mType = getIntent().getIntExtra("type", -1);
        if (mExercisetype == 0 && mType != 3 ) {
            initExerciseDataForExerciseEvaluation(exclusiveId);
        }

        VipQuestionBean = DataStore_VipExamLibrary.VipQuestionBean;
        tv_title.setText(VipQuestionBean.getTitle());
        switch (mExercisetype) {
            case 1:
                img_answer_card.setVisibility(View.VISIBLE);
                tv_main_title.setText(R.string.analysis_error);
                tv_main_title.setVisibility(View.VISIBLE);
                break;
            case 2:
                tv_main_title.setText(R.string.analysis_all);
                tv_main_title.setVisibility(View.VISIBLE);
                break;
            case 0:
                tv_main_title.setVisibility(View.VISIBLE);
                tv_main_right.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        switch (mExercisetype) {
            case 1:
                questionDetails = DataStore_VipExamLibrary.wrongResultListBean;
                break;
            case 2:
            case 0:
            default:
                questionDetails = DataStore_VipExamLibrary.ResultListBeanDetailList;
                if (mType == 3) {
                    tv_main_right.setVisibility(View.VISIBLE);
                    img_answer_card.setVisibility(View.VISIBLE);
                    tv_main_right.setBackgroundResource(R.drawable.ic_del_w);
                    tv_main_title.setText(R.string.my_error_exercise);
                }
                break;
        }

        if (questionDetails == null || questionDetails.size() == 0) {
            return;
        }
        // 有可能 选中的10道题的qids中根据qid找不到该题,即实际总题数小于记录的exerciseNum
        caseNum = questionDetails.size();

        isAnalysisArr = new boolean[caseNum];
        //mExercisetype 1错题解析 2全部解析 0正常做题
        switch (mExercisetype) {
            case 1:
                selectedChoiceList = DataStore_VipExamLibrary.wrongChoiceList;
                break;
            case 2:
            case 0:
            default:
                selectedChoiceList = DataStore_VipExamLibrary.selectedChoiceList;
                break;
        }
        if (selectedChoiceList != null && selectedChoiceList.size() > 0) {
            DebugUtil.e(" initView selectedChoiceList.size()" + selectedChoiceList.size());
        }
        if (selectedChoiceList == null || selectedChoiceList.size() != caseNum) {
            selectedChoiceList = new ArrayList<>();
            if (mExercisetype == 0 && selectedChoiceListRecord != null && selectedChoiceListRecord.size() > 0) {
                selectedChoiceList.addAll(selectedChoiceListRecord);
            }
            // 进行初始化
            for (int index = selectedChoiceList.size(); index < caseNum; index++) {
                ArrayList<String> array = new ArrayList<>();
                //String类型
                selectedChoiceList.add(array);
            }
        }

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
                if (handPaperBean == null) {
                    currentNum = 0;
                } else {
                    currentNum = handPaperBean.getCurrentNum();
                    if (currentNum >= caseNum) {
                        currentNum = caseNum-1;
                    }
                }
                break;
        }
        if (mType == 3) {
            //为错题中心的时候 自动滑到第一题
            currentNum = 0;
        }
        tv_progress.setText((currentNum + 1) + "/" + caseNum);
        myViewPagerAdapter = new VipExerciseViewPagerAdapter(getSupportFragmentManager(), this, questionDetails, mExercisetype, mType);
        vp_doexercise.setAdapter(myViewPagerAdapter);
        currentQuestionDetail = questionDetails.get(currentNum);
        vp_doexercise.setCurrentItem(currentNum);

    }

    @Override
    public void setListener() {
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        tv_main_right.setOnClickListener(this);
        img_answer_card.setOnClickListener(this);
        tv_their_papers.setOnClickListener(this);
        rl_next_papers.setOnClickListener(this);
        chronometer.setOnChronometerTickListener(this);
        mCustomHandinDialog.setOkOnClickListener(this);
        mCustomHandinDialog.setCancelOnClickListener(this);
        vp_doexercise.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                DebugUtil.e(TAG, "onPageScrolled" + " position:" + position + " positionOffset:" + positionOffset + " positionOffsetPixels:" +
                        positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if (isDoExercise()) {
                    if (position == questionDetails.size()) {
                        //答题卡
//                        myPagerAdapter.reFreshAnswerCard();
                        VipAnswerCardFragment item = (VipAnswerCardFragment) myViewPagerAdapter.getItem(position);
                        item.reFreshAnswerCard();
//                    ll_time_exercise_title.setVisibility(View.GONE);
                        tv_main_title.setText(R.string.answercard);
                        tv_main_right.setVisibility(View.VISIBLE);
                        img_answer_card.setVisibility(View.GONE);
                        tv_their_papers.setVisibility(View.VISIBLE);
                        return;
                    } else {
                        //正常做题
                        tv_main_right.setVisibility(View.GONE);
//                    ll_time_exercise_title.setVisibility(View.VISIBLE);
                        tv_main_title.setVisibility(View.VISIBLE);
                        img_answer_card.setVisibility(View.VISIBLE);
                        tv_their_papers.setVisibility(View.GONE);
                    }
                } else if (position == questionDetails.size()) {
                    return;
                }
                CommonUtils.closeInputMethod(DoVipQuestionActivity.this);
                currentNum = position;
                tv_progress.setText((currentNum + 1) + "/" + caseNum);
                DebugUtil.e(TAG, "onPageSelected" + " position:" + position);
                currentQuestionDetail = questionDetails.get(position);
//                //判断是否删除或者更新
//                deleteOrUpdateQuestions();
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
     * 准备题库习题数据 针对真题测评
     *
     * @param exclusiveId
     */
    public void initExerciseDataForExerciseEvaluation(String exclusiveId) {
        String sharedPreferenceItem = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_VIPHANDPAPERBEAN + uid + exclusiveId, "");
        if (!StringUtils.isEmpty(sharedPreferenceItem)) {
            Gson gson = new Gson();
            handPaperBean = gson.fromJson(sharedPreferenceItem, HandPaperBean.class);
            selectedChoiceListRecord = new ArrayList<List>();
            for (int i = 0; i < handPaperBean.getCurrentNum(); i++) {
                List<String> result = handPaperBean.getPaperHandBean().get(i).getResult();
                selectedChoiceListRecord.add(result);
            }
            DataStore_VipExamLibrary.answerFlag = handPaperBean.getAnswersFlag();
            DataStore_VipExamLibrary.selectedChoiceList = selectedChoiceListRecord;
            maxNum = selectedChoiceListRecord.size();
            DataStore_VipExamLibrary.wrongChoiceList = handPaperBean.getWrongChoiceList();
            DataStore_VipExamLibrary.wrongResultListBean = handPaperBean.getWrongResultListBean();
            miss = handPaperBean.getTime() - 1;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_cancel:
                //取消交卷
                mCustomHandinDialog.dismiss();
                break;
            case R.id.tv_main_right:
                //删除错题
                delVipQuestionError();
                break;
            case R.id.tv_dialog_ok:
                //确定交卷
                mCustomHandinDialog.dismiss();
                handinPapers(true);
                break;
            case R.id.img_answer_card://答题卡
                if (currentQuestionDetail == null) {
                    ToastUtils.showToast("该题目暂未加载出来。。。");
                    return;
                }
                startAnswerCardActivity();
                break;
            case R.id.tv_their_papers://交卷查看结果
                //交卷
                handinpapersClick();
                break;
            case R.id.rl_next_papers://错题收藏下一题
                //错题收藏下一题
                moveToNext();
                break;
            case R.id.rl_main_left:
                //退出保存做题记录
                if (mExercisetype == 0 && mType != 3) {
                    //正常做题且不是错题中心
                    handinPapers(false);
                    tv_main_title.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            back();
                        }
                    }, 500);
                }else {
                    back();
                }

                break;
        }
    }

    private void delVipQuestionError() {
        if (obtainDataListenerForDeleteQuestion == null) {
            obtainDataListenerForDeleteQuestion = new ObtainDataListenerForDeleteQuestion(this);
        }
        SendRequest.delVipQuestionError(uid, exclusiveId, currentQuestionDetail.getQuestionId(), currentQuestionDetail.getChildId(),
                obtainDataListenerForDeleteQuestion);
    }

    private void moveToNext() {
        if (currentNum == caseNum - 1) {
            //最后一题
//            if (mType == 3 || isEvaluationError || isEvaluationCollect) {
//                //错题中心和试卷的收藏 错题 都要弹出来
//                mCustomLastDialog.show();
//                mCustomLastDialog.setCancelGone();
//            } else {
            ToastUtils.showToast(getResources().getString(R.string.lastexericse));
//            }
            return;
        } else {
            currentNum++;
            vp_doexercise.setCurrentItem(currentNum);
        }
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, DoVipQuestionActivity.class);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param exercisetype 1错题解析 2全部解析 0正常做题
     * @param type         3错题 -》正常做题时,底部依次显示 答题卡、解析、垃圾桶；
     *                     0 今日特训、1 模块题海、2真题测评、3错题 4收藏 则依次显示为交卷、答题卡、收藏  模块：-1 错题和全部解析  真题 -2
     */
    public static void newIntent(Activity context, int exercisetype, int type) {
        Intent intent = new Intent(context, DoVipQuestionActivity.class);
        intent.putExtra("exercisetype", exercisetype);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

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

    @Override
    public void onChronometerTick(Chronometer chronometer) {
//        chronometer.setText(FormatMiss(miss));
        if (isDoExercise()) {
            if (tv_main_right.getVisibility() == View.VISIBLE) {
                tv_main_right.setText(CommonUtils.FormatMiss(miss));
            } else {
                tv_main_title.setText(CommonUtils.FormatMiss(miss));
            }

        }
        miss++;
    }

    /**
     * 设置最大做题数
     * @param position
     */
    public void setMaxNum(int position) {
        if (position >= maxNum) {
            maxNum = position+1;
        }
    }

    public interface PageScrollListener {
        void onPageScrollStateChangedListener(int state, int currentNum);
    }

    public void setPageScrollListener(PageScrollListener pageScrollListener) {
        this.pageScrollListener = pageScrollListener;
    }

    @Override
    protected void onResume() {
        chronometer.start();
        super.onResume();
    }

    /**
     * 是否正确
     *
     * @param currentNum
     * @return
     */
    public boolean isCorrect(int currentNum) {
        VipQuestionBean.ResultListBean resultListBean = questionDetails.get(currentNum);
        List<String> answers;
        if (resultListBean.getTypeName().equals("共用题干题")) {
            answers = resultListBean.getChildAnswer();
        } else {
            answers = resultListBean.getAnswer();
        }
        List<String> choices = selectedChoiceList.get(currentNum);// 用户选择的答案
        if (!resultListBean.getTypeName().equals("填空题") && !resultListBean.getTypeName().equals("主观题")) {//判断如果不为填空和主观题，选择的答案按字母排序
            Collections.sort(choices);
        }
        if (answers == null || answers.size() < 1) {
            //表明没有选项->创建4个选项
            answers = new ArrayList<String>();
            for (int index = 0; index < 4; index++) {
                answers.add(String.valueOf((char) ('A' + index)));
            }
        }

        if (null != choices && choices.size() > 0) {
            if (answers.size() != choices.size()) {
                //答错
                return false;
            } else {
                boolean isRight = true;
                for (int j = 0; j < answers.size(); j++) {
                    if (!choices.contains(answers.get(j))) {
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

    /**
     * 移动到下一题
     *
     * @param position
     */
    public void moveToNext(final int position) {
        vp_doexercise.postDelayed(new Runnable() {
            @Override
            public void run() {
                vp_doexercise.setCurrentItem(position + 1);
            }
        }, 200);

    }

    /**
     * 滚动到答题卡页面调用
     */
    public String[] startAnswerCardPager() {
        //如果是错题解析不需要重置本套题的相关修改，只需要传多少题错题就行了，questionDetails.size()错题数量
        if (mExercisetype == 1) {
//            AnswerCardActivity.newIntent(this, requestcode_answercard, DataStore_ExamLibrary.answerFlag/*, qrootpointList*/);
//            AnswerCardActivity.newIntent(this, requestcode_answercard, DataStore_ExamLibrary.answerFlag, questionDetails.size());
            return DataStore_VipExamLibrary.answerFlag;
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
                VipQuestionBean.ResultListBean questionDetailEve = questionDetails.get(i);
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
            }
//            AnswerCardActivity.newIntent(this, requestcode_answercard, answersFlag/*, qrootpointList*/);
            DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
            return answersFlag;
        }
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
        ArrayList<VipQuestionBean.ResultListBean> wrongExerciseList = new ArrayList<>();
        List<List> wrongChoiceList = new ArrayList<>();

        for (int i = 0; i < caseNum; i++) {
            VipQuestionBean.ResultListBean questionDetailEve = questionDetails.get(i);
            boolean isCommonage = false;//是否共用题干题
            if (questionDetailEve.getTypeName().equals("共用题干题")) {
                isCommonage = true;
            }
            double scoreForcurrentQues;
            //统计总分数
            if (questionDetailEve.getScore() == 0) {
                scoreForcurrentQues = 1;
            } else {
                try {
                    scoreForcurrentQues = questionDetailEve.getScore();
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
            List<String> answers = isCommonage ? questionDetailEve.getChildAnswer() : questionDetailEve.getAnswer();
            List<String> choices = selectedChoiceList.get(i);// 用户选择的答案
            if (answers == null || answers.size() < 1) {
                //表明没有选项->创建4个选项
                for (int index = 0; index < 4; index++) {
                    answers.add(String.valueOf((char) ('A' + index)));
                }
            }
            if (null != choices && choices.size() > 0) {
                //已做
                if (answers.size() != choices.size()) {
                    //如果是 主观题 正常做题 真题
                    if (questionDetailEve.getTypeName().equals("主观题")) {
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
                        boolean b = isCommonage ? wrongExerciseIdList.add(questionDetailEve.getChildId()) : wrongExerciseIdList.add(questionDetailEve
                                .getQuestionId());
                        wrongChoiceList.add(choices);
                    }
                    continue;
                } else {
                    //有的真题主观题有默认选项，所以这边也得判断
                    if (questionDetailEve.getTypeName().equals("主观题")) {
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
                    for (int j = 0; j < answers.size(); j++) {
                        if (!choices.contains(answers.get(j))) {
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
                        boolean b = isCommonage ? wrongExerciseIdList.add(questionDetailEve.getChildId()) : wrongExerciseIdList.add(questionDetailEve
                                .getQuestionId());
                        wrongChoiceList.add(choices);
                    }
                }
            } else {
                //未做
                answersFlag[i] = "-1";
                if(i < maxNum){
                    wrongExerciseList.add(questionDetailEve);
                    boolean b = isCommonage ? wrongExerciseIdList.add(questionDetailEve.getChildId()) : wrongExerciseIdList.add(questionDetailEve
                            .getQuestionId());
                    wrongChoiceList.add(choices);
                }
                if (orderType == 1) {
                    answersFlag[i] = "0";
                    //真题未做算答错
                    if (!questionDetailEve.getTypeName().equals("主观题")) {
                        wrongExerciseList.add(questionDetailEve);
                        boolean b = isCommonage ? wrongExerciseIdList.add(questionDetailEve.getChildId()) : wrongExerciseIdList.add(questionDetailEve
                                .getQuestionId());
                        wrongChoiceList.add(choices);
                    }
                    //针对全部解析 主观题全部为未做
                    if (mType == -2 && questionDetailEve.getTypeName().equals("主观题")) {
                        answersFlag[i] = "-1";
                    }
                }
                if (orderType == 2) {
                    //真题未做算答错
                    if (!questionDetailEve.getTypeName().equals("主观题")) {
                        wrongExerciseList.add(questionDetailEve);
                        boolean b = isCommonage ? wrongExerciseIdList.add(questionDetailEve.getChildId()) : wrongExerciseIdList.add(questionDetailEve
                                .getQuestionId());
                        wrongChoiceList.add(choices);
                    }
                    //针对全部解析 主观题全部为未做
                    if (mType == -2 && questionDetailEve.getTypeName().equals("主观题")) {
                        answersFlag[i] = "-1";
                    }
                }
            }
        }
        DataStore_VipExamLibrary.answerFlag = answersFlag;
        DataStore_VipExamLibrary.wrongChoiceList = wrongChoiceList;
        if (handPaperBean != null) {
            handPaperBean.setWrongChoiceList(wrongChoiceList);
            handPaperBean.setWrongResultListBean(wrongExerciseList);
        }
        DataStore_VipExamLibrary.wrongResultListBean = wrongExerciseList;
        DataStore_VipExamLibrary.selectedChoiceList = selectedChoiceList;
        DebugUtil.e(" orderResult selectedChoiceList.size()" + selectedChoiceList.size());
    }

    /**
     * 打开答题卡页面
     */
    private void startAnswerCardActivity() {
        //如果是错题解析不需要重置本套题的相关修改，只需要传多少题错题就行了，questionDetails.size()错题数量
        if (mExercisetype == 1) {
//            AnswerCardActivity.newIntent(this, requestcode_answercard, DataStore_ExamLibrary.answerFlag/*, qrootpointList*/);
            AnswerCardActivity.newIntent(this, requestcode_answercard, DataStore_VipExamLibrary.answerFlag, questionDetails.size(), mType, currentNum);
        } else {
            DateFormat dateFormat = new SimpleDateFormat("HH-mm-ss-SSSS");
            DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
            String[] answersFlag = new String[caseNum];
            orderResult(answersFlag, 0);
            DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
            // 答题卡不显示答题正确与错误，只有已作答和未作答  -2已作答  -1未作答
            String[] qrootpointList = new String[caseNum];
            for (int i = 0; i < answersFlag.length; i++) {
                VipQuestionBean.ResultListBean questionDetailEve = questionDetails.get(i);
                if (mExercisetype == 0) {
                    //错题中心过来正常做题 需要显示答案  试卷收藏也要显示对错
                    if (mType == 3) {
                        DebugUtil.e("答题卡错题显示");
                    } else {
                        // 正常做题把正确和错误答案分别标识为已作答
                        if (!TextUtils.isEmpty(answersFlag[i]) && ("0".equals(answersFlag[i]) || "1".equals(answersFlag[i]))) {
                            answersFlag[i] = "-2";
                        }
                    }
                }
            }
            AnswerCardActivity.newIntent(this, requestcode_answercard, answersFlag, 0, mType, currentNum);
            DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                    handinPapers(true);
                }
            },500);

        } else {
            // 还有未答完的题目，提示是否交卷
            mCustomHandinDialog.show();
        }
    }

    /**
     * 交卷 或者保存答案到 本地
     *
     * @param isHand //是否交卷
     */
    protected void handinPapers(boolean isHand) {
        String[] answersFlag = new String[caseNum];
        orderResult(answersFlag, 0);
//        ToastUtils.showToast("交卷");
        List<PaperHandBean> queslist = new ArrayList<>();
        for (int i = 0; i < maxNum; i++) {
            VipQuestionBean.ResultListBean questionDetail = questionDetails.get(i);
            PaperHandBean paperHandBean = new PaperHandBean();
//            paperHandBean.setCreateTime();
            paperHandBean.setQuestionId(questionDetail.getQuestionId());
            if (questionDetail.getTypeName().equals("共用题干题")) {
                paperHandBean.setChildId(questionDetail.getChildId());
            }
            Collections.sort(selectedChoiceList.get(i));
            paperHandBean.setResult(selectedChoiceList.get(i));
            paperHandBean.setCreateTime(selectedDate.get(i + ""));
            queslist.add(paperHandBean);
        }
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
        String secondss = String.valueOf(seconds);

        if (isHand) {
            if (maxNum != caseNum) {
                handPaperBean = new HandPaperBean();
                handPaperBean.setAnswersFlag(answersFlag);
                handPaperBean.setCreateUserId(createUserId);
                handPaperBean.setTime(seconds);
                handPaperBean.setUid(uid);
                handPaperBean.setExclusiveId(exclusiveId);
                handPaperBean.setPaperHandBean(queslist);
                handPaperBean.setCurrentNum(maxNum);
                Gson gson = new Gson();
                String handPaperBeanString = gson.toJson(handPaperBean);
                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_VIPHANDPAPERBEAN + uid + exclusiveId, handPaperBeanString);
            }else {
                //最大做题数等于题目数为学员做完该套试卷  不记录
                CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_VIPHANDPAPERBEAN + uid + exclusiveId, "");
            }

            if (mObtainDataListenerForHandinVipPapers == null) {
                mObtainDataListenerForHandinVipPapers = new ObtainDataListenerForHandinVipPapers(this);
            }
            mCustomLoadingDialog.setTitle(getString(R.string.handinpapers_ing));
            //VIP交卷
            SendRequest.handVipExclusive(uid, exclusiveId, createUserId, seconds, queslist, mObtainDataListenerForHandinVipPapers);

        } else {
            handPaperBean = new HandPaperBean();
            handPaperBean.setAnswersFlag(answersFlag);
            handPaperBean.setCreateUserId(createUserId);
            handPaperBean.setTime(seconds);
            handPaperBean.setUid(uid);
            handPaperBean.setExclusiveId(exclusiveId);
            handPaperBean.setPaperHandBean(queslist);
            handPaperBean.setCurrentNum(maxNum);
            Gson gson = new Gson();
            String handPaperBeanString = gson.toJson(handPaperBean);
            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_VIPHANDPAPERBEAN + uid + exclusiveId, handPaperBeanString);
        }
    }

    private static class ObtainDataListenerForHandinVipPapers extends ObtainDataFromNetListener<String, String> {

        private DoVipQuestionActivity weak_activity;

        public ObtainDataListenerForHandinVipPapers(DoVipQuestionActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("1".equals(res)) {
                            weak_activity.finish();
//                            CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_VIPHANDPAPERBEAN + weak_activity.uid + weak_activity.exclusiveId, "");
                            VipReportEvaluationActivity.newIntent(weak_activity, weak_activity.exclusiveId);
                        }
                    }
                });
            }

        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
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

    private static class ObtainDataListenerForDeleteQuestion extends ObtainDataFromNetListener<String, String> {

        private DoVipQuestionActivity weak_activity;

        public ObtainDataListenerForDeleteQuestion(DoVipQuestionActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!StringUtils.isEmpty(res)&&res.equals("1")) {
                            ToastUtils.showToast("错题删除成功");
                        }
                    }
                });
            }

        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mExercisetype == 0 && mType != 3) {
                //正常做题且不是错题中心
                tv_main_title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handinPapers(false);
                    }
                }, 500);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
