package receiver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.babyweather.mode.ClassforList;
import com.example.babyweather.mode.MainList;

import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

@SuppressWarnings("unused")
public class AutoUpdateReceiver extends BroadcastReceiver {
	/**
	 * 一些更新天气需要的变量
	 */
	private static String cityname;
	private static String time;
	private static String weather;
	private static String dressing_index;
	private static String temp;
	private String address;
	private String address2;

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
	public static final List<MainList> updatacityList = new ArrayList<MainList>();

	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.i("LHD", "AutoUpdateReceiver--->onReceive");
		updateWeather(context);

	}

	/**
	 * 更新天气信息
	 */
	protected void updateWeather(Context context) {
		Log.i("LHD", "AutoUpdateReceiver--->updateWeather--->updateWeather  citysize==" + ClassforList.getCityList().size());
		for (int i = 0; i < ClassforList.getCityList().size(); i++) {
			try {
				cityname = ClassforList.getCityList().get(i).getCitynameString();
				String citynameString = URLEncoder.encode(cityname, "UTF-8");
				address = // 此处以返回json格式数据示例,所以format=2,以根据城市名称为例,cityName传入中文
				"http://v.juhe.cn/weather/index?cityname=" + citynameString + "&key=f70eeb1f409350ace226078e20e22eb5";
				Log.i("AutoUpdateReceiver--->updateWeather", address);
				address2 = "http://op.juhe.cn/onebox/weather/query?cityname=" + citynameString + "&key=5fc2b142029b1b0d371d5449a8b8927d";
			
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpUtil.sendHttpRequest(address, address2, new HttpCallbackListener() {
				@Override
				public void onFinish(String response, String response2) {
					Log.i("LHD", "AutoUpdateReceiver--->onFinish");
					handleWeatherResponse(response, response2);
				}

				@Override
				public void onError(Exception e) {
					e.printStackTrace();
				}
			});
		}
		ClassforList.cityList.clear();
		ClassforList.setCityList(updatacityList);
	}

	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
	 * 
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(final String response, final String response2) {
		Log.i("LHD", "weather =====" + response);
		jiexishuju(response, response2);
		Log.i("LHD", "删除后的字符串==========" + time.subSequence(5, 11));
		MainList city = new MainList(cityname, dressing_index, temp, weather, time.subSequence(5, 11).toString(), publish_time, mingtianweek, mingtianweather,
				mingtiantemp, houtianweek, houtianweather, houtiantemp, dahoutianweek, dahoutianweather, dahoutiantemp, AQI, quality);
		updatacityList.add(city);

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
			reason = jsonObject.getString("reason");
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

}
