package com.huatu.teacheronline.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.ProvinceBean;
import com.huatu.teacheronline.bean.ProvinceWithCityBean;
import com.huatu.teacheronline.login.ExamCatagoryChooseNewActivity;
import com.huatu.teacheronline.paymethod.bean.CityModel;
import com.huatu.teacheronline.paymethod.bean.DistrictModel;
import com.huatu.teacheronline.paymethod.kankan.wheel.chooseaddresswidget.adapters.ArrayWheelAdapter;
import com.huatu.teacheronline.paymethod.kankan.wheel.chooseaddresswidget.customview.OnWheelChangedListener;
import com.huatu.teacheronline.paymethod.kankan.wheel.chooseaddresswidget.customview.WheelView;
import com.huatu.teacheronline.utils.DebugUtil;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首次进来地区选择dialog
 * @author ljyu
 * @date 2017-11-24 11:47:05
 */
public class AreaSelectionDialog extends Dialog implements View.OnClickListener,OnWheelChangedListener {

	private ArrayList<ProvinceWithCityBean> mProvinceWithCityBeanList;
	private Context context;

	private WheelView wv_province_area_select;
	private WheelView wv_city_area_select;

	private String mCurrentProviceName = "";
	private String mCurrentCityName = "";
	private String mCurrentProviceXzqh = "";
	private String mCurrentCityXzqh = "";

	private String[] mProvinceDatas;
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();

	private AreaSelectionDialogClickListener listener;

	public AreaSelectionDialog(Context context) {
		super(context);
		this.context = context;
	}

	public AreaSelectionDialog(Context context, AreaSelectionDialogClickListener listener) {
		super(context);
		this.context = context;
		this.listener = listener;
	}
	public AreaSelectionDialog(Context context, int style, AreaSelectionDialogClickListener listener) {
	    super(context,style);
	    this.context = context;
	    this.listener = listener;
	}

