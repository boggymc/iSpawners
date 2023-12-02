package com.boggy.ispawners.spawner;

import com.boggy.ispawners.ISpConfig;
import com.boggy.ispawners.ISpawners;
import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnersManager {

    private final ISpawners plugin;
    private final ISpConfig config;
    NamespacedKey spawnersStorageKey;
    NamespacedKey spawnerIdKey;
    NamespacedKey spawnerDropsKey;
    NamespacedKey spawnerXpKey;
    NamespacedKey spawnerStackSizeKey;

    private Set<CreatureSpawner> spawners;

    public SpawnersManager(){
        this.plugin = ISpawners.getInstance();
        this.config = this.plugin.getIspConfig();

        this.spawnersStorageKey = new NamespacedKey(this.plugin, "storage");
        this.spawnerIdKey = new NamespacedKey(this.plugin, "spawner_id");
        this.spawnerDropsKey = new NamespacedKey(this.plugin, "spawner_drops");
        this.spawnerXpKey = new NamespacedKey(this.plugin, "spawner_xp");
        this.spawnerStackSizeKey = new NamespacedKey(this.plugin, "spawner_stack_size");

        this.spawners = new HashSet<>();
        this.loadSpawners();
    }

    private @NotNull PersistentDataContainer getSpawnersStorage(World world){
//        gets the pdc from the world
        PersistentDataContainer worldPdc = world.getPersistentDataContainer();
//        if world doesn't have an ispawners storage pdc, make a new one
        if(!worldPdc.has(this.spawnersStorageKey, PersistentDataType.TAG_CONTAINER))
            worldPdc.set(this.spawnersStorageKey, PersistentDataType.TAG_CONTAINER, worldPdc.getAdapterContext().newPersistentDataContainer());

//        return the ispawners storage
        return Objects.requireNonNull(worldPdc.get(this.spawnersStorageKey, PersistentDataType.TAG_CONTAINER));
    }

    public @NotNull String getSpawnerId(CreatureSpawner spawner){
        PersistentDataContainer spawnerPdc = spawner.getPersistentDataContainer();

        if(!spawnerPdc.has(this.spawnerIdKey, PersistentDataType.STRING)){
            spawnerPdc.set(this.spawnerIdKey, PersistentDataType.STRING, UUID.randomUUID().toString());
            spawner.update();
        }

        return Objects.requireNonNull(spawnerPdc.get(this.spawnerIdKey, PersistentDataType.STRING));
    }

    private void loadSpawners(){
        plugin.getServer().getWorlds().forEach( (world) -> {
                PersistentDataContainer spawnersStorage = this.getSpawnersStorage(world);
                for(NamespacedKey spawnerIdKey : spawnersStorage.getKeys()) {
                    int[] spawnerCoords = spawnersStorage.get(spawnerIdKey, PersistentDataType.INTEGER_ARRAY);
                    if(spawnerCoords == null) continue;
                    if(!(world.getBlockAt(spawnerCoords[0], spawnerCoords[1], spawnerCoords[2]).getState() instanceof CreatureSpawner spawner)) continue;
                    this.spawners.add(spawner);
                }
            }
        );
    }

    public void createSpawner(CreatureSpawner spawner, int stackSize){

        String spawnerId = this.getSpawnerId(spawner);

//        Adds to storage
        NamespacedKey key = new NamespacedKey(this.plugin, spawnerId);
        PersistentDataContainer spawnersStorage = getSpawnersStorage(spawner.getWorld());
        int[] spawnerCoords = new int[]{spawner.getX(), spawner.getY(), spawner.getZ()};
        spawnersStorage.set(key, PersistentDataType.INTEGER_ARRAY, spawnerCoords);
//        saves the changes to storage
        spawner.getWorld().getPersistentDataContainer().set(spawnersStorageKey, PersistentDataType.TAG_CONTAINER, spawnersStorage);

        int[] test = spawnersStorage.get(key, PersistentDataType.INTEGER_ARRAY);

//        Populates PDC with id, xp, stacksize
        PersistentDataContainer spawnerPdc = spawner.getPersistentDataContainer();
        spawnerPdc.set(this.spawnerIdKey, PersistentDataType.STRING, spawnerId);
        spawnerPdc.set(this.spawnerXpKey, PersistentDataType.INTEGER, 0);
        spawnerPdc.set(this.spawnerStackSizeKey, PersistentDataType.INTEGER, stackSize);

//        Updates spawner's BlockState
        spawner.update();

//        Adds to memory
        spawners.add(spawner);
    }

    public void removeSpawner(CreatureSpawner spawner){

        String spawnerId = this.getSpawnerId(spawner);

        PersistentDataContainer spawnersStorage = getSpawnersStorage(spawner.getWorld());
        NamespacedKey key = new NamespacedKey(this.plugin, spawnerId);

//        Remove from storage
        spawnersStorage.remove(key);
//        Saves the changes to storage
        spawner.getWorld().getPersistentDataContainer().set(spawnersStorageKey, PersistentDataType.TAG_CONTAINER, spawnersStorage);

//        Remove from memory
        spawners.remove(spawner);
    }

    public boolean exists(CreatureSpawner spawner){
        return this.spawners.contains(spawner);
    }

    public @NotNull Integer getStackSize(CreatureSpawner spawner){
        PersistentDataContainer spawnerPdc = spawner.getPersistentDataContainer();
        Integer stackSize = spawnerPdc.get(this.spawnerStackSizeKey, PersistentDataType.INTEGER);
        return stackSize == null ? 1 : stackSize;

    }

    public void setStackSize(CreatureSpawner spawner, int newStackSize){
        PersistentDataContainer spawnerPdc = spawner.getPersistentDataContainer();
        spawnerPdc.set(this.spawnerStackSizeKey, PersistentDataType.INTEGER, newStackSize);
        spawner.update();
    }

    public @Nullable EntityType getSpawnerType(CreatureSpawner spawner){
        return spawner.getSpawnedType();
    }

    public @Nullable EntityType getSpawnerType(ItemStack heldItem){
        if(heldItem.getType() != Material.SPAWNER) return null;
        BlockStateMeta blockStateMeta = (BlockStateMeta) heldItem.getItemMeta();
        CreatureSpawner creatureSpawner = (CreatureSpawner) Objects.requireNonNull(blockStateMeta).getBlockState();
        return getSpawnerType(creatureSpawner);

    }

    public BlockStateMeta updateSpawnerType(ItemMeta spawnerMeta, EntityType spawnType){
        BlockStateMeta blockStateMeta = (BlockStateMeta) spawnerMeta;
        CreatureSpawner creatureSpawner = (CreatureSpawner) blockStateMeta.getBlockState();
        creatureSpawner.setSpawnedType(spawnType);
        blockStateMeta.setBlockState(creatureSpawner);
        return blockStateMeta;
    }

    public void debug(CreatureSpawner spawner, Player player){

        PersistentDataContainer pdc = spawner.getPersistentDataContainer();

        String id = pdc.get(this.spawnerIdKey, PersistentDataType.STRING);
        Integer stack = pdc.get(this.spawnerStackSizeKey, PersistentDataType.INTEGER);
        Integer xp = pdc.get(this.spawnerXpKey, PersistentDataType.INTEGER);
//        Integer xp = pdc.get(this.spawnerXpKey, PersistentDataType.INTEGER);
        player.sendMessage("Spawner: " + id);
        player.sendMessage(" - Stack Size: " + stack);
        player.sendMessage(" - XP stored: " + xp);
        player.sendMessage(" - Drops: " + xp);

    }

    public @NotNull ConcurrentHashMap<Material, Integer> getDrops(CreatureSpawner spawner){
        PersistentDataContainer pdc = spawner.getPersistentDataContainer();
        ConcurrentHashMap<Material, Integer> dropsData;

        if(!pdc.has(this.spawnerDropsKey, DataType.asConcurrentHashMap(DataType.asEnum(Material.class), PersistentDataType.INTEGER)))
            dropsData = new ConcurrentHashMap<>();
        else
            dropsData = pdc.get(this.spawnerDropsKey, DataType.asConcurrentHashMap(DataType.asEnum(Material.class), PersistentDataType.INTEGER));
        return dropsData;
    }

    public void clearDrops(CreatureSpawner spawner){
        PersistentDataContainer pdc = spawner.getPersistentDataContainer();
        pdc.set(this.spawnerDropsKey, DataType.asConcurrentHashMap(DataType.asEnum(Material.class), PersistentDataType.INTEGER), new ConcurrentHashMap<>());
    }

    public void updateDrops(CreatureSpawner spawner, double stackMultiplier){

//        Gets current drops
        ConcurrentHashMap<Material, Integer> dropsData = this.getDrops(spawner);
        int maxDrops = this.config.getMaxDrops();
        int totalDropsCount = this.getDropsCount(dropsData);
        if(totalDropsCount >= maxDrops) return;

//        Gets spawner ino
        EntityType spawnerType = this.getSpawnerType(spawner);
        int stackSize = this.getStackSize(spawner);

//        Simulates a death and retrieves the drops
        ConcurrentHashMap<Material, Integer> simulatedDeathDrops = this.simulateDeathDrops(spawnerType, stackSize, 1.5 );
        simulatedDeathDrops.forEach( (material, addedAmount) -> {
            if((totalDropsCount + addedAmount) > maxDrops)
                addedAmount = maxDrops-totalDropsCount;
            Integer currentAmount = dropsData.get(material);
            if(currentAmount == null) currentAmount = 0;

            dropsData.put(material, currentAmount + addedAmount);
        });

//        Saves updated drops to spawner's pdc
        PersistentDataContainer pdc = spawner.getPersistentDataContainer();
        pdc.set(this.spawnerDropsKey, DataType.asConcurrentHashMap(DataType.asEnum(Material.class), PersistentDataType.INTEGER), dropsData);
        spawner.update();

    }

    public @NotNull ConcurrentHashMap<Material, Integer> simulateDeathDrops(EntityType spawnerType, int stackSize, double stackMultiplier){
        ConcurrentHashMap<Material, Integer> dropsData = new ConcurrentHashMap<>();

        dropsData.put(Material.ROTTEN_FLESH, 30);
        dropsData.put(Material.BONE, 10);
        dropsData.put(Material.STRING, 100);
        return dropsData;
    }

    public int getDropsCount(CreatureSpawner spawner){
        return this.getDropsCount(this.getDrops(spawner));
    }

    public int getDropsCount(ConcurrentHashMap<Material, Integer> drops){

        int currentDrops = 0;

        Collection<Integer> amounts = drops.values();
        for(int itemAmount : amounts){
            currentDrops += itemAmount;
        }

        return currentDrops;

    }

    public int getXp(CreatureSpawner spawner){
        PersistentDataContainer pdc = spawner.getPersistentDataContainer();
        Integer xpValue = pdc.get(this.spawnerXpKey, PersistentDataType.INTEGER);
        return xpValue != null ? xpValue : 0;
    }

    public void setXp(CreatureSpawner spawner, int xpValue){
        PersistentDataContainer pdc = spawner.getPersistentDataContainer();
        pdc.set(this.spawnerXpKey, PersistentDataType.INTEGER, xpValue);
        spawner.update();
    }

}
