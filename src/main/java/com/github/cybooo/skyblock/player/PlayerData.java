package com.github.cybooo.skyblock.player;

public class PlayerData {

    private double money;
    private long timePlayed;

    public PlayerData() {
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void addMoney(double money) {
        this.money += money;
    }

    public void removeMoney(double money) {
        this.money -= money;
    }

    public long getTimePlayed() {
        return timePlayed;
    }

    public void setTimePlayed(long timePlayed) {
        this.timePlayed = timePlayed;
    }

    public void addTimePlayed(long timePlayed) {
        this.timePlayed += timePlayed;
    }
}
