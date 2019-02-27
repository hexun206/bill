package com.huatu.teacheronline.personal;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.HomeActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.adapter.MessageListAdapter;
import com.huatu.teacheronline.personal.bean.MsgBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.CustomListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 开课通知详情
 * @author
 * @time 2017-3-21
 */
public class DetailsnoticeActivity extends BaseActivity {


    private RelativeLayout rl_left;
    private RelativeLayout rl_confirm;
    private CustomAlertDialog mCustomDelDirectDilog;
    private int noticeId;
    private TextView tv_receipt;
    private TextView tv_prompt;
    private TextView tv_title;
    private TextView tv_time;
    private TextView tv_message_type;
    private TextView tv_num;
    private String tid;
    private String notibar;
    private String uid;
    private int BarNoticeId;
    private int Isconfir=3;//1为已确认 0未确认 3 无网络状态栏跳转过来
    private TextView tv_leave;
    private CustomListView lisiview;
    private int messageId=1;
    private MessageListAdapter messageListAdapter;
    private List<MsgBean.LeaveMessageListEntity> msgbean=new ArrayList<>();
    @Override
    public void initView() {
      setContentView(R.layout.activity_detailsnotice);
      noticeId = getIntent().getIntExtra("noticeId", 0);
      tid = getIntent().getStringExtra("directId");
      notibar = getIntent().getStringExtra("bar");//状态栏传过来，用于判断是状态栏传过来的。
        if (notibar==null){
            notibar="99";
        }
      uid = CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, "");
      TextView title= (TextView) findViewById(R.id.tv_main_title);
      title.setText("通知详情");
      rl_left = (RelativeLayout) findViewById(R.id.rl_main_left);
      rl_confirm = (RelativeLayout) findViewById(R.id.rl_confirm);
      tv_receipt = (TextView) findViewById(R.id.tv_receipt);
      tv_title = (TextView) findViewById(R.id.tv_title);
      tv_time = (TextView) findViewById(R.id.tv_time);
      tv_num = (TextView) findViewById(R.id.tv_num);
      tv_prompt = (TextView) findViewById(R.id.tv_prompt);
      tv_leave = (TextView) findViewById(R.id.tv_leave);
      tv_message_type = (TextView) findViewById(R.id.tv_message_type);
      lisiview = (CustomListView) findViewById(R.id.lisiview);//留言回复列表
      mCustomDelDirectDilog = new CustomAlertDialog(this, R.layout.dialog_join_received);
      mCustomDelDirectDilog.setTitle("提示<br/>确定已经浏览完开课具体信息？");
    }
    /**
     * 开课详情数据
     */
    private void DetailsData() {
        ObtatinDetails obtatinDetails= new ObtatinDetails(this);
        if (notibar.equals("Notificationbar")){//相同说明从状态栏跳转到课程详情
            SendRequest.getNotificationbar(tid, uid, obtatinDetails);
        }else{
            SendRequest.getarrangement(String.valueOf(noticeId), obtatinDetails);
        }
    }
    private static class ObtatinDetails extends ObtainDataFromNetListener<MsgBean,String >{
        private DetailsnoticeActivity weak_activity;
        public ObtatinDetails(DetailsnoticeActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }
        @Override
        public void onSuccess(final MsgBean res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.msgbean.clear();
                        if(res.getLeaveMessageList()!=null){
                            weak_activity.msgbean.addAll(res.getLeaveMessageList());
                        }
                        weak_activity.MessageAdapert(weak_activity.msgbean);
                        weak_activity.messageId=res.getMessageId();
                        weak_activity.tv_title.setText(res.getTitle());
                        weak_activity.tv_prompt.setText(Html.fromHtml(res.getContent()));
                        weak_activity.tv_time.setText("时间："+res.getPushTime()+"");
                        weak_activity.tv_num.setText("已有"+res.getCheckedCount()+"人确定");
                        weak_activity.tv_message_type.setText("信息类型："+res.getType());
                        weak_activity.Isconfir=res.getChecked();
                        weak_activity.BarNoticeId=res.getnoticeId();
                        if (res !=null&&res.getChecked()==1){//如果checked=1 已确认 0 未确认
                            weak_activity.tv_receipt.setText("已确认");
                            weak_activity.tv_receipt.setTextColor(weak_activity.getResources().getColor(R.color.green007));
                            weak_activity.rl_confirm.setBackgroundResource(R.drawable.bt_confirmed_n);
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



    public void MessageAdapert(List<MsgBean.LeaveMessageListEntity> res){
        if (messageListAdapter==null){
            messageListAdapter = new MessageListAdapter(this, res);
            lisiview.setAdapter(messageListAdapter);
        }else{
            messageListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DetailsData();
    }

    @Override
    public void setListener() {
        rl_left.setOnClickListener(this);
        tv_leave.setOnClickListener(this);
        rl_confirm.setOnClickListener(this);
        mCustomDelDirectDilog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmationData();
                mCustomDelDirectDilog.dismiss();
            }
        });
    }
    /**
     * 确认课程通知接口　变更状态
     */
    private void ConfirmationData() {
        ObtatinMessageData obtatinMessageData=new ObtatinMessageData(this);
        if (notibar.equals("Notificationbar")){
            SendRequest.getsnoticeok(String.valueOf(BarNoticeId),obtatinMessageData);
        }else{
            SendRequest.getsnoticeok(String.valueOf(noticeId),obtatinMessageData);
        }
    }
    private static class ObtatinMessageData extends ObtainDataFromNetListener<String ,String >{
        private DetailsnoticeActivity weak_activity;
        public ObtatinMessageData(DetailsnoticeActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }
        @Override
        public void onSuccess(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals("1")){
                            weak_activity.tv_receipt.setText("已确认");
                            weak_activity.tv_receipt.setTextColor(weak_activity.getResources().getColor(R.color.green007));
                            weak_activity.rl_confirm.setBackgroundResource(R.drawable.bt_confirmed_n);
                            weak_activity.DetailsData();
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
    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.rl_main_left:
              if (Isconfir==0){
                  mCustomDelDirectDilog.show();
              }else if (Isconfir!=0&&notibar.equals("Notificationbar")){
//                  MainActivity.newIntent(this);
                  HomeActivity.newIntent(this);
                  back();
                  return;
              }else{
                  back();
              }
              break;
          case R.id.rl_confirm:
              ConfirmationData();
              break;
          case R.id.tv_leave:
              AddMessageActivity.newIntent(this,noticeId,messageId);
              break;
      }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {//如果为已确认　ｂａｃｋ键正常　否则弹出
            if (Isconfir==0&&!tv_receipt.getText().toString().equals("已确认")){
                mCustomDelDirectDilog.show();
            }else if (Isconfir==1&&notibar.equals("Notificationbar")){
//                MainActivity.newIntent(this);
                HomeActivity.newIntent(this);
                return back();
            }else if (Isconfir==1){
                return back();
            }else{
                return back();
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public static void newIntent(Context context, int noticeId ) {
        Intent intent = new Intent(context, DetailsnoticeActivity.class);
        intent.putExtra("noticeId", noticeId);
        context.startActivity(intent);
    }
}
