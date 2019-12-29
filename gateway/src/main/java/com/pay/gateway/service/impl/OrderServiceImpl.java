package com.pay.gateway.service.impl;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.gateway.channel.H5ailiPay.util.BankUtil;
import com.pay.gateway.config.common.Common;
import com.pay.gateway.config.exception.OtherErrors;
import com.pay.gateway.config.service.PayOrderService;
import com.pay.gateway.entity.Account;
import com.pay.gateway.entity.AccountFee;
import com.pay.gateway.entity.AccountFeeExample;
import com.pay.gateway.entity.BankCard;
import com.pay.gateway.entity.BankCardExample;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.entity.DealOrderExample;
import com.pay.gateway.entity.OrderAll;
import com.pay.gateway.entity.OrderAllExample;
import com.pay.gateway.entity.OrderAllExample.Criteria;
import com.pay.gateway.entity.dealEntity.Deal;
import com.pay.gateway.entity.dealEntity.ResultDeal;
import com.pay.gateway.mapper.AccountFeeMapper;
import com.pay.gateway.mapper.AccountMapper;
import com.pay.gateway.mapper.BankCardMapper;
import com.pay.gateway.mapper.DealOrderMapper;
import com.pay.gateway.mapper.OrderAllMapper;
import com.pay.gateway.service.OrderService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
@Service
public class OrderServiceImpl extends PayOrderService implements OrderService  {
	Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
	@Autowired
	OrderAllMapper orderAllDao;
	@Autowired
	DealOrderMapper dealOrderDao;
	@Autowired
	AccountFeeMapper accountFeeDao;
	@Autowired
	AccountMapper accountDao;
	@Resource
	BankCardMapper  BankCardDao;
	@Resource
	BankUtil bankUtil;
	@Override
	public OrderAll findOrderByTradeId(String appid, String orderid) {
		OrderAllExample example = new OrderAllExample();
		Criteria criteria = example.createCriteria();
		criteria.andOrderAccountEqualTo(appid);
		criteria.andRetain1EqualTo(orderid);
		List<OrderAll> selectByExample = orderAllDao.selectByExample(example);
		if(CollUtil.isNotEmpty(selectByExample))
			return CollUtil.getFirst(selectByExample);
		return null;
	}
	@Override
	public Boolean addOrderAll(OrderAll orderAll) {
		int insertSelective = orderAllDao.insertSelective(orderAll);
		return insertSelective > 0 && insertSelective < 2;
	}
	@Override
	public Boolean addDealOrder(DealOrder dealOrder) {
		int insertSelective = dealOrderDao.insertSelective(dealOrder);
		return insertSelective > 0 && insertSelective < 2;
	}
	@Override
	@Transactional
	public BankCard createOrder(String order, String amount) {
		log.info("=========【订单生成抽象实现类开始执行】============");
		OrderAllExample example = new OrderAllExample();
		Criteria criteria = example.createCriteria();
		criteria.andOrderIdEqualTo(order);
		List<OrderAll> selectByExample = orderAllDao.selectByExample(example);
		OrderAll orderAll = CollUtil.getFirst(selectByExample);//理论上这个值不可能为null
		AccountFeeExample exampleF = new AccountFeeExample();
		com.pay.gateway.entity.AccountFeeExample.Criteria criteriaF = exampleF.createCriteria();
		criteriaF.andIdEqualTo(Integer.valueOf(orderAll.getRetain2()));
		List<AccountFee> selectByExample2 = accountFeeDao.selectByExample(exampleF);
		AccountFee accountFee = CollUtil.getFirst(selectByExample2);//理论上这个值不可能为null
		this.amount = new BigDecimal(amount);
		this.appid = orderAll.getOrderAccount();
		this.tradeId = orderAll.getRetain1();//外部订单号
		this.orderType = orderAll.getOrderType();
		this.dealChannel = accountFee.getAccountChannel();//
		this.orderGenerationIp = orderAll.getOrderIp();
		this.orderAll = orderAll;
		this.accountFee = accountFee ;
		this.notfty = orderAll.getRetain3();
		List<BankCard> bankList = getBankList();
		System.out.println("查询银行卡数量："+bankList.size());
		BankCard findDealBankCard = bankUtil.findDealBankCard(bankList, this.amount, orderAll.getOrderId());
		if(ObjectUtil.isNull(findDealBankCard)) 
			throw new OtherErrors("未获取到银行卡");
		this.dealCardId  =  findDealBankCard.getBankCard();                      
		this.accountFeeId = orderAll.getRetain4();
		this.payType = accountFee.getChannelProduct();
		if(dealOrder()) {
			log.info("===============【交易订单成生成功】==================");
		}
		if(ObjectUtil.isNull(findDealBankCard)) {
			log.info("===============【银行卡获取失败】==================");
			return findDealBankCard;
		}else {
			return findDealBankCard;
		}
	}
	
	
	
	
	/**
	 * <p>银行卡提取</p>
	 * <p>当前收款卡选取只局限于当前卡片是否满额，当前卡片是否为收款卡两个条件，后期会增加相关算法</p>
	 * @return
	 */
	synchronized List<BankCard> getBankList(){
		BankCardExample example = new BankCardExample();
		com.pay.gateway.entity.BankCardExample.Criteria criteriaB = example.createCriteria();
		criteriaB.andStatusEqualTo(1);
		criteriaB.andBankTypeEqualTo(Common.BANKCARDTYPE_DEAL);
		List<BankCard> selectByExample = BankCardDao.selectByExample(example);
		return selectByExample;
	}
	@Override
	public boolean updataOrderStatusByAssociatedId(String orderIdAll) {
		log.info("===============【根据全局订单编号修改交易订单为成功，捕获全局订单编号："+orderIdAll+"】==================");
		DealOrder record = new DealOrder();
		DealOrderExample example = new DealOrderExample();
		com.pay.gateway.entity.DealOrderExample.Criteria criteriaDealOrder = example.createCriteria();
		criteriaDealOrder.andAssociatedIdEqualTo(orderIdAll);
		record.setOrderStatus(Common.ORDERDEASTATUS_SU);
		record.setCreateTime(null);
		int updateByExampleSelective = dealOrderDao.updateByExampleSelective(record,  example);
		boolean flag = updateByExampleSelective > 0 && updateByExampleSelective <2;
		log.info("===============【修改订单完毕：修改结果为："+flag+"】==================");
		return flag ;
	}
	@Override
	public DealOrder findOrderByOrderAll(String orderIdAll) {
		DealOrderExample example = new DealOrderExample();
		com.pay.gateway.entity.DealOrderExample.Criteria criteriaDealOrder = example.createCriteria();
		criteriaDealOrder.andAssociatedIdEqualTo(orderIdAll);
		List<DealOrder> selectByExample = dealOrderDao.selectByExample(example);
		if(CollUtil.isNotEmpty(selectByExample))
			return CollUtil.getFirst(selectByExample);
		return null;
	}
	@Override
	public boolean updataNotifyYesByNo(String orderNo) {
		DealOrder record = new DealOrder();
		DealOrderExample example = new DealOrderExample();
		com.pay.gateway.entity.DealOrderExample.Criteria criteriaDealOrder = example.createCriteria();
		record.setRetain3("YES");
		record.setCreateTime(null);
		criteriaDealOrder.andOrderIdEqualTo(orderNo);
		int updateByExample = dealOrderDao.updateByExample(record, example);
		return updateByExample > 0 && updateByExample < 2;
	}
	/**
	 * <p>修改4分钟之前未收到回调的订单的状态</p>
	 */
	@Override
	public void updataOrderStatus(Integer second) {
		dealOrderDao.updataOrderStatus(second);
	}
	@Override
	public DealOrder findOrderByOrderId(String orderId) {
		DealOrder record = new DealOrder();
		DealOrderExample example = new DealOrderExample();
		com.pay.gateway.entity.DealOrderExample.Criteria criteriaDealOrder = example.createCriteria();
		criteriaDealOrder.andOrderIdEqualTo(orderId);
		List<DealOrder> selectByExample = dealOrderDao.selectByExample(example);
		if(CollUtil.isNotEmpty(selectByExample))
			return CollUtil.getFirst(selectByExample);
		return null;
	}
	@Override
	public OrderAll findOrderAllByOrderAll(String orderIdAll) {
		OrderAllExample example = new OrderAllExample();
		Criteria criteria = example.createCriteria();
		criteria.andOrderIdEqualTo(orderIdAll);
		List<OrderAll> selectByExample = orderAllDao.selectByExample(example);
		if(CollUtil.isNotEmpty(selectByExample))
			return CollUtil.getFirst(selectByExample);
		return null;
	}
	@Override
	public ResultDeal deal(Deal deal, Account account, AccountFee accountFee, OrderAll orderAll) {
		return null;
	}
	@Override
	public boolean createOrderNoBankCaed(String orderId, String dealAmount) {
		log.info("=========【订单生成抽象实现类开始执行，四方模式】============");
		OrderAllExample example = new OrderAllExample();
		Criteria criteria = example.createCriteria();
		criteria.andOrderIdEqualTo(orderId);
		List<OrderAll> selectByExample = orderAllDao.selectByExample(example);
		OrderAll orderAll = CollUtil.getFirst(selectByExample);//理论上这个值不可能为null
		AccountFeeExample exampleF = new AccountFeeExample();
		com.pay.gateway.entity.AccountFeeExample.Criteria criteriaF = exampleF.createCriteria();
		criteriaF.andIdEqualTo(Integer.valueOf(orderAll.getRetain2()));
		List<AccountFee> selectByExample2 = accountFeeDao.selectByExample(exampleF);
		AccountFee accountFee = CollUtil.getFirst(selectByExample2);//理论上这个值不可能为null
		this.amount = new BigDecimal(dealAmount);
		this.appid = orderAll.getOrderAccount();
		this.tradeId = orderAll.getRetain1();//外部订单号
		this.orderType = orderAll.getOrderType();
		this.dealChannel = accountFee.getAccountChannel();//
		this.orderGenerationIp = orderAll.getOrderIp();
		this.orderAll = orderAll;
		this.accountFee = accountFee ;
		this.notfty = orderAll.getRetain3();
		this.accountFeeId = orderAll.getRetain4();
		this.payType = accountFee.getChannelProduct();
		return dealOrder();
	}
	@Override
	public boolean updataOrderEr(String orderId) {
		DealOrder record = new DealOrder();
		DealOrderExample example = new DealOrderExample();
		com.pay.gateway.entity.DealOrderExample.Criteria criteriaDealOrder = example.createCriteria();
		criteriaDealOrder.andOrderIdEqualTo(orderId);
		DealOrder dealOrder = new DealOrder();
		dealOrder.setOrderStatus(Common.ORDERDEASTATUS_ER);
		dealOrder.setCreateTime(null);
		int updateByExampleSelective = dealOrderDao.updateByExampleSelective(dealOrder, example);
		return updateByExampleSelective > 0 && updateByExampleSelective < 2;
	}
	@Override
	public Boolean updataOrderByStatusAndMsg(String orderId, String msg) {
		DealOrder record = new DealOrder();
		DealOrderExample example = new DealOrderExample();
		com.pay.gateway.entity.DealOrderExample.Criteria criteriaDealOrder = example.createCriteria();
		criteriaDealOrder.andOrderIdEqualTo(orderId);
		DealOrder dealOrder = new DealOrder();
		dealOrder.setOrderStatus(Common.ORDERDEASTATUS_ER);
		dealOrder.setDealDescribe(msg);
		dealOrder.setCreateTime(null);
		int updateByExampleSelective = dealOrderDao.updateByExampleSelective(dealOrder, example);
		return updateByExampleSelective > 0 && updateByExampleSelective < 2;
	}
}
