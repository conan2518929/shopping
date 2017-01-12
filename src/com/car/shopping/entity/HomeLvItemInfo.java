package com.car.shopping.entity;

import java.util.List;

public class HomeLvItemInfo{
	
	private List<BrandsInfo>brands;
	private List<TelsInfo>tels;
	private String im_username;
	private String shop_id;
	private String address;
	private String image_url;
	private String shop_name;
	private String pay_amount;
	private String status;
	private String create_time;
	private String order_no;
	
	
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
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getShop_name() {
		return shop_name;
	}
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}
	public String getIm_username() {
		return im_username;
	}
	public void setIm_username(String im_username) {
		this.im_username = im_username;
	}
	public String getShop_id() {
		return shop_id;
	}
	public void setShop_id(String shop_id) {
		this.shop_id = shop_id;
	}
	public String getPay_amount() {
		return pay_amount;
	}
	public void setPay_amount(String pay_amount) {
		this.pay_amount = pay_amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getOrder_no() {
		return order_no;
	}
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
}
