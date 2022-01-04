package net.wycre.itemlore;

import net.wycre.itemlore.commands.MainCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        MainCommand commands = new MainCommand(this);
        getCommand("itemlore").setExecutor(commands);
        getCommand("itemname").setExecutor(commands);



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
