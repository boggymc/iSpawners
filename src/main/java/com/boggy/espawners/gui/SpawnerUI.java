package com.boggy.espawners.gui;

import com.boggy.espawners.ISpawners;
import com.boggy.espawners.pdc.SpawnerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpawnerUI {
    public SpawnerUI(Player player, CreatureSpawner spawner, ISpawners plugin) {
        HashMap<Player, CreatureSpawner> openedSpawners = plugin.getSpawnerUITracker();
        for (Player playerSpawnerOpen : openedSpawners.keySet()) {
            if (openedSpawners.get(playerSpawnerOpen).equals(spawner)) {
                if (playerSpawnerOpen.getOpenInventory().getTitle().contains("Spawner")) {
                    return;
                }
            }
        }
        if (!plugin.getSpawners().contains(spawner)) {
            player.closeInventory();
            return;
        }
        plugin.getSpawnerUITracker().remove(player, spawner);
        plugin.getSpawnerUITracker().put(player, spawner);

        int stackSize = plugin.getStackSize(spawner);

        String spawnerType = spawner.getSpawnedType().toString().toLowerCase();
        String spawnerTypeCapitalised = spawnerType.substring(0, 1).toUpperCase() + spawnerType.substring(1);
        String spawnerText = (stackSize > 1) ? "Spawners" : "Spawner";
        String items = "0";

        if (plugin.getDrops(spawner) != null) {
           items = String.valueOf(plugin.getDrops(spawner).size());
        }


        Inventory gui = Bukkit.createInventory(null, 27, stackSize + " " + spawnerTypeCapitalised + " " + spawnerText);

        ItemStack drops = new ItemStack(Material.CHEST);
        ItemMeta dropsMeta = drops.getItemMeta();
        dropsMeta.setDisplayName(net.md_5.bungee.api.ChatColor.of("#ffaa00") + "" + ChatColor.BOLD + "SPAWNER STORAGE");
        dropsMeta.setLore(List.of(net.md_5.bungee.api.ChatColor.of("#ffaa00") + items + ChatColor.WHITE + " Items"));
        dropsMeta.setLocalizedName("spawnerUI");
        drops.setItemMeta(dropsMeta);
        gui.setItem(11, drops);

        ItemStack xp = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta xpMeta = xp.getItemMeta();
        xpMeta.setDisplayName(net.md_5.bungee.api.ChatColor.of("#36ffa4") + "" + ChatColor.BOLD + "COLLECT XP");
        xpMeta.setLore(List.of(net.md_5.bungee.api.ChatColor.of("#36ffa4") + String.valueOf(plugin.getXP(spawner)) + ChatColor.WHITE + " XP Points"));
        xpMeta.setLocalizedName("spawnerUI");
        xp.setItemMeta(xpMeta);
        gui.setItem(15, xp);

        ItemStack middle;

        Material head = Material.getMaterial(spawnerType.toUpperCase() + "_HEAD");

        if (head != null) {
            middle = new ItemStack(Material.valueOf(spawnerType.toUpperCase() + "_HEAD"));
        } else {
            middle = new ItemStack(Material.SPAWNER);
        }

        ItemMeta middleMeta = middle.getItemMeta();
        middleMeta.setDisplayName(net.md_5.bungee.api.ChatColor.of("#fc6aae") + "" + ChatColor.BOLD + stackSize + " " + spawnerType.toUpperCase() + " " + spawnerText.toUpperCase());
        middleMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        middleMeta.setLocalizedName("spawnerUI");
        ArrayList<String> lore = new ArrayList<>();
        double fillPercent = SpawnerUtils.getFillPercent(plugin, spawner);
        lore.add(net.md_5.bungee.api.ChatColor.of("#fc6aae") + String.valueOf(fillPercent) + "%" + ChatColor.WHITE + " filled");
        middleMeta.setLore(lore);
        middle.setItemMeta(middleMeta);
        gui.setItem(13, middle);

        player.openInventory(gui);
    }

}
