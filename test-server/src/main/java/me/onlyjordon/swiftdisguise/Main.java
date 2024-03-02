package me.onlyjordon.swiftdisguise;

import me.onlyjordon.swiftdisguise.api.SwiftDisguise;
import me.onlyjordon.swiftdisguise.spigot.SpigotPlatform;
import me.onlyjordon.swiftdisguise.spigot.SwiftDisguiseSpigot;
import me.onlyjordon.swiftdisguise.commands.CommandManager;
import me.onlyjordon.swiftdisguise.commands.SimpleCommandManager;
import me.onlyjordon.swiftdisguise.commands.impl.CommandDisguise;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private CommandManager commandManager;
    @Override
    public void onEnable() {
        SimpleCommandManager scm = new SimpleCommandManager();
        scm.initialise();
        commandManager = scm;
        commandManager.addCommand(new CommandDisguise());
        commandManager.updateCommandMap();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        commandManager.removeCommands();
        ((SwiftDisguiseSpigot)(SpigotPlatform.get().getAPI())).disable();
    }
}