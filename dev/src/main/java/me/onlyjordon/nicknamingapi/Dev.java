package me.onlyjordon.nicknamingapi;

import me.onlyjordon.nicknamingapi.commands.CommandManager;
import me.onlyjordon.nicknamingapi.commands.SimpleCommandManager;
import me.onlyjordon.nicknamingapi.debug.CommandDev;

public class Dev {
    private final CommandManager commandManager;

    public Dev() {
        SimpleCommandManager cm = new SimpleCommandManager();
        cm.initialise();
        cm.addCommand(new CommandDev());
        cm.updateCommandMap();
        commandManager = cm;
    }

    public void disable() {
        commandManager.removeCommands();
    }
}
