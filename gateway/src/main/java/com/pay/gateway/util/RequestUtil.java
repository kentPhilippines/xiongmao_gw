package com.pay.gateway.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.gateway.config.common.Common;
import com.pay.gateway.entity.Account;
import com.pay.gateway.entity.AccountInfo;
import com.pay.gateway.entity.dealEntity.ResultDeal;
import com.pay.gateway.service.AccountService;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

@Service
public class RequestUtil {
	Logger log = LoggerFactory.getLogger(RequestUtil.class);
	@Autowired
	AccountService accountServiceImpl;
	/**
	 * <p>公共验证类</p>
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@SuppressWarnings("unlikely-arg-type")
	public boolean validationAll(HttpServletRequest request, HttpServletResponse response,	ResultDeal resultDeal) throws IOException {
		 log.info("|-----------------【进入项目拦截器】-----------------------");
		 log.info("|-----------------【基本数据拦截】-----------------------");
		 log.info("访问URL：" + request.getRequestURL());
		 log.info("请求参数字符编码: " + request.getCharacterEncoding());
		 if(StrUtil.isBlank(request.getCharacterEncoding())) { 
			 log.info("|--------------【15030 :字符编码未设置】----------------");
			 resultDeal.setCod(Common.COD_15030);
			 resultDeal.setMsg(Common.MSG_15030);
			 response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			 return false;
		 }
		 if(!Common.CODING.equalsIgnoreCase(request.getCharacterEncoding())) {
			 log.info("|--------------【15031 :字符编码错误】----------------");
			 resultDeal.setCod(Common.COD_15031);
			 resultDeal.setMsg(Common.MSG_15031);
			 response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			 return false;
		 }
		 String appid = request.getParameter("appid");
		 if(StrUtil.isBlank(appid)) {
			 log.info("------------------------------【必传参数为空】------------------------------");
				 resultDeal.setCod(Common.COD_15034);
				 resultDeal.setMsg(Common.MSG_15034);
				 response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
				return false;
		 }
		 Account account = accountServiceImpl.findAccountByAppId(appid);
		 if(ObjectUtil.isNull(account)) {
			 log.info("|--------------【15020 ：用户不存在】----------------");
			 resultDeal.setCod(Common.COD_15020);
			 resultDeal.setMsg(Common.MSG_15020);
			 response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			 return false;
		 }
		 
		 Integer isDeal = account.getIsDeal();
		 if(Common.IS_DEAL_OFF.equals(isDeal)) {
			 log.info("|--------------【15002 ：用户未开通交易服务】----------------");
			 resultDeal.setCod(Common.COD_15002);
			 resultDeal.setMsg(Common.MSG_15002);
			 response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			 return false;
		 }
		 String applydate = request.getParameter("applydate");
		 DateFormat formatter = new SimpleDateFormat(Common.DATATYPE);
		 Date date;
		try {
			date = formatter.parse(applydate);
			boolean expired = DateUtil.isExpired(date,DateField.SECOND,600000000,new Date());//请求10秒过期
			if(!expired) {
				 log.info("|--------------【15033 ：请求过期】----------------");
				 resultDeal.setCod(Common.COD_15033);
				 resultDeal.setMsg(Common.MSG_15033);
				 response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
				 return false;
			}
		} catch (ParseException e1) {
			 log.info("|--------------【15032 ：时间格式错误】----------------");
			 resultDeal.setCod(Common.COD_15032);
			 resultDeal.setMsg(Common.MSG_15032);
			 response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			 return false;
		}
		 AccountInfo accountInfo = accountServiceImpl.findAccountInfoByAppId(appid);
		 if(ObjectUtil.isNull(accountInfo)) {
			 log.info("|--------------【15020 ：用户不存在】----------------");
			 resultDeal.setCod(Common.COD_15020);
			 resultDeal.setMsg(Common.MSG_15020);
			 response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			 return false;
		 }
		 log.info("|--------------【用户开始验签】----------------");
		 String appKey = accountInfo.getAppKey();
		 String appDesKey = accountInfo.getAppDesKey();
		 String rsasign = request.getParameter("rsasign");
		 try {
			boolean verify = verify(request,appDesKey,rsasign);
			if(verify)
				log.info("|--------------【验签正常】----------------");
			else {
				log.info("|--------------【验签失败】----------------");
				 resultDeal.setCod(Common.COD_15010);
				 resultDeal.setMsg(Common.MSG_15010);
				 response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
				 return false;
			}
		} catch (Exception e) {
			 log.info("|--------------【验签异常】----------------");
			 resultDeal.setCod(Common.COD_15010);
			 resultDeal.setMsg(Common.MSG_15010);
			 response.getWriter().write(JSONUtil.toJsonPrettyStr(resultDeal));
			 return false;
		}
		 return true ;
	}
	private boolean verify(HttpServletRequest request, String appDesKey, String rsasign)throws Exception {
		String appid = request.getParameter("appid");
		String orderid = request.getParameter("orderid");
		String notifyurl = request.getParameter("notifyurl");
		String applydate = request.getParameter("applydate");
		String amount = request.getParameter("amount");
		String passcode = request.getParameter("passcode");
		String sign = "appid"+appid + "orderid"+orderid +"notifyurl"+notifyurl+ "applydate"+applydate+"amount"+amount +"passcode"+passcode+ appDesKey;
		String newA = sign.toUpperCase();
		log.info("大写转换：" + newA);
		sign =  md5(newA);
		log.info("大写转换：" + sign);
		newA = sign.toUpperCase();
		log.info("|--------------【进入验签核心方法】----------------");
		log.info("|--------------【我方验签结果："+newA+"，请求方签名结果："+rsasign+"】----------------");
		if(newA.equals(rsasign)) {
			return true;
		}
		return false;
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
