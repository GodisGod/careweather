package util;

public interface HttpCallbackListener {
	void onFinish(String response, String response2);
	void onError(Exception e);
}
