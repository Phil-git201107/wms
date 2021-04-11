package com.chiczu.wms.entity.po;

public class Users {
    private Integer id;

    private String acctname;

    private String password;

    private String nickname;

    private String email;

    private String department; // 1:管理成員;2:行政組;3:進貨組;5:出貨組

    private String role = "0"; // 1:經理;2:組長;3:作業人員
    
    private String memberid; // 以createdate加序號兩碼組成,例如createdate是2021-03-17,則員工編號為2021031701...

    public String getMemberid() {
		return memberid;
	}

	public void setMemberid(String memberid) {
		this.memberid = memberid;
	}

	private String createdate;
    
    @Override
	public String toString() {
		return "Users [id=" + id + ", acctname=" + acctname + ", password=" + password + ", nickname=" + nickname
				+ ", email=" + email + ", department=" + department + ", role=" + role + ", createdate=" + createdate
				+ "]";
	}

	public Users() {
		super();
	}

	public Users(Integer id, String acctname, String password, String nickname, String email, String department,
			String role, String createdate) {
		super();
		this.id = id;
		this.acctname = acctname;
		this.password = password;
		this.nickname = nickname;
		this.email = email;
		this.department = department;
		this.role = role;
		this.createdate = createdate;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAcctname() {
        return acctname;
    }

    public void setAcctname(String acctname) {
        this.acctname = acctname == null ? null : acctname.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department == null ? null : department.trim();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate == null ? null : createdate.trim();
    }
}