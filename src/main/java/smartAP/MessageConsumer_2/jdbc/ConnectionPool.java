package smartAP.MessageConsumer_2.jdbc;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import smartAP.MessageConsumer_2.entity.ColumnDefind;
import smartAP.MessageConsumer_2.util.PropertiesReader;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

public class ConnectionPool {

	private static Logger msgLogInfo = Logger.getLogger("msgInfo");
	private static ComboPooledDataSource ds;
	private static ConnectionPool pool;


	private ConnectionPool() {
	}

	/**
	 * 初始化连接池
	 */
	static {
		ds = new ComboPooledDataSource();

		try {
			ds.setDriverClass(PropertiesReader.getProperty("DriverClass"));
		} catch (PropertyVetoException e) {
			msgLogInfo.error("加载oracle数据库驱动时出现异常!e:" + e);
			e.printStackTrace();
		}

		ds.setJdbcUrl(PropertiesReader.getProperty("Url"));
		ds.setUser(PropertiesReader.getProperty("User"));
		ds.setPassword(PropertiesReader.getProperty("Password"));
		// 初始化时获取100条连接
		ds.setInitialPoolSize(PropertiesReader.getIntProperty(
				"InitialPoolSize", 18));
		// 连接池中保留的最大连接数
		ds.setMaxPoolSize(PropertiesReader.getIntProperty("MaxPoolSize", 18));
		// 连接池中保留的最小连接数。
		ds.setMinPoolSize(PropertiesReader.getIntProperty("MinPoolSize", 18));
		// 当连接池中的连接耗尽的时候c3p0一次同时获取的连接数
		ds.setAcquireIncrement(PropertiesReader.getIntProperty(
				"AcquireIncrement", 10));
		// 每60秒检查所有连接池中的空闲连接
		ds.setIdleConnectionTestPeriod(PropertiesReader.getIntProperty(
				"IdleConnectionTestPeriod", 60));
		// 最大空闲时间,3600秒内未使用则连接被丢弃。若为0则永不丢弃
		ds.setMaxIdleTime(PropertiesReader.getIntProperty("MaxIdleTime", 3600));
		// 连接关闭时默认将所有未提交的操作回滚。Default: false autoCommitOnClose
		ds.setAutoCommitOnClose(PropertiesReader.getBooleanProperty(
				"AutoCommitOnClose", true));
		// 定义在从数据库获取新连接失败后重复尝试的次数
		ds.setAcquireRetryAttempts(PropertiesReader.getIntProperty(
				"AcquireRetryAttempts", 30));
		// 两次连接中间隔时间，单位毫秒
		ds.setAcquireRetryDelay(PropertiesReader.getIntProperty(
				"AcquireRetryDelay", 1000));
		// 获取连接失败将会引起所有等待连接池来获取连接的线程抛出异常。
		// 但是数据源仍有效保留，并在下次调用getConnection()的时候继续尝试获取连接。
		// 如果设为true，那么在尝试获取连接失败后该数据源将申明已断开并永久关闭
		ds.setBreakAfterAcquireFailure(PropertiesReader.getBooleanProperty(
				"BreakAfterAcquireFailure", false));// true
	}

	/**
	 * 获取连接池实例
	 * 
	 * @return
	 */
	public static final ConnectionPool getInstance() {
		if (pool == null) {
			try {
				pool = new ConnectionPool();
			} catch (Exception e) {
				msgLogInfo.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return pool;
	}

	/**
	 * 重载finalize
	 */
	protected void finalize() throws Throwable {
		DataSources.destroy(ds);
		super.finalize();
	}

	/**
	 * 执行SQL语句，支持增、删、改
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public int execSql(String sql) throws Exception {
		int affectedRows = -1;

		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = this.getConnection();
			stmt = conn.prepareStatement(sql);
			affectedRows = stmt.executeUpdate();
			conn.commit();
		} catch (Exception ex) {
			msgLogInfo.error(sql);
			msgLogInfo.error(ex.getMessage());
			ex.printStackTrace();
			throw new Exception("数据库错误!");
		} finally {
			free(null, stmt, conn);
		}
		return affectedRows;
	}

	/**
	 * 批量执行SQL语句，支持增、删、改
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public boolean execBatchSql(List<String> sqlList) throws Exception {

		Connection conn = null;
		Statement statemenet = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			statemenet = conn.createStatement();
			for (String sql : sqlList) {
				statemenet.addBatch(sql);
			}
			int[] i = statemenet.executeBatch();
			for (int j : i) {
				if (j == 0) {
					conn.rollback();
					statemenet.close();
					conn.close();
					return false;
				}
			}
			conn.commit();
		} catch (Exception ex) {
			for (String string : sqlList) {
				msgLogInfo.error(string);
			}
			msgLogInfo.error(ex.getMessage());
			// ex.printStackTrace();
			// throw new Exception("数据库错误!");
		} finally {
			statemenet.close();
			conn.close();
		}
		return true;
	}

	/**
	 * 执行查询SQL语句
	 * 
	 * @param sql
	 * @return
	 */
	public ResultSet execSearch(String sql) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			conn = this.getConnection();
			stmt = conn.prepareStatement(sql.toString());
			result = stmt.executeQuery();
			return result;
		} catch (Exception ex) {
			msgLogInfo.error(sql);
			msgLogInfo.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			free(result, stmt, conn);
		}
		return result;
	}

