package ru.cristalix.npcs.server;

import org.bukkit.plugin.java.JavaPlugin;

public class Bootstrap extends JavaPlugin {

    @Override
    public void onEnable() {
        Npcs.init(this);
    }

}
