package dev.quozul.EnhancedSurvival.TempBan;

import dev.quozul.EnhancedSurvival.Main;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Locale;

public class TempBanCommand implements CommandExecutor, Listener {
    public boolean isBanned(Player player) {
        String sql = "SELECT ban_end FROM user WHERE uuid = ?";
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

        Player player = null;
        for (Player p : Bukkit.getServer().getOnlinePlayers())
            if (p.getDisplayName().equals(args[0]))
                player = p;

        if (player == null) return false;

        // Get ban time
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

        Date d = new Date(System.currentTimeMillis() + time);

        Bukkit.getServer().broadcastMessage(String.format("§c%s a été banni pendant §6%s§c%s", args[0], args[1], !String.valueOf(reason).equals("") ? String.format(" pour la raison\n§r§7%s", String.valueOf(reason)) : ""));
        player.kickPlayer("Vous avez été banni du serveur, reconnectez-vous pour plus d'informations.");
        Bukkit.getBanList(BanList.Type.NAME).addBan(args[0], String.valueOf(reason), d, sender.getName());

        return true;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        BanEntry banEntry = Bukkit.getBanList(BanList.Type.NAME).getBanEntry(e.getName());
        if (banEntry != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMMM yyyy HH:mm:ss", new Locale("fr", "FR"));
            String date = dateFormat.format(banEntry.getExpiration());
            System.out.println(date);

            long diff = banEntry.getExpiration().getTime() - new Date(System.currentTimeMillis()).getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            String reason = String.format("§7Tu as été banni du serveur pour la raison :\n§6%s\n\n§7Le bannissement arrive à expiration le :\n§6%s§7 (dans %d jours %02dh %02dm %02ds)§r",
                    banEntry.getReason(), date, diffDays, diffHours, diffMinutes, diffSeconds);

            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, reason);
        }
    }
}
