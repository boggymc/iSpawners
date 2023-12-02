package com.boggy.ispawners.inventory.pages;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PageUtil {

    public static List<ItemStack> getPageItems(List<ItemStack> items, int page, int spaces) {
        int upperBound = page * spaces;
        int lowerBound = upperBound - spaces;

        List<ItemStack> newItems = new ArrayList<>();
        for (int i = lowerBound; i < upperBound; i++) {
            try {
                newItems.add(items.get(i));
            } catch (IndexOutOfBoundsException ex) {
                break;
            }
        }
        return newItems;
    }

    public static boolean isPageValid(int dropCount, int page, int spaces) {
        if (page <= 0) { return false; }

        int upperBound = page * spaces;
        int lowerBound = upperBound - spaces;

        return dropCount > lowerBound;
    }

    public static List<ISpDropsPage> generatePages(Player viewer, String title, CreatureSpawner spawner, ConcurrentHashMap<Material, Integer> drops){
        List<ISpDropsPage> pages = new ArrayList<>();

        Collection<ItemStack> pageItemStacks = new ArrayList<>();

        new ISpDropsPage(viewer, title, 1, spawner, pageItemStacks);




    }

}
