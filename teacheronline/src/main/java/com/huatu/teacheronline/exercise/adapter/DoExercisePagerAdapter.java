package com.huatu.teacheronline.exercise.adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.greendao.QuestionDetail;
import com.huatu.teacheronline.CCVideo.VideoParseDetailsActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.exercise.DataStore_ExamLibrary;
import com.huatu.teacheronline.exercise.DoVipExerciseActivity;
import com.huatu.teacheronline.exercise.bean.QuesAttrVo;
import com.huatu.teacheronline.utils.AnimationUtil;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.widget.ExpandableTextView;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 做题页面pageradapter
 * Created by ljyu on 2017/6/29.
 */
public class DoExercisePagerAdapter extends PagerAdapter implements DoVipExerciseActivity.PageScrollListener {
    private List<View> viewPagerItems;
    private Map<Integer, ArrayList<EditText>> EDT_Edits = new HashMap<Integer, ArrayList<EditText>>();
    private Map<Integer, ViewHolder> View_Holders = new HashMap<Integer, ViewHolder>();
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

//    public MyPagerAdapter(ArrayList<View> mListViews,AdapterView.OnItemClickListener onItemClickListener) {
//        this.mListViews = mListViews;
//        this.onItemClickListener = onItemClickListener;
//    }

    public DoExercisePagerAdapter(DoVipExerciseActivity context, List<View> viewPagerItems, String title, List<QuestionDetail> questionDetails, int
            mExercisetype, int mType) {
        this.viewPagerItems = viewPagerItems;
        this.questionDetails = questionDetails;
        this.mExercisetype = mExercisetype;
        this.mType = mType;
        this.title = title;
        this.context = context;
        context.setPageScrollListener(this);
        questionAttrMap = DataStore_ExamLibrary.questionAttrMap;
        RichText.setScreenWidthHeight(CommonUtils.getScreenWidth(), CommonUtils.getScreenHeight());

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewPagerItems.get(position));
    }

    @Override
    public int getCount() {
        return viewPagerItems == null ? 0 : viewPagerItems.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        if (position == viewPagerItems.size() - 1 && context.isDoExercise()) {
            View view = viewPagerItems.get(position);
            gv_answercard = (GridView) view.findViewById(R.id.gv_answercard);
            reFreshAnswerCard();
            container.addView(viewPagerItems.get(position));
            gv_answercard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Integer integer = codeList.get(position);
                    context.vp_doexercise.setCurrentItem(integer - 1);
                }
            });
            return viewPagerItems.get(position);
        } else {
            convertView = viewPagerItems.get(position);
            final ViewHolder holder = new ViewHolder();
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.sv_pager = (ScrollView) convertView.findViewById(R.id.sv_pager);
            holder.ll_item_llchoose = (LinearLayout) convertView.findViewById(R.id.ll_item_llchoose);
            holder.ll_item_llEdit = (LinearLayout) convertView.findViewById(R.id.ll_item_llEdit);
            holder.ll_sliding_estimate = (LinearLayout) convertView.findViewById(R.id.ll_sliding_estimate);
            holder.ll_video_resolution = (RelativeLayout) convertView.findViewById(R.id.ll_video_resolution);
            holder.ll_toal_data = (LinearLayout) convertView.findViewById(R.id.ll_toal_data);
            holder.ll_eva_subjective = (LinearLayout) convertView.findViewById(R.id.ll_eva_subjective);

            holder.ll_item_chooseA = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseA);
            holder.ll_item_chooseB = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseB);
            holder.ll_item_chooseC = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseC);
            holder.ll_item_chooseD = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseD);
            holder.ll_item_chooseE = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseE);
            holder.ll_item_chooseF = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseF);
            holder.ll_item_chooseG = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseG);
            holder.ll_item_chooseH = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseH);
            holder.ll_item_chooseI = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseI);
            holder.ll_item_chooseJ = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseJ);
            holder.ll_item_chooseJ = (LinearLayout) convertView.findViewById(R.id.ll_item_chooseJ);
            holder.tv_ex_optionsA = (TextView) convertView.findViewById(R.id.tv_ex_optionsA);
            holder.tv_ex_optionsB = (TextView) convertView.findViewById(R.id.tv_ex_optionsB);
            holder.tv_ex_optionsC = (TextView) convertView.findViewById(R.id.tv_ex_optionsC);
            holder.tv_ex_optionsD = (TextView) convertView.findViewById(R.id.tv_ex_optionsD);
            holder.tv_ex_optionsE = (TextView) convertView.findViewById(R.id.tv_ex_optionsE);
            holder.tv_ex_optionsF = (TextView) convertView.findViewById(R.id.tv_ex_optionsF);
            holder.tv_ex_optionsG = (TextView) convertView.findViewById(R.id.tv_ex_optionsG);
            holder.tv_ex_optionsH = (TextView) convertView.findViewById(R.id.tv_ex_optionsH);
            holder.tv_ex_optionsI = (TextView) convertView.findViewById(R.id.tv_ex_optionsI);
            holder.tv_ex_optionsJ = (TextView) convertView.findViewById(R.id.tv_ex_optionsJ);

            holder.tv_selector_exchooseA = (TextView) convertView.findViewById(R.id.tv_selector_exchooseA);
            holder.tv_selector_exchooseB = (TextView) convertView.findViewById(R.id.tv_selector_exchooseB);
            holder.tv_selector_exchooseC = (TextView) convertView.findViewById(R.id.tv_selector_exchooseC);
            holder.tv_selector_exchooseD = (TextView) convertView.findViewById(R.id.tv_selector_exchooseD);
            holder.tv_selector_exchooseE = (TextView) convertView.findViewById(R.id.tv_selector_exchooseE);
            holder.tv_selector_exchooseF = (TextView) convertView.findViewById(R.id.tv_selector_exchooseF);
            holder.tv_selector_exchooseG = (TextView) convertView.findViewById(R.id.tv_selector_exchooseG);
            holder.tv_selector_exchooseH = (TextView) convertView.findViewById(R.id.tv_selector_exchooseH);
            holder.tv_selector_exchooseI = (TextView) convertView.findViewById(R.id.tv_selector_exchooseI);
            holder.tv_selector_exchooseJ = (TextView) convertView.findViewById(R.id.tv_selector_exchooseJ);

            holder.edt_ex_edit1 = (EditText) convertView.findViewById(R.id.edt_ex_edit1);
            holder.edt_ex_edit2 = (EditText) convertView.findViewById(R.id.edt_ex_edit2);
            holder.edt_ex_edit3 = (EditText) convertView.findViewById(R.id.edt_ex_edit3);
            holder.edt_ex_edit4 = (EditText) convertView.findViewById(R.id.edt_ex_edit4);
            holder.edt_ex_edit5 = (EditText) convertView.findViewById(R.id.edt_ex_edit5);
            holder.edt_ex_edit6 = (EditText) convertView.findViewById(R.id.edt_ex_edit6);
            holder.edt_ex_edit7 = (EditText) convertView.findViewById(R.id.edt_ex_edit7);
            holder.edt_ex_edit8 = (EditText) convertView.findViewById(R.id.edt_ex_edit8);
            holder.edt_ex_edit9 = (EditText) convertView.findViewById(R.id.edt_ex_edit9);
            holder.edt_ex_edit10 = (EditText) convertView.findViewById(R.id.edt_ex_edit10);

            holder.ll_item_edit1 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit1);
            holder.ll_item_edit2 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit2);
            holder.ll_item_edit3 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit3);
            holder.ll_item_edit4 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit4);
            holder.ll_item_edit5 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit5);
            holder.ll_item_edit6 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit6);
            holder.ll_item_edit7 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit7);
            holder.ll_item_edit8 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit8);
            holder.ll_item_edit9 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit9);
            holder.ll_item_edit10 = (LinearLayout) convertView.findViewById(R.id.ll_item_edit10);

            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_progress = (TextView) convertView.findViewById(R.id.tv_progress);
            holder.tv_parsing = (TextView) convertView.findViewById(R.id.tv_parsing);
            holder.tv_check_parsing = (TextView) convertView.findViewById(R.id.tv_check_parsing);
            holder.tv_quota = (TextView) convertView.findViewById(R.id.tv_quota);
            holder.sb_sliding_estimate = (SeekBar) convertView.findViewById(R.id.sb_sliding_estimate);
            holder.img_video = (ImageView) convertView.findViewById(R.id.img_video);
            holder.tv_correct_error = (TextView) convertView.findViewById(R.id.tv_correct_error);
            holder.tv_answer_times = (TextView) convertView.findViewById(R.id.tv_answer_times);
            holder.tv_error_rate = (TextView) convertView.findViewById(R.id.tv_error_rate);
            holder.tv_easy_wrong_item = (TextView) convertView.findViewById(R.id.tv_easy_wrong_item);
            holder.btn_eva_standard = (TextView) convertView.findViewById(R.id.btn_eva_standard);
            holder.btn_eva_parsing = (TextView) convertView.findViewById(R.id.btn_eva_parsing);
            holder.tv_eva_parsing = (ExpandableTextView) convertView.findViewById(R.id.tv_eva_parsing);
