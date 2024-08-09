package com.boggy.espawners.spawner.listener;

import com.boggy.espawners.ISpawners;
import com.boggy.espawners.gui.SpawnerUI;
import com.boggy.espawners.pdc.SpawnerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

public class SpawnerInteractListener implements Listener {
    private ISpawners plugin;

    public SpawnerInteractListener(ISpawners plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() == null || e.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }

        Player player = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.SPAWNER)) {
            CreatureSpawner spawner = (CreatureSpawner) e.getClickedBlock().getState();
            Block block = e.getPlayer().getTargetBlockExact(5);
            if (block == null) {
                return;
            }

            if (block.getState() instanceof CreatureSpawner && player.getInventory().getItemInMainHand().hasItemMeta() && SpawnerUtils.getType(this.plugin, player.getInventory().getItemInMainHand()) != null) {
                String spawnerType = SpawnerUtils.getType(this.plugin, player.getInventory().getItemInMainHand()).toUpperCase();
                if (spawner.getSpawnedType().toString().equals(spawnerType)) {
                    e.setCancelled(true);
                    int maxStackSize = this.plugin.getConfig().getInt("max_stacked_spawners");
                    int currentStackSize = this.plugin.getStackSize(spawner);
                    int increase = player.getInventory().getItemInMainHand().getAmount();
                    int newStackSize = currentStackSize + increase;

                    if (newStackSize > maxStackSize) {
                        player.sendMessage(ChatColor.RED + "You have reached the maximum number of stacked spawners!");
                        return;
                    }

                    if (newStackSize < 0) {
                        player.sendMessage(ChatColor.RED + "Cannot stack a negative amount of spawners!");
                        return;
                    }

                    this.plugin.updateStackSize(spawner, increase);
                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - increase);
                } else {
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot stack different spawner types!");
                }
            } else {
                if (!spawner.getPersistentDataContainer().has(new NamespacedKey(this.plugin, "spawnerID"), PersistentDataType.STRING)) {
                    spawner.getPersistentDataContainer().set(new NamespacedKey(this.plugin, "spawnerID"), PersistentDataType.STRING, String.valueOf(java.util.UUID.randomUUID()));
                    spawner.update();
                }

                if (!this.plugin.getSpawners().contains(spawner)) {
                    this.plugin.createSpawner(spawner);
                }

                this.plugin.getSpawnerUITracker().put(e.getPlayer(), spawner);
                new SpawnerUI(e.getPlayer(), spawner, this.plugin);
            }
        }
    }
}
