package com.boggy.espawners.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 1) {
            ArrayList<String> tab = new ArrayList<>();
            tab.add("about");
            if (sender.isOp()) {
                tab.add("setspawnerstack");
                tab.add("givespawner");
            }
            return tab;
        }
        return null;
    }
}
