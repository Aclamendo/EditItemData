package net.wycre.itemlore.commands;

import net.wycre.itemlore.Main;
import net.wycre.itemlore.utils.StringManagement;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static net.wycre.itemlore.utils.CommonStrings.*;

/**
 * Handle Commands for ItemData plugin (codename ItemLore) <br><br>
 * Implements two commands under the permission "wycre.itemdata" <br><br>
 * Both commands fully support color codes
 * @author Wycre; Aclamendo
 */
public class ItemLoreCommand implements TabExecutor {
    // Establish Main as an object to be referenced later
    private final Main main;

    // Constructor
    public ItemLoreCommand(Main main) {
        this.main = main;
    }

    // Various Values
    private static final List<String> TCOMPLETE_NOARGS = new ArrayList<String>(Arrays.asList("add", "remove"));


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
        if (!(player.isOp() || player.hasPermission("wycre.itemlore"))) {
            player.sendMessage(PLAYER_NEEDS_PERMISSION);
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
                player.sendMessage(PLAYER_HAND_EMPTY);
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
                    if (!(oldLore == null)) { // If old lore is null tell player a new lore is being made
                        stagingLore = oldLore;
                    }
                    // Add the new line to the staging lore
                    stagingLore.add(completeLine);

                    // Finalize staging lore by merging it with the item
                    metadata.setLore(StringManagement.color(stagingLore));
                    item.setItemMeta(metadata);


                } // Handle adding lines of lore

                // Removes one line from the lore
                else if (args[0].equalsIgnoreCase("remove")) {
                    List<String> stagingLore = metadata.getLore();
                    if (stagingLore == null) {
                        player.sendMessage(ITEM_HAS_NO_LORE);
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

        return true;
    }


    // Tab complete options
    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender,
                                      @NonNull Command command,
                                      @NonNull String alias,
                                      String[] args) {
        if (args.length == 1) { // WHY IS IT ONE!!!!#@!!@#!@#@
            return TCOMPLETE_NOARGS;
        }

        return null;
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
}
