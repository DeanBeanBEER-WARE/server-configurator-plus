package dev.dean;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Manages the server's maintenance mode state and associated settings.
 * Handles loading configuration values and toggling the maintenance status.
 */
public class MaintenanceManager {

    private final ServerConfiguratorPlus plugin;
    private boolean enabled;
    private Component motd;
    private Component kickMessage;

    /**
     * Constructs a new MaintenanceManager.
     *
     * @param plugin The plugin instance.
     */
    public MaintenanceManager(ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    /**
     * Loads maintenance settings from the configuration.
     */
    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        this.enabled = config.getBoolean("maintenance.enabled", false);
        
        String motdString = config.getString("maintenance.motd", "&4&lMaintenance Mode");
        this.motd = LegacyComponentSerializer.legacyAmpersand().deserialize(motdString);
        
        String kickMessageString = config.getString("maintenance.kick-message", "&cServer is currently in maintenance mode.");
        this.kickMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(kickMessageString);
    }

    /**
     * Checks if maintenance mode is currently enabled.
     *
     * @return true if enabled, false otherwise.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Toggles the maintenance mode state and saves it to the configuration.
     * <p>
     * Before saving, the configuration is reloaded from disk to ensure any manual
     * changes made by the user while the server is running are not accidentally overwritten.
     * </p>
     *
     * @return The new state of maintenance mode.
     */
    public boolean toggleMaintenance() {
        this.enabled = !this.enabled;
        // Reload config first to prevent overwriting pending manual changes on disk
        plugin.reloadConfig();
        plugin.getConfig().set("maintenance.enabled", this.enabled);
        plugin.saveConfig();
        return this.enabled;
    }

    /**
     * Gets the MOTD shown during maintenance.
     *
     * @return The maintenance MOTD as a Component.
     */
    public Component getMotd() {
        return motd;
    }

    /**
     * Gets the kick message shown to players when maintenance is active.
     *
     * @return The kick message as a Component.
     */
    public Component getKickMessage() {
        return kickMessage;
    }
}
