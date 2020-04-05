package dev.quozul.EnhancedSurvival.Spawners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventHandler;
import de.tr7zw.nbtapi.NBTCompound;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Location;
import de.tr7zw.nbtapi.NBTItem;
import java.util.List;
import java.util.ArrayList;
import com.meowj.langutils.lang.LanguageHelper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;

public class Spawner implements Listener {
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.SPAWNER)
            return;

        final CreatureSpawner original_spawner = (CreatureSpawner)e.getBlock().getState();
        if (e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SILK_TOUCH) >= 2) {
            System.out.println("Spawner broken with silk touch 2");
            e.setExpToDrop(0);

            final Location l = e.getBlock().getLocation();
            final String spawn_type = original_spawner.getSpawnedType().getKey().toString();

            ItemStack spawner_item = new ItemStack(Material.SPAWNER);

            final String name = String.format("§eCage \u00e0 %s", LanguageHelper.getEntityName(original_spawner.getSpawnedType(), "fr_FR"));
            final ItemMeta spawner_meta = spawner_item.getItemMeta();

            spawner_meta.setDisplayName(name);

            final List<String> lore = new ArrayList<>();

            lore.add(0, String.format("§r§7Delay d'appatition: %d-%ds", original_spawner.getMinSpawnDelay() / 20, original_spawner.getMaxSpawnDelay() / 20));
            lore.add(1, String.format("§r§7Nombre d'apparition par cycle: %d", original_spawner.getSpawnCount()));
            lore.add(2, String.format("§r§7Nombre d'entit\u00e9s \u00e0 proximit\u00e9: %d", original_spawner.getMaxNearbyEntities()));
            spawner_meta.setLore(lore);
            spawner_item.setItemMeta(spawner_meta);

            final NBTItem spawner_nbt = new NBTItem(spawner_item);
            final NBTCompound BlockEntityTag = spawner_nbt.addCompound("BlockEntityTag");

            BlockEntityTag.addCompound("SpawnData").setString("id", spawn_type);
            BlockEntityTag.setInteger("SpawnCount", original_spawner.getSpawnCount());
            BlockEntityTag.setInteger("MaxNearbyEntities", original_spawner.getMaxNearbyEntities());
            BlockEntityTag.setInteger("Delay", original_spawner.getDelay());
            BlockEntityTag.setInteger("MinSpawnDelay", original_spawner.getMinSpawnDelay());
            BlockEntityTag.setInteger("MaxSpawnDelay", original_spawner.getMaxSpawnDelay());
            spawner_item = spawner_nbt.getItem();

            l.getWorld().dropItemNaturally(l, spawner_item);
        }
    }
    
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent e) {
        if (e.getBlock().getType() != Material.SPAWNER)
            return;

        final NBTItem spawner_item = new NBTItem(e.getItemInHand());
        final NBTCompound BlockEntityTag = spawner_item.getCompound("BlockEntityTag");
        final String item_spawn_type = BlockEntityTag.getCompound("SpawnData").getString("id");
        EntityType spawn_type = EntityType.UNKNOWN;

        if (item_spawn_type != null)
            spawn_type = EntityType.valueOf(item_spawn_type.replace("minecraft:", "").toUpperCase());

        final CreatureSpawner spawner = (CreatureSpawner)e.getBlock().getState();
        spawner.setSpawnedType(spawn_type);

        if (BlockEntityTag.hasKey("SpawnCount"))
            spawner.setSpawnCount((int)BlockEntityTag.getInteger("SpawnCount"));

        if (BlockEntityTag.hasKey("MaxNearbyEntities"))
            spawner.setMaxNearbyEntities((int)BlockEntityTag.getInteger("MaxNearbyEntities"));

        if (BlockEntityTag.hasKey("Delay"))
            spawner.setDelay((int)BlockEntityTag.getInteger("Delay"));

        if (BlockEntityTag.hasKey("MinSpawnDelay"))
            spawner.setMinSpawnDelay((int)BlockEntityTag.getInteger("MinSpawnDelay"));

        if (BlockEntityTag.hasKey("MaxSpawnDelay"))
            spawner.setMaxSpawnDelay((int)BlockEntityTag.getInteger("MaxSpawnDelay"));

        spawner.update();
    }
}
