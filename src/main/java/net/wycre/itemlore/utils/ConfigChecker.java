package net.wycre.itemlore.utils;

import net.wycre.itemlore.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

import static net.wycre.itemlore.utils.CommonStrings.*;

public class ConfigChecker {

    private FileConfiguration config;
    private String relevant;

    // Constructor
    public ConfigChecker(Main main, FileConfiguration config) {
        // Instanced Vars
        this.config = config;
    }



    /**
     * Comprehensive check of entire config. <br> If no exception is thrown, the check passes successfully
     * @throws InvalidConfigurationException if the config file has an error
     */
    public void compCheck() throws InvalidConfigurationException {

        // Check version (Future releases may have different config defaults
        if (!checkConfigVersion()) { throw new InvalidConfigurationException(CONFIG_VER_INVALID + relevant); }
        if (!checkItemLoreProtected()) { throw new InvalidConfigurationException(CONFIG_IL_PROTECT_INVALID + relevant); }
    }

    public boolean checkConfigVersion() {
        int configVer = config.getInt(CONF_VER);
        return configVer == 1;
    }

    public boolean checkItemLoreProtected() {
        List<String> list = config.getStringList(IL_PROTECT);

        for (String current : list) {
            String[] split = current.split(":");
            if (split.length != 2) {
                relevant = current;
                return false;
            }
        }
        return true;
    }







}
