package br.com.bytebank;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionFactory {

	private static volatile ConnectionFactory factory = null;
	private DataSource ds;
	
	private ConnectionFactory() {
		HikariConfig config = new HikariConfig();
		
        config.setJdbcUrl("jdbc:mysql://localhost:3306/byte_bank");
        config.setUsername("root");
        config.setPassword("teste123");
        config.setMaximumPoolSize(10);
        ds = new HikariDataSource(config);
        
        try {
			System.out.println("DS Timeout: " + ds.getLoginTimeout());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static ConnectionFactory getInstance() {
        if (factory == null) {
            synchronized(ConnectionFactory.class) {
                if (factory == null) {
                	factory = new ConnectionFactory();
                }
            }
        }

        return factory;
    }
	
	public Connection getConnection() {
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}