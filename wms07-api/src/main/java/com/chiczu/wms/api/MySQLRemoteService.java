package com.chiczu.wms.api;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.entity.po.Users;


@FeignClient("wms-mysql")
public interface MySQLRemoteService {
	
	@RequestMapping("/save/member/remote")
	public ResultEntity<String> saveUserRemote(
			@RequestBody Users user);
	
}
