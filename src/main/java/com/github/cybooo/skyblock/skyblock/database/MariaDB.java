package com.github.cybooo.skyblock.skyblock.database;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import java.sql.Connection;
import java.sql.SQLException;

public class MariaDB {

    private final HikariDataSource hikariDataSource;

    public MariaDB(String host, int port, String username, String password, String database) {

        hikariDataSource = new HikariDataSource();

        hikariDataSource.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikariDataSource.setConnectionTimeout(5000);
        hikariDataSource.setMaxLifetime(20000000);
        hikariDataSource.setMaximumPoolSize(5);
        hikariDataSource.setMinimumIdle(5);
        hikariDataSource.setPoolName("skyblock-mariadb");
        hikariDataSource.addDataSourceProperty("url", "jdbc:mariadb://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false");
        hikariDataSource.addDataSourceProperty("user", username);
        hikariDataSource.addDataSourceProperty("password", password);

    }

    public Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    public HikariPoolMXBean getHikariPool() {
        return hikariDataSource.getHikariPoolMXBean();
    }

    public void close() {
        if (hikariDataSource != null) {
            hikariDataSource.close();
        }
    }
}