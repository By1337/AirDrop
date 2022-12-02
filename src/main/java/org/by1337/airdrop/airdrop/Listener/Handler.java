package org.by1337.airdrop.airdrop.Listener;

//import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.by1337.airdrop.airdrop.AirDrop;
import org.by1337.airdrop.airdrop.util.Message;
import static org.by1337.airdrop.airdrop.util.CfgManager.Config.*;
import static org.by1337.airdrop.airdrop.AirSpawn.*;

public class Handler implements Listener {
    AirDrop plugin;

    public Handler(AirDrop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void PlayerClick(PlayerInteractEvent e) {
        Player pl = e.getPlayer();
        if(getAirLocation() != null){
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getLocation().equals(getAirLocation())) {
                    if(chestLocked){
                        Message.SendMsg(pl, getChestLocked());
                        e.setCancelled(true);
                    }
                }
            }
        }


    }
}
