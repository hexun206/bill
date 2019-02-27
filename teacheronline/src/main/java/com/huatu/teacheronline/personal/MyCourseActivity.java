package com.huatu.teacheronline.personal;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.bean.ClassDetaliBean;
import com.huatu.teacheronline.personal.frament.MyDirectTabFragment;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.StringUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程信息详情
 *
 * @author cwq
 * @time 2017-3-21
 */
public class MyCourseActivity extends BaseActivity {

    private RelativeLayout rl_mai_left;
    private int classId;
    private List<ClassDetaliBean> UpdateList = new ArrayList<ClassDetaliBean>();
    private TextView tv_num;
    private TextView tv_course;
    private TextView tv_subject;
    private TextView tv_courlass;
    private TextView tv_class_time;
    private TextView tv_class_region;
    private TextView phone;
    private TextView tv_reminder;
    private TextView tv_attribution;
    private TextView tv_lessons_time;
    private TextView tv_specific_region;
    private TextView tv_other_tips;
    private String uid;
    private ImageView iv_dot7;
    private ImageView iv_dot1;
    private ImageView iv_dot2;
    private ImageView iv_dot3;
    private ImageView iv_dot4;
    private ImageView iv_dot5;
    private ImageView iv_dot6;
    private ImageView iv_dot8;
    private ImageView iv_dot9;
    private ImageView iv_dot10;
    private ImageView iv_dot11;
    private TextView tv_kClass_time;

    private boolean isWisdomClass;

    @Override
    public void initView() {
        setContentView(R.layout.activity_my_course);
        classId = getIntent().getIntExtra("classId", 0);
        isWisdomClass = getIntent().getBooleanExtra(MyDirectTabFragment.KEY_ISWISDOMCLASS, false);
        TextView tv_title = (TextView) findViewById(R.id.tv_main_title);
        rl_mai_left = (RelativeLayout) findViewById(R.id.rl_main_left);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_course = (TextView) findViewById(R.id.tv_course);
        tv_subject = (TextView) findViewById(R.id.tv_Subjec);
        tv_courlass = (TextView) findViewById(R.id.tv_courlass);
        tv_class_time = (TextView) findViewById(R.id.tv_ClassTime);
        tv_class_region = (TextView) findViewById(R.id.tv_class_region);
        phone = (TextView) findViewById(R.id.tv_service_phone);
        tv_reminder = (TextView) findViewById(R.id.tv_Class_reminder);
        tv_attribution = (TextView) findViewById(R.id.tv_attribution);
        tv_lessons_time = (TextView) findViewById(R.id.tv_lessons_time);
        tv_specific_region = (TextView) findViewById(R.id.tv_specific_region);
        iv_dot1 = (ImageView) findViewById(R.id.iv_add1);
        iv_dot2 = (ImageView) findViewById(R.id.iv_add2);
        iv_dot3 = (ImageView) findViewById(R.id.iv_add3);
        iv_dot4 = (ImageView) findViewById(R.id.iv_add4);
        iv_dot5 = (ImageView) findViewById(R.id.iv_add5);
        iv_dot6 = (ImageView) findViewById(R.id.iv_add6);
        iv_dot7 = (ImageView) findViewById(R.id.iv_add7);
        iv_dot8 = (ImageView) findViewById(R.id.iv_add8);
        iv_dot9 = (ImageView) findViewById(R.id.iv_add9);
        iv_dot10 = (ImageView) findViewById(R.id.iv_add10);
        iv_dot11 = (ImageView) findViewById(R.id.iv_add11);//新加上课时间
        tv_kClass_time = (TextView) findViewById(R.id.tv_kClass_time);
        tv_other_tips = (TextView) findViewById(R.id.tv_Other_tips);
        uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
        tv_title.setText("课程信息");
        ClassDetalisData();
    }

    /**
     * 请求详情数据
     */
    private void ClassDetalisData() {
        ObtatinclassDetalis obtatinclassDetalis = new ObtatinclassDetalis(this);

        if(isWisdomClass){
            SendRequest.getWisdomClassDetails(uid, String.valueOf(classId), obtatinclassDetalis);
        }else{
            SendRequest.getClassDetails(uid, String.valueOf(classId), obtatinclassDetalis);
        }


    }

