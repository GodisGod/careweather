package receiver;

import java.util.Calendar;

import com.example.babyweather.MainActivity;
import com.example.babyweather.R;
import com.example.babyweather.mode.ClassforList;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoYujingReceiver extends BroadcastReceiver {

	private static String cityname;
	private static String weather;
	private static String temp;
	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.i("LHD", "预警闹钟收到了广播");
		yujing(context);
	}

	/**
	 * 
	 * @param context
	 *            上下文
	 * @param content
	 *            预警内容
	 * @param i
	 *            通知id
	 */
	@SuppressWarnings("deprecation")
	private void sendnotify(Context context, String content, int i) {
		Log.i("LHD", "sendnotify");
		// 定义NotificationManager
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager manager = (NotificationManager) context
				.getSystemService(ns);
		// 定义通知栏展现的内容信息
		int icon = R.drawable.guanxinicon;
		String tickerText = "关心天气预警";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		long[] vibrates = { 0, 1000, 1000, 1000 };
		notification.vibrate = vibrates;

		// 点击进入主页面
		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		notification.setLatestEventInfo(context, tickerText, content, pIntent);
		manager.notify(i, notification);
	}

	/**
	 * 查询天气信息
	 */
	protected void yujing(Context context) {
		Log.i("LHD", "进入yujing函数"+"   Citylist size ="+ClassforList.getCityList().size()+"  "+ClassforList.cityList.size());
		for (int i = 0; i < ClassforList.getCityList().size(); i++) {
			cityname = ClassforList.getCityList().get(i).getCitynameString();
			temp = ClassforList.getCityList().get(i).getTemp();
			ClassforList.getCityList().get(i).getPublishtime();
			weather = ClassforList.getCityList().get(i).getCityweather();
			if (weather.contains("雨") || weather.contains("霾")|| weather.contains("雾")||weather.contains("大风")) {
			
				Calendar calendar = Calendar.getInstance();
				//24小时制就用HOUR_OF_DAY  12小时制就用HOUR
				int hour = calendar.get(Calendar.HOUR_OF_DAY);	
				int minute = calendar.get(Calendar.MINUTE);
				Log.i("LHD", "hour of day =" + hour + "  minute=" + minute+" hour  ="+calendar.get(Calendar.HOUR));
				if ((hour ==7) && (minute==30)) {
					Log.i("LHD", "hour =" + hour + "  minute=" + minute);
					sendnotify(context,
							cityname + "  " + temp + "  " + weather, i);
				}
			}
		}
	}

}
