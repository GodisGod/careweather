package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.text.TextUtils;
import android.util.Log;

public class HttpUtil {
	static StringBuilder response;
	static StringBuilder response2;

	public static void sendHttpRequest(final String address, final String address2, final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
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
//						Log.i("LHD", "HttpUtil-->response1" + response.toString());
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

					if (listener != null) {
						// 回调onFinish()方法
						listener.onFinish(response.toString(), response2.toString());
					}

				} catch (Exception e) {
					if (listener != null) {
						// 回调onError()方法
						listener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}

			}
		}).start();
		;
	}

}
