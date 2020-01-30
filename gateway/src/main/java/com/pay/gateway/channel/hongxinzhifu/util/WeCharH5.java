package com.pay.gateway.channel.hongxinzhifu.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpUtil;

public class WeCharH5 {
	static SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
	public static void main(String[] args) {
		String url = "http://ycpay.kuaima888.cn:18000/GW/paywch5.do";
		String version = "2.0";
		String cmd = "PAYH5WECHAT";
		String appid = "30060";
		String userid = "CAT202001241103480002";
		String apporderid = "asd2@sa2sss555555ssss32223412521";
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
		map.put("tradetype","1");
		map.put("pageurl","www.baidu.com");
		String createParam = HongXinUtil.createParam(map, key);
		Map<String, List<String>> decodeParams = HttpUtil.decodeParams(createParam, "UTF-8");
        System.out.println(decodeParams.toString());
		Set<String> keySet = decodeParams.keySet();
		StringBuffer buffer = new StringBuffer();
		buffer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">")
		.append("<html>")
		.append("<head>")
		.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">")
		.append("</head>")
		.append("<body>")
		.append("<form action=\""+url+"\" id=\"from\" method=\"post\" >");
		for(String key1 : keySet) {
			List<String> list = decodeParams.get(key1);
			String first = CollUtil.getFirst(list);
			buffer.append(" <input type=\"text\" name=\""+key1+"\" value=\""+first+"\">");
		}
		buffer.append(" <button type=\"submit\">提交</button>")
		.append("</form>")
		.append("</body>")
		.append("</html>")
		.append("<script language=javascript>")
		.append("window.onload= function(){\r\n" + 
				"document.getElementById('from').submit();\r\n" + 
				"}")
		.append("</script>");
		
		
		
		
		
        
        
        
        
        System.out.println(buffer.toString());
        
		
		
		
		
		
		
		
		
		
		
		
		
		
	//	String hongXinResult = HongXinUtil.invoke(map, url, key);
	//	System.out.println(hongXinResult);
	}

}
