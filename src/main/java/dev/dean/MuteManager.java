package dev.dean;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player mutes, including temporary and permanent stumbles.
 * Optimized for Folia and persistent storage.
 */
public class MuteManager {

    private final ServerConfiguratorPlus plugin;
    private final File muteFile;
    private FileConfiguration muteConfig;
    private final Map<UUID, Long> mutes = new HashMap<>();

    public MuteManager(ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
        this.muteFile = new File(plugin.getDataFolder(), "mutes.yml");
        loadMutes();
    }

    /**
     * Loads mutes from the mutes.yml file.
     */
    public void loadMutes() {
        if (!muteFile.exists()) {
            plugin.saveResource("mutes.yml", false);
        }
        muteConfig = YamlConfiguration.loadConfiguration(muteFile);
        mutes.clear();
        if (muteConfig.getConfigurationSection("mutes") != null) {
            for (String key : muteConfig.getConfigurationSection("mutes").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    long expiry = muteConfig.getLong("mutes." + key);
                    mutes.put(uuid, expiry);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    /**
     * Saves current mutes to the mutes.yml file.
     */
    public void saveMutes() {
        muteConfig.set("mutes", null);
        for (Map.Entry<UUID, Long> entry : mutes.entrySet()) {
            muteConfig.set("mutes." + entry.getKey().toString(), entry.getValue());
        }
        try {
            muteConfig.save(muteFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save mutes.yml: " + e.getMessage());
        }
    }

    /**
     * Mutes a player.
     * @param uuid The UUID of the player.
     * @param durationMinutes Duration in minutes. Use -1 for infinite.
     */
    public void mute(UUID uuid, long durationMinutes) {
        long expiry;
        if (durationMinutes == -1) {
            expiry = -1;
        } else {
            expiry = System.currentTimeMillis() + (durationMinutes * 60 * 1000);
        }
        mutes.put(uuid, expiry);
        saveMutes();
    }

    /**
     * Unmutes a player.
     * @param uuid The UUID of the player.
     */
    public void unmute(UUID uuid) {
        mutes.remove(uuid);
        saveMutes();
    }

    /**
     * Checks if a player is currently muted.
     * @param uuid The UUID of the player.
     * @return True if muted, false otherwise.
     */
    public boolean isMuted(UUID uuid) {
        if (!mutes.containsKey(uuid)) {
            return false;
        }
        long expiry = mutes.get(uuid);
        if (expiry == -1) {
            return true;
        }
        if (System.currentTimeMillis() > expiry) {
            unmute(uuid); // Cleanup expired mute
            return false;
        }
        return true;
    }

    /**
     * Gets the remaining time of a mute in a readable format or null if not muted.
     * @param uuid The UUID of the player.
     * @return Remaining time string or null.
     */
    public String getRemainingTime(UUID uuid) {
        if (!mutes.containsKey(uuid)) return null;
        long expiry = mutes.get(uuid);
        if (expiry == -1) return "permanent";
        
        long remainingMillis = expiry - System.currentTimeMillis();
        if (remainingMillis <= 0) return null;

        long minutes = (remainingMillis / 1000) / 60;
        long seconds = (remainingMillis / 1000) % 60;
        
        if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        } else {
            return seconds + "s";
        }
    }
}
