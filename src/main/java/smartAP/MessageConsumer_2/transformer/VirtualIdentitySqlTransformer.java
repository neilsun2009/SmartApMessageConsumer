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

public class VirtualIdentitySqlTransformer implements SqlTransformer {

	private static final Logger LOGGER = Logger
			.getLogger(VirtualIdentitySqlTransformer.class);

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Map<Integer, Object> sendVirtualIdentityData(List<String> detail) {
		Map<Integer, Object> result = new HashMap<Integer, Object>();
		List<String> BatchUpdateSqls = new ArrayList<String>();
		List<String> extNormalCollectResult = new ArrayList<String>();
		String locationId = null;
		String areaCode = null;
		String orgCode = null;
		String oneDetail = DataLengthValidateUtils.getCorrectData(detail, 1);
		if (StringUtil.isNullOrEmpty(oneDetail)) {
			LOGGER.error("the error file data :" + ListUtils.toString(detail));
			return result;
		}
		String deviceInfo = DeviceInfoCache.getInstance()
				.getDeviceInfoByDeviceCode(oneDetail.split("\\t", -1)[1]); // 通过DeviceCode，获取F_LOCATION_ID;F_AREA_CODE;F_ORGANIZATION_CODE;F_DEVICE_ID
		String[] deviceInfos = null;
		if (StringUtil.isNullOrEmpty(deviceInfo)) {
			LOGGER.warn("deviceInfo is null.the deviceCode is :"
					+ oneDetail.split("\\t", -1)[1]);
			return result;
		}
		deviceInfos = deviceInfo.split(";");
		locationId = deviceInfos[0];
		areaCode = deviceInfos[1];
		orgCode = deviceInfos[2];
		if (StringUtil.isNullOrEmpty(locationId)
				|| StringUtil.isNullOrEmpty(areaCode)
				|| StringUtil.isNullOrEmpty(orgCode)) {
			LOGGER.warn("locationId,areaCode,orgCode one of them is null.the original data :"
					+ deviceInfo);
			return result;
		}
		for (String string : detail) {
			String[] arr = string.split("\\t", -1);
			if (arr.length != 25) {
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
		sb.append(StringUtil.sqlColumnDecorator(locationId, false));// locationid
		sb.append(StringUtil.sqlColumnDecorator(arr[1], false));// DEVICE_CODE
		sb.append(StringUtil.sqlColumnDecorator(arr[2], false));// CLIENT_MAC
		sb.append(StringUtil.sqlColumnDecorator(arr[4], false));// IMSI
		sb.append(StringUtil.sqlColumnDecorator(arr[3], false));// IMEI
		sb.append(StringUtil.sqlColumnDecorator(arr[5], false));// TEL
		sb.append(StringUtil.sqlColumnDecorator(arr[6], false));// WEIXIN
		sb.append(StringUtil.sqlColumnDecorator(arr[7], false));// TAOBAO
		sb.append(StringUtil.sqlColumnDecorator(arr[8], false));// EMAIL
		sb.append(StringUtil.sqlColumnDecorator(arr[9], false));// QQ
		sb.append(StringUtil.sqlColumnDecorator(arr[10], false));// QQ_HISTORY
		sb.append(StringUtil.sqlColumnDecorator(areaCode, false));// AREA_CODE
		sb.append(StringUtil.sqlColumnDecorator(orgCode, false));// ORG_CODE
		sb.append(StringUtil.sqlColumnDecorator(arr[11], false));// ILLEGAL_APP
		sb.append(StringUtil.sqlColumnDecorator(arr[12], false));// BAIDU_ID
		sb.append(StringUtil.sqlColumnDecorator(arr[13], false));// JINGDONG_ID
		sb.append(StringUtil.sqlColumnDecorator(arr[14], false));// TENCENT_WB_ID
		sb.append(StringUtil.sqlColumnDecorator(arr[15], false));// SINA_ID
		sb.append(StringUtil.sqlColumnDecorator(arr[16], false));// YOUKU_ID
		sb.append(StringUtil.sqlColumnDecorator(arr[17], false));// MILIAO_ID
		sb.append(StringUtil.sqlColumnDecorator(arr[18], false));// XIECHENG_ID
		sb.append(StringUtil.sqlColumnDecorator(arr[19], false));// DIDI_ID
		sb.append(StringUtil.sqlColumnDecorator(arr[20], false));// MONO_ID
		sb.append(StringUtil.sqlColumnDecorator(arr[21], false));// DANGDANG_ID
		sb.append(StringUtil.sqlColumnDecorator(arr[22], false));// MEITUAN_ID
		sb.append(StringUtil.sqlColumnDecorator(arr[23], false));// DAZHONG_ID
		sb.append(StringUtil.sqlColumnDecorator(arr[24], false));// KUGOU_ID
		sb.append(DateUtil.getTimestamp(arr[0]) + ",");// CAP_TIME  
		sb.append(DateUtil.dateToStrTimestamp(new Date()));// CREATION_TIME
		result.add(sb.toString());
		return result;
	}

	@Override
	public Map<Integer, Object> transform(List<String> lines) {
		Map<Integer, Object> result = new HashMap<Integer, Object>();
		result.putAll(sendVirtualIdentityData(lines));
		return result;
	}
}
