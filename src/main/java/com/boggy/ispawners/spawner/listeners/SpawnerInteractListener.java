package com.boggy.ispawners.spawner.listeners;

import com.boggy.ispawners.ISpConfig;
import com.boggy.ispawners.ISpawners;
import com.boggy.ispawners.inventory.pages.ISpMenuPage;
import com.boggy.ispawners.spawner.SpawnersManager;
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

import javax.annotation.Nullable;
import java.util.UUID;

public class SpawnerInteractListener implements Listener {
    private final ISpawners plugin;
    private final ISpConfig config;
    private final SpawnersManager spawnersManager;
    public SpawnerInteractListener() {
        this.plugin = ISpawners.getInstance();
        this.config = this.plugin.getIspConfig();
        this.spawnersManager = this.plugin.getSpawnersManager();
    }

    public boolean tryingToStack(ItemStack heldItem, CreatureSpawner spawner){
//        Checks if they're holding a spawner
        if(!heldItem.getType().equals(Material.SPAWNER)) return false;
        if(spawnersManager.getSpawnerType(spawner) == null) return false;
//        Gets the SpawnerType of the held spawner
        EntityType heldSpawnerType = spawnersManager.getSpawnerType(heldItem);
        if(heldSpawnerType == null) return false;
//        Returns whether they're the same SpawnerType
        return heldSpawnerType.name().equalsIgnoreCase((spawner.getSpawnedType().name()));
    }

    public @Nullable CreatureSpawner getRightClickedSpawner(PlayerInteractEvent e){

        if(e.getHand() == null || e.getHand().equals(EquipmentSlot.OFF_HAND)) return null;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return null;

        Player player = e.getPlayer();

        Block targetedBlock = player.getTargetBlockExact(5, FluidCollisionMode.NEVER);
        if (targetedBlock == null) return null;

        return targetedBlock.getState() instanceof CreatureSpawner ? (CreatureSpawner) targetedBlock.getState() : null;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

//        Gets the spawner they right clicked, returns null if its not a spawner
        CreatureSpawner spawner = this.getRightClickedSpawner(e);
        if(spawner == null) return;

        Player player = e.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if(heldItem.getType().equals(Material.DIAMOND_SWORD)){
            this.spawnersManager.updateDrops(spawner, 1.5);
            return;
        }

//
        if (tryingToStack(heldItem, spawner)){
            player.sendMessage("trying to stack");
            e.setCancelled(true);
            this.stackSpawner(spawner, player, heldItem);
            return;
        }
//
        if (!spawner.getPersistentDataContainer().has(new NamespacedKey(plugin, "spawnerID"), PersistentDataType.STRING)) {
            player.sendMessage("doesn't have a spawner id");
            spawner.getPersistentDataContainer().set(new NamespacedKey(plugin, "spawnerID"), PersistentDataType.STRING, String.valueOf(UUID.randomUUID()));
            spawner.update();
        }


        if(!plugin.getSpawnersManager().exists(spawner)) {
            player.sendMessage("doesnt exist");
            spawnersManager.createSpawner(spawner, 1);
        }

        new ISpMenuPage(player, spawner, this.spawnersManager.getSpawnerType(spawner), this.spawnersManager.getStackSize(spawner));

    }

    public void stackSpawner(CreatureSpawner spawner, Player player, ItemStack heldItem){
        int maxStackSize = plugin.getConfig().getInt("max_stacked_spawners");
        int currentStackSize = spawnersManager.getStackSize(spawner);
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
        spawnersManager.setStackSize(spawner, currentStackSize+addStackSize);
        spawner.update();
    }
}
