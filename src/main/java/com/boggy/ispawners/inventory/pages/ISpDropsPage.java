package com.boggy.ispawners.inventory.pages;

import com.boggy.ispawners.inventory.ISpInventoryHolder;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class ISpDropsPage extends ISpInventoryHolder {

    private final int PAGE_NUMBER;
    private final int TOTAL_PAGES;


    public ISpDropsPage(int pageNumber, int totalPages, ItemStack[] itemStacks, int stackSize, EntityType spawnerType)  {
        super();

        this.PAGE_NUMBER = pageNumber;
        this.TOTAL_PAGES = totalPages;

        this.title = (stackSize == 1 ? "" : (stackSize + " ")) + (spawnerType == null ? "Empty" : WordUtils.capitalizeFully(spawnerType.name())) + (stackSize > 1 ? " Spawners" : " Spawner");
        this.title += " (Page " + PAGE_NUMBER + "/" + TOTAL_PAGES + ")";

        inventory.setItem(45, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        inventory.setItem(46, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        inventory.setItem(47, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        inventory.setItem(48, this.getPrevPageItem(pageNumber, totalPages));
        inventory.setItem(49, this.getSellAllItem(1));
        inventory.setItem(50, this.getNextPageItem(pageNumber, totalPages));

        inventory.setItem(51, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        inventory.setItem(52, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        inventory.setItem(53, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        this.inventory.setStorageContents(itemStacks);

    }


    @Override
    public void onClick(InventoryClickEvent e) {
        switch(e.getAction()){

            case PICKUP_ALL, PICKUP_HALF, PICKUP_ONE, PICKUP_SOME:
                return;
            case DROP_ALL_CURSOR, DROP_ALL_SLOT, DROP_ONE_CURSOR, DROP_ONE_SLOT:
                if(e.getClickedInventory() instanceof PlayerInventory)
                    e.getWhoClicked().sendMessage("Is player inventory");
                else
                    e.getWhoClicked().sendMessage("Is not player inventory");
            default:
                e.setCancelled(true);
        }
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
