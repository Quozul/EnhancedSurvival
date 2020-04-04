package dev.quozul.EnhancedSurvival;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class Motd implements Listener {
    @EventHandler
    public void onServerListPing(ServerListPingEvent e) {
        World world = Bukkit.getServer().getWorld("world");
        long ticks = world.getTime();
        long hours = (ticks / 1000 + 6) % 24;
        long minutes = (ticks % 1000) * 60 / 1000;

        String time = String.format("%02d:%02d", hours, minutes);
        e.setMotd("§r                    §k||§r    §6§lPickaria§r    §k||§r\n§r                      §6Heure :§7 " + time);

        e.setMaxPlayers(e.getNumPlayers() + 1);
    }
}
