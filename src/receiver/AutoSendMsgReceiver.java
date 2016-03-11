package receiver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import util.HttpCallbackListener;
import util.HttpUtil;
import com.example.babyweather.mode.ClassforList;
import com.example.babyweather.mode.GuanxinPerson;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.gsm.SmsManager;
import android.util.Log;

@SuppressWarnings("deprecation")
public class AutoSendMsgReceiver extends BroadcastReceiver {
	private List<GuanxinPerson> persons;
	private String hour;
	private String minute;
	private static String weather;
	private String address;
	private String address2;

	@Override
	public void onReceive(Context context, Intent intent) {
		persons = ClassforList.getPersonlist();
		Log.i("MSG", "personlistsize" + persons.size());
		Calendar calendar = Calendar.getInstance();
		// 24小时制就用HOUR_OF_DAY 12小时制就用HOUR
		hour = calendar.get(Calendar.HOUR_OF_DAY) + "";
		// 在凌晨的3-5点检测今天的天气是否需要给关心的是额人发送报警短信
		minute = calendar.get(Calendar.MINUTE) + "";

		Log.i("MSG", "gooooooooooooooo" + hour + "时" + minute + "分");

		for (GuanxinPerson p : persons) {
			String PersonTime = p.getTime();
			String personHour = PersonTime.substring(0, PersonTime.indexOf("时"));
			String personMinute = PersonTime.substring(PersonTime.indexOf("时") + 1, PersonTime.indexOf("分"));
			Log.i("MSG", "persontime" + personHour + personMinute);
			// 如果当前时间和某一个person的设定的时间吻合，就查询对应的天气
			Log.i("MSG", "--" + personHour + "  " + personMinute + "   " + hour + "  " + minute);

			if (hour.equals(personHour) && minute.equals(personMinute)) {
				Log.i("MSG", "--" + personHour + "  " + personMinute + "   " + hour + "  " + minute);
				checkPersonWeather(context, p.getCity());
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 如果关心的人所在的城市和设定的天气吻合就发送关心短信
				Log.i("MSG", weather + "  =  " + p.getWeather());
				Log.i("MSG", p.getCity() + p.getName() + p.getWeather() + p.getTime());
				String st1 = null;
				String st2 = null;
				try {
					st1 = URLEncoder.encode(p.getWeather(), "utf-8");
					st2 = URLEncoder.encode(weather, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (st2.contains(st1)) {
					Log.i("MSG", st2 + "contains" + st1);
					sendMsgToPerson(context, p);
				} else {
					Log.i("MSG", weather + "!!!!!" + p.getWeather());
				}
			}
		}
	}

	private void sendMsgToPerson(Context context, GuanxinPerson p) {
		Log.i("MSG", "--" + "sendMsgToPerson");
		SmsManager smsManager = SmsManager.getDefault();
		Intent sendIntent = new Intent("SENT_SMS_ACTION");
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, sendIntent, 0);
		smsManager.sendTextMessage(p.getPhnum(), null, p.getContent(), pi, null);
	}

	/**
	 * 更新天气信息
	 */
	protected void checkPersonWeather(Context context, String cityname) {

		String citynameString;
		try {
			citynameString = URLEncoder.encode(cityname, "UTF-8");
			address = // 此处以返回json格式数据示例,所以format=2,以根据城市名称为例,cityName传入中文
			"http://v.juhe.cn/weather/index?cityname=" + citynameString + "&key=f70eeb1f409350ace226078e20e22eb5";
			Log.i("MSG", "person==" + address);
			address2 = "http://web.juhe.cn:8080/environment/air/cityair?city=" + citynameString + "&key=34f17fbd9ed83ca9097e8a4caea8db73";
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpUtil.sendHttpRequest(address, address2, new HttpCallbackListener() {
			@Override
			public void onFinish(String response, String response2) {
				Log.i("LHD", "person==" + "onFinish");
				handleWeatherResponse(response);
			}

			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}

		});

	}

	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
	 * 
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(final String response) {

		try {
			JSONObject jsonObject = new JSONObject(response);
			int resultcode = jsonObject.getInt("resultcode");
			if (resultcode == 200) {
				JSONObject result = jsonObject.getJSONObject("result");
				// 今天信息
				JSONObject today = result.getJSONObject("today");
				weather = today.getString("weather");
				Log.i("MSG", "所关心人的城市的天气是==" + weather);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
