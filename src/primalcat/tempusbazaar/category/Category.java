package primalcat.tempusbazaar.category;


import org.bukkit.inventory.ItemStack;
import primalcat.tempusbazaar.category.atributes.Border;
import primalcat.tempusbazaar.category.atributes.Product;

import java.util.List;

public class Category {
    private ItemStack item;
    private Border border;
    private List<Product> products;


    // Геттеры и сеттеры
    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }


    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        this.border = border;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
