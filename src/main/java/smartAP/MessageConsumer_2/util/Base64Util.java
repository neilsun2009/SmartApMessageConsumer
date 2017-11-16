package smartAP.MessageConsumer_2.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public class Base64Util {
	public final static String ENCODING = "UTF-8";

	// 加密
	public static String encoded(String data) throws UnsupportedEncodingException {
		byte[] b = Base64.encodeBase64(data.getBytes(ENCODING));
		return new String(b, ENCODING);
	}

	// 加密,遵循RFC标准
	public static String encodedSafe(String data) throws UnsupportedEncodingException {
		byte[] b = Base64.encodeBase64(data.getBytes(ENCODING), true);
		return new String(b, ENCODING);
	}

	// 解密
	public static String decode(String data) throws UnsupportedEncodingException {
		byte[] b = Base64.decodeBase64(data.getBytes(ENCODING));
		return new String(b, ENCODING);
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {  
        String str = "Ad3中华@#$%^";  
        // 加密该字符串  
        String encodedString = Base64Util.encodedSafe(str);  
        System.out.println("BASE64:"+encodedString);  
        encodedString = encodedString.replace("Q", "@").replace("z", "*");
        System.out.println("混淆后的BASE64:"+encodedString);
        // 解密该字符串  
        // 解密前先替换掉混淆字符。
        encodedString = encodedString.replace("@", "Q").replace("*", "z");
        String decodedString = Base64Util.decode("u5u6EfeWH5l7habuvDHjejCLBMIsHwOGZ2ZtpFEdBWTacgFhf4mNenjGBIMmX4E3W68JUIKlAcq5qNOnZ2YYJoP7Kiya514Prtun+/ZDeGbCyItW7/9Zv4GLbV0GPIGRHVQPOB/VQivca+O/kGkWY92Oi8Qotb/2TsAylMm7Aj8AU95pwt70ETEgCB/LzOXZ8QMsyZg4jjcYg4ymldH26Zdhff/Jt6O96Oti6qcHlAv0lQlxhO7wBB9KbxR7RthNe5K5u8ZsNO7Lf3CwfSWJyZnRFu+RStHBeeFhEu08p9ahR5CPrd4y5SJzwPI=");  
        System.out.println(decodedString);  
    }  
}
