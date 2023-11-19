package com.boggy.espawners.command;

import com.boggy.espawners.ISpawners;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

            if (args[0].equalsIgnoreCase("setstacksize")) {
                if (!player.isOp()) { return false; }
                Block block = player.getTargetBlockExact(5);

                if (block != null && block.getState() instanceof CreatureSpawner spawner) {
                    try {
                        Integer.parseInt(args[1]);
                    } catch (NumberFormatException nfe) {
                        player.sendMessage(ChatColor.RED + "Incorrect usage! Please use /ispawners setspawnerstack <number>");
                        return false;
                    }

                    int stackSize = Integer.parseInt(args[1]);
                    plugin.setStackSize(spawner, stackSize);

                    player.sendMessage(ChatColor.GREEN + "Spawner stack amount set to " + stackSize);
                } else {
                    player.sendMessage(ChatColor.RED + "You are not looking at a spawner!");
                }

            }

        }

        return false;
    }


}
