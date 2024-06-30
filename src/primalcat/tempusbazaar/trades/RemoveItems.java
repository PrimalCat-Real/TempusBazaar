package primalcat.tempusbazaar.trades;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import primalcat.tempusbazaar.TempusBazaar;

import java.util.Arrays;

public class RemoveItems {

    private static final NamespacedKey MARK_KEY = new NamespacedKey(TempusBazaar.getPlugin(TempusBazaar.class), "remove_mark");
    /**
     * Удаляет указанное количество предметов из инвентаря, учитывая все метаданные и NBT данные.
     *
     * @param itemToMatch     образец предмета, который нужно удалить (включая метаданные и NBT данные)
     * @param amountToRemove  количество предметов, которое нужно удалить
     * @param inventory       инвентарь, из которого нужно удалить предметы
     * @param player          игрок, для которого воспроизводится звук подтверждения или отказа
     * @return                true, если предметы успешно удалены, иначе false
     */
    public static boolean removeSpecificItemsWithMarking(ItemStack itemToMatch, Integer amountToRemove, Inventory inventory, Player player) {
        int totalMarked = 0;

        // Пометить предметы и подсчитать общее количество доступных для удаления
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.isSimilar(itemToMatch) && totalMarked < amountToRemove) {
                int currentAmount = item.getAmount();
                int amountToMark = Math.min(currentAmount, amountToRemove - totalMarked);
                markItemForRemoval(item, amountToMark);
                totalMarked += amountToMark;
                if (totalMarked >= amountToRemove) break;
            }
        }

        // Если не достаточно предметов для удаления, воспроизвести звук и выйти
        if (totalMarked < amountToRemove) {
            removeMarks(inventory);
            return false;
        }

        int remainingAmountToRemove = amountToRemove;

        // Удалить помеченные предметы из инвентаря
        for (ItemStack item : inventory.getContents()) {
            if (item != null && isMarkedForRemoval(item)) {
                remainingAmountToRemove = removeMarkedItem(item, remainingAmountToRemove);
                if (remainingAmountToRemove <= 0) break;
            }
        }

        removeMarks(inventory);
        player.updateInventory(); // Обновить инвентарь после удаления предметов
        return true;
    }

    /**
     * Удаляет указанное количество предметов из инвентаря по типу материала.
     *
     * @param material        тип материала предметов, которые нужно удалить
     * @param amountToRemove  количество предметов, которое нужно удалить
     * @param inventory       инвентарь, из которого нужно удалить предметы
     * @param player          игрок, для которого воспроизводится звук подтверждения или отказа
     * @return                true, если предметы успешно удалены, иначе false
     */
    public static boolean removeItemsWithMarking(Material material, Integer amountToRemove, Inventory inventory, Player player) {
        int totalMarked = 0;

        // Пометить предметы и подсчитать общее количество доступных для удаления
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material && totalMarked < amountToRemove) {
                int currentAmount = item.getAmount();
                int amountToMark = Math.min(currentAmount, amountToRemove - totalMarked);
                markItemForRemoval(item, amountToMark);
                totalMarked += amountToMark;
                if (totalMarked >= amountToRemove) break;
            }
        }

        // Если не достаточно предметов для удаления, воспроизвести звук и выйти
        if (totalMarked < amountToRemove) {
            removeMarks(inventory);
            return false;
        }

        int remainingAmountToRemove = amountToRemove;

        // Удалить помеченные предметы из инвентаря
        for (ItemStack item : inventory.getContents()) {
            if (item != null && isMarkedForRemoval(item)) {
                remainingAmountToRemove = removeMarkedItem(item, remainingAmountToRemove);
                if (remainingAmountToRemove <= 0) break;
            }
        }

        removeMarks(inventory);
        player.updateInventory(); // Обновить инвентарь после удаления предметов
        return true;
    }

    private static void markItemForRemoval(ItemStack item, int amount) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(MARK_KEY, PersistentDataType.INTEGER, amount);
            item.setItemMeta(meta);
        }
    }

    private static boolean isMarkedForRemoval(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            return container.has(MARK_KEY, PersistentDataType.INTEGER);
        }
        return false;
    }

    private static int removeMarkedItem(ItemStack item, int amountToRemove) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        int markedAmount = container.getOrDefault(MARK_KEY, PersistentDataType.INTEGER, 0);

        int currentAmount = item.getAmount();
        int amountToRemoveNow = Math.min(currentAmount, amountToRemove);

        if (currentAmount > amountToRemoveNow) {
            item.setAmount(currentAmount - amountToRemoveNow);
            return amountToRemove - amountToRemoveNow; // Все предметы удалены, вернуть остаток
        } else {
            item.setAmount(0);
            item.setType(Material.AIR); // Явно устанавливаем тип в AIR, если предмет полностью удален
            return amountToRemove - currentAmount; // Вернуть оставшееся количество для удаления
        }
    }

    private static void removeMarks(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && isMarkedForRemoval(item)) {
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();
                container.remove(MARK_KEY);
                item.setItemMeta(meta);
            }
        }
    }

    /**
     * Удаляет предметы из инвентаря с учетом наличия NBT данных.
     *
     * @param itemToMatch     образец предмета, который нужно удалить
     * @param amountToRemove  количество предметов, которое нужно удалить
     * @param inventory       инвентарь, из которого нужно удалить предметы
     * @param player          игрок, для которого воспроизводится звук подтверждения или отказа
     * @return                true, если предметы успешно удалены, иначе false
     */
    public static boolean removeItemsConsideringNBT(ItemStack itemToMatch, Integer amountToRemove, Inventory inventory, Player player) {
        boolean hasNBT = hasNBTData(itemToMatch);

        if (hasNBT) {
            return removeSpecificItemsWithMarking(itemToMatch, amountToRemove, inventory, player);
        } else {
            return removeItemsWithMarking(itemToMatch.getType(), amountToRemove, inventory, player);
        }
    }

    /**
     * Проверяет, содержит ли предмет NBT данные (исключая название и лор).
     *
     * @param item  предмет для проверки
     * @return      true, если предмет содержит NBT данные, иначе false
     */
    public static boolean hasNBTData(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        // Проверяем наличие только зачарований и custom model data
        if (meta.hasEnchants() || meta.hasCustomModelData()) {
            return true;
        }

        // Проверяем наличие пользовательских данных в PersistentDataContainer
        PersistentDataContainer container = meta.getPersistentDataContainer();
        for (NamespacedKey key : container.getKeys()) {
            if (!key.equals(MARK_KEY)) {
                return true;
            }
        }
        return false;
    }
}
