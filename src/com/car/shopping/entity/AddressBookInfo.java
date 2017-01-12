package com.car.shopping.entity;

import java.util.List;

public class AddressBookInfo{
	
	private String id;
	private List<BrandsInfo>brands;
	private List<TelsInfo>tels;
	private String call_time;
	private String im_username;
	private String address;
	private String image_url;
	private String shop_name;
	private String py;
	private String record_id;
	/**
	 * ºóÌí¼ÓµÄ
	 * */
	private String alias;
	private String has_activity;
	private String has_tag;
	private String unify_name;
	private String has_video;

	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getHas_activity() {
		return has_activity;
	}
	public void setHas_activity(String has_activity) {
		this.has_activity = has_activity;
	}
	public String getHas_tag() {
		return has_tag;
	}
	public void setHas_tag(String has_tag) {
		this.has_tag = has_tag;
	}
	public String getUnify_name() {
		return unify_name;
	}
	public void setUnify_name(String unify_name) {
		this.unify_name = unify_name;
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
	public String getCall_time() {
		return call_time;
	}
	public void setCall_time(String call_time) {
		this.call_time = call_time;
	}
	public String getIm_username() {
		return im_username;
	}
	public void setIm_username(String im_username) {
		this.im_username = im_username;
	}
	public List<TelsInfo> getTels() {
		return tels;
	}
	public void setTels(List<TelsInfo> tels) {
		this.tels = tels;
	}
	public List<BrandsInfo> getBrands() {
		return brands;
	}
	public void setBrands(List<BrandsInfo> brands) {
		this.brands = brands;
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
	public String getPy() {
		return py;
	}
	public void setPy(String py) {
		this.py = py;
	}
	public String getRecord_id() {
		return record_id;
	}
	public void setRecord_id(String record_id) {
		this.record_id = record_id;
	}
}
