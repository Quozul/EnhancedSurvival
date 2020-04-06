package dev.quozul.EnhancedSurvival;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinatesChorus implements Listener {
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) {
            String coordinates = e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName();

            List<Double> coords = new ArrayList<>();

            String pattern = "([-]?[0-9.]*)";
            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);

            // Now create matcher object.
            Matcher m = r.matcher(coordinates);

            while (m.find()) {
                if (!m.group().equals("")) {
                    double num = Double.parseDouble(m.group());
                    coords.add(num);
                }
            }

            if (coords.size() == 2 || coords.size() == 3) {
                if (coords.size() == 3 && coords.get(1) < 0) {
                    e.getPlayer().sendMessage("§cCoordonnées invalides, la coordonnée Y est négative");
                    return;
                }

                Location location = null;
                if (coords.size() == 3) {
                    location = new Location(e.getFrom().getWorld(), coords.get(0), coords.get(1), coords.get(2));
                    if (e.getFrom().getWorld().getBlockAt(location).getType() != Material.AIR) {
                        e.getPlayer().sendMessage("§cCe lieu de téléportation n'est pas sécurisé !");
                        return;
                    }
                } else {
                    location = new Location(e.getFrom().getWorld(), coords.get(0), 0, coords.get(1));
                    int y = e.getFrom().getWorld().getHighestBlockYAt(location);
                    location.setY(y + 1);
                }

                if (location.getChunk().getInhabitedTime() >= 12000) {
                    e.setTo(location);
                    e.getPlayer().sendMessage(String.format("§6Téléportation !"));
                    e.getPlayer().playSound(location, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
                } else {
                    e.getPlayer().sendMessage("§cCe lieu de téléportation n'a pas été suffisamment utilisé !");
                }
            } else {
                e.getPlayer().sendMessage("§cCoordonnées invalides, il y a trop ou pas assez de valeurs");
            }
        }
    }
}
