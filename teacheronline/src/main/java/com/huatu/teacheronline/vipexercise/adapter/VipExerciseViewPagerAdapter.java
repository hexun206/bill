package com.huatu.teacheronline.vipexercise.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.greendao.QuestionDetail;
import com.huatu.teacheronline.exercise.DoVipExerciseActivity;
import com.huatu.teacheronline.exercise.adapter.AnswercardGridAdapter;
import com.huatu.teacheronline.exercise.bean.QuesAttrVo;
import com.huatu.teacheronline.exercise.fragment.AnswerCardFragment;
import com.huatu.teacheronline.exercise.fragment.ExerciseFragment;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.vipexercise.DoVipQuestionActivity;
import com.huatu.teacheronline.vipexercise.vipbean.VipQuestionBean;
import com.huatu.teacheronline.vipexercise.fragment.VipAnswerCardFragment;
import com.huatu.teacheronline.vipexercise.fragment.VipExerciseFragment;

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
public class VipExerciseViewPagerAdapter extends FragmentPagerAdapter implements DoVipQuestionActivity.PageScrollListener {
    private int mExercisetype;//mExercisetype 1错题解析 2全部解析 0正常做题
    private int mType;//错题 -> 底部三个按钮功能分别为答题卡、解析、垃圾桶 0 今日特训、1 模块题海、2真题估分 3 错题中心、4收藏 则依次显示为交卷、答题卡、收藏 -1是模块分数页面过来的全部解析或者错题解析 5模考大赛 -2是真题分数页面过来的全部解析或者错题解析
    private DoVipQuestionActivity context;
    private List<VipQuestionBean.ResultListBean> questionDetails;//题目
    private static final String TAG = "VipExerciseViewPagerAdapter";

    private VipExerciseFragment exerciseFragment;
    private VipAnswerCardFragment answerCardFragment;
    private VipExerciseFragment currentQuestionFragment;

    public VipExerciseViewPagerAdapter(FragmentManager fm, DoVipQuestionActivity context, List<VipQuestionBean.ResultListBean> questionDetails, int
            mExercisetype, int mType) {
        super(fm);
        this.questionDetails = questionDetails;
        this.mExercisetype = mExercisetype;
        this.mType = mType;
        this.context = context;
        context.setPageScrollListener(this);

    }

    @Override
    public Fragment getItem(int arg0) {

        if (arg0 == questionDetails.size()) {
            if (answerCardFragment == null) {
                answerCardFragment = new VipAnswerCardFragment();
                Bundle Bundle = new Bundle();
                Bundle.putInt("mExercisetype", mExercisetype);
                Bundle.putInt("mType", mType);
                Bundle.putInt("position", arg0);
                Bundle.putInt("questionDetailsSize", questionDetails.size());
                answerCardFragment.setArguments(Bundle);
            }
            return answerCardFragment;
        } else {
            exerciseFragment = new VipExerciseFragment();
            Bundle Bundle = new Bundle();
            Bundle.putSerializable("questionDetail", questionDetails.get(arg0));
            Bundle.putInt("mExercisetype", mExercisetype);
            Bundle.putInt("mType", mType);
            Bundle.putInt("position", arg0);
            exerciseFragment.setArguments(Bundle);
            return exerciseFragment;
        }
    }

    @Override
    public int getCount() {
        boolean doExercise = context.isDoExercise();
        DebugUtil.e("VipExerciseViewPagerAdapter getCount:"+(doExercise ? questionDetails.size() + 1 : questionDetails.size()));
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
            currentQuestionFragment = (VipExerciseFragment) object;

        }
        super.setPrimaryItem(container, position, object);
    }


    @Override
    public void onPageScrollStateChangedListener(int state, int currentNum) {
//        if (questionDetails.get(currentNum).getTypeName().equals("") && state == 1) {
//            //填空题
//            currentQuestionFragment.vocabularySaveData(currentNum);
//            context.updateStudyRecord(0);
//        }
//        if (Integer.parseInt(questionDetails.get(currentNum).getQtype()) == 5 && mType == 3 && state == 1 ) {
//            //错题中心填空题需要显示其他内容  试卷的收藏也需要显示其他内容
//            if (!currentQuestionFragment.isShowParsing) {
//                currentQuestionFragment.questionOtherShow(currentNum);
//                context.vp_doexercise.setCurrentItem(currentNum - 1);
//            }
//        }

        List<String> answer = questionDetails.get(currentNum).getAnswer();
        if (null != answer && answer.size() > 1 && mType == 3 && state == 1) {
            //错题中心多选题 需要显示其他内容 试卷的收藏也需要显示其他内容
            if (!currentQuestionFragment.isShowParsing) {
                currentQuestionFragment.questionOtherShow(currentNum);
                context.vp_doexercise.setCurrentItem(currentNum - 1);
            }
        }
    }
}