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
	 * һЩ����������Ҫ�ı���
	 */
	private static String cityname;
	private static String time;
	private static String weather;
	private static String dressing_index;
	private static String temp;
	private String address;
	private String address2;

	static String publish_time; // ��������ʱ��

	// δ�����������
	static String mingtianweek;
	static String mingtianweather;
	static String mingtiantemp;

	static String houtianweek;
	static String houtianweather;
	static String houtiantemp;

	static String dahoutianweek;
	static String dahoutianweather;
	static String dahoutiantemp;

	// ��������״��
	static String AQI;// ��������ָ����PM2.5ָ��ֻ��AQI6������׼������һ��
	static String quality;// �������� ��������Ⱦ �ŵ�������
	public static final List<MainList> updatacityList = new ArrayList<MainList>();

	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.i("LHD", "AutoUpdateReceiver--->onReceive");
		updateWeather(context);

	}

	/**
	 * ����������Ϣ
	 */
	protected void updateWeather(Context context) {
		Log.i("LHD", "AutoUpdateReceiver--->updateWeather--->updateWeather  citysize==" + ClassforList.getCityList().size());
		for (int i = 0; i < ClassforList.getCityList().size(); i++) {
			try {
				cityname = ClassforList.getCityList().get(i).getCitynameString();
				String citynameString = URLEncoder.encode(cityname, "UTF-8");
				address = // �˴��Է���json��ʽ����ʾ��,����format=2,�Ը��ݳ�������Ϊ��,cityName��������
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
	 * �������������ص�JSON���ݣ����������������ݴ洢������
	 * 
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(final String response, final String response2) {
		Log.i("LHD", "weather =====" + response);
		jiexishuju(response, response2);
		Log.i("LHD", "ɾ������ַ���==========" + time.subSequence(5, 11));
		MainList city = new MainList(cityname, dressing_index, temp, weather, time.subSequence(5, 11).toString(), publish_time, mingtianweek, mingtianweather,
				mingtiantemp, houtianweek, houtianweather, houtiantemp, dahoutianweek, dahoutianweather, dahoutiantemp, AQI, quality);
		updatacityList.add(city);

	}

	/**
	 * ��������
	 * 
	 * @param context
	 * @param response
	 */
	public static void jiexishuju(final String response, final String response2) {
		jiexitianqi(response);
		jiexikongqizhiliang(response2);

	}

	/**
	 * �������������ص�JSON���ݣ����������������ݴ洢������static������
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
				// ʵʱ��Ϣ
				JSONObject sk = result.getJSONObject("sk");
				publish_time = sk.getString("time");
				// ������Ϣ
				JSONObject today = result.getJSONObject("today");
				cityname = today.getString("city");
				time = today.getString("date_y");
				temp = today.getString("temperature");
				dressing_index = today.getString("dressing_index");
				weather = today.getString("weather");
				// δ���������Ϣ
				JSONObject future = result.getJSONObject("future");
				Log.i("LHD", publish_time + "  " + cityname + time + temp + dressing_index + weather);

				String mingtianObjectString = "day_" + (Integer.parseInt(time.replace("��", "").replace("��", "").replace("��", "")) + 1);
				String houtianObjectString = "day_" + (Integer.parseInt(time.replace("��", "").replace("��", "").replace("��", "")) + 2);
				String dahoutianObjectString = "day_" + (Integer.parseInt(time.replace("��", "").replace("��", "").replace("��", "")) + 3);

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
				// // ���������
				mingtianweek = mingtianObject.getString("week");
				mingtianweather = mingtianObject.getString("weather");
				mingtiantemp = mingtianObject.getString("temperature");
				Log.i("LHD", mingtianweek + mingtianweather + mingtiantemp);
				// ���������
				houtianweek = houtianObject.getString("week");
				houtianweather = houtianObject.getString("weather");
				houtiantemp = houtianObject.getString("temperature");
				// ����������
				dahoutianweek = dahoutianObject.getString("week");
				dahoutianweather = dahoutianObject.getString("weather");
				dahoutiantemp = dahoutianObject.getString("temperature");

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * �������������ص�JSON���ݣ����������������ݴ洢������static������
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
					AQI = "��";
					quality = "��";
				}
			} else {
				AQI = "��";
				quality = "��";
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
