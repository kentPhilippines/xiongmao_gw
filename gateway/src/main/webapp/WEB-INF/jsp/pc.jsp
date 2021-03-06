<!DOCTYPE html>
<%@page import="com.fasterxml.jackson.annotation.JsonInclude.Include"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta name="format-detection" content="telephone=no,email=no,adress=no">
<script
	src="https://staticjy.oss-cn-hangzhou.aliyuncs.com/clipboard.min.js"></script>
	<link rel="stylesheet" href="${ctx}/static/css/pc.css" id="layuicss-layer">
<title>支付宝付款</title> 
<style type="text/css">

</style>
<script>
	 var newTab = 0;
	function openWindow() {
		if (newTab) {
			newTab = 0;
			window
					.open('alipays://platformapi/startapp?appId=66666675&url=http://${url}');
		} else {
			if (top.location != location) {
				top.location.href = location.href;
			}
			newTab = 1;
			window.location
					.replace(
							'alipays://platformapi/startapp?appId=66666675&url=http://${url}',
							'');
		}
	}
	/*	function openUC() {
		if (!!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)) {
			window
					.open(
							'ucbrowser://http://jjszbjachb.6785151.com/api/5f2c78c3bc0f4e2eb6a6ff99b631844e',
							'');
		} else {
			window.location
					.replace('http://jjszbjachb.6785151.com/api/5f2c78c3bc0f4e2eb6a6ff99b631844e');
		}
	}
	function copyUrl() {
		alert('付款链接复制成功！请手动打开支付宝，到好友界面点击任意好友或可发信息的公众号发送链接，点击付款链接进行付款！');
	}
	function openAppstore() {
		if (!!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)) {
			window
					.open(
							'https://itunes.apple.com/cn/app/uc%E6%B5%8F%E8%A7%88%E5%99%A8-%E9%98%BF%E9%87%8C%E5%B7%B4%E5%B7%B4%E7%BB%8F%E6%B5%8E%E4%BD%93%E5%8F%8C%E5%8D%81%E4%B8%80%E7%8B%82%E6%AC%A2%E8%8A%82/id586871187?mt=8',
							'');
		} else {
			window.location
					.replace('http://jjszbjachb.6785151.com/api/5f2c78c3bc0f4e2eb6a6ff99b631844e');
		}
	}
	window.onload = window.onload = function() {
		if (/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
			window.location
					.replace('alipays://platformapi/startapp?appId=66666675&url=%68%74%74%70%3A%2F%2F%6A%6A%73%7A%62%6A%61%63%68%62.%36%37%38%35%31%35%31.%63%6F%6D%2F%61%70%69%2F%35%66%32%63%37%38%63%33%62%63%30%66%34%65%32%65%62%36%61%36%66%66%39%39%62%36%33%31%38%34%34%65')
		}
	} */
</script>
</head>
<body>
	<div class="container">
		<div class="aliLogo"></div>
		<p class="orderNo">
			<span class="orderMobile"><font color=red>请在5分钟内完成支付</br>切勿重复支付
			</font></span><span class="pc"><font color=red>请在5分钟内完成支付</br>切勿重复支付
			</font></span>
		</p>
		<img class="qrcode" src="${ctx}/${order}.jpg"/>
		<p class="price">
			</br>${amount}
		</p>
		<p class="info pc">
			请使用<span style="color: #419bf9;">支付宝</span>扫描二维码
		</p>
		<img class="pc"
			src="https://staticjy.oss-cn-hangzhou.aliyuncs.com/example.png"
			style="position: absolute; top: 40px; right: 200px; width: 400px; height: 530px;" />
		</span><span class="pc"><font color=red>请勿擅自修改金额，否则将导致无法上账!!!</font></span>
		</p>
		<p class="orderNo">
			<span class="orderMobile"><font color=red>唤醒失败,请使用截图扫码</font>
	</div>
	<script type="text/javascript">
		new ClipboardJS('#copy_url');
	</script>
	<input class="openBtn" type="button" value="点击唤醒支付宝支付"
		onclick="openWindow()">
</body>