package me.onlyjordon.swiftdisguise.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleCommandManager implements CommandManager {
    public List<Command> commands = null;
    private String prefix = "core";

    public void initialise() {
        commands = new ArrayList<>();
    }

    @Override
    public void removeCommands() {
        SimpleCommandMap map = null;
        Map<String, org.bukkit.command.Command> knownCommands = null;
        try {
            Field cmdMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            cmdMap.setAccessible(true);
            map = (SimpleCommandMap) cmdMap.get(Bukkit.getPluginManager());
            Field knownCmds = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCmds.setAccessible(true);
            knownCommands = (Map<String, org.bukkit.command.Command>) knownCmds.get(map);
            try {
                for (Command command : commands) {
                    command.unregister(map);
                }
            } catch (Exception | Error e) {
                e.printStackTrace();
            }
            for (Command cmd : commands) {
                try {
                    knownCommands.remove(cmd.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            knownCmds.set(map, knownCommands);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }

    }

    @Override
    public void addCommand(Command command) {
        commands.add(command);
    }

    @Override
    public void removeCommand(Command command) {
        commands.remove(command);
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void updateCommandMap() {
        removeCommands();
        CommandMap map = null;
        try {
            Field cmdMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            cmdMap.setAccessible(true);
            map = (CommandMap) cmdMap.get(Bukkit.getPluginManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Command cmd : commands) {
            try {
                if (map == null) {
                    return;
                }
                map.register(cmd.getName(), prefix, cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void disable() {
        removeCommands();
    }
}
