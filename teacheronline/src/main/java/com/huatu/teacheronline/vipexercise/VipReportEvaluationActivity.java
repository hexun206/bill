package com.huatu.teacheronline.vipexercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.exercise.adapter.AnswercardGridAdapter;
import com.huatu.teacheronline.exercise.bean.QuesAttrVo;
import com.huatu.teacheronline.exercise.bean.erbean.PointanalysisBean;
import com.huatu.teacheronline.exercise.bean.erbean.QuestionsBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.ShareUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.vipexercise.adapter.VipResultChapterTreeAdapter;
import com.huatu.teacheronline.vipexercise.vipbean.PaperAnalysiBean;
import com.huatu.teacheronline.vipexercise.vipbean.VipPaperBean;
import com.huatu.teacheronline.vipexercise.vipbean.VipQuestionBean;
import com.huatu.teacheronline.widget.AnimatedExpandableListView;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.CustomGridView;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vip评测报告
 *
 * @author ljyu
 * @time 2016-12-19 11:34:38
 */
public class VipReportEvaluationActivity extends BaseActivity implements PopupWindow.OnDismissListener {
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
    private TextView tv_difficulty_reprot;//平均难度
    private TextView tv_share_this_result;//底部分享战果

    private RelativeLayout rl_continue;//继续做题
    private RelativeLayout rl_message;//留言老师
    private RelativeLayout rl_back_check;//返回查看

    private String titleName;
    private String[] answersFlag;
    private AnswercardGridAdapter answercardGridAdapter;//客观题答题卡
    private PaperAdapter paper_dapter;//试卷知识点分析adapter
    private SubAdpater subper_dapter;//主观题adapter
    private RelativeLayout rl_main_left;
    private List<VipQuestionBean.ResultListBean> wrongExerciseList;
    private LinearLayout ll_subAnswercard;//主观题答题卡
    private LinearLayout ll_objAnswercard;//客观题答题卡

    DecimalFormat df = new DecimalFormat("0.0");
    private RelativeLayout rl_main_right;
    private RelativeLayout rl_no_parsing;//没数据时候这个显示
    private LinearLayout rl_explain;
    private ImageView ib_main_right;
    private VipResultChapterTreeAdapter resultChapterTreeAdapter;//知识点
    private ScrollView sv_result_paper;//有数据时候这个显示


    private String exclusiveId = "";//试卷id
    private String uid;
    private ObtatinQuestionReportDataListener mObtatinQuestionReportDataListener;
    private PaperAnalysiBean mPaperAnalysiBean;//试卷分析实体类
    private List<PaperAnalysiBean.ObjectiveAnalysisBean> objectiveAnalysis;
    private List<PaperAnalysiBean.PointAnalysisBean> pointAnalysis;
    private List<PaperAnalysiBean.SubjectiveAnalysisBean> subjectiveAnalysis;
    private CustomAlertDialog mCustomLoadingDialog;
    private PopupWindow popupWindow;
    private VipQuestionBean vipQuestionBean;
    private VipPaperBean vipPaperBean;


