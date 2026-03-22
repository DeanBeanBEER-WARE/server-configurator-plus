package dev.dean.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Command to show player count and a list of online players.
 * Implements CommandExecutor for stable Bukkit integration.
 */
public class PlayersCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Collection<? extends Player> onlinePlayers = sender.getServer().getOnlinePlayers();
        int count = onlinePlayers.size();
        
        String playerNames = onlinePlayers.stream()
                .map(Player::getName)
                .collect(Collectors.joining(", "));

        Component message = Component.text()
                .append(Component.text("Online Players (" + count + "): ", NamedTextColor.GOLD))
                .append(Component.text(playerNames, NamedTextColor.WHITE))
                .build();

        sender.sendMessage(message);
        return true;
    }
}
