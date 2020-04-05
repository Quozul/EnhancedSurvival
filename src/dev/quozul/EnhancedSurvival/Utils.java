package dev.quozul.EnhancedSurvival;

import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.List;

public class Utils {
    public static void spawnArmorStand(Location ploc, String name) {
        final ArmorStand as = ploc.getWorld().spawn(ploc, ArmorStand.class);
        final NBTEntity nbtent = new NBTEntity(as);

        nbtent.setString("CustomName", name);
        as.setGravity(false);
        as.setCanPickupItems(false);
        as.setCustomNameVisible(true);
        as.setVisible(false);
        as.setMarker(true);
    }

    public static void removeArmorStand(Location ploc) {
        final List<Entity> near = ploc.getWorld().getEntities();
        for (final Entity ent : near)
            if (ent.getLocation().distance(ploc) == 0.0)
                ent.remove();
    }
}
