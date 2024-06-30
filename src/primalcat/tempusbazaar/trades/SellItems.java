package primalcat.tempusbazaar.trades;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import primalcat.tempusbazaar.TempusBazaar;
import primalcat.tempusbazaar.category.atributes.Product;
import primalcat.tempusbazaar.gui.BuyerGui;
import primalcat.tempusbazaar.utils.DataUtil;
import primalcat.tempusbazaar.utils.DisplayUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class SellItems {

    public static boolean sellItemsWrapper(Player player, Inventory inventory, Product product, int amountToRemove){
        boolean isSellSuccessful = sellItems(player,inventory,product, amountToRemove);
        int sellPrice = product.getPrice().getCurrent();
        if(isSellSuccessful){
            // Проиграть звук подтверждения
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
            // Записать историю продажи
            addSale(player, LocalDateTime.now(), amountToRemove, sellPrice, product.getItem());
            return true;
        }else{
            // Проиграть звук отказа
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return false;
        }
    }
    public static boolean sellItems(Player player, Inventory inventory, Product product, int amountToRemove) {
        ItemStack itemToRemove = product.getItem();
        int moneyToGive = product.getPrice().getCurrent();

        boolean itemsRemoved = RemoveItems.removeItemsConsideringNBT(itemToRemove, amountToRemove, inventory, player);

        if (itemsRemoved) {
            // Выдать деньги игроку
            ItemStack moneyItem = TempusCoins.createTempus(moneyToGive);
            GiveItems.giveItemOrDrop(player, moneyItem, moneyToGive);

//            try {
//                sendSaleInfoToDiscord(player, product, amountToRemove, product.getPrice().getCurrent());
//            }catch (Exception e) {
//                // Логирование ошибки, если необходимо
//                e.printStackTrace(); // Это опционально, только для отладки
//            }
            // Увеличить количество и уменьшить цену
            product.increaseCount();
            product.decreasePrice();

            return true;
        } else {
            return false;
        }
    }
    public static boolean sellALLItems(Player player, Inventory inventory, Product product) {
        int amountToRemove = product.getCount().getCurrent();
        ItemStack itemToRemove = product.getItem();
        int moneyToGive = product.getPrice().getCurrent();

        int totalMoneyEarned = 0;  // Общее количество заработанных денег
        int totalSold = 0;  // Общее количество проданных предметов

        // Подсчитать доступные предметы в инвентаре
        int availableItems = DataUtil.countItemsConsideringNBT(player.getInventory(), itemToRemove);

        while (availableItems >= amountToRemove) {
            boolean itemsRemoved = RemoveItems.removeItemsConsideringNBT(itemToRemove, amountToRemove, inventory, player);

            if (itemsRemoved) {
                // Выдать деньги игроку
                ItemStack moneyItem = TempusCoins.createTempus(moneyToGive);
                GiveItems.giveItemOrDrop(player, moneyItem, moneyToGive);

                // Увеличить количество и уменьшить цену
                product.increaseCount();
                product.decreasePrice();

                // Проиграть звук подтверждения
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);

                // Обновить количество доступных предметов
                availableItems -= amountToRemove;
                totalMoneyEarned += moneyToGive;  // Обновить общую сумму заработанных денег
                totalSold += amountToRemove;  // Обновить общее количество проданных предметов
                amountToRemove = product.getCount().getCurrent();
                moneyToGive = product.getPrice().getCurrent();
            } else {
                // Проиграть звук отказа
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return false;
            }
        }

        // Если недостаточно предметов, проиграть звук отказа
        if (availableItems < amountToRemove) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }

        // Записать историю продажи
        addSale(player, LocalDateTime.now(), totalSold, totalMoneyEarned, product.getItem());


        // Вывести итоговые данные