    @Override
    public void setListener() {
        rl_mai_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                Intent intent = new Intent(MyDirectTabFragment.ACTION_NEXT);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                back();
                break;
        }
    }

    private static class ObtatinclassDetalis extends ObtainDataFromNetListener<ClassDetaliBean, String> {
        private MyCourseActivity weak_activity;

        public ObtatinclassDetalis(MyCourseActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final ClassDetaliBean res) {
            weak_activity.UpdateList.add(res);
            weak_activity.UpDetalisData();//页面数据请求成功调用这个接口更新查看
            if (res != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.DataSet();//数据的设置和红点绿点显示
                    }
                });
            }
        }

        @Override
        public void onFailure(String res) {
            if (SendRequest.ERROR_NETWORK.equals(res)) {
                ToastUtils.showToast(R.string.network);
            } else if (SendRequest.ERROR_SERVER.equals(res)) {
                ToastUtils.showToast(R.string.server_error);
            }
        }
    }

    /**
     * 标识已读课程
     */
    private void UpDetalisData() {
        ObtatinhasRead obtatinclass = new ObtatinhasRead(this);
        SendRequest.gethasReadClassInfo(uid, String.valueOf(classId), obtatinclass);
    }

    private static class ObtatinhasRead extends ObtainDataFromNetListener<String, String> {
        private MyCourseActivity weak_activity;

        public ObtatinhasRead(MyCourseActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final String res) {
            if (res != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals("1")) {
                            //标记成功！
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(String res) {
            if (SendRequest.ERROR_NETWORK.equals(res)) {
                ToastUtils.showToast(R.string.network);
            } else if (SendRequest.ERROR_SERVER.equals(res)) {
                ToastUtils.showToast(R.string.server_error);
            }
        }
    }

    private String checkNull(String str) {
        return StringUtils.isEmpty(str) ? "【无】" : str;

    }


    private void DataSet() {
        tv_num.setText("班号：" + checkNull(UpdateList.get(0).getNumber()));
        tv_course.setText(checkNull(UpdateList.get(0).getTitle()));
        tv_subject.setText(checkNull(UpdateList.get(0).getSubject()));
        tv_attribution.setText(checkNull(UpdateList.get(0).getSysAttribution()));
        tv_courlass.setText(checkNull(UpdateList.get(0).getCatalog()));
        tv_class_time.setText(checkNull(UpdateList.get(0).getStartDate() + ""));
        tv_lessons_time.setText(checkNull(UpdateList.get(0).getEndDate() + ""));
        tv_class_region.setText(checkNull(UpdateList.get(0).getArea()));
        tv_specific_region.setText(checkNull(UpdateList.get(0).getAddress()));
        tv_reminder.setText(checkNull(UpdateList.get(0).getTurnBack() + ""));
        phone.setText(checkNull(UpdateList.get(0).getCustomService() + ""));
        tv_other_tips.setText(checkNull(UpdateList.get(0).getReminder()));
        tv_kClass_time.setText(checkNull(UpdateList.get(0).getschedule()));
        if (UpdateList.get(0).getUpdateItem() == null) {
            return;
        }
        for (int i = 0; i < UpdateList.get(0).getUpdateItem().size(); i++) {
            String updatalist = UpdateList.get(0).getUpdateItem().get(i);
            if (updatalist.equals("0")) {
                iv_dot1.setImageResource(R.drawable.unread_kaike);
            }
            if (updatalist.equals("1")) {
                iv_dot2.setImageResource(R.drawable.unread_kaike);
            }
            if (updatalist.equals("2")) {
                iv_dot3.setImageResource(R.drawable.unread_kaike);
            }
            if (updatalist.equals("3")) {
                iv_dot4.setImageResource(R.drawable.unread_kaike);
            }
            if (updatalist.equals("4")) {
                iv_dot5.setImageResource(R.drawable.unread_kaike);
            }
            if (updatalist.equals("5")) {
                iv_dot6.setImageResource(R.drawable.unread_kaike);
            }
            if (updatalist.equals("6")) {
                iv_dot7.setImageResource(R.drawable.unread_kaike);
            }
            if (updatalist.equals("7")) {
                iv_dot8.setImageResource(R.drawable.unread_kaike);
            }
            if (updatalist.equals("8")) {
                iv_dot9.setImageResource(R.drawable.unread_kaike);
            }
            if (updatalist.equals("9")) {
                iv_dot10.setImageResource(R.drawable.unread_kaike);
            }
            if (updatalist.equals("10")) {
                iv_dot11.setImageResource(R.drawable.unread_kaike);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(MyDirectTabFragment.ACTION_NEXT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            back();
        }
        return false;
    }
}
