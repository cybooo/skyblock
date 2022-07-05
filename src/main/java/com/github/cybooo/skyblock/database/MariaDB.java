package com.github.cybooo.skyblock.database;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        hikariDataSource.addDataSourceProperty("url",
                "jdbc:mariadb://" + host + ":" + port + "/" + database + "?useUnicode=true§characterEncoding=UTF-8§useSSL=false");
        hikariDataSource.addDataSourceProperty("user", username);
        hikariDataSource.addDataSourceProperty("password", password);

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(
                             "CREATE TABLE IF NOT EXISTS skyblock_islands (id INT NOT NULL AUTO_INCREMENT," +

                                     " owner VARCHAR(16) NOT NULL," +
                                     " server_port INT NOT NULL," +
                                     " date_created BIGINT NOT NULL," +
                                     " island_world VARCHAR(16) NOT NULL," +
                                     " island_center VARCHAR(64) NOT NULL," +
                                     " spawn_location VARCHAR(64) NOT NULL," +

                                     " PRIMARY KEY (id));");
             PreparedStatement preparedStatement1 = connection.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS island_members (" +

                             "island_id INT NOT NULL," +
                             " player_name VARCHAR(16) NOT NULL" +

                             ")");
             PreparedStatement preparedStatement2 = connection.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS achievements (" +
                             "player_name VARCHAR(16) NOT NULL," +
                             " achievement_id INT NOT NULL," +
                             " progress INT NOT NULL," +
                             " completed INT NOT NULL" +
                             ");"
             );
             PreparedStatement preparedStatement3 = connection.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS player_data (" +
                             "player_name VARCHAR(16) NOT NULL," +
                             " money DOUBLE NOT NULL," +
                             " time_played INT NOT NULL," +
                             " PRIMARY KEY (player_name));"
             )) {
            preparedStatement.execute();
            preparedStatement1.execute();
            preparedStatement2.execute();
            preparedStatement3.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

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