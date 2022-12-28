package org.by1337.airdrop.airdrop.Listener;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.by1337.airdrop.airdrop.AirDrop;
import org.by1337.airdrop.airdrop.Chest;
import org.by1337.airdrop.airdrop.util.Message;

import java.util.Objects;

import static org.by1337.airdrop.airdrop.AirDrop.instance;
import static org.by1337.airdrop.airdrop.util.CfgManager.Config.*;

public class Handler implements Listener {
    AirDrop plugin;

    public Handler(AirDrop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void PlayerClick(PlayerInteractEvent e) {
        Player pl = e.getPlayer();
        if (AirDrop.ChestList.isEmpty())
            return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            for (Chest chest : AirDrop.ChestList) {
                if (!chest.isEventActivity())
                    continue;
                if (chest.getAirLocation().equals(e.getClickedBlock().getLocation())) {
                    if (chest.isChestLocked()) {
                        Message.SendMsg(pl, getChestLocked());
                        SoundClickEvent(e.getClickedBlock().getLocation(), pl);
                        e.setCancelled(true);
                        return;
                    } else {
                        e.setCancelled(true);
                        pl.openInventory(chest.getChestInventory());
                        SoundClickOpenEvent(e.getClickedBlock().getLocation(), pl);
                        chest.PlayerOpenChest(e.getPlayer().getDisplayName());
                        return;
                    }

                }
            }
        }

    }
    private void SoundClickEvent(Location loc, Player pl) {
        ///SOUND-EFFECT//CLICK-EVENT///
        if (instance.getConfig().getBoolean("settings.effect-settings.sound-effect.click-event.play-sound")) {
            String sound = instance.getConfig().getString("settings.effect-settings.sound-effect.click-event.sound");
            String listeners = instance.getConfig().getString("settings.effect-settings.sound-effect.click-event.listeners");
            switch (Objects.requireNonNull(listeners)) {
                case "all":
                    try {
                        Message.PlaySoundAllOnline(Sound.valueOf(sound));
                    } catch (Exception e) {
                        Message.Error("sound-effect - click-event");
                        Message.Error(e.getLocalizedMessage());
                    }
                    break;

                case "near":
                    try {
                        Message.PlaySoundNear(Sound.valueOf(sound), 30, loc);
                    } catch (Exception e) {
                        Message.Error("sound-effect - click-event");
                        Message.Error(e.getLocalizedMessage());
                    }
                    break;
                case "player":
                    try {
                        Message.PlaySound(pl, Sound.valueOf(sound));
                    } catch (Exception e) {
                        Message.Error("sound-effect - click-event");
                        Message.Error(e.getLocalizedMessage());
                    }
                    break;
                default:
                    Message.Error("Unknown listener - " + listeners);
                    Message.Error("Неизвестный слушатель - " + listeners);
                    break;
            }
        }
    }

    private void SoundClickOpenEvent(Location loc, Player pl) {
        ///SOUND-EFFECT//CLICK-OPEN-EVENT///
        if (instance.getConfig().getBoolean("settings.effect-settings.sound-effect.click-open-event.play-sound")) {
            String sound = instance.getConfig().getString("settings.effect-settings.sound-effect.click-open-event.sound");
            String listeners = instance.getConfig().getString("settings.effect-settings.sound-effect.click-open-event.listeners");
            switch (Objects.requireNonNull(listeners)) {
                case "all":
                    try {
                        Message.PlaySoundAllOnline(Sound.valueOf(sound));
                    } catch (Exception e) {
                        Message.Error("sound-effect - click-open-event");
                        Message.Error(e.getLocalizedMessage());
                    }
                    break;

                case "near":
                    try {
                        Message.PlaySoundNear(Sound.valueOf(sound), 30, loc);
                    } catch (Exception e) {
                        Message.Error("sound-effect - click-open-event");
                        Message.Error(e.getLocalizedMessage());
                    }
                    break;
                case "player":
                    try {
                        Message.PlaySound(pl, Sound.valueOf(sound));
                    } catch (Exception e) {
                        Message.Error("sound-effect - click-open-event");
                        Message.Error(e.getLocalizedMessage());
                    }
                    break;
                default:
                    Message.Error("Unknown listener - " + listeners);
                    Message.Error("Неизвестный слушатель - " + listeners);
                    break;
            }
        }
    }
}
