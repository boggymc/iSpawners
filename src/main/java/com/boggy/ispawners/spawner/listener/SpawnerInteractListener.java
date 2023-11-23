package com.boggy.ispawners.spawner.listener;

import com.boggy.ispawners.ISpawners;
import com.boggy.ispawners.gui.SpawnerUI;
import com.boggy.ispawners.pdc.SpawnerUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class SpawnerInteractListener implements Listener {
    private ISpawners plugin;
    public SpawnerInteractListener(ISpawners plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() == null) { return; }
        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) { return; }
        Player player = e.getPlayer();

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.SPAWNER)) {
                CreatureSpawner spawner = (CreatureSpawner) e.getClickedBlock().getState();

                Block block = e.getPlayer().getTargetBlockExact(5);
                if (block == null) {
                    return;
                }
                if (block.getState() instanceof CreatureSpawner) {
                    if (spawner.getSpawnedType() == null || spawner.getSpawnedType().equals(EntityType.DROPPED_ITEM)) {
                        return;
                    }
                    if (player.getInventory().getItemInMainHand().hasItemMeta()) {
                        if (SpawnerUtils.getType(plugin, player.getInventory().getItemInMainHand()) != null) {
                            String spawnerType = SpawnerUtils.getType(plugin, player.getInventory().getItemInMainHand()).toUpperCase();
                            System.out.println(spawner.getSpawnedType());
                            if (spawner.getSpawnedType().toString().equals(spawnerType)) {
                                e.setCancelled(true);
                                int maxStackSize = plugin.getConfig().getInt("max_stacked_spawners");
                                if (player.isSneaking()) {
                                    int increase = player.getInventory().getItemInMainHand().getAmount();
                                    if (plugin.getStackSize(spawner) + increase >= maxStackSize) {
                                        if (plugin.getStackSize(spawner) >= maxStackSize) {
                                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You have reached the maximum number of stacked spawners!"));
                                            return;
                                        }
                                        increase = maxStackSize - plugin.getStackSize(spawner);
                                        System.out.println(increase);
                                        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - increase);
                                        plugin.updateStackSize(spawner, increase);
                                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You have reached the maximum number of stacked spawners!"));
                                        return;
                                    }
                                    player.getInventory().getItemInMainHand().setAmount(0);
                                    plugin.updateStackSize(spawner, increase);
                                    return;
                                } else {
                                    if (plugin.getStackSize(spawner) >= maxStackSize) {
                                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You have reached the maximum number of stacked spawners!"));
                                        return;
                                    }
                                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                                    plugin.updateStackSize(spawner, 1);
                                    return;
                                }
                            } else {
                                e.setCancelled(true);
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You cannot stack different spawner types!"));
                                return;
                            }
                        }
                    }
                }

                if (!spawner.getPersistentDataContainer().has(new NamespacedKey(plugin, "spawnerID"), PersistentDataType.STRING)) {
                    spawner.getPersistentDataContainer().set(new NamespacedKey(plugin, "spawnerID"), PersistentDataType.STRING, String.valueOf(UUID.randomUUID()));
                    spawner.update();
                }

                if (!plugin.getSpawners().contains(spawner)) {
                    plugin.createSpawner(spawner);
                }
                plugin.getSpawnerUITracker().put(e.getPlayer(), spawner);

                new SpawnerUI(e.getPlayer(), spawner, plugin).open(player);
            }
        }
    }
}
