package com.huatu.teacheronline.exercise.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.exercise.DoVipExerciseActivity;
import com.huatu.teacheronline.exercise.adapter.AnswercardGridAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 79937 on 2017/8/10.
 */
public class AnswerCardFragment extends Fragment{

    private View convertView;
    private List<Integer> codeList = new ArrayList<>();// 题号集合
    private Map<Integer, String> gridMap = new HashMap<>();// <题号，答题情况>
    private AnswercardGridAdapter answercardGridAdapterChoose;
    private GridView gv_answercard;//答题卡
    private DoVipExerciseActivity context;
    private int mExercisetype;
    private int questionDetailsSize;//题目数量

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        convertView = inflater.inflate(R.layout.pager_answercard, null);
        context = (DoVipExerciseActivity) getActivity();
        Bundle arguments = getArguments();
        mExercisetype = arguments.getInt("mExercisetype");
        questionDetailsSize = arguments.getInt("questionDetailsSize");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        gv_answercard = (GridView) convertView.findViewById(R.id.gv_answercard);
        reFreshAnswerCard();
        gv_answercard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer integer = codeList.get(position);
                context.vp_doexercise.setCurrentItem(integer - 1);
            }
        });
        return convertView;
    }

    /**
     * 刷新答题卡页面
     */
    public void reFreshAnswerCard() {
        String[] answersFlag = context.startAnswerCardPager();
        codeList.clear();
        gridMap.clear();
        if (mExercisetype == 1) {
            for (int i = 0; i < questionDetailsSize; i++) {
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
}
