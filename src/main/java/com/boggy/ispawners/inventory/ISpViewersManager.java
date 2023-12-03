package com.boggy.ispawners.inventory;

import com.boggy.ispawners.ISpawners;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ISpViewersManager {

    ISpawners plugin;
    HashMap<CreatureSpawner, Player> openedSpawners;

    public boolean isOpened(@NotNull CreatureSpawner spawner){
      return openedSpawners.containsKey(spawner);
    };

    public void setViewer(CreatureSpawner spawner, Player player){
        openedSpawners.put(spawner, player);
    }

    public void removeViewer(CreatureSpawner spawner){
        openedSpawners.remove(spawner);
    }



}
