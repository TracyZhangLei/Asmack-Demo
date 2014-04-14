package com.example.smackdemo.model;

public class User {
	
	private String userName; 
	private String name;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "User [userName=" + userName + ", name=" + name + "]";
	} 
}
