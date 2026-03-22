package dev.dean.commands;

import dev.dean.ServerConfiguratorPlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Admin command for ServerConfiguratorPlus to manage configurations and maintenance mode.
 * Implements CommandExecutor for stable Bukkit integration.
 */
public class ScpCommand implements CommandExecutor {

    private final ServerConfiguratorPlus plugin;

    /**
     * Constructs a new ScpCommand.
     *
     * @param plugin The plugin instance.
     */
    public ScpCommand(@NotNull ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("[SCP] Usage: /scp <reload|maintenance>", NamedTextColor.RED));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPluginConfig();
            sender.sendMessage(Component.text("[SCP] All configurations reloaded successfully!", NamedTextColor.GREEN));
            return true;
        }

        if (args[0].equalsIgnoreCase("maintenance")) {
            boolean active = plugin.getMaintenanceManager().toggleMaintenance();
            Component status = active ? Component.text("ON", NamedTextColor.RED) : Component.text("OFF", NamedTextColor.GREEN);
            sender.sendMessage(Component.text()
                    .append(Component.text("[SCP] Maintenance mode is now: ", NamedTextColor.GOLD))
                    .append(status)
                    .build());
            return true;
        }

        sender.sendMessage(Component.text("[SCP] Unknown subcommand. Use reload or maintenance.", NamedTextColor.RED));
        return true;
    }
}
