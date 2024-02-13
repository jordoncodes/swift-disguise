package me.onlyjordon.nicknamingapi;

import me.onlyjordon.nicknamingapi.commands.CommandManager;
import me.onlyjordon.nicknamingapi.commands.SimpleCommandManager;
import me.onlyjordon.nicknamingapi.debug.CommandDev;
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
