package smartAP.MessageConsumer_2.transformer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import smartAP.MessageConsumer_2.SourceFilesHelper;
import smartAP.MessageConsumer_2.cache.DeviceInfoCache;

import smartAP.MessageConsumer_2.util.DataLengthValidateUtils;
import smartAP.MessageConsumer_2.util.DateUtil;
import smartAP.MessageConsumer_2.util.ListUtils;
import smartAP.MessageConsumer_2.util.StringUtil;
import smartAP.MessageConsumer_2.util.UUIDUtil;

public class TerminalFeatureSqlTransformer implements SqlTransformer {
	private static final Logger LOGGER = Logger
			.getLogger(TerminalFeatureSqlTransformer.class);

	private static final String macFlag = "26AE";
	
	private static HashMap<String, String> map = new LinkedHashMap<String, String>();
	

	@Override
	public Map<Integer, Object> transform(List<String> lines) {
		Map<Integer, Object> result = new HashMap<Integer, Object>();
		result.putAll(sendTerminalFeatureData(lines));
		return result;
	}

	private Map<Integer, Object> sendTerminalFeatureData(List<String> detail) {
		Map<Integer, Object> result = new HashMap<Integer, Object>();
		List<String> extNormalCollectResult = new ArrayList<String>();
		List<String> BatchUpdateSqls = new ArrayList<String>();
		String oneDetail = DataLengthValidateUtils.getCorrectData(detail, 14);
		if (StringUtil.isNullOrEmpty(oneDetail)) {
			LOGGER.error("the error file data :" + ListUtils.toString(detail));
			return result;
		}
		String deviceInfo = DeviceInfoCache.getInstance()
				.getDeviceInfoByDeviceCode(oneDetail.split("\\t", -1)[14]); // 通过DeviceCode，获取F_LOCATION_ID;F_AREA_CODE;F_ORGANIZATION_CODE;F_DEVICE_ID
		String[] deviceInfos = null;
		if (StringUtil.isNullOrEmpty(deviceInfo)) {
			LOGGER.warn("the deviceInfo is null.the deviceCode is :"
					+ oneDetail.split("\\t", -1)[14]);
			return result;
		}
		// deviceInfos-contains:F_LOCATION_ID;F_AREA_CODE;F_ORGANIZATION_CODE;F_DEVICE_ID
		deviceInfos = deviceInfo.split(";");
		if (StringUtil.isNullOrEmpty(deviceInfos[0])
				|| StringUtil.isNullOrEmpty(deviceInfos[1])
				|| StringUtil.isNullOrEmpty(deviceInfos[2])) {
			LOGGER.warn("loactionId,areaCode,orgCode,one of them is null. the original data :"
					+ deviceInfo);
			return result;
		}

		for (String string : detail) {
			String[] arr = string.split("\\t", -1);
			if (arr.length != 17 || arr[0].length() != 17) {
				LOGGER.error("数据格式位数不一致:" + string);
				continue;
			}
			if (macFlag.contains(arr[0].substring(1, 2))) {
				// DisguiseMacHandler.fileContents.append("'"+string).append("'"+"\r\n");
				continue;
			}

			String uuid = UUIDUtil.generateUUIDString();// 自动生成ID
			generateExternalDatas(extNormalCollectResult, deviceInfos, arr,
						uuid);
		}
		String updateSql = "UPDATE t_location_server SET f_last_data_time = current_timestamp, f_last_beat_time = current_timestamp WHERE f_location_id = '"
				+ deviceInfos[0] + "'";
		BatchUpdateSqls.add(updateSql);
		result.put(SourceFilesHelper.EXTERNAL, extNormalCollectResult);
		result.put(SourceFilesHelper.UPDATE_LOCATION, BatchUpdateSqls);
		return result;
	}

	private List<String> generateExternalDatas(List<String> result,
			String[] deviceInfos, String[] arr, String uuid) {
		StringBuffer sb = new StringBuffer();
		// F_LOCATION_ID;F_AREA_CODE;F_ORGANIZATION_CODE ;F_DEVICE_ID
		String locationId = deviceInfos[0];
		String areaCode = deviceInfos[1];
		String organizationCode = deviceInfos[2];
		String capTime = DateUtil.getTimestamp(arr[3]);
		sb.append(StringUtil.sqlColumnDecorator(uuid, false));// uuid
		sb.append(StringUtil.sqlColumnDecorator(locationId, false));// locationid
		sb.append(capTime + ",");// captime
		sb.append(StringUtil.sqlColumnDecorator(arr[0], false));// mac
		sb.append(StringUtil.sqlColumnDecorator(arr[1], false));// mactype
		sb.append(StringUtil.sqlColumnDecorator(arr[4], false));// signal
		sb.append(StringUtil.sqlColumnDecorator(arr[2], false));// terminal ssid list
		sb.append(StringUtil.sqlColumnDecorator(arr[5], false));// id type
		sb.append(StringUtil.sqlColumnDecorator(arr[6], false));// id content
		sb.append(StringUtil.sqlColumnDecorator(arr[7], false));// access ssid
		sb.append(StringUtil.sqlColumnDecorator(arr[9], false));// access channel
		sb.append(StringUtil.sqlColumnDecorator(arr[8], false));// devicemac
		sb.append(StringUtil.sqlColumnDecorator(arr[14], false));// devicecode
		sb.append(StringUtil.sqlColumnDecorator(arr[10], false));// hostspot encryption type
		sb.append(StringUtil.sqlColumnDecorator(arr[11], false));// x site
		sb.append(StringUtil.sqlColumnDecorator(arr[12], false));// y site
		sb.append(StringUtil.sqlColumnDecorator(arr[13], false));// site code
		sb.append(StringUtil.sqlColumnDecorator(arr[15], false));// longitude
		sb.append(StringUtil.sqlColumnDecorator(arr[16], false));// latitude
		sb.append(StringUtil.sqlColumnDecorator("0", false));// has fullindex
		sb.append(StringUtil.sqlColumnDecorator(areaCode, false));
		sb.append(DateUtil.dateToStrTimestamp(new Date())+ ",");// CREATION_TIME
		sb.append(StringUtil.sqlColumnDecorator(organizationCode, true));
		result.add(sb.toString());
		return result;
	}
}
