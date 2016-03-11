package receiver;

import com.example.babyweather.GuanxinActivity;
import com.example.babyweather.MainActivity;
import com.example.babyweather.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class sendMsgReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		if (getResultCode() == Activity.RESULT_OK) {
			Log.i("LHD", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			Toast.makeText(context, "发送成功", Toast.LENGTH_LONG).show();
			sendnotify(context, "关心短信发送成功", 1);
		} else {
			Toast.makeText(context, "发送失败", Toast.LENGTH_LONG).show();
			sendnotify(context, "关心短信发送失败", 2);
		}
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
		NotificationManager manager = (NotificationManager) context.getSystemService(ns);
		// 定义通知栏展现的内容信息
		int icon = R.drawable.guanxinicon;
		String tickerText = "关心天气 更关心你";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		long[] vibrates = { 0, 1000, 1000, 1000 };
		notification.vibrate = vibrates;

		// 点击进入主页面
		Intent intent = new Intent(context, GuanxinActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		notification.setLatestEventInfo(context, tickerText, content, pIntent);
		manager.notify(i, notification);
	}

}
