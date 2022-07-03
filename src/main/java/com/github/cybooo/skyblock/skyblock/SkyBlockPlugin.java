package com.github.cybooo.skyblock.skyblock;

import com.github.cybooo.skyblock.skyblock.database.MariaDB;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyBlockPlugin extends JavaPlugin {

    private MariaDB mariaDB;

    @Override
    public void onEnable() {

        mariaDB = new MariaDB(
                getConfig().getString("database.host"),
                getConfig().getInt("database.port"),
                getConfig().getString("database.username"),
                getConfig().getString("database.password"),
                getConfig().getString("database.database")
        );

    }

    @Override
    public void onDisable() {

    }

    public MariaDB getMariaDB() {
        return mariaDB;
    }
}
