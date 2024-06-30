package primalcat.tempusbazaar.category.atributes;


import org.bukkit.inventory.ItemStack;

public class Border {
    private ItemStack item;

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    // Конструктор, который принимает item и nbt
    public Border(ItemStack item) {
        this.item = item;
    }
}
