package com.car.shopping.entity;

import java.util.List;

public class CarNameInfo{

	private String brand_name;
	private String brand_id;
	private List<CarModelInfo> series_list;

	public String getBrand_name() {
		return brand_name;
	}

	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}

	public String getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(String brand_id) {
		this.brand_id = brand_id;
	}

	public List<CarModelInfo> getSeries_list() {
		return series_list;
	}

	public void setSeries_list(List<CarModelInfo> series_list) {
		this.series_list = series_list;
	}

}
