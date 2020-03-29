package dev.quozul.EnhancedSurvival;

import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class Bed implements Listener {
    int sleeping;
    double server_percentage;
    String sleep_message;
    String sleep_count;
    
    public Bed() {
        this.sleeping = 0;
        this.server_percentage = 1.0;
        this.sleep_message = "§6%s§7 est parti dormir, il manque §8%d§7 joueurs pour passer la nuit";
        this.sleep_count = "§6%d§7 sur §6%d§7 joueurs dorment";
    }
    
    private void sleepActionBar() {
        final int needed = Math.max((int)Math.round(Bukkit.getOnlinePlayers().size() * this.server_percentage), 1);
        for (final Player p : Bukkit.getOnlinePlayers())
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(this.sleep_count, this.sleeping, needed)));
    }
    
    @EventHandler
    public void onPlayerEnterBed(final PlayerBedEnterEvent e) {
        if (e.isCancelled())
            return;

        this.sleeping = Math.max(this.sleeping + 1, 0);
        final int needed = Math.max((int)Math.round(Bukkit.getOnlinePlayers().size() * this.server_percentage), 1);
        Bukkit.broadcastMessage(String.format(this.sleep_message, e.getPlayer().getDisplayName(), needed - this.sleeping));
        this.sleepActionBar();
    }
    
    @EventHandler
    public void onPlayerLeaveBed(final PlayerBedLeaveEvent e) {
        this.sleeping = Math.max(this.sleeping - 1, 0);
        this.sleepActionBar();
    }
}
