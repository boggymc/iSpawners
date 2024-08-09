package com.boggy.espawners.command;

import com.boggy.espawners.ISpawners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("about"))) {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "iSpawners");
            player.sendMessage("Made by Boggy");
            player.sendMessage("For support contact " + ChatColor.BLUE + "boggymc" + ChatColor.WHITE + " on Discord");
        } else if (args.length == 2 || args.length == 3) {
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }

            if (args[0].equalsIgnoreCase("setstacksize")) {
                Block block = player.getTargetBlockExact(5);
                if (block != null && block.getState() instanceof CreatureSpawner) {
                    CreatureSpawner spawner = (CreatureSpawner) block.getState();
                    try {
                        int stackSize = Integer.parseInt(args[1]);
                        this.plugin.setStackSize(spawner, stackSize);
                        player.sendMessage(ChatColor.GREEN + "Spawner stack amount set to " + stackSize);
                    } catch (NumberFormatException nfe) {
                        player.sendMessage(ChatColor.RED + "Incorrect usage! Please use /ispawners setstacksize <number>");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You are not looking at a spawner!");
                }
            } else if (args[0].equalsIgnoreCase("givespawner")) {
                String spawnerType = args[1].toUpperCase();
                if (this.plugin.getConfig().getConfigurationSection("spawners").getKeys(false).contains(spawnerType)) {
                    Player targetPlayer = player;
                    if (args.length == 3) {
                        targetPlayer = Bukkit.getPlayer(args[2]);
                        if (targetPlayer == null) {
                            player.sendMessage(ChatColor.RED + "Player not found!");
                            return false;
                        }
                    }
                    giveSpawnerToPlayer(spawnerType, targetPlayer);
                    player.sendMessage(ChatColor.GREEN + "Spawner given to " + targetPlayer.getName());
                } else {
                    player.sendMessage(ChatColor.RED + "Spawner type not found in config!");
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "Incorrect usage! Please use /ispawners givespawner <type> [player]");
        }

        return true;
    }

    private void giveSpawnerToPlayer(String spawnerType, Player player) {
        ItemStack spawner = new ItemStack(Material.SPAWNER);
        ItemMeta spawnerMeta = spawner.getItemMeta();
        spawnerMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.YELLOW + ChatColor.BOLD + spawnerType + " Spawner");
        spawnerMeta.getPersistentDataContainer().set(new NamespacedKey(this.plugin, "spawnerType"), PersistentDataType.STRING, spawnerType);
        spawner.setItemMeta(spawnerMeta);
        player.getInventory().addItem(spawner);
    }
}
