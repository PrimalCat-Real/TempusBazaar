package primalcat.tempusbazaar.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primalcat.tempusbazaar.TempusBazaar;
import primalcat.tempusbazaar.category.Category;
import primalcat.tempusbazaar.gui.*;
import primalcat.tempusbazaar.utils.ConfigUtil;

import java.util.List;

public class OpenGuiCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("tempusbazaar.bazaaropen")) {
            commandSender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        Player targetPlayer;
        GuiType guiType;

        if (strings.length == 1 && commandSender instanceof Player) {
            targetPlayer = (Player) commandSender;
            guiType = GuiType.fromString(strings[0]);
        } else if (strings.length == 2) {
            targetPlayer = Bukkit.getPlayer(strings[0]);
            if (targetPlayer == null) {
                commandSender.sendMessage("Player not found: " + strings[0]);
                return true;
            }
            guiType = GuiType.fromString(strings[1]);
        } else {
            commandSender.sendMessage("Usage: /command <player> <guiType> or /command <guiType>");
            return true;
        }

        if (guiType == null) {
            commandSender.sendMessage("Unknown GUI type: " + strings[strings.length - 1]);
            return true;
        }

        switch (guiType) {
            case BUYER:
                InventoryGui buyerGui = new BuyerGui(targetPlayer, 6 * 9, GuiType.getCategories(guiType), Component.text("Торговец").color(TextColor.color(0x0341fc)));
                buyerGui.open();
                break;
            case RANDOM_BUYER:
                InventoryGui randomBuyerGui = new BuyerGui(targetPlayer, 6 * 9, GuiType.getRandomBuyerCategories(), Component.text("Скупщик").color(TextColor.color(0x0341fc)));
                randomBuyerGui.open();
                break;
            case SELLER:
                InventoryGui sellerGui = new SellerGui(targetPlayer, 6 * 9, GuiType.getCategories(guiType), Component.text("Продавец").color(TextColor.color(0x0341fc)));
                sellerGui.open();
                break;

            default:
                commandSender.sendMessage("Unknown GUI type: " + strings[strings.length - 1]);
                return true;
        }

        return true;
    }
}
