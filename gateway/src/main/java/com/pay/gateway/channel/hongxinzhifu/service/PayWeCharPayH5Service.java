package com.pay.gateway.channel.hongxinzhifu.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay.gateway.channel.hongxinzhifu.util.HongXinUtil;
import com.pay.gateway.config.common.Common;
import com.pay.gateway.config.service.PayOrderService;
import com.pay.gateway.entity.Account;
import com.pay.gateway.entity.AccountFee;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.entity.OrderAll;
import com.pay.gateway.entity.dealEntity.Deal;
import com.pay.gateway.entity.dealEntity.ResultDeal;
import com.pay.gateway.util.SettingFile;
@Component("HongXinWeCharH5")
public class PayWeCharPayH5Service extends PayOrderService{
		Logger log = LoggerFactory.getLogger(PayAlipayScanService.class);
		public static final String CHARSET_UTF8 = "UTF-8";
		SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
		@Autowired
		SettingFile settingFile;
	@Override
	public ResultDeal deal(Deal deal, Account account, AccountFee accountFee, OrderAll orderAll) {
		log.info("===========【进入红星支付支付业务处理类】======");
		ResultDeal result = new ResultDeal();
		boolean flag = createOrder(orderAll);
		DealOrder order = null;	
		if(flag) {
			order = findOrderByAllId(orderAll.getOrderId());
			log.info("【红星支付，微信H5模式交易订单创建成功，交易订单号："+order.getOrderId()+"】");
		}else {
			result.setCod(Common.RESPONSE_STATUS_ER);
			result.setMsg(Common.RESPONSE_STATUS_ER_MSG);
			result.setRedirect("订单创建失败，请联系运营人员处理");
			return result;
		}
		return Post(order);
	}
	private ResultDeal Post(DealOrder order) {
		ResultDeal result = new ResultDeal();
		String url = settingFile.getName(SettingFile.CHANNEL_HONGXING_WECHAR_H5_URL);
		String version = "2.0";
		String cmd = "PAYH5WECHAT";
		String appid = settingFile.getName(SettingFile.CHANNEL_HONGXING_APPID);
		String userid = settingFile.getName(SettingFile.CHANNEL_HONGXING_USERID);
		String apporderid = order.getOrderId();
		String orderbody = "虹付支付";
		String notifyurl = settingFile.getName(SettingFile.LOCATION) +  HongXinUtil.CHANNEL+HongXinUtil.NOTFIY ;
		String amount = order.getDealAmount().toString();
		String key = settingFile.getName(SettingFile.CHANNEL_HONGXING_KEY);//"G8BuBs4kU8ouL1yWxlflXDHgdcwA0ViohSernnB6bXgju0bu9whFzRib4MhWPgbtUdWWfufMY7iv9WXbZI2K3BeWfo0JamirXfvqruiaqbXPcJotjfXGONXwb2MYyiap";
		String desKey = settingFile.getName(SettingFile.CHANNEL_HONGXING_DESKEY);//"t2Qd9LaMdEUdE0ez6MbgboQNeHuLQNJY";
		Map<String, String> map = new HashMap<String, String>();
		map.put("cmd", "PAYH5WECHAT");//命令字
		map.put("version", "2.0");//版本号
		map.put("appid",appid);//商户id      
		map.put("ordertime", d.format(new Date()));
	    map.put("userid",userid); 
		map.put("apporderid", apporderid);
		map.put("orderbody",orderbody);
		map.put("tradetype","1");
		map.put("orderdesc",orderbody);
		map.put("amount",Double.valueOf(amount).intValue()+"");
		map.put("front_skip_url",order.getRetain5());
		map.put("notifyurl",notifyurl);
		String hongXinResult = HongXinUtil.invoke(map, url, key);
		log.info("红星微信H5上游返回："+hongXinResult);
		if(!hongXinResult.contains("<html") || !hongXinResult.contains("<meta")) //成功跳转
			return returnBean(apporderid, "订单状态失败,上游返回失败");
		result.setCod(Common.RESPONSE_STATUS_SU);
		result.setReturnUrl(hongXinResult);
		result.setOpenType(Common.OPENTYPE_HTML);
		return result;
	}

}
