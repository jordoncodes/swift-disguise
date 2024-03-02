package me.onlyjordon.swiftdisguise.spigot;

import me.onlyjordon.swiftdisguise.api.ISwiftDisguiseAPI;
import me.onlyjordon.swiftdisguise.api.SwiftDisguiseAPI;
import me.onlyjordon.swiftdisguise.platforms.Platform;

public class SpigotPlatform extends Platform {

    private final SwiftDisguiseSpigot api;
    private static SpigotPlatform instance;

    private SpigotPlatform() {
        api = new SwiftDisguiseSpigot();
    }

    /**
     * @return The instance of <b>SpigotPlatform</b>.
     */
    public static SpigotPlatform get() {
        if (instance == null) instance = new SpigotPlatform();
        return instance;
    }

    /**
     * @return The API for the platform.
     */
    @Override
    public SwiftDisguiseAPI getAPI() {
        return api;
    }
}
