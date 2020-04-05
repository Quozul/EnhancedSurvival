package dev.quozul.EnhancedSurvival;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Nitwit implements Listener {
    private ItemStack getRandomItemStack() {
        boolean haveRecipe;
        ItemStack itemStack;
        float hardness = 0;

        do {
            Material material = Material.values()[new Random().nextInt(Material.values().length)];
            if (material.isBlock()) hardness = material.getHardness();
            else hardness = 1;

            itemStack = new ItemStack(material);
            haveRecipe = Bukkit.getServer().getRecipesFor(itemStack).size() > 0;
        } while (!haveRecipe || hardness <= 0);

        return itemStack;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getType() == EntityType.VILLAGER) {
            Villager villager = (Villager)e.getRightClicked();

            Villager.Profession profession = villager.getProfession();
            if (profession == Villager.Profession.NITWIT) {
                int tradeCount = villager.getRecipes().size();

                if (tradeCount == 0) {
                    List<MerchantRecipe> recipes = new ArrayList<>();
                    //int recipeCount = (int)Math.floor(Math.random() * 4);
                    int recipeCount = 100;

                    for (int i = 0; i < recipeCount; i++) {
                        MerchantRecipe recipe = new MerchantRecipe(getRandomItemStack(), 1);
                        recipe.addIngredient(getRandomItemStack());

                        if (Math.random() > .5)
                            recipe.addIngredient(getRandomItemStack());

                        recipes.add(recipe);
                    }
                    villager.setRecipes(recipes);
                }
            }
        }
    }
}
