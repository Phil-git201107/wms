package com.chiczu.wms.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.entity.po.Commodity;
import com.chiczu.wms.entity.po.Position;
import com.chiczu.wms.entity.po.PurchaseOrderCommodity;
import com.chiczu.wms.entity.po.SingleProductPurchase;
import com.chiczu.wms.entity.po.SingleProductShip;
import com.chiczu.wms.service.api.CommodityService;
import com.chiczu.wms.service.api.PositionService;
import com.github.pagehelper.PageInfo;


@RestController
public class CommodityHandler {
	
	Logger logger = LoggerFactory.getLogger(CommodityHandler.class);
	
	@Autowired
	private CommodityService commodityService;
	
	@Autowired
	private PositionService positionService;
	
	@RequestMapping("/get/item/to/adjust/position/page")
	public ResultEntity<Commodity> getItemToAdjustPositionPage(@RequestParam("itemno") String itemno){
		ResultEntity<Commodity> result = commodityService.getItemToAdjustPositionPage(itemno);
		if("SUCCESS".equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	@RequestMapping("/save/purchase/order/item")
	public ResultEntity<List<PurchaseOrderCommodity>> savePurchaseOrderItem(
			@RequestParam("purchaseOrederNo") String purchaseOrederNo,
			@RequestParam("itemno") String itemno,
			@RequestParam("purchaseQuantity") Integer purchaseQuantity){
		ResultEntity<List<PurchaseOrderCommodity>> result = commodityService.savePurchaseOrderItem(purchaseOrederNo,itemno,purchaseQuantity);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	@RequestMapping("/get/item/from/purchase/order")
	public ResultEntity<List<Commodity>> getItemFromPurchaseOrder(@RequestParam("purchaseOreder") String purchaseOreder){
		ResultEntity<List<Commodity>> result = commodityService.getItemFromPurchaseOrder(purchaseOreder);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	@RequestMapping("/get/item/from/ship/order")
	public ResultEntity<List<Commodity>> getItemFromShipOrder(@RequestParam("shipOrederNo") String shipOrederNo){
		ResultEntity<List<Commodity>> result = commodityService.getItemFromShipOrder(shipOrederNo);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	@RequestMapping("/get/today/instock/item/info")
	public ResultEntity<List<SingleProductPurchase>> getTodayInstockItemInfo(){
		
		ResultEntity<List<SingleProductPurchase>> result = commodityService.getTodayInstockItemInfo();
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	@RequestMapping("/get/today/shipment/item/info")
	public ResultEntity<List<SingleProductShip>> getTodayShipmentItemInfo(){
		ResultEntity<List<SingleProductShip>> result = commodityService.getTodayShipmentItemInfo();
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	@RequestMapping("/save/single/item/purchase")
	public ResultEntity<SingleProductPurchase> saveSingleItemPurchase(
			@RequestParam("itemno") String itemno,
			@RequestParam("purchaseAmount") Integer purchaseAmount){
		ResultEntity<SingleProductPurchase> result = commodityService.saveSingleItemPurchase(itemno, purchaseAmount);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	@RequestMapping("/save/single/item/shipment")
	public ResultEntity<SingleProductShip> saveSingleItemShip(
			@RequestParam("itemno") String itemno,
			@RequestParam("itemCurrentStock")Integer itemCurrentStock,
			@RequestParam("shipAmount") Integer shipAmount){
		ResultEntity<SingleProductShip> result = commodityService.saveSingleItemShip(itemno,itemCurrentStock, shipAmount);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	@RequestMapping("/get/item/to/instock-shipment")
	public ResultEntity<Commodity> getItemToInstockOrShipment(@RequestParam("itemSearchVal")String itemSearchVal){
		ResultEntity<Commodity> commodity = commodityService.getCommodityBySearchVal(itemSearchVal);
		if(ResultEntity.SUCCESS.equals(commodity.getResult())) {
			return ResultEntity.successWithData(commodity.getData());
		}else {
			return ResultEntity.failed(commodity.getMessage());
		}
	}
	
	@RequestMapping("/create/newitem/data")
	public ResultEntity<Commodity> insertCommodity(@RequestBody Commodity commodity){
		
		logger.info("CommodityHandler commodity: "+commodity.toString());
		ResultEntity<Commodity> result = commodityService.insertCommodity(commodity);
		if(ResultEntity.SUCCESS == result.getResult()) {
			return ResultEntity.successWithoutData();
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	@RequestMapping("/check/itemno/used")
	public ResultEntity<Commodity> checkItemnoUsed(@RequestParam("itemno") String itemno){
		ResultEntity<Commodity> result = commodityService.checkItemnoUsed(itemno);
		if(ResultEntity.SUCCESS == result.getResult()) {
			return ResultEntity.successWithoutData();
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	@RequestMapping("/get/down/item")
	public ResultEntity<Commodity> getDownItemByItemNo(@RequestParam("itemNo")String itemNo){
		
		ResultEntity<Commodity> resultCommodity = commodityService.getDownItemByItemNo(itemNo);
		
		// 判斷返回的結果,true:表示有自資料庫獲取商品編號為itemno且庫存為0的commodity;false:表示資料庫無此商品
		if(ResultEntity.SUCCESS.equals(resultCommodity.getResult())) {
			// 判斷資料庫返回的item是否已有儲位,true:表示item沒有儲位,返回false;false:表示此商品有儲位,返回true
			ResultEntity<Position> resultPosition = positionService.getDownItemPosition(itemNo);
			
			if(ResultEntity.SUCCESS.equals(resultPosition.getResult())) {
				//將儲位資料設置給commodity
				resultCommodity.getData().setPosition(resultPosition.getData());
				return ResultEntity.successWithData(resultCommodity.getData()); 		
			}else {
				return ResultEntity.failed(resultPosition.getMessage());
			}		
		}else {
			return ResultEntity.failed(resultCommodity.getMessage());
		}

	}
	
	@RequestMapping("/get/seize/position/item/info")
	public PageInfo<Commodity> getSeizePositionItemPageinfo(
			@RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
			//pageSize,默認值設置"5"
			@RequestParam(value="pageSize",defaultValue = "5") Integer pageSize){
		PageInfo<Commodity> pageInfo = commodityService.getSeizePositionItemPageinfo(pageNum,pageSize);
		return pageInfo;
	}
	
	@RequestMapping("/get/not-up/item/info")
	public PageInfo<Commodity> getNotUpItemPageInfo(
			//pageNum默認值使用"1"
			@RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
			//pageSize,默認值設置"5"
			@RequestParam(value="pageSize",defaultValue = "5") Integer pageSize
			){
		
		PageInfo<Commodity> pageInfo = commodityService.getNotUpItemPageInfo(pageNum,pageSize);
		return pageInfo;
	}
	
	@RequestMapping("/get/page/info")
	public PageInfo<Commodity> getPageInfo(
			//pageNum默認值使用"1"
			@RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
			//pageSize,默認值設置"5"
			@RequestParam(value="pageSize",defaultValue = "5") Integer pageSize,
			@RequestParam(value="keyword",defaultValue = "") String keyword
			){
		
		PageInfo<Commodity> pageInfo = commodityService.getPageInfo(pageNum,pageSize,keyword);
		return pageInfo;
	}
	
	@RequestMapping("/get/item/to/up-down/page")
	public ResultEntity<Commodity> getCommodityByItemNo(@RequestParam("itemNo")String itemNo) {
		
		ResultEntity<Commodity> resultCommodity = commodityService.getCommodityByItemNo(itemNo);
		// 判斷返回的結果,true:表示有自資料庫獲取商品編號為itemno的commodity;false:表示資料庫無此商品,應該是尚未建立商品基本資料檔
		if(ResultEntity.SUCCESS.equals(resultCommodity.getResult())) {
			// 判斷資料庫返回的item是否已有儲位,true:需要建立儲位;false:無須再建立儲位
			ResultEntity<Position> resultPosition = positionService.getItemPosition(itemNo);
			if(ResultEntity.SUCCESS.equals(resultPosition.getResult())) {
				return ResultEntity.successWithData(resultCommodity.getData()); 
			}else {
				return ResultEntity.failed(resultPosition.getMessage());
			}		
		}else {
			return ResultEntity.failed(resultCommodity.getMessage());
		}
		
	}
	
}
