package com.pay.gateway.config.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay.gateway.api.MyDealContorller;
import com.pay.gateway.config.common.Common;
import com.pay.gateway.entity.Account;
import com.pay.gateway.entity.AccountFee;
import com.pay.gateway.entity.BankCard;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.entity.OrderAll;
import com.pay.gateway.entity.dealEntity.ResultDeal;
import com.pay.gateway.service.OrderService;
import com.pay.gateway.util.DealNumber;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

/**
 *	<p>交易订单操作类</p>
 * @author K
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
	protected String pageUrl;//支付成功跳转地址
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
		dealOrder.setRetain5(pageUrl);
		if(StrUtil.isNotBlank(dealCardId))//只有使用自己渠道的时候该值才存在
			dealOrder.setDealCardId(dealCardId);
		dealOrder.setDealChannel(dealChannel);
		dealOrder.setRetain1(notfty);
		log.info("---【订单填充后的数据："+dealOrder.toString()+"】------");
		Boolean  falg = orderServiceImpl.addDealOrder(dealOrder);
		return falg;
	}
	
	/**
	 * <p>创建订单</p>
	 * @param orderAll			全局订单
	 * @return
	 */
	public Boolean createOrder(OrderAll orderAll) {
		 boolean createOrderNoBankCaed = orderServiceImpl.createOrderNoBankCaed(orderAll.getOrderId(), orderAll.getOrderAmount());
		return createOrderNoBankCaed;
	}
	
	/**
	 * <p>查找订单，根据全局订单号</p>
	 * @param orderAllId			全局订单号
	 * @return
	 */
	public DealOrder findOrderByAllId(String orderAllId) {
		DealOrder findOrderByOrderAll = orderServiceImpl.findOrderByOrderAll(orderAllId);
		return findOrderByOrderAll;
	}
	
	
	/**
	 * <p>订单置为失败，并说明原因</p>
	 * @param orderId		失败订单号
	 * @param msg			原因
	 * @return
	 */
	public ResultDeal returnBean(String orderId,String msg) {
		ResultDeal result = new ResultDeal();
		result.setCod(Common.RESPONSE_STATUS_ER);
		result.setMsg(Common.RESPONSE_STATUS_ER_MSG);
		result.setRedirect("订单失败");
		Boolean flag = orderServiceImpl.updataOrderByStatusAndMsg(orderId,msg);
		log.info("订单修改成功");
		return result;
	}
	/**
	 * <p>H5接口提交返回抽象类</p>
	 * @param decodeParams
	 * @param url
	 * @return
	 */
	public StringBuffer getFrom(Map<String, List<String>> decodeParams,String url) {
		Set<String> keySet = decodeParams.keySet();
		StringBuffer buffer = new StringBuffer();
		buffer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">")
		.append("<html>")
		.append("<head>")
		.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">")
		.append("</head>")
		.append("<body>")
		.append("<form action=\""+url+"\" id=\"from\" method=\"post\" >");
		for(String key : keySet) {
			List<String> list = decodeParams.get(key);
			String first = CollUtil.getFirst(list);
			buffer.append("<input type=\"text\" name=\""+key+"\" value=\""+first+"\">");
		}
		buffer.append("<button type=\"submit\">提交</button>")
		.append("</form>")
		.append("</body>")
		.append("</html>")
		.append("<script language=javascript>")
		.append("window.onload= function(){\r\n" + 
				"document.getElementById('from').submit();\r\n" + 
				"}")
		.append("</script>");
		return buffer;
	}
	
	
	
	
}
