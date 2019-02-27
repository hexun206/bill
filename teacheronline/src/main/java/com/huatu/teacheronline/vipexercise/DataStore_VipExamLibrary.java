package com.huatu.teacheronline.vipexercise;


import java.util.ArrayList;
import java.util.List;

import com.huatu.teacheronline.vipexercise.vipbean.PaperAnalysiBean;
import com.huatu.teacheronline.vipexercise.vipbean.VipPaperBean;
import com.huatu.teacheronline.vipexercise.vipbean.VipQuestionBean;
import com.huatu.teacheronline.vipexercise.vipbean.VipQuestionBean.ResultListBean;
/**
 * Vip做题相关数据保存
 * Created by 79937 on 2017/10/13.
 */
public class DataStore_VipExamLibrary {
    //当前试卷
    public static VipPaperBean VipPaperBean;
    //做题相关信息
    public static VipQuestionBean VipQuestionBean;
    // 全部题目数据
    public static List<ResultListBean> ResultListBeanDetailList;
    // 错误题目数据
    public static List<ResultListBean> wrongResultListBean;
    // 所有题目用户选择的答案集合
    public static List<List> selectedChoiceList;
    // 所有错题用户选择的答案集合
    public static List<List> wrongChoiceList;
    //用户答题情况answerFlag 0错误  1正确  -1未答
    public static String[] answerFlag;
    // 试题分析结果
    public static PaperAnalysiBean paperAnalysiBean;

    public static void resetDatas() {
        DataStore_VipExamLibrary.VipQuestionBean = null;
        DataStore_VipExamLibrary.ResultListBeanDetailList = null;
        DataStore_VipExamLibrary.wrongResultListBean = null;
        DataStore_VipExamLibrary.selectedChoiceList = null;
        DataStore_VipExamLibrary.wrongChoiceList = null;
        DataStore_VipExamLibrary.answerFlag = null;
        DataStore_VipExamLibrary.paperAnalysiBean = null;
        DataStore_VipExamLibrary.VipPaperBean = null;
    }
}
