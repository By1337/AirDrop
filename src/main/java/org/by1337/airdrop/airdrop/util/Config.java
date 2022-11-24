package org.by1337.airdrop.airdrop.util;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.by1337.airdrop.airdrop.AirDrop.instance;

public class Config {
    private static int spawnMax;
    private static int spawnMin;
    private static World world;
    private static List<String> blackList = new ArrayList<String>();
    private static String dropNameHolo;
    private static String dropLocked;
    private static int timeStartInterval;
    private static int timeLockedChest;
    private static int timeStopEvent;
    private static List<String> formatTime;
    private static List<String> notificationTime;
    private static List<String> notificationOpenTime;
    private static List<String> dropOpenEffect;
    private static String msgStartEvent;
    private static String dropUnlocked;
    private static String noPrem;
    private static String noEvent;
    private static String fewArguments;
    private static String chestLocked;
    private static String dropSpawning;
    private static String msgOpenEvent;
    private static String eventEnd;
    private static String prefix;
    private static String errorNumber;
    private static String itemAdd;
    private static String reload;
    private static String onlyPlayers;
    private static String invalidKey;
    private static String unknownCommand;
    private static String itemDel;
    private static Double explosionPower;
    private static int radiusProtectBlock;
    private static int emptySlotChance;


    public static void LoadConfig(){
        spawnMax = instance.getConfig().getInt("settings.spawn-max");
        spawnMin = instance.getConfig().getInt("settings.spawn-min");
        emptySlotChance = instance.getConfig().getInt("settings.empty-slot-chance");

        timeStartInterval = instance.getConfig().getInt("settings.time-start-interval");
        timeLockedChest = instance.getConfig().getInt("settings.duration-event");
        timeStopEvent = instance.getConfig().getInt("settings.time-stop-event");
        world = Bukkit.getWorld((String) Objects.requireNonNull(instance.getConfig().get("settings.world")));
        blackList = instance.getConfig().getStringList("black-List");
        dropNameHolo = instance.getConfig().getString("msg.drop-name-holo");
        dropLocked = instance.getConfig().getString("msg.drop-locked");
        msgStartEvent = instance.getConfig().getString("msg.msg-start-event");
        dropUnlocked = instance.getConfig().getString("msg.drop-unlocked");
        noPrem = instance.getConfig().getString("msg.no-prem");
        noEvent = instance.getConfig().getString("msg.no-event");
        fewArguments = instance.getConfig().getString("msg.few-arguments");
        chestLocked = instance.getConfig().getString("msg.chest-locked");
        dropSpawning = instance.getConfig().getString("msg.drop-spawning");
        msgOpenEvent = instance.getConfig().getString("msg.msg-open-event");
        eventEnd = instance.getConfig().getString("msg.event-end");
        prefix = instance.getConfig().getString("msg.prefix");
        errorNumber = instance.getConfig().getString("msg.error-number");
        itemAdd = instance.getConfig().getString("msg.item-add");
        reload = instance.getConfig().getString("msg.reload");
        onlyPlayers = instance.getConfig().getString("msg.only-players");
        invalidKey = instance.getConfig().getString("msg.invalid-key");
        unknownCommand = instance.getConfig().getString("msg.unknown-command");
        itemDel = instance.getConfig().getString("msg.item-del");

        explosionPower = (double) instance.getConfig().getDouble("settings.effect-settings.explosion-power");
        radiusProtectBlock = instance.getConfig().getInt("settings.radius-protect-block");

        formatTime = instance.getConfig().getStringList("msg.format-time");
        notificationTime = instance.getConfig().getStringList("msg.notification-time");
        notificationOpenTime = instance.getConfig().getStringList("msg.notification-open-time");
        dropOpenEffect = instance.getConfig().getStringList("settings.effect-settings.drop-open-effect");
    }

    public static String getItemDel() {
        return itemDel;
    }

    public static int getEmptySlotChance() {
        return emptySlotChance;
    }

    public static String getUnknownCommand() {
        return unknownCommand;
    }

    public static String getInvalidKey() {
        return invalidKey;
    }

    public static String getOnlyPlayers() {
        return onlyPlayers;
    }

    public static String getReload() {
        return reload;
    }

    public static String getItemAdd() {
        return itemAdd;
    }

    public static String getErrorNumber() {
        return errorNumber;
    }

    public static int getRadiusProtectBlock() {
        return radiusProtectBlock;
    }

    public static Double getExplosionPower() {
        return explosionPower;
    }

    public static List<String> getDropOpenEffect() {
        return dropOpenEffect;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static String getEventEnd() {
        return eventEnd;
    }

    public static List<String> getNotificationOpenTime() {
        return notificationOpenTime;
    }

    public static String getMsgOpenEvent() {
        return msgOpenEvent;
    }

    public static String getChestLocked() {
        return chestLocked;
    }

    public static String getDropSpawning() {
        return dropSpawning;
    }

    public static String getFewArguments() {
        return fewArguments;
    }

    public static String getNoEvent() {
        return noEvent;
    }

    public static String getNoPrem() {
        return noPrem;
    }

    public static String getDropUnlocked() {
        return dropUnlocked;
    }

    public static List<String> getNotificationTime() {
        return notificationTime;
    }

    public static String getMsgStartEvent() {
        return msgStartEvent;
    }

    public static List<String> getFormatTime() {
        return formatTime;
    }

    public static int getTimeStartInterval() {
        return timeStartInterval;
    }

    public static int getTimeLockedChest() {
        return timeLockedChest;
    }

    public static int getTimeStopEvent() {
        return timeStopEvent;
    }

    public static String getDropNameHolo() {
        return dropNameHolo;
    }

    public static String getDropLocked() {
        return dropLocked;
    }

    public static World getWorld() {
        return world;
    }

    public static List<String> getBlackList() {
        return blackList;
    }

    public static int getSpawnMax() {
        return spawnMax;
    }

    public static int getSpawnMin() {
        return spawnMin;
    }


}
