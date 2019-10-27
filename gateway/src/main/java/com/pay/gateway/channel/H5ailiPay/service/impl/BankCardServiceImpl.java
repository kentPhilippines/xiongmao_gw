package com.pay.gateway.channel.H5ailiPay.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.gateway.channel.H5ailiPay.service.BankCardService;
import com.pay.gateway.config.common.Common;
import com.pay.gateway.entity.BankCard;
import com.pay.gateway.entity.BankCardExample;
import com.pay.gateway.entity.BankCardExample.Criteria;
import com.pay.gateway.entity.BankCardRun;
import com.pay.gateway.entity.User;
import com.pay.gateway.entity.UserExample;
import com.pay.gateway.mapper.BankCardMapper;
import com.pay.gateway.mapper.BankCardRunMapper;
import com.pay.gateway.mapper.UserMapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
@Service
public class BankCardServiceImpl implements BankCardService {
	Logger log = LoggerFactory.getLogger(BankCardServiceImpl.class);
	@Autowired
	BankCardMapper bankCardDao;
	@Autowired
	BankCardRunMapper bankCardRunDao;
	@Autowired
	UserMapper userDao;
	@Override
	public List<BankCard> findBankCardAll() {
		BankCardExample example = new BankCardExample();
		Criteria criteria = example.createCriteria();
		criteria.andBankTypeEqualTo(0);
		criteria.andStatusEqualTo(1);
		List<BankCard> selectByExample = bankCardDao.selectByExample(example);
		return selectByExample;
	}
	@Override
	public boolean updataAmountByBankCardId(BankCard bank) {
		log.info("|----【进入银行卡核心处理类，当前位置为修改银行卡记录数据，当前修改的银行卡为："+bank.getBankCard()+"】");
		BankCardExample example = new BankCardExample();
		Criteria criteria = example.createCriteria();
		criteria.andBankCardEqualTo(bank.getBankCard());
		List<BankCard> selectByExample = bankCardDao.selectByExample(example);
		BankCard first = null;
		if(CollUtil.isNotEmpty(selectByExample)) {
			first = CollUtil.getFirst(selectByExample);
			log.info("|----【当前银行卡的交易金额为："+bank.getBankAmount()+"】");
			bank.setBankAmount(first.getBankAmount().add(bank.getBankAmount()));
		}		
		String retain1 = first.getRetain1();
		log.info("|----【当前银行卡的限制交易金额为："+retain1+"】");
		BigDecimal bankAmount = bank.getBankAmount();//儅纍計金額大於銀行卡交易範圍的時候  將銀行卡設置爲不可用
		BigDecimal limitAmount = new BigDecimal(retain1);//銀行卡限制交易金額
		log.info("|----【当前银行卡的同批次累计交易金额为："+bankAmount+"】");
		if(limitAmount.compareTo(bankAmount) == -1){//交易纍計大於交易額度的時候
			bank.setStatus(0);
		}
		bank.setCreateTime(null);
		int updateByExample = bankCardDao.updateByExampleSelective(bank, example);
		return updateByExample > 0 && updateByExample < 2;
	}
	@Override
	public boolean addBankRun(final BankCard bank,final BigDecimal actualAmount,final String accountId,final String orderId) {
		BigDecimal bankAmount = bank.getBankAmount();//银行交易金额 
		String bankCard =  bank.getBankCard();//入款卡号
		BigDecimal dealAmount = bankAmount.subtract(actualAmount);//自有账户分润金额
		BankCardRun entity = new BankCardRun();
		entity.setWithdrawAccount(accountId);
		entity.setWithdrawAmount(bankAmount);
		entity.setWithdrawBankCard(accountId);
		entity.setDealAccount("SYS");
		entity.setDealBankCard(bankCard);
		entity.setDealAmount(dealAmount);
		entity.setRunType(Common.BANKCARD_RUN_BENEFIT);
		entity.setRetain1(orderId);
		int insertSelective = bankCardRunDao.insertSelective(entity);//添加自有商户分润银行卡流水
		boolean flag = insertSelective > 0 && insertSelective < 2;
		entity.setDealAccount(accountId);
		entity.setDealAmount(actualAmount);
		entity.setDealBankCard(bankCard);
		entity.setRunType(Common.BANKCARD_RUN_DEAL);
		int insertSelective2 = bankCardRunDao.insertSelective(entity);//添加下游商户银行卡交易流水
		BankCardExample example = new BankCardExample();
		
		/**
		 * ###################
		 * 	新增卡商利润
		 */
		log.info("------【开始计算码商交易利润】-------");
		Criteria criteria = example.createCriteria();
		criteria.andBankCardEqualTo(bankCard);
		List<BankCard> selectByExampleWithBLOBs = bankCardDao.selectByExampleWithBLOBs(example);//理论上只有一个
		BankCard first = CollUtil.getFirst(selectByExampleWithBLOBs);
		if(Common.BANKCARDTYPE_DEAL.equals(first.getBankType()) && StrUtil.isNotBlank(first.getRetain2())) {//不是本家卡 且为收款卡的时候  计算码商利润
			log.info("------【进入码商利润计算】-------");
			int i = 0;
			UserExample userExample = new UserExample();
			com.pay.gateway.entity.UserExample.Criteria createCriteria = userExample.createCriteria();
			createCriteria.andUserIdEqualTo(first.getLiabilities());
			List<User> selectByExample = userDao.selectByExample(userExample);
			if(CollUtil.isNotEmpty(selectByExample)) {
				User first2 = CollUtil.getFirst(selectByExample);
				log.info("------【获取码商："+first2+"】-------");
				String retain1 = first2.getRetain1();//码商或者代理商的利率
				String retain3 = first2.getRetain3();//码商或者代理商的利润
				//利润 =  以前利润 + 当前利润 
				// 当前利润  = 当前交易额度*当前利率
				BigDecimal money = new BigDecimal(StrUtil.isBlank(retain3)?"0":retain3);//利润
				BigDecimal decimal = dealAmount.add(actualAmount);//当前交易额度
				BigDecimal re = new BigDecimal(StrUtil.isBlank(retain1)?"0":retain1);//利率
				BigDecimal multiply = decimal.multiply(re);//当前交易利润
				money = money.add(multiply);
				log.info("------【码商交易利润计算完毕，当前分润码商："+first2.getUserId()+"，当前交易金额计算码商分润本次金额为："+multiply+"，当前码商总分润金额为："+money+"】-------");
				first2.setRetain3(money.toString());
				i = userDao.updateByExampleSelective(first2, userExample);
				log.info("【码商利润调整完毕】");
			}
		}
		boolean flag1 = insertSelective2 > 0 && insertSelective2 < 2;
		if(flag && flag1 ) {
			return true ;
		}
		return false;
	}
	@Override
	public BankCard findBankCardByBankCardId(String dealCardId) {
		BankCardExample example = new BankCardExample();
		Criteria criteria = example.createCriteria();
		criteria.andBankCardEqualTo(dealCardId);
		List<BankCard> selectByExample = bankCardDao.selectByExample(example);
		if(CollUtil.isNotEmpty(selectByExample)) 
			return CollUtil.getFirst(selectByExample);
		return null;
	}
}
