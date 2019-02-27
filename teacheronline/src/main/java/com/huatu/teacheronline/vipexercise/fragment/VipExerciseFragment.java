package com.huatu.teacheronline.vipexercise.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.exercise.DataStore_ExamLibrary;
import com.huatu.teacheronline.exercise.bean.QuesAttrVo;
import com.huatu.teacheronline.utils.AnimationUtil;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.vipexercise.DataStore_VipExamLibrary;
import com.huatu.teacheronline.vipexercise.DoVipQuestionActivity;
import com.huatu.teacheronline.vipexercise.vipbean.VipQuestionBean;
import com.huatu.teacheronline.widget.ExpandableTextView;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichText;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 79937 on 2017/8/10.
 */
public class VipExerciseFragment extends Fragment {

    private View convertView;
    private Map<String, QuesAttrVo> questionAttrMap;
    private VipQuestionBean.ResultListBean questionDetail;
    private int mExercisetype;//mExercisetype 1错题解析 2全部解析 0正常做题
    private int mType;//错题 -> 底部三个按钮功能分别为答题卡、解析、垃圾桶 0 今日特训、1 模块题海、2真题估分 3 错题中心、4收藏 则依次显示为交卷、答题卡、收藏 -1是模块分数页面过来的全部解析或者错题解析 5模考大赛 -2是真题分数页面过来的全部解析或者错题解析
    private String choiceType = "";//题目类型
    private int choiceSum = 4;//默认选项
    private DoVipQuestionActivity context;
    private int ediLength;//当前页面填空题分割数 几个空需要加一
    private int position;//位置
    private static final String TAG = "ExerciseFragment";
    private DecimalFormat fnum = new DecimalFormat("##0.0");//小数点后一位
    private Map<Integer, String> chooseMap = new HashMap<Integer, String>() {
    };


    public TextView tv_content;//题目内容
    public TextView tv_parsing;//解析
    public TextView tv_check_parsing;//查看解析
    public TextView tv_quota;//滑动估分
    public ImageView img_video;//滑动估分
    public SeekBar sb_sliding_estimate;//滑块
    public LinearLayout ll_item_llchoose;//选项 布局
    public LinearLayout ll_item_llEdit;//填空题 布局
    public LinearLayout ll_sliding_estimate;//主观题底部估分 布局
    public RelativeLayout ll_video_resolution;//视频解析布局
    public LinearLayout ll_eva_subjective;//模考 主观题解析和评分标准 布局

    public LinearLayout ll_item_chooseA;
    public LinearLayout ll_item_chooseB;
    public LinearLayout ll_item_chooseC;
    public LinearLayout ll_item_chooseD;
    public LinearLayout ll_item_chooseE;
    public LinearLayout ll_item_chooseF;
    public LinearLayout ll_item_chooseG;
    public LinearLayout ll_item_chooseH;
    public LinearLayout ll_item_chooseI;
    public LinearLayout ll_item_chooseJ;
    public TextView tv_ex_optionsA;
    public TextView tv_ex_optionsB;
    public TextView tv_ex_optionsC;
    public TextView tv_ex_optionsD;
    public TextView tv_ex_optionsE;
    public TextView tv_ex_optionsF;
    public TextView tv_ex_optionsG;
    public TextView tv_ex_optionsH;
    public TextView tv_ex_optionsI;
    public TextView tv_ex_optionsJ;

    public TextView tv_selector_exchooseA;
    public TextView tv_selector_exchooseB;
    public TextView tv_selector_exchooseC;
    public TextView tv_selector_exchooseD;
    public TextView tv_selector_exchooseE;
    public TextView tv_selector_exchooseF;
    public TextView tv_selector_exchooseG;
    public TextView tv_selector_exchooseH;
    public TextView tv_selector_exchooseI;
    public TextView tv_selector_exchooseJ;

    public EditText edt_ex_edit1;
    public EditText edt_ex_edit2;
    public EditText edt_ex_edit3;
    public EditText edt_ex_edit4;
    public EditText edt_ex_edit5;
    public EditText edt_ex_edit6;
    public EditText edt_ex_edit7;
    public EditText edt_ex_edit8;
    public EditText edt_ex_edit9;
    public EditText edt_ex_edit10;

    public LinearLayout ll_item_edit1;
    public LinearLayout ll_item_edit2;
    public LinearLayout ll_item_edit3;
    public LinearLayout ll_item_edit4;
    public LinearLayout ll_item_edit5;
    public LinearLayout ll_item_edit6;
    public LinearLayout ll_item_edit7;
    public LinearLayout ll_item_edit8;
    public LinearLayout ll_item_edit9;
    public LinearLayout ll_item_edit10;
    public TextView tv_correct_error;//正确答案是
    public LinearLayout ll_toal_data;//全站数据
    public TextView tv_answer_times;//被作答次数
    public TextView tv_error_rate;//错误率
    public TextView tv_easy_wrong_item;//易错项
    public TextView btn_eva_standard;// 主观题解析 按钮
    public TextView btn_eva_parsing;//主观题评分标准 按钮
    public ExpandableTextView tv_eva_parsing;//主观题解析
    //        public TextView tv_eva_standard;//主观题评分标准
    public ImageView img_close_eva_parsing;//主观题 关闭评分 按钮
    public ScrollView sv_pager;//页面滚动
    private ArrayList<TextView> tv_chooses;
    private ArrayList<TextView> tv_selectors;
    private ArrayList<LinearLayout> ll_chooses;
    private ArrayList<LinearLayout> ll_Edits;
    private ArrayList<EditText> edt_Edits;
    public boolean isShowParsing;//是否打开了解析
    private boolean isCommonage = false;//是否共用题干题
    private LinearLayout ll_check_vip_parsing;//vip答案做题解析等等
    private TextView tv_answer_vip;//vip 你的答案是C
    private TextView tv_vip_parsing_value;//vip 答案
    private TextView tv_vip_analysis_value;//vip 答案解析
    private VipQuestionBean vipQuestionBean;//题目信息
    private TextView tv_vip_parsing;//本题答案
    private TextView tv_vip_analysis;//本题解析

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        questionDetail = (VipQuestionBean.ResultListBean) arguments.getSerializable("questionDetail");
        if ((isCommonage ? questionDetail.getChildTypeName() : questionDetail.getTypeName()).equals("共用题干题")) {
            isCommonage = true;
        }
        mExercisetype = arguments.getInt("mExercisetype");
        mType = arguments.getInt("mType");
        position = arguments.getInt("position");
        context = (DoVipQuestionActivity) getActivity();
        vipQuestionBean = DataStore_VipExamLibrary.VipQuestionBean;