//        holder.tv_eva_standard = (TextView) convertView.findViewById(R.id.tv_eva_standard);
            holder.img_close_eva_parsing = (ImageView) convertView.findViewById(R.id.img_close_eva_parsing);


            String[] answersList = questionDetails.get(position).answersArr;
            //这个是联网请求得到的所有选项
            String[] choicesList = questionDetails.get(position).choicesArr;//选项
            ArrayList<TextView> tv_chooses = new ArrayList<TextView>();
            final ArrayList<TextView> tv_selectors = new ArrayList<TextView>();
            final ArrayList<LinearLayout> ll_chooses = new ArrayList<LinearLayout>();
            ArrayList<LinearLayout> ll_Edits = new ArrayList<LinearLayout>();
            ArrayList<EditText> edt_Edits = new ArrayList<EditText>();
            EDT_Edits.put(position, edt_Edits);
            View_Holders.put(position, holder);
            tv_chooses.add(holder.tv_ex_optionsA);
            tv_chooses.add(holder.tv_ex_optionsB);
            tv_chooses.add(holder.tv_ex_optionsC);
            tv_chooses.add(holder.tv_ex_optionsD);
            tv_chooses.add(holder.tv_ex_optionsE);
            tv_chooses.add(holder.tv_ex_optionsF);
            tv_chooses.add(holder.tv_ex_optionsG);
            tv_chooses.add(holder.tv_ex_optionsH);
            tv_chooses.add(holder.tv_ex_optionsI);
            tv_chooses.add(holder.tv_ex_optionsJ);

            tv_selectors.add(holder.tv_selector_exchooseA);
            tv_selectors.add(holder.tv_selector_exchooseB);
            tv_selectors.add(holder.tv_selector_exchooseC);
            tv_selectors.add(holder.tv_selector_exchooseD);
            tv_selectors.add(holder.tv_selector_exchooseE);
            tv_selectors.add(holder.tv_selector_exchooseF);
            tv_selectors.add(holder.tv_selector_exchooseG);
            tv_selectors.add(holder.tv_selector_exchooseH);
            tv_selectors.add(holder.tv_selector_exchooseI);
            tv_selectors.add(holder.tv_selector_exchooseJ);

            edt_Edits.add(holder.edt_ex_edit1);
            edt_Edits.add(holder.edt_ex_edit2);
            edt_Edits.add(holder.edt_ex_edit3);
            edt_Edits.add(holder.edt_ex_edit4);
            edt_Edits.add(holder.edt_ex_edit5);
            edt_Edits.add(holder.edt_ex_edit6);
            edt_Edits.add(holder.edt_ex_edit7);
            edt_Edits.add(holder.edt_ex_edit8);
            edt_Edits.add(holder.edt_ex_edit9);
            edt_Edits.add(holder.edt_ex_edit10);

            ll_chooses.add(holder.ll_item_chooseA);
            ll_chooses.add(holder.ll_item_chooseB);
            ll_chooses.add(holder.ll_item_chooseC);
            ll_chooses.add(holder.ll_item_chooseD);
            ll_chooses.add(holder.ll_item_chooseE);
            ll_chooses.add(holder.ll_item_chooseF);
            ll_chooses.add(holder.ll_item_chooseG);
            ll_chooses.add(holder.ll_item_chooseH);
            ll_chooses.add(holder.ll_item_chooseI);
            ll_chooses.add(holder.ll_item_chooseJ);

            ll_Edits.add(holder.ll_item_edit1);
            ll_Edits.add(holder.ll_item_edit2);
            ll_Edits.add(holder.ll_item_edit3);
            ll_Edits.add(holder.ll_item_edit4);
            ll_Edits.add(holder.ll_item_edit5);
            ll_Edits.add(holder.ll_item_edit6);
            ll_Edits.add(holder.ll_item_edit7);
            ll_Edits.add(holder.ll_item_edit8);
            ll_Edits.add(holder.ll_item_edit9);
            ll_Edits.add(holder.ll_item_edit10);

            if (choicesList == null) {
                choiceSum = 0;
                holder.ll_item_llchoose.setVisibility(View.GONE);
            } else if (choicesList.length == 1 && StringUtils.isEmpty(choicesList[0])) {
                choiceSum = 0;
                holder.ll_item_llchoose.setVisibility(View.GONE);
            }
            {
                holder.ll_item_llchoose.setVisibility(View.VISIBLE);
                choiceSum = choicesList.length;
                for (int i = 0; i < choiceSum; i++) {
                    String substringQuestion = "";
                    String choose = choicesList[i];
                    if (choose.length() > 3 && choose.substring(0, 3).equals("<p>")) {
                        if (choose.length() > 10 && choose.substring(0, 10).contains("<p><img")) {
                            substringQuestion = choose;
                        } else {
                            substringQuestion = choose.trim().substring(3, choose.length() - 5);
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
            if (null != answersList && answersList.length > 1) {//根据答案判断多选题还是单选题
                choiceType = "<font color=\"#ff0000\">(多选题)</font>";
//            setViewDataAndClickListener(convertView, holder.lv_chooselist, position, onClickListener);
                holder.ll_sliding_estimate.setVisibility(View.GONE);

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
            } else if (null != answersList && answersList.length == 1) {
                choiceType = "<font color=\"#ff0000\">(单选题)</font>";
                holder.ll_sliding_estimate.setVisibility(View.GONE);
                for (int i = 0; i < tv_selectors.size(); i++) {
                    tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.green004));
                    tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_green_hollow);
                    String text = tv_selectors.get(i).getText() + "";
                    for (int j = 0; j < context.selectedChoiceList.get(position).size(); j++) {
                        if (text.equals(context.selectedChoiceList.get(position).get(j))) {
                            tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.white));
                            tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_green);
                        }
                    }
                }
