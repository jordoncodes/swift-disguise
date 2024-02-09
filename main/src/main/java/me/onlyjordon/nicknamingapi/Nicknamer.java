package me.onlyjordon.nicknamingapi;

import me.onlyjordon.nicknamingapi.commands.CommandManager;
import me.onlyjordon.nicknamingapi.commands.SimpleCommandManager;
import me.onlyjordon.nicknamingapi.commands.impl.CommandDebug;
import me.onlyjordon.nicknamingapi.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public final class Nicknamer extends JavaPlugin {
    private static NMSDisguiser disguiser;
    private static CommandManager commandManager;
    private static boolean isDev = false;


    @Override
    public void onEnable() {
        if (isDev) { // only register dev commands in dev
            SimpleCommandManager cm = new SimpleCommandManager();
            cm.setPrefix("nicknamer");
            cm.initialise();
            cm.addCommand(new CommandDebug());
            cm.updateCommandMap();
            commandManager = cm;
        }

        if (disguiser instanceof Listener) {
            getServer().getPluginManager().registerEvents((Listener) disguiser, Nicknamer.this);
        }
        Bukkit.getScheduler().runTaskLater(this, () -> disguiser.setup(), 1L);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onLoad() {
        String verAndRev = NMSUtils.getMinecraftPackage();
        try {
            Class<? extends NMSDisguiser> disguiserClass = (Class<? extends NMSDisguiser>) Class.forName("me.onlyjordon.nicknamingapi.nms." + verAndRev + ".util.Disguiser");
            Nicknamer.disguiser = disguiserClass.getConstructor().newInstance();
        } catch (NoSuchMethodException | ClassNotFoundException | ClassCastException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to load - Try updating the plugin or your server!", e);
        }

        String env = System.getenv("NICKNAMER_DEV");
        if (env != null)
            isDev = env.equalsIgnoreCase("true");

    }

    @Override
    public void onDisable() {
        if (isDev)
            commandManager.removeCommands();
        disguiser.disable();
    }

    public static NMSDisguiser getDisguiser() {
        return disguiser;
    }
}
