package com.boggy.ispawners.inventory.pages;

import com.boggy.ispawners.inventory.ISpInventoryHolder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;

public class ISpDropsPage extends ISpInventoryHolder {

    public ISpDropsPage(Player player, String title, int pageNumber, int totalPages, Collection<ItemStack> pageItemStacks)  {
        super(54, title, null);

        int stacks = pageItemStacks.size();
        player.sendMessage("Page Stacks:" + stacks);

        inventory.setItem(48, this.getPrevPageItem(pageNumber, totalPages));
        inventory.setItem(49, this.getSellAllItem(1));
        inventory.setItem(50, this.getNextPageItem(pageNumber, totalPages));

        pageItemStacks.forEach( (item) -> {
            inventory.addItem(item);
        });

        player.openInventory(this.getInventory());
    }

//    public Collection<ItemStack> addItems(){
//        inventory.all()
//    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);

    }

    @Override
    public void onDrag(InventoryDragEvent e) {

    }

    public ItemStack getPrevPageItem(int dropCount, int currentPage){
        ItemStack prevItem = getPageItem(false, "BACKWARDS");
        ItemMeta prevMeta = prevItem.getItemMeta();
        prevMeta.setLocalizedName(currentPage + "");
        prevItem.setItemMeta(prevMeta);
        return prevItem;
    }

    public ItemStack getNextPageItem(int dropCount, int currentPage){
        ItemStack nextItem = getPageItem(true, "FORWARDS");
        ItemMeta nextMeta = nextItem.getItemMeta();
        nextMeta.setLocalizedName(currentPage + "");
        nextItem.setItemMeta(nextMeta);
        return nextItem;
    }

    public ItemStack getSellAllItem(int dropCount){
        ItemStack sellAllItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta sellAllMeta = sellAllItem.getItemMeta();
        if(dropCount > 0) {
            sellAllMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
            sellAllMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        sellAllMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "SELL ALL");
        sellAllItem.setItemMeta(sellAllMeta);
        return sellAllItem;
    }

    private ItemStack getPageItem(boolean pageExists, String text) {
        ItemStack itemStack = new ItemStack(pageExists ? Material.SPECTRAL_ARROW : Material.ARROW);

        ItemMeta meta = itemStack.getItemMeta();
        ChatColor color = pageExists ? ChatColor.YELLOW : ChatColor.RED;
        meta.setDisplayName(color.toString() + ChatColor.BOLD + text);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
