package test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class testTime {
	static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws ParseException {
	Date dNow = new Date(); //当前时间
	Date dBefore = new Date();
	Calendar calendar = Calendar.getInstance(); //得到日历
	calendar.setTime(dNow);//把当前时间赋给日历
	calendar.add(Calendar.DAY_OF_MONTH, -1); //设置为前一天
	dBefore = calendar.getTime(); //得到前一天的时间
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
	String defaultStartDate = sdf.format(dBefore); //格式化前一天
	defaultStartDate = defaultStartDate+" 00:00:00";
	String defaultEndDate = defaultStartDate.substring(0,10)+" 23:59:59";
	System.out.println("前一天的起始时间是：" + defaultStartDate);
	System.out.println("前一天的结束时间是：" + defaultEndDate);
	Date parse = formatter.parse(defaultStartDate);
	Date parse1 = formatter.parse(defaultEndDate);
	
		System.out.println(parse.toString());
		System.out.println(parse1.toString());
	}

}
