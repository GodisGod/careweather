package com.example.babyweather;

import java.util.LinkedList;
import java.util.List;

import com.thinkland.sdk.android.JuheSDKInitializer;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application {
	
	private List<Activity> mList = new LinkedList<Activity>();
	private static MyApplication instance;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		JuheSDKInitializer.initialize(getApplicationContext());
	}

	public MyApplication() {
	}

	public synchronized static MyApplication getInstance() {
		if (null == instance) {
			instance = new MyApplication();
		}
		return instance;
	}

	// add Activity
	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	public void exit() {
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}

}
