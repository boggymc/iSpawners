package com.boggy.espawners.spawner.listener;

import com.boggy.espawners.ISpawners;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SpawnerBreakListener implements Listener {
    private final ISpawners plugin;

    public SpawnerBreakListener(ISpawners plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent e) {
        if (!(e.getBlock().getState() instanceof CreatureSpawner)) {
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner) e.getBlock().getState();
        Player player = e.getPlayer();

        if (player.getInventory().getItemInMainHand().hasItemMeta() &&
                player.getInventory().getItemInMainHand().getItemMeta().getEnchants().containsKey(Enchantment.SILK_TOUCH)) {

            if (!player.getInventory().getItemInMainHand().getType().toString().contains("PICKAXE")) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You need silk touch to pick up spawners!");
                return;
            }

            String spawnedType = spawner.getSpawnedType().toString().toLowerCase();
            if (spawnedType == null) {
                return;
            }
            spawnedType = spawnedType.substring(0, 1).toUpperCase() + spawnedType.substring(1);

            ItemStack spawnerDrop = new ItemStack(Material.SPAWNER);
            ItemMeta spawnerMeta = spawnerDrop.getItemMeta();
            spawnerMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.YELLOW + ChatColor.BOLD + spawnedType + " Spawner");
            spawnerMeta.getPersistentDataContainer().set(new NamespacedKey(this.plugin, "spawnerType"), PersistentDataType.STRING, spawnedType);
            spawnerDrop.setItemMeta(spawnerMeta);

            int stackSize = this.plugin.getStackSize(spawner);

            if (stackSize - 1 <= 0) {
                this.plugin.removeSpawner(spawner);
                e.setCancelled(true);
                e.getBlock().setType(Material.AIR);
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), spawnerDrop);
            } else {
                this.plugin.updateStackSize(spawner, -1);
                e.setCancelled(true);
                spawnerDrop.setAmount(1);
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), spawnerDrop);
            }
        } else {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You need silk touch to pick up spawners!");
        }
    }
}
