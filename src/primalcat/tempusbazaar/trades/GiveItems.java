package primalcat.tempusbazaar.trades;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GiveItems {
    public static void giveItemOrDrop(Player player, ItemStack itemStack, int quantity) {
        itemStack.setAmount(quantity);
        HashMap<Integer, ItemStack> notFittedItems = player.getInventory().addItem(itemStack);

        // Если предметы не поместились в инвентарь, то выкидываем их на землю возле игрока.
        if (!notFittedItems.isEmpty()) {
            for (ItemStack leftover : notFittedItems.values()) {
                Location location = player.getLocation();
                player.getWorld().dropItemNaturally(location, leftover);
            }
        }
    }
}
