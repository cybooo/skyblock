package com.github.cybooo.skyblock;

import com.github.cybooo.skyblock.achievements.AchievementManager;
import com.github.cybooo.skyblock.commands.AchievementsCommand;
import com.github.cybooo.skyblock.commands.IslandCommand;
import com.github.cybooo.skyblock.commands.MoneyCommand;
import com.github.cybooo.skyblock.commands.SetSpawnCommand;
import com.github.cybooo.skyblock.database.MariaDB;
import com.github.cybooo.skyblock.economy.EconomyImpl;
import com.github.cybooo.skyblock.island.IslandManager;
import com.github.cybooo.skyblock.listeners.PlayerListener;
import com.github.cybooo.skyblock.menu.MenuManager;
import com.github.cybooo.skyblock.player.PlayerManager;
import com.github.cybooo.skyblock.schematic.SchematicLoader;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
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
    private MenuManager menuManager;
    private SchematicLoader schematicLoader;
    private Economy economy;
    private PlayerManager playerManager;
    private AchievementManager achievementManager;

    @Override
    public void onEnable() {

        long start = System.currentTimeMillis();

        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().info("Nebylo mozne nalezt Vault, vypinam server..");
            Bukkit.shutdown();
            return;
        }

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

            SlimeLoader slimeLoader = slimePlugin.getLoader("mysql");

            for (int i = 0; i < getConfig().getInt("settings.island-worlds"); i++) {

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
                        slimePlugin.generateWorld(slimePlugin.loadWorld(slimeLoader, islandManager.getIslandWorldNamePrefix() + i, false, new SlimePropertyMap()));
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
        getCommand("money").setExecutor(new MoneyCommand(this));
        getCommand("achievements").setExecutor(new AchievementsCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        islandManager.loadIslands();
        schematicLoader = new SchematicLoader(this);
        menuManager = new MenuManager(this);

        playerManager = new PlayerManager(this);
        achievementManager = new AchievementManager(this);

        getLogger().info("Plugin spusten za " + (System.currentTimeMillis() - start) + "ms!");

    }

    @Override
    public void onDisable() {

    }

    private boolean setupEconomy() {

        if (this.getServer().getPluginManager().getPlugin("Vault") != null) {
            this.getServer().getServicesManager().register(Economy.class, new EconomyImpl(this), this, ServicePriority.Highest);
        }

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
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

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public AchievementManager getAchievementManager() {
        return achievementManager;
    }
}
