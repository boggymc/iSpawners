package com.boggy.ispawners;

import com.boggy.ispawners.command.ISpawnersCommand;
import com.boggy.ispawners.command.TabCompletion;
import com.boggy.ispawners.gui.GUIHolder;
import com.boggy.ispawners.pdc.ListDataType;
import com.boggy.ispawners.pdc.SpawnerUtils;
import com.boggy.ispawners.spawner.SpawnerGenerator;
import com.boggy.ispawners.spawner.listener.SpawnerBreakListener;
import com.boggy.ispawners.spawner.listener.SpawnerInteractListener;
import com.boggy.ispawners.spawner.listener.SpawnerPlaceListener;
import com.boggy.ispawners.spawner.listener.SpawnerSpawnListener;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Getter
public final class ISpawners extends JavaPlugin {

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
    @Getter
    private FileConfiguration customConfig;

    private SpawnerGenerator spawnerGenerator;

    @Getter
    private static Economy econ = null;

    private HashMap<Player, CreatureSpawner> spawnerUITracker = new HashMap<>();

    @Getter
    private List<CreatureSpawner> spawners = new ArrayList<>();

    private HashMap<CreatureSpawner, Integer> xpValues = new HashMap<>();
    private HashMap<CreatureSpawner, Integer> stackSizes = new HashMap<>();
    private HashMap<CreatureSpawner, String[]> spawnerDrops = new HashMap<>();

    @Override
    public void onEnable() {
        GUIHolder.init(this);
        saveDefaultConfig();
        createCustomConfig();
        registerEvents();
        loadSpawners();
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        spawnerGenerator.startGeneration();
        getCommand("ispawners").setExecutor(new ISpawnersCommand(this));
        getCommand("ispawners").setTabCompleter(new TabCompletion());
    }

    private void loadSpawners() {
        if (customConfig.getConfigurationSection(SPAWNERS_CONFIG_SECTION) == null) {
            customConfig.createSection(SPAWNERS_CONFIG_SECTION);
            saveCustomConfig();
        }
        ConfigurationSection spawnerSection = customConfig.getConfigurationSection(SPAWNERS_CONFIG_SECTION);
        if (spawnerSection.getKeys(false).isEmpty()) {
            return;
        }
        for (String key : spawnerSection.getKeys(false)) {
            World world = Bukkit.getWorld(spawnerSection.getConfigurationSection(key).getString(WORLD_KEY));
            double x = spawnerSection.getConfigurationSection(key).getDouble(X_KEY);
            double y = spawnerSection.getConfigurationSection(key).getDouble(Y_KEY);
            double z = spawnerSection.getConfigurationSection(key).getDouble(Z_KEY);

            Location loc = new Location(world, x, y, z);

            BlockState state = world.getBlockAt(loc).getState();
            if (state instanceof CreatureSpawner spawner) {
                spawners.add(spawner);
                int stackSize = spawnerSection.getConfigurationSection(key).getInt(STACK_SIZE_KEY);
                Integer exp = spawner.getPersistentDataContainer().get(new NamespacedKey(this, EXP_KEY), PersistentDataType.INTEGER);
                if (exp == null) {
                    exp = 0;
                }
                String[] drops = spawner.getPersistentDataContainer().get(new NamespacedKey(this, DROPS_KEY), new ListDataType(Charset.defaultCharset()));
                if (drops == null) {
                    drops = new String[]{Material.AIR.toString()};
                }
                stackSizes.put(spawner, stackSize);
                xpValues.put(spawner, exp);
                spawnerDrops.put(spawner, drops);
            }
        }
    }

    public void createSpawner(CreatureSpawner spawner) {
        PersistentDataContainer pdc = spawner.getPersistentDataContainer();
        String spawnerID = pdc.get(new NamespacedKey(this, SPAWNER_ID_KEY), PersistentDataType.STRING);
        World world = spawner.getWorld();
        Double x = spawner.getLocation().getX();
        Double y = spawner.getLocation().getY();
        Double z = spawner.getLocation().getZ();
        ConfigurationSection configSection = customConfig.getConfigurationSection(SPAWNERS_CONFIG_SECTION);
        if (configSection == null) {
            customConfig.createSection(SPAWNERS_CONFIG_SECTION);
            saveCustomConfig();
        }
        ConfigurationSection spawnerSection = configSection.createSection(spawnerID);
        spawnerSection.set(WORLD_KEY, world.getName());
        spawnerSection.set(X_KEY, x);
        spawnerSection.set(Y_KEY, y);
        spawnerSection.set(Z_KEY, z);
        spawnerSection.set(STACK_SIZE_KEY, 1);

        saveCustomConfig();
        spawners.add(spawner);
    }

