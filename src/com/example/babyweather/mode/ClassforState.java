package com.example.babyweather.mode;

public class ClassforState {
	//netState=0为默认不存在网络
	public static  int netState =0;

	public static int getNetState() {
		return netState;
	}

	public static void setNetState(int netState) {
		ClassforState.netState = netState;
	}


	
}
