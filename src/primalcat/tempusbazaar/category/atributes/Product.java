package primalcat.tempusbazaar.category.atributes;

import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import primalcat.tempusbazaar.category.atributes.Count;
import primalcat.tempusbazaar.category.atributes.Price;

import java.util.List;

public class Product {
    private ItemStack item;
    private Price price;
    private Count count;
    private Component rarity;

    private static double countIncrementPercentage = 0.2; // Процент увеличения количества
    private static double countDecrementPercentage = 0.2; // Процент уменьшения количества

    private static double priceIncrementPercentage = 0.1; // Процент увеличения цены
    private static double priceDecrementPercentage = 0.1; // Процент уменьшения цены

    public static double getCountIncrementPercentage() {
        return countIncrementPercentage;
    }

    public static void setCountIncrementPercentage(double countIncrementPercentage) {
        Product.countIncrementPercentage = countIncrementPercentage;
    }

    public static double getCountDecrementPercentage() {
        return countDecrementPercentage;
    }

    public static void setCountDecrementPercentage(double countDecrementPercentage) {
        Product.countDecrementPercentage = countDecrementPercentage;
    }

    public static double getPriceIncrementPercentage() {
        return priceIncrementPercentage;
    }

    public static void setPriceIncrementPercentage(double priceIncrementPercentage) {
        Product.priceIncrementPercentage = priceIncrementPercentage;
    }

    public static double getPriceDecrementPercentage() {
        return priceDecrementPercentage;
    }

    public static void setPriceDecrementPercentage(double priceDecrementPercentage) {
        Product.priceDecrementPercentage = priceDecrementPercentage;
    }



    @SerializedName("transaction_type")
    private String transactionType;

    // Геттеры и сеттеры
    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Count getCount() {
        return count;
    }

    public void setCount(Count count) {
        this.count = count;
    }

    public Component getRarity() {
        return rarity;
    }

    public void setRarity(Component rarity) {
        this.rarity = rarity;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }


    public void increaseCount() {
        int currentAmount = getCount().getCurrent();
        int increment = (int) (currentAmount * countIncrementPercentage);  // 20% увеличение
        if (increment < 1) increment = 1;  // Если увеличение меньше 1, делаем его равным 1
        int newAmount = currentAmount + increment;
        int max = getCount().getMax() < 1 ? Integer.MAX_VALUE : getCount().getMax();
        getCount().setCurrent(Math.min(newAmount, max));
    }

    public void decreaseCount() {
        int currentAmount = getCount().getCurrent();
        int decrement = (int) (currentAmount * countDecrementPercentage);  // 20% уменьшение
        if (decrement < 1) decrement = 1;  // Если уменьшение меньше 1, делаем его равным 1
        int newAmount = currentAmount - decrement;
        int min = Math.max(getCount().getMin(), 1);
        getCount().setCurrent(Math.max(newAmount, min));
    }

    public void increasePrice() {
        int oldPrice = getPrice().getCurrent();
        int increment = (int) (oldPrice * priceIncrementPercentage);  // 10% увеличение
        if (increment < 1) increment = 1;  // Если увеличение меньше 1, делаем его равным 1
        int newPrice = oldPrice + increment;
        int max = getPrice().getMax() < 1 ? Integer.MAX_VALUE : getPrice().getMax();
        getPrice().setCurrent(Math.min(newPrice, max));
    }

    public void decreasePrice() {
        int oldPrice = getPrice().getCurrent();
        int decrement = (int) (oldPrice * priceDecrementPercentage);  // 10% уменьшение
        if (decrement < 1) decrement = 1;  // Если уменьшение меньше 1, делаем его равным 1
        int newPrice = oldPrice - decrement;
        int min = Math.max(getPrice().getMin(), 1);
        getPrice().setCurrent(Math.max(newPrice, min));
    }

    public double getPriceRatio() {
        int min = Math.max(price.getMin(), 1);
        int max = price.getMax() < 1 ? Integer.MAX_VALUE : price.getMax();

        if (price.getMin() < 1 && price.getMax() < 1) {
            return price.getCurrent();
        }

        // Если max игнорируется, используем разницу между текущей и минимальной ценой
        return (double) (price.getCurrent() - min) / Math.max(max, 1);
    }

    public double getCountRatio() {
        int min = Math.max(count.getMin(), 1);
        int max = count.getMax() < 1 ? Integer.MAX_VALUE : count.getMax();

        if (count.getMin() < 1 && count.getMax() < 1) {
            return count.getCurrent();
        }

        // Если max игнорируется, используем разницу между текущей и минимальной ценой
        return (double) (count.getCurrent() - min) / Math.max(max, 1);
    }


    public void increaseCount0() {
        int currentAmount = getCount().getCurrent();
        int increment = (int) (currentAmount * countIncrementPercentage);  // 20% увеличение
        if (increment < 1) increment = 1;  // Если увеличение меньше 1, делаем его равным 1
        int newAmount = currentAmount + increment;
        int max = getCount().getMax() < 1 ? Integer.MAX_VALUE : getCount().getMax();
        getCount().setCurrent(Math.min(newAmount, max));
    }

    public void decreaseCount0() {
        int currentAmount = getCount().getCurrent();
        int decrement = (int) (currentAmount * countDecrementPercentage);  // 20% уменьшение
        if (decrement < 1) decrement = 1;  // Если уменьшение меньше 1, делаем его равным 1
        int newAmount = currentAmount - decrement;
        int min = Math.max(getCount().getMin(), 0);
        getCount().setCurrent(Math.max(newAmount, min));
    }

    public void increasePrice0() {
        int oldPrice = getPrice().getCurrent();
        int increment = (int) (oldPrice * priceIncrementPercentage);  // 10% увеличение
        if (increment < 1) increment = 1;  // Если увеличение меньше 1, делаем его равным 1
        int newPrice = oldPrice + increment;
        int max = getPrice().getMax() < 1 ? Integer.MAX_VALUE : getPrice().getMax();
        getPrice().setCurrent(Math.min(newPrice, max));
    }

    public void decreasePrice0() {
        int oldPrice = getPrice().getCurrent();
        int decrement = (int) (oldPrice * priceDecrementPercentage);  // 10% уменьшение
        if (decrement < 1) decrement = 1;  // Если уменьшение меньше 1, делаем его равным 1
        int newPrice = oldPrice - decrement;
        int min = Math.max(getPrice().getMin(), 0);
        getPrice().setCurrent(Math.max(newPrice, min));
    }
}