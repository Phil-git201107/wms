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
		registry.addViewController("/member/to/loginpage").setViewName("login");
		registry.addViewController("/member/to/homepage").setViewName("homepage");
		registry.addViewController("/member/to/item/up-down").setViewName("item/item-up-down");
		registry.addViewController("/member/to/item/create").setViewName("item/item-create");
		registry.addViewController("/member/to/role/assign").setViewName("role-assign");
		registry.addViewController("/member/to/item/in-stock").setViewName("item/item-purchase");
		registry.addViewController("/member/to/batchitem/in-stock").setViewName("item/batchitem-purchase");
		registry.addViewController("/member/to/item/shipment").setViewName("item/item-shipment");
		registry.addViewController("/member/to/batchitem/shipment").setViewName("item/batchitem-shipment");
		registry.addViewController("/to/test").setViewName("test");
		

	}

}
