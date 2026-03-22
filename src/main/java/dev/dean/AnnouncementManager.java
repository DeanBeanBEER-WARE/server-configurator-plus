package dev.dean;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Manages automated chat announcements.
 * Handles loading messages from configuration and broadcasting them periodically.
 * Optimized for Folia's multi-threaded environment using the GlobalRegionScheduler.
 */
public class AnnouncementManager {

    private final ServerConfiguratorPlus plugin;
    private List<String> messages;
    private int interval; // in minutes
    private boolean enabled;
    private int currentIndex = 0;
    private ScheduledTask announcementTask;

    /**
     * Constructs a new AnnouncementManager.
     *
     * @param plugin The plugin instance.
     */
    public AnnouncementManager(@NotNull ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
        loadConfig();
        startAnnouncements();
    }

    /**
     * Loads announcement data from the configuration and restarts the announcement task.
     */
    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        this.enabled = config.getBoolean("announcements.enabled", true);
        this.interval = config.getInt("announcements.interval", 10);
        this.messages = config.getStringList("announcements.messages");
        
        // Restart announcements with the new configuration
        startAnnouncements();
    }

    /**
     * Starts the automatic announcement task using Folia's GlobalRegionScheduler.
     */
    private void startAnnouncements() {
        stopAnnouncements();

        if (!enabled || messages == null || messages.isEmpty()) {
            return;
        }

        // Convert minutes to ticks (minutes * 60 seconds * 20 ticks)
        long ticks = interval * 60 * 20L;
        
        announcementTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> broadcastNext(), ticks, ticks);
    }

    /**
     * Stops the announcement task.
     */
    public void stopAnnouncements() {
        if (announcementTask != null) {
            announcementTask.cancel();
            announcementTask = null;
        }
    }

    /**
     * Broadcasts the next message in the list to all players.
     */
    private void broadcastNext() {
        if (messages == null || messages.isEmpty()) return;

        String message = messages.get(currentIndex);
        plugin.getServer().broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize(message));

        currentIndex = (currentIndex + 1) % messages.size();
    }
}
