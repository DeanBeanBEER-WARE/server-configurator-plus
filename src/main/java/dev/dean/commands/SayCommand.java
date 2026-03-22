package dev.dean.commands;

import dev.dean.ServerConfiguratorPlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Command to override the default /say behavior.
 * Broadcasts a custom-formatted message to all players.
 * Usage: /say <message>
 */
public class SayCommand implements CommandExecutor {

    private final ServerConfiguratorPlus plugin;

    public SayCommand(ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("serverconfiguratorplus.say")) {
            sender.sendMessage(Component.text("You don't have permission to use this command.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /say <message>", NamedTextColor.RED));
            return true;
        }

        // Combine all arguments into the final message string
        String messageContent = String.join(" ", args);

        // Get the configured format
        String format = plugin.getConfig().getString("say-format", "&c[2X2T]&f: {message}");
        
        // Replace placeholder
        String finalMessageString = format.replace("{message}", messageContent);

        // Parse legacy color codes (e.g., &c, &l) to Adventure Component
        Component finalMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(finalMessageString);

        // Broadcast to everyone (console + players)
        Bukkit.broadcast(finalMessage);

        return true;
    }
}
