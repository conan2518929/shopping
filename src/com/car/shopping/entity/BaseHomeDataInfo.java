package com.car.shopping.entity;

import java.util.List;

public class BaseHomeDataInfo {
	private NewVersionInfo version;
	private List<RecommendShopsInfo> recommend_shops;
	private List<LunBoTuInfo> lunbotu;
	private SystemInfo system_info;
	private List<CityListInfo> city_list;
	
	public NewVersionInfo getVersion() {
		return version;
	}

	public void setVersion(NewVersionInfo version) {
		this.version = version;
	}

	public List<RecommendShopsInfo> getRecommend_shops() {
		return recommend_shops;
	}

	public void setRecommend_shops(List<RecommendShopsInfo> recommend_shops) {
		this.recommend_shops = recommend_shops;
	}

	public List<LunBoTuInfo> getLunbotu() {
		return lunbotu;
	}

	public void setLunbotu(List<LunBoTuInfo> lunbotu) {
		this.lunbotu = lunbotu;
	}

	public SystemInfo getSystem_info() {
		return system_info;
	}

	public void setSystem_info(SystemInfo system_info) {
		this.system_info = system_info;
	}

	public List<CityListInfo> getCity_list() {
		return city_list;
	}

	public void setCity_list(List<CityListInfo> city_list) {
		this.city_list = city_list;
	}

}
