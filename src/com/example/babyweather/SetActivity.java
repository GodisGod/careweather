package com.example.babyweather;

import util.Utility;

import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class SetActivity extends Activity {
	private ToggleButton yujingButton;
	private ToggleButton guanxinButton;
	private Button backset;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setactivity);
		MyApplication.getInstance().addActivity(this);
		Log.i("LHD", "setactivity-----onCreate");
		yujingButton = (ToggleButton) findViewById(R.id.yujing_togbtn);
		guanxinButton = (ToggleButton) findViewById(R.id.guanxin_togbtn);
		backset = (Button) findViewById(R.id.back_set);
		backset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
				
			}
		});
		
		if (Utility.getsetBoolean(SetActivity.this, Utility.GX_NAME, false)) {
			guanxinButton.setToggleOn();
		} else {
			guanxinButton.setToggleOff();
		}
		if (Utility.getsetBoolean(SetActivity.this, Utility.YJ_NAME, false)) {
			yujingButton.setToggleOn();
		} else {
			yujingButton.setToggleOff();
		}
		yujingButton.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				if (on) {
//					// 启动后台预警服务
//					Intent intent = new Intent(SetActivity.this,AutoYujingService.class);
//					startService(intent);
//					Toast.makeText(SetActivity.this, "预警功能开",Toast.LENGTH_SHORT).show();
					Utility.putsetBoolean(SetActivity.this, Utility.YJ_NAME,true);
				} else {
//					// 启动后台预警服务
//					Intent intent = new Intent(SetActivity.this,
//							AutoYujingService.class);
//					stopService(intent);
//					Toast.makeText(SetActivity.this, "预警功能关",
//							Toast.LENGTH_SHORT).show();
					Utility.putsetBoolean(SetActivity.this, Utility.YJ_NAME,
							false);
				}
			}
		});
		guanxinButton.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				if (on) {
					Toast.makeText(SetActivity.this, "关心功能开",
							Toast.LENGTH_SHORT).show();
					Utility.putsetBoolean(SetActivity.this, Utility.GX_NAME,
							true);
				} else {
					Toast.makeText(SetActivity.this, "关心功能关",
							Toast.LENGTH_SHORT).show();
					Utility.putsetBoolean(SetActivity.this, Utility.GX_NAME,
							false);
				}
			}
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i("LHD", "set---onRestart");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("LHD", "onDestroy");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i("LHD", "onNewIntent");
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
