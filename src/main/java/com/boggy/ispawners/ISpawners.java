package com.boggy.ispawners;

import com.boggy.ispawners.inventory.ISpViewersManager;
import com.boggy.ispawners.inventory.listeners.InventoryListeners;
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
    private ISpConfig ispConfig;
    private SpawnersManager spawnersManager;
    private ISpViewersManager viewersManager;

    @Override
    public void onEnable() {
        instance = this;
        this.ispConfig = new ISpConfig();
        this.spawnersManager = new SpawnersManager();
        this.viewersManager = new ISpViewersManager();

        this.registerEvents();
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new SpawnerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerSpawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListeners(), this);
//        spawnerGenerator = new SpawnerGenerator(this);
    }

}