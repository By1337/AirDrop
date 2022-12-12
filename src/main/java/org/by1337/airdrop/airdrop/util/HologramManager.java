package org.by1337.airdrop.airdrop.util;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;

import org.bukkit.Location;
import java.util.Arrays;
import java.util.List;

import static org.by1337.airdrop.airdrop.AirDrop.*;
import static org.by1337.airdrop.airdrop.util.CfgManager.Config.*;


public class HologramManager {
;
    public void HoloCreate(Location loc, String holoId, String FirstLine){
        Hologram hologram;
        hologram = DHAPI.getHologram(holoId);
        List<String> lines = Arrays.asList(Message.MessageBuilder(FirstLine), "Line 2");
        if(hologram == null)
            DHAPI.createHologram(holoId, loc, lines);
        else{
            HoloDel(holoId);
            HoloCreate(loc, holoId, FirstLine);
        }
    }
    public void HoloUpdate(long time, boolean chestLocked, String holoName){
        Hologram hologram;
        hologram = DHAPI.getHologram(holoName);
        if(hologram == null)
            return;
        if(chestLocked)
            DHAPI.setHologramLine(Hologram.getCachedHologram(holoName), 1, getDropLocked().replace("{time2_locked}", Format2(time)).replace("{time_locked}", Format(time)).replace("{time3_locked}", Format3(time)));
        else
            DHAPI.setHologramLine(Hologram.getCachedHologram(holoName), 1, getDropUnlocked());
    }
    public void HoloDel(String holoName){
        Hologram hologram;
        hologram = DHAPI.getHologram(holoName);
        if(hologram != null)
            DHAPI.removeHologram(holoName);
    }
}
