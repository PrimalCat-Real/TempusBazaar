package primalcat.tempusbazaar.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import primalcat.tempusbazaar.category.Category;
import primalcat.tempusbazaar.trades.GiveItems;
import primalcat.tempusbazaar.trades.RemoveItems;
import primalcat.tempusbazaar.trades.TempusCoins;
import primalcat.tempusbazaar.utils.StackBuilder;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomGui extends InventoryGui{
    public int invSize;
    public Component title;
    public Category selectedCategory;
    public List<Category>  originalCategories;

    public static List<List<Component>> history = new ArrayList<>();

    private List<Component> exchangeTen = Arrays.asList(
            Component.empty(),
            Component.text("   Обмен купюры номиналом в 10 Темпусов", TextColor.color(255, 255, 255)).decoration(TextDecoration.ITALIC, false),
            Component.text(" на 10 купюр в 1 Темпус.", TextColor.color(255, 255, 255)).decoration(TextDecoration.ITALIC, false),
            Component.empty()
    );

    private List<Component> exchangeOne = Arrays.asList(
            Component.empty(),
            Component.text("   Обмен 10 купюр номиналом в 1 Темпус", TextColor.color(255, 255, 255)).decoration(TextDecoration.ITALIC, false),
            Component.text(" на купюру в 10 Темпусов.", TextColor.color(255, 255, 255)).decoration(TextDecoration.ITALIC, false),
            Component.empty()
    );

    private List<Component> selectLore = Arrays.asList(
            Component.empty(),
            MiniMessage.miniMessage().deserialize("<i:false><gradient:#D658E4:#E678F2>Выбраная категория</gradient></i>"),
            Component.empty()
    );

    private List<Component> unSelectLore = Arrays.asList(
            Component.empty(),
            MiniMessage.miniMessage().deserialize("<i:false><gradient:#E4CC58:#F2E178>Нажми, чтобы выбрать</gradient></i>"),
            Component.empty()
    );




    public CustomGui(Player player, int invSize, List<Category> categories, Component title) {
        super(player);
        this.invSize = invSize;
        this.selectedCategory = categories.get(0);
        this.originalCategories = categories;
        this.title = title;
//        this.inventory = player.getServer().createInventory(player, invSize, getTitle());
    }
    @Override
    public void render() {
        updateCategories();
        updateProducts();
        updateFeatureButtons();
        updateBorders();
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getSlots() {
        return invSize;
    }

    public void updateCategories() {
        int i = 0;
//        System.out.println(originalCategories);
        for (Category category : originalCategories) {
            int slotIndex = i * 9; // Calculate slot index for the first column (0, 9, 18, 27, ...)
            if (slotIndex < getSlots()) { // Check if the slot index is within the inventory size
                if(category == this.selectedCategory){
                    this.button(slotIndex,
                            (new StackBuilder(category.getItem()).withEditMeta(
                                    meta -> {
                                        meta.addEnchant(Enchantment.LUCK, 1, true); // Добавление чара
                                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);  // Скрытие информации о чарах
                                        List<Component> lore  = meta.lore();
//
                                        if (lore != null) {
                                            lore.removeAll(selectLore);
                                            lore.removeAll(unSelectLore);

                                            lore.addAll(selectLore);
                                        }

                                        meta.lore(lore);
                                    }
                            ).getStack()),
                            (click, event) -> {
//                                this.selectedCategory = category;
//                                updateCategories(); // Обновляем категории после выбора новой
                            });
                }

                if(category != this.selectedCategory){
                    this.button(slotIndex,

                            (new StackBuilder(category.getItem()).withEditMeta(meta -> {
                                List<Component> lore  = meta.lore();
                                meta.removeEnchant(Enchantment.LUCK);
                                if(lore != null){
                                    lore.removeAll(selectLore);
                                    lore.removeAll(unSelectLore);
                                    lore.addAll(unSelectLore);
                                }
                                meta.lore(lore);
                            }).getStack()),
                            (click, event) -> {
                        this.selectedCategory = category;
                        updateCategories(); // Обновляем категории после выбора новой
                    });
                }
            }
            i++; // Increment index after processing each category
        }
    }

    public void updateProducts() {
    }

    public void updateFeatureButtons(){
        int rows = getSlots() / 9; // Вычисляем количество рядов, основываясь на общем количестве слотов
        int startOfLastRow = (rows - 1) * 9; // Начало последнего ряда
        for (int slotIndex = startOfLastRow; slotIndex < startOfLastRow + 9; slotIndex++) {
            switch (slotIndex) {
                case 47:
                    this.button(slotIndex, new StackBuilder(TempusCoins.createTempus()).withEditMeta(
                            meta -> {
                                List<Component> lore = exchangeOne;
                                meta.lore(lore);
                            }
                    ).getStack(), (click, event) -> {
                        Player player = (Player) event.getWhoClicked();
                        Inventory inventory = player.getInventory();
                        ItemStack tenTempusBill = TempusCoins.createTempus10(); // Купюра номиналом 10 Темпусов

                        // Пытаемся удалить 10 купюр номиналом в 1 Темпус
                        if (RemoveItems.removeItemsConsideringNBT(TempusCoins.createTempus(), 10, inventory, player)) {
                            // Добавляем 1 купюру номиналом в 10 Темпусов
                            GiveItems.giveItemOrDrop(player, tenTempusBill, 1);
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
                        } else {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                        }
                    }); // Здесь ваш код для индекса 52
                    break;
                case 48:
                    this.button(slotIndex, new StackBuilder(TempusCoins.createTempus10()).withEditMeta(
                            meta -> {
                                List<Component> lore = exchangeTen;
                                meta.lore(lore);
                            }
                    ).getStack(), (click, event) -> {
                        Player player = (Player) event.getWhoClicked();
                        Inventory inventory = player.getInventory();
                        ItemStack oneTempusBill = TempusCoins.createTempus(); // Купюра номиналом 1 Темпус

                        // Пытаемся удалить 1 купюру номиналом в 10 Темпусов
                        if (RemoveItems.removeItemsConsideringNBT(TempusCoins.createTempus10(), 1, inventory, player)) {
                            // Добавляем 10 купюр номиналом в 1 Темпус
                            for (int i = 0; i < 10; i++) {
                                GiveItems.giveItemOrDrop(player, oneTempusBill, 1);
                            }
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
                        } else {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                        }
                    }); // Здесь ваш код для индекса 52
                    break;
                case 50:
                    this.button(slotIndex, new StackBuilder(Material.BOOK).withEditMeta(
                            meta -> {
                                List<Component> lore = new ArrayList<>();
                                if (history.isEmpty()) {
                                    lore.add(Component.text("История продаж отсутствует.").decoration(TextDecoration.ITALIC, false));
                                } else {
                                    for (List<Component> saleRecord : history) {
                                        lore.addAll(saleRecord.subList(0, Math.min(saleRecord.size(), 7)));
                                        lore.add(Component.empty());
                                    }
                                }
                                meta.lore(lore);
                            }
                    ).withDisplayName(Component.text("История сделок").decoration(TextDecoration.ITALIC, false)).getStack(), (click, event) -> {
                        // Здесь ваш код для обработки нажатия на кнопку
                    });
                    break;

                default:
                    break;
            }
        }

    }

    private void updateBorders() {
        // Количество рядов в инвентаре

        int rows = getSlots() / 9;
        ItemStack borderItem = selectedCategory.getBorder().getItem();

////        // Заполнение левой колонки, начиная со второго ряда
        for (int i = 1; i < rows; i++) {
            this.decorationButton(i * 9 + 1, borderItem);
        }

        // Заполнение правой колонки, начиная со второго ряда
        for (int i = 1; i < rows; i++) {
            this.decorationButton(i * 9 + 8, borderItem);
        }

        // Заполнение верхней строки, начиная со второго столбца
        for (int i = 1; i < 9; i++) {
            this.decorationButton(i, borderItem);
        }
    }

}
