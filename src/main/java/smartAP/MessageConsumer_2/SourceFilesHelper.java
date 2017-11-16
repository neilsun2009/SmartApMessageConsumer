package smartAP.MessageConsumer_2;

import smartAP.MessageConsumer_2.transformer.ApEpAssociatedSqlTransformer;
import smartAP.MessageConsumer_2.transformer.VirtualIdentitySqlTransformer;
import smartAP.MessageConsumer_2.transformer.TerminalFeatureSqlTransformer;
import smartAP.MessageConsumer_2.transformer.CollectionHotspotSqlTransformer;
import smartAP.MessageConsumer_2.transformer.SqlTransformer;
import smartAP.MessageConsumer_2.util.PropertiesReader;

/**
 * file path enum
 * 
 *
 */
public class SourceFilesHelper {

	public static enum SourceFiles {
		WIFI_COLLECT_DATA("WIFI_COLLECT_DATA", PropertiesReader
				.getProperty("WifiCollectDataFilePath"));

		private final String name;
		private final String path;

		SourceFiles(String name, String path) {
			this.name = name;
			this.path = path;
		}

		public String getPath() {
			return this.path;
		}

		public String getName() {
			return name;
		}

		public static SourceFiles valueOfName(String name) {
			for (SourceFiles item : SourceFiles.values()) {
				if (item.getName().equals(name)) {
					return item;
				}
			}

			throw new AssertionError("cannot get value of name: " + name);
		}
	}

	public static enum TempFiles {
		TERMINAL_FEATURE("TERMINAL_FEATURE", "SMART_EP_1001", PropertiesReader.getProperty("TerminalFeatureBatchSqlFilePath")), 
		COLLECTION_HOTSPOT("COLLECTION_HOTSPOT", "SMART_AP_1002", PropertiesReader.getProperty("CollectionHotspotBatchSqlFilePath")), 
		//ONLINE_OFFLINE_LOG("ONLINE_OFFLINE_LOG", "WA_SOURCE_FJ_0001", PropertiesReader.getProperty("OnlineOfflineLogBatchSqlFilePath")), 
		//INTERNET_LOG("INTERNET_LOG", "WA_SOURCE_FJ_0002", PropertiesReader.getProperty("InternetLogBatchSqlFilePath")), 
		//DEVICE_PATH("DEVICE_PATH", "WA_BASIC_FJ_1001", PropertiesReader.getProperty("DevicePathBatchSqlFilePath")),
		VIRTUAL_IDENTITY("VIRTUAL_IDENTITY", "SMART_EXT_EP", PropertiesReader.getProperty("VirtualIdentitySqlFilePath")), 
		AP_EP_ASSOCIATED("AP_EP_ASSOCIATED", "SMART_EXT_AP_EP", PropertiesReader.getProperty("ApEpAssociatedSqlFilePath"));
		//IMSI_RECORD("IMSI_RECORD", "", PropertiesReader.getProperty("ImsiRecordBatchSqlFilePath")), 
		//WIFI_TAG_TERMINAL_FEATURE("WIFI_TAG_TERMINAL_FEATURE", "", PropertiesReader.getProperty("WIFITagMonitorRecordBatchSqlFilePath"));

		private final String name;
		private final String identifier;
		private final String path;

		TempFiles(String name, String identifier, String path) {
			this.name = name;
			this.identifier = identifier;
			this.path = path;
		}

		public String getName() {
			return this.name;
		}

		public String getIdentifier() {
			return identifier;
		}

		public String getPath() {
			return this.path;
		}

		public static TempFiles valueOfName(String name) {
			for (TempFiles item : TempFiles.values()) {
				if (item.name.equals(name))
					return item;
			}

			throw new AssertionError("cannot find TempFiles of name " + name);
		}
	}

	public static enum DataTypeProperties {
		TERMINAL_FEATURE("TERMINAL_FEATURE", "TERMINAL_FEATURE_DAY", "terminalfeature",
				"SMART_EP_1001", new TerminalFeatureSqlTransformer()), 

		COLLECTION_HOTSPOT("COLLECTION_HOTSPOT", "COLLECTION_HOTSPOT_DAY", "collectionhotspot",
				"SMART_AP_1002", new CollectionHotspotSqlTransformer()), 

