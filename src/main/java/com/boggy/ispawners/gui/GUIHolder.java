package com.boggy.ispawners.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

public abstract class GUIHolder implements InventoryHolder {

    public static void init(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.HIGH)
            public void onClick(InventoryClickEvent event) {
                if (event.getInventory() == null) return;
                if (event.getInventory().getHolder() == null) return;
                if (!(event.getInventory().getHolder() instanceof GUIHolder)) return;
                ((GUIHolder) event.getInventory().getHolder()).onClick(event);
            }
        }, plugin);
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if (event.getInventory() == null) return;
                if (event.getInventory().getHolder() == null) return;
                if (!(event.getInventory().getHolder() instanceof GUIHolder)) return;
                ((GUIHolder) event.getInventory().getHolder()).onInventoryClose(event);
            }
        }, plugin);
    }

    protected Inventory inventory;

    public GUIHolder(String title, int size) {
        inventory = Bukkit.createInventory(this, size, title);
    }

    public GUIHolder() {}

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void onClick(InventoryClickEvent e) {}

    public void onInventoryClose(InventoryCloseEvent e) {}

    public void open(Player player){
        player.openInventory(inventory);
    }
}