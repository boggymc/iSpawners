package com.boggy.espawners.spawner.listener;

import org.bukkit.event.entity.*;
import org.bukkit.event.*;

public class SpawnerSpawnListener implements Listener
{
    @EventHandler
    public void onSpawnerSpawn(final SpawnerSpawnEvent e) {
        e.setCancelled(true);
    }
}
