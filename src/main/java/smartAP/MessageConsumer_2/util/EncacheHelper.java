package smartAP.MessageConsumer_2.util;

import java.io.InputStream;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

/**
 * @Title: EncacheHelper
 * @Description: Encache帮助类
 */
public final class EncacheHelper {

	private static Logger logger = Logger.getLogger(EncacheHelper.class);
	private static CacheManager cacheManager = null;
	private static EncacheHelper instance = null;

	private EncacheHelper(String encacheName) {
		InputStream configFile = PropertiesReader.class.getClassLoader().getResourceAsStream("ehcache.xml");
		cacheManager = CacheManager.create(configFile);
		Cache cache = cacheManager.getCache(encacheName);
		if (cache == null) {
			cacheManager.addCache(encacheName);
		}
	}

	/**
	 * 
	 * @Title: getInstance
	 * @Description: 获取实例
	 * @return
	 */
	public static EncacheHelper getInstance(String encacheName) {
		if (instance == null) {
			instance = new EncacheHelper(encacheName);
		}
		return instance;
	}

	/**
	 * 
	 * @Title: AddToCache
	 * @Description: 添加信息到缓存
	 * @param keys
	 * @param values
	 * @return
	 */
	public boolean addToCache(String[] keys, Object[] values,String cacheName) {
		boolean result = false;
		try {
			Cache cache = cacheManager.getCache(cacheName);
			for (int i = 0; i < keys.length; i++) {
				cache.put(new Element(keys[i], values[i]));
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("addToCache[]", e);
		}
		return result;
	}

	/**
	 * 
	 * @Title: RemoveCache
	 * @Description: 从缓存中清空所有数据
	 * @return
	 */
	public boolean removeCache(String cacheName) {
		boolean result = false;
		try {
			Cache cache = cacheManager.getCache(cacheName);
			cache.removeAll();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("removeCache[]", e);
		}
		return result;
	}

	/**
	 * 
	 * @Title: getCache
	 * @Description: 获取存储信息
	 * @param key
	 * @return
	 */
	public Object getCache(String key,String cacheName) {
		Object value = null;
		try {
			Element ele = cacheManager.getCache(cacheName).get(key);
			if (ele != null) {
				value = ele.getObjectValue();
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getCache[]", e);
		}
		return value;
	}

	/**
	 * @Title: main
	 * @Description: 主方法
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// 插入
		String keys[] = {"1", "2", "3", "4"};
		String values[] = {"变形金刚哈利波特", "[90 TO 100]", "[2.0 TO 3.0]",
				"[2011-07-18T00:00:00.000Z TO 2011-07-19T00:00:00.000Z]"};
		EncacheHelper.getInstance("DEVICE_INFO").addToCache(keys, values,"DEVICE_INFO");
		// 显示 循环
		for (String key : keys) {
			System.out.println("key:" + key + ",value:"
					+ EncacheHelper.getInstance("DEVICE_INFO").getCache(key,"DEVICE_INFO"));
		}
		// 删除
		EncacheHelper.getInstance("DEVICE_INFO").removeCache("DEVICE_INFO");
		// 显示 循环
		for (String key : keys) {
			System.out.println("key:" + key + ",value:"
					+ EncacheHelper.getInstance("DEVICE_INFO").getCache(key,"DEVICE_INFO"));
		}
	}
}