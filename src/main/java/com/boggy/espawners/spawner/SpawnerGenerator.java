package com.boggy.espawners.spawner;

import com.boggy.espawners.ISpawners;
import com.boggy.espawners.pdc.SpawnerUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public class SpawnerGenerator {
    @NonNull
    private ISpawners plugin;
    public void startGeneration() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            List<CreatureSpawner> spawners = plugin.getSpawners();
            if (spawners.isEmpty()) {
                return;
            }
            for (CreatureSpawner spawner : spawners) {
                boolean loaded = false;
                for (Entity entity : spawner.getWorld().getNearbyEntities(spawner.getLocation(), 16, 16, 16)) {
                    if (entity instanceof Player) {
                        loaded = true;
                        break;
                    }
                }

                if (loaded) {
                    plugin.updateDrops(plugin, spawner, plugin.getStackSize(spawner));
                    int xpToAdd = plugin.getConfig().getConfigurationSection("spawners")
                            .getConfigurationSection(spawner.getSpawnedType().toString()).getInt("exp");
                    if (plugin.getDrops(spawner) == null) {
                        return;
                    }
                    if (plugin.getDrops(spawner).size() < SpawnerUtils.getMaxDrops(plugin, spawner)) {
                        plugin.updateXP(spawner, xpToAdd, plugin.getStackSize(spawner));
                    }
                }
            }

        }, 0, plugin.getConfig().getInt("refresh_time"));
    }

}
