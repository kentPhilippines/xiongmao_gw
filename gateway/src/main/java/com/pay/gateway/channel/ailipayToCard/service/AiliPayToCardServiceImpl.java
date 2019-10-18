package com.pay.gateway.channel.ailipayToCard.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pay.gateway.channel.H5ailiPay.service.BankCardService;
import com.pay.gateway.channel.H5ailiPay.service.H5aliPayService;
import com.pay.gateway.channel.H5ailiPay.util.BankUtil;
import com.pay.gateway.config.common.Common;
import com.pay.gateway.config.service.PayOrderService;
import com.pay.gateway.entity.Account;
import com.pay.gateway.entity.AccountFee;
import com.pay.gateway.entity.BankCard;
import com.pay.gateway.entity.OrderAll;
import com.pay.gateway.entity.dealEntity.Deal;
import com.pay.gateway.entity.dealEntity.ResultDeal;
import com.pay.gateway.util.SendUtil;

import cn.hutool.http.HttpUtil;
@Component("AiliPayToCardServiceImpl")
public class AiliPayToCardServiceImpl extends PayOrderService{
	Logger log = LoggerFactory.getLogger(AiliPayToCardServiceImpl.class);
	@Autowired
	BankCardService bankCardServiceImpl;
	@Resource
	BankUtil bankUtil;
	@Autowired
	SendUtil sendUtil;
	@Value("${deal.url.path}")
	private String dealurl;
	/**
	 * <p>宝转卡简便模式</p>
	 */
	@Override
	public ResultDeal deal(Deal deal, Account account, AccountFee accountFee, OrderAll orderAll) {
		log.info("===========【本地支付宝处理类--宝转卡简便模式】======");
		ResultDeal result = new ResultDeal();
		DecimalFormat df = new DecimalFormat(".##");  
		String format = df.format(new BigDecimal(orderAll.getOrderAmount()));
		BigDecimal amount = bankUtil.findDealAmount(new BigDecimal(format));
		String param = "order="+orderAll.getOrderId();
		param += "|amount="+amount;
		Map<String, Object> careteParam;
		log.info("===========【测试："+amount+"======");
		log.info("===========【缓存取到页面金额："+amount+"======");
		log.info("===========【测试："+amount+"======");
		//加密
		 DateFormat formatter = new SimpleDateFormat(Common.DATATYPE);
		try {
			careteParam = sendUtil.careteParam("order="+orderAll.getOrderId()+"&amount="+amount+"&data="+formatter.format(new Date()));
		} catch (Exception e) {
			log.info("【四方请求参数加密异常】");
			result.setCod(Common.RESPONSE_STATUS_ER);
			result.setMsg("返回参数加密异常");
			return result;
		}
		String params = HttpUtil.toParams(careteParam);
		result.setReturnUrl("http://"+dealurl+"/api/ailiPayToCard?"+params);
		log.info("===========【转发的get请求路径："+result.getReturnUrl()+"======");
		result.setCod(Common.RESPONSE_STATUS_SU);
		result.setMsg(Common.RESPONSE_STATUS_SU_MSG);
		result.setOpenType(Common.OPENTYPE_URL);//URL打开方式
		log.info("支付宝转到银行卡边界模式返回的结果集为："+result.toString());
 		return result;
	}
}
