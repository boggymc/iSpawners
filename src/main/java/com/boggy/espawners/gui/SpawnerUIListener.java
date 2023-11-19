package com.boggy.espawners.gui;

import com.boggy.espawners.ISpawners;
import com.boggy.espawners.pdc.SpawnerUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class SpawnerUIListener implements Listener {
    private ISpawners plugin;

    public SpawnerUIListener(ISpawners plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();

        CreatureSpawner spawner = plugin.getSpawnerUITracker().get(player);

        if (item == null || item.getType().equals(Material.AIR)) {
            return;
        }

        if (e.getView().getTitle().contains("Spawner")) {
            e.setCancelled(true);
            if (e.getInventory().getSize() == 54 && e.getInventory().getItem(50).getType().toString().contains("ARROW")) {
                int page = Integer.parseInt(e.getInventory().getItem(48).getItemMeta().getLocalizedName());
                if (e.getRawSlot() == 48 && e.getCurrentItem().getType().equals(Material.SPECTRAL_ARROW)) {
                    new DropsUI(player, spawner, plugin, page - 1);
                } else if (e.getRawSlot() == 50 && e.getCurrentItem().getType().equals(Material.SPECTRAL_ARROW)) {
                    new DropsUI(player, spawner, plugin, page + 1);
                } else if (e.getRawSlot() == 49 && e.getCurrentItem().getType().equals(Material.GOLD_INGOT)) {
                    handleSell(player, spawner, true);
                    new DropsUI(player, spawner, plugin, 1);
                }
            }
            if (item.getItemMeta().hasLocalizedName() && item.getItemMeta().getLocalizedName().equals("spawnerUI")) {
                if (item.getType().equals(Material.CHEST)) {
                    player.closeInventory();
                    new DropsUI(player, spawner, plugin, 1);
                } else if (item.getType().equals(Material.EXPERIENCE_BOTTLE)) {
                    handleXP(player, spawner, item);
                } else if (item.getType().equals(Material.GOLD_INGOT)) {
                    handleSell(player, spawner, true);
                    new DropsUI(player, spawner, plugin, 1);
                } else if (item.getType().toString().contains("HEAD") || item.getType().equals(Material.SPAWNER)) {
                    handleSell(player, spawner, true);
                    handleXP(player, spawner, e.getInventory().getItem(15));
                    ItemMeta itemMeta = item.getItemMeta();
                    ArrayList<String> middleLore = new ArrayList<>();
                    middleLore.add(net.md_5.bungee.api.ChatColor.of("#fc6aae") + "0.0%" + ChatColor.WHITE + " filled");
                    itemMeta.setLore(middleLore);
                    item.setItemMeta(itemMeta);
                    ItemStack drops = e.getInventory().getItem(11);
                    ItemMeta dropsMeta = drops.getItemMeta();
                    dropsMeta.setLore(Arrays.asList(net.md_5.bungee.api.ChatColor.of("#ffaa00") + "0" + ChatColor.WHITE + " Items"));
                    drops.setItemMeta(dropsMeta);
                }
            }
        }
    }


    private void handleXP(Player player, CreatureSpawner spawner, ItemStack item) {
        if (!plugin.getSpawners().contains(spawner)) {
            player.closeInventory();
            return;
        }
        int xp = plugin.getXP(spawner);
        plugin.setXP(spawner, 0);
        player.giveExp(xp / 20);
        ItemMeta xpMeta = item.getItemMeta();
        xpMeta.setDisplayName(net.md_5.bungee.api.ChatColor.of("#36ffa4") + "" + ChatColor.BOLD + "COLLECT XP");
        xpMeta.setLore(Arrays.asList(net.md_5.bungee.api.ChatColor.of("#36ffa4") + "0" + ChatColor.WHITE + " XP Points"));
        item.setItemMeta(xpMeta);
        if (xp > 0) {
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        }
    }

    private void handleSell(Player player, CreatureSpawner spawner, Boolean sound) {
        if (!plugin.getSpawners().contains(spawner)) {
            player.closeInventory();
            return;
        }
        int price = SpawnerUtils.getPrice(plugin, spawner);
        if (price > 0) {
            plugin.clearDrops(plugin, spawner);
            ISpawners.getEcon().depositPlayer(player, price);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "+$" + SpawnerUtils.formatCurrency(price)));
            if (sound) {
                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e){
        if (e.getView().getTitle().contains("Spawner")) {
            e.setCancelled(true);
        }
    }
}
