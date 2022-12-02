package org.by1337.airdrop.airdrop.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.by1337.airdrop.airdrop.AirDrop;
import org.by1337.airdrop.airdrop.util.CfgManager.Config;

public class Message {

    private static final ConsoleCommandSender SENDER = Bukkit.getConsoleSender();
    private static final String AUTHOR = "By1337";
    private static final String prefixPlugin = "[AirDrop]";
    private static final String Prefix = Config.getPrefix();

    public static void SendMsg(Player pl, String msg) {
        pl.sendMessage(MessageBuilder(msg));
    }

    public static void Logger(String msg) {
        SENDER.sendMessage(MessageBuilder(msg));
    }

    public static void Error(String msg) {
        SENDER.sendMessage(ChatColor.RED + MessageBuilder(msg));
    }
    public static void Warning(String msg) {
        AirDrop.instance.getLogger().warning(MessageBuilder(msg));
       // SENDER.sendMessage(ChatColor.YELLOW + MessageBuilder(msg));
    }

    public static String MessageBuilder(String msg) {
        return msg.replace("PX", prefixPlugin).replace("AU", AUTHOR).replace("{px}", Prefix).replace("&", "§");
    }
    public static void SendAllMsg(String msg) {
        for(Player pl : Bukkit.getOnlinePlayers())
            pl.sendMessage(MessageBuilder(msg));
    }

}