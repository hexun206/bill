package com.huatu.teacheronline.exercise.adapter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.greendao.QuestionDetail;
import com.huatu.teacheronline.exercise.fragment.AnswerCardFragment;
import com.huatu.teacheronline.exercise.DoVipExerciseActivity;
import com.huatu.teacheronline.exercise.fragment.ExerciseFragment;
import com.huatu.teacheronline.exercise.bean.QuesAttrVo;
import com.huatu.teacheronline.utils.DebugUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 做题页面fragmentpageradapter
 * Created by ljyu on 2017/6/29.
 */
public class ExerciseViewPagerAdapter extends FragmentPagerAdapter implements DoVipExerciseActivity.PageScrollListener {
    public boolean isEvaluationCollect = false;
    public boolean isEvaluationError = false;
    private List<View> viewPagerItems;
    private Map<String, QuesAttrVo> questionAttrMap;//易错项 正确率
    private int mExercisetype;//mExercisetype 1错题解析 2全部解析 0正常做题
    private int mType;//错题 -> 底部三个按钮功能分别为答题卡、解析、垃圾桶 0 今日特训、1 模块题海、2真题估分 3 错题中心、4收藏 则依次显示为交卷、答题卡、收藏 -1是模块分数页面过来的全部解析或者错题解析 5模考大赛 -2是真题分数页面过来的全部解析或者错题解析
    private String title;//标题
    private LinkedList<View> mViewCache;
    private DoVipExerciseActivity context;
    private List<QuestionDetail> questionDetails;//题目
    private static final String TAG = "MyPagerAdapter";
    private String choiceType;//题目类型
    private int choiceSum = 4;//默认选项
    private View convertView;
    private DecimalFormat fnum = new DecimalFormat("##0.0");//小数点后一位

    private int ediLength;//当前页面填空题分割数 几个空需要加一
    private int moveNext = 1;//错题中心 填空和多选需要滑动两下

    private List<Integer> codeList = new ArrayList<>();// 题号集合
    private Map<Integer, String> gridMap = new HashMap<>();// <题号，答题情况>
    private AnswercardGridAdapter answercardGridAdapterChoose;
    private GridView gv_answercard;//答题卡
    private ExerciseFragment exerciseFragment;
    private AnswerCardFragment answerCardFragment;
    private ExerciseFragment currentQuestionFragment;

    public ExerciseViewPagerAdapter(FragmentManager fm, DoVipExerciseActivity context, List<QuestionDetail> questionDetails, int
            mExercisetype, int mType) {
        super(fm);
        this.questionDetails = questionDetails;
        this.mExercisetype = mExercisetype;
        this.mType = mType;
        this.context = context;
        context.setPageScrollListener(this);

    }

    public ExerciseViewPagerAdapter(FragmentManager supportFragmentManager, DoVipExerciseActivity doVipExerciseActivity, List<QuestionDetail>
            questionDetails, int mExercisetype, int mType, boolean isEvaluationCollect, boolean isEvaluationError) {
        super(supportFragmentManager);
        this.questionDetails = questionDetails;
        this.mExercisetype = mExercisetype;
        this.mType = mType;
        this.context = doVipExerciseActivity;
        this.isEvaluationCollect = isEvaluationCollect;
        this.isEvaluationError = isEvaluationError;

        context.setPageScrollListener(this);
    }


    @Override
    public Fragment getItem(int arg0) {

        if (arg0 == questionDetails.size()) {
            if (answerCardFragment == null) {
                answerCardFragment = new AnswerCardFragment();
                Bundle Bundle = new Bundle();
                Bundle.putInt("mExercisetype", mExercisetype);
                Bundle.putInt("mType", mType);
                Bundle.putInt("position", arg0);
                Bundle.putInt("questionDetailsSize", questionDetails.size());
                answerCardFragment.setArguments(Bundle);
            }
            return answerCardFragment;
        } else {
            exerciseFragment = new ExerciseFragment();
            Bundle Bundle = new Bundle();
            Bundle.putSerializable("questionDetail", questionDetails.get(arg0));
            Bundle.putInt("mExercisetype", mExercisetype);
            Bundle.putBoolean("isEvaluationCollect", isEvaluationCollect);
            Bundle.putBoolean("isEvaluationError", isEvaluationError);
            Bundle.putInt("mType", mType);
            Bundle.putInt("position", arg0);
            exerciseFragment.setArguments(Bundle);
            return exerciseFragment;
        }
    }

    @Override
    public int getCount() {
//        DebugUtil.e("是否错题："+isEvaluationCollect);
//        DebugUtil.e("是否收藏："+isEvaluationError);
        boolean doExercise = context.isDoExercise()&&(!isEvaluationCollect&&!isEvaluationError);
//        DebugUtil.e("ExerciseViewPagerAdapter getCount:"+(doExercise ? questionDetails.size() + 1 : questionDetails.size()));
        return doExercise ? questionDetails.size() + 1 : questionDetails.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // TODO Auto-generated method stub
        return "sss" + position;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (position ==questionDetails.size()) {
        }else {
            currentQuestionFragment = (ExerciseFragment) object;

        }
        super.setPrimaryItem(container, position, object);
    }


    @Override
    public void onPageScrollStateChangedListener(int state, int currentNum) {
        if (Integer.parseInt(questionDetails.get(currentNum).getQtype()) == 5 && state == 1) {
            currentQuestionFragment.vocabularySaveData(currentNum);
            context.updateStudyRecord(0);
        }
        if (Integer.parseInt(questionDetails.get(currentNum).getQtype()) == 5 && (mType == 3 || isEvaluationCollect) && state == 1 ) {
            //错题中心填空题需要显示其他内容  试卷的收藏也需要显示其他内容
            if (!currentQuestionFragment.isShowParsing) {
                currentQuestionFragment.questionOtherShow(currentNum);
                context.vp_doexercise.setCurrentItem(currentNum - 1);
            }
        }
        String[] answersList = questionDetails.get(currentNum).answersArr;
        if (null != answersList && answersList.length > 1 && (mType == 3 || isEvaluationCollect) && state == 1) {
            //错题中心多选题 需要显示其他内容 试卷的收藏也需要显示其他内容
            if (!currentQuestionFragment.isShowParsing) {
                currentQuestionFragment.questionOtherShow(currentNum);
                context.vp_doexercise.setCurrentItem(currentNum - 1);
            }
        }
    }


}