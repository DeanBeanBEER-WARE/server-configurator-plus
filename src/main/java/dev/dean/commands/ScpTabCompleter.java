package dev.dean.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tab completer for the /scp command.
 * Provides suggestions for subcommands like 'reload' and 'maintenance'.
 */
public class ScpTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList("reload", "maintenance");

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Suggest subcommands that start with the current input
            String input = args[0].toLowerCase();
            for (String subcommand : SUBCOMMANDS) {
                if (subcommand.startsWith(input)) {
                    completions.add(subcommand);
                }
            }
        }

        return completions;
    }
}
