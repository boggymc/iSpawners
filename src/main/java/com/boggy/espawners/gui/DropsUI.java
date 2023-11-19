package com.boggy.espawners.gui;

import com.boggy.espawners.ISpawners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DropsUI {
    public DropsUI(Player player, CreatureSpawner spawner, ISpawners plugin, int page) {
        if (!plugin.getSpawners().contains(spawner)) {
            player.closeInventory();
            return;
        }
        int stackSize = plugin.getStackSize(spawner);

        String spawnerType = spawner.getSpawnedType().toString().toLowerCase();
        String spawnerTypeCapitalised = spawnerType.substring(0, 1).toUpperCase() + spawnerType.substring(1);
        String spawnerText = (stackSize > 1) ? "Spawners" : "Spawner";

        Inventory gui = Bukkit.createInventory(null, 54, stackSize + " " + spawnerTypeCapitalised + " " + spawnerText);


        ArrayList<Material> materials;
        List<ItemStack> allItems = new ArrayList<>();
        materials = plugin.getDrops(spawner);
        if (materials == null) {
            materials = new ArrayList<>();
            materials.add(Material.AIR);
        }
        for (Material material : materials) {
            if (material == null) { break; }
            allItems.add(new ItemStack(material));
        }

        ItemStack left;
        ItemMeta leftMeta;
        if (PageUtil.isPageValid(allItems, page - 1, 45 * 64)) {
            left = new ItemStack(Material.SPECTRAL_ARROW);
            leftMeta = left.getItemMeta();
            leftMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "BACKWARDS");
        } else {
            left = new ItemStack(Material.ARROW);
            leftMeta = left.getItemMeta();
            leftMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "BACKWARDS");
        }
        leftMeta.setLocalizedName(page + "");
        left.setItemMeta(leftMeta);
        gui.setItem(48, left);

        ItemStack right;
        ItemMeta rightMeta;
        if (PageUtil.isPageValid(allItems, page + 1,45 * 64)) {
            right = new ItemStack(Material.SPECTRAL_ARROW);
            rightMeta = right.getItemMeta();
            rightMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "FORWARDS");
        } else {
            right = new ItemStack(Material.ARROW);
            rightMeta = right.getItemMeta();
            rightMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "FORWARDS");
        }
        right.setItemMeta(rightMeta);
        gui.setItem(50, right);

        for (ItemStack is : PageUtil.getPageItems(allItems, page,  45 * 64)) {
            gui.addItem(is);
        }

        gui.setItem(45, new ItemStack(Material.AIR));
        gui.setItem(46, new ItemStack(Material.AIR));
        gui.setItem(47, new ItemStack(Material.AIR));
        gui.setItem(51, new ItemStack(Material.AIR));
        gui.setItem(52, new ItemStack(Material.AIR));
        gui.setItem(53, new ItemStack(Material.AIR));

        ItemStack sell = new ItemStack(Material.GOLD_INGOT);
        ItemMeta sellMeta = sell.getItemMeta();
        sellMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "SELL ALL");
        sell.setItemMeta(sellMeta);
        gui.setItem(49, sell);
        player.openInventory(gui);
    }

}
