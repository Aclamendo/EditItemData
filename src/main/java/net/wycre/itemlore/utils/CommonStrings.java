package net.wycre.itemlore.utils;

import org.bukkit.ChatColor;

/**
 * A collection of strings intended to be used throughout the plugin
 */
public class CommonStrings {
    /**
     * Intended if the command caller is the console
     */
    public final static String CALLER_MUST_BE_PLAYER = "Only players can run this command";

    /**
     * If the player should lack the permission for a command
     */
    public final static String PLAYER_NEEDS_PERMISSION = ChatColor.RED + "You do not have permission to use this command!";
    public final static String PLAYER_ASKS_PERMISSION = ChatColor.RED + "Contact an administrator if you think you should have permission.";

    /**
     * If the player has made an error in command execution
     */
    public final static String PLAYER_HAND_EMPTY = ChatColor.RED + "You must be holding an item!";
    public final static String ARG_REQUIRES_INT = ChatColor.RED + "Line number must be an integer!";
    public final static String ARG_LESS_THAN_ONE = ChatColor.RED + "Line number must be greater than zero!";
    public final static String MISSING_LINE_ARG = ChatColor.RED + "Line number must be set!";
    public final static String MISSING_TEXT_ARG = ChatColor.RED + "You must specify some text!";

    /**
     * If the command is not applicable to a given item
     */
    public final static String ITEM_HAS_NO_LORE = ChatColor.RED + "Item already has no lore!";
}
