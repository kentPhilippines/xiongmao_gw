package com.pay.gateway.util;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.gateway.api.MyDealContorller;
import com.pay.gateway.channel.H5ailiPay.service.BankCardService;
import com.pay.gateway.config.common.Common;
import com.pay.gateway.entity.Account;
import com.pay.gateway.entity.AccountFee;
import com.pay.gateway.entity.BankCard;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.service.AccountFeeService;
import com.pay.gateway.service.AccountService;
import com.pay.gateway.service.ChannelService;
import com.pay.gateway.service.OrderService;
import com.pay.gateway.service.RunningOrderService;

/**
 * ########################################
 * 目前该方法计算了总流水金额和银行卡流水金额，要想获取
 * 渠道交易流水，就可以将总流水金额减去银行卡流水金额则为 
 * 四方交易金额
 * ########################################
 * @author K
 */
@Service
public class OrderUtil {
	@Autowired
	OrderService orderServiceImpl;
	@Autowired
	RunningOrderService RunningOrderServiceImpl;
	@Autowired
	AccountService accountServiceImpl;
	@Autowired
	AccountFeeService accountFeeServiceImpl;
	@Autowired
	BankCardService BankCardServiceImpl;
	Logger log = LoggerFactory.getLogger(OrderUtil.class);
	/**
	 * <p>根据全局订单号修改相应的流水和账变记录</p>
	 * <li>這個是儅交易成功我方收到其他系統的匯款時候調用的方法</li>
	 * <li>這慎重調用</li>
	 * @param orderIdAll
	 */
	@Transactional
	public synchronized boolean updataOrderStatus(String orderIdAll) {
		return updataOrderStatus(orderIdAll,Common.RUN_STATUS_1);
	}
	
	
	
