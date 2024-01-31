package com.popeye.orm;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private static HikariConfig config = new HikariConfig();

    private static HikariDataSource ds;

    static {
        config.setDriverClassName(DbConfiguration.DB_DRIVER);
        config.setJdbcUrl(DbConfiguration.CONNECTION_URL);
        config.setUsername(DbConfiguration.USER_NAME);
        config.setPassword(DbConfiguration.PASSWORD);
        config.setMinimumIdle(DbConfiguration.DB_MIN_CONNECTIONS);
        config.setMaximumPoolSize(DbConfiguration.DB_MAX_CONNECTIONS);
        // Some additional properties
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    private ConnectionPool() {
        super();
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}

class DbConfiguration {

    public static final String HOST_NAME = "localhost";
    public static final String DB_NAME = "demo";
    public static final String DB_PORT = "3306";
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final int DB_MIN_CONNECTIONS = 2;
    public static final int DB_MAX_CONNECTIONS = 4;
    public static final String CONNECTION_URL = "jdbc:mysql://" + HOST_NAME + ":" + DB_PORT + "/" + DB_NAME;

    private DbConfiguration() {
        super();
    }
}