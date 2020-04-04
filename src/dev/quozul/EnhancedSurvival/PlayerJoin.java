package dev.quozul.EnhancedSurvival;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(String.format("§7[§6+§7]§r %s", e.getPlayer().getDisplayName()));

        String sql = "INSERT INTO user(uuid) VALUES(?)";
        PreparedStatement stmt = null;
        try {
            stmt = Main.connection.prepareStatement(sql);
            stmt.setString(1, e.getPlayer().getUniqueId().toString());
            stmt.executeUpdate();
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        World world = e.getPlayer().getWorld();
        Player player = e.getPlayer();
        Location location = e.getPlayer().getLocation();

        world.playEffect(location.add(0, 1, 0), Effect.ENDER_SIGNAL, 100);

        e.setQuitMessage(String.format("§7[§6-§7]§r %s", e.getPlayer().getDisplayName()));
    }
}
