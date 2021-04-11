package com.chiczu.wms.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.chiczu.wms.AccessPassResources;
import com.chiczu.wms.WmsConstant;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class WmsAccessFilter extends ZuulFilter{

	// 定義需要過濾的請求
	@Override
	public boolean shouldFilter() {
		// 1.獲取RequestContext物件
		RequestContext requestContext = RequestContext.getCurrentContext();
		// 2.通過RequestContext物件,獲取當前Request物件
		// (框架底層借助了threadLocal,自當前執行緒上獲取事先綁定的request)
		HttpServletRequest request = requestContext.getRequest();
		// 3.獲取servletPath值
		String servletPath = request.getServletPath();
		// 4.根據servletPath判斷當前請求是否可無須檢查,直接放行
		boolean containResult = AccessPassResources.PASS_RES_SET.contains(servletPath);
		if (containResult) {
			// 如果當前請求是可直接放行的功能,返回false
			return false;
		}
		// 判斷當前請求是否為靜態資源
		boolean judgeStaticResult = AccessPassResources.judgeCurrentServletPathWetherStaticResource(servletPath);
		if (judgeStaticResult) {
			// 如果當前請求是靜態資源,返回false
			return false;
		} else {
			// 返回true,表需執行後續的run()方法,檢查是否已登錄
			return true;
		}
	}

	@Override
	public Object run() throws ZuulException {
		// 1.獲取Request物件
		RequestContext requestContext = RequestContext.getCurrentContext();
		HttpServletRequest request = requestContext.getRequest();
		// 2.獲取當前session物件
		HttpSession session = request.getSession();
		// 3.自session物件中,獲取用戶資料
		Object loginMember = session.getAttribute(WmsConstant.ATTR_NAME_LOGIN_MEMBER);
		// 4.判斷loginMember是否為空
		if (loginMember == null) {
			// 自requestContext物件中獲取Response物件
			HttpServletResponse response = requestContext.getResponse();
			// 將提示錯誤訊息存入session域
			session.setAttribute(
					WmsConstant.ATTR_NAME_ERRORMSG, WmsConstant.MESSAGE_ACCESS_DENIED);
			// 重定向到登錄頁面
			try {
				response.sendRedirect("/member/to/loginpage");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public String filterType() {
		// 返回"pre",表示在目標服務前執行過濾檢查
		return "pre";
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

}
