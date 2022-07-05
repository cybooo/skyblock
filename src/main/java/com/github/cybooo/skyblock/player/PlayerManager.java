package com.github.cybooo.skyblock.player;

import com.github.cybooo.skyblock.SkyBlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private final SkyBlockPlugin plugin;
    private final Map<String, PlayerData> dataMap;

    public PlayerManager(SkyBlockPlugin plugin) {
        this.plugin = plugin;
        this.dataMap = new HashMap<>();
    }

    public PlayerData getPlayerData(String player) {
        if (dataMap.get(player) == null) {
            try (Connection connection = plugin.getMariaDB().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM player_data WHERE player_name = ?;")) {
                preparedStatement.setString(1, player);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) {
                    try (PreparedStatement preparedStatement1 = connection.prepareStatement("REPLACE player_data (player_name, money, time_played) VALUES (?, ?, ?);")) {
                        preparedStatement1.setString(1, player);
                        preparedStatement1.setLong(2, 0);
                        preparedStatement1.setLong(3, 0);
                        preparedStatement1.execute();
                        PlayerData playerData = new PlayerData();
                        playerData.setMoney(0);
                        playerData.setTimePlayed(0);
                        dataMap.put(player, playerData);
                        return playerData;
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                        return null;
                    }
                } else {
                    PlayerData playerData = new PlayerData();
                    playerData.setMoney(resultSet.getLong("money"));
                    playerData.setTimePlayed(resultSet.getLong("time_played"));
                    dataMap.put(player, playerData);
                    return playerData;
                }
            } catch (SQLException exception) {
                return null;
            }
        } else {
            return dataMap.get(player);
        }
    }

    public void saveDataIntoDatabase(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getMariaDB().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("REPLACE player_data (player_name, money, time_played) VALUES (?, ?, ?);")) {
                preparedStatement.setString(1, player.getName());
                preparedStatement.setDouble(2, getPlayerData(player.getName()).getMoney());
                preparedStatement.setLong(3, getPlayerData(player.getName()).getTimePlayed());
                preparedStatement.execute();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public Map<String, PlayerData> getDataMap() {
        return dataMap;
    }
}
