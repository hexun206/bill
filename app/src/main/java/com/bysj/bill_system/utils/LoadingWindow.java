package com.bysj.bill_system.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bysj.bill_system.R;


/**
 * 创建人：yyq
 * 创建时间：2018/8/8 下午4:23
 */
public class LoadingWindow extends Dialog  {

    private Context mContext;
    private ImageView loading_img;


    public LoadingWindow( Context context) {
        super(context, R.style.MyCustomDialog);
        this.mContext = context;
        View convertView = getLayoutInflater().inflate(R.layout.pop_loading, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(convertView);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loading_img = findViewById(R.id.loading_img);
        Glide.with(mContext).asGif().load(R.drawable.loading).into(loading_img);
//        setCanceledOnTouchOutside(true);
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth(); //设置dialog的宽度为当前手机屏幕的宽度
        window.setAttributes(p);
    }

//    @Override
//    public void setCancelable(boolean flag) {
//        super.setCancelable(flag);
//    }
//
//    @Override
//    public void setCanceledOnTouchOutside(boolean cancel) {
//        super.setCanceledOnTouchOutside(cancel);
//    }
}
