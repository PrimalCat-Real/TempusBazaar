package primalcat.tempusbazaar.utils;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import primalcat.tempusbazaar.TempusBazaar;
import primalcat.tempusbazaar.category.Category;
import primalcat.tempusbazaar.gui.GuiType;
import primalcat.tempusbazaar.trades.RemoveItems;

import java.util.*;

public class DataUtil {
    public static void fillContainer(PersistentDataContainer container, JsonObject jsonObject) {
        jsonObject.entrySet().forEach(entry -> {
            NamespacedKey key = createNamespacedKey(entry.getKey());
            JsonElement element = entry.getValue();

            if (element.isJsonObject()) {
                PersistentDataContainer subContainer = container.getAdapterContext().newPersistentDataContainer();
                fillContainer(subContainer, element.getAsJsonObject(), key.getNamespace());
                container.set(key, PersistentDataType.TAG_CONTAINER, subContainer);
            } else if (element.isJsonArray()) {
                handleArray(element, container, key);
            } else if (element.isJsonPrimitive()) {
                setPrimitive(container, key, element.getAsJsonPrimitive());
            }
        });
    }

    public static void fillContainer(PersistentDataContainer container, JsonObject jsonObject, String namespace) {
        jsonObject.entrySet().forEach(entry -> {
            NamespacedKey key = createNamespacedKey(entry.getKey(), namespace);
            JsonElement element = entry.getValue();

            if (element.isJsonObject()) {
                PersistentDataContainer subContainer = container.getAdapterContext().newPersistentDataContainer();
                fillContainer(subContainer, element.getAsJsonObject(), key.getNamespace());
                container.set(key, PersistentDataType.TAG_CONTAINER, subContainer);
            } else if (element.isJsonArray()) {
                handleArray(element, container, key);
            } else if (element.isJsonPrimitive()) {
                setPrimitive(container, key, element.getAsJsonPrimitive());
            }
        });
    }

    private static NamespacedKey createNamespacedKey(String keyString) {
        String[] keyParts = keyString.split(":");
        if (keyParts.length == 2) {
            return new NamespacedKey(keyParts[0], keyParts[1].toLowerCase());
        } else if (keyParts.length == 1) {
            return new NamespacedKey(TempusBazaar.getPlugin(), keyParts[0].toLowerCase());
        } else {
            throw new IllegalArgumentException("Invalid key. Must be namespace:key format: " + keyString);
        }
    }

    private static NamespacedKey createNamespacedKey(String keyString, String defaultNamespace) {
        String[] keyParts = keyString.split(":");
        if (keyParts.length == 2) {
            return new NamespacedKey(keyParts[0], keyParts[1].toLowerCase());
        } else if (keyParts.length == 1) {
            return new NamespacedKey(defaultNamespace, keyParts[0].toLowerCase());
        } else {
            throw new IllegalArgumentException("Invalid key. Must be namespace:key format: " + keyString);
        }
    }

    private static void handleArray(JsonElement element, PersistentDataContainer container, NamespacedKey key) {
        JsonArray array = element.getAsJsonArray();
        if (!array.isEmpty() && array.get(0).isJsonObject()) {
            List<PersistentDataContainer> list = new ArrayList<>();
            array.forEach(item -> {
                PersistentDataContainer listItem = container.getAdapterContext().newPersistentDataContainer();
                fillContainer(listItem, item.getAsJsonObject(), key.getNamespace());
                list.add(listItem);
            });
            container.set(key, PersistentDataType.TAG_CONTAINER_ARRAY, list.toArray(new PersistentDataContainer[0]));
        } else {
            if (!array.isEmpty() && array.get(0).isJsonPrimitive()) {
                JsonPrimitive primitive = array.get(0).getAsJsonPrimitive();
                if (primitive.isString()) {
                    List<String> strings = new ArrayList<>();
                    array.forEach(item -> {
                        if (item.isJsonPrimitive() && item.getAsJsonPrimitive().isString()) {
                            strings.add(item.getAsString());
                        }
                    });
                    storeStringList(container, strings, key);
                } else if (primitive.isNumber()) {
                    if (primitive.getAsNumber() instanceof Integer) {
                        int[] intArray = new int[array.size()];
                        for (int i = 0; i < array.size(); i++) {
                            intArray[i] = array.get(i).getAsInt();
                        }
                        container.set(key, PersistentDataType.INTEGER_ARRAY, intArray);
                    } else if (primitive.getAsNumber() instanceof Long) {
                        long[] longArray = new long[array.size()];
                        for (int i = 0; i < array.size(); i++) {
                            longArray[i] = array.get(i).getAsLong();
                        }
                        container.set(key, PersistentDataType.LONG_ARRAY, longArray);
                    } else if (primitive.getAsNumber() instanceof Byte) {
                        byte[] byteArray = new byte[array.size()];
                        for (int i = 0; i < array.size(); i++) {
                            byteArray[i] = array.get(i).getAsByte();
                        }
                        container.set(key, PersistentDataType.BYTE_ARRAY, byteArray);
                    }
                }
            }
        }
    }

