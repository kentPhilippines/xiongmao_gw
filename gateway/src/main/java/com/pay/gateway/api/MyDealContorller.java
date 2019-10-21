package com.pay.gateway.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pay.gateway.config.common.Common;
import com.pay.gateway.config.service.FactoryForStrategy;
import com.pay.gateway.entity.Account;
import com.pay.gateway.entity.AccountFee;
import com.pay.gateway.entity.ChannelFee;
import com.pay.gateway.entity.OrderAll;
import com.pay.gateway.entity.dealEntity.Deal;
import com.pay.gateway.entity.dealEntity.ResultDeal;
import com.pay.gateway.service.AccountFeeService;
import com.pay.gateway.service.AccountService;
import com.pay.gateway.service.ChannelService;
import com.pay.gateway.service.OrderService;
import com.pay.gateway.util.DealNumber;
import com.pay.gateway.util.IpUtil;
import com.pay.gateway.util.RequestUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

/**
 * <p>用户交易</p>
 * @author K
 *<li>1,当前交易是否超额</li>
 *<li>2,查询当前费率是否存在</li>
 *<li>3,根据费率计算利润</li>
 *<li>4,生成订单和相应订单数据</li>
 *<li>5,填充用户数据修改用户金额</li>
 *<li>6,请求上游渠道</li>
 */
