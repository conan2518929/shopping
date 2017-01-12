package com.car.shopping.entity;

import java.io.Serializable;
import java.util.List;

public class BaseBrandInfo implements Serializable{
	private List<BrandInfo> data;

	public List<BrandInfo> getData() {
		return data;
	}

	public void setData(List<BrandInfo> data) {
		this.data = data;
	}

}
