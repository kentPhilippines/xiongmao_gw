package test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;

public class test {
	public static void main(String[] args) {
		double  a =Double.valueOf("666").doubleValue() ;
		System.out.println(a);
		BigDecimal bigDecimal = new BigDecimal(a);
		  BigDecimal setScale = bigDecimal.setScale(666,BigDecimal.ROUND_HALF_UP);
		System.out.println(setScale);
		
		
		
		DecimalFormat df = new DecimalFormat(".##");  
		String format = df.format(new BigDecimal("666.94"));
        BigDecimal b1 = new BigDecimal(format);  
        BigDecimal b2 = new BigDecimal("28.00");  
        System.out.println("小数格式化：" + df.format(b1));  
        System.out.println("整数格式化：" + df.format(b2));  
        
        String aa = "600.00";
        Integer valueOf = Double.valueOf(aa).intValue();
        System.out.println(valueOf);
        
        
	}

}
