package com.huatu.teacheronline.exercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.exercise.adapter.AnswercardGridAdapter;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.DebugUtil;
import com.huatu.teacheronline.widget.MyScrollView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ljzyuhenda on 16/2/14.
 */
public class AnswerCardActivity extends BaseActivity {
    //客观题
    private GridView gv_answercard;
    //主观题
    private GridView gv_answercard_subjective;
    private String[] answersFlag;// 所有题目答案集合
    private RelativeLayout rl_root;
    public static final int resultCode_doExerciseAcitivty = 0;
    public static final int resultCode_doExerciseAcitivtyTh = 0;
    public static final int resultCode_doExerciseAcitivtyTheirPapers = 11;
    private static final String TAG = "AnswerCardActivity";
    private int isWrongCenter;//是否是错题中心过来的
    private int chooseNum = 0;//真题模考里面选择题
    private AnswercardGridAdapter answercardGridAdapterChoose;//填空题
    private AnswercardGridAdapter answercardGridAdapterEr;//真题主观题
    private TextView tv_sub_answercard;//主观题
    private TextView tv_obj_answercard;//客观题
    private LinearLayout ll_in_answercard;
    private MyScrollView sv_answercard;
    private int mType;//错题 -> 底部三个按钮功能分别为答题卡、解析、垃圾桶 0 今日特训、1 模块题海、2真题估分 3 错题中心、4收藏 则依次显示为交卷、答题卡、收藏 -1是模块分数页面过来的全部解析或者错题解析 5模考大赛 -2是真题分数页面过来的全部解析或者错题解析   -3 试卷收藏（针对真题演练和在线模拟）
    private TextView out_answercard;
    private int position;//滚动到指定位置

