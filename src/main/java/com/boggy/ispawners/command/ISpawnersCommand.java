package com.boggy.ispawners.command;

import com.boggy.ispawners.ISpawners;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ISpawnersCommand implements CommandExecutor {

    private ISpawners plugin;
    public ISpawnersCommand(ISpawners plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("about"))) {
            player.sendMessage(ChatColor.of("#7dedfa") + "" + ChatColor.BOLD + "iSpawners");
            player.sendMessage("Made by Boggy");
            player.sendMessage("For support contact " + ChatColor.of("#89a2fa") + "boggymc" + ChatColor.WHITE + " on discord");
        } else if (args.length == 2) {
            if (!player.isOp()) { return false; }
            if (args[0].equalsIgnoreCase("setstacksize")) {
                Block block = player.getTargetBlockExact(5);

                if (block != null && block.getState() instanceof CreatureSpawner spawner) {
                    if (!isInt(args[1])) {
                        player.sendMessage(ChatColor.RED + "Incorrect usage! Please use /ispawners setspawnerstack <number>");
                        return false;
                    }

                    int stackSize = Integer.parseInt(args[1]);
                    plugin.setStackSize(spawner, stackSize);

                    player.sendMessage(ChatColor.GREEN + "Spawner stack amount set to " + stackSize);
                } else {
                    player.sendMessage(ChatColor.RED + "You are not looking at a spawner!");
                }

            } else if (args[0].equalsIgnoreCase("givespawner")) {
                if (plugin.getConfig().getConfigurationSection("spawners").getKeys(false).contains(args[1].toUpperCase())) {
                    ItemStack spawner = new ItemStack(Material.SPAWNER);
                    ItemMeta spawnerMeta = spawner.getItemMeta();

                    String spawnedType = args[1].toLowerCase().substring(0, 1).toUpperCase() + args[1].substring(1);

                    spawnerMeta.setDisplayName(org.bukkit.ChatColor.RESET + "" + org.bukkit.ChatColor.YELLOW + "" + org.bukkit.ChatColor.BOLD + spawnedType + " spawner");
                    spawnerMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "spawnerType"), PersistentDataType.STRING, spawnedType);
                    spawner.setItemMeta(spawnerMeta);
                    player.getInventory().addItem(spawner);
                } else {
                    player.sendMessage(ChatColor.RED + "Spawner type not found in config!");
                }
            }

        }

        return false;
    }

    public static boolean isInt(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

}
