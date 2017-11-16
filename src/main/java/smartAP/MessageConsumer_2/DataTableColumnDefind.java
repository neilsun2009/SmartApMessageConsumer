package smartAP.MessageConsumer_2;

import java.util.HashMap;
import java.util.Map;

import smartAP.MessageConsumer_2.entity.ColumnDefind;
import smartAP.MessageConsumer_2.jdbc.ConnectionPool;

public class DataTableColumnDefind {

	private static ConnectionPool pool = ConnectionPool.getInstance();

	private final static String T_TERMINAL_FEATURE = "SELECT c.relname AS table_name, a.attname AS column_name, t.typname AS data_type, a.atttypmod AS data_length, NOT a.attnotnull AS nullable FROM pg_catalog.pg_class AS c INNER JOIN pg_catalog.pg_attribute AS a ON c.oid=a.attrelid INNER JOIN pg_catalog.pg_type AS t ON a.atttypid=t.oid WHERE c.relname='t_terminal_feature';";

	private final static String T_COLLECTION_HOTSPOT = "SELECT c.relname AS table_name, a.attname AS column_name, t.typname AS data_type, a.atttypmod AS data_length, NOT a.attnotnull AS nullable FROM pg_catalog.pg_class AS c INNER JOIN pg_catalog.pg_attribute AS a ON c.oid=a.attrelid INNER JOIN pg_catalog.pg_type AS t ON a.atttypid=t.oid WHERE c.relname='t_collection_hotspot';";

	private final static String T_VIRTUAL_IDENTITY = "SELECT c.relname AS table_name, a.attname AS column_name, t.typname AS data_type, a.atttypmod AS data_length, NOT a.attnotnull AS nullable FROM pg_catalog.pg_class AS c INNER JOIN pg_catalog.pg_attribute AS a ON c.oid=a.attrelid INNER JOIN pg_catalog.pg_type AS t ON a.atttypid=t.oid WHERE c.relname='t_virtual_identity';";
	
	private final static String T_AP_EP_ASSOCIATED = "SELECT c.relname AS table_name, a.attname AS column_name, t.typname AS data_type, a.atttypmod AS data_length, NOT a.attnotnull AS nullable FROM pg_catalog.pg_class AS c INNER JOIN pg_catalog.pg_attribute AS a ON c.oid=a.attrelid INNER JOIN pg_catalog.pg_type AS t ON a.atttypid=t.oid WHERE c.relname='t_ap_ep_associated';";

	private static Map<String, Map<String, ColumnDefind>> datasMap = new HashMap<String, Map<String, ColumnDefind>>();

	public static Map<String, ColumnDefind> getColumnDefind(String tableName) {
		return datasMap.get(tableName);
	}

	public static enum DataTableSql {
		T_TERMINAL_FEATURE("T_TERMINAL_FEATURE",DataTableColumnDefind.T_TERMINAL_FEATURE), 
		T_COLLECTION_HOTSPOT("T_COLLECTION_HOTSPOT", DataTableColumnDefind.T_COLLECTION_HOTSPOT), 
		T_VIRTUAL_IDENTITY("T_VIRTUAL_IDENTITY",DataTableColumnDefind.T_VIRTUAL_IDENTITY), 
		T_AP_EP_ASSOCIATED("T_AP_EP_ASSOCIATED",DataTableColumnDefind.T_AP_EP_ASSOCIATED), ;

		private final String name;
		private final String sql;

		DataTableSql(String name, String sql) {
			this.sql = sql;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getSql() {
			return sql;
		}

		public static DataTableSql valueOfName(String name) {
			for (DataTableSql item : DataTableSql.values()) {
				if (item.getSql().equals(name)) {
					return item;
				}
			}

			throw new AssertionError("cannot get value of name: " + name);
		}
	}

	public static void init() {
		for (DataTableColumnDefind.DataTableSql item : DataTableColumnDefind.DataTableSql
				.values()) {
			datasMap.put(item.getName(), pool.getColumnStruct(item.getSql()));
		}
	}
}
