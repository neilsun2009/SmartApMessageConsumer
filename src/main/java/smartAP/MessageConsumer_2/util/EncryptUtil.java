package smartAP.MessageConsumer_2.util;
import java.security.MessageDigest;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class EncryptUtil {
	public static String Algorithm = "DESede"; // 定义 加密算法,可用 DES,DESede,Blowfish
	public static String SHA = "SHA"; // 定义 加密算法,可用 DES,DESede,Blowfish


	/**
	 * 
	 * @Title: encryptMode
	 * @Description: 加密
	 * @param keybyte	密钥，长度为24字节
	 * @param src		欲加密内容
	 * @return
	 */
	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			// 加密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @Title: decryptMode
	 * @Description: 解密
	 * @param keybyte 	密钥，长度为24字节
	 * @param src		欲解密内容
	 * @return
	 */
	public static byte[] decryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			// 解密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @Title: byte2hex
	 * @Description: 转换成十六进制字符串
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			if (n < b.length - 1)
				hs = hs + ":";
		}
		return hs.toUpperCase();
	}
	
	/**
	 * 
	 * @Title: encryptDBInfo
	 * @Description: 依据给定的密钥进行加密
	 * @param src
	 * @return
	 */
	public static String encryptDBInfo(byte[] src) {
		try {
			byte[] key = new byte[] { 0x11, 0x22, 0x7F, 0x58, (byte) 0x88,
					0x11, 0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB,
					(byte) 0xD5, 0x55, 0x66, 0x77, 0x69, 0x74, (byte) 0x98,
					0x30, 0x40, 0x36, (byte) 0xE2 };
			return new String(encryptMode(key, src));
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @Title: decryptDBInfo
	 * @Description: 依据给定的密钥进行解密
	 * @param src
	 * @return
	 */
	public static String decryptDBInfo(byte[] src) {
		try {
			byte[] key = new byte[] { 0x11, 0x22, 0x7F, 0x58, (byte) 0x88,
					0x11, 0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB,
					(byte) 0xD5, 0x55, 0x66, 0x77, 0x69, 0x74, (byte) 0x98,
					0x30, 0x40, 0x36, (byte) 0xE2 };
			return new String(decryptMode(key, src));
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("restriction")
	public static void main(String[] args) {
		Map<String, byte[]> keyMap = new HashMap<String, byte[]>();

		keyMap.put("DES", new byte[] { 0x58, (byte) 0x88, 0x11, 0x20, 0x38,
				0x28, 0x25, (byte) 0x98 });
		keyMap.put("DESede", new byte[] { 0x11, 0x22, 0x7F, 0x58, (byte) 0x88,
				0x11, 0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB,
				(byte) 0xD5, 0x55, 0x66, 0x77, 0x69, 0x74, (byte) 0x98, 0x30,
				0x40, 0x36, (byte) 0xE2 });
		keyMap.put("Blowfish", new byte[] { 0x59, (byte) 0x48, 0x11, 0x40,
				0x38, 0x28, 0x22, (byte) 0x98 });

		// 添加新安全算法,如果用JCE就要把它添加进去
		Security.addProvider(new com.sun.crypto.provider.SunJCE());

		String szSrc = "c##Eqbypf1192FWE8QBYP";
		System.out.println("加密前的字符串:" + szSrc);

		byte[] encoded = encryptMode(keyMap.get(Algorithm), szSrc.getBytes());
		System.out.println("加密后的字符串:"
				+ new String(Base64.encodeBase64(encoded)));

		byte[] srcBytes = decryptMode(keyMap.get(Algorithm), encoded);
		System.out.println("解密后的字符串:" + (new String(srcBytes)));
	}

	/**
	 * 依据给定的加密算法对密码进行加密
	 * 
	 * @param password
	 * @param algorithm
	 * @return
	 */
	public static String encodePassword(String password, String algorithm) {
		byte[] unencodedPassword = password.getBytes();

		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (Exception e) {
			return password;
		}

		md.reset();
		md.update(unencodedPassword);
		byte[] encodedPassword = md.digest();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < encodedPassword.length; i++) {
			if ((encodedPassword[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
		}
		return buf.toString();
	}
}
