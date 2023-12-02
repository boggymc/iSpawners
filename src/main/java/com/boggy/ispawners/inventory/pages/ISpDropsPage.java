package com.boggy.ispawners.inventory.pages;

import com.boggy.ispawners.inventory.ISpInventoryHolder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ISpDropsPage extends ISpInventoryHolder {

    public ISpDropsPage(Player player, String title, int pageNumber, CreatureSpawner spawner, Collection<ItemStack> pageItemStacks)  {
        super(54, title, spawner);


        ConcurrentHashMap<Material, Integer> spawnerDrops = this.spawnersManager.getDrops(spawner);
        int dropCount = this.spawnersManager.getDropsCount(spawner);
        List<ItemStack> allDrops = new ArrayList<>();
        spawnerDrops.forEach( (material, amount) -> {
            ItemStack dropStack = new ItemStack(material, amount);
            dropStack.setAmount(amount);
            allDrops.add(dropStack);
        });

        inventory.setItem(48, this.getPrevPageItem(dropCount, pageNumber));
        inventory.setItem(49, this.getSellAllItem(dropCount));
        inventory.setItem(50, this.getNextPageItem(dropCount, pageNumber));

        for (ItemStack is : PageUtil.getPageItems(allDrops, pageNumber,  45 * 64)) {
            inventory.addItem(is);
        }

        player.openInventory(this.getInventory());
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);

    }

    @Override
    public void onDrag(InventoryDragEvent e) {

    }

    public ItemStack getPrevPageItem(int dropCount, int currentPage){
        ItemStack prevItem = getPageItem(PageUtil.isPageValid(dropCount, currentPage - 1, 45 * 64), "BACKWARDS");
        ItemMeta prevMeta = prevItem.getItemMeta();
        prevMeta.setLocalizedName(currentPage + "");
        prevItem.setItemMeta(prevMeta);
        return prevItem;
    }

    public ItemStack getNextPageItem(int dropCount, int currentPage){
        ItemStack nextItem = getPageItem(PageUtil.isPageValid(dropCount, currentPage + 1, 45 * 64), "FORWARDS");
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
