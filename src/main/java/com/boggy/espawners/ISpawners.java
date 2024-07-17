// Decompiled with: CFR 0.152
// Class Version: 17
package com.boggy.espawners;

import com.boggy.espawners.command.ISpawnersCommand;
import com.boggy.espawners.command.TabCompletion;
import com.boggy.espawners.gui.SpawnerUIListener;
import com.boggy.espawners.pdc.ListDataType;
import com.boggy.espawners.pdc.SpawnerUtils;
import com.boggy.espawners.spawner.SpawnerGenerator;
import com.boggy.espawners.spawner.listener.SpawnerBreakListener;
import com.boggy.espawners.spawner.listener.SpawnerInteractListener;
import com.boggy.espawners.spawner.listener.SpawnerPlaceListener;
import com.boggy.espawners.spawner.listener.SpawnerSpawnListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class ISpawners
        extends JavaPlugin {
    private static final String SPAWNERS_CONFIG_SECTION = "spawners";
    private static final String WORLD_KEY = "world";
    private static final String X_KEY = "x";
    private static final String Y_KEY = "y";
    private static final String Z_KEY = "z";
    private static final String STACK_SIZE_KEY = "stackSize";
    private static final String EXP_KEY = "exp";
    private static final String DROPS_KEY = "drops";
    private static final String SPAWNER_ID_KEY = "spawnerID";
    private File customConfigFile;
    private FileConfiguration customConfig;
    private SpawnerGenerator spawnerGenerator;
    @Getter
    private static Economy econ = null;
    private HashMap<Player, CreatureSpawner> spawnerUITracker = new HashMap();
    private List<CreatureSpawner> spawners = new ArrayList<CreatureSpawner>();
    private HashMap<CreatureSpawner, Integer> xpValues = new HashMap();
    private HashMap<CreatureSpawner, Integer> stackSizes = new HashMap();
    private HashMap<CreatureSpawner, String[]> spawnerDrops = new HashMap();

    public void onEnable() {
        this.saveDefaultConfig();
        this.createCustomConfig();
        this.registerEvents();
        this.loadSpawners();
        if (!this.setupEconomy()) {
            this.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", this.getDescription().getName()));
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.spawnerGenerator.startGeneration();
        this.getCommand("ispawners").setExecutor(new ISpawnersCommand(this));
        this.getCommand("ispawners").setTabCompleter(new TabCompletion());
    }

    private void loadSpawners() {
        ConfigurationSection spawnerSection;
        if (this.customConfig.getConfigurationSection(SPAWNERS_CONFIG_SECTION) == null) {
            this.customConfig.createSection(SPAWNERS_CONFIG_SECTION);
            this.saveCustomConfig();
        }
        if ((spawnerSection = this.customConfig.getConfigurationSection(SPAWNERS_CONFIG_SECTION)).getKeys(false).isEmpty()) {
            return;
        }
        for (String key : spawnerSection.getKeys(false)) {
            String[] drops;
            double z;
            double y;
            double x;
            Location loc;
            World world = Bukkit.getWorld(spawnerSection.getConfigurationSection(key).getString(WORLD_KEY));
            BlockState state = world.getBlockAt(loc = new Location(world, x = spawnerSection.getConfigurationSection(key).getDouble(X_KEY), y = spawnerSection.getConfigurationSection(key).getDouble(Y_KEY), z = spawnerSection.getConfigurationSection(key).getDouble(Z_KEY))).getState();
            if (!(state instanceof CreatureSpawner)) continue;
            CreatureSpawner spawner = (CreatureSpawner)((Object)state);
            this.spawners.add(spawner);
            int stackSize = spawnerSection.getConfigurationSection(key).getInt(STACK_SIZE_KEY);
            Integer exp = (Integer)spawner.getPersistentDataContainer().get(new NamespacedKey(this, EXP_KEY), PersistentDataType.INTEGER);
            if (exp == null) {
                exp = 0;
            }
            if ((drops = (String[])spawner.getPersistentDataContainer().get(new NamespacedKey(this, DROPS_KEY), new ListDataType(Charset.defaultCharset()))) == null) {
                drops = new String[]{Material.AIR.toString()};
            }
            this.stackSizes.put(spawner, stackSize);
            this.xpValues.put(spawner, exp);
            this.spawnerDrops.put(spawner, drops);
        }
    }

    public void createSpawner(CreatureSpawner spawner) {
        PersistentDataContainer pdc = spawner.getPersistentDataContainer();
        String spawnerID = (String)pdc.get(new NamespacedKey(this, SPAWNER_ID_KEY), PersistentDataType.STRING);
        World world = spawner.getWorld();
        Double x = spawner.getLocation().getX();
        Double y = spawner.getLocation().getY();
        Double z = spawner.getLocation().getZ();
        ConfigurationSection configSection = this.customConfig.getConfigurationSection(SPAWNERS_CONFIG_SECTION);
        if (configSection == null) {
            this.customConfig.createSection(SPAWNERS_CONFIG_SECTION);
            this.saveCustomConfig();
        }
        ConfigurationSection spawnerSection = configSection.createSection(spawnerID);
        spawnerSection.set(WORLD_KEY, world.getName());
        spawnerSection.set(X_KEY, x);
        spawnerSection.set(Y_KEY, y);
        spawnerSection.set(Z_KEY, z);
        spawnerSection.set(STACK_SIZE_KEY, 1);
        this.saveCustomConfig();
        this.spawners.add(spawner);
    }

    public void removeSpawner(CreatureSpawner spawner) {
        this.spawners.remove(spawner);
        String spawnerID = (String)spawner.getPersistentDataContainer().get(new NamespacedKey(this, SPAWNER_ID_KEY), PersistentDataType.STRING);
        if (spawnerID != null) {
            this.customConfig.getConfigurationSection(SPAWNERS_CONFIG_SECTION).set(spawnerID, null);
            this.saveCustomConfig();
        }
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new SpawnerUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerSpawnListener(), this);
        this.spawnerGenerator = new SpawnerGenerator(this);
    }

    private void createCustomConfig() {
        this.customConfigFile = new File(this.getDataFolder(), "spawners.yml");
        if (!this.customConfigFile.exists()) {
            this.customConfigFile.getParentFile().mkdirs();
            this.saveResource("spawners.yml", false);
        }
        this.customConfig = YamlConfiguration.loadConfiguration(this.customConfigFile);
    }

    private void saveCustomConfig() {
        try {
            this.customConfig.save(this.customConfigFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setXP(CreatureSpawner spawner, int xp) {
        spawner.getPersistentDataContainer().set(new NamespacedKey(this, "xp"), PersistentDataType.INTEGER, xp);
        spawner.update();
        this.xpValues.put(spawner, xp);
    }

    public void updateXP(CreatureSpawner spawner, int xpToAdd, int multiplier) {
        xpToAdd *= multiplier;
        Integer currentExp = this.xpValues.get(spawner);
        if (currentExp == null) {
            this.xpValues.put(spawner, 0);
            currentExp = 0;
        }
        currentExp = currentExp + xpToAdd;
        spawner.getPersistentDataContainer().set(new NamespacedKey(this, EXP_KEY), PersistentDataType.INTEGER, currentExp);
        spawner.update();
        this.xpValues.put(spawner, currentExp);
    }

    public int getXP(CreatureSpawner spawner) {
        Integer exp = this.xpValues.get(spawner);
        if (exp == null) {
            exp = 1;
        }
        return exp;
    }

    public int getStackSize(CreatureSpawner spawner) {
        Integer stackSize = this.stackSizes.get(spawner);
        if (stackSize == null) {
            stackSize = 1;
        }
        return stackSize;
    }

    public void updateStackSize(CreatureSpawner spawner, int stackSizeToAdd) {
        Integer currentSize = this.stackSizes.get(spawner);
        if (currentSize == null) {
            this.stackSizes.put(spawner, 1);
            currentSize = 1;
        }
        currentSize = currentSize + stackSizeToAdd;
        ConfigurationSection configSection = this.customConfig.getConfigurationSection(SPAWNERS_CONFIG_SECTION);
        ConfigurationSection spawnerSection = configSection.getConfigurationSection((String)spawner.getPersistentDataContainer().get(new NamespacedKey(this, SPAWNER_ID_KEY), PersistentDataType.STRING));
        spawnerSection.set(STACK_SIZE_KEY, currentSize);
        this.saveCustomConfig();
        this.stackSizes.put(spawner, currentSize);
    }

    public void setStackSize(CreatureSpawner spawner, int stackSize) {
        String spawnerID = (String)spawner.getPersistentDataContainer().get(new NamespacedKey(this, SPAWNER_ID_KEY), PersistentDataType.STRING);
        ConfigurationSection configSection = this.customConfig.getConfigurationSection(SPAWNERS_CONFIG_SECTION);
        ConfigurationSection spawnerSection = configSection.getConfigurationSection((String)spawner.getPersistentDataContainer().get(new NamespacedKey(this, SPAWNER_ID_KEY), PersistentDataType.STRING));
        if (this.customConfig.contains("spawners." + spawnerID + ".stackSize")) {
            spawnerSection.set(STACK_SIZE_KEY, stackSize);
            this.saveCustomConfig();
            this.stackSizes.put(spawner, stackSize);
        }
    }

    public void clearDrops(ISpawners plugin, CreatureSpawner spawner) {
        String[] emptyDrops = new String[]{Material.AIR.toString()};
        spawner.getPersistentDataContainer().set(new NamespacedKey(plugin, DROPS_KEY), new ListDataType(Charset.defaultCharset()), emptyDrops);
        spawner.update();
        this.spawnerDrops.put(spawner, new String[]{Material.AIR.toString()});
    }

    public ArrayList<Material> getDrops(CreatureSpawner spawner) {
        String[] drops = this.spawnerDrops.get(spawner);
        ArrayList<Material> dropsMaterials = new ArrayList<Material>();
        if (drops == null) {
            return null;
        }
        for (String drop : drops) {
            Material material = Material.getMaterial(drop);
            if (material == null) continue;
            dropsMaterials.add(material);
        }
        dropsMaterials.removeAll(Collections.singleton(Material.AIR));
        return dropsMaterials;
    }

    public void updateDrops(ISpawners plugin, CreatureSpawner spawner, int multiplier) {
        ArrayList<Material> dropsMaterials = this.getDrops(spawner);
        ArrayList<String> dropsStrings = new ArrayList<String>();
        if (dropsMaterials == null) {
            dropsMaterials = new ArrayList();
        }
        List configList = plugin.getConfig().getConfigurationSection(SPAWNERS_CONFIG_SECTION).getConfigurationSection(spawner.getSpawnedType().toString()).getList("loot");
        for (Material material : dropsMaterials) {
            dropsStrings.add(material.toString());
        }
        int maxDrops = SpawnerUtils.getMaxDrops(this, spawner);
        if (dropsMaterials.size() >= SpawnerUtils.getMaxDrops(plugin, spawner)) {
            return;
        }
        if (configList != null) {
            block1: for (Object loot : configList) {
                Material material = Material.getMaterial(loot.toString());
                if (material == null) continue;
                for (int i = 0; i < multiplier; ++i) {
                    dropsStrings.add(material.toString());
                    if (dropsStrings.size() >= maxDrops) continue block1;
                }
            }
        }
        String[] stringArray = dropsStrings.toArray(new String[0]);
        this.spawnerDrops.put(spawner, stringArray);
        spawner.getPersistentDataContainer().set(new NamespacedKey(plugin, DROPS_KEY), new ListDataType(Charset.defaultCharset()), stringArray);
        spawner.update();
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = (Economy)rsp.getProvider();
        return econ != null;
    }

}
