package com.pay.gateway.util;


import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;

@Component
public class SendUtil {
	private final String privateKey = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAu0wb+QnOIwVPMj3hs1Q6pFuLLdQFdc9baTRMPw6X+x3Lhmrk16AGep6ggcvFKEAWyZmyg33gmgZwJMGoWj1utQIDAQABAkB9lv5W0p1X3FKLhPUX043y8bN0ymvS4HUSKVBLJBUC+4GUpH4ng/4NkA3hYoa91AJfK/kQ7PTuTNIbdzUzLkntAiEA/uQS8RMT41P/ZUQofiDDgUGRuFgL+vsOgR387QextfMCIQC8HL3wlkZfwvcVKDuQ5OEICpzc8Ci2ZfTEogPnrluqtwIhAL620CVo7NyPIO0YTmPxB9dSxEF2P6CO8I9TbMe9lg5ZAiEAhGV+UcySr2ebW6q7cdmFgJFnoiDtpqLPyW12biPLpLUCIDU0PmBhO3nomUgNdUwyQESFPBKh0T9Y0w3Dh1x8mb/F\r\n" ; 
	private final String publicKey ="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALtMG/kJziMFTzI94bNUOqRbiy3UBXXPW2k0TD8Ol/sdy4Zq5NegBnqeoIHLxShAFsmZsoN94JoGcCTBqFo9brUCAwEAAQ==\r\n"; //new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
	private String getPrivateKey() {
		return privateKey;
	}
	private String getPublicKey() {
		return publicKey;
	}
	/**
	 * <p>对参数进行加密</p>
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String ,Object > careteParam(Map<String,Object> map) throws Exception {
		String params = HttpUtil.toParams(map);
		System.out.println("-----------------------【请求参数加密，请求参数："+params+"】");
		String encryptData = AES.Encrypt(params);
        System.out.println("加密后内容:" + encryptData);
        Map<String ,Object > parasMap = new HashMap<String,Object>();
        parasMap.put("MD5",encryptData);
		return parasMap;
	}
	public Map<String ,Object > careteParam(String params) throws Exception {
		System.out.println("-----------------------【请求参数加密，请求参数："+params+"】");
		String encryptData =  AES.Encrypt(params);
		System.out.println("加密后内容:" + encryptData);
		Map<String ,Object > parasMap = new HashMap<String,Object>();
		parasMap.put("MD5",encryptData);
		return parasMap;
	}
	/**
	 * <p>对参数进行解密</p>
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> decryptionParam(HttpServletRequest request) throws Exception{
        XRsa rsa = new XRsa(publicKey,privateKey);
		String MD5 = request.getParameter("MD5");//参数加密结果  这是要解密的值
		String decryptData =AES.Decrypt(MD5);
		System.out.println("解密后内容:" + decryptData);
        HashMap<String, String> decodeParamMap = HttpUtil.decodeParamMap(decryptData,"UTF-8");
		return decodeParamMap;
	}
	
	public static String sendHttpsGet(String url, List<NameValuePair> params) {
		HttpGet httpGet;// 创建get请求
 
		if (params == null || params.isEmpty()) {
			httpGet = new HttpGet(url);
		} else {
			List<NameValuePair> parameters = new LinkedList<NameValuePair>();
			for (NameValuePair param : params) {
				if (StrUtil.isEmpty(param.getName()))
					continue;
				parameters.add(param);
			}
 
			if (!url.contains("?")) {
				url += "?" + URLEncodedUtils.format(parameters, "UTF-8");
			} else {
				url += "&" + URLEncodedUtils.format(parameters, "UTF-8");
			}
			System.out.println(url);
			httpGet = new HttpGet(url);
		}
		return sendHttpGet(httpGet);
	}
	private static  RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(15000).setConnectTimeout(15000)
			.setConnectionRequestTimeout(15000).build();
	/**
	 * 发送Get请求
	 * 
	 * @param httpPost
	 * @return
	 */
	private static String sendHttpGet(HttpGet httpGet) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		String responseContent = null;
		try {
			// 创建默认的httpClient实例.
			httpClient = HttpClients.createDefault();
			httpGet.setConfig(requestConfig);
			// 执行请求
			response = httpClient.execute(httpGet);
			entity = response.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭连接,释放资源
				if (response != null) {
					response.close();
				}
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseContent;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