    @Override
    public void initView() {
        setContentView(R.layout.activity_answercard);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DoVipExerciseActivity.ACTION_FINISH);
        setIntentFilter(intentFilter);
        gv_answercard = (GridView) findViewById(R.id.gv_answercard);
        gv_answercard_subjective = (GridView) findViewById(R.id.gv_answercard_subjective);
        answersFlag = getIntent().getStringArrayExtra("answersFlag");
        isWrongCenter = getIntent().getIntExtra("isWrongCenter", 0);
        mType = getIntent().getIntExtra("mType", 0);
        position = getIntent().getIntExtra("position", 0);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        ll_in_answercard = (LinearLayout) findViewById(R.id.ll_in_answercard);
        tv_sub_answercard = (TextView) findViewById(R.id.tv_sub_answercard);
        out_answercard = (TextView) findViewById(R.id.out_answercard);
        sv_answercard = (MyScrollView) findViewById(R.id.sv_answercard);
        tv_obj_answercard = (TextView) findViewById(R.id.tv_obj_answercard);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rl_root.getLayoutParams();
        params.width = CommonUtils.getScreenWidth();
        params.height = CommonUtils.getScreenHeight();
        rl_root.setLayoutParams(params);
        sv_answercard.requestChildFocus(ll_in_answercard,ll_in_answercard.findFocus());
        if (mType == 3||mType == -1||mType == -2||mType == -3) {
            out_answercard.setText(R.string.text_close);
        }
        initData();
    }

    @Override
    public void setListener() {
        rl_root.setOnClickListener(this);
        out_answercard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_root:
                finish();
                break;
            case R.id.out_answercard:
                if (mType == 3||mType == -1||mType == -2||mType == -3) {
                    finish();
                }else {
                    setResult(resultCode_doExerciseAcitivtyTheirPapers);
                    finish();
                }
                break;
        }
    }

    private void initData() {
        chooseNum = DoVipExerciseActivity.chooseNum;
        DateFormat dateFormat = new SimpleDateFormat("HH-mm-ss-SSSS");
        DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
        List<Integer> codeList = new ArrayList<>();// 题号集合
        Map<Integer, String> gridMap = new HashMap<>();// <题号，答题情况>

        List<Integer> codeListEr = new ArrayList<>();// 题号集合
        Map<Integer, String> gridMapEr = new HashMap<>();// <题号，答题情况>


        if (isWrongCenter != 0) {
            //错题解析吧所有的answersFlag设置为0，也就是红圈
            for (int i = 0; i < isWrongCenter; i++) {
                codeList.add(i + 1);
                gridMap.put(i + 1, "0");
            }
            answercardGridAdapterChoose = new AnswercardGridAdapter(this, codeList, gridMap);
            gv_answercard.setAdapter(answercardGridAdapterChoose);
        } else {
            if(chooseNum == -4){//表示模块题
                //其他的根据对错来显示红绿圈
                for (int i = 0; i < answersFlag.length; i++) {
                    codeList.add(i + 1);
                    gridMap.put(i + 1, answersFlag[i]);
                }
                answercardGridAdapterChoose = new AnswercardGridAdapter(this, codeList, gridMap);
                gv_answercard.setAdapter(answercardGridAdapterChoose);
            }else {//表示真题 只有真题chooseNum才会被赋值
                DebugUtil.e("chooseNum:"+chooseNum);
                if(chooseNum == 0){
                    //只有主观题
                    iniViewOnlySub(codeListEr, gridMapEr);
                }else {
//                    gv_answercard_subjective.setVisibility(View.VISIBLE);
//                    tv_sub_answercard.setVisibility(View.VISIBLE);
//                    tv_obj_answercard.setVisibility(View.VISIBLE);

                    //表示全部是客观题
                    if(chooseNum == answersFlag.length){
                        iniViewOnlyObj(codeList, gridMap);
                    }else {
                        iniViewOnlyObj(codeList, gridMap);
                        iniViewOnlySub(codeListEr, gridMapEr);
                    }

                    //主观题
//                    for(int j = chooseNum; j < answersFlag.length; j++){
//                        codeListEr.add(j + 1);
//                        gridMapEr.put(j + 1, answersFlag[j]);
//                    }
//                    answercardGridAdapterEr = new AnswercardGridAdapter(this, codeListEr, gridMapEr);
//                    gv_answercard_subjective.setAdapter(answercardGridAdapterEr);
                }
            }
        }
        DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
//        gv_answercard_subjective.setAdapter(answercardGridAdapter);
        DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
        gv_answercard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("currentNum", answercardGridAdapterChoose.getItem(i));
                setResult(AnswerCardActivity.resultCode_doExerciseAcitivty, intent);
                finish();
            }
        });
        gv_answercard_subjective.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("currentNum", answercardGridAdapterEr.getItem(i));
                setResult(AnswerCardActivity.resultCode_doExerciseAcitivty, intent);
                finish();
            }
        });
        DebugUtil.i(TAG, "readFromFile-end:" + dateFormat.format(new Date()));
        gv_answercard.setSelection(position);
    }

    /**
     * 客观题
     * @param codeList
     * @param gridMap
     */
    private void iniViewOnlyObj(List<Integer> codeList, Map<Integer, String> gridMap) {
        gv_answercard.setVisibility(View.VISIBLE);
        tv_obj_answercard.setVisibility(View.VISIBLE);
        //客观题
        for (int i = 0; i < chooseNum; i++) {
            codeList.add(i + 1);
            gridMap.put(i + 1, answersFlag[i]);
        }
        answercardGridAdapterChoose = new AnswercardGridAdapter(this, codeList, gridMap);
        gv_answercard.setAdapter(answercardGridAdapterChoose);
    }

    /**
     * 主观题
     */
    private void iniViewOnlySub(List<Integer> codeList, Map<Integer, String> gridMap) {
        gv_answercard_subjective.setVisibility(View.VISIBLE);
        tv_sub_answercard.setVisibility(View.VISIBLE);
        //客观题
        for(int j = chooseNum; j < answersFlag.length; j++){
            codeList.add(j + 1);
            gridMap.put(j + 1, answersFlag[j]);
        }
        answercardGridAdapterEr = new AnswercardGridAdapter(this, codeList, gridMap);
        gv_answercard_subjective.setAdapter(answercardGridAdapterEr);
    }

    public static void newIntent(Activity context, int requestCode, String[] answersFlag,int mType,int position/*, String[] qrootPointList*/) {
        Intent intent = new Intent(context, AnswerCardActivity.class);
        intent.putExtra("answersFlag", answersFlag);
        intent.putExtra("isWrongCenter", 0);
        intent.putExtra("mType", mType);
        intent.putExtra("position", position);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 专门为打开错题解析
     * @param context
     * @param requestCode
     * @param answersFlag
     * @param isWrongCenter
     */
    public static void newIntent(Activity context, int requestCode, String[] answersFlag, int isWrongCenter,int mType,int position) {
        Intent intent = new Intent(context, AnswerCardActivity.class);
        intent.putExtra("answersFlag", answersFlag);
        intent.putExtra("isWrongCenter", isWrongCenter);
        intent.putExtra("mType", mType);
        intent.putExtra("position", position);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);
        back();
    }
}
