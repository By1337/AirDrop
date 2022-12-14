package org.by1337.airdrop.airdrop;

import org.bukkit.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.by1337.airdrop.airdrop.util.Effect;
import org.by1337.airdrop.airdrop.util.HologramManager;
import org.by1337.airdrop.airdrop.util.LasersManager;
import org.by1337.airdrop.airdrop.util.Message;

import static org.by1337.airdrop.airdrop.AirDrop.*;
import static org.by1337.airdrop.airdrop.util.CfgManager.Config.*;
import static org.by1337.airdrop.airdrop.AirRegion.*;
import static org.by1337.airdrop.airdrop.util.GetRandomItem.GetItem;
import static org.by1337.airdrop.airdrop.util.GetRandomItem.Sort;

public class Chest {
    String DisplayName;
    String chestName;
    String regionName;
    Inventory chestInventory;
    int chanceBoost;
    World world;
    int spawnMin;
    int spawnMax;
    int radiusProtect;
    int startDelay;
    int chestLockedDelay;
    int stopEventDelay;
    int ConsStartDelay;
    int ConsChestLockedDelay;
    int ConsStopEventDelay;
    int chestInventorySize;
    int searchBeforeStart;
    Material lockedMaterial;
    Material unlockedMaterial;

    Location airLocation;
    Location newLocation;
    private boolean eventActivity = false;
    private boolean chestLocked = false;
    private boolean itWasOpen = false;
    private final HologramManager hologram = new HologramManager();

    public Chest(String displayName, String chestName, int chanceBoost, World world, int[] spawn, int radiusProtect, int startDelay, int chestLockedDelay, int stopEventDelay, Material lockedMaterial, Material unlockedMaterial, int chestInventorySize, int searchBeforeStart) {
        DisplayName = displayName;
        this.chestName = chestName;
        this.chanceBoost = chanceBoost;
        this.world = world;
        this.spawnMin = spawn[0];
        this.spawnMax = spawn[1];
        this.radiusProtect = radiusProtect;
        this.searchBeforeStart = searchBeforeStart * 60;

        this.startDelay = startDelay * 60;
        this.chestLockedDelay = chestLockedDelay * 60;
        this.stopEventDelay = stopEventDelay * 60;

        this.ConsStartDelay = startDelay * 60;
        this.ConsChestLockedDelay = chestLockedDelay * 60;
        this.ConsStopEventDelay = stopEventDelay * 60;

        this.lockedMaterial = lockedMaterial;
        this.unlockedMaterial = unlockedMaterial;
        this.chestInventorySize = chestInventorySize;
        regionName = this.chestName + "_region";
    }

    public void End() { //насильно стопнуть ивент
        airLocation = null;
        newLocation = null;
        chestLocked = false;
        eventActivity = false;
        itWasOpen = false;
        startDelay = ConsStartDelay;
        chestLockedDelay = ConsChestLockedDelay;
        stopEventDelay = ConsStopEventDelay;
    }

    public void Stop() { //остановить ивент
        chestLockedDelay = 0;
        stopEventDelay = 0;
        startDelay = ConsStartDelay;
    }

    public void Unlock() {
        chestLockedDelay = 0;
        startDelay = 0;
    }

    public void StartEvent() {
        chestLockedDelay = ConsChestLockedDelay;
        stopEventDelay = ConsStopEventDelay;
        startDelay = 0;
    }

    private String CodeReplace(String str) {
        String BuildMessage = str;
        BuildMessage = BuildMessage.replace("{time_start}", Format(startDelay)).replace("{time2_start}", Format2(startDelay)).replace("{time3_start}", Format3(startDelay));
        BuildMessage = BuildMessage.replace("{time_locked}", Format(chestLockedDelay)).replace("{time2_locked}", Format2(chestLockedDelay)).replace("{time3_locked}", Format3(chestLockedDelay));
        BuildMessage = BuildMessage.replace("{time_end}", Format(stopEventDelay)).replace("{time2_end}", Format2(stopEventDelay)).replace("{time3_end}", Format3(stopEventDelay));
        if (getAirLocation() != null)
            BuildMessage = BuildMessage.replace("{x}", "" + getAirLocation().getX()).replace("{y}", "" + getAirLocation().getY()).replace("{z}", "" + getAirLocation().getZ());
        BuildMessage = BuildMessage.replace("{name}", DisplayName);
        if (BuildMessage.contains("{world}")) {
            for (String key : instance.getConfig().getConfigurationSection("msg.world-localization").getKeys(false)) {
                if (key.equalsIgnoreCase(world.getName())) {
                    BuildMessage = BuildMessage.replace("{world}", instance.getConfig().getString("msg.world-localization." + key));
                    break;
                }
            }
        }

        return BuildMessage;
    }

