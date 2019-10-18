package com.pay.gateway.channel.H5ailiPay.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.gateway.channel.H5ailiPay.service.H5aliPayService;
import com.pay.gateway.config.common.Common;
import com.pay.gateway.config.redis.RedisUtil;
import com.pay.gateway.entity.BankCard;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * <p>项目银行卡管理</p>
 * @author K
 */
@Service
public class BankUtil {
	Logger log = LoggerFactory.getLogger(BankUtil.class);
	 @Autowired
	 RedisUtil redisUtil;
     private static BankUtil bankUtil;
     private final Integer  second = 300;
     //初始化 ②
     @PostConstruct 
     public void init() {      
    	 bankUtil = this;
    	 bankUtil.redisUtil = this.redisUtil;
      }
	public Integer getSecond() {
		return second;
	}
	/**
	 * <p>通过关键数据确认收款银行卡</p>
	 * @param bankList			所有目前可以使用的银行卡
	 * @param amount			当前交易金额
	 * @param orderNo			交易订单号(全局)
	 * @return
	 */
     @Transactional
	public  BankCard findDealBankCard(List<BankCard> bankList,BigDecimal amount , String orderNo) {
    	 log.info("可使用银行卡数量："+bankList.size());
    	 if(bankList.size() == 0) {
    		 return null ;
    	 }
		/**
		 * <p>银行卡选择逻辑</p>
		 * <li>1,优先选择目前可以使用的</li>
		 * <li>2.1如果没有银行卡可以使用则选择 5分钟之内 不会出现相同订单金额的银行卡</li>
		 * <li>2.2 每张银行卡 交前都会存入缓存</li>
		 * <li>2.3 再次选择的的时候通过金额和过期时间判断银行卡是否可以使用</li>
		 * 同一个银行卡和金额只要在缓存里面存在的话就不会被选择作为  收款的银行卡
		 */
		Collections.shuffle(bankList); // 随机排列
		Collections.shuffle(bankList); // 随机排列
		Collections.shuffle(bankList); // 随机排列
		Map<String,List> map = new HashMap<String,List>();
		List<String> listk = new ArrayList();
		for(BankCard bank : bankList) {
			String object = (String) bankUtil.redisUtil.get(bank.getBankPhone());//获取当前该银行卡下所有的正在使用的银行卡的配置情况
			String object2 = (String) bankUtil.redisUtil.get(bank.getBankPhone()+amount);
			if(StrUtil.isBlank(object) || StrUtil.isBlank(object2)) {
				//存入缓存并返回可以使用的银行卡
				bankUtil.redisUtil.set(bank.getBankPhone()+amount.toString(),orderNo,second);//4分钟过期时间  存入全局订单   key  银行卡唯一标识 +  金额     value = 全局订单号
				listk.add(bank.getBankPhone()+amount.toString());//手机号 + 金额
				String join = CollUtil.join(listk, ",");
				bankUtil.redisUtil.set(bank.getBankPhone(), join);//手机号+金额  的  所有字符串
				bank.setDealAmount(amount);
				return bank;
			}
			//如果发现没有银行卡可以使用则，获取所有的银行卡正在使用的银行卡
			ArrayList<String> bankListuse = CollUtil.newArrayList(object.toString());
			map.put(bank.getBankPhone(), bankListuse);
		}
		/**
		 * ######获取目前正在使用的所有银行卡他们在4分钟内使用的次数(list集合长度就是银行卡4分钟使用的次数)的最小次数######使用这张卡
		 */
		int size = 0;
		String keys = null; //计算之后这个值就是我要替换的数据key 也就是银行卡
		for(String key : map.keySet()) {//获取最大个数
			//选择最少的使用次数
			List<Object> list = map.get(key);
			if(list.size() > size) {
				size = list.size();
				keys = key;
			};
		}
		for(String key : map.keySet()) {
			List<Object> list = map.get(key);
			if(list.size() < size) {
				size = list.size();
				keys = key;
			};
		}
		/**
		 * ######获取目前正在使用的所有银行卡他们在4分钟内使用的次数(list集合长度就是银行卡4分钟使用的次数)的最小次数######使用这张卡
		 */
		/**
		 * ##这里有可能发生下标越界
		 */
		List<Object> listKeys = map.get(keys);//获取这个银行卡的使用记录key   获取使用最少的银行卡号 拿到这个卡号下面的所有的使用记录
		List keysL = new ArrayList();
		for(Object bankKey: listKeys) {//bankKey 就是具体的银行卡使用记录的  key
			String[] split = bankKey.toString().split(",");
			for(String ketss : split) {
				Object object =  bankUtil.redisUtil.get(ketss.toString());//使用记录
				if(ObjectUtil.isNotNull(object)) {//说明当前缓存内的银行卡使用记录 已过期 删除使用记录汇总
					keysL.add(ketss);//所有目前正在使用的key
				}
			}
		}
		 bankUtil.redisUtil.deleteKey(keys);//删除Key 以及数据
		 String join = CollUtil.join(keysL, ",");
		 bankUtil.redisUtil.set(keys,join);//更新数据
		 log.info("==============【正在使用银行卡号："+keysL.toString()+"】=====================");
		 if(CollUtil.isEmpty(keysL)) {//当前没有正在使用的银行卡的时候
			 Collections.shuffle(bankList);
				for(BankCard bank : bankList) {
					if(ObjectUtil.isNotNull(bank) ) {
						//存入缓存并返回可以使用的银行卡
						bankUtil.redisUtil.set(bank.getBankPhone()+amount.toString(),orderNo,second);//4分钟过期时间
						listk.add(bank.getBankPhone()+amount.toString());
						String join1 = CollUtil.join(listk, ",");
						bankUtil.redisUtil.set(bank.getBankPhone(), join1);
						bank.setDealAmount(amount);
						return bank;
					}
				}
		 }
		//现在集合里面的就是目前该银行卡所有的正在使用的记录
		List amountList = new ArrayList();
		String phoneK = null;
		for(Object k: keysL) {
			phoneK = StrUtil.subPre(k.toString(),Common.BANKCARD_AMOUNT_BUMBER);//理论上是电话号码-且都是一样的数据
			String amountK = StrUtil.subSuf(k.toString(),Common.BANKCARD_AMOUNT_BUMBER);//金额
			amountList.add(new BigDecimal(amountK));
		}
		BigDecimal amount2 = getAmount(amountList,amount);//得到钱数
		bankUtil.redisUtil.set(phoneK+amount2.toString(),orderNo,second);//4分钟过期时间
		List<String> listk1 = new ArrayList();
		log.info("手机号码："+phoneK);//储存的是所有的key
		Object object = bankUtil.redisUtil.get(phoneK);
		
		
		
		ArrayList<String> newArrayList = CollUtil.newArrayList(object.toString());
		newArrayList.add(phoneK+amount2.toString());
		bankUtil.redisUtil.deleteKey(phoneK);//删除Key 以及数据
		String join1 = CollUtil.join(newArrayList, ",");
		bankUtil.redisUtil.set(phoneK,join1);//更新数据
		
		
		
		
		for(BankCard bankCardR : bankList) {
			if(bankCardR.getBankPhone().equals(phoneK)) {
				bankCardR.setDealAmount(amount2);
				return bankCardR;
			}
		}
		return null;
	}
	/**
	 * <p>获取银行卡和金额所对应的订单</p>
	 * @param bankList	 所有在缓存中的银行卡   目前这个金额和卡片是包括订单号的
	 * @param amount	卡池回调金额	
	 * @param bankPhone	银行卡绑定手机号
	 * @return
	 */
	public String findOrderBankCard(BigDecimal amount , String bankPhone) {
		String key = bankPhone + amount.toString() ;
		String orderId = (String) bankUtil.redisUtil.get(key);
		 bankUtil.redisUtil.del(key);//该数据4分钟过期时间
		return orderId;
	}
	private BigDecimal getAmount(List amountList ,BigDecimal  amount) {
		if(amountList.contains(amount)) {//出现金额相同的情况的时候  生成不一样的金额
			amount = amount.add(new BigDecimal("0.01"));//递增的钱数
			return getAmount(amountList,amount);
		}
		return amount;
	}
	
	
	
	/**
	 * <p>该选金额算法需要优化</p>
	 * <p>需求一：尾数不为0</p>
	 * @param amount
	 * @return
	 */
	public synchronized BigDecimal findDealAmount(BigDecimal amount ) {
		Object object = bankUtil.redisUtil.get(amount.toString());
		if(ObjectUtil.isNotNull(object)) {//证明存在数据  要加 0.01
			BigDecimal amount1 = amount.add(new BigDecimal("0.01"));//递增的钱数
			return findDealAmount(amount1);
		}
		String amount1 = amount.toString();
		boolean endWith = StrUtil.endWith(amount1,'0');
		if(endWith) {//如果金额以 0 结尾 就  加一分钱
			amount = amount.add(new BigDecimal("0.01"));
			return	findDealAmount(amount);
		}
		bankUtil.redisUtil.set(amount.toString(),amount.toString(),second);//4分钟过期时间  金额存入
		return amount;
	}
	/**
	 * <p>删除缓存里面的金额</p>
	 * @param amount
	 */
	public void deleteDealAmount(BigDecimal amount ) {
		bankUtil.redisUtil.deleteKey(amount.toString());//删除Key 以及数据
	}
	
	
	
	
	
	
	
	
	
	
}
