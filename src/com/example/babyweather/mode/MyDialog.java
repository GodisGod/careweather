package com.example.babyweather.mode;

import com.example.babyweather.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class MyDialog extends Dialog {
	Context context;
	View.OnClickListener onClickListener;
	String title;

	public MyDialog(Context context, String title,
			View.OnClickListener onClickListener) {
		super(context, R.style.MyDialog2);
		this.context = context;
		this.title = title;
		this.onClickListener = onClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.my_dialog);
		// 实例化控件
		Button btn_confir = (Button) findViewById(R.id.btn_confir);
		Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
		Button btn_zuiButton = (Button) findViewById(R.id.btn_zuixiaohua);
		TextView textv_title = (TextView) findViewById(R.id.textv_title);
		// 设置标题
		textv_title.setText(title);

		// 点击取消关闭dialog
		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();				
				MyDialog.this.dismiss();
			}
		});
		// 给最小化键设置监听
		btn_zuiButton.setOnClickListener(onClickListener);
		// 给确认键设置监听
//		btn_confir.setOnClickListener(tuichu);
		btn_confir.setOnClickListener(onClickListener);
	}
}
