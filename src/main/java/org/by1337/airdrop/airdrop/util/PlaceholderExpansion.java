package org.by1337.airdrop.airdrop.util;

import org.bukkit.OfflinePlayer;
import org.by1337.airdrop.airdrop.AirDrop;
import org.by1337.airdrop.airdrop.AirRegion;
import org.jetbrains.annotations.NotNull;

import static org.by1337.airdrop.airdrop.util.CfgManager.Config.*;

public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {

    private final AirDrop plugin;

    public PlaceholderExpansion(AirDrop plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "By1337";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "AirDrop";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
//        if (params.equalsIgnoreCase("start")) { //%airdrop_start%
//            if (AirDrop.startDelay == 0)
//                return getPapiEventIsActivity();
//            return plugin.Format(AirDrop.startDelay);
//        }
//        if (params.equalsIgnoreCase("start2")) { //%airdrop_start2%
//            return plugin.Format2(AirDrop.startDelay);
//        }
//        if (params.equalsIgnoreCase("poz")) { //%airdrop_poz%
//            if (AirRegion.eventActivity)
//                return Message.MessageBuilder(getPapiPoz()).replace("{x}", "" + getAirLocation().getX()).replace("{y}", "" + getAirLocation().getY()).replace("{z}", "" + getAirLocation().getZ());
//            return getPapiPozNone();
//        }
//        if (params.equalsIgnoreCase("locked")) { //%airdrop_locked%
//            if (AirRegion.eventActivity) {
//                if (AirDrop.chestLockedDelay == 0)
//                    return getPapiChestUnlocked();
//                else
//                    return plugin.Format(AirDrop.chestLockedDelay);
//            }
//            return getPapiPozNone();
//        }
//        if (params.equalsIgnoreCase("locked2")) { //%airdrop_locked2%
//            if (AirRegion.eventActivity) {
//                if (AirDrop.chestLockedDelay == 0)
//                    return getPapiChestUnlocked();
//                else
//                    return plugin.Format2(AirDrop.chestLockedDelay);
//            }
//            return getPapiPozNone();
//        }
        return null;
    }
}