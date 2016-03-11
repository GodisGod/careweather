package com.example.babyweather.mode;

import java.util.List;

import com.example.babyweather.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerAdapter extends ArrayAdapter<DrawerMenu>{
	private int resourceId;
	private List<DrawerMenu>list;
	private Context context;
	public DrawerAdapter(Context context, int resource, List<DrawerMenu> list) {
		super(context, resource, list);
		this.context = context;
		this.resourceId = resource;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}


	@Override
	public DrawerMenu getItem(int position) {
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
//		Log.i("LHD", "getDrawerView");
	    if (convertView == null) {
	    	convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);

	    	new ViewHolder(convertView);
		}
				
		ViewHolder holder = (ViewHolder) convertView.getTag();
		DrawerMenu mycurrentMenu = list.get(position);		
		holder.pic.setBackgroundResource(mycurrentMenu.getPicid());
//		Log.i("LHD", "setImageResource"+mycurrentMenu.getPicid());
		holder.textView.setText(mycurrentMenu.getName());
		return convertView;
	}


	class ViewHolder {
		Button pic;
		TextView textView;
		
		public ViewHolder(View view) {
			pic = (Button) view.findViewById(R.id.drawer_pic);
			textView = (TextView) view.findViewById(R.id.drawer_text);
			view.setTag(this);
		}
	}

}
