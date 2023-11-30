package com.boggy.ispawners.spawner.listener;

import com.boggy.ispawners.ISpawners;
import com.boggy.ispawners.gui.SpawnerUI;
import com.boggy.ispawners.pdc.SpawnerUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class SpawnerInteractListener implements Listener {
    private final ISpawners plugin;
    public SpawnerInteractListener(ISpawners plugin) {
        this.plugin = plugin;
    }

    public boolean tryingToStack(ItemStack heldItem, CreatureSpawner spawner){
//        Checks if they're holding a spawner
        if(!heldItem.getType().equals(Material.SPAWNER)) return false;
//        Gets the SpawnerType of the held spawner
        String heldSpawnerType = SpawnerUtils.getType(plugin, heldItem);
        if(heldSpawnerType == null) return false;
//        Returns whether they're the same SpawnerType
        return heldSpawnerType.equalsIgnoreCase((spawner.getSpawnedType().toString()));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        if (e.getHand() == null || e.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        Player player = e.getPlayer();

        Block targetedBlock = player.getTargetBlockExact(5, FluidCollisionMode.NEVER);
        if (targetedBlock == null) return;
        if (!(targetedBlock.getState() instanceof CreatureSpawner spawner)) return;
        if (spawner.getSpawnedType().equals(EntityType.DROPPED_ITEM)) return;


        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (tryingToStack(heldItem, spawner)){
            e.setCancelled(true);
            int maxStackSize = plugin.getConfig().getInt("max_stacked_spawners");
            int currentStackSize = plugin.getStackSize(spawner);
            int addStackSize = player.isSneaking() ? heldItem.getAmount() : 1;

            if(currentStackSize >= maxStackSize){
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You have reached the maximum number of stacked spawners!"));
                return;
            }
            if(currentStackSize+addStackSize > maxStackSize){
                addStackSize = maxStackSize - currentStackSize;
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You have reached the maximum number of stacked spawners!"));
            }
            heldItem.setAmount(heldItem.getAmount() - addStackSize);
            plugin.updateStackSize(spawner, addStackSize);

            return;
        }
//
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
