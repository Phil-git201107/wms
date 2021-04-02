package com.chiczu.wms.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.WmsConstant;
import com.chiczu.wms.entity.po.Users;
import com.chiczu.wms.service.api.UsersService;

@RestController
public class MemberHandler {

	private Logger logger = LoggerFactory.getLogger(MemberHandler.class);
	
	@Autowired
	private UsersService userService;
	
	@RequestMapping("/save/member/role/assign")
	public ResultEntity<Users> saveMemberRoleAssign(
			@RequestParam("acctName") String acctName,
			@RequestParam("role") String role,
			@RequestParam("id") Integer id){
		ResultEntity<Users> result = userService.saveMemberRoleAssign(acctName,role,id);
		return result;
	}
	
	@RequestMapping("/get/member/info")
	public ResultEntity<Users> getMemberInfo(@RequestParam("searchVal") String searchVal){
		ResultEntity<Users> result = userService.getMemberInfo(searchVal);
		return result;
	}
	
	@RequestMapping("/save/member/remote")
	public ResultEntity<String> saveUserRemote(@RequestBody Users user){
		
		try {
			userService.saveUser(user);
			return ResultEntity.successWithoutData();
			
		} catch (Exception e) {
			
			if(e instanceof DuplicateKeyException) {
				return ResultEntity.failed(WmsConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
			}
			
			return ResultEntity.failed(e.getMessage());
		}
	}
	
	@RequestMapping("/member/do/login")
	public ResultEntity<Users> getUserByLoginAcctRemote(@RequestParam("acctname")String acctname){
		
		try {
			// 調用本地Service完成查詢
			Users user = userService.getUserByLoginAcct(acctname);
			// 沒有異常,返回成功的結果
			return ResultEntity.successWithData(user);
		} catch (Exception e) {
			e.printStackTrace();
			// 有異常,返回失敗的訊息
			return ResultEntity.failed(e.getMessage());
		}
			
	}
	
}
