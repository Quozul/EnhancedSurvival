package dev.quozul.EnhancedSurvival;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public void onEnable() {
    	System.out.println("[EnhancedSurvival] Le plugin vient de démarrer !");
        final FileConfiguration config = this.getConfig();
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(new Banners(), this);
        // this.getServer().getPluginManager().registerEvents((Listener)new Bed(), this);
        this.getServer().getPluginManager().registerEvents(new Death(this.getConfig()), this);

        this.getServer().getPluginManager().registerEvents(new Anvil(), this);
        this.getServer().getPluginManager().registerEvents(new Spawner(), this);
        this.getServer().getPluginManager().registerEvents(new SpawnerGUI(), this);
        this.getServer().getPluginManager().registerEvents(new Chat(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new SecureChest(), this);
        this.getServer().getPluginManager().registerEvents(new Nitwit(), this);
    }
    
    public void onDisable() {
    	System.out.println("[EnhancedSurvival] Le plugin vient de se stopper !");
    }
}
