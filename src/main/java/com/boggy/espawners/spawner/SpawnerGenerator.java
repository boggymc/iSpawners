package com.boggy.espawners.spawner;

import com.boggy.espawners.ISpawners;
import com.boggy.espawners.pdc.SpawnerUtils;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SpawnerGenerator {
    @NonNull
    private ISpawners plugin;

    public void startGeneration() {
        Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            List<CreatureSpawner> spawners = this.plugin.getSpawners();
            if (spawners.isEmpty()) {
                return;
            }
            for (CreatureSpawner spawner : spawners) {
                boolean loaded = false;
                for (Entity entity : spawner.getWorld().getNearbyEntities(spawner.getLocation(), 16.0, 16.0, 16.0)) {
                    if (entity instanceof Player) {
                        loaded = true;
                        break;
                    }
                }
                if (!loaded) continue;

                int stackSize = this.plugin.getStackSize(spawner);
                if (stackSize <= 0) continue; // TEST: Skip if no spawners are stacked

                this.plugin.updateDrops(this.plugin, spawner, stackSize);
                int xpToAdd = this.plugin.getConfig().getConfigurationSection("spawners").getConfigurationSection(spawner.getSpawnedType().toString()).getInt("exp");

                if (this.plugin.getDrops(spawner) == null) {
                    return;
                }
                if (this.plugin.getDrops(spawner).size() < SpawnerUtils.getMaxDrops(this.plugin, spawner)) {
                    this.plugin.updateXP(spawner, xpToAdd, stackSize);
                }
            }
        }, 0L, this.plugin.getConfig().getInt("refresh_time"));
    }

    public SpawnerGenerator(@NonNull ISpawners plugin) {
        if (plugin == null) {
            throw new NullPointerException("plugin is marked non-null but is null");
        }
        this.plugin = plugin;
    }
}
