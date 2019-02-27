package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.bean.DirecourBean;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.widget.CustomAlertDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的课程-直播课-通知详情
 */
public class DirectCourseActivity extends BaseActivity {

    private String rid;
    private CustomAlertDialog mCustomLoadingDialog;
    private List<DirecourBean.DataEntity> direbean=new ArrayList<>();
    private String title;
    private MyAdpater adpater;
    private ListView listview;

    @Override
    public void initView() {
        setContentView(R.layout.activity_direct_course);
        TextView tv_title= (TextView) findViewById(R.id.tv_main_title);
        listview = (ListView) findViewById(R.id.listview);
        rid = getIntent().getStringExtra("rid");
        title = getIntent().getStringExtra("title");
        tv_title.setText("课程通知");
        mCustomLoadingDialog = new CustomAlertDialog(this, R.layout.dialog_loading_custom);
        initData();
    }

    private void initData() {
        ObtainDataLister  obtatinDataListener = new ObtainDataLister(this);
        SendRequest.getLivebroadcast(rid, obtatinDataListener);
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
        Intent intent = new Intent(context, DirectCourseActivity.class);
        intent.putExtra("rid",rid);
        intent.putExtra("title",title);
        context.startActivity(intent);
    }

    /****
     * 课程通知
     *****/
    private class ObtainDataLister extends ObtainDataFromNetListener<DirecourBean, String> {
        private DirectCourseActivity weak_activity;
        public ObtainDataLister(DirectCourseActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }

        @Override
        public void onSuccess(final DirecourBean res) {
            if (weak_activity != null) {
                weak_activity.mCustomLoadingDialog.dismiss();
                if (res != null) {
                    weak_activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            direbean.clear();
                            direbean.addAll(res.getData());
                            if (adpater==null){
                                adpater = new MyAdpater();
                                listview.setAdapter(adpater);
                            }else{
                                adpater.notifyDataSetChanged();
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

    class MyAdpater extends BaseAdapter{
        @Override
        public int getCount() {
            return (direbean == null ? 0 : direbean.size());
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view=getLayoutInflater().inflate(R.layout.item_direcourse_list,null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            TextView tv_class_time= (TextView) view.findViewById(R.id.tv_class_time);
            tv_title.setText("课程标题："+direbean.get(position).getCourseware_title());
            tv_class_time.setText("时间："+direbean.get(position).getZhibotime());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DirCourseDeatailActivity.newIntent(DirectCourseActivity.this,direbean.get(position)
                    .getCourseware_title(),title,direbean.get(position).getTeacherName()
                     ,direbean.get(position).getZhibotime(),direbean.get(position).getReminder());
                }
            });
            return view;
        }
    }
}
