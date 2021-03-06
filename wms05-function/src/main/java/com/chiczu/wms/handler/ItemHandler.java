package com.chiczu.wms.handler;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.WmsUtil;
import com.chiczu.wms.api.MySQLRemoteService;
import com.chiczu.wms.entity.po.CheckOrderCommodity;
import com.chiczu.wms.entity.po.Commodity;
import com.chiczu.wms.entity.po.PurchaseOrderCommodity;
import com.chiczu.wms.entity.po.ShipOrderCommodity;
import com.chiczu.wms.entity.po.SingleProductPurchase;
import com.chiczu.wms.entity.po.SingleProductShip;
import com.chiczu.wms.entity.vo.CommodityDataCreateVO;
import com.chiczu.wms.entity.vo.ItemUpAndDownPositionSideNavVO;
import com.chiczu.wms.entity.vo.PurchaseUndoneOrderVO;
import com.chiczu.wms.entity.vo.ShipmentUndoneOrderVO;
import com.chiczu.wms.entity.vo.StorageTableVO;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;

@Controller
public class ItemHandler {
	
	Logger logger = LoggerFactory.getLogger(ItemHandler.class);
	
	@Autowired
	private MySQLRemoteService mysqlRemoteService;
	
	// 接收用戶上傳的進貨單csv檔,存入資料庫,並將此進貨單資料返回到進貨單出貨頁面 
	@RequestMapping("/member/upload/purchase-order")
	public String saveUploadPurchaseOrder(
			@RequestPart("uploadPurchaseOrder") MultipartFile uploadPurchaseOrder,
			Map<String,Object> map) {
		List<PurchaseOrderCommodity> poCommodityList = mysqlRemoteService.saveUploadPurchaseOrder(uploadPurchaseOrder);
		String originalName = uploadPurchaseOrder.getOriginalFilename();
		logger.info("ItemHandler: "+originalName);
		String name = originalName.substring(0,originalName.lastIndexOf("."));
		
		
        map.put("orderId", name);
        map.put("poCommpdityList", poCommodityList);
		return "item/upload-purchase-order"; 
	}
	
