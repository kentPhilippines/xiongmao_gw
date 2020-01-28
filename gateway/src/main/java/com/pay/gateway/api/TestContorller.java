package com.pay.gateway.api;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pay.gateway.config.redis.RedisUtil;
import com.pay.gateway.util.SettingFile;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;

@Controller
@RequestMapping("/test1234569")
public class TestContorller {
	@Autowired
	SettingFile settingFile;
	
	 @Autowired
	 RedisUtil redisUtil;
	@RequestMapping("/test")
	public void TestConnection(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.getWriter().write("项目启动正常<br>");
		Setting setting = settingFile.getSetting();
		boolean load = setting.load();
		if(load) {
			response.getWriter().write("获取配置文件<br>");
		}
		Set<String> keySet = setting.keySet();
		Collection<String> values = setting.values();
		for( String key : keySet ) {
			String context = setting.get(key);
			response.getWriter().write(key+" = "+context+"<br>");
		}
		response.getWriter().write( "<br>");
		response.getWriter().write( "<br>");
		response.getWriter().write( "<br>");
		response.getWriter().write( "<br>");
		response.getWriter().write( "<br>");
		response.getWriter().write( "<br>");
		response.getWriter().write( "<br>");
		response.getWriter().write( "<br>");
		response.getWriter().write( "<br>");
		response.getWriter().write( "<br>");
		response.getWriter().write( "<br>");
		response.getWriter().write( "<br>");
		response.getWriter().write( "<br>");
		String parameter = request.getParameter("key");
		if(StrUtil.isNotBlank(parameter)) {
			response.getWriter().write("缓存查询的结果为："+"<br>");
			response.getWriter().write("查询的key："+parameter+ "<br>");
			Object object = redisUtil.get(parameter);
			response.getWriter().write("value = "+object+"<br>");
		}
		
		
		
		
		
		
		
	}
}