        LayoutInflater inflater = getActivity().getLayoutInflater();
        convertView = inflater.inflate(R.layout.pager_do_exercise, null);
        questionAttrMap = DataStore_ExamLibrary.questionAttrMap;
        RichText.setScreenWidthHeight(CommonUtils.getScreenWidth(), CommonUtils.getScreenHeight());
        chooseMap.put(0, "A");
        chooseMap.put(1, "B");
        chooseMap.put(2, "C");
        chooseMap.put(3, "D");
        chooseMap.put(4, "E");
        chooseMap.put(5, "F");
        chooseMap.put(6, "G");
        chooseMap.put(7, "H");
        chooseMap.put(8, "I");
        chooseMap.put(9, "J");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        ViewGroup convertView = (ViewGroup) mMainView.getParent();
//        if (p != null) {
//            p.removeAllViewsInLayout();
//            DebugUtil.e("ExerciseFragment", "ExerciseFragment-->移除已存在的View");
//        }

        tv_content = (TextView) convertView.findViewById(R.id.tv_content);
        sv_pager = (ScrollView) convertView.findViewById(R.id.sv_pager);
        ll_item_llchoose = (LinearLayout) convertView.findViewById(R.id.ll_item_llchoose);
        ll_item_llEdit = (LinearLayout) convertView.findViewById(R.id.ll_item_llEdit);
        ll_sliding_estimate = (LinearLayout) convertView.findViewById(R.id.ll_sliding_estimate);
        ll_video_resolution = (RelativeLayout) convertView.findViewById(R.id.ll_video_resolution);
        ll_toal_data = (LinearLayout) convertView.findViewById(R.id.ll_toal_data);
        ll_eva_subjective = (LinearLayout) convertView.findViewById(R.id.ll_eva_subjective);
        ll_check_vip_parsing = (LinearLayout) convertView.findViewById(R.id.ll_check_vip_parsing);

