package org.by1337.airdrop.airdrop.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.by1337.airdrop.airdrop.AirDrop;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class cmdCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
            if (sender.hasPermission("air.chest")) {
                if (args.length == 1) {
                    return List.of(
                            "tp",
                            "reload",
                            "create",
                            "start",
                            "unlock",
                            "stop",
                            "gui",
                            "chest"
                    );
                }
                if (args[0].equals("gui")) {
                    Short[] chance = AirDrop.baseItem.keySet().toArray(new Short[0]);
                    List<String> list = new ArrayList<>();
                    for (Short c : chance)
                        list.add(c.toString());
                    return list;

                }
            }
        } else {
            if (sender.hasPermission("air.chest")) {
                if (args.length == 1) {
                    return List.of(
                            "tp",
                            "reload",
                            "create",
                            "start",
                            "unlock",
                            "stop",
                            "gui",
                            "chest"
                    );
                }
                if (args[0].equals("gui")) {
                    Short[] chance = AirDrop.baseItem.keySet().toArray(new Short[0]);
                    List<String> list = new ArrayList<>();
                    for (Short c : chance)
                        list.add(c.toString());
                    return list;

                }
            }
        }
        return null;
    }
}