package org.by1337.airdrop.airdrop.util;

import org.bukkit.OfflinePlayer;
import org.by1337.airdrop.airdrop.AirDrop;
import org.by1337.airdrop.airdrop.Chest;
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
    public String onRequest(OfflinePlayer player, String params) {// %airdrop_default_locked_format3%
        for (Chest chest : AirDrop.ChestList) {
            if (params.contains(chest.getChestName())) {
                if (params.contains("start")) {
                    if (chest.getStartDelay() == 0)
                        return getPapiEventIsActivity();
                    if (params.contains("format3"))
                        return AirDrop.Format3(chest.getStartDelay());
                    if (params.contains("format2"))
                        return AirDrop.Format2(chest.getStartDelay());
                    if (params.contains("format"))
                        return AirDrop.Format(chest.getStartDelay());
                }
                if (params.contains("locked")) {
                    if (chest.getChestLockedDelay() == 0)
                        return getPapiChestUnlocked();

                    if (params.contains("format3"))
                        return AirDrop.Format3(chest.getChestLockedDelay());

                    if (params.contains("format2"))
                        return AirDrop.Format2(chest.getChestLockedDelay());
                    if (params.contains("format"))
                        return AirDrop.Format(chest.getChestLockedDelay());
                }
                if (params.contains("stop")) {
                    if (chest.getStopEventDelay() == 0)
                        return getPapiEventEnded();


                    if (params.contains("format3"))
                        return AirDrop.Format3(chest.getStopEventDelay());
                    if (params.contains("format2"))
                        return AirDrop.Format2(chest.getStopEventDelay());
                    if (params.contains("format"))
                        return AirDrop.Format(chest.getStopEventDelay());
                }
                if (params.contains("poz")) {
                    if (chest.isEventActivity())
                        return Message.MessageBuilder(getPapiPoz()).replace("{x}", "" + chest.getAirLocation().getX()).replace("{y}", "" + chest.getAirLocation().getY()).replace("{z}", "" + chest.getAirLocation().getZ());
                    return getPapiPozNone();
                }
            }
        }
        return null;
    }
}