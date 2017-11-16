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
		String updateSql = "update T_LOCATION_SERVER set F_LAST_DATA_TIME = sysdate,F_LAST_BEAT_TIME=sysdate where F_LOCATION_ID = '"
				+ locationId + "'";
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
		sb.append("'" + uuid + "',");// uuid
		sb.append("'" + locationId + "',");// LOCATION_ID
		sb.append("'" + arr[1] + "',");// DEVICE_CODE
		sb.append("'" + arr[2] + "',");// CLIENT_MAC
		sb.append("'" + arr[4] + "',");// IMSI
		sb.append("'" + arr[3] + "',");// IMEI
		sb.append("'" + arr[5] + "',");// TEL
		sb.append("'" + arr[6] + "',");// WEIXIN
		sb.append("'" + arr[7] + "',");// TAOBAO
		sb.append("'" + arr[8] + "',");// EMAIL
		sb.append("'" + arr[9] + "',");// QQ
		sb.append("'" + arr[10] + "',");// QQ_HISTORY
		sb.append("'" + areaCode + "',");// AREA_CODE
		sb.append("'" + orgCode + "',");// ORG_CODE
		sb.append("'" + arr[11] + "',");// ILLEGAL_APP
		sb.append("'" + arr[12] + "',");// BAIDU_ID
		sb.append("'" + arr[13] + "',");// JINGDONG_ID
		sb.append("'" + arr[14] + "',");// TENCENT_WB_ID
		sb.append("'" + arr[15] + "',");// SINA_ID
		sb.append("'" + arr[16] + "',");// YOUKU_ID
		sb.append("'" + arr[17] + "',");// MILIAO_ID
		sb.append("'" + arr[18] + "',");// XIECHENG_ID
		sb.append("'" + arr[19] + "',");// DIDI_ID
		sb.append("'" + arr[20] + "',");// MONO_ID
		sb.append("'" + arr[21] + "',");// DANGDANG_ID
		sb.append("'" + arr[22] + "',");// MEITUAN_ID
		sb.append("'" + arr[23] + "',");// DAZHONG_ID
		sb.append("'" + arr[24] + "',");// KUGOU_ID
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
