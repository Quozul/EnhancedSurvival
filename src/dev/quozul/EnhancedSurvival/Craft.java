package dev.quozul.EnhancedSurvival;

import de.tr7zw.nbtapi.NBTCompound;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ShapedRecipe;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class Craft {
    static Main plugin;

    public Craft(final Main instance) {
        Craft.plugin = instance;
    }

    public static void main() {
        ItemStack spawner = new ItemStack(Material.SPAWNER, 1);
        final NBTItem spawner_nbt = new NBTItem(spawner);
        final NBTCompound BlockEntityTag = spawner_nbt.addCompound("BlockEntityTag");
        spawner = spawner_nbt.getItem();

        NamespacedKey ns_key = new NamespacedKey(plugin, "PickariaCraft");

        final ShapedRecipe spawner_recipe = new ShapedRecipe(ns_key, spawner);
        spawner_recipe.shape("III", "IWI", "III");
        spawner_recipe.setIngredient('I', Material.IRON_BARS);
        spawner_recipe.setIngredient('W', Material.NETHER_STAR);
        Bukkit.addRecipe((Recipe)spawner_recipe);
    }
}
