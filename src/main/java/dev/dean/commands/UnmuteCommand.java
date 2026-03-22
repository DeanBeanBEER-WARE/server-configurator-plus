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
 * Command to unmute a player.
 * Usage: /unmute <username>
 */
public class UnmuteCommand implements CommandExecutor, TabCompleter {

    private final ServerConfiguratorPlus plugin;

    public UnmuteCommand(ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("scp.unmute")) {
            sender.sendMessage(Component.text("You don't have permission to use this command.", NamedTextColor.RED));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /unmute <username>", NamedTextColor.RED));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        
        // Even if offline, we should try to resolve the name to a UUID if possible
        // But for this simple implementation, we focus on names that we can find online
        // or just pass the name to the manager if it handled names.
        // Our MuteManager uses UUIDs.
        
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

                plugin.getLogger().info("[UnmuteCommand Debug] Sender: " + senderPlayer.getName() + " | Group: " + senderUser.getPrimaryGroup() + " | Weight: " + senderWeight);
                plugin.getLogger().info("[UnmuteCommand Debug] Target: " + target.getName() + " | Group: " + targetUser.getPrimaryGroup() + " | Weight: " + targetWeight);

                if (senderWeight <= targetWeight) {
                    if (targetWeight >= 700) {
                        sender.sendMessage(Component.text("You cannot unmute this player because they are immune.", NamedTextColor.RED));
                    } else {
                        sender.sendMessage(Component.text("You cannot unmute this player because they have a higher or equal rank.", NamedTextColor.RED));
                    }
                    return true;
                }
            }
        }

        if (!plugin.getMuteManager().isMuted(target.getUniqueId())) {
            sender.sendMessage(Component.text("This player is not muted.", NamedTextColor.RED));
            return true;
        }

        plugin.getMuteManager().unmute(target.getUniqueId());

        sender.sendMessage(Component.text("You have unmuted ", NamedTextColor.GREEN)
                .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                .append(Component.text(".", NamedTextColor.GREEN)));

        target.sendMessage(Component.text("You have been unmuted!", NamedTextColor.GREEN));

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
        return new ArrayList<>();
    }
}