    public void removeSpawner(CreatureSpawner spawner) {
        spawners.remove(spawner);
        stackSizes.remove(spawner);
        String spawnerID = spawner.getPersistentDataContainer().get(new NamespacedKey(this, "spawnerID"), PersistentDataType.STRING);
        if (spawnerID != null) {
            customConfig.getConfigurationSection("spawners").set(spawnerID, null);
            saveCustomConfig();
        }
    }
    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new SpawnerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerSpawnListener(), this);
        spawnerGenerator = new SpawnerGenerator(this);
    }

    private void createCustomConfig() {
        customConfigFile = new File(getDataFolder(), "spawners.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();

            saveResource("spawners.yml", false);
        }

        this.customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    }

    private void saveCustomConfig() {
        try {
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setXP(CreatureSpawner spawner, int xp) {
        spawner.getPersistentDataContainer().set(new NamespacedKey(this,  "xp"), PersistentDataType.INTEGER, xp);
        spawner.update();

        xpValues.put(spawner, xp);
    }

    public void updateXP(CreatureSpawner spawner, int xpToAdd, int multiplier) {
        xpToAdd *= multiplier;
        Integer currentExp = xpValues.get(spawner);
        if (currentExp == null) {
            xpValues.put(spawner, 0);
            currentExp = 0;
        }
        currentExp += xpToAdd;
        spawner.getPersistentDataContainer().set(new NamespacedKey(this, "exp"), PersistentDataType.INTEGER, currentExp);
        spawner.update();
        xpValues.put(spawner, currentExp);
    }

    public int getXP(CreatureSpawner spawner) {
        Integer exp = xpValues.get(spawner);
        if (exp == null) {
            exp = 1;
        }
        return exp;
    }

    public int getStackSize(CreatureSpawner spawner) {
        Integer stackSize = stackSizes.get(spawner);
        if (stackSize == null) {
            stackSize = 1;
        }
        return stackSize;
    }
    public void updateStackSize(CreatureSpawner spawner, int stackSizeToAdd) {
        Integer currentSize = stackSizes.get(spawner);
        if (currentSize == null) {
            stackSizes.put(spawner, 1);
            currentSize = 1;
        }
        currentSize += stackSizeToAdd;
        ConfigurationSection configSection = customConfig.getConfigurationSection("spawners");
        ConfigurationSection spawnerSection = configSection.getConfigurationSection(spawner.getPersistentDataContainer()
                        .get(new NamespacedKey(this, "spawnerID"), PersistentDataType.STRING));

        spawnerSection.set("stackSize", currentSize);
        saveCustomConfig();
        stackSizes.put(spawner, currentSize);
    }

    public void setStackSize(CreatureSpawner spawner, int stackSize) {
        String spawnerID = spawner.getPersistentDataContainer()
                .get(new NamespacedKey(this, "spawnerID"), PersistentDataType.STRING);

        ConfigurationSection configSection = customConfig.getConfigurationSection("spawners");
        ConfigurationSection spawnerSection = configSection.getConfigurationSection(spawner.getPersistentDataContainer()
                .get(new NamespacedKey(this, "spawnerID"), PersistentDataType.STRING));

        if (customConfig.contains("spawners." + spawnerID + ".stackSize")) {
            spawnerSection.set("stackSize", stackSize);
            saveCustomConfig();
            stackSizes.put(spawner, stackSize);
        }
    }

    public void clearDrops(ISpawners plugin, CreatureSpawner spawner) {
        String[] emptyDrops = new String[] {Material.AIR.toString()};
        spawner.getPersistentDataContainer().set(new NamespacedKey(plugin, "drops"), new ListDataType(Charset.defaultCharset()), emptyDrops);
        spawner.update();
        spawnerDrops.put(spawner, new String[]{ Material.AIR.toString() });
    }

    public ArrayList<Material> getDrops(CreatureSpawner spawner) {
        String[] drops = spawnerDrops.get(spawner);
        ArrayList<Material> dropsMaterials = new ArrayList<>();

        if (drops == null) { return null; }

        for (String drop : drops) {
            Material material = Material.getMaterial(drop);
            if (material != null) {
                dropsMaterials.add(material);
            }
        }
        dropsMaterials.removeAll(Collections.singleton(Material.AIR));
        return dropsMaterials;
    }

    public void updateDrops(ISpawners plugin, CreatureSpawner spawner, int multiplier) {
        if (spawner.getSpawnedType().equals(EntityType.DROPPED_ITEM)) {
            return;
        }
        ArrayList<Material> dropsMaterials = getDrops(spawner);
        ArrayList<String> dropsStrings = new ArrayList<>();

        if (dropsMaterials == null) {
            dropsMaterials = new ArrayList<>();
        }

        List<?> configList = plugin.getConfig().getConfigurationSection("spawners")
                .getConfigurationSection(spawner.getSpawnedType().toString()).getList("loot");

        for (Material material : dropsMaterials) {
            dropsStrings.add(material.toString());
        }

        int maxDrops = SpawnerUtils.getMaxDrops(this, spawner);

        if (dropsMaterials.size() >= SpawnerUtils.getMaxDrops(plugin, spawner)) {
            return;
        }

        if (configList != null) {
            for (Object loot : configList) {
                Material material = Material.getMaterial(loot.toString());
                if (material != null) {
                    for (int i = 0; i < multiplier; i++) {
                        dropsStrings.add(material.toString());
                        if (dropsStrings.size() >= maxDrops) {
                            break;
                        }
                    }
                }
            }
        }

        String[] dropString = dropsStrings.toArray(new String[0]);
        spawnerDrops.put(spawner, dropString);
        spawner.getPersistentDataContainer().set(new NamespacedKey(plugin, "drops"), new ListDataType(Charset.defaultCharset()), dropString);
        spawner.update();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}