package dev.quozul.EnhancedSurvival;

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

        EnchantmentStorageMeta enchant1 = null;
        EnchantmentStorageMeta enchant2 = null;

        if (item1.getType() == Material.ENCHANTED_BOOK)
            enchant1 = (EnchantmentStorageMeta)item1.getItemMeta();
        if (item2.getType() == Material.ENCHANTED_BOOK)
            enchant2 = (EnchantmentStorageMeta)item2.getItemMeta();

        final boolean item1_has_silk1 = item1.getEnchantments().containsKey(Enchantment.SILK_TOUCH) || enchant1 != null && enchant1.hasStoredEnchant(Enchantment.SILK_TOUCH);
        final boolean item2_has_silk1 = item2.getEnchantments().containsKey(Enchantment.SILK_TOUCH) || enchant2 != null && enchant2.hasStoredEnchant(Enchantment.SILK_TOUCH);
        final boolean item1_has_silk2 = item1.getEnchantmentLevel(Enchantment.SILK_TOUCH) == 2 || enchant1 != null && enchant1.getStoredEnchantLevel(Enchantment.SILK_TOUCH) == 2;
        final boolean item2_has_silk2 = item2.getEnchantmentLevel(Enchantment.SILK_TOUCH) == 2 || enchant2 != null && enchant2.getStoredEnchantLevel(Enchantment.SILK_TOUCH) == 2;
        final boolean both_silk = item1_has_silk1 && item2_has_silk1;

        // Silk touch 2
        if (both_silk || item1_has_silk2 || item2_has_silk2) {
            final ItemStack result = e.getResult();

            if (result.getType() == Material.ENCHANTED_BOOK) {
                final EnchantmentStorageMeta book_meta = (EnchantmentStorageMeta) result.getItemMeta();
                book_meta.addStoredEnchant(Enchantment.SILK_TOUCH, 2, true);
                result.setItemMeta(book_meta);
            } else
                result.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 2);

            e.setResult(result);
        // Other enchantments
        } else {
            final ItemStack result = e.getResult();

            Map<Enchantment, Integer> enchantments2;
            if (result.getType() == Material.ENCHANTED_BOOK) {
                enchantments2 = enchant2.getStoredEnchants();
            } else {
                enchantments2 = item2.getEnchantments();
            }

            for (Map.Entry<Enchantment, Integer> enchant : enchantments2.entrySet()) {
                int level = enchant.getValue();
                // If enchant is on both items
                if (enchant1 != null && enchant1.hasStoredEnchant(enchant.getKey()) || item1.getEnchantments().containsKey(enchant.getKey())) {
                    // If enchant is same level
                    if (enchant1 != null && enchant1.getStoredEnchantLevel(enchant.getKey()) == level || item1.getEnchantments().get(enchant.getKey()) == level)
                        level++;
                } else {
                    int flevel = enchant1.getStoredEnchantLevel(enchant.getKey());
                    if (enchant1 != null && flevel > level)
                        level = flevel;
                }

                if (result.getType() == Material.ENCHANTED_BOOK) {
                    final EnchantmentStorageMeta book_meta = (EnchantmentStorageMeta)result.getItemMeta();
                    book_meta.addStoredEnchant(enchant.getKey(), level, true);
                    result.setItemMeta(book_meta);
                } else
                    result.addUnsafeEnchantment(enchant.getKey(), level);
            }

            e.setResult(result);
        }
    }
}
