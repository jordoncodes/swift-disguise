package me.onlyjordon.swiftdisguise.commands;

public interface CommandManager {
    void updateCommandMap();

    void removeCommands();

    void addCommand(Command command);

    void removeCommand(Command command);

    void setPrefix(String prefix);
}
