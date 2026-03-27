package dev.dean.commands;

import dev.dean.ServerConfiguratorPlus;
import dev.dean.util.TextFormatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class FormattingCommands implements CommandExecutor {

    private final ServerConfiguratorPlus plugin;

    public FormattingCommands(ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        String name = command.getName().toLowerCase();
        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /" + label + " <text>", NamedTextColor.RED));
            return true;
        }

        String input = String.join(" ", args);
        Component formatted = TextFormatUtil.format(input);

        switch (name) {
            case "nickname":
                plugin.getPlayerCacheManager().setNickname(player.getUniqueId().toString(), input);
                player.displayName(formatted);
                player.sendMessage(Component.text("Your nickname has been updated!").color(NamedTextColor.GREEN));
                break;
            case "itemname":
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.isEmpty()) {
                    player.sendMessage(Component.text("You must hold an item in your main hand.", NamedTextColor.RED));
                    return true;
                }

                // Execute on the player's entity scheduler for Folia compatibility
                player.getScheduler().run(plugin, task -> {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.displayName(formatted);
                        item.setItemMeta(meta);
                        player.sendMessage(Component.text("Item name updated!").color(NamedTextColor.GREEN));
                    }
                }, null);
                break;
        }

        return true;
    }
}
