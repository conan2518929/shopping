package com.car.shopping.entity;

import java.util.List;

public class RecommendShopsInfo {
	
	private String id;
	private List<BrandsInfo> brands;
	private List<TelsInfo> tels;
	private String im_username;
	private String address;
	private String image_url;
	private String shop_name;
	
	private String has_video;
	private String has_tag;
	private String has_activity;
	private String activity;
	
	public String getHas_tag() {
		return has_tag;
	}

	public void setHas_tag(String has_tag) {
		this.has_tag = has_tag;
	}

	public String getHas_activity() {
		return has_activity;
	}

	public void setHas_activity(String has_activity) {
		this.has_activity = has_activity;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getHas_video() {
		return has_video;
	}

	public void setHas_video(String has_video) {
		this.has_video = has_video;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<BrandsInfo> getBrands() {
		return brands;
	}

	public void setBrands(List<BrandsInfo> brands) {
		this.brands = brands;
	}

	public List<TelsInfo> getTels() {
		return tels;
	}

	public void setTels(List<TelsInfo> tels) {
		this.tels = tels;
	}

	public String getIm_username() {
		return im_username;
	}

	public void setIm_username(String im_username) {
		this.im_username = im_username;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getShop_name() {
		return shop_name;
	}

	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}

}
