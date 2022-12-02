package org.by1337.airdrop.airdrop.command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.by1337.airdrop.airdrop.AirDrop;

import org.by1337.airdrop.airdrop.AirSpawn;
import org.by1337.airdrop.airdrop.Gui.GuiBuilder;
import org.by1337.airdrop.airdrop.util.Message;

import static org.by1337.airdrop.airdrop.AirSpawn.getAirLocation;
import static org.by1337.airdrop.airdrop.util.CfgManager.Config.*;
import java.util.*;

import static org.by1337.airdrop.airdrop.AirDrop.*;
import static org.by1337.airdrop.airdrop.util.GetRandomItem.GetItem;
import static org.by1337.airdrop.airdrop.util.GetRandomItem.Sort;

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
            if (args[0].equals("vr")) {
                Message.SendMsg(p, "" + getConfigVersion());
                return true;
            }
            if (args[0].equals("chest")) {
                if (p.hasPermission("air.chest")) {
                    p.getLocation().getBlock().setType(Material.CHEST);
                    Sort();
                    if (p.getLocation().getBlock().getState() instanceof Chest) {
                        Chest chest = (Chest) p.getLocation().getBlock().getState();
                        Inventory inv = chest.getBlockInventory();
                        chest.getInventory().clear();
                        for (int x = 0; x < chest.getInventory().getSize(); x++) {
                            ItemStack item = GetItem();
                            if (item != null)
                                inv.setItem(x, item);
                        }
                    } else {
                        Message.Error("No chest was found at the spawn site!");
                    }
                } else
                    Message.SendMsg(p, getNoPrem());
            }

            if (args[0].equals("gui")) {
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

            }
            if (args[0].equals("tp")) {
                if (p.hasPermission("air.tp")) {
                    if (AirSpawn.eventActivity)
                        p.teleport(getAirLocation());
                    else
                        Message.SendMsg(p, getNoEvent());
                } else
                    Message.SendMsg(p, getNoPrem());
                return true;
            }

            if (args[0].equals("unlock")) {
                if (p.hasPermission("air.unlock")) {
                    if (AirSpawn.eventActivity)
                        AirDrop.Unlock();
                    else
                        Message.SendMsg(p, getNoEvent());
                } else
                    Message.SendMsg(p, getNoPrem());
                return true;
            }

            if (args[0].equals("stop")) {
                if (p.hasPermission("air.stop")) {
                    if (AirSpawn.eventActivity)
                        AirDrop.Stop();
                    else
                        Message.SendMsg(p, getNoEvent());
                } else
                    Message.SendMsg(p, getNoPrem());
                return true;
            }

            if (args[0].equals("start")) {
                if (p.hasPermission("air.start")) {
                    AirDrop.Start();
                } else
                    Message.SendMsg(p, getNoPrem());
                return true;
            }

            if (args[0].equals("create")) {
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
                if (p.hasPermission("pholo.reload")) {
                    plugin.reloadConfig();
                    LoadConfig();
                    Message.SendMsg(p, getReload());
                    return true;
                } else
                    Message.SendMsg(p, getNoPrem());
                return true;
            }
            return true;
        } else {
            if (args[0].equals("unlock")) {
                if (AirSpawn.eventActivity)
                    AirDrop.Unlock();
                else
                    Message.Logger(getNoEvent());
                return true;
            }
            if (args[0].equals("stop")) {

                if (AirSpawn.eventActivity)
                    AirDrop.Stop();
                else
                    Message.Logger(getNoEvent());
                return true;
            }
            if (args[0].equals("start")) {
                AirDrop.Start();
                return true;
            }
            Message.Warning(getOnlyPlayers());
        }
        return true;
    }
}