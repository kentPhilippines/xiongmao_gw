package com.pay.gateway.channel.futongzhifu.notfiy;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.pay.gateway.service.OrderService;
import com.pay.gateway.util.NotifyUtil;
import com.pay.gateway.util.OrderUtil;

@Controller("/channel")
public class FutongzhifuNotfiy {
	Logger log = LoggerFactory.getLogger(FutongzhifuNotfiy.class);
	@Autowired
	OrderService orderServiceImpl;
	
	@Resource
	OrderUtil orderUtil;
	@Resource
	NotifyUtil notifyUtil;
	@PostMapping("/futongzhifuNotfiy")
	public void futongzhifuNotfiy(HttpServletRequest request ,HttpServletResponse response) {
		log.info("【进入拼多多回调处理】");
	}

}
