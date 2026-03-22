package dev.dean;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import java.util.List;

/**
 * Filtert Konsolen-Logs basierend auf Strings, die in der config.yml definiert wurden.
 */
public class ConsoleFilter extends AbstractFilter {

    private final ServerConfiguratorPlus plugin;
    private List<String> filteredStrings;

    public ConsoleFilter(ServerConfiguratorPlus plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        this.filteredStrings = plugin.getConfig().getStringList("console-filters");
    }

    private Result checkMessage(String message) {
        if (message == null || filteredStrings == null || filteredStrings.isEmpty()) {
            return Result.NEUTRAL;
        }

        for (String filter : filteredStrings) {
            if (message.contains(filter)) {
                return Result.DENY; // Blockt die Nachricht komplett ab
            }
        }
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(LogEvent event) {
        if (event == null || event.getMessage() == null) {
            return Result.NEUTRAL;
        }
        return checkMessage(event.getMessage().getFormattedMessage());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        if (msg == null) return Result.NEUTRAL;
        return checkMessage(msg.getFormattedMessage());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        return checkMessage(msg);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        if (msg == null) return Result.NEUTRAL;
        return checkMessage(msg.toString());
    }
}
