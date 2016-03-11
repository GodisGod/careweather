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



public class DelDialog extends Dialog {
	Context context;
	View.OnClickListener onClickListener;
	public DelDialog(Context context, View.OnClickListener onClickListener) {
		super(context, R.style.MyDialog2);
		this.context = context;
		this.onClickListener = onClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.my_dialog);
		// 实例化控件
		Button del_queren = (Button) findViewById(R.id.btn_quereng);
		Button del_quxiao = (Button) findViewById(R.id.btn_quxiao);
	


		// 点击取消关闭dialog
		del_quxiao.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();				
				DelDialog.this.dismiss();
			}
		});

		del_queren.setOnClickListener(onClickListener);
	}
}
