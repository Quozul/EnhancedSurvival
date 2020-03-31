package dev.quozul.EnhancedSurvival;

import de.tr7zw.nbtapi.NBTTileEntity;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lockable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class SecureChest implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock().getState() instanceof Lockable) {

            NBTTileEntity chest = new NBTTileEntity(e.getClickedBlock().getState());
            String lock = chest.getString("Lock");
            Boolean isLocked = lock != null && lock != "";

            ItemStack item = e.getItem();
            Boolean sneak = e.getPlayer().isSneaking();

            String password = "";
            if (item != null)
                password = e.getItem().getItemMeta().getDisplayName();

            if (isLocked) {
                if (password.equals(lock)) { // Always returns false for some reasons
                    if (sneak) {
                        e.setCancelled(true);
                        chest.setString("Lock", null);
                        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6§lLa clé a été retirée du conteneur."));
                    }
                } else {
                    e.setCancelled(true);
                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§c§lCe conteneur est vérouillé."));
                }
            } else {
                if (sneak) {
                    e.setCancelled(true);
                    chest.setString("Lock", password);
                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("§6§lLe conteneur a été vérouillé avec la clé: §r§7%s", password)));
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

            if (lock != null && lock != "" && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§c§lCe conteneur est vérouillé."));
            }
        }
    }
}
