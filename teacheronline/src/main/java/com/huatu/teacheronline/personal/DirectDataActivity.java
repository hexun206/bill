package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.bean.InformaBean;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.CustomAlertDialog;
import com.huatu.teacheronline.widget.CustomListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的课程-直播课-信息详情
 */
public class DirectDataActivity extends BaseActivity {

    private String rid;
    private CustomListView listview;
    private String title;
    private CustomAlertDialog mCustomLoadingDialog;
    private DirectAdapter adapter;
    private List<InformaBean.DataEntity.ContentEntity> informaBeans =new ArrayList<>();
    private TextView tv_num;
    private TextView tv_course_title;
    private TextView tv_service_phone;
    private TextView tv_quit;
    private TextView tv_ptompt;

    @Override
    public void initView() {
        setContentView(R.layout.activity_direct_data);
        TextView tv_title= (TextView) findViewById(R.id.tv_main_title);
        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        rid = getIntent().getStringExtra("rid");
        title = getIntent().getStringExtra("title");
        listview = (CustomListView) findViewById(R.id.listview);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_course_title = (TextView) findViewById(R.id.tv_course);
        tv_service_phone = (TextView) findViewById(R.id.tv_service_phone);
        tv_quit = (TextView) findViewById(R.id.tv_quit);
        tv_ptompt = (TextView) findViewById(R.id.tv_ptompt);
        tv_num.setText("班号："+rid);
        tv_course_title.setText(title);
        tv_title.setText("课程信息");
        initData();
    }

    private void initData() {
        ObtainDataLister  obtatinDataListener = new ObtainDataLister(this);
        SendRequest.getcourse_information(rid, obtatinDataListener);
    }

    @Override
    public void setListener() {
        findViewById(R.id.rl_main_left).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.rl_main_left:
              back();
              break;
      }
    }


    public static void newIntent(Activity context ,String rid,String title) {
        Intent intent = new Intent(context, DirectDataActivity.class);
        intent.putExtra("rid",rid);
        intent.putExtra("title",title);
        context.startActivity(intent);
    }


    /****
     * 课程信息
     *****/
    private class ObtainDataLister extends ObtainDataFromNetListener<InformaBean, String> {
        private DirectDataActivity weak_activity;
        public ObtainDataLister(DirectDataActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final InformaBean res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                if (res != null) {
                    weak_activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            informaBeans.addAll(res.getData().getContent());
                            tv_service_phone.setText(res.getData().getCustomer_service());
                            tv_quit.setText(res.getData().getTransfer_class());
                            tv_ptompt.setText(res.getData().getReminder());
                            if (adapter==null){
                                adapter = new DirectAdapter();
                                listview.setAdapter(adapter);
                            }else{
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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

    //适配器正常情况只显示1条最多不超过三条，所以暂未做优化处理
   class DirectAdapter extends BaseAdapter {
       private View inflate;
       @Override
       public int getCount() {
           return informaBeans.size();
       }

       @Override
       public Object getItem(int position) {
           return position;
       }

       @Override
       public long getItemId(int position) {
           return position;
       }

       @Override
       public View getView(int position, View convertView, ViewGroup parent) {
           inflate = getLayoutInflater().inflate(R.layout.item_direct_data, null);
           TextView tv_direct_time = (TextView) inflate.findViewById(R.id.tv_direct_time);
           TextView tv_context = (TextView) inflate.findViewById(R.id.tv_context);
           tv_context.setText(informaBeans.get(position).getCourseware_title()+"   "+informaBeans.get(position).getTeacherName());
           tv_direct_time.setText(informaBeans.get(position).getZhibotime());
           return inflate;
       }
   }
}
