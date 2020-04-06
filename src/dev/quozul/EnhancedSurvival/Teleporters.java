package dev.quozul.EnhancedSurvival;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.EndGateway;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class Teleporters implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null) return;
        Material blockType = block.getType();
        Location location = block.getLocation();
        World world = location.getWorld();
        ItemStack item = e.getItem();
        Player player = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && blockType.toString().endsWith("BED") && item != null && item.getType() == Material.ENDER_PEARL) {
            if (player.getBedSpawnLocation() == null) {
                player.sendMessage("§cVous n'avez pas de lit.");
                e.setCancelled(true);
                return;
            }

            block.breakNaturally();
            block.setType(Material.END_GATEWAY);

            // Utils.spawnArmorStand(block.getLocation().add(.5, 1, .5), String.format("{\"text\":\"Lit de %s\"}", e.getPlayer().getDisplayName()));

            EndGateway endGateway = (EndGateway)block.getState();
            endGateway.setExitLocation(player.getBedSpawnLocation());
            endGateway.setExactTeleport(true);
            endGateway.update();

            player.playSound(block.getLocation(), Sound.BLOCK_END_GATEWAY_SPAWN, 1, 1);
            world.spawnParticle(Particle.DRAGON_BREATH, location.add(.5, .5, .5), 100, .1, .1, .1);
            item.setAmount(item.getAmount() - 1);

            System.out.println(String.format("Portal created at location %.0f/%.0f/%.0f", location.getX(), location.getY(), location.getZ()));

            e.setCancelled(true);

        } else if (e.getAction() == Action.LEFT_CLICK_BLOCK && blockType == Material.END_GATEWAY &&
                // if player is in overworld
                world != null && world.getEnvironment() == World.Environment.NORMAL) {
            block.breakNaturally();
            world.dropItemNaturally(location, new ItemStack(Material.ENDER_PEARL));
            // Utils.removeArmorStand(block.getLocation().add(.5, 1, .5));
            player.playSound(location, Sound.BLOCK_END_GATEWAY_SPAWN, 1, 1);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            e.getFrom().add(e.getFrom().getDirection()).add(0, 1, 0).getBlock().breakNaturally();
            e.getTo().getWorld().dropItemNaturally(e.getTo(), new ItemStack(Material.ENDER_PEARL));
        }
    }
}
