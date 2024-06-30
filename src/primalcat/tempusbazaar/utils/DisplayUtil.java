package primalcat.tempusbazaar.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class DisplayUtil {
    public static Map<String, Gradient> rarityColors = new HashMap<>();
    public static final MiniMessage miniMessage = MiniMessage.miniMessage();

    // Возвращает текстовый компонент с градиентом для данной редкости
    public static Component getRarityComponent(String rarity, String text) {
        Gradient gradient = rarityColors.get(rarity.toUpperCase());
        if (gradient == null) {
            throw new IllegalArgumentException("Rarity not defined.");
        }

        String gradientTag = "<i:false><gradient:" + gradient.start.asHexString() + ":" + gradient.end.asHexString() + ">" + text + "</gradient></i>";
        return miniMessage.deserialize(gradientTag);
    }

    public static class Gradient {
        final TextColor start;
        final TextColor end;

        Gradient(TextColor start, TextColor end) {
            this.start = start;
            this.end = end;
        }
    }

    public static String serializeRarityComponent(Component component) {
        return miniMessage.serialize(component);
    }
}
