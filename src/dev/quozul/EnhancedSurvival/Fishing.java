package dev.quozul.EnhancedSurvival;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class Fishing implements Listener {

	@EventHandler
	public void onFish(PlayerFishEvent e) {
		
		if(e.getCaught() instanceof Item) {
			
			Item fished = (Item) e.getCaught();
			
			ItemStack inHand = e.getPlayer().getInventory().getItemInMainHand(); // get item held
			Map<Enchantment, Integer> ench = inHand.getEnchantments(); // get enchantment
			
			if(inHand.getType().equals(Material.FISHING_ROD) && (ench.containsKey(Enchantment.FIRE_ASPECT) || ench.containsKey(Enchantment.ARROW_FIRE))) {
				ItemStack COD = new ItemStack(Material.COD, 1);
				ItemStack SALMON = new ItemStack(Material.SALMON, 1);
				
				if(fished.getItemStack().equals(COD)) fished.setItemStack(new ItemStack(Material.COOKED_COD, 1));
				if(fished.getItemStack().equals(SALMON)) fished.setItemStack(new ItemStack(Material.COOKED_SALMON, 1));
			}
		}
	}

}
