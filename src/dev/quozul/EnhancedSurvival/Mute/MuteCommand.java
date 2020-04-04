package dev.quozul.EnhancedSurvival.Mute;

import dev.quozul.EnhancedSurvival.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class MuteCommand implements CommandExecutor, Listener {
    public boolean isMuted(Player player) {
        String sql = "SELECT mute_end FROM user WHERE uuid = ?";
        PreparedStatement stmt = null;
        try {
            stmt = Main.connection.prepareStatement(sql);
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                Timestamp cur_time = new Timestamp(System.currentTimeMillis());
                Timestamp mute_end = results.getTimestamp(1);
                int remaining_mute = mute_end.compareTo(cur_time);
                if (remaining_mute > 0) {
                    return true;
                }
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) return false;

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.getDisplayName().equals(args[0])) {
                if (isMuted(p)) {
                    sender.sendMessage("§7Ce joueur est déjà en sourdine !");
                    return true;
                }

                // Get mute time
                String unit = args[1].substring(args[1].length() - 1);
                int time = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
                switch (unit) {
                    case "m": time *= 60; break;
                    case "h": time *= 3600; break;
                    case "j": time *= 86400; break;
                }

                time *= 1000;

                StringBuilder reason = new StringBuilder();
                for (int i = 2; i < args.length; i++)
                    reason.append(args[i]).append(' ');

                // Insert muted player into database
                String sql = "UPDATE user SET mute_end = ? WHERE uuid = ?;";
                PreparedStatement stmt = null;
                try {
                    stmt = Main.connection.prepareStatement(sql);
                    stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis() + time));
                    stmt.setString(2, p.getUniqueId().toString());
                    stmt.executeUpdate();

                    Bukkit.getServer().broadcastMessage(String.format("§c%s a été mis en sourdine pendant §6%s§c%s", args[0], args[1], !reason.equals("") ? String.format(" pour la raison\n§r§7%s", reason) : ""));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return true;
            }
        }

        return false;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (isMuted(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§7Désolé, vous n'avez pas la permission de parler pour le moment.");
        }
    }
}
