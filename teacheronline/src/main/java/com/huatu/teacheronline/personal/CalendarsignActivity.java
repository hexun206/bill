package com.huatu.teacheronline.personal;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.H5DetailActivity;
import com.huatu.teacheronline.HomeActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.PersonalInfoBean;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.bean.NCakenBean;
import com.huatu.teacheronline.personal.custom.MonthDateView;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.TripleDES;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.SlideSelectView;
import com.huatu.teacheronline.widget.linecharview.LineChartView;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
/*
日历签到详情页
* */
public class CalendarsignActivity extends BaseActivity{
    private NCakenBean nCakenBean=new NCakenBean();
    private List<NCakenBean.DataBean> cakendlist = new ArrayList<>();//签到详情日历容器
    private List<Double> recentlist=new ArrayList<>();//正确率容器
    private ImageView iv_left;
    private ImageView iv_right;
    static final String FILL_COLOR = "#50ffffff";
    static final String LINE_COLOR = "#ffffff";
    private MonthDateView monthDateView;
    private TextView tv_date;
    private String uid;
    private String year;
    private String month;
    private TextView tv_sign;
    private String day;
    private boolean isSingCard;
    private TextView tv_gold;
    private  List<Integer> Dotlist = new ArrayList<Integer>();//保存绿点日期容器
    private  List<Integer> Doylist = new ArrayList<Integer>();//保存灰点日期容器
    private LinkedHashMap<String, String> drawMap;
    private LineChartView lineChartView;
    private RelativeLayout rl_no_subject;
    private RelativeLayout rl_go_subject;
    private SlideSelectView slideSelectView;
    private String[] textStrings;
    private String[] textDownStrings;
    private CustomAlertDialog mCustomLoadingDialog;
    private Calendar ca;
    private Date lastMonth;
    private SimpleDateFormat sf;
    private int  ContSign=0;//连续签到时间
    private RelativeLayout rl_seesubject;
    private LinearLayout ll_broken;
    private RelativeLayout rl_correct;
    private RequestParams requestParams;
    private String OcYear="";//获得选中年
    private String OcMonth="";//获得选中月
    private String OcDay="";//获得选中日
    private int firstsign_time=0;
    @Override
    public void initView() {
        setContentView(R.layout.activity_calendarsign);
        TextView tv_title = (TextView) findViewById(R.id.tv_main_title);
        tv_title.setText("签到日历");
        SimpleDateFormat sDateyear = new SimpleDateFormat("yyyy");
        SimpleDateFormat sDatemonth = new SimpleDateFormat("MM");
        SimpleDateFormat sDateday = new SimpleDateFormat("dd");
        year = sDateyear.format(new Date());
        month = sDatemonth.format(new Date());
        day = sDateday.format(new Date());
        slideSelectView = (SlideSelectView) findViewById(R.id.slideSelectView);//时间轴控件
        ContinuousSign();//连续签到数据
        mCustomLoadingDialog = new CustomAlertDialog(CalendarsignActivity
                .this, R.layout.dialog_loading_custom);
        monthDateView = (MonthDateView) findViewById(R.id.monthDateView);//日历控件
        lineChartView = (LineChartView) findViewById(R.id.chart_view);
        drawMap = new LinkedHashMap<>();
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        tv_sign = (TextView) findViewById(R.id.tv_sign);//签到
        tv_gold = (TextView) findViewById(R.id.tv_gold);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        tv_date = (TextView) findViewById(R.id.date_text);
        rl_seesubject = (RelativeLayout) findViewById(R.id.rl_Seesubject);//查看做题记录
        rl_no_subject = (RelativeLayout) findViewById(R.id.rl_no_subject);//无做题布局
        rl_go_subject = (RelativeLayout) findViewById(R.id.rl_go_subject);//点击做题布局
        ll_broken = (LinearLayout) findViewById(R.id.ll_Broken);//连续签到布局
        rl_correct = (RelativeLayout) findViewById(R.id.rl_Correct);//正确率布局
        monthDateView.setTextView1(tv_date);
        getLastmonth();//获得上个月份
        CaldetailsData();//日历签到详情数据
    }

    private void FirstSignData(){//记录首次签到 （旧用户打完卡更新到最新版本的问题解决）
        ObFirstsignListener obfirstListener = new ObFirstsignListener(this);
        SendRequest.getfirstsign(uid, obfirstListener);
    }

