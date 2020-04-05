package dev.quozul.EnhancedSurvival;

import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(String.format("§7[§6+§7]§r %s", e.getPlayer().getDisplayName()));

        String sql = "SELECT uuid FROM user WHERE uuid = ?";
        PreparedStatement stmt = null;
        try {
            stmt = Main.connection.prepareStatement(sql);
            stmt.setString(1, e.getPlayer().getUniqueId().toString());
            ResultSet results = stmt.executeQuery();

            // If player isn't in database, add it, else do nothing
            if (!results.next()) {
                sql = "INSERT INTO user(uuid) VALUES(?)";
                stmt = null;
                try {
                    stmt = Main.connection.prepareStatement(sql);
                    stmt.setString(1, e.getPlayer().getUniqueId().toString());
                    stmt.executeUpdate();
                } catch (SQLException err) {
                    err.printStackTrace();
                }
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }

        for (Player p : Bukkit.getOnlinePlayers())
            sendTablist(p, "§6§lPickaria", String.format("§7%d§6/§7%d §6joueurs", Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers()));
        //e.getPlayer().sendTitle("Bonjour " + e.getPlayer().getDisplayName(), "", 10, 40, 10);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        World world = e.getPlayer().getWorld();
        Player player = e.getPlayer();
        Location location = e.getPlayer().getLocation();

        world.playEffect(location.add(0, 0, 0), Effect.ENDER_SIGNAL, 100);

        e.setQuitMessage(String.format("§7[§6-§7]§r %s", e.getPlayer().getDisplayName()));
    }

    public void sendTablist(Player p, String Title, String subTitle) {
        if(Title == null) Title = "";
        if(subTitle == null) subTitle = "";

        IChatBaseComponent tabTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + Title+ "\"}");
        IChatBaseComponent tabSubTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subTitle + "\"}");

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        packet.header = tabTitle;
        packet.footer = tabSubTitle;

        try {
            Field field = packet.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(packet, tabSubTitle);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
        }
    }
}
