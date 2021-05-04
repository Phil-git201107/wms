package com.chiczu.wms.service.api;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.entity.po.PurchaseOrderCommodity;

public interface PurchaseOrderCommodityService {

	ResultEntity<List<PurchaseOrderCommodity>> getPurchaseOrderItemDone(String purchaseOrederNo);

	List<PurchaseOrderCommodity> saveUploadPurchaseOrder(MultipartFile uploadPurchaseOrder);
	
}