        ll_item_chooseA = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseA);
        ll_item_chooseB = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseB);
        ll_item_chooseC = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseC);
        ll_item_chooseD = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseD);
        ll_item_chooseE = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseE);
        ll_item_chooseF = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseF);
        ll_item_chooseG = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseG);
        ll_item_chooseH = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseH);
        ll_item_chooseI = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseI);
        ll_item_chooseJ = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseJ);
        ll_item_chooseJ = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseJ);
        tv_ex_optionsA = (TextView) convertView.findViewById(R.id.tv_ex_optionsA);
        tv_ex_optionsB = (TextView) convertView.findViewById(R.id.tv_ex_optionsB);
        tv_ex_optionsC = (TextView) convertView.findViewById(R.id.tv_ex_optionsC);
        tv_ex_optionsD = (TextView) convertView.findViewById(R.id.tv_ex_optionsD);
        tv_ex_optionsE = (TextView) convertView.findViewById(R.id.tv_ex_optionsE);
        tv_ex_optionsF = (TextView) convertView.findViewById(R.id.tv_ex_optionsF);
        tv_ex_optionsG = (TextView) convertView.findViewById(R.id.tv_ex_optionsG);
        tv_ex_optionsH = (TextView) convertView.findViewById(R.id.tv_ex_optionsH);
        tv_ex_optionsI = (TextView) convertView.findViewById(R.id.tv_ex_optionsI);
        tv_ex_optionsJ = (TextView) convertView.findViewById(R.id.tv_ex_optionsJ);

        tv_selector_exchooseA = (TextView) convertView.findViewById(R.id.tv_selector_exchooseA);
        tv_selector_exchooseB = (TextView) convertView.findViewById(R.id.tv_selector_exchooseB);
        tv_selector_exchooseC = (TextView) convertView.findViewById(R.id.tv_selector_exchooseC);
        tv_selector_exchooseD = (TextView) convertView.findViewById(R.id.tv_selector_exchooseD);
        tv_selector_exchooseE = (TextView) convertView.findViewById(R.id.tv_selector_exchooseE);
        tv_selector_exchooseF = (TextView) convertView.findViewById(R.id.tv_selector_exchooseF);
        tv_selector_exchooseG = (TextView) convertView.findViewById(R.id.tv_selector_exchooseG);
        tv_selector_exchooseH = (TextView) convertView.findViewById(R.id.tv_selector_exchooseH);
        tv_selector_exchooseI = (TextView) convertView.findViewById(R.id.tv_selector_exchooseI);
        tv_selector_exchooseJ = (TextView) convertView.findViewById(R.id.tv_selector_exchooseJ);

        edt_ex_edit1 = (EditText) convertView.findViewById(R.id.edt_ex_edit1);
        edt_ex_edit2 = (EditText) convertView.findViewById(R.id.edt_ex_edit2);
        edt_ex_edit3 = (EditText) convertView.findViewById(R.id.edt_ex_edit3);
        edt_ex_edit4 = (EditText) convertView.findViewById(R.id.edt_ex_edit4);
        edt_ex_edit5 = (EditText) convertView.findViewById(R.id.edt_ex_edit5);
        edt_ex_edit6 = (EditText) convertView.findViewById(R.id.edt_ex_edit6);
        edt_ex_edit7 = (EditText) convertView.findViewById(R.id.edt_ex_edit7);
        edt_ex_edit8 = (EditText) convertView.findViewById(R.id.edt_ex_edit8);
        edt_ex_edit9 = (EditText) convertView.findViewById(R.id.edt_ex_edit9);
        edt_ex_edit10 = (EditText) convertView.findViewById(R.id.edt_ex_edit10);

        ll_item_edit1 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit1);
        ll_item_edit2 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit2);
        ll_item_edit3 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit3);
        ll_item_edit4 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit4);
        ll_item_edit5 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit5);
        ll_item_edit6 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit6);
        ll_item_edit7 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit7);
        ll_item_edit8 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit8);
        ll_item_edit9 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit9);
        ll_item_edit10 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit10);

        tv_parsing = (TextView) convertView.findViewById(R.id.tv_parsing);
        tv_check_parsing = (TextView) convertView.findViewById(R.id.tv_check_parsing);
        tv_answer_vip = (TextView) convertView.findViewById(R.id.tv_answer_vip);
        tv_vip_parsing_value = (TextView) convertView.findViewById(R.id.tv_vip_parsing_value);
        tv_vip_analysis_value = (TextView) convertView.findViewById(R.id.tv_vip_analysis_value);
        tv_vip_parsing = (TextView) convertView.findViewById(R.id.tv_vip_parsing);
        tv_vip_analysis = (TextView) convertView.findViewById(R.id.tv_vip_analysis);

        tv_quota = (TextView) convertView.findViewById(R.id.tv_quota);
        sb_sliding_estimate = (SeekBar) convertView.findViewById(R.id.sb_sliding_estimate);
        img_video = (ImageView) convertView.findViewById(R.id.img_video);
        tv_correct_error = (TextView) convertView.findViewById(R.id.tv_correct_error);
        tv_answer_times = (TextView) convertView.findViewById(R.id.tv_answer_times);
        tv_error_rate = (TextView) convertView.findViewById(R.id.tv_error_rate);
        tv_easy_wrong_item = (TextView) convertView.findViewById(R.id.tv_easy_wrong_item);
        btn_eva_standard = (TextView) convertView.findViewById(R.id.btn_eva_standard);
        btn_eva_parsing = (TextView) convertView.findViewById(R.id.btn_eva_parsing);
        tv_eva_parsing = (ExpandableTextView) convertView.findViewById(R.id.tv_eva_parsing);
