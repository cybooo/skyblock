package com.github.cybooo.skyblock.achievements;

import com.github.cybooo.skyblock.SkyBlockPlugin;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AchievementData {

    private final SkyBlockPlugin plugin;
    private final Player player;
    private final List<Achievement> completedAchievements;
    private final Map<Achievement, Long> pendingAchievements;

    public AchievementData(SkyBlockPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.completedAchievements = new ArrayList<>();
        this.pendingAchievements = new HashMap<>();
    }

    public AchievementData cache() {
        for (Achievement achievement : Achievement.values()) {
            if (plugin.getAchievementManager().isCompleted(player, achievement)) {
                completedAchievements.add(achievement);
            } else {
                pendingAchievements.put(achievement, plugin.getAchievementManager().getAchievementProgress(player, achievement));
            }
        }
        return this;
    }

    public List<Achievement> getCompletedAchievements() {
        return completedAchievements;
    }

    public Map<Achievement, Long> getPendingAchievements() {
        return pendingAchievements;
    }
}
