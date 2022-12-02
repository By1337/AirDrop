package org.by1337.airdrop.airdrop.Gui;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.by1337.airdrop.airdrop.AirDrop;
import java.util.*;
import static org.by1337.airdrop.airdrop.AirDrop.baseItem;
import static org.by1337.airdrop.airdrop.AirDrop.instance;
import static org.by1337.airdrop.airdrop.Gui.GuiBuilder.pageNumber;

public class ClickEvent implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        int nextPage = 45;
        if (e.getClickedInventory() != null && e.getClickedInventory().equals(GuiBuilder.menuInventory)) {
            if (e.getCurrentItem() != null) {
                if (e.getSlot() == 53) {
                    pageNumber += nextPage;
                    GuiBuilder.GenerateMenu(GuiBuilder.listItem);
                    e.setCancelled(true);
                    return;
                }
                if (e.getSlot() == 45) {
                    if (pageNumber >= 53) {
                        pageNumber -= nextPage;
                        GuiBuilder.GenerateMenu(GuiBuilder.listItem);
                    } else {
                        pageNumber = 0;
                        GuiBuilder.GenerateMenu(GuiBuilder.listItem);
                    }
                    e.setCancelled(true);
                    return;
                }
                if (e.getSlot() == 49) {
                    if (e.getClick() == ClickType.LEFT) {
                        instance.reloadConfig();
                        AirDrop.Load();
                        pageNumber = 0;
                        GuiBuilder.GenerateMenu(GuiBuilder.listItem);
                    }
                    e.setCancelled(true);
                    return;
                }

                if (e.getClick() == ClickType.RIGHT && e.getSlot() <= 44 && baseItem.get(GuiBuilder.listItem).size() >= e.getSlot() + pageNumber) {
                    List<ItemStack> itemList = baseItem.get(GuiBuilder.listItem);
                    itemList.set(e.getSlot() + pageNumber, new ItemStack(Material.AIR));
                    baseItem.put(GuiBuilder.listItem, itemList);
                    if (baseItem.get(GuiBuilder.listItem).size() == 0)
                        baseItem.remove(GuiBuilder.listItem);
                    GuiBuilder.menuInventory.setItem(e.getSlot(), new ItemStack(Material.AIR));
                    //GuiBuilder.GenerateMenu(GuiBuilder.listItem);
                }
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCloseGui(InventoryCloseEvent e) {
        if (e.getInventory().equals(GuiBuilder.menuInventory)) {
            baseItem.get(GuiBuilder.listItem).removeIf(item -> (item.getType() == Material.AIR));
            AirDrop.Save();
        }
    }
}
