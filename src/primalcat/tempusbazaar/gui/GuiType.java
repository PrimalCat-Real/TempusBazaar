package primalcat.tempusbazaar.gui;

import primalcat.tempusbazaar.TempusBazaar;
import primalcat.tempusbazaar.category.Category;
import primalcat.tempusbazaar.category.atributes.Product;
import primalcat.tempusbazaar.utils.ConfigUtil;
import primalcat.tempusbazaar.utils.DataUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum GuiType {
    BUYER("buyer"),
    RANDOM_BUYER("randombuyer"),
    SELLER("seller");
//    SELLER("seller");

    private final String type;
    private static final Map<GuiType, List<Category>> categoriesMap = new HashMap<>();
    private static List<Category> randomBuyerCategories;

    public static List<Category> getRandomBuyerCategories() {
        return randomBuyerCategories;
    }

    GuiType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static GuiType fromString(String type) {
        for (GuiType guiType : GuiType.values()) {
            if (guiType.getType().equalsIgnoreCase(type)) {
                return guiType;
            }
        }
        return null;
    }

    public static List<Category> getCategories(GuiType guiType) {
        return categoriesMap.get(guiType);
    }

    public static void initializeRandomBuyerCategories() {
        int maxCategories = TempusBazaar.configPath.getInt("max-buyer-categories", 3); // Получаем значение из config.yml, по умолчанию 3
        List<Category> buyerCategories = categoriesMap.get(BUYER);
        randomBuyerCategories = DataUtil.randomSelectCategories(buyerCategories, maxCategories);
    }

    // Новый метод для обновления рандомных категорий
    public static void updateRandomBuyerCategories() {
        initializeRandomBuyerCategories();
    }

    static {
        categoriesMap.put(BUYER, ConfigUtil.readCategoriesFromFile(TempusBazaar.getPlugin().getDataFolder(), "buyer.json"));
        categoriesMap.put(SELLER, ConfigUtil.readCategoriesFromFile(TempusBazaar.getPlugin().getDataFolder(), "seller.json"));
        initializeRandomBuyerCategories();
    }

    public static void reloadCategories() {
        for (GuiType guiType : GuiType.values()) {
            reloadCategories(guiType);
        }
    }

    public static void restoreSellerProducts() {
        List<Category> sellerCategories = getCategories(SELLER);
        for (Category category : sellerCategories) {
            for (Product product : category.getProducts()) {
                product.getCount().setCurrent(product.getCount().getMax());
            }
        }
        // Сохранить изменения в файл
//        ConfigUtil.saveCategoriesToFile(TempusBazaar.getPlugin().getDataFolder(), "seller.json", sellerCategories);
        TempusBazaar.getPlugin().getLogger().info("Seller products have been restored to maximum quantities.");
    }

    public static void reloadCategories(GuiType guiType) {
        categoriesMap.put(guiType, ConfigUtil.readCategoriesFromFile(TempusBazaar.getPlugin().getDataFolder(), guiType.getType() + ".json"));
        if (guiType == BUYER) {
            initializeRandomBuyerCategories();
        }
    }
}
