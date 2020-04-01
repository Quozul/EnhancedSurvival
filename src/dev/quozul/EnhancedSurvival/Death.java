package dev.quozul.EnhancedSurvival;

import org.bukkit.entity.*;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.meowj.langutils.lang.LanguageHelper;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.Statistic;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

public class Death implements Listener {
    FileConfiguration conf;
    
    public Death(final FileConfiguration instance) {
        this.conf = instance;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerDeathEvent(final PlayerDeathEvent e) {
        final Player entity = e.getEntity();
        String cause = entity.getLastDamageCause().getCause().toString();
        final int deaths = entity.getStatistic(Statistic.DEATHS) + 1;
        final String name = entity.getDisplayName();

        String using = "";
        String death_message = "";
        String killer = "";

        if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent)entity.getLastDamageCause();
            if (nEvent.getDamager() instanceof Player)
                killer = ((Player)nEvent.getDamager()).getDisplayName();
            else
                killer = LanguageHelper.getEntityDisplayName(nEvent.getDamager(), "fr_FR");

            if (nEvent.getDamager() instanceof Arrow) {
                final Arrow arrow = (Arrow)nEvent.getDamager();
                cause = "ARROW";
                if (arrow.getShooter() instanceof Player) {
                    killer = ((Player)arrow.getShooter()).getDisplayName();
                    using = LanguageHelper.getItemDisplayName(((Player)arrow.getShooter()).getEquipment().getItemInMainHand(), "fr_FR");
                } else {
                    killer = LanguageHelper.getEntityName(((LivingEntity)arrow.getShooter()).getType(), "fr_FR");
                    System.out.println(killer);
                    using = LanguageHelper.getItemDisplayName(((LivingEntity) arrow.getShooter()).getEquipment().getItemInMainHand(), "fr_FR");
                }
            } else if (cause.equals("FALLING_BLOCK")) {
                killer = LanguageHelper.getItemDisplayName(new ItemStack(((FallingBlock)nEvent.getDamager()).getBlockData().getMaterial()), "fr_FR");
            } else if (((LivingEntity)nEvent.getDamager()).getEquipment().getItemInMainHand().getType() != Material.AIR) {
                using = LanguageHelper.getItemDisplayName(((LivingEntity)nEvent.getDamager()).getEquipment().getItemInMainHand(), "fr_FR");
            }

            if (using.equals("")) {
                death_message = this.conf.getString(cause) + this.conf.getString("by_message");
            } else {
                death_message = this.conf.getString(cause) + this.conf.getString("by_message") + this.conf.getString("using_message");
            }
        } else if (entity.getLastDamageCause() instanceof EntityDamageByBlockEvent && !cause.matches("LAVA|VOID")) {
            final EntityDamageByBlockEvent bEvent = (EntityDamageByBlockEvent)entity.getLastDamageCause();
            if (bEvent.getDamager().getBlockData().getMaterial().toString() == "SWEET_BERRY_BUSH")
                killer = "Buisson à baies sucrées";
            else
                killer = LanguageHelper.getItemDisplayName(new ItemStack(bEvent.getDamager().getBlockData().getMaterial()), "fr_FR");
            death_message = this.conf.getString(cause) + this.conf.getString("by_message");
        } else if (cause.equals("SUFFOCATION")) {
            killer = LanguageHelper.getItemDisplayName(new ItemStack(entity.getLocation().add(0.0, 1.0, 0.0).getBlock().getBlockData().getMaterial()), "fr_FR");
            death_message = this.conf.getString(cause) + this.conf.getString("in_message");
        } else {
            death_message = this.conf.getString(cause);
        }

        e.setDeathMessage(death_message.replace("[victim]", name).replace("[killer]", killer).replace("[using]", using).replace("[fall_dist]", Integer.toString((int)entity.getFallDistance())));
        Bukkit.broadcastMessage(this.conf.getString("death_counter_message").replace("[victim]", name).replace("[deaths]", Integer.toString(deaths)));
    }
}
