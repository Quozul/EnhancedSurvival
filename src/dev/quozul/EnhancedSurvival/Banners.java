package dev.quozul.EnhancedSurvival;

import java.util.List;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.entity.ArmorStand;
import de.tr7zw.nbtapi.NBTTileEntity;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;

public class Banners implements Listener
{
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent e) {
        final Block block = e.getBlock();
        if (block.getType().toString().endsWith("BANNER")) {
            final NBTTileEntity tent = new NBTTileEntity(block.getState());
            final String name = tent.getString("CustomName");

            if (name != "") {
                Location ploc;
                if (block.getType().toString().endsWith("WALL_BANNER"))
                    ploc = block.getLocation().add(0.5, 0.75, 0.5);
                else
                    ploc = block.getLocation().add(0.5, 1.75, 0.5);

                final ArmorStand as = (ArmorStand)ploc.getWorld().spawn(ploc, (Class)ArmorStand.class);
                final NBTEntity nbtent = new NBTEntity((Entity)as);

                nbtent.setString("CustomName", name);
                as.setGravity(false);
                as.setCanPickupItems(false);
                as.setCustomNameVisible(true);
                as.setVisible(false);
                as.setMarker(true);
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent e) {
        final Block block = e.getBlock();
        if (block.getType().toString().endsWith("BANNER")) {
            Location location;
            if (block.getType().toString().endsWith("WALL_BANNER"))
                location = block.getLocation().add(0.5, 0.75, 0.5);
            else
                location = block.getLocation().add(0.5, 1.75, 0.5);

            final List<Entity> near = (List<Entity>)location.getWorld().getEntities();
            for (final Entity ent : near)
                if (ent.getLocation().distance(location) == 0.0)
                    ent.remove();
        }
    }
}
