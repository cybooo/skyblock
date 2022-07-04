package com.github.cybooo.skyblock.skyblock.schematic;

import com.fastasyncworldedit.core.FaweAPI;
import com.github.cybooo.skyblock.skyblock.SkyBlockPlugin;
import com.github.cybooo.skyblock.skyblock.island.Island;
import com.github.cybooo.skyblock.skyblock.listeners.PlayerListener;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SchematicLoader {

    private final SkyBlockPlugin plugin;

    public SchematicLoader(SkyBlockPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadSchematic(Location location) {

        File file = new File(plugin.getDataFolder(), "schematics/default_island.schematic");
        BlockVector3 to = BlockVector3.at(0, 0, 0);
        World world = FaweAPI.getWorld(location.getWorld().getName());
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (ClipboardReader clipboardReader = format.getReader(Files.newInputStream(file.toPath()));
                 Clipboard clipboard = clipboardReader.read()) {
                try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(to)
                            .ignoreAirBlocks(true)
                            .build();
                    Operations.complete(operation);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
