package dev.dean.commands;

import dev.dean.ServerConfiguratorPlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Command to display the current rotating MOTD.
 * Implements CommandExecutor for stable Bukkit integration.
 */
public class MotdCommand implements CommandExecutor {

    private final ServerConfiguratorPlus plugin;

    /**
     * Constructs a new MotdCommand.
     *
     * @param plugin The plugin instance.
     */
    public MotdCommand(@NotNull ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String currentMotd = plugin.getMotdManager().getCurrentMotd();
        Component motdComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(currentMotd);

        Component message = Component.text("Current Rotating MOTD: ", NamedTextColor.GOLD)
                .append(motdComponent);

        sender.sendMessage(message);
        return true;
    }
}
