package com.boggy.ispawners.inventory;

import com.boggy.ispawners.ISpawners;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;

public class InventoryManager {

    ISpawners plugin;

    private HashMap<Player, CreatureSpawner> viewers;

    public InventoryManager(){
        this.plugin = ISpawners.getInstance();
        this.viewers = new HashMap<>();
    }

    public void addViewer(Player player, CreatureSpawner spawner){
        viewers.put(player, spawner);
    }

    public void removeViewer(Player player){
        viewers.remove(player);
    }

    public Collection<CreatureSpawner> viewedSpawners(){
        return this.viewers.values();
    }


}
