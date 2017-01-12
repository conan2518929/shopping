package com.car.shopping.entity;

import java.util.List;

public class BaseShopDetail {
	private List<TagsInfo> tags;
	private String id;
	private String im_username;
	private String address;
	private String image_url;
	private String description;
	private String saved;
	private String jianying;
	private String am_series;
	private String shop_name;
	private List<BrandsInfo> brands;
	private List<TelsNumInfo> tels;
	private List<ImageUrlInfo> lunbotu;

	private String visit_num;
	private String save_num;
//	private String visitors;
	private String service_tel;
	private String visitors_str;
	private String has_video;
	private String video_url;
	
	public String getVisitors_str() {
		return visitors_str;
	}

	public void setVisitors_str(String visitors_str) {
		this.visitors_str = visitors_str;
	}

	public String getHas_video() {
		return has_video;
	}

	public void setHas_video(String has_video) {
		this.has_video = has_video;
	}

	public String getVisit_num() {
		return visit_num;
	}

	public void setVisit_num(String visit_num) {
		this.visit_num = visit_num;
	}

	public String getSave_num() {
		return save_num;
	}

	public void setSave_num(String save_num) {
		this.save_num = save_num;
	}

//	public String getVisitors() {
//		return visitors;
//	}

//	public void setVisitors(String visitors) {
//		this.visitors = visitors;
//	}
//
	public String getService_tel() {
		return service_tel;
	}

	public void setService_tel(String service_tel) {
		this.service_tel = service_tel;
	}

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}

	public List<TagsInfo> getTags() {
		return tags;
	}

	public void setTags(List<TagsInfo> tags) {
		this.tags = tags;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSaved() {
		return saved;
	}

	public void setSaved(String saved) {
		this.saved = saved;
	}

	public String getJianying() {
		return jianying;
	}

	public void setJianying(String jianying) {
		this.jianying = jianying;
	}

	public String getAm_series() {
		return am_series;
	}

	public void setAm_series(String am_series) {
		this.am_series = am_series;
	}

	public String getShop_name() {
		return shop_name;
	}

	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}

	public List<BrandsInfo> getBrands() {
		return brands;
	}

	public void setBrands(List<BrandsInfo> brands) {
		this.brands = brands;
	}

	public List<TelsNumInfo> getTels() {
		return tels;
	}

	public void setTels(List<TelsNumInfo> tels) {
		this.tels = tels;
	}

	public List<ImageUrlInfo> getLunbotu() {
		return lunbotu;
	}

	public void setLunbotu(List<ImageUrlInfo> lunbotu) {
		this.lunbotu = lunbotu;
	}

}
