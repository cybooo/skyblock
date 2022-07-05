package com.github.cybooo.skyblock.island;

import com.github.cybooo.skyblock.SkyBlockPlugin;
import com.github.cybooo.skyblock.achievements.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mariadb.jdbc.Statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IslandManager {

    private final SkyBlockPlugin plugin;
    private final Map<String, HashMap<String, Island>> islands;

    public IslandManager(SkyBlockPlugin plugin) {
        this.plugin = plugin;
        this.islands = new HashMap<>();
    }

    public void loadIslands() {
        try (Connection connection = plugin.getMariaDB().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM skyblock_islands WHERE server_port = ?;")) {
            preparedStatement.setInt(1, plugin.getServer().getPort());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String owner = resultSet.getString("owner");
                long createdMillis = resultSet.getLong("date_created");
                String islandWorld = resultSet.getString("island_world");

                String[] centerString = resultSet.getString("island_center").split(",");
                Location center = new Location(Bukkit.getWorld(islandWorld),
                        Double.parseDouble(centerString[0]),
                        Double.parseDouble(centerString[1]),
                        Double.parseDouble(centerString[2]),
                        Float.parseFloat(centerString[3]),
                        Float.parseFloat(centerString[4]));

                String[] spawnLocationString = resultSet.getString("spawn_location").split(",");
                Location spawnLocation = new Location(Bukkit.getWorld(islandWorld),
                        Double.parseDouble(spawnLocationString[0]),
                        Double.parseDouble(spawnLocationString[1]),
                        Double.parseDouble(spawnLocationString[2]),
                        Float.parseFloat(spawnLocationString[3]),
                        Float.parseFloat(spawnLocationString[4]));

                islands.get(islandWorld).put(owner, new Island(id, owner, createdMillis, islandWorld, center, spawnLocation, new ArrayList<>()));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        try (Connection connection = plugin.getMariaDB().getConnection()) {
            for (Map.Entry<String, HashMap<String, Island>> entry : islands.entrySet()) {
                HashMap<String, Island> islandHashMap = entry.getValue();
                try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM island_members WHERE island_id = ?;")) {
                    for (Map.Entry<String, Island> entry1 : islandHashMap.entrySet()) {
                        preparedStatement.setInt(1, entry1.getValue().getId());
                        ResultSet resultSet = preparedStatement.executeQuery();
                        List<String> members = new ArrayList<>();
                        Bukkit.getLogger().info("Loading members for island " + entry1.getValue().getId());
                        while (resultSet.next()) {
                            members.add(resultSet.getString("player_name"));
                            plugin.getLogger().info("Loaded member: " + resultSet.getString("player_name"));
                        }
                        entry1.getValue().getMembers().addAll(members);
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

    }

    public void createIsland(Player player) {
        String islandWorld = null;
        int islandId = -1;
        for (int i = 0; i < plugin.getConfig().getInt("settings.island-worlds"); i++) {
            if (islands.get(getIslandWorldNamePrefix() + i).size() < plugin.getConfig().getInt("settings.islands-per-world")) {
                islandWorld = getIslandWorldNamePrefix() + i;
            }
        }
        if (islandWorld == null) {
            player.sendMessage("§cNebyl nalezen žádný volný svět pro tvůj ostrov, kontaktuj administrátory!");
            return;
        }
        try (Connection connection = plugin.getMariaDB().getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement("INSERT INTO skyblock_islands (owner, server_port, date_created, island_world, island_center, spawn_location) VALUES (?, ?, ?, ?, ?, ?);",
                             Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, player.getName());
            preparedStatement.setInt(2, plugin.getServer().getPort());
            preparedStatement.setLong(3, System.currentTimeMillis());
            preparedStatement.setString(4, islandWorld);
            preparedStatement.setString(5, "0,0,0,0,0");
            preparedStatement.setString(6, "0,0,0");
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                islandId = Math.toIntExact(resultSet.getLong(1));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return;
        }
        if (islandId == -1) {
            player.sendMessage("§cNastala chyba pri vytvareni tveho ostrova!");
            Bukkit.getLogger().info("Nebylo mozne vytvorit ostrove pro hrace " + player.getName() + ", nebylo mozne ziskat id!");
            return;
        }

        Location center = new Location(Bukkit.getWorld(islandWorld),
                islandId * 500,
                64,
                islandId * 500,
                (float) 0,
                (float) 0
        );

        plugin.getSchematicLoader().pasteSchematic(center);

        Island island = new Island(islandId, player.getName(), System.currentTimeMillis(), islandWorld, center, center, new ArrayList<>());
        islands.get(islandWorld).put(player.getName(), island);
        setIslandSpawn(island, center);
        setIslandCenter(island, center);

        // Dáme pluginu čas na správný vygenerování ostrova.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.teleport(island.getSpawnLocation());
            player.sendTitle("§c§lSkyBlock", "§7Užij si hru!", 5, 40, 5);
            plugin.getAchievementManager().addAchievementProgress(player, Achievement.CREATE_ISLAND, 1);
        }, 20L);
    }

    public boolean hasIsland(Player player) {
        try (Connection connection = plugin.getMariaDB().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM skyblock_islands WHERE owner = ? AND server_port = ?;")) {
            preparedStatement.setString(1, player.getName());
            preparedStatement.setInt(2, plugin.getServer().getPort());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() || getIslandIdByMember(player) != -1;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public int getIslandIdByMember(Player player) {
        try (Connection connection = plugin.getMariaDB().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM island_members WHERE player_name = ?;")) {
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("island_id");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public Island getIslandByMember(Player player) {
        try (Connection connection = plugin.getMariaDB().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM island_members WHERE player_name = ?;")) {
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getIslandById(resultSet.getInt("island_id"));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Island getIslandById(int islandId) {
        for (String islandWorld : islands.keySet()) {
            for (Island island : islands.get(islandWorld).values()) {
                if (island.getId() == islandId) {
                    return island;
                }
            }
        }
        return null;
    }

    public void setIslandSpawn(Island island, Location spawn) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getMariaDB().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("UPDATE skyblock_islands SET spawn_location = ? WHERE id = ?;")) {
                preparedStatement.setString(1, spawn.getX() + "," + spawn.getY() + "," + spawn.getZ() + "," + spawn.getPitch() + "," + spawn.getYaw());
                preparedStatement.setInt(2, island.getId());
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void setIslandCenter(Island island, Location spawn) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getMariaDB().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("UPDATE skyblock_islands SET island_center = ? WHERE id = ?;")) {
                preparedStatement.setString(1, spawn.getX() + "," + spawn.getY() + "," + spawn.getZ() + "," + spawn.getPitch() + "," + spawn.getYaw());
                preparedStatement.setInt(2, island.getId());
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void addMemberToIsland(Player player, Island island) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getMariaDB().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO island_members (island_id, player_name) VALUES (?, ?);")) {
                preparedStatement.setInt(1, island.getId());
                preparedStatement.setString(2, player.getName());
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void removeMemberFromIsland(String player, Island island) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getMariaDB().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM island_members WHERE island_id = ? AND player_name = ?;")) {
                preparedStatement.setInt(1, island.getId());
                preparedStatement.setString(2, player);
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public Island getIsland(Player player) {
        for (String islandWorld : islands.keySet()) {
            if (islands.get(islandWorld).get(player.getName()) != null) {
                return islands.get(islandWorld).get(player.getName());
            }
            for (String owner : islands.get(islandWorld).keySet()) {
                if (islands.get(islandWorld).get(owner).getMembers().contains(player.getName())) {
                    return islands.get(islandWorld).get(owner);
                }
            }
        }
        if (getIslandIdByMember(player) != -1) {
            return getIslandById(getIslandIdByMember(player));
        }
        return null;
    }

    public boolean isInIsland(Player player, Island island) {
        if (island == null) {
            return true;
        }
        int minX = island.getIslandCenter().getBlockX() - 150;
        int maxX = island.getIslandCenter().getBlockX() + 150;
        int minZ = island.getIslandCenter().getBlockZ() - 150;
        int maxZ = island.getIslandCenter().getBlockZ() + 150;
        if (player.getLocation().getBlockX() <= minX && player.getLocation().getBlockX() <= maxX) {
            return player.getLocation().getBlockZ() <= minZ && player.getLocation().getBlockZ() <= maxZ;
        }
        return false;
    }

    public String getIslandWorldNamePrefix() {
        return "islands-" + plugin.getConfig().getString("settings.server-id") + "-";
    }

    public Map<String, HashMap<String, Island>> getIslands() {
        return islands;
    }
}
