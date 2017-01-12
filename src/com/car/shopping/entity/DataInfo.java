package com.car.shopping.entity;

import java.util.List;

public class DataInfo {

	private List<Recommend_RecordInfo> recommend_record;
	private String point;
	private String my_code;
	private String choujiang_rule;//抽奖规则
	private String point_rule;//积分规则
	private String point_choujiang;
	private String point_recommend;
	private List<LunBoTuInfo> point_lunbotu;
	private List<ListInfo> item_list;
	private int has_checked_in;
	
	private List<ExchangeRecordInfo>exchange_record;
	private List<ChoujiangRecordInfo>choujiang_record;
	
	public List<ExchangeRecordInfo> getExchange_record() {
		return exchange_record;
	}

	public void setExchange_record(List<ExchangeRecordInfo> exchange_record) {
		this.exchange_record = exchange_record;
	}

	public List<ChoujiangRecordInfo> getChoujiang_record() {
		return choujiang_record;
	}

	public void setChoujiang_record(List<ChoujiangRecordInfo> choujiang_record) {
		this.choujiang_record = choujiang_record;
	}

	public List<LunBoTuInfo> getPoint_lunbotu() {
		return point_lunbotu;
	}

	public void setPoint_lunbotu(List<LunBoTuInfo> point_lunbotu) {
		this.point_lunbotu = point_lunbotu;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public List<Recommend_RecordInfo> getRecommend_record() {
		return recommend_record;
	}

	public void setRecommend_record(List<Recommend_RecordInfo> recommend_record) {
		this.recommend_record = recommend_record;
	}

	public List<ListInfo> getItem_list() {
		return item_list;
	}

	public void setItem_list(List<ListInfo> item_list) {
		this.item_list = item_list;
	}

	public int getHas_checked_in() {
		return has_checked_in;
	}

	public void setHas_checked_in(int has_checked_in) {
		this.has_checked_in = has_checked_in;
	}

	public String getMy_code() {
		return my_code;
	}

	public void setMy_code(String my_code) {
		this.my_code = my_code;
	}

	public String getChoujiang_rule() {
		return choujiang_rule;
	}

	public void setChoujiang_rule(String choujiang_rule) {
		this.choujiang_rule = choujiang_rule;
	}

	public String getPoint_rule() {
		return point_rule;
	}

	public void setPoint_rule(String point_rule) {
		this.point_rule = point_rule;
	}

	public String getPoint_choujiang() {
		return point_choujiang;
	}

	public void setPoint_choujiang(String point_choujiang) {
		this.point_choujiang = point_choujiang;
	}

	public String getPoint_recommend() {
		return point_recommend;
	}

	public void setPoint_recommend(String point_recommend) {
		this.point_recommend = point_recommend;
	}

}
