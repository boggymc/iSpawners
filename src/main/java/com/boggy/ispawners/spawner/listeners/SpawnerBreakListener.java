package com.boggy.ispawners.spawner.listeners;

import com.boggy.ispawners.ISpawners;
import com.boggy.ispawners.spawner.SpawnersManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnerBreakListener implements Listener {
    private final ISpawners plugin;
    private final SpawnersManager spawnersManager;
    public SpawnerBreakListener() {
        this.plugin = ISpawners.getInstance();
        this.spawnersManager = this.plugin.getSpawnersManager();
    }

    private boolean canBreakSpawner(ItemStack item){
//        If the item isn't a pickaxe
        if(!item.getType().toString().contains("PICKAXE")) return false;
//        If the item has silk touch
        return item.hasItemMeta() && item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
    }

    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent e) {

        if(!(e.getBlock().getState() instanceof CreatureSpawner spawner)) return;

        Player player = e.getPlayer();

//        Handles breaking spawner in creative mode
        if(player.getGameMode().equals(GameMode.CREATIVE)){
            if(!player.isSneaking()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You must be sneaking to break a spawner when in creative mode"));
                e.setCancelled(true);
            } else
                this.spawnersManager.removeSpawner(spawner);
            return;
        }

//        Requirements silk pick to break spawner
        if(!canBreakSpawner(player.getInventory().getItemInMainHand())){
            e.setCancelled(true);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You need silk touch to pick up spawners!"));
            return;
        }

//        Get spawner type
        EntityType spawnedType = this.spawnersManager.getSpawnerType(spawner);
        if(spawnedType == null) return;

//        Create the spawner item to drop after breaking
        ItemStack spawnerDrop = new ItemStack(Material.SPAWNER);
        ItemMeta spawnerMeta = spawnerDrop.getItemMeta();

//        Updates the item's spawned type
        spawnerDrop.setItemMeta(this.spawnersManager.updateSpawnerType(spawnerMeta, spawnedType));

//        Updates the display name
        spawnerMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.YELLOW + "" + ChatColor.BOLD + WordUtils.capitalizeFully(spawnedType.toString()) + " spawner");

//        Saves our changes
        spawnerDrop.setItemMeta(spawnerMeta);

//        Get current stack size
        int stackSize = this.spawnersManager.getStackSize(spawner);
//        if sneaking remove 64 instead of 1
        int toRemove = player.isSneaking() ? 64 : 1;

//        if new stacksize makes spawner stack less than 0
        if(stackSize-toRemove <= 0){
//            remove spawner from storage
            this.spawnersManager.removeSpawner(spawner);
//            set the spawner to air
            e.getBlock().setType(Material.AIR);
            spawnerDrop.setAmount(stackSize);
        } else {
//            update the stack size to the new stacksize
            this.spawnersManager.setStackSize(spawner, stackSize-toRemove);
//            set item amount to the amount removing
            spawnerDrop.setAmount(toRemove);
        }

//        drops the item 1 block above the spawner
        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation().add(0,.5,0), spawnerDrop);
//        cancels the breaking spawner event (since we simulated it)
        e.setCancelled(true);
    }
}