	/**
	 * 
	 * @Title: query4List
	 * @Description: 列表
	 * @author wuyy
	 * @date 2016年8月8日 上午10:36:25
	 *
	 * @param sql
	 * @param obj
	 * @return
	 */
	public List<LinkedHashMap<String, Object>> query4List(String sql,
			Object... obj) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		StringBuilder psql = new StringBuilder();
		psql.append(" select * from ( " + sql);
		psql.append(" )p");

		List<LinkedHashMap<String, Object>> list = new ArrayList<LinkedHashMap<String, Object>>();

		try {
			conn = this.getConnection();
			stmt = conn.prepareStatement(psql.toString());
			if (obj != null) {
				for (int i = 0; i < obj.length; i++) {
					stmt.setObject(i + 1, obj[i]);
				}
			}

			result = stmt.executeQuery();
			ResultSetMetaData md = result.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
			int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数
			while (result.next()) {
				LinkedHashMap<String, Object> rowData = new LinkedHashMap<String, Object>(
						columnCount);
				for (int i = 1; i <= columnCount; i++) {
					rowData.put(md.getColumnName(i), result.getObject(i));
				}
				list.add(rowData);
			}

			return list;
		} catch (Exception ex) {
			msgLogInfo.error(sql);
			msgLogInfo.error(ex.getMessage());
			ex.printStackTrace();
			return null;
		} finally {
			free(result, stmt, conn);
		}

	}

	/**
	 * 
	 * @Title: execSql
	 * @Description: 执行sql PreparedStatement
	 * @author wuyy
	 * @date 2016年8月6日 下午5:31:44
	 *
	 * @param sql
	 * @param arrObj
	 * @return
	 */
	public boolean execSql(String sql, Object[] arrObj) {
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = this.getConnection();
			pstm = conn.prepareStatement(sql);

			for (int i = 0; i < arrObj.length; i++) {
				pstm.setObject(i + 1, arrObj[i]);
			}
			pstm.execute();
			conn.commit();// 2,进行手动提交（commit）
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				// 若出现异常，对数据库中所有已完成的操作全部撤销，则回滚到事务开始状态
				if (!conn.isClosed()) {
					conn.rollback();// 4,当异常发生执行catch中SQLException时，记得要rollback(回滚)；
					conn.setAutoCommit(true);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			free(null, pstm, conn);
		}
	}

	/**
	 * 执行语句
	 * 
	 * @Title: execBatch
	 * @Description: 执行sql PreparedStatement
	 * @author wuyy
	 * @date 2016年5月20日 上午10:25:59
	 *
	 * @param sql
	 * @param arrObj
	 */
	public boolean execBatch(String sql, List<Object[]> arrObj) {
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);// 1,首先把Auto commit设置为false,不让它自动提交
			pstm = conn.prepareStatement(sql);
			final int batchSize = 1000;
			int count = 0;
			for (Object[] item : arrObj) {
				for (int i = 0; i < item.length; i++) {
					pstm.setObject(i + 1, item[i]);
				}
				pstm.addBatch();
				if (++count % batchSize == 0) {
					pstm.executeBatch(); // 提交一部分;
				}
			}
			pstm.executeBatch(); // 提交剩下的;
			conn.commit();// 2,进行手动提交（commit）
			conn.setAutoCommit(true);// 3,提交完成后回复现场将Auto commit,还原为true,
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				// 若出现异常，对数据库中所有已完成的操作全部撤销，则回滚到事务开始状态
				if (!conn.isClosed()) {
					conn.rollback();// 4,当异常发生执行catch中SQLException时，记得要rollback(回滚)；
					conn.setAutoCommit(true);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			free(null, pstm, conn);
		}
	}

	/**
	 * 
	 * @Title: execBatch
	 * @Description: 多条sql
	 * @author wuyy
	 * @date 2016年6月2日 下午1:55:52
	 *
	 * @param sqls
	 * @param arrObjs
	 * @return
	 */
	public boolean execBatch(List<String> sqls, List<Object[]> arrObjs) {
		Connection conn = null;
		List<PreparedStatement> pstms = new ArrayList<PreparedStatement>();
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);// 1,首先把Auto commit设置为false,不让它自动提交

			for (int i = 0; i < sqls.size(); i++) {
				PreparedStatement pstm = conn.prepareStatement(sqls.get(i));
				pstms.add(pstm);
			}

			for (int i = 0; i < pstms.size(); i++) {
				PreparedStatement pstm = pstms.get(i);

				Object[] item = arrObjs.get(i);
				for (int j = 0; j < item.length; j++) {
					pstm.setObject(j + 1, item[j]);
				}
				pstm.addBatch();
				pstm.executeBatch();

			}

			conn.commit();// 2,进行手动提交（commit）
			conn.setAutoCommit(true);// 3,提交完成后回复现场将Auto commit,还原为true,
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				// 若出现异常，对数据库中所有已完成的操作全部撤销，则回滚到事务开始状态
				if (!conn.isClosed()) {
					conn.rollback();// 4,当异常发生执行catch中SQLException时，记得要rollback(回滚)；
					conn.setAutoCommit(true);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			free(null, pstms, conn);
		}
	}

	/**
	 * 
	 * @Title: query4Int
	 * @Description: 总数
	 * @author wuyy
	 * @date 2016年7月8日 上午10:43:38
	 *
	 * @param sql
	 * @return
	 */
	public int query4Int(String sql) {
		return query4Int(sql, new Object[] {});
	}

	/**
	 * 
	 * @Title: query4Int
	 * @Description: 总数
	 * @author wuyy
	 * @date 2016年7月8日 上午10:44:49
	 *
	 * @param sql
	 * @param obj
	 * @return
	 */
	public int query4Int(String sql, Object... obj) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			conn = this.getConnection();
			stmt = conn.prepareStatement(sql.toString());
			if (obj != null) {
				for (int i = 0; i < obj.length; i++) {
					stmt.setObject(i + 1, obj[i]);
				}
			}
			result = stmt.executeQuery();
			while (result.next()) {
				return result.getInt(1);
			}
			return 0;
		} catch (Exception ex) {
			msgLogInfo.error(sql);
			msgLogInfo.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			free(result, stmt, conn);
		}
		return -1;
	}

	/**
	 * 执行查询SQL语句 返回个以key为device
	 * ，value为F_LOCATION_ID+F_AREA_CODE+F_ORGAZINATION_CODE
	 * +F_DEVICE_ID+F_MAC的Map
	 * 
	 * @param sql
	 * @return
	 */
	public Map<String, String> getData(String sql) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			conn = this.getConnection();
			stmt = conn.prepareStatement(sql.toString());
			result = stmt.executeQuery();
			Map<String, String> map = new HashMap<String, String>();
			while (result.next()) {
				map.put(result.getString("f_device_code"),
						result.getString("f_location_id") + ";"
								+ result.getString("f_area_code") + ";"
								+ result.getString("f_organization_code") + ";"
								+ result.getString("f_device_code") + ";"
								+ result.getString("f_mac"));
			}
			return map;
		} catch (Exception ex) {
			msgLogInfo.error(sql);
			msgLogInfo.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			free(result, stmt, conn);
		}
		return null;
	}

	/**
	 * 释放资源
	 * 
	 * @param rs
	 * @param stmt
	 * @param con
	 */
	public void free(ResultSet rs, PreparedStatement stmt, Connection con) {
		List<PreparedStatement> stmts = new ArrayList<PreparedStatement>();
		stmts.add(stmt);
		free(rs, stmts, con);
	}

	public void free(ResultSet rs, List<PreparedStatement> stmts, Connection con) {
		try {
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			try {
				for (PreparedStatement stmt : stmts) {
					if (stmt != null)
						stmt.close();
				}

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			try {
				if (con != null)
					con.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public boolean execListBatch(List<String> sqls, List<List<Object[]>> arrObjs) {
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);// 1,首先把Auto commit设置为false,不让它自动提交
			for (int i = 0; i < sqls.size(); i++) {
				pstm = conn.prepareStatement(sqls.get(i));
				final int batchSize = 1000;
				int count = 0;
				for (Object[] item : arrObjs.get(i)) {
					for (int k = 0; k < item.length; k++) {
						pstm.setObject(k + 1, item[k]);
					}
					pstm.addBatch();
					if (++count % batchSize == 0) {
						pstm.executeBatch(); // 提交一部分;
					}
				}
				pstm.executeBatch(); // 提交剩下的;
			}

			conn.commit();// 2,进行手动提交（commit）
			conn.setAutoCommit(true);// 3,提交完成后回复现场将Auto commit,还原为true,
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				// 若出现异常，对数据库中所有已完成的操作全部撤销，则回滚到事务开始状态
				if (!conn.isClosed()) {
					conn.rollback();// 4,当异常发生执行catch中SQLException时，记得要rollback(回滚)；
					conn.setAutoCommit(true);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			free(null, pstm, conn);
		}
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 */
	public synchronized final Connection getConnection() {
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			msgLogInfo.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * get the table defind map ,the key is columnName and value contains
	 * dataType,dataLength,dataNullable
	 * 
	 * @param sql
	 * @return
	 */
	public Map<String, ColumnDefind> getColumnStruct(String sql) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		try {
			conn = this.getConnection();
			stmt = conn.prepareStatement(sql.toString());
			result = stmt.executeQuery();
			Map<String, ColumnDefind> map = new HashMap<String, ColumnDefind>();
			while (result.next()) {
				ColumnDefind columnDefind = new ColumnDefind();
				String columnName = result.getString("column_name");
				columnDefind.setDataType(result.getString("data_type"));
				columnDefind.setDataLength(result.getString("data_length"));
				columnDefind.setNullable(result.getString("nullable"));
				map.put(columnName, columnDefind);
			}
			return map;
		} catch (Exception ex) {
			msgLogInfo.error(sql);
			msgLogInfo.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			free(result, stmt, conn);
		}
		return null;
	}

}
