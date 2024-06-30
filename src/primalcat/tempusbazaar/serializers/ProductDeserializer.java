package primalcat.tempusbazaar.serializers;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import primalcat.tempusbazaar.category.atributes.Count;
import primalcat.tempusbazaar.category.atributes.Price;
import primalcat.tempusbazaar.category.atributes.Product;
import primalcat.tempusbazaar.utils.ConfigUtil;
import primalcat.tempusbazaar.utils.DataUtil;
import primalcat.tempusbazaar.utils.DisplayUtil;
import primalcat.tempusbazaar.utils.StackBuilder;

import java.lang.reflect.Type;

public class ProductDeserializer implements JsonDeserializer<Product> {

    @Override
    public Product deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        Product product = new Product();
        String itemMaterialName = jsonObject.get("item").getAsString();
        ItemStack productItem = new StackBuilder(itemMaterialName).withEditMeta(
                meta -> {
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    if (jsonObject.has("nbt")) {
                        JsonObject nbtObject = jsonObject.getAsJsonObject("nbt");
                        DataUtil.handleVanillaNbtKeys(meta, nbtObject);
                        DataUtil.fillContainer(container, nbtObject);
                    }
                }
        ).getStack();
        product.setItem(productItem);
        // set count class
        JsonObject countJson = jsonObject.getAsJsonObject("count");
        Count count = new Count();
        count.setMin(countJson.get("min").getAsInt());
        count.setCurrent(countJson.get("current").getAsInt());
        count.setMax(countJson.get("max").getAsInt());
        product.setCount(count);

        // set price class
        JsonObject priceJson = jsonObject.getAsJsonObject("price");
        Price price = new Price();
        price.setMin(priceJson.get("min").getAsInt());
        price.setCurrent(priceJson.get("current").getAsInt());
        price.setMax(priceJson.get("max").getAsInt());
        product.setPrice(price);

        product.setRarity(DisplayUtil.getRarityComponent(jsonObject.get("rarity").getAsString(), jsonObject.get("rarity").getAsString()));
        product.setTransactionType(jsonObject.get("transaction_type").getAsString());

        return product;
    }


}