//        tv_eva_standard = (TextView) convertView.findViewById(R.id.tv_eva_standard);
        img_close_eva_parsing = (ImageView) convertView.findViewById(R.id.img_close_eva_parsing);
        List<String> answersList = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
        //这个是联网请求得到的所有选项
        List<String> choicesList = isCommonage ? questionDetail.getChildOptions() : questionDetail.getOptions();//选项
        tv_chooses = new ArrayList<TextView>();
        tv_selectors = new ArrayList<TextView>();
        ll_chooses = new ArrayList<LinearLayout>();
        ll_Edits = new ArrayList<LinearLayout>();
        edt_Edits = new ArrayList<EditText>();
        tv_chooses.add(tv_ex_optionsA);
        tv_chooses.add(tv_ex_optionsB);
        tv_chooses.add(tv_ex_optionsC);
        tv_chooses.add(tv_ex_optionsD);
        tv_chooses.add(tv_ex_optionsE);
        tv_chooses.add(tv_ex_optionsF);
        tv_chooses.add(tv_ex_optionsG);
        tv_chooses.add(tv_ex_optionsH);
        tv_chooses.add(tv_ex_optionsI);
        tv_chooses.add(tv_ex_optionsJ);

        tv_selectors.add(tv_selector_exchooseA);
        tv_selectors.add(tv_selector_exchooseB);
        tv_selectors.add(tv_selector_exchooseC);
        tv_selectors.add(tv_selector_exchooseD);
        tv_selectors.add(tv_selector_exchooseE);
        tv_selectors.add(tv_selector_exchooseF);
        tv_selectors.add(tv_selector_exchooseG);
        tv_selectors.add(tv_selector_exchooseH);
        tv_selectors.add(tv_selector_exchooseI);
        tv_selectors.add(tv_selector_exchooseJ);

        edt_Edits.add(edt_ex_edit1);
        edt_Edits.add(edt_ex_edit2);
        edt_Edits.add(edt_ex_edit3);
        edt_Edits.add(edt_ex_edit4);
        edt_Edits.add(edt_ex_edit5);
        edt_Edits.add(edt_ex_edit6);
        edt_Edits.add(edt_ex_edit7);
        edt_Edits.add(edt_ex_edit8);
        edt_Edits.add(edt_ex_edit9);
        edt_Edits.add(edt_ex_edit10);

        ll_chooses.add(ll_item_chooseA);
        ll_chooses.add(ll_item_chooseB);
        ll_chooses.add(ll_item_chooseC);
        ll_chooses.add(ll_item_chooseD);
        ll_chooses.add(ll_item_chooseE);
        ll_chooses.add(ll_item_chooseF);
        ll_chooses.add(ll_item_chooseG);
        ll_chooses.add(ll_item_chooseH);
        ll_chooses.add(ll_item_chooseI);
        ll_chooses.add(ll_item_chooseJ);

        ll_Edits.add(ll_item_edit1);
        ll_Edits.add(ll_item_edit2);
        ll_Edits.add(ll_item_edit3);
        ll_Edits.add(ll_item_edit4);
        ll_Edits.add(ll_item_edit5);
        ll_Edits.add(ll_item_edit6);
        ll_Edits.add(ll_item_edit7);
        ll_Edits.add(ll_item_edit8);
        ll_Edits.add(ll_item_edit9);
        ll_Edits.add(ll_item_edit10);


        if (choicesList == null) {
            choiceSum = 0;
            ll_item_llchoose.setVisibility(View.GONE);
        } else if (choicesList.size() == 1 && StringUtils.isEmpty(choicesList.get(0))) {
            choiceSum = 0;
            ll_item_llchoose.setVisibility(View.GONE);
        }else {
            ll_item_llchoose.setVisibility(View.VISIBLE);
            choiceSum = choicesList.size();
            for (int i = 0; i < choiceSum; i++) {
                String substringQuestion = "";
                String choose = choicesList.get(i);
                if (choose.length() > 3 && choose.substring(0, 3).equals("<p>")) {
                    if (choose.length() > 10 && choose.substring(0, 10).contains("<p><img")) {
                        substringQuestion = choose;
                    } else {
                        substringQuestion = choose.trim().substring(3, choose.length() - 4);
                    }
                } else {
                    substringQuestion = choose;
                }
//                tv_chooses.get(i).setText(choicesList[i]);
                RichText.fromHtml(substringQuestion).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).clickable(true).into(tv_chooses.get(i));
            }
            for (int i1 = 9; i1 >= choiceSum; i1--) {
                ll_chooses.get(i1).setVisibility(View.GONE);
            }
        }
        if (null != answersList && answersList.size() > 1) {//根据答案判断多选题还是单选题
            choiceType = "<font color=\"#ff0000\">(多选题)</font>";
//            setViewDataAndClickListener(convertView, lv_chooselist, position, onClickListener);
            ll_sliding_estimate.setVisibility(View.GONE);

            for (int i = 0; i < tv_selectors.size(); i++) {
                tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.green004));
                tv_selectors.get(i).setBackgroundResource(R.drawable.bg_rectangle_green_hollow_more);
                if (mExercisetype != 0) {//错题和全部解析 不让点
                    ll_chooses.get(i).setClickable(false);
                    tv_chooses.get(i).setClickable(false);
                }
                String text = tv_selectors.get(i).getText() + "";
                for (int j = 0; j < context.selectedChoiceList.get(position).size(); j++) {
                    if (text.equals(context.selectedChoiceList.get(position).get(j))) {
                        tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.white));
                        tv_selectors.get(i).setBackgroundResource(R.drawable.bg_rectangle_green_more);
                    }
                }
            }


            //判断是否第一次进入多选题，要有toast提示 注：还需要正常模式做题下
//            if (!CommonUtils.getSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_MULTISELECT) && mExercisetype == 0) {
//                ToastUtils.showToastGestures(this);
//                CommonUtils.putSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_ISFRIST_MULTISELECT, true);
//            }
        } else if (null != answersList && answersList.size() == 1) {
            choiceType = "<font color=\"#ff0000\">(单选题)</font>";
            ll_sliding_estimate.setVisibility(View.GONE);
            for (int i = 0; i < tv_selectors.size(); i++) {
                tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.green004));
                tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_green_hollow);
                String text = tv_selectors.get(i).getText() + "";
                for (int j = 0; j < context.selectedChoiceList.get(position).size(); j++) {
                    if (text.equals(context.selectedChoiceList.get(position).get(j))) {
                        if (mExercisetype != 0 && !context.isCorrect(position)&& vipQuestionBean.getIsPushAnswer() == 1) {
                            tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.white));
                            tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_red);
                        } else {
                            tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.white));
                            tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_green);
                        }
                    }
                    if (mExercisetype != 0 && !context.isCorrect(position)&& vipQuestionBean.getIsPushAnswer() == 1) {
                        String s = StringUtils.changHtmlTo(StringUtils.changHtmlTo(StringUtils.decode1(isCommonage ? questionDetail.getChildAnswer().toString
                                () : StringUtils.decode1(isCommonage ? questionDetail.getChildAnswer().toString() : questionDetail.getAnswer().toString()))));

                        int i1 = valueGetKey(chooseMap, s.substring(2, 3));
                        tv_selectors.get(i1).setTextColor(context.getResources().getColor(R.color.white));
                        tv_selectors.get(i1).setBackgroundResource(R.drawable.bg_round_green);
                    }
                }
            }
//            setViewDataAndClickListener(convertView, lv_chooselist, position, onClickListener);
        }

        if (choiceSum == 2) {
            choiceType = "<font color=\"#ff0000\">(判断题)</font>";
            ll_sliding_estimate.setVisibility(View.GONE);
            ll_item_llEdit.setVisibility(View.GONE);
            for (int i = 0; i < tv_selectors.size(); i++) {
                tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.green004));
                tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_green_hollow);
                String text = tv_selectors.get(i).getText() + "";
                for (int j = 0; j < context.selectedChoiceList.get(position).size(); j++) {
                    if (text.equals(context.selectedChoiceList.get(position).get(j))) {
                        if (mExercisetype != 0 && !context.isCorrect(position)&& vipQuestionBean.getIsPushAnswer() == 1) {
                            tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.white));
                            tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_red);
                        } else {
                            tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.white));
                            tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_green);
                        }
                    }
                }
                if (mExercisetype != 0 && !context.isCorrect(position)&& vipQuestionBean.getIsPushAnswer() == 1) {
                    String s = StringUtils.changHtmlTo(StringUtils.changHtmlTo(StringUtils.decode1(StringUtils.decode1(isCommonage ? questionDetail
                            .getChildAnswer().toString() : questionDetail.getAnswer().toString()))));
                    DebugUtil.e("答案：" + s);
                    int i1 = valueGetKey(chooseMap, s);
                    tv_selectors.get(i1).setTextColor(context.getResources().getColor(R.color.white));
                    tv_selectors.get(i1).setBackgroundResource(R.drawable.bg_round_green);
                }
            }
