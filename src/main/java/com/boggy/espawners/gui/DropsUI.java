package com.boggy.espawners.gui;

import org.bukkit.entity.*;
import org.bukkit.block.*;
import com.boggy.espawners.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import java.util.*;
import org.bukkit.inventory.meta.*;

public class DropsUI
{
    public DropsUI(final Player player, final CreatureSpawner spawner, final ISpawners plugin, final int page) {
        if (!plugin.getSpawners().contains(spawner)) {
            player.closeInventory();
            return;
        }
        final int stackSize = plugin.getStackSize(spawner);
        final String spawnerType = spawner.getSpawnedType().toString().toLowerCase();
        final String spawnerTypeCapitalised = spawnerType.substring(0, 1).toUpperCase() + spawnerType.substring(1);
        final String spawnerText = (stackSize > 1) ? "Spawners" : "Spawner";
        final Inventory gui = Bukkit.createInventory((InventoryHolder)null, 54, stackSize + " " + spawnerTypeCapitalised + " " + spawnerText);
        final List<ItemStack> allItems = new ArrayList<ItemStack>();
        ArrayList<Material> materials = plugin.getDrops(spawner);
        if (materials == null) {
            materials = new ArrayList<Material>();
            materials.add(Material.AIR);
        }
        for (final Material material : materials) {
            if (material == null) {
                break;
            }
            allItems.add(new ItemStack(material));
        }
        ItemStack left;
        ItemMeta leftMeta;
        if (PageUtil.isPageValid(allItems, page - 1, 2880)) {
            left = new ItemStack(Material.SPECTRAL_ARROW);
            leftMeta = left.getItemMeta();
            leftMeta.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + "BACKWARDS");
        }
        else {
            left = new ItemStack(Material.ARROW);
            leftMeta = left.getItemMeta();
            leftMeta.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + "BACKWARDS");
        }
        leftMeta.setLocalizedName("" + page);
        left.setItemMeta(leftMeta);
        gui.setItem(48, left);
        ItemStack right;
        ItemMeta rightMeta;
        if (PageUtil.isPageValid(allItems, page + 1, 2880)) {
            right = new ItemStack(Material.SPECTRAL_ARROW);
            rightMeta = right.getItemMeta();
            rightMeta.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + "FORWARDS");
        }
        else {
            right = new ItemStack(Material.ARROW);
            rightMeta = right.getItemMeta();
            rightMeta.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + "FORWARDS");
        }
        right.setItemMeta(rightMeta);
        gui.setItem(50, right);
        for (final ItemStack is : PageUtil.getPageItems(allItems, page, 2880)) {
            gui.addItem(new ItemStack[] { is });
        }
        gui.setItem(45, new ItemStack(Material.AIR));
        gui.setItem(46, new ItemStack(Material.AIR));
        gui.setItem(47, new ItemStack(Material.AIR));
        gui.setItem(51, new ItemStack(Material.AIR));
        gui.setItem(52, new ItemStack(Material.AIR));
        gui.setItem(53, new ItemStack(Material.AIR));
        final ItemStack sell = new ItemStack(Material.GOLD_INGOT);
        final ItemMeta sellMeta = sell.getItemMeta();
        sellMeta.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + "SELL ALL");
        sell.setItemMeta(sellMeta);
        gui.setItem(49, sell);
        player.openInventory(gui);
    }
}
