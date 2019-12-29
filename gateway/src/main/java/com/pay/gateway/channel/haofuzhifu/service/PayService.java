package com.pay.gateway.channel.haofuzhifu.service;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay.gateway.channel.futongzhifu.util.HttpClientUtil;
import com.pay.gateway.channel.haofuzhifu.util.HaoFuResponseBean;
import com.pay.gateway.channel.haofuzhifu.util.HaofuUtil;
import com.pay.gateway.channel.haofuzhifu.util.MD5Util;
import com.pay.gateway.config.common.Common;
import com.pay.gateway.config.service.PayOrderService;
import com.pay.gateway.entity.Account;
import com.pay.gateway.entity.AccountFee;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.entity.OrderAll;
import com.pay.gateway.entity.dealEntity.Deal;
import com.pay.gateway.entity.dealEntity.ResultDeal;
import com.pay.gateway.util.SettingFile;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
@Component("haofuzhifuAlipayScan")
public class PayService extends PayOrderService{
	Logger log = LoggerFactory.getLogger(PayService.class);
	public static final String CHARSET_UTF8 = "UTF-8";
	@Autowired
	SettingFile settingFile;
	@Override
	public ResultDeal deal(Deal deal, Account account, AccountFee accountFee, OrderAll orderAll) {
		log.info("===========【进入豪富支付支付业务处理类】======");
		ResultDeal result = new ResultDeal();
		boolean flag = createOrder(orderAll);
		DealOrder order = null;	
		if(flag) {
			order = findOrderByAllId(orderAll.getOrderId());
			log.info("【豪富支付，支付宝扫码模式交易订单创建成功，交易订单号："+order.getOrderId()+"】");
		}else {
			result.setCod(Common.RESPONSE_STATUS_ER);
			result.setMsg(Common.RESPONSE_STATUS_ER_MSG);
			result.setRedirect("订单创建失败，请联系运营人员处理");
			return result;
		}
		return Post(order);
	}

	private ResultDeal Post(DealOrder order) {
		log.info("开始请求豪富上游支付渠道");
		ResultDeal result = new ResultDeal();
		/**
		 * 	字段 			长度 是否必填 是否加入验签 注释 
			partner 		32 		是 		是 		商户合作号，由平台注册提供 
			amount 			32 		是 		是 		金额(单位:元,支持两位小数) 
			request_time 	10		是 		是 		时间戳,精确到秒 
			sign 			256		是 		否 		签名字符串 
			trade_no 		32 		是 		是 		订单号 
			pay_type 		2 		是 		是 		唤醒参数:h5扫码参数: sm
			notify_url 64 是 是 异步通知地址 
			/payCenter/aliPay2
		 */
		StringBuffer keyString = new StringBuffer();
		Map<String, String> treturnMap = new TreeMap<String, String>();
		treturnMap.put("partner", settingFile.getName(settingFile.HAOFU_APPID));/*商户合作号，由平台注册提供*/
		treturnMap.put("amount", order.getDealAmount().toString());/*金额(单位:元,支持两位小数)*/
		treturnMap.put("request_time", String.valueOf(Calendar.getInstance().getTimeInMillis()));/*时间戳,精确到秒*/
		treturnMap.put("trade_no", order.getOrderId());/*订单号*/
		treturnMap.put("pay_type", HaofuUtil.H5);
		treturnMap.put("notify_url",settingFile.getName(settingFile.HAOFU_APPID)+HaofuUtil.CHANNEL+HaofuUtil.NOTFIY+HaofuUtil.PAYTYPR);
		String param = HaofuUtil.getRequestStr(treturnMap);
		String md5key = settingFile.getName(settingFile.HAOFU_DEAL_KEY);
		keyString.append(param + "&" + md5key);
		String sign = MD5Util.MD5Encode(keyString.toString(), CHARSET_UTF8);
		treturnMap.put("sign", sign);
		String paramStr = HaofuUtil.getRequestStr(treturnMap);
		String response = null;
		String alipayUrl =  HaofuUtil.HAOFU_URL;
		log.info("请求豪富支付上游渠道参数："+paramStr);
		response = HttpUtil.post(alipayUrl, paramStr, 2000);//2秒未返回则默认超时
		//{"sign":"9efa7e55c65b254962681c1018a67fc2","charset":null,"result":"https://mnapi2b.cshushu.com/api/ali/e861ee753d3b4ecdab81a3c25c208ebd","is_success":"T","fail_code":null,"fail_msg":null,"resp_time":"2019-12-29 13:56:53"}
		if(StrUtil.isNotBlank(response)) {
			HaoFuResponseBean bean = JSONUtil.toBean(response, HaoFuResponseBean.class);
			if(StrUtil.isBlank(bean.getIs_success()) || bean.getIs_success().equals(HaofuUtil.IS_SUCCESS_ER))
				return returnBean(order.getOrderId(),"上游渠道返回失败，失败原因："+bean.getFail_msg());
			result.setCod(Common.RESPONSE_STATUS_SU);
			result.setReturnUrl(bean.getResult());
			result.setOpenType(Common.OPENTYPE_URL);
			return result;
		}
		return returnBean(order.getOrderId(),"上游渠道返回失败，失败原因："+"上游返回为空");
	}
}
