package org.by1337.airdrop.airdrop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.by1337.airdrop.airdrop.Gui.ClickEvent;
import org.by1337.airdrop.airdrop.Listener.Handler;
import org.by1337.airdrop.airdrop.command.Cmd;
import org.by1337.airdrop.airdrop.command.cmdCompleter;
import org.by1337.airdrop.airdrop.util.*;

import java.io.File;
import java.util.*;

import static org.by1337.airdrop.airdrop.util.CfgManager.Config.*;

public final class AirDrop extends JavaPlugin implements Runnable {

    public static AirDrop instance;
    public static HashMap<Short, List<ItemStack>> baseItem = new HashMap<>();
    public static int startDelay;
    public static int chestLockedDelay;
    public static int stopEventDelay;
    private final HologramManager hologram = new HologramManager();
    public static List<org.by1337.airdrop.airdrop.Chest> ChestList = new ArrayList<>();

    public static void setInstance(AirDrop instance) {
        AirDrop.instance = instance;
    }

    @Override
    public void onEnable() {
        setInstance(this);

        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            this.getLogger().info("Creating new config file, please wait");
            this.getConfig().options().copyDefaults(true);
            this.saveDefaultConfig();
        }

        LoadConfig();
        if (instance.getConfig().getConfigurationSection("data") != null)
            Load();
        Update();
        startDelay = getTimeStartInterval() * 60;
        chestLockedDelay = getTimeLockedChest() * 60;
        stopEventDelay = getTimeStopEvent() * 60;
        Objects.requireNonNull(this.getCommand("airdrop")).setExecutor(new Cmd(this));
        Objects.requireNonNull(this.getCommand("airdrop")).setTabCompleter((new cmdCompleter()));
        getServer().getPluginManager().registerEvents(new Handler(this), this);
        getServer().getPluginManager().registerEvents(new ClickEvent(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this, 20L, 20L);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PlaceholderExpansion(this).register();
        else
            Message.Error("PlaceholderAPI is off!");

    }

    private void Update() {
        double currentVersion = 1.1;
        if (getConfigVersion() == null || getConfigVersion() != currentVersion) {
            Message.Logger("PX &aConfig update starts");
            File config = new File(instance.getDataFolder() + File.separator + "config.yml");
            File configOld = new File(instance.getDataFolder() + File.separator + "config.yml.old");
            if (configOld.exists()) {
                boolean isRemove = new File(instance.getDataFolder() + File.separator + "config.yml.old").delete();
                if (!isRemove) {
                    Message.Error("The old config dump was not deleted! Delete it manually!");
                    Message.Error("Config update failed!");
                    return;
                }
            }
            if (!config.exists()) {
                Message.Error("Config update failed!");
                return;
            }
            boolean isRename = config.renameTo(configOld);
            if (isRename) {
                new File(instance.getDataFolder() + File.separator + "config.yml");
                instance.getLogger().info("Creating new config file, please wait");
                instance.getConfig().options().copyDefaults(true);
                instance.saveDefaultConfig();
                instance.saveConfig();
                instance.reloadConfig();
                SetConfig();
                instance.getConfig().set("config-version", currentVersion);
                Save();
                instance.saveConfig();
                instance.reloadConfig();
                LoadConfig();
            } else {
                Message.Error("An error occurred while updating the config!");
                Message.Error("Config update failed!");
                return;
            }
            Message.Logger("PX &aConfig Successfully updated!");
        }
    }

    public static void Save() {
        if (baseItem == null)
            return;
        instance.getConfig().set("data", null);

        for (short chance : baseItem.keySet()) {
            instance.getConfig().set("data." + chance, baseItem.get(chance));
        }
        instance.saveConfig();
        GetRandomItem.chance = AirDrop.baseItem.keySet().toArray(new Short[0]);
        Message.Logger("PX baseItem save &asuccessful!");
    }

    @SuppressWarnings("unchecked")
    public static void Load() {
        ChestList.clear();
        for (String key : Objects.requireNonNull(instance.getConfig().getConfigurationSection("chests")).getKeys(false)) {
            String chestId = instance.getConfig().getString("chests." + key + ".chest-id");
            String chestName = instance.getConfig().getString("chests." + key + ".chest-name");
            int inventorySiz = instance.getConfig().getInt("chests." + key + ".chest-inventory-size");
            int chanceBoost = instance.getConfig().getInt("chests." + key + ".item-chance-boost");
            int radiusProtect = instance.getConfig().getInt("chests." + key + ".chest-radius-protect");
            int timeStartInterval = instance.getConfig().getInt("chests." + key + ".time-start-interval");
            int durationEvent = instance.getConfig().getInt("chests." + key + ".duration-event");
            int timeStopEvent = instance.getConfig().getInt("chests." + key + ".time-stop-event");
            World world = Bukkit.getWorld((String) Objects.requireNonNull(instance.getConfig().get("chests." + key + ".chest-spawn-world")));
            int spawnRadiusMin = instance.getConfig().getInt("chests." + key + ".chest-spawn-radius-min");
            int spawnRadiusMax = instance.getConfig().getInt("chests." + key + ".chest-spawn-radius-max");
            Material materialLocked = Material.valueOf(instance.getConfig().getString("chests." + key + ".chest-material-locked"));
            Material materialUnlocked = Material.valueOf(instance.getConfig().getString("chests." + key + ".chest-material-unlocked"));
            int[] spawn = new int[2];
            spawn[0] = spawnRadiusMin;
            spawn[1] = spawnRadiusMax;
            ChestList.add( new org.by1337.airdrop.airdrop.Chest(chestName, chestId, chanceBoost, world, spawn, radiusProtect, timeStartInterval, durationEvent, timeStopEvent, materialLocked, materialUnlocked, inventorySiz));
        }
        baseItem.clear();
        for (String chance : instance.getConfig().getConfigurationSection("data").getKeys(false)) {
            baseItem.put(Short.valueOf(chance), (List<ItemStack>) instance.getConfig().getList("data." + chance));
        }
    }

