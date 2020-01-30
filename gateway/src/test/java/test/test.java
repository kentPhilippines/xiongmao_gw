package test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pay.gateway.channel.hongxinzhifu.util.HongXinUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpUtil;

public class test {
	public static void main(String[] args) {
		double  a =Double.valueOf("666").doubleValue() ;
		System.out.println(a);
		BigDecimal bigDecimal = new BigDecimal(a);
		  BigDecimal setScale = bigDecimal.setScale(666,BigDecimal.ROUND_HALF_UP);
		System.out.println(setScale);
		
		
		
		DecimalFormat df = new DecimalFormat(".##");  
		String format = df.format(new BigDecimal("666.94"));
        BigDecimal b1 = new BigDecimal(format);  
        BigDecimal b2 = new BigDecimal("28.00");  
        System.out.println("小数格式化：" + df.format(b1));  
        System.out.println("整数格式化：" + df.format(b2));  
        
        String aa = "600.00";
        Integer valueOf = Double.valueOf(aa).intValue();
        System.out.println(valueOf);
        Map<String, String> map = new HashMap<String, String>();
		map.put("cmd", "PAYH5WECHAT");//命令字
		map.put("version", "2.0");//版本号
		map.put("appid","asdsa");//商户id      
		map.put("ordertime", "asdsa");
	    map.put("userid","asdsa"); 
		map.put("apporderid", "asdsa");
		map.put("orderbody","asdsa");
		map.put("tradetype","1");
		map.put("orderdesc","asdsa");
		map.put("amount","asdsa");
		map.put("front_skip_url","asdsa");
		map.put("notifyurl","asdsa");
		map.put("pageurl","asdsa");
		String createParam = HongXinUtil.createParam(map, "asdsa");
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
		.append("<form action=\"http://ycpay.kuaima888.cn:18000/GW/paywch5.do\"  name=\"from\" method=\"post\" >");
		for(String key : keySet) {
			List<String> list = decodeParams.get(key);
			String first = CollUtil.getFirst(list);
			buffer.append(" <input type=\"text\" name=\""+key+"\" value=\""+first+"\">");
		}
		buffer.append(" <button type=\"submit\">提交</button>")
		.append("</form>")
		.append("</body>")
		.append("</html>")
		.append("<script language=javascript>")
		.append("document.form.submit()")
		.append("</script>");
        
        
        
        
        System.out.println(buffer.toString());
        
        
        
        
        
        
	}

}
