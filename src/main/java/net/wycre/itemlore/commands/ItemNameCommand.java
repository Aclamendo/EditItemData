package net.wycre.itemlore.commands;

import net.wycre.itemlore.Main;
import net.wycre.itemlore.utils.StringManagement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.logging.Logger;

public class ItemNameCommand implements CommandExecutor {

    // Establish Main as an object to be referenced later
    private final Main main;
    public ItemNameCommand(Main main) {
        this.main = main;
    }

    // Command Logic
    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command command,
                             @NonNull String label,
                             String[] args) {
        // Define logger to main
        Logger mainLog = main.getLogger();

        // Check if caller is valid, if so cast caller to Player
        if (!(sender instanceof Player)) {
            mainLog.warning("Caller must be a player");
            return true;
        }
        // After sender is verified as player, cast the sender to a Player
        Player player = (Player) sender;

        // Check if caller has permission to run this suite of commands
        if (!(player.isOp() || player.hasPermission("wycre.itemname"))) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return true;
        }

        // /itemname command
        if (command.getName().equalsIgnoreCase("itemname")) {
            // Get the held item and metadata
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta metadata = item.getItemMeta();

            // If User enters no args, treat like incorrect arg or help arg
            if (args.length < 1) { // No args
                itemNameHelp(player);
                return true;
            }

            // check if metadata is null
            if (metadata == null) {
                player.sendMessage(ChatColor.RED + "You are not holding an item!");
                return true;
            } // End Command and warn player

            // If args are present
            else {
                // Use StringBuilder to put all args on one string
                StringBuilder stringBuilder = new StringBuilder();
                // Create the new lore line from all other args
                stringBuilder.append(args[0]); // Create initial word
                for (int i = 1; i < args.length; i++) { // Add all other words
                    stringBuilder.append(" ").append(args[i]);
                } // Add all other words
                String fullName = stringBuilder.toString();

                metadata.setDisplayName(StringManagement.color(fullName));
                item.setItemMeta(metadata);
            } // Cat all args into a string, set displayName to that string

            return true;
        }
        return true;
    }

    // Help Statement for /itemname
    private static void itemNameHelp(Player player) {
        player.sendMessage(ChatColor.RED + "===============================================");
        player.sendMessage(ChatColor.AQUA + "                         /itemname usage");
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_GREEN + "/itemname " + ChatColor.AQUA + "<New Name>");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "    Will change the name of the item (supports color codes)");
        player.sendMessage("");
        player.sendMessage(ChatColor.RED + "===============================================");
    }

}
