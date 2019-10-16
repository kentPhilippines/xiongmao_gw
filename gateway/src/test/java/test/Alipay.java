package test;

import com.pay.gateway.channel.H5ailiPay.util.QRCodeUtil;

public class Alipay {
	public static void main(String[] args) throws Exception {
		/**
		 * 
		alipays://platformapi/startapp?appId=20000032	//进入余额宝服务协议
		alipays://platformapi/startapp?appId=09999988	//进入转账页面
		alipays://platformapi/startapp?appId=09999988&&actionType=toAccount&&goBack=YES//转到支付宝账户，这里要填写账户才能转账
		alipays://platformapi/startapp?appId=09999988&&actionType=toCard&&goBack=YES//转到银行卡，但是没有需要自己填写银行卡
		alipays://platformapi/startapp?appId=09999988&&actionType=toFastTransfer&&goBack=YES//进入转账页面
		alipays://platformapi/startapp?appId=10000003//手机号充值
		alipays://platformapi/startapp?appId=10000003&&actionType=PPBindMobileNo//手机号充值
		alipays://platformapi/startapp?appId=10000003&&actionType=Sign//手机号充值
		alipays://platformapi/startapp?appId=10000003&&actionType=recharge//手机号充值
		alipays://platformapi/startapp?appId=10000003&&actionType=flow
		alipays://platformapi/startapp?appId=09999999//信用卡还款
		alipays://platformapi/startapp?appId=09999999&&actionType=repayment&&cardNumber=1932&&cardNumberTyp//添加信用卡  其中一个应该可以直接写入卡号
		alipays://platformapi/startApp?appId=09999999&&actionType=bill_query_open&&cardNumber=1932&&cardNum//添加信用卡  其中一个应该可以直接写入卡号
		alipays://platformapi/startapp?appId=20000037//暂未找到功能
		alipays://platformapi/startapp?appId=20000037&&gotoView=BillDetailView&&bankCardId=06985eb36149412e
		alipays://platformapi/startapp?appId=20000120//饿了吗外卖
		alipays://platformapi/startapp?appId=20000205//亲情圈亲密付
		alipays://platformapi/startapp?appId=20000131//啥都没有
		alipays://platformapi/startapp?appId=20000135//飞猪火车票飞机票
		alipays://platformapi/startapp?appId=20000161//理财小工具
		alipays://platformapi/startapp?appId=20000150//超省汇率比比省
		alipays://platformapi/startapp?appId=20000161&&path=deposit
		alipays://platformapi/startapp?appId=20000161&&path=loan
		alipays://platformapi/startapp?appId=88886666&&target=receiveList//现金红包  我收到的红包
		alipays://platformapi/startapp?appId=88886666&&target=sendList//现金红包  我发出的红包
		alipays://platformapi/startapp?appId=88886666&&target=personalPre&&crowdNo={crowdNo}
		alipays://platformapi/startapp?appId=88886666&&target=personalReceiveDetail&&crowdNo={crowdNo}
		alipays://platformapi/startapp?appId=88886666&&target=personalSendDetail&&crowdNo={crowdNo}
		alipays://platformapi/startapp?appId=88886666&&target=doubiPre
		alipays://platformapi/startapp?appId=88886666&&target=doubiReceiveDetail&&crowdNo={crowdNo}
		alipays://platformapi/startapp?appId=88886666&&target=doubiSendDetail&&crowdNo={crowdNo}
		alipays://platformapi/startapp?appId=88886666&&target=groupPre&&crowdNo={crowdNo}
		alipays://platformapi/startapp?appId=88886666&&target=groupReceiveDetail&&crowdNo={crowdNo}
		alipays://platformapi/startapp?appId=88886666&&target=groupSendDetail&&crowdNo={crowdNo}
		alipays://platformapi/startapp?appId=88886666&&target=ChainResult&&crowdNo={crowdNo}&&preNodeId={pr
		alipays://platformapi/startapp?appId=88886666&&target=onsiteSendDetail&&crowdNo={crowdNo}
		alipays://platformapi/startapp?appId=88886666&&target=onsiteSendDetail&&crowdNo={crowdNo}
		alipays://platformapi/startapp?appId=88886666&&passcode={passcode}
		alipays://platformapi/startapp?appId=20000737
		alipays://platformapi/startapp?appId=20000134
		alipays://platformapi/startApp?appId=20000042&&publicId=2014060900006384&&followType=PUBLIC&&source//任天堂官网
		alipays://platformapi/startApp?appId=20000042&&publicId=2015060900116695&&followType=PUBLIC&&source//
		alipays://platformapi/startApp?actionType=ShowRecords&&actionParam=MOBILEBILL_PUBSERVICE_ABOSSTRADE
		alipays://platformapi/startApp?appId=20000101
		alipays://platformapi/startapp?appId=20000114&&followObjectId=2014030300106652
		alipays://platformapi/startApp?appId=20000048&&actionType=resultByRecomm&&type=MORE&&
		alipays://platformapi/startApp?appId=20000048
		接口1商家：  
		alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={"s": "money","u": "商户id","a": "金额","m":"备注"}  
		接口2个人：  
		alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=金额&userId=商户id&memo=备注
		 */
		QRCodeUtil.encode(
				"alipays://platformapi/startapp?appId=20000042&&publicId=2015060900116695&&followType=PUBLIC&&source",
				"/file",
				true,"20000042&&publicId=2015060900116695&&followType=PUBLIC&&source");
	}

}
