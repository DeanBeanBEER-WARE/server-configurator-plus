package dev.dean;

import dev.dean.commands.*;
import dev.dean.listeners.JoinListener;
import dev.dean.listeners.MuteListener;
import dev.dean.listeners.PingListener;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * Main plugin class for ServerConfiguratorPlus.
 * Optimized for Folia 1.21.8 using stable command registration.
 */
public class ServerConfiguratorPlus extends JavaPlugin {

    private MotdManager motdManager;
    private PlayerCacheManager playerCacheManager;
    private IconManager iconManager;
    private MaintenanceManager maintenanceManager;
    private AnnouncementManager announcementManager;
    private MuteManager muteManager;
    private LuckPerms luckPerms;
    private ConsoleFilter consoleFilter;

    @Override
    public void onEnable() {
        try {
            // Initialize LuckPerms
            try {
                this.luckPerms = LuckPermsProvider.get();
            } catch (IllegalStateException e) {
                getLogger().warning("LuckPerms API not found! Hierarchy checks will be disabled.");
            }

            // Save default config only if it does not exist
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
            
            // Re-save config to write any missing default values
            saveConfig();
            
            this.motdManager = new MotdManager(this);
            this.playerCacheManager = new PlayerCacheManager(this);
            this.iconManager = new IconManager(this);
            this.maintenanceManager = new MaintenanceManager(this);
            this.announcementManager = new AnnouncementManager(this);
            this.muteManager = new MuteManager(this);

            // Setup Console Filter
            this.consoleFilter = new ConsoleFilter(this);
            this.consoleFilter.start();
            ((Logger) LogManager.getRootLogger()).addFilter(this.consoleFilter);

            getServer().getPluginManager().registerEvents(new PingListener(this), this);
            getServer().getPluginManager().registerEvents(new JoinListener(this), this);
            getServer().getPluginManager().registerEvents(new MuteListener(this), this);

            registerCommands();

            getLogger().info("ServerConfiguratorPlus has been enabled on Folia!");
        } catch (Exception e) {
            getLogger().severe("Failed to enable ServerConfiguratorPlus: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    /**
     * Registers all plugin commands using the stable Bukkit Command API.
     */
    private void registerCommands() {
        LocationCommand locationCommand = new LocationCommand();
        MuteCommand muteCommand = new MuteCommand(this);
        UnmuteCommand unmuteCommand = new UnmuteCommand(this);
        
        Objects.requireNonNull(getCommand("say")).setExecutor(new SayCommand(this));
        Objects.requireNonNull(getCommand("motd")).setExecutor(new MotdCommand(this));
        Objects.requireNonNull(getCommand("players")).setExecutor(new PlayersCommand());
        Objects.requireNonNull(getCommand("status")).setExecutor(new StatusCommand(this));
        Objects.requireNonNull(getCommand("scp")).setExecutor(new ScpCommand(this));
        Objects.requireNonNull(getCommand("scp")).setTabCompleter(new ScpTabCompleter());
        Objects.requireNonNull(getCommand("kill")).setExecutor(new KillCommand());
        Objects.requireNonNull(getCommand("dupe")).setExecutor(locationCommand);
        Objects.requireNonNull(getCommand("rules")).setExecutor(locationCommand);
        
        Objects.requireNonNull(getCommand("mute")).setExecutor(muteCommand);
        Objects.requireNonNull(getCommand("mute")).setTabCompleter(muteCommand);
        Objects.requireNonNull(getCommand("unmute")).setExecutor(unmuteCommand);
        Objects.requireNonNull(getCommand("unmute")).setTabCompleter(unmuteCommand);
    }

    @Override
    public void onDisable() {
        if (consoleFilter != null) {
            ((Logger) LogManager.getRootLogger()).get().removeFilter(consoleFilter);
            consoleFilter.stop();
        }
        if (motdManager != null) {
            motdManager.stopRotation();
        }
        if (announcementManager != null) {
            announcementManager.stopAnnouncements();
        }
        if (playerCacheManager != null) {
            playerCacheManager.saveCache();
        }
        if (muteManager != null) {
            muteManager.saveMutes();
        }
        getLogger().info("ServerConfiguratorPlus has been disabled!");
    }

    public MotdManager getMotdManager() {
        return motdManager;
    }

    public PlayerCacheManager getPlayerCacheManager() {
        return playerCacheManager;
    }

    public IconManager getIconManager() {
        return iconManager;
    }

    public MaintenanceManager getMaintenanceManager() {
        return maintenanceManager;
    }

    public AnnouncementManager getAnnouncementManager() {
        return announcementManager;
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    /**
     * Reloads all plugin configurations and managers.
     */
    public void reloadPluginConfig() {
        try {
            reloadConfig();
            
            if (motdManager != null) motdManager.loadConfig();
            if (iconManager != null) iconManager.loadIcons();
            if (announcementManager != null) announcementManager.loadConfig();
            if (maintenanceManager != null) maintenanceManager.loadConfig();
            if (muteManager != null) muteManager.loadMutes();
            
            getLogger().info("Plugin configuration fully reloaded!");
        } catch (Exception e) {
            getLogger().severe("Error during configuration reload: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
