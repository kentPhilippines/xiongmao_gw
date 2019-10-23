package com.pay.gateway.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pay.gateway.channel.H5ailiPay.service.H5aliPayService;
import com.pay.gateway.service.AccountFeeService;
import com.pay.gateway.service.AccountService;
import com.pay.gateway.service.ChannelService;
import com.pay.gateway.service.OrderService;
import com.pay.gateway.util.RequestUtil;

@Controller
@RequestMapping("/deal")
public class DealContorller {
	@Autowired
	AccountService accountServiceImpl;
	@Autowired
	AccountFeeService accountFeeServiceImpl;
	@Autowired
	ChannelService channelServiceImpl;
	@Autowired
	OrderService orderServiceImpl;
	@Autowired
	H5aliPayService H5aliPayServiceImpl;
	@Autowired
	RequestUtil requestUtil;
	Logger log = LoggerFactory.getLogger(DealContorller.class);
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
