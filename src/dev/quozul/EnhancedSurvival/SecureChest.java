package dev.quozul.EnhancedSurvival;

import de.tr7zw.nbtapi.NBTTileEntity;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Lockable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;


public class SecureChest implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Lockable) {

            Chest chest = (Chest)e.getClickedBlock().getState();
            String lock = chest.getLock();
            boolean isLocked = chest.isLocked();

            ItemStack item = e.getItem();
            boolean sneak = e.getPlayer().isSneaking();

            String password = "";
            if (item != null && item.getItemMeta() != null)
                password = item.getItemMeta().getDisplayName();

            if (isLocked) {
                if (password.equals(lock)) {
                    if (sneak) {
                        e.setCancelled(true);
                        chest.setLock(null);
                        chest.update();
                        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6§lLa clé a été retirée du conteneur."));
                    }
                } else {
                    e.setCancelled(true);
                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§c§lCe conteneur est vérouillé."));
                    e.getPlayer().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 1);
                }
            } else {
                if (sneak && !password.equals("")) {
                    e.setCancelled(true);
                    chest.setLock(password);
                    chest.update();
                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("§6§lLe conteneur a été vérouillé avec la clé: §r§7%s", password)));
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        BlockState blockState = e.getBlock().getState();
        if (blockState instanceof Lockable) {
            Chest chest = (Chest)blockState;

            if (chest.isLocked() && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§c§lCe conteneur est vérouillé."));
            }
        }
    }
}
