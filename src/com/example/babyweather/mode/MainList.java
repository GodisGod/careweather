package com.example.babyweather.mode;

public class MainList {
	private String citynameString;
	private String temp;
	private String cityweather;
	private String dressing_index;
	private String publishtime;
	private String time;
	
	private String AQI;
	private String quality;
	
	private String mingtianweek;
	private String mingtianweather;
	private String mingtiantemp;

	private String houtianweek;
	private String houtianweather;
	private String houtiantemp;

	private String dahoutianweek;
	private String dahoutianweather;
	private String dahoutiantemp;

	
	public MainList(String name, String dressing_index, String tempereture,
			String weatherdesp, String time, String publishTime,
			String mingtianweek, String mingtianweather, String mingtiantemp,
			String houtianweek, String houtianweather, String houtiantemp,
			String dahoutianweek, String dahoutianweather, String dahoutiantemp,String AQI,String quality) {
		this.citynameString = name;
		this.temp = tempereture;
		this.cityweather = weatherdesp;
		this.dressing_index = dressing_index;
		this.publishtime = publishTime;// 今天的天气更新时间
		this.time = time;// 今天的日期

		this.mingtianweek = mingtianweek;
		this.mingtianweather = mingtianweather;
		this.mingtiantemp = mingtiantemp;

		this.houtianweek = houtianweek;
		this.houtianweather = houtianweather;
		this.houtiantemp = houtiantemp;

		this.dahoutianweek = dahoutianweek;
		this.dahoutianweather = dahoutianweather;
		this.dahoutiantemp = dahoutiantemp;

		this.AQI = AQI;
		this.quality = quality;
	}

	public String getAQI() {
		return AQI;
	}

	public String getQuality() {
		return quality;
	}

	public String getCitynameString() {
		return citynameString;
	}

	public String getTime() {
		return time;
	}

	public  String getMingtianweek() {
		return mingtianweek;
	}

	public String getMingtianweather() {
		return mingtianweather;
	}

	public  String getMingtiantemp() {
		return mingtiantemp;
	}

	public  String getHoutianweek() {
		return houtianweek;
	}

	public  String getHoutianweather() {
		return houtianweather;
	}

	public  String getHoutiantemp() {
		return houtiantemp;
	}

	public  String getDahoutianweek() {
		return dahoutianweek;
	}

	public  String getDahoutianweather() {
		return dahoutianweather;
	}

	public  String getDahoutiantemp() {
		return dahoutiantemp;
	}

	public String getTemp() {
		return temp;
	}

	public String getCityweather() {
		return cityweather;
	}

	public String getDressing_index() {
		return dressing_index;
	}

	public String getPublishtime() {
		return publishtime;
	}

}
