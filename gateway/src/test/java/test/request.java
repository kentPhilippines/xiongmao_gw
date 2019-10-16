package test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.pay.gateway.util.DateUtils;

import cn.hutool.core.lang.UUID;


public class request {
	static DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		String appDesKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIS67ixkE+CU2jCgxadhiYFuCU9A9qLQK84HIJObgb7extbAFvh1Q81dJ5FPCkOsqU59lL5WZ52jxIC6Ie2HxLsCAwEAAQ==";
		String url ="http://47.91.218.103:45466/deal/payTo";
		String appid = "AC1000";
		String orderid = UUID.randomUUID().toString();
		String applydate = formatter.format(new Date());
		String notifyurl = "www.baidu.com";
		String amount = "50000";
		String passcode = "PAY1004";
		String rsasign = "";
		String sign = "appid"+appid + "orderid"+orderid + "amount"+amount + appDesKey;
		rsasign =  md5(sign);
		map.put("appid", appid);//版本号
		map.put("orderid", orderid);//订单号
		map.put("notifyurl", notifyurl);
		map.put("applydate",applydate);
		map.put("amount",amount);
		map.put("passcode",passcode);
		map.put("rsasign",rsasign);
		String param = createParam(map);
		String result = submitPost(url, param);
		System.out.println(result);
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
				if (in != null) {
					in.close();
				}
				if (reqOut != null) {
					reqOut.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return responseMessage.toString();
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
	public static String createParam(Map<String, String> map) {
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
			return rStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
