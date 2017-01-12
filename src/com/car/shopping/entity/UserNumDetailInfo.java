package com.car.shopping.entity;

import java.util.List;

public class UserNumDetailInfo {
	
	private List<UserNumInfo> data;
	private int total_num;
	private int page_limit;
	private int page_num;
	private int total_page;
	
	public List<UserNumInfo> getData() {
		return data;
	}
	public void setData(List<UserNumInfo> data) {
		this.data = data;
	}
	public int getTotal_num() {
		return total_num;
	}
	public void setTotal_num(int total_num) {
		this.total_num = total_num;
	}
	public int getPage_limit() {
		return page_limit;
	}
	public void setPage_limit(int page_limit) {
		this.page_limit = page_limit;
	}
	public int getPage_num() {
		return page_num;
	}
	public void setPage_num(int page_num) {
		this.page_num = page_num;
	}
	public int getTotal_page() {
		return total_page;
	}
	public void setTotal_page(int total_page) {
		this.total_page = total_page;
	}
	
}
