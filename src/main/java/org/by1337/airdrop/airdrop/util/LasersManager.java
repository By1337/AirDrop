package org.by1337.airdrop.airdrop.util;


import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.event.Listener;
import static org.by1337.airdrop.airdrop.AirDrop.instance;


public class LasersManager implements Listener {
    public static List<Laser> laserList = new ArrayList<>();

    public static void createLaser(Location loc) {
        String mode = instance.getConfig().getString("settings.laser-settings.laser");
        int time = instance.getConfig().getInt("settings.laser-settings.laser-time");
        Location toprandomloc = new Location(loc.getWorld(), loc.getX(), (double) Objects.requireNonNull(loc.getWorld()).getMaxHeight(), loc.getZ());
        Location fixedrandomloc = new Location(loc.getWorld(), loc.getX(), loc.getY() - 2.0, loc.getZ());
        String airdropLaser = instance.getConfig().getString("settings.laser-settings.laser");
        int laserViewDistance = instance.getConfig().getInt("settings.laser-settings.laser-view-distance");
        switch (Objects.requireNonNull(mode)) {
            case "CRYSTAL_LASER":
                try {
                    Laser laser = new Laser.CrystalLaser(toprandomloc, fixedrandomloc, time, laserViewDistance);
                    laser.start(instance);
                    laserList.add(laser);
                } catch (ReflectiveOperationException var12) {
                    var12.printStackTrace();
                    Message.Warning("Ошибка спавна лазера, пропуск"); //Error spawning laser, skipping
                }
                break;
            case "GUARDIAN_LASER":
                try {
                    Laser laser = new Laser.GuardianLaser(toprandomloc, fixedrandomloc, time, laserViewDistance);
                    laser.start(instance);
                    laserList.add(laser);
                } catch (ReflectiveOperationException var11) {
                    var11.printStackTrace();
                    Message.Warning("Ошибка спавна лазера, пропуск"); //Error spawning laser, skipping
                }
            case "NONE":
                break;
            default:
                Message.Error("Режим лазера - " + airdropLaser + " не существует, измените его в конфиге."); //" The Airdrop falling mode " + airdropLaser + " does not exist, change it in the config."
        }

    }

    public static void removeLaser(Location loc) {

        for (Laser laser : laserList) {
            if (laser.getEnd().getX() == loc.getX() && laser.getEnd().getZ() == loc.getZ()) {
                laser.stop();
                laserList.remove(laser);
                break;
            }
        }

    }
}