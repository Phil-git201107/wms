package com.chiczu.wms.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.WmsUtil;
import com.chiczu.wms.entity.po.Commodity;
import com.chiczu.wms.entity.po.CommodityExample;
import com.chiczu.wms.entity.po.CommodityExample.Criteria;
import com.chiczu.wms.entity.po.Position;
import com.chiczu.wms.entity.po.PurchaseOrder;
import com.chiczu.wms.entity.po.PurchaseOrderCommodity;
import com.chiczu.wms.entity.po.PurchaseOrderCommodityExample;
import com.chiczu.wms.entity.po.PurchaseOrderExample;
import com.chiczu.wms.entity.po.SingleProductPurchase;
import com.chiczu.wms.entity.po.SingleProductPurchaseExample;
import com.chiczu.wms.entity.po.SingleProductShip;
import com.chiczu.wms.entity.po.SingleProductShipExample;
import com.chiczu.wms.mapper.CommodityMapper;
import com.chiczu.wms.mapper.PositionMapper;
import com.chiczu.wms.mapper.PurchaseOrderCommodityMapper;
import com.chiczu.wms.mapper.PurchaseOrderMapper;
import com.chiczu.wms.mapper.SingleProductPurchaseMapper;
import com.chiczu.wms.mapper.SingleProductShipMapper;
import com.chiczu.wms.service.api.CommodityService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class CommodityServiceImpl implements CommodityService{
	
	Logger logger = LoggerFactory.getLogger(CommodityServiceImpl.class);

	@Autowired
	private CommodityMapper commodityMapper;
	@Autowired
	private PositionMapper positionMapper;
	@Autowired
	private SingleProductPurchaseMapper sppMapper;
	@Autowired
	private SingleProductShipMapper spsMapper;
	@Autowired
	private PurchaseOrderCommodityMapper poCommodityMapper;
	@Autowired
	private PurchaseOrderMapper poMapper; 
	
	@Override
	public PageInfo<Commodity> getNotUpItemPageInfo(Integer pageNum, Integer pageSize) {
		
		//1.開啟分頁功能
		PageHelper.startPage(pageNum, pageSize);
		//2.執行查詢
		List<Commodity>	commodityList = commodityMapper.selectNotUpItem();
		// 獲取commodity的倉儲位置
		String itemno;
		Position position;
		for (int i = 0; i < commodityList.size(); i++) {
			itemno = commodityList.get(i).getItemno();
			// 根據itemno,自position表獲取庫存位置position
			position = positionMapper.selectItemPosition(itemno);
			// 將position設置回commodity
			if (position != null ) {
				commodityList.get(i).setPosition(position);
			}else {
			commodityList.get(i).setPosition(new Position("","","",itemno));	
			}
		}

		//3.封裝為pageInfo物件返回
		return new PageInfo<>(commodityList);
		
	}
	
	@Override
	public PageInfo<Commodity> getPageInfo(Integer pageNum, Integer pageSize, String keyword) {
		
		List<Commodity> commodityList;
		// keyword不為null,依keyword進行查詢
		if (keyword != null) {
			//1.開啟分頁功能
			PageHelper.startPage(pageNum, pageSize);
			 //2.執行查詢
			commodityList = commodityMapper.selectCommodityByKeyword(keyword);
		}else {
			// keyword為null,則查詢所有的
			commodityList = commodityMapper.selectByExample(null);
		}
		// 獲取commodity的倉儲位置
		String itemno;
		Position position;
		for (int i = 0; i < commodityList.size(); i++) {
			itemno = commodityList.get(i).getItemno();
			// 根據itemno,自position表獲取庫存位置position
			position = positionMapper.selectItemPosition(itemno);
			// 將position設置回commodity
			if (position != null ) {
				commodityList.get(i).setPosition(position);
			}else {
			commodityList.get(i).setPosition(new Position("","","",itemno));	
			}
		}

		//3.封裝為pageInfo物件返回
		return new PageInfo<>(commodityList);
	}
	
	@Override
	public ResultEntity<Commodity> getCommodityByItemNo(String itemNo) {
		// 執行查詢
		CommodityExample example = new CommodityExample();
		// 創建Criteria物件
		Criteria criteria = example.createCriteria();
		// 封裝查詢條件
		criteria.andItemnoEqualTo(itemNo);
		// 執行查詢
		List<Commodity> list = commodityMapper.selectByExample(example);
		// 獲取查詢結果
		if(list.isEmpty()) {
			return ResultEntity.failed("沒有您查詢商品編號為"+itemNo+"的商品");		
		}else {
			return ResultEntity.successWithData(list.get(0));
		}
		
	}

	@Override
	public PageInfo<Commodity> getSeizePositionItemPageinfo(Integer pageNum, Integer pageSize) {

		//1.開啟分頁功能,startPage()下,必須是要做分頁的list
		PageHelper.startPage(pageNum, pageSize);
		//2.執行查詢
		List<Commodity>	commodityList = commodityMapper.selectSeizePositionZeroStockItem();
		// 獲取commodity的倉儲位置
		String itemno;
		Position position;
		for (int i = 0; i < commodityList.size(); i++) {
			itemno = commodityList.get(i).getItemno();
			// 根據itemno,自position表獲取庫存位置position
			position = positionMapper.selectItemPosition(itemno);
			// 將position設置回commodity
			if (position != null ) {
				commodityList.get(i).setPosition(position);
			}else {
			commodityList.get(i).setPosition(new Position("","","",itemno));	
			}
		}

		//3.封裝為pageInfo物件返回
		return new PageInfo<>(commodityList);
	}

	@Override
	public ResultEntity<Commodity> getDownItemByItemNo(String itemNo) {
		List<Commodity> list = commodityMapper.selectCommodityZeroStockByItemNo(itemNo);
		// 獲取查詢結果,list為空,表示沒有返回商品;不為空,表示有返回商品
		if(list.isEmpty()) {
			return ResultEntity.failed("沒有您查詢商品編號為"+itemNo+"的商品");		
		}else {
			return ResultEntity.successWithData(list.get(0));
		}
	}

	@Override
	public ResultEntity<Commodity> insertCommodity(Commodity commodity) {
		logger.info("CommodityServiceImpl commodity: "+commodity.toString());
		int insertCount = commodityMapper.insertSelective(commodity);
		if(insertCount > 0) {
			return ResultEntity.successWithoutData();
		}else {
			return ResultEntity.failed("因不明原因建檔失敗,請重新建檔!");
		}
	}

	@Override
	public ResultEntity<Commodity> checkItemnoUsed(String itemno) {
		CommodityExample example = new CommodityExample();
		Criteria criteria = example.createCriteria();
		criteria.andItemnoEqualTo(itemno);
		List<Commodity> list = commodityMapper.selectByExample(example);
		if(!(list.size() > 0)) {
			return ResultEntity.successWithoutData();
		}else {
			return ResultEntity.failed("商品編號: "+itemno+"已存在,商品名稱為: "+list.get(0).getName());
		}
	}

	@Override
	public ResultEntity<Commodity> getCommodityBySearchVal(String itemSearchVal) {
		List<Commodity> list = commodityMapper.selectCommodityByKeyword(itemSearchVal);
		if(list.size() > 0) {
			Commodity commodity = list.get(0);
			Position itemPosition = positionMapper.selectItemPosition(commodity.getItemno());
			commodity.setPosition(itemPosition);
			return ResultEntity.successWithData(commodity);
		}else {
			return ResultEntity.failed("沒有您查詢: "+itemSearchVal+"的商品資料!");
		}
	}

	@Override
	public ResultEntity<SingleProductPurchase> saveSingleItemPurchase(String itemno, Integer purchaseAmount) {
		// 獲取進貨商品
		List<Commodity> list = commodityMapper.selectCommodityByItemNo(itemno);
		Commodity commodity = list.get(0);
		// 獲得進貨商品原來庫存
		Integer inventoryOld = commodity.getInventory();
		// 將進貨量加入原來庫存量後存回資料庫
		Integer inventoryNew = inventoryOld + purchaseAmount;
		int updateInventory = commodityMapper.updateInventoryByItemno(itemno,inventoryNew);
		if(updateInventory > 0) {
			// 封裝資料入商品進貨類SingleProductPurchase返回給頁面
			// 先確認該品項當天是第幾筆輸入進貨量
			SingleProductPurchaseExample sppeExample = new SingleProductPurchaseExample();
			com.chiczu.wms.entity.po.SingleProductPurchaseExample.Criteria sppeCriteria = sppeExample.createCriteria();
			sppeCriteria.andItemnoEqualTo(itemno).andCreatedateEqualTo(WmsUtil.getCurrentDate());
			List<SingleProductPurchase> sppeSelectList = sppMapper.selectByExample(sppeExample);
			SingleProductPurchase spp = new SingleProductPurchase();
			if(sppeSelectList.size() > 0) {
				// 根據itemno與createdate,獲取當天最大的count(即當天輸入進貨量第?筆)
				int maxCount = sppMapper.selectMaxCount(itemno,WmsUtil.getCurrentDate());
				// 將當天第?筆進貨封裝入商品進貨類
				spp.setCount(maxCount+1);
			}else {
				spp.setCount(1);
			}
			// 將其他資料封裝入商品進貨類
			spp.setCategory(commodity.getCategory());
			spp.setItemno(itemno);
			spp.setName(commodity.getName());
			spp.setPurchaseAmount(purchaseAmount);
			spp.setInventory(inventoryNew);
			spp.setCreatedate(WmsUtil.getCurrentDate());
			// 將商品資料類存入資料庫
			int insert = sppMapper.insert(spp);
			if(insert > 0) {
				return ResultEntity.successWithData(spp);
			}else {
				return ResultEntity.failed("存入進貨檔階段失敗!");
			}
		}else {
			return ResultEntity.failed("更新庫存檔階段失敗!");
		}
					
	}
	
	@Override
	public ResultEntity<SingleProductShip> saveSingleItemShip(String itemno, Integer itemCurrentStock,
			Integer shipAmount) {
		// 獲取出貨商品資料
		List<Commodity> list = commodityMapper.selectCommodityByItemNo(itemno);
		Commodity commodity = list.get(0);
		// 將庫存量減去出貨量後,更新出或商品庫存數
		Integer inventoryNew = itemCurrentStock - shipAmount;
		int updateInventory = commodityMapper.updateInventoryByItemno(itemno,inventoryNew);
		if(updateInventory > 0) {
			// 封裝資料入商品出貨類SingleProductShip返回給頁面
			// 先確認該品項當天是第幾筆輸入出貨量
			SingleProductShipExample spsExample = new SingleProductShipExample();
			com.chiczu.wms.entity.po.SingleProductShipExample.Criteria spsCriteria = spsExample.createCriteria();
			spsCriteria.andItemnoEqualTo(itemno).andShipdateEqualTo(WmsUtil.getCurrentDate());
			List<SingleProductShip> spsSelectList = spsMapper.selectByExample(spsExample);
			SingleProductShip sps = new SingleProductShip();
			if(spsSelectList.size() > 0) {
				// 根據itemno與spsdate,獲取當天最大的count(即當天輸入進貨量第?筆)
				int maxCount = spsMapper.selectMaxCount(itemno,WmsUtil.getCurrentDate());
				// 將當天第?筆進貨封裝入商品進貨類
				sps.setCount(maxCount+1);
			}else {
				sps.setCount(1);
			}
			// 將其他資料封裝入商品進貨類
			sps.setCategory(commodity.getCategory());
			sps.setItemno(itemno);
			sps.setName(commodity.getName());
			sps.setShipAmount(shipAmount);
			sps.setInventory(inventoryNew);
			sps.setShipdate(WmsUtil.getCurrentDate());
			// 將商品資料類存入資料庫
			int insert = spsMapper.insert(sps);
			if(insert > 0) {
				return ResultEntity.successWithData(sps);
			}else {
				return ResultEntity.failed("存入單一商品出貨檔資料失敗!");
			}
		}else {
			return ResultEntity.failed("更新商品類庫存檔資料失敗!");
		}
	}

	@Override
	public ResultEntity<List<SingleProductPurchase>> getTodayInstockItemInfo() {
		logger.info("CommodityServiceImpl-getTodayInstockItemInfo come in");
		SingleProductPurchaseExample sppExample = new SingleProductPurchaseExample();
		com.chiczu.wms.entity.po.SingleProductPurchaseExample.Criteria sppCriteria = sppExample.createCriteria();
		sppCriteria.andCreatedateEqualTo(WmsUtil.getCurrentDate());
		List<SingleProductPurchase> list = sppMapper.selectByExample(sppExample);
		for (SingleProductPurchase singleProductPurchase : list) {
			logger.info(""+singleProductPurchase.getInventory());
		}
		if(list.size() > 0) {
			return ResultEntity.successWithData(list);
		}else {
			return ResultEntity.failed("今日尚無完成任何商品進貨");
		}
	}
	
	@Override
	public ResultEntity<List<SingleProductShip>> getTodayShipmentItemInfo() {
		logger.info("CommodityServiceImpl-getTodayShipmentItemInfo come in");
		SingleProductShipExample spsExample = new SingleProductShipExample();
		com.chiczu.wms.entity.po.SingleProductShipExample.Criteria spsCriteria = spsExample.createCriteria();
		spsCriteria.andShipdateEqualTo(WmsUtil.getCurrentDate());
		List<SingleProductShip> list = spsMapper.selectByExample(spsExample);
		for (SingleProductShip singleProductShip : list) {
			logger.info(""+singleProductShip.getInventory());
		}
		if(list.size() > 0) {
			return ResultEntity.successWithData(list);
		}else {
			return ResultEntity.failed("今日尚無完成任何商品出貨。");
		}
	}

	@Override
	public ResultEntity<List<Commodity>> getItemFromPurchaseOrder(String purchaseOreder) {
		// 確認是否有這筆訂單
		PurchaseOrder po = poMapper.selectByPrimaryKey(purchaseOreder);
		if(po != null) {
			// 有這筆訂單,確認這筆訂單是否已完成進貨fulfill=0,表示完成進貨;fulfill=1,表示未完成進貨
			String fulfill = po.getFulfill();
			if("1".equals(fulfill)) {
				List<Commodity> list = commodityMapper.selectCommodityByPurchaseOrder(purchaseOreder);
				return ResultEntity.successWithData(list);
			}else {
				PurchaseOrderCommodityExample example = new PurchaseOrderCommodityExample();
				com.chiczu.wms.entity.po.PurchaseOrderCommodityExample.Criteria criteria = example.createCriteria();
				criteria.andOrderidEqualTo(purchaseOreder);
				int countByExample = poCommodityMapper.countByExample(example);
				return ResultEntity.failed(purchaseOreder+" 進貨單,共有 "+countByExample+" 個品項,已全數完成進貨!");
			}
		}else {
			return ResultEntity.failed("查無此進貨單號資料。");
		}
		
		
	}

	@Override
	public ResultEntity<List<PurchaseOrderCommodity>> savePurchaseOrderItem(String purchaseOrederNo,String itemno, Integer purchaseQuantity) {
		// 獲取原來商品
		List<Commodity> list = commodityMapper.selectCommodityByItemNo(itemno);
		Commodity commodity = list.get(0);
		// 獲得原來商品庫存
		Integer inventoryOld = commodity.getInventory();
		// 將進貨量加入原來庫存量後存回資料庫
		Integer inventoryNew = inventoryOld + purchaseQuantity;
		int updateInventory = commodityMapper.updateInventoryByItemno(itemno,inventoryNew);
		if(updateInventory > 0) {
			// 依進貨單號與商品編號,更新資料庫PurchaseOrderCommodity表的進貨數與完成狀態
			int updatePOCommodityCount = poCommodityMapper.updatePOCommodityByItemnoAndOrderId(purchaseOrederNo, itemno,purchaseQuantity);	
		}else {
			return ResultEntity.failed(itemno+" 商品,進貨存檔失敗,請重新作業");
		}
		// 判斷當前進貨單所有品項是否都已完成進貨,若是,修改purchaseOrder完成狀態
		PurchaseOrderCommodityExample example = new PurchaseOrderCommodityExample();
		com.chiczu.wms.entity.po.PurchaseOrderCommodityExample.Criteria criteria = example.createCriteria();
		criteria.andOrderidEqualTo(purchaseOrederNo).andFulfillEqualTo("1");
		List<PurchaseOrderCommodity> purchaseOrderCommodityList = poCommodityMapper.selectByExample(example);
		// 若list2.size()>0,表當前進貨單仍有未完成進貨的品項;若否,則表示所有品項皆已完成進貨,需將資料庫進貨單purchaseOrder表調整完成狀態為0,並記錄完成日期
		if(!(purchaseOrderCommodityList.size() > 0 || purchaseOrderCommodityList == null)) {
			// 獲取進貨單類物件
			String fulfilldate = WmsUtil.getCurrentDate();
			int updatePOFulfill = poMapper.updateFulfillState(purchaseOrederNo,fulfilldate);
		}			
		return ResultEntity.successWithoutData();
		
	}

	

	

	
}
