package net.wycre.itemlore;

import net.wycre.itemlore.commands.ItemLoreCommand;
import net.wycre.itemlore.commands.ItemNameCommand;
import net.wycre.itemlore.utils.ConfigChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.util.logging.Logger;

import static net.wycre.itemlore.utils.CommonStrings.PLAYER_NEEDS_PERMISSION;

public final class Main extends JavaPlugin {

    private FileConfiguration config;
    private Logger log = this.getLogger();
    private boolean validConfig;

    // Primary plugin logic register
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {

        // Check and Register config
        if (!(new File(getDataFolder(), "config.yml").isFile())) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        if (!checkLocalConfig()) {
            log.warning(ChatColor.RED + "" + ChatColor.BOLD + "Config check failed! See trace for error!");
            getPluginLoader().disablePlugin(this);
        }
        else {
            saveDefaultConfig();
            validConfig = true;


            // Instantiate command classes
            ItemLoreCommand itemLore = new ItemLoreCommand(this, config);
            ItemNameCommand itemName = new ItemNameCommand(this);

            // Define Commands
            getCommand("itemlore").setExecutor(itemLore);
            getCommand("itemname").setExecutor(itemName);
            getCommand("itemdata").setExecutor(this);

            // Define tabCompleters
            getCommand("itemlore").setTabCompleter(itemLore);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // Admin commands
    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(@NonNull CommandSender sender,
                             @NonNull Command command,
                             @NonNull String label,
                             String[] args) {

        // Check if caller has permission to run this suite of commands
        if (!(sender.isOp() || sender.hasPermission("wycre.admin") || sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(PLAYER_NEEDS_PERMISSION);
            log.info("Sender Lacks Permission");
            return true;
        }

        // Check command name
        if (command.getName().equalsIgnoreCase("itemdata")) {

            // Check if config is valid
            if (!(checkLocalConfig())) {
                sender.sendMessage(ChatColor.RED + "Config check failed! check console.");
                return true;
            }

            // reload the config
            reloadConfig();
            validConfig = true;
            sender.sendMessage(ChatColor.AQUA + "Config Reloaded!");

        }


    return true;
    }

    // Misc Methods
    /**
     * Check if the instanced config file is valid. Will print stack if error occurred
     * @return true if the file is valid, false if an error occurred
     */
    private boolean checkLocalConfig() {
        File cFile = new File(getDataFolder(), "config.yml");
        FileConfiguration checkFile = YamlConfiguration.loadConfiguration(cFile);
        ConfigChecker confCheck = new ConfigChecker(this, checkFile);
        try {
            confCheck.compCheck(); // Check if config is correct
            return true;
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidConfig() { return validConfig; }

}
