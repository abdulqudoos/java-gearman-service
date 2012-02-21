package org.gearman.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import org.gearman.GearmanPersistable;
import org.gearman.GearmanPersistence;

public class JdbcGearmanPersistence implements GearmanPersistence {

	private final String driver;
	private final String url;
	private final Properties info;
	
	private Connection conn;
	
	/**
	 * Creates a new {@link JdbcGearmanPersistence} instance
	 * @param driver
	 * 		The JDBC driver
	 * @param url
	 * 		The JDBC url
	 * @param info
	 * 		The JDBC properties 
	 * @throws ClassNotFoundException
	 * 		If the given driver cannot be found 
	 */
	public JdbcGearmanPersistence(String driver, String url, Properties info) throws ClassNotFoundException {
		this.driver = driver;
		this.url = url;
		this.info = (Properties) info.clone();
		
		loadDriver();
	}
	
	private final void loadDriver() throws ClassNotFoundException {
		Class.forName(driver);
	}
	
	protected Connection getConnection() throws SQLException {
		if(conn==null || conn.isClosed()) {
			conn = DriverManager.getConnection(url, info);
		}
		return conn;
	}
	
	public String getDriver() {
		return driver;
	}
	
	public String getUrl() {
		return url;
	}
	
	public Properties getInfo() {
		return (Properties) info.clone();
	}
	
	@Override
	public void write(GearmanPersistable item) throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(GearmanPersistable item) throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeAll() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public Collection<GearmanPersistable> readAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void createTable() throws SQLException {
		
	}
	
	/**
	 * Closes any open connections
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		if(this.conn!=null) this.conn.close();
	}
}
