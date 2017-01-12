package com.car.shopping.entity;


public class UserInfo{
	private String id;
	private String name;
	private String mobile_register;
	private String mobile_in_use;
	private String address;
	private String im_username;
	private String im_password;
	
	private String my_code;
	
	
	public String getMy_code() {
		return my_code;
	}
	public void setMy_code(String my_code) {
		this.my_code = my_code;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile_register() {
		return mobile_register;
	}
	public void setMobile_register(String mobile_register) {
		this.mobile_register = mobile_register;
	}
	public String getMobile_in_use() {
		return mobile_in_use;
	}
	public void setMobile_in_use(String mobile_in_use) {
		this.mobile_in_use = mobile_in_use;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIm_username() {
		return im_username;
	}
	public void setIm_username(String im_username) {
		this.im_username = im_username;
	}
	public String getIm_password() {
		return im_password;
	}
	public void setIm_password(String im_password) {
		this.im_password = im_password;
	}
}
