package org.by1337.airdrop.airdrop.Gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.by1337.airdrop.airdrop.util.CfgManager.Config;
import org.by1337.airdrop.airdrop.util.Message;
import static org.bukkit.inventory.ItemFlag.*;
import java.util.ArrayList;
import java.util.List;
import static org.by1337.airdrop.airdrop.AirDrop.baseItem;
public class GuiBuilder {
    public static Inventory menuInventory = Bukkit.createInventory(null, 54, "AirDrop Editor loot");
    public static int pageNumber = 0;
    public static Short listItem;

    public static void GenerateMenu(Short key){
        listItem = key;
        menuInventory.clear();
        if(!baseItem.containsKey(key)) {
            Message.Warning("Key is not valid!");
            return;
        }
        baseItem.get(GuiBuilder.listItem).removeIf(item -> (item.getType() == Material.AIR));
        menuInventory.setItem(46,  ItemBuilder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), null, " ", false));
        menuInventory.setItem(47,  ItemBuilder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), null, " ", false));
        menuInventory.setItem(48,  ItemBuilder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), null, " ", false));
        menuInventory.setItem(50,  ItemBuilder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), null, " ", false));
        menuInventory.setItem(51,  ItemBuilder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), null, " ", false));
        menuInventory.setItem(52,  ItemBuilder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), null, " ", false));
        menuInventory.setItem(45,  ItemBuilder(new ItemStack(Material.ARROW), "§7Предыдущая страница", "§fНазад", true));
        menuInventory.setItem(53,  ItemBuilder(new ItemStack(Material.ARROW), "§7Следующая страница", "§fВперёд", true));
        ItemStack Exit = new ItemStack(Material.EMERALD);
        Exit = ItemBuilder(Exit, "§7Отменить все изменения", "§cОтмена", true);
        menuInventory.setItem(49,  Exit);
        for(int x = 0; x < baseItem.get(key).size(); x++){
            if(x >= 45)
                continue;
            if(baseItem.get(key).size() <= x + pageNumber)
                continue;
            ItemStack item = new ItemStack(baseItem.get(key).get(x + pageNumber));
            item = ItemBuilder(item, Message.MessageBuilder(Config.getItemDel()), null, false);
            menuInventory.setItem(x, item);
        }
    }
    private static ItemStack ItemBuilder(ItemStack getItem, String addLore, String name, Boolean addEnchant){
        ItemStack item = new ItemStack(getItem);
        ItemMeta meta = item.getItemMeta();
        if(addEnchant){
            meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 0, true);
            meta.addItemFlags(HIDE_ENCHANTS);
        }
        if(name != null)
            meta.setDisplayName(name);
        if(addLore != null){
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(addLore);
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }
}
