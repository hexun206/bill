package com.huatu.teacheronline.exercise;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.greendao.DaoUtils;
import com.greendao.QuestionDetail;
import com.greendao.StudyRecords;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.adapter.AnswercardGridAdapter;
import com.huatu.teacheronline.exercise.adapter.ResultChapterTreeAdapter;
import com.huatu.teacheronline.exercise.bean.PaperItem;
import com.huatu.teacheronline.exercise.bean.QuesAttrVo;
import com.huatu.teacheronline.exercise.bean.erbean.ErPaperAttrVo;
import com.huatu.teacheronline.exercise.bean.erbean.PaperAnalysisTypeBean;
import com.huatu.teacheronline.exercise.bean.erbean.PointanalysisBean;
import com.huatu.teacheronline.exercise.bean.erbean.QuestionsBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ShareUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.AnimatedExpandableListView;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.CustomGridView;
import com.huatu.teacheronline.widget.PopwindowDoExercise;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 评测报告
 *
 * @author ljyu
 * @time 2016-12-19 11:34:38
 */
public class ReportEvaluationActivity extends BaseActivity {
    private ListView lv_mai_listview;
    private AnimatedExpandableListView lv_analysis_listview;
    private TextView tv_paper_title;
    private TextView tv_paper_title2;
    private TextView tv_answerRightNum_section;
    private TextView tv_realTime;
    private TextView tv_realTime2;
    private TextView tv_recommand_time;
    private TextView tv_recommand_time2;
    //    private TextView tv_wrong_explain;
//    private TextView tv_all_explain;
//    private TextView tv_continue_exercise;
    private CustomGridView gv_objective_answer;
    private TextView tv_objective_score;
    private TextView tv_objective_ratings;
    private TextView tv_subjective_score;
    private TextView tv_subjective_rate;
    //    private TextView tv_current_ranking;
//    private TextView tv_defeat_numbers;
    private TextView tv_marks;//最高分
    private TextView tv_low;//最低分
    private TextView tv_beat_value;
    private TextView tv_rank_value;

    private long mId;
    private StudyRecords mStudyRecord;
    private DaoUtils mDaoUtils;

    private int caseNum = 0;
    private int rightNum = 0;
    private String titleName;
    private String[] answersFlag;
    private ErPaperAttrVo erPaperAttrVo;//评测报告数据
    private String type;// 0随机联系  1顺序练习  2模拟题  3真题  4错题  5收藏
    //    private TextView tv_paper_score;//试卷总分
    private ArrayList<PaperAnalysisTypeBean> paperAnalysisTypeVo; //主观题客观题模块 type=1 主观题 type=0客观题
    private AnswercardGridAdapter answercardGridAdapter;//客观题答题卡

    private PaperAnalysisTypeBean paperAnalysisType0;//客观题
    private PaperAnalysisTypeBean paperAnalysisType1;//主观题
    private ArrayList<PointanalysisBean> pointanalysis;//知识点分析
    private PaperAdapter paper_dapter;//试卷知识点分析adapter
    private SubAdpater subper_dapter;//主观题adapter
    //    private int chooseNum  = 0;//客观题数量
    private RelativeLayout rl_main_left;
    private List<QuestionDetail> wrongExerciseList;
    private LinearLayout ll_subAnswercard;//主观题答题卡
    private LinearLayout ll_objAnswercard;//客观题答题卡

