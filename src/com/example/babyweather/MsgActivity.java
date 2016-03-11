package com.example.babyweather;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import receiver.AutoYujingReceiver;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;
import cn.sharesdk.tencent.qq.h;

import com.example.babyweather.MainActivity.MyBrodcast;
import com.example.babyweather.mode.ClassforList;
import com.example.babyweather.mode.GuanxinPerson;

import android.R.integer;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MsgActivity extends Activity implements OnClickListener {

	private Button msg_city;
	private EditText msg_name;
	private EditText msg_num;
	private Spinner msg_weather;
	private EditText msg_content;
	private TimePicker msg_time;
	private Button msg_save;

	// �����������
	private static final int DAY = 1000 * 60 * 60 * 24;// 24h
	private Date xinsetTime;
	private Date CurrentTime;
	private Date setTime;

	// person�������Ϣ
	private String name;
	private String num;
	private String content;
	private String city;
	private String weather;
	private String time;

	// person��list�е�λ��
	private int position = -1;
	private String[] yujingtianqi = { "��", "��", "��", "ѩ", "���" };

	private String address;
	private String address2;
	// �����ʱ����ʾ���ȿ�
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.msgactivity);

		msg_city = (Button) findViewById(R.id.msg_city);
		msg_name = (EditText) findViewById(R.id.msg_name);
		msg_num = (EditText) findViewById(R.id.msg_num);
		msg_weather = (Spinner) findViewById(R.id.msg_weather);
		msg_content = (EditText) findViewById(R.id.msg_content);
		msg_time = (TimePicker) findViewById(R.id.msg_time);
		msg_save = (Button) findViewById(R.id.msg_save);

		msg_save.setOnClickListener(this);
		msg_city.setOnClickListener(this);
		
		// �õ���ӳ��еķ���ֵ
		city = getIntent().getStringExtra("cityName");
		if (!TextUtils.isEmpty(city)) {
			msg_city.setText(city);
		}
		// ����ǵ����GuanxinActivity�����ģ��ͻ�ȡ��Ӧ��ֵ
		position = getIntent().getIntExtra("position", -1);
		if (position > -1) {
			name = getIntent().getStringExtra("name");
			if (!TextUtils.isEmpty(name)) {
				msg_name.setText(name);
			}
			num = getIntent().getStringExtra("num");
			if (!TextUtils.isEmpty(num)) {
				msg_num.setText(num);
			}
			content = getIntent().getStringExtra("content");
			if (!TextUtils.isEmpty(content)) {
				msg_content.setText(content);
			}

		}

		// �õ���ҪԤ��������
		ArrayAdapter<String> weatherAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, yujingtianqi);
		msg_weather.setAdapter(weatherAdapter);
		msg_weather.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				weather = parent.getItemAtPosition(position).toString();
				Toast.makeText(MsgActivity.this, "������ " + weather, Toast.LENGTH_SHORT).show();

				TextView tv = (TextView) view;
				tv.setTextColor(getResources().getColor(R.color.blue)); // ������ɫ
				tv.setTextSize(16.0f); // ���ô�С
				tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL); // ���þ���
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.msg_city:
			Intent intent = new Intent(MsgActivity.this, ChooseAreaActivity.class);
			intent.putExtra("MsgActivity", "MsgActivity");
			startActivity(intent);
			finish();
			break;
		case R.id.msg_save:

			String h = msg_time.getCurrentHour() + "";
			String m = msg_time.getCurrentMinute() + "";
			time = h + "ʱ" + m + "��";
			name = msg_name.getText().toString();
			num = msg_num.getText().toString();
			city = getIntent().getStringExtra("cityName");
			content = msg_content.getText().toString();
			if (checkTheInput(city, name, num, content)) {

				GuanxinPerson person = new GuanxinPerson(name, num, city, weather, content, time, true);
				Log.i("MSG", "�޸ĺ��person����ϢΪ" + person.getCity() + person.getName() + person.getTime());
				if (position == -1) {
					ClassforList.getPersonlist().add(person);
					// ��personlist��Ϊ�ַ����浽SharedPreferences��
					Utility.SaveGuanxinList(MsgActivity.this, ClassforList.getPersonlist());
					// ���Ů�����ڵĳ��е���ҳ����������б���,�����͹㲥֪ͨ���������UI
					Log.i("MSG", "person cityname" + person.getCity());
					queryWeatherCode(person.getCity());
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Intent intent2 = new Intent("MainActivityBrodcast");
					sendBroadcast(intent2);
					Log.i("MSG", "personlist==" + ClassforList.personlist.size());
				} else {
					// ����Ǳ༭����,��ֱ���滻��ԭ��λ�õ�Person����
					ClassforList.personlist.remove(position);
					ClassforList.personlist.add(position, person);
					Log.i("MSG", "���޸ĺ��Person�滻ԭ����person");
					// ��personlist��Ϊ�ַ����浽SharedPreferences��
					Utility.SaveGuanxinList(MsgActivity.this, ClassforList.getPersonlist());
				}
				Intent intent1 = new Intent(MsgActivity.this, GuanxinActivity.class);
				startActivity(intent1);

				finish();
			}
			break;

		default:
			break;
		}
	}

	/**
	 * ��ѯ����������Ӧ������
	 * 
	 * @param countyCode
	 */
	private void queryWeatherCode(String cityName) {
		try {
			String citynameString = URLEncoder.encode(cityName, "UTF-8");
			address = // �˴��Է���json��ʽ����ʾ��,����format=2,�Ը��ݳ�������Ϊ��,cityName��������
			"http://v.juhe.cn/weather/index?cityname=" + citynameString + "&key=f70eeb1f409350ace226078e20e22eb5";
			Log.i("MSG", "msg----queryWeatherCode" + address);

			address2 = "http://op.juhe.cn/onebox/weather/query?cityname=" + citynameString + "&key=5fc2b142029b1b0d371d5449a8b8927d";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, address2, new HttpCallbackListener() {

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				Toast.makeText(MsgActivity.this, "���ʧ�ܣ��������������Ƿ�����", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onFinish(String response, String response2) {
				// ������������ص�������Ϣ
				Utility.handleWeatherResponse(response, response2);
				Log.i("MSG", "msgactivity handleWeatherResponse----" + ClassforList.cityList.size());
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
					}
				});
			}
		});

	}

	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڱ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	private boolean checkTheInput(String city, String name, String num, String content) {
		if (TextUtils.isEmpty(city)) {
			Toast.makeText(MsgActivity.this, "��ѡ�����", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(MsgActivity.this, "������Ҫ�����˵�����", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!TextUtils.isEmpty(num)) {
			if (num.length() == 11) {
				Pattern p = Pattern.compile("[0-9]*");
				Matcher m = p.matcher(num);
				if (!m.matches()) {
					Toast.makeText(MsgActivity.this, "���ĵ��˵ĵ绰�����������", Toast.LENGTH_SHORT).show();
					return false;
				}
			} else {
				Toast.makeText(MsgActivity.this, "���ĵ��˵ĵ绰�����������", Toast.LENGTH_SHORT).show();
				msg_num.setFocusable(true);
				msg_num.setFocusableInTouchMode(true);
				msg_num.requestFocus();
				return false;
			}
		} else {
			Toast.makeText(MsgActivity.this, "��������ĵ��˵ĵ绰����", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(MsgActivity.this, "�������������", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(MsgActivity.this, GuanxinActivity.class);
		startActivity(intent);
		finish();
	}
}
