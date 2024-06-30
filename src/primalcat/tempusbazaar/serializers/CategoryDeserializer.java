package primalcat.tempusbazaar.serializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import primalcat.tempusbazaar.category.Category;
import primalcat.tempusbazaar.category.atributes.Border;
import primalcat.tempusbazaar.category.atributes.Product;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import primalcat.tempusbazaar.utils.ConfigUtil;
import primalcat.tempusbazaar.utils.DataUtil;
import primalcat.tempusbazaar.utils.StackBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoryDeserializer implements JsonDeserializer<Category> {
    private final GsonComponentSerializer componentSerializer = GsonComponentSerializer.gson();
    @Override
    public Category deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String itemMaterialName = jsonObject.get("item").getAsString();
        ItemStack categoryItem = new StackBuilder(itemMaterialName).withEditMeta(
                meta -> {
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    if (jsonObject.has("nbt")) {
                        JsonObject nbtObject = jsonObject.getAsJsonObject("nbt");
                        DataUtil.handleVanillaNbtKeys(meta, nbtObject);
                        DataUtil.fillContainer(container, nbtObject);
                    }
                }
        ).getStack();
//        System.out.println("Test des " + categoryItem);


        JsonObject borderJson = jsonObject.getAsJsonObject("border");
        String borderMaterialName = borderJson.get("item").getAsString();
        ItemStack borderItem = new StackBuilder(borderMaterialName).withEditMeta(
                meta -> {
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    if (borderJson.has("nbt")) {
                        JsonObject nbtObject = borderJson.getAsJsonObject("nbt");
                        DataUtil.handleVanillaNbtKeys(meta, nbtObject);
                        DataUtil.fillContainer(container, nbtObject);
                    }

                    NamespacedKey key = new NamespacedKey("minecraft", "custom_name");
                    container.set(key, PersistentDataType.STRING, "Custom Lore Data");

                }
        ).getStack();

        Border border = new Border(borderItem);


        // Дополнительная логика для обработки других полей объекта Category
//        Component name = componentSerializer.deserialize(jsonObject.get("name").getAsString());
//        List<Component> lore = deserializeComponentList(jsonObject.getAsJsonArray("lore"));

        Category category = new Category();
        category.setItem(categoryItem);
        category.setBorder(border);

        Type productListType = new TypeToken<List<Product>>(){}.getType();
        List<Product> products = context.deserialize(jsonObject.get("products"), productListType);
//        System.out.println("products " + products.get(0).getRarity());
        category.setProducts(products);

        return category;
    }


    private List<Component> deserializeComponentList(JsonArray jsonArray) {
        List<Component> components = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            components.add(componentSerializer.deserialize(element.toString()));
        }
        return components;
    }
}
