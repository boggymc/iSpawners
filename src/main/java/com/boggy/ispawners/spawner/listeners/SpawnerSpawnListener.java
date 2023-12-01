package com.boggy.ispawners.spawner.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class SpawnerSpawnListener implements Listener {

    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent e) {
        e.setCancelled(true);
    }
}
