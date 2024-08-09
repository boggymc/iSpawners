package com.boggy.espawners.gui;

import com.boggy.espawners.ISpawners;
import com.boggy.espawners.pdc.SpawnerUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpawnerUI {

    public SpawnerUI(final Player player, final CreatureSpawner spawner, final ISpawners plugin) {
        final HashMap<Player, CreatureSpawner> openedSpawners = plugin.getSpawnerUITracker();
        for (final Player playerSpawnerOpen : openedSpawners.keySet()) {
            if (openedSpawners.get(playerSpawnerOpen).equals(spawner) && playerSpawnerOpen.getOpenInventory().getTitle().contains("Spawner")) {
                return;
            }
        }
        if (!plugin.getSpawners().contains(spawner)) {
            player.closeInventory();
            return;
        }
        plugin.getSpawnerUITracker().remove(player, spawner);
        plugin.getSpawnerUITracker().put(player, spawner);
        final int stackSize = plugin.getStackSize(spawner);
        final String spawnerType = spawner.getSpawnedType().toString().toLowerCase();
        final String spawnerTypeCapitalised = spawnerType.substring(0, 1).toUpperCase() + spawnerType.substring(1);
        final String spawnerText = (stackSize > 1) ? "Spawners" : "Spawner";
        String items = "0";
        if (plugin.getDrops(spawner) != null) {
            items = String.valueOf(plugin.getDrops(spawner).size());
        }
        final Inventory gui = Bukkit.createInventory((InventoryHolder) null, 27, stackSize + " " + spawnerTypeCapitalised + " " + spawnerText);
        final ItemStack drops = new ItemStack(Material.CHEST);
        final ItemMeta dropsMeta = drops.getItemMeta();
        dropsMeta.setDisplayName("" + ChatColor.of("#ffaa00") + org.bukkit.ChatColor.BOLD + "SPAWNER STORAGE");
        dropsMeta.setLore((List) List.of(ChatColor.of("#ffaa00") + items + org.bukkit.ChatColor.WHITE + " Items"));
        dropsMeta.setLocalizedName("spawnerUI");
        drops.setItemMeta(dropsMeta);
        gui.setItem(11, drops);
        final ItemStack xp = new ItemStack(Material.EXPERIENCE_BOTTLE);
        final ItemMeta xpMeta = xp.getItemMeta();
        xpMeta.setDisplayName("" + ChatColor.of("#36ffa4") + org.bukkit.ChatColor.BOLD + "COLLECT XP");
        xpMeta.setLore((List) List.of(ChatColor.of("#36ffa4") + String.valueOf(plugin.getXP(spawner)) + org.bukkit.ChatColor.WHITE + " XP Points"));
        xpMeta.setLocalizedName("spawnerUI");
        xp.setItemMeta(xpMeta);
        gui.setItem(15, xp);
        final Material head = Material.getMaterial(spawnerType.toUpperCase() + "_HEAD");
        ItemStack middle;
        if (head != null) {
            middle = new ItemStack(Material.valueOf(spawnerType.toUpperCase() + "_HEAD"));
        } else {
            middle = new ItemStack(Material.SPAWNER);
        }
        final ItemMeta middleMeta = middle.getItemMeta();
        middleMeta.setDisplayName("" + ChatColor.of("#fc6aae") + org.bukkit.ChatColor.BOLD + stackSize + " " + spawnerType.toUpperCase() + " " + spawnerText.toUpperCase());
        middleMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        middleMeta.setLocalizedName("spawnerUI");
        final ArrayList<String> lore = new ArrayList<String>();
        final double fillPercent = SpawnerUtils.getFillPercent(plugin, spawner);
        lore.add(ChatColor.of("#fc6aae") + String.valueOf(fillPercent) + "%" + org.bukkit.ChatColor.WHITE + " filled");
        middleMeta.setLore((List) lore);
        middle.setItemMeta(middleMeta);
        gui.setItem(13, middle);
        player.openInventory(gui);
    }
}
