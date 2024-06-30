package primalcat.tempusbazaar.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import primalcat.tempusbazaar.category.Category;
import primalcat.tempusbazaar.category.atributes.Product;
import primalcat.tempusbazaar.trades.BuyItems;
import primalcat.tempusbazaar.utils.DisplayUtil;
import primalcat.tempusbazaar.utils.StackBuilder;

import java.util.*;

public class SellerGui extends CustomGui{
    public SellerGui(Player player, int invSize, List<Category> categories, Component title) {

        super(player, invSize, categories, title);

    }

    private Map<String, List<Component>> loreCache = new HashMap<>();
    private List<Component> getOrCreateLore(Product product) {
        String key = product.getRarity() + "|" + product.getPrice().getCurrent() + "|" + product.getCount().getCurrent();
        if (!loreCache.containsKey(key)) {
            List<Component> lore = Arrays.asList(
                    DisplayUtil.miniMessage.deserialize("<i:false><gray>Редкость: </gray></i>").append(product.getRarity()),
                    Component.empty(),
                    DisplayUtil.miniMessage.deserialize("<i:false><gradient:#2CFD74:#3BFF49><bold>ЛКМ</bold> <gray>- Совершить покупку</gray></gradient></i>"),
                    DisplayUtil.miniMessage.deserialize("<i:false><gradient:#2CFD74:#3BFF49><bold>ШИФТ</bold><gray>+</gray><bold>ЛКМ</bold> <gray>- Купить все</gray></gradient></i>"),
                    Component.empty(),
                    DisplayUtil.miniMessage.deserialize("<i:false><gray>Цена покупки: <gold>" + product.getPrice().getCurrent() + "T</gold></gray></i>"),
                    DisplayUtil.miniMessage.deserialize("<i:false><gray>Доступное количество: <gold>" + product.getCount().getCurrent() + "шт.</gold></gray></i>")
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
                                if (event.isShiftClick()) {
                                    BuyItems.buyALLItems(player, this.player.getInventory(), product);
                                }else {
                                    BuyItems.buyItemsWrapper(player, player.getInventory(), product, 1);
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
