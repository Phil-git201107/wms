package com.chiczu.wms.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chiczu.wms.ResultEntity;
import com.chiczu.wms.WmsUtil;
import com.chiczu.wms.entity.po.PurchaseOrder;
import com.chiczu.wms.entity.po.PurchaseOrderCommodity;
import com.chiczu.wms.entity.po.PurchaseOrderCommodityExample;
import com.chiczu.wms.entity.po.PurchaseOrderCommodityExample.Criteria;
import com.chiczu.wms.mapper.PurchaseOrderCommodityMapper;
import com.chiczu.wms.mapper.PurchaseOrderMapper;
import com.chiczu.wms.service.api.PurchaseOrderCommodityService;

@Service
public class PurchaseOrderCommodityServiceImpl implements PurchaseOrderCommodityService{
	
	Logger logger = LoggerFactory.getLogger(PurchaseOrderCommodityServiceImpl.class);
	
	@Autowired
	private PurchaseOrderCommodityMapper poCommodityMapper;
	@Autowired
	private PurchaseOrderMapper poMapper;
	
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

	@Override
	public List<PurchaseOrderCommodity> saveUploadPurchaseOrder(MultipartFile uploadPurchaseOrder) {
		
		String originalName = uploadPurchaseOrder.getOriginalFilename();
		String name = originalName.substring(0,originalName.lastIndexOf("."));
		List<PurchaseOrderCommodity> poCommodityList = new ArrayList<>();
        try {
        	String contentType = uploadPurchaseOrder.getContentType();
        	logger.info("contentType: "+contentType);
        	InputStream is = uploadPurchaseOrder.getInputStream();
        	InputStreamReader iseader = new InputStreamReader(is);
        	BufferedReader reader =new BufferedReader(iseader);
            reader.readLine();//第一行資料，為表格第一行，不用,如果需要，注釋掉
            String line = null;
            while ((line = reader.readLine()) != null) {
                PurchaseOrderCommodity poCommodity=new PurchaseOrderCommodity();
                String item[] = line.split(",");// CSV格式文件為逗號分隔符文件，這裡根據逗號切分
                poCommodity.setOrderid(name);
                poCommodity.setCategory(item[0]);
                poCommodity.setItemno(item[1]);
                poCommodity.setName(item[2]);
                poCommodity.setFulfill("1");

                poCommodityList.add(poCommodity);
            }
            logger.info("csv資料:"+poCommodityList);
        } catch (Exception e) {
            e.printStackTrace();
        }
		// 將上傳資料存入資料庫 purchaseOrder、purchaseOrderCommodity
        
        PurchaseOrder po = new PurchaseOrder();
        po.setOrderid(name);
        po.setPurchasedate(WmsUtil.getCurrentDate());
        po.setFulfill("1");
        poMapper.insertSelective(po);
        poCommodityMapper.insertPurchaseOrderCommodityList(poCommodityList);
        // 將上傳的金貨單資料返回給頁面
        PurchaseOrderCommodityExample example = new PurchaseOrderCommodityExample();
        Criteria criteria = example.createCriteria();
        criteria.andOrderidEqualTo(name);
        List<PurchaseOrderCommodity> list = poCommodityMapper.selectByExample(example);
        
		return list;
	}

}