@Controller
@RequestMapping("/deal")
public class MyDealContorller {
	@Autowired
	FactoryForStrategy factoryForStrategy;
	@Autowired
	AccountService accountServiceImpl;
	@Autowired
	AccountFeeService accountFeeServiceImpl;
	@Autowired
	ChannelService channelServiceImpl;
	@Autowired
	OrderService orderServiceImpl;
	@Autowired
	RequestUtil requestUtil;
	Logger log = LoggerFactory.getLogger(MyDealContorller.class);
	@RequestMapping("/payTo")
	@Transactional
	public void payToCard(Deal deal ,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultDeal resultDeal = new ResultDeal();
		boolean validationAll = requestUtil.validationAll(request, response,resultDeal);
		if(!validationAll)  
			return; 
		log.info("--------------【进入交易处理】------------------------");
		 String appid = request.getParameter("appid");
		String orderid = request.getParameter("orderid");
		String notifyurl = request.getParameter("notifyurl");
		String passcode = request.getParameter("passcode");
		String callbackurl = request.getParameter("callbackurl");
		String amount1 = request.getParameter("amount");
		log.info("-------------【请求参数：appid："+appid+"，orderid："+orderid+"，notifyurl："+notifyurl+"，passcode："+passcode+"，amount："+amount1+"】------------------------------");
		if(StrUtil.isBlank(appid) || StrUtil.isBlank(orderid) ||StrUtil.isBlank(notifyurl) ||StrUtil.isBlank(passcode)  || StrUtil.isBlank(amount1)) {
			log.info("------------------------------【必传参数为空】------------------------------");
			resultDeal.setCod(Common.COD_15034);
			resultDeal.setMsg(Common.MSG_15034);
			response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			return;
		}
		Account account = accountServiceImpl.findAccountByAppId(appid);
		OrderAll order = orderServiceImpl.findOrderByTradeId(appid,orderid);
		if(ObjectUtil.isNotNull(order)) {
			log.info("------------------------------【14004:外部商户订单号重复】------------------------------");
			resultDeal.setCod(Common.COD_14006);
			resultDeal.setMsg(Common.MSG_14006);
			response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			return;
		}
		BigDecimal dayDealAmountMax = account.getDayDealAmountMax();//交易最大金额区间
		BigDecimal dayDealAmountMin = account.getDayDealAmountMin();//交易最小金额区间
		BigDecimal maxDeal = account.getMaxDeal();
		BigDecimal sumDealToDayAmount = account.getSumDealToDayAmount();//当天累计交易额度
		BigDecimal amount = new BigDecimal(amount1);
		amount = amount.divide(new BigDecimal(100));
		if(amount.compareTo(dayDealAmountMax) == 1) {
			log.info("------------------------------【14001:当前交易额度超过该账号最大交易额度，最大交易额度为："+dayDealAmountMax+"，当前交易额为："+amount+"】------------------------------");
			resultDeal.setCod(Common.COD_14001);
			resultDeal.setMsg(Common.MSG_14001);
			response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			return;
		}
		if(amount.compareTo(dayDealAmountMin) == -1 ) {
			log.info("------------------------------【14002:当前交易额度小于该账号最小交易额度 ，最小交易额度为："+dayDealAmountMin+"，当前传入金额为："+amount+"】------------------------------");
			resultDeal.setCod(Common.COD_14002);
			resultDeal.setMsg(Common.MSG_14002);
			response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			return;
		}
		BigDecimal amountSum = amount.add(sumDealToDayAmount);
		if(amountSum.compareTo(maxDeal) == 1 ) {
			log.info("------------------------------【14003:当日交易累计已超过该账号最大交易额度，当日累计交易："+sumDealToDayAmount+"，交易额度："+maxDeal+"，当前交易金额："+amount1+"分】------------------------------");
			resultDeal.setCod(Common.COD_14003);
			resultDeal.setMsg("当日交易累计已超过该账号最大交易额度，当日累计交易："+amountSum+"，交易额度："+maxDeal+"，当前交易金额："+amount1+"分");
			response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			return;
		}
		List<AccountFee>  accountFeeList = accountFeeServiceImpl.findAccountFeeBy(appid,passcode,Common.FEE_STATUS1);//理论上可以查询到一条费率状态
		if(CollUtil.isEmpty(accountFeeList)) {
			log.info("------------------------------【15001:当前商户账户费率未配置或费率状态未开通】------------------------------");
			resultDeal.setCod(Common.COD_15001);
			resultDeal.setMsg(Common.MSG_15001);
			response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			return;
		}
		AccountFee accountFee = CollUtil.getFirst(accountFeeList);	
		log.info("---------------------------------【当前费率为:"+accountFee.getAccountFee()+"，当前费率成本为："+accountFee.getAccountCost()+"】---------------------------------");
		log.info("---------------------------------【当前渠道编号为:"+accountFee.getAccountChannel()+"，当前产品类型编号为："+accountFee.getChannelProduct()+"】---------------------------------");
		  String channel = ""; 
		  ChannelFee channelFee = channelServiceImpl.findChanenlFeeByChannelNo(accountFee.getAccountChannel(), accountFee.getChannelProduct()); 
		  channel = channelFee.getRouting();
		  if(StrUtil.isBlank(channel)) { 
			  log.info("------------------------------【15002:当前商户账户渠道路由未开通】------------------------------"); 
			  response.getWriter().write("15002:当前商户账户渠道路由未开通"); 
			  return; 
			 }
			String ipAddr = IpUtil.getIpAddr(request);
			deal.setAmount(amount.toString());
			deal.setAppid(appid);
			deal.setOrderid(orderid);
			deal.setIp(ipAddr);
			OrderAll orderAll =  new OrderAll();
			orderAll.setOrderId(DealNumber.GetAllOrder());
			orderAll.setOrderAccount(appid);
			orderAll.setOrderAmount(amount.toString());//目前这里的金额就是用户提交的金额 和我们实际支付的金额是不同的,但是我们用订单号相关联就不会存在金额不一样的问题
			orderAll.setOrderIp(IpUtil.getIpAddr(request));
			orderAll.setOrderType(Common.BANKORDERALL_DEAL);
			orderAll.setRetain1(orderid);
			orderAll.setRetain2(accountFee.getId().toString());
			orderAll.setRetain3(notifyurl);//回调
			orderAll.setRetain4(accountFee.getId().toString());
			Boolean flag = orderServiceImpl.addOrderAll(orderAll);
			if(!flag) {
				log.info("------------------------------【14005:全局订单生成异常】------------------------------");
				resultDeal.setCod(Common.COD_14005);
				resultDeal.setMsg(Common.MSG_14005);
				response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
				return;
			}
			log.info("------------------------------【全局订单生成成功，订单详情为："+orderAll.toString()+"】------------------------------");
			deal.setOrderNo(orderAll.getOrderId());
			log.info("【交易路由id为："+channel+"】");
			resultDeal = factoryForStrategy.getStrategy(channel).deal(deal,account,accountFee,orderAll);
			log.info("状态响应码："+resultDeal.toString());
			if(!Common.RESPONSE_STATUS_SU.equalsIgnoreCase(resultDeal.getCod())) {
				log.info("------------------------------【14004:交易失败"+resultDeal.getMsg()+"】------------------------------");
				response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
				return;
			}
			log.info("------------------------------【转发到："+resultDeal.getReturnUrl()+"】------------------------------");
			response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			//	request.getRequestDispatcher(resultDeal.getReturnUrl()).forward(request, response);//自己的二维码信息到订单信息里查找
	}
}
	

