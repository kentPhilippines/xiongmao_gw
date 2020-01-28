package com.pay.gateway.config.listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.pay.gateway.util.SettingFile;

import cn.hutool.core.io.FileUtil;
import cn.hutool.setting.Setting;
@Configuration
public class EnvironmentConfig implements EnvironmentAware{
	Logger log = LoggerFactory.getLogger(EnvironmentConfig.class);
	@Autowired
	SettingFile settingFile;
	@Override
	public void setEnvironment(Environment environment) {
		// 获取操作系统信息
		log.info("操作系统的名称：" + environment.getProperty("os.name"));
		log.info("操作系统的构架：" + environment.getProperty("os.arch"));
		log.info("操作系统的版本：" + environment.getProperty("os.version"));
		// 获取配置信息
		Setting setting = new Setting(FileUtil.touch("/config/GW/gateway.setting") , null, true);
		settingFile.setSetting(setting);
		String settingPath = setting.getSettingPath();
		log.info("-------->>配置文件加载-----"+settingPath+"");	
		}
	
	
}
