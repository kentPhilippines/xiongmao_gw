package com.pay.gateway.channel.ailipayToCard.contorller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pay.gateway.channel.H5ailiPay.service.BankCardService;
import com.pay.gateway.channel.H5ailiPay.util.QRCodeUtil;
import com.pay.gateway.config.common.Common;
import com.pay.gateway.config.redis.RedisUtil;
import com.pay.gateway.entity.BankCard;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.service.OrderService;
import com.pay.gateway.util.SendUtil;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
@Controller
@RequestMapping("/api")
public class ToCardPayContorller {
	 Logger log = LoggerFactory.getLogger(ToCardPayContorller.class);
	 @Value("${tomcat.imgpath.path}")
	 private String imgpath;
	@Autowired
	 RedisUtil redisUtil;
	@Autowired
	OrderService orderServiceImpl;
	@Autowired
	BankCardService bankCardServiceImpl;
	@Autowired
	SendUtil sendUtil;
	private Lock lock = new ReentrantLock();
	@GetMapping("/ailiPayToCard")
	public String startOrder(Model m,HttpServletRequest request) throws Exception {
		lock.lock();
		HashMap<String,String> decryptionParam = sendUtil.decryptionParam(request);
		String order = decryptionParam.get("order");
		String amount = decryptionParam.get("amount");
		String data = decryptionParam.get("data");
		DateFormat formatter = new SimpleDateFormat(Common.DATATYPE);
		Date date;
		date = formatter.parse(data);
		log.info("================【请求时间戳为："+data+"】===============");
		/*boolean expired = DateUtil.isExpired(date,DateField.SECOND,300,new Date());//请求300秒过期
		if(!expired) {
			log.info("================【订单过期】===============");
			return "/orderEr";
		}
		*/
		log.info("================【宝转卡简便模式】===============");
		 String serverName = request.getServerName();
		 int serverPort = request.getServerPort();
		 
		 log.info("================【页面展示：金额："+amount+"，订单号："+order+"】===============");
		 String[] split = amount.split(",");
		 amount = split[0];
		 /**
		  * <p>生成交易订单</p>
		  */
		DealOrder findOrderByOrderAll = orderServiceImpl.findOrderByOrderAll(order);
		BankCard bankCard = new BankCard();
		if(ObjectUtil.isNotNull(findOrderByOrderAll)) {
			String dealCardId = findOrderByOrderAll.getDealCardId();
			bankCard = bankCardServiceImpl.findBankCardByBankCardId(dealCardId);
			bankCard.setDealAmount(findOrderByOrderAll.getDealAmount());
		}else {
			bankCard = orderServiceImpl.createOrder(order,amount.toString());
		}
		lock.unlock();
		String money = bankCard.getDealAmount().toString();
		String bankMark = bankCard.getRetain3();
		String bankName = bankCard.getBankName();
		String bankCardId =bankCard.getBankCard();//8
		String bankCardName = bankCard.getCardholder();
		String url = "https://www.alipay.com/?appId=09999988"
				+ "&actionType=toCard"
				+ "&sourceId=bill&"
				+ "cardNo="+bankCardId+""
				+ "&bankAccount="+bankCardName+""
				+ "&money="+money+""
				+ "&amount="+money+""
				+ "&bankMark="+bankMark+""
				+ "&bankName="+bankName+"";
		/*
		 * String url1 = "https://www.alipay.com/?appId=09999988&"+
		 * "actionType=toCard&sourceId=bill&cardNo="+bankCardId+""+
		 * "&bankAccount="+bankAccount+"&money="+money+"&amount="+money+"&bankMark="+
		 * bankMark+"&bankName="+bankName+"&orderSource= from";
		 */
		log.info("--【短连接网址拼接前的长网址："+url+"】");
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("url", url));
		String sendHttpsGet = SendUtil.sendHttpsGet("http://tinyurl.com/api-create.php", params);
		log.info("--【短连接网址："+sendHttpsGet+"】");
		QRCodeUtil.encode(
				sendHttpsGet,
				imgpath,
				true,order);
		String removePrefixIgnoreCase = StrUtil.removePrefixIgnoreCase(sendHttpsGet,"http://");
		String url1 = removePrefixIgnoreCase;
		m.addAttribute("url", url1);
		m.addAttribute("order", order);
		m.addAttribute("amount", amount);
		 log.info("================【页面展示：金额："+amount+"，订单号："+order+"】===============");
		return "/pcToCard";
	}

}
