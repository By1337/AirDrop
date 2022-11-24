package org.by1337.airdrop.airdrop.util;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.by1337.airdrop.airdrop.AirDrop;
import org.jetbrains.annotations.NotNull;


import java.util.Objects;

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
        if(params.equalsIgnoreCase("start")){ //%airdrop_start%
            return plugin.Format(AirDrop.startIntervalChange);
        }
        if(params.equalsIgnoreCase("start2")){ //%airdrop_start2%
            return plugin.Format2(AirDrop.startIntervalChange);
        }
        return null;
    }
}