		VIRTUAL_IDENTITY("VIRTUAL_IDENTITY", "VIRTUAL_IDENTITY_DAY","virtualidentity", 
				"SMART_EXT_EP",new VirtualIdentitySqlTransformer()),

		AP_EP_ASSOCIATED("AP_EP_ASSOCIATED", "AP_EP_ASSOCIATED_DAY","apepassociated", 
				"SMART_EXT_AP_EP",new ApEpAssociatedSqlTransformer());

		
		private final String primary;
		private final String daily;
		private final String esIndex;
		private final String identifier;
		private final SqlTransformer sqlTransformer;

		DataTypeProperties(String primary, String daily, String index,
				String identifier, SqlTransformer sqlTransformer) {
			this.primary = primary;
			this.daily = daily;
			this.esIndex = index;
			this.identifier = identifier;
			this.sqlTransformer = sqlTransformer;
		}

		public String getPrimary() {
			return primary;
		}

		public String getDaily() {
			return daily;
		}

		public String getIdentifier() {
			return identifier;
		}

		public SqlTransformer getSqlTransformer() {
			return sqlTransformer;
		}

		public String getEsIndex() {
			return esIndex;
		}

	}

	public static enum ExternalTableProperties {
		TERMINAL_FEATURE(
			"TERMINAL_FEATURE", "T_TERMINAL_FEATURE","ext_terminal_feature", "ext_terminal_feature.dat",
			PropertiesReader.getProperty("ExternalBadFilePath"),
			PropertiesReader.getProperty("TerminalFeatureBatchSqlFilePath")), 

		COLLECTION_HOTSPOT(
			"COLLECTION_HOTSPOT", "T_COLLECTION_HOTSPOT", "ext_collection_hotspot",
			"ext_collection_hotspot.dat", 
			PropertiesReader.getProperty("ExternalBadFilePath"),
			PropertiesReader.getProperty("CollectionHotspotBatchSqlFilePath")), 

		VIRTUAL_IDENTITY(
			"VIRTUAL_IDENTITY", "T_VIRTUAL_IDENTITY",
			"ext_virtual_identity", "ext_virtual_identity.dat",
			PropertiesReader.getProperty("ExternalBadFilePath"),
			PropertiesReader.getProperty("VirtualIdentitySqlFilePath")),
		
		AP_EP_ASSOCIATED(
			"AP_EP_ASSOCIATED", "T_AP_EP_ASSOCIATED",
			"ext_ap_ep_associated", "ext_ap_ep_associated.dat",
			PropertiesReader.getProperty("ExternalBadFilePath"),
			PropertiesReader.getProperty("ApEpAssociatedSqlFilePath"));
		

		private final String threadName;
		private final String normalTableName;
		private final String externaltableName;
		private final String externalDataFileName;
		private final String externalBadFilePath;
		private final String tempFilePath;

		ExternalTableProperties(String name, String normalTableName,
				String ExternaltableName, String externalDataFileName,
				String externalBadFilePath, String tempFilePath) {
			this.threadName = name;
			this.normalTableName = normalTableName;
			this.externaltableName = ExternaltableName;
			this.externalDataFileName = externalDataFileName;
			this.externalBadFilePath = externalBadFilePath;
			this.tempFilePath = tempFilePath;
		}

		public String getTempFilePath() {
			return this.tempFilePath;
		}

		public String getExternalBadFilePath() {
			return this.externalBadFilePath;
		}

		public String getName() {
			return this.threadName;
		}

		public String getNormalTableName() {
			return this.normalTableName;
		}

		public String getExternaltableName() {
			return this.externaltableName;
		}

		public String getExternalDataFileName() {
			return this.externalDataFileName;
		}

		public static ExternalTableProperties valueOfName(String name) {
			for (ExternalTableProperties item : ExternalTableProperties
					.values()) {
				if (item.threadName.equals(name))
					return item;
			}

			throw new AssertionError(
					"cannot find ExternalTableProperties of name " + name);
		}
	}

	/**
	 * update location_server
	 */
	public static final Integer UPDATE_LOCATION = 1;
	/**
	 * EXTERNAL TABLE
	 */
	public static final Integer EXTERNAL = 4;

}
