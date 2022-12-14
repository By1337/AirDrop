package org.by1337.airdrop.airdrop.util;

import eu.decentsoftware.holograms.api.utils.PAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.by1337.airdrop.airdrop.AirDrop;
import org.by1337.airdrop.airdrop.util.CfgManager.Config;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Message {

    private static final ConsoleCommandSender SENDER = Bukkit.getConsoleSender();
    private static final String AUTHOR = "&#a612cb&lB&#9a17d2&ly&#8d1bd9&l1&#8120e1&l3&#7424e8&l3&#6829ef&l7";
    private static final String prefixPlugin = "&7[&#a612cbA&#9c16d1i&#911ad7r&#871eddD&#7d21e3r&#7225e9o&#6829efp&7]";
    private static final String Prefix = Config.getPrefix();
    public static final Pattern RAW_HEX_REGEX = Pattern.compile("&(#[a-f0-9]{6})", Pattern.CASE_INSENSITIVE);


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
        if(msg == null)
            return "";
        String str = msg.replace("{PP}", prefixPlugin).replace("AU", AUTHOR).replace("{px}", Prefix); // msg.replace("PX", prefixPlugin).replace("AU", AUTHOR).replace("{px}", Prefix).replace("&", "§");
        str = PAPI.setPlaceholders(null, str);
        return  hex(str);
    }

    public static void SendAllMsg(String msg) {
        for (Player pl : Bukkit.getOnlinePlayers())
            pl.sendMessage(MessageBuilder(msg));
    }


    private static String hex(String message) {
        Matcher m = RAW_HEX_REGEX.matcher(message);
       // if (VersionHelper.IS_HEX_VERSION)
            while (m.find())
                message = message.replace(m.group(), ChatColor.of(m.group(1)).toString());
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}