package dev.quozul.EnhancedSurvival;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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

            // Utils.spawnArmorStand(block.getLocation().add(.5, 1, .5), String.format("{\"text\":\"Lit de %s\"}", e.getPlayer().getDisplayName()));

            EndGateway endGateway = (EndGateway)block.getState();
            endGateway.setExitLocation(e.getPlayer().getBedSpawnLocation());
            endGateway.setExactTeleport(true);
            endGateway.update();

            e.getPlayer().playSound(block.getLocation(), Sound.BLOCK_END_GATEWAY_SPAWN, 1, 1);
            block.getLocation().getWorld().spawnParticle(Particle.DRAGON_BREATH, block.getLocation().add(.5, .5, .5), 100, .1, .1, .1);
            e.getItem().setAmount(e.getItem().getAmount() - 1);

            System.out.println(String.format("Portal created at location %.0f/%.0f/%.0f", block.getLocation().getX(), block.getLocation().getY(), block.getLocation().getZ()));

            e.setCancelled(true);
        } else if (e.getAction() == Action.LEFT_CLICK_BLOCK && block.getType() == Material.END_GATEWAY) {
            block.breakNaturally();
            // Utils.removeArmorStand(block.getLocation().add(.5, 1, .5));
            e.getPlayer().playSound(block.getLocation(), Sound.BLOCK_END_GATEWAY_SPAWN, 1, 1);
        }
    }
}
