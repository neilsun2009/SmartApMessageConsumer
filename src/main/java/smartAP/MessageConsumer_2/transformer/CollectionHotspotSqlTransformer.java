package smartAP.MessageConsumer_2.transformer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

public class CollectionHotspotSqlTransformer implements SqlTransformer {
	private static final Logger LOGGER = Logger
			.getLogger(CollectionHotspotSqlTransformer.class);

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Map<Integer, Object> sendCollectionHotspotData(List<String> detail) {
		Map<Integer, Object> result = new HashMap<Integer, Object>();
		List<String> extNormalCollectResult = new ArrayList<String>();
		List<String> BatchUpdateSqls = new ArrayList<String>();
		String locationId = null;
		String oneDetail = DataLengthValidateUtils.getCorrectData(detail, 9);
		if (StringUtil.isNullOrEmpty(oneDetail)) {
			LOGGER.error("the collection_hotspot error file data :"
					+ ListUtils.toString(detail));
			return result;
		}
		String deviceInfo = DeviceInfoCache.getInstance()
				.getDeviceInfoByDeviceCode(oneDetail.split("\\t", -1)[9]); // 通过DeviceCode，获取F_LOCATION_ID;F_AREA_CODE;F_ORGANIZATION_CODE;F_DEVICE_ID
		String[] deviceInfos = null;
		if (StringUtil.isNullOrEmpty(deviceInfo)) {
			LOGGER.warn("deviceInfo is null.the deviceCode is :"
					+ oneDetail.split("\\t", -1)[9]);
			return result;
		}
		deviceInfos = deviceInfo.split(";");
		locationId = deviceInfos[0];
		if (StringUtil.isNullOrEmpty(locationId)) {
			LOGGER.warn("locationId is null");
			return result;
		}
		for (String string : detail) {
			String[] arr = string.split("\\t", -1);
			// deal F_HOTSPOT_MAC is null OR F_HOTSPOT_MAC ERROR case
			if (StringUtil.isNullOrEmpty(arr[0]) || arr[0].length() != 17) {
				LOGGER.error("F_HOTSPOT_MAC IS NULL OR F_HOTSPOT_MAC ERROR . "
						+ arr[0]);
				continue;
			}
			if (arr.length != 12) {
				LOGGER.error("数据格式位数不一致:" + string);
				continue;
			}
			String uuid = UUIDUtil.generateUUIDString();
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
		String orgCode = deviceInfos[2];
		sb.append(StringUtil.sqlColumnDecorator(uuid, false));// uuid
		sb.append(StringUtil.sqlColumnDecorator(arr[0], false));// HOTSPOT_MAC
		sb.append(StringUtil.sqlColumnDecorator(arr[1], false));// HOTSPOT_SSID
		sb.append(StringUtil.sqlColumnDecorator(arr[2], false));// HOTSPOT_CHANNEL
		sb.append(StringUtil.sqlColumnDecorator(arr[3], false));// HOTSPOT_ENCRYPTION_TYPE
		sb.append( DateUtil.getTimestamp(arr[4]) + ",");// CAP_TIME
		sb.append(StringUtil.sqlColumnDecorator(arr[5], false));// SIGNAL
		sb.append(StringUtil.sqlColumnDecorator(arr[6], false));// F_X
		sb.append(StringUtil.sqlColumnDecorator(arr[7], false));// F_Y
		sb.append(StringUtil.sqlColumnDecorator(arr[8], false));// SITE_CODE
		sb.append(StringUtil.sqlColumnDecorator(arr[9], false));// DEVICE_CODE
		sb.append(StringUtil.sqlColumnDecorator(locationId,  false));// LOCATION_ID
		sb.append(StringUtil.sqlColumnDecorator(arr[10], false));// LONGITUDE
		sb.append(StringUtil.sqlColumnDecorator(arr[11], false));// LATITUDE
		sb.append(StringUtil.sqlColumnDecorator("0", false));// HAS_FULLINDEX
		sb.append(StringUtil.sqlColumnDecorator(areaCode, false));// AREA_CODE
		sb.append(DateUtil.dateToStrTimestamp(new Date()) + ",");// CREATION_TIME
		sb.append(StringUtil.sqlColumnDecorator(orgCode, true));// ORG_CODE
		result.add(sb.toString());
		return result;
	}

	@Override
	public Map<Integer, Object> transform(List<String> lines) {
		Map<Integer, Object> result = new HashMap<Integer, Object>();
		result.putAll(sendCollectionHotspotData(lines));
		return result;
	}
}
