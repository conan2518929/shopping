package com.car.shopping.entity;

import java.util.List;

public class BaseUserInfo{
	private UserInfo user_info;
	private List<LogsInfo>logs;
	public UserInfo getUser_info() {
		return user_info;
	}
	public void setUser_info(UserInfo user_info) {
		this.user_info = user_info;
	}
	public List<LogsInfo> getLogs() {
		return logs;
	}
	public void setLogs(List<LogsInfo> logs) {
		this.logs = logs;
	}
}
