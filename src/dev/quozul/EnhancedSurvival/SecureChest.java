package dev.quozul.EnhancedSurvival;

import de.tr7zw.nbtapi.NBTTileEntity;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lockable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static dev.quozul.EnhancedSurvival.Utils.actionMessage;

public class SecureChest implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Lockable) {

            NBTTileEntity chest = new NBTTileEntity(e.getClickedBlock().getState());
            String lock = chest.getString("Lock");
            boolean isLocked = lock != null && !lock.equals("");

            ItemStack item = e.getItem();
            boolean sneak = e.getPlayer().isSneaking();

            String password = "";
            if (item != null && item.getItemMeta() != null)
                password = item.getItemMeta().getDisplayName();

            if (isLocked) {
                if (password.equals(lock)) {
                    if (sneak) {
                        e.setCancelled(true);
                        chest.setString("Lock", null);
                        actionMessage(e.getPlayer(), "§6§lLa clé a été retirée du conteneur.");
                    }
                } else {
                    e.setCancelled(true);
                    actionMessage(e.getPlayer(), "§c§lCe conteneur est vérouillé.");
                    e.getPlayer().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 1);
                }
            } else {
                if (sneak && !password.equals("")) {
                    e.setCancelled(true);
                    chest.setString("Lock", password);
                    actionMessage(e.getPlayer(), "§6§lLe conteneur a été vérouillé avec la clé: §r§7%s");
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        BlockState blockState = e.getBlock().getState();
        if (blockState instanceof Lockable) {
            NBTTileEntity chest = new NBTTileEntity(blockState);
            String lock = chest.getString("Lock");

            if (lock != null && !lock.equals("") && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
                actionMessage(e.getPlayer(), "§c§lCe conteneur est vérouillé.");
            }
        }
    }
}