    private static class ObFirstsignListener extends ObtainDataFromNetListener<String, String>{
        private CalendarsignActivity weak_activity;

        public ObFirstsignListener(CalendarsignActivity contextWeakReference) {
            weak_activity = new WeakReference<>(contextWeakReference).get();
        }
        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.show();
            }
        }
        @Override
        public void onSuccess(String res) {
            if (weak_activity != null){
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.firstsign_time=1;
                        weak_activity.CaldetailsData();
                        if (weak_activity.nCakenBean.getcycle()==1){
                            weak_activity.ProblemData();
                            return;
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(String res) {
            weak_activity.mCustomLoadingDialog.dismiss();
            if (res.equals(SendRequest.ERROR_NETWORK)) {
                ToastUtils.showToast(R.string.network);
            } else if (res.equals(SendRequest.ERROR_SERVER)) {
                ToastUtils.showToast(R.string.server_error);
            } else {
                ToastUtils.showToast(res);
            }
        }
    }



    private void CaldetailsData() {
        ObCaldetailsListener obCaldetailsListener = new ObCaldetailsListener(this);
        SendRequest.getCalendardetails(uid, year, month, obCaldetailsListener);

    }
    private static class ObCaldetailsListener extends ObtainDataFromNetListener<NCakenBean, String> {
        private CalendarsignActivity weak_activity;

        public ObCaldetailsListener(CalendarsignActivity contextWeakReference) {
            weak_activity = new WeakReference<>(contextWeakReference).get();
        }
        @Override
        public void onStart() {
            super.onStart();
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.show();
            }
        }
        @Override
        public void onSuccess(final NCakenBean res) {
            weak_activity.cakendlist.clear();
            weak_activity.Dotlist.clear();
            weak_activity.Doylist.clear();
            weak_activity.nCakenBean=res;//日历详情所有数据容器
            weak_activity.cakendlist.addAll(res.getData());//日历
            weak_activity.recentlist.addAll(res.getRecentlyAccuracy());//正确率
            weak_activity.ContSign=res.getContinuous_sign();//连续签到天数
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.DaysHasThing();//判断签到与未签到的显示圆点方法
                        weak_activity.Brokenviews();//折线图
                        weak_activity.monthDateView.setDaysHasThingList(weak_activity.Dotlist);
                        weak_activity.monthDateView.setDaysHasThingList1(weak_activity.Doylist);
                        if (weak_activity.month.equals(new SimpleDateFormat("MM").format(new Date()))){
                            if (weak_activity.cakendlist.get(Integer.parseInt(weak_activity.day)-1).getSign()==1){
                                weak_activity.tv_sign.setText("今日已签");
                                weak_activity.tv_gold.setText(weak_activity.cakendlist.get(Integer.parseInt(weak_activity.day)-1).getGive_gold());
                            }else{
                                weak_activity.tv_gold.setText(weak_activity.cakendlist.get(Integer.parseInt(weak_activity.day)-1).getGive_gold());
                            }
                        }
                        if (weak_activity.firstsign_time==1){
                            if (weak_activity.nCakenBean.getcycle()==1){
                                weak_activity.ProblemData();
                                return;
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            this.weak_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weak_activity.mCustomLoadingDialog.dismiss();
                    if (res.equals(SendRequest.ERROR_NETWORK)) {
                        ToastUtils.showToast(R.string.network);
                    } else if (res.equals(SendRequest.ERROR_SERVER)) {
                        ToastUtils.showToast(R.string.server_error);
                    } else {
                        ToastUtils.showToast(res);
                    }
                }
            });
        }
    }

    private void DaysHasThing() {
        for (int i = 0; i <cakendlist.size() ; i++) {
          if (cakendlist.get(i).getSign()==1&&!cakendlist.get(i).getType().equals("future")){
              Dotlist.add(i+1);
          }
            if (cakendlist.get(i).getSign()==0&&!cakendlist.get(i).getType().equals("future")){
                Doylist.add(i + 1);
          }
        }
        slideSelectView.postDelayed(new Runnable() {
            @Override
            public void run() { //连续签到的天数
                slideSelectView.setCurrentSign(ContSign);
            }
        }, 100);
    }

    //进行打卡
    public void signCarding() {
        ObtainDataListenerForSignCard   mSignCardListener = new ObtainDataListenerForSignCard(this);
        SendRequest.signCardEveDay(uid, mSignCardListener);
    }

    private class ObtainDataListenerForSignCard extends ObtainDataFromNetListener<PersonalInfoBean, String> {

        private CalendarsignActivity weak_activity;

        public ObtainDataListenerForSignCard(CalendarsignActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }
        @Override
        public void onSuccess(final PersonalInfoBean res) {
            if (weak_activity != null) {
                if (res != null) {
                    ToastUtils.showToast("签到成功！");
                    CaldetailsData();
                }
            }
        }
        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                if ("0".equals(res)) {
                    //打卡失败
                    ToastUtils.showToast(R.string.signcard_failed);
                } else if ("3".equals(res)) {
                    //用户已打卡
                    ToastUtils.showToast(R.string.signcard_already);
                    weak_activity.isSingCard = true;
                } else {
                    //打卡失败
                    ToastUtils.showToast(R.string.signcard_failed);
                }
            }
        }
    }


    @Override
    public void setListener() {
        findViewById(R.id.rl_sign).setOnClickListener(this);
        rl_go_subject.setOnClickListener(this);
        findViewById(R.id.rl_main_left).setOnClickListener(this);
        findViewById(R.id.rl_left).setOnClickListener(this);
        findViewById(R.id.rl_righ).setOnClickListener(this);
        rl_seesubject.setOnClickListener(this);
        monthDateView.setDateClick(new MonthDateView.DateClick() {
            private int getmSelDay;

            @Override
            public void onClickOnDate() {
                getmSelDay = monthDateView.getmSelDay() - 1;
                if (getmSelDay == -1) {
                    return;
                }
                OcYear=String.valueOf(monthDateView.getmSelYear());
                OcMonth=String.valueOf(monthDateView.getmSelMonth()+1);
                OcDay=String.valueOf(monthDateView.getmSelDay());

                if (cakendlist != null && cakendlist.size() > 0) {
                    if (cakendlist.get(getmSelDay).getDone() == 1 && !cakendlist.get(getmSelDay).getType().equals("today")) {
                        rl_no_subject.setVisibility(View.GONE);
                        rl_go_subject.setVisibility(View.GONE);
                        rl_correct.setVisibility(View.GONE);
                        rl_seesubject.setVisibility(View.VISIBLE);
                        ll_broken.setVisibility(View.GONE);
                    } else if (!cakendlist.get(getmSelDay).getType().equals("today") && cakendlist.get(getmSelDay).getDone() == 0) {
                        rl_no_subject.setVisibility(View.VISIBLE);
                        rl_seesubject.setVisibility(View.GONE);
                        rl_go_subject.setVisibility(View.GONE);
                        ll_broken.setVisibility(View.GONE);
                        rl_correct.setVisibility(View.GONE);
                    } else if (cakendlist.get(getmSelDay).getType().equals("today")) {
                        rl_no_subject.setVisibility(View.GONE);
                        ll_broken.setVisibility(View.VISIBLE);
                        rl_go_subject.setVisibility(View.VISIBLE);
                        rl_seesubject.setVisibility(View.GONE);
                        rl_correct.setVisibility(View.VISIBLE);

                    }

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
          switch (v.getId()){
              case R.id.rl_main_left:
                  back();
                  break;
              case R.id.rl_go_subject:
//                  MainActivity.newIntent(this);
                  HomeActivity.newIntent(this);
                  finish();
                  break;
              case R.id.rl_sign:
                  //签到打卡
                  MobclickAgent.onEvent(this, "signOnclik");
                  signCarding();
                  break;
              case R.id.rl_Seesubject://查看做题记录H5页面
                  if (nCakenBean.getcycle()==0&&cakendlist.get(Integer.parseInt(day)-1).getSign()==1){
                      FirstSignData();//记录首次签到
                      return;
                  }else if(nCakenBean.getcycle()==0){
                      ToastUtils.showToast("请先打卡签到！");
                      return;
                  }else{
                      ProblemData();//做题记录需要的链接加密 ,跳转
                  }
                  break;
              case R.id.rl_left:
                  if (!sf.format(lastMonth).equals(month)){
                      iv_right.setImageResource(R.drawable.next_month);
                      iv_left.setImageResource(R.drawable.last_month_f);
                      monthDateView.onLeftClick();
                      Dotlist.clear();
                      Doylist.clear();
                      monthDateView.setDaysHasThingList(Dotlist);//
                      monthDateView.setDaysHasThingList1(Doylist);
                      monthDateView.invalidate();
                      month = sf.format(lastMonth);
                      CaldetailsData();
                  }
                  break;
              case R.id.rl_righ://点击右边下个月
                   if (month.equals(new SimpleDateFormat("MM").format(new Date()))){
                       return;
                   }
                         iv_right.setImageResource(R.drawable.next_month_f);
                         iv_left.setImageResource(R.drawable.last_month);
                          monthDateView.onRightClick();
                          Dotlist.clear();
                          Doylist.clear();
                          monthDateView.setDaysHasThingList(Dotlist);
                          monthDateView.setDaysHasThingList1(Doylist);
                          monthDateView.invalidate();
                          month= new SimpleDateFormat("MM").format(new Date());
                          CaldetailsData();
                  break;
          }
    }
    private void Brokenviews() {
        if (recentlist.size()>0&&recentlist!=null){
            for (int i = 0; i < 30 ; i++) {
                drawMap.put("x_" + i,recentlist.get(i)+"");
            }
        }
        lineChartView.getLineParameters()
                .setDrawMap(drawMap)
                .setLineColor(Color.parseColor(LINE_COLOR))//设置折线的颜色
                .setyAxesColor(Color.TRANSPARENT)//设置Y坐标轴的颜色
                .setyAxesTextColor(Color.TRANSPARENT)//设置Y坐标轴字体颜色
                .setyAxesTextSize(30)//设置Y坐标轴字体大小
                .setxAxesMarginBottom(2)//设置距离底部距离
                .setChartMarginRight(20)//设置距离右边距离
                .setyAxesMarginTop(90)//设置距上部距离
                .setxAxesColor(Color.TRANSPARENT)//设置X坐标轴的颜色
                .setxAxesTextColor(Color.TRANSPARENT)//设置X坐标轴字体颜色
                .setxAxesTextSize(30)//设置Y坐标轴字体大小
                .setGridLineColor(Color.TRANSPARENT)//设置北京网格颜色
                .setFillColor(Color.parseColor(FILL_COLOR))//设置填充颜色
                .setyAxesMaxValue(150.0)
                .setVerticalUnitValue(10)
                .setLineVaueTextSize(12);//设置选中文字的大小
        lineChartView.refreshChartView();
    }

    private void ContinuousSign() {
        textStrings = new String[]{"积分\n+10", "积分\n+15", "积分+20\n" +
                "金币+2", "积分+50\n" +
                "金币+4", "积分+100\n" + "金币+6"};
        slideSelectView.setString(textStrings);
        textDownStrings = new String[]{"2", "3", "7", "14", "22"};
        slideSelectView.setString(textStrings);
        slideSelectView.setDownString(textDownStrings);
    }
//    private void ContinuousSign() {
//        textStrings = new String[]{"积分+10\n金币+1", "积分+15\n" +
//                "金币+1", "积分+20\n" +
//                "金币+4", "积分+50\n" +
//                "金币+10", "积分+100\n" + "金币+20"};
//        slideSelectView.setString(textStrings);
//        textDownStrings = new String[]{"2", "3", "7", "14", "22"};
//        slideSelectView.setString(textStrings);
//        slideSelectView.setDownString(textDownStrings);
//    }
    private void ProblemData() {
        String key = "0123456789QWEQWEEWQQ1234";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uid);
        jsonObject.addProperty("year", OcYear);
        jsonObject.addProperty("month", OcMonth);
        jsonObject.addProperty("day", OcDay);
        byte[] bs = null;
        try {
            bs = TripleDES.encrypt(jsonObject.toString().getBytes(), key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newParams = TripleDES.byte2hex(bs);
        requestParams = new RequestParams();
        requestParams.put("p", newParams);
        H5DetailActivity.newIntent(this, SendRequest.url_Share_questions + requestParams.toString(), 9, "华图教师", OcYear, OcMonth, OcDay, nCakenBean.getcycle());

    }
    private void getLastmonth() {
        ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(new Date()); //设置时间为当前时间 1
        ca.add(Calendar.MONTH, -1); //月份减1
        lastMonth = ca.getTime();//结果
        sf = new SimpleDateFormat("MM");
    }
}
