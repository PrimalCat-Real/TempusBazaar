package primalcat.tempusbazaar.utils;

import java.util.Objects;
import java.util.function.Consumer;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StackBuilder {
    private ItemStack stack;

    public StackBuilder(String materialName) {
        this(new ItemStack(
                        Objects.requireNonNull(Material.getMaterial(materialName.toUpperCase())),
        1));
    }
    public StackBuilder(Material material) {
        this(new ItemStack(material, 1));
    }

    public StackBuilder(ItemStack stack) {
        this.stack = stack;
    }

    public StackBuilder withCount(int count) {
        this.stack.setAmount(count);
        return this;
    }

    public StackBuilder withEditMeta(Consumer<ItemMeta> edit) {
        this.stack.editMeta(edit);
        return this;
    }

    public StackBuilder withDisplayName(Component displayName) {
        return this.withEditMeta((it) -> {
            it.displayName(displayName);
        });
    }

    public ItemStack getStack() {
        return stack;
    }

}


