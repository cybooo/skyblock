package com.github.cybooo.skyblock.skyblock.schematic;

import com.github.cybooo.skyblock.skyblock.SkyBlockPlugin;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;

public class SchematicLoader {

    private final SkyBlockPlugin plugin;

    public SchematicLoader(SkyBlockPlugin plugin) {
        this.plugin = plugin;
    }

    public void pasteSchematic(Location location) {
        World world = new BukkitWorld(location.getWorld());

        File file = new File(plugin.getDataFolder() + "/schematics/default_island.schem");
        if (!file.exists()) {
            plugin.getLogger().warning("Schematatic neexistuje!");
            return;
        }
        BlockVector3 blockVector3 = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        try {
            ClipboardFormats.findByFile(file)
                    .load(file)
                    .paste(world, blockVector3, false, false, null)
                    .close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
