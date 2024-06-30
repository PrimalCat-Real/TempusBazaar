package primalcat.tempusbazaar.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import primalcat.tempusbazaar.gui.GuiType;

public class ReRollGuiCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("tempusbazaar.bazaarreroll")) {
            commandSender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length != 0) {
            commandSender.sendMessage("Usage: /buyerreroll");
            return true;
        }

        // Переролл случайных категорий
        GuiType.updateRandomBuyerCategories();
        commandSender.sendMessage("Random buyer categories rerolled successfully.");

        return true;
    }
}
