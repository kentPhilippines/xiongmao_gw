package com.pay.gateway.channel.haofuzhifu.util;

/**
 * <p>豪富同步响应参数</p>
 * @author K
 *
 */
public class HaoFuResponseBean {
	/*
	is_success 10 是 成功标志:T/F 
	charset 10 是 编码格式:UTF-8 
	fail_code 3 是 错误码 
	fail_msg 256 是 错误值说明 
	resp_time 3 是 yyyy-MM-dd HH:mm:ss 
	result 256 是 响应链接 
	sign 256 否 签名字符串 
	 */
	private String is_success;
	private String charset;
	private String fail_code;
	private String fail_msg;
	private String resp_time;
	private String result;
	private String sign;
	public String getIs_success() {
		return is_success;
	}
	public void setIs_success(String is_success) {
		this.is_success = is_success;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String getFail_code() {
		return fail_code;
	}
	public void setFail_code(String fail_code) {
		this.fail_code = fail_code;
	}
	public String getFail_msg() {
		return fail_msg;
	}
	public void setFail_msg(String fail_msg) {
		this.fail_msg = fail_msg;
	}
	public String getResp_time() {
		return resp_time;
	}
	public void setResp_time(String resp_time) {
		this.resp_time = resp_time;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
}
