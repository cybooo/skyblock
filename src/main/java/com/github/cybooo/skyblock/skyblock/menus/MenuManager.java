package com.github.cybooo.skyblock.skyblock.menus;

import com.github.cybooo.skyblock.skyblock.SkyBlockPlugin;

public class MenuManager {

    private SkyBlockPlugin plugin;
    private final ControlPanelMenu controlPanelMenu;

    public MenuManager(SkyBlockPlugin plugin) {
        this.plugin = plugin;
        this.controlPanelMenu = new ControlPanelMenu(plugin);
    }

    public ControlPanelMenu getControlPanelMenu() {
        return controlPanelMenu;
    }
}
