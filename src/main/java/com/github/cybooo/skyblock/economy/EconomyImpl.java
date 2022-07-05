package com.github.cybooo.skyblock.economy;

import com.github.cybooo.skyblock.SkyBlockPlugin;
import com.github.cybooo.skyblock.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class EconomyImpl implements Economy {

    private final SkyBlockPlugin plugin;

    public EconomyImpl(SkyBlockPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return "Gold";
    }

    public boolean hasBankSupport() {
        return false;
    }

    public int fractionalDigits() {
        return 1;
    }

    public String format(double amount) {
        return Utils.formatCurrency(amount);
    }

    public String currencyNamePlural() {
        return "Mince";
    }

    public String currencyNameSingular() {
        return "Mince";
    }

    public boolean hasAccount(String playerName) {
        return true;
    }

    public boolean hasAccount(OfflinePlayer player) {
        return true;
    }

    public boolean hasAccount(String playerName, String worldName) {
        return true;
    }

    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return true;
    }

    public double getBalance(String playerName) {
        return plugin.getPlayerManager().getPlayerData(playerName).getMoney();
    }

    public double getBalance(OfflinePlayer player) {
        return plugin.getPlayerManager().getPlayerData(player.getName()).getMoney();
    }

    public double getBalance(String playerName, String world) {
        return plugin.getPlayerManager().getPlayerData(playerName).getMoney();
    }

    public double getBalance(OfflinePlayer player, String world) {
        return plugin.getPlayerManager().getPlayerData(player.getName()).getMoney();
    }

    public boolean has(String playerName, double amount) {
        return plugin.getPlayerManager().getPlayerData(playerName).getMoney() >= amount;

    }

    public boolean has(OfflinePlayer player, double amount) {
        return plugin.getPlayerManager().getPlayerData(player.getName()).getMoney() >= amount;
    }

    public boolean has(String playerName, String worldName, double amount) {
        return plugin.getPlayerManager().getPlayerData(playerName).getMoney() >= amount;
    }

    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return plugin.getPlayerManager().getPlayerData(player.getName()).getMoney() >= amount;
    }

    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (plugin.getPlayerManager().getPlayerData(playerName).getMoney() >= amount) {
            plugin.getPlayerManager().getPlayerData(playerName).removeMoney(amount);
            return new EconomyResponse(amount, plugin.getPlayerManager().getPlayerData(playerName).getMoney(), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(amount, plugin.getPlayerManager().getPlayerData(playerName).getMoney(), EconomyResponse.ResponseType.FAILURE, "");
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (plugin.getPlayerManager().getPlayerData(player.getName()).getMoney() >= amount) {
            plugin.getPlayerManager().getPlayerData(player.getName()).removeMoney(amount);
            return new EconomyResponse(amount, plugin.getPlayerManager().getPlayerData(player.getName()).getMoney(), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(amount, plugin.getPlayerManager().getPlayerData(player.getName()).getMoney(), EconomyResponse.ResponseType.FAILURE, "");
    }

    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        if (plugin.getPlayerManager().getPlayerData(playerName).getMoney() >= amount) {
            plugin.getPlayerManager().getPlayerData(playerName).removeMoney(amount);
            return new EconomyResponse(amount, plugin.getPlayerManager().getPlayerData(playerName).getMoney(), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(amount, plugin.getPlayerManager().getPlayerData(playerName).getMoney(), EconomyResponse.ResponseType.FAILURE, "");
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        if (plugin.getPlayerManager().getPlayerData(player.getName()).getMoney() >= amount) {
            plugin.getPlayerManager().getPlayerData(player.getName()).removeMoney(amount);
            return new EconomyResponse(amount, plugin.getPlayerManager().getPlayerData(player.getName()).getMoney(), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(amount, plugin.getPlayerManager().getPlayerData(player.getName()).getMoney(), EconomyResponse.ResponseType.FAILURE, "");
    }

    public EconomyResponse depositPlayer(String playerName, double amount) {
        plugin.getPlayerManager().getPlayerData(playerName).addMoney(amount);
        return new EconomyResponse(amount, plugin.getPlayerManager().getPlayerData(playerName).getMoney(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        plugin.getPlayerManager().getPlayerData(player.getName()).addMoney(amount);
        return new EconomyResponse(amount, plugin.getPlayerManager().getPlayerData(player.getName()).getMoney(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        plugin.getPlayerManager().getPlayerData(playerName).addMoney(amount);
        return new EconomyResponse(amount, plugin.getPlayerManager().getPlayerData(playerName).getMoney(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        plugin.getPlayerManager().getPlayerData(player.getName()).addMoney(amount);
        return new EconomyResponse(amount, plugin.getPlayerManager().getPlayerData(player.getName()).getMoney(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    public EconomyResponse deleteBank(String name) {
        return null;
    }

    public EconomyResponse bankBalance(String name) {
        return null;
    }

    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    public List<String> getBanks() {
        return null;
    }

    public boolean createPlayerAccount(String playerName) {
        return true;
    }

    public boolean createPlayerAccount(OfflinePlayer player) {
        return true;
    }

    public boolean createPlayerAccount(String playerName, String worldName) {
        return true;
    }

    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return true;
    }
}

