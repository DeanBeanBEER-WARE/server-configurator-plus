package dev.dean.listeners;

import dev.dean.ServerConfiguratorPlus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

/**
 * Listener for player join and login events.
 * Handles IP-to-name caching and maintenance mode enforcement.
 */
public class JoinListener implements Listener {

    private final ServerConfiguratorPlus plugin;

    /**
     * Constructs a new JoinListener.
     *
     * @param plugin The plugin instance.
     */
    public JoinListener(ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
    }

    /**
     * Caches the player's name and IP address upon joining.
     *
     * @param event The player join event.
     */
    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        InetSocketAddress address = player.getAddress();
        if (address != null) {
            String ip = address.getAddress().getHostAddress();
            String name = player.getName();
            plugin.getPlayerCacheManager().updateCache(ip, name);
        }
    }

    /**
     * Prevents non-authorized players from logging in when maintenance mode is enabled.
     * Uses AsyncPlayerPreLoginEvent for Folia compatibility and modern API usage.
     *
     * @param event The async player pre-login event.
     */
    @EventHandler
    public void onPreLogin(@NotNull AsyncPlayerPreLoginEvent event) {
        if (plugin.getMaintenanceManager().isEnabled()) {
            // Note: Since we don't have the Player object yet, we can't check permissions easily.
            // However, we can check if the name is in the list of ops or if they have a bypass.
            // For simplicity in this maintenance mode, we disallow all via this event, 
            // but normally you would use the UUID to check against a whitelist/perm-cache.
            
            // Re-checking maintenance status
            if (plugin.getMaintenanceManager().isEnabled()) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, 
                        plugin.getMaintenanceManager().getKickMessage());
            }
        }
    }
}
