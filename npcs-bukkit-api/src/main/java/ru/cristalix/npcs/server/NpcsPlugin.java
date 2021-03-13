package ru.cristalix.npcs.server;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cristalix.npcs.data.NpcBehaviour;
import ru.cristalix.npcs.data.NpcData;

public class NpcsPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Npcs.init(this);
        Npcs.spawn(Npc.builder()
                .location(new Location(Bukkit.getWorlds().get(1), 100, 100, 10000))
                .name("§6Редкий стив")
                .build()
        );
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        boolean slim = Math.random() > 0.5;
        Npcs.spawn(Npc.builder()
                .name(slim ? "alex" : "steve").location(e.getPlayer().getLocation()).slimArms(slim).build());
    }


}
