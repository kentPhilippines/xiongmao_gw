package com.pay.gateway.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.gateway.entity.Account;
import com.pay.gateway.entity.AccountInfo;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.service.AccountService;
import com.pay.gateway.service.OrderService;

import cn.hutool.http.HttpUtil;

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
		log.info("============【准备向下游商戶发送通知】================");
		DealOrder dealOrder = orderServiceImpl.findOrderByOrderAll(OrderIdAll);
		String url = dealOrder.getRetain1();
		Account account = accountServiceImpl.findAccountByAppId(dealOrder.getOrderAccount());
		AccountInfo accountInfo = accountServiceImpl.findAccountInfoByAppId(dealOrder.getOrderAccount());
		String appid =   account.getAccountId();
		String secretKey = accountInfo.getAppDesKey(); 
		String orderNo = dealOrder.getOrderId(); // 本系統訂單號
		String externalOrderId = dealOrder.getExternalOrderId(); // 外部訂單號  (就是下有商戶的訂單號)
		String amount = dealOrder.getDealAmount().toString();
		String cod = flag?"Y":"N";
		String sign = appid + orderNo + amount + externalOrderId +cod+ secretKey;
		sign =  md5(sign);
		sign = sign.toUpperCase();
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("appid", appid);
		msg.put("orderNo", orderNo);
		msg.put("amount", amount);
		msg.put("externalOrderId", externalOrderId);
		msg.put("cod",cod);
		msg.put("body",flag?"交易成功":"交易失败");
		msg.put("sign", sign);
		log.info("============【发送通知的参数情況："+msg.toString()+"】================");
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
		 Object orderNo = msg.get("orderNo");
		if("success".equalsIgnoreCase(result)){
			log.info("【下游商户返回信息为成功，成功收到回调信息】");
			boolean updataNotifyYesByNo = orderServiceImpl.updataNotifyYesByNo(orderNo.toString());
			if(updataNotifyYesByNo)
			log.info("============【发送通知成功，订单发送通知状态已修改为YES：orderNo："+orderNo+ "】================");
		}else {
			log.info("【下游商户未收到回调信息，或回调信息下游未成功返回】");
		}
	}
	public static String md5(String str){
		  MessageDigest md5 = null;  
		  try{  
		     md5 = MessageDigest.getInstance("MD5");  
		  }catch (Exception e){  
		    System.out.println(e.toString());  
		    e.printStackTrace();  
		    return "";  
		  }  
		  char[] charArray = str.toCharArray();  
		  byte[] byteArray = new byte[charArray.length];
		  for (int i = 0; i < charArray.length; i++)  
		    byteArray[i] = (byte) charArray[i];  
		  byte[] md5Bytes = md5.digest(byteArray);  
		  StringBuffer hexValue = new StringBuffer();  
		  for (int i = 0; i < md5Bytes.length; i++){  
		    int val = ((int) md5Bytes[i]) & 0xff;  
		    if (val < 16)  
		      hexValue.append("0");  
		    hexValue.append(Integer.toHexString(val));  
		  }  
		  return hexValue.toString();
		}
}
