package com.example.babyweather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import receiver.AutoYujingReceiver;
import util.Utility;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.example.babyweather.mode.ClassforList;
import com.example.babyweather.mode.GuanxinAdapter;
import com.example.babyweather.mode.GuanxinPerson;
import com.example.babyweather.mode.MainList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class GuanxinActivity extends Activity implements OnClickListener, OnItemClickListener {
	private List<GuanxinPerson> mperson = new ArrayList<GuanxinPerson>();
	private GuanxinAdapter madapter;
	private SwipeMenuListView mlistView;

	private Button addguanxinperson;

	// 闹钟设置相关
		private static final int DAY = 1000 * 60 * 60 * 24;// 24h
		private Date xinsetTime;
		private Date CurrentTime;
		private Date setTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guanxinactivity);

		addguanxinperson = (Button) findViewById(R.id.add_guanxin_person);
		addguanxinperson.setOnClickListener(this);
		mlistView = (SwipeMenuListView) findViewById(R.id.guanxin_list);
		mlistView.setOnItemClickListener(this);
		// 从SharedPreferences中取出Personlist并设置到ClassforList全局变量中
		ClassforList.setPersonlist(Utility.getGuanxinPersons(GuanxinActivity.this));
		// 如果有数据就加载
		if (ClassforList.getPersonlist().size() > 0) {
			mperson.clear();
			for (int i = 0; i < ClassforList.getPersonlist().size(); i++) {
				mperson.add(ClassforList.getPersonlist().get(i));
				SetAlarm(i);
				Log.i("LHD", "设置闹钟"+i+ClassforList.getPersonlist().get(i).getCity()+
						ClassforList.getPersonlist().get(i).getName());
			}
		}

		madapter = new GuanxinAdapter(GuanxinActivity.this, R.layout.guanxinlistitem, mperson);
		mlistView.setAdapter(madapter);

		// listview左滑分享、刪除
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				// deleteItem.setIcon(R.drawable.ic_launcher);
				// set a title
				deleteItem.setTitle("delete");
				// set item title fontsize
				deleteItem.setTitleSize(18);
				// set item title font color
				deleteItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};

		// set creator
		mlistView.setMenuCreator(creator);

		// step 2. listener item click event
		mlistView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					// delete
					// delete(item);
					mperson.remove(position);
					ClassforList.personlist.remove(position);
					madapter.notifyDataSetChanged();
					// 在这里取消闹钟
					 CancelAlarm(position);
					break;
				}
				return false;
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_guanxin_person:
			Intent intent = new Intent(GuanxinActivity.this, MsgActivity.class);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}

	@Override
	public void onBackPressed() {
		// 将personlist变为字符串存到SharedPreferences中
		Utility.SaveGuanxinList(GuanxinActivity.this, ClassforList.getPersonlist());
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		GuanxinPerson person = mperson.get(position);
		Intent intent = new Intent(GuanxinActivity.this, MsgActivity.class);
		intent.putExtra("cityName", person.getCity());
		intent.putExtra("name", person.getName());
		intent.putExtra("num", person.getPhnum());
		intent.putExtra("weather", person.getWeather());
		intent.putExtra("content", person.getContent());
		intent.putExtra("time", person.getTime());
		intent.putExtra("position", position);
		Log.i("LHD", "要修改第" + position + "个数据");
		startActivity(intent);
		finish();
		Toast.makeText(GuanxinActivity.this, person.getName() + "编辑", Toast.LENGTH_LONG).show();
	}

	@SuppressLint("SimpleDateFormat")
	private void SetAlarm(int i) {
		Log.i("LHD", "设定预警广播1");
		GuanxinPerson person = ClassforList.getPersonlist().get(i);
		Intent intent = new Intent(GuanxinActivity.this, AutoYujingReceiver.class);
		intent.putExtra("Alarm", "这个闹铃是：：" + person.getCity() + person.getName());
		PendingIntent sender = PendingIntent.getBroadcast(GuanxinActivity.this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		Log.i("LHD", "设定预警广播2");
		Calendar calendar = Calendar.getInstance();
		
		String PersonTime = person.getTime();
		String personHour = PersonTime.substring(0, PersonTime.indexOf("时"));
		String personMinute = PersonTime.substring(PersonTime.indexOf("时") + 1, PersonTime.indexOf("分"));
		
		calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(personHour));
		calendar.set(Calendar.MINUTE, Integer.parseInt(personMinute));
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		CurrentTime = new Date(System.currentTimeMillis());
		Log.i("LHD", "当前时间是——" + f.format(CurrentTime));

		setTime = new Date(calendar.getTimeInMillis());
		Log.i("LHD", "定时时间是——" + f.format(setTime));

		// 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
		if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
			// Toast.makeText(MainActivity.this, "设置的时间小于当前时间",
			// Toast.LENGTH_SHORT).show();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			xinsetTime = new Date(calendar.getTimeInMillis());
			Log.i("LHD", "新的定时时间是——" + f.format(xinsetTime));
		}

		// 计算现在时间到设定时间的时间差
		long time = calendar.getTimeInMillis() - System.currentTimeMillis();

		long firstTime = SystemClock.elapsedRealtime(); // 开机之后到现在的运行时间(包括睡眠时间)
		firstTime += time;

		Date shijiancha = new Date(firstTime);
		SimpleDateFormat f2 = new SimpleDateFormat("HH-mm-ss");
		Log.i("LHD", "时间差是——" + f2.format(shijiancha));
		Log.i("LHD", "time ==== " + time + ", selectTime ===== " + calendar.getTimeInMillis() + ", systemTime ==== " + System.currentTimeMillis()
				+ ", firstTime === " + firstTime);
		am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, DAY, sender);
		Log.i("LHD", "设定预警广播3");

	}

	private void CancelAlarm(int i) {
		Intent intent = new Intent(GuanxinActivity.this, AutoYujingReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(GuanxinActivity.this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
		Log.i("MSG", "取消闹钟"+i);
	}

}
