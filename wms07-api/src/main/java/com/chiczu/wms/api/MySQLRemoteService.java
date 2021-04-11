package com.chiczu.wms.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.entity.po.Commodity;
import com.chiczu.wms.entity.po.PurchaseOrderCommodity;
import com.chiczu.wms.entity.po.SingleProductPurchase;
import com.chiczu.wms.entity.po.SingleProductShip;
import com.chiczu.wms.entity.po.Users;
import com.github.pagehelper.PageInfo;

@FeignClient("wms-mysql")
public interface MySQLRemoteService {
	
	@RequestMapping("/save/member/remote")
	public ResultEntity<String> saveUserRemote(
			@RequestBody Users user);
	
	@RequestMapping("/member/do/login")
	public ResultEntity<Users> getUserByLoginAcctRemote(@RequestParam("acctname")String acctname);
	
	@RequestMapping("/get/page/info")
	public PageInfo<Commodity> getPageInfo(
			//pageNum默認值使用"1"
			@RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
			//pageSize,默認值設置"5"
			@RequestParam(value="pageSize",defaultValue = "5") Integer pageSize,
			@RequestParam(value="keyword",defaultValue = "") String keyword
			);
	
	@RequestMapping("/get/item/to/up-down/page")
	public ResultEntity<Commodity> getCommodityByItemNo(@RequestParam("itemNo") String itemNo);

	@RequestMapping("/get/storage/empty/spaces")
	public List<String> getStorageEmptySpaces(@RequestParam("areaVal") String areaVal);
	
	@RequestMapping("/save/item/position")
	public ResultEntity<String> saveItemPosition(
			@RequestParam("itemNo") String itemNo,
			@RequestParam("area") String area,
			@RequestParam("position") String position
			);

	@RequestMapping("/get/not-up/item/info")
	public PageInfo<Commodity> getNotUpItemPageInfo(
			//pageNum默認值使用"1"
			@RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
			//pageSize,默認值設置"5"
			@RequestParam(value="pageSize",defaultValue = "5") Integer pageSize
			);

	@RequestMapping("/get/seize/position/item/info")
	public PageInfo<Commodity> getSeizePositionItemPageinfo(
			@RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
			//pageSize,默認值設置"5"
			@RequestParam(value="pageSize",defaultValue = "5") Integer pageSize
			);
	
	@RequestMapping("/get/down/item")
	public ResultEntity<Commodity> getDownItemByItemNo(@RequestParam("itemNo")String itemNo);

	@RequestMapping("/commodity/remove/position/by/commodity/itemno/array")
	public Integer removePositionByCommodityItemno(@RequestBody List<String> commodityItemnoList);

	@RequestMapping("/create/newitem/data")
	public ResultEntity<Commodity> insertCommodity(@RequestBody Commodity commodity);

	@RequestMapping("/check/itemno/used")
	public ResultEntity<Commodity> checkItemnoUsed(@RequestParam("itemno") String itemno);
	
	@RequestMapping("/get/member/info")
	public ResultEntity<Users> getMemberInfo(@RequestParam("searchVal") String searchVal);

	@RequestMapping("/save/member/role/assign")
	public ResultEntity<Users> saveMemberRoleAssign(
			@RequestParam("acctName") String acctName,
			@RequestParam("role") String role,
			@RequestParam("id") Integer id);

	@RequestMapping("/get/item/to/instock-shipment")
	public ResultEntity<Commodity> getItemToInstockOrShipment(@RequestParam("itemSearchVal")String itemSearchVal);

	@RequestMapping("/save/single/item/purchase")
	public ResultEntity<SingleProductPurchase> saveSingleItemPurchase(
			@RequestParam("itemno") String itemno,
			@RequestParam("purchaseAmount") Integer purchaseAmount);

	@RequestMapping("/get/today/instock/item/info")
	public ResultEntity<List<SingleProductPurchase>> getTodayInstockItemInfo();

	@RequestMapping("/get/item/from/purchase/order")
	public ResultEntity<List<Commodity>> getItemFromPurchaseOrder(@RequestParam("purchaseOreder") String purchaseOreder);

	@RequestMapping("/save/purchase/order/item")
	public ResultEntity<List<PurchaseOrderCommodity>> savePurchaseOrderItem(
			@RequestParam("purchaseOrederNo") String purchaseOrederNo,
			@RequestParam("itemno") String itemno,
			@RequestParam("purchaseQuantity") Integer purchaseQuantity);

	@RequestMapping("/get/purchase/order/item/done")
	public ResultEntity<List<PurchaseOrderCommodity>> getPurchaseOrderItemDone(
			@RequestParam("purchaseOrederNo") String purchaseOrederNo);

	@RequestMapping("/get/today/shipment/item/info")
	public ResultEntity<List<SingleProductShip>> getTodayShipmentItemInfo();

	@RequestMapping("/save/single/item/shipment")
	public ResultEntity<SingleProductShip> saveSingleItemShip(
			@RequestParam("itemno") String itemno,
			@RequestParam("itemCurrentStock")Integer itemCurrentStock,
			@RequestParam("shipAmount") Integer shipAmount);
	
}
