package com.github.cybooo.skyblock.achievements;

import com.github.cybooo.skyblock.SkyBlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AchievementManager {

    private final SkyBlockPlugin plugin;
    private final Map<Player, AchievementData> dataMap;

    public AchievementManager(SkyBlockPlugin plugin) {
        this.plugin = plugin;
        this.dataMap = new HashMap<>();
    }

    public void registerIntoDatabase(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!isInDatabase(player)) {
                try (Connection connection = plugin.getMariaDB().getConnection()) {
                    for (Achievement achievement : Achievement.values()) {
                        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO achievements (player_name, achievement_id, progress, completed) VALUES (?, ?, ?, ?)")) {
                            preparedStatement.setString(1, player.getName());
                            preparedStatement.setInt(2, achievement.getId());
                            preparedStatement.setLong(3, 0);
                            preparedStatement.setInt(4, 0);
                            preparedStatement.execute();
                        }
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getAchievementManager().getDataMap().put(player, new AchievementData(plugin, player).cache());
                plugin.getLogger().info("Pocet zaznamu v cache: " + plugin.getAchievementManager().getData(player).getPendingAchievements().size());
            });
        });
    }

    public boolean isInDatabase(Player player) {
        try (Connection connection = plugin.getMariaDB().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM achievements WHERE player_name = ?")) {
            preparedStatement.setString(1, player.getName());
            return preparedStatement.executeQuery().next();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public long getAchievementProgress(Player player, Achievement achievement) {
        try (Connection connection = plugin.getMariaDB().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM achievements WHERE player_name = ? AND achievement_id = ?")) {
            preparedStatement.setString(1, player.getName());
            preparedStatement.setInt(2, achievement.getId());
            try (java.sql.ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("progress");
                }
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public void addAchievementProgress(Player player, Achievement achievement, long progress) {
        if (getData(player).getCompletedAchievements().contains(achievement)) {
            return;
        }
        getData(player).getPendingAchievements().put(achievement, getData(player).getPendingAchievements().get(achievement) + progress);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getMariaDB().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("UPDATE achievements SET progress = progress + ? WHERE player_name = ? AND achievement_id = ?")) {
                preparedStatement.setLong(1, progress);
                preparedStatement.setString(2, player.getName());
                preparedStatement.setInt(3, achievement.getId());
                preparedStatement.execute();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
        checkAchievement(player, achievement);
    }

    public void setCompleted(Player player, Achievement achievement) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getMariaDB().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("UPDATE achievements SET completed = 1 WHERE player_name = ? AND achievement_id = ?")) {
                preparedStatement.setString(1, player.getName());
                preparedStatement.setInt(2, achievement.getId());
                preparedStatement.execute();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public boolean isCompleted(Player player, Achievement achievement) {
        try (Connection connection = plugin.getMariaDB().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM achievements WHERE player_name = ? AND achievement_id = ?")) {
            preparedStatement.setString(1, player.getName());
            preparedStatement.setInt(2, achievement.getId());
            try (java.sql.ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("completed") == 1;
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public void checkAchievement(Player player, Achievement achievement) {
        if (getData(player).getCompletedAchievements().contains(achievement)) {
            return;
        }
        if (!getData(player).getPendingAchievements().containsKey(achievement)) {
            return;
        }
        if (getData(player).getPendingAchievements().get(achievement) >= achievement.getGoal()) {
            completeAchievement(player, achievement);
        }
    }

    public void completeAchievement(Player player, Achievement achievement) {
        getData(player).getCompletedAchievements().add(achievement);
        getData(player).getPendingAchievements().remove(achievement);
        player.sendTitle("§6§LSPLNĚN ACHIEVEMENT", achievement.getName(), 5, 60, 5);
        player.sendMessage("§aSplnil jsi achievement §2§l" + achievement.getName() + "§a a získal jsi §2§l" + achievement.getReward() + "§a!");
        plugin.getEconomy().depositPlayer(player, achievement.getReward());
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
        setCompleted(player, achievement);
    }

    public AchievementData getData(Player player) {
        return dataMap.get(player);
    }

    public Map<Player, AchievementData> getDataMap() {
        return dataMap;
    }
}