    @Override
    public void initView() {
        setContentView(R.layout.activity_gufen_score_new);
        exclusiveId = getIntent().getStringExtra("exclusiveId");
        vipQuestionBean = DataStore_VipExamLibrary.VipQuestionBean;
        vipPaperBean = DataStore_VipExamLibrary.VipPaperBean;
        uid = CommonUtils.getSharedPreferenceItem(null, com.huatu.teacheronline.utils.UserInfo.KEY_SP_USERID, "");
        TextView tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.score_result);
        rl_main_right = (RelativeLayout) findViewById(R.id.rl_main_right);
        ib_main_right = (ImageView) findViewById(R.id.ib_main_right);
        ib_main_right.setImageResource(R.drawable.ic_share);
        ib_main_right.setVisibility(View.VISIBLE);
        rl_explain = (LinearLayout) findViewById(R.id.rl_explain);
        rl_main_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        ll_subAnswercard = (LinearLayout) findViewById(R.id.ll_answercard1);
        ll_objAnswercard = (LinearLayout) findViewById(R.id.ll_answercard);
        lv_mai_listview = (ListView) findViewById(R.id.lv_mai_listview);//主观题listview
        lv_analysis_listview = (AnimatedExpandableListView) findViewById(R.id.lv_analysis_listview);//试卷分析listview
        tv_answerRightNum_section = (TextView) findViewById(R.id.tv_answerRightNum_section);
        tv_paper_title = (TextView) findViewById(R.id.tv_paper_title);//试卷标题
        tv_realTime = (TextView) findViewById(R.id.tv_realTime);//实际用时
        tv_recommand_time = (TextView) findViewById(R.id.tv_recommand_time);//推荐用时
        tv_paper_title2 = (TextView) findViewById(R.id.tv_paper_title2);//试卷标题
        tv_realTime2 = (TextView) findViewById(R.id.tv_realTime2);//实际用时
        tv_recommand_time2 = (TextView) findViewById(R.id.tv_recommand_time2);//推荐用时
        rl_continue = (RelativeLayout) findViewById(R.id.rl_continue);
        rl_message = (RelativeLayout) findViewById(R.id.rl_message);
        rl_back_check = (RelativeLayout) findViewById(R.id.rl_back_check);
        gv_objective_answer = (CustomGridView) findViewById(R.id.gv_objective_answer);//客观题gridview
        tv_objective_score = (TextView) findViewById(R.id.tv_objective_score);//客观得分
        tv_objective_ratings = (TextView) findViewById(R.id.tv_objective_ratings);//客观得分率
        tv_subjective_score = (TextView) findViewById(R.id.tv_subjective_score);//主观题得分
        tv_subjective_rate = (TextView) findViewById(R.id.tv_subjective_rate);//主观题得分率
        tv_difficulty_reprot = (TextView) findViewById(R.id.tv_difficulty_reprot);//平均难度
        tv_share_this_result = (TextView) findViewById(R.id.tv_share_this_result);//平均难度
//        tv_current_ranking = (TextView) findViewById(R.id.tv_current_ranking);//排名
//        tv_defeat_numbers = (TextView) findViewById(R.id.tv_defeat_numbers);//打败人数
//        tv_paper_score = (TextView) findViewById(R.id.tv_paper_score);//打败人数
        tv_marks = (TextView) findViewById(R.id.tv_marks);//最高分
        tv_low = (TextView) findViewById(R.id.tv_low);//最低分
        tv_beat_value = (TextView) findViewById(R.id.tv_beat_value);//打败多少人
        tv_rank_value = (TextView) findViewById(R.id.tv_rank_value);//当前排名
        sv_result_paper = (ScrollView) findViewById(R.id.sv_result_paper);//当前排名
        tv_share_this_result.setVisibility(View.GONE);
        rl_continue.setVisibility(View.VISIBLE);
        rl_message.setVisibility(View.VISIBLE);
        rl_back_check.setVisibility(View.VISIBLE);
//        initsetData();
        mCustomLoadingDialog = new CustomAlertDialog(VipReportEvaluationActivity.this, R.layout.dialog_loading_custom);
        getEvaluationReport();


    }

    /**
     * 获取直播列表
     *
     * @param
     */
    public void getEvaluationReport() {
        mObtatinQuestionReportDataListener = new ObtatinQuestionReportDataListener(this);
        SendRequest.getEvaluationReport(uid, exclusiveId, mObtatinQuestionReportDataListener);
    }

    private static class ObtatinQuestionReportDataListener extends ObtainDataFromNetListener<PaperAnalysiBean, String> {
        private VipReportEvaluationActivity activity;

        public ObtatinQuestionReportDataListener(VipReportEvaluationActivity Activity) {
            activity = new WeakReference<>(Activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            activity.mCustomLoadingDialog.show();
        }

        @Override
        public void onSuccess(final PaperAnalysiBean res) {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.mCustomLoadingDialog.dismiss();
                        activity.flushQuestionContent_OnSucess(res);
//                        if (res!=null){
//                            weak_fragment.rl_nodata.setVisibility(View.GONE);
//                        }else{
//                            weak_fragment.rl_nodata.setVisibility(View.VISIBLE);
//                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (activity != null) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.mCustomLoadingDialog.dismiss();
                        activity.flushQuestionContent_OnFailure(res);
                    }
                });
            }
        }
    }

    private void flushQuestionContent_OnFailure(String res) {
        if (SendRequest.ERROR_NETWORK.equals(res)) {
            ToastUtils.showToast(R.string.network);
        } else {
            ToastUtils.showToast(R.string.server_error);
        }
    }

    private void flushQuestionContent_OnSucess(PaperAnalysiBean res) {
        if (res != null) {
            mPaperAnalysiBean = res;
            initsetData();
        }
    }

    private void initsetData() {
//        mId = getIntent().getLongExtra("id", -1);
//        mDaoUtils = DaoUtils.getInstance();
//        mStudyRecord = mDaoUtils.queryStudyRecords(mId);
        //保存全站数据 用在错题 和全部解析
//        caseNum = mStudyRecord.getExercisenum();
//        rightNum = mStudyRecord.getRightnum();
        titleName = mPaperAnalysiBean.getTitle();
        answersFlag = DataStore_VipExamLibrary.answerFlag;
        wrongExerciseList = DataStore_VipExamLibrary.wrongResultListBean;
        objectiveAnalysis = mPaperAnalysiBean.getObjectiveAnalysis();
        pointAnalysis = mPaperAnalysiBean.getPointAnalysis();
        subjectiveAnalysis = mPaperAnalysiBean.getSubjectiveAnalysis();

        //试卷分析
        tv_realTime.setText(getString(R.string.time_used) + " " + formatTimes(mPaperAnalysiBean.getActualTime()));
        tv_paper_title.setText(titleName);
        tv_recommand_time.setText(getString(R.string.time_recommand) + " " + formatTimes(mPaperAnalysiBean.getTime()));
        tv_realTime2.setText(getString(R.string.time_used) + " " + formatTimes(mPaperAnalysiBean.getActualTime()));
        tv_paper_title2.setText(titleName);
        tv_recommand_time2.setText(getString(R.string.time_recommand) + " " + formatTimes(mPaperAnalysiBean.getTime()));
        tv_answerRightNum_section.setText(CommonUtils.subZeroAndDot(mPaperAnalysiBean.getCorrectRate()));
//        tv_paper_score.setText(erPaperAttrVo.getMostscore() + "");
        tv_beat_value.setText(mPaperAnalysiBean.getDefeatNum() + "");
        tv_rank_value.setText(mPaperAnalysiBean.getRanking() + "");

        List<Integer> codeList = new ArrayList<>();// 题号集合
        Map<Integer, String> gridMap = new HashMap<>();// <题号，答题情况>
        QuesAttrVo quesAttrVo;

        //如果客观题不为空，设置客观题相关信息
        if (objectiveAnalysis != null && objectiveAnalysis.size() > 0) {
            //客观题
            tv_objective_score.setText("总分" + mPaperAnalysiBean.getObjectiveScoreSum() + "分，得分" + mPaperAnalysiBean.getObjectiveActualScoreSum() + "分 / ");
            tv_objective_ratings.setText("得分率：" + mPaperAnalysiBean.getObjectiveScoreRate());
            ll_objAnswercard.setVisibility(View.VISIBLE);
            for (int i = 0; i < objectiveAnalysis.size(); i++) {
                codeList.add(i + 1);
                boolean isRightFlg = objectiveAnalysis.get(i).isIsRightFlg();
                if (objectiveAnalysis.get(i).isAnsweredFlg()) {
                    if (isRightFlg) {
                        answersFlag[i] = "1";
                    } else {
                        answersFlag[i] = "0";
                    }
                } else {
                    answersFlag[i] = "-1";
                }

                gridMap.put(i + 1, answersFlag[i]);
            }
            answercardGridAdapter = new AnswercardGridAdapter(this, codeList, gridMap);
            gv_objective_answer.setAdapter(answercardGridAdapter);

        } else {
            ll_objAnswercard.setVisibility(View.GONE);
        }

        if (subjectiveAnalysis != null && subjectiveAnalysis.size() > 0) {
            ll_subAnswercard.setVisibility(View.GONE);
            //主观题
            tv_subjective_score.setText("总分" + mPaperAnalysiBean.getSubjectiveScoreSum() + "分，得分" + mPaperAnalysiBean.getObjectiveScoreAvg() + "分 / ");
            tv_subjective_rate.setText("得分率：" + mPaperAnalysiBean.getObjectiveScoreRate());
//            ArrayList<QuestionsBean> questionsBeans = mPaperAnalysiBean.getQuestions();
//            ArrayList<QuestionsBean> sortQuestionsBeans = getSortQuestionsBeans(questionsBeans);
//            subper_dapter = new SubAdpater(VipReportEvaluationActivity.this, sortQuestionsBeans);
//            lv_mai_listview.setAdapter(subper_dapter);
        } else {
            ll_subAnswercard.setVisibility(View.GONE);
        }
        //试卷分析
        tv_marks.setText("共" + mPaperAnalysiBean.getQuestionNum() + "题，答对" + mPaperAnalysiBean.getCorrectTimes() + "题，答错" + mPaperAnalysiBean.getErrorTimes()
                + "题，未答" + mPaperAnalysiBean.getUnansweredTimes() + "题");
        tv_low.setVisibility(View.GONE);
        tv_difficulty_reprot.setVisibility(View.VISIBLE);
        tv_difficulty_reprot.setText("平均难度：" + mPaperAnalysiBean.getDifficulty());
//        tv_low.setText("最低分：" + erPaperAttrVo.getMinscore());

//        paper_dapter = new PaperAdapter(ReportEvaluationActivity.this,erPaperAttrVo.getPointanalysis());
//        lv_analysis_listview.setAdapter(paper_dapter);
        resultChapterTreeAdapter = new VipResultChapterTreeAdapter(this, lv_analysis_listview);
        lv_analysis_listview.setAdapter(resultChapterTreeAdapter);
        resultChapterTreeAdapter.setData(pointAnalysis);
        resultChapterTreeAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(lv_mai_listview);
        setListViewHeightBasedOnChildren(lv_analysis_listview);
    }

