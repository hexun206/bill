package com.huatu.teacheronline.paymethod.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.engine.ObtainDataFromNetListener;
import com.huatu.teacheronline.engine.SendRequest;
import com.huatu.teacheronline.paymethod.ChooseObtainAddressActivity;
import com.huatu.teacheronline.paymethod.CreateNewAddressActivity;
import com.huatu.teacheronline.paymethod.bean.PersonalAddressBean;
import com.huatu.teacheronline.utils.CommonUtils;
import com.huatu.teacheronline.utils.ToastUtils;
import com.huatu.teacheronline.utils.UserInfo;
import com.huatu.teacheronline.widget.CustomAlertDialog;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author wf
 * @date 2016/5/22
 */
public class ChooseObtainAddressAdapter extends BaseAdapter {
    private CustomAlertDialog customAlertDialog;
    public Context context;
    private List<PersonalAddressBean> personalAddressBeanList;
    private boolean isClick;


    public ChooseObtainAddressAdapter(Context context) {
        this.context = context;
    }

    public List<PersonalAddressBean> getPersonalAddressBeanList() {
        notifyDataSetChanged();
        return personalAddressBeanList;
    }

    public void setPersonalAddressBeanList(List<PersonalAddressBean> personalAddressBeanList) {
        this.personalAddressBeanList = personalAddressBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (personalAddressBeanList == null ? 0 : personalAddressBeanList.size());
    }