	/**
	 * <p>订单处理【三方资金处理】</p> 
	 * @param orderIdAll				全局订单
	 * @param runStatus					处理状态     自然处理   人工处理
	 * @return
	 */
	public synchronized boolean updataOrderStatus(String orderIdAll,Integer runStatus) {
		log.info("|---------【进入订单修改核心处理类，捕获全局订单编号："+orderIdAll+"】");
		 //修改訂單狀態
		 boolean flag = orderServiceImpl.updataOrderStatusByAssociatedId(orderIdAll);
		 DealOrder dealOrder = orderServiceImpl.findOrderByOrderAll(orderIdAll);
		 //生成流水 總共會生成2筆流水
		 boolean runAmount = RunningOrderServiceImpl.createDealRun(dealOrder,runStatus);//交易流水
		 boolean runFee = RunningOrderServiceImpl.createDealRunFee(dealOrder,runStatus);//費率流水
		 if(!(runAmount || runFee)) 
				return false;
		 //修改賬戶金額變動
		 Account account = accountServiceImpl.findAccountByAppId(dealOrder.getOrderAccount());
		 AccountFee accountFee = accountFeeServiceImpl.findAccountFeeByFeeId(Integer.valueOf(dealOrder.getRetain2()));
		 boolean flagAccount = updataAccountRunAmount(account,accountFee,dealOrder);
		 if(flagAccount && runFee && runAmount && flag)
			 return true;
		return false;
	}
	/**
	 * <p>根據當前賬戶詳情修改金額</p>
	 * @param account				當前賬戶詳情
	 * @param accountFee			當前賬戶手續費
	 * @param dealOrder				當前用戶成交訂單
	 * @return
	 */
	private synchronized boolean updataAccountRunAmount(Account account, AccountFee accountFee, DealOrder dealOrder) {
		log.info("|---------【进入个人账户资金变动核心处理类，当前处理账户为："+account.getAccountId()+"】");
		/**
		 * ################################
		 * 修改個人賬戶邏輯
		 * 1,獲取T1凍結 D1 凍結
		 * 2,獲取賬戶餘額,可取現餘額,凍結總額(凍結總額=T1凍結+D1凍結)
		 * 3,獲取凍結比例,獲取賬戶類型(T1或者D1)
		 * 4,獲取今日交易金額,獲取纍計交易金額
		 * 5,獲取實際到賬金額和交易金額(交易訂單)
		 * 6,計算T1或者D1凍結
		 * 7,計算今日交易金額纍計
		 * 8,計算交易金額纍計
		 * 9,計算賬戶餘額
		 * 10,計算縂凍結金額(T1+D1)
		 * 11,計算可提現縂額
		 * 12,計算賬戶餘額
		 * 13,獲取交易銀行卡
		 * 14,記錄銀行卡交易數據
		 * 15,告知數據流水生成成功
		 * ################################
		 */
		//1,獲取凍結
		 BigDecimal freezeD1 = account.getFreezeD1();//D1凍結
		 BigDecimal freezeT1 = account.getFreezeT1();//T1凍結
		//2,獲取賬戶餘額,可取現餘額,凍結總額
		 BigDecimal accountBalance = account.getAccountBalance();
		 BigDecimal cashBalance = account.getCashBalance();//可提現
		 BigDecimal freezeBalance = account.getFreezeBalance();
		//3,獲取凍結比例,凍結類型
		 String settlementType = accountFee.getSettlementType();//
		 String accountSette = accountFee.getAccountSette();//凍結比例  該比例修改之後才生效  但是修改之前的比例不作數
		 //4,獲取今日交易金額,獲取纍計交易金額
		 BigDecimal sumDealAmount = account.getSumDealAmount();//纍計交易金額
		 BigDecimal sumDealToDayAmount = account.getSumDealToDayAmount();//當日纍計交易金額 
		 //5,獲取實際到賬金額和交易金額(交易訂單)
		 BigDecimal dealAmount = dealOrder.getDealAmount();//訂單交易金額  該金額并不等於實際到賬金額
		 BigDecimal actualAmount = dealOrder.getActualAmount();//交易實際到賬金額
		 //6,計算T1或者D1凍結      實際到賬金額*凍結比例
		 BigDecimal sette = new BigDecimal(accountSette).divide(new BigDecimal("100"));//凍結比例
		 log.info("冻结比例："+new BigDecimal("1").subtract(sette));
		 BigDecimal freeze = actualAmount.multiply(new BigDecimal("1").subtract(sette));//實際凍結金額
		 log.info("冻结金额："+freeze);
		 //7,計算今日交易金額纍計
		 sumDealToDayAmount = sumDealToDayAmount.add(dealAmount);
		 //8,計算交易金額纍計
		 sumDealAmount = sumDealAmount.add(dealAmount);
		 //9,計算賬戶餘額 (凍結金額+可提現金額)
		 log.info("冻结模式："+settlementType);
		 if(Common.FREEZE_D1.equalsIgnoreCase(settlementType))//當爲D1凍結的時候將凍結資金放入D1凍結賬戶
			 account.setFreezeD1(freezeD1.add(freeze));
		 else 
			 account.setFreezeT1(freezeT1.add(freeze));
		 BigDecimal freezeD12 = account.getFreezeD1(); 
		 BigDecimal freezeT12 = account.getFreezeT1();
		 freezeBalance = freezeT12.add(freezeD12);
		 BigDecimal subtract = actualAmount.subtract(freeze);//實際可取現金額
		 cashBalance = cashBalance.add(subtract);//新建可取現金額
		 accountBalance = cashBalance.add(freezeBalance);
		 account.setAccountBalance(accountBalance);
		 account.setCashBalance(cashBalance);
		 account.setFreezeBalance(freezeBalance);
		 account.setFreezeD1(freezeD12);
		 log.info("D1冻结："+freezeD12);
		 account.setFreezeT1(freezeT12);
		 log.info("T1冻结："+freezeT12);
		 account.setSumDealAmount(sumDealAmount);
		 account.setSumDealToDayAmount(sumDealToDayAmount);
		 //14,記錄銀行卡交易數據
		 String dealCardId = dealOrder.getDealCardId();//交易銀行卡
		 BankCard bank = new BankCard();
		 bank.setBankCard(dealCardId);
		 bank.setBankAmount(dealAmount);//新增交易金额到银行卡余额上
		 boolean flag = BankCardServiceImpl.updataAmountByBankCardId(bank);
		 bank.setBankAmount(dealAmount);//因为改变银行卡余额导致这里银行卡余额会变为银行卡总余额,这里是类的继承关系
		 boolean addBankRun = BankCardServiceImpl.addBankRun(bank,actualAmount,account.getAccountId(),dealOrder.getOrderId());
		 log.info("个人账变记录详情："+account.toString());
		 boolean acc = accountServiceImpl.updataAccountByAcoountId(account);
		 log.info("修改 银行卡流水的结果为："+addBankRun+"，增加银行卡账变记录："+flag+"，增加账户账变记录的结果："+acc+"");
		 if(flag && acc && addBankRun) {
			 return true;
		 }
		return false;
	}
	
