package primalcat.tempusbazaar.utils;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import primalcat.tempusbazaar.TempusBazaar;
import primalcat.tempusbazaar.category.Category;
import primalcat.tempusbazaar.category.atributes.Product;
import primalcat.tempusbazaar.serializers.CategoryDeserializer;
import primalcat.tempusbazaar.serializers.ProductDeserializer;

import primalcat.tempusbazaar.trades.ProductFinder;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConfigUtil {

    public static void createJsonFile(File dataFolder, String fileName) {
        File file = new File(dataFolder, fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("{}");
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Обработка ошибок создания файла
            }
        }
    }

    public static void readConfigValues(FileConfiguration configPath){
        Product.setCountIncrementPercentage(configPath.getDouble("count-increment-percentage", 0.2));
        Product.setCountDecrementPercentage(configPath.getDouble("count-decrement-percentage", 0.1));
        Product.setPriceIncrementPercentage(configPath.getDouble("price-increment-percentage", 0.1));
        Product.setPriceDecrementPercentage(configPath.getDouble("price-decrement-percentage", 0.2));
        ProductFinder.setLimit(configPath.getInt("max-categories-changing", 3));
        initRarities(configPath);
    }

    public static void initRarities(FileConfiguration  config) {
        config.getConfigurationSection("rarities").getKeys(false).forEach(key -> {
            String startColor = config.getString("rarities." + key + ".start_color");
            String endColor = config.getString("rarities." + key + ".end_color");
            DisplayUtil.rarityColors.put(key.toUpperCase(), new DisplayUtil.Gradient(
                    TextColor.fromCSSHexString(startColor),
                    TextColor.fromCSSHexString(endColor)
            ));
        });
    }

    public static List<Category> readCategoriesFromFile(File dataFolder, String fileName) {
        File file = new File(dataFolder, fileName);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Category.class, new CategoryDeserializer())
                .registerTypeAdapter(Product.class, new ProductDeserializer())
                .create();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray categoriesArray = json.getAsJsonArray("categories");
            Type listType = new TypeToken<List<Category>>() {}.getType();
            return gson.fromJson(categoriesArray, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
