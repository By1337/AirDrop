package org.by1337.airdrop.airdrop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.by1337.airdrop.airdrop.Gui.ClickEvent;
import org.by1337.airdrop.airdrop.Listener.Handler;
import org.by1337.airdrop.airdrop.command.Cmd;
import org.by1337.airdrop.airdrop.command.cmdCompleter;
import org.by1337.airdrop.airdrop.util.*;
import java.io.File;
import java.util.*;
import static org.by1337.airdrop.airdrop.AirSpawn.getAirLocation;
import static org.by1337.airdrop.airdrop.util.CfgManager.Config.*;

public final class AirDrop extends JavaPlugin implements Runnable {

    public static AirDrop instance;
    public static HashMap<Short, List<ItemStack>> baseItem = new HashMap<>();
    public static HashMap<Short, List<ItemStack>> tempBaseItem = new HashMap<>();
    public static int startDelay;
    public static int chestLockedDelay;
    public static int stopEventDelay;
    private final HologramManager hologram = new HologramManager();
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
            if(configOld.exists()){
               boolean isRemove = new File(instance.getDataFolder() + File.separator + "config.yml.old").delete();
               if(!isRemove){
                   Message.Error("The old config dump was not deleted! Delete it manually!");
                   Message.Error("Config update failed!");
                   return;
               }
            }
            if (!config.exists()){
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
            }else{
                Message.Error("An error occurred while updating the config!");
                Message.Error("Config update failed!");
                return;
            }
            Message.Logger("PX &aConfig Successfully updated!");
        }
    }

    public static void Save() {
        if(baseItem == null)
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
        baseItem.clear();
        for (String chance : instance.getConfig().getConfigurationSection("data").getKeys(false)) {
            baseItem.put(Short.valueOf(chance), (List<ItemStack>) instance.getConfig().getList("data." + chance));
        }
    }

    public static void Start() {
        chestLockedDelay = getTimeLockedChest() * 60;
        stopEventDelay = getTimeStopEvent() * 60;
        startDelay = 0;
    }

    public static void Stop() {
        chestLockedDelay = 0;
        stopEventDelay = 0;
        startDelay = getTimeStartInterval() * 60;
    }

    public static void Unlock() {
        chestLockedDelay = 0;
        startDelay = 0;
    }

    @Override
    public void onDisable() {
        Save();
        if (getAirLocation() != null)
            getAirLocation().getBlock().setType(Material.AIR);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PlaceholderExpansion(this).unregister();
    }

    private void Effects() {
        if (!AirSpawn.eventActivity)
            return;
        for (String str : getDropOpenEffect()) {
            str = str.toLowerCase();
            switch (str){
                case ("strikelightning"):
                    Effect.StrikeLightning(getAirLocation());break;
                case ("firework"):
                    Effect.createFireWork(getAirLocation());break;
                case ("explosion"):
                    Effect.Explosion(getAirLocation(), getExplosionPower());break;
                case ("fakestrikelightning"):
                    Effect.FakeStrikeLightning(getAirLocation());break;
                case ("defenders"):
                    Effect.SpawnGuards(getAirLocation());break;
                default:
                    Message.Warning("Unknown effect! drop-open-effect line - " + str + " Change it to config");break;
            }
        }
    }

    @Override
    public void run() {
        if (startDelay <= 0 && !AirSpawn.eventActivity && Bukkit.getOnlinePlayers().size() >= getMinOnlinePlayers()) {
            AirSpawn.Start();
            LasersManager.createLaser(getAirLocation());
            Message.SendAllMsg(getDropSpawning().replace("{x}", "" + getAirLocation().getX()).replace("{y}", "" + getAirLocation().getY()).replace("{z}", "" + getAirLocation().getZ()));
        } else if (!AirSpawn.eventActivity) {
            if (getNotificationTime().contains(String.valueOf(startDelay)))
                Message.SendAllMsg(getMsgStartEvent().replace("{time}", Format(startDelay)).replace("{time2}", Format2(startDelay)));
            startDelay--;
        }
        if (AirSpawn.eventActivity) {
            if (chestLockedDelay <= 0 && AirSpawn.chestLocked) {
                AirSpawn.chestLocked = false;
                Effects();
            } else if (AirSpawn.chestLocked) {
                if (getNotificationOpenTime().contains(String.valueOf(chestLockedDelay)))
                    Message.SendAllMsg(getMsgOpenEvent().replace("{time2}", Format2(chestLockedDelay)).replace("{time}", Format(chestLockedDelay)));
                chestLockedDelay--;
            }
        }

        if (AirSpawn.eventActivity && stopEventDelay <= 0) {
            if (getAirLocation().getBlock().getState() instanceof Chest) {
                Chest chest = (Chest) getAirLocation().getBlock().getState();
                chest.getInventory().clear();
            }
            getAirLocation().getBlock().setType(Material.AIR);
            LasersManager.removeLaser(getAirLocation());
            hologram.HoloDel();
            AirSpawn.RemoveRegion();
            AirSpawn.End();
            Message.SendAllMsg(getEventEnd());
            startDelay = getTimeStartInterval() * 60;
            chestLockedDelay = getTimeLockedChest() * 60;
            stopEventDelay = getTimeStopEvent() * 60;
        } else if (AirSpawn.eventActivity && chestLockedDelay <= 0) {
            stopEventDelay--;
        }
        if (AirSpawn.eventActivity && getAirLocation() != null) {
            hologram.HoloCreate(new Location(getAirLocation().getWorld(), getAirLocation().getX() + 0.5, getAirLocation().getY() + 1.7, getAirLocation().getZ() + 0.5));
            hologram.HoloUpdate(Format(chestLockedDelay), Format2(chestLockedDelay));
        }
        AirSpawn.RndLoc();

        if (AirSpawn.eventActivity && getAirLocation().getBlock().getState() instanceof Chest) {
            Chest chest = (Chest) getAirLocation().getBlock().getState();
            Inventory inv = chest.getBlockInventory();
            boolean empty = true;
            for (ItemStack item : inv.getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    empty = false;
                    break;
                }
            }
            if (empty) {
                Stop();
            }

        }
    }

    public String Format(long Sec) {
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

    public String Format2(long Sec) {
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
}
