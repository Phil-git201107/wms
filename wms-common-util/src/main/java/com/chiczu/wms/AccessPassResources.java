package com.chiczu.wms;

import java.util.HashSet;
import java.util.Set;

// 封裝無需做登錄檢查,可以放行的資源

public class AccessPassResources {
	
	public static final Set<String> PASS_RES_SET = new HashSet<>();
	
	//無需做登錄檢查的頁面
	static {
		PASS_RES_SET.add("/");
		PASS_RES_SET.add("/member/to/regpage");
		PASS_RES_SET.add("/member/do/register");
		PASS_RES_SET.add("/member/to/loginpage");
		PASS_RES_SET.add("/member/do/login");
		PASS_RES_SET.add("/member/logout");
		PASS_RES_SET.add("/getCode");
		PASS_RES_SET.add("/member/do/sendEmail");
		PASS_RES_SET.add("/get/page/info");
		PASS_RES_SET.add("/member/to/item/up-down");
		PASS_RES_SET.add("/to/test");
	}
	
	public static final Set<String> STATIC_RES_SET = new HashSet<>();
	// 無需做登錄檢查的靜態資源
	static {
		STATIC_RES_SET.add("bootstrap");
		STATIC_RES_SET.add("css");
		STATIC_RES_SET.add("fonts");
		STATIC_RES_SET.add("img");
		STATIC_RES_SET.add("jquery");
		STATIC_RES_SET.add("layer");
		STATIC_RES_SET.add("script");
		
	}
	
	// 用於判斷某個servletPath值是否對應一個靜態資源的目錄,返回true:是靜態資源;false:不是靜態資源
	public static boolean judgeCurrentServletPathWetherStaticResource(String servletPath){
		// 排除字串無效的情形
		if(servletPath == null || servletPath.length() == 0) {
			throw new RuntimeException(WmsConstant.MESSAGE_STRING_INVALIDATE);
		}
		// 根據"/",拆分servlet字串
		String[] split = servletPath.split("/");
		// 第1個斜槓左邊經過拆分後得到一個空字串(即split[0]是空字串),故取1
		String firstLevelPath = split[1];
		// 判斷是否在集合中
		return STATIC_RES_SET.contains(firstLevelPath);
	}
	
//	public static void main(String[] args) {
//		String servletPath = "/css/bbb/ccc";
//		boolean result = judgeCurrentServletPathWetherStaticResource(servletPath);
//		System.out.println(result);
//	}
}
