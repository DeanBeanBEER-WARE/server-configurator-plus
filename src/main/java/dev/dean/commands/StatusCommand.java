package dev.dean.commands;

import dev.dean.ServerConfiguratorPlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Command to display comprehensive server status information.
 * Implements CommandExecutor for stable Bukkit integration.
 */
public class StatusCommand implements CommandExecutor {

    private final ServerConfiguratorPlus plugin;

    /**
     * Constructs a new StatusCommand.
     *
     * @param plugin The plugin instance.
     */
    public StatusCommand(@NotNull ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Collection<? extends Player> onlinePlayers = sender.getServer().getOnlinePlayers();
        int count = onlinePlayers.size();
        
        String playerNames = onlinePlayers.stream()
                .map(Player::getName)
                .collect(Collectors.joining(", "));

        String currentMotd = plugin.getMotdManager().getCurrentMotd();
        Component motdComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(currentMotd);
        String version = plugin.getServer().getVersion();

        Component separator = Component.text("---------------------------------", NamedTextColor.DARK_GRAY);

        sender.sendMessage(separator);
        sender.sendMessage(Component.text("Server Status:", NamedTextColor.GOLD));
        sender.sendMessage(Component.text()
                .append(Component.text("Online Players (" + count + "): ", NamedTextColor.GOLD))
                .append(Component.text(playerNames, NamedTextColor.WHITE))
                .build());
        sender.sendMessage(Component.text()
                .append(Component.text("Current MOTD: ", NamedTextColor.GOLD))
                .append(motdComponent)
                .build());
        sender.sendMessage(Component.text()
                .append(Component.text("Server Version: ", NamedTextColor.GOLD))
                .append(Component.text(version, NamedTextColor.WHITE))
                .build());
        sender.sendMessage(Component.text()
                .append(Component.text("Folia Supported: ", NamedTextColor.GOLD))
                .append(Component.text("Yes", NamedTextColor.GREEN))
                .build());
        sender.sendMessage(separator);
        
        return true;
    }
}
