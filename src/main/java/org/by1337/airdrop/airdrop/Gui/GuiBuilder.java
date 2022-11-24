package org.by1337.airdrop.airdrop.Gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.by1337.airdrop.airdrop.util.Config;
import org.by1337.airdrop.airdrop.util.Message;

import java.util.ArrayList;
import java.util.List;

import static org.by1337.airdrop.airdrop.AirDrop.baseItem;

public class GuiBuilder {
    public static Inventory menuInventory = Bukkit.createInventory(null, 54, "AirDrop Editor loot");
    public static Short listItem;

    public static void GenerateMenu(Short key){
        listItem = key;
        menuInventory.clear();
        if(!baseItem.containsKey(key)) {
            Message.Warning("Key is not valid!");
            return;
        }
        for(int x = 0; x < baseItem.get(key).size(); x++){
            if(x >= 54)
                continue;
            ItemStack item = new ItemStack(baseItem.get(key).get(x));
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(Config.getItemDel().replace("&", "§"));
            meta.setLore(lore);
            item.setItemMeta(meta);
            menuInventory.setItem(x, item);
        }
    }
}
