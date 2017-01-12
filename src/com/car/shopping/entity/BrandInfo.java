package com.car.shopping.entity;

import java.util.List;

public class BrandInfo{
	private String brand_id;
	private String zimu;
	private List<CarModelInfo> series_list;
	private String brand_name;
	private String brand_logo;

	public String getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(String brand_id) {
		this.brand_id = brand_id;
	}

	public String getZimu() {
		return zimu;
	}

	public void setZimu(String zimu) {
		this.zimu = zimu;
	}

	public List<CarModelInfo> getSeries_list() {
		return series_list;
	}

	public void setSeries_list(List<CarModelInfo> series_list) {
		this.series_list = series_list;
	}

	public String getBrand_name() {
		return brand_name;
	}

	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}

	public String getBrand_logo() {
		return brand_logo;
	}

	public void setBrand_logo(String brand_logo) {
		this.brand_logo = brand_logo;
	}

}
