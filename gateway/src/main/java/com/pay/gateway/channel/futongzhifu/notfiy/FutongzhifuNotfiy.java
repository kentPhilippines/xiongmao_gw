package com.pay.gateway.channel.futongzhifu.notfiy;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.pay.gateway.service.OrderService;
import com.pay.gateway.util.NotifyUtil;
import com.pay.gateway.util.OrderUtil;

import cn.hutool.core.util.StrUtil;

@Controller("/channel")
public class FutongzhifuNotfiy {
	Logger log = LoggerFactory.getLogger(FutongzhifuNotfiy.class);
	@Autowired
	OrderService orderServiceImpl;
	
	@Resource
	OrderUtil orderUtil;
	@Resource
	NotifyUtil notifyUtil;
	@PostMapping("/futongzhifuNotfiy")
	public void futongzhifuNotfiy(HttpServletRequest request ,HttpServletResponse response) {
		log.info("【进入富通支付回调处理】");
		/*
		 * 	交易结果 				status 			是 				String(10)					0: 未支付 1: 支付成功 2: 支付失败 3: 预支付成功 4: 支付处理中
			商户号					merAccount 		是 				String(32) 					商户号
			返回消息 				returnMessage 	是 				String(100)					商户交易信息
			商户订单号				merOrderId 		是 				String(32)					商户订单号，32个字符内、只允许 使用数字、字母，确保在商户系统唯 一
			通知类型 				notifyType 		是 				String(10)					
			支付金额 				amount 			是 				String(14)					支付金额，必须大于0，包含2位小数 如：amount=1.00
			交易时间 				orderTime 		是 				String(14)					用户通过商户网站提交订单的时间， 格式为:yyyy-MM-dd HH:mm:ss
			支付时间 				payTime 		是 				String(14)					用户通过商户网站支付订单的时间， 格式为:yyyy-MM-dd HH:mm:ss
			支付订单号 				orderId 		是 				String(40)					富通支付系统的交易号，商户只需记 录10
			签名类型 				signType 		是 				String(10) 					签名类型，如：MD5
			签名串 					sign 			是 				String(14) 					签名结
		 */
		String status = request.getParameter("status");
		String merAccount = request.getParameter("merAccount");
		String returnMessage = request.getParameter("returnMessage");
		String merOrderId = request.getParameter("merOrderId");
		String notifyType = request.getParameter("notifyType");
		String amount = request.getParameter("amount");
		String orderTime = request.getParameter("orderTime");
		String payTime = request.getParameter("payTime");
		String orderId = request.getParameter("orderId");
		String signType = request.getParameter("signType");
		String sign = request.getParameter("sign");
		if(StrUtil.isBlank(status)||StrUtil.isBlank(merAccount)
				||StrUtil.isBlank(returnMessage)||StrUtil.isBlank(merOrderId)||StrUtil.isBlank(notifyType)
				||StrUtil.isBlank(amount)||StrUtil.isBlank(orderTime)||StrUtil.isBlank(payTime)||StrUtil.isBlank(orderId)
				||StrUtil.isBlank(signType)||StrUtil.isBlank(sign)) {
				log.info("当前接受富通支付回调参数，必传参数为空");
			return ;}
			
		
		
		
		
		
	}

}
