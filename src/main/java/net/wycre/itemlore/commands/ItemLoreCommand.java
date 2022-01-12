package net.wycre.itemlore.commands;

import net.wycre.itemlore.Main;
import net.wycre.itemlore.utils.StringManagement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static net.wycre.itemlore.utils.CommonStrings.*;

/*
*  ## TODO Line specifiers for /itemlore remove:
*  - range of lines
*  - specific lines
*/


/**
 * Handle Commands for ItemData plugin (codename ItemLore) <br><br>
 * Implements two commands under the permission "wycre.itemdata" <br><br>
 * Both commands fully support color codes
 * @author Wycre; Aclamendo
 */
public class ItemLoreCommand implements TabExecutor {
    // Instanced variables
    private final Main main;
    private FileConfiguration config;
    private Logger log;

    // Constructor
    public ItemLoreCommand(Main main, FileConfiguration configFile) {
        this.main = main;
        this.config = configFile;
        this.log = this.main.getLogger();
    }

    // Static Class Values
    private static final List<String> TCOMPLETE_NOARGS = new ArrayList<>(Arrays.asList("add", "remove", "set"));


    // Command Logic
    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command command,
                             @NonNull String label,
                             String[] args) {
        config = main.getConfig();

        // Check if caller is valid, if so cast caller to Player
        if (!(sender instanceof Player)) {
            log.warning(CALLER_MUST_BE_PLAYER);
            return true;
        }
        // After sender is verified as player, cast the sender to a Player
        Player player = (Player) sender;

        // Check if caller has permission to run this suite of commands
        if (!(player.isOp() || player.hasPermission("wycre.itemlore"))) {
            player.sendMessage(PLAYER_NEEDS_PERMISSION);
            return true;
        }

        // Get the held item and metadata
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta metadata = item.getItemMeta();
        List<String> protectedMaterials = config.getStringList(IL_PROTECT);

        // check if metadata is null
        if (metadata == null) {
            player.sendMessage(PLAYER_HAND_EMPTY);
            return true;
        } // End Command and warn player

        // Check if held item is allowed
        if (isItemProtected(item, protectedMaterials)) {
            player.sendMessage(ITEM_NOT_ALLOWED);
            return true;
        }

        // COMMAND LOGIC
        if (command.getName().equalsIgnoreCase("itemlore")) {

            // If User enters no args, treat like incorrect arg or help arg
            if (args.length < 1) { // No args
                itemLoreHelp(player);
                return true;
            }

            // If args are present
            else {
                // if "add" is in first arg
                if (args[0].equalsIgnoreCase("add")) {

                    // Handle Line Creation
                    String completeLine;
                    if (args.length > 1) {
                        completeLine = StringManagement.argsToString(1, args);
                    } else {
                        player.sendMessage(MISSING_TEXT_ARG);
                        return true;
                    }

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

                    return true;
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
                    return true;
                } // handle removing lines of lore

                // Set Command
                if (args[0].equalsIgnoreCase("set")) {

                    int lineNum;

                    // Handle Line number
                    try {
                        // Attempt to parse line number
                        lineNum = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ex) {
                        player.sendMessage(ARG_REQUIRES_INT);
                        return true;
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        player.sendMessage(MISSING_LINE_ARG);
                        return true;
                    }
                    if (lineNum < 1) {
                        player.sendMessage(ARG_LESS_THAN_ONE);
                        return true;
                    }


                    // Create or modify lore
                    List<String> stagingLore;
                    if (metadata.getLore() == null) {
                        stagingLore = new ArrayList<>();
                    } else {
                        stagingLore = metadata.getLore();
                    }

                    // Handle Line Creation
                    String completeLine;
                    if (args.length > 2) {
                        completeLine = StringManagement.argsToString(2, args);
                    } else {
                        player.sendMessage(MISSING_TEXT_ARG);
                        return true;
                    }

                    // Handle lore on elevated lines
                    if (metadata.getLore() == null) {
                        for (int i = 0; i < lineNum; i++) {
                            stagingLore.add(" ");
                        } // Fill with empty lines
                    } // If setting excess line in empty lore, add excess lines
                    else if (lineNum > metadata.getLore().size()) {
                        for (int i = metadata.getLore().size()-1; i<lineNum-1; i++ ) {
                            stagingLore.add(" ");
                        } // Fill with empty lines
                    } // If setting excess line, add blank lines before

                    // Merge Data and meta
                    stagingLore.set(lineNum - 1, StringManagement.color(completeLine));
                    metadata.setLore(stagingLore);
                    item.setItemMeta(metadata);
                    return true;
                } // Handle setting lines individually

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

    // protectedMaterials Handling
    private static boolean isItemProtected(ItemStack item, List<String> protectedMats) {

        if (protectedMats == null || protectedMats.size() == 0) { return false; }

        Material itemMat = item.getType();

        for (String protectedMat : protectedMats) {
            // Split input string at colon, then uppercase.
            String[] matSplit = protectedMat.split(":");
            String matString = matSplit[1].toUpperCase();
            //Material checkMat = Material.getMaterial(matString);
            if (itemMat.name().equalsIgnoreCase(matString)) {
                return true;
            }
        }


        return false;
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





