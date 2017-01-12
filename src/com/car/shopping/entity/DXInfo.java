package com.car.shopping.entity;

import java.util.List;

public class DXInfo{
	private String part_type_img;
	private String part_type_name;
	private String part_type_id;
	private List<PartListInfo> part_list;

	public String getPart_type_img() {
		return part_type_img;
	}

	public void setPart_type_img(String part_type_img) {
		this.part_type_img = part_type_img;
	}

	public String getPart_type_name() {
		return part_type_name;
	}

	public void setPart_type_name(String part_type_name) {
		this.part_type_name = part_type_name;
	}

	public String getPart_type_id() {
		return part_type_id;
	}

	public void setPart_type_id(String part_type_id) {
		this.part_type_id = part_type_id;
	}

	public List<PartListInfo> getPart_list() {
		return part_list;
	}

	public void setPart_list(List<PartListInfo> part_list) {
		this.part_list = part_list;
	}

}