	private synchronized boolean createAccountRunAmount(Account account, AccountFee accountFee, DealOrder dealOrder) {
		log.info("|---------【进入个人账户资金变动核心处理类，当前处理账户为："+account.getAccountId()+"，该处理类为四方账户专用处理】");
		/**
		 * ################################
		 * 修改個人賬戶邏輯
		 * 1,獲取T1凍結 D1 凍結
		 * 2,獲取賬戶餘額,可取現餘額,凍結總額(凍結總額=T1凍結+D1凍結)
		 * 3,獲取凍結比例,獲取賬戶類型(T1或者D1)
		 * 4,獲取今日交易金額,獲取纍計交易金額
		 * 5,獲取實際到賬金額和交易金額(交易訂單)
		 * 6,計算T1或者D1凍結
		 * 7,計算今日交易金額纍計
		 * 8,計算交易金額纍計
		 * 9,計算賬戶餘額
		 * 10,計算縂凍結金額(T1+D1)
		 * 11,計算可提現縂額
		 * 12,計算賬戶餘額
		 * ################################
		 */
		//1,獲取凍結
		 BigDecimal freezeD1 = account.getFreezeD1();//D1凍結
		 BigDecimal freezeT1 = account.getFreezeT1();//T1凍結
		//2,獲取賬戶餘額,可取現餘額,凍結總額
		 BigDecimal accountBalance = account.getAccountBalance();
		 BigDecimal cashBalance = account.getCashBalance();//可提現
		 BigDecimal freezeBalance = account.getFreezeBalance();
		//3,獲取凍結比例,凍結類型
		 String settlementType = accountFee.getSettlementType();//
		 String accountSette = accountFee.getAccountSette();//凍結比例  該比例修改之後才生效  但是修改之前的比例不作數
		 //4,獲取今日交易金額,獲取纍計交易金額
		 BigDecimal sumDealAmount = account.getSumDealAmount();//纍計交易金額
		 BigDecimal sumDealToDayAmount = account.getSumDealToDayAmount();//當日纍計交易金額 
		 //5,獲取實際到賬金額和交易金額(交易訂單)
		 BigDecimal dealAmount = dealOrder.getDealAmount();//訂單交易金額  該金額并不等於實際到賬金額
		 BigDecimal actualAmount = dealOrder.getActualAmount();//交易實際到賬金額
		 //6,計算T1或者D1凍結      實際到賬金額*凍結比例
		 BigDecimal sette = new BigDecimal(accountSette).divide(new BigDecimal("100"));//凍結比例
		 log.info("冻结比例："+new BigDecimal("1").subtract(sette));
		 BigDecimal freeze = actualAmount.multiply(new BigDecimal("1").subtract(sette));//實際凍結金額
		 log.info("冻结金额："+freeze);
		 //7,計算今日交易金額纍計
		 sumDealToDayAmount = sumDealToDayAmount.add(dealAmount);
		 //8,計算交易金額纍計
		 sumDealAmount = sumDealAmount.add(dealAmount);
		 //9,計算賬戶餘額 (凍結金額+可提現金額)
		 log.info("冻结模式："+settlementType);
		 if(Common.FREEZE_D1.equalsIgnoreCase(settlementType))//當爲D1凍結的時候將凍結資金放入D1凍結賬戶
			 account.setFreezeD1(freezeD1.add(freeze));
		 else 
			 account.setFreezeT1(freezeT1.add(freeze));
		 BigDecimal freezeD12 = account.getFreezeD1(); 
		 BigDecimal freezeT12 = account.getFreezeT1();
		 freezeBalance = freezeT12.add(freezeD12);
		 BigDecimal subtract = actualAmount.subtract(freeze);//實際可取現金額
		 cashBalance = cashBalance.add(subtract);//新建可取現金額
		 accountBalance = cashBalance.add(freezeBalance);
		 account.setAccountBalance(accountBalance);
		 account.setCashBalance(cashBalance);
		 account.setFreezeBalance(freezeBalance);
		 account.setFreezeD1(freezeD12);
		 log.info("D1冻结："+freezeD12);
		 account.setFreezeT1(freezeT12);
		 log.info("T1冻结："+freezeT12);
		 account.setSumDealAmount(sumDealAmount);
		 account.setSumDealToDayAmount(sumDealToDayAmount);
		 log.info("个人账变记录详情："+account.toString());
		 boolean acc = accountServiceImpl.updataAccountByAcoountId(account);
		 log.info( "增加账户账变记录的结果："+acc+"");
		 if(acc) {
			 return true;
		 }
		return false;
	}
	/**
	 * <p>四方资金处理</p>
	 * @param orderIdAll
	 * @param runStatus
	 * @return
	 */
	@Transactional
	public synchronized boolean updataOrder(String orderIdAll,Integer runStatus) {
		log.info("|---------【进入订单修改核心处理类，捕获全局订单编号："+orderIdAll+"】");
		 //修改訂單狀態
		 boolean flag = orderServiceImpl.updataOrderStatusByAssociatedId(orderIdAll);
		 DealOrder dealOrder = orderServiceImpl.findOrderByOrderAll(orderIdAll);
		 //生成流水 總共會生成2筆流水
		 boolean runAmount = RunningOrderServiceImpl.createDealRun(dealOrder,runStatus);//交易流水
		 boolean runFee = RunningOrderServiceImpl.createDealRunFee(dealOrder,runStatus);//費率流水
		 if(!(runAmount || runFee)) 
				return false;
		 //修改賬戶金額變動
		 Account account = accountServiceImpl.findAccountByAppId(dealOrder.getOrderAccount());
		 AccountFee accountFee = accountFeeServiceImpl.findAccountFeeByFeeId(Integer.valueOf(dealOrder.getRetain2()));
		 boolean flagAccount = createAccountRunAmount(account,accountFee,dealOrder);
		 if(flagAccount && runFee && runAmount && flag)
			 return true;
		return false;
	}
	/**
	 * <p>四方资金处理</p>
	 * @param orderIdAll
	 * @return
	 */
	public synchronized boolean updataOrder(String orderIdAll) {
		return updataOrder(orderIdAll,Common.RUN_STATUS_1);
	}
	
	

}
