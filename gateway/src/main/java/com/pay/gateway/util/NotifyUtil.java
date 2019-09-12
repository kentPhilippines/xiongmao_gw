package com.pay.gateway.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.gateway.channel.H5ailiPay.contorller.NotfiyContorller;
import com.pay.gateway.entity.Account;
import com.pay.gateway.entity.AccountInfo;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.service.AccountService;
import com.pay.gateway.service.OrderService;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * <p>給下游商戶發送通知</p>
 * @author K
 */
@Service
public class NotifyUtil {
	Logger log = LoggerFactory.getLogger(NotifyUtil.class);
	@Autowired
	OrderService orderServiceImpl;
	@Autowired
	AccountService accountServiceImpl;
	/**
	 * <p>根據訂單號給下游發送通知</p>
	 * @param OrderId		訂單號
	 * @param flag			成功或失敗的通知
	 */
	public void sendMsg(String OrderIdAll,boolean flag) {
		log.info("============【準備向下游商戶發送通知】================");
		DealOrder dealOrder = orderServiceImpl.findOrderByOrderAll(OrderIdAll);
		String url = dealOrder.getRetain1();
		Account account = accountServiceImpl.findAccountByAppId(dealOrder.getOrderAccount());
		AccountInfo accountInfo = accountServiceImpl.findAccountInfoByAppId(dealOrder.getOrderAccount());
		String appid =   account.getAccountId();
		String secretKey = accountInfo.getAppKey(); 
		String orderNo = dealOrder.getOrderId(); // 本系統訂單號
		String externalOrderId = dealOrder.getExternalOrderId(); // 外部訂單號  (就是下有商戶的訂單號)
		String amount = dealOrder.getDealAmount().toString();
		String sign = appid + orderNo + amount + externalOrderId + secretKey;
		sign =  md5(sign);
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("appid", appid);
		msg.put("orderNo", orderNo);
		msg.put("amount", amount);
		msg.put("externalOrderId", externalOrderId);
		msg.put("body",flag?"交易成功":"交易失敗");
		msg.put("sign", sign);
		log.info("============【发送通知的參數情況："+msg.toString()+"】================");
		log.info("============【发送通知的地址："+url.toString()+"】================");
		send(url,msg);
	}
	
	/**
	 * <p>發送通知</p>
	 * @param url			發送通知的地址
	 * @param msg			發送通知的内容
	 */
	private void send( String url, Map<String, Object> msg ) {
		String result= HttpUtil.post(url, msg);
		log.info("服务器返回结果为："+result.toString());
		/*
		 * JSONObject parseObj = JSONUtil.parseObj(result); Object object =
		 * parseObj.get("return"); Object orderNo = msg.get("orderNo");
		 * if("suss".equalsIgnoreCase(object.toString())) {//修改訂單通知 boolean flag =
		 * orderServiceImpl.updataNotifyYesByNo(orderNo.toString()); if(flag)
		 * log.info("============【发送通知成功，订单发送通知状态已修改为YES：orderNo："+orderNo+
		 * "】================"); }
		 */
		
	}
	public  static String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            return new BigInteger(1,md.digest()).toString(16);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
