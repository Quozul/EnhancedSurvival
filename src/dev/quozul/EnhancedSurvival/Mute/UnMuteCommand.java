package dev.quozul.EnhancedSurvival.Mute;

import dev.quozul.EnhancedSurvival.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UnMuteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) return false;

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.getDisplayName().equals(args[0])) {
                String sql = "UPDATE user SET mute_end = NOW() WHERE uuid = ?;";
                PreparedStatement stmt = null;
                try {
                    stmt = Main.connection.prepareStatement(sql);
                    stmt.setString(1, p.getUniqueId().toString());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                p.sendMessage("§6Vous avez de nouveau la permission de parler !");
                return true;
            }
        }

        return false;
    }
}
