package com.boggy.espawners.gui;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PageUtil {
    public static List<ItemStack> getPageItems(final List<ItemStack> items, final int page, final int spaces) {
        final int upperBound = page * spaces;
        final int lowerBound = upperBound - spaces;
        final List<ItemStack> newItems = new ArrayList<ItemStack>();
        for (int i = lowerBound; i < upperBound; ++i) {
            try {
                newItems.add(items.get(i));
            } catch (final IndexOutOfBoundsException ex) {
                break;
            }
        }
        return newItems;
    }

    public static boolean isPageValid(final List<ItemStack> items, final int page, final int spaces) {
        if (page <= 0) {
            return false;
        }
        final int upperBound = page * spaces;
        final int lowerBound = upperBound - spaces;
        return items.size() > lowerBound;
    }
}
