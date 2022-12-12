package org.by1337.airdrop.airdrop;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;


import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.by1337.airdrop.airdrop.util.Message;

import static org.by1337.airdrop.airdrop.util.CfgManager.Config.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.sk89q.worldguard.protection.flags.StateFlag.State.*;
import static org.by1337.airdrop.airdrop.util.GetRandomItem.GetItem;
import static org.by1337.airdrop.airdrop.util.GetRandomItem.Sort;

public class AirRegion {

    public static Location RndLoc(String rgName, Location location, double size, World world, int spawnMin, int spawnMax) {
        if (location != null)
            return location;
        double x = ThreadLocalRandom.current().nextLong(spawnMin, spawnMax);
        double y = 100.0;
        double z = ThreadLocalRandom.current().nextLong(spawnMin, spawnMax);
        Location loc1 = new Location(world, x, y, z);
        double y2 = Objects.requireNonNull(loc1.getWorld()).getHighestBlockAt(loc1).getLocation().getY();
        Location loc = new Location(world, x, y2 + 1, z);

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null){
            Message.Error("Not found WorldGuard! WorldGuard check is off!");
            return loc;
        }


        com.sk89q.worldedit.util.Location WGlocation = BukkitAdapter.adapt(loc);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(WGlocation);

        ProtectedCuboidRegion rg = GetProtectedCuboidRegion(loc, size, rgName);
        if (!RegionOverlap(BukkitAdapter.adapt(world), rg))
            return null;
        if (!set.getRegions().isEmpty())
            return null;
        if (getBlackList().contains(new Location(world, x, y2, z).getBlock().getType().toString()))
            return null;
        return loc;
    }

    public static boolean RegionOverlap(com.sk89q.worldedit.world.World world, ProtectedCuboidRegion region) {

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(world);
        assert regions != null;

        Map<String, ProtectedRegion> rg = regions.getRegions();

        List<ProtectedRegion> candidates = new ArrayList<>(rg.values());
        List<ProtectedRegion> overlapping = region.getIntersectingRegions(candidates);
        return overlapping.size() == 0;

    }

    public static void RemoveRegion(String rgName, World world) {
       // World world = getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));
        assert regions != null;
        if (regions.hasRegion(rgName))
            regions.removeRegion(rgName, RemovalStrategy.REMOVE_CHILDREN);

    }

    public static ProtectedCuboidRegion GetProtectedCuboidRegion(Location loc, double size, String rgName) {
        Location point1 = new Location(loc.getWorld(), loc.getX() + size, loc.getY() + size, loc.getZ() + size);
        Location point2 = new Location(loc.getWorld(), loc.getX() - size, loc.getY() - size, loc.getZ() - size);
        return new ProtectedCuboidRegion(rgName,
                BlockVector3.at(point1.getX(), point1.getY(), point1.getZ()),
                BlockVector3.at(point2.getX(), point2.getY(), point2.getZ()));
    }

    public static void SetRegion(String rgName, Location loc, double size) {
        ProtectedCuboidRegion rg = GetProtectedCuboidRegion(loc, size, rgName);
        World world = loc.getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));

        assert regions != null;
        for (String key : AirDrop.instance.getConfig().getConfigurationSection("settings.world-guard-flags").getKeys(false)) {
            // Flag<?> flag = WorldGuard.getInstance().getFlagRegistry().get(key);
            try {
                String stateFlag = AirDrop.instance.getConfig().getString("settings.world-guard-flags." + key);
                if (stateFlag.equalsIgnoreCase("allow"))
                    rg.setFlag(new StateFlag(key, true), StateFlag.State.ALLOW);
                else
                    rg.setFlag(new StateFlag(key, true), DENY);
            }catch (Exception e){
                Message.Error(e.getLocalizedMessage());
            }

        }
        rg.setFlag(Flags.PVP, ALLOW);
        rg.setFlag(Flags.USE, ALLOW);
        rg.setFlag(Flags.CHEST_ACCESS, ALLOW);
        rg.setFlag(Flags.CREEPER_EXPLOSION, DENY);
        rg.setFlag(Flags.TNT, DENY);
        rg.setFlag(Flags.FIRE_SPREAD, DENY);
        rg.setFlag(Flags.LAVA_FIRE, DENY);
        rg.setFlag(Flags.OTHER_EXPLOSION, DENY);
        regions.addRegion(rg);
    }

}
