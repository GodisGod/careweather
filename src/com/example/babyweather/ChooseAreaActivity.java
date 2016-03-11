package com.example.babyweather;

import java.util.ArrayList;
import java.util.List;

import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import db.GodTemperDB;
import entity.City;
import entity.County;
import entity.Province;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;

	private GodTemperDB godTemperDB;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private List<String> dataList = new ArrayList<String>();

	/**
	 * 省市县列表
	 */
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	/**
	 * 选中的省市县
	 */
	private Province selectedProvince;
	private City selectedCity;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.choosecity_list);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		godTemperDB = GodTemperDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					String cityName = countyList.get(index).getCountyName();
					String msg = getIntent().getStringExtra("MsgActivity");
					if (!TextUtils.isEmpty(msg)) {
						Intent intent = new Intent(ChooseAreaActivity.this, MsgActivity.class);
						intent.putExtra("cityName", cityName);
						startActivity(intent);
					} else {
						Intent intent = new Intent();
						intent.putExtra("cityName", cityName);
						setResult(RESULT_OK, intent);
					}
					finish();
				}
			}
		});
		queryProvinces();
	}

	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到再到服务器上查询
	 */
	private void queryProvinces() {
		provinceList = godTemperDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	private void queryCities() {
		cityList = godTemperDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * 查询选中省的所有市，优先从数据库查询，如果没有再到服务器查询
	 */
	private void queryCounties() {
		countyList = godTemperDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据
	 * 
	 * @param object
	 * @param string
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, null, new HttpCallbackListener() {

			@Override
			public void onFinish(String response, String response2) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(godTemperDB, response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(godTemperDB, response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(godTemperDB, response, selectedCity.getId());
				}
				if (result) {
					// 通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// 通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败,可能未联网或者网速过慢", Toast.LENGTH_SHORT).show();
					}
				});
			}

		});
	}

	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表，省列表，还是直接退出
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else if (currentLevel == LEVEL_PROVINCE) {
			String str = getIntent().getStringExtra("MainActivity");
			// 如果是MainActivity的添加城市按钮，返回的时候就返回MainActivity
			if (!TextUtils.isEmpty(str)) {
				Intent intent = new Intent();
				intent.putExtra("province", "province");
				setResult(RESULT_OK, intent);
			}
			// 如果是关心的人的预警功能的选择城市按钮，返回的时候就直接finish返回MsgActivity
			finish();
		}
	}
}
