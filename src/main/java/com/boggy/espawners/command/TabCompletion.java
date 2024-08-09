package com.boggy.espawners.command;

import org.bukkit.command.*;
import java.util.*;

public class TabCompletion implements TabCompleter
{
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length == 1) {
            final ArrayList<String> tab = new ArrayList<String>();
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