//            setViewDataAndClickListener(convertView, lv_chooselist, position, onClickListener);
        }
        if ((isCommonage ? questionDetail.getChildTypeName() : questionDetail.getTypeName()).equals("填空题")) {
            choiceType = "<font color=\"#ff0000\">(填空题)</font>";
            ll_sliding_estimate.setVisibility(View.GONE);
            ll_item_llchoose.setVisibility(View.GONE);
            ll_item_llEdit.setVisibility(View.VISIBLE);
//            int ediLength = 0;

            if (answersList != null) {
                StringBuffer sbf = new StringBuffer();//获得填空题的字符串
                for (int i = 0; i < answersList.size(); i++) {
                    sbf.append(answersList.get(i));
                }
                String s = sbf.toString();
                ediLength = s.length() - s.replace("|", "").length();//得到“|”出现的次数
                for (int i = 9; i > ediLength; i--) {
                    ll_Edits.get(i).setVisibility(View.GONE);
                }
            }
            if (context.selectedChoiceList.get(position).size() > 0) {
                String edit = (String) context.selectedChoiceList.get(position).get(0);
                String[] split = edit.split("\\|");
                for (int i = 0; i < split.length; i++) {
                    DebugUtil.e(TAG, "我截取了：" + split[i]);
                    edt_Edits.get(i).setText(split[i]);
                }
            }

        }
        if ((isCommonage ? questionDetail.getChildTypeName() : questionDetail.getTypeName()).equals("主观题")) {
            choiceType = "<font color=\"#ff0000\">(主观题)</font>";
//                ll_sliding_estimate.setVisibility(View.GONE);
            ll_item_llchoose.setVisibility(View.GONE);
            tv_check_parsing.setVisibility(View.VISIBLE);
            double scro = questionDetail.getScore() * 10;
            final int max = (int) (scro / 5);
            sb_sliding_estimate.setMax(max);
            sb_sliding_estimate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress == 0) {
                        tv_quota.setText("滑动估分");
                    } else {
                        tv_quota.setText(0.5 * progress + "");
                    }
                    if (context.selectedChoiceList.get(position) != null) {
                        context.selectedChoiceList.get(position).clear();
                        context.selectedChoiceList.get(position).add(0.5 * progress + "");
                    } else {
                        context.selectedChoiceList.get(position).add(0.5 * progress + "");
                    }
                    float x = seekBar.getWidth();//seekbar的当前位置
                    int screenWidth = CommonUtils.getScreenWidth();
                    float seekbarWidth = sb_sliding_estimate.getX(); //seekbar的宽度
                    float width = (float) ((sb_sliding_estimate.getProgress() * x * 0.9) / max + seekbarWidth); //seekbar当前位置的宽度
                    tv_quota.setX(width);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            btn_eva_parsing.setTextSize(CommonUtils.px2sp(44));
            btn_eva_standard.setTextSize(CommonUtils.px2sp(40));
        }
        final String question = isCommonage ? questionDetail.getContent() + questionDetail.getChildContent() : questionDetail.getContent();
        String substringQuestion = "";
        if (question.length() > 3 && question.substring(0, 3).equals("<p>")) {
            if (question.length() > 10 && question.substring(0, 10).contains("<p><img")) {
                substringQuestion = question;
            } else {
                substringQuestion = question.trim().substring(3, question.length() - 4);
            }
        } else {
            substringQuestion = question;
        }
        DebugUtil.e(TAG, "question:" + questionDetail.toString());
        String comment = "";

        if (mType == 3) {
            if (isCommonage) {
                comment = "答案解析 \n" + questionDetail.getChildAnalysis();
                RichText.fromHtml(choiceType + substringQuestion).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_content);
//            tv_answer_vip;//vip 你的答案是C
//            tv_vip_parsing_value;//vip 答案
//            tv_vip_analysis_value;//vip 答案解析
                if (vipQuestionBean.getIsPushAnalysis() == 1) {
                    RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into
                            (tv_vip_analysis_value);
                } else {
                    RichText.fromHtml("老师未开放").autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_vip_analysis_value);
                }
                RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_parsing);

            } else {
                comment = "答案解析 \n" + questionDetail.getAnalysis();
                RichText.fromHtml(choiceType + substringQuestion).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_content);
                if (vipQuestionBean.getIsPushAnalysis() == 1) {
                    RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into
                            (tv_vip_analysis_value);
                } else {
                    RichText.fromHtml("老师未开放").autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_vip_analysis_value);
                }
                RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_eva_parsing);
                RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_parsing);
            }
        } else {
            if (isCommonage) {
                comment = "答案解析 \n" + questionDetail.getChildAnalysis();
                RichText.fromHtml(choiceType + substringQuestion).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_content);
                RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_parsing);
                RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_eva_parsing);
                if (vipQuestionBean.getIsPushAnalysis() == 1) {
                    RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into
                            (tv_vip_analysis_value);
                } else {
                    RichText.fromHtml("老师未开放").autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_vip_analysis_value);
                }

            } else {
                comment = "答案解析 \n" + questionDetail.getAnalysis();
                RichText.fromHtml(choiceType + substringQuestion).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_content);
                RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_parsing);
                RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_eva_parsing);
                if (vipQuestionBean.getIsPushAnalysis() == 1) {
                    RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into
                            (tv_vip_analysis_value);
                } else {
                    RichText.fromHtml("老师未开放").autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_vip_analysis_value);
                }

            }
        }
