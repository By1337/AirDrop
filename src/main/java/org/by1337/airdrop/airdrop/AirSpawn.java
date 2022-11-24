package org.by1337.airdrop.airdrop;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;

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

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static com.sk89q.worldguard.protection.flags.StateFlag.State.*;
import static org.by1337.airdrop.airdrop.AirDrop.instance;
import static org.by1337.airdrop.airdrop.util.Config.*;
import static org.by1337.airdrop.airdrop.util.GetRandomItem.GetItem;
import static org.by1337.airdrop.airdrop.util.GetRandomItem.Sort;

public class AirSpawn {
    private static Location airLocation;
    public static Location newLocation;
    public static boolean eventActivity = false;
    public static boolean chestLocked = false;
    private static final String regionName = "By1337RegionAirDrop";


    public static void Start() {
        chestLocked = true;
        if (newLocation == null) {
            Message.Warning("Заранее сгенерированная локация имеет значение null! Не критичная ошибка.");
            while (newLocation == null)
                RndLoc();
        } else
            setAirLocation(newLocation);
        getAirLocation().getBlock().setType(Material.CHEST);
        Sort();
        if (getAirLocation().getBlock().getState() instanceof Chest) {
            Chest chest = (Chest) getAirLocation().getBlock().getState();

            Inventory inv = chest.getBlockInventory();
            chest.getInventory().clear();
            AirSpawn.RemoveRegion();
            SetRegion();
            for (int x = 0; x < chest.getInventory().getSize(); x++) {
                ItemStack item = GetItem();
                if (item != null)
                    inv.setItem(x, item);
            }
            eventActivity = true;
        } else {
            Message.Error("No chest was found at the spawn site!");
        }
    }


    public static void End() {
        setAirLocation(null);
        newLocation = null;
        chestLocked = false;
        eventActivity = false;
    }

    public static void RndLoc() {
        if (newLocation != null)
            return;
        double x = ThreadLocalRandom.current().nextLong(getSpawnMin(), getSpawnMax());
        double y = 100.0;
        double z = ThreadLocalRandom.current().nextLong(getSpawnMin(), getSpawnMax());
        World world = getWorld();
        Location loc1 = new Location(world, x, y, z);
        double y2 = Objects.requireNonNull(loc1.getWorld()).getHighestBlockAt(loc1).getLocation().getY();
        Location loc = new Location(world, x, y2 + 1, z);

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null)
            Message.Error("Not found WorldGuard! WorldGuard check is off!");

        com.sk89q.worldedit.util.Location location = BukkitAdapter.adapt(loc);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);
        if (!set.getRegions().isEmpty())
            return;
        if (getBlackList().contains(new Location(world, x, y2, z).getBlock().getType().toString()))
            return;
        newLocation = loc;
    }

    public static void RemoveRegion() {
        World world = getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));
        assert regions != null;
        if(regions.hasRegion(regionName))
            regions.removeRegion(regionName, RemovalStrategy.REMOVE_CHILDREN);

    }

    public static void SetRegion() {
        double size = getRadiusProtectBlock();
        Location point1 = new Location(getAirLocation().getWorld(), getAirLocation().getX() + size, getAirLocation().getY() + size, getAirLocation().getZ() + size);
        Location point2 = new Location(getAirLocation().getWorld(), getAirLocation().getX() - size, getAirLocation().getY() - size, getAirLocation().getZ() - size);
        World world = getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));
        ProtectedCuboidRegion rg = new ProtectedCuboidRegion(regionName,
                BlockVector3.at(point1.getX(), point1.getY(), point1.getZ()),
                BlockVector3.at(point2.getX(), point2.getY(), point2.getZ()));

        assert regions != null;
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

    public static void setAirLocation(Location airLocation) {
        AirSpawn.airLocation = airLocation;
    }

    public static Location getAirLocation() {
        return airLocation;
    }
}
