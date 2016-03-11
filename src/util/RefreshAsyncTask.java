package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import com.example.babyweather.mode.ClassforList;
import com.example.babyweather.mode.MainList;
import com.example.babyweather.mode.MainListAdapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.NoCopySpan.Concrete;
import android.util.Log;

public class RefreshAsyncTask extends AsyncTask<Void, Integer, Boolean> {

	private ProgressDialog mProgressDialog;
	private List<MainList> mcitylist;
	private MainListAdapter madapter;
	private Context mcontext;
	private String address;
	private String address2;
	static StringBuilder response;
	static StringBuilder response2;

	public RefreshAsyncTask(Context context, ProgressDialog progressDialog, List<MainList> citylist, MainListAdapter adapter) {
		this.mProgressDialog = progressDialog;
		this.mcitylist = citylist;
		this.madapter = adapter;
		this.mcontext = context;
	}

	@Override
	protected void onPreExecute() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(mcontext);
			mProgressDialog.setMessage("正在刷新天气信息...");
			mProgressDialog.setCanceledOnTouchOutside(false);
		}
		mProgressDialog.show();
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		ClassforList.cityList.clear();
		for (MainList c : mcitylist) {
			Log.i("LHD", "doInBackground.....  " + c.getCitynameString());

			try {
				String citynameString = URLEncoder.encode(c.getCitynameString(), "UTF-8");
				address = // 此处以返回json格式数据示例,所以format=2,以根据城市名称为例,cityName传入中文
				"http://v.juhe.cn/weather/index?cityname=" + citynameString + "&key=f70eeb1f409350ace226078e20e22eb5";

				address2 = "http://op.juhe.cn/onebox/weather/query?cityname=" + citynameString + "&key=5fc2b142029b1b0d371d5449a8b8927d";
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("LHD", address + "\n" + address2);

			HttpURLConnection connection = null;
			// Log.i("LHD", "1");
			URL url;
			URL url2;
			try {
				if (!TextUtils.isEmpty(address)) {
					url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					// Log.i("LHD", "HttpUtil-->response1" +
					// response.toString());
				}
				if (!TextUtils.isEmpty(address2)) {
					url2 = new URL(address2);
					connection = (HttpURLConnection) url2.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in2 = connection.getInputStream();
					BufferedReader reader2 = new BufferedReader(new InputStreamReader(in2));
					response2 = new StringBuilder();
					String line2;
					while ((line2 = reader2.readLine()) != null) {
						response2.append(line2);
					}
					// Log.i("LHD", "HttpUtil-->response2"+
					// response2.toString());
				}

			} catch (ProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}

			Utility.handleWeatherResponse(response.toString(), response2.toString());

		}

		return null;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		madapter.notifyDataSetChanged();
		mProgressDialog.dismiss();
		super.onPostExecute(result);
	}
}
