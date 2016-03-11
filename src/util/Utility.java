package util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.babyweather.mode.ClassforList;
import com.example.babyweather.mode.GuanxinPerson;
import com.example.babyweather.mode.MainList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import db.GodTemperDB;
import entity.City;
import entity.County;
import entity.Province;

public class Utility {

	/**
	 * ��������ز���
	 */
	private static String temp; // ������¶�
	private static String cityname; // ��ǰ�ĳ�����
	private static String time; // ��ǰ������
	private static String weather; // ��ǰ������
	private static String dressing_index;// ����ָ�� ���� ���� ��
	private static String publish_time; // ��������ʱ��
	// δ�����������
	private static String mingtianweek;
	private static String mingtianweather;
	private static String mingtiantemp;

	private static String houtianweek;
	private static String houtianweather;
	private static String houtiantemp;

	private static String dahoutianweek;
	private static String dahoutianweather;
	private static String dahoutiantemp;

	// ��������״��
	private static String AQI;// ��������ָ����PM2.5ָ��ֻ��AQI6������׼������һ��
	private static String quality;// �������� ��������Ⱦ �ŵ�������
	// �洢�ļ���������
	private static final String FILE_NAME = "guanxinweather";
	// �洢�����key
	private static final String NODE_NAME = "welcome";
	public static final String YJ_NAME = "yujing";
	public static final String GX_NAME = "guanxin";
	public static final String GUANXINLIST = "guanxinlist";
	public static final String CITYLIST = "citylist";

	// ����boolean���͵�ֵ
	public static boolean getWelcomeBoolean(Context context) {
		// ͨ��getSharedPreferences����ָ��SharedPreference�ļ���ΪFILE_NAME,
		// ��дģʽΪMODE_PRIVATE ��ȡ����ֵΪNODE_NAME������
		return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getBoolean(NODE_NAME, true);// Ĭ��ֵΪtrue
																											// ��Ĭ���ǵ�һ�ν���Ӧ�ó���
	}

