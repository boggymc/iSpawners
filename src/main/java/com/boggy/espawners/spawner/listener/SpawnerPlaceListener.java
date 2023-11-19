package com.boggy.espawners.spawner.listener;

import com.boggy.espawners.ISpawners;
import com.boggy.espawners.pdc.SpawnerUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class SpawnerPlaceListener implements Listener {

    private ISpawners plugin;

    public SpawnerPlaceListener(ISpawners plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawnerPlace(BlockPlaceEvent e) {

        if (!(e.getBlockPlaced().getType().equals(Material.SPAWNER))) {
            return;
        }

        Player player = e.getPlayer();

        if (e.getBlockPlaced().getState() instanceof CreatureSpawner) {
            CreatureSpawner spawner = (CreatureSpawner) e.getBlockPlaced().getState();
            if (e.getItemInHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "spawnerType"), PersistentDataType.STRING)) {
                String spawnerType = SpawnerUtils.getType(plugin, e.getItemInHand());
                spawner.setSpawnedType(EntityType.valueOf(spawnerType.toUpperCase()));
                spawner.getPersistentDataContainer().set(new NamespacedKey(plugin, "spawnerID"), PersistentDataType.STRING, String.valueOf(UUID.randomUUID()));
                spawner.getPersistentDataContainer().set(new NamespacedKey(plugin, "exp"), PersistentDataType.INTEGER, 0);
                spawner.update();

                plugin.createSpawner(spawner);

                if (player.isSneaking()) {
                    int itemStackSize = e.getItemInHand().getAmount();
                    e.getItemInHand().setAmount(0);
                    plugin.updateStackSize(spawner, itemStackSize);
                }
            }
        }
    }
}
