package dev.quozul.EnhancedSurvival.Spawners;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import com.meowj.langutils.lang.LanguageHelper;
import org.bukkit.entity.Player;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.Listener;

public class SpawnerGUI implements Listener {
    public void createnopen(final CreatureSpawner spawner, final Player player) {
        final Location loc = spawner.getLocation();
        final String entity_name = LanguageHelper.getEntityName(spawner.getSpawnedType(), "fr_FR");
        final double min_delay = spawner.getMinSpawnDelay() / 20.0;
        final double max_delay = spawner.getMaxSpawnDelay() / 20.0;
        final int spawn_count = spawner.getSpawnCount();
        final int max_nearby = spawner.getMaxNearbyEntities();
        final int required_player_range = spawner.getRequiredPlayerRange();

        final double delay_upgrade = 100.0 / ((min_delay + max_delay) / 2.0);
        final double spawn_count_upgrade = Math.pow(spawn_count, 2.0);
        final double max_nearby_upgrade = Math.pow(2.0, max_nearby / 2.0);
        final double required_player_range_upgrade = Math.pow(2.0, required_player_range / 2.0);

        final Inventory inv = Bukkit.createInventory(null, 9, String.format("§eCage à %s", entity_name));

        inv.setItem(0, this.createGuiItem("§r§5Informations", new ArrayList<>(Arrays.asList("§r§7Cage à §6" + entity_name, "§r§7Position:", "§r§7 - x: §6" + loc.getX(), "§r§7 - y: §6" + loc.getY(), "§r§7 - z: §6" + loc.getZ())), Material.SPAWNER, 1));
        inv.setItem(2, this.createGuiItem("§r§5Delay d'apparition", new ArrayList<>(Arrays.asList("§r§7Delay entre les apparitions", "§r§7Minimum: §6" + min_delay + "§7s", "§r§7Maximum: §6" + max_delay + "§7s", "", (delay_upgrade < 40.0) ? ("§r§7Prix pour -0.5 (en niveau d'exp): §6" + Math.ceil(delay_upgrade)) : "§r§cTrop cher!")), (delay_upgrade < 40.0) ? Material.REPEATER : Material.BARRIER, (int)(min_delay + max_delay) / 2));
        inv.setItem(4, this.createGuiItem("§r§5Apparitions par cycle", new ArrayList<>(Arrays.asList("§r§7Nombre d'apparition par cycle", "§r§7Actuellement: §6" + spawn_count, "", (spawn_count_upgrade < 40.0) ? ("§r§7Prix pour +1 (en niveau d'exp): §6" + Math.ceil(spawn_count_upgrade)) : "§r§cTrop cher!")), (spawn_count_upgrade < 40.0) ? Material.BLAZE_POWDER : Material.BARRIER, spawn_count));
        inv.setItem(6, this.createGuiItem("§r§5Entités à proximité", new ArrayList<>(Arrays.asList("§r§7Nombre maximum d'entités dans la zone d'apparition du spawner", "§r§7Actuellement: §6" + max_nearby, "", (max_nearby_upgrade < 40.0) ? ("§r§7Prix pour +1 (en niveau d'exp): §6" + Math.ceil(max_nearby_upgrade)) : "§r§cTrop cher!")), (max_nearby_upgrade < 40.0) ? Material.CREEPER_HEAD : Material.BARRIER, max_nearby));
        inv.setItem(8, this.createGuiItem("§r§5Rayon d'activation", new ArrayList<>(Arrays.asList("§r§7Distance requise du joueur pour le fonctionnement du spawner", "§r§7Actuellement: §6" + required_player_range, "", (required_player_range_upgrade < 40.0) ? ("§r§7Prix pour +1 (en niveau d'exp): §6" + Math.ceil(required_player_range_upgrade)) : "§r§cTrop cher!")), (required_player_range_upgrade < 40.0) ? Material.ARROW : Material.BARRIER, required_player_range));

        player.openInventory(inv);
    }
    
