package smartAP.MessageConsumer_2.util;


/**
 * 经纬度Util
 * 
 * @Title: LongitudeAndLatitudeUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 */
public class LongitudeAndLatitudeUtil {

	public static String formatParam(String paramer) {
		if ("".equals(paramer) || null == paramer) {
			return "0.00000";
		}
		if(!paramer.contains(".")){
			return paramer+".00000";
		}
		String[] array = paramer.split("\\.");
		if (array[1].length() == 5) {
			return paramer;
		}
		// 如果小数点后小于5位则补齐
		if (array[1].length() < 5) {
			if ("".equals(array[1]) || null == array[1]) {
				return array[0] + "." + "00000";
			}
			switch (array[1].length()) {
			case 4:
				return array[0] + "." + array[1] + "0";
			case 3:
				return array[0] + "." + array[1] + "00";
			case 2:
				return array[0] + "." + array[1] + "000";
			case 1:
				return array[0] + "." + array[1] + "0000";
			}
		}
		return array[0] + "." + array[1].substring(0, 5);
	}

}