//        RichText.fromHtml(questionDetail.getExtension()).autoFix(false).scaleType(ImageScaleType.FIT_XY).into(tv_eva_standard);


        if (mExercisetype != 0) {//全部解析和错题解析 需要显示解析
            questionOtherShow(position);
        }/*else if(mExercisetype == 0 && mType == 3){
            context.vp_doexercise.setScrollable(false);
        }*/

        tv_check_parsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (context.isEvaluation()) {
//                    if (ll_eva_subjective.getVisibility() == View.VISIBLE) {
//                        ll_eva_subjective.setVisibility(View.GONE);
//                        ll_sliding_estimate.setVisibility(View.GONE);
//                        ll_eva_subjective.setAnimation(AnimationUtil.moveToViewBottom());
//                        ll_sliding_estimate.setAnimation(AnimationUtil.moveToViewBottom());
//                    } else {
//
//                        ll_sliding_estimate.setVisibility(View.VISIBLE);
//                        ll_eva_subjective.setVisibility(View.VISIBLE);
//                        ll_eva_subjective.setAnimation(AnimationUtil.moveToViewLocation());
//                        ll_sliding_estimate.setAnimation(AnimationUtil.moveToViewLocation());
//                        sv_pager.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                sv_pager.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
//                            }
//                        }, 350);
//
//                    }
//                } else {
                if (mExercisetype == 0) {
                    context.setMaxNum(position);
                    if (tv_parsing.getVisibility() == View.VISIBLE) {
                        tv_parsing.setVisibility(View.GONE);
                    } else {
                        tv_parsing.setVisibility(View.VISIBLE);
                    }
                }
//                }
            }
        });
        btn_eva_standard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_eva_standard.setTextColor(context.getResources().getColor(R.color.white));
                btn_eva_standard.setBackgroundResource(R.drawable.bt_ckjx_green);
                btn_eva_parsing.setTextColor(context.getResources().getColor(R.color.green004));
                btn_eva_parsing.setBackgroundResource(R.drawable.bt_ckjx_white);
                btn_eva_standard.setTextSize(CommonUtils.px2sp(44));
                btn_eva_parsing.setTextSize(CommonUtils.px2sp(40));
//                tv_eva_standard.setVisibility(View.VISIBLE);
//                tv_eva_parsing.setVisibility(View.GONE);
                RichText.fromHtml(questionDetail.getAnalysis()).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_eva_parsing);
            }
        });
        final String finalComment = comment;
        btn_eva_parsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_eva_parsing.setTextColor(context.getResources().getColor(R.color.white));
                btn_eva_parsing.setBackgroundResource(R.drawable.bt_ckjx_green);
                btn_eva_standard.setTextColor(context.getResources().getColor(R.color.green004));
                btn_eva_standard.setBackgroundResource(R.drawable.bt_ckjx_white);
                btn_eva_parsing.setTextSize(CommonUtils.px2sp(44));
                btn_eva_standard.setTextSize(CommonUtils.px2sp(40));