    public ItemStack createGuiItem(final String name, final ArrayList<String> desc, final Material mat, final int amount) {
        final ItemStack i = new ItemStack(mat, Math.max(amount, 1));
        final ItemMeta iMeta = i.getItemMeta();

        iMeta.setDisplayName(name);
        iMeta.setLore(desc);
        i.setItemMeta(iMeta);

        return i;
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getView().getTitle().contains("Cage \u00e0"))
            return;

        if (e.getClick().equals(ClickType.NUMBER_KEY))
            e.setCancelled(true);

        e.setCancelled(true);
        final Player p = (Player)e.getWhoClicked();
        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().equals(Material.AIR))
            return;

        final List<String> lore = e.getInventory().getItem(1).getItemMeta().getLore();
        final Location loc = new Location(p.getWorld(), Double.parseDouble(lore.get(2).substring(lore.get(2).lastIndexOf("§6") + 2)), Double.parseDouble(lore.get(3).substring(lore.get(3).lastIndexOf("§6") + 2)), Double.parseDouble(lore.get(4).substring(lore.get(4).lastIndexOf("§6") + 2)));
        final CreatureSpawner spawner = (CreatureSpawner)loc.getBlock().getState();
        final ItemStack clicked_item = e.getCurrentItem();
        final int player_level = p.getLevel();

        switch (clicked_item.getType()) {
            case REPEATER: {
                final double min_delay = spawner.getMinSpawnDelay();
                final double max_delay = spawner.getMaxSpawnDelay();
                final double delay_upgrade = 100.0 / ((min_delay / 20.0 + max_delay / 20.0) / 2.0);
                if (player_level >= delay_upgrade) {
                    p.setLevel(player_level - (int)Math.ceil(delay_upgrade));
                    spawner.setMinSpawnDelay(Math.max((int)min_delay - 10, 0));
                    spawner.setMaxSpawnDelay(Math.max((int)max_delay - 10, 0));
                    break;
                }
                break;
            }
            case BLAZE_POWDER: {
                final int spawn_count = spawner.getSpawnCount();
                final double spawn_count_upgrade = Math.pow(spawn_count, 2.0);
                if (player_level >= spawn_count_upgrade) {
                    p.setLevel(player_level - (int)Math.ceil(spawn_count_upgrade));
                    spawner.setSpawnCount(spawn_count + 1);
                    break;
                }
                break;
            }
            case CREEPER_HEAD: {
                final int max_nearby = spawner.getMaxNearbyEntities();
                final double max_nearby_upgrade = Math.pow(2.0, max_nearby / 2.0);
                if (player_level >= max_nearby_upgrade) {
                    p.setLevel(player_level - (int)Math.ceil(max_nearby_upgrade));
                    spawner.setMaxNearbyEntities(max_nearby + 1);
                    break;
                }
                break;
            }
            case ARROW: {
                final int required_player_range = spawner.getRequiredPlayerRange();
                final double required_player_range_upgrade = Math.pow(2.0, required_player_range / 2.0);
                if (player_level >= required_player_range_upgrade) {
                    p.setLevel(player_level - (int)Math.ceil(required_player_range_upgrade));
                    spawner.setMaxNearbyEntities(required_player_range + 1);
                    break;
                }
                break;
            }
        }
        spawner.update();
        this.createnopen(spawner, p);
    }
    
    @EventHandler
    public void onSpawnerClick(final PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getPlayer().isSneaking())
            return;

        if (e.getClickedBlock().getType() == Material.SPAWNER)
            this.createnopen((CreatureSpawner)e.getClickedBlock().getState(), e.getPlayer());
    }
    
    @EventHandler
    public void onBlockPlaced(final BlockPlaceEvent e) {
        if (e.getBlockAgainst().getType() == Material.SPAWNER && !e.getPlayer().isSneaking())
            e.setCancelled(true);
    }
}
