package com.example.babyweather.mode;

public class DrawerMenu {
	private int picid;
	private String name;

	public DrawerMenu(int picid,String name) {
		this.picid = picid;
		this.name = name;
	}
	
	public int getPicid() {
		return picid;
	}

	public void setPicid(int picid) {
		this.picid = picid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
