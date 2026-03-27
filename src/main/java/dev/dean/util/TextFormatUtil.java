package dev.dean.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class TextFormatUtil {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    /**
     * Converts a string with legacy formatting codes to an Adventure Component.
     * Supports standard colors, hex, and a special &u code to disable italics.
     *
     * @param text The input string with legacy color codes
     * @return The formatted Adventure Component
     */
    @NotNull
    public static Component format(@NotNull String text) {
        boolean disableItalic = text.contains("&u");
        if (disableItalic) {
            text = text.replace("&u", "");
        }

        Component component = SERIALIZER.deserialize(text);

        if (disableItalic) {
            component = component.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        }

        return component;
    }
}
