package dev.quozul.EnhancedSurvival;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.Material;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.Listener;

import java.util.Map;

public class Anvil implements Listener {
    @EventHandler
    public void onAnvilEvent(final PrepareAnvilEvent e) {
        final ItemStack[] anvil_items = e.getInventory().getContents();
        if (anvil_items.length < 2)
            return;

        final ItemStack item1 = anvil_items[0];
        final ItemStack item2 = anvil_items[1];

        if (item1 == null || item2 == null)
            return;

        e.getInventory().setMaximumRepairCost(e.getInventory().getRepairCost() + 1);
        ((Player)e.getView().getPlayer()).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("§7Le coût de réparation de cet objet est de §r§6§l%s §r§7niveaux.", e.getInventory().getRepairCost())));

        Map<Enchantment, Integer> enchant1 = null;
        Map<Enchantment, Integer> enchant2 = null;

        if (item1.getType() == Material.ENCHANTED_BOOK) enchant1 = ((EnchantmentStorageMeta)item1.getItemMeta()).getStoredEnchants();
        else enchant1 = item1.getEnchantments();

        if (item2.getType() == Material.ENCHANTED_BOOK) enchant2 = ((EnchantmentStorageMeta)item2.getItemMeta()).getStoredEnchants();
        else enchant2 = item2.getEnchantments();

        // Enchantment calculation
        final ItemStack result = e.getResult();

        for (Map.Entry<Enchantment, Integer> enchant : enchant2.entrySet()) {
            int level = enchant.getValue();
            // If enchant is on both items and is same level
            if (enchant1.containsKey(enchant.getKey()) && enchant1.get(enchant.getKey()) == level) level++;

            if (level > 10) level = 10;

            if (result.getType() == Material.ENCHANTED_BOOK) {
                final EnchantmentStorageMeta book_meta = (EnchantmentStorageMeta)result.getItemMeta();
                book_meta.addStoredEnchant(enchant.getKey(), level, true);
                result.setItemMeta(book_meta);
            } else {
                result.addUnsafeEnchantment(enchant.getKey(), level);
            }
        }

        e.setResult(result);
    }
}
