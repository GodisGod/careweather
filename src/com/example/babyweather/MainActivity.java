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
	// 主界面
	private List<MainList> mcitylist = new ArrayList<MainList>();
	private MainListAdapter madapter;
	private SwipeMenuListView mlistView;
	// 侧边栏
	private DrawerLayout drawerLayout = null;
	private List<DrawerMenu> mDrawerMenus = new ArrayList<DrawerMenu>();
	private ListView drawerlistView;
	private DrawerAdapter mDrawerAdapter;

	// 城市名
	private static String cityName;
	// 根据城市名得到的天气地址
	private static String address;// 天气状况
	private static String address2;// 空气质量
	private Button drawerButton;
	private Button dingweiButton;
	private Button addcityButton;
	// 主活动广播接收器
	MyBrodcast brodcastReceiver = null;
	NetReceiver netReceiver = new NetReceiver();
	IntentFilter netFilter = new IntentFilter();
	// 定位相关
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	// 定位的时候显示进度框
	private ProgressDialog progressDialog;
	// 刷新的时候显示进度框
	private ProgressDialog progressDialog2;
	// 定位到的本地城市
	public static String localCityname;
	// 刷新城市列表按钮button
	private Button freshButton;
	// 退出的时候显示的对话框
	private MyDialog myDialog;
	// 侧边栏打开关闭状态值
	private int CEBIAN = 0;// 等于0为关闭

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		MyApplication.getInstance().addActivity(this);
		Log.i("LHD", "-----onCreat MainActivity-----");
		// 初始化
		initKongjians();

		/**
		 * 启动服务相关的代码
		 */
		// 默认启动后台自动更新服务
		// startupdate();
		// 默认开启给关心的人发送短信服务
		// startSendMsg();
		// 检测是否开启了自动预警服务
		if (Utility.getsetBoolean(MainActivity.this, Utility.YJ_NAME, true)) {
			Startyujing();
			Log.i("LHD", "开启预警服务");
		} else {
			// 关闭后台预警服务 在退出应用时应关闭闹钟
			Stopyujing();
			Log.i("LHD", "关闭预警服务");
		}

		// 新建活动的时候得到上次退出保存的数据，处理数据
		if (ClassforList.getCityList().size() == 0 && Utility.getCityList(MainActivity.this).size() > 0) {
			Log.i("LHD", "读取citylist的json=====");
			ClassforList.setCityList(Utility.getCityList(MainActivity.this));
		}
		// 新建活动的时候如果上次没有保存数据就自动定位本地城市
		if (ClassforList.getCityList().size() == 0 && Utility.getCityList(MainActivity.this).size() == 0) {
			Log.i("LHD", "111111111111111111111111");
			showProgressDialog("正在定位本地城市...");
			initLocation();
			mLocationClient.start();
		}

		// listview左滑分享、刪除
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
		Log.i("MSG", "开启自动发送关心短信服务");
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
		Log.i("LHD", "设定预警广播1");
		Intent intent = new Intent(MainActivity.this, AutoYujingReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		Log.i("LHD", "设定预警广播2");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		calendar.set(Calendar.MINUTE, 30);
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
		Log.i("LHD", "关闭自动发送关心短信服务");
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
		// 定位相关
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		// 积分墙广告初始化
		OffersManager.getInstance(this).onAppLaunch();
		// 有米广告初始化
		AdManager.getInstance(this).init("c6cbf7707bbe30f2", "5e6204870f203c75", false);
		// 设置是否在通知栏显示下载相关提示。默认为true，标识开启；设置为false则关闭。
		AdManager.setIsDownloadTipsDisplayOnNotification(true);
		// 获取是否在通知栏显示下载进度的值。
		AdManager.isDownloadTipsDisplayOnNotification();
		// 设置安装完成后是否在通知栏显示已安装成功的通知。默认为true，标识开启；设置为false则关闭。
		AdManager.setIsInstallationSuccessTipsDisplayOnNotification(true);
		// 获取安装完成后是否在通知栏显示已安装成功的通知的值。
		AdManager.isInstallationSuccessTipsDisplayOnNotification();

		// 广播接收器初始化
		brodcastReceiver = new MyBrodcast();
		IntentFilter filter = new IntentFilter("MainActivityBrodcast");
		// 注册广播接收器
		registerReceiver(brodcastReceiver, filter);
		// 各控件初始化
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
		 * 关于主页面listview的代码
		 */
		// 主页面适配器初始化，listview加载适配器

		madapter = new MainListAdapter(MainActivity.this, R.layout.city_list_item, mcitylist);
		mlistView.setAdapter(madapter);
		mlistView.setOnItemClickListener(this);
		/**
		 * 关于主页面侧边栏初始化
		 */
		initDrawerMenus();
		mDrawerAdapter = new DrawerAdapter(MainActivity.this, R.layout.drawerlayout, mDrawerMenus);
		drawerlistView.setAdapter(mDrawerAdapter);
		drawerlistView.setOnItemClickListener(this);

	}

	private void initDrawerMenus() {

		DrawerMenu guanxinDrawerMenu = new DrawerMenu(R.drawable.guanxinicon, "关心的人");
		DrawerMenu setDrawerMenu = new DrawerMenu(R.drawable.set, "设置");
		DrawerMenu moreDrawerMenu = new DrawerMenu(R.drawable.list, "精彩推荐");

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
			showProgressDialog("正在定位本地城市...");
			initLocation();
			mLocationClient.start();
			break;
		case R.id.add_city:
			Intent intent = new Intent(MainActivity.this, ChooseAreaActivity.class);
			intent.putExtra("MainActivity", "MainActivity");
			// ChooseActivity的请求码是1
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
	 * 查询城市名所对应的天气
	 * 
	 * @param countyCode
	 */

	private void queryWeatherCode(String cityName) {
		try {
			String citynameString = URLEncoder.encode(cityName, "UTF-8");
			address = // 此处以返回json格式数据示例,所以format=2,以根据城市名称为例,cityName传入中文
			"http://v.juhe.cn/weather/index?cityname=" + citynameString + "&key=f70eeb1f409350ace226078e20e22eb5";
			// 聚合数据天气预报接口 免费
			address2 = "http://op.juhe.cn/onebox/weather/query?cityname=" + citynameString + "&key=5fc2b142029b1b0d371d5449a8b8927d";
			Log.i("LHD", "address2----->" + address2);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpUtil.sendHttpRequest(address, address2, new HttpCallbackListener() {
			@Override
			public void onFinish(String response, String response2) {
				// 处理服务器返回的天气信息
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
				// Toast.makeText(MainActivity.this, "添加失败，请检查您的网络是否连接",
				// Toast.LENGTH_LONG).show();
			}

		});

	}

	/**
	 * 从ClassforCityList.java文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather() {
		mcitylist.clear();// 先清空再重新加载
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
				// SetActivity的请求码是3
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
			// CityActivity的请求码是2
			startActivityForResult(intent, 2);
		}

	}

	private void showShare(MainList city) {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.hello_world));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://openbox.mobilem.360.cn/index/d/sid/3208423");
		// text是分享文本，所有平台都需要这个字段
		oks.setText(city.getCitynameString() + "  " + city.getCityweather() + "  " + city.getTemp() + city.getDressing_index());
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		// oks.setImagePath("drawable/ic_launcher.jpg");

		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://openbox.mobilem.360.cn/index/d/sid/3208423");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("来自关心天气");

		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("www.baidu.com");

		// 启动分享GUI
		oks.show(this);
	}

	private void share(MainList city) {
		showShare(city);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:// Choosectivity的请求码是1
				// 如果是添加城市从province返回就加载旧数据
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
				// 如果是从选择城市的页面跳转过来，就得到城市名
				cityName = data.getStringExtra("cityName");
				if (!TextUtils.isEmpty(cityName)) {
					// 有城市名就去查询天气
					Log.i("LHD", "cityName = " + cityName);
					queryWeatherCode(cityName);
				}
			}
			break;
		case 2:// CityActivity的请求码是2
				// 如果是从CityActivity返回就
			if (resultCode == RESULT_OK) {
				showWeather();
			}
			break;
		case 3:// SetActivity的请求码是3
				// 如果是从SetActivity返回就
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

	// 主活动广播接收器，用于接受添加关心的人之后发过来的广播，来更新主页面添加关心的人的城市
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
					Toast.makeText(context, "已经连接网络", Toast.LENGTH_LONG).show();
					Log.i("MSG", "已经连接网络");
					refreshweather(progressDialog2, mcitylist, madapter);
				} else {
					Toast.makeText(context, "已经断开网络", Toast.LENGTH_LONG).show();
					Utility.SaveCityList(MainActivity.this, mcitylist);
					Log.i("MSG", "已经断开网络");
				}
			}
		}

	}

	// 定位相关
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
				Toast.makeText(MainActivity.this, "gps定位成功", Toast.LENGTH_SHORT).show();
				Log.i("LHD", "gps定位成功");

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				Toast.makeText(MainActivity.this, "网络定位成功", Toast.LENGTH_SHORT).show();
				Log.i("LHD", "网络定位成功");

			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				Toast.makeText(MainActivity.this, "离线定位成功", Toast.LENGTH_SHORT).show();
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				Toast.makeText(MainActivity.this, "服务端网络定位失败", Toast.LENGTH_SHORT).show();
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				Toast.makeText(MainActivity.this, "网络不同导致定位失败，请检查网络是否通畅", Toast.LENGTH_SHORT).show();

			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				Toast.makeText(MainActivity.this, "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机", Toast.LENGTH_SHORT).show();
			}

			// 位置语义化信息
			int i = location.getCity().indexOf('市');
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
	 * 显示进度对话框
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
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		OffersManager.getInstance(this).onAppExit();
		Toast.makeText(MainActivity.this, "我的对话框", Toast.LENGTH_SHORT).show();
		myDialog = new MyDialog(MainActivity.this, "温馨提示", new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_zuixiaohua:
					Toast.makeText(MainActivity.this, "最小化", Toast.LENGTH_SHORT).show();
					moveTaskToBack(true);
					myDialog.dismiss();
					break;
				case R.id.btn_confir:
					Toast.makeText(MainActivity.this, "确定", Toast.LENGTH_SHORT).show();
					ClassforList.setCityList(mcitylist);
					Utility.SaveCityList(MainActivity.this, ClassforList.getCityList());
					// 取消闹钟
					Intent intent = new Intent(MainActivity.this, AutoYujingReceiver.class);
					PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
					AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					am.cancel(sender);
					for (MainList c : ClassforList.getCityList()) {
						Log.i("LHD", "   保存的数据   " + c.getCitynameString());
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
