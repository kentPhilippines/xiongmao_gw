package com.pay.gateway.channel.haofuzhifu.util;

import java.util.Arrays;
import java.util.Map;

public class HaofuUtil {
	public static final String HAOFU_URL = "https://mmbpccomsx.6785151.com";
	public static final String  CHANNEL =  "/haofu";
	public static final String  PAYTYPR =  "alipayScan";
	public static final String  NOTFIY =  "/notfiy";
	public static final String IS_SUCCESS_SU = "T";
	public static final String IS_SUCCESS_ER = "F";
	
	
	public static final String  H5 =  "h5";
	public static final String  SM =  "sm";
	
	
	
	/**
	 * map转换成参数串
	 * @param mmap
	 * @return
	 */
	public static String getRequestStr(Map<String,String> mmap){
		StringBuffer sbuf = new StringBuffer(128);
		//对参数名进行ASCII排序
    	Object[] key = mmap.keySet().toArray();
    	Arrays.sort(key);
    	for(int i=0; i<key.length; i++){
    		sbuf.append(key[i]+"="+mmap.get(key[i])+"&");
    	}
    	String sSend = sbuf.toString();
		return sSend.substring(0,sSend.length()-1);
	}
	

}
