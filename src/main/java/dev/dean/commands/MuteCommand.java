package dev.dean.commands;

import dev.dean.ServerConfiguratorPlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to mute a player.
 * Usage: /mute <username> [minutes]
 */
public class MuteCommand implements CommandExecutor, TabCompleter {

    private final ServerConfiguratorPlus plugin;

    public MuteCommand(ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("scp.mute")) {
            sender.sendMessage(Component.text("You don't have permission to use this command.", NamedTextColor.RED));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /mute <username> [minutes]", NamedTextColor.RED));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        
        // We allow muting offline players if we have their UUID, but for simplicity we'll focus on online players
        // or names that can be resolved.
        if (target == null) {
            sender.sendMessage(Component.text("Player not found or offline.", NamedTextColor.RED));
            return true;
        }

        // Hierarchy check
        LuckPerms lp = plugin.getLuckPerms();
        if (lp != null && sender instanceof Player senderPlayer) {
            User senderUser = lp.getUserManager().getUser(senderPlayer.getUniqueId());
            User targetUser = lp.getUserManager().getUser(target.getUniqueId());

            if (senderUser != null && targetUser != null) {
                int senderWeight = getPlayerWeight(lp, senderUser);
                int targetWeight = getPlayerWeight(lp, targetUser);

                plugin.getLogger().info("[MuteCommand Debug] Sender: " + senderPlayer.getName() + " | Group: " + senderUser.getPrimaryGroup() + " | Weight: " + senderWeight);
                plugin.getLogger().info("[MuteCommand Debug] Target: " + target.getName() + " | Group: " + targetUser.getPrimaryGroup() + " | Weight: " + targetWeight);

                if (senderWeight <= targetWeight) {
                    if (targetWeight >= 700) {
                        sender.sendMessage(Component.text("You cannot mute this player because they are immune.", NamedTextColor.RED));
                    } else {
                        sender.sendMessage(Component.text("You cannot mute this player because they have a higher or equal rank.", NamedTextColor.RED));
                    }
                    return true;
                }
            }
        }

        long minutes = -1;
        if (args.length >= 2) {
            try {
                minutes = Long.parseLong(args[1]);
                if (minutes <= 0) {
                    sender.sendMessage(Component.text("Minutes must be a positive number.", NamedTextColor.RED));
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Invalid duration. Please enter a number.", NamedTextColor.RED));
                return true;
            }
        }

        plugin.getMuteManager().mute(target.getUniqueId(), minutes);

        String timeStr = (minutes == -1) ? "permanently" : "for " + minutes + " minutes";
        sender.sendMessage(Component.text("You have muted ", NamedTextColor.GREEN)
                .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" " + timeStr + ".", NamedTextColor.GREEN)));

        target.sendMessage(Component.text("You have been muted " + timeStr + "!", NamedTextColor.RED));

        return true;
    }

    private int getPlayerWeight(LuckPerms lp, User user) {
        String groupName = user.getPrimaryGroup();
        Group group = lp.getGroupManager().getGroup(groupName);
        if (group != null) {
            return group.getWeight().orElse(0);
        }
        return 0;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String search = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(search))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add("5");
            suggestions.add("10");
            suggestions.add("30");
            suggestions.add("60");
            suggestions.add("1440");
            return suggestions;
        }
        return new ArrayList<>();
    }
}
