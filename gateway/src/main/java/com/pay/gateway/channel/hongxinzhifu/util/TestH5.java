package com.pay.gateway.channel.hongxinzhifu.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestH5 {
	static SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
	public static void main(String[] args) {
		String url = "http://ycpay.kuaima888.cn:18000/GW/paywch5.do";
		String version = "2.0";
		String cmd = "PAYH5WECHAT";
		String appid = "30060";
		String userid = "CAT202001241103480002";
		String apporderid = "asd2@sa2ssss32223412521";
		String orderbody = "虹付支付";
		String notifyurl = "www.baidu.com";
		String amount ="200.00";
		String key = "G8BuBs4kU8ouL1yWxlflXDHgdcwA0ViohSernnB6bXgju0bu9whFzRib4MhWPgbtUdWWfufMY7iv9WXbZI2K3BeWfo0JamirXfvqruiaqbXPcJotjfXGONXwb2MYyiap";
		String desKey = "t2Qd9LaMdEUdE0ez6MbgboQNeHuLQNJY";
		Map<String, String> map = new HashMap<String, String>();
		map.put("cmd", "PAYH5WECHAT");//命令字
		map.put("version", "2.0");//版本号
		map.put("appid",appid);//商户id      
		map.put("ordertime", d.format(new Date()));
	    map.put("userid",userid); 
		map.put("apporderid", apporderid);
		map.put("orderbody",orderbody);
		map.put("orderbody","h5支付测试");
		map.put("orderdesc","h5支付测试");
		map.put("amount",amount);
		map.put("front_skip_url","www.baidu.com");
		map.put("notifyurl","www.baidu.com");
		
		String hongXinResult = HongXinUtil.invoke(map, url, key);
		System.out.println(hongXinResult);
	}

}
