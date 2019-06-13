package com.socket.pojo;

/**
  
*/
public class AuthOrg {

	private Integer id; //
	private String uuid; // 唯一标识
	private String phone; // 机构电话--注册手机号
	private String name; // 机构名称

	public void setId(Integer id){
		this.id = id;
	}

	public Integer getId(){
		return this.id;
	}

	public void setUuid(String uuid){
		this.uuid = uuid;
	}

	public String getUuid(){
		return this.uuid;
	}

	public void setPhone(String phone){
		this.phone = phone;
	}

	public String getPhone(){
		return this.phone;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

}