package com.boggy.espawners.gui;

import com.boggy.espawners.ISpawners;
import com.boggy.espawners.pdc.SpawnerUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpawnerUIListener implements Listener {
    private ISpawners plugin;

    public SpawnerUIListener(final ISpawners plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        final ItemStack item = e.getCurrentItem();
        final Player player = (Player) e.getWhoClicked();
        final CreatureSpawner spawner = this.plugin.getSpawnerUITracker().get(player);
        if (item == null || item.getType().equals((Object) Material.AIR)) {
            return;
        }
        if (e.getView().getTitle().contains("Spawner")) {
            e.setCancelled(true);
            if (e.getInventory().getSize() == 54 && e.getInventory().getItem(50).getType().toString().contains("ARROW")) {
                final int page = Integer.parseInt(e.getInventory().getItem(48).getItemMeta().getLocalizedName());
                if (e.getRawSlot() == 48 && e.getCurrentItem().getType().equals((Object) Material.SPECTRAL_ARROW)) {
                    new DropsUI(player, spawner, this.plugin, page - 1);
                } else if (e.getRawSlot() == 50 && e.getCurrentItem().getType().equals((Object) Material.SPECTRAL_ARROW)) {
                    new DropsUI(player, spawner, this.plugin, page + 1);
                } else if (e.getRawSlot() == 49 && e.getCurrentItem().getType().equals((Object) Material.GOLD_INGOT)) {
                    this.handleSell(player, spawner, true);
                    new DropsUI(player, spawner, this.plugin, 1);
                }
            }
            if (item.getItemMeta().hasLocalizedName() && item.getItemMeta().getLocalizedName().equals("spawnerUI")) {
                if (item.getType().equals((Object) Material.CHEST)) {
                    player.closeInventory();
                    new DropsUI(player, spawner, this.plugin, 1);
                } else if (item.getType().equals((Object) Material.EXPERIENCE_BOTTLE)) {
                    this.handleXP(player, spawner, item);
                } else if (item.getType().equals((Object) Material.GOLD_INGOT)) {
                    this.handleSell(player, spawner, true);
                    new DropsUI(player, spawner, this.plugin, 1);
                } else if (item.getType().toString().contains("HEAD") || item.getType().equals((Object) Material.SPAWNER)) {
                    this.handleSell(player, spawner, true);
                    this.handleXP(player, spawner, e.getInventory().getItem(15));
                    final ItemMeta itemMeta = item.getItemMeta();
                    final ArrayList<String> middleLore = new ArrayList<String>();
                    middleLore.add(ChatColor.of("#fc6aae") + "0.0%" + org.bukkit.ChatColor.WHITE + " filled");
                    itemMeta.setLore((List) middleLore);
                    item.setItemMeta(itemMeta);
                    final ItemStack drops = e.getInventory().getItem(11);
                    final ItemMeta dropsMeta = drops.getItemMeta();
                    dropsMeta.setLore((List) Arrays.asList(ChatColor.of("#ffaa00") + "0" + org.bukkit.ChatColor.WHITE + " Items"));
                    drops.setItemMeta(dropsMeta);
                }
            }
        }
    }

    private void handleXP(final Player player, final CreatureSpawner spawner, final ItemStack item) {
        if (!this.plugin.getSpawners().contains(spawner)) {
            player.closeInventory();
            return;
        }
        final int xp = this.plugin.getXP(spawner);
        this.plugin.setXP(spawner, 0);
        player.giveExp(xp / 20);
        final ItemMeta xpMeta = item.getItemMeta();
        xpMeta.setDisplayName("" + ChatColor.of("#36ffa4") + org.bukkit.ChatColor.BOLD + "COLLECT XP");
        xpMeta.setLore((List) Arrays.asList(ChatColor.of("#36ffa4") + "0" + org.bukkit.ChatColor.WHITE + " XP Points"));
        item.setItemMeta(xpMeta);
        if (xp > 0) {
            player.playSound((Entity) player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
    }

    private void handleSell(final Player player, final CreatureSpawner spawner, final Boolean sound) {
        if (!this.plugin.getSpawners().contains(spawner)) {
            player.closeInventory();
            return;
        }
        final int price = SpawnerUtils.getPrice(this.plugin, spawner);
        if (price > 0) {
            this.plugin.clearDrops(this.plugin, spawner);
            ISpawners.getEcon().depositPlayer((OfflinePlayer) player, (double) price);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent) new TextComponent(org.bukkit.ChatColor.GREEN + "+$" + SpawnerUtils.formatCurrency(price)));
            if (sound) {
                player.playSound((Entity) player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent e) {
        if (e.getView().getTitle().contains("Spawner")) {
            e.setCancelled(true);
        }
    }
}
