package com.pay.gateway.service.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.gateway.config.common.Common;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.entity.RunningOrder;
import com.pay.gateway.entity.User;
import com.pay.gateway.entity.UserAccount;
import com.pay.gateway.entity.WithdrawalsOrder;
import com.pay.gateway.mapper.RunningOrderMapper;
import com.pay.gateway.service.RunningOrderService;
import com.pay.gateway.service.UserAccountService;
import com.pay.gateway.service.UserService;
import com.pay.gateway.util.DealNumber;

import cn.hutool.core.util.StrUtil;
@Service
public class RunningOrderServiceImpl implements RunningOrderService {
	Logger log = LoggerFactory.getLogger(RunningOrderServiceImpl.class);
	@Autowired
	RunningOrderMapper runningOrderDao;
	@Autowired 
	UserAccountService userAccountServiceImpl;
	@Autowired
	UserService  userServiceImpl;
	@Override
	public boolean createDealRun(DealOrder dealOrder, Integer runStatus) {
		log.info("---------进入交易流水处理类，生成交易流水，流水方式为交易金额，当前交易金额为："+dealOrder.getDealAmount()+"，实际到账金额为："+dealOrder.getActualAmount().toString()+"");
		RunningOrder runBean  = new RunningOrder();
		runBean.setOrderRunId(DealNumber.GetRunOrder());
		runBean.setOrderId(dealOrder.getOrderId());
		runBean.setRunStatus(runStatus);
		runBean.setRunType(Common.RUN_TYPE_DEAL);
		runBean.setOrderAccount(dealOrder.getOrderAccount());
		runBean.setRunOrderAmount(dealOrder.getActualAmount().toString());
		runBean.setOrderGenerationIp(dealOrder.getOrderGenerationIp());
		runBean.setDealDescribe("用户正常交易流水");
		runBean.setCardRunD(dealOrder.getOrderAccount());
		runBean.setCardRunW("SYS");//系統賬戶簡稱
		runBean.setCardNameRunW(StrUtil.isNotBlank(dealOrder.getDealCardId())?dealOrder.getDealCardId():"");
		runBean.setRetain1(dealOrder.getDealChannel());//渠道类型
		runBean.setRetain2(dealOrder.getRetain4());//交易产品
		int insertSelective = runningOrderDao.insertSelective(runBean);
		boolean flag = insertSelective > 0 && insertSelective < 2;
		if(flag) {
			log.info("【当前流水生成成功，流水金额："+dealOrder.getActualAmount().toString()+"】");
			log.info("【若当前账户存在代理商，则开始计算代理商利润当前账户："+dealOrder.getOrderAccount()+"】");
			log.info("【通过商户号查找账户号】");
			UserAccount userAccount  = userAccountServiceImpl.findUserByAccount(dealOrder.getOrderAccount());
			if(StrUtil.isNotBlank(userAccount.getUserId())) {
				User user = userServiceImpl.findUserByuserId(userAccount.getUserId());
				if(StrUtil.isBlank(user.getRetain4())) {
					log.info("【当前账户不存在代理商】");
				}else {
					log.info("【当前账号存在代理商】");
					User agent = userServiceImpl.findUserByuserId(user.getRetain4());//代理商
					String fee = agent.getRetain1();//代理商费率
					if(StrUtil.isNotBlank(fee)) {
						String money = agent.getRetain3();//代理商当前金额
						BigDecimal amonut = new BigDecimal(StrUtil.isNotBlank(money)?money:"0");
						BigDecimal dealAmount = dealOrder.getDealAmount();
						BigDecimal fee1 = new BigDecimal(fee);
						BigDecimal agentMount = dealAmount.multiply(fee1);//代理商分润
						log.info("【代理商分润："+agentMount+"，代理商分润费率："+fee1+"，代理商现有金额："+amonut+"】");
						amonut = amonut.add(agentMount);
						agent.setRetain3(amonut.toString());
						User agentUser = new User();
						agentUser.setId(agent.getId());
						agentUser.setUserId(agent.getUserId());
						agentUser.setRetain3(agent.getRetain3());
						agentUser.setCreateTime(null);
						agentUser.setSubmitTime(null);
						boolean flag1 = userServiceImpl.updataUserById(agent);
						if(flag1) {
							log.info("【当前代理商分润更新成功】");
						}else {
							return flag1;
						}
					}
				}
			}
		}else {
			log.info("当前流水生成失败，流水金额："+dealOrder.getActualAmount().toString()+"");
		}
		return flag;
	}
	@Override
	public boolean createDealRunFee(DealOrder dealOrder, Integer runStatus) {
		log.info("---------【进入交易流水处理类，生成交易手续费流水，流水方式为交易手续费，当前交易金额为："+dealOrder.getDealAmount()+"，交易手续费金额为："+dealOrder.getDealFee().toString()+"】");
		RunningOrder runBean  = new RunningOrder();
		runBean.setOrderRunId(DealNumber.GetRunOrder());
		runBean.setOrderId(dealOrder.getOrderId());
		runBean.setRunStatus(runStatus);
		runBean.setRunType(Common.RUN_DEAL_FEE);
		runBean.setOrderAccount(dealOrder.getOrderAccount());
		runBean.setRunOrderAmount(dealOrder.getDealFee().toString());
		runBean.setOrderGenerationIp(dealOrder.getOrderGenerationIp());
		runBean.setDealDescribe("用户正常交易流水手续费");
		runBean.setCardRunD(dealOrder.getOrderAccount());
		runBean.setCardRunW("SYS");//系統賬戶簡稱
		runBean.setCardNameRunW(StrUtil.isNotBlank(dealOrder.getDealCardId())?dealOrder.getDealCardId():"");
		int insertSelective = runningOrderDao.insertSelective(runBean);
		boolean flag = insertSelective > 0 && insertSelective < 2;
		if(flag) {
			log.info("【当前流水生成成功，流水金额："+dealOrder.getActualAmount().toString()+"】");
		}else {
			log.info("【当前流水生成失败，流水金额："+dealOrder.getActualAmount().toString()+"】");
		}
		return flag;
	}
	@Override
	public boolean createMerchantsRun(WithdrawalsOrder order, Integer runStatus) {
		log.info("---------【进入交易流水处理类，生成交易流水，流水方式为代付冻结，当前代付金额为："+ order.getWithdrawalsAmount()+"，实际到账金额为："+order.getActualAmount().toString()+"】");
		RunningOrder runBean  = new RunningOrder();
		runBean.setOrderRunId(DealNumber.GetRunOrder());
		runBean.setOrderId(order.getOrderId());
		runBean.setRunStatus(runStatus);
		runBean.setRunType(Common.RUN_DPAY_FREEZE);
		runBean.setOrderAccount(order.getOrderAccount());
		runBean.setRunOrderAmount(order.getActualAmount().toString());
		runBean.setOrderGenerationIp(order.getOrderGenerationIp());
		runBean.setDealDescribe("用户代付资金账户冻结");
		runBean.setCardRunD("SYS");//系統賬戶簡稱
		runBean.setCardRunW(order.getOrderAccount());
		int insertSelective = runningOrderDao.insertSelective(runBean);
		boolean flag = insertSelective > 0 && insertSelective < 2;
		if(flag) {
			log.info("【当前流水生成成功，流水金额："+order.getActualAmount().toString()+"】");
		}else {
			log.info("【当前流水生成失败，流水金额："+order.getActualAmount().toString()+"】");
		}
		return flag;
	}
	@Override
	public boolean createMerchantsFeeRun(WithdrawalsOrder order, Integer runStatus) {

		log.info("---------【进入交易流水处理类，生成交易流水，流水方式为代付冻结，当前代付金额为："+ order.getWithdrawalsAmount()+"，实际到账金额为："+order.getActualAmount().toString()+"，代付手续费为："+order.getWithdrawalsFee());
		RunningOrder runBean  = new RunningOrder();
		runBean.setOrderRunId(DealNumber.GetRunOrder());
		runBean.setOrderId(order.getOrderId());
		runBean.setRunStatus(runStatus);
		runBean.setRunType(Common.RUN_DPAY_FEE_FREEZE);
		runBean.setOrderAccount(order.getOrderAccount());
		runBean.setRunOrderAmount(order.getWithdrawalsFee().toString());
		runBean.setOrderGenerationIp(order.getOrderGenerationIp());
		runBean.setDealDescribe("用户代付手续费资金账户冻结");
		runBean.setCardRunD(order.getOrderAccount());//系統賬戶簡稱
		runBean.setCardRunW("SYS");
		int insertSelective = runningOrderDao.insertSelective(runBean);
		boolean flag = insertSelective > 0 && insertSelective < 2;
		if(flag) {
			log.info("【当前流水生成成功，流水金额："+order.getWithdrawalsFee().toString()+"】");
		}else {
			log.info("【当前流水生成失败，流水金额："+order.getWithdrawalsFee().toString()+"】");
		}
		return flag;
	
	}
}
