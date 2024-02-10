package me.onlyjordon.nicknamingapi.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;

@ApiStatus.Internal
public abstract class PlayerOnlyCommand extends Command {

    public PlayerOnlyCommand(String name) {
        super(name);
    }

    public PlayerOnlyCommand(String name, String permission) {
        super(name, permission);
    }

    public PlayerOnlyCommand(String name, Permission permission) {
        super(name, permission);
    }

    public PlayerOnlyCommand(String name, String permission, List<String> aliases) {
        super(name, permission, aliases);
    }

    public PlayerOnlyCommand(String name, String permission, String[] aliases) {
        super(name, permission, aliases);
    }

    protected abstract boolean execute(Player player, String[] args);
    protected abstract HashMap<String, Integer> completeTab(Player player, String[] args);

    @Override
    protected boolean execute(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            try {
                return execute(player, args);
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage("There was an error doing this command. Please try again later.");
                return true;
            }
        } else {
            sender.sendMessage("Only players can use this command!");
            return false;
        }
    }

    @Override
    public HashMap<String, Integer> completeTab(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) return null;
        Player player = (Player) sender;
        return completeTab(player, args);
    }
}
