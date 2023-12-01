package com.boggy.ispawners.spawner.listeners;

import com.boggy.ispawners.ISpawners;
import com.boggy.ispawners.spawner.SpawnersManager;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnerPlaceListener implements Listener {

    private final ISpawners plugin;
    private final SpawnersManager spawnersManager;

    public SpawnerPlaceListener() {
        this.plugin = ISpawners.getInstance();
        this.spawnersManager = this.plugin.getSpawnersManager();
    }

    @EventHandler
    public void onSpawnerPlace(BlockPlaceEvent e) throws InstantiationException, IllegalAccessException {

//        Makes sure placing a spawner
        if(!(e.getBlockPlaced().getState() instanceof CreatureSpawner spawner)) return;

        Player player = e.getPlayer();
        ItemStack heldSpawner = e.getItemInHand();
        EntityType spawnerType = spawnersManager.getSpawnerType(heldSpawner);
        if(spawnerType != null)
            spawner.setSpawnedType(spawnerType);

        int stackSize = player.isSneaking() ? e.getItemInHand().getAmount() : 1;
        heldSpawner.setAmount(heldSpawner.getAmount() - stackSize);

        this.spawnersManager.createSpawner(spawner, stackSize);
    }
}