    public void Timer() {
        if (eventActivity) {
            Particle();
        }
        if (startDelay <= 0 && !eventActivity && Bukkit.getOnlinePlayers().size() >= getMinOnlinePlayers() && newLocation != null) {//старт ивента
            Start();
            LasersManager.createLaser(getAirLocation());
            Message.SendAllMsg(CodeReplace(getDropSpawning()));
        } else if (!eventActivity) {//отнимаем от таймера до начала ивента
            if (getNotificationTime().contains(String.valueOf(startDelay)))
                Message.SendAllMsg(CodeReplace(getMsgStartEvent()));
            startDelay--;
        }
        if (eventActivity) {
            if (chestLockedDelay <= 0 && chestLocked) { //Открываем аирдроп
                chestLocked = false;
                getAirLocation().getBlock().setType(unlockedMaterial);
                Message.SendAllMsg(CodeReplace(getDropOpen()));

                AirDrop.Effects(airLocation);
            } else if (chestLocked) {//отнимаем от таймера до открытия
                if (getNotificationOpenTime().contains(String.valueOf(chestLockedDelay)))
                    Message.SendAllMsg(CodeReplace(getMsgOpenEvent()));
                chestLockedDelay--;
            }
        }

        if (eventActivity && stopEventDelay <= 0) {//стопаем ивент
            chestInventory.clear();
            getAirLocation().getBlock().setType(Material.AIR);
            LasersManager.removeLaser(getAirLocation());
            hologram.HoloDel(regionName);
            RemoveRegion(regionName, getWorld());
            End();
            Message.SendAllMsg(CodeReplace(getEventEnd()));
        } else if (eventActivity && chestLockedDelay <= 0) {//отнимаем от таймера до конца ивента
            stopEventDelay--;
        }
        if (eventActivity && getAirLocation() != null) {//
            hologram.HoloCreate(new Location(getAirLocation().getWorld(), getAirLocation().getX() + 0.5, getAirLocation().getY() + 1.7, getAirLocation().getZ() + 0.5), regionName, DisplayName);
            hologram.HoloUpdate(chestLockedDelay, chestLocked, regionName);
        }
        if (startDelay <= searchBeforeStart) {
            if (newLocation == null) {
                if (activeTasks.size() == 0) {
                    newLocation = RndLoc(regionName, radiusProtect, world, spawnMin, spawnMax);
                    activeTasks.add(chestName);
                //    Message.Logger("Ищу локацию для " + chestName);
                } else if (activeTasks.contains(chestName)) {
                    newLocation = RndLoc(regionName, radiusProtect, world, spawnMin, spawnMax);
                //    Message.Logger("Ищу локацию для " + chestName);
                }

            }
            if (newLocation != null) {
                if (activeTasks.size() != 0) {
                    if (activeTasks.removeIf(task -> task.equals(chestName))) {
                      //  Message.Logger("Нашёл локацию для " + chestName + " и удалил его из активных задач");
                    }

                }
            }
        }

        if (eventActivity) {
            boolean empty = true;
            for (ItemStack item : chestInventory.getContents()) {
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

    public void PlayerOpenChest(String name) {
        if (!itWasOpen) {
            Message.SendAllMsg(CodeReplace(getDropOpenEvent().replace("{player}", name)));
            itWasOpen = true;
        }
    }

    public void Start() {
        chestLocked = true;
        if (newLocation == null) {
            Message.Error("Локация для аирдропа не определенна!");
            Message.Error("Аирдроп появится как только будет найдено подходящее место для его спавна!");
            StartEvent();
            return;
        } else
            setAirLocation(newLocation);
        getAirLocation().getBlock().setType(lockedMaterial);
        Sort();

        chestInventory = Bukkit.createInventory(null, chestInventorySize, Message.MessageBuilder(DisplayName));
        RemoveRegion(regionName, getWorld());
        SetRegion(regionName, getAirLocation(), radiusProtect);
        for (int x = 0; x < chestInventory.getSize(); x++) {
            ItemStack item = GetItem((short) chanceBoost);
            if (item != null)
                chestInventory.setItem(x, item);
        }
        eventActivity = true;

    }

    public void Destroyer() {
        if (getAirLocation() != null) {
            getAirLocation().getBlock().setType(Material.AIR);
            LasersManager.removeLaser(getAirLocation());
        }
        hologram.HoloDel(regionName);
        RemoveRegion(regionName, getWorld());
    }


    private void Particle() {
        String helixType = instance.getConfig().getString("chests." + chestName + ".helix");
        if (helixType.equalsIgnoreCase("helix")) {
            double radius = instance.getConfig().getDouble("settings.effect-settings.helix-settings.radius");
            double helixPitch = instance.getConfig().getDouble("settings.effect-settings.helix-settings.helix-pitch");
            int viewDistance = instance.getConfig().getInt("settings.effect-settings.helix-settings.view-distance");
            int height = instance.getConfig().getInt("settings.effect-settings.helix-settings.height-y");
            double x = instance.getConfig().getDouble("settings.effect-settings.helix-settings.offset-x"),
                    y = instance.getConfig().getDouble("settings.effect-settings.helix-settings.offset-y"),
                    z = instance.getConfig().getDouble("settings.effect-settings.helix-settings.offset-z");
            Vector vector = new Vector(x, y, z);
            Color color;
            if (chestLocked) {
                int r = instance.getConfig().getInt("settings.effect-settings.helix-settings.close.color.r"),
                        g = instance.getConfig().getInt("settings.effect-settings.helix-settings.close.color.g"),
                        b = instance.getConfig().getInt("settings.effect-settings.helix-settings.close.color.b");
                color = Color.fromBGR(b, g, r);
            } else {
                int r = instance.getConfig().getInt("settings.effect-settings.helix-settings.open.color.r"),
                        g = instance.getConfig().getInt("settings.effect-settings.helix-settings.open.color.g"),
                        b = instance.getConfig().getInt("settings.effect-settings.helix-settings.open.color.b");
                color = Color.fromBGR(b, g, r);
            }
            Effect.createHelix(getAirLocation(), radius, color, viewDistance, height, vector, helixPitch);
        }
        if (helixType.equalsIgnoreCase("double-helix")) {
            double radius = instance.getConfig().getDouble("settings.effect-settings.double-helix-settings.radius");
            double helixPitch = instance.getConfig().getDouble("settings.effect-settings.double-helix-settings.helix-pitch");
            int viewDistance = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.view-distance");
            int height = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.height-y");
            double x = instance.getConfig().getDouble("settings.effect-settings.double-helix-settings.offset-x"),
                    y = instance.getConfig().getDouble("settings.effect-settings.double-helix-settings.offset-y"),
                    z = instance.getConfig().getDouble("settings.effect-settings.double-helix-settings.offset-z");
            Vector vector = new Vector(x, y, z);
            Color color;
            Color color1;
            if (chestLocked) {
                int r = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.close.color.r"),
                        g = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.close.color.g"),
                        b = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.close.color.b");

                int r1 = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.close.color2.r"),
                        g1 = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.close.color2.g"),
                        b1 = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.close.color2.b");
                color = Color.fromBGR(b, g, r);
                color1 = Color.fromBGR(b1, g1, r1);
            } else {
                int r = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.open.color.r"),
                        g = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.open.color.g"),
                        b = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.open.color.b");

                int r1 = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.open.color2.r"),
                        g1 = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.open.color2.g"),
                        b1 = instance.getConfig().getInt("settings.effect-settings.double-helix-settings.open.color2.b");
                color = Color.fromBGR(b, g, r);
                color1 = Color.fromBGR(b1, g1, r1);
            }
            Effect.createDoubleHelix(getAirLocation(), radius, color, color1, viewDistance, height, vector, helixPitch);
        }

        if (instance.getConfig().getBoolean("chests." + chestName + ".random-particle")) {
            String[] particle = new String[2];
            int amount = instance.getConfig().getInt("settings.effect-settings.random-particle.amount");
            int radius = instance.getConfig().getInt("settings.effect-settings.random-particle.radius");
            int viewDistance = instance.getConfig().getInt("settings.effect-settings.random-particle.view-distance");
            particle[0] = instance.getConfig().getString("settings.effect-settings.random-particle.particle");
            particle[1] = instance.getConfig().getString("settings.effect-settings.random-particle.particle2");
            try {
                Effect.createRandomParticle(getAirLocation(), radius, particle, amount, viewDistance);
            } catch (Exception e) {
                Message.Error("Ошибка при создании рандомных партиклов");
                Message.Error(e.getLocalizedMessage());

            }

        }
    }


    public Location getAirLocation() {
        return airLocation;
    }

    public void setAirLocation(Location airLocation) {
        this.airLocation = airLocation;
    }

    public Location getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(Location newLocation) {
        this.newLocation = newLocation;
    }

    public String getChestName() {
        return chestName;
    }

    public Inventory getChestInventory() {
        return chestInventory;
    }

    public boolean isEventActivity() {
        return eventActivity;
    }

    public boolean isChestLocked() {
        return chestLocked;
    }

    public String getRegionName() {
        return regionName;
    }

    public World getWorld() {
        return world;
    }

    public int getStartDelay() {
        return startDelay;
    }

    public int getChestLockedDelay() {
        return chestLockedDelay;
    }

    public int getStopEventDelay() {
        return stopEventDelay;
    }
}
