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
		// 延时跳转方法1 使用Handler延时发送消息
		// new Handler(new Handler.Callback() {
		//
		// @Override
		// public boolean handleMessage(Message arg0) {
		// //处理接收到的消息的方法
		// startActivity(new
		// Intent(getApplicationContext(),MainActivity.class));
		// return false;
		// }
		// }).sendEmptyMessageDelayed(0, 3000);//延时3秒发送

		// 延时跳转方法2 使用Timer定时器
		Timer timer = new Timer();
		timer.schedule(new Task(), 1000);
	}

	class Task extends TimerTask {

		@Override
		public void run() {
			// 页面跳转
//			 如果是第一次进入 getWelcomeBoolean默认为false
//			if (Utility.getWelcomeBoolean(getBaseContext())) {
//				startActivity(new Intent(Welcom.this, WelcomGuide.class));
//				// 保存访问记录 已经不是第一访问了 将NODE_NAME设置为false
//				Utility.putWelcomBoolean(getBaseContext(), false);
//				Log.i("LHD", "第一次启动");
//			} else {
				startActivity(new Intent(Welcom.this, MainActivity.class));
//				Log.i("LHD", "不是第一次启动");
//			}
		}

	}
}
