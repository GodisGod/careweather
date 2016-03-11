package com.example.babyweather;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import kll.dod.rtk.br.AdSize;
import kll.dod.rtk.br.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.babyweather.mode.ClassforList;
import com.example.babyweather.mode.MainList;

import util.HttpCallbackListener;
import util.HttpUtil;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CityActivity extends Activity implements OnClickListener {

	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;
	private Button backWeather;
	/**
	 * 主界面布局
	 */
	private LinearLayout main_layout;

	/**
	 * 和天气相关的控件
	 */
	// 今天天气相关
	private RelativeLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView city_temp;
	private TextView dress;
	private TextView tip_AQI;
	private TextView city_AQI;
	private TextView city_quality;
	// 未来三天天气相关
	private RelativeLayout future_RelativeLayout;
	private TextView mingtianweekText;
	private TextView mingtianweatherText;
	private TextView mingtiantempText;

	private TextView houtianweekText;
	private TextView houtianweatherText;
	private TextView houtiantempText;

	private TextView dahoutianweekText;
	private TextView dahoutianweatherText;
	private TextView dahoutiantempText;

	/**
	 * 一些更新天气需要的变量
	 */
	private String address; // 请求天气的地址
	private String address2; // 请求空气质量的地址
	private static String cityname; // 当前的城市名
	private static String time; // 当前年月日
	private static String weather; // 当前的天气
	private static String dressing_index; // 穿衣指数 较冷 很冷 等
	private static String temp; // 今天的温度

	static String publish_time; // 天气更新时间

	// 未来三天的天气
	static String mingtianweek;
	static String mingtianweather;
	static String mingtiantemp;

	static String houtianweek;
	static String houtianweather;
	static String houtiantemp;

	static String dahoutianweek;
	static String dahoutianweather;
	static String dahoutiantemp;

	// 空气质量状况
	static String AQI;// 空气质量指数。PM2.5指数只是AQI6个检测标准的其中一个
	static String quality;// 空气质量 ：严重污染 优等描述词

	// 此城市天气在list中的位置
	private static int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

		// 实例化广告条
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// 获取要嵌入广告条的布局
		LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);

		// 将广告条加入到布局中
		adLayout.addView(adView);
		
		// 初始化各控件
		weatherInfoLayout = (RelativeLayout) findViewById(R.id.weather_info_layout);
		future_RelativeLayout = (RelativeLayout) findViewById(R.id.future_layout);
		// 城市名
		cityNameText = (TextView) findViewById(R.id.city_name);
		// 发布时间
		publishText = (TextView) findViewById(R.id.publish_text);
		// 天气
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		// 温度
		city_temp = (TextView) findViewById(R.id.city_temp);
		// 穿衣指数描述温度
		dress = (TextView) findViewById(R.id.dress);
		// AQI
		tip_AQI = (TextView) findViewById(R.id.tip_AQI);
		city_AQI = (TextView) findViewById(R.id.city_AQI);
		tip_AQI.setVisibility(View.GONE);
		city_AQI.setVisibility(View.GONE);
		// quality
		city_quality = (TextView) findViewById(R.id.city_quality);
		city_quality.setVisibility(View.GONE);
		// 未来三天天气相关的控件
		mingtianweekText = (TextView) findViewById(R.id.mingtian);
		mingtianweatherText = (TextView) findViewById(R.id.mingtianweather);
		mingtiantempText = (TextView) findViewById(R.id.mingtiantemp);

		houtianweekText = (TextView) findViewById(R.id.houtian);
		houtianweatherText = (TextView) findViewById(R.id.houtianweather);
		houtiantempText = (TextView) findViewById(R.id.houtiantemp);

		dahoutianweekText = (TextView) findViewById(R.id.dahoutian);
		dahoutianweatherText = (TextView) findViewById(R.id.dahoutianweather);
		dahoutiantempText = (TextView) findViewById(R.id.dahoutiantemp);

		// 刷新城市
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		// 返回按钮
		backWeather = (Button) findViewById(R.id.back_weather);
		// 详细天气显示的父布局
		main_layout = (LinearLayout) findViewById(R.id.city_layout);

		weatherInfoLayout.setVisibility(View.INVISIBLE);
		future_RelativeLayout.setVisibility(View.INVISIBLE);
		cityNameText.setVisibility(View.INVISIBLE);
		refreshWeather.setOnClickListener(this);
		backWeather.setOnClickListener(this);

		position = getIntent().getIntExtra("position", 0);
		MainList city = ClassforList.getCityList().get(position);

		cityname = city.getCitynameString();
		dressing_index = city.getDressing_index();
		temp = city.getTemp();
		weather = city.getCityweather();
		time = city.getTime();
		publish_time = city.getPublishtime();
		mingtianweek = city.getMingtianweek();
		mingtianweather = city.getMingtianweather();
		mingtiantemp = city.getMingtiantemp();
		houtianweek = city.getHoutianweek();
		houtianweather = city.getHoutianweather();
		houtiantemp = city.getHoutiantemp();
		dahoutianweek = city.getDahoutianweek();
		dahoutianweather = city.getDahoutianweather();
		dahoutiantemp = city.getDahoutiantemp();
		AQI = city.getAQI();
		quality = city.getQuality();

		showWeather(cityname, dressing_index, temp, weather, time, publish_time, mingtianweek, mingtianweather, mingtiantemp, houtianweek, houtianweather,
				houtiantemp, dahoutianweek, dahoutianweather, dahoutiantemp, AQI, quality);
	}

	/**
	 * 查询城市名所对应的天气
	 * 
	 * @param countyCode
	 */
	private void queryWeatherCode(String cityName) {
		try {
			String citynameString = URLEncoder.encode(cityName, "UTF-8");
			address = // 此处以返回json格式数据示例,所以format=2,以根据城市名称为例,cityName传入中文
			"http://v.juhe.cn/weather/index?cityname=" + citynameString + "&key=f70eeb1f409350ace226078e20e22eb5";

			address2 = "http://op.juhe.cn/onebox/weather/query?cityname=" + citynameString + "&key=5fc2b142029b1b0d371d5449a8b8927d";

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("LHD", "CountyWeatherAddress = " + address);

		HttpUtil.sendHttpRequest(address, address2, new HttpCallbackListener() {
			@Override
			public void onFinish(String response, String response2) {
				// 处理服务器返回的天气信息
				handleWeatherResponse(CityActivity.this, response, response2);
				runOnUiThread(new Runnable() {
					public void run() {
						showWeather(cityname, dressing_index, temp, weather, time, publish_time, mingtianweek, mingtianweather, mingtiantemp, houtianweek,
								houtianweather, houtiantemp, dahoutianweek, dahoutianweather, dahoutiantemp, AQI, quality);
					}
				});
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				Toast.makeText(CityActivity.this, "同步失败", Toast.LENGTH_LONG).show();
			}

		});

	}

	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
	 * 
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(Context context, final String response, final String response2) {

		jiexishuju(response, response2);
		MainList city = new MainList(cityname, dressing_index, temp, weather, time.subSequence(5, 11).toString(), publish_time, mingtianweek, mingtianweather,
				mingtiantemp, houtianweek, houtianweather, houtiantemp, dahoutianweek, dahoutianweather, dahoutiantemp, AQI, quality);

		ClassforList.cityList.remove(position);
		ClassforList.cityList.add(position, city);

	}

	/**
	 * 解析数据
	 * 
	 * @param context
	 * @param response
	 */
	public static void jiexishuju(final String response, final String response2) {
		jiexitianqi(response);
		jiexikongqizhiliang(response2);

	}

	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地static变量中
	 * 
	 * @param context
	 * @param response
	 */
	public static void jiexitianqi(final String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			int resultcode = jsonObject.getInt("resultcode");
			if (resultcode == 200) {
				JSONObject result = jsonObject.getJSONObject("result");
				// 实时信息
				JSONObject sk = result.getJSONObject("sk");
				publish_time = sk.getString("time");
				// 今天信息
				JSONObject today = result.getJSONObject("today");
				cityname = today.getString("city");
				time = today.getString("date_y");
				temp = today.getString("temperature");
				dressing_index = today.getString("dressing_index");
				weather = today.getString("weather");
				// 未来三天的信息
				JSONObject future = result.getJSONObject("future");
				Log.i("LHD", publish_time + "  " + cityname + time + temp + dressing_index + weather);

				String mingtianObjectString = "day_" + (Integer.parseInt(time.replace("年", "").replace("月", "").replace("日", "")) + 1);
				String houtianObjectString = "day_" + (Integer.parseInt(time.replace("年", "").replace("月", "").replace("日", "")) + 2);
				String dahoutianObjectString = "day_" + (Integer.parseInt(time.replace("年", "").replace("月", "").replace("日", "")) + 3);

				String mingtianString = null;
				String houtianString = null;
				String dahoutianString = null;
				try {
					mingtianString = URLEncoder.encode(mingtianObjectString, "UTF-8");
					houtianString = URLEncoder.encode(houtianObjectString, "UTF-8");
					dahoutianString = URLEncoder.encode(dahoutianObjectString, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i("LHD", mingtianObjectString + "  " + houtianObjectString + "  " + dahoutianObjectString);
				JSONObject mingtianObject = future.getJSONObject(mingtianString);
				JSONObject houtianObject = future.getJSONObject(houtianString);
				JSONObject dahoutianObject = future.getJSONObject(dahoutianString);
				// // 明天的天气
				mingtianweek = mingtianObject.getString("week");
				mingtianweather = mingtianObject.getString("weather");
				mingtiantemp = mingtianObject.getString("temperature");
				Log.i("LHD", mingtianweek + mingtianweather + mingtiantemp);
				// 后天的天气
				houtianweek = houtianObject.getString("week");
				houtianweather = houtianObject.getString("weather");
				houtiantemp = houtianObject.getString("temperature");
				// 大后天的天气
				dahoutianweek = dahoutianObject.getString("week");
				dahoutianweather = dahoutianObject.getString("weather");
				dahoutiantemp = dahoutianObject.getString("temperature");

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地static变量中
	 * 
	 * @param context
	 * @param response
	 */
	public static void jiexikongqizhiliang(final String response2) {

		JSONObject jsonObject;
		String reason;
		try {
			jsonObject = new JSONObject(response2);
			// reason = URLEncoder.encode(jsonObject.getString("reason"),
			// "UTF-8");
			reason = jsonObject.getString("reason");
			Log.i("LHD", "reason===" + reason);
			if (reason.equals("successed!")) {
				JSONObject result = jsonObject.getJSONObject("result");
				JSONObject data = result.getJSONObject("data");
				JSONObject pm25object = data.getJSONObject("pm25");
				JSONObject pm25ob = pm25object.getJSONObject("pm25");
				AQI = pm25ob.getString("pm25");
				quality = pm25ob.getString("quality");
				Log.i("LHD", "AQI  " + AQI + "quality   " + quality);
				if (TextUtils.isEmpty(AQI) || TextUtils.isEmpty(quality)) {
					AQI = "无";
					quality = "无";
				}
			} else {
				AQI = "无";
				quality = "无";
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 显示天气界面
	 */
	private void showWeather(String name, String dress, String temp, String weather, String time, String publishtime, String mingtianweek,
			String mingtianweather, String mingtiantemp, String houtianweek, String houtianweather, String houtiantemp, String dahoutianweek,
			String dahoutianweather, String dahoutiantemp, String AQI, String quality) {

		cityNameText.setText(name);
		publishText.setText(publishtime + "发布");
		weatherDespText.setText(weather);
		city_temp.setText(temp);
		this.dress.setText(dress);

		if (AQI.equals("无") || quality.equals("无")) {
			city_AQI.setVisibility(View.GONE);
			city_quality.setVisibility(View.GONE);
		} else {
			tip_AQI.setVisibility(View.VISIBLE);
			city_AQI.setVisibility(View.VISIBLE);
			city_quality.setVisibility(View.VISIBLE);
			city_AQI.setText(AQI);
			city_quality.setText(quality);
		}

		mingtianweekText.setText(mingtianweek);
		mingtianweatherText.setText(mingtianweather);
		mingtiantempText.setText(mingtiantemp);

		houtianweekText.setText(houtianweek);
		houtianweatherText.setText(houtianweather);
		houtiantempText.setText(houtiantemp);

		dahoutianweekText.setText(dahoutianweek);
		dahoutianweatherText.setText(dahoutianweather);
		dahoutiantempText.setText(dahoutiantemp);

		weatherInfoLayout.setVisibility(View.VISIBLE);
		future_RelativeLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);

		/**
		 * 切换图片
		 */
		weatherpic(weather);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh_weather:

			// 得到城市名
			MainList city = ClassforList.getCityList().get(position);
			cityname = city.getCitynameString();
			if (!TextUtils.isEmpty(cityname)) {
				// 有城市名就去查询天气
				Log.i("LHD", "cityName = " + cityname);
				queryWeatherCode(cityname);
			} else {
				// 没有县级代号就直接显示本地天气
				showWeather(cityname, dressing_index, temp, weather, time, publish_time, mingtianweek, mingtianweather, mingtiantemp, houtianweek,
						houtianweather, houtiantemp, dahoutianweek, dahoutianweather, dahoutiantemp, AQI, quality);
			}
			break;
		case R.id.back_weather:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}

	private void weatherpic(String weatherState) {
		if ("晴".equals(weatherState)) {
			main_layout.setBackgroundResource(R.drawable.qing);
		} else if ("多云".equals(weatherState)) {
			main_layout.setBackgroundResource(R.drawable.duoyun);
		} else if ("小雨".equals(weatherState)) {
			main_layout.setBackgroundResource(R.drawable.weimei_de_yu);
		} else if (weatherState.contains("风")) {
			main_layout.setBackgroundResource(R.drawable.storm);
		} else if (weatherState.contains("雷")) {
			main_layout.setBackgroundResource(R.drawable.leiyu);
		} else if (weatherState.contains("多云转")) {
			main_layout.setBackgroundResource(R.drawable.duoyuzhuanqing);
		} else if (weatherState.contains("转多云")) {
			main_layout.setBackgroundResource(R.drawable.qingzhuanduoyun);
		} else if (weatherState.contains("霾")) {
			main_layout.setBackgroundResource(R.drawable.wumai);
		} else if ("阵雨".equals(weatherState)) {
			main_layout.setBackgroundResource(R.drawable.zhenyu);
		} else if(weatherState.contains("阴")){
			main_layout.setBackgroundResource(R.drawable.yin);
		}else if(weatherState.contains("雪")){
			main_layout.setBackgroundResource(R.drawable.xue);
		}else {
			main_layout.setBackgroundResource(R.drawable.qita);
		}
		

	}
}
