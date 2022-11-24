package org.by1337.airdrop.airdrop.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.by1337.airdrop.airdrop.AirDrop;

import java.util.List;

import java.util.concurrent.ThreadLocalRandom;

public class GetRandomItem {


    public static Short[] chance = AirDrop.baseItem.keySet().toArray(new Short[0]);

    public static ItemStack GetItem(){
        for (Short aShort : chance)
            if (ThreadLocalRandom.current().nextInt(0, 100) < aShort && ThreadLocalRandom.current().nextInt(0, 100) > Config.getEmptySlotChance()) {
                List<ItemStack> item = AirDrop.baseItem.get(aShort);
                return item.get(ThreadLocalRandom.current().nextInt(0, item.size()));
            }
        return null;
    }

    public static void Sort(){
        boolean isSorted = false;
        Short buff;
        while (!isSorted){
            isSorted = true;
            for(int i = 0; i < chance.length-1; i++){
                if(chance[i] != null && chance[i+1] != null){
                    Short c1 = chance[i];
                    Short c2 = chance[i+1];
                    if(c1 > c2){
                        isSorted = false;
                        buff = chance[i];
                        chance[i] = chance[i+1];
                        chance[i+1] = buff;
                    }
                }
            }
        }
    }
}
