package dev.quozul.EnhancedSurvival.Spawners;

import dev.quozul.EnhancedSurvival.Main;
import org.bukkit.Location;

public class RegisterSpawner {
    static Main plugin;
    
    public RegisterSpawner(final Main instance) {
        RegisterSpawner.plugin = instance;
    }
    
    public static void register(final Location loc, int exp) {
        final String location_string = String.format("%d;%d;%d", (int)loc.getX(), (int)loc.getY(), (int)loc.getZ());
        exp = ((exp != -1) ? exp : 100);
        RegisterSpawner.plugin.getConfig().set(String.format("%s.xp", location_string), (Object)Integer.toString(exp));
        RegisterSpawner.plugin.saveConfig();
    }
    
    public static int getExp(final Location loc) {
        final String location_string = String.format("%d;%d;%d", (int)loc.getX(), (int)loc.getY(), (int)loc.getZ());
        final String path = String.format("%s.xp", location_string);
        return RegisterSpawner.plugin.getConfig().isSet(path) ? RegisterSpawner.plugin.getConfig().getInt(path) : -1;
    }
    
    public static void remove(final Location loc) {
        final String location_string = String.format("%d;%d;%d", (int)loc.getX(), (int)loc.getY(), (int)loc.getZ());
        RegisterSpawner.plugin.getConfig().set(String.format("%s.xp", location_string), (Object)null);
    }
}
