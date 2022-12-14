package org.by1337.airdrop.airdrop.util.CfgManager;

import org.by1337.airdrop.airdrop.AirRegion;
import java.util.ArrayList;
import java.util.List;


import static org.by1337.airdrop.airdrop.AirDrop.instance;

public class Config {

    private static List<String> blackList = new ArrayList<String>();

    private static String dropLocked;

    private static int minOnlinePlayers;
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
    private static String papiEventIsActivity;
    private static Double explosionPower;
    private static Double configVersion;
    private static int emptySlotChance;
    private static String defendersName;
    private static String papiPoz;
    private static String defendersType;
    private static String papiPozNone;
    private static String papiChestUnlocked;
    private static String dropOpenEvent;
    private static String dropOpen;
    private static String papiEventEnded;


    public static void LoadConfig() {
        papiPoz = instance.getConfig().getString("msg.papi-poz");
        dropOpenEvent = instance.getConfig().getString("msg.drop-open-event");
        dropOpen = instance.getConfig().getString("msg.drop-open");
        papiEventEnded = instance.getConfig().getString("msg.papi-event-ended");

        papiPozNone = instance.getConfig().getString("msg.papi-poz-none");
        papiChestUnlocked = instance.getConfig().getString("msg.papi-chest-is-unlocked");


        emptySlotChance = instance.getConfig().getInt("settings.empty-slot-chance");
        minOnlinePlayers = instance.getConfig().getInt("settings.min-online-players");


        blackList = instance.getConfig().getStringList("black-List");

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
        papiEventIsActivity = instance.getConfig().getString("msg.papi-event-is-activity");
        defendersName = instance.getConfig().getString("settings.defenders.name");
        defendersType = instance.getConfig().getString("settings.defenders.type");

        explosionPower = (double) instance.getConfig().getDouble("settings.effect-settings.explosion-power");
        configVersion = (double) instance.getConfig().getDouble("config-version");


        formatTime = instance.getConfig().getStringList("msg.format-time");
        notificationTime = instance.getConfig().getStringList("msg.notification-time");
        notificationOpenTime = instance.getConfig().getStringList("msg.notification-open-time");
        dropOpenEffect = instance.getConfig().getStringList("settings.effect-settings.drop-open-effect");
        AirRegion.LoadFlags();
    }

    public static String getPapiPozNone() {
        return papiPozNone;
    }

    public static String getPapiChestUnlocked() {
        return papiChestUnlocked;
    }

    public static String getPapiPoz() {
        return papiPoz;
    }

    public static String getDefendersType() {
        return defendersType;
    }

    public static String getDefendersName() {
        return defendersName;
    }

    public static String getPapiEventIsActivity() {
        return papiEventIsActivity;
    }

    public static int getMinOnlinePlayers() {
        return minOnlinePlayers;
    }

    public static Double getConfigVersion() {
        return configVersion;
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



    public static String getDropLocked() {
        return dropLocked;
    }



    public static List<String> getBlackList() {
        return blackList;
    }



    public static String getDropOpenEvent() {
        return dropOpenEvent;
    }

    public static String getDropOpen() {
        return dropOpen;
    }

    public static String getPapiEventEnded() {
        return papiEventEnded;
    }

    public static void SetConfig() {
        instance.getConfig().options().copyDefaults(true);
        instance.getConfig().set("msg.papi-poz-none", getPapiPozNone());
        instance.getConfig().set("msg.papi-chest-is-unlocked", getPapiChestUnlocked());
        instance.getConfig().set("msg.drop-open-event", getDropOpenEvent());
        instance.getConfig().set("msg.drop-open", getDropOpen());
        instance.getConfig().set("msg.papi-event-ended", getPapiEventEnded());
        instance.getConfig().set("msg.papi-poz", getPapiPoz());

        instance.getConfig().set("settings.empty-slot-chance", getEmptySlotChance());

        instance.getConfig().set("black-List", getBlackList());

        instance.getConfig().set("msg.drop-locked", getDropLocked());
        instance.getConfig().set("msg.msg-start-event", getMsgStartEvent());
        instance.getConfig().set("msg.drop-unlocked", getDropUnlocked());
        instance.getConfig().set("msg.no-prem", getNoPrem());
        instance.getConfig().set("msg.no-event", getNoEvent());
        instance.getConfig().set("msg.few-arguments", getFewArguments());
        instance.getConfig().set("msg.chest-locked", getChestLocked());
        instance.getConfig().set("msg.drop-spawning", getDropSpawning());
        instance.getConfig().set("msg.msg-open-event", getMsgOpenEvent());
        instance.getConfig().set("msg.event-end", getEventEnd());
        instance.getConfig().set("msg.prefix", getPrefix());
        instance.getConfig().set("msg.error-number", getErrorNumber());
        instance.getConfig().set("msg.item-add", getItemAdd());
        instance.getConfig().set("msg.reload", getReload());
        instance.getConfig().set("msg.only-players", getOnlyPlayers());
        instance.getConfig().set("msg.invalid-key", getInvalidKey());
        instance.getConfig().set("msg.unknown-command", getUnknownCommand());
        instance.getConfig().set("msg.item-del", getItemDel());
        instance.getConfig().set("settings.min-online-players", getMinOnlinePlayers());
        instance.getConfig().set("msg.papi-event-is-activity", getPapiEventIsActivity());
        instance.getConfig().set("settings.effect-settings.explosion-power", getExplosionPower());
        instance.getConfig().set("settings.defenders.name", getDefendersName());
        instance.getConfig().set("msg.format-time", getFormatTime());
        instance.getConfig().set("msg.notification-time", getNotificationTime());
        instance.getConfig().set("msg.notification-open-time", getNotificationOpenTime());
        instance.getConfig().set("settings.effect-settings.drop-open-effect", getDropOpenEffect());
        instance.getConfig().set("settings.defenders.type", getDefendersType());
    }

}
