package com.chiczu.wms.entity.vo;


public class UserRegVO {

	private String acctname;
	private String password;
	private String nickname;
	private String email;
	private String code; // 封裝郵件驗證碼
	private String department; // 1:管理成員;2:行政組;3:進貨-儲位組;5:出貨組
	
	public UserRegVO() {
		super();
	}

	public UserRegVO(String acctname, String password, String nickname, String email, String code, String department) {
		super();
		this.acctname = acctname;
		this.password = password;
		this.nickname = nickname;
		this.email = email;
		this.code = code;
		this.department = department;
	}

	public String getAcctname() {
		return acctname;
	}

	public void setAcctname(String acctname) {
		this.acctname = acctname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Override
	public String toString() {
		return "UserRegVO [acctname=" + acctname + ", password=" + password + ", nickname=" + nickname + ", email="
				+ email + ", code=" + code + ", department=" + department + "]";
	}
	
	
	
}
