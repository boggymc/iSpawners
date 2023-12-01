package com.boggy.ispawners;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private final ISpawners plugin;

    public FileConfiguration file;

//    Config properties
    @Getter
    private boolean CONFIG_VERSION;
    @Getter
    private  int refreshTime;
    @Getter
    private int maxDrops;

    public Config(){
        this.plugin = ISpawners.getInstance();
        this.plugin.saveDefaultConfig();
        this.file = this.plugin.getConfig();
        this.loadConfigData();

    }

    public void loadConfigData(){
        this.refreshTime = this.file.getInt("refresh-time");
        this.maxDrops = this.file.getInt("max-drops");
    }


}
