package smartAP.MessageConsumer_2.util;

import java.util.List;

import org.apache.log4j.Logger;

public class DataLengthValidateUtils {
	private static final Logger LOGGER = Logger
			.getLogger(DataLengthValidateUtils.class);

	/**
	 * get the right line data,if the line data is imcomplete ,log it
	 * 
	 * @param datas
	 *            datas
	 * @param correctLength
	 *            length
	 * @return
	 */
	public static String getCorrectData(List<String> datas, int correctLength) {
		for (String string : datas) {
			if (string.split("\\t", -1).length > correctLength) {
				return string;
			} else {
				LOGGER.warn("the line data is incomplete.the error data :"
						+ string);
			}
		}
		return "";
	}

	public static String getDatCorrectData(List<String> datas, int correctLength) {
		for (String string : datas) {
			if (string.split("\\,", -1).length > correctLength) {
				return string;
			} else {
				LOGGER.warn("the line data is incomplete.the error data :"
						+ string);
			}
		}
		return "";
	}
}
