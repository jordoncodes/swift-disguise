package me.onlyjordon.swiftdisguise.api;

import me.onlyjordon.swiftdisguise.api.utils.NMSUtils;
import me.onlyjordon.swiftdisguise.api.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public final class SwiftDisguiseAPI extends JavaPlugin implements Listener {
    private static Disguiser disguiser;
    private static boolean isDev = false;
    private Object dev;

    @Override
    public void onEnable() {
        if (disguiser.getImplementation() instanceof Listener) {
            getServer().getPluginManager().registerEvents((Listener) disguiser.getImplementation(), this);
        }

        if (isDev) {
            try {
                dev = Class.forName("me.onlyjordon.swiftdisguise.Dev").getConstructor(JavaPlugin.class).newInstance(this);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        Bukkit.getScheduler().runTaskLater(this, () -> disguiser.setup(), 1L);
    }

    @Override
    public void onLoad() {
        setDisguiser();
        Utils.checkFloodgate();
        String env = System.getenv("SWIFTDISGUISE_DEV");
        if (env != null)
            isDev = env.equalsIgnoreCase("true");
    }


    @SuppressWarnings("unchecked")
    private void setDisguiser() {
        String verAndRev = NMSUtils.getMinecraftPackage();
        try {
            Class<? extends IDisguiser> disguiserClass = (Class<? extends IDisguiser>) Class.forName("me.onlyjordon.swiftdisguise.nms." + verAndRev + ".util.Disguiser");
            IDisguiser nicknamer = disguiserClass.getConstructor().newInstance();
            disguiser = new Disguiser(nicknamer, this);
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

    public static Disguiser getDisguiser() {
        if (disguiser == null) {
            throw new NullPointerException("Null Disguiser! SwiftDisguiseAPI hasn't loaded, check https://github.com/jordoncodes/swift-disguise?tab=readme-ov-file#null-disguiser on how to fix.");
        }
        return disguiser;
    }
}
