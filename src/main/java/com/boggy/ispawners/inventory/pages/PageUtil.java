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
        Collection<ItemStack> fullItemStacks = new ArrayList<>();
        Collection<ItemStack> leftoverItemStacks = new ArrayList<>();

        drops.forEach( (material, amount) -> {
            viewer.sendMessage("Item: " + material + " - Amount: " + amount);
            int numberOfStacks = (amount/64);

            for(int i=0; i>numberOfStacks; i++){
                ItemStack fullStack = new ItemStack(material);
                fullStack.setAmount(64);
                fullItemStacks.add(fullStack);
            }
            int leftover = amount%64;
            ItemStack leftoverStack = new ItemStack(material);
            leftoverStack.setAmount(leftover);
            leftoverItemStacks.add(leftoverStack);
        });

        int stacksInPage= 0;
        int pageNumber = 1;
        Collection<ItemStack> pageContent = new ArrayList<>();
        for(ItemStack item : fullItemStacks){
            if(stacksInPage == 47){
                ISpDropsPage page = new ISpDropsPage(viewer, title,pageNumber, 5, pageContent);
                pages.add(page);
                stacksInPage = 0;
                pageNumber++;
                pageContent = new ArrayList<>();
            }
            pageContent.add(item);
            stacksInPage++;
        }

        return pages;
    }

}
