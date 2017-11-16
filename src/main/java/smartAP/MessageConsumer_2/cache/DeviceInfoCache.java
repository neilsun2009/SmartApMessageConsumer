
package smartAP.MessageConsumer_2.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import smartAP.MessageConsumer_2.constant.EhcacheNameConstant;
import smartAP.MessageConsumer_2.jdbc.ConnectionPool;
import smartAP.MessageConsumer_2.util.EncacheHelper;

import org.apache.log4j.Logger;


public class DeviceInfoCache {
	private static Logger logger = Logger.getLogger(DeviceInfoCache.class);
	private static DeviceInfoCache instance = null;
	private static ConnectionPool pool = ConnectionPool.getInstance();


	private DeviceInfoCache() {
	}

	public static DeviceInfoCache getInstance() {
		if (instance == null) {
			instance = new DeviceInfoCache();
		}
		return instance;
	}

	public boolean loadDeviceInfoToCache() {
		boolean result = false;
		try {
			String sql = "SELECT d.f_device_code,l.f_location_id,d.f_area_code,dm.f_organization_code,d.f_device_id,d.f_mac FROM t_location AS l , t_device AS d, t_device_manufacturer AS dm WHERE d.f_device_id = l.f_device_id AND d.f_manufacturer_id = dm.f_id AND d.f_isdel = 0;";
			Map<String, String> map = pool.getData(sql);
			List<String> keys = new ArrayList<String>();
			List<String> values = new ArrayList<String>();
			for (Entry<String, String> entry : map.entrySet()) {
				keys.add(entry.getKey());
				values.add(entry.getValue());
			}
			String[] key = (String[]) keys.toArray(new String[keys.size()]);
			String[] value = (String[]) values
					.toArray(new String[values.size()]);
			result = EncacheHelper.getInstance(EhcacheNameConstant.DEVICE_INFO_EHCACHE_NAME).addToCache(key, value,EhcacheNameConstant.DEVICE_INFO_EHCACHE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("loadDeviceInfoToCache[]", e);
		}
		return result;
	}

	public boolean reloadCache() {
		boolean result = false;
		try {
			// EncacheHelper.getInstance().removeCache();
			result = loadDeviceInfoToCache();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("reloadDeviceInfoToCache[]", e);
		}
		return result;
	}

	public String getDeviceInfoByDeviceCode(String deviceCode) {
		String s = null;
		if (EncacheHelper.getInstance(EhcacheNameConstant.DEVICE_INFO_EHCACHE_NAME).getCache(deviceCode,EhcacheNameConstant.DEVICE_INFO_EHCACHE_NAME) == null) {
			return s;
		} else {
			s = EncacheHelper.getInstance(EhcacheNameConstant.DEVICE_INFO_EHCACHE_NAME).getCache(deviceCode,EhcacheNameConstant.DEVICE_INFO_EHCACHE_NAME).toString();
		}
		return s;
	}
}
