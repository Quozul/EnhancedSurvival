package dev.quozul.EnhancedSurvival;

import dev.quozul.EnhancedSurvival.Mute.MuteCommand;
import dev.quozul.EnhancedSurvival.Mute.UnMuteCommand;
import dev.quozul.EnhancedSurvival.Spawners.SpawnEgg;
import dev.quozul.EnhancedSurvival.Spawners.Spawner;
import dev.quozul.EnhancedSurvival.Spawners.SpawnerGUI;
import dev.quozul.EnhancedSurvival.TempBan.TempBanCommand;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;

public class Main extends JavaPlugin {
    // DataBase vars.
    String username;
    String password;
    String url;

    // Connection vars
    public static Connection connection; //This is the variable we will use to connect to database

    public void onEnable() {
    	System.out.println("[EnhancedSurvival] Le plugin vient de démarrer !");
        final FileConfiguration config = this.getConfig();
        this.saveDefaultConfig();

        username = config.getString("database-username");
        password = config.getString("database-password");
        url = config.getString("database-url");

        this.getServer().getPluginManager().registerEvents(new Banners(), this);
        // this.getServer().getPluginManager().registerEvents(new Bed(), this);
        this.getServer().getPluginManager().registerEvents(new Death(this.getConfig()), this);

        this.getServer().getPluginManager().registerEvents(new Motd(), this);
        this.getServer().getPluginManager().registerEvents(new Anvil(), this);
        this.getServer().getPluginManager().registerEvents(new Spawner(), this);
        this.getServer().getPluginManager().registerEvents(new SpawnerGUI(), this);
        this.getServer().getPluginManager().registerEvents(new Chat(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new SecureChest(), this);
        this.getServer().getPluginManager().registerEvents(new Nitwit(), this);
        this.getServer().getPluginManager().registerEvents(new Fishing(), this);
        // this.getServer().getPluginManager().registerEvents(new Teleporters(), this);
        this.getServer().getPluginManager().registerEvents(new CoordinatesChorus(), this);
        this.getServer().getPluginManager().registerEvents(new SpawnEgg(), this);

        this.getCommand("mute").setExecutor(new MuteCommand());
        this.getCommand("unmute").setExecutor(new UnMuteCommand());
        this.getServer().getPluginManager().registerEvents(new MuteCommand(), this);

        this.getCommand("tempban").setExecutor(new TempBanCommand());
        this.getServer().getPluginManager().registerEvents(new TempBanCommand(), this);

        this.getCommand("claim").setExecutor(new ChunkClaim());
        this.getServer().getPluginManager().registerEvents(new ChunkClaim(), this);

        // Database connection
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("jdbc driver unavailable!");
            return;
        }
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Clock
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            World world = Bukkit.getServer().getWorld("world");
            long ticks = world.getTime();
            long hours = (ticks / 1000 + 6) % 24;
            long minutes = (ticks % 1000) * 60 / 1000;

            String time = String.format("%02d:%02d", hours, minutes);

            for (Player p : Bukkit.getOnlinePlayers())
                PlayerJoin.sendTablist(p, "§6§lPickaria", String.format("§7%d§6/§7%d §6joueurs\n§6TPS : §7%d/20\nHeure : %s", Bukkit.getOnlinePlayers().size() - 1, Bukkit.getMaxPlayers(), MinecraftServer.TPS, time));
        }, 0, 40);
    }
    
    public void onDisable() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    	System.out.println("[EnhancedSurvival] Le plugin vient de se stopper !");
    }
}