//                tv_eva_parsing.setVisibility(View.VISIBLE);
//                tv_eva_standard.setVisibility(View.GONE);
                RichText.fromHtml(finalComment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_eva_parsing);
            }
        });
        img_close_eva_parsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_eva_subjective.setVisibility(View.GONE);
                ll_sliding_estimate.setVisibility(View.GONE);
                ll_eva_subjective.setAnimation(AnimationUtil.moveToViewBottom());
                ll_sliding_estimate.setAnimation(AnimationUtil.moveToViewBottom());
            }
        });
        ll_item_chooseA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 0);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 0);
                }
            }
        });
        tv_ex_optionsA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 0);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 0);
                }
            }
        });
        ll_item_chooseB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 1);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 1);
                }
            }
        });
        tv_ex_optionsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 1);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 1);
                }
            }
        });
        ll_item_chooseC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 2);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 2);
                }
            }
        });
        tv_ex_optionsC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 2);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 2);
                }
            }
        });
        ll_item_chooseD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 3);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 3);
                }
            }
        });
        tv_ex_optionsD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 3);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 3);
                }
            }
        });
        ll_item_chooseE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 4);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 4);
                }
            }
        });
        tv_ex_optionsE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 4);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 4);
                }
            }
        });
        ll_item_chooseF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 5);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 5);
                }
            }
        });
        tv_ex_optionsF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 5);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 5);
                }
            }
        });
        ll_item_chooseG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 6);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 6);
                }
            }
        });
        tv_ex_optionsG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 6);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 6);
                }
            }
        });
        ll_item_chooseH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 7);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 7);
                }
            }
        });
        tv_ex_optionsH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 7);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 7);
                }
            }
        });
        ll_item_chooseI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 8);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 8);
                }
            }
        });
        tv_ex_optionsI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 8);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 8);
                }
            }
        });
        ll_item_chooseJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 9);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 9);
                }
            }
        });
        tv_ex_optionsJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> answersArr = isCommonage ? questionDetail.getChildAnswer() : questionDetail.getAnswer();
                if (null != answersArr && answersArr.size() > 1) {
                    //多选
                    moreChooseClick(tv_selectors, position, 9);
                } else {
                    //单选
                    sigChooseClick(tv_selectors, position, 9);
                }
            }
        });

        return convertView;
    }


    /**
     * 显示正确答案 视频解析 答案解析 全站数据等
     *
     * @param position
     */
    public void questionOtherShow(int position) {
        isShowParsing = true;
//        tv_parsing.setVisibility(View.VISIBLE);
//        tv_correct_error.setVisibility(View.VISIBLE);

        //用户选线，答案
        String userChoose1 = context.selectedChoiceList.get(position).toString();
        DebugUtil.e("userChoose1:"+userChoose1);
        String userChoose = StringUtils.changHtmlTo(userChoose1);
        //查看解析
        String htmlTo = StringUtils.decode1(isCommonage ? questionDetail.getChildAnswer().toString() : questionDetail.getAnswer().toString());//替换掉/u003d
        String answer = StringUtils.changHtmlTo(htmlTo);//将＞ ＜这些替换成html能识别的符号
        String correct = "";
        String replaceUserChoose = "";
        String replaceAnswer = "";
        if ((isCommonage ? questionDetail.getChildTypeName() : (isCommonage ? questionDetail.getChildTypeName() : questionDetail.getTypeName())).equals
                ("填空题")) {
            //填空题以|分割
            if (!StringUtils.isEmpty(userChoose)) {
                replaceUserChoose = userChoose.replace("|", ",");
            }
            if (!StringUtils.isEmpty(answer)) {
                replaceAnswer = answer.replace("|", ",");
            }
            if (context.isCorrect(position)) {
                correct = "正确答案是<font color=\"#31bc77\">" + replaceAnswer + "</font>你的答案是<font color=\"#ff5a5a\">" + replaceUserChoose + "</font>,回答正确";
            } else {
                correct = "正确答案是<font color=\"#31bc77\">" + replaceAnswer + "</font>你的答案是<font color=\"#ff5a5a\">" + replaceUserChoose + "</font>,回答错误";
            }
        } else {
            if (context.isCorrect(position)) {

                correct = "正确答案是<font color=\"#31bc77\">" + answer + "</font>你的答案是<font color=\"#ff5a5a\">" + userChoose + "</font>,回答正确";
            } else {
                correct = "正确答案是<font color=\"#31bc77\">" + answer + "</font>你的答案是<font color=\"#ff5a5a\">" + userChoose + "</font>,回答错误";
            }
        }

//        RichText.fromHtml(correct).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_correct_error);

//        if (mType == 3) {
        ll_check_vip_parsing.setVisibility(View.VISIBLE);
        String vip = "";
        if (context.isCorrect(position)) {
            vip = "</font>你的答案是<font color=\"#ff5a5a\">" + userChoose + "</font>,回答正确";
        } else {
            vip = "</font>你的答案是<font color=\"#ff5a5a\">" + userChoose + "</font>,回答错误";
        }
        String s = isCommonage ? questionDetail.getChildTypeName() : questionDetail.getTypeName();
        if (s.equals("主观题")) {
            RichText.fromHtml("").autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_answer_vip);
            tv_answer_vip.setVisibility(View.GONE);
            tv_vip_parsing_value.setVisibility(View.GONE);
            tv_vip_parsing.setVisibility(View.GONE);
        }else {
            tv_answer_vip.setVisibility(View.VISIBLE);
            tv_vip_parsing_value.setVisibility(View.VISIBLE);
            tv_vip_parsing.setVisibility(View.VISIBLE);
            RichText.fromHtml(vip).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_answer_vip);
        }
        RichText.fromHtml("正确答案是<font color=\"#31bc77\">" + answer).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_vip_parsing_value);
        if (vipQuestionBean.getIsPushAnswer() == 1) {
            RichText.fromHtml("正确答案是<font color=\"#31bc77\">" + answer).autoFix(false).scaleType(ImageHolder.ScaleType
                    .FIT_XY).into(tv_vip_parsing_value);
        } else {
            RichText.fromHtml("老师未开放").autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_vip_parsing_value);
        }


//        }else {
//            tv_parsing.setVisibility(View.VISIBLE);
//            tv_correct_error.setVisibility(View.VISIBLE);
//            RichText.fromHtml(correct).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(tv_correct_error);
//        }

//        if (null != questionAttrMap && questionAttrMap.size() > 0 && (isCommonage?questionDetail.getChildTypeName():questionDetail.getTypeName()).equals
// ("主观题")) {
//            //有全站数据
//            ll_toal_data.setVisibility(View.VISIBLE);
//            tv_answer_times.setText(context.questionAttrMap.get(questionDetail.getQuestionId()).getAnswercount());
//            String[] strings = context.questionAttrMap.get(questionDetail.getQuestionId()).getPrecision().split("%");
//            double wrongParent = 0.0;
//            if (strings.length > 0) {
//                String precision = strings[0];
//                wrongParent = (100 - Float.parseFloat(precision));
//            }
//            tv_error_rate.setText(fnum.format(wrongParent) + "%");
//            tv_easy_wrong_item.setText(context.questionAttrMap.get(questionDetail.getQuestionId()).getFallibility());
//        }