	// 依用戶輸入的盤點數量,生成隨機獲得的受盤點商品資料 
	@ResponseBody
	@RequestMapping("/generate/checkitem/amount/list")
	public ResultEntity<List<CheckOrderCommodity>> generateCheckItemAmountList(@RequestParam("checkAmount") Integer checkAmount){
		logger.info("generateCheckItemAmountList: "+checkAmount);
		ResultEntity<List<CheckOrderCommodity>> result = mysqlRemoteService.generateCheckItemAmountList(checkAmount);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 依用戶調整後的儲位內容,更新資料庫資料
	@ResponseBody
	@RequestMapping("/save/item/adjust/position")
	public ResultEntity<String> saveItemAdjustPosition(
			@RequestParam("itemNo") String itemNo,
			@RequestParam("area") String area,
			@RequestParam("position") String position){
		itemNo = itemNo.toUpperCase();
		ResultEntity<String> resultEntity = mysqlRemoteService.saveItemAdjustPosition(itemNo,area,position);
		if(ResultEntity.SUCCESS.equals(resultEntity.getResult())) {
			return ResultEntity.successWithoutData();
		}else {
			return ResultEntity.failed(resultEntity.getMessage());
		}
	}
	
	// 為顯示儲位表,獲取儲位資料 
	@ResponseBody
	@RequestMapping("/get/storage/table/Info")
	public ResultEntity<List<StorageTableVO>> getStorageTableInfo(){
		ResultEntity<List<StorageTableVO>> result = mysqlRemoteService.getStorageTableInfo();
		if("SUCCESS".equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 側邊欄通知用戶待上下架商品數
	@ResponseBody
	@RequestMapping("/get/item/upAnddown/position/sideNav")
	public ResultEntity<ItemUpAndDownPositionSideNavVO> getItemUpAnddownPositionForSideNav(){
		ResultEntity<ItemUpAndDownPositionSideNavVO> result = mysqlRemoteService.getItemUpAnddownPositionForSideNav();
		if("SUCCESS".equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 側邊欄通知未完成的進貨單數,通知用戶     
	@ResponseBody
	@RequestMapping("/get/undone/purchase/order")
	public ResultEntity<List<PurchaseUndoneOrderVO>> getUndonePurchaseOrder(){
		
		ResultEntity<List<PurchaseUndoneOrderVO>> result = mysqlRemoteService.getUndonePurchaseOrder();
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
		
	}
	
	// 側邊欄通知未揀貨完的出貨訂單數,通知用戶 
	@ResponseBody
	@RequestMapping("/get/undone/ship/order")
	public ResultEntity<List<ShipmentUndoneOrderVO>> getUndoneShipOrder(){
		
		ResultEntity<List<ShipmentUndoneOrderVO>> result = mysqlRemoteService.getUndoneShipOrder();
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 獲取進貨單內已完成進貨的商品資料
	@ResponseBody
	@RequestMapping("/get/purchase/order/item/done")
	public ResultEntity<List<PurchaseOrderCommodity>> getPurchaseOrderItemDone(@RequestParam("purchaseOrederNo") String purchaseOrederNo){
		ResultEntity<List<PurchaseOrderCommodity>> result = mysqlRemoteService.getPurchaseOrderItemDone(purchaseOrederNo);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 為調整儲位頁,依itemno獲取商品資料 
	@ResponseBody
	@RequestMapping("/get/item/to/adjust/position/page")
	public ResultEntity<Commodity> getItemToAdjustPositionPage(@RequestParam("itemNo") String itemno){
		itemno = itemno.toUpperCase().trim();
		ResultEntity<Commodity> result = mysqlRemoteService.getItemToAdjustPositionPage(itemno);
		if("SUCCESS".equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
		
	}
	
	// 獲取出貨單內已完成出貨的品項 
	@ResponseBody
	@RequestMapping("/get/ship/order/item/done")
	public ResultEntity<List<ShipOrderCommodity>> getShipOrderItemDone(@RequestParam("shipOrederNo") String shipOrederNo){
		ResultEntity<List<ShipOrderCommodity>> result = mysqlRemoteService.getShipOrderItemDone(shipOrederNo);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 為進貨單內的商品,完成進貨存檔動作
	@ResponseBody
	@RequestMapping("/save/purchase/order/item")
	public ResultEntity<List<PurchaseOrderCommodity>> savePurchaseOrderItem(
			@RequestParam("purchaseOrederNo") String purchaseOrederNo,
			@RequestParam("itemno") String itemno,
			@RequestParam("purchaseQuantity") Integer purchaseQuantity){
		ResultEntity<List<PurchaseOrderCommodity>> result = mysqlRemoteService.savePurchaseOrderItem(purchaseOrederNo,itemno,purchaseQuantity);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 為出貨單內商品,完成出貨扣除庫存量...等商品資料更新 
	@ResponseBody
	@RequestMapping("/save/ship/order/item")
	public ResultEntity<List<ShipOrderCommodity>> saveShipOrderItem(
			@RequestParam("shipOrederNo") String shipOrederNo,
			@RequestParam("itemno") String itemno,
			@RequestParam("itemCurrentStock") Integer itemCurrentStock,
			@RequestParam("shipQuantity") Integer shipQuantity){
		if( shipQuantity > itemCurrentStock) {
			return ResultEntity.failed("出貨量大於庫存量,請確認庫存數或調整出貨量。");
		}
		ResultEntity<List<ShipOrderCommodity>> result = mysqlRemoteService.saveShipOrderItem(shipOrederNo,itemno,shipQuantity);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 依進貨單號,返回待進貨商品資料
	@ResponseBody
	@RequestMapping("/get/item/from/purchase/order")
	public ResultEntity<List<Commodity>> getItemFromPurchaseOrder(@RequestParam("purchaseOreder") String purchaseOreder){
		purchaseOreder = purchaseOreder.trim().toUpperCase();
		ResultEntity<List<Commodity>> result = mysqlRemoteService.getItemFromPurchaseOrder(purchaseOreder);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 依出貨單號,返回待揀貨商品資料 
	@ResponseBody
	@RequestMapping("/get/item/from/ship/order")
	public ResultEntity<List<Commodity>> getItemFromShipOrder(@RequestParam("shipOrederNo") String shipOrederNo){
		shipOrederNo = shipOrederNo.trim().toUpperCase();
		ResultEntity<List<Commodity>> result = mysqlRemoteService.getItemFromShipOrder(shipOrederNo);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 單品進貨,更新庫存資料
	@ResponseBody
	@RequestMapping("/save/single/item/purchase")
	public ResultEntity<SingleProductPurchase> saveSingleItemPurchase(
			@RequestParam("itemno") String itemno,
			@RequestParam("purchaseQuantity") Integer purchaseQuantity){
		
		ResultEntity<SingleProductPurchase> result = mysqlRemoteService.saveSingleItemPurchase(itemno,purchaseQuantity);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 單品揀貨,更新庫存資料
	@ResponseBody
	@RequestMapping("/save/single/item/shipment")
	public ResultEntity<SingleProductShip> saveSingleItemShip(
			@RequestParam("itemno") String itemno,
			@RequestParam("itemCurrentStock")Integer itemCurrentStock,
			@RequestParam("shipAmount") Integer shipAmount){
		if( shipAmount > itemCurrentStock) {
			return ResultEntity.failed("出貨量大於庫存量,請確認庫存數或調整出貨量。");
		}
		
		ResultEntity<SingleProductShip> result = mysqlRemoteService.saveSingleItemShip(itemno,itemCurrentStock,shipAmount);
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 獲取今日完成進貨的品項
	@ResponseBody
	@RequestMapping("/get/today/instock/item/info")
	public ResultEntity<List<SingleProductPurchase>> getTodayInstockItemInfo(){
		
		ResultEntity<List<SingleProductPurchase>> result = mysqlRemoteService.getTodayInstockItemInfo();
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 獲取今天完成揀貨的品項
	@ResponseBody
	@RequestMapping("/get/today/shipment/item/info")
	public ResultEntity<List<SingleProductShip>> getTodayShipmentItemInfo(){
		
		ResultEntity<List<SingleProductShip>> result = mysqlRemoteService.getTodayShipmentItemInfo();
		if(ResultEntity.SUCCESS.equals(result.getResult())) {
			return ResultEntity.successWithData(result.getData());
		}else {
			return ResultEntity.failed(result.getMessage());
		}
	}
	
	// 進貨-出貨,返回待進貨/出貨單品項商品資料
	@ResponseBody
	@RequestMapping("/get/item/to/instock-shipment")
	public ResultEntity<Commodity> getItemToInstockOrShipment(@RequestParam("itemSearchVal") String itemSearchVal){
		ResultEntity<Commodity> commodity = mysqlRemoteService.getItemToInstockOrShipment(itemSearchVal);
		if(ResultEntity.SUCCESS.equals(commodity.getResult())) {
			return ResultEntity.successWithData(commodity.getData());
		}else {
			return ResultEntity.failed(commodity.getMessage());
		}
	}
	
	// 上架,為商品設定儲位
	@ResponseBody
	@RequestMapping("/save/item/position")	
	public ResultEntity<String> saveItemPosition(
			@RequestParam("itemNo") String itemNo,
			@RequestParam("area") String area,
			@RequestParam("position") String position
			) {
		itemNo = itemNo.toUpperCase();
		ResultEntity<String> resultEntity = mysqlRemoteService.saveItemPosition(itemNo,area,position);
		if(ResultEntity.SUCCESS.equals(resultEntity.getResult())) {
			return ResultEntity.successWithoutData();
		}else {
			return ResultEntity.failed(resultEntity.getMessage());
		}
		
	}
	
	// 依區域,返回空儲位給頁面
	@ResponseBody
	@RequestMapping("/get/storage/empty/spaces")
	public ResultEntity<List<String>> getStorageEmptySpaces(@RequestParam("areaVal") String areaVal){
		logger.info(areaVal);
		List<String> position = mysqlRemoteService.getStorageEmptySpaces(areaVal);
		if(position != null) {
			logger.info(position.toString());
			return ResultEntity.successWithData(position);
		}else {
			logger.info("沒有空儲位");
			return ResultEntity.failed("沒有空儲位");
		}
	}
	
	// 依商品編號獲取商品資料,返回給頁面
	@ResponseBody
	@RequestMapping("/get/item/to/up-down/page")
	public ResultEntity<Commodity> getCommodityByItemNo(
			@RequestParam("itemNo") String itemNo
			){
		itemNo = itemNo.toUpperCase().trim();
		ResultEntity<Commodity> commodityResult = mysqlRemoteService.getCommodityByItemNo(itemNo);
		// commodityResult.getResult():如果是success->表示返回的commodity,沒有儲位;若是failed->表示返回的commodity,可能尚未建檔而沒有資料,或已有儲位了
		if(ResultEntity.SUCCESS.equals(commodityResult.getResult())) {
			return ResultEntity.successWithData(commodityResult.getData());
		}else {
			return ResultEntity.failed(commodityResult.getMessage());
		}
			
	}
	
	//獲取所有商品
	@ResponseBody
	@RequestMapping("/get/page/info")
	public ResultEntity<PageInfo<Commodity>> getPageInfo(
			//使用@RequestParam註解的defaultValue屬性,指定默認值,在請求中沒有攜帶對應參數時使用默認值
			//pageNum默認值使用"1"
			@RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
			//pageSize,默認值設置"5"
			@RequestParam(value="pageSize",defaultValue = "5") Integer pageSize,
			//keyword默認值使用空字串,與sql語句配合實現兩種情況(帶參數行,不帶參數也行)
			@RequestParam(value="keyword",defaultValue = "") String keyword
			) {
		//調用Service方法,獲取PageInfo物件
		PageInfo<Commodity> pageInfo = mysqlRemoteService.getPageInfo(pageNum, pageSize,keyword);
		
		return  ResultEntity.successWithData(pageInfo);
	}
	
	// 獲取沒有儲位的商品資料
	@ResponseBody
	@RequestMapping("/get/not-up/item/info")
	public ResultEntity<PageInfo<Commodity>> getNotUpItemPageInfo(
			//使用@RequestParam註解的defaultValue屬性,指定默認值,在請求中沒有攜帶對應參數時使用默認值
			//pageNum默認值使用"1"
			@RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
			//pageSize,默認值設置"5"
			@RequestParam(value="pageSize",defaultValue = "5") Integer pageSize
			) {
		//調用Service方法,獲取PageInfo物件
		PageInfo<Commodity> pageInfo = mysqlRemoteService.getNotUpItemPageInfo(pageNum, pageSize);
		
		return  ResultEntity.successWithData(pageInfo);
	}
	
	// 獲取佔有儲位,庫存為0的所有商品資料
	@ResponseBody
	@RequestMapping("/get/seize/position/item/info")
	public ResultEntity<PageInfo<Commodity>> getSeizePositionItemPageinfo(
			@RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
			//pageSize,默認值設置"5"
			@RequestParam(value="pageSize",defaultValue = "5") Integer pageSize
			){
		//調用Service方法,獲取PageInfo物件
		PageInfo<Commodity> pageInfo = mysqlRemoteService.getSeizePositionItemPageinfo(pageNum, pageSize);
		
		return  ResultEntity.successWithData(pageInfo);
	}

	// 依itemno,獲取佔有儲位庫存為0的商品資料
	@ResponseBody
	@RequestMapping("/get/down/item")
	public ResultEntity<Commodity> getDownItem(@RequestParam("itemNo") String itemNo){
		itemNo = itemNo.toUpperCase().trim();
		ResultEntity<Commodity> commodityResult = mysqlRemoteService.getDownItemByItemNo(itemNo);
		// commodityResult.getResult():如果是success->表示返回的commodity,沒有儲位;若是failed->表示返回的commodity,可能尚未建檔而沒有資料,或已有儲位了
		if(ResultEntity.SUCCESS.equals(commodityResult.getResult())) {
			return ResultEntity.successWithData(commodityResult.getData());
			
		}else {
			return ResultEntity.failed(commodityResult.getMessage());
		}
	}
	
	// 批量下架商品,將儲位釋放出來
	@ResponseBody
	@RequestMapping("/commodity/remove/position/by/commodity/itemno/array")
	public ResultEntity<String> removePositionByCommodityItemnoArray(@RequestBody List<String> commodityItemnoList){
		for (String string : commodityItemnoList) {
		}
		Integer removePositionByCommodityItemno = mysqlRemoteService.removePositionByCommodityItemno(commodityItemnoList);
		if(removePositionByCommodityItemno > 0) {
			return ResultEntity.successWithoutData();
		}else {
			return ResultEntity.failed("下架失敗!");
		}
	}
	
	// 檢查itemno是否已使用過,未使用過,返回true;已使用:返回false
	@ResponseBody
	@RequestMapping("/check/itemno/used")
	public String checkItemnoUsed(@RequestParam("itemno") String itemno){
		itemno = itemno.toUpperCase();
		Map<String,Boolean> map = new HashMap<String, Boolean>();
		map.put("valid", true);
		ResultEntity<Commodity> result = mysqlRemoteService.checkItemnoUsed(itemno);
		// FAILED:表示itemno已存在
		if(ResultEntity.FAILED.equals(result.getResult())) {
			map.put("valid", false);
		}
		Gson gson = new Gson();
		String jsonStr = gson.toJson(map);
		return jsonStr;
	}
		
	// 獲取新商品建檔資料,並存入資料庫
	@RequestMapping("/create/newitem/data")
	public String saveCommodityBasicInfo(
			// 接收除了圖片以外的上傳資料
			CommodityDataCreateVO commodityVO,
			// 接收頭圖
			MultipartFile samplepic,
			// 用於當前操作失敗後,返回上個表單頁面時攜帶的錯誤訊息
			Map<String,String> map,
			HttpServletResponse resp) throws Exception {
		// 將樣圖存入cloudinary
		WmsUtil.savePicToCloudinary(samplepic);
		Commodity commodity = new Commodity();
		// 將自頁面獲取的資料封裝入commodity
		BeanUtils.copyProperties(commodityVO, commodity);
		commodity.setItemno(commodityVO.getItemno().toUpperCase());
		commodity.setCreatedate(WmsUtil.getCurrentDate());
		// 自網路空間(cloudinary)獲取存儲圖片訊息
		String picUrl = WmsUtil.getPicUrl(samplepic);
		if(picUrl != null) {
			commodity.setSamplePicturePath(picUrl);
		}
		// 將新建商品資料存入資料庫
		ResultEntity<Commodity> commoditySaveResult = mysqlRemoteService.insertCommodity(commodity);
		if(ResultEntity.SUCCESS.equals(commoditySaveResult.getResult())) {
			// 讓網頁跳出提示框
			resp.setContentType("text/html; charset=UTF-8"); //轉碼
		    PrintWriter out = resp.getWriter();
		    out.flush();
		    out.println("<script>");
		    out.println("alert('商品建檔成功。');");
		    out.println("history.back();");
		    out.println("</script>");
			
		}else {
			resp.setContentType("text/html; charset=UTF-8"); //轉碼
		    PrintWriter out = resp.getWriter();
		    out.flush();
		    out.println("<script>");
		    out.println("alert('商品建檔失敗。');");
		    out.println("history.back();");
		    out.println("</script>");
		}	
		return "item/item-create";
	}
	
	
}
