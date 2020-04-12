package dev.quozul.EnhancedSurvival;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

enum ChunkClaimed {
    CLAIMED,
    UNREGISTERED,
    UNCLAIMED,
    UNKNOWN
}

public class ChunkClaim implements CommandExecutor, Listener {
    public ChunkClaimed isClaimed(Chunk chunk) {
        if (Main.connection == null) return ChunkClaimed.UNKNOWN;

        String sql = "SELECT protected FROM chunk WHERE x = ? AND y = ?";
        PreparedStatement stmt = null;
        try {
            stmt = Main.connection.prepareStatement(sql);
            stmt.setInt(1, chunk.getX());
            stmt.setInt(2, chunk.getZ());
            ResultSet results = stmt.executeQuery();

            if (!results.next()) return ChunkClaimed.UNREGISTERED;

            if (results.getBoolean(1)) return ChunkClaimed.CLAIMED;
            else return ChunkClaimed.UNCLAIMED;
        } catch (SQLException err) {
            err.printStackTrace();
            return ChunkClaimed.UNKNOWN;
        }
    }

    public String getOwner(Chunk chunk) {
        if (Main.connection == null) return "";

        String sql = "SELECT user FROM chunk WHERE x = ? AND y = ?";
        PreparedStatement stmt = null;
        try {
            stmt = Main.connection.prepareStatement(sql);
            stmt.setInt(1, chunk.getX());
            stmt.setInt(2, chunk.getZ());
            ResultSet results = stmt.executeQuery();

            if (!results.next()) return "";
            else return results.getString(1);
        } catch (SQLException err) {
            err.printStackTrace();
            return "";
        }
    }

    public void registerChunk(Chunk chunk) {
        if (Main.connection == null) return;

        String sql = "INSERT INTO chunk (x, y) VALUES (?, ?)";
        PreparedStatement stmt = null;
        try {
            stmt = Main.connection.prepareStatement(sql);
            stmt.setInt(1, chunk.getX());
            stmt.setInt(2, chunk.getZ());
            stmt.executeUpdate();
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }

    public void setClaimed(Chunk chunk, Player player, boolean claimed) {
        if (Main.connection == null) return;

        String sql = "UPDATE chunk SET protected = ?, user = ? WHERE x = ? AND y = ?";
        PreparedStatement stmt = null;
        try {
            stmt = Main.connection.prepareStatement(sql);
            stmt.setBoolean(1, claimed);
            stmt.setString(2, player.getUniqueId().toString());
            stmt.setInt(3, chunk.getX());
            stmt.setInt(4, chunk.getZ());
            stmt.executeUpdate();
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        Player player = (Player)sender;
        Chunk chunk = player.getLocation().getChunk();

        if (isClaimed(chunk) == ChunkClaimed.UNREGISTERED) {
            player.sendMessage("Le chunk est nouveau, il vous appartiens désormais.");
            registerChunk(chunk);
            setClaimed(chunk, player, true);
        } else if (isClaimed(chunk) == ChunkClaimed.UNCLAIMED) {
            player.sendMessage("Le chunk vous appartiens désormais.");
            setClaimed(chunk, player, true);
        } else if (isClaimed(chunk) == ChunkClaimed.CLAIMED && getOwner(chunk).equals(player.getUniqueId().toString())) {
            player.sendMessage("Le chunk vous appartenait, il ne vous appartient plus désormais.");
            setClaimed(chunk, player, false);
        }

        player.sendMessage(String.valueOf(isClaimed(chunk)));
        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Chunk chunk = e.getBlockAgainst().getLocation().getChunk();

        if (isClaimed(chunk) == ChunkClaimed.CLAIMED && !getOwner(chunk).equals(e.getPlayer().getUniqueId().toString())) {
            e.getPlayer().sendMessage("§cCe territoire ne vous appartient pas.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Chunk chunk = e.getBlock().getLocation().getChunk();

        if (isClaimed(chunk) == ChunkClaimed.CLAIMED && !getOwner(chunk).equals(e.getPlayer().getUniqueId().toString())) {
            e.getPlayer().sendMessage("§cCe territoire ne vous appartient pas.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        Chunk chunk = e.getClickedBlock().getLocation().getChunk();

        if (isClaimed(chunk) == ChunkClaimed.CLAIMED && !getOwner(chunk).equals(e.getPlayer().getUniqueId().toString())) {
            e.getPlayer().sendMessage("§cCe territoire ne vous appartient pas.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        Chunk chunk = e.getRightClicked().getLocation().getChunk();

        if (isClaimed(chunk) == ChunkClaimed.CLAIMED && !getOwner(chunk).equals(e.getPlayer().getUniqueId().toString())) {
            e.getPlayer().sendMessage("§cCe territoire ne vous appartient pas.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Chunk chunkFrom = e.getFrom().getChunk();
        if (e.getTo() == null) return;
        Chunk chunkTo = e.getTo().getChunk();

        if (chunkFrom == chunkTo) return;

        ChunkClaimed previousClaimed = isClaimed(chunkFrom);
        ChunkClaimed nextClaimed = isClaimed(chunkTo);

        String previousOwner = getOwner(chunkFrom);
        String nextOwner = getOwner(chunkTo);

        if (!previousOwner.equals(nextOwner) || previousClaimed != nextClaimed) {
            if (nextClaimed == ChunkClaimed.CLAIMED) {
                player.sendTitle("§6Entrée chez", Bukkit.getOfflinePlayer(UUID.fromString(nextOwner)).getName(), 10, 40, 10);
            } else if (previousClaimed == ChunkClaimed.CLAIMED) {
                player.sendTitle("§6Sortie de chez", Bukkit.getOfflinePlayer(UUID.fromString(previousOwner)).getName(), 10, 40, 10);
            }
        }
    }
}
