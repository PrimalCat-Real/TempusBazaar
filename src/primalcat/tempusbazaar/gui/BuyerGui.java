package primalcat.tempusbazaar.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import primalcat.tempusbazaar.category.Category;
import primalcat.tempusbazaar.category.atributes.Product;
import primalcat.tempusbazaar.trades.*;
import primalcat.tempusbazaar.utils.DisplayUtil;
import primalcat.tempusbazaar.utils.StackBuilder;

import java.util.*;

public class BuyerGui extends CustomGui{
    public BuyerGui(Player player, int invSize, List<Category> categories, Component title) {
        super(player, invSize, categories, title);
    }


    private Map<String, List<Component>> loreCache = new HashMap<>();

    private List<Component> getOrCreateLore(Product product) {
        String key = product.getRarity() + "|" + product.getPrice().getCurrent() + "|" + product.getCount().getCurrent();
        if (!loreCache.containsKey(key)) {
            List<Component> lore = Arrays.asList(
                    DisplayUtil.miniMessage.deserialize("<i:false><gray>Редкость: </gray></i>").append(product.getRarity()),
                    Component.empty(),
                    DisplayUtil.miniMessage.deserialize("<i:false><gradient:#2CFD74:#3BFF49><bold>ЛКМ</bold> <gray>- Совершить продажу</gray></gradient></i>"),
                    DisplayUtil.miniMessage.deserialize("<i:false><gradient:#2CFD74:#3BFF49><bold>ЛКМ</bold><gray>+</gray><bold>ШАЛКЕР</bold> <gray>- Продать с шалкера</gray></gradient></i>"),
                    DisplayUtil.miniMessage.deserialize("<i:false><gradient:#2CFD74:#3BFF49><bold>ШИФТ</bold><gray>+</gray><bold>ЛКМ</bold> <gray>- Продать все</gray></gradient></i>"),
                    Component.empty(),
                    DisplayUtil.miniMessage.deserialize("<i:false><gray>Цена продажи: <gold>" + product.getPrice().getCurrent() + "T</gold></gray></i>"),
                    DisplayUtil.miniMessage.deserialize("<i:false><gray>Количество для продажи: <gold>" + product.getCount().getCurrent() + "шт.</gold></gray></i>")
            );
            loreCache.put(key, lore);
        }
        return loreCache.get(key);
    }
    @Override
    public void updateProducts() {
        int productIndex = 0; // Индекс для перебора продуктов
        List<Product> productList = new ArrayList<>(this.selectedCategory.getProducts()); // Список продуктов из map
        for (int row = 1; row < getSlots() / 9 - 1; row++) { // Пропускаем первый и последний ряды
            for (int col = 2; col < 8; col++) { // Пропускаем первую и последнюю колонки
                int slotIndex = row * 9 + col;
                if(productIndex < productList.size()){
                    Product product = productList.get(productIndex);
                    List<Component> lore = getOrCreateLore(product);
                    this.button(slotIndex,
                            new StackBuilder(product.getItem()).withEditMeta(meta ->
                                    {
                                        meta.lore(lore);
                                    })
                                    .getStack(),
                            (click, event) -> {
                                if(event.getCursor().getType().toString().endsWith("SHULKER_BOX")){
                                    if (event.getCursor().hasItemMeta()) {
                                        BlockStateMeta meta = (BlockStateMeta) event.getCursor().getItemMeta();
                                        if (meta.getBlockState() instanceof ShulkerBox) {
                                            ShulkerBox shulker = (ShulkerBox) meta.getBlockState();
                                            Inventory temporaryInventory = Bukkit.createInventory(null, 27, ChatColor.RED + "Briefcase");

                                            // Загружаем содержимое шалкера во временный инвентарь
                                            temporaryInventory.setContents(shulker.getInventory().getContents());

                                            // Удаляем предметы из шалкера
                                            SellItems.sellItemsFromShulkerBox(player, temporaryInventory, product);

                                            // Обновляем содержимое шалкера
                                            shulker.getInventory().setContents(temporaryInventory.getContents());
                                            meta.setBlockState(shulker);
                                            event.getCursor().setItemMeta(meta);
                                        }
                                    }
                                } else if (event.isShiftClick()) {
                                    SellItems.sellALLItems(player, this.player.getInventory(), product);
                                }else {
                                    SellItems.sellItemsWrapper(player, this.player.getInventory(), product, product.getCount().getCurrent());
                                }
                                // для баланса вселенной
                                Random random = new Random();
                                if (random.nextInt(100) > 95) {  // 10% вероятность на выполнение
                                    ProductFinder.findTopHighestAmountProducts(GuiType.getCategories(GuiType.BUYER));
                                    ProductFinder.findTopLowestPricedProducts(GuiType.getCategories(GuiType.BUYER));
                                }
                            });
                    productIndex+=1;
                }else {
                    this.decorationButton(slotIndex, new ItemStack(Material.AIR));

                }
            }
        }
    }
}
