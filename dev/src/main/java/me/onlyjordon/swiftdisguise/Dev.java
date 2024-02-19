package me.onlyjordon.swiftdisguise;

import me.onlyjordon.swiftdisguise.commands.CommandManager;
import me.onlyjordon.swiftdisguise.commands.SimpleCommandManager;
import me.onlyjordon.swiftdisguise.debug.CommandDev;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Dev {
    private final CommandManager commandManager;

    public Dev(JavaPlugin plugin) {
        SimpleCommandManager cm = new SimpleCommandManager();
        cm.initialise();
        cm.addCommand(new CommandDev());
        cm.updateCommandMap();
        commandManager = cm;
        Bukkit.getPluginManager().registerEvents(new DevListener(), plugin);
    }


    public void disable() {
        commandManager.removeCommands();
    }
}
