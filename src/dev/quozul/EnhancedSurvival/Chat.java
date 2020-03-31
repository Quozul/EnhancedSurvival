package dev.quozul.EnhancedSurvival;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class Chat implements Listener {
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        e.setFormat("§7%1$s:§r %2$s");

        // Markdown format
        int i_start = message.indexOf("*");
        int i_end = message.indexOf("*", i_start);

        message = message.replaceAll("(\\*\\*)(.*?)\\1", "§l$2§r"); // bold
        message = message.replaceAll("(__)(.*?)\\1", "§n$2§r"); // underline
        message = message.replaceAll("(~~)(.*?)\\1", "§m$2§r"); // stroke
        message = message.replaceAll("(\\*|_)(.*?)\\1", "§o$2§r"); // emphasis
        message = message.replaceAll("&([0-9a-f])", "§$1"); // colors

        e.setMessage(message);

        // Player mention
        List<Player> playersToRemove = new ArrayList<>();

        for (Player p : e.getRecipients())
            if (message.contains(p.getDisplayName())) {
                playersToRemove.add(p);
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                p.sendMessage(String.format(e.getFormat(), e.getPlayer().getDisplayName(), message.replace(p.getDisplayName(), String.format("§6%s§r", p.getDisplayName()))));
            }

        for (Player p : playersToRemove)
            e.getRecipients().remove(p);
    }
}