    DecimalFormat df = new DecimalFormat("0.0");
    private Map<String, Integer> erPaperOrder;
    private List<Integer> objExerciseList;
    private List<String> subExerciseList;
    private RelativeLayout rl_main_right;
    private RelativeLayout rl_no_parsing;//没数据时候这个显示
    private PopwindowDoExercise popwindowDoExercise;// popwindow
    private LinearLayout rl_explain;
    private ImageView ib_main_right;
    private ResultChapterTreeAdapter resultChapterTreeAdapter;//知识点
    private ScrollView sv_result_paper;//有数据时候这个显示
    private CustomAlertDialog mCustomLoadingDialog;
    private PaperItem paperItem;
    private boolean isRecord = false;//是否学习记录过来的


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void initView() {
        setContentView(R.layout.activity_gufen_score_new);
        TextView tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.score_result);
        paperItem = DataStore_ExamLibrary.paperItem;
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        ib_main_right = (ImageView) findViewById(R.id.ib_main_right);
        rl_main_right.setVisibility(View.VISIBLE);
//        ib_main_right.setImageResource(R.drawable.more_dot);
        ib_main_right.setVisibility(View.GONE);
        rl_explain = (LinearLayout) findViewById(R.id.rl_explain);
        TextView tv_main_right= (TextView) findViewById(R.id.tv_main_right);
        tv_main_right.setVisibility(View.VISIBLE);
        tv_main_right.setBackgroundResource(R.drawable.bg_rectangle_frame_green_radius);
        tv_main_right.setText(R.string.analysis);
        tv_main_right.setTextColor(getResources().getColor(R.color.green004));
        findViewById(R.id.line_continue).setVisibility(View.GONE);
        findViewById(R.id.line_message).setVisibility(View.GONE);
        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        ll_subAnswercard = (LinearLayout) findViewById(R.id.ll_answercard1);
        ll_objAnswercard = (LinearLayout) findViewById(R.id.ll_answercard);
        lv_mai_listview = (ListView) findViewById(R.id.lv_mai_listview);//主观题listview
        lv_analysis_listview = (AnimatedExpandableListView) findViewById(R.id.lv_analysis_listview);//试卷分析listview
        TextView tv_answerRightNum = (TextView) findViewById(R.id.tv_answerRightNum);
        tv_answerRightNum.setText("得分");
        TextView tv_answerRightpercent = (TextView) findViewById(R.id.tv_answerRightpercent);
        tv_answerRightpercent.setText("分");
        tv_answerRightNum_section = (TextView) findViewById(R.id.tv_answerRightNum_section);
        tv_answerRightNum_section = (TextView) findViewById(R.id.tv_answerRightNum_section);
        tv_paper_title = (TextView) findViewById(R.id.tv_paper_title);//试卷标题
        tv_realTime = (TextView) findViewById(R.id.tv_realTime);//实际用时
        tv_recommand_time = (TextView) findViewById(R.id.tv_recommand_time);//推荐用时
        tv_paper_title2 = (TextView) findViewById(R.id.tv_paper_title2);//试卷标题
        tv_realTime2 = (TextView) findViewById(R.id.tv_realTime2);//实际用时
        tv_recommand_time2 = (TextView) findViewById(R.id.tv_recommand_time2);//推荐用时
//        tv_wrong_explain = (TextView) findViewById(R.id.tv_wrong_explain);
//        tv_all_explain = (TextView) findViewById(R.id.tv_all_explain);//全部解析
//        tv_continue_exercise = (TextView) findViewById(R.id.tv_continue_exercise);//继续做题
        gv_objective_answer = (CustomGridView) findViewById(R.id.gv_objective_answer);//客观题gridview
        tv_objective_score = (TextView) findViewById(R.id.tv_objective_score);//客观得分
        tv_objective_ratings = (TextView) findViewById(R.id.tv_objective_ratings);//客观得分率
        tv_subjective_score = (TextView) findViewById(R.id.tv_subjective_score);//主观题得分
        tv_subjective_rate = (TextView) findViewById(R.id.tv_subjective_rate);//主观题得分率
//        tv_current_ranking = (TextView) findViewById(R.id.tv_current_ranking);//排名
//        tv_defeat_numbers = (TextView) findViewById(R.id.tv_defeat_numbers);//打败人数
//        tv_paper_score = (TextView) findViewById(R.id.tv_paper_score);//打败人数
        tv_marks = (TextView) findViewById(R.id.tv_marks);//最高分
        tv_low = (TextView) findViewById(R.id.tv_low);//最低分
        tv_beat_value = (TextView) findViewById(R.id.tv_beat_value);//打败多少人
        tv_rank_value = (TextView) findViewById(R.id.tv_rank_value);//当前排名
        sv_result_paper = (ScrollView) findViewById(R.id.sv_result_paper);//当前排名

        popwindowDoExercise = new PopwindowDoExercise(this, this);

