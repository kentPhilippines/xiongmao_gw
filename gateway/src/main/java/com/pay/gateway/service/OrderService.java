package com.pay.gateway.service;

import com.pay.gateway.entity.BankCard;
import com.pay.gateway.entity.DealOrder;
import com.pay.gateway.entity.OrderAll;

public interface OrderService {

	/**
	 * <p>根据外部商户号和外部订单号查询交易订单</p>
	 * @param appid
	 * @param orderid
	 * @return
	 */
	OrderAll findOrderByTradeId(String appid, String orderid);

	/**
	 * <p>增加交易订单</p>
	 * @param dealOrder
	 * @return
	 */
	Boolean addDealOrder(DealOrder dealOrder);

	/**
	 * <p>生成全局关联订单</p>
	 * @param orderAll
	 * @return
	 */
	Boolean addOrderAll(OrderAll orderAll);

	/**
	 * <p>根据全局订单号和订单金额生成交易订单</p>
	 * @param order
	 * @param amount
	 * @return
	 */
	BankCard createOrder(String order, String amount);

	/**
	 * <p>根據全局訂單修改交易訂單狀態</p>
	 * @param orderIdAll		全局訂單號
	 * @return
	 */
	boolean updataOrderStatusByAssociatedId(String orderIdAll);

	/**
	 * <p>根據全局訂單查找交易訂單詳情</p>
	 * @param orderIdAll
	 * @return
	 */
	DealOrder findOrderByOrderAll(String orderIdAll);

	/**
	 * <p>根據訂單號修改訂單通知狀態為YES</p>
	 * @param orderNo		訂單號
	 * @return
	 */
	boolean updataNotifyYesByNo(String orderNo);
	/**
	 * <p>修改未收到回调的订单状态</p>
	 */
	void updataOrderStatus(Integer second);

	/**
	 * <p>根据订单号查询订单详情</p>
	 * @param orderId
	 * @return
	 */
	DealOrder findOrderByOrderId(String orderId);

	/**
	 * <p>根据全局订单查询全局订单详细数据</p>
	 * @param orderIdAll
	 * @return
	 */
	OrderAll findOrderAllByOrderAll(String orderIdAll);

	/**
	 * <p>创建订单,四方模式</p>
	 * @param orderId
	 * @param dealAmount
	 * @return
	 */
	boolean createOrderNoBankCaed(String orderId, String dealAmount);

	/**
	 * <p>根据订单号修改订单状态为失败</p>
	 * @param orderId
	 * @return
	 */
	boolean updataOrderEr(String orderId);

	/**
	 * <p>将交易订单置为失败并写上失败消息</p>
	 * @param orderId
	 * @param msg
	 * @return
	 */
	Boolean updataOrderByStatusAndMsg(String orderId, String msg);

}
