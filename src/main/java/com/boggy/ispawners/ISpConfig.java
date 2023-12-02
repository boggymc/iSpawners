package com.boggy.ispawners;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.concurrent.ConcurrentHashMap;

public class ISpConfig {

    private final ISpawners plugin;

    public FileConfiguration file;

//    Config properties
    @Getter
    private boolean CONFIG_VERSION;
    @Getter
    private  int refreshTime;
    @Getter
    private int maxDrops;
    private double maxDropsMultiplier;
    private int maxStackedSpawners;

    private ConcurrentHashMap<Material, Double> prices;

    public ISpConfig(){
        this.plugin = ISpawners.getInstance();
        this.plugin.saveDefaultConfig();
        this.file = this.plugin.getConfig();
        this.loadConfigData();

    }

    public void loadConfigData(){
        this.refreshTime = this.file.getInt("refresh-time", 80);
        this.maxDrops = this.file.getInt("max-drops", 1000);
        this.maxDropsMultiplier = this.file.getInt("max-drops-multiple", 1000);
        this.maxStackedSpawners = this.file.getInt("max-stacked-spawners", 1000);


    }

    public double getPrice(Material material){
        return prices.get(material);
    }

}
