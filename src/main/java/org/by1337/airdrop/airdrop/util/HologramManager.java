package org.by1337.airdrop.airdrop.util;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;

import org.bukkit.Location;
import java.util.Arrays;
import java.util.List;

import static org.by1337.airdrop.airdrop.AirSpawn.chestLocked;
import static org.by1337.airdrop.airdrop.util.Config.*;

public class HologramManager {
    private final String holoName = "airDropHologram";

    public void HoloCreate(Location loc){
        Hologram hologram;
        hologram = DHAPI.getHologram(holoName);
        List<String> lines = Arrays.asList(getDropNameHolo(), "Line 2");
        if(hologram == null)
            DHAPI.createHologram(holoName, loc, lines);
    }

    public void HoloUpdate(String time, String time2){
        Hologram hologram;
        hologram = DHAPI.getHologram(holoName);
        if(hologram == null)
            return;
        if(chestLocked)
            DHAPI.setHologramLine(Hologram.getCachedHologram(holoName), 1, getDropLocked().replace("{time}", time).replace("{time2}", time2));
        else
            DHAPI.setHologramLine(Hologram.getCachedHologram(holoName), 1, getDropUnlocked());
    }
    public void HoloDel(){
        Hologram hologram;
        hologram = DHAPI.getHologram(holoName);
        if(hologram != null)
            DHAPI.removeHologram(holoName);
    }
}
