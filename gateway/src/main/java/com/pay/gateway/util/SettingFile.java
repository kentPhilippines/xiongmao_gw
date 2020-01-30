package com.pay.gateway.util;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cn.hutool.setting.Setting;

/**
 * <p>外部配置文件处理</p>
 * @author K
 *
 */
@Component
public class SettingFile {
	public static final String LOCATION = "LOCATION";//当前项目ip路径
	public static final String HAOFU_APPID = "HAOFU_APPID";//豪富APPId
	public static final String HAOFU_DEAL_KEY = "HAOFU_DEAL_KEY";//豪富交易密钥
	public static final String HAOFU_ALIPAY_H5 = "HAOFU_ALIPAY_H5";//豪富支付宝H5接口
	
	
	public static final String CHANNEL_HONGXING_ALIPAY_SCAN_URL = "CHANNEL_HONGXING_ALIPAY_SCAN_URL";//红星支付请求支付宝扫码
	public static final String CHANNEL_HONGXING_ALIPAY_H5_URL = "CHANNEL_HONGXING_ALIPAY_H5_URL";//红星支付支付宝H5
	public static final String CHANNEL_HONGXING_APPID = "CHANNEL_HONGXING_APPID";//红星支付 商户号
	public static final String CHANNEL_HONGXING_USERID = "CHANNEL_HONGXING_USERID";//红星支付 用户号
	public static final String CHANNEL_HONGXING_KEY = "CHANNEL_HONGXING_KEY";//交易密钥
	public static final String CHANNEL_HONGXING_DESKEY = "CHANNEL_HONGXING_DESKEY";//交易解密密钥
	public static final String CHANNEL_HONGXING_WECHAR_H5_URL = "CHANNEL_HONGXING_WECHAR_H5_URL";//红星支付微信H5 接口
	
	private Setting setting = new Setting();
	public Setting getSetting() {
		return setting;
	}
	public void setSetting(Setting setting) {
		this.setting = setting;
	}
	
	/**
	 * <p>根据key值获取value</p>
	 * @param key
	 * @return
	 */
	public String getName(String key) {
		String string = setting.get(key);
		return string;
	}
	/**
	 * <p>从新加载配置文件</p>
	 * @return
	 */
	public boolean load() {
		boolean load = setting.load();
		return load;
	}
	
}
