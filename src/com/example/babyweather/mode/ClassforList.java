package com.example.babyweather.mode;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ClassforList {

	public static List<MainList> cityList = new ArrayList<MainList>();
	public static List<GuanxinPerson> personlist = new ArrayList<GuanxinPerson>();

	public static final List<MainList> getCityList() {

		return cityList;
	}

	public static List<GuanxinPerson> getPersonlist() {
		return personlist;
	}

	public static void setCityList(List<MainList> cityList) {
		ClassforList.cityList = cityList;
	}

	public static void setPersonlist(List<GuanxinPerson> personlist) {
		ClassforList.personlist = personlist;
	}
	
}