//        if (!StringUtils.isEmpty(questionDetail.getQvideo())) {
//            ll_video_resolution.setVisibility(View.VISIBLE);
//            String qvideo = questionDetail.getQvideo();
//            //有视频解析
//            String videos[] = qvideo.split("###");
//            final String videoId = videos[0];
//            final String video_uid = videos[1];
//            final String video_key = videos[2];
//            img_video.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //TODO 视频解析
//                    Intent intent = new Intent(context, VideoParseDetailsActivity.class);
//                    intent.putExtra("videoId", videoId);
//                    intent.putExtra("video_uid", video_uid);
//                    intent.putExtra("video_key", video_key);
//                    context.startActivity(intent);
//                }
//            });
//        }
        if ((isCommonage ? questionDetail.getChildTypeName() : questionDetail.getTypeName()).equals("主观题")) {
            //主观题
            tv_correct_error.setVisibility(View.GONE);
            ll_toal_data.setVisibility(View.GONE);
        } else if ((isCommonage ? questionDetail.getChildTypeName() : questionDetail.getTypeName()).equals("填空题")) {
            //填空题
            ll_toal_data.setVisibility(View.GONE);

            edt_ex_edit1.setEnabled(false);
            edt_ex_edit1.setEnabled(false);
            edt_ex_edit2.setEnabled(false);
            edt_ex_edit3.setEnabled(false);
            edt_ex_edit4.setEnabled(false);
            edt_ex_edit5.setEnabled(false);
            edt_ex_edit6.setEnabled(false);
            edt_ex_edit7.setEnabled(false);
            edt_ex_edit8.setEnabled(false);
            edt_ex_edit9.setEnabled(false);
            edt_ex_edit10.setEnabled(false);
        } else {
            ll_item_chooseA.setEnabled(false);
            ll_item_chooseB.setEnabled(false);
            ll_item_chooseC.setEnabled(false);
            ll_item_chooseD.setEnabled(false);
            ll_item_chooseE.setEnabled(false);
            ll_item_chooseF.setEnabled(false);
            ll_item_chooseG.setEnabled(false);
            ll_item_chooseH.setEnabled(false);
            ll_item_chooseI.setEnabled(false);
            ll_item_chooseJ.setEnabled(false);
            tv_ex_optionsA.setEnabled(false);
            tv_ex_optionsB.setEnabled(false);
            tv_ex_optionsC.setEnabled(false);
            tv_ex_optionsD.setEnabled(false);
            tv_ex_optionsE.setEnabled(false);
            tv_ex_optionsF.setEnabled(false);
            tv_ex_optionsG.setEnabled(false);
            tv_ex_optionsH.setEnabled(false);
            tv_ex_optionsI.setEnabled(false);
            tv_ex_optionsJ.setEnabled(false);
        }
    }

    /**
     * 多选题 点击事件
     *
     * @param tv_selectors 选项集合
     * @param thisPosition 选项位置
     */
    private void moreChooseClick(ArrayList<TextView> tv_selectors, int position, int thisPosition) {
        if (mExercisetype == 0) {//做题页面 才能点击
            ColorStateList textColors = tv_selectors.get(thisPosition).getTextColors();
            String text = tv_selectors.get(thisPosition).getText() + "";
            context.selectedDate.put(position + "", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            context.setMaxNum(position);
            if (textColors == ColorStateList.valueOf(Color.WHITE)) {
                tv_selectors.get(thisPosition).setTextColor(context.getResources().getColor(R.color.green004));
                tv_selectors.get(thisPosition).setBackgroundResource(R.drawable.bg_rectangle_green_hollow_more);
                context.selectedChoiceList.get(position).remove(text);
//                context.updateStudyRecord(0);
            } else {
                tv_selectors.get(thisPosition).setTextColor(Color.WHITE);
                tv_selectors.get(thisPosition).setBackgroundResource(R.drawable.bg_rectangle_green_more);
                context.selectedChoiceList.get(position).add(text);
//                context.updateStudyRecord(0);

            }
        }
    }


    /**
     * 单选题 点击事件
     *
     * @param tv_selectors 选项集合
     * @param position     当前题目位置
     * @param thisPosition 选项位置
     */
    private void sigChooseClick(ArrayList<TextView> tv_selectors, int position, int thisPosition) {
        if (mExercisetype == 0 && mType != 3) {//做题页面 并且不是错题中心并且不是试卷的收藏
            for (int i = 0; i < tv_selectors.size(); i++) {
                tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.green004));
                tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_green_hollow);
            }
            context.selectedChoiceList.get(position).clear();
            context.selectedChoiceList.get(position).add(tv_selectors.get(thisPosition).getText());
            context.selectedDate.put(position + "", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            tv_selectors.get(thisPosition).setTextColor(Color.WHITE);
            tv_selectors.get(thisPosition).setBackgroundResource(R.drawable.bg_round_green);
            context.moveToNext(position);
            context.setMaxNum(position);
        } else if ((mExercisetype == 0 && mType == 3)) {//错题中心 或者试卷的收藏
            for (int i = 0; i < tv_selectors.size(); i++) {
                tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.green004));
                tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_green_hollow);
            }
            context.selectedChoiceList.get(position).clear();
            context.selectedChoiceList.get(position).add(tv_selectors.get(thisPosition).getText());
            tv_selectors.get(thisPosition).setTextColor(Color.WHITE);
            tv_selectors.get(thisPosition).setBackgroundResource(R.drawable.bg_round_green);
            questionOtherShow(position);
        }
    }

    /**
     * 填空题 答案拼接
     *
     * @param currentNum
     */
    public void vocabularySaveData(int currentNum) {
        StringBuilder ediString = new StringBuilder();
        for (int i = 0; i < ediLength + 1; i++) {
            String text = edt_Edits.get(i).getText() + "";
            ediString = ediString.append(text + "|");
        }
        String lastResult = ediString.substring(0, ediString.length() - 1);
        context.selectedChoiceList.get(currentNum).clear();
        DebugUtil.e(TAG, "onPageScrollStateChangedListener lastResult: " + lastResult + " currentNum:" + currentNum);
        context.selectedChoiceList.get(currentNum).add(lastResult);
    }

    /**
     * 根据vaule查找key
     *
     * @param map
     * @param value
     * @return
     */
    private int valueGetKey(Map map, String value) {
        Set set = map.entrySet();
        int i = 0;
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().equals(value)) {
                i = (int) entry.getKey();
            }
        }
        return i;
    }
}
