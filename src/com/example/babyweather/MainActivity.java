package com.example.babyweather;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kll.dod.rtk.AdManager;
import kll.dod.rtk.os.OffersManager;
import receiver.AutoSendMsgReceiver;
import receiver.AutoUpdateReceiver;
import receiver.AutoYujingReceiver;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.NetUtils;
import util.RefreshAsyncTask;
import util.Utility;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.example.babyweather.mode.ClassforList;
import com.example.babyweather.mode.DrawerAdapter;
import com.example.babyweather.mode.DrawerMenu;
import com.example.babyweather.mode.MainList;
import com.example.babyweather.mode.MainListAdapter;
import com.example.babyweather.mode.MyDialog;

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener {
	private static final int DAY = 1000 * 60 * 60 * 24;// 24h
	private Date xinsetTime;
	private Date CurrentTime;
	private Date setTime;
	// ������
	private List<MainList> mcitylist = new ArrayList<MainList>();
	private MainListAdapter madapter;
	private SwipeMenuListView mlistView;
	// �����
	private DrawerLayout drawerLayout = null;
	private List<DrawerMenu> mDrawerMenus = new ArrayList<DrawerMenu>();
	private ListView drawerlistView;
	private DrawerAdapter mDrawerAdapter;

	// ������
	private static String cityName;
	// ���ݳ������õ���������ַ
	private static String address;// ����״��
	private static String address2;// ��������
	private Button drawerButton;
	private Button dingweiButton;
	private Button addcityButton;
	// ����㲥������
	MyBrodcast brodcastReceiver = null;
	NetReceiver netReceiver = new NetReceiver();
	IntentFilter netFilter = new IntentFilter();
	// ��λ���
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	// ��λ��ʱ����ʾ���ȿ�
	private ProgressDialog progressDialog;
	// ˢ�µ�ʱ����ʾ���ȿ�
	private ProgressDialog progressDialog2;
	// ��λ���ı��س���
	public static String localCityname;
	// ˢ�³����б�ťbutton
	private Button freshButton;
	// �˳���ʱ����ʾ�ĶԻ���
	private MyDialog myDialog;
	// ������򿪹ر�״ֵ̬
	private int CEBIAN = 0;// ����0Ϊ�ر�

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		MyApplication.getInstance().addActivity(this);
		Log.i("LHD", "-----onCreat MainActivity-----");
		// ��ʼ��
		initKongjians();

		/**
		 * ����������صĴ���
		 */
		// Ĭ��������̨�Զ����·���
		// startupdate();
		// Ĭ�Ͽ��������ĵ��˷��Ͷ��ŷ���
		// startSendMsg();
		// ����Ƿ������Զ�Ԥ������
		if (Utility.getsetBoolean(MainActivity.this, Utility.YJ_NAME, true)) {
			Startyujing();
			Log.i("LHD", "����Ԥ������");
		} else {
			// �رպ�̨Ԥ������ ���˳�Ӧ��ʱӦ�ر�����
			Stopyujing();
			Log.i("LHD", "�ر�Ԥ������");
		}

		// �½����ʱ��õ��ϴ��˳���������ݣ���������
		if (ClassforList.getCityList().size() == 0 && Utility.getCityList(MainActivity.this).size() > 0) {
			Log.i("LHD", "��ȡcitylist��json=====");
			ClassforList.setCityList(Utility.getCityList(MainActivity.this));
		}
		// �½����ʱ������ϴ�û�б������ݾ��Զ���λ���س���
		if (ClassforList.getCityList().size() == 0 && Utility.getCityList(MainActivity.this).size() == 0) {
			Log.i("LHD", "111111111111111111111111");
			showProgressDialog("���ڶ�λ���س���...");
			initLocation();
			mLocationClient.start();
		}

		// listview�󻬷����h��
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "share" item
				SwipeMenuItem shareItem = new SwipeMenuItem(getApplicationContext());
				shareItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
				shareItem.setWidth(dp2px(90));
				shareItem.setBackground(R.drawable.share);
				// add to menu
				menu.addMenuItem(shareItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				deleteItem.setWidth(dp2px(90));
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};

		// set creator
		mlistView.setMenuCreator(creator);

		// step 2. listener item click event
		mlistView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
				MainList city = mcitylist.get(position);
				switch (index) {
				case 0:
					// share
					share(city);
					break;
				case 1:
					// delete
					delete(position);
					break;
				}
				return false;
			}
		});
		netFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(netReceiver, netFilter);
		showWeather();

	}

	private void startSendMsg() {
		Log.i("MSG", "�����Զ����͹��Ķ��ŷ���");
		Intent intent = new Intent(MainActivity.this, AutoSendMsgReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MINUTE, 1);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, sender);
	}

	private void startupdate() {
		Intent intent = new Intent(MainActivity.this, AutoUpdateReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.HOUR_OF_DAY, 1);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 60 * 1000, sender);

	}

	private void Startyujing() {
		Log.i("LHD", "�趨Ԥ���㲥1");
		Intent intent = new Intent(MainActivity.this, AutoYujingReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		Log.i("LHD", "�趨Ԥ���㲥2");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		calendar.set(Calendar.MINUTE, 30);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		CurrentTime = new Date(System.currentTimeMillis());
		Log.i("LHD", "��ǰʱ���ǡ���" + f.format(CurrentTime));

		setTime = new Date(calendar.getTimeInMillis());
		Log.i("LHD", "��ʱʱ���ǡ���" + f.format(setTime));

		// �����ǰʱ��������õ�ʱ�䣬��ô�ʹӵڶ�����趨ʱ�俪ʼ
		if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
			// Toast.makeText(MainActivity.this, "���õ�ʱ��С�ڵ�ǰʱ��",
			// Toast.LENGTH_SHORT).show();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			xinsetTime = new Date(calendar.getTimeInMillis());
			Log.i("LHD", "�µĶ�ʱʱ���ǡ���" + f.format(xinsetTime));
		}

		// ��������ʱ�䵽�趨ʱ���ʱ���
		long time = calendar.getTimeInMillis() - System.currentTimeMillis();

		long firstTime = SystemClock.elapsedRealtime(); // ����֮�����ڵ�����ʱ��(����˯��ʱ��)
		firstTime += time;

		Date shijiancha = new Date(firstTime);
		SimpleDateFormat f2 = new SimpleDateFormat("HH-mm-ss");
		Log.i("LHD", "ʱ����ǡ���" + f2.format(shijiancha));
		Log.i("LHD", "time ==== " + time + ", selectTime ===== " + calendar.getTimeInMillis() + ", systemTime ==== " + System.currentTimeMillis()
				+ ", firstTime === " + firstTime);
		am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, DAY, sender);
		Log.i("LHD", "�趨Ԥ���㲥3");

	}

	private void Stopyujing() {
		Intent intent = new Intent(MainActivity.this, AutoYujingReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
	}

	private void StopUpdata() {
		Intent intent = new Intent(MainActivity.this, AutoUpdateReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(sender);
	}

	private void StopSendMsg() {
		Log.i("LHD", "�ر��Զ����͹��Ķ��ŷ���");
		Intent intent = new Intent(MainActivity.this, AutoSendMsgReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(sender);
	}

	protected void delete(final int position) {
		mcitylist.remove(position);
		madapter.notifyDataSetChanged();
		ClassforList.cityList.remove(position);
		Toast.makeText(MainActivity.this, "delete", Toast.LENGTH_SHORT).show();
	}

	private void refreshweather(ProgressDialog progressDialog, List<MainList> list, MainListAdapter madapter) {
		RefreshAsyncTask freshasyncTask = new RefreshAsyncTask(MainActivity.this, progressDialog, list, madapter);
		freshasyncTask.execute();

	}

	private void initKongjians() {
		// ��λ���
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		// ����ǽ����ʼ��
		OffersManager.getInstance(this).onAppLaunch();
		// ���׹���ʼ��
		AdManager.getInstance(this).init("c6cbf7707bbe30f2", "5e6204870f203c75", false);
		// �����Ƿ���֪ͨ����ʾ���������ʾ��Ĭ��Ϊtrue����ʶ����������Ϊfalse��رա�
		AdManager.setIsDownloadTipsDisplayOnNotification(true);
		// ��ȡ�Ƿ���֪ͨ����ʾ���ؽ��ȵ�ֵ��
		AdManager.isDownloadTipsDisplayOnNotification();
		// ���ð�װ��ɺ��Ƿ���֪ͨ����ʾ�Ѱ�װ�ɹ���֪ͨ��Ĭ��Ϊtrue����ʶ����������Ϊfalse��رա�
		AdManager.setIsInstallationSuccessTipsDisplayOnNotification(true);
		// ��ȡ��װ��ɺ��Ƿ���֪ͨ����ʾ�Ѱ�װ�ɹ���֪ͨ��ֵ��
		AdManager.isInstallationSuccessTipsDisplayOnNotification();

		// �㲥��������ʼ��
		brodcastReceiver = new MyBrodcast();
		IntentFilter filter = new IntentFilter("MainActivityBrodcast");
		// ע��㲥������
		registerReceiver(brodcastReceiver, filter);
		// ���ؼ���ʼ��
		drawerButton = (Button) findViewById(R.id.drawer_btn);
		dingweiButton = (Button) findViewById(R.id.dingwei_btn);
		addcityButton = (Button) findViewById(R.id.add_city);
		freshButton = (Button) findViewById(R.id.mainfresh_btn);
		drawerButton.setOnClickListener(this);
		dingweiButton.setOnClickListener(this);
		addcityButton.setOnClickListener(this);
		freshButton.setOnClickListener(this);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mlistView = (SwipeMenuListView) findViewById(R.id.main_list);
		drawerlistView = (ListView) findViewById(R.id.left_drawer);
		/**
		 * ������ҳ��listview�Ĵ���
		 */
		// ��ҳ����������ʼ����listview����������

		madapter = new MainListAdapter(MainActivity.this, R.layout.city_list_item, mcitylist);
		mlistView.setAdapter(madapter);
		mlistView.setOnItemClickListener(this);
		/**
		 * ������ҳ��������ʼ��
		 */
		initDrawerMenus();
		mDrawerAdapter = new DrawerAdapter(MainActivity.this, R.layout.drawerlayout, mDrawerMenus);
		drawerlistView.setAdapter(mDrawerAdapter);
		drawerlistView.setOnItemClickListener(this);

	}

	private void initDrawerMenus() {

		DrawerMenu guanxinDrawerMenu = new DrawerMenu(R.drawable.guanxinicon, "���ĵ���");
		DrawerMenu setDrawerMenu = new DrawerMenu(R.drawable.set, "����");
		DrawerMenu moreDrawerMenu = new DrawerMenu(R.drawable.list, "�����Ƽ�");

		mDrawerMenus.add(guanxinDrawerMenu);
		mDrawerMenus.add(setDrawerMenu);
		mDrawerMenus.add(moreDrawerMenu);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.drawer_btn:
			if (CEBIAN == 0) {
				drawerLayout.openDrawer(Gravity.START);
				CEBIAN = 1;
			} else if (CEBIAN == 1) {
				drawerLayout.closeDrawer(Gravity.START);
				CEBIAN = 0;
			}
			break;
		case R.id.dingwei_btn:
			showProgressDialog("���ڶ�λ���س���...");
			initLocation();
			mLocationClient.start();
			break;
		case R.id.add_city:
			Intent intent = new Intent(MainActivity.this, ChooseAreaActivity.class);
			intent.putExtra("MainActivity", "MainActivity");
			// ChooseActivity����������1
			startActivityForResult(intent, 1);
			Toast.makeText(this, "start intent", Toast.LENGTH_LONG).show();
			break;
		case R.id.mainfresh_btn:
			refreshweather(progressDialog2, mcitylist, madapter);
			Log.i("LHD", "222");
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
			// �ۺ���������Ԥ���ӿ� ���
			address2 = "http://op.juhe.cn/onebox/weather/query?cityname=" + citynameString + "&key=5fc2b142029b1b0d371d5449a8b8927d";
			Log.i("LHD", "address2----->" + address2);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpUtil.sendHttpRequest(address, address2, new HttpCallbackListener() {
			@Override
			public void onFinish(String response, String response2) {
				// ������������ص�������Ϣ
				Utility.handleWeatherResponse(response, response2);
				runOnUiThread(new Runnable() {
					public void run() {
						showWeather();
					}
				});
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				// Toast.makeText(MainActivity.this, "���ʧ�ܣ��������������Ƿ�����",
				// Toast.LENGTH_LONG).show();
			}

		});

	}

	/**
	 * ��ClassforCityList.java�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather() {
		mcitylist.clear();// ����������¼���
		for (MainList city : ClassforList.getCityList()) {
			mcitylist.add(city);
		}
		Log.i("LHD", "" + "Main--showWeather-----mcitylist.size() == " + mcitylist.size());
		Log.i("LHD", "" + "Main--showWeather-----ClassforListsize() == " + ClassforList.getCityList().size());
		madapter.notifyDataSetChanged();
		Log.i("LHD", "notifyDataSetChanged");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.left_drawer) {
			switch (position) {
			case 0:
				Intent intent2 = new Intent(MainActivity.this, GuanxinActivity.class);
				startActivity(intent2);
				Toast.makeText(this, "start intent", Toast.LENGTH_LONG).show();
				break;
			case 1:
				Intent intent1 = new Intent(MainActivity.this, SetActivity.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// SetActivity����������3
				startActivityForResult(intent1, 3);
				Toast.makeText(this, "start intent", Toast.LENGTH_LONG).show();
				break;
			case 2:
				OffersManager.getInstance(this).showOffersWall();
				break;

			default:
				break;
			}
		}

		if (parent.getId() == R.id.main_list) {
			Intent intent = new Intent(MainActivity.this, CityActivity.class);
			intent.putExtra("position", position);
			// CityActivity����������2
			startActivityForResult(intent, 2);
		}

	}

	private void showShare(MainList city) {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// �ر�sso��Ȩ
		oks.disableSSOWhenAuthorize();

		// ����ʱNotification��ͼ������� 2.5.9�Ժ�İ汾�����ô˷���
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
		oks.setTitle(getString(R.string.hello_world));
		// titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
		oks.setTitleUrl("http://openbox.mobilem.360.cn/index/d/sid/3208423");
		// text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		oks.setText(city.getCitynameString() + "  " + city.getCityweather() + "  " + city.getTemp() + city.getDressing_index());
		// imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		// oks.setImagePath("/sdcard/test.jpg");// ȷ��SDcard������ڴ���ͼƬ
		// oks.setImagePath("drawable/ic_launcher.jpg");

		// url����΢�ţ��������Ѻ�����Ȧ����ʹ��
		oks.setUrl("http://openbox.mobilem.360.cn/index/d/sid/3208423");
		// comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
		oks.setComment("���Թ�������");

		// site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
		oks.setSite(getString(R.string.app_name));
		// siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
		oks.setSiteUrl("www.baidu.com");

		// ��������GUI
		oks.show(this);
	}

	private void share(MainList city) {
		showShare(city);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:// Choosectivity����������1
				// �������ӳ��д�province���ؾͼ��ؾ�����
			if (resultCode == RESULT_OK) {
				String returnData = data.getStringExtra("province");
				if (!TextUtils.isEmpty(returnData)) {
					if (ClassforList.getCityList().size() > 0) {
						mcitylist.clear();
						for (MainList city : ClassforList.getCityList()) {
							mcitylist.add(city);
						}
					}
				}
				// ����Ǵ�ѡ����е�ҳ����ת�������͵õ�������
				cityName = data.getStringExtra("cityName");
				if (!TextUtils.isEmpty(cityName)) {
					// �г�������ȥ��ѯ����
					Log.i("LHD", "cityName = " + cityName);
					queryWeatherCode(cityName);
				}
			}
			break;
		case 2:// CityActivity����������2
				// ����Ǵ�CityActivity���ؾ�
			if (resultCode == RESULT_OK) {
				showWeather();
			}
			break;
		case 3:// SetActivity����������3
				// ����Ǵ�SetActivity���ؾ�
			if (resultCode == RESULT_OK) {
				Startyujing();
			}
			break;
		default:
			break;
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}

	// ����㲥�����������ڽ�����ӹ��ĵ���֮�󷢹����Ĺ㲥����������ҳ����ӹ��ĵ��˵ĳ���
	public class MyBrodcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("MSG", "MyBrodcast--->onReceive");
			showWeather();

		}

	}

	public class NetReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				boolean isConnected = NetUtils.isNetworkConnected(context);
				if (isConnected) {
					Toast.makeText(context, "�Ѿ���������", Toast.LENGTH_LONG).show();
					Log.i("MSG", "�Ѿ���������");
					refreshweather(progressDialog2, mcitylist, madapter);
				} else {
					Toast.makeText(context, "�Ѿ��Ͽ�����", Toast.LENGTH_LONG).show();
					Utility.SaveCityList(MainActivity.this, mcitylist);
					Log.i("MSG", "�Ѿ��Ͽ�����");
				}
			}
		}

	}

	// ��λ���
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// ��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
		option.setCoorType("bd09ll");// ��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ
		int span = 1000;
		option.setScanSpan(span);// ��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
		option.setIsNeedAddress(true);// ��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
		option.setOpenGps(true);// ��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
		option.setLocationNotify(true);// ��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
		option.setIsNeedLocationDescribe(true);// ��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
		option.setIsNeedLocationPoiList(true);// ��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
		option.setIgnoreKillProcess(false);// ��ѡ��Ĭ��false����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ��ɱ��
		option.SetIgnoreCacheException(false);// ��ѡ��Ĭ��false�������Ƿ��ռ�CRASH��Ϣ��Ĭ���ռ�
		option.setEnableSimulateGps(false);// ��ѡ��Ĭ��false�������Ƿ���Ҫ����gps��������Ĭ����Ҫ
		mLocationClient.setLocOption(option);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS��λ���
				Toast.makeText(MainActivity.this, "gps��λ�ɹ�", Toast.LENGTH_SHORT).show();
				Log.i("LHD", "gps��λ�ɹ�");

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// ���綨λ���
				Toast.makeText(MainActivity.this, "���綨λ�ɹ�", Toast.LENGTH_SHORT).show();
				Log.i("LHD", "���綨λ�ɹ�");

			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// ���߶�λ���
				Toast.makeText(MainActivity.this, "���߶�λ�ɹ�", Toast.LENGTH_SHORT).show();
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				Toast.makeText(MainActivity.this, "��������綨λʧ��", Toast.LENGTH_SHORT).show();
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				Toast.makeText(MainActivity.this, "���粻ͬ���¶�λʧ�ܣ����������Ƿ�ͨ��", Toast.LENGTH_SHORT).show();

			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				Toast.makeText(MainActivity.this, "�޷���ȡ��Ч��λ���ݵ��¶�λʧ�ܣ�һ���������ֻ���ԭ�򣬴��ڷ���ģʽ��һ���������ֽ�����������������ֻ�", Toast.LENGTH_SHORT).show();
			}

			// λ�����廯��Ϣ
			int i = location.getCity().indexOf('��');
			cityName = location.getCity().substring(0, i);
			Log.i("LHD", location.getLocationDescribe() + "cityname--->" + cityName + "countyname==" + location.getCountry());
			localCityname = cityName;
			queryWeatherCode(cityName);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			closeProgressDialog();
			mLocationClient.stop();
		}
	}

	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog(String string) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(string);
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

	@Override
	public void onBackPressed() {
		OffersManager.getInstance(this).onAppExit();
		Toast.makeText(MainActivity.this, "�ҵĶԻ���", Toast.LENGTH_SHORT).show();
		myDialog = new MyDialog(MainActivity.this, "��ܰ��ʾ", new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_zuixiaohua:
					Toast.makeText(MainActivity.this, "��С��", Toast.LENGTH_SHORT).show();
					moveTaskToBack(true);
					myDialog.dismiss();
					break;
				case R.id.btn_confir:
					Toast.makeText(MainActivity.this, "ȷ��", Toast.LENGTH_SHORT).show();
					ClassforList.setCityList(mcitylist);
					Utility.SaveCityList(MainActivity.this, ClassforList.getCityList());
					// ȡ������
					Intent intent = new Intent(MainActivity.this, AutoYujingReceiver.class);
					PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
					AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					am.cancel(sender);
					for (MainList c : ClassforList.getCityList()) {
						Log.i("LHD", "   ���������   " + c.getCitynameString());
					}
					MyApplication.getInstance().exit();
					myDialog.dismiss();
					break;
				default:
					break;
				}

			}
		});
		myDialog.show();

	}

	@Override
	protected void onDestroy() {
		OffersManager.getInstance(MainActivity.this).onAppExit();
		unregisterReceiver(brodcastReceiver);
		Stopyujing();
		// StopUpdata();
		StopSendMsg();
		super.onDestroy();
	}

}
