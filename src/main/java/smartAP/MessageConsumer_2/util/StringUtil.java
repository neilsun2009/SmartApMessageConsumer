package smartAP.MessageConsumer_2.util;

import java.text.DecimalFormat;

public class StringUtil {
	/**
	 * 給用","号隔开的字符串增加单引号，用户数据库IN查询
	 * 
	 * @param str
	 * @return
	 */
	public static String addQuote(String Ids) {
		String str = "";
		String[] array = Ids.split(",");
		for (int i = 0; i < array.length; i++) {
			str = str + "'" + array[i] + "',";
		}
		str = str.substring(0, str.length() - 1);
		return str;
	}

	/**
	 * 判断给定的字符串是否为NULL或者“”
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNullOrEmpty(String string) {
		if (string == null) {
			return true;
		} else {
			if (string.equalsIgnoreCase(""))
				return true;
			else
				return false;
		}
	}

	/**
	 * 判断给定的字符对象是否为NULL或者“”
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNullOrEmpty(Object string) {
		if (string == null) {
			return true;
		} else {
			if (string.toString().equalsIgnoreCase(""))
				return true;
			else
				return false;
		}
	}

	/**
	 * 如果改定的字符串为NULL，那么返回“”
	 * 
	 * @param string
	 * @return
	 */
	public static String nullToEmpty(String string) {
		if (string == null) {
			return "";
		} else {
			return string;
		}
	}

	/**
	 * 如果改定的字符串为NULL，那么返回“”
	 * 
	 * @param string
	 * @return
	 */
	public static String nullToEmpty(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return obj.toString();
		}
	}

	/**
	 * 去左空格
	 * 
	 * @param sSourceString
	 * @return
	 */
	public static String ltrim(String sSourceString) {
		return ltrim(sSourceString, ' ');
	}

	/**
	 * 去左边字符
	 * 
	 * @param sSourceString
	 * @param cset
	 * @return
	 */
	public static String ltrim(String sSourceString, char cset) {
		for (int i = 0; i < sSourceString.length(); i++) {
			if (sSourceString.charAt(i) != cset) {
				return sSourceString.substring(i, sSourceString.length() - i);
			}
		}
		return "";
	}

	/**
	 * 去右空格
	 * 
	 * @param sSourceString
	 * @return
	 */
	public static String rtrim(String sSourceString) {
		return rtrim(sSourceString, ' ');
	}

	/**
	 * 去右边字符
	 * 
	 * @param sSourceString
	 * @param cset
	 * @return
	 */
	public static String rtrim(String sSourceString, char cset) {
		for (int i = sSourceString.length() - 1; i >= 0; i--) {
			if (sSourceString.charAt(i) != cset) {
				return sSourceString.substring(0, i + 1);
			}
		}
		return "";
	}

	/**
	 * 
	 * 补零返回字符串
	 * 
	 * @param value
	 * @param zeroNumber
	 *            零的位数，如：00000，0000000
	 * @return
	 */
	public static String addZero(Integer value, String zeroNumber) {
		DecimalFormat df = new DecimalFormat(zeroNumber);
		return df.format(value);
	}
	
	/**
	 * 将字符数组转换成数字数组
	 * 
	 * @param ids
	 * @return
	 */
	public static int[] changeToInt(String[] ids) {
		int idsCount = ids.length;
		int[] intIds = new int[idsCount];
		for (int i = 0; i < idsCount; i++) {
			intIds[i] = Integer.valueOf(ids[i]);
		}
		return intIds;
	}
	/**
	 * 将字符数组转换成Integer数组
	 * 
	 * @param ids
	 * @return
	 * @author lihaishan
	 */
	public static Integer[] changeToInteger(String[] ids) {
		int idsCount = ids.length;
		Integer[] intIds = new Integer[idsCount];
		for (int i = 0; i < idsCount; i++) {
			intIds[i] = Integer.valueOf(ids[i]);
		}
		return intIds;
	}
	
	/**
	 * 对象为空即返回空字符串
	 * 
	 * @Title: NullToString
	 * @Description: 
	 * @author zhengcx
	 * @date 2015年5月26日 下午5:28:33
	 *
	 * @param object
	 * @return
	 */
	public static String NullToString(Object object) {
		if (object == null || "".equals(object.toString())) {
			return "";
		}
		return object.toString();
	}
}
