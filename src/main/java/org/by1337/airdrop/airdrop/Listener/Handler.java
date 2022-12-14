package org.by1337.airdrop.airdrop.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.by1337.airdrop.airdrop.AirDrop;
import org.by1337.airdrop.airdrop.Chest;
import org.by1337.airdrop.airdrop.util.Message;
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
                    e.setCancelled(true);
                    return;
                } else {
                    e.setCancelled(true);
                    pl.openInventory(chest.getChestInventory());
                    chest.PlayerOpenChest(e.getPlayer().getDisplayName());
                    return;
                }

            }
        }
    }



    }
}
