package com.common.api.widget.adr;


import java.util.HashMap;
import java.util.Map;




import com.common.api.R;
import com.common.api.utils.LogUtils;
import com.common.api.vo.AddressList;
import com.common.api.vo.Area;
import com.common.api.vo.Province;
import com.common.api.vo.Town;
import com.common.api.widget.adrWheel.ArrayWheelAdapter;

import android.content.Context;
import android.view.View;

public class WheelAddress implements OnWheelChangedListener {
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;
	private View AddressPicker;
	private Context mContext;
	
	/**
	 * 所有省
	 */
	protected String[] mProvinceDatas;
	/**
	 * key - 省 value - 市  key = id  value = List<Town>
	 */
	protected Map<String, AddressList<Town>> mCitisDatasMap = new HashMap<String, AddressList<Town>>();
	/**
	 * key - 市 values - 区
	 */
	protected Map<String, AddressList<Area>> mDistrictDatasMap = new HashMap<String, AddressList<Area>>();
	
	/**
	 * key - 区 values - 邮编
	 */
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>(); 

	/**
	 * 当前省的名称
	 */
	protected Province province;
	/**
	 * 当前市的名称
	 */
	protected Town town;
	/**
	 * 当前区的名称
	 */
	protected Area area = null;
	
	/**
	 * 当前区的邮政编码
	 */
	protected String mCurrentZipCode ="";
	
	private AddressList<Province> mProvinces;
	private AddressList<Town> mTowns;
	private AddressList<Area> mAreas;
	
	public WheelAddress(Context context,View addressPicker) {
		super();
		this.mContext = context;
		AddressPicker = addressPicker;
	}
   
	public void initDateTimePicker() {
		setUpViews();
		setUpListener();
		// 加载省市信息
//		loadCountry();
		setUpData();
	}
	private void setUpViews() {
		mViewProvince = (WheelView) AddressPicker.findViewById(R.id.id_province);
		mViewCity = (WheelView) AddressPicker.findViewById(R.id.id_city);
		mViewDistrict = (WheelView) AddressPicker.findViewById(R.id.id_district);
	}
	
	private void setUpListener() {
    	// 添加change事件
    	mViewProvince.addChangingListener(this);
    	// 添加change事件
    	mViewCity.addChangingListener(this);
    	// 添加change事件
    	mViewDistrict.addChangingListener(this);
    }
	
	private void setUpData() {
		initAdressDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<Province>(mContext, mProvinces));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
		updateCities();
		updateAreas();
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			area = mDistrictDatasMap.get(town.getId()).get(newValue);
		}
	}
   /**
    * 更新当前区域信息，区域名称和邮箱编号
    */
	private void updateDistrict(){
		int pCurrent = mViewDistrict.getCurrentItem();
		area = mDistrictDatasMap.get(town.getId()).get(pCurrent);
	}
	
	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		town = mTowns.get(pCurrent);
		mAreas = mDistrictDatasMap.get(town.getId());

		if (mAreas == null) {
			mAreas = new AddressList<Area>();
		}
		mViewDistrict.setViewAdapter(new ArrayWheelAdapter<Area>(mContext, mAreas));
		mViewDistrict.setCurrentItem(0);
		updateDistrict();
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		province = mProvinces.get(pCurrent);
		mTowns = mCitisDatasMap.get(province.getId());
		if (mTowns == null) {
			mTowns = new AddressList<Town>();
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<Town>(mContext, mTowns));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}
	
    /**
     * 设置省市数据
     * @param mProvinces
     */
	public void setmProvinces(AddressList<Province> mProvinces) {
		this.mProvinces = mProvinces;
	}

	/**
	 * 加载城市信息
	 */
//	private void loadCountry() {
//		CountryParser countryParser = new CountryParser();
//		try {
//			mProvinces = countryParser.parseInner(mContext.getResources().getXml(
//					R.xml.address));
//			if (mProvinces != null && mProvinces.size() > 0) {
//				LogUtils.d("解析成功！");
//			}
//		} catch (NotFoundException e) {
//			e.printStackTrace();
//		} catch (XmlPullParserException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	private void initAdressDatas() {
		mProvinceDatas = new String[mProvinces.size()];
		for(int i=0;i<mProvinces.size();i++){
			String provinceId = mProvinces.get(i).getId();
			LogUtils.i("provinceId:"+provinceId);
			mProvinceDatas[i] = provinceId;
			AddressList<Town> towns = mProvinces.get(i).getTowns();
			String[] townDatas = new String[towns.size()];
			for(int j=0;j<towns.size();j++){
				String townId = towns.get(j).getId(); 
				townDatas[j] = townId;
				AddressList<Area> areas = towns.get(j).getAreas();
				mDistrictDatasMap.put(townId, areas);
			}
			mCitisDatasMap.put(provinceId, towns);
		}
	}
	
    /*
     * 获取当前的省
     */
	public Province getProvince() {
		return province;
	}
    
	 /*
     * 获取当前的市
     */
	public Town getTown() {
		return town;
	}
    
	/*
     * 获取当前的区，县
     */
	public Area getArea() {
		return area;
	}

    
}
