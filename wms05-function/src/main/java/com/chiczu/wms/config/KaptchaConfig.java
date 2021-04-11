package com.chiczu.wms.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

/**
 * google驗證碼Kaptcha配置類
 * 使用google驗證碼,由三個部分組成
 * 1.網頁上
 * 2.配置類
 * 3.handler中獲取驗證碼
 * 
 * @author Phil
 *
 */
@Configuration
public class KaptchaConfig {

	@Bean
	public DefaultKaptcha producer() {
		Properties properties = new Properties();
		properties.put("kaptcha.border", "no");
		// 設置驗證碼文字顏色
		properties.put("kaptcha.textproducer.font,color", "black");
		// 設置驗證碼字數
		properties.put("kaptcha.textproducer.char.space", "5");
		// 設置驗證碼文字來源
		properties.put("kaptcha.textproducer.char.string", "ACDEFGHKMNPQRSTWX2356789");
		// 設置干擾線顏色
		properties.put("kaptcha.noise.color", "red");
		Config config = new Config(properties);
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}
}