    public static void UnLoad() {
        if(!ChestList.isEmpty()){
            for(Chest chest : ChestList){
                chest.Destroyer();
            }
        }
    }
    @Override
    public void onDisable() {
        Save();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PlaceholderExpansion(this).unregister();
        if(!ChestList.isEmpty()){
            for(Chest chest : ChestList){
                AirRegion.RemoveRegion(chest.getRegionName(), chest.getWorld());
            }
        }


    }

    static void Effects(Location loc) {
        for (String str : getDropOpenEffect()) {
            str = str.toLowerCase();
            switch (str) {
                case ("strikelightning"):
                    Effect.StrikeLightning(loc);
                    break;
                case ("firework"):
                    Effect.createFireWork(loc);
                    break;
                case ("explosion"):
                    Effect.Explosion(loc, getExplosionPower());
                    break;
                case ("fakestrikelightning"):
                    Effect.FakeStrikeLightning(loc);
                    break;
                case ("defenders"):
                    Effect.SpawnGuards(loc);
                    break;
                default:
                    Message.Warning("Unknown effect! drop-open-effect line - " + str + " Change it to config");
                    break;
            }
        }
    }

    @Override
    public void run() {
        if(ChestList.isEmpty())
            return;
        for(Chest chest : ChestList){
            chest.Timer();
        }
    }

    public static String Format(long Sec) {
        int hour = (int) Sec / 3600;
        int min = (int) Sec % 3600 / 60;
        int sec = (int) Sec % 60;
        String fin = "";
        if (hour != 0) {
            int x = hour % 10;
            if (x == 1)
                fin += "" + hour + getFormatTime().get(0);
            if (x == 2 || x == 3 || x == 4)
                fin += "" + hour + getFormatTime().get(1);
            if (x >= 5 || x == 0)
                fin += "" + hour + getFormatTime().get(2);
        }
        if (min != 0) {
            int x = min % 10;
            if (x == 1)
                fin += "" + min + getFormatTime().get(3);
            if (x == 2 || x == 3 || x == 4)
                fin += "" + min + getFormatTime().get(4);
            if (x >= 5 || x == 0)
                fin += "" + min + getFormatTime().get(5);
        }
        if (sec != 0) {
            int x = sec % 10;
            if (x == 1)
                fin += "" + sec + getFormatTime().get(6);
            if (x == 2 || x == 3 || x == 4)
                fin += "" + sec + getFormatTime().get(7);
            if (x >= 5 || x == 0)
                fin += "" + sec + getFormatTime().get(8);
        }
        return fin;
    }

    public static String Format2(long Sec) {
        int hour = (int) Sec / 3600;
        int min = (int) Sec % 3600 / 60;
        int sec = (int) Sec % 60;
        String fin = "";
        if (hour != 0) {
            int x = hour % 10;
            if (x == 1)
                fin += "" + hour + getFormatTime().get(0);
            if (x == 2 || x == 3 || x == 4)
                fin += "" + hour + getFormatTime().get(1);
            if (x >= 5 || x == 0)
                fin += "" + hour + getFormatTime().get(2);
        }
        if (min != 0) {
            int x = min % 10;
            if (x == 1)
                fin += "" + min + getFormatTime().get(10);
            if (x == 2 || x == 3 || x == 4)
                fin += "" + min + getFormatTime().get(4);
            if (x >= 5 || x == 0)
                fin += "" + min + getFormatTime().get(5);
        }
        if (sec != 0) {
            int x = sec % 10;
            if (x == 1)
                fin += "" + sec + getFormatTime().get(11);
            if (x == 2 || x == 3 || x == 4)
                fin += "" + sec + getFormatTime().get(7);
            if (x >= 5 || x == 0)
                fin += "" + sec + getFormatTime().get(8);
        }
        return fin;
    }

    public static String Format3(long Sec) {
        int hour = (int) Sec / 3600;
        int min = (int) Sec % 3600 / 60;
        int sec = (int) Sec % 60;
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }
}
