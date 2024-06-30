package primalcat.tempusbazaar.trades;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import primalcat.tempusbazaar.utils.StackBuilder;

import java.util.ArrayList;
import java.util.List;

public class TempusCoins {
    public static List<Component> getTempusLore() {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(""));
        lore.add(Component.text("   Официальное платежное банковское средство.", TextColor.color(255, 255, 255)).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("        Подделка преследуется по закону!", TextColor.color(255, 5, 48)).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(""));
        return lore;
    }

    public static List<Component> getTempus10Lore() {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(""));
        lore.add(Component.text("   Официальное платежное банковское средство.", TextColor.color(255, 255, 255)).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("        Подделка преследуется по закону!", TextColor.color(255, 5, 48)).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(""));
        return lore;
    }
    public static ItemStack createTempus(int count) {
        List<Component> lore = getTempusLore();

        return new StackBuilder(Material.CAMEL_SPAWN_EGG)
                .withCount(count)
                .withEditMeta(meta -> {
                    meta.setCustomModelData(10032004);
                    meta.displayName(Component.text("Темпус", TextColor.color(255, 228, 58)).decoration(TextDecoration.ITALIC, false));
                    meta.lore(lore);
                })
                .getStack();
    }

    public static ItemStack createTempus() {
        List<Component> lore = getTempusLore();

        return new StackBuilder(Material.CAMEL_SPAWN_EGG)
                .withCount(1)
                .withEditMeta(meta -> {
                    meta.setCustomModelData(10032004);
                    meta.displayName(Component.text("Темпус", TextColor.color(255, 228, 58)).decoration(TextDecoration.ITALIC, false));
                    meta.lore(lore);
                })
                .getStack();
    }

    public static ItemStack createTempus10(int count) {
        List<Component> lore = getTempus10Lore();

        return new StackBuilder(Material.PIGLIN_SPAWN_EGG)
                .withCount(count)
                .withEditMeta(meta -> {
                    meta.setCustomModelData(10032024);
                    meta.displayName(Component.text("10 Темпусов", TextColor.color(255, 176, 19)).decoration(TextDecoration.ITALIC, false));
                    meta.lore(lore);
                })
                .getStack();
    }

    public static ItemStack createTempus10() {
        List<Component> lore = getTempus10Lore();

        return new StackBuilder(Material.PIGLIN_SPAWN_EGG)
                .withCount(1)
                .withEditMeta(meta -> {
                    meta.setCustomModelData(10032024);
                    meta.displayName(Component.text("10 Темпусов", TextColor.color(255, 176, 19)).decoration(TextDecoration.ITALIC, false));
                    meta.lore(lore);
                })
                .getStack();
    }
}
