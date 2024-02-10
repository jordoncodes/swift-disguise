package me.onlyjordon.nicknamingapi.commands;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface CommandManager {
    void updateCommandMap();

    void removeCommands();

    void addCommand(Command command);

    void removeCommand(Command command);

    void setPrefix(String prefix);
}
