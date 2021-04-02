package com.chiczu.wms.handler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.WmsConstant;
import com.chiczu.wms.WmsUtil;
import com.chiczu.wms.api.MySQLRemoteService;
import com.chiczu.wms.entity.po.Users;
import com.chiczu.wms.entity.vo.UserLoginVO;
import com.chiczu.wms.entity.vo.UserRegVO;
import com.google.code.kaptcha.impl.DefaultKaptcha;

@Controller
public class UserHandler {
	
	Logger logger = LoggerFactory.getLogger(UserHandler.class);
	
	@Autowired
    private DefaultKaptcha defaultKaptcha;
	
	@Autowired
	JavaMailSender mailSender;
	
	@Autowired
	private MySQLRemoteService mysqlRemoteService;
	
	@ResponseBody
	@RequestMapping("/save/member/role/assign")
	public ResultEntity<Users> saveMemberRoleAssogn(
			@RequestParam("acctName") String acctName,
			@RequestParam("role") String role,
			@RequestParam("id") Integer id){
		ResultEntity<Users> result = mysqlRemoteService.saveMemberRoleAssign(acctName,role,id);
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/get/member/info")
	public ResultEntity<Users> getMemberInfo(@RequestParam("searchVal") String searchVal){
		ResultEntity<Users> result = mysqlRemoteService.getMemberInfo(searchVal);
		return result;
	}
	
	
	@RequestMapping("/member/logout")
	public String logout(HttpSession session){
		session.invalidate();
		return "redirect:/";
	}
	
	@RequestMapping("/member/do/login")
	public String login(
			UserLoginVO userLoginVO,
			@RequestParam("code") String code,
			Map<String,Object> map,
			HttpSession session) {
		
		// 獲取google驗證碼
        String token = (String)session.getAttribute("verifyCode");
        // 刪除session裡的google驗證碼紀錄
        session.removeAttribute("verifyCode");
        // 比對驗證碼
        if(!token.equalsIgnoreCase(code.trim()) || token == null) {
        	map.put(WmsConstant.ATTR_NAME_ERRORMSG, "驗證碼錯誤");
			return "login";
        }
		
		// 調用遠程介面,根據登錄帳號查詢MemberPO物件
		ResultEntity<Users> resultEntity = mysqlRemoteService.getUserByLoginAcctRemote(userLoginVO.getAcctname());
		
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			map.put(WmsConstant.ATTR_NAME_ERRORMSG, "帳號輸入錯誤");
			return "login";
		}
		
		Users user = resultEntity.getData();
		
		String userpswdFromDB = user.getPassword();
		// 將傳入的密碼進行加密
		String userpswd = userLoginVO.getPassword().trim();
		BCrypt bCrypt = new BCrypt();
		// 因BCrypt以隨機方式進行加密,
		//故比較時,須由BCrypt的checkpw(String plaintext, String hashed)方法來比較是否相同(不能用object.equals())
		boolean matchResult = bCrypt.checkpw(userpswd,userpswdFromDB);
		if(!matchResult) {
			map.put(WmsConstant.ATTR_NAME_ERRORMSG, WmsConstant.MESSAGE_LOGIN_FAILED_PASSWORD);
			return "login";
		}
		
		// 運用user物件內容,封裝入userLoginVO(已有acctname,password,需另set入其他屬性值),做為登錄成功的用戶資料,並存入Session域
		userLoginVO.setNickname(user.getNickname());
		userLoginVO.setDepartment(user.getDepartment());
		userLoginVO.setRole(user.getRole());
		session.setAttribute(WmsConstant.ATTR_NAME_LOGIN_MEMBER, userLoginVO);
		
//		PageInfo<Commodity> pageInfo = mysqlRemoteService.getPageInfo(1, 5, null);
//		map.put(WmsConstant.ATTR_NAME_PAGE_INFO, pageInfo);
		return "redirect:/member/to/homepage";
	}

	@RequestMapping("/member/do/register")
	public String register(UserRegVO userRegVO,Map<String,String> map,HttpServletRequest req) {
		// 獲取用戶輸入的驗證碼
		String code =  userRegVO.getCode().trim();
		// 至session獲取發送至用戶email的驗證碼
        String token = req.getParameter("token");
        // 刪除請求域中vericode
        req.removeAttribute(WmsConstant.ATTR_NAME_VERICODE);
        
        if(code != null && token.equalsIgnoreCase(code)) {
        	// 執行密碼加密
        	// 獲取用戶輸入的密碼
        	String password = userRegVO.getPassword();
        	// 加密
        	String userpswdAfterEncode = WmsUtil.passwordEncry(password);
        	// 將加密後的密碼封裝入userRegVO物件
        	userRegVO.setPassword(userpswdAfterEncode);
        	// 執行保存
        	// 創建一個空的users
//        	Users user = new Users();
        	// 複製屬性
        	Users user = new Users();
        	BeanUtils.copyProperties(userRegVO, user);
        	// 調用遠程方法
        	ResultEntity<String> saveMemberResultEntity = mysqlRemoteService.saveUserRemote(user);
        	if(ResultEntity.FAILED.equals(saveMemberResultEntity.getResult())){
        		map.put(WmsConstant.ATTR_NAME_ERRORMSG, saveMemberResultEntity.getMessage());
            	
        		return "reg";
        	}
        }else {
        	map.put(WmsConstant.ATTR_NAME_ERRORMSG, "驗證碼錯誤");
        	
        	return "reg";
        }
       	// 使用重定向,避免刷新瀏覽器導致重新執行註冊流程
		return "redirect:/member/to/loginpage";
	}
	/*
	 * 發送郵件
	 * 1.需導入spring-boot-starter-mail這個依賴
	 * 2.於配置文件中,設置相關配置
	 * 3.運用SimpleMailMessage類,發送郵件
	 */
	@ResponseBody
	@RequestMapping("/member/do/sendEmail")
	public ResultEntity<Map<String,String>> sendEmail(
			@RequestParam("email")String email,
			Map<String,String> map,
			HttpServletResponse response){
		// 設置一個有62個字元的字串,再隨機獲取8個字元(可能重複)作為驗證碼
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer sb = new StringBuffer();
		for (int i = 1; i <= 8; i++) {
			char charOne = chars.charAt((int) (Math.random() * 62));
			sb.append(charOne);
		}
		String verificationCode = sb.toString();
		// 獲取發信的SimpleMailMessage物件
		SimpleMailMessage message = new SimpleMailMessage();
		// 設置要發送的電郵地址
		message.setTo(email);
		// 設置郵件標題
		message.setSubject("CHicZu倉庫管理平台-用戶註冊驗證碼");
		// 設置郵件內容
		message.setText("請於註冊頁面輸入此驗證碼: "+verificationCode);
		// 發送郵件
		mailSender.send(message);
		
		map.put(WmsConstant.ATTR_NAME_VERICODE, verificationCode);
		
		return ResultEntity.successWithData(map);
		
	}
	/*
	 * 讓頁面獲取google驗證碼
	 */
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
