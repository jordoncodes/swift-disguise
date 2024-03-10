package me.onlyjordon.swiftdisguise.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.permissions.Permission;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Command extends org.bukkit.command.Command implements CommandExecutor, TabCompleter {

    private final transient Map<String, String> usageStrings;


    protected Command(String name) {
        this(name, "");
    }

    protected Command(String name, String permission) {
        this(name, permission, new ArrayList<>());
    }

    protected Command(String name, Permission permission) {
        this(name, permission.getName());
    }

    protected Command(String name, List<String> aliases) {
        this(name, null, aliases);
    }

    protected Command(String name, String permission, List<String> aliases) {
        super(name);
        if (!Objects.equals(permission, "")) {
            this.setPermission(permission);
        }
        if (aliases != null && aliases.isEmpty()) {
            this.setAliases(aliases);
        }
        try {
            Field activeAliases = org.bukkit.command.Command.class.getDeclaredField("activeAliases");
            activeAliases.setAccessible(true);
            activeAliases.set(this, aliases);
            Field al = org.bukkit.command.Command.class.getDeclaredField("aliases");
            al.setAccessible(true);
            al.set(this, aliases);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.usageStrings = new LinkedHashMap<>();
    }

    protected Command(String name, String permission, String[] aliases) {
        this(name, permission, Arrays.stream(aliases).collect(Collectors.toList()));
    }

//    @NotNull
//    @Override
//    public List<String> tabComplete(CommandSender sender, String alias, String[] args, @Nullable Location location) throws IllegalArgumentException {
//        return tabComplete(sender, alias, args);
//    }


    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        return tabComplete(sender, args);
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return tabComplete(sender, getName(), args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> tabs = null;
        try {
            tabs = onTabComplete(sender, this, alias, args);
        } catch (Exception e) {
            System.err.println("There was an error tab-completing command " + alias + " for " + sender.getName() + ". Full command: (/" + alias + " " + String.join(" ", args) + ")");
            e.printStackTrace();
        }
        if (tabs == null) {
            tabs = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
        }
        tabs.remove(null);
        return tabs;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return onCommand(sender, this, commandLabel, args);
    }

    public Map<String, String> getUsageStrings() {
        return usageStrings;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!testPermission(sender)) return false;
        return execute(sender, command, label, args);
    }

    protected abstract boolean execute(CommandSender sender, org.bukkit.command.Command command, String label, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (!this.testPermission(sender)) return null;
        HashMap<String, Integer> allTabs = completeTab(sender, alias, args);
        if (allTabs == null) return null;
        if (allTabs.isEmpty()) return new ArrayList<>();
        List<String> currentTabs = new ArrayList<>();
        allTabs.keySet().forEach((tab) -> {
            int arg = allTabs.get(tab);

            if (args.length == 0 && arg == 0) {
                currentTabs.add(tab);
            }
            if (args.length - 1 != arg) return;
            if (args.length == 1) {
                if (tab.length() < args[arg].length()) return;

                String currentArg = args[arg];
                int length = currentArg.length();
                if (length == 0) {
                    currentTabs.add(tab);
                    return;
                }
                String argument1 = tab.substring(0, length);
                if (currentArg.equalsIgnoreCase(argument1)) {
                    currentTabs.add(tab);
                }
            } else if (args.length >= 1 && arg >= 1) {
                if (args[arg].isEmpty()) {
                    currentTabs.add(tab);
                } else {
                    if (tab.length() < args[arg].length()) return;

                    String currentArg = args[arg];
                    int length = currentArg.length();
                    if (length == 0) {
                        currentTabs.add(tab);
                        return;
                    }
                    String argument1 = tab.substring(0, length);
                    if (currentArg.equalsIgnoreCase(argument1)) {
                        currentTabs.add(tab);
                    }
                }
            }
        });
        return currentTabs;
    }


    public abstract HashMap<String, Integer> completeTab(CommandSender sender, String alias, String[] args);

}
