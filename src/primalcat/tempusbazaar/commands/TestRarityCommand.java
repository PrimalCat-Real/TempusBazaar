package primalcat.tempusbazaar.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primalcat.tempusbazaar.utils.DisplayUtil;

public class TestRarityCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tempusbazaar.bazaarrarity")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length > 0) {
            String rarity = args[0].toUpperCase();
            try {
                Component message = DisplayUtil.getRarityComponent(rarity, "This is a " + rarity + " item!");
                player.sendMessage(message); // Отправляем сообщение игроку
            } catch (IllegalArgumentException e) {
                player.sendMessage("Rarity not defined. Please use a valid rarity.");
            }
        } else {
            player.sendMessage("Please specify a rarity.");
        }

        return true;
    }
}