	// ����boolean���͵�ֵ
	public static void putWelcomBoolean(Context context, boolean isFirst) {
		// ͨ��getSharedPreferences����ָ��SharedPreference�ļ���ΪFILE_NAME,
		// ��дģʽΪMODE_APPEND ��ͨ��edit�����õ�һ��editor����
		Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_APPEND).edit();
		// �����ֵ��
		editor.putBoolean(NODE_NAME, isFirst);
		editor.commit();
	}

	// ��������״̬
	public static void putsetBoolean(Context context, String key, boolean isFirst) {
		// ͨ��getSharedPreferences����ָ��SharedPreference�ļ���ΪFILE_NAME,
		// ��дģʽΪMODE_APPEND ��ͨ��edit�����õ�һ��editor����
		Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_APPEND).edit();
		// �����ֵ��
		editor.putBoolean(key, isFirst);
		editor.commit();
	}

	// �õ�����״̬
	public static boolean getsetBoolean(Context context, String key, boolean isFirst) {
		return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getBoolean(key, true);// Ĭ��ֵΪtrue
																									// ��Ĭ�ǿ���
	}

	// ������ĵ��˵����� name, num, city,weather, content,time,boolean checked
	public static void SaveGuanxinList(Context context, List<GuanxinPerson> guanxinlist) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

		JSONArray jsonArray = new JSONArray();
		for (GuanxinPerson guanxinPerson : guanxinlist) {
			JSONObject object = new JSONObject();
			try {
				object.put("name", guanxinPerson.getName());
				object.put("num", guanxinPerson.getPhnum());
				object.put("city", guanxinPerson.getCity());
				object.put("weather", guanxinPerson.getWeather());
				object.put("content", guanxinPerson.getContent());
				object.put("time", guanxinPerson.getTime());
				object.put("checked", guanxinPerson.isChecked());
				jsonArray.put(object);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		editor.putString(GUANXINLIST, jsonArray.toString());
		editor.commit();
		Log.i("LHD", "guanxinlist====" + jsonArray.toString());
	}

	// �õ����ĵ��˵�����
	public static List<GuanxinPerson> getGuanxinPersons(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String guanxinlistJsonArray = pref.getString(GUANXINLIST, "");
		List<GuanxinPerson> list = new ArrayList<GuanxinPerson>();
		try {
			JSONArray jsonArray = new JSONArray(guanxinlistJsonArray);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = (JSONObject) jsonArray.get(i);
				GuanxinPerson person = new GuanxinPerson(object.getString("name"), object.getString("num"), object.getString("city"),
						object.getString("weather"), object.getString("content"), object.getString("time"), object.getBoolean("checked"));
				list.add(person);
				Log.i("LHD", "getguanxinlist====" + list.size() + object.getString("name") + object.getString("num"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;

	}

	// �������list name, num, city,weather, content,time,boolean checked
	public static void SaveCityList(Context context, List<MainList> citylist) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		JSONArray jsonArray = new JSONArray();
		for (MainList city : citylist) {
			JSONObject object = new JSONObject();
			try {
				object.put("name", city.getCitynameString());
				object.put("dressing_index", city.getDressing_index());
				object.put("tempereture", city.getTemp());
				object.put("weatherdesp", city.getCityweather());
				object.put("time", city.getTime());
				object.put("publishTime", city.getPublishtime());

				object.put("mingtianweek", city.getMingtianweek());
				object.put("mingtianweather", city.getMingtianweather());
				object.put("mingtiantemp", city.getMingtiantemp());

				object.put("houtianweek", city.getHoutianweek());
				object.put("houtianweather", city.getHoutianweather());
				object.put("houtiantemp", city.getHoutiantemp());

				object.put("dahoutianweek", city.getDahoutianweek());
				object.put("dahoutianweather", city.getDahoutianweather());
				object.put("dahoutiantemp", city.getDahoutiantemp());

				object.put("AQI", city.getAQI());
				object.put("quality", city.getQuality());

				jsonArray.put(object);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		editor.putString(CITYLIST, jsonArray.toString());
		editor.commit();
		// Log.i("LHD", "savecitylist====" + jsonArray.toString());
	}

	// �õ����е�list
	public static List<MainList> getCityList(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String citylistJsonArray = pref.getString(CITYLIST, "");
		List<MainList> list = new ArrayList<MainList>();
		try {
			JSONArray jsonArray = new JSONArray(citylistJsonArray);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = (JSONObject) jsonArray.get(i);

				MainList city = new MainList(object.getString("name"), object.getString("dressing_index"), object.getString("tempereture"),
						object.getString("weatherdesp"), object.getString("time"), object.getString("publishTime"), object.getString("mingtianweek"),
						object.getString("mingtianweather"), object.getString("mingtiantemp"), object.getString("houtianweek"),
						object.getString("houtianweather"), object.getString("houtiantemp"), object.getString("dahoutianweek"),
						object.getString("dahoutianweather"), object.getString("dahoutiantemp"), object.getString("AQI"), object.getString("quality"));

				list.add(city);
				// Log.i("LHD", "getcitylist====" + list.size() +
				// object.getString("name"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;

	}

	/**
	 * �����ʹ�����������ص�ʡ������ ���������ص�ʡ�������ݶ��ǡ�����|���У�����|���С����ָ�ʽ�ģ���Ҫ����
	 */
	public synchronized static boolean handleProvincesResponse(GodTemperDB godTemperDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvince = response.split(",");// ���÷ָ���
			if (allProvince != null && allProvince.length > 0) {
				for (String p : allProvince) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// ���������������ݴ洢��Province��
					godTemperDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �����ʹ�����������ص��м�����
	 */
	public static boolean handleCitiesResponse(GodTemperDB godTemperDB, String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allcities = response.split(",");
			if (allcities != null && allcities.length > 0) {
				for (String c : allcities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					// ���������������ݴ洢��City��
					godTemperDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �����ʹ�����������ص��ؼ�����
	 */
	public static boolean handleCountiesResponse(GodTemperDB godTemperDB, String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					// ���������������ݴ洢��County��
					godTemperDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �������������ص�JSON���ݣ����������������ݴ洢������
	 * 
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(final String response, final String response2) {
		Log.i("LHD", "��ʼ��������------handleWeatherResponse");
//		Log.i("LHD", response.toString()+"\n"+response2.toString());
		jiexishuju(response, response2);
		MainList city = new MainList(cityname, dressing_index, temp, weather, time.subSequence(5, 11).toString(), publish_time, mingtianweek, mingtianweather,
				mingtiantemp, houtianweek, houtianweather, houtiantemp, dahoutianweek, dahoutianweather, dahoutiantemp, AQI, quality);
		Log.i("LHD", cityname + "  " + weather + "  " + AQI + "  " + quality);
		ClassforList.cityList.add(city);
	}

	/**
	 * ��������
	 * 
	 * @param context
	 * @param response
	 */
	public static void jiexishuju(final String response, final String response2) {
		Log.i("LHD", "��������״����--��"+response.toString());
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
				Log.i("LHD", "��ʼ��������������������");
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
				Log.i("LHD", "�������ݣ�" + "  " + cityname + time + temp + dressing_index + weather);
//				Log.i("LHD", "δ��������----"+future.toString());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				 Calendar calendar = new GregorianCalendar();
				 calendar.setTime(date);
				 calendar.add(calendar.DATE,1);//��������������һ��.����������,������ǰ�ƶ�
				 date=calendar.getTime(); //���ʱ���������������һ��Ľ��				 
				String mingtian = format.format(date);
//				Log.i("LHD", "����������ǣ�   "+mingtian);
				
				 calendar.add(calendar.DATE,1);//��������������һ��.����������,������ǰ�ƶ�
				 date=calendar.getTime(); //���ʱ���������������һ��Ľ��				 
				String houtian = format.format(date);
//				Log.i("LHD", "����������ǣ�   "+houtian);
				
				 calendar.add(calendar.DATE,1);//��������������һ��.����������,������ǰ�ƶ�
				 date=calendar.getTime(); //���ʱ���������������һ��Ľ��				 
				String dahoutian = format.format(date);
//				Log.i("LHD", "�����������ǣ�   "+dahoutian);
				
				String mingtianObjectString = "day_" + (mingtian.replace("-", ""));
				String houtianObjectString = "day_" + (houtian.replace("-", ""));
				String dahoutianObjectString = "day_" + (dahoutian.replace("-", ""));
				Log.i("LHD", mingtianObjectString+"  "+houtianObjectString+"  "+dahoutianObjectString);
				String mingtianString = null;
				String houtianString = null;
				String dahoutianString = null;
				try {
					mingtianString = URLEncoder.encode(mingtianObjectString, "UTF-8");
					houtianString = URLEncoder.encode(houtianObjectString, "UTF-8");
					dahoutianString = URLEncoder.encode(dahoutianObjectString, "UTF-8");
					Log.i("LHD", mingtianString+"  "+houtianString+"  "+dahoutianString);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JSONObject mingtianObject = future.getJSONObject(mingtianString);
				JSONObject houtianObject = future.getJSONObject(houtianString);
				JSONObject dahoutianObject = future.getJSONObject(dahoutianString);
				Log.i("LHD", mingtianObject.toString());
				// // ���������
				mingtianweek = mingtianObject.getString("week");
				mingtianweather = mingtianObject.getString("weather");
				mingtiantemp = mingtianObject.getString("temperature");
//				Log.i("LHD", mingtianweek + mingtianweather + mingtiantemp);
				// ���������
				houtianweek = houtianObject.getString("week");
				houtianweather = houtianObject.getString("weather");
				houtiantemp = houtianObject.getString("temperature");
				// ����������
				dahoutianweek = dahoutianObject.getString("week");
				dahoutianweather = dahoutianObject.getString("weather");
				dahoutiantemp = dahoutianObject.getString("temperature");
				
//				Log.i("LHD", "���죺"+mingtianweather+"  ���죺"+houtianweather+"  ����죺"+dahoutianweather);

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
		// �ۺ���������Ԥ���ӿڽ���

		JSONObject jsonObject;
		String reason;
		try {
			jsonObject = new JSONObject(response2);
			// reason = URLEncoder.encode(jsonObject.getString("reason"),
			// "UTF-8");
			reason = jsonObject.getString("reason");
			Log.i("LHD", "Utility reason===" + reason);
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
