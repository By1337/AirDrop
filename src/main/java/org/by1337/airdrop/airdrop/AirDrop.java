package org.by1337.airdrop.airdrop;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.inventory.InventoryType;
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
import static org.by1337.airdrop.airdrop.util.Config.*;
import static org.by1337.airdrop.airdrop.util.GetRandomItem.GetItem;

public final class AirDrop extends JavaPlugin implements Runnable {

    public static AirDrop instance;
    // public static List<ItemStack> listItem;
    public static HashMap<Short, List<ItemStack>> baseItem = new HashMap<>();
    public static int startIntervalChange;
    public static int TimeLockedChest;
    public static int timeStopEventChange;
    private final HologramManager holo = new HologramManager();

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
        startIntervalChange = getTimeStartInterval() * 60;
        TimeLockedChest = getTimeLockedChest() * 60;
        timeStopEventChange = getTimeStopEvent() * 60;

        Objects.requireNonNull(this.getCommand("airdrop")).setExecutor(new Cmd(this));
        Objects.requireNonNull(this.getCommand("airdrop")).setTabCompleter((new cmdCompleter()));
        getServer().getPluginManager().registerEvents(new Handler(this), this);
        getServer().getPluginManager().registerEvents(new ClickEvent(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this, 20L, 20L);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PlaceholderExpansion(this).register();
        else
            Message.Error("PlaceholderAPI is off!");

        // AirSpawn.RemoveRegion();
    }

    public static void Save() {
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
        TimeLockedChest = getTimeLockedChest() * 60;
        timeStopEventChange = getTimeStopEvent() * 60;
        startIntervalChange = 0;
    }

    public static void Stop() {
        TimeLockedChest = 0;
        timeStopEventChange = 0;
        startIntervalChange = getTimeStartInterval() * 60;
    }

    public static void Unlock() {
        TimeLockedChest = 0;
        startIntervalChange = 0;
    }


    @Override
    public void onDisable() {
        Save();
        if (getAirLocation() != null) {
            getAirLocation().getBlock().setType(Material.AIR);
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PlaceholderExpansion(this).unregister();
        //AirSpawn.RemoveRegion();
        // Plugin shutdown logic
    }

    private void Effects() {
        if (!AirSpawn.eventActivity)
            return;
        for (String str : getDropOpenEffect()) {
            str = str.toLowerCase();
            if (str.equals("strikelightning".toLowerCase()))
                Effect.StrikeLightning(getAirLocation());
            else if (str.equals("firework".toLowerCase()))
                Effect.createFireWork(getAirLocation());
            else if (str.equals("explosion".toLowerCase()))
                Effect.Explosion(getAirLocation(), getExplosionPower());
            else if (str.equals("fakestrikelightning".toLowerCase()))
                Effect.FakeStrikeLightning(getAirLocation());
            else
                Message.Warning("Неизвестный эффект! drop-open-effect строчка - " + str + " Измените его в config.yml");
        }
    }

    @Override
    public void run() {
        if (startIntervalChange <= 0 && !AirSpawn.eventActivity) {
            AirSpawn.Start();
            LasersManager.createLaser(getAirLocation());
            Message.SendAllMsg(getDropSpawning().replace("{x}", "" + getAirLocation().getX()).replace("{y}", "" + getAirLocation().getY()).replace("{z}", "" + getAirLocation().getZ()));
        } else if (!AirSpawn.eventActivity) {
            if (getNotificationTime().contains(String.valueOf(startIntervalChange)))
                Message.SendAllMsg(getMsgStartEvent().replace("{time}", Format(startIntervalChange)).replace("{time2}", Format2(startIntervalChange)));
            startIntervalChange--;
        }
        if (AirSpawn.eventActivity) {
            if (TimeLockedChest <= 0 && AirSpawn.chestLocked) {
                AirSpawn.chestLocked = false;
                Effects();
            } else if (AirSpawn.chestLocked) {
                if (getNotificationOpenTime().contains(String.valueOf(TimeLockedChest)))
                    Message.SendAllMsg(getMsgOpenEvent().replace("{time2}", Format2(TimeLockedChest)).replace("{time}", Format(TimeLockedChest)));
                TimeLockedChest--;
            }
        }

        if (AirSpawn.eventActivity && timeStopEventChange <= 0) {
            if (getAirLocation().getBlock().getState() instanceof Chest) {
                Chest chest = (Chest) getAirLocation().getBlock().getState();;
                chest.getInventory().clear();
            }
            getAirLocation().getBlock().setType(Material.AIR);
            LasersManager.removeLaser(getAirLocation());
            holo.HoloDel();
            AirSpawn.RemoveRegion();
            AirSpawn.End();
            Message.SendAllMsg(getEventEnd());
            startIntervalChange = getTimeStartInterval() * 60;
            TimeLockedChest = getTimeLockedChest() * 60;
            timeStopEventChange = getTimeStopEvent() * 60;
        } else if (AirSpawn.eventActivity && TimeLockedChest <= 0) {
            timeStopEventChange--;
        }
        if (AirSpawn.eventActivity && getAirLocation() != null) {
            holo.HoloCreate(new Location(getAirLocation().getWorld(), getAirLocation().getX() + 0.5, getAirLocation().getY() + 1.7, getAirLocation().getZ() + 0.5));
            holo.HoloUpdate(Format(TimeLockedChest), Format2(TimeLockedChest));
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
            if (hour < 5) {
                if (hour == 1)
                    fin += getFormatTime().get(0);
                else
                    fin += hour + " " + getFormatTime().get(1);
            } else
                fin += hour + getFormatTime().get(2);
        }

        if (min != 0) {
            if (min < 5) {
                if (min == 1)
                    fin += hour == 0 ? getFormatTime().get(3) : " " + getFormatTime().get(3);
                else
                    fin += hour == 0 ? min + " " + getFormatTime().get(4) : " " + min + " " + getFormatTime().get(4);
            } else
                fin += min + getFormatTime().get(5);
        }

        if (sec != 0) {
            if (sec < 5) {
                if (sec == 1)
                    fin += min == 0 ? getFormatTime().get(6) : " " + getFormatTime().get(6);
                else
                    fin += min == 0 ? sec + " " + getFormatTime().get(7) : " " + sec + " " + getFormatTime().get(7);
            } else
                fin += min == 0 ? sec + getFormatTime().get(8) : " " + sec + getFormatTime().get(8);
        }
        return fin;

    }

    public String Format2(long Sec) {
        int hour = (int) Sec / 3600;
        int min = (int) Sec % 3600 / 60;
        int sec = (int) Sec % 60;
        String fin = "";
        if (hour != 0) {
            if (hour < 5) {
                if (hour == 1)
                    fin += getFormatTime().get(0);
                else
                    fin += hour + " " + getFormatTime().get(1);
            } else
                fin += hour + " " + getFormatTime().get(9);
        }

        if (min != 0) {
            if (min < 5) {
                if (min == 1)
                    fin += hour == 0 ? getFormatTime().get(10) : " " + getFormatTime().get(10);
                else
                    fin += min + " " + getFormatTime().get(4);
            } else
                fin += min + " " + getFormatTime().get(11);
        }

        if (sec != 0) {
            if (sec < 5) {
                if (sec == 1)
                    fin += min == 0 ? getFormatTime().get(12) : " " + getFormatTime().get(12);
                else
                    fin += sec + " " + getFormatTime().get(7);
            } else
                fin += sec + " " + getFormatTime().get(13);
        }
        return fin;

    }

}
