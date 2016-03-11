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
			Toast.makeText(context, "���ͳɹ�", Toast.LENGTH_LONG).show();
			sendnotify(context, "���Ķ��ŷ��ͳɹ�", 1);
		} else {
			Toast.makeText(context, "����ʧ��", Toast.LENGTH_LONG).show();
			sendnotify(context, "���Ķ��ŷ���ʧ��", 2);
		}
	}

	/**
	 * 
	 * @param context
	 *            ������
	 * @param content
	 *            Ԥ������
	 * @param i
	 *            ֪ͨid
	 */
	@SuppressWarnings("deprecation")
	private void sendnotify(Context context, String content, int i) {
		Log.i("LHD", "sendnotify");
		// ����NotificationManager
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager manager = (NotificationManager) context.getSystemService(ns);
		// ����֪ͨ��չ�ֵ�������Ϣ
		int icon = R.drawable.guanxinicon;
		String tickerText = "�������� ��������";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		long[] vibrates = { 0, 1000, 1000, 1000 };
		notification.vibrate = vibrates;

		// ���������ҳ��
		Intent intent = new Intent(context, GuanxinActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		notification.setLatestEventInfo(context, tickerText, content, pIntent);
		manager.notify(i, notification);
	}

}
