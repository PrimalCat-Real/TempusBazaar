package primalcat.tempusbazaar.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import primalcat.tempusbazaar.TempusBazaar;
import primalcat.tempusbazaar.gui.GuiType;
import primalcat.tempusbazaar.serializers.JsonUpdater;
import primalcat.tempusbazaar.utils.ConfigUtil;

public class SaveGuiCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission("tempusbuyer.bazaarsave")) {
            commandSender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (strings.length != 1) {
            commandSender.sendMessage("Usage: /savelist <guiType>");
            return true;
        }

        GuiType guiType = GuiType.fromString(strings[0]);
        if (guiType == null) {
            commandSender.sendMessage("Unknown GUI type: " + strings[0]);
            return true;
        }

        JsonUpdater.saveCurrentValuesToFile(TempusBazaar.getPlugin().getDataFolder(), guiType.name().toLowerCase() + ".json", GuiType.getCategories(guiType));
//        ConfigUtil.saveCategoriesToFile(TempusBazaar.getPlugin().getDataFolder(), guiType.name().toLowerCase() + ".json", GuiType.getCategories(guiType));
        commandSender.sendMessage(guiType.name() + " categories saved successfully.");

        return true;
    }
}
