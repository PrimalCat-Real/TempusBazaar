package primalcat.tempusbazaar.gui;


import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import primalcat.tempusbazaar.TempusBazaar;

import java.util.HashMap;
import java.util.function.BiConsumer;

public abstract class InventoryGui implements Listener {
    private final HashMap<Integer, ItemStack> stacks = new HashMap<>();
    private final HashMap<Integer, BiConsumer<ItemStack, InventoryClickEvent>> buttons = new HashMap<>();
    protected final Player player;
    private Inventory inventory;
    private BukkitTask refreshTask;

    public InventoryGui(Player player) {
        this.player = player;
    }

    public abstract void render();

    public abstract Component getTitle();

    public abstract int getSlots();

    public void refresh() {
        stacks.clear();
        buttons.clear();
        inventory.clear();

        render();

        stacks.forEach((slot, stack) -> inventory.setItem(slot, stack));
    }

    public void open() {
        Bukkit.getPluginManager().registerEvents(this, TempusBazaar.getPlugin());

        inventory = Bukkit.createInventory(null, getSlots(), getTitle());
        refreshTask = Bukkit.getScheduler().runTaskTimerAsynchronously(TempusBazaar.getPlugin(), this::refresh, 1, 1);

        player.openInventory(inventory);
    }

    public void decorationButton(int i, ItemStack stack) {
        button(i, stack, (stack1, event) -> {});
    }

    public void button(int i, ItemStack stack, BiConsumer<ItemStack, InventoryClickEvent> onClick) {
        buttons.put(i, onClick);
        stacks.put(i, stack);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(event.getInventory() == inventory) close();
    }


//    @EventHandler
//    public void onInventoryDragEvent(InventoryDragEvent  event){
//
//    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getView().getTopInventory() == inventory && event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)){
            event.setCancelled(true);
        }
        if (event.getClickedInventory() != inventory){
            return;
        }
        event.setCancelled(true);
        buttons.computeIfPresent(event.getSlot(), (slot, btn) -> {
            btn.accept(stacks.get(slot), event);
            return btn;
        });

    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event) {
        if (event.getInventory() == inventory) event.setCancelled(true);
    }

    public void close() {
        refreshTask.cancel();
        HandlerList.unregisterAll(this);
        inventory.clear();
        inventory.close();
    }
}
