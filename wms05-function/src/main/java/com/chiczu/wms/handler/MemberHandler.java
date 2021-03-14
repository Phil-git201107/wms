package com.chiczu.wms.handler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.WmsConstant;
import com.chiczu.wms.WmsUtill;
import com.chiczu.wms.api.MySQLRemoteService;
import com.chiczu.wms.entity.po.Users;
import com.chiczu.wms.entity.vo.UserRegVO;
import com.google.code.kaptcha.impl.DefaultKaptcha;

@Controller
public class MemberHandler {
	
	Logger logger = LoggerFactory.getLogger(MemberHandler.class);
	
	@Autowired
    private DefaultKaptcha defaultKaptcha;
	
	@Autowired 
	private HttpServletRequest req; 
	
	@Autowired
	private MySQLRemoteService mysqlRemoteService;
//	
//	@RequestMapping("/member/logout")
//	public String logout(HttpSession session){
//		session.invalidate();
//		return "redirect:/";
//	}
//	
//	@RequestMapping("/member/do/login")
//	public String login(
//			UserRegVO userRegVO,
//			@RequestParam("code") String code,
//			Map<String,String> map,
//			HttpSession session) {
//		
//		// 獲取google驗證碼
//        String token = (String)req.getSession().getAttribute("verifyCode");
//        // 刪除session裡的google驗證碼紀錄
//        req.getSession().removeAttribute("verifyCode");
//        // 比對驗證碼
//        if(!token.equalsIgnoreCase(code.trim()) || token == null) {
//        	map.put("errorMsg", "驗證碼錯誤");
//			return "member-login";
//        }
//		
//		// 調用遠程介面,根據登錄帳號查詢MemberPO物件
//		ResultEntity<MemberPO> resultEntity = mysqlRemoteService.getMemberPOByLoginAcctRemote(loginacct);
//		
//		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
//			map.put("errorMsg", "帳號輸入錯誤");
//			return "member-login";
//		}
//		
//		MemberPO memberPO = resultEntity.getData();
//		
//		String userpswdFromDB = memberPO.getUserpswd();
//		// 將傳入的密碼進行加密
//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//		userpswd = userpswd.trim();
//		
//		// 因BCryptPasswordEncoder以隨機方式進行加密,
//		//故比較時,須由BCryptPasswordEncoder的matches()方法來比較是否相同(不能用object.equals())
//		boolean matchResult = encoder.matches(userpswd,userpswdFromDB);
//		if(!matchResult) {
//			map.put("errorMsg", "密碼錯誤");
//			return "member-login";
//		}
//		
//		// 創建MemberVO物件,封裝登錄成功的用戶部分資料,並存入Session域
//		MemberLoginVO memberLoginVO = new MemberLoginVO(memberPO.getId(),memberPO.getUsername(),memberPO.getEmail());
//		session.setAttribute("loginMember", memberLoginVO);
//		
//		return "redirect:/auth/member/to/centerpage";
//	}
//	
//	
	@RequestMapping("/member/do/register")
	public String register(UserRegVO userRegVO,Map<String,String> map) {
		// 獲取用戶輸入的驗證碼
		String code =  userRegVO.getCode().trim();
		// 獲取google驗證碼,attributeName設置在/getCode
        String token = (String)req.getSession().getAttribute("verifyCode");
        // 刪除session裡的google驗證碼紀錄
        req.getSession().removeAttribute("verifyCode");
        
        if(token != null && token.equalsIgnoreCase(code)) {
        	// 執行密碼加密
        	// 獲取用戶輸入的密碼
        	String password = userRegVO.getPassword();
        	// 加密
        	String userpswdAfterEncode = WmsUtill.passwordEncry(password);
        	// 將加密後的密碼封裝入userRegVO物件
        	userRegVO.setPassword(userpswdAfterEncode);
        	// 執行保存
        	// 創建一個空的memberPO
        	Users user = new Users();
        	// 複製屬性
        	BeanUtils.copyProperties(userRegVO, user);
        	// 調用遠程方法
        	ResultEntity<String> saveMemberResultEntity = mysqlRemoteService.saveUserRemote(user);
        	if(ResultEntity.FAILED.equals(saveMemberResultEntity.getResult())){
        		map.put("errorMsg", saveMemberResultEntity.getMsg());
            	return "member-reg";
        	}
        }else {
        	
        	map.put(WmsConstant.ATTR_NAME_ERRORMSG, "驗證碼錯誤");
        	return "reg";
        }
       	// 使用重定向,避免刷新瀏覽器導致重新執行註冊流程
		return "redirect:/auth/member/to/loginpage";
	}

	@RequestMapping("/getCode")
	public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws Exception {
		byte[] captchaChallengeAsJpeg = null;
		ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
		try {
			// 生成驗證碼字串並儲存到session中
			String createText = defaultKaptcha.createText();
			httpServletRequest.getSession().setAttribute("verifyCode", createText);
			// 使用生產的驗證碼字串返回一個BufferedImage物件並轉為byte寫入到byte陣列中
			BufferedImage challenge = defaultKaptcha.createImage(createText);
			ImageIO.write(challenge, "jpg", jpegOutputStream);
		} catch (IllegalArgumentException e) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		// 定義response輸出型別為image/jpeg型別，使用response輸出流輸出圖片的byte陣列
		captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
		httpServletResponse.setHeader("Cache-Control", "no-store");
		httpServletResponse.setHeader("Pragma", "no-cache");
		httpServletResponse.setDateHeader("Expires", 0);
		httpServletResponse.setContentType("image/jpeg");
		ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
		responseOutputStream.write(captchaChallengeAsJpeg);
		responseOutputStream.flush();
		responseOutputStream.close();
	}
	
	
}
