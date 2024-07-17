package com.boggy.espawners.pdc;

import com.boggy.espawners.*;
import org.bukkit.block.*;
import org.bukkit.inventory.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.persistence.*;
import java.util.*;
import java.text.*;

public class SpawnerUtils
{
    public static int getPrice(final ISpawners plugin, final CreatureSpawner spawner) {
        final ArrayList<Material> drops = plugin.getDrops(spawner);
        if (drops == null || drops.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (final Material drop : drops) {
            if (plugin.getConfig().getConfigurationSection("prices").contains(drop.name())) {
                total += plugin.getConfig().getConfigurationSection("prices").getInt(drop.name());
            }
        }
        return total;
    }

    public static double getFillPercent(final ISpawners plugin, final CreatureSpawner spawner) {
        final double maxDrops = getMaxDrops(plugin, spawner);
        double currentDrops = 0.0;
        if (plugin.getDrops(spawner) != null) {
            currentDrops = plugin.getDrops(spawner).size();
        }
        double percentage = currentDrops / maxDrops * 100.0;
        percentage = (int)(percentage * 100.0) / 100.0;
        return percentage;
    }

    public static int getMaxDrops(final ISpawners plugin, final CreatureSpawner spawner) {
        final int maxDrops = plugin.getConfig().getInt("max_items");
        final int stackSize = plugin.getStackSize(spawner);
        final double multiplier = plugin.getConfig().getDouble("max_items_multiplier");
        return (int) (maxDrops * multiplier * stackSize);
    }

    public static String getType(final ISpawners plugin, final ItemStack im) {
        return im.getItemMeta().getPersistentDataContainer().get(new NamespacedKey((Plugin)plugin, "spawnerType"), PersistentDataType.STRING);
    }

    public static String formatCurrency(final double money) {
        final NumberFormat fmt = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
        return fmt.format(money);
    }
}
