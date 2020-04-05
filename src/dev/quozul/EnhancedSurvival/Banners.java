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

                Utils.spawnArmorStand(ploc, name);
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

            Utils.removeArmorStand(location);
        }
    }
}
