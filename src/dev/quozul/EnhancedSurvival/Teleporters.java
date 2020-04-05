package dev.quozul.EnhancedSurvival;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.EndGateway;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Teleporters implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType().toString().endsWith("BED") && e.getItem() != null && e.getItem().getType() == Material.ENDER_PEARL) {
            block.breakNaturally();
            block.setType(Material.END_GATEWAY);

            EndGateway endGateway = (EndGateway)block.getState();
            endGateway.setExitLocation(e.getPlayer().getBedSpawnLocation());
            endGateway.update();

            e.setCancelled(true);
        } else if (e.getAction() == Action.LEFT_CLICK_BLOCK && block.getType() == Material.END_GATEWAY) {
            block.breakNaturally();
        }
    }
}
