package smartAP.MessageConsumer_2.constant;

import java.util.HashMap;
import java.util.Map;

public class TableInsertParameter {
	public static Map<String,String> inertParameter = new HashMap<String,String>();
	
	public static final String virtualIdentityTableName = "t_virtual_identity";
	public static final String virtualIdentityColumnName = "f_id,f_location_id,f_device_code,f_client_mac,f_imsi,f_imei,f_tel,f_weixin,f_taobao,f_email,f_qq,f_qq_history,f_area_code,f_org_code,f_illegal_app,f_baidu_id,f_jingdong_id,f_tencent_wb_id,f_sina_id,f_youku_id,f_miliao_id,f_xiecheng_id,f_didi_id,f_mono_id,f_dangdang_id,f_meituan_id,f_dazhong_id,f_kugou_id,f_cap_time,f_create_time";
	public static final String terminalFeatureTableName ="t_terminal_feature";
	public static final String terminalFeatureColumnParameter ="f_id,f_location_id,f_cap_time,f_mac,f_mac_type,f_signal,f_terminal_ssid_list,f_id_type,f_id_content,f_access_ssid,f_access_channel,f_device_mac,f_device_code,f_hotspot_encryption_type,f_x,f_y,f_site_code,f_longitude,f_latitude,f_has_fullindex,f_area_code,f_create_time,f_org_code";
	public static final String collectionHotspotTableName =	"t_collection_hotspot";	
	public static final String collectionHotspotColumnParameter = "f_id,f_hotspot_mac,f_hotspot_ssid,f_hotspot_channel,f_hotspot_encryption_type,f_cap_time,f_signal,f_x,f_y,f_site_code,f_device_code,f_location_id,f_longitude,f_latitude,f_has_fullindex,f_area_code,f_create_time,f_org_code";		
	public static final String apEpAssociatedTableName = "t_ap_ep_associated";
	public static final String apEpAssociatedColumnParameter = "associated_id,timestap,device_mac,device_code,ap_mac,ap_ssid,ep_macs";
	
	
	static{
		inertParameter.put(virtualIdentityTableName,virtualIdentityColumnName);
		inertParameter.put(terminalFeatureTableName,terminalFeatureColumnParameter);
		inertParameter.put(collectionHotspotTableName,collectionHotspotColumnParameter);
		inertParameter.put(apEpAssociatedTableName,apEpAssociatedColumnParameter);
	}
}
