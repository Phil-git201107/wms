package com.chiczu.wms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.entity.po.PurchaseOrderCommodity;
import com.chiczu.wms.entity.po.PurchaseOrderCommodityExample;
import com.chiczu.wms.mapper.PurchaseOrderCommodityMapper;
import com.chiczu.wms.service.api.PurchaseOrderCommodityService;

@Service
public class PurchaseOrderCommodityServiceImpl implements PurchaseOrderCommodityService{
	
	@Autowired
	private PurchaseOrderCommodityMapper poCommodityMapper;

	@Override
	public ResultEntity<List<PurchaseOrderCommodity>> getPurchaseOrderItemDone(String purchaseOrederNo) {
		// 獲取當前進貨單已完成進貨品項,返回給頁面
		PurchaseOrderCommodityExample poCommodityEample = new PurchaseOrderCommodityExample();
		com.chiczu.wms.entity.po.PurchaseOrderCommodityExample.Criteria poCommodityCriteria = poCommodityEample.createCriteria();
		poCommodityCriteria.andOrderidEqualTo(purchaseOrederNo).andFulfillEqualTo("0");
		List<PurchaseOrderCommodity> poDoneItemList = poCommodityMapper.selectByExample(poCommodityEample);
		
		if(poDoneItemList.size() > 0) {
			return ResultEntity.successWithData(poDoneItemList);
		}else {
			return ResultEntity.failed(purchaseOrederNo+" 進貨單,尚未完成任何品項進貨,或無此訂貨單。");
		}
		
	}

}
