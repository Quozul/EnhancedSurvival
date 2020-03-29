package dev.quozul.EnhancedSurvival;

import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.Listener;

public class AdvancedSpawner implements Listener {
    @EventHandler
    public void onSpawnerActivated(final SpawnerSpawnEvent e) {
        final CreatureSpawner spawner = e.getSpawner();
        final Location loc = e.getSpawner().getLocation();
        final int exp = (RegisterSpawner.getExp(loc) != -1) ? (RegisterSpawner.getExp(loc) - 5) : 100;
        RegisterSpawner.register(loc, exp);
        ((LivingEntity)e.getEntity()).setMemory(MemoryKey.HOME, loc);
    }
    
    public Location blockInArea(final Material material, final Location center_location) {
        final Location top_location = center_location.add(1.0, 1.0, 1.0);
        for (int x = 0; x < 2; ++x)
            for (int y = 0; y < 2; ++y)
                for (int z = 0; z < 2; ++z) {
                    final Location loc = new Location(center_location.getWorld(), (double)x, (double)y, (double)z);
                    final Block block = loc.getBlock();
                    if (block.getType() == material)
                        return loc;
                }
        return new Location(center_location.getWorld(), 0.0, -1.0, 0.0);
    }
    
    @EventHandler
    public void onExpHit(final ExpBottleEvent e) {
        final Location location = e.getEntity().getLocation();
        final Location block_location = this.blockInArea(Material.SPAWNER, location);
        if (block_location == new Location(location.getWorld(), 0.0, -1.0, 0.0)) {
            return;
        }
        final Block block = block_location.getBlock();
        final Player player = (Player)e.getEntity().getShooter();
        final int exp = 0;
        player.sendMessage(String.format("§7Le spawner \u00e0 gagn\u00e9 §6%d§7 points d'exp\u00e9rience\nIl a maintenant §8%d§7 points d'exp", exp, (RegisterSpawner.getExp(block_location) != -1) ? RegisterSpawner.getExp(block_location) : 100));
    }
    
    @EventHandler
    public void onEntityDeath(final EntityDeathEvent e) {
        final Location spawner_location = (Location)e.getEntity().getMemory(MemoryKey.HOME);
        final int exp = (RegisterSpawner.getExp(spawner_location) != -1) ? (RegisterSpawner.getExp(spawner_location) + 10) : 100;
        RegisterSpawner.register(spawner_location, exp);
    }
}
