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
                if (args[0].equals("chest") && args.length <= 2) {
                    List<String> list = new ArrayList<>();
                    for(Chest chest : AirDrop.ChestList){
                        list.add(chest.getChestName());
                    }
                    return list;

                }
                if (args.length == 1) {
                    return List.of(
                            "reload",
                            "create",
                            "gui",
                            "chest"
                    );
                }
                if (args.length == 3) {
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
        } else {
                if (args.length == 2) {
                    return List.of(
                            "reload",
                            "start",
                            "unlock",
                            "stop"
                    );
                }
        }
        return null;
    }
}