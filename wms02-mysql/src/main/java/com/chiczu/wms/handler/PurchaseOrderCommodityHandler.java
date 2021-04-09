package com.chiczu.wms.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.entity.po.PurchaseOrderCommodity;
import com.chiczu.wms.service.api.PurchaseOrderCommodityService;

@RestController
public class PurchaseOrderCommodityHandler {
	
	@Autowired
	private PurchaseOrderCommodityService pooCommodityService;

	@RequestMapping("/get/purchase/order/item/done")
	public ResultEntity<List<PurchaseOrderCommodity>> getPurchaseOrderItemDone(
			@RequestParam("purchaseOrederNo") String purchaseOrederNo){
		ResultEntity<List<PurchaseOrderCommodity>> result = pooCommodityService.getPurchaseOrderItemDone(purchaseOrederNo);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
}