//        System.out.println("Total money earned: " + totalMoneyEarned);
//        System.out.println("Total items sold: " + totalSold);
//        System.out.println("Remaining items: " + availableItems);
//        System.out.println("Current price: " + product.getPrice().getCurrent());
//        System.out.println("Current count for sale: " + product.getCount().getCurrent());

        return true;
    }

    public static boolean sellItemsFromShulkerBox(Player player, Inventory shulkerInventory, Product product) {
        int amountToRemove = product.getCount().getCurrent();
        ItemStack itemToRemove = product.getItem();
        int moneyToGive = product.getPrice().getCurrent();

        int totalMoneyEarned = 0;  // Общее количество заработанных денег
        int totalSold = 0;  // Общее количество проданных предметов

        // Подсчитать доступные предметы в инвентаре шалкера
        int availableItems = DataUtil.countItemsConsideringNBT(shulkerInventory, itemToRemove);

        while (availableItems >= amountToRemove) {
            boolean itemsRemoved = RemoveItems.removeItemsConsideringNBT(itemToRemove, amountToRemove, shulkerInventory, player);

            if (itemsRemoved) {
                // Выдать деньги игроку
                ItemStack moneyItem = TempusCoins.createTempus(moneyToGive);
                GiveItems.giveItemOrDrop(player, moneyItem, moneyToGive);

                // Увеличить количество и уменьшить цену
                product.increaseCount();
                product.decreasePrice();

                // Проиграть звук подтверждения
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);

                // Обновить количество доступных предметов
                availableItems -= amountToRemove;
                totalMoneyEarned += moneyToGive;  // Обновить общую сумму заработанных денег
                totalSold += amountToRemove;  // Обновить общее количество проданных предметов
                amountToRemove = product.getCount().getCurrent();
                moneyToGive = product.getPrice().getCurrent();
            } else {
                // Проиграть звук отказа
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return false;
            }
        }

        // Если недостаточно предметов, проиграть звук отказа
        if (availableItems < amountToRemove) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }

        // Записать историю продажи
        addSale(player, LocalDateTime.now(), totalSold, totalMoneyEarned, product.getItem());

        // Вывести итоговые данные
//        System.out.println("Total money earned: " + totalMoneyEarned);
//        System.out.println("Total items sold: " + totalSold);
//        System.out.println("Remaining items: " + availableItems);
//        System.out.println("Current price: " + product.getPrice().getCurrent());
//        System.out.println("Current count for sale: " + product.getCount().getCurrent());

        return true;
    }




    public static void addSale(Player playerName, LocalDateTime saleTime, int amountSold, int moneyEarned, ItemStack itemSold) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


        List<Component> saleRecord = new ArrayList<>();
        saleRecord.add(DisplayUtil.miniMessage.deserialize("<i:false><gray>Игрок: </gray>").append(playerName.displayName().color(TextColor.color(0x00AAAA)).decoration(TextDecoration.ITALIC, false)));
        saleRecord.add(DisplayUtil.miniMessage.deserialize("<i:false><gray>Проданный предмет: </gray>").append(itemSold.displayName().color(TextColor.color(0xcd1d5f))));
        saleRecord.add(DisplayUtil.miniMessage.deserialize("<i:false><gray>Время: <gradient:#2CFD74:#3BFF49>" + saleTime.format(formatter) + "</gradient></gray>"));
        saleRecord.add(DisplayUtil.miniMessage.deserialize("<i:false><gray>Количество продажи: <gold>" + amountSold + "</gold></gray>"));
        saleRecord.add(DisplayUtil.miniMessage.deserialize("<i:false><gray>Получено денег: <gold>" + moneyEarned + "</gold></gray>"));
        saleRecord.add(Component.empty());

        BuyerGui.history.add(saleRecord);

        // Ограничиваем размер истории до последних 10 записей
        if (BuyerGui.history.size() > 10) {
            BuyerGui.history = BuyerGui.history.subList(BuyerGui.history.size() - 10, BuyerGui.history.size());
        }
    }

    public static void sendSaleInfoToDiscord(Player player, Product product, int amountSold, int moneyEarned) {
        try{
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(java.awt.Color.decode("#015D52"))
                    .setThumbnail("https://skins.subland.fun/resources/server/skinRender.php?format=png&user=" + player.getName() + "&vr=-15&hrh=-10&headOnly=true&aa=true&ratio=25")
                    .addField("", "Транзакция игрока " + player.getName() + ":", false)
                    .addField("\u1CBCПродано:", "\u1CBC\u1CBC\u1CBC**" + amountSold + " " + product.getItem().getItemMeta().getDisplayName() + "**", false)
                    .addField("\u1CBCПолучено:", "\u1CBC\u1CBC\u1CBC**" + moneyEarned + " Т**", false)
                    .build();
            Message message = new MessageBuilder()
                    .setEmbeds(embed)
                    .build();
            DiscordUtil.queueMessage(DiscordUtil.getTextChannelById("1207187897560866816"), message);
        } catch (Exception e){
            TempusBazaar.logger.warning("Cannot send discord log for player: " + player.getName() + " action with gui");
            e.printStackTrace();
        }

    }
}
