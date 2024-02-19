package me.onlyjordon.nicknamingapi;

import me.onlyjordon.nicknamingapi.utils.NMSUtils;
import me.onlyjordon.nicknamingapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public final class NicknamerAPI extends JavaPlugin implements Listener {
    private static Nicknamer disguiser;
    private static boolean isDev = false;
    private Object dev;

    @Override
    public void onEnable() {
        if (disguiser.getImplementation() instanceof Listener) {
            getServer().getPluginManager().registerEvents((Listener) disguiser.getImplementation(), this);
        }

        if (isDev) {
            try {
                dev = Class.forName("me.onlyjordon.nicknamingapi.Dev").getConstructor(JavaPlugin.class).newInstance(this);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        Bukkit.getScheduler().runTaskLater(this, () -> disguiser.setup(), 1L);
    }

    @Override
    public void onLoad() {
        setNicknamer();
        Utils.checkFloodgate();
        String env = System.getenv("NICKNAMER_DEV");
        if (env != null)
            isDev = env.equalsIgnoreCase("true");
    }


    @SuppressWarnings("unchecked")
    private void setNicknamer() {
        String verAndRev = NMSUtils.getMinecraftPackage();
        try {
            Class<? extends INicknamer> disguiserClass = (Class<? extends INicknamer>) Class.forName("me.onlyjordon.nicknamingapi.nms." + verAndRev + ".util.Disguiser");
            INicknamer nicknamer = disguiserClass.getConstructor().newInstance();
            disguiser = new Nicknamer(nicknamer, this);
        } catch (NoSuchMethodException | ClassNotFoundException | ClassCastException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to load - Try updating the plugin or your server!", e);
        }
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
        if (disguiser == null) {
            throw new NullPointerException("Null Nicknamer! NicknamerAPI hasn't loaded, check https://github.com/jordoncodes/nicknamer-api?tab=readme-ov-file#null-nicknamer on how to fix.");
        }
        return disguiser;
    }
}
