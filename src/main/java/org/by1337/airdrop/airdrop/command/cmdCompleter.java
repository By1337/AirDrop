package org.by1337.airdrop.airdrop.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.by1337.airdrop.airdrop.AirDrop;
import org.by1337.airdrop.airdrop.Chest;
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
            if (sender.hasPermission("air.*")) {
                if (args.length == 1) {
                    List<String> list = new ArrayList<>();
                    for (Chest chest : AirDrop.ChestList) {
                        list.add(chest.getChestName());
                    }
                    list.add("reload");
                    list.add("create");
                    list.add("gui");
                    return list;
                }

                if(args[0].equals("create"))
                    return List.of("<chance>");
                if(args[0].equals("reload"))
                    return List.of(" ");
                if(args.length == 3 && !args[0].equals("gui"))
                    return List.of("<time>");
                if (args.length == 2 && !args[0].equals("gui")) {
                    return List.of(
                            "tp",
                            "start",
                            "unlock",
                            "stop"
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