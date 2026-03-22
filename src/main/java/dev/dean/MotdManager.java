package dev.dean;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the rotating Message Of The Day (MOTD) system.
 * This class handles loading MOTDs from configuration and rotating them periodically.
 * Optimized for Folia's multi-threaded environment using the GlobalRegionScheduler.
 */
public class MotdManager {

    private final ServerConfiguratorPlus plugin;
    private List<String> motds;
    private int currentIndex;
    private ScheduledTask rotationTask;

    /**
     * Constructs a new MotdManager.
     *
     * @param plugin The plugin instance.
     */
    public MotdManager(@NotNull ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
        loadConfig();
        startRotation();
    }

    /**
     * Loads the MOTD data from the configuration.
     * This method reads the configuration file without modifying it.
     */
    public void loadConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        List<String> loadedMotds = config.getStringList("motds");
        if (loadedMotds.isEmpty()) {
            loadedMotds = new ArrayList<>();
            loadedMotds.add("&6Welcome to the &bSimple Server&6!");
            loadedMotds.add("&aCheck out our &lnew features&a!");
            config.set("motds", loadedMotds);
            plugin.saveConfig();
        }
        this.motds = loadedMotds;

        this.currentIndex = config.getInt("current-index", 0);
        if (this.currentIndex >= motds.size()) {
            this.currentIndex = 0;
        }
    }

    /**
     * Starts the automatic MOTD rotation task using Folia's GlobalRegionScheduler.
     */
    private void startRotation() {
        if (rotationTask != null) {
            rotationTask.cancel();
        }

        // Rotate every 5 minutes
        // 5 minutes * 60 seconds * 20 ticks = 6000 ticks
        long ticks = 5 * 60 * 20L;
        rotationTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> rotate(), ticks, ticks);
    }

    /**
     * Stops the rotation task and saves the current state.
     */
    public void stopRotation() {
        if (rotationTask != null) {
            rotationTask.cancel();
            rotationTask = null;
        }
        saveState();
    }

    /**
     * Rotates to the next MOTD in the list.
     */
    private void rotate() {
        if (motds.isEmpty()) return;

        currentIndex = (currentIndex + 1) % motds.size();
    }

    /**
     * Saves the current rotation state to the configuration.
     */
    private void saveState() {
        FileConfiguration config = plugin.getConfig();
        config.set("current-index", currentIndex);
        plugin.saveConfig();
    }

    /**
     * Gets the current active MOTD string.
     *
     * @return The current MOTD string with legacy color codes.
     */
    @NotNull
    public String getCurrentMotd() {
        if (motds.isEmpty()) return "A Minecraft Server";
        return motds.get(currentIndex);
    }
}
