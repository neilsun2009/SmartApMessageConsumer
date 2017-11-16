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
		String updateSql = "update T_LOCATION_SERVER set F_LAST_DATA_TIME = sysdate,F_LAST_BEAT_TIME=sysdate where F_LOCATION_ID = '"
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
		sb.append("'"+uuid + "',");// uuid
		sb.append("'"+locationId + "',");// locationid
		sb.append(capTime + ",");// captime
		sb.append("'"+arr[0] + "',");// mac
		sb.append("'"+arr[1] + "',");// mactype
		sb.append("'"+arr[4] + "',");// signal
		sb.append("'"+arr[2] + "',");// terminal ssid list
		sb.append("'"+arr[5] + "',");// id type
		sb.append("'"+arr[6] + "',");// id content
		sb.append("'"+arr[7] + "',");// access ssid
		sb.append("'"+arr[9] + "',");// access channel
		sb.append("'"+arr[8] + "',");// devicemac
		sb.append("'"+arr[14] + "',");// devicecode
		sb.append("'"+arr[10] + "',");// hostspot encryption type
		sb.append("'"+arr[11] + "',");// x site
		sb.append("'"+arr[12] + "',");// y site
		sb.append("'"+arr[13] + "',");// site code
		sb.append("'"+arr[15] + "',");// longitude
		sb.append("'"+arr[16] + "',");// latitude
		sb.append("'"+"0',");// has fullindex
		sb.append("'"+areaCode + "',");
		sb.append(DateUtil.dateToStrTimestamp(new Date()) + ",");// CREATION_TIME
		sb.append("'"+organizationCode+ "'");
		result.add(sb.toString());
		return result;
	}
}
