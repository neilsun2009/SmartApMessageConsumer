package smartAP.MessageConsumer_2.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static String getDateBytimes(String longTime) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis((long) Double.parseDouble(longTime) * 1000);
		return formatter.format(calendar.getTime());
	}
	
	
	public static String getTimestamp(String longTime) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis((long) Double.parseDouble(longTime) * 1000);
		return String.format("TO_TIMESTAMP('%s', 'YYYY-MM-DD HH24-MI-SS')", formatter.format(calendar.getTime()));
	}
	
	/**
	 * 
	 * @Title: getDateByLongTimes
	 * @Description: 依据给定的longTime返回时间类型
	 * @param longTime
	 * @return
	 */
	public static Date getDateByLongTimes(String longTime) {		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis((long) Double.parseDouble(longTime) * 1000);
		return calendar.getTime();
	}

	/**
	 * 
	 * @Title: getDateByLong
	 * @Description: new Date() 的getTime()值转为时间
	 * @param longTime
	 * @return
	 */
	public static String getDateByLong(long longTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(longTime));
	}
	
	public static String dateToStrLong(Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(dateDate);
		return dateString;
	}
	
	public static String dateToStrTimestamp(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		return String.format("TO_TIMESTAMP('%s', 'YYYY-MM-DD HH24-MI-SS')", dateString);
	}
	
	public static String getWindowsDateByLinuxTime(String linuxTime){
		return dateToStrLong(getDateByLongTimes(linuxTime)); 
	}
	
	
	public static String toTimestamp(String date) {
		return String.format("TO_TIMESTAMP('%s', 'YYYY-MM-DD HH24-MI-SS')", date);
	}
	
	/**
	 * 
	 * @Title: isSameDay
	 * @Description: 是否同一天
	 * @param dt1
	 * @param dt2
	 * @return
	 */
	public static boolean isSameDay(Date dt1, Date dt2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(dt1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(dt2);

		boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
		boolean isSameMonth = isSameYear
				&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
		boolean isSameDate = isSameMonth
				&& cal1.get(Calendar.DAY_OF_MONTH) == cal2
						.get(Calendar.DAY_OF_MONTH);

		return isSameDate;
	}
}
