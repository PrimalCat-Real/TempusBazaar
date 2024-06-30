package primalcat.tempusbazaar.trades;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import primalcat.tempusbazaar.TempusBazaar;
import primalcat.tempusbazaar.category.atributes.Product;
import primalcat.tempusbazaar.gui.SellerGui;
import primalcat.tempusbazaar.utils.DataUtil;
import primalcat.tempusbazaar.utils.DisplayUtil;
import primalcat.tempusbazaar.utils.StackBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BuyItems {
    public static boolean buyItemsWrapper(Player player, Inventory inventory, Product product, int amountToRemove) {
        boolean isBuySuccessful = buyItems(player, inventory, product, amountToRemove);
        int buyPrice = product.getPrice().getCurrent();
        if (isBuySuccessful) {
            // Проиграть звук подтверждения
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
            // Записать историю покупки
            addPurchase(player, LocalDateTime.now(), amountToRemove, buyPrice, product.getItem());
            return true;
        } else {
            // Проиграть звук отказа
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return false;
        }
    }


    public static boolean buyItems(Player player, Inventory inventory, Product product, int amountToRemove) {
        if (product.getCount().getCurrent() == 0) {
            return false; // Нельзя покупать, если количество продукта равно нулю
        }

        int moneyToRemove = product.getPrice().getCurrent();
        ItemStack moneyItem = TempusCoins.createTempus(moneyToRemove);

        boolean itemsRemoved = RemoveItems.removeItemsConsideringNBT(moneyItem, amountToRemove, inventory, player);

        if (itemsRemoved) {
            // Выдать продукт игроку
            ItemStack productItem = product.getItem();
            GiveItems.giveItemOrDrop(player, new StackBuilder(productItem.getType()).withEditMeta(meta -> {
                ItemMeta productMeta = productItem.getItemMeta();
                if (productMeta != null) {
                    PersistentDataContainer productData = productMeta.getPersistentDataContainer();
                    PersistentDataContainer newMetaData = meta.getPersistentDataContainer();
                    for (NamespacedKey key : productData.getKeys()) {
                        PersistentDataType type = productData.get(key, PersistentDataType.TAG_CONTAINER) != null ? PersistentDataType.TAG_CONTAINER : PersistentDataType.STRING;
                        newMetaData.set(key, type, productData.get(key, type));
                    }
                }
            }).getStack(), amountToRemove);

//            try {
//                sendPurchaseInfoToDiscord(player, product, amountToRemove, product.getPrice().getCurrent());
//            }catch (Exception e) {
//                // Логирование ошибки, если необходимо
//                e.printStackTrace(); // Это опционально, только для отладки
//            }

            // Уменьшить количество и увеличить цену
            product.decreaseCount0();
            product.increasePrice0();

            return true;
        } else {
            return false;
        }
    }

    public static boolean buyALLItems(Player player, Inventory inventory, Product product) {
        int totalMoneySpent = 0;  // Общее количество потраченных денег
        int totalBought = 0;  // Общее количество купленных предметов
        int moneyToRemove = product.getPrice().getCurrent();

        // Подсчитать доступные деньги у игрока
        int availableMoney = DataUtil.countItemsConsideringNBT(inventory, TempusCoins.createTempus());
        int availableMoney10 = DataUtil.countItemsConsideringNBT(inventory, TempusCoins.createTempus10()) * 10;
        int totalAvailableMoney = availableMoney + availableMoney10;
        boolean anyPurchaseSuccessful = false;

        while (totalAvailableMoney >= moneyToRemove) {
            boolean isBuySuccessful = buyItems(player, inventory, product, 1);

            if (isBuySuccessful) {
                totalMoneySpent += moneyToRemove;  // Обновить общую сумму потраченных денег
                totalBought += 1;  // Обновить общее количество купленных предметов
                anyPurchaseSuccessful = true;

                // Подсчитать оставшиеся деньги у игрока после покупки
                availableMoney = DataUtil.countItemsConsideringNBT(inventory, TempusCoins.createTempus());
                availableMoney10 = DataUtil.countItemsConsideringNBT(inventory, TempusCoins.createTempus10()) * 10;
                totalAvailableMoney = availableMoney + availableMoney10;
            } else {
                // Прекратить покупку, если метод buyItems вернул false
                break;
            }
        }

        if (anyPurchaseSuccessful) {
            // Проиграть звук подтверждения
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
        } else {
            // Проиграть звук отказа
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }

        // Записать историю покупки
        addPurchase(player, LocalDateTime.now(), totalBought, totalMoneySpent, product.getItem());

        // Вывести итоговые данные
//        System.out.println("Total money spent: " + totalMoneySpent);
//        System.out.println("Total items bought: " + totalBought);
//        System.out.println("Current price: " + product.getPrice().getCurrent());
//        System.out.println("Current count for sale: " + product.getCount().getCurrent());

        return totalBought > 0;
    }
    public static void addPurchase(Player playerName, LocalDateTime purchaseTime, int amountPurchased, int moneySpent, ItemStack itemPurchased) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<Component> purchaseRecord = new ArrayList<>();
        purchaseRecord.add(DisplayUtil.miniMessage.deserialize("<i:false><gray>Игрок: </gray>").append(playerName.displayName().color(TextColor.color(0x00AAAA)).decoration(TextDecoration.ITALIC, false)));
        purchaseRecord.add(DisplayUtil.miniMessage.deserialize("<i:false><gray>Купленный предмет: </gray>").append(itemPurchased.displayName().color(TextColor.color(0xcd1d5f))));
        purchaseRecord.add(DisplayUtil.miniMessage.deserialize("<i:false><gray>Время: <gradient:#2CFD74:#3BFF49>" + purchaseTime.format(formatter) + "</gradient></gray>"));
        purchaseRecord.add(DisplayUtil.miniMessage.deserialize("<i:false><gray>Количество покупки: <gold>" + amountPurchased + "</gold></gray>"));
        purchaseRecord.add(DisplayUtil.miniMessage.deserialize("<i:false><gray>Потрачено денег: <gold>" + moneySpent + "</gold></gray>"));
        purchaseRecord.add(Component.empty());

        SellerGui.history.add(purchaseRecord);

        // Ограничиваем размер истории до последних 10 записей
        if (SellerGui.history.size() > 10) {
            SellerGui.history = SellerGui.history.subList(SellerGui.history.size() - 10, SellerGui.history.size());
        }
    }

    public static void sendPurchaseInfoToDiscord(Player player, Product product, int amountPurchased, int moneySpent) {
        try {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(java.awt.Color.decode("#ff8f38"))
                    .setThumbnail("https://skins.subland.fun/resources/server/skinRender.php?format=png&user=" + player.getName() + "&vr=-15&hrh=-10&headOnly=true&aa=true&ratio=25")
                    .addField("", "Транзакция игрока " + player.getName() + ":", false)
                    .addField("\u1CBCКуплено:", "\u1CBC\u1CBC\u1CBC**" + amountPurchased + " " + product.getItem().getItemMeta().getDisplayName() + "**", false)
                    .addField("\u1CBCПотрачено:", "\u1CBC\u1CBC\u1CBC**" + moneySpent + " Т**", false)
                    .build();
            Message message = new MessageBuilder()
                    .setEmbeds(embed)
                    .build();
            DiscordUtil.queueMessage(DiscordUtil.getTextChannelById("1207187897560866816"), message);
        } catch (Exception e) {
            TempusBazaar.logger.warning("Cannot send discord log for player: " + player.getName() + " action with gui");
        }
    }
}
