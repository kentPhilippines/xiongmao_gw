package com.pay.gateway.channel.test;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay.gateway.channel.haofuzhifu.service.PayService;
import com.pay.gateway.channel.haofuzhifu.util.HaofuUtil;
import com.pay.gateway.channel.haofuzhifu.util.MD5Util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;

public class test {
	static Logger log = LoggerFactory.getLogger(test.class);
	public static final String CHARSET_UTF8 = "UTF-8";
	public static void main(String[] args) {
		test();
	}
	  private static void test() {
			StringBuffer keyString = new StringBuffer();
			Map<String, String> treturnMap = new TreeMap<String, String>();
			treturnMap.put("partner", "1012485027");/*商户合作号，由平台注册提供*/
			treturnMap.put("amount", "650");/*金额(单位:元,支持两位小数)*/
			treturnMap.put("request_time", String.valueOf(Calendar.getInstance().getTimeInMillis()));/*时间戳,精确到秒*/
			treturnMap.put("trade_no", "1321221132132132");/*订单号*/
			treturnMap.put("pay_type", HaofuUtil.H5);
			treturnMap.put("notify_url","135.156.212.5:8008"+HaofuUtil.CHANNEL+HaofuUtil.NOTFIY+HaofuUtil.PAYTYPR);
			String param = HaofuUtil.getRequestStr(treturnMap);
			String md5key = "Hf19909jsnb19909hf8866";
			keyString.append(param + "&" + md5key);
			String sign = MD5Util.MD5Encode(keyString.toString(), CHARSET_UTF8);
			treturnMap.put("sign", sign);
			String paramStr = HaofuUtil.getRequestStr(treturnMap);
			String response = null;
			String alipayUrl = "https://mmbpccomsx.6785151.com/payCenter/aliPay2";
			log.info("请求豪富支付上游渠道参数："+paramStr);
			response = HttpUtil.post(alipayUrl, paramStr, 2000);//2秒未返回则默认超时
			System.out.println("上游渠道返回："+response);
			if(StrUtil.isNotBlank(response)) {
		}
		
	}
	test() {
	
}
}
