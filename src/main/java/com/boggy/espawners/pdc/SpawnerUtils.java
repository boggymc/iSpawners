package com.boggy.espawners.pdc;

import com.boggy.espawners.ISpawners;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SpawnerUtils {

    public static int getPrice(ISpawners plugin, CreatureSpawner spawner) {
        ArrayList<Material> drops = plugin.getDrops(spawner);

        if (drops == null || drops.isEmpty()) {
            return 0;
        } else {
            int total = 0;
            for (Material drop : drops) {
                if (plugin.getConfig().getConfigurationSection("prices").contains(drop.name())) {
                    total += plugin.getConfig().getConfigurationSection("prices").getInt(drop.name());
                }
            }
            return total;
        }
    }

    public static double getFillPercent(ISpawners plugin, CreatureSpawner spawner) {
        double maxDrops = getMaxDrops(plugin, spawner);
        double currentDrops = 0;
        if (plugin.getDrops(spawner) != null) {
            currentDrops = plugin.getDrops(spawner).size();
        }
        double percentage = (currentDrops / maxDrops) * 100;
        percentage = ((double)((int)(percentage*100.0)))/100.0;
        return percentage;
    }

    public static int getMaxDrops(ISpawners plugin, CreatureSpawner spawner) {
        int maxDrops = plugin.getConfig().getInt("max_items");
        int stackSize = plugin.getStackSize(spawner);
        double multiplier = plugin.getConfig().getDouble("max_items_multiplier");
        double finalMaxDrops = 0;
        for (int i = 0; i < stackSize; i++) {
            finalMaxDrops += maxDrops * multiplier;
        }
        return (int) finalMaxDrops;
    }

    public static String getType(ISpawners plugin, ItemStack im) {
        return im.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "spawnerType"), PersistentDataType.STRING);
    }

    public static String formatCurrency(double money) {
        NumberFormat fmt = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
        return fmt.format(money);
    }
}
