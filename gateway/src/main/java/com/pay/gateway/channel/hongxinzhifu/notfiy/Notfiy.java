package com.pay.gateway.channel.hongxinzhifu.notfiy;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.pay.gateway.channel.haofuzhifu.notfiy.HaoFuNotfiy;
import com.pay.gateway.channel.haofuzhifu.util.HaofuUtil;
import com.pay.gateway.channel.hongxinzhifu.util.HongXinUtil;
import com.pay.gateway.service.OrderService;
import com.pay.gateway.util.NotifyUtil;
import com.pay.gateway.util.OrderUtil;
import com.pay.gateway.util.SettingFile;

import cn.hutool.core.util.StrUtil;

@Controller(HongXinUtil.CHANNEL)
public class Notfiy {
	Logger log = LoggerFactory.getLogger(HaoFuNotfiy.class);
	public static final String CHARSET_UTF8 = "UTF-8";
	//状态，0-成功，1-失败，3-处理中。
	static final String STAYUS_SU = "0";
	static final String STAYUS_ER = "1";
	static final String STAYUS_YU = "3";
	@Autowired
	OrderService orderServiceImpl;
	@Resource
	OrderUtil orderUtil;
	@Resource
	NotifyUtil notifyUtil;
	@Autowired
	SettingFile settingFile;
	@PostMapping(HongXinUtil.NOTFIY)
	public void notfiy(HttpServletRequest request ,HttpServletResponse response) {
		log.info("【进入红星回调处理】");
		/*
		 * 	tradesno				M(5)			网关订单号
			status					M(32)			状态，0-成功，1-失败，3-处理中
			realamount				M(20)			实际交易金额（元）
			timeend					M(32)			完成时间,格式: yyyyMMddHHmmss
			appid					M(20)			平台id
			apporderid				M(14)			请求订单号
			statusdesc				M(20)			状态描述
			hmac					M(32)			签名数据
		 */
		String tradesno = request.getParameter("tradesno");
		String status = request.getParameter("status");
		String realamount = request.getParameter("realamount");
		String timeend = request.getParameter("timeend");
		String appid = request.getParameter("appid");
		String apporderid = request.getParameter("apporderid");
		String statusdesc = request.getParameter("statusdesc");
		String hmac = request.getParameter("hmac");
		log.info("红星回调参数传递详细： tradesno"+tradesno+"，status"+status+"，realamount"
		+realamount+"，timeend"+timeend+"，appid"+appid+"，apporderid"+apporderid+"，statusdesc"+statusdesc+"，hmac"+hmac+"");
		if(StrUtil.isBlank(tradesno)
				|| StrUtil.isBlank(status)
				|| StrUtil.isBlank(realamount)
				|| StrUtil.isBlank(timeend)
				|| StrUtil.isBlank(appid)
				|| StrUtil.isBlank(apporderid)
				|| StrUtil.isBlank(statusdesc)
				|| StrUtil.isBlank(hmac)
				)  {
			log.info("收到红星回调，必传参数为空");
			return ;
		}
		Map map = new HashMap();
		map.put("tradesno", tradesno);
		map.put("status", status);
		map.put("realamount", realamount);
		map.put("timeend", timeend);
		map.put("appid", appid);
		map.put("apporderid", apporderid);
		map.put("statusdesc", statusdesc);
		Object[] key = map.keySet().toArray();
		Arrays.sort(key);
		StringBuffer res = new StringBuffer(128);
		for (int i = 0; i < key.length; i++) 
			res.append(key[i] + "=" + map.get(key[i]) + "&");
		String rStr = res.substring(0, res.length() - 1);
		String keyedDigestUTF8 = HongXinUtil.getKeyedDigestUTF8(rStr, settingFile.getName(SettingFile.CHANNEL_HONGXING_KEY));
		if(!hmac.equals(keyedDigestUTF8)) {
			log.info("验签错误，我方签名结果："+keyedDigestUTF8+"，对方签名结果："+hmac);
			return;
		}else 
			log.info("签名成功，我方签名结果"+keyedDigestUTF8+"，对方签名结果："+hmac);
		if(STAYUS_SU.equals(status)) {
			log.info("【交易成功】");
			try {
				response.getWriter().write("success");
			} catch (IOException e) {
				log.info("【异步通知：返回红星success状态失败】");
			}
			orderUtil.orderSu(apporderid);
			return;
		}else {
			log.info("【交易失败】");
			orderUtil.orderEr(apporderid);
			return;
		}
	}
	
	
	

}
