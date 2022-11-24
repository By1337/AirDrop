package org.by1337.airdrop.airdrop.Gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.by1337.airdrop.airdrop.AirDrop;

import java.util.List;

import static org.by1337.airdrop.airdrop.AirDrop.baseItem;

public class ClickEvent implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().equals(GuiBuilder.menuInventory)) {
            if (e.getCurrentItem() != null) {
                if (e.getClick() == ClickType.RIGHT) {
                    List<ItemStack> itemList = baseItem.get(GuiBuilder.listItem);
                    itemList.remove(e.getSlot());
                    baseItem.put(GuiBuilder.listItem, itemList);
                    if (baseItem.get(GuiBuilder.listItem).size() == 0)
                        baseItem.remove(GuiBuilder.listItem);
                    GuiBuilder.GenerateMenu(GuiBuilder.listItem);
                }
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCloseGui(InventoryCloseEvent e) {
        if (e.getInventory().equals(GuiBuilder.menuInventory)) {
            AirDrop.Save();
        }
    }
}
