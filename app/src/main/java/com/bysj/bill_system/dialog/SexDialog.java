package com.bysj.bill_system.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.bysj.bill_system.R;
import com.bysj.bill_system.listener.OnSexClickListener;


import androidx.annotation.NonNull;

public class SexDialog extends Dialog {
    Context context;
    OnSexClickListener onSexClickListener;
    int sex = 1;

    public SexDialog(@NonNull Context context, int sex, OnSexClickListener onSexClickListener) {
        super(context, R.style.dialog);
        this.sex = sex;
        this.onSexClickListener = onSexClickListener;
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_sex, null, false);
        setContentView(view);
        Button boy = view.findViewById(R.id.btnBoy);
        Button girl = view.findViewById(R.id.btnGirl);
        changeBtnBac(boy, girl);
        boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = 1;
                changeBtnBac(boy, girl);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        if (onSexClickListener != null)
                            onSexClickListener.onClick(sex);
                    }
                }, 100);
            }
        });
        girl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = 2;
                changeBtnBac(boy, girl);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        if (onSexClickListener != null)
                            onSexClickListener.onClick(sex);
                    }
                }, 100);
            }
        });
    }

    private void changeBtnBac(Button boy, Button girl) {
        if (sex == 0 || sex == 1) {
            boy.setBackgroundResource(R.drawable.shape_choosed_btn_bac);
            girl.setBackgroundResource(R.drawable.shape_choose_btn_bac);
        } else {
            boy.setBackgroundResource(R.drawable.shape_choose_btn_bac);
            girl.setBackgroundResource(R.drawable.shape_choosed_btn_bac);
        }
    }
}
