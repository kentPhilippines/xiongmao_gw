package test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;

public class pingduoduoTest {
	public static void main(String[] args) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String url = "http://open.drupcloud.net/pay_index"; 
		Map<String, Object> careteParam = new HashMap();
		/**
		 * 	pay_memberid 		商户号 		int 	Y Y 商户号mchid
			pay_orderid 		商户订 单号	 	string 	Y Y 上送订单号唯一，不能有特殊字符
			pay_applydate 		提交时 间 		string 	Y Y 时间格式：2016-12-26 18:18:18
			pay_bankcode 		支付类 型编码 	string 	Y Y 如：614，请参照附件中支付代码
			pay_notifyurl 		异步通 知地址 	string 	Y Y 支付完成后异步通知的地址
			pay_callbackurl 	页面跳 转地址 	string 	Y Y 暂不支持支付完成后同步回调,提交不 为空的值即可，如666
			pay_amount 			支付金 额 		float 	Y Y 订单金额，最多2位小数
			pay_md5sign 		请求签 名 		string 	Y N 请求签名
			pay_productname 	商品名 称 		string 	Y N 支付说明
			pay_attach 			附加信 息 		string 	N N 附加信息
		 */
		careteParam.put("pay_memberid", "15250");
		careteParam.put("pay_orderid", new Date().getTime());
		careteParam.put("pay_applydate",  formatter.format(new Date()));//2019-12-26 09:24:25
		careteParam.put("pay_bankcode", "619");
		careteParam.put("pay_notifyurl", "http://www.baidu.com");
		careteParam.put("pay_callbackurl", "http://www.baidu.com");
		careteParam.put("pay_amount", "1157");
		String appkey = "ddtisnzft0vpnf3kqb8708n1p89xzol7";
		/**
		 * 	614 支付宝H5(pdd模式)
			619 微信H5(pdd模式)
			620 话费支付宝H5(pdd模式)
		 */
		String param = createParam(careteParam,appkey);
		System.out.println("请求未加密前："+param);
		String md5 = md5(param);
		String newA = md5.toUpperCase();
        System.out.println("大写转换：" + newA);
		careteParam.put("pay_md5sign", newA);
		careteParam.put("pay_productname", "这是测试支付");
		careteParam.put("pay_attach", "这是测试支付");
		String post = HttpUtil.post(url, careteParam);
		System.out.println("请求返回情况："+post);
		
		
		
		
		/**
		 * 
		 * "url":"weixin:
		 * \/\/wap\/pay?prepayid%3Dwx1112033109183966b73feab91100948632&package=423165327&noncestr=1570766611&sign=8971254191c523e7d5a8f9617c07b86d",
		 * 
		 * "gateway":"http:\/\/open.drupcloud.net\/gateway\/wxh5?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJvcmRlcl9pZCI6IjIwMTkxMDExMTIwMzMwMjUwMTU1MDEwMDU5MTMiLCJleHAiOjE1NzA3NjcyMTF9.Fn25lJudvkYg_iMC83LPzncpZq-B1cMkS222LtyWhMk"
		 * 
		 * 
		 * 
		 */
		
		/**
		 * {"status":true,"msg":"获取成功","data":{
		 * 
		 * "url":"http://open.drupcloud.net/cashier?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJvcmRlcl9pZCI6IjIwMTkxMDExMTIxMTMyNTAyMTU1Mjk4OTkzNjgiLCJleHAiOjE1NzA3Njc2OTJ9.jpB42fSno1kJyfvq3Qq31QTqxfew87YO9LoB6NlOOc8","gateway":"http://open.drupcloud.net/cashier?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJvcmRlcl9pZCI6IjIwMTkxMDExMTIxMTMyNTAyMTU1Mjk4OTkzNjgiLCJleHAiOjE1NzA3Njc2OTJ9.jpB42fSno1kJyfvq3Qq31QTqxfew87YO9LoB6NlOOc8"}}
		 */
		
		
		
		
	}

	private static String createParam(Map<String, Object> map, String appInitKey) {
		try {
			if (map == null || map.isEmpty()) {
				return null;
			}
			//对参数名按照ASCII升序排序
			Object[] key = map.keySet().toArray();
			Arrays.sort(key);
			//生成加密原串  
			StringBuffer res = new StringBuffer(128);
			for (int i = 0; i < key.length; i++) {
				res.append(key[i] + "=" + map.get(key[i]) + "&");
			}
			String rStr = res.substring(0, res.length() - 1);
			System.out.println("请求接口加密原串 = " + rStr);
			if (appInitKey == null) {
				return rStr + "&key=" + appInitKey;
			}
			return rStr + "&key=" + appInitKey;                                                                                                                                                                                                                                
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public synchronized  static String md5(String params) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(params.getBytes());
            return new BigInteger(1,md.digest()).toString(16);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}
