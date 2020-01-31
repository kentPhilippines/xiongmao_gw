package com.pay.gateway.channel.hongxinzhifu.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestH5 {
	static SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
	public static void main(String[] args) {
		
		
		/**
		 * 	appId: mx_ceshi
			userId: CAT201911091955560003
			key: JMYM06FId4DmitCm65x6VPhdc22oDJduxFHArRlOdFC5QHxflUGlZSubTNW0j4eT9zaQdzrPQ9mI3RJyZv6pa6a0QANu7J18Q6Bu90xVL7b3lSw0wA1wIrT6QzNrPz5s
			desKey: Cg8RWiKddSEKUq6HwvCbRCB3OLCchLxR
		 */
		
		
		
		String url = "http://ycpay.kuaima888.cn:18000/GW/PayH5Ali.do";
		String version = "2.0";
		String cmd = "PAYH5ALIPAY";
		String appid = "mx_ceshi";//appid
		String userid = "CAT201911091955560003";//userId
		String apporderid = "a546753336578768723412521";//你的订单号
		String orderbody = "支付";
		String notifyurl = "www.baidu.com";
		String amount ="200.00";
		String key = "JMYM06FId4DmitCm65x6VPhdc22oDJduxFHArRlOdFC5QHxflUGlZSubTNW0j4eT9zaQdzrPQ9mI3RJyZv6pa6a0QANu7J18Q6Bu90xVL7b3lSw0wA1wIrT6QzNrPz5s";
		String desKey = "Cg8RWiKddSEKUq6HwvCbRCB3OLCchLxR";
		Map<String, String> map = new HashMap<String, String>();
		map.put("cmd", "PAYH5ALIPAY");//命令字
		map.put("version", "2.0");//版本号
		map.put("appid",appid);//商户id      
		map.put("ordertime", d.format(new Date()));
	    map.put("userid",userid); 
		map.put("apporderid", apporderid);
		map.put("amount",Double.valueOf(amount).intValue()+"");
		map.put("front_skip_url","www.dsajghsahd.com");
		map.put("notifyurl",notifyurl);
		System.out.println( invoke(map, url, key));
		//String createParam = HongXinUtil.createParam(map, key);
	//	System.out.println(createParam);
		
		
		
		//System.out.println(hongXinResult);
	}
	public static String invoke(Map<String, String> paramMap, String url, String appInitKey ) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String params = createParam(paramMap, appInitKey);
		String result = submitPost(url, params);
		if (result == null) 
			return null;
		String[] split = result.split("&");
		for (int i = 0; i < split.length; i++) {
			String[] temp = split[i].split("=");
			if (temp.length == 1) 
				resultMap.put(temp[0], "");
			if (temp.length > 1) 
				resultMap.put(temp[0], temp[1]);
		}
		return result;
	}
	public static String createParam(Map<String, String> map, String appInitKey) {
		try {
			if (map == null || map.isEmpty()) 
				return null;
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
			if (appInitKey == null) 
				return rStr + "&hmac=" + getKeyedDigestUTF8(rStr,appInitKey);
			System.out.println("&hmac=" + getKeyedDigestUTF8(rStr,appInitKey));
			return rStr + "&hmac=" + getKeyedDigestUTF8(rStr,appInitKey);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	public String getKeyedDigest(String strSrc, String key) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(strSrc.getBytes("GBK"));
			String result = "";
			byte[] temp;
			temp = md5.digest(key.getBytes("GBK"));
			for (int i = 0; i < temp.length; i++) 
				result += Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getKeyedDigestUTF8(String strSrc, String key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(strSrc.getBytes("UTF8"));
            String result="";
            byte[] temp;
            temp=md5.digest(key.getBytes("UTF8"));
    		for (int i=0; i<temp.length; i++)
    			result+=Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
    		return result;
        } catch (NoSuchAlgorithmException e) {
        	e.printStackTrace();
        }catch(Exception e) {
          e.printStackTrace();
        }
        return null;
    }
	public static String submitPost(String url, String params) {
		StringBuffer responseMessage = null;
		java.net.HttpURLConnection connection = null;
		java.net.URL reqUrl = null;
		OutputStreamWriter reqOut = null;
		InputStream in = null;
		BufferedReader br = null;
		int charCount = -1;
		try {
			responseMessage = new StringBuffer(128);
			reqUrl = new java.net.URL(url);
			connection = (java.net.HttpURLConnection) reqUrl.openConnection();
			connection.setReadTimeout(50000);
			connection.setConnectTimeout(100000);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			reqOut = new OutputStreamWriter(connection.getOutputStream(),"UTF-8");
			reqOut.write(params);
			reqOut.flush();

			in = connection.getInputStream();
			br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			while ((charCount = br.read()) != -1) {
				responseMessage.append((char) charCount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) 
					in.close();
				if (reqOut != null) 
					reqOut.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return responseMessage.toString();
	}
	
}
