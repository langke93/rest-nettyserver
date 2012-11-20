package org.langke.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.langke.common.Config;


/*
 * @copyright (c) langke 2011 
 * @author langke    2011-5-1 
 */
public class ConnManager {
	
	public static Connection createConn() throws ClassNotFoundException, SQLException {
		Connection connection = null;
		String conn_url = Config.get().get("mall.connection.url");
		String username = Config.get().get("mall.connection.username");
		String password = Config.get().get("mall.connection.password");
		Class.forName("com.mysql.jdbc.Driver" ); 
		connection = DriverManager.getConnection(conn_url,username,password ); 
		return connection;
	}
	
	public static Connection createCmsConn() throws ClassNotFoundException, SQLException {
		Connection connection = null;
		String conn_url = Config.get().get("cms.connection.url");
		String username = Config.get().get("cms.connection.username");
		String password = Config.get().get("cms.connection.password");
		Class.forName("com.mysql.jdbc.Driver" ); 
		connection = DriverManager.getConnection(conn_url,username,password ); 
		return connection;
	}
	
	public static void main(String[] args) {
		Connection conn;
		try {
			conn = createConn();
			conn = createCmsConn();
			System.out.println(conn);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
