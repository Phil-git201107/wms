package com.chiczu.wms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FunctionWebMvcConfig implements WebMvcConfigurer{

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// 瀏覽器訪問註冊的地址
		String urlPath = "/member/to/regpage";
		// 目標註冊頁面的名稱,併接在xml裡的前(classpath:/templates/)後(.html)綴,自templates取出頁面
		String viewName = "reg";
		registry.addViewController(urlPath).setViewName(viewName);

		registry.addViewController("/").setViewName("login");
		registry.addViewController("member/to/loginpage").setViewName("login");

	}

}
