package com.github.cybooo.skyblock.menu;

import com.github.cybooo.skyblock.SkyBlockPlugin;

public class MenuManager {

    private final SkyBlockPlugin plugin;
    private final ControlPanelMenu controlPanelMenu;
    private final MemberListMenu memberListMenu;
    private final AchievementsMenu achievementsMenu;
    private final ShopMenu shopMenu;

    public MenuManager(SkyBlockPlugin plugin) {
        this.plugin = plugin;
        this.controlPanelMenu = new ControlPanelMenu(plugin);
        this.memberListMenu = new MemberListMenu(plugin);
        this.achievementsMenu = new AchievementsMenu(plugin);
        this.shopMenu = new ShopMenu(plugin);
    }

    public ControlPanelMenu getControlPanelMenu() {
        return controlPanelMenu;
    }

    public MemberListMenu getMemberListMenu() {
        return memberListMenu;
    }

    public AchievementsMenu getAchievementsMenu() {
        return achievementsMenu;
    }

    public ShopMenu getShopMenu() {
        return shopMenu;
    }
}
