package com.pay.gateway.channel.yunzhifu.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay.gateway.config.common.Common;
import com.pay.gateway.config.service.PayOrderService;
import com.pay.gateway.entity.Account;
import com.pay.gateway.entity.AccountFee;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.entity.OrderAll;
import com.pay.gateway.entity.dealEntity.Deal;
import com.pay.gateway.entity.dealEntity.ResultDeal;
import com.pay.gateway.util.SettingFile;

@Component("yunzhifuAlipayScan")
public class PayService extends PayOrderService{
	Logger log = LoggerFactory.getLogger(PayService.class);
	public static final String CHARSET_UTF8 = "UTF-8";
	@Autowired
	SettingFile settingFile;
	@Override
	public ResultDeal deal(Deal deal, Account account, AccountFee accountFee, OrderAll orderAll) {
		log.info("===========【进入云支付支付业务处理类】======");
		ResultDeal result = new ResultDeal();
		boolean flag = createOrder(orderAll);
		DealOrder order = null;	
		if(flag) {
			order = findOrderByAllId(orderAll.getOrderId());
			log.info("【云支付，支付宝扫码模式交易订单创建成功，交易订单号："+order.getOrderId()+"】");
		}else {
			result.setCod(Common.RESPONSE_STATUS_ER);
			result.setMsg(Common.RESPONSE_STATUS_ER_MSG);
			result.setRedirect("订单创建失败，请联系运营人员处理");
			return result;
		}
		return Post(order);
	}
	private ResultDeal Post(DealOrder order) {
		log.info("开始请求云支付上游支付渠道");
		ResultDeal result = new ResultDeal();
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return returnBean(order.getOrderId(),"上游渠道返回失败，失败原因："+"上游返回为空");
	}
}
