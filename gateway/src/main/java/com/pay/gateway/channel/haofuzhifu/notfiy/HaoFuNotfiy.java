package com.pay.gateway.channel.haofuzhifu.notfiy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.pay.gateway.channel.haofuzhifu.util.HaofuUtil;
import com.pay.gateway.channel.haofuzhifu.util.MD5Util;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.service.OrderService;
import com.pay.gateway.util.NotifyUtil;
import com.pay.gateway.util.OrderUtil;
import com.pay.gateway.util.SettingFile;

import cn.hutool.core.util.StrUtil;

@Controller(HaofuUtil.CHANNEL)
public class HaoFuNotfiy {
	static final String STAYUS_SU = "1";
	static final String STAYUS_ER = "2";
	static final String STAYUS_YU = "0";
	Logger log = LoggerFactory.getLogger(HaoFuNotfiy.class);
	public static final String CHARSET_UTF8 = "UTF-8";
	@Autowired
	OrderService orderServiceImpl;
	@Resource
	OrderUtil orderUtil;
	@Resource
	NotifyUtil notifyUtil;
	@Autowired
	SettingFile settingFile;
	@PostMapping(HaofuUtil.NOTFIY+HaofuUtil.PAYTYPR)
	public void futongzhifuNotfiy(HttpServletRequest request ,HttpServletResponse response) {
		log.info("【进入豪富回调处理】");
		/**
			字段			长度 		是否加入验签 		注释 
			input_charset 	10 				是 				编码格式:UTF-8 
			sign_type 		3 				是 				签名方式:MD5 
			sign 			256 			否 				MD5加密串 
			request_time 	20 				是				yyyy-MM-dd HH:mm:ss 
			trade_id 		32 				是 				系统订单号 
			out_trade_no 	32 				是 				外部订单号 
			amount_str 		9 				是 				金额(单位:元,支持两位小数) 
			amount_fee 		9 				是 				手续费 
			status 			2 				是 				状态:0处理中,1完成,2失败 
			for_trade_id 	32 				是 				原始订单号(针对退款) 
			business_type 	3 				是 				交易类型 
			remark 			256 			是 				备注 
			create_time 	11 				是 				yyyy-MM-dd HH:mm:ss 
			modified_time 	11 				是 				yyyy-MM-dd HH:mm:ss 
		 */
		String input_charset = request.getParameter("input_charset");	
		String sign_type = request.getParameter("sign_type");
		String sign = request.getParameter("sign");
		String request_time = request.getParameter("request_time");
		String trade_id = request.getParameter("trade_id");
		String orderid = request.getParameter("out_trade_no");
		String amount_str = request.getParameter("amount_str");
		String amount_fee = request.getParameter("amount_fee");
		String status = request.getParameter("status");
		String for_trade_id = request.getParameter("for_trade_id");
		String business_type = request.getParameter("business_type");
		String remark = request.getParameter("remark");
		String create_time = request.getParameter("create_time");
		String modified_time = request.getParameter("modified_time");
		if(StrUtil.isBlank(input_charset)||StrUtil.isBlank(sign_type)||StrUtil.isBlank(sign)||StrUtil.isBlank(request_time)||StrUtil.isBlank(trade_id)||StrUtil.isBlank(orderid)
				||StrUtil.isBlank(amount_str)||StrUtil.isBlank(amount_fee)||StrUtil.isBlank(status)||StrUtil.isBlank(for_trade_id)
				||StrUtil.isBlank(business_type)||StrUtil.isBlank(remark)
			||StrUtil.isBlank(create_time)||StrUtil.isBlank(modified_time)) {
			log.info("收到豪富回调，必传参数为空");
			try {
				response.getWriter().write("必传参数为空");
			} catch (IOException e) {
				log.info("【异步通知：返回页面失败】");
			}
			return ;
		}
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("input_charset", input_charset);
		resultMap.put("sign_type", sign_type);
		//resultMap.put("sign", sign);
		resultMap.put("request_time",request_time);
		resultMap.put("trade_id", trade_id);
		resultMap.put("out_trade_no", orderid);
		resultMap.put("amount_str", amount_str);
		resultMap.put("amount_fee", amount_fee);
		resultMap.put("status", status);
		resultMap.put("for_trade_id", for_trade_id);
		resultMap.put("business_type", business_type);
		resultMap.put("remark", remark);
		resultMap.put("create_time", create_time);
		resultMap.put("modified_time", modified_time);
		log.info(resultMap.toString());
		String mysign = HaofuUtil.getRequestStr(resultMap) + "&" +settingFile.getName(settingFile.HAOFU_DEAL_KEY);
		mysign = MD5Util.MD5Encode(mysign,CHARSET_UTF8);
		if(!sign.equals(mysign)) {
			log.info("接受豪富回调失败，我方验签串" + mysign+"，上游验签串："+sign);
			return;
		}
		if(STAYUS_SU.equals(status)) {
			log.info("【交易成功】");
			try {
				response.getWriter().write("success");
			} catch (IOException e) {
				log.info("【异步通知：返回豪富success状态失败】");
			}
			orderUtil.orderSu(orderid);
			return;
		}
		log.info("【交易失败】");
		orderUtil.orderEr(orderid);
		return;
	}
}
