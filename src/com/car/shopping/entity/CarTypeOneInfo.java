package com.car.shopping.entity;

import java.util.List;

public class CarTypeOneInfo{

	private String type_name;
	private String type_id;
	private List<CarNameInfo> brand_list;

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}

	public String getType_id() {
		return type_id;
	}

	public void setType_id(String type_id) {
		this.type_id = type_id;
	}

	public List<CarNameInfo> getBrand_list() {
		return brand_list;
	}

	public void setBrand_list(List<CarNameInfo> brand_list) {
		this.brand_list = brand_list;
	}
}
