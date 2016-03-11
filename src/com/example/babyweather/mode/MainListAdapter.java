package com.example.babyweather.mode;

import java.util.List;

import com.example.babyweather.R;

import android.R.color;
import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainListAdapter extends ArrayAdapter<MainList> {

	private int resourceId;
	private List<MainList> list;
	private Context context;

	public MainListAdapter(Context context, int resource, List<MainList> list) {
		super(context, resource, list);
		this.resourceId = resource;
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public MainList getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(resourceId,
					null);
			// istview每行的高度是由 inflater填充布局中高度最大的那个控件来决定的。
			// 所以下面这一行代码是无效的
			// convertView.setLayoutParams(new
			// LayoutParams(LayoutParams.MATCH_PARENT,60));
			new ViewHolder(convertView);
		}

		ViewHolder holder = (ViewHolder) convertView.getTag();
		MainList mycurrentList = getItem(position);
		holder.city_name.setText(mycurrentList.getCitynameString());
		holder.city_temp.setText(mycurrentList.getTemp());
		holder.city_desp.setText(mycurrentList.getCityweather());
		// weatherpic(convertView, mycurrentList.getCityweather());
		setWeatherTempColor(holder, mycurrentList.getTemp());
		return convertView;
	}

	private void setWeatherTempColor(ViewHolder holder, String temp) {
		int positon = temp.indexOf('℃');
		String diwenString = (String) temp.subSequence(0, positon);
		int diwen = Integer.parseInt(diwenString);	//取出低温值
		if (diwen < 0) {
			holder.city_temp.setTextColor(Color.BLUE);
		} else if (diwen < 10) {
			holder.city_temp.setTextColor(Color.GRAY);
		} else if (diwen < 20) {
			holder.city_temp.setTextColor(Color.WHITE);
		} else if (diwen < 30) {
			holder.city_temp.setTextColor(Color.YELLOW);
		}

	}

	/**
	 * 根据不同的天气设置Item的背景图片
	 * 
	 * @param view
	 * @param weatherState
	 */
	private void weatherpic(View view, String weatherState) {
		if ("晴".equals(weatherState)) {
			view.setBackgroundResource(R.drawable.qing);
		} else if ("多云".equals(weatherState)) {
			view.setBackgroundResource(R.drawable.duoyun);
		} else if ("小雨".equals(weatherState)) {
			view.setBackgroundResource(R.drawable.weimei_de_yu);
		} else if (weatherState.contains("风")) {
			view.setBackgroundResource(R.drawable.storm);
		} else if (weatherState.contains("雷")) {
			view.setBackgroundResource(R.drawable.leiyu);
		} else if ("多云转晴".equals(weatherState)) {
			view.setBackgroundResource(R.drawable.duoyuzhuanqing);
		} else if ("晴转多云".equals(weatherState)) {
			view.setBackgroundResource(R.drawable.qingzhuanduoyun);
		} else if (weatherState.contains("霾")) {
			view.setBackgroundResource(R.drawable.wumai);
		} else if ("阵雨".equals(weatherState)) {
			view.setBackgroundResource(R.drawable.zhenyu);
		} else if (weatherState.contains("转")) {
			view.setBackgroundResource(R.drawable.duoyuzhuanqing);
		}
	}

	class ViewHolder {
		TextView city_name;
		TextView city_temp;
		TextView city_desp;

		public ViewHolder(View view) {
			city_name = (TextView) view.findViewById(R.id.text_city);
			city_temp = (TextView) view.findViewById(R.id.text_temp1);
			city_desp = (TextView) view.findViewById(R.id.text_weather);
			view.setTag(this);
		}
	}

}