    public static void storeStringList(PersistentDataContainer container, List<String> list, NamespacedKey baseKey) {
        for (int i = 0; i < list.size(); i++) {
            NamespacedKey key = new NamespacedKey(baseKey.getNamespace(), baseKey.getKey() + "_" + i);
            container.set(key, PersistentDataType.STRING, list.get(i));
        }
    }

    private static void setPrimitive(PersistentDataContainer container, NamespacedKey key, JsonPrimitive primitive) {
        if (primitive.isBoolean()) {
            container.set(key, PersistentDataType.BYTE, (byte) (primitive.getAsBoolean() ? 1 : 0));
        } else if (primitive.isNumber()) {
            Number num = primitive.getAsNumber();
            if (Math.ceil(num.doubleValue()) == num.longValue()) {
                container.set(key, PersistentDataType.INTEGER, num.intValue());
            } else {
                container.set(key, PersistentDataType.DOUBLE, num.doubleValue());
            }
        } else if (primitive.isString()) {
            container.set(key, PersistentDataType.STRING, primitive.getAsString());
        }
    }


    public static void handleVanillaNbtKeys(ItemMeta meta, JsonObject nbtObject) {
        if (nbtObject.has("custom_name")) {
            String nameJson = nbtObject.get("custom_name").toString();
            meta.displayName(GsonComponentSerializer.gson().deserialize(nameJson));
            nbtObject.remove("custom_name");
        }

        if (nbtObject.has("lore")) {
            JsonArray loreArray = nbtObject.getAsJsonArray("lore");
            List<Component> lore = new ArrayList<>();
            loreArray.forEach(element -> {
                lore.add(GsonComponentSerializer.gson().deserialize(element.toString()));
            });
            meta.lore(lore);
            nbtObject.remove("lore");
        }

        if (nbtObject.has("enchants")) {
            JsonObject enchants = nbtObject.getAsJsonObject("enchants");
            enchants.entrySet().forEach(entry -> {
                Enchantment enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(entry.getKey()));
                if (enchantment != null) {
                    meta.addEnchant(enchantment, entry.getValue().getAsInt(), true);
                }
            });
            nbtObject.remove("enchants");
        }

        if (nbtObject.has("attribute_modifiers")) {
            JsonObject attributeModifiers = nbtObject.getAsJsonObject("attribute_modifiers");
            attributeModifiers.entrySet().forEach(entry -> {
                Attribute attribute = Attribute.valueOf(entry.getKey().toUpperCase());
                JsonArray modifiers = entry.getValue().getAsJsonArray();
                for (int i = 0; i < modifiers.size(); i++) {
                    JsonObject mod = modifiers.get(i).getAsJsonObject();
                    AttributeModifier modifier = new AttributeModifier(
                            UUID.randomUUID(),
                            mod.get("name").getAsString(),
                            mod.get("amount").getAsDouble(),
                            AttributeModifier.Operation.valueOf(mod.get("operation").getAsString().toUpperCase()),
                            org.bukkit.inventory.EquipmentSlot.valueOf(mod.get("slot").getAsString().toUpperCase())
                    );
                    meta.addAttributeModifier(attribute, modifier);
                }
            });
            nbtObject.remove("attribute_modifiers");
        }
        if (nbtObject.has("custom_model_data")) {
            int customModelData = nbtObject.get("custom_model_data").getAsInt();
            meta.setCustomModelData(customModelData);
            nbtObject.remove("custom_model_data");
        }

        if (nbtObject.has("unbreakable")) {
            boolean unbreakable = nbtObject.get("unbreakable").getAsBoolean();
            meta.setUnbreakable(unbreakable);
            nbtObject.remove("unbreakable");
        }

