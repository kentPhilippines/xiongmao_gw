package test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;

public class md5test {
public static void main(String[] args) {
	while (true) {
		String string = UUID.randomUUID().toString();
		String md5 = md5(string);
		String md52 = md52(string);
		if(md5.equals(md52)) {
			System.out.println("正规加密方案："+md5);
			System.out.println("简便加密方案："+md52);
		}else {
			System.out.println("正规加密方案："+md5);
			System.out.println("简便加密方案："+md52);
			return;
		}
	}
		
	}
	public static String md5(String str){
		  MessageDigest md5 = null;  
		  try{  
		     md5 = MessageDigest.getInstance("MD5");  
		  }catch (Exception e){  
		    System.out.println(e.toString());  
		    e.printStackTrace();  
		    return "";  
		  }  
		  char[] charArray = str.toCharArray();  
		  byte[] byteArray = new byte[charArray.length];
		  for (int i = 0; i < charArray.length; i++)  
		    byteArray[i] = (byte) charArray[i];  
		  byte[] md5Bytes = md5.digest(byteArray);  
		  StringBuffer hexValue = new StringBuffer();  
		  for (int i = 0; i < md5Bytes.length; i++){  
		    int val = ((int) md5Bytes[i]) & 0xff;  
		    if (val < 16)  
		      hexValue.append("0");  
		    hexValue.append(Integer.toHexString(val));  
		  }  
		  return hexValue.toString();
		}
	
	public  static String md52(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            return new BigInteger(1,md.digest()).toString(16);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	
	
}
