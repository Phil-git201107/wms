package com.chiczu.wms;

import org.mindrot.jbcrypt.BCrypt;

public class WmsUtill {

	// 使用BCrypt,為使用者註冊密碼進行加密,再讓密碼存入資料庫
	public static String passwordEncry(String password) {
		
		String hashpw = BCrypt.hashpw(password, BCrypt.gensalt());
		return hashpw;
	}
}
