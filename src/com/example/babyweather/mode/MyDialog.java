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
		// ʵ�����ؼ�
		Button btn_confir = (Button) findViewById(R.id.btn_confir);
		Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
		Button btn_zuiButton = (Button) findViewById(R.id.btn_zuixiaohua);
		TextView textv_title = (TextView) findViewById(R.id.textv_title);
		// ���ñ���
		textv_title.setText(title);

		// ���ȡ���ر�dialog
		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(context, "ȡ��", Toast.LENGTH_SHORT).show();				
				MyDialog.this.dismiss();
			}
		});
		// ����С�������ü���
		btn_zuiButton.setOnClickListener(onClickListener);
		// ��ȷ�ϼ����ü���
//		btn_confir.setOnClickListener(tuichu);
		btn_confir.setOnClickListener(onClickListener);
	}
}
