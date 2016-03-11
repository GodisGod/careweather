package com.example.babyweather.mode;

import java.util.List;

import com.example.babyweather.R;
import com.example.babyweather.mode.MainListAdapter.ViewHolder;
import com.zcw.togglebutton.ToggleButton;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GuanxinAdapter extends ArrayAdapter<GuanxinPerson> {

	private Context context;
	private int resourceId;
	private List<GuanxinPerson> persons;

	public GuanxinAdapter(Context context, int resource,
			List<GuanxinPerson> list) {
		super(context, resource, list);
		this.context = context;
		this.resourceId = resource;
		this.persons = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return persons.size();
	}

	@Override
	public GuanxinPerson getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return super.getItemId(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(resourceId,
					null);
			new ViewHolder(convertView);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		GuanxinPerson person = getItem(position);
		holder.name.setText(person.getName());
		holder.num.setText(person.getPhnum());
		holder.city.setText(person.getCity());
		holder.weather.setText(person.getWeather());
		holder.time.setText(person.getTime());
		holder.content.setText(person.getContent());
		return convertView;
	}

	class ViewHolder {
		TextView name;
		TextView num;
		TextView city;
		TextView weather;
		TextView time;
		TextView content;

		public ViewHolder(View view) {
			name = (TextView) view.findViewById(R.id.guanxin_name);
			num = (TextView) view.findViewById(R.id.guanxin_num);
			city = (TextView) view.findViewById(R.id.guanxin_chengshi);
			weather = (TextView) view.findViewById(R.id.guanxin_tianqi);
			time = (TextView) view.findViewById(R.id.guanxin_shijian);
			content = (TextView) view.findViewById(R.id.guanxin_content);
			view.setTag(this);
		}
	}

}
