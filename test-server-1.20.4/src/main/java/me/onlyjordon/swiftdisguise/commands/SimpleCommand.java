package me.onlyjordon.swiftdisguise.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.List;

public abstract class SimpleCommand extends Command {

    public SimpleCommand(String name) {
        super(name);
    }

    public SimpleCommand(String name, String permission) {
        super(name, permission);
    }

    public SimpleCommand(String name, Permission permission) {
        super(name, permission);
    }

    public SimpleCommand(String name, String permission, List<String> aliases) {
        super(name, permission, aliases);
    }

    public SimpleCommand(String name, String permission, String[] aliases) {
        super(name, permission, aliases);
    }

    @Override
    protected boolean execute(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return execute(sender, args);
    }

    protected abstract boolean execute(CommandSender sender, String[] args);

}
