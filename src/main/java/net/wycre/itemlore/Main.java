package net.wycre.itemlore;

import net.wycre.itemlore.commands.ItemLoreCommand;
import net.wycre.itemlore.commands.ItemNameCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        ItemLoreCommand itemLore = new ItemLoreCommand(this);
        ItemNameCommand itemName = new ItemNameCommand(this);
        getCommand("itemlore").setExecutor(itemLore);
        getCommand("itemname").setExecutor(itemName);



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
