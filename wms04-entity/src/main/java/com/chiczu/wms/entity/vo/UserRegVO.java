package com.chiczu.wms.entity.vo;


public class UserRegVO {

	private String acctName;
	private String password;
	private String nickName;
	private String email;
	private String code;
	
	public UserRegVO() {
		super();
	}

	public UserRegVO(String acctName, String password, String nickName, String email, String code) {
		super();
		this.acctName = acctName;
		this.password = password;
		this.nickName = nickName;
		this.email = email;
		this.code = code;
	}

	public String getAcctName() {
		return acctName;
	}

	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
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

	@Override
	public String toString() {
		return "UserRegVO [acctName=" + acctName + ", password=" + password + ", nickName=" + nickName + ", email="
				+ email + ", code=" + code + "]";
	}
	
	
}
