package com.pay.gateway.api;

import java.math.BigDecimal;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.gateway.channel.H5ailiPay.util.BankUtil;
import com.pay.gateway.config.common.Common;
import com.pay.gateway.config.exception.OtherErrors;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.service.OrderService;
import com.pay.gateway.util.IpUtil;
import com.pay.gateway.util.JsonResult;
import com.pay.gateway.util.NotifyUtil;
import com.pay.gateway.util.OrderUtil;
import com.pay.gateway.util.SendUtil;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.lettuce.core.dynamic.annotation.Param;

@Controller
@RequestMapping("/notify")
public class OrderContorller {
	Logger log = LoggerFactory.getLogger(OrderContorller.class);
	@Autowired
	OrderUtil orderUtil;
	@Autowired
	NotifyUtil notifyUtil;
	@Autowired
	OrderService orderServiceImpl;
	@Resource
	BankUtil bankUtil;
	@Autowired
	SendUtil sendUtil;
	/**
	 * <p>单纯向下游发送现有订单的通知</p>
	 * @param OrderId		订单号
	 */
	@PostMapping("/notifyOrder")	
	@Transactional
	@ResponseBody
	public JsonResult notifyOrder(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, String> decryptionParam = null;
		try {
			decryptionParam = sendUtil.decryptionParam(request);
		} catch (Exception e) {
			return JsonResult.buildFailResult("请求参数解密异常");
		}
		String orderId = decryptionParam.get("orderId");
		log.info("|----收到后台向网关调用网关向用户发送通知的方法，订单号为："+orderId);
		if(StrUtil.isBlank(orderId)) {
			return JsonResult.buildFailResult("订单号为空");
		}
		DealOrder dealOrder = orderServiceImpl.findOrderByOrderId(orderId);
		if(ObjectUtil.isNull(dealOrder)) {
			return JsonResult.buildFailResult("服务器内未查询到有效订单");
		}
		Integer orderStatus = dealOrder.getOrderStatus();
		if(Common.ORDERDEASTATUS_SU.equals(orderStatus)) {//成功的时候
			notifyUtil.sendMsg(dealOrder.getAssociatedId(), true);
			return JsonResult.buildSuccessMessage("订单通知补发成功");
		}
		notifyUtil.sendMsg(dealOrder.getAssociatedId(), false);
		return JsonResult.buildSuccessMessage("订单通知补发成功");
	}
	/**
	 * <p>修改订单为成功,并生成流水</p>
	 * @param orderId
	 * @return
	 */
	@PostMapping("/updataOrder")	
	@ResponseBody
	@Transactional
	public JsonResult updataOrder(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, String> decryptionParam = null;
		StringBuffer requestURL = request.getRequestURL();
		log.info("【收到后台调用修改订单请求，具体请求为：】"+requestURL.toString()+"");
		String ipAddr = IpUtil.getIpAddr(request);
		log.info("请求方ip地址为："+ipAddr);
		try {
			decryptionParam = sendUtil.decryptionParam(request);
		} catch (Exception e) {
			return JsonResult.buildFailResult("请求参数解密异常");
		}
		String orderId = decryptionParam.get("orderId");
		log.info("|----收到后台向网关调用网关将订单置为成功的方法并向下游用户重新发送通知，订单号为："+orderId);
		if(StrUtil.isBlank(orderId)) {
			return JsonResult.buildFailResult("订单号为空");
		}
		DealOrder dealOrder = orderServiceImpl.findOrderByOrderId(orderId);
		if(ObjectUtil.isNull(dealOrder)) {
			return JsonResult.buildFailResult("服务器内未查询到有效订单");
		}
		boolean updataOrderStatus = orderUtil.updataOrderStatus(dealOrder.getAssociatedId(),Common.RUN_STATUS_2);
		log.info("|-----------接收到后台请求修改订单结果："+updataOrderStatus);
		notifyUtil.sendMsg(dealOrder.getAssociatedId(), updataOrderStatus);
		log.info("|-----------接收到后台请求修改订单结果："+updataOrderStatus);
		if(updataOrderStatus) {
			return JsonResult.buildSuccessMessage("订单修改成功,并向用户发送通知");
		}
		if(!updataOrderStatus)//回滚所有数据
			 throw new OtherErrors("交易回调发生异常");
		return JsonResult.buildFailResult("修改失败");
	}
	
	
	
	/**
	 * <p>给回调接口未作处理</p>
	 * <p>暂时无用</p>
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping("/mms")	
	@ResponseBody
	public JsonResult mms(HttpServletRequest request, HttpServletResponse response) { 
		log.info("【收到回调服务短信回调通知】");
		String money = request.getParameter("amount");
		String deviceid = request.getParameter("bankPhone");
		log.info("【获取金额为："+money+"】");
		log.info("【获取物理介质唯一标识为："+deviceid+"】");
		String key = deviceid + money;
		log.info("【组合当前唯一KEY为："+key+"】");
		String orderIdAll = bankUtil.findOrderBankCard(new BigDecimal(money),deviceid);//获取全局订单	根据全局订单查询  交易订单
		log.info("=============【獲得全局訂單編號，并開始修改交易訂單狀態，全局訂單編號："+orderIdAll+"】============");
		boolean flag = orderUtil.updataOrderStatus(orderIdAll);//修改訂單狀態並生成對應的流水
		//發給下游通知回調
		notifyUtil.sendMsg(orderIdAll, flag);
		if(!flag)//回滚所有数据
			 throw new OtherErrors("交易回调发生异常");
		String message = "回调成功";
		String result =  orderIdAll;
		return JsonResult.buildSuccessResult(message, result);
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