//    /**
//     * 获取排序好的主观题
//     *
//     * @param questionsBeans
//     * @return
//     */
//    private ArrayList<QuestionsBean> getSortQuestionsBeans(ArrayList<QuestionsBean> questionsBeans) {
//        ArrayList<QuestionsBean> sortQuestionsBeans = new ArrayList<QuestionsBean>();
//        for (int i = 0; i < subExerciseList.size(); i++) {
//            String qid = subExerciseList.get(i);
//            for (int i1 = 0; i1 < questionsBeans.size(); i1++) {
//                if (qid.equals(questionsBeans.get(i1).getQid())) {
//                    sortQuestionsBeans.add(questionsBeans.get(i1));
//                }
//            }
//        }
//        return sortQuestionsBeans;
//    }

    @Override
    public void setListener() {
        rl_main_left.setOnClickListener(this);
        rl_main_right.setOnClickListener(this);
//        rl_explain.setOnClickListener(this);
        rl_continue.setOnClickListener(this);
        rl_back_check.setOnClickListener(this);
        rl_message.setOnClickListener(this);

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
//            case R.id.rl_explain:
//                ShareUtils.popShare(this, ShareUtils.url_appdownload_qq, ShareUtils.content_share, ShareUtils.title_share, false);
//                break;
            case R.id.rl_main_left:
                back();
                break;
            case R.id.rl_main_right:
//                ShareUtils.popShare(VipReportEvaluationActivity.this, ShareUtils.url_appdownload_qq, ShareUtils.content_share_evaluation_score, ShareUtils.title_share, true);
                SendRequest.getVipShareResultUrl(uid,vipPaperBean.getExclusiveId(), mPaperAnalysiBean.getDefeatNum()+"", mPaperAnalysiBean.getRanking()+"",
                        mPaperAnalysiBean.getDifficulty()+"",mPaperAnalysiBean.getCorrectRate(), new ObtainDataListener(this));

                break;
            case R.id.rl_continue:
                //继续做题
                back();
                break;
            case R.id.rl_back_check:
                //返回查看
//                DoVipQuestionActivity.newIntent(this, 1, -1);
                openPopupWindow(v);
                break;
            case R.id.rl_message:
                //留言老师
//                startActivity(new Intent(VipReportEvaluationActivity.this, MyLeaveMessageActivity.class));
                LeaTeacherActivity.newIntent(VipReportEvaluationActivity.this, vipPaperBean.getCreateUserId() + "", vipPaperBean.getCreateUser());
                break;
            case R.id.tv_pick_phone:
                DoVipQuestionActivity.newIntent(this, 2, -1);
                popupWindow.dismiss();
                break;
            case R.id.tv_pick_zone:
                if (vipQuestionBean.getIsPushAnalysis() == 1) {
                    if (null != wrongExerciseList && wrongExerciseList.size() > 0) {
                        DoVipQuestionActivity.newIntent(this, 1, -1);
                    } else {
                        ToastUtils.showToast(R.string.nowrongexercise_info);
                    }
                }else {
                    DoVipQuestionActivity.newIntent(this, 2, -1);
                }
                popupWindow.dismiss();
                break;
            case R.id.tv_cancel:
                popupWindow.dismiss();
                break;
        }
    }

    /**
     * 试卷知识点分析adapter
     */
    class PaperAdapter extends BaseAdapter {
        ArrayList<PointanalysisBean> pointAnalysis;
        Context cotext;

        public PaperAdapter(Activity cotext, ArrayList<PointanalysisBean> pointAnalysis) {
            this.cotext = cotext;
            this.pointAnalysis = pointAnalysis;
        }

        @Override
        public int getCount() {
            return pointAnalysis == null ? 0 : pointAnalysis.size();
        }

        @Override
        public PointanalysisBean getItem(int position) {
            return pointAnalysis.get(position);
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

        public void setData(ArrayList<PointanalysisBean> pointAnalysis) {
            this.pointAnalysis = pointAnalysis;
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
//            sub_viewHolder.tv_num_subjective.setText(erPaperOrder.get(item.getQid()) + "");
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

    public static void newIntent(Activity context, String exclusiveId) {
        Intent intent = new Intent(context, VipReportEvaluationActivity.class);
        intent.putExtra("exclusiveId", exclusiveId);
        context.startActivity(intent);
    }

    private void openPopupWindow(View v) {
        //防止重复按按钮
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        //设置PopupWindow的View
        View view = LayoutInflater.from(this).inflate(R.layout.popupwindow_vip_report, null);
        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        //设置背景,这个没什么效果，不添加会报错
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //设置点击弹窗外隐藏自身
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //设置动画
        popupWindow.setAnimationStyle(R.style.PopupWindow);
        //设置位置
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        //设置消失监听
        popupWindow.setOnDismissListener(this);
        //设置PopupWindow的View点击事件
        setOnPopupViewClick(view);
        //设置背景色
        setBackgroundAlpha(0.5f);
    }

    private void setOnPopupViewClick(View view) {
        TextView tv_pick_phone, tv_pick_zone, tv_cancel,tv_cotent_pop;
        View view_line_pop = view.findViewById(R.id.view_line_pop);
        tv_pick_phone = (TextView) view.findViewById(R.id.tv_pick_phone);
        tv_cotent_pop = (TextView) view.findViewById(R.id.tv_cotent_pop);
        tv_pick_zone = (TextView) view.findViewById(R.id.tv_pick_zone);
        if (vipQuestionBean.getIsPushAnalysis() == 1) {
            tv_cotent_pop.setText(R.string.analysis_pop_report);
            tv_pick_phone.setText(R.string.analysis_all);
            tv_pick_zone.setText(R.string.analysis_error);
        }else {
            tv_cotent_pop.setText(R.string.analysis_pop_report);
            tv_pick_phone.setVisibility(View.GONE);
            view_line_pop.setVisibility(View.GONE);
            tv_cotent_pop.setText(R.string.no_analysis_pop_report);
            tv_pick_zone.setTextColor(Color.RED);
            tv_pick_zone.setText("确认");
        }

        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);

        tv_pick_phone.setOnClickListener(this);
        tv_pick_zone.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    @Override
    public void onDismiss() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setBackgroundAlpha(1);
    }

    //设置屏幕背景透明效果
    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    private static class ObtainDataListener extends ObtainDataFromNetListener<String, String> {

        private VipReportEvaluationActivity weak_activity;

        public ObtainDataListener(VipReportEvaluationActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.mCustomLoadingDialog.dismiss();
                        if (!StringUtils.isEmpty(res)) {
                            ShareUtils.popShare(weak_activity, res, ShareUtils.content_share_evaluation_score, ShareUtils.title_share, false);
                        }else {
                            ToastUtils.showToast(R.string.server_error);
                        }
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
