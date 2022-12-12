package org.by1337.airdrop.airdrop.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.by1337.airdrop.airdrop.AirDrop;
import org.by1337.airdrop.airdrop.util.CfgManager.Config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {

    private static final ConsoleCommandSender SENDER = Bukkit.getConsoleSender();
    private static final String AUTHOR = "By1337";
    private static final String prefixPlugin = "[AirDrop]";
    private static final String Prefix = Config.getPrefix();
    public static final Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");
    public static final char COLOR_CHAR = ChatColor.COLOR_CHAR;

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
        String str = msg.replace("PX", prefixPlugin).replace("AU", AUTHOR).replace("{px}", Prefix).replace("&", "§");
        return str;
    }
    public static void SendAllMsg(String msg) {
        for(Player pl : Bukkit.getOnlinePlayers())
            pl.sendMessage(MessageBuilder(msg));
    }


    public static String translateHexColorCodes(String message) {
//        Matcher matcher = HEX_PATTERN.matcher(message);
//        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
//        while (matcher.find()) {
//            String group = matcher.group(1);
//            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
//                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
//                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
//                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
//            );
//        }
//        return matcher.appendTail(buffer).toString();

        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            final ChatColor hexColor = ChatColor.valueOf(matcher.group().substring(1, matcher.group().length() - 1));
            final String before = message.substring(0, matcher.start());
            final String after = message.substring(matcher.end());
            message = before + hexColor + after;
            matcher = HEX_PATTERN.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}