//            setViewDataAndClickListener(convertView, holder.lv_chooselist, position, onClickListener);
            }

            if (choiceSum == 2) {
                choiceType = "<font color=\"#ff0000\">(判断题)</font>";
                holder.ll_sliding_estimate.setVisibility(View.GONE);
                holder.ll_item_llEdit.setVisibility(View.GONE);
                for (int i = 0; i < tv_selectors.size(); i++) {
                    tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.green004));
                    tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_green_hollow);
                    String text = tv_selectors.get(i).getText() + "";
                    for (int j = 0; j < context.selectedChoiceList.get(position).size(); j++) {
                        if (text.equals(context.selectedChoiceList.get(position).get(j))) {
                            tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.white));
                            tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_green);
                        }
                    }
                }
//            setViewDataAndClickListener(convertView, holder.lv_chooselist, position, onClickListener);
            }
            if (Integer.parseInt(questionDetails.get(position).getQtype()) == 5) {
                choiceType = "<font color=\"#ff0000\">(填空题)</font>";
                holder.ll_sliding_estimate.setVisibility(View.GONE);
                holder.ll_item_llchoose.setVisibility(View.GONE);
                holder.ll_item_llEdit.setVisibility(View.VISIBLE);
//            int ediLength = 0;

                if (answersList != null) {
                    StringBuffer sbf = new StringBuffer();//获得填空题的字符串
                    for (int i = 0; i < answersList.length; i++) {
                        sbf.append(answersList[i]);
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
            if (Integer.parseInt(questionDetails.get(position).getQtype()) == 4) {
                choiceType = "<font color=\"#ff0000\">(主观题)</font>";
//                holder.ll_sliding_estimate.setVisibility(View.GONE);
                holder.ll_item_llchoose.setVisibility(View.GONE);
                holder.tv_check_parsing.setVisibility(View.VISIBLE);
                double scro = Double.parseDouble(questionDetails.get(position).getScore()) * 10;
                final int max = (int) (scro / 5);
                holder.sb_sliding_estimate.setMax(max);
                holder.sb_sliding_estimate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (progress == 0) {
                            holder.tv_quota.setText("滑动估分");
                        } else {
                            holder.tv_quota.setText(0.5 * progress + "");
                        }
                        if (context.selectedChoiceList.get(position) != null) {
                            context.selectedChoiceList.get(position).clear();
                            context.selectedChoiceList.get(position).add(0.5 * progress + "");
                        } else {
                            context.selectedChoiceList.get(position).add(0.5 * progress + "");
                        }
                        float x = seekBar.getWidth();//seekbar的当前位置
                        int screenWidth = CommonUtils.getScreenWidth();
                        float seekbarWidth = holder.sb_sliding_estimate.getX(); //seekbar的宽度
                        float width = (float) ((holder.sb_sliding_estimate.getProgress() * x * 0.9) / max + seekbarWidth); //seekbar当前位置的宽度
                        holder.tv_quota.setX(width);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                holder.btn_eva_parsing.setTextSize(CommonUtils.px2sp(44));
                holder.btn_eva_standard.setTextSize(CommonUtils.px2sp(40));
            }
            final String question = questionDetails.get(position).getQuestion();
            String substringQuestion = "";
            if (question.length() > 3 && question.substring(0, 3).equals("<p>")) {
                if (question.length() > 10 && question.substring(0, 10).contains("<p><img")) {
                    substringQuestion = question;
                } else {
                    substringQuestion = question.trim().substring(3, question.length() - 5);
                }
            } else {
                substringQuestion = question;
            }
            final String comment = "答案解析 \n" + questionDetails.get(position).getComment();
            DebugUtil.e(TAG, "question:" + questionDetails.get(position).toString());
            RichText.fromHtml(choiceType + substringQuestion).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(holder.tv_content);
            RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(holder.tv_parsing);
            RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(holder.tv_eva_parsing);
//        RichText.fromHtml(questionDetails.get(position).getExtension()).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(holder.tv_eva_standard);


            if (mExercisetype != 0) {//全部解析和错题解析 需要显示解析
                questionOtherShow(position, holder);
            }/*else if(mExercisetype == 0 && mType == 3){
            context.vp_doexercise.setScrollable(false);
        }*/

            holder.title.setText(title);
            holder.tv_progress.setText(position + 1 + "/" + questionDetails.size());

            holder.tv_check_parsing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context.isEvaluation()) {
                        if (holder.ll_eva_subjective.getVisibility() == View.VISIBLE) {
                            holder.ll_eva_subjective.setVisibility(View.GONE);
                            holder.ll_sliding_estimate.setVisibility(View.GONE);
                            holder.ll_eva_subjective.setAnimation(AnimationUtil.moveToViewBottom());
                            holder.ll_sliding_estimate.setAnimation(AnimationUtil.moveToViewBottom());
                        } else {

                            holder.ll_sliding_estimate.setVisibility(View.VISIBLE);
                            holder.ll_eva_subjective.setVisibility(View.VISIBLE);
                            holder.ll_eva_subjective.setAnimation(AnimationUtil.moveToViewLocation());
                            holder.ll_sliding_estimate.setAnimation(AnimationUtil.moveToViewLocation());
                            holder.sv_pager.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    holder.sv_pager.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                                }
                            }, 350);

                        }
                    } else {
                        if (holder.tv_parsing.getVisibility() == View.VISIBLE) {
                            holder.tv_parsing.setVisibility(View.GONE);
                        } else {
                            holder.tv_parsing.setVisibility(View.VISIBLE);
                        }
                    }

                }
            });
            holder.btn_eva_standard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.btn_eva_standard.setTextColor(context.getResources().getColor(R.color.white));
                    holder.btn_eva_standard.setBackgroundResource(R.drawable.bt_ckjx_green);
                    holder.btn_eva_parsing.setTextColor(context.getResources().getColor(R.color.green004));
                    holder.btn_eva_parsing.setBackgroundResource(R.drawable.bt_ckjx_white);
                    holder.btn_eva_standard.setTextSize(CommonUtils.px2sp(44));
                    holder.btn_eva_parsing.setTextSize(CommonUtils.px2sp(40));
