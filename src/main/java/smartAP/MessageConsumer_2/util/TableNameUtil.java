package smartAP.MessageConsumer_2.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @Title: TableNameUtil
 * @Description: 依据给定的时间区域，给出对应的分表名称列表
 */
public class TableNameUtil {

	/**
	 * 
	 * @Title: getMonitorRecordSplieName
	 * @Description: 依据给定的时间，给出对应的分表名称
	 * @param beginDt
	 * @param endDt
	 * @return
	 */
	public static String getMonitorRecordSplieName(Date dt) {
		return getTableNames(dt, "T_MR_");
	}

	/**
	 * 
	 * @Title: getRouterRecordSplieName
	 * @Description: 依据给定的时间，给出对应的分表名称
	 * @param dt
	 * @return
	 */
	public static String getRouterRecordSplieName(Date dt) {
		return getTableNames(dt, "T_RR_");
	}

	/**
	 * 
	 * @Title: getMacRelationInfoSplieName
	 * @Description: 依据给定的时间，给出对应的分表名称
	 * @param dt
	 * @return
	 */
	public static String getMacRelationInfoSplieName(Date dt) {
		return getTableNames(dt, "T_MRI_");
	}

	/**
	 * 
	 * @Title: getImsiRecordSplieName
	 * @Description: 依据给定的时间，给出对应的分表名称
	 * @param dt
	 * @return
	 */
	public static String getImsiRecordSplieName(Date dt) {
		return getTableNames(dt, "T_IR_");
	}

	/**
	 * 
	 * @Title: getCarRecordSplieName
	 * @Description: 依据给定的时间，给出对应的分表名称
	 * @param dt
	 * @return
	 */
	public static String getCarRecordSplieName(Date dt) {
		return getTableNames(dt, "T_CR_");
	}

	/**
	 * 
	 * @Title: getFaceRecordSplieName
	 * @Description: 依据给定的时间，给出对应的分表名称
	 * @param beginDt
	 * @param endDt
	 * @return
	 */
	public static String getFaceRecordSplieName(Date dt) {
		return getTableNames(dt, "T_FR_");
	}
	
	/**
	 * 
	 * @Title: getDevicePathSplieName
	 * @Description: 依据给定的时间，给出对应的分表名称
	 * @param dt
	 * @return
	 */
	public static String getDevicePathSplieName(Date dt) {
		return getTableNames(dt, "T_DP_");
	}

	/**
	 * 
	 * @Title: getOnlineOfflineLogSplieName
	 * @Description: 依据给定的时间，给出对应的分表名称
	 * @param beginDt
	 * @param endDt
	 * @return
	 */
	public static String getOnlineOfflineLogSplieName(Date dt) {
		return getTableNames(dt, "T_OOL_");
	}
	
	/**
	 * 
	 * @Title: getInternetLogSplieName
	 * @Description: 依据给定的时间，给出对应的分表名称
	 * @param beginDt
	 * @param endDt
	 * @return
	 */
	public static String getInternetLogSplieName(Date dt) {
		return getTableNames(dt, "T_IL_");
	}

	/**
	 * 
	 * @Title: getTableNames
	 * @Description: 依据给定的时间，给出对应的分表名称
	 * @param dt
	 * @param preTable
	 * @return
	 */
	private static String getTableNames(Date dt, String preTable) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return preTable + formatter.format(dt);
	}
}
