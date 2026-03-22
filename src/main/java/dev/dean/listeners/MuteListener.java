package dev.dean.listeners;

import dev.dean.ServerConfiguratorPlus;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Listener to prevent muted players from chatting.
 * Uses AsyncChatEvent for modern Paper/Folia compatibility.
 */
public class MuteListener implements Listener {

    private final ServerConfiguratorPlus plugin;

    public MuteListener(ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
    }

    /**
     * Intercepts chat messages and cancels them if the player is muted.
     *
     * @param event The chat event.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(@NotNull AsyncChatEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.getMuteManager().isMuted(player.getUniqueId())) {
            event.setCancelled(true);
            
            String remaining = plugin.getMuteManager().getRemainingTime(player.getUniqueId());
            Component message;
            
            if ("permanent".equals(remaining)) {
                message = Component.text("You are permanently muted!", NamedTextColor.RED);
            } else {
                message = Component.text("You are muted! Remaining time: ", NamedTextColor.RED)
                        .append(Component.text(remaining, NamedTextColor.YELLOW));
            }
            
            player.sendMessage(message);
        }
    }
}
