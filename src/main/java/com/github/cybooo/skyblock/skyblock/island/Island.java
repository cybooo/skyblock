package com.github.cybooo.skyblock.skyblock.island;


import org.bukkit.Location;

public class Island {

    private final int id;
    private final String owner;
    private final String islandWorld;
    private final Location islandCenter;
    private Location spawnLocation;

    public Island(int id, String owner, String islandWorld, Location islandCenter, Location spawnLocation) {
        this.id = id;
        this.owner = owner;
        this.islandWorld = islandWorld;
        this.islandCenter = islandCenter;
        this.spawnLocation = spawnLocation;
    }

    public int getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getIslandWorld() {
        return islandWorld;
    }

    public Location getIslandCenter() {
        return islandCenter;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
}
