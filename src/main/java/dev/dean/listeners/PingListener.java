package dev.dean.listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import dev.dean.ServerConfiguratorPlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

/**
 * Listener for server list ping events.
 * Handles MOTD personalization, maintenance mode visuals, custom slot counts, and server icons.
 * Optimized for Folia 1.21.8 using modern non-deprecated Paper APIs.
 */
public class PingListener implements Listener {

    private final ServerConfiguratorPlus plugin;

    /**
     * Constructs a new PingListener.
     *
     * @param plugin The plugin instance.
     */
    public PingListener(ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles the PaperServerListPingEvent to customize the server's appearance in the server list.
     *
     * @param event The ping event.
     */
    @EventHandler
    public void onPing(@NotNull PaperServerListPingEvent event) {
        // 1. Maintenance Check
        if (plugin.getMaintenanceManager().isEnabled()) {
            event.motd(plugin.getMaintenanceManager().getMotd());
            event.setVersion("Maintenance");
            event.setProtocolVersion(-1);
            event.setMaxPlayers(0);
            return;
        }

        // 2. MOTD & Personalization
        String serverName = plugin.getConfig().getString("server-name", "&b&lServer");
        String dailyMotd = plugin.getMotdManager().getCurrentMotd();
        
        String fullMotd = serverName + "\n" + dailyMotd;
        
        if (fullMotd.contains("%player%")) {
            InetAddress address = event.getAddress();
            String ip = address != null ? address.getHostAddress() : "unknown";
            String playerName = plugin.getPlayerCacheManager().getPlayerName(ip);
            fullMotd = fullMotd.replace("%player%", playerName);
        }
        
        event.motd(LegacyComponentSerializer.legacyAmpersand().deserialize(fullMotd));

        // 3. Icons
        CachedServerIcon icon = plugin.getIconManager().getRandomIcon();
        if (icon != null) {
            event.setServerIcon(icon);
        }

        // 4. Slots
        boolean slotsEnabled = plugin.getConfig().getBoolean("slots.enabled", true);
        if (slotsEnabled) {
            String type = plugin.getConfig().getString("slots.type", "custom");
            if ("one-more".equalsIgnoreCase(type)) {
                event.setMaxPlayers(event.getNumPlayers() + 1);
            } else {
                event.setMaxPlayers(plugin.getConfig().getInt("slots.max", 100));
            }
            
            String customVersion = plugin.getConfig().getString("slots.custom-text");
            if (customVersion != null && !customVersion.isEmpty()) {
                Component versionComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(customVersion);
                event.setVersion(LegacyComponentSerializer.legacyAmpersand().serialize(versionComponent));
            }
        }
    }
}
