package net.wycre.itemlore.commands;

import net.wycre.itemlore.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Handle Commands for ItemData plugin (codename ItemLore) <br><br>
 * Implements two commands under the permission "wycre.itemdata" <br><br>
 * Both commands fully support color codes
 * @author Wycre; Aclamendo
 */
public class MainCommand implements CommandExecutor {
    // Establish Main as an object to be referenced later
    private final Main main;
    public MainCommand(Main main) {
        this.main = main;
    }

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
        if (!(player.isOp() || player.hasPermission("wycre.itemdata"))) {
            player.sendMessage(ChatColor.RED + "You do not have permission to run this command");
            return true;
        }

        // /itemlore command
        if (command.getName().equalsIgnoreCase("itemlore")) {
            // Get the held item and metadata
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta metadata = item.getItemMeta();

            // If User enters no args, treat like incorrect arg or help arg
            if (args.length < 1) { // No args
                itemLoreHelp(player);
                return true;
            }

            // check if metadata is null
            if (metadata == null) {
                player.sendMessage(ChatColor.RED + "You are not holding an item!");
                return true;
            } // End Command and warn player

            // If args are present
            else {
                // if "add" is in first arg
                if (args[0].equalsIgnoreCase("add")) {
                    // Use StringBuilder to put all args on one string
                    StringBuilder stringBuilder = new StringBuilder();
                    // Create the new lore line from all other args
                    stringBuilder.append(args[1]); // Create initial word
                    for (int i = 2; i < args.length; i++) { // Add all other words
                        stringBuilder.append(" ").append(args[i]);
                    } // Add all other words

                    // convert the stringBuilder into a string
                    String completeLine = stringBuilder.toString();

                    // Create staging list of lore lines, add new entry
                    List<String> stagingLore = new ArrayList<>();

                    // Get the old lore and check if it is null
                    List<String> oldLore = metadata.getLore();
                    if (oldLore == null) { // If old lore is null tell player a new lore is being made
                        player.sendMessage(ChatColor.YELLOW + "Item does not have lore, creating new lore.");
                    } else { // If old lore is not null, set the staging lore to be equal to the old lore
                        stagingLore = oldLore;
                    }
                    // Add the new line to the staging lore
                    stagingLore.add(completeLine);

                    // Finalize staging lore by merging it with the item
                    metadata.setLore(this.color(stagingLore));
                    item.setItemMeta(metadata);


                } // Handle adding lines of lore

                // Removes one line from the lore
                else if (args[0].equalsIgnoreCase("remove")) {
                    List<String> stagingLore = metadata.getLore();
                    if (stagingLore == null) {
                        player.sendMessage(ChatColor.RED + "Item has no lore to remove!");
                    } else { // A lore exists
                        // Construct message to player, First segment
                        String message1 = ChatColor.YELLOW + "Removing \"" + ChatColor.DARK_PURPLE + ChatColor.ITALIC;
                        String message3 = ChatColor.YELLOW + "\" from your lore!"; // third segment

                        // Establish the entry to remove from the lore
                        int index = stagingLore.size() - 1;

                        // Set second segment of message to player
                        String message2 = stagingLore.get(index);

                        // Remove the line and merge the data
                        stagingLore.remove(index);
                        metadata.setLore(stagingLore);
                        item.setItemMeta(metadata);

                        // Finalize player message
                        String playerMessage = message1 + message2 + message3;
                        player.sendMessage(playerMessage);
                    }
                } // handle removing lines of lore
                // Incorrect First arg
                else {
                    itemLoreHelp(player);

                }

            }
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

                metadata.setDisplayName(this.color(fullName));
                item.setItemMeta(metadata);
            } // Cat all args into a string, set displayName to that string

            return true;
        }

        return true;
    }

    /**
     * Translates '&' from color codes within a List to the correct char
     * Call with <code> this.color()</code>
     * @param lore String List containing color codes
     * @return String List with the correct color code delimiters
     * @author Remceau
     */
    private List<String> color(List<String> lore){
        return lore.stream().map(this::color).collect(Collectors.toList());
    }
    /**
     * Translates '&' from color codes within a string to the correct char
     * @param string That contains alternate color codes
     * @return String that has replaced the color codes with the correct ones
     * @author Remceau
     */
    private String color(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    // Help Statement for /itemlore
    private static void itemLoreHelp(Player player) {
        player.sendMessage(ChatColor.DARK_RED + "===============================================");
        player.sendMessage(ChatColor.AQUA + "                         /itemlore usage");
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_GREEN + "/itemlore add " + ChatColor.AQUA + "<lore>");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "    Will add the specified lore in one line (supports color codes)");
        player.sendMessage(ChatColor.DARK_GREEN + "/itemlore remove ");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "    Will remove one line of lore from the bottom");
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_RED + "===============================================");
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
