package com.github.cybooo.skyblock.skyblock;

import com.github.cybooo.skyblock.skyblock.commands.IslandCommand;
import com.github.cybooo.skyblock.skyblock.commands.SetSpawnCommand;
import com.github.cybooo.skyblock.skyblock.database.MariaDB;
import com.github.cybooo.skyblock.skyblock.island.IslandManager;
import com.github.cybooo.skyblock.skyblock.listeners.PlayerListener;
import com.github.cybooo.skyblock.skyblock.schematic.SchematicLoader;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class SkyBlockPlugin extends JavaPlugin {

    private MariaDB mariaDB;
    private SlimePlugin slimePlugin;
    private IslandManager islandManager;
    private SchematicLoader schematicLoader;

    @Override
    public void onEnable() {

        long start = System.currentTimeMillis();

        saveDefaultConfig();

        mariaDB = new MariaDB(
                getConfig().getString("database.host"),
                getConfig().getInt("database.port"),
                getConfig().getString("database.username"),
                getConfig().getString("database.password"),
                getConfig().getString("database.database")
        );

        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");

        islandManager = new IslandManager(this);

        try {
            for (int i = 0; i < getConfig().getInt("settings.island-worlds"); i++) {

                SlimeLoader slimeLoader = slimePlugin.getLoader("mysql");

                if (!slimeLoader.worldExists(islandManager.getIslandWorldNamePrefix() + i)) {
                    getLogger().info("Vytvarim svet " + islandManager.getIslandWorldNamePrefix() + i);
                    try {
                        slimePlugin.createEmptyWorld(slimeLoader, islandManager.getIslandWorldNamePrefix() + i, false,
                                new SlimePropertyMap());
                        getLogger().info("Svet " + islandManager.getIslandWorldNamePrefix() + i + " vytvoren!");
                        islandManager.getIslands().putIfAbsent(islandManager.getIslandWorldNamePrefix() + i, new HashMap<>());
                    } catch (WorldAlreadyExistsException | IOException e) {
                        getLogger().warning("Nastala chyba!");
                        e.printStackTrace();
                    }
                } else {

                    try (Connection connection = mariaDB.getConnection();
                         PreparedStatement preparedStatement = connection.prepareStatement("UPDATE worlds SET locked = ? WHERE name = ?")) {
                        preparedStatement.setInt(1, 0);
                        preparedStatement.setString(2, islandManager.getIslandWorldNamePrefix() + i);
                        preparedStatement.executeUpdate();
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }

                    try {
                        slimePlugin.loadWorld(slimeLoader, islandManager.getIslandWorldNamePrefix() + i, false, new SlimePropertyMap());
                        islandManager.getIslands().putIfAbsent(islandManager.getIslandWorldNamePrefix() + i, new HashMap<>());
                    } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException |
                             WorldInUseException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            getLogger().info("Nebylo mozne nacist svety! Vypinam server..");
            Bukkit.shutdown();
        }

        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("island").setExecutor(new IslandCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        islandManager.loadIslands();

        schematicLoader = new SchematicLoader(this);

        getLogger().info("Plugin spusten za " + (System.currentTimeMillis() - start) + "ms!");

    }

    @Override
    public void onDisable() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().startsWith("islands")) {
                Bukkit.unloadWorld(world.getName(), true);
            }
        }
    }

    public MariaDB getMariaDB() {
        return mariaDB;
    }

    public SlimePlugin getSlimePlugin() {
        return slimePlugin;
    }

    public IslandManager getIslandManager() {
        return islandManager;
    }

    public SchematicLoader getSchematicLoader() {
        return schematicLoader;
    }
}
