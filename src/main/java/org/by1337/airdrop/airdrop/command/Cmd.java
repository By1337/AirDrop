package org.by1337.airdrop.airdrop.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.by1337.airdrop.airdrop.AirDrop;
import org.by1337.airdrop.airdrop.Chest;
import org.by1337.airdrop.airdrop.Gui.GuiBuilder;
import org.by1337.airdrop.airdrop.util.Message;
import static org.by1337.airdrop.airdrop.util.CfgManager.Config.*;
import java.util.*;
import static org.by1337.airdrop.airdrop.AirDrop.*;


public class Cmd implements CommandExecutor {
    private final AirDrop plugin;

    public Cmd(AirDrop plugin) {
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
            if (args.length == 0) {
                Message.SendMsg(p, getUnknownCommand());
                return true;
            }
            if (args.length >= 2) {
                if (args[1].equals("tp")) {

                    if (!p.hasPermission("air.tp")) {
                        Message.SendMsg(p, getNoPrem());
                        return true;
                    }

                    for (Chest chest : AirDrop.ChestList) {
                        if (chest.getChestName().equals(args[0])) {
                            if (chest.isEventActivity()) {
                                p.teleport(chest.getAirLocation());
                                Message.SendMsg(p, "&aTeleported!");
                                return true;
                            } else {
                                Message.SendMsg(p, getNoEvent());
                                return true;
                            }
                        }
                    }
                    Message.SendMsg(p, "&cНет аирдропа с таким именем");
                    return true;
                }
                if (args[1].equals("start")) {

                    if (!p.hasPermission("air.start")) {
                        Message.SendMsg(p, getNoPrem());
                        return true;
                    }

                    for (Chest chest : AirDrop.ChestList) {
                        if (chest.getChestName().equals(args[0])) {
                            if (chest.isEventActivity()) {
                                Message.SendMsg(p, "&cИвент уже начат!");
                                return true;
                            }
                            if(args.length == 3){
                                int time;
                                try {
                                    time = Integer.parseInt(args[2]);
                                }catch (ClassCastException e){
                                    Message.SendMsg(p, "{PP} &cЭто не число!");
                                    Message.SendMsg(p, "&c" + e.getLocalizedMessage());
                                    return true;
                                }
                                chest.StartEvent(time * 60);
                                Message.SendMsg(p, "&aЗапуск ивента через " + time + " минут");
                                return true;
                            }
                            chest.StartEvent(0);
                            Message.SendMsg(p, "&aЗапуск ивента...");
                            return true;
                        }
                    }
                    Message.SendMsg(p, "&cНет аирдропа с таким именем");
                    return true;
                }
                if (args[1].equals("stop")) {

                    if (!p.hasPermission("air.stop")) {
                        Message.SendMsg(p, getNoPrem());
                        return true;
                    }

                    for (Chest chest : AirDrop.ChestList) {
                        if (chest.getChestName().equals(args[0])) {
                            if (!chest.isEventActivity()) {
                                Message.SendMsg(p, getNoEvent());
                                return true;
                            }
                            chest.Stop();
                            Message.SendMsg(p, "&aИвент остановлен!");
                            return true;
                        }
                    }
                    Message.SendMsg(p, "&cНет аирдропа с таким именем");
                    return true;
                }
                if (args[1].equals("unlock")) {

                    if (!p.hasPermission("air.unlock")) {
                        Message.SendMsg(p, getNoPrem());
                        return true;
                    }

                    for (Chest chest : AirDrop.ChestList) {
                        if (chest.getChestName().equals(args[0])) {
                            if (!chest.isEventActivity()) {
                                Message.SendMsg(p, getNoEvent());
                                return true;
                            }
                            chest.Unlock();
                            Message.SendMsg(p, "&aАирдроп открыт!");
                            return true;
                        }
                    }
                    Message.SendMsg(p, "&cНет аирдропа с таким именем");
                    return true;
                }
            }


            if (args[0].equals("gui")) {

                if (!p.hasPermission("air.gui")) {
                    Message.SendMsg(p, getNoPrem());
                    return true;
                }

                if (p.hasPermission("air.gui")) {
                    if (args.length >= 2) {
                        Short[] chance = AirDrop.baseItem.keySet().toArray(new Short[0]);
                        List<String> list = new ArrayList<>();
                        for (Short c : chance)
                            list.add(c.toString());
                        if (!list.contains(args[1])) {
                            Message.SendMsg(p, getInvalidKey());
                            return true;
                        }
                        GuiBuilder.pageNumber = 0;
                        GuiBuilder.GenerateMenu(Short.valueOf(args[1]));
                        p.openInventory(GuiBuilder.menuInventory);
                    } else
                        Message.SendMsg(p, getFewArguments());
                } else
                    Message.SendMsg(p, getNoPrem());
                return true;
            }
            if (args[0].equals("create")) {

                if (!p.hasPermission("air.create")) {
                    Message.SendMsg(p, getNoPrem());
                    return true;
                }

                if (p.hasPermission("air.create")) {
                    if (args.length >= 2) {
                        List<ItemStack> list = new ArrayList<>();

                        for (ItemStack item : p.getInventory().getContents())
                            if (item != null)
                                list.add(item);
                        try {
                            if (!baseItem.containsKey(Short.valueOf(args[1]))) {
                                baseItem.put(Short.valueOf(args[1]), list);

                            } else {
                                List<ItemStack> list2 = baseItem.get(Short.valueOf(args[1]));
                                list2.addAll(list);
                                baseItem.put(Short.valueOf(args[1]), list2);
                            }
                        } catch (Exception e) {
                            Message.SendMsg(p, getErrorNumber());
                            Message.Error("" + e.getLocalizedMessage());
                            return true;
                        }

                        Save();
                        Message.SendMsg(p, getItemAdd().replace("{key}", args[1]));
                    } else
                        Message.SendMsg(p, getFewArguments());
                } else
                    Message.SendMsg(p, getNoPrem());
                return true;
            }
            if (args[0].equals("reload")) {
                if (p.hasPermission("air.reload")) {
                    plugin.reloadConfig();
                    LoadConfig();
                    AirDrop.UnLoad();
                    AirDrop.Load();
                    Message.SendMsg(p, getReload());
                    return true;
                } else
                    Message.SendMsg(p, getNoPrem());
                return true;
            }
            Message.SendMsg(p, getUnknownCommand());
            return true;
        }
        return true;
    }
}