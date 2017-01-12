package com.car.shopping.entity;

import java.util.List;

public class FindByTypeOneInfo{
	
	private int id;
	private int img;
	private String name;
	private List<FindByTypeTwoInfo>list;
	
	public FindByTypeOneInfo() {
		super();
	}
	
	public FindByTypeOneInfo(int img,String name,List<FindByTypeTwoInfo>list) {
		super();
		this.list = list;
		this.name = name;
		this.img = img;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getImg() {
		return img;
	}
	public void setImg(int img) {
		this.img = img;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<FindByTypeTwoInfo> getList() {
		return list;
	}
	public void setList(List<FindByTypeTwoInfo> list) {
		this.list = list;
	}
	
	
}
