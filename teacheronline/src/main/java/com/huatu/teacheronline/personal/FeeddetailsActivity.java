package com.huatu.teacheronline.personal;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huatu.teacheronline.BaseActivity;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.personal.bean.DeedDetailiBean;
import com.huatu.teacheronline.utils.FrescoUtils;
import com.huatu.teacheronline.utils.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
/**
 *  反馈详情页
 * Created by ply on 2016/1/5.
 */
public class FeeddetailsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private TextView tv_problem;
    private TextView tv_name;
    private TextView tv_time;
    private TextView tv_feed;
    private String id;
    private ArrayList<DeedDetailiBean.DataBean> detaibean=new ArrayList<>();
    private LinearLayout ll_reply;
    private RelativeLayout rl_no_reply;
    private GridView gridView;
    private DetailsAdapter adapter;
    private List<String> picbean=new ArrayList<>();
    public void initView() {
        setContentView(R.layout.activity_feeddetails);
        TextView tv_title= (TextView) findViewById(R.id.tv_main_title);
        tv_title.setText("反馈详情");
        id = getIntent().getStringExtra("id");
        gridView = (GridView) findViewById(R.id.gridview);
        tv_problem = (TextView) findViewById(R.id.tv_problem);//反馈内容
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_feed = (TextView) findViewById(R.id.tv_feed);
        ll_reply = (LinearLayout) findViewById(R.id.ll_reply);
        rl_no_reply = (RelativeLayout) findViewById(R.id.rl_no_reply);
        DetailsData();
        gridView.setOnItemClickListener(this);

    }

    private void DetailsData() {
        ObtatinDataListener   obtatinDataListener = new ObtatinDataListener(this);
        SendRequest.getFeeddetail(id, obtatinDataListener);
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



    public static void newIntent(Activity context,String id) {
        Intent goldPersonIntent = new Intent(context, FeeddetailsActivity.class);
        goldPersonIntent.putExtra("id",id);
        context.startActivity(goldPersonIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DialogAdvert(picbean.get(position));
    }

    class DetailsAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            if (picbean == null) {
                return 0;
            }
            return picbean.size();
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
            convertView=getLayoutInflater().inflate(R.layout.item_feed_details  ,null);
            SimpleDraweeView  iv_personal_face= (SimpleDraweeView) convertView.findViewById(R.id.iv_personal_face);
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
             GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200).setRoundingParams(RoundingParams.fromCornersRadius(30))
                .setPlaceholderImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.CENTER_CROP)
                .setFailureImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.CENTER_CROP)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .build();
             iv_personal_face.setHierarchy(hierarchy);
             FrescoUtils.setFrescoImageUri(iv_personal_face,picbean.get(position), R.drawable.ic_loading);
            return convertView;
        }
    }
   //点击图片弹出dialog看大图
    private void DialogAdvert(String url) {
        final android.app.AlertDialog   builder_advert = new android.app.AlertDialog.Builder(this, R.style.Dialog_Fullscreenx).create();
        builder_advert.setCancelable(false);
        builder_advert.show();
        Window window = builder_advert.getWindow();
        window.setContentView(R.layout.dialog_imaview);

        SimpleDraweeView sdv_adver= (SimpleDraweeView) window.findViewById(R.id.sdv_draweeView);
        sdv_adver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder_advert.cancel();
            }
        });
        window.findViewById(R.id.lLayout_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder_advert.cancel();
            }
        });
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setPlaceholderImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(getResources().getDrawable(R.drawable.ic_loading), ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        sdv_adver.setHierarchy(hierarchy);
        FrescoUtils.setFrescoImageUri(sdv_adver,url, R.drawable.ic_loading);
    }



    private static class ObtatinDataListener extends ObtainDataFromNetListener<DeedDetailiBean, String> {
        private FeeddetailsActivity weak_activity;

        public ObtatinDataListener(FeeddetailsActivity activity) {
            weak_activity = new WeakReference<>(activity).get();
        }


        @Override
        public void onSuccess(final DeedDetailiBean res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        weak_activity.AdapterMethod(res);
                        weak_activity.tv_problem.setText(res.getData().getContent());
                       if (res.getData().getFeedback_time().equals("")){//如果回复时间为空说明没有回复
                           weak_activity.ll_reply.setVisibility(View.GONE);
                           weak_activity.rl_no_reply.setVisibility(View.VISIBLE);
                       }else{
                           weak_activity.ll_reply.setVisibility(View.VISIBLE);
                           weak_activity.rl_no_reply.setVisibility(View.GONE);
                           weak_activity.tv_time.setText(res.getData().getFeedback_time());
                           weak_activity.tv_feed.setText(res.getData().getFeedback());
                       }
                     }
                });
            }
        }

        @Override
        public void onFailure(final String res) {
            if (weak_activity != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(res);
                    }
                });
            }
        }

    }
  //设置适配添加容器
  public void  AdapterMethod(DeedDetailiBean deedDetailiBean){
            detaibean.add(deedDetailiBean.getData());
        if (deedDetailiBean.getData().getPic()!=null){
          picbean.addAll(deedDetailiBean.getData().getPic());
          }
        if (adapter!=null){
              adapter.notifyDataSetChanged();
          }else{
              adapter =new DetailsAdapter();
              gridView.setAdapter(adapter);
          }
    }
}
