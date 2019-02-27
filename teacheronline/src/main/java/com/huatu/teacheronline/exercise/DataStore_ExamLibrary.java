package com.huatu.teacheronline.exercise;

import com.greendao.QuestionDetail;
import com.huatu.teacheronline.exercise.bean.PaperItem;
import com.huatu.teacheronline.exercise.bean.QuesAttrVo;
import com.huatu.teacheronline.exercise.bean.erbean.ErPaperAttrVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxm on 2015/11/3.
 * 静态类，存储做题时用到的全部题目集合，错题题目集合，题目解析（易错项和准确率）
 * 退出时需要置空
 */
public class DataStore_ExamLibrary {
    // 全部题目数据
    public static List<QuestionDetail> questionDetailList;
    // 错误题目数据
    public static List<QuestionDetail> wrongExerciseList;
    // 试题分析结果列表
    public static Map<String, QuesAttrVo> questionAttrMap;
    // 所有题目用户选择的答案集合
    public static ArrayList<ArrayList> selectedChoiceList;
    // 所有错题用户选择的答案集合
    public static ArrayList<ArrayList> wrongChoiceList;
    //用户答题情况answerFlag 0错误  1正确  -1未答
    public static String[] answerFlag;
    //用户试题分析评测结果
    public static ErPaperAttrVo erPaperAttrVo;
    //用户真题模考相关信息
    public static PaperItem paperItem;
    //用户真题模考主观题客观题题序保存
    public static Map<String, Integer> erPaperOrder;
    //用户真题模考客观题题序保存 保存题号顺序
    public static List<Integer> objExerciseList;
    //用户真题模考主观题题序保存 保存id
    public static List<String> subExerciseList;
    //删除的题目id集合
    public static List<Integer> delExerciseList;

    public static void resetDatas() {
        DataStore_ExamLibrary.selectedChoiceList = null;
        DataStore_ExamLibrary.wrongExerciseList = null;
        DataStore_ExamLibrary.wrongChoiceList = null;
        DataStore_ExamLibrary.answerFlag = null;
        DataStore_ExamLibrary.questionAttrMap = null;
        DataStore_ExamLibrary.questionDetailList = null;
        DataStore_ExamLibrary.erPaperAttrVo = null;
        DataStore_ExamLibrary.paperItem = null;
        DataStore_ExamLibrary.erPaperOrder = null;
        DataStore_ExamLibrary.objExerciseList = null;
        DataStore_ExamLibrary.subExerciseList = null;
        DataStore_ExamLibrary.delExerciseList = null;
    }
}
