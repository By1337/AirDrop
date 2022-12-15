package org.by1337.airdrop.airdrop.util;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.by1337.airdrop.airdrop.Chest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.by1337.airdrop.airdrop.AirDrop.*;
import static org.by1337.airdrop.airdrop.util.CfgManager.Config.*;


public class HologramManager {
    public void HoloCreate(Location loc, String holoId, String FirstLine) {
        Hologram hologram;
        hologram = DHAPI.getHologram(holoId);
        List<String> lines = Arrays.asList(Message.MessageBuilder(FirstLine), "Line 2");
        if (hologram == null)
            DHAPI.createHologram(holoId, loc, lines);
        else if (!hologram.getLocation().equals(loc)) {
            DHAPI.moveHologram(holoId, loc);
        }
    }

    public void HoloUpdate(Chest chest) {
        Hologram hologram;
        hologram = DHAPI.getHologram(chest.getRegionName());
        if (hologram == null)
            return;
        try {
            if (chest.isChestLocked()){
                List<String> list = new ArrayList<>(getHologramsLinesLocked());
                for(int x = 0; x < list.size(); x++){
                    String str = list.get(x);
                    str = Message.MessageBuilder(str);
                    str = chest.CodeReplace(str);
                    list.set(x, str);
                }
                DHAPI.setHologramLines(hologram, list);
                if(!hologram.getLocation().equals(new Location(hologram.getLocation().getWorld(), hologram.getLocation().getX(), chest.getAirLocation().getY() + list.size() * 0.35 + 1, hologram.getLocation().getZ())))
                    DHAPI.moveHologram(hologram, new Location(hologram.getLocation().getWorld(), hologram.getLocation().getX(), chest.getAirLocation().getY() + list.size() * 0.35 + 1, hologram.getLocation().getZ()));
            }
            else{
                List<String> list = new ArrayList<>(getHologramsLinesUnlocked());
                for(int x = 0; x < list.size(); x++){
                    String str = list.get(x);
                    str = Message.MessageBuilder(str);
                    str = chest.CodeReplace(str);
                    list.set(x, str);
                }
                DHAPI.setHologramLines(hologram, list);
                if(!hologram.getLocation().equals(new Location(hologram.getLocation().getWorld(), hologram.getLocation().getX(), chest.getAirLocation().getY() + list.size() * 0.35 + 1, hologram.getLocation().getZ())))
                    DHAPI.moveHologram(hologram, new Location(hologram.getLocation().getWorld(), hologram.getLocation().getX(), chest.getAirLocation().getY() + list.size() * 0.35 + 1, hologram.getLocation().getZ()));
            }


        }catch (Exception e){
            Message.Error("Ошибка при обновлении голограммы. Пробую исправить");
            Message.Error(e.getLocalizedMessage());
            HoloDel(chest.getRegionName());
        }

    }

    public void HoloDel(String holoName) {
        Hologram hologram;
        hologram = DHAPI.getHologram(holoName);
        if (hologram != null)
            DHAPI.removeHologram(holoName);
    }
}
