package com.example.babyweather;

import java.util.ArrayList;
import java.util.List;

import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

import com.example.babyweather.mode.ClassforState;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomGuide extends Activity {

	private Button btn;
	private TextView textView1;
	private TextView textView2;
	private ViewPager pager;
	private List<View> list;
	private String provider;

	private String latitude;
	private String longitude;
	private String address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		btn = (Button) findViewById(R.id.welcom_guide_btn);
		pager = (ViewPager) findViewById(R.id.welcom_pager);
		textView1 = (TextView) findViewById(R.id.textView1);
		textView2 = (TextView) findViewById(R.id.textView2);
		initViewPager();

		// 得到本地经纬度
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providerList = locationManager.getProviders(true);
		if (providerList.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
			ClassforState.netState = 1;
		} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
			// 使用网络定位
			provider = LocationManager.NETWORK_PROVIDER;
			ClassforState.netState = 1;
		} else {
			// netState=1为存在网络
			ClassforState.netState = 0;
		}

		Location location = locationManager.getLastKnownLocation(provider);
		latitude = location.getLatitude() + "";
		longitude = location.getLongitude() + "";
		// 根据经纬度查询本地天气并添加到ClassforList.cityList中，这样第一次进入APP就能显示一个本地天气
		getLocalCityWeather(latitude, longitude);
		Log.i("LHD", "维度latitude=" + latitude + "   经度longitude" + longitude);
	}

	public void doclick(View view) {
		// 页面跳转
		startActivity(new Intent(getBaseContext(), MainActivity.class));
		finish();
	}

	// 初始化ViewPager的方法
	public void initViewPager() {
		list = new ArrayList<View>();

		ImageView iv = new ImageView(this);
		iv.setImageResource(R.drawable.guid1);

		list.add(iv);

		ImageView iv2 = new ImageView(this);
		iv2.setImageResource(R.drawable.guid1);

		list.add(iv2);

		ImageView iv3 = new ImageView(this);
		iv3.setImageResource(R.drawable.guid1);

		list.add(iv3);
		pager.setAdapter(new MyPagerAdapter());

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// 如果是第二个页面
				if (arg0 == 2) {
					btn.setVisibility(View.VISIBLE);
					textView1.setVisibility(View.VISIBLE);
					textView2.setVisibility(View.VISIBLE);
				} else {
					btn.setVisibility(View.GONE);
					textView1.setVisibility(View.GONE);
					textView2.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	// 定义ViewPager的适配器
	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		// 初始化item实例的方法
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(list.get(position));
			return list.get(position);
		}

		// item销毁的方法
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(list.get(position));
		}
	}

	// 根据经纬度得到城市的名字

	/**
	 * 查询城市名所对应的天气
	 * 
	 * @param countyCode
	 */
	private void getLocalCityWeather(String latitude, String longitude) {
		// 拼写请求地址
		address = "http://v.juhe.cn/weather/geo?format=2&key=f70eeb1f409350ace226078e20e22eb5&lon="
				+ longitude + "&lat=" + latitude;
		Log.i("LHD", "CountyWeatherAddress = " + address);

		HttpUtil.sendHttpRequest(address, null, new HttpCallbackListener() {

			@Override
			public void onFinish(String response, String response2) {
				// 处理服务器返回的天气信息
				Utility.handleWeatherResponse(response, response2);
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				Toast.makeText(WelcomGuide.this, "定位城市失败，请开启定位功能",
						Toast.LENGTH_LONG).show();
			}

		});

	}

}
