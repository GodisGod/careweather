package com.example.babyweather;

import java.util.Timer;
import java.util.TimerTask;

import util.Utility;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class Welcom extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		MyApplication.getInstance().addActivity(this);
		// ��ʱ��ת����1 ʹ��Handler��ʱ������Ϣ
		// new Handler(new Handler.Callback() {
		//
		// @Override
		// public boolean handleMessage(Message arg0) {
		// //������յ�����Ϣ�ķ���
		// startActivity(new
		// Intent(getApplicationContext(),MainActivity.class));
		// return false;
		// }
		// }).sendEmptyMessageDelayed(0, 3000);//��ʱ3�뷢��

		// ��ʱ��ת����2 ʹ��Timer��ʱ��
		Timer timer = new Timer();
		timer.schedule(new Task(), 1000);
	}

	class Task extends TimerTask {

		@Override
		public void run() {
			// ҳ����ת
//			 ����ǵ�һ�ν��� getWelcomeBooleanĬ��Ϊfalse
//			if (Utility.getWelcomeBoolean(getBaseContext())) {
//				startActivity(new Intent(Welcom.this, WelcomGuide.class));
//				// ������ʼ�¼ �Ѿ����ǵ�һ������ ��NODE_NAME����Ϊfalse
//				Utility.putWelcomBoolean(getBaseContext(), false);
//				Log.i("LHD", "��һ������");
//			} else {
				startActivity(new Intent(Welcom.this, MainActivity.class));
//				Log.i("LHD", "���ǵ�һ������");
//			}
		}

	}
}
