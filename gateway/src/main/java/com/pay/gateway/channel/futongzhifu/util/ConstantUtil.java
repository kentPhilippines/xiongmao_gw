package com.pay.gateway.channel.futongzhifu.util;
/**
 * 参数配置
 * 请找客服获取相关参数配置
 */
public class ConstantUtil {
    //商户号
    public static final String MERCHANT_ACCOUNT = "你的商户号"; // 富通支付系统提供的商户号
    //商户支付秘钥
    public static final String PAY_KEY = "你的支付秘钥"; //富通支付提供的支付秘钥
    //商户代付秘钥
    public static final String TRANSFER_KEY = "你的代付秘钥"; ///富通支付提供的代付秘钥
    //签名类型
    public static final String SIGN_TYPE = "MD5";
    //代付请求地址
    private static final String REMIT_BASE_URL = "http://remit.fulltonepay.com";
    public static final String REMIT_URL = REMIT_BASE_URL + "/remit/transfer";  //代付申请
    public static final String REMIT_ORDER_URL = REMIT_BASE_URL + "/remit/order"; //代付订单查询
    public static final String BALANCE_QUERY_URL = REMIT_BASE_URL + "/balance/query"; //账户余额
    //支付请求地址
    private static final String PAY_BASE_URL = "https://api.fulltonepay.com";
    public static final String PAY_URL = PAY_BASE_URL + "/pay/webPay";  //支付请求路径 web Form表单提交
    public static final String PAY_ORDER_URL = PAY_BASE_URL + "/pay/order"; //支付订单查询
}
