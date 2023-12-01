package com.boggy.ispawners;

import com.boggy.ispawners.inventory.InventoryManager;
import com.boggy.ispawners.spawner.SpawnersManager;
import com.boggy.ispawners.spawner.listeners.SpawnerBreakListener;
import com.boggy.ispawners.spawner.listeners.SpawnerInteractListener;
import com.boggy.ispawners.spawner.listeners.SpawnerPlaceListener;
import com.boggy.ispawners.spawner.listeners.SpawnerSpawnListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class ISpawners extends JavaPlugin {

    @Getter
    private static ISpawners instance;
    private Config pluginConfig;
    private SpawnersManager spawnersManager;
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        instance = this;
        this.pluginConfig = new Config();
        this.spawnersManager = new SpawnersManager();
        this.inventoryManager = new InventoryManager();

        this.registerEvents();
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new SpawnerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerSpawnListener(), this);
//        spawnerGenerator = new SpawnerGenerator(this);
    }

}