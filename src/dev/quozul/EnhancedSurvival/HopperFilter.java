package dev.quozul.EnhancedSurvival;

import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HopperFilter implements Listener {
    private int itemCount(Inventory inv) {
        ItemStack[] content = inv.getContents();
        int count = 0;

        for (ItemStack itemStack : content) if (itemStack != null && itemStack.getType() != Material.AIR) count++;

        return count;
    }

    private ItemStack getLastItem(Inventory inv) {
        ItemStack[] content = inv.getContents();

        for (ItemStack itemStack : content) if (itemStack != null) return itemStack;
        return null;
    }

    @EventHandler
    public void onHopperMove(InventoryMoveItemEvent e) {
        if (e.getDestination().getType() == InventoryType.HOPPER) {
            Hopper hopper = (Hopper) e.getDestination().getLocation().getBlock().getState();
            String hopperName = hopper.getCustomName();

            if (hopperName != null && hopperName.toLowerCase().matches("filter")) {
                Inventory inv = e.getDestination();
                ItemStack itemFilter = getLastItem(inv);
                if (itemFilter != null && e.getItem().getType() != itemFilter.getType()) e.setCancelled(true);
            }
        }

        if (e.getSource().getType() == InventoryType.HOPPER) {
            Hopper hopper = (Hopper) e.getSource().getLocation().getBlock().getState();
            String hopperName = hopper.getCustomName();

            if (hopperName != null && hopperName.toLowerCase().equals("filter"))
                if (itemCount(e.getSource()) <= 0) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHopperPickupItem(InventoryPickupItemEvent e) {
        if (e.getInventory().getType() == InventoryType.HOPPER) {
            Hopper hopper = (Hopper) e.getInventory().getLocation().getBlock().getState();
            String hopperName = hopper.getCustomName();

            if (hopperName != null && hopperName.toLowerCase().matches("filter")) {
                Inventory inv = e.getInventory();
                ItemStack itemFilter = inv.getItem(0);
                if (itemFilter == null || e.getItem().getItemStack().getType() != itemFilter.getType()) e.setCancelled(true);
            }
        }

    }
}
