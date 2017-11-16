package smartAP.MessageConsumer_2.transformer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smartAP.MessageConsumer_2.SourceFilesHelper;
import smartAP.MessageConsumer_2.cache.DeviceInfoCache;
import smartAP.MessageConsumer_2.util.DataLengthValidateUtils;
import smartAP.MessageConsumer_2.util.DateUtil;
import smartAP.MessageConsumer_2.util.ListUtils;
import smartAP.MessageConsumer_2.util.StringUtil;
import smartAP.MessageConsumer_2.util.UUIDUtil;

import org.apache.log4j.Logger;

public class ApEpAssociatedSqlTransformer implements SqlTransformer {
	
	private static final Logger LOGGER = Logger
			.getLogger(ApEpAssociatedSqlTransformer.class);

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Map<Integer, Object> sendApEpAssociatedData(List<String> detail) {
		Map<Integer, Object> result = new HashMap<Integer, Object>();
		List<String> BatchUpdateSqls = new ArrayList<String>();
		List<String> extNormalCollectResult = new ArrayList<String>();
		
		String oneDetail = DataLengthValidateUtils.getCorrectData(detail, 5);
		if (StringUtil.isNullOrEmpty(oneDetail)) {
			LOGGER.error("the error file data :" + ListUtils.toString(detail));
			return result;
		}
		String deviceInfo = DeviceInfoCache.getInstance()
				.getDeviceInfoByDeviceCode(oneDetail.split("\\t", -1)[2]); // 通过DeviceCode，获取F_LOCATION_ID;F_AREA_CODE;F_ORGANIZATION_CODE;F_DEVICE_ID
		String[] deviceInfos = null;
		if (StringUtil.isNullOrEmpty(deviceInfo)) {
			LOGGER.warn("deviceInfo is null.the deviceCode is :"
					+ oneDetail.split("\\t", -1)[2]);
			return result;
		}
		deviceInfos = deviceInfo.split(";");

		for (String string : detail) {
			String[] arr = string.split("\\t", -1);
			if (arr.length != 6 || arr[3].length() != 17) {
				LOGGER.error("数据格式位数不一致:" + string);
				continue;
			}
			String uuid = UUIDUtil.generateUUIDString();
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
		sb.append("'"+uuid+ "',");
		sb.append(DateUtil.getTimestamp(arr[0]) + ",");// CAP_TIME
		sb.append("'"+arr[1]+ "',");//DEVICE_MAC
		sb.append("'"+arr[2] + "',");// DEVICE_CODE
		sb.append("'"+arr[3] + "',"); //AP_MAC
		sb.append("'"+arr[4] + "',"); //AP_SSID
		sb.append("'"+arr[5]+"'"); //EP_MACS
		result.add(sb.toString());
		return result;
	}

	@Override
	public Map<Integer, Object> transform(List<String> lines) {
		Map<Integer, Object> result = new HashMap<Integer, Object>();
		result.putAll(sendApEpAssociatedData(lines));
		return result;
	}

}
