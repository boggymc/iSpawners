package com.boggy.ispawners.spawner.listener;

import com.boggy.ispawners.ISpawners;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.WordUtils;
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
    private ISpawners plugin;
    public SpawnerBreakListener(ISpawners plugin) {
        this.plugin = plugin;
    }


    public boolean canBreakSpawner(ItemStack item){
//        If the item isn't a pickaxe
        if(!item.getType().toString().contains("PICKAXE")) return false;
//        If the item has silk touch
        return item.hasItemMeta() && item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
    }

    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent e) {

        if(!(e.getBlock().getState() instanceof CreatureSpawner spawner)) return;

        Player player = e.getPlayer();

        if(!canBreakSpawner(player.getInventory().getItemInMainHand())){
            e.setCancelled(true);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You need silk touch to pick up spawners!"));
            return;
        }

        String spawnedType = spawner.getSpawnedType().toString();
        if(spawnedType == null) return;

        spawnedType = WordUtils.capitalizeFully(spawnedType);
        ItemStack spawnerDrop = new ItemStack(Material.SPAWNER);
        ItemMeta spawnerMeta = spawnerDrop.getItemMeta();

        spawnerMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.YELLOW + "" + ChatColor.BOLD + spawnedType + " spawner");
        spawnerMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "spawnerType"), PersistentDataType.STRING, spawnedType);
        spawnerDrop.setItemMeta(spawnerMeta);

        int stackSize = plugin.getStackSize(spawner);
        int toRemove = player.isSneaking() ? 64 : 1;

        if(stackSize <= 0){
            plugin.removeSpawner(spawner);
            e.getBlock().setType(Material.AIR);
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), spawnerDrop);
        } else {
            plugin.updateStackSize(spawner, -toRemove);
            spawnerDrop.setAmount(toRemove);
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), spawnerDrop);
        }

        e.setCancelled(true);
    }
}
