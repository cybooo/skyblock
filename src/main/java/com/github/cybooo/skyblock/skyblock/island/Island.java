package com.github.cybooo.skyblock.skyblock.island;


import org.bukkit.Location;

import java.util.List;

public class Island {

    private final int id;
    private final String owner;
    private final long createdMillis;
    private final String islandWorld;
    private final Location islandCenter;
    private Location spawnLocation;
    private List<String> members;

    public Island(int id, String owner, long createdMillis, String islandWorld, Location islandCenter, Location spawnLocation, List<String> members) {
        this.id = id;
        this.owner = owner;
        this.createdMillis = createdMillis;
        this.islandWorld = islandWorld;
        this.islandCenter = islandCenter;
        this.spawnLocation = spawnLocation;
        this.members = members;
    }

    public int getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public long getCreatedMillis() {
        return createdMillis;
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

    public List<String> getMembers() {
        return members;
    }
}
