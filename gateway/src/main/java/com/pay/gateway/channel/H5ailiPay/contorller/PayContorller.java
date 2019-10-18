package com.pay.gateway.channel.H5ailiPay.contorller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.gateway.api.MyDealContorller;
import com.pay.gateway.channel.H5ailiPay.service.BankCardService;
import com.pay.gateway.channel.H5ailiPay.util.QRCodeUtil;
import com.pay.gateway.config.common.Common;
import com.pay.gateway.config.redis.RedisUtil;
import com.pay.gateway.entity.BankCard;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.service.OrderService;
import com.pay.gateway.util.JsonResult;
import com.pay.gateway.util.SendUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
@Controller
@RequestMapping("/api")
public class PayContorller {
	 @Value("${deal.url.path}")
	 private String dealurl;
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
	 Logger log = LoggerFactory.getLogger(PayContorller.class);
	/**
	 * <p>跳转到PC二维码页面</p>
	 * @return
	 */
	@GetMapping("/startOrder")
	public String startOrder(Model m,HttpServletRequest request) throws Exception {
		HashMap<String,String> decryptionParam = sendUtil.decryptionParam(request);
		String order = decryptionParam.get("order");
		String amount = decryptionParam.get("amount");
		String data = decryptionParam.get("data");
		DateFormat formatter = new SimpleDateFormat(Common.DATATYPE);
		Date date;
		date = formatter.parse(data);
		log.info("================【请求时间戳为："+data+"】===============");
		boolean expired = DateUtil.isExpired(date,DateField.SECOND,300,new Date());//请求300秒过期
		if(!expired) {
			log.info("================【订单过期】===============");
			return "/orderEr";
		}
		log.info("================【进入到第一次页面跳转处理类】===============");
		 String serverName = request.getServerName();
		 int serverPort = request.getServerPort();
		 
		 log.info("================【页面展示：金额："+amount+"，订单号："+order+"】===============");
		 String[] split = amount.split(",");
		 amount = split[0];
		 /**
		  * <p>生成二维码到本地</p>
		  */
		 Map<String, Object> careteParam = sendUtil.careteParam("order="+order+"&amount="+amount);
		 String params = HttpUtil.toParams(careteParam);
		QRCodeUtil.encode(
				"http://"+serverName+":"+serverPort+"/api/payAli"+"?"+params,
				imgpath,
				true,order);
		log.info("================【成功生成二维码：url："+"http://"+serverName+":"+serverPort+"/api/payAli"+"?order="+order+"&amount="+amount+"，订单号："+order+"】===============");
		String url =  ""+serverName+":"+serverPort+"/api/payAli"+"?"+params ;
		m.addAttribute("url", url);
		m.addAttribute("order", order);
		m.addAttribute("amount", amount);
		 log.info("================【页面展示：金额："+amount+"，订单号："+order+"】===============");
		return "/pc";
	}
	/**
	 * <p>生成支付宝类的连接跳转</p>
	 * @param order
	 * @param amount
	 * @param m
	 * 這裏要查找銀行卡來生成支付寳的跳轉鏈接
	 * @throws Exception 
	 */
	@GetMapping("/payAli")
	public String payAli(HttpServletRequest request, Model m) throws Exception {
		HashMap<String,String> decryptionParam = sendUtil.decryptionParam(request);
		String order = decryptionParam.get("order");
		String amount = decryptionParam.get("amount");
		log.info("参数解密金额："+amount);
		log.info("参数解密订单："+order);
		log.info("================【飞行页面转发】===============");
		log.info("【金额："+amount+"，订单："+order+"】");
		m.addAttribute("order", order);//订单号
		m.addAttribute("amount", amount);//金额
		return "/alipay";
	}
	
	
	/**
	 * <p>自執行函數一部請求生成訂單和請求支付包鏈接</p>
	 * @param order
	 * @param amount
	 * @param m
	 * @return
	 */
	@ResponseBody
	@PostMapping("/createOrder")
	@Transactional
	public JsonResult createOrder(String order ,String amount,Model m) {
		log.info("【缓存金额获取为："+amount+"】");
		if(ObjectUtil.isNull(amount))
			return JsonResult.buildFailResult();
		log.info("=========【全局訂單:order="+order+"，全局訂單金額：amount="+amount+"，正在生成訂單】============");
		DealOrder dealOrder = orderServiceImpl.findOrderByOrderAll(order);
		if(ObjectUtil.isNotNull(dealOrder)) {//当订单存在的时候不在创建直接返回订单相关信息从缓存中获取当前
			if(Common.ORDERDEASTATUS_T.equals(dealOrder.getOrderStatus())) {
				log.info("|------【当前订单已生成，进入金额筛选算法，订单号为："+dealOrder.getOrderId()+"】");
				BankCard bankcard = bankCardServiceImpl.findBankCardByBankCardId(dealOrder.getDealCardId());
				String object = (String) redisUtil.get(bankcard.getBankPhone());
				ArrayList<String> bankListuse = CollUtil.newArrayList(object.toString());//该银行卡目前正在使用的所有的key    key = 银行唯一标识(非卡号) + 交易金额    value = 全局订单号
				for( String key : bankListuse) {
					Object object2 = redisUtil.get(key);
					if(null != object2) {
						if(dealOrder.getAssociatedId().equals(object2)) {//当 value和全局订单号相等的时候
							String amountK = StrUtil.subSuf(key.toString(),Common.BANKCARD_AMOUNT_BUMBER);//金额
							bankcard.setDealAmount(new BigDecimal(amountK));
							return JsonResult.buildSuccessResult(bankcard);
						}
					}
				}
			}else {
				return JsonResult.buildFailResult();
			}
		}else {
			BankCard bankCard = orderServiceImpl.createOrder(order,amount.toString());
			if(ObjectUtil.isNotNull(bankCard)) {
				return JsonResult.buildSuccessResult(bankCard);
			}
			log.info("=========【交易订单生成失败】============");
			return null;
		}
		log.info("=========【交易订单生成失败】============");
		return null;
		
	}
	
	
	

}
