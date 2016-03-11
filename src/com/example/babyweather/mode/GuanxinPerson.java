package com.example.babyweather.mode;

public class GuanxinPerson {
	private String name ;
	private String city ;
	private String weather ;
	private String time ;
	private String phnum;
	private String content;
	private boolean checked;
	
	public GuanxinPerson(String name,String num,String city,String weather,String content,String time,boolean checked) {
		this.name = name;
		this.city = city;
		this.weather = weather;
		this.content = content;
		this.time = time;
		this.phnum = num;
		this.checked = checked;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPhnum() {
		return phnum;
	}

	public void setPhnum(String phnum) {
		this.phnum = phnum;
	}

	public String getName() {
		return name;
	}
	public String getCity() {
		return city;
	}
	public String getWeather() {
		return weather;
	}
	public String getTime() {
		return time;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
