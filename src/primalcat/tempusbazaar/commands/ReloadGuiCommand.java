package primalcat.tempusbazaar.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import primalcat.tempusbazaar.gui.GuiType;

public class ReloadGuiCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("tempusbazaar.bazaarreload")) {
            commandSender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length != 1) {
            commandSender.sendMessage("Usage: /reloadcategories <guiType>");
            return true;
        }

        GuiType guiType = GuiType.fromString(args[0]);
        if (guiType == null) {
            commandSender.sendMessage("Unknown GUI type: " + args[0]);
            return true;
        }

        // Перезагружаем категории для указанного типа GUI
        GuiType.reloadCategories(guiType);
        commandSender.sendMessage("Categories for " + guiType.getType() + " reloaded successfully.");

        return true;
    }
}
