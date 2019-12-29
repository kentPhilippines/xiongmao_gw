package com.pay.gateway.channel.futongzhifu.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay.gateway.channel.H5ailiPay.service.BankCardService;
import com.pay.gateway.channel.H5ailiPay.util.BankUtil;
import com.pay.gateway.channel.futongzhifu.util.ConstantUtil;
import com.pay.gateway.channel.futongzhifu.util.HtmlUtil;
import com.pay.gateway.channel.futongzhifu.util.Md5Util;
import com.pay.gateway.channel.futongzhifu.util.SignUtil;
import com.pay.gateway.config.common.Common;
import com.pay.gateway.config.service.PayOrderService;
import com.pay.gateway.entity.Account;
import com.pay.gateway.entity.AccountFee;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.entity.OrderAll;
import com.pay.gateway.entity.dealEntity.Deal;
import com.pay.gateway.entity.dealEntity.ResultDeal;
import com.pay.gateway.service.OrderService;
import com.pay.gateway.util.SendUtil;
import com.pay.gateway.util.SettingFile;
@Component("futongzhifuAlipayScan")
public class PayService extends PayOrderService{
	Logger log = LoggerFactory.getLogger(PayService.class);
	@Autowired
	BankCardService bankCardServiceImpl;
	@Autowired
	OrderService orderServiceImpl;
	@Resource
	BankUtil bankUtil;
	@Autowired
	SendUtil sendUtil;
	@Autowired
	SettingFile settingFile;
	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	public ResultDeal deal(Deal deal, Account account, AccountFee accountFee, OrderAll orderAll) {	
		log.info("===========【进入富通支付支付业务处理类】======");
	ResultDeal result = new ResultDeal();
	boolean flag = createOrder(orderAll);
	DealOrder order = null;	
	if(flag) {
		log.info("【富通支付，支付宝扫码模式交易订单创建成功】");
		order = orderServiceImpl.findOrderByOrderAll(orderAll.getOrderId());
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
		log.info("");
	/*	商户编号	 		merAccount 		是 		String(32)			由富通支付提供，默认为:商户号(由富通 支付提供的9位正整数)，用于富通支付 判别请求方的身份
		商户订单号	 		merOrderId 		是 		String(32)			商户订单号，32个字符内、只允许使用 数字、字母，确保在商户系统唯一
		支付金额		 	amount 			是 		String(14)			支付金额，必须大于0，包含2位小数 如：amount=200.27
		商户订单提交 时间	orderTime 		是 		String(14)			商户提交用户订单时间，格式 为:yyyy-MM-dd HH:mm:ss
		支付类型编码		payType 		是 		String(10)			见附录7.1.1支付类型，如： payType=quick
		银行编码			bankCode 		否 		String(14)			见附录7.2.1借记卡网银编码列表，如： bankCode=ICBC，网银支付时必填
		银行类型 			cardType 		否 		String(256)			固定选项值：debit-card(借记卡) credit-card(信用卡) 目前暂不支持信用卡支付
支付成功后客户端浏览器回调地址	pageUrl 	是 		String(256)			客户端浏览器回调地址，支付成功后， 将附带回调数据跳转到此页面，商户可 以进行相关处理并显示给终端用户
服务端支付成 功通知地址		backUrl 		是 		String(256)			支付成功后，富通支付将发送“支付成 功”信息至该地址，商户收到信息后， 需返回相应信息至富通支付，表明已收 到通知。返回信息只能定义为 “success”（注意为小写英文字母）， 返回其他信息均为失败
		商品名称 			subject 		否 		String(56)			商品名称，如：subject=商品XX 特殊情况下，如需提前传入银行卡号， 可用此字段，如：subject=6216XXXX
		商品单价 			price 			否		String(20)			
		购买数量 			quantity 		否 		String(20)
		买家IP地址 		clientIp 			否 		String(20) 			防钓鱼用，买家的ip地址
		签名类型 			signType 		是 		String(10)			 签名类型，固定：MD5 （不参与签名）
		签名串			sign 				是 		String(1024) 		签名结果 （不参与签名）
*/
		/*
		 * 	支付类型
		 * 	快捷 H5 quick
			网银 e_bank
			微信wap weixin_wap
			微信扫码 weixin_scan
			支付宝wap alipay_wap
			支付宝扫码 alipay_scan
			银联扫码 yinlian_scan
		 */
        String merOrderId = order.getOrderId(); //自身系统的订单号
        String bankCode = "";//网银支付必填
        String cardType = "";//网银支付必填
        String amount = order.getDealAmount().toString();
        String merAccount =  ConstantUtil.MERCHANT_ACCOUNT;
        String subject = "豪汇支付，发起交易"; //订单标题
        String price = "";///商品单价 非必填字段
        String quantity = "";//购买数量 非必填字段
        String clientIp = "";//买家IP地址 防钓鱼
        String payType = "alipay_scan";
        String signType = "MD5";
        String pageUrl = "";
        String backUrl = "";
        Map<String, String> map = new HashMap<String, String>();
        map.put("merOrderId", merOrderId);
        map.put("bankCode", bankCode);
        map.put("cardType", cardType);
        map.put("amount", amount);
        map.put("merAccount", merAccount);
        map.put("subject", subject);
        map.put("price", price);
        map.put("quantity", quantity);
        map.put("clientIp", clientIp);
        map.put("payType", payType);
        map.put("pageUrl", pageUrl);
        map.put("backUrl", backUrl);
        map.put("orderTime", formatter.format(new Date()));
        String signStr = SignUtil.sortData(map);
        log.info(signStr);
        String sign = Md5Util.MD5Encode(signStr, ConstantUtil.PAY_KEY);
        map.put("signType", signType);
        map.put("sign", sign);
        String form = HtmlUtil.map2From(ConstantUtil.PAY_URL, map);
        
        
        
        
		return null;
	}

}
