package dev.dean;

import org.bukkit.util.CachedServerIcon;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

/**
 * Manages server icons loaded from the icons folder.
 * Handles automatic resizing to 64x64 and conversion to PNG.
 * Optimized for memory and thread-safe access.
 */
public class IconManager {

    private final ServerConfiguratorPlus plugin;
    private final List<CachedServerIcon> icons;
    private final Random random;

    /**
     * Constructs a new IconManager.
     *
     * @param plugin The plugin instance.
     */
    public IconManager(ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
        this.icons = new ArrayList<>();
        this.random = new Random();
        loadIcons();
    }

    /**
     * Loads and refreshes icons from the 'icons' directory within the plugin folder.
     */
    public void loadIcons() {
        synchronized (icons) {
            icons.clear();
            File iconFolder = new File(plugin.getDataFolder(), "icons");
            if (!iconFolder.exists()) {
                iconFolder.mkdirs();
            }

            File[] files = iconFolder.listFiles((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".png") || lower.endsWith(".jpg") || 
                       lower.endsWith(".jpeg") || lower.endsWith(".webp") || 
                       lower.endsWith(".avif");
            });

            if (files == null) return;

            for (File file : files) {
                try {
                    CachedServerIcon icon = loadAndConvertIcon(file);
                    if (icon != null) {
                        icons.add(icon);
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Failed to load icon: " + file.getName(), e);
                }
            }
            plugin.getLogger().info("Loaded " + icons.size() + " server icons.");
        }
    }

    /**
     * Loads an image file, converts/resizes it to 64x64 PNG, and returns a CachedServerIcon.
     *
     * @param file The image file to load.
     * @return A CachedServerIcon instance, or null if loading failed.
     * @throws Exception If an error occurs during image processing.
     */
    private CachedServerIcon loadAndConvertIcon(File file) throws Exception {
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            plugin.getLogger().warning("Could not read image file: " + file.getName() + " (Unsupported format or corrupt)");
            return null;
        }

        // Check if resize or conversion is needed (Minecraft icons must be 64x64 PNG)
        if (image.getWidth() != 64 || image.getHeight() != 64 || !file.getName().toLowerCase().endsWith(".png")) {
            BufferedImage resized = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resized.createGraphics();
            g.drawImage(image, 0, 0, 64, 64, null);
            g.dispose();
            image = resized;
        }

        return plugin.getServer().loadServerIcon(image);
    }

    /**
     * Gets a random server icon from the cache.
     *
     * @return A random CachedServerIcon, or null if no icons are loaded.
     */
    public CachedServerIcon getRandomIcon() {
        synchronized (icons) {
            if (icons.isEmpty()) return null;
            return icons.get(random.nextInt(icons.size()));
        }
    }
}
