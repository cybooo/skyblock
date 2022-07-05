package com.github.cybooo.skyblock.commands;

import com.github.cybooo.skyblock.SkyBlockPlugin;
import com.github.cybooo.skyblock.island.Island;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.ConcurrentMap;

public class IslandCommand implements CommandExecutor {

    private final SkyBlockPlugin plugin;
    private final Cache<Player, Player> invites;

    public IslandCommand(SkyBlockPlugin plugin) {
        this.plugin = plugin;
        this.invites = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(2)).build();
    }

    private void sendHelp(Player player) {
        player.sendMessage("§c§lPříkazy:");
        player.sendMessage("§e/island panel §7Otevře ovládací panel ostrova");
        player.sendMessage("§e/island invite <Hráč> §7Pozve hráče na tvůj ostrov.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Tento prikaz neni dostupny z konzole.");
            return false;
        }
        Island island = plugin.getIslandManager().getIsland(player);
        if (args.length == 0 && island == null) {
            if (!plugin.getIslandManager().hasIsland(player)) {
                player.sendTitle("§6§lSkyBlock", "§7Vytvářím tvůj ostrov..", 5, 40, 5);
                plugin.getIslandManager().createIsland(player);
            } else {
                player.sendMessage("§cTvůj ostrov není na tomto serveru dostupný!");
            }
        } else {
            if (args.length == 0) {
                sendHelp(player);
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("panel")) {
                    plugin.getMenuManager().getControlPanelMenu().open(player);
                } else {
                    sendHelp(player);
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("invite")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        player.sendMessage("§cPožadovaný hráč není online!");
                        return false;
                    }
                    if (target.getName().equals(player.getName())) {
                        player.sendMessage("§cNemůžeš zvát sám sebe!");
                        return false;
                    }
                    if (plugin.getIslandManager().getIsland(player) == null) {
                        player.sendMessage("§cNemáš žádný ostrov!");
                        return false;
                    }
                    if (plugin.getIslandManager().getIsland(player).getMembers().size() >= 4) {
                        player.sendMessage("§cTvůj ostrov je plný!");
                        return false;
                    }
                    if (plugin.getIslandManager().getIsland(target) != null) {
                        player.sendMessage("§cPožadovaný hráč již je na jiném ostrově!");
                        return false;
                    }
                    if (!plugin.getIslandManager().getIsland(player).getOwner().equals(player.getName())) {
                        player.sendMessage("§cNejsi vlastník ostrova!");
                        return false;
                    }
                    ConcurrentMap<Player, Player> map = invites.asMap();
                    if (map.get(player) != null) {
                        player.sendMessage("§cJiž jsi někoho pozval, vyčkej na příjmutí nebo expiraci pozvánky!");
                        return false;
                    }
                    for (Player inviter : map.keySet()) {
                        if (map.get(inviter).equals(target)) {
                            player.sendMessage("§cDaný hráč má již od někoho pozvánku, vyčkej na příjmutí nebo expiraci pozvánky!");
                            return false;
                        }
                    }
                    invites.put(player, target);
                    player.sendMessage("§aPozvánka odeslána!");
                    target.sendMessage("§7Hráč §e" + player.getName() + "§7 tě pozval na svůj ostrov!");
                    TextComponent textComponent = new TextComponent("§a§lPŘÍJMOUT POZVÁNKU");
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island acceptinvite " + player.getName()));
                    target.spigot().sendMessage(textComponent);
                } else if (args[0].equalsIgnoreCase("acceptinvite")) {
                    ConcurrentMap<Player, Player> map = invites.asMap();
                    if (!map.containsValue(player)) {
                        player.sendMessage("§cNemáš žádnou aktivní pozvánku!");
                        return false;
                    }
                    if (plugin.getIslandManager().getIsland(player) != null) {
                        player.sendMessage("§cJiž jsi na nějakém ostrově!");
                        map.values().remove(player);
                        return false;
                    }
                    Island inviterIsland = null;
                    for (Player inviter : map.keySet()) {
                        if (map.get(inviter).equals(player)) {
                            inviterIsland = plugin.getIslandManager().getIsland(inviter);
                        }
                    }
                    if (inviterIsland == null) {
                        player.sendMessage("§cNěco se pokazilo, ostrov nenalzenen!");
                        return false;
                    }
                    Bukkit.getPlayer(inviterIsland.getOwner()).sendMessage("§7Hráč §e" + player.getName() + "§7 příjmul tvojí pozvánku!");
                    player.sendMessage("§aPozvánka přijata!");
                    inviterIsland.getMembers().add(player.getName());
                    plugin.getIslandManager().addMemberToIsland(player, inviterIsland);
                }
            }
        }
        return true;
    }

}
