package me.onlyjordon.nicknamingapi;

import me.onlyjordon.nicknamingapi.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public final class NicknamerAPI extends JavaPlugin {
    private static Nicknamer disguiser;
    private static boolean isDev = false;
    private Object dev;

    @Override
    public void onEnable() {
        if (disguiser instanceof Listener) {
            getServer().getPluginManager().registerEvents((Listener) disguiser, NicknamerAPI.this);
        }
        if (isDev) {
            try {
                dev = Class.forName("me.onlyjordon.nicknamingapi.Dev").getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        Bukkit.getScheduler().runTaskLater(this, () -> disguiser.setup(), 1L);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onLoad() {
        String verAndRev = NMSUtils.getMinecraftPackage();
        try {
            Class<? extends Nicknamer> disguiserClass = (Class<? extends Nicknamer>) Class.forName("me.onlyjordon.nicknamingapi.nms." + verAndRev + ".util.Disguiser");
            NicknamerAPI.disguiser = disguiserClass.getConstructor().newInstance();
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
        disguiser.disable();
        if (isDev) {
            try {
                dev.getClass().getDeclaredMethod("disable").invoke(dev);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Nicknamer getNicknamer() {
        return disguiser;
    }
}
