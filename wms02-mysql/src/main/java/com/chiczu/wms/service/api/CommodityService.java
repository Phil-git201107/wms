package com.chiczu.wms.service.api;

import java.util.List;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.entity.po.Commodity;
import com.chiczu.wms.entity.po.PurchaseOrderCommodity;
import com.chiczu.wms.entity.po.SingleProductPurchase;
import com.chiczu.wms.entity.po.SingleProductShip;
import com.github.pagehelper.PageInfo;

public interface CommodityService {

	PageInfo<Commodity> getPageInfo(Integer pageNum, Integer pageSize, String keyword);

	ResultEntity<Commodity> getCommodityByItemNo(String itemNo);

	PageInfo<Commodity> getNotUpItemPageInfo(Integer pageNum, Integer pageSize);

	PageInfo<Commodity> getSeizePositionItemPageinfo(Integer pageNum, Integer pageSize);

	ResultEntity<Commodity> getDownItemByItemNo(String itemNo);

	ResultEntity<Commodity> insertCommodity(Commodity commodity);

	ResultEntity<Commodity> checkItemnoUsed(String itemno);

	ResultEntity<Commodity> getCommodityBySearchVal(String itemSearchVal);

	ResultEntity<SingleProductPurchase> saveSingleItemPurchase(String itemno, Integer purchaseAmount);

	ResultEntity<List<SingleProductPurchase>> getTodayInstockItemInfo();

	ResultEntity<List<Commodity>> getItemFromPurchaseOrder(String purchaseOreder);

	ResultEntity<List<PurchaseOrderCommodity>> savePurchaseOrderItem(String purchaseOrederNo,String itemno, Integer purchaseQuantity);

	ResultEntity<List<SingleProductShip>> getTodayShipmentItemInfo();

	ResultEntity<SingleProductShip> saveSingleItemShip(String itemno, Integer itemCurrentStock, Integer shipAmount);


}
