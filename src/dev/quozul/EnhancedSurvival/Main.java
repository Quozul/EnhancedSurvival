package dev.quozul.EnhancedSurvival;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public void onEnable() {
        final FileConfiguration config = this.getConfig();
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(new Banners(), this);
        // this.getServer().getPluginManager().registerEvents((Listener)new Bed(), this);
        this.getServer().getPluginManager().registerEvents(new Death(this.getConfig()), this);

        this.getServer().getPluginManager().registerEvents(new Anvil(), this);
        this.getServer().getPluginManager().registerEvents(new Spawner(), this);
        this.getServer().getPluginManager().registerEvents(new SpawnerGUI(), this);
    }
    
    public void onDisable() {
    }
}
