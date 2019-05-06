package com.bysj.bill_system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bysj.bill_system.R;
import com.bysj.bill_system.bean.AccountBean;
import com.bysj.bill_system.dialog.SexDialog;
import com.bysj.bill_system.listener.OnSexClickListener;
import com.bysj.bill_system.utils.DataUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PersonalSettingActivity extends BaseActivity {

    @BindView(R.id.vStub)
    View vStub;
    @BindView(R.id.rlBack)
    RelativeLayout rlBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.ivHead)
    ImageView ivHead;
    @BindView(R.id.rlHead)
    RelativeLayout rlHead;
    @BindView(R.id.etNickname)
    EditText etNickname;
    @BindView(R.id.rlUserName)
    RelativeLayout rlUserName;
    @BindView(R.id.tvSex)
    TextView tvSex;
    @BindView(R.id.rlSex)
    RelativeLayout rlSex;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.rlGrade)
    RelativeLayout rlGrade;

    AccountBean accountBean;

    @Override
    void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_personal_setting);
    }

    @Override
    void initData() {
        tvTitle.setText("个人信息");
        accountBean = DataUtils.getLoginAccount(this);
        Glide.with(this).load(accountBean.headUrl).apply(new RequestOptions().circleCrop().error(R.mipmap.ic_header)).into(ivHead);
        tvPhone.setText(accountBean.phone);
        if (accountBean.nickname != null && !accountBean.nickname.isEmpty())
            etNickname.setText(accountBean.nickname);
        if (accountBean.sex == 0)
            tvSex.setText("未设置");
        if (accountBean.sex == 1)
            tvSex.setText("男");
        if (accountBean.sex == 2)
            tvSex.setText("女");
        tvPhone.setText(accountBean.phone);
    }

    @OnClick({R.id.rlBack, R.id.rlHead, R.id.rlSex})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlBack:
                finish();
                break;
            case R.id.rlHead:
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())
                        .selectionMode(PictureConfig.SINGLE)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case R.id.rlSex:
                new SexDialog(this, accountBean.sex, new OnSexClickListener() {
                    @Override
                    public void onClick(int sex) {
                        accountBean.sex = sex;
                        if (accountBean.sex == 1)
                            tvSex.setText("男");
                        if (accountBean.sex == 2)
                            tvSex.setText("女");
                    }
                }).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    if (selectList != null && selectList.size() > 0)
                        accountBean.headUrl = selectList.get(0).getPath();
                    Glide.with(this).load(accountBean.headUrl).apply(new RequestOptions().circleCrop().error(R.mipmap.ic_header)).into(ivHead);
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        accountBean.nickname = etNickname.getText().toString();
        DataUtils.saveLoginAccount(this, accountBean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
