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
	public static final String LOCATION = "LOCATION";//当前项目路径
	public static final String HAOFU_APPID = "HAOFU_APPID";//豪富APPId
	public static final String HAOFU_DEAL_KEY = "HAOFU_DEAL_KEY";//豪富交易密钥
	public static final String HAOFU_ALIPAY_H5 = "HAOFU_ALIPAY_H5";//豪富支付宝H5接口
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
