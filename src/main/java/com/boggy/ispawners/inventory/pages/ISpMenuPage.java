package com.boggy.ispawners.inventory.pages;

import com.boggy.ispawners.inventory.ISpInventoryHolder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ISpMenuPage extends ISpInventoryHolder {

    public ISpMenuPage(Player player, CreatureSpawner spawner, EntityType spawnerType, int stackSize)  {
        super(27, spawner, spawnerType, stackSize);

        this.inventory.setMaxStackSize(64);

        double dropsCount = this.spawnersManager.getDropsCount(spawner);
        this.inventory.setItem(11, this.getDropsItemStack((int) dropsCount));

        double maxDrops = this.config.getMaxDrops();
        double fillPercent = (dropsCount * 100) / maxDrops;
        fillPercent = ((double)((int)(fillPercent*100.0)))/100.0;
        this.inventory.setItem(13, this.getSpawnerTypeItemStack(spawnerType, stackSize, fillPercent));

        int xpValue = this.spawnersManager.getXp(spawner);
        this.inventory.setItem(15, this.getXPItemStack(xpValue));

//        Open's inventory
        player.openInventory(this.getInventory());
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();

        if(item == null) return;

        switch(item.getType()) {
            case CHEST:
                player.closeInventory();
                new ISpDropsPage(player, this.title, 1, this.spawner);
                break;
            case EXPERIENCE_BOTTLE:
                break;
            case GOLD_INGOT:
                this.plugin.getLogger().info("Sold all items - ISpMenuPage");
                new ISpDropsPage(player, this.title, 1, this.spawner);
                break;
            default:
                break;
        }

//        if (item.getType().equals(Material.CHEST)) {
//            player.closeInventory();
//            new DropsUI(player, spawner, plugin, 1);
//        } else if (item.getType().equals(Material.EXPERIENCE_BOTTLE)) {
//            SpawnerUtils.handleXP(player, spawner, item, plugin);
//        } else if (item.getType().equals(Material.GOLD_INGOT)) {
//            SpawnerUtils.handleSell(player, spawner, true, plugin);
//            new DropsUI(player, spawner, plugin, 1);
//        } else if (item.getType().toString().contains("HEAD") || item.getType().equals(Material.SPAWNER)) {
//            SpawnerUtils.handleSell(player, spawner, true, plugin);
//            SpawnerUtils.handleXP(player, spawner, e.getInventory().getItem(15), plugin);
//            ItemMeta itemMeta = item.getItemMeta();
//            ArrayList<String> middleLore = new ArrayList<>();
//            middleLore.add(net.md_5.bungee.api.ChatColor.of("#fc6aae") + "0.0%" + ChatColor.WHITE + " filled");
//            itemMeta.setLore(middleLore);
//            item.setItemMeta(itemMeta);
//            ItemStack drops = e.getInventory().getItem(11);
//            ItemMeta dropsMeta = drops.getItemMeta();
//            dropsMeta.setLore(Arrays.asList(net.md_5.bungee.api.ChatColor.of("#ffaa00") + "0" + ChatColor.WHITE + " Items"));
//            drops.setItemMeta(dropsMeta);
//        }
    }

    @Override
    public void onDrag(InventoryDragEvent event) {

    }

    private ItemStack getDropsItemStack(int dropsAmount){
        ItemStack drops = new ItemStack(Material.CHEST);
        ItemMeta dropsMeta = drops.getItemMeta();
        dropsMeta.setDisplayName(net.md_5.bungee.api.ChatColor.of("#ffaa00") + "" + ChatColor.BOLD + "SPAWNER STORAGE");
        dropsMeta.setLore(List.of(net.md_5.bungee.api.ChatColor.of("#ffaa00") + String.valueOf(dropsAmount) + ChatColor.WHITE + " Items"));
        dropsMeta.setLocalizedName("iSpawnersMenu");
        drops.setItemMeta(dropsMeta);
        return drops;
    }

    private ItemStack getSpawnerTypeItemStack(@Nullable EntityType spawnerType, int stackSize, double fillPercent){

        String mobName = spawnerType == null ? "Empty" : spawnerType.name();

        Material mobHead = getMobHead(mobName);
        ItemStack spawnerTypeItem = new ItemStack(mobHead);
        ItemMeta dropsMeta = spawnerTypeItem.getItemMeta();
        dropsMeta.setDisplayName((net.md_5.bungee.api.ChatColor.of("#fc6aae") + "" + ChatColor.BOLD + stackSize + " " + mobName.toUpperCase() + (stackSize > 1 ? " Spawners" : " Spawner")));
        dropsMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        dropsMeta.setLore(List.of(net.md_5.bungee.api.ChatColor.of("#fc6aae") + String.valueOf(fillPercent) + "%" + ChatColor.WHITE + " filled"));
        dropsMeta.setLocalizedName("iSpawnersMenu");
        spawnerTypeItem.setItemMeta(dropsMeta);
        return spawnerTypeItem;
    }

    private ItemStack getXPItemStack(int xp){
        ItemStack xpItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta dropsMeta = xpItem.getItemMeta();
        dropsMeta.setDisplayName(net.md_5.bungee.api.ChatColor.of("#36ffa4") + "" + ChatColor.BOLD + "COLLECT XP");
        dropsMeta.setLore(List.of(net.md_5.bungee.api.ChatColor.of("#36ffa4") + String.valueOf(xp) + ChatColor.WHITE + " XP Points"));
        dropsMeta.setLocalizedName("iSpawnersMenu");
        xpItem.setItemMeta(dropsMeta);
        return xpItem;
    }

    private Material getMobHead(String mobName){

        Material headMaterial = Material.getMaterial(mobName.toUpperCase() + "_HEAD");

        if(headMaterial == null)
            headMaterial = Material.SPAWNER;

        return headMaterial;
    }

}