    @Override
    public Object getItem(int position) {
        return (personalAddressBeanList == null ? null : personalAddressBeanList.get(position));
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    class ViewHolder {
        public TextView tv_choose_obtain_address_person, tv_choose_obtain_address, tv_choose_obtain_address_number;
        public RelativeLayout rl_choose_address_default, rl_choose_address_edit, rl_choose_address_delete;
        public ImageView iv_choose_address;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.choose_obtain_address_item, null);
            viewHolder.tv_choose_obtain_address_person = (TextView) convertView.
                    findViewById(R.id.tv_choose_obtain_address_person);
            viewHolder.tv_choose_obtain_address = (TextView) convertView.findViewById(R.id.tv_choose_obtain_address);
            viewHolder.tv_choose_obtain_address_number = (TextView) convertView.findViewById(R.id.tv_choose_obtain_address_number);
            viewHolder.rl_choose_address_default = (RelativeLayout) convertView.findViewById(R.id.rl_choose_address_default);
            viewHolder.rl_choose_address_edit = (RelativeLayout) convertView.findViewById(R.id.rl_choose_address_edit);
            viewHolder.rl_choose_address_delete = (RelativeLayout) convertView.findViewById(R.id.rl_choose_address_delete);
            viewHolder.iv_choose_address = (ImageView) convertView.findViewById(R.id.iv_choose_address);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final PersonalAddressBean personalAddressBean = personalAddressBeanList.get(position);
        if (personalAddressBean.getMr() == 1) {
            viewHolder.iv_choose_address.setImageResource(R.drawable.bt_choosed);
        }

        viewHolder.rl_choose_address_edit.setTag(personalAddressBean);
        viewHolder.rl_choose_address_delete.setTag(personalAddressBean);
        viewHolder.rl_choose_address_default.setTag(personalAddressBean);

        /**
         * 适配器行布局编辑按钮相关逻辑
         */
        viewHolder.rl_choose_address_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalAddressBean personalAddressBean = (PersonalAddressBean) v.getTag();
                Intent intent = new Intent(context, CreateNewAddressActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("PersonalAddressBean", personalAddressBean);
                intent.putExtras(bundle);
                intent.setFlags(2);
                context.startActivity(intent);
            }
        });
        viewHolder.rl_choose_address_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PersonalAddressBean personalAddressBean = (PersonalAddressBean) v.getTag();
                customAlertDialog = new CustomAlertDialog((Activity)context, R.layout.dialog_confirm_delete);
                customAlertDialog.show();
                customAlertDialog.setOkOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customAlertDialog.dismiss();
                        CommonUtils.putSharedPreferenceItem(null, UserInfo.KEY_SP_SELECT_ID, personalAddressBean.getId());
                        deleteAddressInformation(personalAddressBean);
                    }
                });
                customAlertDialog.setCancelOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customAlertDialog.dismiss();
                    }
                });

            }
        });

        viewHolder.iv_choose_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (personalAddressBean.getMr() == 1) {//判断，如果当前已经是默认选中的，则点击无效
                    return;
                } else {
//                    PersonalAddressBean personalAddressBean = (PersonalAddressBean) v.getTag();
                    setDefault(personalAddressBean);
                }
            }
        });


        viewHolder.tv_choose_obtain_address_person.setText(personalAddressBean.getName());
        viewHolder.tv_choose_obtain_address.setText(personalAddressBean.getProvince() + personalAddressBean.getAddress());
        viewHolder.tv_choose_obtain_address_number.setText(personalAddressBean.getTel());

        return convertView;
    }


    private void setDefault(PersonalAddressBean personalAddressBean) {
        ObtainDataFromNetListenerUpdateAddress obtainDataFromNetListenerUpdateAddress = new ObtainDataFromNetListenerUpdateAddress(context);
        SendRequest.updateAddress(CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null), personalAddressBean.getId(),
                personalAddressBean.getName(), personalAddressBean.getTel(), personalAddressBean.getProvince(),
                personalAddressBean.getAddress(),1,
                obtainDataFromNetListenerUpdateAddress);

    }

    public class ObtainDataFromNetListenerUpdateAddress extends ObtainDataFromNetListener<String, String> {

        private ChooseObtainAddressActivity weak_activity;

        public ObtainDataFromNetListenerUpdateAddress(Context activity) {
            weak_activity = (ChooseObtainAddressActivity) new WeakReference<>(activity).get();
        }


        @Override
        public void onStart() {
            super.onStart();
            weak_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weak_activity.customAlertDialog = new CustomAlertDialog(weak_activity, R.layout.dialog_loading_custom);
                    weak_activity.customAlertDialog.show();
                    weak_activity.customAlertDialog.setTitle(weak_activity.getResources().getString(R.string.saveing));

                }
            });
        }

        @Override
        public void onSuccess(String res) {

            if (res != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.customAlertDialog.dismiss();
                        ToastUtils.showToast("设置成功");
                        weak_activity.loadAddressInfomation();

                    }
                });
            }
        }


        @Override
        public void onFailure(final String res) {
            weak_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weak_activity.customAlertDialog.dismiss();
                    if (res.equals(SendRequest.ERROR_NETWORK)) {
                        ToastUtils.showToast(R.string.network);
                    } else if (res.equals(SendRequest.ERROR_SERVER)) {
                        ToastUtils.showToast(R.string.save_success);
                    } else {
                        ToastUtils.showToast(res);
                    }
                }
            });
        }
    }

    /**
     * 删除地址信息并同步更新到服务器
     */
    private void deleteAddressInformation(PersonalAddressBean personalAddressBean) {
        ObtainDataFromNetListenerDeleteAddress obtainDataFromNetListenerShowAddress = new ObtainDataFromNetListenerDeleteAddress(context);
        SendRequest.deleteAddress(CommonUtils.getSharedPreferenceItem(null, UserInfo.KEY_SP_USERID, null), personalAddressBean.getId(),
                obtainDataFromNetListenerShowAddress);
    }


    private static class ObtainDataFromNetListenerDeleteAddress extends ObtainDataFromNetListener<String, String> {

        private ChooseObtainAddressActivity weak_activity;

        public ObtainDataFromNetListenerDeleteAddress(Context activity) {
            weak_activity = (ChooseObtainAddressActivity) new WeakReference<>(activity).get();
        }

        @Override
        public void onStart() {
            super.onStart();
            weak_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weak_activity.customAlertDialog = new CustomAlertDialog(weak_activity, R.layout.dialog_loading_custom);
                    weak_activity.customAlertDialog.show();
                    weak_activity.customAlertDialog.setTitle(weak_activity.getResources().getString(R.string.deleting));

                }
            });
        }

        @Override
        public void onSuccess(final String res) {
            if (res != null) {
                weak_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weak_activity.customAlertDialog.dismiss();
                        weak_activity.loadAddressInfomation();
                        ToastUtils.showToast("删除成功");
                    }
                });

            }
        }


        @Override
        public void onFailure(String res) {
            weak_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weak_activity.customAlertDialog.dismiss();
                    weak_activity.loadAddressInfomation();
                    ToastUtils.showToast("删除失败");
                }
            });

        }
    }

}
