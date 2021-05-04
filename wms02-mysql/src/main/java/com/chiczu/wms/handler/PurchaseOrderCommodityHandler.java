package com.chiczu.wms.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.entity.po.PurchaseOrderCommodity;
import com.chiczu.wms.service.api.PurchaseOrderCommodityService;

@RestController
public class PurchaseOrderCommodityHandler {
	
	Logger logger = LoggerFactory.getLogger(PurchaseOrderCommodityHandler.class);
	
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
	
	@RequestMapping("/member/upload/purchase-order")
	public List<PurchaseOrderCommodity> saveUploadPurchaseOrder(@RequestPart("uploadPurchaseOrder") MultipartFile uploadPurchaseOrder){
		String originalName = uploadPurchaseOrder.getOriginalFilename();
		List<PurchaseOrderCommodity> poCommodityList = pooCommodityService.saveUploadPurchaseOrder(uploadPurchaseOrder);
		return poCommodityList;
	}
	
	
}
