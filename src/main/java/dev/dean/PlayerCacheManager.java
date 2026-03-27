package dev.dean;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the caching of player names based on their IP addresses.
 * This cache is used to personalize the MOTD for returning players.
 * Uses ConcurrentHashMap for thread-safe access in Folia.
 */
public class PlayerCacheManager {

    private final ServerConfiguratorPlus plugin;
    private final File cacheFile;
    private FileConfiguration cacheConfig;
    private final Map<String, String> ipToName = new ConcurrentHashMap<>();
    private final Map<String, String> uuidToNickname = new ConcurrentHashMap<>();

    /**
     * Constructs a new PlayerCacheManager.
     *
     * @param plugin The plugin instance.
     */
    public PlayerCacheManager(@NotNull ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
        this.cacheFile = new File(plugin.getDataFolder(), "player-cache.yml");
        loadCache();
    }

    /**
     * Loads the player cache from the player-cache.yml file.
     */
    public void loadCache() {
        if (!cacheFile.exists()) {
            try {
                // Try to save from resources, if it exists
                if (plugin.getResource("player-cache.yml") != null) {
                    plugin.saveResource("player-cache.yml", false);
                } else {
                    // Create empty file if not in resources
                    if (cacheFile.getParentFile().mkdirs() || cacheFile.getParentFile().exists()) {
                        cacheFile.createNewFile();
                    }
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create player-cache.yml: " + e.getMessage());
            }
        }
        
        cacheConfig = YamlConfiguration.loadConfiguration(cacheFile);
        for (String ipKey : cacheConfig.getKeys(false)) {
            if (ipKey.equals("nicknames")) continue;
            String name = cacheConfig.getString(ipKey);
            if (name != null && !cacheConfig.isConfigurationSection(ipKey)) {
                ipToName.put(ipKey.replace("_", "."), name);
            }
        }
        
        if (cacheConfig.isConfigurationSection("nicknames")) {
            for (String uuid : cacheConfig.getConfigurationSection("nicknames").getKeys(false)) {
                String nick = cacheConfig.getString("nicknames." + uuid);
                if (nick != null) {
                    uuidToNickname.put(uuid, nick);
                }
            }
        }
    }

    /**
     * Updates the cache with a player's current IP and name.
     *
     * @param ip   The player's IP address.
     * @param name The player's name.
     */
    public void updateCache(@NotNull String ip, @NotNull String name) {
        ipToName.put(ip, name);
    }

    /**
     * Retrieves a player's name from the cache based on their IP.
     *
     * @param ip The IP address to look up.
     * @return The cached player name, or "Friend" if not found.
     */
    @NotNull
    public String getPlayerName(@NotNull String ip) {
        return ipToName.getOrDefault(ip, "Friend");
    }

    /**
     * Updates the cache with a player's UUID and nickname.
     *
     * @param uuid The player's UUID as a string.
     * @param nickname The player's formatted nickname string.
     */
    public void setNickname(@NotNull String uuid, @NotNull String nickname) {
        uuidToNickname.put(uuid, nickname);
    }

    /**
     * Retrieves a player's nickname from the cache based on their UUID.
     *
     * @param uuid The UUID string to look up.
     * @return The cached nickname, or null if not found.
     */
    public String getNickname(@NotNull String uuid) {
        return uuidToNickname.get(uuid);
    }

    /**
     * Saves the current cache to the player-cache.yml file.
     */
    public void saveCache() {
        if (cacheConfig == null) return;
        
        for (Map.Entry<String, String> entry : ipToName.entrySet()) {
            cacheConfig.set(entry.getKey().replace(".", "_"), entry.getValue());
        }
        
        for (Map.Entry<String, String> entry : uuidToNickname.entrySet()) {
            cacheConfig.set("nicknames." + entry.getKey(), entry.getValue());
        }
        
        try {
            cacheConfig.save(cacheFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player cache: " + e.getMessage());
        }
    }
}
