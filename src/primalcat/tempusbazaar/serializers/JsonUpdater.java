package primalcat.tempusbazaar.serializers;

import com.google.gson.*;
import primalcat.tempusbazaar.category.Category;
import primalcat.tempusbazaar.category.atributes.Product;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonUpdater {
    public static void saveCurrentValuesToFile(File dataFolder, String fileName, List<Category> updatedCategories) {
        File file = new File(dataFolder, fileName);

        if (!file.exists()) {
            return;
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray categoriesArray = json.getAsJsonArray("categories");

            for (Category updatedCategory : updatedCategories) {
                for (JsonElement categoryElement : categoriesArray) {
                    JsonObject categoryJson = categoryElement.getAsJsonObject();
                    if (categoryJson.get("item").getAsString().equals(updatedCategory.getItem().getType().name())) {
                        JsonArray productsArray = categoryJson.getAsJsonArray("products");

                        for (Product updatedProduct : updatedCategory.getProducts()) {
                            for (JsonElement productElement : productsArray) {
                                JsonObject productJson = productElement.getAsJsonObject();
                                if (productJson.get("item").getAsString().equals(updatedProduct.getItem().getType().name())) {
                                    JsonObject priceJson = productJson.getAsJsonObject("price");
                                    priceJson.addProperty("current", updatedProduct.getPrice().getCurrent());

                                    JsonObject countJson = productJson.getAsJsonObject("count");
                                    countJson.addProperty("current", updatedProduct.getCount().getCurrent());
                                }
                            }
                        }
                    }
                }
            }

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                gson.toJson(json, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
