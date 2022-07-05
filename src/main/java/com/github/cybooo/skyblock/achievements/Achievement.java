package com.github.cybooo.skyblock.achievements;

public enum Achievement {

    CREATE_ISLAND(1, "Začátky", "Vytvoř si pomocí příkazu /island ostrov!", 1, 5000),
    BREAK_10_BLOCKS(2, "První bloky", "Znič 10 bloků!", 10, 5000);

    private final int id;
    private final String name;
    private final String description;
    private final long goal;
    private final int reward;

    Achievement(int id, String name, String description, long goal, int reward) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.goal = goal;
        this.reward = reward;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getGoal() {
        return goal;
    }

    public int getReward() {
        return reward;
    }
}
