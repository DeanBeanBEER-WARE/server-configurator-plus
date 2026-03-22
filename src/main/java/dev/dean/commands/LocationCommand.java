package dev.dean.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to broadcast the player's current coordinates to the public chat.
 * Used by commands like /dupe and /rules to share locations with all players.
 * Implements CommandExecutor for stable Bukkit integration.
 */
public class LocationCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }

        Location loc = player.getLocation();
        
        // Format coordinates with 2 decimal places
        String coordinates = String.format("X: %.2f, Y: %.2f, Z: %.2f", 
                loc.getX(), loc.getY(), loc.getZ());
        
        // Build and broadcast the location message to all players
        Component message = Component.text()
                .append(Component.text(player.getName(), NamedTextColor.GOLD))
                .append(Component.text("'s coordinates: ", NamedTextColor.GOLD))
                .append(Component.text(coordinates + " ", NamedTextColor.WHITE))
                .append(Component.text("(World: " + loc.getWorld().getName() + ")", NamedTextColor.GRAY))
                .build();

        player.getServer().broadcast(message);
        return true;
    }
}
