package com.pay.gateway.channel.hongxinzhifu.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.pay.gateway.util.SettingFile;

import cn.hutool.http.HttpUtil;

public class Test {
	static SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
	
	//支付宝扫码
	public static void main(String[] args) throws Exception {
		String url ="http://ycpay.kuaima888.cn:18000/GW/gw.inter";
	//	String url = "http://ycpay.kuaima888.cn:18000/GW/PayH5Ali.do";
		String version = "2.0";
		String cmd = "PAYH5ALIPAY";
		String appid = "30060";
		String userid = "CAT202001241103480002";
		String apporderid = "as112521";
		String orderbody = "虹付支付";
		String notifyurl = "www.baidu.com";
		String amount ="200.00";
		String key = "G8BuBs4kU8ouL1yWxlflXDHgdcwA0ViohSernnB6bXgju0bu9whFzRib4MhWPgbtUdWWfufMY7iv9WXbZI2K3BeWfo0JamirXfvqruiaqbXPcJotjfXGONXwb2MYyiap";
		String desKey = "t2Qd9LaMdEUdE0ez6MbgboQNeHuLQNJY";
		Map<String, String> map = new HashMap<String, String>();
		map.put("cmd", "PAYH5ALIPAY");//命令字
		map.put("version", "2.0");//版本号
		map.put("appid",appid);//商户id      
		map.put("ordertime", d.format(new Date()));
	    map.put("userid",userid); 
		map.put("apporderid", apporderid);
		map.put("orderbody",orderbody);
		map.put("amount",amount);
		map.put("notifyurl",notifyurl);
		map.put("custip","127.0.0.1");
		String hongXinResult = HongXinUtil.invoke(map, url, key);
		HashMap<String, String> decodeParamMap = HttpUtil.decodeParamMap(hongXinResult,"UTF-8");
		System.out.println(hongXinResult.toString());
		String string = decodeParamMap.get("payurl");
		String requresUrl = CipherUtil.decryptDataUTF8(string,desKey);
		System.out.println(requresUrl);
	}
	
	
	
	
	
	
	
	
	
	

}
