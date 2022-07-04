package com.github.cybooo.skyblock.skyblock.island;

import com.github.cybooo.skyblock.skyblock.SkyBlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mariadb.jdbc.Statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
                String islandWorld = resultSet.getString("island_world");

                String[] centerString = resultSet.getString("iland_center").split(",");
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

                islands.get(islandWorld).put(owner, new Island(id, owner, islandWorld, center, spawnLocation));
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
            player.sendMessage("§cNebyl nalezen zadny volny svet pro tvuj ostrov!");
            return;
        }
        try (Connection connection = plugin.getMariaDB().getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement("INSERT INTO skyblock_islands (owner, server_port, island_world, island_center, spawn_location) VALUES (?, ?, ?, ?);",
                             Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setInt(2, plugin.getServer().getPort());
            preparedStatement.setString(3, islandWorld);
            preparedStatement.setString(4, "0,0,0,0,0");
            preparedStatement.setString(5, "0,0,0");
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                islandId = Integer.parseInt(resultSet.getRowId(1).toString());
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

        plugin.getSchematicLoader().loadSchematic(center);

        Island island = new Island(islandId, player.getName(), islandWorld, center, center);
        islands.get(islandWorld).put(player.getName(), island);
        setIslandSpawn(island, center);
        setIslandCenter(island, center);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.teleport(island.getSpawnLocation());
        }, 20L);
    }

    public boolean hasIsland(Player player) {
        try (Connection connection = plugin.getMariaDB().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM skyblock_islands WHERE owner = ? AND server_port = ?;")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setInt(2, plugin.getServer().getPort());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
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

    public Island getIsland(Player player) {
        for (String islandWorld : islands.keySet()) {
            if (islands.get(islandWorld).get(player.getName()) != null) {
                return islands.get(islandWorld).get(player.getName());
            }
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
        if (player.getLocation().getBlockX() >= minX && player.getLocation().getBlockX() <= maxX) {
            return player.getLocation().getBlockZ() < minZ || player.getLocation().getBlockZ() > maxZ;
        }
        return true;
    }

    public String getIslandWorldNamePrefix() {
        return "islands-" + plugin.getConfig().getString("settings.server-id") + "-";
    }

    public Map<String, HashMap<String, Island>> getIslands() {
        return islands;
    }
}