//                holder.tv_eva_standard.setVisibility(View.VISIBLE);
//                holder.tv_eva_parsing.setVisibility(View.GONE);
                    RichText.fromHtml(questionDetails.get(position).getExtension()).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(holder
                            .tv_eva_parsing);
                }
            });
            holder.btn_eva_parsing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.btn_eva_parsing.setTextColor(context.getResources().getColor(R.color.white));
                    holder.btn_eva_parsing.setBackgroundResource(R.drawable.bt_ckjx_green);
                    holder.btn_eva_standard.setTextColor(context.getResources().getColor(R.color.green004));
                    holder.btn_eva_standard.setBackgroundResource(R.drawable.bt_ckjx_white);
                    holder.btn_eva_parsing.setTextSize(CommonUtils.px2sp(44));
                    holder.btn_eva_standard.setTextSize(CommonUtils.px2sp(40));
//                holder.tv_eva_parsing.setVisibility(View.VISIBLE);
//                holder.tv_eva_standard.setVisibility(View.GONE);
                    RichText.fromHtml(comment).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(holder.tv_eva_parsing);
                }
            });
            holder.img_close_eva_parsing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.ll_eva_subjective.setVisibility(View.GONE);
                    holder.ll_sliding_estimate.setVisibility(View.GONE);
                    holder.ll_eva_subjective.setAnimation(AnimationUtil.moveToViewBottom());
                    holder.ll_sliding_estimate.setAnimation(AnimationUtil.moveToViewBottom());
                }
            });
            container.addView(viewPagerItems.get(position));
            holder.ll_item_chooseA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 0);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 0);
                    }
                }
            });
            holder.tv_ex_optionsA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 0);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 0);
                    }
                }
            });
            holder.ll_item_chooseB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 1);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 1);
                    }
                }
            });
            holder.tv_ex_optionsB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 1);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 1);
                    }
                }
            });
            holder.ll_item_chooseC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 2);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 2);
                    }
                }
            });
            holder.tv_ex_optionsC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 2);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 2);
                    }
                }
            });
            holder.ll_item_chooseD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 3);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 3);
                    }
                }
            });
            holder.tv_ex_optionsD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 3);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 3);
                    }
                }
            });
            holder.ll_item_chooseE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 4);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 4);
                    }
                }
            });
            holder.tv_ex_optionsE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 4);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 4);
                    }
                }
            });
            holder.ll_item_chooseF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 5);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 5);
                    }
                }
            });
            holder.tv_ex_optionsF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 5);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 5);
                    }
                }
            });
            holder.ll_item_chooseG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 6);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 6);
                    }
                }
            });
            holder.tv_ex_optionsG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 6);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 6);
                    }
                }
            });
            holder.ll_item_chooseH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 7);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 7);
                    }
                }
            });
            holder.tv_ex_optionsH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 7);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 7);
                    }
                }
            });
            holder.ll_item_chooseI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 8);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 8);
                    }
                }
            });
            holder.tv_ex_optionsI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 8);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 8);
                    }
                }
            });
            holder.ll_item_chooseJ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 9);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 9);
                    }
                }
            });
            holder.tv_ex_optionsJ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] answersArr = questionDetails.get(position).answersArr;
                    if (null != answersArr && answersArr.length > 1) {
                        //多选
                        moreChooseClick(tv_selectors, position, 9);
                    } else {
                        //单选
                        sigChooseClick(tv_selectors, position, 9);
                    }
                }
            });
            return viewPagerItems.get(position);
        }
    }

    /**
     * 刷新答题卡页面
     */
    public void reFreshAnswerCard() {
        String[] answersFlag = context.startAnswerCardPager();
        codeList.clear();
        gridMap.clear();
        if (mExercisetype == 1) {
            for (int i = 0; i < questionDetails.size(); i++) {
                codeList.add(i + 1);
                gridMap.put(i + 1, "0");
            }
        } else {
            for (int i = 0; i < answersFlag.length; i++) {
                codeList.add(i + 1);
                gridMap.put(i + 1, answersFlag[i]);
            }
        }
        if (answercardGridAdapterChoose == null) {
            answercardGridAdapterChoose = new AnswercardGridAdapter(context, codeList, gridMap);
            gv_answercard.setAdapter(answercardGridAdapterChoose);
        } else {
            answercardGridAdapterChoose.notifyDataSetChanged();
        }
    }

    /**
     * 显示正确答案 视频解析 答案解析 全站数据等
     *
     * @param position
     * @param holder
     */
    private void questionOtherShow(int position, ViewHolder holder) {
        holder.tv_parsing.setVisibility(View.VISIBLE);
        holder.tv_correct_error.setVisibility(View.VISIBLE);

        //用户选线，答案
        String userChoose1 = context.selectedChoiceList.get(position).toString();
        String userChoose = StringUtils.changHtmlTo(userChoose1);
        //查看解析
        String htmlTo = StringUtils.decode1(questionDetails.get(position).getAnswers());//替换掉/u003d
        String answer = StringUtils.changHtmlTo(htmlTo);//将＞ ＜这些替换成html能识别的符号
        String correct = "";
        String replaceUserChoose = "";
        String replaceAnswer = "";
        if (Integer.parseInt(questionDetails.get(position).getQtype()) == 5) {
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

        RichText.fromHtml(correct).autoFix(false).scaleType(ImageHolder.ScaleType.FIT_XY).into(holder.tv_correct_error);

        if (null != questionAttrMap && questionAttrMap.size() > 0 && Integer.parseInt(questionDetails.get(position).getQtype()) != 4) {
            //有全站数据
            holder.ll_toal_data.setVisibility(View.VISIBLE);
            holder.tv_answer_times.setText(context.questionAttrMap.get(questionDetails.get(position).getQid()).getAnswercount());
            String[] strings = context.questionAttrMap.get(questionDetails.get(position).getQid()).getPrecision().split("%");
            double wrongParent = 0.0;
            if (strings.length > 0) {
                String precision = strings[0];
                wrongParent = (100 - Float.parseFloat(precision));
            }
            holder.tv_error_rate.setText(fnum.format(wrongParent) + "%");
            holder.tv_easy_wrong_item.setText(context.questionAttrMap.get(questionDetails.get(position).getQid()).getFallibility());
        }

        if (!StringUtils.isEmpty(questionDetails.get(position).getQvideo())) {
            holder.ll_video_resolution.setVisibility(View.VISIBLE);
            String qvideo = questionDetails.get(position).getQvideo();
            //有视频解析
            String videos[] = qvideo.split("###");
            final String videoId = videos[0];
            final String video_uid = videos[1];
            final String video_key = videos[2];
            holder.img_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 视频解析
                    Intent intent = new Intent(context, VideoParseDetailsActivity.class);
                    intent.putExtra("videoId", videoId);
                    intent.putExtra("video_uid", video_uid);
                    intent.putExtra("video_key", video_key);
                    context.startActivity(intent);
                }
            });
        }
        if (Integer.parseInt(questionDetails.get(position).getQtype()) == 4) {
            //主观题
            holder.tv_correct_error.setVisibility(View.GONE);
            holder.ll_toal_data.setVisibility(View.GONE);
        } else if (Integer.parseInt(questionDetails.get(position).getQtype()) == 5) {
            //填空题
            holder.ll_toal_data.setVisibility(View.GONE);

            holder.edt_ex_edit1.setEnabled(false);
            holder.edt_ex_edit1.setEnabled(false);
            holder.edt_ex_edit2.setEnabled(false);
            holder.edt_ex_edit3.setEnabled(false);
            holder.edt_ex_edit4.setEnabled(false);
            holder.edt_ex_edit5.setEnabled(false);
            holder.edt_ex_edit6.setEnabled(false);
            holder.edt_ex_edit7.setEnabled(false);
            holder.edt_ex_edit8.setEnabled(false);
            holder.edt_ex_edit9.setEnabled(false);
            holder.edt_ex_edit10.setEnabled(false);
        } else {
            holder.ll_item_chooseA.setEnabled(false);
            holder.ll_item_chooseB.setEnabled(false);
            holder.ll_item_chooseC.setEnabled(false);
            holder.ll_item_chooseD.setEnabled(false);
            holder.ll_item_chooseE.setEnabled(false);
            holder.ll_item_chooseF.setEnabled(false);
            holder.ll_item_chooseG.setEnabled(false);
            holder.ll_item_chooseH.setEnabled(false);
            holder.ll_item_chooseI.setEnabled(false);
            holder.ll_item_chooseJ.setEnabled(false);
            holder.tv_ex_optionsA.setEnabled(false);
            holder.tv_ex_optionsB.setEnabled(false);
            holder.tv_ex_optionsC.setEnabled(false);
            holder.tv_ex_optionsD.setEnabled(false);
            holder.tv_ex_optionsE.setEnabled(false);
            holder.tv_ex_optionsF.setEnabled(false);
            holder.tv_ex_optionsG.setEnabled(false);
            holder.tv_ex_optionsH.setEnabled(false);
            holder.tv_ex_optionsI.setEnabled(false);
            holder.tv_ex_optionsJ.setEnabled(false);
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
            if (textColors == ColorStateList.valueOf(Color.WHITE)) {
                tv_selectors.get(thisPosition).setTextColor(context.getResources().getColor(R.color.green004));
                tv_selectors.get(thisPosition).setBackgroundResource(R.drawable.bg_rectangle_green_hollow_more);
                context.selectedChoiceList.get(position).remove(text);
                context.updateStudyRecord(0);
            } else {
                tv_selectors.get(thisPosition).setTextColor(Color.WHITE);
                tv_selectors.get(thisPosition).setBackgroundResource(R.drawable.bg_rectangle_green_more);
                context.selectedChoiceList.get(position).add(text);
                context.updateStudyRecord(0);

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
        if (mExercisetype == 0 && mType != 3) {//做题页面 并且不是错题中心
            for (int i = 0; i < tv_selectors.size(); i++) {
                tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.green004));
                tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_green_hollow);
            }
            context.selectedChoiceList.get(position).clear();
            context.selectedChoiceList.get(position).add(tv_selectors.get(thisPosition).getText());
            tv_selectors.get(thisPosition).setTextColor(Color.WHITE);
            tv_selectors.get(thisPosition).setBackgroundResource(R.drawable.bg_round_green);
            context.moveToNext(position);
        } else if (mExercisetype == 0 && mType == 3) {//错题中心
            for (int i = 0; i < tv_selectors.size(); i++) {
                tv_selectors.get(i).setTextColor(context.getResources().getColor(R.color.green004));
                tv_selectors.get(i).setBackgroundResource(R.drawable.bg_round_green_hollow);
            }
            context.selectedChoiceList.get(position).clear();
            context.selectedChoiceList.get(position).add(tv_selectors.get(thisPosition).getText());
            tv_selectors.get(thisPosition).setTextColor(Color.WHITE);
            tv_selectors.get(thisPosition).setBackgroundResource(R.drawable.bg_round_green);
            questionOtherShow(position, View_Holders.get(position));
        }
    }

    @Override
    public int getItemPosition(Object object) {
//        if (questionDetails != null && questionDetails.size()==0) {
//            return POSITION_NONE;
//        }
//        if (((View) object).getTag().equals(refreshId)) {
//            return POSITION_NONE;
//        }
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }

    /**
     * @param state      0：什么都没做 1：开始滑动 2：滑动结束
     * @param currentNum 当前的题目
     */
    @Override
    public void onPageScrollStateChangedListener(int state, int currentNum) {
        if (Integer.parseInt(questionDetails.get(currentNum).getQtype()) == 5 && state == 1) {
            vocabularySaveData(currentNum);
            context.updateStudyRecord(0);
        }
        if (Integer.parseInt(questionDetails.get(currentNum).getQtype()) == 5 && mType == 3 && state == 1) {
            //错题中心填空题需要显示其他内容
            if (moveNext % 2 == 1) {
                moveNext++;
                questionOtherShow(currentNum, View_Holders.get(currentNum));
                context.vp_doexercise.setCurrentItem(currentNum - 1);
            } else {
                moveNext = 1;
            }
        }
        String[] answersList = questionDetails.get(currentNum).answersArr;
        if (null != answersList && answersList.length > 1 && mType == 3 && state == 1) {
            //错题中心多选题 需要显示其他内容
            if (moveNext % 2 == 1) {
                moveNext++;
                questionOtherShow(currentNum, View_Holders.get(currentNum));
                context.vp_doexercise.setCurrentItem(currentNum - 1);
            } else {
                moveNext = 1;
            }
        }


    }

    /**
     * 填空题 答案拼接
     *
     * @param currentNum
     */
    private void vocabularySaveData(int currentNum) {
        StringBuilder ediString = new StringBuilder();
        for (int i = 0; i < ediLength + 1; i++) {
            String text = EDT_Edits.get(currentNum).get(i).getText() + "";
            ediString = ediString.append(text + "|");
        }
        String lastResult = ediString.substring(0, ediString.length() - 1);
        context.selectedChoiceList.get(currentNum).clear();
        DebugUtil.e(TAG, "onPageScrollStateChangedListener lastResult: " + lastResult + " currentNum:" + currentNum);
        context.selectedChoiceList.get(currentNum).add(lastResult);
    }

    class ViewHolder {
        public TextView tv_content;//题目内容
        public TextView title;
        public TextView tv_progress;
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
    }

}