        initData();
    }

    private void initData() {
        mId = getIntent().getLongExtra("id", -1);
        isRecord = getIntent().getBooleanExtra("isRecord", false);
        mDaoUtils = DaoUtils.getInstance();
        mStudyRecord = mDaoUtils.queryStudyRecords(mId);
        //保存全站数据 用在错题 和全部解析
        List<QuesAttrVo> qavlistRes = new LinkedList<QuesAttrVo>();
        Map questionAttrMap = new HashMap<>();

        caseNum = mStudyRecord.getExercisenum();
        rightNum = mStudyRecord.getRightnum();
        titleName = mStudyRecord.getName();
        answersFlag = DataStore_ExamLibrary.answerFlag;
        erPaperAttrVo = DataStore_ExamLibrary.erPaperAttrVo;
        wrongExerciseList = DataStore_ExamLibrary.wrongExerciseList;
        erPaperOrder = DataStore_ExamLibrary.erPaperOrder;
        objExerciseList = DataStore_ExamLibrary.objExerciseList;
        subExerciseList = DataStore_ExamLibrary.subExerciseList;
        type = mStudyRecord.getType();
        paperAnalysisTypeVo = erPaperAttrVo.getPaperAnalysisTypeVo();
        PaperAnalysisTypeBean paperAnalysisTypeBean0 = paperAnalysisTypeVo.get(0);
        if (paperAnalysisTypeVo.size() == 2) {
            PaperAnalysisTypeBean paperAnalysisTypeBean1 = paperAnalysisTypeVo.get(1);
            //type=0 客观题 1主观题
            if (Integer.parseInt(paperAnalysisTypeBean0.getType()) == 0) {
                paperAnalysisType0 = paperAnalysisTypeBean0;
                paperAnalysisType1 = paperAnalysisTypeBean1;
            } else {
                paperAnalysisType0 = paperAnalysisTypeBean1;
                paperAnalysisType1 = paperAnalysisTypeBean0;
            }
        } else {
            //type=0 客观题 1主观题
            if (Integer.parseInt(paperAnalysisTypeBean0.getType()) == 0) {
                paperAnalysisType0 = paperAnalysisTypeBean0;
            } else {
                paperAnalysisType1 = paperAnalysisTypeBean0;
            }
        }
        pointanalysis = erPaperAttrVo.getPointanalysis();
        //试卷分析
        tv_realTime.setText(getString(R.string.time_used) + " " + formatTimes((int) Float.parseFloat(erPaperAttrVo.getUsersecond())));
        tv_paper_title.setText(titleName);
        tv_recommand_time.setText(getString(R.string.time_recommand) + " " + formatTimes((int) Float.parseFloat(erPaperAttrVo.getMostsecond())));
        tv_realTime2.setText(getString(R.string.time_used) + " " + formatTimes((int) Float.parseFloat(erPaperAttrVo.getUsersecond())));
        tv_paper_title2.setText(titleName);
        tv_recommand_time2.setText(getString(R.string.time_recommand) + " " + formatTimes((int) Float.parseFloat(erPaperAttrVo.getMostsecond())));
        tv_answerRightNum_section.setText(Double.parseDouble(erPaperAttrVo.getUserscore()) + "");
//        tv_paper_score.setText(erPaperAttrVo.getMostscore() + "");
        tv_beat_value.setText(erPaperAttrVo.getOvermember());
        tv_rank_value.setText(erPaperAttrVo.getTheranking() + "");

        List<Integer> codeList = new ArrayList<>();// 题号集合
        Map<Integer, String> gridMap = new HashMap<>();// <题号，答题情况>
        QuesAttrVo quesAttrVo;

        //如果客观题不为空，设置客观题相关信息
        if (paperAnalysisType0 != null && paperAnalysisType0.getQuestions().size() > 0) {
            //客观题
            tv_objective_score.setText("总分" + paperAnalysisType0.getTypemostscore() + "分，得分" + paperAnalysisType0.getTypeuserscore() + "分 / ");
            tv_objective_ratings.setText("得分率：" + df.format(paperAnalysisType0.getAnalysis() * 100) + "%");
            ll_objAnswercard.setVisibility(View.VISIBLE);
            for (int i = 0; i < paperAnalysisType0.getQuestions().size(); i++) {
//                codeList.add(i + 1);
//                if(answersFlag[i].equals("-1")){
//                    answersFlag[i] = "0";
//                }
                QuestionsBean questionsBean = paperAnalysisType0.getQuestions().get(i);
//                gridMap.put(i + 1, answersFlag[i]);
                quesAttrVo = new QuesAttrVo();
                quesAttrVo.setQid(questionsBean.getQid());
                quesAttrVo.setAnswercount(questionsBean.getAnswercount());
                quesAttrVo.setFallibility(questionsBean.getFallibility());
                quesAttrVo.setPrecision(questionsBean.getPrecision());
                questionAttrMap.put(questionsBean.getQid(), quesAttrVo);
            }
//            chooseNum = paperAnalysisType0.getQuestions().size();
            //qu
            for (int i = 0; i < objExerciseList.size(); i++) {
                codeList.add(objExerciseList.get(i));
                if (answersFlag[objExerciseList.get(i) - 1].equals("-1")) {
                    answersFlag[objExerciseList.get(i) - 1] = "0";
                }
                gridMap.put(objExerciseList.get(i), answersFlag[objExerciseList.get(i) - 1]);
            }
            answercardGridAdapter = new AnswercardGridAdapter(this, codeList, gridMap);
            gv_objective_answer.setAdapter(answercardGridAdapter);

        } else {
            ll_objAnswercard.setVisibility(View.GONE);
        }

        if (paperAnalysisType1 != null && paperAnalysisType1.getQuestions().size() > 0) {
            ll_subAnswercard.setVisibility(View.VISIBLE);
//            for (int i = 0; i < paperAnalysisType1.getQuestions().size(); i++) {
//                QuestionsBean questionsBean = paperAnalysisType1.getQuestions().get(i);
//                quesAttrVo.setAnswercount(questionsBean.getAnswercount());
//                quesAttrVo.setFallibility(questionsBean.getFallibility());
//                quesAttrVo.setPrecision(questionsBean.getPrecision());
//                questionAttrMap.put(questionsBean.getQid(),quesAttrVo);
//            }
            //主观题
            tv_subjective_score.setText("总分" + paperAnalysisType1.getTypemostscore() + "分，得分" + paperAnalysisType1.getTypeuserscore() + "分 / ");
            tv_subjective_rate.setText("得分率：" + df.format(paperAnalysisType1.getAnalysis() * 100) + "%");
            ArrayList<QuestionsBean> questionsBeans = paperAnalysisType1.getQuestions();
            ArrayList<QuestionsBean> sortQuestionsBeans = getSortQuestionsBeans(questionsBeans);
            subper_dapter = new SubAdpater(ReportEvaluationActivity.this, sortQuestionsBeans);
            lv_mai_listview.setAdapter(subper_dapter);
        } else {
            ll_subAnswercard.setVisibility(View.GONE);
        }
        DataStore_ExamLibrary.questionAttrMap = questionAttrMap;
        //试卷分析
        tv_marks.setText("最高分：" + erPaperAttrVo.getMaxscore() + "，");
        tv_low.setText("最低分：" + erPaperAttrVo.getMinscore());

//        paper_dapter = new PaperAdapter(ReportEvaluationActivity.this,erPaperAttrVo.getPointanalysis());
//        lv_analysis_listview.setAdapter(paper_dapter);
        resultChapterTreeAdapter = new ResultChapterTreeAdapter(this, lv_analysis_listview);
        lv_analysis_listview.setAdapter(resultChapterTreeAdapter);
        resultChapterTreeAdapter.setData(erPaperAttrVo.getPointanalysis());
        resultChapterTreeAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(lv_mai_listview);
        setListViewHeightBasedOnChildren(lv_analysis_listview);
    }

    /**
     * 获取排序好的主观题
     *
     * @param questionsBeans
     * @return
     */
    private ArrayList<QuestionsBean> getSortQuestionsBeans(ArrayList<QuestionsBean> questionsBeans) {
        ArrayList<QuestionsBean> sortQuestionsBeans = new ArrayList<QuestionsBean>();
        for (int i = 0; i < subExerciseList.size(); i++) {
            String qid = subExerciseList.get(i);
            for (int i1 = 0; i1 < questionsBeans.size(); i1++) {
                if (qid.equals(questionsBeans.get(i1).getQid())) {
                    sortQuestionsBeans.add(questionsBeans.get(i1));
                }
            }
        }
        return sortQuestionsBeans;
    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
        rl_explain.setOnClickListener(this);
//        tv_wrong_explain.setOnClickListener(this);//错题解析
//        tv_all_explain.setOnClickListener(this);//全部解析
//        tv_continue_exercise.setOnClickListener(this);//继续做题
        // 这里是控制只有一个group展开的效果
        lv_analysis_listview.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < resultChapterTreeAdapter.getGroupCount(); i++) {
                    if (groupPosition != i) {
                        lv_analysis_listview.collapseGroup(i);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_collection_exercise:
                popwindowDoExercise.dismiss();
                openError();
                break;
            case R.id.tv_all_explain:
            case R.id.tv_correction_exercise:
                popwindowDoExercise.dismiss();
                openAllExercise();
                break;
            case R.id.tv_continue_exercise:
            case R.id.tv_share_exercise:
                popwindowDoExercise.dismiss();
                back();
                break;
            case R.id.rl_main_left:
                back();
                break;
            case R.id.rl_main_right:
                popwindowDoExercise.setData(3);
                popwindowDoExercise.show(rl_main_right);
                break;
            case R.id.rl_explain:
                SendRequest.getShareResultUrl(erPaperAttrVo.getOvermember(), erPaperAttrVo.getTheranking(), erPaperAttrVo.getUserscore(), null, new ObtainDataListener(this));
                break;
        }
    }

    /**
     * 打开全部解析
     */
    private void openAllExercise() {
        if (paperItem.onlyOne == 1) {
            //活动卷
            if (paperItem.open.equals("1")) {
                if (isRecord) {
                    //学习记录过来的 全部解析
                    DoVipExerciseActivity.newIntentEvaluationRecord(this, mId, 2, -2);
                }else {
                    DoVipExerciseActivity.newIntent(this, mId, 2, -2);
                }
            }else {
                EvaluationNoAnalysisActivity.newIntent(getString(R.string.exercise_analysisforall), titleName, getString(R.string
                        .time_used) + " " + formatTimes((int) Float.parseFloat(erPaperAttrVo.getUsersecond())), getString(R
                        .string.time_recommand) + " " + formatTimes((int) Float.parseFloat(erPaperAttrVo.getMostsecond())), this);

            }
        }else {
            //非活动卷
//            if (paperItem.openHour==-1) {
                DoVipExerciseActivity.newIntent(this, mId, 2, -2);
//            }else {
//                EvaluationNoAnalysisActivity.newIntent(getString(R.string.exercise_analysisforall), titleName, getString(R.string
//                        .time_used) + " " + formatTimes((int) Float.parseFloat(erPaperAttrVo.getUsersecond())), getString(R
//                        .string.time_recommand) + " " + formatTimes((int) Float.parseFloat(erPaperAttrVo.getMostsecond())), this);
//            }
        }
    }

    /**
     * 打开错题解析
     */
    private void openError() {
        if (paperItem.onlyOne == 1) {
            //活动卷
            if (paperItem.open.equals("1")) {
                startErrorExercise();
            }else {
                EvaluationNoAnalysisActivity.newIntent(getString(R.string.wrongexercise_analysis), titleName, getString(R.string
                        .time_used) + " " + formatTimes((int) Float.parseFloat(erPaperAttrVo.getUsersecond())), getString(R
                        .string.time_recommand) + " " + formatTimes((int) Float.parseFloat(erPaperAttrVo.getMostsecond())), this);

            }
        }else {
            //非活动卷
//            if (paperItem.openHour==-1) {
                startErrorExercise();
//            }else {
//                EvaluationNoAnalysisActivity.newIntent(getString(R.string.wrongexercise_analysis), titleName, getString(R.string
//                        .time_used) + " " + formatTimes((int) Float.parseFloat(erPaperAttrVo.getUsersecond())), getString(R
//                        .string.time_recommand) + " " + formatTimes((int) Float.parseFloat(erPaperAttrVo.getMostsecond())), this);
//            }
        }
    }

    private void startErrorExercise() {
        if (null != wrongExerciseList && wrongExerciseList.size() > 0) {
//                    DoExerciseActivity.newIntent(this, mId, 1,-2);
            if (isRecord) {
                //学习记录过来的 错题解析
                DoVipExerciseActivity.newIntentEvaluationRecord(this, mId, 1, -2);
            }else {
                DoVipExerciseActivity.newIntent(this, mId, 1, -2);
            }
        } else {
            ToastUtils.showToast(R.string.nowrongexercise_info);
        }
    }

    /**
     * 试卷知识点分析adapter
     */
    class PaperAdapter extends BaseAdapter {
        ArrayList<PointanalysisBean> pointanalysis;
        Context cotext;

        public PaperAdapter(Activity cotext, ArrayList<PointanalysisBean> pointanalysis) {
            this.cotext = cotext;
            this.pointanalysis = pointanalysis;
        }

        @Override
        public int getCount() {
            return pointanalysis == null ? 0 : pointanalysis.size();
        }

        @Override
        public PointanalysisBean getItem(int position) {
            return pointanalysis.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Paper_ViewHolder paper_viewHolder;
            if (convertView == null) {
                paper_viewHolder = new Paper_ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.layout_tab_list_item, null);
                paper_viewHolder.tv_point_pointanalysis = (TextView) convertView.findViewById(R.id.tv_point_pointanalysis);
                paper_viewHolder.tv_count_pointanalysis = (TextView) convertView.findViewById(R.id.tv_count_pointanalysis);
                paper_viewHolder.tv_rigthcount_pointanalysis = (TextView) convertView.findViewById(R.id.tv_rigthcount_pointanalysis);
                paper_viewHolder.tv_rate_pointanalysis = (TextView) convertView.findViewById(R.id.tv_rate_pointanalysis);
                convertView.setTag(paper_viewHolder);
            } else {
                paper_viewHolder = (Paper_ViewHolder) convertView.getTag();
            }
            PointanalysisBean item = getItem(position);
            DebugUtil.e("PointanalysisBean:" + item.toString());
            paper_viewHolder.tv_point_pointanalysis.setText(item.getPoint());
            paper_viewHolder.tv_count_pointanalysis.setText(item.getCount());
            paper_viewHolder.tv_rigthcount_pointanalysis.setText(item.getIsright());
//            if(item.getCorrectrate().length() > 4){
//                paper_viewHolder.tv_rate_pointanalysis.setText(df.format(Double.parseDouble(item.getCorrectrate()) * 100 + "%"));
//            }else {
            paper_viewHolder.tv_rate_pointanalysis.setText(df.format(Double.parseDouble(item.getCorrectrate()) * 100) + "%");
//            }
            return convertView;
        }

        public void setData(ArrayList<PointanalysisBean> pointanalysis) {
            this.pointanalysis = pointanalysis;
            notifyDataSetChanged();
        }
    }

    /**
     * 主观题adapter
     */
    class SubAdpater extends BaseAdapter {
        private Context cotext;
        private ArrayList<QuestionsBean> questions;

        public SubAdpater(Activity cotext, ArrayList<QuestionsBean> questions) {
            this.cotext = cotext;
            this.questions = questions;
        }

        @Override
        public int getCount() {
            return questions == null ? 0 : questions.size();
        }

        @Override
        public QuestionsBean getItem(int position) {
            return questions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Sub_ViewHolder sub_viewHolder;
            if (convertView == null) {
                sub_viewHolder = new Sub_ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_subjective_new, null);
                sub_viewHolder.tv_num_subjective = (TextView) convertView.findViewById(R.id.tv_num_subjective);
                sub_viewHolder.tv_score_subjective = (TextView) convertView.findViewById(R.id.tv_score_subjective);
                sub_viewHolder.tv_pro_subjective = (TextView) convertView.findViewById(R.id.tv_pro_subjective);
                sub_viewHolder.tv_total_subjective = (TextView) convertView.findViewById(R.id.tv_total_subjective);
                sub_viewHolder.pb_subjective = (SeekBar) convertView.findViewById(R.id.pb_subjective);
                convertView.setTag(sub_viewHolder);
            } else {
                sub_viewHolder = (Sub_ViewHolder) convertView.getTag();
            }
            final QuestionsBean item = getItem(position);
            sub_viewHolder.tv_num_subjective.setText(erPaperOrder.get(item.getQid()) + "");
            sub_viewHolder.tv_score_subjective.setText(item.getUserscore() + "");
            sub_viewHolder.tv_total_subjective.setText(item.getScore());
            final int max = (int) (Double.parseDouble(item.getScore()) * 100);
            final int pro = (int) (Double.parseDouble(item.getUserscore()) * 100);
            sub_viewHolder.pb_subjective.setMax(max);
            sub_viewHolder.pb_subjective.setProgress(pro);

            float x = sub_viewHolder.pb_subjective.getWidth();//seekbar的当前位置
            int screenWidth = CommonUtils.getScreenWidth();

            double v3 = Double.parseDouble(item.getScore()) / 10 + 50;
            sub_viewHolder.tv_pro_subjective.setX((float) (screenWidth * Double.parseDouble(item.getUserscore()) / Double.parseDouble(item.getScore()
            ) * 0.6 + v3));

            sub_viewHolder.tv_pro_subjective.setText(item.getUserscore() + "");


            return convertView;
        }


        public void setData(ArrayList<QuestionsBean> questions) {
            this.questions = questions;
            notifyDataSetChanged();
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    class Paper_ViewHolder {
        public TextView tv_point_pointanalysis;
        public TextView tv_count_pointanalysis;
        public TextView tv_rigthcount_pointanalysis;
        public TextView tv_rate_pointanalysis;
    }

    class Sub_ViewHolder {
        public TextView tv_num_subjective;
        public TextView tv_score_subjective;
        public TextView tv_total_subjective;
        public SeekBar pb_subjective;
        public TextView tv_pro_subjective;
    }

    /**
     * 时间换算成秒
     *
     * @param miss
     * @return
     */
    private static String formatTimes(long miss) {
        StringBuilder stringBuilder = new StringBuilder();
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "" + miss / 3600;
//        if (miss / 3600 > 0) {
        if (Integer.parseInt(hh) == 0) {
            stringBuilder.append("0" + hh + ":");
        } else if (Integer.parseInt(hh) <= 9) {
            stringBuilder.append("0" + hh + ":");
        } else {
            stringBuilder.append(hh + ":");
        }
//        }
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "" + (miss % 3600) / 60;
//        if ((miss % 3600) / 60 > 0) {
//            stringBuilder.append(mm + ":");
        if (Integer.parseInt(mm) == 0) {
            stringBuilder.append("0" + mm + ":");
        } else if (Integer.parseInt(mm) <= 9) {
            stringBuilder.append("0" + mm + ":");
        } else {
            stringBuilder.append(mm + ":");
        }
//        }
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "" + (miss % 3600) % 60;
//        if ((miss % 3600) % 60 > 0) {

        if (Integer.parseInt(ss) == 0) {
            stringBuilder.append("0" + ss);
        } else if (Integer.parseInt(ss) <= 9) {
            stringBuilder.append("0" + ss);
        } else {
            stringBuilder.append(ss);
        }
//        stringBuilder.append(ss);
//        }

        return stringBuilder.toString();
    }

    public static void newIntent(Activity context, long id) {
        Intent intent = new Intent(context, ReportEvaluationActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }
    public static void newIntent(Activity context, long id,boolean isRecord) {
        Intent intent = new Intent(context, ReportEvaluationActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isRecord", isRecord);
        context.startActivity(intent);
    }

    private static class ObtainDataListener extends ObtainDataFromNetListener<String, String> {

        private ReportEvaluationActivity weak_activity;

        public ObtainDataListener(ReportEvaluationActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();
                        ShareUtils.popShare(weak_activity, SendRequest.ipForExercise+res, ShareUtils.content_share_evaluation_score, ShareUtils.title_share, false);
                    }
                });
            }

        }

        @Override
        public void onStart() {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.setTitle(weak_activity.getString(R.string.loading));
                        weak_activity.mCustomLoadingDialog.show();
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();
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
}
