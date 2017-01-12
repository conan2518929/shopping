package com.car.shopping.entity;

public class ExchangeRecordInfo {
	private String item_name;//商品名称
	private String item_pic;//商品图片
	private String point_cost;//消耗积分数
	private String record_time;//兑换时间
	
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
	public String getPoint_cost() {
		return point_cost;
	}
	public void setPoint_cost(String point_cost) {
		this.point_cost = point_cost;
	}
	public String getRecord_time() {
		return record_time;
	}
	public void setRecord_time(String record_time) {
		this.record_time = record_time;
	}
	
}
