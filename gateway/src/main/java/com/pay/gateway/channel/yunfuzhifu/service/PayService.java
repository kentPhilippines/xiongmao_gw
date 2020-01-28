package com.pay.gateway.channel.yunfuzhifu.service;

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


@Component("yunfuzhifu")
public class PayService extends PayOrderService{
	Logger log = LoggerFactory.getLogger(PayService.class);
	public static final String CHARSET_UTF8 = "UTF-8";
	@Autowired
	SettingFile settingFile;
	@Override
	public ResultDeal deal(Deal deal, Account account, AccountFee accountFee, OrderAll orderAll) {
		log.info("===========【进入云付支付支付业务处理类】======");
		ResultDeal result = new ResultDeal();
		boolean flag = createOrder(orderAll);
		DealOrder order = null;	
		if(flag) {
			order = findOrderByAllId(orderAll.getOrderId());
			log.info("【云付支付，支付宝扫码模式交易订单创建成功，交易订单号："+order.getOrderId()+"】");
		}else {
			result.setCod(Common.RESPONSE_STATUS_ER);
			result.setMsg(Common.RESPONSE_STATUS_ER_MSG);
			result.setRedirect("订单创建失败，请联系运营人员处理");
			return result;
		}
		return Post(order);
	}
	private ResultDeal Post(DealOrder order) {
		log.info("开始请求云付上游支付渠道");
		ResultDeal result = new ResultDeal();
		/**
		 * 	字段 			字符类型				是否必填				     描述 
			sid 	    String                 	是                                            	商户号
			payType		String					是					支付类型编码
			amount		String					是					订单金额，数值类型（元）
			outTradeNo	String 					是					商户订单号
			orderType	int						是					订单类型:1-收款；2-充值
			notifyUrl	String					是					异步回调地址
			sign		String					是					签名【】
		 */
		return null;
	}

}
