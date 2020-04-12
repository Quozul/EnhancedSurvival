package dev.quozul.EnhancedSurvival.Spawners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnEgg implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() == EntityType.EGG && ((LivingEntity)e.getEntity()).getHealth() <= 1) {
            double chance = Math.random();
            //if (chance < .1) {
                Location location = e.getEntity().getLocation();
                String spawnEggName = e.getEntityType().toString() + "_SPAWN_EGG";
                Material material = Material.getMaterial(spawnEggName);

                if (material != null)
                    location.getWorld().dropItemNaturally(location, new ItemStack(material));
            //}

            e.setDamage(1);
        }
    }
}
