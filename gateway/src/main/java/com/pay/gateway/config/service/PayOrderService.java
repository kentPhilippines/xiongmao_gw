package com.pay.gateway.config.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay.gateway.api.MyDealContorller;
import com.pay.gateway.config.common.Common;
import com.pay.gateway.entity.Account;
import com.pay.gateway.entity.AccountFee;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.entity.OrderAll;
import com.pay.gateway.service.OrderService;
import com.pay.gateway.util.DealNumber;

import cn.hutool.core.util.StrUtil;

/**
 *	<p>交易订单操作类</p>
 * @author ADMIN
 *
 */
public abstract class PayOrderService implements PayService{
	Logger log = LoggerFactory.getLogger(PayOrderService.class);
	@Autowired
	OrderService orderServiceImpl;
	protected String tradeId;//外部订单号
	protected BigDecimal amount;//交易金额
	protected String appid;//商户号
	protected Integer orderType;//订单类型
	protected String orderGenerationIp;//外部ip
	protected String dealCardId;//交易银行
	protected String dealChannel;//交易渠道
	protected String payType;//交易渠道
	protected OrderAll orderAll;//全局订单
	protected AccountFee accountFee;//商户手续费
	protected String notfty;//订单回调地址
	protected String accountFeeId;//費率id
	
	
	
	
	
	/**
	 * <p>生成订单逻辑</p>
	 * <li>1,</li>
	 * @return
	 */
	public Boolean dealOrder() {
		log.info("=========【订单生成抽象类开始执行订单成生方法：生成订单的各个参数为：外部订单号："+tradeId+"，交易金额："+amount+"，全局订单："+orderAll.getOrderId()+"】===========");
		DealOrder dealOrder = new DealOrder();
		String fee = accountFee.getAccountFee();
		BigDecimal dealFee = amount.multiply(new BigDecimal(fee));//交易金额 乘 交易费率 = 手续费
		dealOrder.setOrderId(DealNumber.GetDealOrder());
		dealOrder.setAssociatedId(orderAll.getOrderId());
		dealOrder.setOrderStatus(Common.ORDERDEASTATUS_T);//订单处理中
		dealOrder.setDealAmount(amount);//交易金额
		dealOrder.setDealFee(dealFee);//交易手续费
		dealOrder.setActualAmount(amount.subtract(dealFee));//实际到账金额       交易金额减掉扣去手续费
		dealOrder.setOrderType(Common.ORDERDEALTYP_DEAL);//订单为交易订单类型
		dealOrder.setOrderAccount(appid);
		dealOrder.setExternalOrderId(tradeId);
		dealOrder.setOrderGenerationIp(orderGenerationIp);
		dealOrder.setRetain2(accountFeeId);
		dealOrder.setRetain4(payType);
		if(StrUtil.isNotBlank(dealCardId))//只有使用自己渠道的时候该值才存在
			dealOrder.setDealCardId(dealCardId);
		dealOrder.setDealChannel(dealChannel);
		dealOrder.setRetain1(notfty);
		log.info("---【订单填充后的数据："+dealOrder.toString()+"】------");
		Boolean  falg = orderServiceImpl.addDealOrder(dealOrder);
		return falg;
	}
}
