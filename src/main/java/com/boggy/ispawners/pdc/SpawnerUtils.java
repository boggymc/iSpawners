package com.boggy.ispawners.pdc;

public class SpawnerUtils {

//    public static int getPrice(ISpawners plugin, CreatureSpawner spawner) {
//        ArrayList<Material> drops = plugin.getDrops(spawner);
//
//        if (drops == null || drops.isEmpty()) return 0;
//
//        int total = 0;
//        for (Material drop : drops) {
//            if (plugin.getConfig().getConfigurationSection("prices").contains(drop.name())) {
//                total += plugin.getConfig().getConfigurationSection("prices").getInt(drop.name());
//            }
//        }
//        return total;
//    }
//
//    public static double getFillPercent(ISpawners plugin, CreatureSpawner spawner) {
//        double maxDrops = getMaxDrops(plugin, spawner);
//        double currentDrops = 0;
//        if (plugin.getDrops(spawner) != null) {
//            currentDrops = plugin.getDrops(spawner).size();
//        }
//        double percentage = (currentDrops / maxDrops) * 100;
//        percentage = ((double)((int)(percentage*100.0)))/100.0;
//        return percentage;
//    }
//
//    public static int getMaxDrops(ISpawners plugin, CreatureSpawner spawner) {
//        int maxDrops = plugin.getConfig().getInt("max_items");
//        int stackSize = plugin.getStackSize(spawner);
//        double multiplier = plugin.getConfig().getDouble("max_items_multiplier");
//        double finalMaxDrops = 0;
//        for (int i = 0; i < stackSize; i++) {
//            finalMaxDrops += maxDrops * multiplier;
//        }
//        return (int) finalMaxDrops;
//    }
//
//    public static String getType(ISpawners plugin, ItemStack im) {
//        return im.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "spawnerType"), PersistentDataType.STRING);
//    }
//
//    public static String formatCurrency(double money) {
//        NumberFormat fmt = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
//
//        return fmt.format(money);
//    }
//
//    public static void handleXP(Player player, CreatureSpawner spawner, ItemStack item, ISpawners plugin) {
//        if (!plugin.getSpawners().contains(spawner)) {
//            player.closeInventory();
//            return;
//        }
//        int xp = plugin.getXP(spawner);
//        plugin.setXP(spawner, 0);
//        player.giveExp(xp / 20);
//        ItemMeta xpMeta = item.getItemMeta();
//        xpMeta.setDisplayName(net.md_5.bungee.api.ChatColor.of("#36ffa4") + "" + ChatColor.BOLD + "COLLECT XP");
//        xpMeta.setLore(Arrays.asList(net.md_5.bungee.api.ChatColor.of("#36ffa4") + "0" + ChatColor.WHITE + " XP Points"));
//        item.setItemMeta(xpMeta);
//        if (xp > 0) {
//            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
//        }
//    }
//
//    public static void handleSell(Player player, CreatureSpawner spawner, Boolean sound, ISpawners plugin) {
//        if (!plugin.getSpawners().contains(spawner)) {
//            player.closeInventory();
//            return;
//        }
//        int price = SpawnerUtils.getPrice(plugin, spawner);
//        if (price > 0) {
//            plugin.clearDrops(plugin, spawner);
//            ISpawners.getEcon().depositPlayer(player, price);
//            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "+$" + SpawnerUtils.formatCurrency(price)));
//            if (sound) {
//                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
//            }
//        }
//    }
}
