package com.boggy.espawners.spawner.listener;

import com.boggy.espawners.ISpawners;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SpawnerBreakListener implements Listener {
    private ISpawners plugin;
    public SpawnerBreakListener(ISpawners plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent e) {



        CreatureSpawner spawner;

        if (e.getBlock().getState() instanceof CreatureSpawner) {
            spawner = (CreatureSpawner) e.getBlock().getState();
        } else {
            return;
        }


        if (e.getPlayer().getInventory().getItemInMainHand().hasItemMeta() && e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getEnchants().containsKey(Enchantment.SILK_TOUCH)) {
            if (!e.getPlayer().getInventory().getItemInMainHand().getType().toString().contains("PICKAXE")) {
                e.setCancelled(true);
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You need silk touch to pick up spawners!"));
                return;
            }

            String spawnedType = spawner.getSpawnedType().toString().toLowerCase();
            if (spawnedType == null) {
                return;
            }
            spawnedType = spawnedType.substring(0, 1).toUpperCase() + spawnedType.substring(1);

            ItemStack spawnerDrop = new ItemStack(Material.SPAWNER);
            ItemMeta spawnerMeta = spawnerDrop.getItemMeta();

            spawnerMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.YELLOW + "" + ChatColor.BOLD + spawnedType + " spawner");
            spawnerMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "spawnerType"), PersistentDataType.STRING, spawnedType);
            spawnerDrop.setItemMeta(spawnerMeta);

            int stackSize = plugin.getStackSize(spawner);

            if (e.getPlayer().isSneaking()) {
                if ((stackSize - 64) <= 0) {
                    plugin.removeSpawner(spawner);
                    e.getBlock().setType(Material.AIR);
                    spawnerDrop.setAmount(stackSize);
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), spawnerDrop);
                } else {
                    plugin.updateStackSize(spawner, -64);
                    spawnerDrop.setAmount(64);
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), spawnerDrop);
                }
                e.setCancelled(true);
            } else {
                if ((stackSize - 1) <= 0) {
                    plugin.removeSpawner(spawner);
                    e.getBlock().setType(Material.AIR);
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), spawnerDrop);
                } else {
                    plugin.updateStackSize(spawner, -1);
                    e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), spawnerDrop);
                }
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You need silk touch to pick up spawners!"));
        }
    }
}