        if (nbtObject.has("damage")) {
            int damage = nbtObject.get("damage").getAsInt();
            if (meta instanceof Damageable) {
                ((Damageable) meta).setDamage(damage);
            }
            nbtObject.remove("damage");
        }

        if (nbtObject.has("hide_flags")) {
            int hideFlags = nbtObject.get("hide_flags").getAsInt();
            meta.addItemFlags(ItemFlag.values()[hideFlags]);
            nbtObject.remove("hide_flags");
        }

        if (nbtObject.has("repair_cost")) {
            int repairCost = nbtObject.get("repair_cost").getAsInt();
            if (meta instanceof Repairable) {
                ((Repairable) meta).setRepairCost(repairCost);
            }
            nbtObject.remove("repair_cost");
        }
    }


//    public static void extractPersistentData(PersistentDataContainer container, JsonObject jsonObject) {
//        for (Map.Entry<NamespacedKey, PersistentDataType<?, ?>> entry : container.getKeys().entrySet()) {
//            String key = entry.getKey().getKey();
//            PersistentDataType<?, ?> type = entry.getValue();
//
//            if (type == PersistentDataType.STRING) {
//                jsonObject.addProperty(key, container.get(entry.getKey(), PersistentDataType.STRING));
//            } else if (type == PersistentDataType.INTEGER) {
//                jsonObject.addProperty(key, container.get(entry.getKey(), PersistentDataType.INTEGER));
//            } else if (type == PersistentDataType.BYTE) {
//                jsonObject.addProperty(key, container.get(entry.getKey(), PersistentDataType.BYTE));
//            } // Добавьте другие типы данных по мере необходимости
//        }
//    }

    // тестовый метод для сереализации
    public static void fillJsonObjectFromContainer(JsonObject jsonObject, PersistentDataContainer container) {
        for (NamespacedKey key : container.getKeys()) {
            // Пробуем получить данные для каждого известного типа
            if (container.has(key, PersistentDataType.STRING)) {
                jsonObject.addProperty(key.getKey(), container.get(key, PersistentDataType.STRING));
            } else if (container.has(key, PersistentDataType.INTEGER)) {
                jsonObject.addProperty(key.getKey(), container.get(key, PersistentDataType.INTEGER));
            } else if (container.has(key, PersistentDataType.BYTE)) {
                jsonObject.addProperty(key.getKey(), container.get(key, PersistentDataType.BYTE));
            } else if (container.has(key, PersistentDataType.DOUBLE)) {
                jsonObject.addProperty(key.getKey(), container.get(key, PersistentDataType.DOUBLE));
            } else if (container.has(key, PersistentDataType.LONG)) {
                jsonObject.addProperty(key.getKey(), container.get(key, PersistentDataType.LONG));
            } else if (container.has(key, PersistentDataType.TAG_CONTAINER)) {
                JsonObject subContainerJson = new JsonObject();
                fillJsonObjectFromContainer(subContainerJson, container.get(key, PersistentDataType.TAG_CONTAINER));
                jsonObject.add(key.getKey(), subContainerJson);
            } else if (container.has(key, PersistentDataType.TAG_CONTAINER_ARRAY)) {
                JsonArray arrayJson = new JsonArray();
                PersistentDataContainer[] containers = container.get(key, PersistentDataType.TAG_CONTAINER_ARRAY);
                for (PersistentDataContainer subContainer : containers) {
                    JsonObject subContainerJson = new JsonObject();
                    fillJsonObjectFromContainer(subContainerJson, subContainer);
                    arrayJson.add(subContainerJson);
                }
                jsonObject.add(key.getKey(), arrayJson);
            } else if (container.has(key, PersistentDataType.BYTE_ARRAY)) {
                JsonArray arrayJson = new JsonArray();
                byte[] byteArray = container.get(key, PersistentDataType.BYTE_ARRAY);
                for (byte b : byteArray) {
                    arrayJson.add(new JsonPrimitive(b));
                }
                jsonObject.add(key.getKey(), arrayJson);
            } else if (container.has(key, PersistentDataType.INTEGER_ARRAY)) {
                JsonArray arrayJson = new JsonArray();
                int[] intArray = container.get(key, PersistentDataType.INTEGER_ARRAY);
                for (int i : intArray) {
                    arrayJson.add(new JsonPrimitive(i));
                }
                jsonObject.add(key.getKey(), arrayJson);
            } else if (container.has(key, PersistentDataType.LONG_ARRAY)) {
                JsonArray arrayJson = new JsonArray();
                long[] longArray = container.get(key, PersistentDataType.LONG_ARRAY);
                for (long l : longArray) {
                    arrayJson.add(new JsonPrimitive(l));
                }
                jsonObject.add(key.getKey(), arrayJson);
            }
        }
    }

    public static void handleVanillaNbtKeysForSerialization(ItemMeta meta, JsonObject jsonObject) {
        if (meta.hasDisplayName()) {
            jsonObject.add("custom_name", new JsonPrimitive(GsonComponentSerializer.gson().serialize(meta.displayName())));
        }

        if (meta.hasLore()) {
            JsonArray loreArray = new JsonArray();
            meta.lore().forEach(component -> loreArray.add(new JsonPrimitive(GsonComponentSerializer.gson().serialize(component))));
            jsonObject.add("lore", loreArray);
        }

        if (meta.hasEnchants()) {
            JsonObject enchants = new JsonObject();
            meta.getEnchants().forEach((enchantment, level) -> {
                enchants.addProperty(enchantment.getKey().toString(), level);
            });
            jsonObject.add("enchants", enchants);
        }

        if (meta.hasAttributeModifiers()) {
            JsonObject attributeModifiers = new JsonObject();
            Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();
            if (modifiers != null) {
                for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
                    Attribute attribute = entry.getKey();
                    AttributeModifier modifier = entry.getValue();
                    JsonObject modJson = new JsonObject();
                    modifier.serialize().forEach((key, value) -> {
                        if (value instanceof Number) {
                            modJson.addProperty(key, (Number) value);
                        } else if (value instanceof Boolean) {
                            modJson.addProperty(key, (Boolean) value);
                        } else {
                            modJson.addProperty(key, value.toString());
                        }
                    });
                    attributeModifiers.add(attribute.name().toLowerCase(), modJson);
                }
            }
            jsonObject.add("attribute_modifiers", attributeModifiers);
        }

        if (meta.hasCustomModelData()) {
            jsonObject.addProperty("custom_model_data", meta.getCustomModelData());
        }

        if (meta.isUnbreakable()) {
            jsonObject.addProperty("unbreakable", meta.isUnbreakable());
        }

        if (meta instanceof Damageable) {
            int damage = ((Damageable) meta).getDamage();
            jsonObject.addProperty("damage", damage);
        }

        if (!meta.getItemFlags().isEmpty()) {
            int hideFlags = 0;
            for (ItemFlag flag : meta.getItemFlags()) {
                hideFlags |= flag.ordinal();
            }
            jsonObject.addProperty("hide_flags", hideFlags);
        }

        if (meta instanceof Repairable) {
            int repairCost = ((Repairable) meta).getRepairCost();
            jsonObject.addProperty("repair_cost", repairCost);
        }
    }


    public static List<Category> randomSelectCategories(List<Category> originalList, int maxCategories) {
        Random random = new Random();
        List<Category> copiedList = new ArrayList<>(originalList); // Создаем поверхностную копию списка
        List<Category> selectedCategories = new ArrayList<>();

        // Добавляем первую категорию по умолчанию и удаляем ее из копированного списка
        List<Category> buyerCategories = GuiType.getCategories(GuiType.BUYER);
        if (!buyerCategories.isEmpty()) {
            Category defaultCategory = buyerCategories.get(0);
            selectedCategories.add(defaultCategory);
            copiedList.remove(defaultCategory);
        }

        // Дополняем список случайными категориями до максимального числа, не включая первую категорию
        while (selectedCategories.size() < maxCategories && !copiedList.isEmpty()) {
            int randomIndex = random.nextInt(copiedList.size());
            selectedCategories.add(copiedList.remove(randomIndex));
        }

        return selectedCategories;
    }


    public static int countItemsConsideringNBT(Inventory inventory, ItemStack itemToMatch) {
        int count = 0;
        boolean hasNBT = RemoveItems.hasNBTData(itemToMatch);

        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                if (hasNBT && item.isSimilar(itemToMatch)) {
                    count += item.getAmount();
                } else if (!hasNBT && item.getType() == itemToMatch.getType() && !RemoveItems.hasNBTData(item)) {
                    count += item.getAmount();
                }
            }
        }
        return count;
    }


}