	public AreaSelectionDialog(Activity examCatagoryChooseNewActivity, ArrayList<ProvinceWithCityBean> mProvinceWithCityBeanList, int
			fullScreenDialogstyle, AreaSelectionDialogClickListener areaSelectionDialogClickListener) {
		super(examCatagoryChooseNewActivity, fullScreenDialogstyle);
		this.context = examCatagoryChooseNewActivity;
		this.listener = areaSelectionDialogClickListener;
		this.mProvinceWithCityBeanList = mProvinceWithCityBeanList;


	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View inflate = getLayoutInflater().inflate(R.layout.area_selection_dialog_layout, null);
		setContentView(inflate);
		wv_province_area_select = (WheelView) inflate.findViewById(R.id.wv_province_area_select);
		wv_city_area_select = (WheelView) inflate.findViewById(R.id.wv_city_area_select);
//		wv_province_area_select.setShadowColor(0x00000000,0x00000000,0x00000000);
//		wv_city_area_select.setShadowColor(0x00000000,0x00000000,0x00000000);
		TextView okBtn = (TextView) inflate.findViewById(R.id.btn_ok);
		okBtn.setOnClickListener(this);
		// 添加change事件
		wv_province_area_select.addChangingListener(this);
		// 添加change事件
		wv_city_area_select.addChangingListener(this);
		if (mProvinceWithCityBeanList != null) {
			iniDataWheelView(mProvinceWithCityBeanList);
		}
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {

		if (wheel == wv_province_area_select) {
			updateCities();
		} else if (wheel == wv_city_area_select) {
			int cCurrent = wv_city_area_select.getCurrentItem();
			int pCurrent = wv_province_area_select.getCurrentItem();
			mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[cCurrent];
			if (mProvinceWithCityBeanList.get(pCurrent).getCityList() != null && mProvinceWithCityBeanList.get(pCurrent).getCityList().size()==0) {
				mCurrentCityXzqh = mProvinceWithCityBeanList.get(pCurrent).Xzqh;
			}else {
				mCurrentCityXzqh = mProvinceWithCityBeanList.get(pCurrent).getCityList().get(cCurrent).Xzqh;
			}
			DebugUtil.e("当前城市："+ mCurrentCityName +" 市邮便："+mCurrentCityXzqh);
		}

	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		listener.onClick(v,this);
	}

	public interface AreaSelectionDialogClickListener{
		void onClick(View v, AreaSelectionDialog mAreaSelectionDialog);
	}

	private void iniDataWheelView(ArrayList<ProvinceWithCityBean> mProvinceWithCityBeanList){
		if (mProvinceWithCityBeanList != null && !mProvinceWithCityBeanList.isEmpty()) {
			mCurrentProviceName = mProvinceWithCityBeanList.get(0).Name;
			List<ProvinceBean> cityList = mProvinceWithCityBeanList.get(0).getCityList();
		}
		//*/
		mProvinceDatas = new String[mProvinceWithCityBeanList.size()];
		for (int i = 0; i < mProvinceWithCityBeanList.size(); i++) {
			mProvinceDatas[i] = mProvinceWithCityBeanList.get(i).Name;
			List<ProvinceBean> cityList = mProvinceWithCityBeanList.get(i).getCityList();
			if (cityList == null ||cityList.size() == 0) {
				String[] cityNames = new String[]{mProvinceWithCityBeanList.get(i).Name};
				mCitisDatasMap.put(mProvinceWithCityBeanList.get(i).Name, cityNames);
			}else {
				String[] cityNames = new String[cityList.size()];
				for (int j = 0; j < cityList.size(); j++) {
					cityNames[j] = cityList.get(j).Name;
				}
				mCitisDatasMap.put(mProvinceWithCityBeanList.get(i).Name, cityNames);
			}

		}
		wv_province_area_select.setViewAdapter(new ArrayWheelAdapter<String>(context, mProvinceDatas));
		// 设置可见条目数量
		wv_province_area_select.setVisibleItems(5);
		wv_city_area_select.setVisibleItems(5);
//		wv_province_area_select.setWheelForeground(R.color.black);
//		wv_province_area_select.setShadowColor(0xffffffff,
//				0xffffffff, 0xffffffff);
		wv_province_area_select.setWheelForeground(R.color.black);
		//是否需要阴影
		wv_province_area_select.setDrawShadows(false);
		wv_province_area_select.setCyclic(false);
		updateCities();
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = wv_province_area_select.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		mCurrentProviceXzqh  = mProvinceWithCityBeanList.get(pCurrent).Xzqh;
		DebugUtil.e("当前省："+ mCurrentProviceName +" 省邮便："+mCurrentProviceXzqh);
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null || cities.length == 0) {
			cities = new String[]{mCurrentProviceName};
		}
		mCurrentCityName = cities[0];
		if (mProvinceWithCityBeanList.get(pCurrent).getCityList() != null && mProvinceWithCityBeanList.get(pCurrent).getCityList().size()==0) {
			mCurrentCityXzqh = mProvinceWithCityBeanList.get(pCurrent).Xzqh;
		}else {
			mCurrentCityXzqh = mProvinceWithCityBeanList.get(pCurrent).getCityList().get(0).Xzqh;
		}
		DebugUtil.e("当前城市：" + mCurrentCityName +" 市邮便："+ mCurrentCityXzqh);
		wv_city_area_select.setViewAdapter(new ArrayWheelAdapter<String>(context, cities));
		wv_city_area_select.setCurrentItem(0);
	}

	public String getmCurrentCityName() {
		return mCurrentCityName;
	}
	public String getmCurrentProviceName() {
		return mCurrentProviceName;
	}

	public String getmCurrentCityXzqh() {
		return mCurrentCityXzqh;
	}

	public String getmCurrentProviceXzqh() {
		return mCurrentProviceXzqh;
	}
}
