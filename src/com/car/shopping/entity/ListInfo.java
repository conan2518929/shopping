package com.car.shopping.entity;

import java.io.Serializable;

public class ListInfo implements Serializable{
	private String item_id;
	private String item_name;
	private String item_pic;
	private String point;
	private String enabled;
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	public String getItem_pic() {
		return item_pic;
	}
	public void setItem_pic(String item_pic) {
		this.item_pic = item_pic;
	}
	public String getPoint() {
		return point;
	}
	public void setPoint(String point) {
		this.point = point;
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	
}
